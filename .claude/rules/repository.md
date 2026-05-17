---
id: rules-repository
title: Repository 層の実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/model/**/*Repository*.kt"
  - "core/data/**/*Repository*.kt"
related_adrs:
  - ADR-0002
  - ADR-0003
  - ADR-0008
  - ADR-0010
---

# repository.md — Repository 層の実装規約

> Clean Architecture 風の interface 分離: **interface は `core/model`**、
> **implementation は `core/data`** に配置する (ADR 0003)。ViewModel は interface に依存し、
> data 層は network / local DB の詳細を隠蔽する (ADR 0002 / 0008 / 0010)。

## 配置と命名

| 種別 | 配置 | 命名 |
|---|---|---|
| Repository interface | `core/model/src/commonMain/kotlin/net/subroh0508/colormaster/model/*Repository.kt` | `<ドメイン>Repository.kt` (例: `IdolColorsRepository.kt`) |
| 既定実装 | `core/data/src/commonMain/kotlin/net/subroh0508/colormaster/data/Default*Repository.kt` | `Default<ドメイン>Repository.kt` (例: `DefaultIdolColorsRepository.kt`) |
| テスト用 fake | `core/data/src/commonTest/.../Fake*Repository.kt` | `Fake<ドメイン>Repository.kt` |

## interface の構造

```kotlin
interface IdolColorsRepository {
    suspend fun searchByName(query: String): List<Idol>
    suspend fun findById(id: IdolId): Idol?
    fun observeAll(): Flow<List<Idol>>
}
```

- **`suspend fun` で one-shot 取得**、**`Flow<T>` で継続購読**
- 戻り値は **`Result<T>` ではなく直接 `T` を返し**、例外は呼び出し側 (ViewModel) で `runCatching` する (`error-handling.md` 参照)
- ドメインモデル (`Idol` / `IdolId` / `Brand` 等) のみを返す。`Response` / `DTO` 等の network 層型を露出しない

## 既定実装の構造

```kotlin
class DefaultIdolColorsRepository(
    private val sparqlClient: ImasparqlClient,
    private val sqlDelightDb: ColormasterDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : IdolColorsRepository {

    override suspend fun searchByName(query: String): List<Idol> =
        withContext(ioDispatcher) {
            sparqlClient.searchByName(query).map { it.toIdol() }
        }
}
```

- constructor 注入で `Client` (`core/network/*`) と `SqlDelight` を受領
- `suspend` 関数では `withContext(ioDispatcher)` で IO スレッドに切り替え
- DTO → ドメインモデル変換は **mapping 関数** (`fun *Dto.toIdol(): Idol`) で集約

## 依存関係

- **interface は外部依存ゼロ** (`core/model` は network / data に依存しない)
- 既定実装は `core/network/*` (Client) と `core/database/*` (SqlDelight) に依存可
- **ViewModel は interface のみに依存**、`Default*Repository` を直接参照しない

## キャッシュ戦略

| 種別 | 場所 | TTL |
|---|---|---|
| アイドル情報マスタ (`Idol`) | `data/idols.db` (SqlDelight、ローカルファイル) | 同期 PR 経由で更新 (`sync-job.md`) |
| ユーザーデータ (`MyIdols`) | Backend SQLite (`users.db`) + memory cache (TTL 15 分) | ADR 0008 / 0020 |
| GIS userinfo (display name / email / picture) | memory cache のみ (TTL 15 分、PII 最小化) | `pii.md` 参照 |

- 永続化が必要な PII は **`uid` のみ** (ADR 0020)
- memory cache は `MutableStateFlow<Map<K,V>>` を Repository 内に保持、TTL 切れで再取得

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `core/model/**/*Repository.kt` は `interface` のみ (class 禁止)
  - `core/data/**/Default*Repository.kt` は対応する interface を実装している
  - `core/model` モジュールが `core/network` / `core/data` を import していない
  - Repository interface の戻り値型に `Response` / `Dto` 等の network 層型が含まれない

## Gotchas

- **`Result<T>` を返さない**。例外を投げ、呼び出し側 (ViewModel) が `runCatching` で wrap (`error-handling.md` 参照)
- **Repository 内で UI 関連処理をしない** (`Snackbar` 表示 / Navigation 等は呼び出し側)
- **`Default*Repository` を ViewModel が直接型として参照しない**。DI (Koin 等) で interface 解決
- **CoroutineDispatcher のテスト容易性**: constructor デフォルト引数で `Dispatchers.Default` を受け取り、テストでは `TestDispatcher` を注入
- 旧 Firebase Firestore 経由の Repository (`core/network/firestore/*`) は撤去予定 (ADR 0011)、現行コードに残る場合は `removed-modules.md` 参照

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0003 (feature-first モジュール構造)
- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0010 (アイドル情報 SQLite in-repo)
- `.claude/rules/{viewmodel,network-client,sql-delight,error-handling,pii}.md`
- `docs/architecture/layers.md` / `docs/architecture/data-flow.md` (A2-5 で本格化)
