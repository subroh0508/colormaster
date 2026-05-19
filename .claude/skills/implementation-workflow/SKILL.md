---
name: implementation-workflow
description: |
  Plan / Epic 確定後の実装着手 → Lint/Test → AI Review → マージ → レトロ起動 →
  worktree クリーンアップを 10 フェーズ (Phase 0-9) で統合管理するオーケストレーター。
  Phase 0 で git worktree を作成し、Phase 9 で削除することで複数 Claude Code セッションの
  並行実装を物理分離する。`feature-request` / `bug-fix` / `refactor` / `dependency-upgrade`
  が起票した Plan / Epic 確定後の実装着手指示を受けたとき、または orchestrator skill が
  per-task pane に Phase 0-9 自走を委譲したときに本 Skill に従って動作する。
status: active
phase: A3
last_updated: 2026-05-19
related_plan: docs/harness/plan.md §5.3 / §5.4.2 / ADR 0018
related_rules:
  - .claude/rules/implementation-workflow.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pr-draft-policy.md
  - .claude/rules/spec-living-sync.md
  - .claude/rules/branch-naming.md
  - .claude/rules/pr-template.md
  - .claude/rules/commit-message.md
  - .claude/rules/code-reviewer-aspects.md
  - .claude/rules/roadmap.md
  - .claude/rules/pr-poller.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0024
  - ADR-0025
---

# implementation-workflow

> **5 行以内 summary**: Plan / Epic 確定後の実装着手から worktree 削除までを 10 フェーズで統合管理する Skill。
> Phase 0 worktree 作成 + master fetch / Phase 1-4 実装 + Self-Verification / Phase 5 Draft PR 起票 /
> Phase 6 code-reviewer 並列 / Phase 7 R-15 3 条件 merge / Phase 8 pr-poller + Plan/Epic frontmatter 同期 + roadmap-tracker /
> Phase 9 worktree cleanup を順守。fix loop 上限 3 回 (R-14)、auto-merge 禁止 (R-15)、
> Generator/Evaluator 独立性 (R-13) を維持。詳細手順 SoT は `.claude/rules/implementation-workflow.md`。

## 役割

- **単一 PR の実装ライフサイクル統合管理**: Plan / Epic 確定後の implementation 着手指示を受けて、Phase 0-9 を順次実行する責務単位
- **並行実装基盤の提供**: Phase 0 で worktree 物理分離 + Phase 9 で cleanup、複数 Claude Code セッションが touch ファイル衝突なく並走可能
- **Generator/Evaluator 独立性の維持**: 本 Skill は Generator (実装側)、Phase 6 で `code-reviewer` Skill (Evaluator) をサブエージェントで並列起動、R-13 + ADR 0017 準拠
- **品質基準の自動検証**: fix loop (上限 3 回、R-14)、3 条件 merge (CI green + Critical 0 + 人間 approve、R-15)、spec-living-sync (実装中の仕様変更時の双方向同期) を Phase 別動作に組込
- **後続 Skill との連携**: Phase 8 で `pr-poller` (learning ファイル生成 trigger)、`roadmap-tracker` (Epic 配下 PR / B-A-C フェーズ項目のみ起動、Plan 単体は対象外、R-34)

`orchestrator` Skill が per-task pane に Phase 0-9 自走を委譲する上位レイヤとして連携する。本 Skill は単独でも `orchestrator` 経由でも動作可能、入出力契約は同一。

## 入力

- **起動契機**: Plan / Epic 確定後の「実装着手」「Phase 0 開始」「<branch-name> で実装してください」等の指示、または orchestrator skill からの per-task pane キックオフ prompt
- **対象 Plan / Epic**: `docs/plans/PLAN-NNN-*.md` (単一 PR 完結) または `docs/epics/EPIC-NNN-*/` 配下 (構成 PR の 1 件)
- **対象ファイル / 規約**: 編集対象ファイル種別に応じた `.claude/rules/*.md` (CLAUDE.md lookup table + `rules-index.md` から特定)
- **git / gh CLI 認証済環境**: `git fetch origin master` / `gh pr create` / `gh pr merge` 等の権限を持つアカウント
- **`/tmp` 書込権限**: Phase 5 で commit message / PR body を `/tmp/<unique-prefix>-{commit-msg.txt,pr-body.md}` 経由で渡す

## 出力

- **worktree + 専用ブランチ**: Phase 0 で `git worktree add /Users/<user>/IdeaProjects/<repo-name>-worktrees/<branch-slug> -b <branch-name> origin/master`
- **実装 commit (fix loop ≤3)**: Phase 3 で `./gradlew check` green まで修正、Conventional Commits 準拠 + Co-Authored-By 必須
- **Draft PR**: Phase 5 で `gh pr create --draft --body-file <path>` (`--template` と排他、`--body-file` 一択、改修候補 #8 SoT)
- **code-reviewer Coordinator レビューコメント**: Phase 6 で `code-reviewer` Skill 起動 → PR に構造化レビューコメント post
- **merge commit**: Phase 7 で 3 条件充足後 `gh pr merge --squash` (または `--merge`)
- **learning ファイル + Plan/Epic frontmatter + roadmap 更新**: Phase 8 で `pr-poller` / `pr-retrospective` 経由 `docs/harness/learnings/YYYY-MM-DD-pr-<N>.md` 生成 + 関連 Plan/Epic frontmatter 同期 (`docs/plans/PLAN-NNN-*.md` の `status: completed` + `related_pr` + `completed_at`、`docs/plans/INDEX.md` 行更新、Epic 配下全 PR merge 済時のみ `docs/epics/EPIC-NNN-*/README.md` 同期、本 Skill の新責務、R-34 は侵さない) + Epic 配下 PR は `roadmap-tracker` で `docs/epics/<id>/roadmap.md` 完了根拠表に PR# + マージ日追記 (片方向ミラー)
- **副作用**: Phase 9 で `git worktree remove` + `git branch -D` (squash merge 後は強制削除許容、PR state MERGED 確認後のみ)

## フェーズ別動作 (10 フェーズ)

### Phase 0: Worktree 作成 + master fetch

```bash
# 1. master 最新化 (元 worktree で実行)
git fetch origin master

# 2. unstaged changes ありなら stash (PR #135 レトロ Try)
git status --short
git stash push -u   # 必要時のみ

# 3. worktree + ブランチ作成 (絶対パス推奨)
git worktree add /Users/<user>/IdeaProjects/<repo-name>-worktrees/<branch-slug> -b <branch-name> origin/master

# 4. stash していたら pop (元 worktree で)
git stash pop   # 必要時のみ
```

- `<branch-name>` は `.claude/rules/branch-naming.md` 準拠 (`feature/PLAN-NNN-<slug>` / `feature/EPIC-NNN-<slug>-pr-NN` / `harness/<purpose>` 等)
- `<branch-slug>` はスラッシュをハイフン化 (`feature/PLAN-007-add-search` → `feature-PLAN-007-add-search`)
- **以降の全 Phase はこの worktree 内で実行** (chdir または絶対パス指定)
- orchestrator skill 経由で per-task pane spawn される場合、orchestrator が事前に直列で worktree 作成済 (改修候補 #7 SoT、並列 `git worktree add` lock 衝突予防)、per-task pane は Phase 0 で worktree 状態確認のみ

### Phase 1: 要件 / 基本設計 / 詳細設計 Markdown を Read

並列 Read 推奨 (1 message 内で複数 Read tool 呼び出し):

- `docs/requirements/REQ-NNN-*.md`
- `docs/specifications/basic/SPEC-NNN-*.md` + `docs/specifications/detail/SPEC-NNN-*.md`
- frontmatter `related_*` を辿って関連 ADR / Epic / Plan を Read
- **harness PR**: `docs/harness/plan.md` 関連章 + 対象 rule / Skill の現状を Read
- **mirror PR**: 対象フェーズの roadmap.md + 完了済 PR の commit / merge ログを Read

### Phase 2: Spec 整合性チェック

- SPEC-ID 採番の重複なし (`docs/specifications/{basic,detail}/` の `id:` 一意性)
- `related_basic` / `related_detail` の双方向リンク有効
- frontmatter 必須キー JSON Schema 検証 (A6 で機械化、現状は目視)
- 設計書本文にコード断片混入なし (`docs-structure.md` §4.6 のコード禁止原則)
- harness PR は `rules-index.md` の status と実体の整合チェック

### Phase 3: 実装 + Lint + Test (fix loop)

- `.claude/rules/rules-index.md` の lookup table から実装ファイル種別に応じた rules を Read (CLAUDE.md と二重チェック)
- 実装 → `./gradlew check` → 失敗時は修正して再実行
- **fix loop 上限はデフォルト 3 回** (R-14)、超過したら Plan / Epic decisions.md に `status: blocked` を書き込み人間に通知
- spec-living-sync (`.claude/rules/spec-living-sync.md`) 発動時は同 PR で docs 修正、PR description「仕様変更箇所」セクション記入
- commit-msg hook (`scripts/install-git-hooks.sh`) で Conventional Commits 形式検証

### Phase 4: Self-Verification

- 三層指標差分 (Line / Branch coverage / Spec coverage / Mutation score) を計測 (A7 完了前は `N/A (Kover/Konsist Spec/PITest 未導入、A7 で導入予定)` 明記)
- rule 違反チェック (Konsist / Gradle カスタムタスク / 目視):
  - PII / secrets が code / docs / commit に混入していないか (`pii.md` / `secrets.md` redaction 規約)
  - 設計書本文にコード断片がないか
  - frontmatter 配列が block 形式か
  - 日本語見出し / 命名規約準拠か
- harness PR は `rules-index.md` の status 整合を再確認

#### Scope 縮小 redirect 受領時の soft reset 3 段階 (PR #129 レトロ Try)

ユーザーから「ADR-NNNN 起票 + R-15 緩和 + roadmap 追加」→「config 1 ファイルのみ」のような scope 縮小指示を受けた場合、前 commit を完全に取り消して新規 commit を起票:

```bash
git reset --soft HEAD~1      # working tree 維持、staged 化
git restore --staged .        # staged 状態解除
git restore <unwanted-file>  # 元に戻すファイルを個別 restore (or `.` で全 restore)
```

新 scope 範囲のファイルのみ再編集 → `git add <files>` → 新 commit。PR 番号維持しつつ branch 内容を差し替える場合は `git push --force-with-lease` + `gh pr edit --body-file <new-body>`。

### Phase 5: Draft PR 作成

```bash
# commit message と PR body を /tmp ファイル経由で渡す (PR #129 レトロ Try)
git commit -F /tmp/<unique-prefix>-commit-msg.txt

# PR body は --body-file 経由 (--template は排他、改修候補 #8 SoT)
gh pr create --draft \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --body-file /tmp/<unique-prefix>-pr-body.md
```

- PR body 文面は `.github/PULL_REQUEST_TEMPLATE/<type>.md` の内容を `/tmp/<unique-prefix>-pr-body.md` にコピー → カスタマイズ → `--body-file` で渡す
- `<type>.md` は `pr-template.md` §6 種類のテンプレート から選択 (`feature.md` / `bugfix.md` / `refactor.md` / `dependency-upgrade.md` / `docs.md` / `harness.md`)
- `--draft` は既定 (`pr-draft-policy.md`)、orchestrator 明示指示時のみ即 Ready で起票可、mirror PR は省略可
- PR description frontmatter (HTML コメント `<!-- pr-frontmatter ... -->`) に必須キー (`type` / `related_plan` / `related_epic` / `related_specs` / `related_adrs` / `expected_modules`) 埋め
- **`--template` と `--body-file` 同時指定は Exit 1** (gh CLI 実仕様、PR #146 / #158 で実証): `--body-file` 一択で `--template` は使わない
- **`--body "$(cat <<EOF...)"` heredoc 直送は禁止** (PR #146 レトロ Try): cmux + zsh + heredoc の三重解釈で truncate リスク、`--body-file` を使用

### Phase 6: code-reviewer 呼出 (Evaluation、Generator/Evaluator 独立性 R-13)

- `code-reviewer` Skill を起動 (Generator = 本 Skill と独立した Evaluator、R-13)
- 4-8 aspect をローカル Claude Code のサブエージェント (Agent ツール) で並列実行 (R-37)
- **PR 種別別の既定 aspect**:
  - harness PR: 4 aspect (spec-conformance / architecture / security / code-quality)
  - feature / bug-fix / refactor PR: 6 aspect (上記 + test-quality + performance)
  - A10 完了後 enable: visual-regression / design-tokens (UI 変更時に enable)
- Critical findings あり → Phase 3 に戻る (fix loop、累計上限 3 回、R-14)
- Critical findings = 0 → Phase 7 へ

#### Phase 6 直後の二段 fetch + mergeable 確認 (PR #123 / #125 / #126 レトロ Try)

review 待ち中の master 再進化を事前検出:

```bash
git fetch origin master
gh pr view <PR#> --json mergeable,mergeStateStatus
# mergeable: MERGEABLE / CONFLICTING / UNKNOWN
# mergeStateStatus: CLEAN / DIRTY / BEHIND / BLOCKED / DRAFT / HAS_HOOKS / UNKNOWN / UNSTABLE
```

- `CONFLICTING` / `DIRTY` → rebase 必須 (`git rebase origin/master` + `git push --force-with-lease`)
- `BEHIND` → conflict なしなら `git pull --rebase`
- `MERGEABLE` + `CLEAN` → Phase 7

### Phase 7: 人間 approve → squash merge (R-15 3 条件)

- **3 条件** (`merge-readiness.md`):
  1. CI green (`gh pr checks`)
  2. code-reviewer Critical = 0
  3. 人間 approve または orchestrator 事前承認テキスト
- 3 条件充足後 `gh pr ready` → `gh pr merge --squash` (または `--merge`)
- **auto-merge は禁止** (R-15)、orchestrator 明示承認による R-15 代替パスは許可

#### classifier ブロック発生時の運用 3 ステップ (PR #125 / #129 レトロ Try)

orchestrator 事前承認下でも classifier (auto mode safety layer) が別 layer で動作するため `gh pr ready` / `gh pr merge` / `git push` 等が denied されることがある:

1. **denied メッセージを丸ごと報告して停止**: 「Reason: ...」「対象コマンド」「permission allow リストの状態」を抜粋しユーザー (orchestrator) に提示
2. **迂回せず人間判断を仰ぐ**: pbcopy 経由 / commit message 中立化 / sleep-and-retry のような機械的迂回をしない
3. **ユーザー指示に従う**: (a) orchestrator pane で手動実行 / (b) commit message / PR body を中立表現に書き換えて再試行 / (c) 本 PR 中止、の 3 択

詳細パターンは `.claude/rules/harness-meta-criteria.md` §classifier ブロック対応 迂回パターン辞典 参照。

#### orchestrator skill 経由時の特殊フロー (改修候補 #3 SoT)

`orchestrator` Skill から per-task pane に委譲された場合、**per-task pane 自身は `gh pr merge` を実行しない** (classifier denied 率ほぼ 100%)。canonical フロー:

- per-task pane: `gh pr ready` で Ready 昇格 → orchestrator pane に完了報告 (`cmux send` で 200 字未満直送、または touch file `/tmp/orchestrator-status-<task>-ready.txt`)
- orchestrator pane: Ready 昇格を確認 → `gh pr merge --squash` を直接実行 (R-15 事前承認に基づく代行)

### Phase 8: pr-poller 即時起動 + Plan/Epic frontmatter 同期 + roadmap-tracker

merge 直後に以下 3 step を順次実行 (各 step の責務は独立、step 2 で R-34 を侵さない設計):

#### Step 1: pr-poller 即時起動 (learning ファイル生成 trigger)

- `pr-poller` を即時起動 → `pr-retrospective` 駆動 → learning ファイル生成 (`docs/harness/learnings/YYYY-MM-DD-pr-<N>.md`)
- learning ファイルは `harness/learnings-batch-YYYY-WW` ブランチに集約、週次 / 件数閾値到達時に PR 起票

#### Step 2: 関連 Plan / Epic frontmatter 同期 (R-34 を侵さない、本 Skill の新責務)

`roadmap-tracker` は R-34 で Plan 対象外 + Epic 本体 (`README.md`) への逆同期も禁止のため、merge 後の Plan / Epic frontmatter 同期は本 Skill が担う (詳細手順 SoT は `.claude/rules/implementation-workflow.md` §Phase 8 関連 Plan / Epic frontmatter 同期)。

1. **関連 ID 抽出 (3 source 統合)**:
   - **PR description**: `<!-- pr-frontmatter ... -->` 内の `related_plan: PLAN-NNN` / `related_epic: EPIC-NNN` 行を `gh pr view <PR#> --json body` から grep
   - **branch 名**: `feature/PLAN-NNN-*` / `feature/EPIC-NNN-*-pr-NN` / `feature/<phase-id>-*` 等から ID 抽出
   - **merge commit message subject**: `(PLAN-NNN)` / `(EPIC-NNN)` / `(A2-3)` 等の言及から ID 抽出
   - **重複排除 + null skip**: 3 source 統合後に重複 ID を除去、`related_plan: null` / `related_epic: null` の場合は本 step を no-op で抜ける

2. **対応 Plan / Epic ファイルの frontmatter 更新** (現値が `in-progress` の場合のみ):
   - `docs/plans/PLAN-NNN-*.md` frontmatter: `status: in-progress → completed` + `related_pr: <PR#>` + `completed_at: <YYYY-MM-DD>` (merge 日)
   - `docs/epics/EPIC-NNN-*/README.md` frontmatter: `status: in-progress` 維持 + `completed_at` は配下全 PR merge 済の場合のみ `completed` 候補としてフラグ立て (最終決定は orchestrator / 人間レビュー)
   - `docs/plans/INDEX.md`: 対応行の status 列を `in-progress → completed` に更新、`related_pr` 列に PR# 追記
   - `docs/epics/INDEX.md`: Epic 配下全 PR merge 済確定時のみ更新 (部分完了時は手を付けない)
   - **誤検知防止**: 現値が `proposed` / `abandoned` / `promoted` 等の他状態の場合は本 Skill では絶対に書き換えない (人間判断が必要なため warning 出力)
   - **複数 ID 該当時**: 1 PR が複数 Plan / Epic に紐づく場合は全件更新

3. **更新方法の判定 (inline 同期 vs mirror PR)**:

| 方法 | 内容 | 推奨基準 |
|---|---|---|
| **inline 同期** | merge した PR の **同一 PR** に Plan/Epic frontmatter 更新 commit を含める (Phase 5 で本体実装と一緒に commit) | 本体 PR の touch ファイル数が 30 以下、Plan/Epic 更新を含めても影響範囲が増えない場合 |
| **mirror PR** | merge 後に **別 mirror PR** (`harness/mirror-<phase-id>` または `harness/plan-epic-sync-<id>` ブランチ) で Plan/Epic frontmatter 更新を別起票 | 本体 PR が 30 ファイル超 (本セッションの A5/A6 のような 100+ ファイル) で Plan/Epic 更新を分離した方が rebase 容易な場合 |

判定基準は touch ファイル数 > 30 で mirror PR、それ以下なら inline 同期を推奨。並走 spawn 環境では mirror PR がトラブル少ない経験則あり (本セッション A5/A6 事例)、orchestrator 判断で柔軟に切替可。

#### Step 3: roadmap-tracker 起動 (片方向ミラー、既存責務、R-34 維持)

- `roadmap-tracker` を起動 (**Epic 配下 PR / B-A-C フェーズ項目に該当時のみ**、Plan 単体は対象外、R-34)
- `docs/harness/roadmap.md` (全体) + `docs/epics/<id>/roadmap.md` (Epic 別) を Read-only で取り込み → 完了根拠表 / 着手順変更履歴 / 次の推奨着手を更新 (R-34 片方向ミラー)
- mirror PR が必要な場合 (`implementation-workflow` を経由しないマージ + Epic / フェーズ項目該当) は `harness/roadmap-mirror-<phase-id>` ブランチで mirror PR 起票 (`roadmap.md` §手動マージ時の同 PR 更新ルール 参照)

#### Phase 8 stale 事例 (運用例)

本セッション (2026-05-19) で発覚した実運用 gap (本拡張責務の起点):

| 対象 | merge 済 PR | 当時の frontmatter | 本拡張で目指す動作 |
|---|---|---|---|
| PLAN-001 (ADR 0001-0027) | PR #119 (2026-05-17) | `in-progress` / `related_pr: null` | Step 2 で `completed` + `related_pr: 119` + `completed_at: 2026-05-17` に同期 |
| PLAN-003 (A8 im@sparql Docker) | PR #175 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 |
| PLAN-004 (A5 不要モジュール撤去) | PR #176 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 |
| PLAN-005 (A6 Lint/Format step1) | PR #182 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 |
| EPIC-A3 (Skill 群実装) | 全 15 PR merge 済 | `in-progress` | Step 2 で配下全 PR merge 済確定後 `completed` 候補としてフラグ立て、人間レビューで `completed` 確定 |

stale 5 件の実 frontmatter 更新は本 PR スコープ外、別 dogfood PR で消化予定 (本拡張責務の初回適用テスト)。

### Phase 9: Worktree 削除 (orchestrator 経由時は改修候補 #4 SoT)

```bash
# 1. マージ済確認 (PR state ベース、PR #123 / #135 レトロ Try)
gh pr view <PR#> --json state,mergedAt
# state: MERGED かつ mergedAt が non-null であることを確認

# 2. worktree 削除
git worktree remove /Users/<user>/IdeaProjects/<repo-name>-worktrees/<branch-slug>

# 3. ブランチ削除 (merge 方式別)
# 3a. squash merge: 新 commit hash 生成 → 'unmerged' 扱い → -D で強制削除 (PR state=MERGED 確認後)
git branch -D <branch-name>

# 3b. --merge (merge commit): まず -d で試行、失敗時のみ -D
git branch --merged origin/master | grep <branch-name>
git branch -d <branch-name>  # 未検出時は -D
```

- **未マージなら停止して人間に通知** (worktree / branch 残置): `gh pr view --json state` が `MERGED` 以外なら絶対に `-D` で強制削除しない
- **PR state=MERGED 確認後に限り `-D` 許容**: PR state 未確認のまま `-D` は未マージ work 消滅リスク

#### orchestrator skill 経由時の特殊フロー (改修候補 #4 SoT)

`orchestrator` 経由 per-task pane は **`/exit` を実行しない** (per-task pane self-exit 不可)。canonical フロー:

- per-task pane: `git worktree remove` + `git branch -D` (or `-d`) + orchestrator pane に完了報告 (`cmux send` + touch file `/tmp/orchestrator-status-<task>-cleanup.txt`)
- orchestrator pane: per-task pane 完了報告受領 → `cmux close-workspace --workspace workspace:N` で workspace cleanup を代行

## Fix loop の上限と blocked 判定 (R-14)

- **Phase 3 fix loop**: `./gradlew check` 失敗 → 修正 → 再実行 を 3 回まで
- **Phase 6 fix loop**: code-reviewer Critical 修正 → 再実行 を 3 回まで
- **累計 fix loop が 3 回超過** したら Plan / Epic の `decisions.md` (Epic 配下時) または Plan 本体 (Plan 単体時) に `status: blocked` を書き込み、人間に通知

## 並行実装基盤 (worktree)

- **複数 Claude Code セッションの並走** は worktree 物理分離で実現 (EPIC-A2 A2-2 / A2-4 / A2-5 並走実績、EPIC-A3 Group 1 4 並列 / Group 2 3 並列実績)
- **touch ファイル衝突回避**:
  - 並走可能性は `expected_modules` の touch 重複ゼロを事前判定
  - 衝突するペア (例: A2-2 / A2-3 の `rules-index.md` 連続編集) は **直列実行** で rebase 競合予防
- **rebase 競合は roadmap.md / progress.md / rules-index.md で頻発** (PR #121 / #125 / #126 実績) → mirror PR で統合解消
- **`git worktree add` の並列実行は禁止** (改修候補 #7 SoT、`.git/worktrees/.lock` 取得競合で `fatal: cannot lock ref` がほぼ確実): orchestrator が事前に直列で全 worktree 作成 → per-task pane spawn 時は cwd 指定のみ

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `git worktree list` で未削除 worktree が 3 個以上の状態を warning (Phase 9 漏れ検知)
- **GitHub Actions**: PR merge イベントで「Phase 8 起動済か (learning ファイル + roadmap 更新が同日中に push されたか)」を check (A4 で Skill 統合後)
- **commit-msg hook 拡張**: branch-naming 違反を `pre-push` で検出 (A6 で導入予定、`scripts/install-git-hooks.sh` 拡張)

## Gotchas

- **Phase 0 で `git fetch origin master` を省略しない** (PR #121 レトロ Try、PR #120 との rebase 競合再発防止)
- **Phase 0 の `git worktree add` パスは絶対表記推奨** (PR #135 レトロ Try): cwd 依存の `..` 指定で nested path (`colormaster-worktrees/colormaster-worktrees/...`) が生成されるケースを予防
- **Phase 0 で unstaged changes がある場合は `git stash push -u` 後 rebase** (PR #135 レトロ Try): `git rebase origin/master` の `error: cannot rebase: You have unstaged changes` を予防
- **Phase 0 で並列 `git worktree add` 禁止** (改修候補 #7 SoT): `.git/worktrees/.lock` 取得競合、orchestrator skill 経由時は事前直列作成済
- **Phase 0 と Phase 9 はペア**、Phase 9 を忘れると worktree が肥大化
- **Phase 3 fix loop 上限 3 回** (R-14)、超過時は `blocked` + 人間通知
- **Phase 4 の Scope 縮小 redirect は soft reset 3 段階で完全取り消し** (PR #129 レトロ Try): `git reset --soft HEAD~1` + `git restore --staged .` + `git restore <files>` の順、`--hard` は使わない
- **Phase 5 で commit message / PR body は `/tmp` ファイル経由** (PR #129 / #146 レトロ Try): HEREDOC ネストの quoting 事故予防、`git commit -F` / `gh pr edit --body-file` / `gh pr create --body-file` を採用
- **Phase 5 で `gh pr create --template <type>.md --body-file <path>` の同時指定は禁止** (改修候補 #8 SoT、PR #146 / #158 で Exit 1 実証): 本リポジトリは `--body-file` 一択で `--template` は使用しない
- **Phase 5 で `--body "$(cat <<EOF...)"` heredoc 直送は禁止** (PR #146 レトロ Try): cmux + zsh + heredoc の三重解釈、`--body-file <path>` を使用
- **Phase 6 の code-reviewer は Claude API 直接呼び出しではなくサブエージェント並列** (R-37 / ADR 0017): `Agent` ツールで `general-purpose` subagent を 4-8 並列起動、Coordinator が集約
- **Phase 6 完了後に二段 fetch + `gh pr view --json mergeable,mergeStateStatus` 確認** (PR #123 / #125 / #126 レトロ Try): review 待ち中の master 再進化で発生する rebase 必要状態を事前検出
- **Phase 7 で auto-merge 禁止** (R-15)、orchestrator 明示承認テキストでの代替パスは許可
- **Phase 7 で classifier ブロック発生時は迂回せず人間判断を仰ぐ** (PR #125 / #129 レトロ Try): denied メッセージ全文報告 → ユーザー指示待ち、機械的迂回禁止
- **Phase 7 で per-task pane は `gh pr merge` を実行しない** (改修候補 #3 SoT、orchestrator skill 経由時): Ready 昇格まで per-task pane、merge は orchestrator pane が代行
- **Phase 8 で `roadmap-tracker` を呼ぶのは Epic 配下 PR / B-A-C フェーズ項目のみ** (R-34): Plan 単体は対象外
- **Phase 8 Step 2 (Plan/Epic frontmatter 同期) は本 Skill の新責務、R-34 を侵さない** (本セッション 2026-05-19 stale 5 件起点): `roadmap-tracker` は Plan 対象外 + Epic 本体逆同期禁止のため、`docs/plans/PLAN-NNN-*.md` / `docs/plans/INDEX.md` / Epic 配下全 PR merge 済時の `docs/epics/EPIC-NNN-*/README.md` の frontmatter 同期は本 Skill が担う。`roadmap-tracker` の片方向ミラー (`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` 専用) との責務分離を明示し、本 Skill から `docs/harness/plan.md` への書き戻しは禁止
- **Phase 8 Step 2 の ID 抽出は 3 source 統合** (PR description / branch / commit subject): いずれか 1 source からでも抽出できれば更新、`related_plan: null` + `related_epic: null` + branch 名 prefix が `harness/<purpose>` 等 ID 抽出不能ならば本 step は no-op で抜ける
- **Phase 8 Step 2 で書き換える status は `in-progress → completed` のみ** (誤検知防止): 現値が `proposed` / `abandoned` / `promoted` 等の場合は本 Skill では絶対に書き換えず warning 出力、人間判断を仰ぐ
- **Phase 8 Step 2 の inline 同期 vs mirror PR 判定は touch ファイル数 > 30 が基準**: 並走 spawn 環境では mirror PR がトラブル少ない経験則あり (本セッション A5/A6 100+ ファイル事例)、orchestrator 判断で柔軟に切替可
- **Phase 9 の `branch -d` を先にトライし、失敗時のみ `-D` に切替** (PR #123 / #135 レトロ Try): squash merge / merge commit のいずれも `--merged origin/master` で検出されないケースがある、`gh pr view --json state=MERGED` 確認後に `-D` 許容
- **Phase 9 で per-task pane は `/exit` を実行しない** (改修候補 #4 SoT、orchestrator skill 経由時): orchestrator pane が `cmux close-workspace` で代行
- **worktree path の slug は `branch-naming.md` 規約に厳密に従う**: スラッシュをハイフンに置換、特殊文字なし
- **並走中の rules-index.md / roadmap.md / progress.md の rebase 競合** は EPIC-A2 で頻発、mirror PR で統合解消するパターンを A2-2 / A2-4 / A2-5 で確立

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提)
- ADR 0018 (10 フェーズ設計の SoT)
- ADR 0024 (`gh` CLI 採用、PR 操作の SoT)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.3 (Skill の責務) / §5.4.2 (Implementation / Merge フェーズ) / R-13 / R-14 / R-15 / R-34 / R-37
- `.claude/rules/implementation-workflow.md` (本 Skill の詳細手順 SoT)
- `.claude/rules/{branch-naming,pr-template,pr-draft-policy,merge-readiness,code-reviewer-aspects,spec-living-sync,roadmap,pr-poller,commit-message,harness-meta-criteria}.md`
- `.claude/skills/{orchestrator,code-reviewer,pr-poller,pr-retrospective,roadmap-tracker}/SKILL.md` (本 Skill が起動 / 委譲される連携先)
- `.github/PULL_REQUEST_TEMPLATE/{feature,bugfix,refactor,dependency-upgrade,docs,harness}.md` (Phase 5 起票テンプレ)
