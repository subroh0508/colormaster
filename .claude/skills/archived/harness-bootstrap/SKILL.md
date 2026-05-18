---
name: harness-bootstrap
description: |
  [archived A3-14 / 2026-05-18] Phase A の A1〜A10 を進める汎用 Skill。専用 Skill 群が
  A3 で出揃ったため archived 化。新規呼び出し禁止。本ファイルは履歴参照用。
status: archived
phase: B0
archived_at: 2026-05-18
archived_pr: A3-14
related_plan: docs/harness/plan.md §6.2
related_rules:
  - .claude/rules/adr.md
  - .claude/rules/docs-structure.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/template-language.md
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/retrospective-format.md
---

# harness-bootstrap (archived A3-14)

> **本ファイルは archived 済み**。A3 で専用 Skill 群が出揃ったため、Phase A 用の汎用 Skill としての
> 役割は終了。新規呼び出しは禁止し、各タスク種別は対応する専用 Skill を使用すること。
> 本セクション以下は履歴参照用に残置 (元の B0 骨格)。

## 撤去理由 (A3-14)

- **撤去日**: 2026-05-18 (EPIC-A3 A3-14 PR でマージ)
- **理由**: A3 で feature-request / bug-fix / refactor / adr-author / harness-meta /
  harness-evolution / dependency-upgrade / implementation-workflow / code-reviewer /
  pr-retrospective / pr-poller / roadmap-tracker / ui-snapshot の 13 Skill が出揃い、
  本 Skill が担っていた汎用起票・起草の責務は全て専用 Skill に移行済
  (`docs/harness/plan.md` §6.2 A3 / R-6)。
- **代替先 (タスク種別 → 専用 Skill)**: ADR 起草 → `adr-author` / rules 拡充・docs 拡充 →
  対応する Plan / Epic から `implementation-workflow` 経由 / retro 集約改善 → `harness-meta`
  (dry-run 必須条件は `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件を参照) /
  Skill 実装 → `example-skills:skill-creator` (ADR 0025)。
- **復活時の操作**: `.claude/skills/archived/harness-bootstrap/` から
  `.claude/skills/harness-bootstrap/` へ `git mv` で戻し、frontmatter `status` を
  `archived` → `active`、`archived_at` / `archived_pr` を削除、CLAUDE.md /
  `.claude/rules/rules-index.md` / 関連 rule の参照を復活。ただし復活前に「専用 Skill の
  責務分担でカバーできない gap」が ADR / Epic レベルで明示されていることを前提とする
  (汎用 Skill 復活は anti-pattern)。

---

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は Phase A 内 (主に A1-A3) で行う。

## 役割

専用 Skill 群 (`feature-request` / `bug-fix` / `refactor` / `adr-author` / `harness-meta` 等) が
A3 で揃うまで、`harness-bootstrap` が一時的に汎用 Skill として A1〜A10 の起票・起草を担当する。
A3 完了後に `.claude/skills/archived/` へ移動し、CLAUDE.md からの参照も外す。

## タスク種別の自動判定 (入力パスベース)

| 入力パスのパターン | モード |
|---|---|
| `docs/adr/*.md` を作成・更新 | ADR 起草モード |
| `.claude/rules/*.md` を作成・更新 | rules 拡充モード |
| `docs/{requirements,specifications,architecture,api,security,runbooks}/**` を作成・更新 | docs 拡充モード |
| `.claude/skills/*/` を作成 | Skill 実装モード (`example-skills:skill-creator` を呼び出す) |
| モジュールディレクトリ削除指示 | 撤去モード |
| `build.gradle.kts` / lint 設定追加 | Lint 導入モード |
| 複数 merged retro の `🤖 ハーネス改善提案` を集約消化する指示 | retro 集約改善モード (PR #139 で実証、`harness-meta` Skill 本格化 A3/A4 までの暫定運用) |

複数該当時はユーザーに確認する。

## retro 集約改善モード (PR #141 レトロ Try / 人間追加最優先)

PR #139 で 8 retro × 約 47 件の改善提案を 1 PR で集約消化した手動代替実行パターン。
`harness-meta` Skill 本格化 (A3/A4) までの暫定運用として、本 Skill が以下の 4 ステップを担う。
本格実装 (subagent 並列 dedupe + 採用判定 + dry-run + PR 起票) は A3/A4 harness-meta Skill に委任、
本セクションは **配置のみ** (詳細仕様は `.claude/rules/harness-meta-criteria.md` を参照)。

### ステップ 1: merged retro 全件 parse

- 入力: `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` の `🤖 ハーネス改善提案` セクション
- 抽出: `[rule]` / `[skill]` / `[template]` / `[remove]` プレフィックス付きの未消化提案
- 出力: 提案リスト (PR 番号 × カテゴリ × プレフィックス × 採用判定基準該当箇所)

### ステップ 2: dedupe + カテゴリ化 + スコープ分類

- dedupe: 同一トピックの提案を 1 件に集約 (例: A1 レトロ rules-index 正規化 ⇄ A2-3 status drift)
- カテゴリ化: rule / skill / template / docs / ADR / 横断 learnings 別
- スコープ分類: 採用判定基準 1-5 (`harness-meta-criteria.md` §採用判定基準) のどれを満たすか

### ステップ 3: dry-run フェーズ (PR #141 レトロ Try / 人間追加最優先)

`.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 を参照し、改善提案ごとに dry-run 必要 / 不要を判定。
必要な提案については以下の 5 ステップで適用前検証を実施:

1. **dry-run 入力**: 改善提案を適用した rule / template / skill / docs と、未適用の既存版 (両 commit / branch / worktree のどれを使うかは設計時に確定、A3/A4 本格化時に worktree 並列が候補)
2. **dry-run 実行**: 適用版と未適用版それぞれに対して、過去 retro で問題視された具体的シナリオを **AI (サブエージェント、Generator 独立性のため別 system prompt)** に投げて出力を比較。シナリオ抽出元は対象 retrospective の `⚠️ Problem` セクション (再現条件 + 過去の AI 出力との対比で判定可能なもの)
3. **判定基準**: 適用版が未適用版より明らかに望ましい出力 (誤検知減 / 規約遵守率向上 / 提案精度向上) を出す場合のみ commit + push に進む。改善が見られない / 退化する場合は変更を破棄 or 別案検討
4. **dry-run 結果記録**: `docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md` (テンプレ: `docs/harness/dry-runs/template.md`、索引: `docs/harness/dry-runs/INDEX.md`) に before/after の AI 出力差分と判定理由を残す
5. **必須条件参照**: 適用 / 未適用判定が曖昧な場合は `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 §必須条件不一致時のフォールバック に従い orchestrator (subroh0508) に判定委任

dry-run スキップ可の提案 (typo 修正 / リンク追加等、`harness-meta-criteria.md` §dry-run 不要 表参照) は本ステップを skip して直接ステップ 4 へ。

### ステップ 4: PR 起票 + `📝 harness-meta フィードバック` 一括追記

- PR ブランチ: `harness/<purpose>` (`.claude/rules/branch-naming.md` 参照)
- PR テンプレ: `.github/PULL_REQUEST_TEMPLATE/harness.md` (`.claude/rules/pr-template.md` 参照)
- 各 retro の `📝 harness-meta フィードバック` セクションに「採用」「見送り」表を一括追記
  (PR 番号は起票後に確定、placeholder 残存検出は A6 機械検証で実装予定)
- N PR vs 1 PR 包括判断は `harness-meta-criteria.md` §分割粒度 を参照

## Gotchas

- 本 Skill は B0 時点では雛形のみ。本格動作は Phase A で実装する。
- ADR 起草モードでは `.claude/rules/adr.md` の起票基準を必ず参照する。
- Skill 実装モードでは新規 Skill を本リポジトリに作成せず、Claude Code ユーザースコープの `example-skills:skill-creator` を呼び出すこと (ADR 0025)。
- **retro 集約改善モードの dry-run フェーズは A3/A4 harness-meta Skill 本格化までは手動代替実行**: subagent 並列 / 出力比較 / 判定の自動化は未実装、本セクションは仕様の placeholder。本格実装は ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由) に従い harness-meta Skill 起票時に skill-creator が rubric 評価する。
- **dry-run 必須 / 不要の判定は `harness-meta-criteria.md` §dry-run 必須条件 を SoT とする**: 本 Skill 内で重複定義しない (重複時は rule 側を優先、本 Skill 側は誘導のみ)。
- **dry-run 結果記録ファイルは `docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md`**: テンプレ / 索引は同ディレクトリの `template.md` / `INDEX.md` を参照。

## 関連

- `docs/harness/plan.md` §5.3 (Skill の責務)
- `docs/harness/plan.md` §6.2 A1-A3 (Phase A での本格動作内容)
