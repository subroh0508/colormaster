---
id: rules-db-protection
title: ユーザーデータ DB の保護
status: skeleton
last_updated: 2026-05-17
paths:
  - "data/**"
  - "backend/**"
  - "core/network/**"
  - "Dockerfile"
  - ".dockerignore"
related_plan: docs/harness/plan.md §3.8 / ADR 0008 / ADR 0020 / ADR 0021
---

# db-protection.md — ユーザーデータ DB の保護

> Backend 内蔵 SQLite `users.db` の commit 禁止・イメージ焼込み禁止・R2 private 強制を
> 機械検証で担保する規約。アイドル情報 DB (`data/idols.db`) とは扱いが異なる点に注意。

## 対象 DB

| DB | 場所 | 保存内容 | リポジトリ commit | コンテナイメージ焼込 |
|---|---|---|---|---|
| `data/users.db` | Backend 永続 (Litestream で R2 にレプリケート) | uid のみ (PII 最小化、ADR 0020) | **禁止** | **禁止** |
| `data/idols.db` | リポジトリ commit + コンテナイメージ焼込 | アイドル情報マスタ (read-only) | OK | OK |

## 機械検証 (A6 で導入)

- **Konsist + Gradle カスタムタスク** で以下を検証 (R-20):
  - `data/users.db*` がリポジトリに追跡されていない (`git ls-files` チェック)
  - `Dockerfile` 内に `COPY data/users.db` パターンが存在しない
  - `core/network/` 内で `users.db` を hardcode で参照していない (環境変数経由のみ許可)

## R2 アクセス制御

- **R2 bucket private** + bucket policy で Backend Service Token のみ allow
- **R2 token TTL 90 日** + 定期ローテーション (ADR 0021)
- 漏洩時のローテーション runbook を `docs/runbooks/secrets-rotation.md` / `r2-litestream.md` に整備

## Litestream replicate / restore

- WAL を継続的に R2 へ replicate
- Cloud Run 起動時に R2 から restore (詳細は `.claude/rules/r2-litestream.md` で C5 本格化)

## Gotchas

- **`.gitignore` で `data/users.db*` を除外** (B0 で最終形を配置)
- Dockerfile では `data/idols.db` のみ COPY 対象に含める (`data/users.db*` を除外する `.dockerignore` 規約も併用)
- ローカル開発時の `data/users.db` は `.gitignore` 対象なので開発者ごとに別ファイルになる (gist 共有禁止)

## 関連

- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0020 (PII 最小化: uid のみ)
- ADR 0021 (R2 token TTL 90 日)
- `docs/harness/plan.md` §3.8 / R-19 / R-20
- `.claude/rules/{pii,secrets,r2-litestream}.md`
