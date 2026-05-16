---
id: arch-domain-model
title: ドメインモデル (アイドル / ブランド / カラー)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3
---

# ドメインモデル

> **5 行以内 summary**: ColorMaster のドメインモデル概観 (アイドル / ブランド / イメージカラー /
> 担当・推し)。詳細は A2 + C3 で `core/domain/` の Kotlin 実装に整合する形で
> Mermaid `erDiagram` 化する。用語の正規化は `docs/glossary.md` に集約。

## 主要エンティティ (骨格)

| エンティティ | 説明 | 関連属性 |
|---|---|---|
| `Idol` | アイドル | id, name, brand, color (`ColorPalette`) |
| `Brand` | ブランド (765AS / CG / ML / SS / SC 等) | id, name, idols (一対多) |
| `ColorPalette` | アイドルのイメージカラー (16 進カラー + 関連メタ) | hex, name |
| `FavoriteIdol` | ユーザーの担当・推しアイドル (`users.db` 経由) | uid, idolId, addedAt |

## A2 + C3 での本格化内容

- `erDiagram` で関係を可視化
- 値オブジェクト (`Hex`, `BrandId` 等) の定義
- DDD 風の集約境界 (Idol / FavoriteIdol を別 aggregate にするか)
- `core/data/Model*.kt` / `core/domain/UseCase*.kt` と対応

## 関連

- `docs/architecture/overview.md`
- `docs/glossary.md` (用語定義)
- `docs/codebase-map.md` (実装パス)
- `core/data/` (将来の実装)
