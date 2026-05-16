---
id: arch-sequences
title: 主要ユースケースのシーケンス
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3
---

# 主要ユースケースのシーケンス

> **5 行以内 summary**: ColorMaster の主要ユースケース (ログイン / アイドル検索 / 担当追加 /
> 同期実行) のシーケンス図を集約する。Phase C 進行に応じて各 EPIC の詳細設計
> (`docs/specifications/detail/`) で個別に詳細化、本ファイルは概観として残す。
> 本格化は A2 + C3-C6。

## 主要ユースケース (骨格、A2 + C3-C6 で本格化)

| ユースケース | 主参加者 | 関連 EPIC | 詳細 |
|---|---|---|---|
| ログイン (GIS) | Client → GIS → Backend (`/api/me`) | C5 | TODO Mermaid `sequenceDiagram` |
| アイドル検索 | Client → Backend (`/api/idols/search`) → idols.db | C3 | TODO |
| 担当追加 | Client → Backend (`/api/me/favorites`) → users.db → Litestream → R2 | C5 | TODO |
| im@sparql 同期 | GitHub Actions cron → imas/imasparql → idols.db diff PR | C6 | TODO |
| Cloud Run 起動 (Litestream restore) | Cloud Run startup → R2 → users.db restore → Ktor start | C5 / C7 | TODO |

## A2 + C3-C6 での本格化内容

- 各ユースケースを Mermaid `sequenceDiagram` で記述
- エラーケース / リトライ / タイムアウトの分岐を明示
- 関連 Spec basic / detail へのリンク

## 関連

- `docs/architecture/{overview,data-flow,layers}.md`
- `docs/specifications/{basic,detail}/` (各機能の Spec、Phase C で個別作成)
- `docs/runbooks/{sync-imasparql,release,r2-litestream}.md`
