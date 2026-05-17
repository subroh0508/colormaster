---
id: rules-implementation-workflow
title: implementation-workflow 10 フェーズ手順規約
status: skeleton
last_updated: 2026-05-17
paths:
  - ".claude/skills/implementation-workflow/**"
related_plan: docs/harness/plan.md §5.3 / §5.4.2 / ADR 0018
---

# implementation-workflow.md — 10 フェーズ手順規約

> `implementation-workflow` Skill が Plan / Epic 確定後の実装着手から worktree 削除までを
> 10 フェーズで統合管理する際の詳細手順。

## Phase 0: Worktree 作成

```bash
git worktree add ../<repo-name>-worktrees/<branch-slug> -b <branch-name>
```

- `<branch-name>` は `.claude/rules/branch-naming.md` に従う (`feature/PLAN-NNN-<slug>` / `epic/EPIC-NNN-<slug>-pr-NN` / `harness/<purpose>` / `chore/<purpose>`)
- `<branch-slug>` はブランチ名のスラッシュをハイフン化 (`feature/PLAN-007-add-search` → `feature-PLAN-007-add-search`)
- **以降の全 Phase はこの worktree 内で実行**

## Phase 1: 要件 / 基本設計 / 詳細設計 Markdown を Read

- `docs/requirements/REQ-NNN-*.md`
- `docs/specifications/basic/SPEC-NNN-*.md`
- `docs/specifications/detail/SPEC-NNN-*.md`
- frontmatter の `related_*` を辿って関連 ADR / Epic / Plan を Read

## Phase 2: Spec 整合性チェック

- SPEC-ID 採番の重複なし
- `related_basic` / `related_detail` の双方向リンク有効
- frontmatter 必須キー JSON Schema 検証 (A6 で機械化)

## Phase 3: 実装 + Lint + Test (fix loop)

- `.claude/rules/rules-index.md` から実装ファイル種別に応じた rules を Read
- 実装 → `./gradlew check` → 失敗時は修正して再実行
- **fix loop 上限はデフォルト 3 回** (R-14)、超過したら Plan に `status: blocked` を書き込み人間に通知

## Phase 4: Self-Verification

- 三層指標 (line/branch coverage 差分 100% / Spec coverage 差分 100% / mutation score 計測)
- rules 違反がないか自己チェック (PII / secrets / 設計書コード断片混入 等)

## Phase 5: Draft PR 作成

```bash
gh pr create --draft --template <type>.md --body-file <draft>
```

- type は `feature` / `bugfix` / `refactor` / `dependency-upgrade` / `harness` / `docs` から選択 (§4.8)
- frontmatter に `type` / `related_plan` / `related_epic` / `related_adrs` / `related_specs` / `expected_modules` を必ず含める

## Phase 6: code-reviewer 呼出 (Evaluation)

- `code-reviewer` Skill を起動 (Generator と独立した Evaluator、R-13)
- 8 aspect をローカル Claude Code のサブエージェントで並列実行 (R-37)
- Critical findings あり → Phase 3 に戻る (fix loop)
- Critical findings = 0 → Phase 7 へ

## Phase 7: 人間 approve → squash merge

- CI green + 全 aspect pass + **人間 approve** の 3 条件で Ready 昇格
- `gh pr merge --squash`
- **auto-merge は禁止** (GitHub Agentic Workflows 原則、R-15)

## Phase 8: pr-poller 即時起動 + roadmap-tracker

- `pr-poller` を即時起動して `pr-retrospective` を駆動 (learning ファイル生成)
- `roadmap-tracker` を起動して完了根拠を登録 (**Epic 配下 PR または B-A-C フェーズ項目に該当時のみ**、Plan 単体は対象外)

## Phase 9: Worktree 削除

```bash
git branch --merged main | grep <branch-name>  # マージ済確認
git worktree remove ../<repo-name>-worktrees/<branch-slug>
git branch -d <branch-name>
```

- **未マージなら停止して人間に通知** (worktree / branch を残す)

## Gotchas

- **Phase 0 と Phase 9 はペア**。Phase 9 を忘れると worktree が肥大化する。
- **Phase 3 fix loop 上限 3 回** (R-14)。
- **Phase 6 の code-reviewer は Claude API 直接呼び出しではなくサブエージェント並列** (R-37 / ADR 0017)。
- **Phase 7 で auto-merge 禁止** (R-15)。
- **Phase 8 で `roadmap-tracker` を呼ぶのは Epic 配下 PR / B-A-C フェーズ項目のみ**。Plan は対象外 (R-34)。

## 関連

- `docs/harness/plan.md` §5.3 / §5.4.2
- ADR 0018 (10 フェーズ設計)
- `.claude/rules/{merge-readiness,pr-draft-policy,spec-living-sync,branch-naming,code-reviewer-aspects}.md`
- `.claude/skills/implementation-workflow/SKILL.md`
