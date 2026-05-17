---
id: rules-secrets
title: Secrets 管理
status: stable
last_updated: 2026-05-17
# 注意: Secrets 管理は全ファイル編集で遵守すべき安全網のため `paths` を意図的に
# 設定せず、Claude Code セッション起動時に常時ロードする。
related_adrs:
  - ADR-0021
  - ADR-0017
  - ADR-0024
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
- **`*.pem`** (PEM 形式の秘密鍵 / 証明書、PR #119 レトロ Try)
- **`*.key`** (秘密鍵 / API キー、同上)
- **`*.p12`** (PKCS#12 形式の秘密鍵 + 証明書バンドル、同上)
- **`.cloudflare/credentials`** (Cloudflare CLI / wrangler 認証情報、同上)
- **`.gcloud/credentials`** (gcloud CLI 認証情報、同上)

詳細は `.gitignore` 最終形を参照。ADR-0021 「絶対 commit してはいけないもの」リストとも同期 (PR #119 レトロ Try で項目追加、`secrets.md` と ADR-0021 の SoT 整合化)。

## ローテーション

| 対象 | 周期 | 契機 |
|---|---|---|
| R2 access token | 90 日 | TTL 満了 / 漏洩疑い / 退職 |
| Cloudflare API token (Pages デプロイ用) | 90 日 | 同上 |
| GitHub Personal Access Token (Renovate / Actions 用) | 90 日 | 同上 |
| Google Cloud service account key (Cloud Run 用) | 180 日 / 漏洩時即時 | TTL 満了 / 漏洩 |
| GIS Client Secret | ローテ不要 (Public Client Flow) | — |
| MCP OAuth トークン (ローカル Claude Code 安全領域) | Claude 側管理 | 自動 |

- 手順は `docs/runbooks/secrets-rotation.md` に整備 (Phase A〜C で本格化)
- ローテ実施記録は GitHub Issues `rotation:` ラベルで履歴管理 (将来検討)

## 漏洩検出

- **trufflehog** を A6 で CI 導入、全 PR 差分をスキャン (`.github/workflows/secret-scan.yml`)
- 検出時の対応フロー:
  1. immediate rotate (該当キーを Cloudflare / GCP / GitHub Secrets で再発行)
  2. history rewrite (`git filter-repo --invert-paths --path <file>`)
  3. force push (master のみ、collaborator 全員に通知)
  4. 別 ADR で「漏洩 → ローテ完了」を記録 (incident postmortem)

## redaction 強制 (Skill 出力前)

`code-reviewer` / `pr-retrospective` / `harness-meta` / `harness-evolution` が PR description /
learning / ADR / レビューコメントを出力する前に以下を検出してマスク:

| パターン | 置換 |
|---|---|
| `(?i)(api[-_]?key|token|secret|password)\s*[:=]\s*["']?\S+` | `[REDACTED-SECRET]` |
| `AKIA[0-9A-Z]{16}` (AWS access key) | `[REDACTED-AWS-KEY]` |
| `ghp_[0-9A-Za-z]{36}` (GitHub PAT) | `[REDACTED-GH-PAT]` |
| Bearer token / JWT (`eyJ` で始まる長文字列) | `[REDACTED-JWT]` |

詳細は `pii.md` redaction 表と統合運用 (PII と Secrets は同じ Skill チェックポイントで検証)。

## 機械検証 (A6 で導入)

- **trufflehog** で全 PR 差分の secret scan (`.github/workflows/secret-scan.yml`、上記「漏洩検出」と統合)
- **Gradle カスタムタスク** で以下を検証:
  - `.env.example` に **キーのみ + ダミー値** が記載され、実値混入なし (regex で `=.{16,}` のような長文字列を warning)
  - `gradle/libs.versions.toml` / `**/build.gradle.kts` 内に hardcode された access key / token がない (regex `(?i)(api[-_]?key|token|secret|password)\s*[:=]\s*["'][^"']{8,}["']`)
  - `.gitignore` に `.env*` / `*-credentials.json` / `service-account*.json` / `.claude/oauth-tokens*` が含まれる
- **GitHub Actions secret scanning** が repo 設定で有効化されている (Settings → Code security)

## Gotchas

- **Skill が CI ログ / MCP 結果を learning / レビューコメントに含める場合は redaction 必須** (R-26)
- 「.env を `.env.example` に間違えてコピー」事故防止のため、`.env.example` には **キーのみ + ダミー値**
- `.env` を `.env.example` から再生成する手順は runbook (`docs/runbooks/local-development.md`) に集約
- **Claude Code 内の MCP OAuth トークンは `.claude/oauth-tokens*` に保存される想定**。`.gitignore` で `.claude/oauth-tokens*` を除外する規約を維持し、commit 検証は trufflehog の追加パターンで担保
- GitHub Actions で `secrets.GITHUB_TOKEN` 以外を参照する箇所は **必ず明示** (`workflow_run` 等の権限境界を意識、ADR 0017 で Actions から Claude API を呼ばない原則と整合)

## 関連

- ADR 0021 (Secrets 管理ポリシー)
- `docs/harness/plan.md` §3.8 / R-19 / R-25 / R-26
- `.claude/rules/{pii,db-protection,mcp-usage}.md`
- `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)
