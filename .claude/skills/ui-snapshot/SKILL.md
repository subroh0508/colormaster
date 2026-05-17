---
name: ui-snapshot
description: |
  Konsist で Composable をスキャンして @Preview 不在を検出 → Plan 起票、
  Roborazzi で 4 パターン (mobile/desktop × Light/Dark) screenshot baseline 生成、
  DESIGN.md と UI Inventory のドラフト起草、hex/sp/dp ハードコード検出 →
  tokens 化提案を行う。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §6.2 A10 / ADR 0023
related_rules:
  - .claude/rules/ui-snapshot.md
  - .claude/rules/design-tokens.md
  - .claude/rules/ui-inventory.md
  - .claude/rules/behavior-preservation.md
---

# ui-snapshot (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A10 で行う。

## 役割

- **Preview スキャン**: Konsist で全 Composable を走査して `@Preview` 不在を検出、Plan で追加 (段階的)
- **Screenshot baseline 生成**: Roborazzi (Compose Desktop + Android Robolectric) で 4 パターン (mobile-light / mobile-dark / desktop-light / desktop-dark) を `docs/design/inventory/screenshots/` に commit
- **DESIGN.md 起草**: 色・タイポ・スペーシング・radii を実コードから抽出 + Rationale を AI 起草 → 人間レビュー必須
- **UI Inventory 生成**: `docs/design/inventory/{screens,components,states,flows}/*.md` を全件作成
- **デザイントークン化提案**: hex / sp / dp ハードコードを検出して tokens 化を提案

## Gotchas

- **Baseline 更新は human approve 必須** (誤検出を抑制する `changeThreshold` も併用、R-23)。
- wasmJs は Roborazzi 未対応のため commonMain は JVM (Compose Desktop) で screenshot test (R-24)。
- ブランドカラーなど動的色は Preview でアニメーション停止 + 代表値固定 + バリエーション Preview で網羅 (R-23)。
- 重要画面 (Home / Search / Preview / MyIdols) を最優先で baseline 化、補助コンポーネントは Phase C 内で追加することも許容 (R-22)。

## 関連

- `docs/harness/plan.md` §6.2 A10
- ADR 0023 (UI 凍結三本柱)
- `.claude/rules/{ui-snapshot,design-tokens,ui-inventory,behavior-preservation}.md`
