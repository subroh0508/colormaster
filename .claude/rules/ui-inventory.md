---
id: rules-ui-inventory
title: UI Inventory 規約 (docs/design/inventory/ の構造と更新)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs: [ADR-0023]
---

# ui-inventory.md — UI Inventory 規約

> `docs/design/inventory/` 配下に画面・コンポーネント・状態・フローの記録を網羅的に置き、
> リファクタ時の Behavior Preservation の根拠とする規約。
> 本格生成は A10 で `ui-snapshot` Skill が担当。

## ディレクトリ構造

| ディレクトリ | 内容 | ファイル例 |
|---|---|---|
| `docs/design/inventory/screens/` | 画面ごと | `home.md`, `search.md`, `preview.md`, `myidols.md` |
| `docs/design/inventory/components/` | コンポーネントごと | `idol-card.md`, `brand-chip.md`, `color-swatch.md` |
| `docs/design/inventory/states/` | 状態パターン | `empty.md`, `loading.md`, `error.md`, `partially-loaded.md` |
| `docs/design/inventory/flows/` | ユーザーフロー | `login.md`, `add-favorite.md`, `share-list.md` |
| `docs/design/inventory/screenshots/` | Roborazzi 生成 baseline PNG | `<composable>-<device>-<theme>.png` |

## 各ファイル frontmatter

```yaml
---
id: inventory-screen-home
type: screen | component | state | flow
title: <タイトル>
related_specs: [SPEC-NNN-N]
related_screenshots:
  - <composable>-mobile-light.png
  - <composable>-mobile-dark.png
related_design_tokens: [colors.brand.imas-cg-rin, ...]
last_updated: YYYY-MM-DD
---
```

## 本文構造

| セクション | 内容 |
|---|---|
| 概要 (5 行以内 summary) | この画面 / コンポーネント / 状態 / フローの役割 |
| 構成要素 | 含まれるコンポーネント一覧 (テーブル) |
| 状態と遷移 | Mermaid `stateDiagram-v2` で UiState の状態遷移 |
| データソース | どの API / Repository / SPEC を参照するか |
| アクセシビリティ | semantic role / contentDescription / フォーカス順序 |
| 参考 screenshot | `docs/design/inventory/screenshots/` への相対リンク (4 パターン) |
| Open Questions | 未解決の意思決定 |

## 更新ポリシー

- 新規 Composable / 状態を追加したら、対応する Inventory ファイルを作成 (`ui-snapshot` Skill が Plan 起票)
- リファクタで構造が変わったら **Behavior Preservation 検証** (visual regression + spec-conformance) を通した上で Inventory を更新
- 削除されたコンポーネントは Inventory ファイルも削除し、削除コミットメッセージに理由を明記

## Gotchas

- Inventory は **AI が次のリファクタを設計する際の Behavior Preservation 根拠**。網羅性が重要。
- 4 パターン screenshot を必ずペアでリンク (1 パターン欠落 = CI 失敗を A10 完了後に enable)

## 関連

- ADR 0023 (UI 凍結三本柱)
- `docs/harness/plan.md` §3.9
- `.claude/rules/{ui-snapshot,design-tokens,behavior-preservation}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
- `docs/design/README.md`
