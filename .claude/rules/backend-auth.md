---
id: rules-backend-auth
title: Backend 認証 (GIS ID Token + JWKS)
status: stable
last_updated: 2026-05-17
paths:
  - "backend/**/*.kt"
  - "core/network/auth/**/*.kt"
  - "docs/api/auth.md"
related_adrs:
  - ADR-0011
  - ADR-0020
---

# backend-auth.md — Backend 認証 (GIS ID Token + JWKS)

> Firebase Authentication 撤去 (ADR 0011) に伴い、**Google Identity Services (GIS)** で
> ID Token を取得し、Backend (Ktor Server) で **JWKS 検証 + uid 抽出** を行う規約。
> PII 最小化 (ADR 0020) のため Backend に保存するのは **`uid` (sub claim) のみ**。

## 認証フロー

```text
[ClientApp]
    1. GIS Sign-In ボタン押下 → Google OAuth flow
    2. ID Token (JWT) を取得
    3. 以降のリクエストに Authorization: Bearer <id_token> header

[Backend (Cloud Run)]
    4. ID Token を JWKS で検証 (signature / iss / aud / exp)
    5. sub claim を uid として抽出
    6. users.db の users テーブルで uid を確認/挿入
    7. リクエスト処理
```

## ClientApp 側 (`core/network/auth/*`)

```kotlin
// commonMain
interface AuthClient {
    suspend fun signIn(): Result<IdToken>
    suspend fun signOut()
    val idToken: StateFlow<IdToken?>
}

// platform 別 actual で GIS SDK 呼び出し
```

- platform 別 actual: Android (`Identity.getSignInClient` / Credential Manager API)、Wasm (Google Identity Services JS lib)、Desktop (OAuth browser flow)
- ID Token は **memory のみで保持** (永続化禁止、PII 最小化)
- 期限切れ (`exp` 経過) 時は自動 re-fetch

## Backend 側 (Ktor) の検証

```kotlin
fun Application.installAuth() {
    install(Authentication) {
        jwt("gis") {
            verifier(JwkProvider(URL(jwksUrl)), audience) {
                acceptIssuer("https://accounts.google.com")
            }
            validate { credential ->
                val sub = credential.payload.subject ?: return@validate null
                Principal(uid = sub)
            }
        }
    }
}

fun Route.protectedRoutes() {
    authenticate("gis") {
        get("/me") { ... }
    }
}
```

- **JWKS endpoint**: `https://www.googleapis.com/oauth2/v3/certs`
- **issuer**: `https://accounts.google.com` または `accounts.google.com`
- **audience**: GIS Client ID (Cloud Run env var `GIS_CLIENT_ID`)
- 検証失敗 → 401 Unauthorized

## uid の取り扱い

- **`users.db` の `users` テーブルには `uid TEXT PRIMARY KEY` のみ保存** (ADR 0020、`pii.md` 厳守)
- メールアドレス / 表示名 / プロフィール画像 URL は **memory cache (TTL 15 分)** で都度取得 (`pii.md` 参照)
- 初回ログイン時に `INSERT INTO users(uid) VALUES (?)` で row 作成

## userinfo の取得 (display name / email / picture)

- Backend が GIS userinfo endpoint (`https://www.googleapis.com/oauth2/v3/userinfo`) を Bearer token で叩く
- レスポンスは memory cache (TTL 15 分) に保持、DB に persist しない
- ClientApp が `/me` endpoint を叩くと Backend が cache → fallback で userinfo を返却

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `backend/**/*.kt` の `users` テーブル schema (SqlDelight) に `email` / `display_name` / `picture` columns が含まれない (`pii.md` 統合)
  - `core/network/auth/*` に Firebase Authentication SDK の import がない (`firebase-boundary.md`)
- **OpenAPI spec** (`docs/api/colormaster-api.yaml`、A2-5 で本格化) と Ktor route の整合検証 (将来)

## JWKS cache の stale-while-revalidate (PR #126 レトロ Try)

- **stale-while-revalidate grace 秒数**: 60 秒 (`JwkProvider` の cache miss + 取得失敗時に stale cache を 60 秒間継続使用)、その後 503 Service Unavailable で fail-fast
- **Phase C5 着手時に runbook で確定**: `docs/runbooks/r2-litestream.md` 等の関連 runbook と一体で grace 秒数を最終確定 (Cloud Run / Cloudflare のネットワーク特性に応じて 30-120 秒の幅で再評価)
- **挙動**: (a) cache hit → 即時返却、(b) cache miss + JWKS 取得成功 → 取得結果を cache 更新して返却、(c) cache miss + JWKS 取得失敗 → stale cache を grace 期間 (60 秒) 継続使用、(d) grace 期間超過 → 503 Service Unavailable

## mock JWKS モードのガード (PR #126 レトロ Try)

ローカル開発時に mock JWKS endpoint を使う場合の本番漏洩リスク対策:

```kotlin
fun Application.installAuth() {
    val isMockMode = BuildConfig.IS_DEBUG && System.getenv("MOCK_JWKS_ENABLED") == "true"
    if (isMockMode) {
        log.warn("MOCK JWKS ENABLED — never run this configuration in stage/production")
    }
    install(Authentication) {
        jwt("gis") {
            verifier(
                if (isMockMode) MockJwkProvider() else JwkProvider(URL(jwksUrl)),
                audience,
            ) { ... }
        }
    }
}

fun Route.healthRoute() {
    get("/health") {
        call.respond(mapOf(
            "status" to "ok",
            "mock_auth" to isMockMode,  // stage/production では必ず false
        ))
    }
}
```

- **mock モード起動時に `WARN: MOCK JWKS ENABLED` ログを強制出力** (起動時 1 回)
- **`/health` レスポンスで `mock_auth: true|false` を返却**、stage / production では false が確認できる
- **`BuildConfig.IS_DEBUG` + 環境変数 `MOCK_JWKS_ENABLED=true` の AND 条件**: 単一 flag では誤起動リスクあり、二重ガードで本番混入を予防
- **CI で `/health` の `mock_auth: false` を assertion**: stage / production deploy 後の smoke test で必須

## Gotchas

- **GIS ID Token の `aud` は GIS Client ID** (Backend が発行した token ではない、Google が発行)
- **`acceptIssuer` で `accounts.google.com` (https 無し) と `https://accounts.google.com` の両方を許可** する (Google が両方 issue する場合あり)
- **JWKS cache**: `JwkProvider` 内蔵 cache (TTL 10 分) を使う、毎回 fetch するとレイテンシ悪化
- **JWKS stale-while-revalidate grace 60 秒** (PR #126 レトロ Try): cache miss + 取得失敗時の挙動を明示、`docs/runbooks/r2-litestream.md` 等の runbook で C5 着手時に最終確定
- **ID Token のリフレッシュ**: GIS は ID Token のみ提供 (Access Token / Refresh Token は別 API)。期限切れ時は再 sign-in flow
- **Backend が ClientApp の認証情報を log に出さない** (`pii.md` / `secrets.md` redaction、Authorization header sanitize)
- **mock JWKS モード起動時の警告ログ + `/health` `mock_auth: true` ガード必須** (PR #126 レトロ Try): stage / production 漏洩リスク対策
- ローカル開発時の GIS Client ID は **dev 用 OAuth Client** を別途作成 (本番 Client ID を流用しない)

## 関連

- ADR 0011 (Firebase → GIS 移行)
- ADR 0020 (PII 保護)
- GIS docs: https://developers.google.com/identity
- JWKS RFC 7517: https://datatracker.ietf.org/doc/html/rfc7517
- `.claude/rules/{firebase-boundary,no-firebase,pii,secrets,sqlite-data-file,network-client}.md`
- `docs/api/auth.md` (A2-5 で本格化)
