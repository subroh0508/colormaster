---
id: rules-implementation-workflow
title: implementation-workflow 10 フェーズ手順規約
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/implementation-workflow/**"
related_adrs:
  - ADR-0017
  - ADR-0018
related_plan: docs/harness/plan.md §5.3 / §5.4.2 / R-14 / R-15
---

# implementation-workflow.md — 10 フェーズ手順規約

> `implementation-workflow` Skill が Plan / Epic 確定後の実装着手から worktree 削除までを
> 10 フェーズで統合管理する際の詳細手順。各 Phase の入出力 / 成功条件 / 失敗時 fallback /
> 並行実装基盤 (worktree) を規定。

## 全 10 Phase 概要

| Phase | 名称 | 主出力 | 成功条件 |
|---|---|---|---|
| 0 | Worktree 作成 | worktree path + ブランチ | `git worktree list` に新エントリ |
| 1 | docs Read | 要件 / 基本設計 / 詳細設計の理解 | 関連 SPEC / ADR / Plan / Epic を全て読み込み済 |
| 2 | Spec 整合チェック | 整合性レポート | ID 重複・dangling リンクゼロ |
| 3 | 実装 + Lint + Test | commit (fix loop ≤3 回) | `./gradlew check` green |
| 4 | Self-Verification | 三層指標差分 + rule 違反チェック | `@Spec` 整合 + PII / secrets ゼロ |
| 5 | Draft PR 作成 | `gh pr create --draft --template <type>.md` | PR URL 取得 |
| 6 | code-reviewer 呼出 | Coordinator レビューコメント | Critical = 0 (fix loop 後) |
| 7 | 人間 approve → squash merge | merge commit | 3 条件充足 (CI/Critical/approve) |
| 8 | pr-poller + roadmap-tracker | learning ファイル + roadmap 更新 | (該当時) mirror PR 起票 |
| 9 | Worktree 削除 | worktree クリーンアップ | branch -d 成功 |

## Phase 0: Worktree 作成 + master fetch (PR #121 レトロ Try 反映)

```bash
# 1. master を最新化 (本 worktree とは別の作業ディレクトリで実行)
git fetch origin master

# 2. worktree + ブランチ作成 (origin/master ベース)
git worktree add ../<repo-name>-worktrees/<branch-slug> -b <branch-name> origin/master
```

- **`git fetch origin master` を必ず Phase 0 冒頭で実行** (PR #121 レトロ Try、PR #120 との rebase 競合再発防止)
- `<branch-name>` は `.claude/rules/branch-naming.md` に従う (`feature/PLAN-NNN-<slug>` / `feature/EPIC-NNN-<slug>-pr-NN` / `harness/<purpose>` 等)
- `<branch-slug>` はブランチ名のスラッシュをハイフン化 (`feature/PLAN-007-add-search` → `feature-PLAN-007-add-search`)
- **以降の全 Phase はこの worktree 内で実行** (chdir または cwd 指定)
- 並行実装中の touch ファイル重複は worktree 物理分離で回避 (EPIC-A2 A2-2 / A2-4 / A2-5 の並走実績)

## Phase 1: 要件 / 基本設計 / 詳細設計 Markdown を Read

- `docs/requirements/REQ-NNN-*.md`
- `docs/specifications/basic/SPEC-NNN-*.md`
- `docs/specifications/detail/SPEC-NNN-*.md`
- frontmatter の `related_*` を辿って関連 ADR / Epic / Plan を Read
- harness PR の場合: `docs/harness/plan.md` 関連章 + 対象 rule / Skill の現状を Read
- mirror PR の場合: 対象フェーズの roadmap.md + 完了済 PR の commit / merge ログを Read

## Phase 2: Spec 整合性チェック

- SPEC-ID 採番の重複なし (`docs/specifications/{basic,detail}/` の `id:` 一意性)
- `related_basic` / `related_detail` の双方向リンク有効
- frontmatter 必須キー JSON Schema 検証 (A6 で機械化、現状は目視)
- 設計書本文にコード断片混入なし (`docs-structure.md` §4.6 のコード禁止原則)
- harness PR は `rules-index.md` status と実体の整合チェック (A2-1 / A2-2 で正規化済)

## Phase 3: 実装 + Lint + Test (fix loop)

- `.claude/rules/rules-index.md` の lookup table から実装ファイル種別に応じた rules を Read (CLAUDE.md と二重チェック)
- 実装 → `./gradlew check` → 失敗時は修正して再実行
- **fix loop 上限はデフォルト 3 回** (R-14)、超過したら Plan status を `blocked` に書き換え人間に通知
- spec-living-sync (`.claude/rules/spec-living-sync.md`) 発動時は同 PR で docs 修正、PR description「仕様変更箇所」セクション記入
- commit-msg hook (`scripts/install-git-hooks.sh`) で Conventional Commits 形式検証

## Phase 4: Self-Verification

- 三層指標差分 (Line / Branch coverage 差分、Spec coverage 差分、Mutation score) を計測 (A7 完了前は N/A 明記)
- rule 違反チェック (Konsist / Gradle カスタムタスク / 目視):
  - PII / secrets が code / docs / commit に混入していないか
  - 設計書本文にコード断片がないか
  - frontmatter 配列が block 形式か
  - 日本語見出し / 命名規約準拠か
- harness PR は `rules-index.md` の status 整合を再確認

## Phase 5: Draft PR 作成

```bash
gh pr create --draft \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --template <type>.md
```

- `--template` は **必須** (`pr-template.md` 規約)
- `--draft` は **既定** (`pr-draft-policy.md` 規約)、orchestrator 明示指示時のみ即 Ready で起票可
- PR description frontmatter (HTML コメント `<!-- pr-frontmatter ... -->`) に必須キー (`type` / `related_plan` / `related_epic` / `related_specs` / `related_adrs` / `expected_modules`) を埋める
- mirror PR は `--draft` 省略可 (`pr-draft-policy.md` Gotchas 参照)

## Phase 6: code-reviewer 呼出 (Evaluation)

- `code-reviewer` Skill を起動 (Generator と独立した Evaluator、R-13)
- 4〜8 aspect をローカル Claude Code のサブエージェントで並列実行 (R-37)
- harness PR の既定 4 aspect: `spec-conformance` / `architecture` / `security` / `code-quality`
- feature PR の既定 6 aspect: 上記 + `test-quality` + `performance`
- A10 完了後 enable: `visual-regression` / `design-tokens`
- Critical findings あり → Phase 3 に戻る (fix loop)、累計 fix loop 上限 3 回 (R-14)
- Critical findings = 0 → Phase 7 へ

## Phase 7: 人間 approve → squash merge

- **3 条件** (`merge-readiness.md`):
  1. CI green (`gh pr checks`)
  2. code-reviewer Critical = 0
  3. 人間 approve または orchestrator 事前承認テキスト
- 3 条件充足後に `gh pr ready` → `gh pr merge --squash` (または `--merge`)
- **auto-merge は禁止** (`merge-readiness.md` R-15)、orchestrator 明示承認による R-15 代替パスは許可

## Phase 8: pr-poller 即時起動 + roadmap-tracker

- `pr-poller` を即時起動して `pr-retrospective` を駆動 → learning ファイル生成 (`docs/harness/learnings/YYYY-MM-DD-pr-<N>.md`)
- `roadmap-tracker` を起動 (**Epic 配下 PR / B-A-C フェーズ項目に該当時のみ**、Plan 単体は対象外、R-34)
- mirror PR が必要な場合 (`implementation-workflow` を経由しないマージ + Epic / フェーズ項目該当) は `harness/roadmap-mirror-<phase-id>` ブランチで mirror PR 起票 (`roadmap.md` §手動マージ時の同 PR 更新ルール 参照)
- learning ファイルは `harness/learnings-batch-YYYY-WW` ブランチに集約、週次 / 件数閾値到達時に PR 起票

## Phase 9: Worktree 削除

```bash
# 1. マージ済確認
git branch --merged origin/master | grep <branch-name>

# 2. worktree 削除 + ブランチ削除
git worktree remove ../<repo-name>-worktrees/<branch-slug>
git branch -d <branch-name>
```

- **未マージなら停止して人間に通知** (worktree / branch を残す)
- `branch -d` (`-D` ではない) でマージ確認、未マージ時はエラー出力で警告

## Fix loop の上限と blocked 判定

- **Phase 3 fix loop**: `./gradlew check` 失敗 → 修正 → 再実行 を 3 回まで
- **Phase 6 fix loop**: code-reviewer Critical 修正 → 再実行 を 3 回まで
- **累計 fix loop が 3 回超過** したら Plan / Epic の `decisions.md` (Epic 配下時) または Plan 本体 (Plan 単体時) に `status: blocked` を書き込み、人間に通知 (`docs/harness/plan.md` R-14)

## 並行実装基盤 (worktree)

- **複数 Claude Code セッションの並走** は worktree 物理分離で実現 (EPIC-A2 A2-2 / A2-4 / A2-5 並走実績)
- touch ファイル衝突回避:
  - A2-2 (`.claude/rules/`) vs A2-4 (`docs/`) vs A2-5 (`docs/architecture` + `docs/api`) は touch ファイル重複ゼロで並走完走
  - A2-2 / A2-3 は `rules-index.md` の連続編集を回避するため **直列実行** (A2-2 マージ後 A2-3 着手)
- 並走時の rebase 競合は roadmap.md / progress.md / rules-index.md で発生しやすい (PR #121 / #125 / #126 の実績) → mirror PR で解消

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `git worktree list` で未削除 worktree が 3 個以上の状態を warning (Phase 9 漏れ検知)
- **GitHub Actions**: PR merge イベントで「Phase 8 起動済か (learning ファイル + roadmap 更新が同日中に push されたか)」を check (A4 で Skill 統合後)
- **commit-msg hook 拡張**: branch-naming 違反を `pre-push` で検出 (A6 で導入予定、`scripts/install-git-hooks.sh` 拡張)

## Gotchas

- **Phase 0 で `git fetch origin master` を省略しない** (PR #121 レトロ Try、PR #120 との rebase 競合再発防止)
- **Phase 0 と Phase 9 はペア**、Phase 9 を忘れると worktree が肥大化
- **Phase 3 fix loop 上限 3 回** (R-14)、超過時は blocked + 人間通知
- **Phase 6 の code-reviewer は Claude API 直接呼び出しではなくサブエージェント並列** (R-37 / ADR 0017)
- **Phase 7 で auto-merge 禁止** (R-15)、orchestrator 明示承認テキストでの代替パスは許可
- **Phase 8 で `roadmap-tracker` を呼ぶのは Epic 配下 PR / B-A-C フェーズ項目のみ**、Plan 単体は対象外 (R-34)
- **Phase 9 の `branch -d` は `-D` 禁止** (未マージ強制削除を防ぐ)、未マージ検知時は worktree / branch を残して人間判断
- **worktree path の slug は `branch-naming.md` 規約に厳密に従う**: スラッシュをハイフンに置換、特殊文字なし
- **並走中の rules-index.md / roadmap.md / progress.md の rebase 競合** は EPIC-A2 で頻発、mirror PR で統合解消するパターンを A2-2 / A2-4 / A2-5 で確立

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提)
- ADR 0018 (10 フェーズ設計の SoT)
- `docs/harness/plan.md` §5.3 / §5.4.2 / R-14 / R-15 / R-34 / R-37
- `.claude/rules/{branch-naming,pr-template,pr-draft-policy,merge-readiness,code-reviewer-aspects,spec-living-sync,roadmap,pr-poller}.md`
- `.claude/skills/implementation-workflow/SKILL.md`
