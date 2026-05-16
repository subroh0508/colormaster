---
name: implementation-workflow
description: |
  Plan / Epic 確定後の実装着手 → Lint/Test → AI Review → マージ → レトロ起動 →
  worktree クリーンアップを 10 フェーズ (Phase 0-9) で統合管理するオーケストレーター。
  Phase 0 で git worktree を作成し、Phase 9 で削除することで複数 Claude Code セッションの
  並行実装を物理分離する。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §5.4.2 / ADR 0018
related_rules:
  - .claude/rules/implementation-workflow.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pr-draft-policy.md
  - .claude/rules/spec-living-sync.md
  - .claude/rules/branch-naming.md
  - .claude/rules/code-reviewer-aspects.md
---

# implementation-workflow (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。10 フェーズの本格実装は A3 で行う。

## 10 フェーズ構成

| Phase | 内容 |
|---|---|
| 0 | Worktree 作成 (`git worktree add ../<repo-name>-worktrees/<branch-slug> -b <branch-name>`) — 以降全 Phase はこの worktree 内で実行 |
| 1 | 要件 / 基本設計 / 詳細設計 Markdown を Read |
| 2 | Spec 整合性チェック (SPEC-ID 採番確認、`related_*` frontmatter) |
| 3 | rules-index → 実装 + Lint + Test (fix loop 上限 3 回) |
| 4 | Self-Verification (三層指標 + rules チェック) |
| 5 | Draft PR 作成 (`gh pr create --draft --template <type>.md --body-file <draft>`) |
| 6 | `code-reviewer` 呼出 (8 aspect 並列、Coordinator) — Evaluator フェーズ |
| 7 | CI green + 全 aspect pass + 人間 approve → `gh pr merge --squash` (auto-merge 禁止) |
| 8 | `pr-poller` 即時起動 + `roadmap-tracker` で完了根拠登録 (Epic 配下 PR / B-A-C フェーズ項目に該当時) |
| 9 | `git branch --merged main` 確認 → `git worktree remove` + `git branch -d` (未マージなら停止し人間に通知) |

## Gotchas

- **Phase 0 の worktree パスは `../<repo-name>-worktrees/<branch-slug>`** で統一 (`.claude/rules/branch-naming.md`)。`<branch-slug>` はブランチ名のスラッシュをハイフン化 (`feature/PLAN-007-add-search` → `feature-PLAN-007-add-search`)。
- **Phase 3 fix loop は上限 3 回**、超過したら Plan に `status: blocked` を書き込み人間に通知 (R-14)。
- **Phase 7 で auto-merge は禁止** (GitHub Agentic Workflows 原則、R-15)。
- **Phase 9 で未マージブランチを発見したら停止**して人間に通知 (worktree とブランチを残す)。
- `code-reviewer` は **Generator (本 Skill) と同じセッション内のサブエージェント** で並列実行する (R-37、Claude API 直接呼び出しはしない)。

## 関連

- `docs/harness/plan.md` §5.4.2 (Implementation / Merge フェーズ)
- ADR 0018 (`implementation-workflow` Skill の 10 フェーズ設計)
- `.claude/rules/implementation-workflow.md` (詳細手順)
