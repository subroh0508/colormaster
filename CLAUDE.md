# CLAUDE.md

> **常時ロード**: AI Coding Agent (Claude Code) が ColorMaster で作業する際の起点ガイド。
> ハーネス構造 (`.claude/`)・ドキュメント体系 (`docs/`)・ファイル種別 ⇄ rules の lookup table
> を集約する。詳細な設計指針・フェーズ順序は `docs/harness/plan.md` を Single Source of Truth とする。

## 最初に読むもの

1. **本ファイル `CLAUDE.md`** (常時ロード) — lookup table と最低限のルール
2. **`docs/README.md`** — 全 docs 索引 + 推奨読み順
3. **`docs/harness/plan.md`** — 設計指針・フェーズ・rule / Skill 一覧の Single Source of Truth
4. **`docs/harness/roadmap.md`** — 進捗ビュー
5. **`.claude/rules/rules-index.md`** — rules 索引
6. **タスク種別に応じた docs** (`docs/README.md` の「AI が読む順序」セクション参照)

## ハーネス概要

| 要素 | パス | 目的 |
|---|---|---|
| Skill | `.claude/skills/<name>/SKILL.md` | 起動契機別の責務単位 |
| Rule | `.claude/rules/<name>.md` | コーディング / プロセス / 命名 / セキュリティ規約 |
| MCP | `.claude/mcp.json` | JetBrains MCP / Context7 MCP / Cloudflare MCP の接続 |
| Plan | `docs/plans/PLAN-NNN-*.md` | 単一 PR の取り組み |
| Epic | `docs/epics/EPIC-NNN-<slug>/` | 複数 PR の取り組み |
| ADR | `docs/adr/ADR-NNNN-*.md` | 重要なアーキテクチャ決定 |
| Learning | `docs/harness/learnings/YYYY-MM-DD-pr-N.md` | PR ごとの KPT 出力 |
| Evolution | `docs/harness/evolution-proposals/YYYY-MM-DD.md` | 外部研究駆動の改善提案 |

## ファイル編集対象 ⇄ rules lookup table

各 rule は `.claude/rules/<name>.md` の frontmatter `paths` フィールドで対象ファイル
パターンを指定し、Claude Code が該当ファイルを Read したときに **自動ロード** される
([公式 docs](https://code.claude.com/docs/en/memory#organize-rules-with-claude/rules/))。
本 lookup table は **人間 / AI 向けの索引** であり、自動ロードの実体は各 rule の `paths` 側。

**常時ロード (paths 未設定の rule)**: `rules-index.md` / `pii.md` / `secrets.md` /
`template-language.md` (安全網または全 Markdown 共通の規約のため、起動時にロード)。

> **注**: lookup table が指す rule のうち、未実装 (`planned`) のものは A2-2 / A2-3
> で作成予定。各 rule の現在の `status` (`skeleton (B0)` / `planned (A2-2 / A2-3 / A3 / A6 / A7 / Phase C)`)
> は `.claude/rules/rules-index.md` を参照。Read 時に rule ファイルが見つからない場合は
> 該当ファイルを編集する前に rules-index で実装フェーズを確認すること。

編集対象別の参照 rules:

| パターン | 参照する rules |
|---|---|
| `feature/**/*ViewModel.kt` | viewmodel.md, ui-state.md, repository.md |
| `feature/**/*Screen.kt` | composable.md, i18n.md, navigation.md |
| `feature/**/*UiState.kt`, `*UiAction.kt` | ui-state.md |
| `**/Route.kt` | navigation.md |
| `**/composeResources/**/strings.xml` | i18n.md |
| `core/data/**/*Repository*.kt` | repository.md, error-handling.md |
| `core/network/**/*Client*.kt` | network-client.md, error-handling.md |
| `**/*.sq` | sql-delight.md |
| `**/*Spec.kt`, `**/*Test.kt` | kotlin-test.md |
| `**/build.gradle.kts` | gradle.md |
| `docs/adr/*.md` | adr.md |
| `docs/{requirements,specifications,architecture,api,security}/**` | docs-structure.md |
| `docs/api/colormaster-api.yaml` | docs-structure.md, network-client.md, backend-auth.md |
| `docs/glossary.md`, `docs/codebase-map.md`, `docs/traceability.md` | docs-structure.md |
| `docs/epics/**/` | epic.md |
| `docs/plans/*.md` | plan.md |
| `docs/harness/roadmap.md`, `docs/epics/**/roadmap.md` | roadmap.md |
| `data/users.db*` の追跡 | **禁止** (db-protection.md / pii.md / secrets.md / `.gitignore`) |
| `data/idols.db` 関連 | sqlite-data-file.md |
| Dockerfile | cloud-run-deploy.md, db-protection.md |
| `.github/workflows/**` | gradle.md (CI で `./gradlew check` 起動)、Claude API は呼ばない (ADR 0017) |
| `.claude/skills/**/SKILL.md` | skill-authoring.md |
| `.claude/rules/**` | rules-index.md, docs-structure.md, template-language.md |

## グローバルルール

- **Markdown は全て日本語見出し** (ADR 0027 / `.claude/rules/template-language.md`)
- **frontmatter の配列は block 形式必須** (`-` インデント表記、`[A, B]` 形式は禁止、詳細は `.claude/rules/docs-structure.md`)
- **Conventional Commits** 必須 (`.claude/rules/commit-message.md`、`scripts/install-git-hooks.sh` で検証)
- **PR テンプレートは `.github/PULL_REQUEST_TEMPLATE/<type>.md`** を `--template` で指定
- **設計書本文 (`docs/{requirements,specifications}/**`) にコード断片は書かない** (§4.6.1)
- **auto-merge 禁止** (人間 approve 必須、GitHub Agentic Workflows 原則、R-15)
- **GitHub Actions で Claude API を呼ばない** (コスト回避、ADR 0017)
- **GitHub 操作は `gh` CLI** (MCP より優位、ADR 0024)
- **IDE 操作は JetBrains MCP**、ライブラリ docs は **Context7 MCP**、Cloudflare 操作は **Cloudflare MCP**
- **`@example.com` 以外のメールアドレスを fixture に書かない** (`.claude/rules/pii.md`)
- **`.gitignore` 対象のファイルを絶対追跡しない** (`.env*` / `data/users.db*` / `*-credentials.json` 等)

## Worktree 運用 (implementation-workflow)

- ブランチ: `<種別>/<ID>-<slug>` (例: `feature/PLAN-007-add-search`、`epic/EPIC-001-feature-restructure-pr-01`、`harness/<purpose>`、`chore/<purpose>`)
- Worktree path: `../<repo-name>-worktrees/<branch-slug>` (slug はブランチ名のスラッシュをハイフン化)
- Phase 0 で `git worktree add`、Phase 9 で `git worktree remove` + `git branch -d` (未マージなら停止して人間に通知)

## ハーネスループ (6 フェーズ)

`docs/harness/plan.md` §5.4 参照。

```
Spec Gen → Implementation → Evaluation → Merge → Retrospection → Meta
```

- **Spec Gen**: feature-request / bug-fix / refactor / dependency-upgrade
- **Implementation**: implementation-workflow (10 フェーズ)
- **Evaluation**: code-reviewer (8 aspect 並列、サブエージェント、Claude API 直接呼び出し禁止)
- **Merge**: 人間 approve 後 squash merge
- **Retrospection**: pr-poller → pr-retrospective
- **Meta (二系統)**: harness-meta (内部 KPT) + harness-evolution (外部研究、手動起動のみ)

横断 Skill: **roadmap-tracker** (`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` を一方向ミラーで更新、Plan は対象外)

## 関連

- `docs/harness/plan.md` (Single Source of Truth)
- `docs/README.md` (docs 全体索引)
- `.claude/rules/rules-index.md`
- `AGENTS.md` (Claude Code 以外の AI Coding Agent 向けエントリポイント)
