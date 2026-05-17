---
id: arch-data-flow
title: データフロー (im@sparql → Backend → Client)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.3 / §3.4
related_adrs:
  - ADR-0007
  - ADR-0008
  - ADR-0010
---

# データフロー

> **5 行以内 summary**: ColorMaster は im@sparql (上流 RDF/SPARQL) から日次で差分を取り込み、
> Backend 内のアイドル情報 SQLite (`idols.db`、コンテナイメージ焼込) に反映する。
> ユーザーデータ (`users.db`) は Backend 内蔵 + Litestream で R2 にレプリケート。
> クライアントは `/api/idols/*` および `/api/me/*` 経由でアクセスする。本格化は A2 + C5 + C6。

## データソース

| ソース | 種別 | 同期戦略 |
|---|---|---|
| `imas/imasparql` (RDF) | 上流マスタ | upstream-driven sync (日次 cron、SHA 監視、差分時のみ PR 自動作成、ADR 0007) |
| `data/idols.db` (SQLite) | 派生マスタ | リポジトリ commit + コンテナイメージ焼込、read-only |
| `data/users.db` (SQLite) | ユーザーデータ | Backend 内蔵 + Litestream で R2 へ WAL replicate + 起動時 restore (ADR 0008) |

## フロー図 (TODO: A2 で Mermaid 化)

```
imas/imasparql (RDF)
  └─[日次 cron, SHA 監視, ADR 0007]─→ data/idols.db (SQLite)
                                       └─[コンテナイメージ焼込]─→ Backend
                                                                   ├─ /api/idols/* ─→ Client
                                                                   └─ /api/me/*    ─→ Client
                                                                          ↑
                                                                          ├─ GIS ID Token (Bearer)
                                                                          └─ users.db (uid のみ)
                                                                                ↕ Litestream
                                                                              R2 (private)
```

## クライアント側のキャッシュ戦略

- アイドル情報: Repository 層で memory cache (TTL なし、起動時に一括取得)
- ユーザー情報 (GIS userinfo): memory cache TTL 15 分 (`.claude/rules/pii.md` 準拠)

## 関連

- `docs/harness/plan.md` §3.3 (同期戦略) / §3.4 (ホスティング)
- ADR 0007 / 0008 / 0010
- `docs/runbooks/sync-imasparql.md` / `docs/runbooks/r2-litestream.md` (C5 / C6 で本格化)
