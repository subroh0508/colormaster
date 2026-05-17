---
id: rules-network-client
title: Network Client の実装規約 (Ktor)
status: stable
last_updated: 2026-05-17
paths:
  - "core/network/**/*Client*.kt"
  - "core/network/**/Http*.kt"
related_adrs:
  - ADR-0002
  - ADR-0007
  - ADR-0009
  - ADR-0011
  - ADR-0014
---

# network-client.md — Network Client の実装規約 (Ktor)

> Compose Multiplatform 配下で **Ktor Client** を共通実装に採用 (ADR 0002)。
> プラットフォーム別 engine は `expect/actual` で切り替え (Android: OkHttp / wasm-js: JS Fetch / Desktop: CIO)。
> Repository に依存される側で、ドメインモデル変換は Repository 側で行う (`repository.md` 参照)。

## 配置と命名

| 種別 | 配置 | 命名 |
|---|---|---|
| Client interface (commonMain) | `core/network/<api>/src/commonMain/.../*Client.kt` | `<API>Client.kt` (例: `ImasparqlClient.kt` / `AuthClient.kt`) |
| 実装 (`expect/actual` 用) | 同上 + `androidMain` / `wasmJsMain` / `desktopMain` の `expect/actual` Client factory | `<API>Client.<platform>.kt` |
| HttpClient factory | `core/network/<api>/src/<platform>Main/.../di/*HttpClient.kt` | `Android*HttpClient.kt` / `Js*HttpClient.kt` / `Desktop*HttpClient.kt` |
| DTO | `core/network/<api>/src/commonMain/.../dto/*Dto.kt` | `<Entity>Dto.kt` |

## Client interface

```kotlin
interface ImasparqlClient {
    suspend fun searchByName(query: String): List<IdolDto>
    suspend fun findById(id: String): IdolDto?
}
```

- **`suspend fun` 必須**、戻り値は DTO (network 層の型)
- ドメインモデル (`Idol`) の変換は Repository 側、本層では DTO のまま
- 例外は `IOException` / `ResponseException` (Ktor) / `SerializationException` をそのまま投げる (`error-handling.md` 参照)

## HttpClient 設定

```kotlin
fun HttpClientConfig<*>.commonConfig() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 10_000
    }
    install(Logging) {
        level = if (BuildKonfig.IS_DEBUG) LogLevel.HEADERS else LogLevel.NONE
        sanitizeHeader { it == HttpHeaders.Authorization }
    }
}
```

- **TLS 1.2+ 必須**、HTTP は禁止 (公開 endpoint も localhost 開発除く)
- timeout: request 30s / connect 10s (適宜調整可、ただし設定なし禁止)
- Authorization ヘッダは Logging plugin で **sanitize** (PII / Secrets が log に出ない)

## プラットフォーム engine

| Target | Engine | 備考 |
|---|---|---|
| Android | OkHttp | `io.ktor:ktor-client-okhttp` + Android SSL session cache |
| wasm-js | JS Fetch | `io.ktor:ktor-client-js`、HTTPS のみ、CORS 必須 |
| Desktop (JVM) | CIO | `io.ktor:ktor-client-cio` (純 Kotlin、wasm でも理論上動くが Android 排他に注意) |

- **OkHttp は Wasm 非互換** (`wasm-compat.md` 参照)、`core/network` の commonMain で `okhttp3.*` を import しない
- engine 選択は `core/network/<api>/build.gradle.kts` で source set ごとに dependency を分ける

## DTO 規約

```kotlin
@Serializable
data class IdolDto(
    val id: String,
    val name: String,
    @SerialName("brand_id") val brandId: String?,
)
```

- **`@Serializable` 必須** (`kotlinx.serialization`)
- snake_case ⇄ camelCase は `@SerialName` で明示
- `null` 許容フィールドはサーバ仕様に従い、デフォルト値は **必要時のみ** 設定 (silently 0 / "" は誤認の元)

## Backend API (`colormaster-api.yaml`) との対応

- OpenAPI 3.1 spec (`docs/api/colormaster-api.yaml`) と DTO / endpoint 名を **1:1 対応**
- spec 変更時は DTO を同 PR 内で更新、CI で diff 検証 (将来の Lint 候補)
- 認証 endpoint は `backend-auth.md` 参照 (GIS ID Token / JWKS)

## im@sparql endpoint (ADR 0014)

- ローカル開発時は Fuseki Docker (`docker-compose.local.yml` / runbook)
- 本番は外部 im@sparql ホスティング (URL は `BuildKonfig.IMASPARQL_BASE_URL`)
- 切り替えは build config 経由、コード分岐は禁止

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `core/network/**/*Client.kt` (commonMain) は `suspend fun` のみ public、`Flow` 公開は許容 (購読型 endpoint のみ)
  - DTO は `@Serializable` annotation 付与
  - commonMain で `okhttp3.*` / `java.net.*` / `android.*` を import していない
- **Gradle カスタムタスク** で `docs/api/colormaster-api.yaml` と DTO クラス名の整合検証 (将来候補)

## Gotchas

- **commonMain で `HttpClient(OkHttp) { ... }` を直書きしない**。`expect/actual` でプラットフォーム別 factory に分離
- **wasm-js は HTTPS のみ**、HTTP endpoint は CORS / mixed content で動かない
- **TLS 証明書 pinning は不要** (公開 endpoint のみ、自前 CA なし)
- **Authorization header は必ず sanitize**、CI log / レビューコメントに出さない (`secrets.md` redaction 参照)
- Firebase 関連 Client (`core/network/firestore/*`) は撤去予定 (ADR 0011)、`removed-modules.md` 参照

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0007 (im@sparql upstream-driven 同期)
- ADR 0009 (Backend Cloud Run)
- ADR 0011 (Firebase → GIS 移行)
- ADR 0014 (im@sparql ローカル Fuseki)
- `.claude/rules/{repository,backend-auth,sparql,wasm-compat,secrets,error-handling}.md`
- `docs/api/colormaster-api.yaml` (A2-5 で本格化)
