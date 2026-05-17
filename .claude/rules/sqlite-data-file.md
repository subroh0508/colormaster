---
id: rules-sqlite-data-file
title: SQLite データファイルの取り扱い
status: stable
last_updated: 2026-05-17
paths:
  - "data/**"
  - "Dockerfile"
  - ".dockerignore"
related_adrs:
  - ADR-0008
  - ADR-0010
  - ADR-0020
---

# sqlite-data-file.md — SQLite データファイルの取り扱い

> 本リポジトリで扱う SQLite データベースファイルの **コミット可否 / イメージ焼込可否 /
> アクセス制御** を明確化する規約。`db-protection.md` (`users.db` 限定の安全網) と
> `sql-delight.md` (スキーマ実装) の中間の運用規約。

## 対象 DB ファイル

| ファイル | 用途 | リポジトリ commit | Docker image 焼込 | 取り扱い |
|---|---|---|---|---|
| `data/idols.db` | アイドル情報マスタ (read-only) | ✅ OK | ✅ OK | sync job で更新、PR レビュー必須 |
| `data/users.db` | ユーザーデータ (uid のみ + my idols) | ❌ **禁止** | ❌ **禁止** | Backend 起動時に Litestream から restore |
| `data/users.db-wal` / `data/users.db-shm` | SQLite WAL / SHM (Litestream replication 中) | ❌ **禁止** | ❌ **禁止** | Litestream が R2 へ continuous replicate |

## `data/idols.db` の運用

- **配置**: リポジトリ root `data/idols.db`
- **更新**: `.github/workflows/sync-imasparql.yml` (`sync-job.md` 参照)
- **スキーマ**: SqlDelight `.sq` ファイル (`sql-delight.md` 参照)、migration 不要 (全件再生成)
- **アクセス**: ClientApp (Android / Wasm / Desktop) が `core/database` 経由で読み取り、書き込み禁止
- **Backend からも参照**: Backend (Cloud Run) は同じ `idols.db` をコンテナイメージから読み取り (read-only)

## `data/users.db` の禁止運用

- **絶対 commit しない**: `.gitignore` で `data/users.db*` を除外 (B0 で配置済)
- **絶対イメージに焼込まない**: `.dockerignore` で `data/users.db*` を除外 (`db-protection.md` の必須項目)
- **Backend 起動時に Litestream restore**: コンテナ起動スクリプトで R2 から WAL を pull、`/data/users.db` を hydrate
- **アクセスは Backend のみ**: ClientApp は直接アクセス不可、Backend API 経由 (`backend-auth.md` 参照)

## ローカル開発時の `users.db`

- 開発者ごとに別ファイル (`.gitignore` 対象、共有禁止)
- 初回起動時に空の `users.db` を Backend が生成 (schema migration 自動実行)
- PII 含めない fixture data は `scripts/fixtures/seed-users.sql` (将来、`@example.com` ドメインのみ) で投入

## Backup と Restore

- **Backup**: Litestream が WAL を R2 bucket (`colormaster-users-db-backup`) に continuous replicate (`r2-litestream.md` 参照)
- **Restore**: Backend 起動時に Litestream `restore` コマンドで R2 から最新状態を hydrate
- **Disaster Recovery**: Cloud Run コンテナ完全再作成時も R2 から restore で復旧 (RPO ~1 分、RTO ~30 秒)

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `git ls-files data/users.db*` の結果が空 (commit 検知)
  - `Dockerfile` 内に `COPY data/users.db` パターンが存在しない
  - `.dockerignore` に `data/users.db*` / `.env*` / `*-credentials.json` 全てが含まれる (`db-protection.md` 必須項目)
  - `data/idols.db` のスキーマが期待通り (`Idol` テーブル存在 + 件数 > 0)
- **trufflehog** で全 PR 差分の secret scan、`users.db` を含む差分を block

## Gotchas

- **SQLite WAL モードの `users.db-wal` / `users.db-shm` も commit 禁止**。`.gitignore` で `data/users.db*` (`*` glob で全 suffix カバー)
- **`data/idols.db` は git LFS を使わない**。ファイルサイズが大きくなったら別 ADR で LFS 採用検討 (現状 ~MB 以下を想定)
- **本番イメージで `data/idols.db` の整合性検証**: コンテナ起動時に `PRAGMA integrity_check` を実行、failure 時は Cloud Run health check で unhealthy 返答
- ローカル開発者間で `users.db` の sharing は禁止 (PII 漏洩リスク)、テスト用は **個人ローカル のみ**

## 関連

- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0010 (アイドル情報 SQLite in-repo)
- ADR 0020 (PII 保護)
- `.claude/rules/{db-protection,sql-delight,sync-job,r2-litestream,backend-auth,cloud-run-deploy,secrets,pii}.md`
- `docs/runbooks/local-development.md` (A2-4 で本格化)
