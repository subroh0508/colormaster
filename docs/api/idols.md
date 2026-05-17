---
id: api-idols
title: アイドル情報 API (/api/idols/*)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.3
related_adrs:
  - ADR-0007
  - ADR-0010
---

# アイドル情報 API (/api/idols/*)

> **5 行以内 summary**: 公開マスタとしてアイドル / ブランド / イメージカラーを返す read-only API。
> データソースはコンテナイメージ焼込の `data/idols.db` (SQLite)、上流は im@sparql。
> 認証不要。本格実装は C3 (EPIC-001) + C5 (EPIC-003)。

## エンドポイント (骨格、本格化は C3 / C5)

| メソッド | パス | 用途 |
|---|---|---|
| GET | `/api/idols` | アイドル一覧 (ページネーション、ブランド / カラーフィルタ) |
| GET | `/api/idols/{id}` | 個別アイドル詳細 |
| GET | `/api/idols/search` | キーワード検索 (名前 / ブランド / カラー) |
| GET | `/api/brands` | ブランド一覧 |
| GET | `/api/colors` | カラーパレット一覧 |

## キャッシュ戦略

- read-only マスタなので CDN レベルキャッシュ可 (`Cache-Control: public, max-age=86400`)
- 同期 PR (`docs/runbooks/sync-imasparql.md`) でデータ更新時はデプロイで自動失効

## データソース

- `data/idols.db` (SQLite、コンテナイメージ焼込)
- 上流: `imas/imasparql` リポジトリ (RDF)、日次 SHA 監視で差分時のみ PR 自動作成 (ADR 0007)

## 関連

- ADR 0007 (im@sparql upstream-driven 同期)
- ADR 0010 (アイドル情報マスタ SQLite を repo 内 commit)
- `docs/api/colormaster-api.yaml`
- `docs/runbooks/sync-imasparql.md`
