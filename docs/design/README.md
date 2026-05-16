---
id: design-readme
title: UI/UX デザイン (DESIGN.md / Inventory / Baseline) 運用ガイド
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs: [ADR-0023]
---

# UI/UX デザイン (DESIGN.md / Inventory / Baseline) 運用ガイド

> **5 行以内 summary**: Phase C の大規模リファクタ (EPIC-001 〜 EPIC-006) で「ユーザーから
> 見える振る舞いを変えない」ことを保証するため、A10 で **DESIGN.md (デザイントークン) +
> UI Inventory (画面・コンポーネント・状態・フローの記録) + Roborazzi baseline
> (4 パターン screenshot)** の三本柱で UI を凍結する。本ガイドは三本柱の運用方法を記述。

## 三本柱

| 要素 | パス | 役割 |
|---|---|---|
| **DESIGN.md** | `repo root` | Google Stitch 標準準拠の 3 階層デザイントークン (Primitive/Semantic/Component) + Rationale |
| **UI Inventory** | `docs/design/inventory/{screens,components,states,flows}/` | 画面・コンポーネント・状態・フローの記録 |
| **Roborazzi baseline** | `docs/design/inventory/screenshots/` | 4 パターン (mobile/desktop × Light/Dark) screenshot baseline |

## ディレクトリ構造

```
docs/design/
  README.md (本ファイル)
  inventory/
    screens/    (画面ごと: home.md, search.md, preview.md, myidols.md, ...)
    components/ (コンポーネントごと: idol-card.md, brand-chip.md, ...)
    states/     (状態別: empty.md, loading.md, error.md, ...)
    flows/      (ユーザーフロー: login.md, add-favorite.md, ...)
    screenshots/ (Roborazzi 生成 baseline PNG: <composable>-<device>-<theme>.png)
```

## A10 までの段階導入

| フェーズ | 内容 |
|---|---|
| B0 | 本ディレクトリ骨格と DESIGN.md 骨格を配置 (tokens セクションは空) |
| A6 | Konsist で Composable スキャン基盤 (hex/sp/dp ハードコード検出など) を整備 |
| A10 | `ui-snapshot` Skill が DESIGN.md / UI Inventory / Roborazzi baseline を全件生成、`code-reviewer` の visual-regression / design-tokens aspect を enable |
| Phase C | 各 Epic で Behavior Preservation を強制 (リファクタ時に baseline と spec-conformance が同時に green であること) |

## 関連 rules

- `.claude/rules/design-tokens.md` (DESIGN.md 3 階層、hex/sp/dp ハードコード禁止)
- `.claude/rules/ui-snapshot.md` (Roborazzi 4 パターン baseline、human approve 必須)
- `.claude/rules/ui-inventory.md` (Inventory 構造と更新規約)
- `.claude/rules/behavior-preservation.md` (リファクタ時の振る舞い維持原則)

## 関連 Skill

- `.claude/skills/ui-snapshot/SKILL.md` (A10 で本格化)
- `code-reviewer` の visual-regression / design-tokens aspect (A10 完了後 enable)

## 関連

- ADR 0023 (UI 凍結三本柱)
- `docs/harness/plan.md` §3.9 / §6.2 A10
- `DESIGN.md` (repo root)
