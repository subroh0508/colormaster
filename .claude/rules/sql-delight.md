---
id: rules-sql-delight
title: SqlDelight (SQLite) 実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "**/*.sq"
  - "**/sqldelight/**"
  - "core/database/**/*.kt"
related_adrs:
  - ADR-0002
  - ADR-0008
  - ADR-0010
---

# sql-delight.md — SqlDelight (SQLite) 実装規約

> KMP 対応の type-safe SQLite ラッパー **SqlDelight 2.x** を採用 (ADR 0002)。
> アイドル情報マスタ (`data/idols.db`、in-repo) とユーザーデータ (`users.db`、Backend) の
> 両方で使用 (ADR 0008 / 0010)。**users.db の取り扱いは `db-protection.md` を厳守**。

## 配置

| 種別 | 場所 |
|---|---|
| 共通スキーマ (`*.sq`) | `core/database/src/commonMain/sqldelight/net/subroh0508/colormaster/database/*.sq` |
| Generated code | `<module>/build/generated/sqldelight/code/<DbName>/` (自動生成、commit しない) |
| Driver factory (`expect/actual`) | `core/database/src/<platform>Main/.../driver/*Driver.kt` |
| Migration 関数 (将来) | `core/database/src/commonMain/sqldelight/.../migrations/*.sqm` |

## `*.sq` ファイル規約

```sql
CREATE TABLE Idol (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    name_kana TEXT NOT NULL,
    brand_id TEXT NOT NULL,
    color_hex TEXT NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES Brand(id)
);

CREATE INDEX idol_brand_id_idx ON Idol(brand_id);

selectAll:
SELECT * FROM Idol ORDER BY name_kana;

selectByBrand:
SELECT * FROM Idol WHERE brand_id = :brand_id ORDER BY name_kana;

insertIdol:
INSERT INTO Idol(id, name, name_kana, brand_id, color_hex)
VALUES (?, ?, ?, ?, ?);
```

- テーブル名: **PascalCase** (`Idol`、`Brand`、`UserMyIdol`)
- カラム名: **snake_case** (`brand_id`、`color_hex`)
- query name: **camelCase** + 動詞始まり (`selectAll`、`insertIdol`、`updateColor`)
- パラメータ: 名前付き引数 (`:brand_id`) または位置引数 (`?`)、可読性のため 3 個以上は名前付き

## driver の `expect/actual`

```kotlin
// commonMain
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(ColormasterDatabase.Schema, context, "colormaster.db")
}

// wasmJsMain (Web Worker driver)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = WebWorkerDriver(...)
}

// desktopMain (JDBC SQLite)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
}
```

- platform 別 driver は `wasm-compat.md` の方針に従う
- テストでは `JdbcSqliteDriver.IN_MEMORY` を使用、actual file は触らない

## Repository からの利用

```kotlin
class DefaultIdolColorsRepository(
    private val db: ColormasterDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : IdolColorsRepository {

    override suspend fun searchByName(query: String): List<Idol> =
        withContext(ioDispatcher) {
            db.idolQueries.selectByName("%$query%").executeAsList().map { it.toIdol() }
        }
}
```

- query は `.executeAsList()` / `.executeAsOne()` / `.executeAsOneOrNull()` で同期取得 (suspend 内で呼ぶ)
- 継続購読は `.asFlow().mapToList(...)` (`app.cash.sqldelight:coroutines-extensions`)
- ドメインモデルへの変換は mapping 関数 (`fun Idol.toIdol(): Idol`、generated row → ドメイン)

## 2 つの DB の使い分け

| DB 名 | スキーマ | 用途 | 配置 |
|---|---|---|---|
| `idols.db` (`ColormasterDatabase`) | Idol / Brand / Color | アイドル情報マスタ (read-only) | `data/idols.db` (リポジトリ commit、Docker image 焼込) |
| `users.db` (`UserDatabase`) | UserMyIdol / UserSession | ユーザーデータ (uid のみ + my idols) | Backend 内、Litestream で R2 replicate |

- `idols.db` は Git 内 SQLite、コンテナイメージにも焼込 (ADR 0010)、`.dockerignore` で除外しない
- **`users.db` は absolutely 禁止** (commit / イメージ焼込 / 公開、`db-protection.md` 厳守)

## migration

- スキーマ変更時は `migrations/<version>.sqm` を追加 (SqlDelight 公式の version-based migration)
- アイドル情報 (`idols.db`) は **migration 不要** (sync job で全件再生成、`sync-job.md` 参照)
- ユーザーデータ (`users.db`) は migration 必要、Litestream restore との互換性を確認

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証 (§5.2):
  - `*.sq` ファイル内に PII fields (`email TEXT` / `display_name TEXT`) が含まれない (`users.db` スキーマのみ対象、ADR 0020)
  - `*.sq` の query name に typo (UPPERCASE / snake_case) がない
- **SqlDelight 自身** が compile 時に SQL syntax / type 安全性を検証

## Gotchas

- **`*.sq` の generated code は `build/generated/sqldelight/` に出力**、commit しない
- **複数 `*.sq` ファイル間で同名 query は不可** (`selectAll` を複数 file で定義 → compile error)
- **driver の `Schema.create` は初回起動時のみ呼ぶ**、毎回 drop しない (テストは `IN_MEMORY` で都度作成 OK)
- **`Flow` 経由の query は active subscription が必要**、ViewModel scope 外で collect しない
- Backend (Cloud Run) は Linux JDBC SQLite + Litestream WAL replicate、ローカル開発は `JdbcSqliteDriver.IN_MEMORY` または `:file:` で差し替え可

## 関連

- ADR 0002 (Compose Multiplatform)
- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0010 (アイドル情報 SQLite in-repo)
- SqlDelight: https://cashapp.github.io/sqldelight/
- `.claude/rules/{repository,db-protection,sqlite-data-file,sync-job,r2-litestream,wasm-compat}.md`
