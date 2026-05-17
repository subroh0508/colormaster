---
id: rules-composable
title: Composable Screen の実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/features/**/*Screen.kt"
  - "feature/**/*Screen.kt"
  - "core/features/**/composable/**/*.kt"
related_adrs:
  - ADR-0002
  - ADR-0006
  - ADR-0023
---

# composable.md — Composable Screen の実装規約

> Compose Multiplatform (ADR 0002) における `@Composable` 関数の構造・命名・Preview 必須化・
> design tokens 強制を規定する。`@Composable` の引数 contract は UiState 入力 / event callback 出力で固定し、
> Screen から ViewModel への直接参照を禁じる。

## 配置と命名

- 配置: `core/features/<feature>/src/commonMain/kotlin/net/subroh0508/colormaster/features/<feature>/composable/`
- Screen 命名: `<機能>Screen.kt` (例: `SearchIdolsScreen.kt` / `PenlightScreen.kt`)
- 共通 component 命名: `<役割>.kt` (例: `IdolListItem.kt` / `LoadingOverlay.kt`)

## Screen の関数シグネチャ

```kotlin
@Composable
fun SearchIdolsScreen(
    uiState: SearchIdolsUiState,
    onAction: (SearchIdolsUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ...
}
```

- **第 1 引数 `uiState: *UiState`** (画面に表示する全状態)
- **第 2 引数 `onAction: (*UiAction) -> Unit`** (ViewModel への入力 callback)
- **第 3 引数以降 `modifier: Modifier = Modifier`** + 必要に応じ ナビゲーション callback (`onNavigateToDetail: (IdolId) -> Unit`)
- **ViewModel を直接引数に取らない** (Preview / テスト容易性のため、wrapper 関数 (`SearchIdolsRoute`) で ViewModel を解決)

## Route wrapper

```kotlin
@Composable
fun SearchIdolsRoute(
    viewModel: SearchIdolsViewModel = koinViewModel(),
    onNavigateToDetail: (IdolId) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchIdolsScreen(
        uiState = uiState,
        onAction = viewModel::dispatch,
        // ...
    )
}
```

- `*Route` 関数で ViewModel を解決し、`*Screen` に UiState / dispatch を渡す
- ナビゲーション graph の destination として登録するのは `*Route`
- `*Screen` は state-less に近づき、Preview / screenshot test が容易

## Preview 必須化

```kotlin
@Preview
@Composable
private fun SearchIdolsScreenPreview() {
    AppTheme {
        SearchIdolsScreen(
            uiState = SearchIdolsUiState(
                query = "幻奏",
                results = listOf(/* sample */),
            ),
            onAction = {},
        )
    }
}
```

- **全 `*Screen` Composable に `@Preview` を 1 つ以上設置必須** (`ui-snapshot.md` 参照、A10 で Konsist 自動検出)
- Preview 関数は `private` 可視性、命名 `<Screen>Preview` / `<Screen>EmptyPreview` / `<Screen>LoadingPreview` 等
- `AppTheme` でラップ (design tokens を適用)、`Preview` 引数で device / locale / theme を切り替え

## design tokens (DESIGN.md 3 階層) の強制

- **hex 直書き禁止** (`Color(0xFFFF0000)` 等)、`MaterialTheme.colorScheme.primary` 等の Semantic token のみ使用
- **dp / sp の magic number 禁止** (`Padding(16.dp)` 等)、`AppDimensions.spacing.medium` (将来定義) または Compose `MaterialTheme.dimensions.*` 経由
- 詳細は `.claude/rules/design-tokens.md` (A2-3 で本格化)

## i18n 強制

- 文字列 literal を `@Composable` 内に直書きしない (`Text("Search")` 禁止)
- `stringResource(Res.string.search_button)` 経由で `compose-resources` の `strings.xml` から取得
- 詳細は `.claude/rules/i18n.md` 参照 (ADR 0006)

## 機械検証 (A6 / A7 / A10 で導入)

- **Konsist** で以下を強制 (R-22 / R-23):
  - `*Screen` 関数の第 1 引数が `uiState: *UiState`
  - `*Screen` 関数の第 2 引数が `onAction: (*UiAction) -> Unit`
  - `*Screen` 関数に対応する `@Preview` が同ファイル内に 1 つ以上存在
  - Composable 内で hex 直書き (`Color(0x...)`) / dp/sp magic number / 文字列 literal が出現しない
- **Roborazzi** で 4 パターン baseline (mobile/desktop × Light/Dark)、`ui-snapshot.md` 参照

## Gotchas

- **`@Composable` 内で `viewModel()` / `koinViewModel()` を呼ぶのは `*Route` のみ**。`*Screen` は ViewModel 非依存
- **`remember { mutableStateOf(...) }` で local state を持つ場合は注意**。再構成で消えるべき transient state (テキスト入力等) のみ許容、永続化が必要なものは UiState に乗せる
- **`LaunchedEffect` のキー設計**。`uiState.event` で発火する場合、`key1 = uiState.event` で重複起動を防ぐ
- **Wasm 互換性に注意**。`java.awt.*` / `android.content.*` 等のプラットフォーム特有 API を `commonMain` で参照しない (`wasm-compat.md` 参照)

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0006 (i18n + compose-resources)
- ADR 0023 (UI/UX 凍結 三本柱: DESIGN.md + UI Inventory + Roborazzi)
- `.claude/rules/{viewmodel,ui-state,navigation,i18n,design-tokens,ui-snapshot,wasm-compat}.md`
- `docs/architecture/layers.md` (A2-5 で本格化)
