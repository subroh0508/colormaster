---
id: rules-design-tokens
title: DESIGN.md とデザイントークン規約
status: skeleton
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "core/**/*.kt"
  - "composeApp/**/*.kt"
  - "DESIGN.md"
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs:
  - ADR-0023
---

# design-tokens.md — DESIGN.md とデザイントークン規約

> 色・タイポ・スペーシング・radii を実コードから抽出した DESIGN.md (Google Stitch 標準準拠の
> 3 階層構造) を Single Source of Truth とし、コード内 hex / sp / dp ハードコードを禁止する規約。
> DESIGN.md は A10 で `ui-snapshot` Skill が自動生成、その後の編集は human approve 必須。

## DESIGN.md の 3 階層構造 (Google Stitch 準拠)

| 階層 | 内容 | 例 |
|---|---|---|
| **Primitive** | 原始値 (hex / sp / dp / ms 等の生の値) | `colors.brand.imas-cg-rin: #5F4F8A` |
| **Semantic** | 意味的命名 (Primitive を参照) | `semantic.surface.brand.cg-rin: colors.brand.imas-cg-rin` |
| **Component** | コンポーネント別命名 (Semantic を参照) | `component.idolCard.borderColor.cg-rin: semantic.surface.brand.cg-rin` |

実コードからは原則 **Component 階層を参照**。Semantic / Primitive は内部実装。

## ハードコード禁止

- Kotlin / Compose コード内に hex (`#XXXXXX` / `0xFFXXXXXX`) を書かない
- `sp` / `dp` の固定値を書かない (`16.dp` / `14.sp` 等もデザイントークン経由)
- `Color.Red` のような Material 直接参照も避ける (テーマ非対応になる)

## 機械検証 (A6 + A10 で段階導入)

- **A6**: Konsist で「`feature/**` / `core/**` Kotlin source に `Color(0x...)` / `#[0-9A-Fa-f]{6}` パターン無し」
- **A10**: DESIGN.md の Primitive 値以外を実コードから検出したら CI 失敗

## 例外

- テストコード (`*Test.kt` / `*Spec.kt`) は対象外 (色比較が必要なケース)
- Roborazzi baseline 用の Preview パラメータ (デザイントークン経由が望ましいが、可読性優先で許容)

## Gotchas

- **DESIGN.md は A10 で自動生成** (`ui-snapshot` Skill)。それまで本ファイルは骨格のみ。
- **DESIGN.md の編集は human approve 必須** (ブランド一貫性のため)。
- ブランドカラー (アイドル別) は Primitive 階層で全カラーを列挙、Semantic で `brand-cg-rin` のような命名にする。

## 関連

- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline)
- `docs/harness/plan.md` §3.9 / R-23
- `.claude/rules/{ui-snapshot,ui-inventory,behavior-preservation}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
