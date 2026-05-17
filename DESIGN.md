# DESIGN.md (骨格)

> **5 行以内 summary**: ColorMaster の Single Source of Truth デザイントークン。
> Google Stitch 標準準拠の **3 階層構造** (Primitive / Semantic / Component) + Rationale。
> 本ファイルは B0 で配置する **骨格** (tokens セクションは空)。A10 (EPIC-A10) で
> `ui-snapshot` Skill が実コードから値を抽出 + Rationale を AI 起草 → 人間レビューで本格化。
> 詳細な運用ガイドは `docs/design/README.md`、規約は `.claude/rules/design-tokens.md`。

---

## 上部: machine-readable tokens

### Primitive (原始値、A10 で生成)

```yaml
# A10 で ui-snapshot Skill が実コードから抽出
colors:
  brand:
    # (例) imas-cg-rin: "#5F4F8A"
spacing:
  # (例) xs: 4 / sm: 8 / md: 16 / lg: 24 / xl: 32
typography:
  # (例) body: { size: 14, weight: 400, lineHeight: 20 }
radii:
  # (例) sm: 4 / md: 8 / lg: 16
elevation:
  # (例) sm: 1 / md: 4 / lg: 8
```

### Semantic (意味的命名、Primitive を参照)

```yaml
# A10 で生成
semantic:
  surface:
    background: "{colors.neutral.50}"
    brand: "{colors.brand.imas-cg-rin}"  # アイドル別に動的差替
  text:
    primary: "{colors.neutral.900}"
    secondary: "{colors.neutral.600}"
```

### Component (コンポーネント別命名、Semantic を参照)

```yaml
# A10 で生成
component:
  idolCard:
    borderColor: "{semantic.surface.brand}"
    padding: "{spacing.md}"
    radius: "{radii.md}"
  brandChip:
    background: "{semantic.surface.brand}"
    textColor: "{semantic.text.primary}"
```

---

## 下部: human-readable Rationale

### カラー選定理由 (A10 で AI 起草 → 人間レビュー)

(B0 時点では空。A10 で `ui-snapshot` Skill が実コードから抽出 + Rationale を起草 → 人間レビュー必須)

### アクセシビリティ (WCAG AA / AAA)

(A10 で WCAG コントラスト比 / フォントサイズ / タップ領域の検証結果を記述)

### タイポグラフィ選定理由

(A10 で記述)

### ブランドカラー (アイドル別) の扱い

(A10 で記述: 動的色の Preview 戦略 / Roborazzi `changeThreshold` 値 / バリエーション Preview の網羅方針、R-23 参照)

---

## 関連

- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline)
- `docs/design/README.md` (運用ガイド)
- `.claude/rules/design-tokens.md` (ハードコード禁止 / 3 階層参照規約)
- `.claude/skills/ui-snapshot/SKILL.md` (A10 で本格化)
- `docs/harness/plan.md` §3.9 / §6.2 A10
- `docs/design/inventory/` (UI Inventory + Roborazzi baseline screenshots)
