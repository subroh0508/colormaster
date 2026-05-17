---
id: rules-navigation
title: Navigation 3 + Route 型の実装規約
status: stable
last_updated: 2026-05-17
paths:
  - "**/Route.kt"
  - "**/navigation/**/*.kt"
  - "core/features/**/*Route.kt"
related_adrs:
  - ADR-0002
  - ADR-0005
---

# navigation.md — Navigation 3 + Route 型の実装規約

> Compose Multiplatform 上で `androidx.navigation` (Navigation 3) を採用 (ADR 0002)。
> Decompose 由来の `ComponentContext` ベース navigation は撤去済 (ADR 0005)。
> Route は **type-safe** な `@Serializable` data class / data object で定義し、
> graph 上で `composable<RouteType>` により安全に解決する。

## Route 定義

```kotlin
@Serializable
data object SearchIdolsRoute

@Serializable
data class IdolDetailRoute(val idolId: String)

@Serializable
data class SearchResultRoute(val query: String, val brand: Brand? = null)
```

- 配置: `core/features/<feature>/src/commonMain/.../navigation/Route.kt` (機能ごとに 1 ファイル)
- **`@Serializable` 必須** (Navigation 3 の type-safe args 解決のため、`kotlinx.serialization` plugin)
- 引数なし → `data object`、引数あり → `data class`
- 引数型は **primitive + `@Serializable` data class** に限定

## Graph 構築

```kotlin
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SearchIdolsRoute,
    ) {
        composable<SearchIdolsRoute> {
            SearchIdolsRoute(
                onNavigateToDetail = { idolId ->
                    navController.navigate(IdolDetailRoute(idolId.value))
                },
            )
        }
        composable<IdolDetailRoute> { entry ->
            val route: IdolDetailRoute = entry.toRoute()
            IdolDetailRoute(idolId = IdolId(route.idolId))
        }
    }
}
```

- `composable<RouteType>` で type-safe に destination 登録
- `entry.toRoute<RouteType>()` で引数を decode
- `*Route` (Screen wrapper) を登録、`*Screen` 直接ではない (`composable.md` 参照)

## SavedStateHandle との連携

```kotlin
class IdolDetailViewModel(
    private val repository: IdolColorsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route: IdolDetailRoute = savedStateHandle.toRoute()
    // route.idolId を使用
}
```

- ViewModel は `SavedStateHandle.toRoute<RouteType>()` で Route を復元
- Navigation 3 が `SavedStateHandle` に args を inject する仕組みを利用 (`androidx.navigation.toRoute`)
- ViewModel のテスト時は `SavedStateHandle(mapOf(...))` で fixture を組む

## deep link (将来検討)

- Phase A では deep link 未対応
- 必要時に別 ADR で「Custom URL scheme 採用」「path mapping 規約」を起こす

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証 (R-22):
  - `Route.kt` 内の Route 定義は `@Serializable` annotation 付与
  - Route 引数は primitive (`String` / `Int` / `Long` / `Boolean`) または `@Serializable` data class
  - `commonMain` の Route 定義に `android.*` / `androidx.*` (除く `androidx.navigation` の `@Serializable` 互換) が混入していない

## Gotchas

- **`navigate()` の引数に文字列パスを直書きしない**。`navigate(IdolDetailRoute(idolId.value))` 形式のみ許容
- **Route 引数に `@Composable` / `Color` / `Modifier` 等の Compose 型を含めない**。シリアライズ不能
- **大量の引数を Route に乗せない**。`Set<IdolId>` 等の collection は SavedStateHandle に乗せず、ViewModel が repository から取得する設計に
- **`popBackStack()` / `navigateUp()` の動作差**: `popBackStack` は graph 最下層なら no-op、`navigateUp` は parent destination に戻る。意図に合わせて選択
- Decompose の `Store` API は撤去済 (ADR 0005)、再導入は別 ADR

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0005 (Decompose 撤去)
- `.claude/rules/{viewmodel,composable,ui-state}.md`
- `docs/architecture/sequences.md` (A2-5 で本格化、画面遷移シーケンス図)
