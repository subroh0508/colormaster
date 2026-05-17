---
id: rules-screenshot-test
title: スクリーンショットテスト規約 (Roborazzi)
status: stable
last_updated: 2026-05-17
paths:
  - "**/*ScreenshotTest.kt"
  - "**/screenshots/**"
  - "**/composable/**/*.kt"
related_adrs:
  - ADR-0004
  - ADR-0023
---

# screenshot-test.md — スクリーンショットテスト規約 (Roborazzi)

> **Roborazzi** (Compose JVM 上で Robolectric なしで動く screenshot test ライブラリ) を採用 (ADR 0004 / 0023)。
> JVM Compose Desktop + Android 想定で **4 パターン (mobile/desktop × Light/Dark) baseline** を維持。
> wasmJs 固有 actual は対象外 (JVM のみ実行)、UI 凍結三本柱 (ADR 0023) の一角を担う。

## 4 パターン baseline

| 解像度 | テーマ | 用途 |
|---|---|---|
| Mobile (360 × 640) | Light | Android スマホ縦向き |
| Mobile (360 × 640) | Dark | 同上、ダークテーマ |
| Desktop (1280 × 800) | Light | Desktop / Web (Wasm) |
| Desktop (1280 × 800) | Dark | 同上、ダークテーマ |

- 全 `*Screen.kt` Composable について 4 パターン baseline を生成
- baseline 画像は `<module>/src/jvmTest/screenshots/<Screen>/<pattern>.png` に格納 (git LFS は使わない、PNG をリポジトリ commit)

## テスト構造

```kotlin
class SearchIdolsScreenshotTest {
    @get:Rule val roborazziRule = createRoborazziRule()

    @Test
    fun searchIdolsScreen_mobile_light() = captureRoboImage {
        AppTheme(darkTheme = false) {
            Box(Modifier.size(360.dp, 640.dp)) {
                SearchIdolsScreen(
                    uiState = SearchIdolsUiState(query = "幻奏", results = listOf(/* fixture */)),
                    onAction = {},
                )
            }
        }
    }
    // ... mobile_dark / desktop_light / desktop_dark
}
```

- 配置: `<module>/src/jvmTest/kotlin/.../*ScreenshotTest.kt`
- `captureRoboImage` を 4 回呼ぶ (1 test = 1 pattern) or parameterized
- fixture は `kotlin-test.md` 規約に従う (uid / email は dummy)

## baseline 更新フロー (human approve 必須)

1. UI / design tokens を変更
2. `./gradlew :module:recordRoborazziDebug` で baseline 上書き
3. PR に差分画像を含む
4. **人間レビュアーが baseline 更新を承認** (`code-reviewer` の visual-regression aspect が diff を可視化、最終承認は人間)
5. auto-merge 禁止 (`docs/harness/plan.md` R-15)

## CI での verify

- CI (`.github/workflows/ci.yml`) で `./gradlew verifyRoborazziDebug` を起動
- baseline と diff があれば PR check 失敗、差分画像を artifact として upload
- `code-reviewer` の visual-regression aspect が `*.diff.png` を読んで構造化コメントを post

## 対象スコープと除外

| 対象 | 含む / 除く |
|---|---|
| `core/features/**/composable/*Screen.kt` | ✅ 含む |
| `core/features/**/composable/*Item.kt` / `*Card.kt` (共通 component) | △ 主要 component のみ (5+ 画面で使用される widget) |
| `*Route.kt` (ViewModel 解決 wrapper) | ❌ 含まない (state-less でないため) |
| wasm-js 固有 `expect/actual` の Composable | ❌ JVM 実行不可、Konsist で個別検証 |

## design tokens 整合

- 全 screenshot test で `AppTheme` (design tokens 適用) を必ずラップ
- DESIGN.md の Semantic token (`MaterialTheme.colorScheme.*`) を経由した値でレンダリング
- hex / sp / dp 直書きは Composable 内で禁止 (`composable.md` / `design-tokens.md` 参照)

## 機械検証 (A6 / A7 / A10 で導入)

- **Konsist** で以下を検証 (R-22):
  - `core/features/**/composable/*Screen.kt` に対応する `*ScreenshotTest.kt` が `jvmTest` に存在 (`test-paired-class.md` 統合)
  - `*ScreenshotTest.kt` 内に 4 パターン (mobile/desktop × Light/Dark) の `captureRoboImage` 呼び出し
- **Roborazzi verify** で baseline 整合性を CI 検証

## Gotchas

- **Roborazzi は JVM のみ** (Wasm / iOS screenshot は対象外)。Wasm 互換性は Konsist + 手動検証で担保
- **baseline 更新時はファイルサイズ膨張に注意**。PNG ロスレスでも 4 パターン × 数十画面で MB 級に
- **OS / JDK / フォントの違いで diff が出る**ことがある。CI 環境 (Ubuntu + JDK 17 + Noto Sans CJK) を固定
- **Compose Multiplatform の bug fix で minor diff が出る**ことがある。lib version 更新 PR は baseline 更新も同 PR 内で
- 一括 baseline 更新は **段階的に**、一度に全画面更新すると review 不能になる

## 関連

- ADR 0004 (テスト戦略概観)
- ADR 0023 (UI/UX 凍結 三本柱)
- Roborazzi: https://github.com/takahirom/roborazzi
- `.claude/rules/{composable,design-tokens,ui-snapshot,ui-inventory,behavior-preservation,kotlin-test,test-paired-class}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
