---
id: arch-layers
title: 層別責務
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3
related_adrs:
  - ADR-0002
  - ADR-0003
---

# 層別責務

> **5 行以内 summary**: ColorMaster の層構造 (feature → core/domain → core/data → core/network)
> と各層の責務、越境ルール、依存方向を記述する。Phase C で feature-first モジュール再編
> (EPIC-001) が完了した時点で本ファイルが本格的に意味を持つ。本格化は A2 + C3。

## 層 (A2 + C3 で本格化)

| 層 | 責務 | 依存可能な層 | 関連 rules |
|---|---|---|---|
| `feature/<画面>` | 画面固有の ViewModel / Composable / UiState / Route | core/domain, core/data | viewmodel.md, composable.md, navigation.md, ui-state.md |
| `core/domain` | ドメインモデル / ユースケース | core/data, core/network | repository.md (interface) |
| `core/data` | Repository 実装 / DB 抽象化 | core/network, core/database | repository.md (impl), error-handling.md |
| `core/network` | Ktor Client / API DTO | (なし) | network-client.md, error-handling.md |
| `backend` | Ktor Server / `/api/me/*` / 認証検証 | im@sparql, GIS, R2 | backend-auth.md |

## 越境ルール

- **上から下への一方向依存**: feature → core/domain → core/data → core/network。逆向き禁止
- **`core/network/` から DB 直接アクセス禁止**: 必ず Repository 経由
- **`feature/` から `core/network/` 直接参照禁止**: 必ず Repository / UseCase 経由
- **A2 で Konsist 規約として機械化**

## 撤去予定の層

- `core/network/auth/` (Firebase Auth 連携) → C5 で撤去、GIS に統一
- `core/network/firestore/` (Firestore 連携) → C5 で撤去、Backend SQLite に統一

## 関連

- `docs/architecture/overview.md`
- ADR 0002 / 0003 / 0011
- `.claude/rules/{viewmodel,composable,navigation,repository,network-client}.md`
