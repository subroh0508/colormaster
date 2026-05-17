---
id: rules-mcp-usage
title: MCP サーバの使い分けと認証情報の取り扱い
status: skeleton
last_updated: 2026-05-17
paths:
  - ".claude/mcp.json"
  - ".claude/skills/**"
  - "docs/runbooks/mcp-setup.md"
related_plan: docs/harness/plan.md §5.6 / ADR 0024
related_adrs:
  - ADR-0024
---

# mcp-usage.md — MCP サーバ利用規約

> JetBrains MCP + Context7 MCP + Cloudflare MCP の 3 つを使い分け、GitHub MCP は採用せず
> `gh` CLI で代替する規約。詳細は ADR 0024。セットアップ手順は `docs/runbooks/mcp-setup.md`。

## 採用 MCP (3 つ)

| MCP | 用途 | 連携 Skill |
|---|---|---|
| **JetBrains MCP** (IntelliJ IDEA / Android Studio 2025.2+ にバンドル済) | rename / inspection / IDE index 検索 / build / file analysis / refactoring | implementation-workflow, code-reviewer (architecture), refactor, ui-snapshot |
| **Context7 MCP** | バージョン固有のライブラリ docs (Kotlin / Compose MP / Ktor / SQLDelight / Roborazzi 等) を LLM に注入 | feature-request, bug-fix, refactor, implementation-workflow, dependency-upgrade |
| **Cloudflare MCP** | R2 / Pages / Workers / DNS / Secrets 管理 | C7 デプロイ Plan, secrets-rotation runbook |

## 採用見送り (代替手段あり)

| 不採用 MCP | 代替手段 |
|---|---|
| GitHub MCP | **`gh` CLI** (token 効率 10〜32 倍優位、training data 内蔵) |
| Sourcegraph MCP | **JetBrains MCP の IDE indexing** (機能重複) |
| Serena MCP | **JetBrains MCP + Context7 MCP** (Kotlin Indirect Support / 有料 / セットアップ重) |

## 使い分けルール

- **GitHub 操作は原則 `gh` CLI**: PR list / view / diff / search / create / merge / comment / Actions logs / Issue 起票はすべて `gh`
- **IDE 操作は JetBrains MCP**: rename / inspection / file analysis / index 経由 regex search / build / run config 実行。**手動で `git grep` する代わりに JetBrains MCP の検索を優先** (IDE index が高速・正確)
- **ライブラリの API 確認は Context7 MCP**: コード生成前に「`androidx.lifecycle.ViewModel` の `viewModelScope` の API」のような確認を行うときは必ず Context7 を経由 (training data の古い情報や hallucination を回避)
- **Cloudflare 操作**: `wrangler` CLI で済む場合 (`wrangler deploy` 等) は CLI を優先、複雑な API 操作 (R2 token ローテーション、bucket policy 更新等) は Cloudflare MCP を使う

## 認証情報の取り扱い

- **MCP OAuth token はローカル Claude Code 管理**、リポジトリ commit 禁止 (`.gitignore` で `.claude/oauth-tokens*` を除外)
- **Skill が MCP 結果を learning / レビューコメントに含める場合**: PII redaction フェーズで token / API キー類を除去 (`.claude/rules/pii.md` 同等)
- **権限スコープ**: Cloudflare MCP は対象 zone / bucket のみ allow、JetBrains MCP は IDE のプロジェクトスコープに自動的に制限される (R-25)

## Gotchas

- **IDE 未起動 / バージョン 2025.2 未満 / MCP Server プラグイン未有効 / 動的ポート変更** で JetBrains MCP が利用不可になる (R-27)
  - Skill 起動時に接続失敗を検出したら警告を出しつつ `gh` CLI / `git grep` などにフォールバック
  - 長期的に IDE 非起動環境で運用する場合は Serena MCP の採用を別 Plan で再評価
- **Context7 MCP は公開ライブラリ docs** のため、AI が取得した内容を Konsist / detekt / 型チェッカーで二重検証する (R-28)
- **`@jetbrains/mcp-proxy` npm パッケージは deprecated**、使用しない (`docs/runbooks/mcp-setup.md` 参照)
- セットアップ詳細 (3 接続方式の選択基準 / OAuth フロー / port 競合) は `docs/runbooks/mcp-setup.md` に集約

## 関連

- ADR 0024 (MCP サーバ採用)
- `docs/harness/plan.md` §5.6 / R-25 / R-26 / R-27 / R-28
- `docs/runbooks/mcp-setup.md`
- `.claude/mcp.json`
