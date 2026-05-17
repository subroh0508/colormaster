---
id: rules-ui-state
title: UiState / UiAction の実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/features/**/*UiState.kt"
  - "core/features/**/*UiAction.kt"
  - "feature/**/*UiState.kt"
  - "feature/**/*UiAction.kt"
related_adrs:
  - ADR-0002
---

# ui-state.md — UiState / UiAction の実装規約

> Compose Multiplatform + UDF (Unidirectional Data Flow) パターンに基づき、
> `*UiState` data class と `*UiAction` sealed interface の構造・命名・更新方法を規定する。
> ViewModel と Screen の境界 contract として機能 (`viewmodel.md` / `composable.md` 参照)。

## 配置と命名

- 配置: `core/features/<feature>/src/commonMain/kotlin/net/subroh0508/colormaster/features/<feature>/viewmodel/*UiState.kt` (および `*UiAction.kt`)
- 命名: ViewModel と 1:1 対応
  - `SearchIdolsViewModel` ⇄ `SearchIdolsUiState` + `SearchIdolsUiAction`
  - `MyIdolsViewModel` ⇄ `MyIdolsUiState` + `MyIdolsUiAction`

## UiState 構造

```kotlin
data class SearchIdolsUiState(
    val query: String = "",
    val results: List<Idol> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
```

- **`data class` で immutable** (`val` フィールドのみ)
- 全フィールドにデフォルト値を持たせる (`MutableStateFlow(SearchIdolsUiState())` で初期化可能にする)
- フィールド型は **primitive / `List<T>` / `Map<K,V>` / `Idol` 等のドメインモデル** に限定
- **`Flow<T>` / `Job` / `CoroutineScope` / `Composable` を含めない** (シリアライズ不能 + Compose 再構成性壊れる)

## UiAction 構造

```kotlin
sealed interface SearchIdolsUiAction {
    data class QueryChanged(val query: String) : SearchIdolsUiAction
    data object SearchClicked : SearchIdolsUiAction
    data class IdolToggled(val idolId: IdolId) : SearchIdolsUiAction
}
```

- **`sealed interface` で網羅性チェック可能** (when 式の exhaustive 判定)
- 引数なしは `data object`、引数ありは `data class` (Kotlin 1.9+ の `data object` 文法)
- 命名は **過去形・命令形** (`QueryChanged` / `SearchClicked` / `RetryRequested`)、現在形は避ける

## UiState の更新

ViewModel 内で:

```kotlin
private fun handleQueryChanged(query: String) {
    _uiState.update { it.copy(query = query, error = null) }
}
```

- **`MutableStateFlow.update { it.copy(...) }`** で atomic に更新
- 直接 `_uiState.value = ...` は競合状態を生むため避ける
- 複数フィールド更新は **1 つの `update` 内で行う** (途中状態を流さない)

## one-shot event の扱い

```kotlin
data class SearchIdolsUiState(
    ...
    val navigateToDetailEvent: Event<IdolId>? = null,
)

// Screen 側
LaunchedEffect(uiState.navigateToDetailEvent) {
    uiState.navigateToDetailEvent?.consume { idolId -> onNavigate(idolId) }
}
```

- **`SharedFlow` で event を流すパターンは原則禁止** (`viewmodel.md` 参照)
- `Event<T>` ラッパー (consume 後 null 化) を UiState に含める
- Screen が consume したら ViewModel に `EventConsumed` action を dispatch して UiState を null 化

## 派生フィールドの扱い

- 派生フィールド (例: `val hasResults: Boolean = results.isNotEmpty()`) は **computed property** で書く (`get() = results.isNotEmpty()`)
- ただし Compose 側で頻繁に参照する場合は `data class` フィールドとして persist (`copy(hasResults = results.isNotEmpty())`) も許容、好みで判断

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証 (R-21):
  - `*UiState` は `data class` で全フィールド `val` (immutable)
  - `*UiState` フィールド型に `Flow<*>` / `Job` / `CoroutineScope` / `@Composable` が含まれない
  - `*UiAction` は `sealed interface` (top-level)
  - `*UiAction` のサブタイプは `data class` または `data object` のみ
  - `*ViewModel` ⇄ `*UiState` ⇄ `*UiAction` の 1:1 対応 (`SearchIdols*` の prefix が揃う)

## Gotchas

- **`UiState` を共有 (`SharedFlow.shareIn`) しない**。ViewModel 内に閉じる
- **`UiState` の equality は data class の auto-generated equals に依存**。`List<Idol>` の中身が `Idol`(data class) になっていない場合、再構成判定が壊れる
- ドメインモデル (`Idol` 等) の追加フィールドが increasing しすぎると **UiState の全フィールド差分判定がコスト化**。必要に応じて画面用 ViewObject (`SearchIdolsItem`) を導入
- **`UiAction` のサブタイプ数が 10 を超えたら画面分割を検討**。1 つの ViewModel が抱えすぎている兆候

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- `.claude/rules/{viewmodel,composable,navigation,error-handling}.md`
- `docs/architecture/state-machines.md` (A2-5 で本格化)
