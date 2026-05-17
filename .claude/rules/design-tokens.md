---
id: rules-design-tokens
title: DESIGN.md とデザイントークン規約
status: stable
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "core/**/*.kt"
  - "composeApp/**/*.kt"
  - "DESIGN.md"
  - "docs/design/**"
related_adrs:
  - ADR-0023
related_plan: docs/harness/plan.md §3.9 / R-23
---

# design-tokens.md — DESIGN.md とデザイントークン規約

> 色・タイポ・スペーシング・radii を実コードから抽出した DESIGN.md (Google Stitch 標準準拠の
> 3 階層構造) を Single Source of Truth とし、コード内 hex / sp / dp ハードコードを禁止する規約。
> DESIGN.md は A10 で `ui-snapshot` Skill が自動生成、その後の編集は human approve 必須。

## DESIGN.md の 3 階層構造 (Google Stitch 準拠)

| 階層 | 内容 | Kotlin マッピング例 |
|---|---|---|
| **Primitive** | 原始値 (hex / sp / dp / ms 等の生の値) | `object Primitive { val ImasCgRin = Color(0xFF5F4F8A) }` |
| **Semantic** | 意味的命名 (Primitive を参照) | `object Semantic { val SurfaceBrandCgRin = Primitive.ImasCgRin }` |
| **Component** | コンポーネント別命名 (Semantic を参照) | `object IdolCardTokens { val BorderColorCgRin = Semantic.SurfaceBrandCgRin }` |

実コードからは原則 **Component 階層を参照**。Semantic / Primitive は内部実装。

## DESIGN.md の構造例

```markdown
# DESIGN.md

> 5 行以内 summary

## Primitive (原始値)

### Colors

| トークン名 | hex | 用途 |
|---|---|---|
| `Primitive.ImasCgRin` | `#5F4F8A` | imas-cg リン担当カラー |
| ... | ... | ... |

### Typography

| トークン名 | sp | 用途 |
|---|---|---|
| `Primitive.HeadingLargeSize` | 24 | 画面タイトル |

### Spacing

| トークン名 | dp | 用途 |
|---|---|---|
| `Primitive.SpacingMedium` | 16 | カード内余白 |

## Semantic (意味的命名)

...

## Component (コンポーネント別命名)

...
```

## ハードコード禁止パターン

| パターン | 検出 regex | 例外 |
|---|---|---|
| Hex 色 (`Color(0xFFXXXXXX)`) | `Color\(0x[0-9A-Fa-f]{8}\)` | テストコード (`*Test.kt` / `*Spec.kt`) |
| Hex 色 (`#XXXXXX` 文字列) | `"#[0-9A-Fa-f]{6,8}"` | DESIGN.md Primitive 表内 |
| `*.sp` / `*.dp` リテラル | `(\d+)\.(sp\|dp)\b` | テストコード / Roborazzi baseline 用 Preview |
| Material 色直接参照 | `Color\.(Red\|Blue\|...)` | テストコード |
| `MaterialTheme.colors.primary` ハードコーディング | コードレビューで検出 | テーマ切替対応時のみ許可 |

## 機械検証 (A6 + A10 で段階導入)

- **A6**: Konsist で `feature/**` / `core/**` / `composeApp/**` の Kotlin source 内に `Color(0x...)` / `#[0-9A-Fa-f]{6}` / `\d+\.(sp|dp)` パターンの混入なし
- **A10**: DESIGN.md の Primitive 値以外を実コードから参照したら CI 失敗 (`code-reviewer` design-tokens aspect が検証)
- **A10**: Roborazzi screenshot test で 4 パターン baseline (`ui-snapshot.md`) と DESIGN.md トークン値の整合を確認

## 例外

- **テストコード** (`*Test.kt` / `*Spec.kt`): 対象外 (色比較が必要なケース、unit test の独立性確保)
- **Roborazzi baseline 用 Preview パラメータ**: デザイントークン経由が望ましいが、可読性優先で許容
- **A10 完了前のレガシーコード**: 凍結フェーズ A10 完了までは部分的にハードコード残存を許容、Phase C リファクタで段階的に解消
- **DESIGN.md 内 Primitive 表**: hex / sp / dp の生値を記述する場所、ハードコード規約は適用外

## アイドル別ブランドカラーの扱い (im@s 固有)

- **Primitive 階層で全カラーを列挙**: 「283-icg」「283-mr」「283-sl」「283-stage-2」等の brand-id ごとに hex を定義
- **Semantic 階層で `brand-<brand-id>-<color-role>` 命名**: `SurfaceBrandImasCgRin` / `OnSurfaceBrandImasCgRin` 等
- **Component 階層でアイドル UI 要素にマッピング**: `IdolCardTokens` / `BrandChipTokens` / `ColorSwatchTokens` 等
- 動的色 (アイドル選択で切り替わる) は **Composable パラメータで brand-id を渡す**、ハードコードしない

## DESIGN.md 生成と編集

- **A10 で `ui-snapshot` Skill が自動生成** (DESIGN.md + UI Inventory + Roborazzi baseline の三本柱、ADR 0023)
- **生成後の編集は human approve 必須** (ブランド一貫性のため)
- 編集 PR は `refactor.md` テンプレ (Behavior Preservation 証拠必須) または `docs.md` テンプレ (token 追加のみで実コード変更ゼロ)

## Gotchas

- **DESIGN.md は A10 で自動生成** (`ui-snapshot` Skill)、それまで本ファイルは骨格のみ
- **DESIGN.md の編集は human approve 必須** (ブランド一貫性のため、`pr-draft-policy.md` 3 条件と整合)
- **ブランドカラー (アイドル別) は Primitive 階層で全カラーを列挙**、Semantic で `brand-cg-rin` のような命名にする
- **A10 完了前のハードコード許容範囲**: レガシー実コードのみ、新規追加コードは Component 階層参照を強制 (A6 Konsist で部分検出)
- **テストコードの例外は緩く**: 色比較 / Roborazzi parameter / Material 直接参照は許容、ただし production code に混入させない
- **動的ブランドカラー**: ハードコード回避のため Composable パラメータで brand-id を渡し、Component 階層で Semantic.SurfaceBrand<BrandId> をルックアップ
- **`Color.Red` 等の Material 直接参照禁止**: テーマ非対応 (Light/Dark 切替で壊れる) になるため Component 階層経由を強制

## 関連

- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline)
- `docs/harness/plan.md` §3.9 / R-23
- `.claude/rules/{ui-snapshot,ui-inventory,behavior-preservation,code-reviewer-aspects,composable}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
- DESIGN.md (A10 で自動生成、リポジトリルート配置予定)
- `docs/design/` (補助ファイル、`docs/design/inventory/` / `docs/design/screenshots/`)
