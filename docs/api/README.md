---
id: api-readme
title: API 概要 (colormaster-api)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.2 / §3.4
related_adrs:
  - ADR-0008
  - ADR-0009
  - ADR-0011
  - ADR-0020
---

# API 概要

> **5 行以内 summary**: ColorMaster Backend (`colormaster-api`) は Cloud Run 上の Ktor
> Server で稼働し、`/api/idols/*` (read-only 公開マスタ) と `/api/me/*` (GIS Bearer
> 認証必須) を提供する。スキーマの SoT は `colormaster-api.yaml` (OpenAPI 3.1)。
> エンドポイント別の散文解説は `auth.md` / `idols.md` / `me.md`。本格化は C5。

## ベース URL

| 環境 | URL | 備考 |
|---|---|---|
| 本番 | `https://colormaster-api.example.com` | Cloud Run、C7 でデプロイ確定 |
| ローカル | `http://localhost:8080` | `./gradlew :backend:server:run` で起動 |
| Preview | (環境変数で切替) | Cloud Run の preview revision、URL は CI 出力 |

## エンドポイント分類

| グループ | 認証 | 用途 | 詳細 docs |
|---|---|---|---|
| `/api/idols/*` | 不要 (公開マスタ) | アイドル / ブランド / カラーパレットの read-only 取得 | [idols.md](idols.md) |
| `/api/brands` | 不要 | ブランド一覧 | [idols.md](idols.md) |
| `/api/colors` | 不要 | カラーパレット一覧 | [idols.md](idols.md) |
| `/api/me/*` | GIS ID Token (Bearer) 必須 | 担当・推し一覧 / プロファイル / ユーザー削除 | [me.md](me.md) |
| `/health` | 不要 | Cloud Run ヘルスチェック (Litestream lag 含む) | (本ファイル末尾) |

## 認証方式 (詳細は `auth.md`)

- 全 `/api/me/*` で `Authorization: Bearer <GIS ID Token>` 必須
- Backend は Google JWKS で署名検証 + `iss` / `aud` / `exp` を検証
- `sub` claim を `uid` として扱い、Backend memory に **15 分** GIS userinfo cache を持つ
- ID Token は GIS が発行する exp (通常 1 時間) で失効、クライアントは再取得
- 詳細は ADR 0011 (GIS 統一) / `.claude/rules/backend-auth.md` (A2-2 で本格化)

## バージョニング

- **URI バージョニング不採用**: `/v1/api/idols` 等は使わず、互換性を破壊する変更時のみ
  別エンドポイント (`/api/idols2` 等) を追加して旧をしばらく並走 + ADR 起票で記録
- **OpenAPI の `info.version`** を semver で進める (`0.0.0-skeleton` → `0.1.0-alpha` → `1.0.0`)
- 後方非互換変更は **必ず ADR 起票** (`adr.md` 起票基準: §4.5-3 外部サービス変更)

## 標準応答ヘッダ

| ヘッダ | 値 | 適用範囲 |
|---|---|---|
| `Content-Type` | `application/json; charset=utf-8` | 全 endpoint |
| `Cache-Control` | `public, max-age=86400` | `/api/idols/*` `/api/brands` `/api/colors` (read-only マスタ) |
| `Cache-Control` | `private, no-store` | `/api/me/*` (個人データ、CDN キャッシュ禁止) |
| `Cache-Control` | `no-store` | `/health` |
| `X-Request-Id` | UUID v4 | 全 endpoint (ログ追跡用、Skill 出力では redaction 不要) |

## 標準エラー応答

全エンドポイント共通で以下の JSON schema を返す (`colormaster-api.yaml` の `components/schemas/Error` 定義):

```json
{
  "error": {
    "code": "string (machine-readable)",
    "message": "string (human-readable, 日本語)",
    "details": { ... 任意の追加情報 ... }
  }
}
```

### ステータスコード規約

| コード | 用途 | 例 |
|---|---|---|
| 200 | 成功 (GET) | アイドル一覧取得 |
| 201 | 作成成功 (POST) | 担当追加 |
| 204 | 削除成功 (DELETE) | 担当削除 / ユーザー削除 |
| 400 | リクエスト不正 | クエリパラメータ型不正、body schema 違反 |
| 401 | 認証必要 / 失敗 | ID Token なし / 期限切れ / 署名不正 |
| 403 | 認可失敗 | 他人の uid のデータに触れようとした |
| 404 | リソース不存在 | `/api/idols/{id}` で id 不存在 |
| 429 | レート制限 | (Phase C で導入検討) |
| 500 | サーバ内部エラー | DB 破損 / 想定外 exception |
| 503 | 一時的不可 | JWKS 取得失敗 / Litestream restore 中 / R2 unreachable |

### エラーコード語彙 (代表)

| `code` | HTTP | 意味 |
|---|---|---|
| `unauthorized` | 401 | 認証必要 / 失敗 |
| `token_expired` | 401 | ID Token 期限切れ (再取得を促す) |
| `forbidden` | 403 | uid 不一致 |
| `not_found` | 404 | リソース不存在 |
| `bad_request` | 400 | リクエスト不正 |
| `validation_failed` | 400 | body schema 違反 (details に違反フィールド) |
| `internal_error` | 500 | サーバ内部エラー (details は redaction、PII 含めない) |
| `service_unavailable` | 503 | 一時的不可 (Retry-After ヘッダ併用) |

## レート制限 (Phase C で導入検討)

- 現状は **未導入** (個人プロジェクト規模、Cloud Run の無料枠で十分)
- 将来導入時は Cloudflare Rate Limiting または Backend の Ktor `RateLimit` plugin を採用検討
- 採用時は ADR 起票 (§4.5-3 外部サービス / -5 中核方針)

## CORS

- `/api/idols/*` `/api/brands` `/api/colors` は **全 origin 許可** (`Access-Control-Allow-Origin: *`)
- `/api/me/*` は **明示的な allowlist** (`https://colormaster.example.com` 等のみ) — wasmJs クライアントのデプロイ先と一致
- preflight (`OPTIONS`) は Ktor の CORS plugin で処理
- 詳細は `.claude/rules/backend-auth.md` (A2-2 で本格化)

## 健全性チェック (`/health`)

```json
{
  "status": "ok | degraded | down",
  "checks": {
    "users_db": "ok",
    "litestream_lag_seconds": 2,
    "jwks_cache_age_seconds": 1800
  }
}
```

- Cloud Run の `livenessProbe` / `readinessProbe` で参照
- `litestream_lag_seconds` が閾値超で `degraded`、これがアラート発火基準 (C7 で整備)
- 詳細は `infrastructure.md` の「モニタリング」セクション

## ロギング (PII 配慮、`pii.md` 準拠)

- **構造化ログ** (JSON line) を Cloud Logging に出力
- `uid` は ログに含めて良い (内部識別子だが PII 同等取扱、redaction 不要)
- `email` / `name` / `picture` / IP / Authorization ヘッダの **生値はログ禁止** (`.claude/rules/logging.md` で機械検証、A2-2)
- error level ログは PII fields を `[REDACTED-*]` プレースホルダで置換してから出力

## Single Source of Truth

| ファイル | 役割 |
|---|---|
| **`colormaster-api.yaml`** (OpenAPI 3.1) | **全リクエスト/レスポンス JSON スキーマの SoT**、エラー schema、security scheme |
| `auth.md` / `idols.md` / `me.md` | 使い方 / 設計判断 / 例外パターン / PII 取扱 (yaml に書きにくい散文) |
| ADR 0008 / 0011 / 0020 | データ永続化 / 認証 / PII 保護の決定根拠 |

`*.md` 側ではリクエスト/レスポンス JSON 例を **重複記載しない** (yaml に集約、変更時の同期コスト回避)。

## 現状 (B0 段階、2026-05-17 時点)

- `backend/server/` は Gradle module 骨格のみ、API 実装は **未着手** (C5 で本格化)
- `colormaster-api.yaml` は `info.version: 0.0.0-skeleton`、A2-5 で paths / schemas を骨格分は拡張、本格実装時に `0.1.0-alpha` に bump
- `/api/me/*` は GIS 認証 + `users.db` 未実装のため動作不可、`/api/idols/*` も `idols.db` から読むハンドラ未実装

## 関連

- `colormaster-api.yaml` (OpenAPI 3.1 SoT)
- `auth.md` / `idols.md` / `me.md`
- ADR 0008 (Backend SQLite + Litestream + R2) / 0009 (Cloud Run) / 0011 (GIS 統一) / 0020 (PII 保護)
- `.claude/rules/{backend-auth,network-client,logging,error-handling,pii}.md`
- `../architecture/{overview,data-flow,infrastructure,sequences}.md`
