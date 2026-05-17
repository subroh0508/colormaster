---
id: rules-viewmodel
title: ViewModel 層の実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/features/**/*ViewModel.kt"
  - "feature/**/*ViewModel.kt"
related_adrs:
  - ADR-0002
  - ADR-0003
  - ADR-0005
---

# viewmodel.md — ViewModel 層の実装規約

> Compose Multiplatform + 共通 ViewModel (ADR 0002) に基づき、`core/features/<feature>/`
> 配下に配置される `*ViewModel.kt` の構造・公開 API・依存関係を規定する。
> Decompose を撤去済 (ADR 0005)、Navigation 3 + `androidx.lifecycle.ViewModel` を使用。

## 配置と命名

- 配置: `core/features/<feature>/src/commonMain/kotlin/net/subroh0508/colormaster/features/<feature>/viewmodel/*ViewModel.kt`
- 命名: `<機能>ViewModel.kt` (パスカルケース末尾 `ViewModel`)
- 例: `SearchIdolsViewModel.kt` / `MyIdolsViewModel.kt` / `PenlightViewModel.kt`

## 構造

```kotlin
class SearchIdolsViewModel(
    private val repository: IdolColorsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchIdolsUiState())
    val uiState: StateFlow<SearchIdolsUiState> = _uiState.asStateFlow()

    fun dispatch(action: SearchIdolsUiAction) { ... }
}
```

- **`androidx.lifecycle.ViewModel` を継承** (KMP `lifecycle-viewmodel` artifact 経由で共通使用)
- **公開 API は `uiState: StateFlow<*UiState>` と `dispatch(action: *UiAction)` のみ**
- `_uiState: MutableStateFlow` は private、外部から直接 mutate しない
- `viewModelScope` で coroutine を起動、明示的な `Job` 管理は避ける (cancel は ViewModel ライフサイクルに任せる)

## 依存関係

| 種別 | 注入方法 | 例 |
|---|---|---|
| Repository (interface) | constructor 注入 | `private val repository: IdolColorsRepository` |
| `SavedStateHandle` | constructor 注入 (Navigation 3 args 受領) | `savedStateHandle: SavedStateHandle` |
| `CoroutineDispatcher` (テスト容易性のため) | constructor デフォルト引数 (`Dispatchers.Default`) | `private val dispatcher: CoroutineDispatcher = Dispatchers.Default` |

- **直接 `Client` (Ktor / OkHttp 等) を注入しない**。必ず Repository 経由
- **直接 `Context` (Android) / `Activity` (Android) を注入しない**。Wasm / Desktop 互換性が壊れる
- `core/model` (Repository interface) と `core/usecase` (将来追加可) のみ依存可、`core/data` 直接依存は禁止

## UiAction 処理

```kotlin
fun dispatch(action: SearchIdolsUiAction) {
    when (action) {
        is SearchIdolsUiAction.QueryChanged -> handleQueryChanged(action.query)
        SearchIdolsUiAction.SearchClicked -> handleSearchClicked()
    }
}
```

- `when` は exhaustive (sealed interface により網羅性チェック)
- 副作用は `viewModelScope.launch { ... }` で非同期実行、UiState 更新は `_uiState.update { it.copy(...) }`

## エラーハンドリング

- Repository 呼び出しは `runCatching { ... }` で wrap、`onSuccess` / `onFailure` で分岐
- 失敗時は UiState の `error: Throwable?` フィールドに保持、Screen 側で `Snackbar` 表示
- 詳細は `.claude/rules/error-handling.md` 参照

## SavedStateHandle 規約

- Navigation 3 の type-safe args を `savedStateHandle.toRoute<RouteType>()` で受領 (`navigation.md` 参照)
- 検索クエリ等の `restored on process death` 状態は `savedStateHandle["query"] = value` で保存
- 大量データ (検索結果リスト等) は `SavedStateHandle` に保存しない (10KB 上限の Android 制約)

## 機械検証 (A6 で導入)

- **Konsist** で以下を強制 (R-20):
  - `feature/**/*ViewModel.kt` および `core/features/**/*ViewModel.kt` は `androidx.lifecycle.ViewModel` を継承
  - 公開プロパティ (`val` で `public` 可視性) は `uiState: StateFlow<*UiState>` のみ (`dispatch(...)` を除く)
  - `_uiState` は `private` で `MutableStateFlow`
  - constructor に `Client` 系 (`core/network/*Client*`) の型を持たない

## Gotchas

- **`SharedFlow` / `EventFlow` で one-shot event を流すパターンは原則禁止**。UiState に `oneShotEvent: Event?` フィールドを置き、Screen 側で `LaunchedEffect(uiState.oneShotEvent) { ... }` で消費する
- **`ViewModel` 内で `Composable` を呼ばない**。Compose と ViewModel の境界は厳密
- **`viewModelScope` 外で coroutine を起動しない**。`GlobalScope.launch` 禁止、テスト容易性が壊れる
- 旧 Decompose 由来の `ComponentContext` / `Store` API は撤去済 (ADR 0005)。再導入時は別 ADR

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0003 (feature-first モジュール構造)
- ADR 0005 (Decompose 撤去)
- `.claude/rules/{ui-state,composable,navigation,repository,error-handling}.md`
- `docs/architecture/layers.md` (A2-5 で本格化)
