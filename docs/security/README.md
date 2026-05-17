---
id: security-readme
title: セキュリティ ADR 索引と incident 対応 quick-reference
status: living
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.8
related_adrs:
  - ADR-0011
  - ADR-0020
  - ADR-0021
  - ADR-0024
  - ADR-0025
  - ADR-0027
---

# セキュリティ ADR 索引と incident 対応 quick-reference

> **5 行以内 summary**: ColorMaster のセキュリティ・プライバシー関連 ADR の索引、機械検証
> rule と runbook へのリンク、incident 発生時の quick-reference を集約する。個別 ADR の
> 本体は `docs/adr/`、ハンズオン手順は `docs/runbooks/`、機械検証ルールは `.claude/rules/`
> を Single Source of Truth として参照。インシデント対応の最初のアクションは §3 を見る。

## 1. 関連 ADR (索引)

| ADR | タイトル | 関連 rule | 関連 runbook | 起票根拠 §4.5 |
|---|---|---|---|---|
| ADR 0011 | 認証スタック転換 (Firebase Auth 廃止 + GIS 統一) | `no-firebase.md` / `backend-auth.md` / `firebase-boundary.md` | `docs/runbooks/backend-local.md` (C5) | 2 / 3 / 9 / 10 |
| ADR 0020 | PII 保護と権限ロール (uid のみ DB 保存、owner 1 名運用) | `pii.md` / `db-protection.md` | `docs/runbooks/user-deletion.md` (将来) | 6 / 9 / 10 |
| ADR 0021 | Secrets 管理ポリシー (R2 token TTL 90 日、Secret Manager / GitHub Secrets / .env の使い分け) | `secrets.md` | `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化) | 6 / 9 / 10 |
| ADR 0024 | MCP サーバ採用 (JetBrains + Context7 + Cloudflare、token 取扱) | `mcp-usage.md` | `docs/runbooks/mcp-setup.md` | 2 / 7 / 8 |
| ADR 0025 | Skill 作成は `example-skills:skill-creator` 経由 (SKILL.md 仕様統一) | `skill-authoring.md` | — | 7 / 8 |
| ADR 0027 | docs 構造 + 命名規約 + 日本語化 (情報漏洩 / 読み取りミスを構造で防ぐ) | `docs-structure.md` / `template-language.md` | — | 1 / 7 / 9 / 10 |

## 2. 機械検証 (関連 rule)

| 領域 | rule | 機械検証 (本格化フェーズ) |
|---|---|---|
| PII redaction | `.claude/rules/pii.md` | trufflehog (A6) + Konsist (テスト fixture の `@example.com` ドメイン以外検出、R-21) |
| Secrets 漏洩検出 | `.claude/rules/secrets.md` | trufflehog (A6)、`.gitignore` で `.env*` / `.claude/oauth-tokens*` 等を除外 |
| DB 保護 | `.claude/rules/db-protection.md` | Konsist + Gradle カスタムタスク (A6): `data/users.db*` 追跡禁止 / Dockerfile 内 `COPY data/users.db` 禁止 / `core/network/` 内 hardcode 禁止 |
| `.dockerignore` 必須項目 | `.claude/rules/db-protection.md` (`.dockerignore` 配置 TODO) | A6 で「`data/users.db*` / `.env*` / `*-credentials.json` / `service-account*.json` / `.claude/oauth-tokens*` を全て含む」を Gradle で検証 |
| MCP token 取扱 | `.claude/rules/mcp-usage.md` | OAuth token のリポジトリ commit 禁止 (`.gitignore` で `.claude/oauth-tokens*` 除外) |

## 3. Incident 対応 quick-reference

**最初のアクションは「漏洩拡大の即時遮断 → ローテーション → history 除去 → 影響範囲確認」の順**。詳細手順は各 runbook へ。

### 3.1 PII 漏洩 (実 PII が fixture / ログ / レビューコメント / learning に commit された)

1. **即時に該当 commit を revert** (push 済みなら force-push の代わりに新 commit で削除)
2. **`git filter-repo` で history からも除去** (push 済みなら全関係者に再 clone 指示)
3. **影響範囲確認**: 該当 PII の所有者に連絡、ログ系外部サービスにも消去申請
4. **再発防止**: `.claude/rules/pii.md` の redaction パターンを追加、code-reviewer の `security` aspect で再検証
5. 詳細手順: `docs/runbooks/secrets-rotation.md` (将来 PII 対応セクション追加)

### 3.2 Secrets 漏洩 (`.env` / OAuth token / service-account.json が commit / push された)

1. **該当 token / key を即時 revoke** (Google Cloud / Cloudflare / GitHub Secrets dashboard)
2. **新 token を発行 + 関連サービスを更新** (Secret Manager / GitHub Secrets / `.env` 個別配布)
3. **history から `git filter-repo` で完全除去**
4. **R2 token は TTL 90 日を待たずに即時ローテーション** (ADR 0021)
5. 詳細手順: `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)

### 3.3 `users.db` がコンテナイメージに焼き込まれた

1. **該当イメージタグを Cloud Run / レジストリから即時削除** (`gcloud run services delete-revisions` 等)
2. **Dockerfile / `.dockerignore` の不備を `.claude/rules/db-protection.md` に従って修正**
3. **Backend の Litestream replicate チェイン (R2 → restore) の整合性を確認** (ADR 0008)
4. 再デプロイ後、`docs/runbooks/r2-litestream.md` (C5 で本格化) の手順で `users.db` を R2 から restore

### 3.4 MCP OAuth token (Cloudflare) が漏洩した

1. **Cloudflare dashboard で対象アプリの認可を revoke**
2. **新規 OAuth フローで再認証** (`docs/runbooks/mcp-setup.md` §3)
3. **`.claude/oauth-tokens*` が `.gitignore` で除外されているか再確認**
4. 漏洩経路 (PR / Slack / Learning ファイル) を全て確認し、PII redaction と同様に history 除去

## 4. 関連 runbook

- `docs/runbooks/mcp-setup.md` (MCP セットアップ + OAuth 取扱、本 PR で本格化済)
- `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)
- `docs/runbooks/r2-litestream.md` (C5 で本格化、Litestream replicate / restore 手順)
- `docs/runbooks/user-deletion.md` (将来作成、PII 削除リクエスト対応)
- `docs/runbooks/cloud-run-deploy.md` (C7 で起票、デプロイ時の secrets / DB 保護検証手順)

## 5. 権限ロール

- 当面 **owner 1 名のみ** (ADR 0020)
- 複数人体制になったら別 ADR で `developer` / `releaser` 等を追加

## Phase A〜C 持ち越し (本格化)

| 持ち越し項目 | 持ち越し先 | 理由 |
|---|---|---|
| `docs/runbooks/secrets-rotation.md` の具体 token 種別表 (R2 / GIS / Secret Manager / Cloudflare 各 token) | Phase A〜C 進行に応じて | 実 token 種別が確定したフェーズで段階的に追記 |
| `docs/runbooks/user-deletion.md` (GDPR / 個人情報保護法対応の削除手順) | 将来 | ユーザー対応窓口確立後に整備 |
| trufflehog CI 統合 (PR 差分 secret-scan) | A6 | Lint / Format 基盤と同時に導入 |
| `.dockerignore` の機械検証 (Gradle カスタムタスク) | A6 / Dockerfile 配置 PR | `.claude/rules/db-protection.md` の TODO と連動 |
| Konsist rule (PII fixture ドメイン検出、R-21) | A7 | Konsist 本格化と連動 |

## 関連

- ADR 0011 / 0020 / 0021 / 0024 / 0025 / 0027
- `docs/harness/plan.md` §3.8 (PII 保護とアクセス制御)
- `.claude/rules/{pii,secrets,db-protection,mcp-usage,r2-litestream,no-firebase,firebase-boundary}.md`
- `docs/runbooks/mcp-setup.md`
