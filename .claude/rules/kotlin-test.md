---
id: rules-kotlin-test
title: Kotlin テスト規約 (Kotest)
status: stable
last_updated: 2026-05-17
paths:
  - "**/*Spec.kt"
  - "**/*Test.kt"
  - "**/commonTest/**/*.kt"
  - "**/androidUnitTest/**/*.kt"
  - "**/wasmJsTest/**/*.kt"
  - "**/desktopTest/**/*.kt"
related_adrs:
  - ADR-0004
  - ADR-0013
  - ADR-0016
---

# kotlin-test.md — Kotlin テスト規約 (Kotest)

> 共通テストフレームワークに **Kotest 5.x** を採用 (ADR 0004)。`DescribeSpec` を基本スタイルとし、
> KMP の commonTest で共通テストを書き、必要に応じて platform-specific test (`androidUnitTest` /
> `wasmJsTest` / `desktopTest`) で actual 実装を検証する。

## 命名と配置

| 種別 | 配置 | 命名 |
|---|---|---|
| 共通テスト | `<module>/src/commonTest/kotlin/.../*Spec.kt` | `<実装>Spec.kt` (例: `SearchIdolsViewModelSpec.kt`) |
| Android 専用 | `<module>/src/androidUnitTest/kotlin/.../*AndroidSpec.kt` | suffix `AndroidSpec` |
| Wasm 専用 | `<module>/src/wasmJsTest/kotlin/.../*WasmSpec.kt` | suffix `WasmSpec` |
| Desktop 専用 | `<module>/src/desktopTest/kotlin/.../*DesktopSpec.kt` | suffix `DesktopSpec` |
| Screenshot (Roborazzi) | `<module>/src/jvmTest/kotlin/.../*ScreenshotTest.kt` | `screenshot-test.md` 参照 |

- ペアリング (`SearchIdolsViewModel.kt` ⇄ `SearchIdolsViewModelSpec.kt`) は `test-paired-class.md` で Konsist 検証

## スタイル: `DescribeSpec`

```kotlin
class SearchIdolsViewModelSpec : DescribeSpec({
    describe("dispatch(QueryChanged)") {
        it("updates uiState.query") {
            val viewModel = SearchIdolsViewModel(FakeIdolColorsRepository())
            viewModel.dispatch(SearchIdolsUiAction.QueryChanged("Hoshii"))
            viewModel.uiState.value.query shouldBe "Hoshii"
        }

        context("when query is empty") {
            it("clears results") { ... }
        }
    }
})
```

- 階層: `describe("対象 / 状況")` → `context("条件")` → `it("期待")`
- 動詞は **現在形** (`updates`, `clears`)、Compose Multiplatform の英語 / 日本語混在は許容 (テスト内のみ)
- 1 it 内のアサーションは **1-3 個まで**、複数の振る舞いをチェックしたい場合は it を分割

## アサーション

- **Kotest matchers** (`shouldBe` / `shouldContain` / `shouldThrow<...>`) を使用
- 浮動小数点比較は `shouldBe(expected plusOrMinus 0.01)`
- `runTest { ... }` でコルーチン処理を検証 (`kotlinx-coroutines-test`)

## fixture 規約

- 共通 fixture は `<module>/src/commonTest/.../fixture/*.kt` に配置
- `IdolFixture.kt`、`UserFixture.kt` 等で `fun givenIdol(id: String = "test-1"): Idol = ...` パターン
- **テスト fixture のメールアドレスは `@example.com` ドメイン限定** (`pii.md` 参照、R-21)
- **uid は連番文字列** (`test-uid-001`)、`sub` claim 風の 21 桁数字は避ける

## Repository / Network のテスト

- Repository テストは `Fake<実装>Repository` を `core/data/commonTest/.../fake/` に置く
- Network Client は **MockEngine** (Ktor) で HTTP モック化、実エンドポイントは叩かない
- DB テストは SqlDelight の **InMemory driver** で実行 (`sql-delight.md` 参照)

## カバレッジ目標 (ADR 0013)

- **段階達成**: Line/Branch coverage を 段階的に 100% に近づける
- 詳細な閾値 / 段階表は `.claude/rules/coverage-100.md` (A7 で本格化)
- カバレッジ計測は **kover** plugin、CI で `./gradlew koverHtmlReport` 出力

## Spec coverage (ADR 0016)

- **`@Spec("SPEC-IDOL-001-3")`** annotation でテストと仕様 ID を紐付け
- `spec-traceability.md` (A7 で本格化) 参照
- Konsist でカバー漏れ仕様 (annotation 未参照 SPEC ID) を検出

## 機械検証 (A6 / A7 で導入)

- **Konsist** で以下を検証 (R-21 / R-22):
  - `*Spec.kt` が `DescribeSpec` を継承 (FunSpec / BehaviorSpec は warning)
  - 実装クラス ⇄ Spec のペアリング (`test-paired-class.md`)
  - fixture 内のメールアドレス文字列が `@example.com` で終わる
- **Kover** で line / branch coverage を CI で取得
- **PITest** (`mutation-testing.md`、A7) で mutation score を計測

## Gotchas

- **`runBlocking` を test に使わない**。`runTest { ... }` を使用 (Kotest + kotlinx-coroutines-test 統合)
- **`Dispatchers.Main` を直接置換しない**。constructor 注入で `TestDispatcher` を受け取る (`viewmodel.md` 参照)
- **wasm-js test は実行速度が遅い**ことがある。共通ロジックは commonTest、wasm 固有検証のみ wasmJsTest
- **Screenshot test は Android 物理デバイス不要**、Roborazzi (Compose JVM) で `screenshot-test.md` に従う
- `assertThat(...)` (truth-style) は使わず Kotest matchers に統一

## 関連

- ADR 0004 (テスト戦略概観)
- ADR 0013 (Line/Branch 段階達成)
- ADR 0016 (Spec coverage)
- Kotest: https://kotest.io/
- `.claude/rules/{viewmodel,repository,network-client,screenshot-test,test-paired-class,coverage-100,spec-traceability,mutation-testing,pii}.md`
