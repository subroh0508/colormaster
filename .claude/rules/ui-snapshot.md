---
id: rules-ui-snapshot
title: UI screenshot baseline 維持規約 (Roborazzi)
status: skeleton
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "composeApp/**/*.kt"
  - "docs/design/inventory/screenshots/**"
  - ".claude/skills/ui-snapshot/**"
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs:
  - ADR-0023
---

# ui-snapshot.md — UI screenshot baseline 維持規約

> Roborazzi (Compose Desktop + Android Robolectric) で 4 パターンの screenshot baseline を
> 維持し、リファクタ時の意図しない UI 変化を visual regression として検出する規約。
> 本格運用は A10 開始後。

## Baseline マトリックス

| デバイス | テーマ | パス |
|---|---|---|
| mobile | light | `docs/design/inventory/screenshots/<composable>-mobile-light.png` |
| mobile | dark | `docs/design/inventory/screenshots/<composable>-mobile-dark.png` |
| desktop | light | `docs/design/inventory/screenshots/<composable>-desktop-light.png` |
| desktop | dark | `docs/design/inventory/screenshots/<composable>-desktop-dark.png` |

## 対象 Composable

- 全 Composable に対応する `@Preview` を用意 (`ui-snapshot` Skill が不在を検出 → Plan 起票)
- 重要画面 (Home / Search / Preview / MyIdols) を最優先で baseline 化、補助コンポーネントは Phase C 内で追加することも許容 (R-22)
- wasmJs 固有 actual は Roborazzi 未対応のため対象外 (R-24)

## Baseline 更新ポリシー

- **baseline 更新は human approve 必須** (誤検出抑制のための `changeThreshold` も併用、R-23)
- 意図的な UI 変更時のみ更新 (リファクタで意図せず変わったら修正)
- 更新コミットは別途切り出し、レビュアーが diff を視認できるように小さく保つ

## 動的色 (ブランドカラー) の扱い

- Preview ではアニメーション停止 + 代表 brand color を固定パラメータで指定 (R-23)
- 別途 brand-color バリエーション Preview を作成して網羅 (アイドル × カラー数 × デバイス × テーマ)
- `changeThreshold` の許容しきい値も併用 (誤検出抑制)

## Gotchas

- **wasmJs Roborazzi 未対応**: commonMain は JVM (Compose Desktop) で screenshot test、wasmJs 固有 actual は Konsist + 単体テストで担保 (R-24)
- Compose Desktop と Android Robolectric では描画微差があり得る → 両方を baseline 化
- Baseline ファイルが PR diff に大量に出るため、PR description に「visual regression 意図的更新」と明記

## 関連

- ADR 0023 (UI 凍結三本柱)
- `docs/harness/plan.md` §3.9 / R-22 / R-23 / R-24
- `.claude/rules/{design-tokens,ui-inventory,behavior-preservation,screenshot-test}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
