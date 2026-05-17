---
id: rules-naming
title: 命名規約 (Kotlin / Compose / Test)
status: stable
last_updated: 2026-05-17
paths:
  - "**/*.kt"
  - "**/*.kts"
related_adrs:
  - ADR-0002
  - ADR-0003
---

# naming.md — 命名規約 (Kotlin / Compose / Test)

> Kotlin Coding Conventions に準拠 + 本プロジェクト固有の suffix 規約を規定。
> ViewModel / UiState / Composable / Repository / Client / Test の命名を統一し、
> Konsist 検証 (A6) と人間レビューの両方で機械的に判定可能にする。

## 一般 (Kotlin Coding Conventions 準拠)

| 種別 | 規約 | 例 |
|---|---|---|
| package | 全小文字、ドメイン階層 (`net.subroh0508.colormaster.<module>.<feature>`) | `net.subroh0508.colormaster.features.search.viewmodel` |
| class / interface / object | パスカルケース、名詞 | `IdolColorsRepository`、`SearchIdolsViewModel` |
| function | キャメルケース、動詞始まり (一部例外あり) | `searchByName(query: String)`、`onAction(action: ...)` |
| Composable function | パスカルケース | `SearchIdolsScreen`、`IdolListItem` |
| property / val / var | キャメルケース、名詞 | `uiState`、`idolColorsRepository` |
| const val (top-level / object) | UPPER_SNAKE_CASE | `MAX_RESULTS_PER_PAGE` |
| 型パラメータ | 単一文字 or 短い大文字 | `T`、`K`、`V`、`TIdol` (将来の bound 化時のみ) |

## suffix 規約

| suffix | 用途 | 例 |
|---|---|---|
| `ViewModel` | `androidx.lifecycle.ViewModel` 派生 (`viewmodel.md`) | `SearchIdolsViewModel` |
| `UiState` | `data class` 画面状態 (`ui-state.md`) | `SearchIdolsUiState` |
| `UiAction` | `sealed interface` 画面入力 (`ui-state.md`) | `SearchIdolsUiAction` |
| `Screen` | `@Composable` 画面 (`composable.md`) | `SearchIdolsScreen` |
| `Route` | `@Composable` Route wrapper (ViewModel 解決) / `@Serializable` 型 (Nav 3 destination) | `SearchIdolsRoute` |
| `Repository` | interface in `core/model` (`repository.md`) | `IdolColorsRepository` |
| `Default*Repository` | 既定実装 in `core/data` | `DefaultIdolColorsRepository` |
| `Fake*Repository` | テスト用 fake in `core/data/commonTest` | `FakeIdolColorsRepository` |
| `Client` | network interface in `core/network` (`network-client.md`) | `ImasparqlClient` |
| `Dto` | network DTO (`network-client.md`) | `IdolDto` |
| `Spec` | Kotest spec (`kotlin-test.md`) | `SearchIdolsViewModelSpec` |
| `Test` | (Spek / JUnit 5 残存時のみ、原則 `Spec` に統一) | — |

## ドメインモデル命名 (ColorMaster 固有)

- `Idol` (アイドル)、`IdolId` (値オブジェクト、`@JvmInline value class`)
- `Brand` (THE iDOLM@STER ブランド: 765 / シンデレラ / ミリオン / SideM / シャイニーカラーズ / 学園)
- `Color` (アイドル固有色、`ColorHex` 値オブジェクト)
- `Tan(担当)` / `Oshi(推し)` (DB のローマ字でなくドメイン用語で日本語の意図を保持)

詳細用語は `docs/glossary.md` (A2-4 で本格化) を参照。

## ファイル名

- **1 ファイル 1 top-level 宣言を原則** とする (例外: `data class` と `companion object` の同居、`sealed interface` とそのサブタイプ)
- ファイル名は **top-level 宣言の名前と一致** (`SearchIdolsViewModel.kt` 内に `class SearchIdolsViewModel`)
- 例外: `Route.kt` (`object SearchIdolsRoute` + `data class IdolDetailRoute` 等、機能内の Route を 1 ファイルに集約)、`*Mapper.kt` (DTO → ドメイン mapping 関数群)

## テスト命名

```kotlin
class SearchIdolsViewModelSpec : DescribeSpec({
    describe("dispatch(QueryChanged)") {
        it("updates uiState.query") { ... }
        context("when query is empty") {
            it("clears results") { ... }
        }
    }
})
```

- 実装クラス + `Spec` (例: `SearchIdolsViewModelSpec`)
- 配置: 実装と同じ package 階層の `commonTest` (`kotlin-test.md` 参照)
- ペアリングは Konsist `test-paired-class.md` で検証

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証 (R-22):
  - suffix と配置の整合 (`*ViewModel.kt` が `core/features/**/viewmodel/` 配下)
  - DTO は `*Dto` suffix + `@Serializable`
  - top-level `class` 名とファイル名一致 (Kotlin 標準だが、Konsist で再検証)
- **Detekt 規約** で命名 lint (A6 で `detekt-formatting` 導入時)

## Gotchas

- **`*Manager` / `*Helper` / `*Util` suffix は避ける** (責務不明瞭、Repository / Service / Mapper に分解)
- **`Default*` prefix は Repository 実装のみ**。他の context (例: Default config) は別 suffix (`*Config` / `*Settings`)
- **Hungarian notation 禁止** (`strQuery` / `lstResults` 等)
- 旧 Decompose 由来の `*Component` / `*Store` (ADR 0005 撤去前の遺物) は削除済、再導入は別 ADR

## 関連

- ADR 0002 / 0003 (アーキ + モジュール構造)
- Kotlin Coding Conventions: https://kotlinlang.org/docs/coding-conventions.html
- `.claude/rules/{viewmodel,ui-state,composable,navigation,repository,network-client,kotlin-test,test-paired-class}.md`
- `docs/glossary.md` (A2-4 で本格化)
