---
id: arch-state-machines
title: 状態遷移 (UiState 状態機械)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3
---

# 状態遷移 (UiState)

> **5 行以内 summary**: ColorMaster の各画面の UiState 状態遷移 (Loading / Loaded /
> Empty / Error / Partially Loaded) の規約と画面別の状態機械概観。本格化は A2 + C3
> (feature-first モジュール再編後)、各 feature の詳細設計 (`docs/specifications/detail/`)
> でも個別に Mermaid `stateDiagram-v2` で記述する。

## 共通の状態語彙

| 状態 | 意味 | UI 表示例 |
|---|---|---|
| `Loading` | 初回ロード中 | スピナー全画面 |
| `Loaded` | データ取得成功 | コンテンツ表示 |
| `Empty` | 取得成功だが結果が空 | 「該当なし」メッセージ |
| `Error` | 取得失敗 | エラーメッセージ + リトライボタン |
| `PartiallyLoaded` | 一部成功 / 一部失敗 (アイドル N 件中 M 件取得失敗等) | 取得分を表示 + 失敗分の警告 |

## 規約

- **UiState は sealed class** (`.claude/rules/ui-state.md`)
- **UiAction (intent) も sealed class** で受信
- **状態遷移は ViewModel が一元管理**、Composable は State を hoist して受け取る
- 詳細実装規約は `.claude/rules/{ui-state,viewmodel,composable}.md` (A2 で本格化)

## A2 + C3 での本格化内容

- 各画面 (Home / Search / Preview / MyIdols) の状態遷移を Mermaid 化
- 共通状態 (Auth / Network) の上位状態機械
- 状態遷移 + UiAction の対応表

## 関連

- `docs/architecture/overview.md`
- `.claude/rules/{ui-state,viewmodel,composable}.md`
- `docs/design/inventory/states/` (状態別 UI パターン、A10 で本格化)
