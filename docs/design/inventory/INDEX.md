---
id: design-inventory-index
title: UI Inventory 索引
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs: [ADR-0023]
---

# UI Inventory 索引

> **5 行以内 summary**: ColorMaster の画面・コンポーネント・状態・フロー・screenshot の
> 索引。A10 (EPIC-A10) で `ui-snapshot` Skill が全件生成、以降の Phase C リファクタ時に
> Behavior Preservation の根拠として参照される。各ファイル frontmatter は
> `.claude/rules/ui-inventory.md` 参照。

## サブディレクトリ

| ディレクトリ | 内容 | 命名 |
|---|---|---|
| `screens/` | 画面ごと | `home.md`, `search.md`, `preview.md`, `myidols.md`, ... |
| `components/` | コンポーネントごと | `idol-card.md`, `brand-chip.md`, `color-swatch.md`, ... |
| `states/` | 状態パターン | `empty.md`, `loading.md`, `error.md`, `partially-loaded.md` |
| `flows/` | ユーザーフロー | `login.md`, `add-favorite.md`, `share-list.md`, ... |
| `screenshots/` | Roborazzi baseline PNG | `<composable>-<device>-<theme>.png` (4 パターン) |

## 一覧 (A10 完了後に網羅)

### Screens

| ID | 画面名 | 関連 SPEC | 関連 screenshots |
|---|---|---|---|

### Components

| ID | コンポーネント名 | 利用画面 | 関連 screenshots |
|---|---|---|---|

### States

| ID | 状態名 | 適用範囲 | 関連 screenshots |
|---|---|---|---|

### Flows

| ID | フロー名 | 関連画面 | 関連 SPEC |
|---|---|---|---|

## 関連

- `docs/design/README.md`
- `.claude/rules/{ui-inventory,ui-snapshot,design-tokens,behavior-preservation}.md`
- `.claude/skills/ui-snapshot/SKILL.md`
- ADR 0023
