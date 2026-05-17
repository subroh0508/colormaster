---
id: rules-test-paired-class
title: 実装 ⇄ テストクラスペアリング検証
status: stable
last_updated: 2026-05-17
paths:
  - "**/*.kt"
  - "**/*Spec.kt"
related_adrs:
  - ADR-0004
  - ADR-0013
---

# test-paired-class.md — 実装 ⇄ テストクラスペアリング検証

> 主要な実装クラスに対し **1 対 1 のテストクラス** が存在することを Konsist で検証する規約。
> 「テスト書き忘れ」の機械検知と、命名規約 (`*ViewModel` ⇄ `*ViewModelSpec`) の機械強制を担う。

## ペアリング対象

| 実装側 | テスト側 | 配置 |
|---|---|---|
| `*ViewModel.kt` | `*ViewModelSpec.kt` | 同 module の `commonTest` |
| `Default*Repository.kt` | `Default*RepositorySpec.kt` | 同 module の `commonTest` |
| `*Client.kt` (network) | `*ClientSpec.kt` | 同 module の `commonTest` (MockEngine 使用) |
| `*Screen.kt` (Composable) | `*ScreenshotTest.kt` | 同 module の `jvmTest` (Roborazzi、`screenshot-test.md`) |
| `*UseCase.kt` (将来) | `*UseCaseSpec.kt` | 同 module の `commonTest` |
| `*Mapper.kt` (DTO 変換) | `*MapperSpec.kt` | 同 module の `commonTest` |

## ペアリング対象外 (テスト不要 / 別検証で代替)

| 種別 | 理由 |
|---|---|
| `*UiState.kt` / `*UiAction.kt` | data class / sealed interface のみ、振る舞いなし |
| `Route.kt` (Navigation Route 定義) | `@Serializable` data class のみ |
| `*Dto.kt` | `@Serializable` data class のみ |
| `expect class` の宣言 (`commonMain`) | actual 実装側でテスト |
| `package-info.kt` / 拡張関数のみのファイル | 個別 Spec ではなく、関連 Spec 内で検証 |
| `Application.kt` / `MainActivity.kt` 等のエントリポイント | E2E / smoke test で代替 |

## 命名規約 (Konsist で強制)

```kotlin
// 実装
class SearchIdolsViewModel(...) : ViewModel() { ... }

// テスト (同 package 階層、suffix Spec)
class SearchIdolsViewModelSpec : DescribeSpec({ ... })
```

- テストクラス名は **`<実装クラス名>Spec`** (suffix `Spec` 固定)
- 旧 JUnit / Spek の `*Test` suffix は **新規禁止**、`*Spec` に統一 (Kotest)
- 配置 package は実装と一致 (例: `net.subroh0508.colormaster.features.search.viewmodel`)

## Konsist 検証ロジック (擬似コード)

```kotlin
@Test
fun `every ViewModel has a paired Spec`() {
    val viewModels = Konsist.scopeFromProduction()
        .classes()
        .withSuffix("ViewModel")
        .filter { !it.isAbstract && it.parentClass()?.name == "ViewModel" }

    val specs = Konsist.scopeFromTest()
        .classes()
        .withSuffix("Spec")
        .map { it.name }

    viewModels.assertTrue { vm ->
        "${vm.name}Spec" in specs
    }
}
```

- 対象ペアごとに同様の Konsist test を `core/<module>/src/commonTest/konsist/` に配置
- 検証は `./gradlew check` 経由で CI 実行

## Coverage 段階達成 (ADR 0013) との連動

- 1 ペア = 「最低限のテストファイル存在」確認のみ、内容の網羅性 (line / branch coverage) は **kover** で別途検証 (`coverage-100.md`、A7 で本格化)
- 「ペアリング欠如 → CI 失敗」が最初のガードレール、coverage 100% は段階的に目指す

## 機械検証 (A6 で導入)

- **Konsist** で上記ペアリングを検証 (R-21):
  - 実装ファイルが存在し、対応する Spec ファイルが存在しない → CI 失敗
  - 逆方向 (Spec ファイルだけ存在し実装がない) も検出して error 化
  - 例外は **annotation で明示** (`@SkipPairedSpec` を実装側に付与、理由を comment 必須)

## Gotchas

- **`@SkipPairedSpec` の濫用に注意**。理由 (`@SkipPairedSpec("data class only, no behavior")`) を必須化、code-reviewer の test-quality aspect で再評価
- **Composable は `*ScreenshotTest` で代替**、`*Spec` (unit test) は不要 (state-less な前提)
- **interface の Spec は不要**、実装クラスの Spec で振る舞いを検証
- ペアリング検証が「テストを書いた感」だけで終わらないよう、line / branch / mutation coverage と合わせて品質を担保

## 関連

- ADR 0004 (テスト戦略)
- ADR 0013 (Line/Branch 段階達成)
- Konsist: https://docs.konsist.lemonappdev.com/
- `.claude/rules/{kotlin-test,screenshot-test,coverage-100,spec-traceability,naming}.md`
