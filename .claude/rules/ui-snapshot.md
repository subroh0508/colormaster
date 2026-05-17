---
id: rules-ui-snapshot
title: UI screenshot baseline 維持規約 (Roborazzi)
status: stable
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "composeApp/**/*.kt"
  - "docs/design/inventory/screenshots/**"
  - ".claude/skills/ui-snapshot/**"
related_adrs:
  - ADR-0023
related_plan: docs/harness/plan.md §3.9 / R-22 / R-23 / R-24
---

# ui-snapshot.md — UI screenshot baseline 維持規約

> Roborazzi (Compose Desktop + Android Robolectric) で 4 パターンの screenshot baseline を
> 維持し、リファクタ時の意図しない UI 変化を visual regression として検出する規約。
> 本格運用は A10 開始後、`ui-snapshot` Skill が DESIGN.md / UI Inventory / baseline の
> 三本柱を自動生成する (ADR 0023)。

## Baseline マトリックス (4 パターン)

| デバイス | テーマ | パス |
|---|---|---|
| mobile | light | `docs/design/inventory/screenshots/<composable>-mobile-light.png` |
| mobile | dark | `docs/design/inventory/screenshots/<composable>-mobile-dark.png` |
| desktop | light | `docs/design/inventory/screenshots/<composable>-desktop-light.png` |
| desktop | dark | `docs/design/inventory/screenshots/<composable>-desktop-dark.png` |

## 命名規約

- **`<composable>`**: 対象 Composable 関数名を kebab-case (`HomeScreen` → `home-screen`)
- **`<device>`**: `mobile` (Android Robolectric) または `desktop` (Compose Desktop)
- **`<theme>`**: `light` または `dark`
- **拡張子**: `.png` (Roborazzi 既定)
- 例: `home-screen-mobile-light.png` / `idol-card-desktop-dark.png`

## 対象 Composable

- 全 Composable に対応する **`@Preview` を用意** (`ui-snapshot` Skill が不在を検出 → Plan 起票)
- **重要画面 (Home / Search / Preview / MyIdols) を最優先で baseline 化**、補助コンポーネントは Phase C 内で追加することも許容 (R-22)
- **wasmJs 固有 actual は Roborazzi 未対応** のため対象外 (R-24)、commonMain は JVM (Compose Desktop) で screenshot test
- 動的色 (ブランドカラー) は **代表ブランド (例: `imas-cg-rin`) で固定** + 別途 brand-color バリエーション Preview で網羅

## Baseline 更新ポリシー

- **baseline 更新は human approve 必須** (`merge-readiness.md` 3 条件と整合)
- 意図的な UI 変更時のみ更新 (リファクタで意図せず変わったら修正)
- 更新コミットは別途切り出し、レビュアーが diff を視認できるように小さく保つ
- PR description に「visual regression 意図的更新」を必ず明記 (`pr-template.md` refactor.md の Behavior Preservation 証拠と整合)

## 誤検出抑制 (`changeThreshold` 既定値)

- **`changeThreshold = 0.01` (1% 以下の pixel 差は許容)**: フォントレンダリング差 / GPU 微差で発生する false-positive を抑制
- フォント差が大きい場合は **`compareOptions(ImageDiffComparator.ssim)`** を併用検討 (SSIM ベース、A10 で本格化)
- 完全一致が必要なケース (重要画面の核心) は `changeThreshold = 0.0` で個別オーバーライド可

## 動的色 (ブランドカラー) の扱い

- Preview ではアニメーション停止 + 代表 brand color を **固定パラメータ** で指定 (R-23)
- 別途 brand-color バリエーション Preview を作成して網羅 (アイドル × カラー数 × デバイス × テーマ)
- `changeThreshold` の許容しきい値も併用 (誤検出抑制)

## Roborazzi 設定 (`build.gradle.kts`)

```kotlin
// 概要: A10 完了時に本格化
// commonMain: Compose Desktop JVM target
// androidMain: Robolectric (Android JVM target)
// wasmJs: Roborazzi 未対応のため除外
roborazzi {
    outputDir.set(file("docs/design/inventory/screenshots"))
    compareOptions {
        changeThreshold.set(0.01)
    }
}
```

詳細は `.claude/rules/screenshot-test.md` (A10 で本格化) 参照。

## Compose Desktop と Android Robolectric の差分

- **描画微差は両方を baseline 化** して個別管理 (`*-mobile-*` / `*-desktop-*`)
- フォント差: Desktop は OS フォント、Android Robolectric は Roboto (`Robolectric` の `LooperMode` 設定)
- 解像度差: mobile = 1080x2400 (Pixel 7 相当)、desktop = 1920x1080 (FHD)
- ピクセル密度: mobile = 3.5x (xxxhdpi)、desktop = 1x (`dp == px`)

## 機械検証 (A6 + A10 で段階導入)

- **A6**: Konsist で全 Composable (`@Composable` annotation) に対応する `@Preview` 存在検証
- **A10**: `./gradlew verifyRoborazziDebug` を `./gradlew check` に統合、4 パターン baseline diff が `changeThreshold` 内で完走することを CI 必須化
- **A10**: `code-reviewer` の visual-regression aspect で baseline 命名規約 / 4 パターン揃え / 意図的更新時の PR description 明記を検証

## Gotchas

- **wasmJs Roborazzi 未対応**: commonMain は JVM (Compose Desktop) で screenshot test、wasmJs 固有 actual は Konsist + 単体テストで担保 (R-24)
- **Compose Desktop と Android Robolectric では描画微差**があり得る → 両方を baseline 化
- **Baseline ファイルが PR diff に大量に出る**ため、PR description に「visual regression 意図的更新」と明記、`refactor.md` テンプレの Behavior Preservation 証拠と統合
- **4 パターン baseline は必ずペアでリンク** (`docs/design/inventory/<screen>.md` の `related_screenshots` で参照)、1 パターン欠落 = CI 失敗を A10 完了後に enable
- **`changeThreshold` のデフォルトは 0.01**、core 画面は 0.0 で厳格化、補助コンポーネントは 0.05 まで緩和可
- **動的色 Preview は代表ブランド固定 + バリエーション Preview 別建て**、ハードコード回避と網羅を両立 (`design-tokens.md` と整合)
- **Roborazzi baseline 生成コマンド**: `./gradlew recordRoborazziDebug` (生成) / `./gradlew verifyRoborazziDebug` (検証)、誤って record を CI で実行しないよう注意
- **`docs/design/inventory/screenshots/` パスは複数 rule で多重参照** (PR #135 レトロ Try): 本 rule (`paths` + 本文 + 命名規約) / `design-tokens.md` / `ui-inventory.md` / `code-reviewer-aspects.md` (visual-regression aspect の baseline 検証) で参照。A10 で本格化時にパス変更が発生する場合は **4 rule + Skill 群を同 PR で更新**、片方だけ変更すると参照漏れが発生

## 関連

- ADR 0023 (UI 凍結三本柱)
- `docs/harness/plan.md` §3.9 / R-22 / R-23 / R-24
- `.claude/rules/{design-tokens,ui-inventory,behavior-preservation,screenshot-test,composable,wasm-compat}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
- `docs/design/inventory/screenshots/` (baseline 配置先)
