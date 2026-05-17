---
id: rules-secrets
title: Secrets 管理
status: skeleton
last_updated: 2026-05-17
# 注意: Secrets 管理は全ファイル編集で遵守すべき安全網のため `paths` を意図的に
# 設定せず、Claude Code セッション起動時に常時ロードする。
related_plan: docs/harness/plan.md §3.8 / ADR 0021
related_adrs:
  - ADR-0021
---

# secrets.md — Secrets 管理

> API キー / トークン / 認証情報の保管場所・ローテーション・取り扱いを規定。
> 詳細は ADR 0021 を Single Source of Truth とする。
> **本 rule は安全網として常時ロード** (frontmatter `paths` を意図的に未設定、§5.1 参照)。

## 保管場所の使い分け

| 種類 | 保管場所 | 取り扱い |
|---|---|---|
| ローカル開発用 | `.env` (`.gitignore` 対象) | 個人マシン外に絶対出さない |
| CI/CD 用 | GitHub Secrets | repo 設定で管理、PR からは参照不可 |
| 本番 Backend 用 | Google Cloud Secret Manager | Cloud Run service account 経由でアクセス |
| Cloudflare 用 (R2 token / Pages deploy key) | Cloudflare dashboard + GitHub Secrets | TTL 90 日で定期ローテーション |
| Claude Code 内 (MCP OAuth) | ローカル Claude Code 安全領域 | リポジトリ commit 禁止、`.gitignore` で `.claude/oauth-tokens*` を除外 |

## 絶対 commit してはいけないもの

- `.env*`
- `*-credentials.json`
- `users.db*`
- `service-account*.json`
- `.claude/oauth-tokens*`

詳細は `.gitignore` 最終形を参照。

## ローテーション

- **R2 token TTL 90 日** (ADR 0021)
- 漏洩時 / 退職時 / 漏洩疑い時は即時ローテーション
- 手順は `docs/runbooks/secrets-rotation.md` に整備 (Phase A〜C で本格化)

## 漏洩検出

- **trufflehog** を A6 で CI 導入、全 PR 差分をスキャン
- 検出時は immediate rotate + history rewrite (`git filter-repo`)

## Gotchas

- **Skill が CI ログ / MCP 結果を learning / レビューコメントに含める場合は redaction 必須** (R-26)
- 「.env を `.env.example` に間違えてコピー」事故防止のため、`.env.example` には **キーのみ + ダミー値**

## 関連

- ADR 0021 (Secrets 管理ポリシー)
- `docs/harness/plan.md` §3.8 / R-19 / R-25 / R-26
- `.claude/rules/{pii,db-protection,mcp-usage}.md`
- `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)
