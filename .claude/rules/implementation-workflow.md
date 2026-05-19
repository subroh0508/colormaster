---
id: rules-implementation-workflow
title: implementation-workflow 10 フェーズ手順規約
status: stable
last_updated: 2026-05-19
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
| 5 | Draft PR 作成 | `gh pr create --draft --body-file <path>` (`--template` と排他、本格 description 起草必須) | PR URL 取得 |
| 6 | code-reviewer 呼出 | Coordinator レビューコメント | Critical = 0 (fix loop 後) |
| 7 | 人間 approve → squash merge | merge commit | 3 条件充足 (CI/Critical/approve) |
| 8 | pr-poller + Plan/Epic frontmatter 同期 + roadmap-tracker | learning ファイル + Plan/Epic frontmatter 同期 + roadmap 更新 | (該当時) mirror PR 起票 |
| 9 | Worktree 削除 | worktree クリーンアップ | branch -d 成功 |

## Phase 0: Worktree 作成 + master fetch (PR #121 レトロ Try 反映)

```bash
# 1. master を最新化 (本 worktree とは別の作業ディレクトリで実行)
git fetch origin master

# 2. unstaged changes があれば stash (PR #135 レトロ Try)
git status --short  # 確認
git stash push -u   # 必要時のみ

# 3. worktree + ブランチ作成 (絶対パス推奨、PR #135 レトロ Try)
git worktree add /Users/<user>/IdeaProjects/<repo-name>-worktrees/<branch-slug> -b <branch-name> origin/master

# 4. stash していたら pop (worktree 内ではなく元 worktree で)
git stash pop  # 必要時のみ
```

- **`git fetch origin master` を必ず Phase 0 冒頭で実行** (PR #121 レトロ Try、PR #120 との rebase 競合再発防止)
- **`git worktree add` のパスは絶対パス推奨** (PR #135 レトロ Try): 相対パス `..` は cwd 起点で解決されるため、別 worktree 内から実行すると `colormaster-worktrees/colormaster-worktrees/...` のような nested path が生成される。cwd がメイン worktree の場合のみ `../<repo-name>-worktrees/<branch-slug>` が正解
- **unstaged changes ありなら `git stash push -u` 後 worktree add + rebase** (PR #135 レトロ Try): `git rebase origin/master` が `error: cannot rebase: You have unstaged changes` で fail するケースを予防、stash → rebase → stash pop の 3 ステップ fallback
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

### Scope 縮小 redirect 受領時の soft reset 3 段階手順 (PR #129 レトロ Try)

ユーザーから「ADR-NNNN 起票 + R-15 緩和 + roadmap 追加」→「config 1 ファイルのみ」のような scope 縮小指示を受けた場合、前 commit を完全に取り消して新規 commit を起票する:

```bash
# 1. 前 commit を soft reset (working tree は維持、staged 化)
git reset --soft HEAD~1

# 2. staged 状態を解除
git restore --staged .

# 3. 元に戻すファイルを個別 restore (working tree を master 同期)
git restore <unwanted-file-1> <unwanted-file-2> ...
#   または全 restore: git restore .
```

その後、新 scope 範囲のファイルのみ再編集 → `git add <files>` → 新 commit。前 PR 番号を維持しつつ branch / PR 内容を差し替える場合は `git push --force-with-lease` + `gh pr edit --body-file <new-body>`。

## Phase 5: Draft PR 作成

### gh pr create 実行手順 (PR #146 / #156 / #158 レトロ Try 反映、改修候補 #5 + #8 統合)

`gh pr create` で Draft PR を起票する。**`--template` と `--body-file` / `--body` は排他** (gh CLI 実仕様、同時指定すると Exit 1 でエラー `` `--template` is not supported when using `--body` or `--body-file` ``、PR #158 dogfooding で実証)。本リポジトリの全 PR は **本格 PR description 起草が必須** (PR レビュー観点 / AC / 関連 ADR 等を構造化記載するため、`.claude/rules/pr-template.md` §必須セクション参照) なので、実運用上は **`--body-file` 一択** で `--template` は使用しない。

**既定 (本格 PR description を起草する場合、本リポジトリ全 PR で採用)**:

```bash
# commit message と PR body を /tmp ファイル経由で渡す (PR #129 レトロ Try)
git commit -F /tmp/<unique-prefix>-commit-msg.txt

# PR body は --body-file 経由 (--template は指定しない、排他のため)
gh pr create --draft \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --body-file /tmp/<unique-prefix>-pr-body.md
```

- PR body 文面は `.github/PULL_REQUEST_TEMPLATE/<type>.md` の内容を `/tmp/<unique-prefix>-pr-body.md` に コピー → 該当 PR 用にカスタマイズ → `--body-file` で渡す (テンプレ構造は手動で揃える、`.claude/rules/pr-template.md` §起票コマンド §「`--body-file` 代替」参照)
- 対応する `<type>.md` の選択は `.claude/rules/pr-template.md` §6 種類のテンプレート + §gh pr create 必須パラメータ を参照 (`feature.md` / `bugfix.md` / `refactor.md` / `dependency-upgrade.md` / `docs.md` / `harness.md` の 6 種)
- **`--draft` は既定** (`pr-draft-policy.md` 規約)、orchestrator 明示指示時のみ即 Ready で起票可、mirror PR は `--draft` 省略可
- PR description frontmatter (HTML コメント `<!-- pr-frontmatter ... -->`) に必須キー (`type` / `related_plan` / `related_epic` / `related_specs` / `related_adrs` / `expected_modules`) を埋める

**例外 (テンプレ default fill、body 起草を省く trivial chore PR のみ)**:

```bash
gh pr create --draft \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --template <type>.md
# --body-file / --body は指定しない (排他)
```

**禁止パターン** (PR #146 / #158 で Exit 1 を実証):

- `--body "$(cat <<EOF...)"` heredoc 直送 (PR #146 で Exit 1 実証): cmux + zsh + heredoc の三重解釈で truncate / escape 不全リスク → **`--body-file` を使用**
- `--template <type>.md --body-file <path>` 同時指定 (PR #158 dogfooding で Exit 1 実証): gh CLI 実仕様で排他 → **どちらか一方のみ指定**

**`/tmp` ファイル経由パターン** (PR #129 / #146 レトロ Try 反映): HEREDOC ネストが深いと zsh paste で escape 事故が起きうるため、commit message を `/tmp/<unique-prefix>-commit-msg.txt`、PR body を `/tmp/<unique-prefix>-pr-body.md` に Write してから `git commit -F` / `gh pr edit --body-file` / `gh pr create --body-file` で参照する。ユーザー手動実行時のコピペ事故も予防。`<unique-prefix>` は branch slug + timestamp 推奨 (例: `harness-harness-meta-batch-20260518`)。

## Phase 6: code-reviewer 呼出 (Evaluation)

- `code-reviewer` Skill を起動 (Generator と独立した Evaluator、R-13)
- 4〜8 aspect をローカル Claude Code のサブエージェントで並列実行 (R-37)
- harness PR の既定 4 aspect: `spec-conformance` / `architecture` / `security` / `code-quality`
- feature PR の既定 6 aspect: 上記 + `test-quality` + `performance`
- A10 完了後 enable: `visual-regression` / `design-tokens`
- Critical findings あり → Phase 3 に戻る (fix loop)、累計 fix loop 上限 3 回 (R-14)
- Critical findings = 0 → Phase 7 へ

### Phase 6 直後の二段 fetch + mergeable 確認 (PR #123 / #125 / #126 レトロ Try)

review 待ち中に master が再進化して rebase が必要になるケースを事前検出するため、Phase 6 完了後に `git fetch origin master` + `gh pr view` mergeable 確認を実施:

```bash
git fetch origin master
gh pr view <PR#> --json mergeable,mergeStateStatus
# mergeable: MERGEABLE / CONFLICTING / UNKNOWN
# mergeStateStatus: CLEAN / DIRTY / BEHIND / BLOCKED / DRAFT / HAS_HOOKS / UNKNOWN / UNSTABLE
```

- `mergeable: CONFLICTING` または `mergeStateStatus: DIRTY` → rebase 必須 (`git rebase origin/master` + `git push --force-with-lease`)
- `mergeStateStatus: BEHIND` → master 取り込みのみで足る場合あり、conflict なしなら `git pull --rebase`
- `mergeable: MERGEABLE` + `mergeStateStatus: CLEAN` → Phase 7 へ進む

## Phase 7: 人間 approve → squash merge

- **3 条件** (`merge-readiness.md`):
  1. CI green (`gh pr checks`)
  2. code-reviewer Critical = 0
  3. 人間 approve または orchestrator 事前承認テキスト
- 3 条件充足後に `gh pr ready` → `gh pr merge --squash` (または `--merge`)
- **auto-merge は禁止** (`merge-readiness.md` R-15)、orchestrator 明示承認による R-15 代替パスは許可

### classifier ブロック発生時の運用 3 ステップ (PR #125 / #129 レトロ Try)

orchestrator 事前承認下でも、classifier (auto mode safety layer) は別 layer で動作するため `gh pr ready` / `gh pr merge` / `git push` 等が denied されることがある。発生時の対応:

1. **denied メッセージを丸ごと報告して停止**: 「Reason: ...」「対象コマンド」「permission allow リストの状態」を抜粋しユーザー (orchestrator) に提示
2. **迂回せず人間判断を仰ぐ**: pbcopy 経由 / commit message 中立化 / sleep-and-retry のような機械的迂回を **しない** (CLAUDE.md「destructive shortcut を避ける」原則)
3. **ユーザー指示に従う**: (a) orchestrator pane で手動実行 / (b) commit message / PR body を中立表現に書き換えて再試行 / (c) 本 PR 中止、の 3 択から選択

詳細パターンは `.claude/rules/harness-meta-criteria.md` の「classifier ブロック対応 迂回パターン辞典」を参照。

## Phase 8: pr-poller 即時起動 + 関連 Plan / Epic frontmatter 同期 + roadmap-tracker

merge 直後に以下 3 step を順次実行する。各 step の責務は独立、step 2 は本 rule の **新責務** (`roadmap-tracker` 片方向ミラー R-34 を侵さない設計)。

### Step 1: pr-poller 即時起動 (learning ファイル生成 trigger)

- `pr-poller` を即時起動して `pr-retrospective` を駆動 → learning ファイル生成 (`docs/harness/learnings/YYYY-MM-DD-pr-<N>.md`)
- learning ファイルは `harness/learnings-batch-YYYY-WW` ブランチに集約、週次 / 件数閾値到達時に PR 起票
- 詳細は `.claude/rules/pr-poller.md` / `.claude/rules/retrospective-format.md` 参照

### Step 2: 関連 Plan / Epic frontmatter 同期 (本 rule の新責務、R-34 非侵犯)

`roadmap-tracker` は R-34 で **Plan 対象外 + Epic 本体 (`README.md`) への逆同期も禁止** (`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` 専用の片方向ミラー)。merge 後の Plan / Epic frontmatter 同期はこれまで責務空白 (Skill suite 構造的 gap) となっていたため、本 rule で **`implementation-workflow` Phase 8 Step 2** に責務を新規付与する。

#### 2-1. 関連 ID 抽出 (3 source 統合)

| Source | 抽出方法 | 例 |
|---|---|---|
| PR description | `gh pr view <PR#> --json body` の出力から `<!-- pr-frontmatter ... -->` ブロックを grep し `related_plan:` / `related_epic:` 行を取得 | `related_plan: PLAN-007` / `related_epic: EPIC-A3` |
| branch 名 | `gh pr view <PR#> --json headRefName` の出力から prefix 別に正規表現抽出 (`^feature/(PLAN-\d{3})-`, `^feature/(EPIC-[A-Z0-9-]+)-pr-\d+$`, `^feature/(A\d+-\d+)-` 等) | `feature/PLAN-007-add-search` → `PLAN-007`、`feature/A2-3-rules-process` → `A2-3` |
| merge commit subject | `gh pr view <PR#> --json mergeCommit` の `mergeCommit.messageHeadline` から `(PLAN-NNN)` / `(EPIC-NNN)` / `(A2-3)` 等の言及を抽出 | `harness: A2-3 process rules (PLAN-005)` → `PLAN-005` + `A2-3` |

3 source の結果を集約 → 重複 ID を除去 → 抽出 ID 集合を確定。

**no-op 条件**: 3 source 全てから ID 抽出不能 (例: branch 名が `harness/<purpose>` で PR description `related_plan: null` + `related_epic: null` + commit subject に ID 言及なし) ならば本 step は no-op で抜ける (`harness/<purpose>` 系の純粋ハーネス改修 PR / mirror PR 等は通常該当)。

#### 2-2. 対応 Plan / Epic ファイルの frontmatter 更新

**Plan** (`docs/plans/PLAN-NNN-*.md`):

- 現値 `status: in-progress` の場合のみ以下を実行:
  - `status: in-progress → completed`
  - `related_pr: null → <PR#>` (整数)
  - `completed_at: null → <YYYY-MM-DD>` (`gh pr view --json mergedAt` の日付部、UTC → JST 変換)
- 現値が `proposed` / `abandoned` / `promoted` の場合は **本 Skill では絶対に書き換えない** (誤検知防止、warning 出力で人間判断を仰ぐ)

**Plan INDEX.md** (`docs/plans/INDEX.md`):

- 対応行 (`| PLAN-NNN | タイトル | type | status | related_epic | 起票日 |`) の **status 列を `in-progress → completed`** に更新
- 行構造は `.claude/rules/plan.md` §INDEX.md 更新規約 と整合、列追加は本 step ではしない

**Epic README** (`docs/epics/EPIC-NNN-*/README.md`):

- 配下構成 PR 全件 merge 済の場合のみ:
  - `status: in-progress` 維持 + **`completed_at` 候補としてフラグ立て** (人間レビューで最終確定)
  - 全 PR merge 済の判定は `gh pr list --search "label:epic:EPIC-NNN" --state all --json state,number,mergedAt` で全件 `state: MERGED` 確認 (label 運用が確立していない場合は構成 PR 番号を README から手動取得して全件確認)
- 部分完了時 (構成 PR の一部が未 merge / open) は **Epic README に手を付けない** (Phase 8 Step 2 では status を `in-progress` 維持)
- **本 Skill は Epic 本体への直接書き戻しを最小化**: 配下全 PR merge 済の確定状態のみ更新、それ以外は人間レビュー / orchestrator 判断で確定

**Epic INDEX.md** (`docs/epics/INDEX.md`):

- Epic 配下全 PR merge 済確定時のみ更新 (status 列 + 完了日列)
- 部分完了時は手を付けない (構成 PR 数列は本 step では更新しない、別 PR で構成 PR 増減時に更新)

#### 2-3. 複数 ID 該当時 / 双方向リンク

- 1 PR が複数 Plan / Epic に紐づく場合 (例: Epic 配下 PR + 関連 Plan): **全件更新** (重複適用)
- Plan が `related_epic: EPIC-NNN` を持つ場合は Epic 配下 PR とみなし、Epic README + Plan の両方を 2-2 ルールで更新
- frontmatter `related_pr` / `related_epic` 等の双方向リンク整合は本 step 完了後に再確認 (`.claude/rules/spec-living-sync.md` 参照)

#### 2-4. 更新方法の 2 通り (inline 同期 vs mirror PR)

| 方法 | 内容 | 推奨基準 | branch 命名例 |
|---|---|---|---|
| **inline 同期** | merge した PR の **同一 PR** に Plan/Epic frontmatter 更新 commit を含める (Phase 5 で本体実装と一緒に commit、`git add -p` で hunk 分離) | 本体 PR の touch ファイル数が **30 以下**、Plan/Epic 更新を含めても影響範囲が増えない場合 | (該当 PR と同一 branch、別 branch 不要) |
| **mirror PR** | merge 後に **別 mirror PR** で Plan/Epic frontmatter 更新を別起票 | 本体 PR が **30 ファイル超** (A5/A6 100+ ファイル等)、Plan/Epic 更新を分離した方が rebase 容易な場合 | `harness/plan-epic-sync-<id>` または `harness/mirror-<phase-id>` |

判定基準は touch ファイル数 > 30 で mirror PR、それ以下なら inline 同期を推奨。**ただし orchestrator 判断で柔軟に切替可**: 並走 spawn 環境では mirror PR がトラブル少ない経験則あり (本セッション 2026-05-19 A5/A6 100+ ファイル並走で stale 化が発生した事例)。

#### 2-5. status 遷移条件 (誤検知防止の安全網)

| 現値 | 抽出 ID 該当 PR merge 済時の動作 | 根拠 |
|---|---|---|
| `proposed` | **書き換えない** (warning 出力) | 着手前に merge は通常起きない、想定外状態のため人間判断 |
| `in-progress` | `completed` に遷移 + `related_pr` / `completed_at` 更新 | 正常系 |
| `completed` | **書き換えない** (no-op) | 既に完了済、重複適用しない |
| `abandoned` | **書き換えない** (warning 出力) | 取り下げ済の Plan が PR merge と関連 = 想定外、人間判断 |
| `promoted` | **書き換えない** (warning 出力) | Epic 昇格済、Plan ファイルは履歴保持、Epic 側で更新 |
| `blocked` | **書き換えない** (warning 出力) | 障壁解消の確認が必要、人間判断 |

#### 2-6. roadmap-tracker との責務分離 (R-34 維持)

- 本 Skill (`implementation-workflow` Phase 8 Step 2): **Plan / Epic 本体 frontmatter + INDEX.md** を更新 (双方向同期、必要時のみ)
- `roadmap-tracker` (Phase 8 Step 3): **`docs/harness/roadmap.md` + `docs/epics/<id>/roadmap.md`** を Read-only で取り込み片方向ミラー (R-34)
- 本 Skill から **`docs/harness/plan.md` への書き戻しは禁止** (`docs/harness/plan.md` は Single Source of Truth、本 Skill は読み取りのみ)
- `roadmap-tracker` は本 Skill が更新した Plan / Epic frontmatter を Read で取り込んで roadmap.md 完了根拠表に反映 (順序依存: Step 2 → Step 3)

### Step 3: roadmap-tracker 起動 (片方向ミラー、既存責務、R-34 維持)

- `roadmap-tracker` を起動 (**Epic 配下 PR / B-A-C フェーズ項目に該当時のみ**、Plan 単体は対象外、R-34)
- mirror PR が必要な場合 (`implementation-workflow` を経由しないマージ + Epic / フェーズ項目該当) は `harness/roadmap-mirror-<phase-id>` ブランチで mirror PR 起票 (`roadmap.md` §手動マージ時の同 PR 更新ルール 参照)

### 本拡張責務の起点 (運用例)

本セッション (2026-05-19) で発覚した実運用 gap (本拡張責務の起点):

| 対象 | merge 済 PR | 当時の frontmatter | 本拡張で目指す動作 |
|---|---|---|---|
| PLAN-001 (ADR 0001-0027) | PR #119 (2026-05-17) | `in-progress` / `related_pr: null` | Step 2-2 で `status: completed` + `related_pr: 119` + `completed_at: 2026-05-17` に同期、INDEX.md 行更新 |
| PLAN-003 (A8 im@sparql Docker) | PR #175 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 (`related_pr: 175` / `completed_at: 2026-05-19`) |
| PLAN-004 (A5 不要モジュール撤去) | PR #176 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 (`related_pr: 176`) |
| PLAN-005 (A6 Lint/Format step1) | PR #182 (2026-05-19) | `in-progress` / `related_pr: null` | 同上 (`related_pr: 182`) |
| EPIC-A3 (Skill 群実装) | 全 15 PR merge 済 | `in-progress` | Step 2-2 で配下全 PR merge 済確定後 `completed` 候補としてフラグ立て、人間レビューで `completed` + `completed_at` 確定 |

stale 5 件の実 frontmatter 更新は本拡張責務の **初回適用 (段 1 dogfood) として別 PR で消化**、本 PR は Skill / rule 改修のみ。

## Phase 9: Worktree 削除

```bash
# 1. マージ済確認 (PR state ベース、PR #123 / #135 レトロ Try)
gh pr view <PR#> --json state,mergedAt
# state: MERGED かつ mergedAt が non-null であることを確認

# 2. worktree 削除 + ブランチ削除 (merge 方式別に分岐)
git worktree remove /Users/<user>/IdeaProjects/<repo-name>-worktrees/<branch-slug>

# 3a. squash merge の場合: branch -d は失敗するため -D で強制削除
git branch -D <branch-name>

# 3b. merge commit (--merge) の場合: branch -d で OK のケース / NG のケースあり
git branch --merged origin/master | grep <branch-name>  # 検出されれば -d
git branch -d <branch-name>  # 検出されなければ -D に切替

# 3c. rebase merge は本リポジトリで未採用
```

- **未マージなら停止して人間に通知** (worktree / branch を残す): `gh pr view --json state` で `MERGED` 以外なら絶対に `-D` で強制削除しない
- **merge 方式別に branch cleanup を分岐** (PR #123 / #135 レトロ Try):
  - `--squash` で merge した PR は新しい commit hash が生成されるため git 視点で「unmerged」扱い → `git branch -d` が拒否される → `gh pr view --json state=MERGED` 確認後に `-D` で強制削除
  - `--merge` (merge commit) でも親 commit が異なるため `--merged origin/master` で検出されないケースあり → 同様に `-D` 許容
  - `-D` を使うのは **PR state が MERGED であることを確認した後** に限定
- `branch -d` で先にトライし、失敗時のみ `-D` に切り替える順序を守る (PR state 未確認のまま `-D` を使うと未マージ work が消滅するリスク)

## commit 分離規範 (single-file PR 内、PR #161/#162/#164/#165/#167 レトロ Try 反映)

`harness-meta-criteria.md` §分割粒度 表は **ファイル数ベース** の PR 粒度規範 (1-5 ファイル = 1 PR 包括、6-20 ファイル = グループ単位 commit 分離)。本セクションは補完として、**1 ファイル PR でも論理的に独立な変更を別 commit に分離する** 規範を SoT 化する。skeleton → active 本格化 PR / Skill SoT 反映 PR / rule 改修 PR では、変更行数が +200 を超えても 1 ファイル PR として並走可能だが、1 commit に集約すると後続レビュー / retro で logical separator が読み取れない問題が PR #161/#162/#164/#165/#167 (Group 3 wave1/wave2) で 5 回反復観測された (採用判定基準 1 強化該当)。

### 分離判定基準 (commit 分離 vs 単一 commit OK)

以下のいずれかに該当する場合、同一ファイル内でも別 commit に分離:

| 区分 | 例 |
|---|---|
| **論理的に独立した目的** | rule 本体の規範追加 + frontmatter `last_updated` 更新 + `rules-index.md` 索引行追加 = 3 commit (各々の取り消し粒度を独立化) |
| **改修候補 ID が異なる** | 改修候補 #3 / #4 / #6 / #7 を SKILL.md 1 ファイルに反映する PR では、各改修候補ごとに 1 commit (`git add -p` 経由でファイル内 hunk 分離) |
| **skeleton → active 本格化 PR の Phase 別** | Phase 0-3 セクション / Phase 4-6 セクション / Phase 7-9 セクション + frontmatter で 4 commit 分離 (例: `.claude/skills/implementation-workflow/SKILL.md` の skeleton → active 本格化) |
| **取り消し時の粒度を独立させたい** | 後続レビューで「frontmatter 更新だけ revert したい」「§N セクション追加だけ revert したい」等の要望が想定される場合 |
| **レビュー時の差分追跡を容易にしたい** | 単一 commit で +200 行超は読み切りコスト高、論理単位で commit 分離すれば `git log -p --first-parent` で差分追跡可能 |

### 例外 (単一 commit OK、commit 分離不要)

以下のいずれかに該当する場合、単一 commit で OK:

| 区分 | 例 |
|---|---|
| **typo / 表記揺れ修正のみ** | 全角半角統一、識別子の表記正規化、機械検出可能な誤字 (`harness-meta-criteria.md` §dry-run 不要条件 と整合) |
| **1-2 行の補正** | frontmatter `last_updated` 単独更新、リンク 1 件追加、見出し 1 件修正 |
| **同一目的の小規模変更が複数箇所** | 同じ rule の §A / §B / §C に同じ修正パターンを適用 (例: 全角コロン → 半角コロン置換を 5 箇所) |
| **logical separator が分離コストに見合わない** | +50 行未満 + 単一目的 (rule 1 セクション追加のみ) は単一 commit で OK |

### 実装手順 (`git add -p` 経由のファイル内 hunk 分離)

同一ファイル内で複数の論理的変更を別 commit に分離する場合、`git add -p` で hunk 単位に staging する:

```bash
# 1. ファイル全体を編集 (Edit / Write tool で複数箇所を一括変更)

# 2. hunk 単位で staging (interactive)
git add -p <file>
# 各 hunk について y/n/s/e/q を選択
#   y = この hunk を staging
#   n = この hunk を skip
#   s = hunk を分割 (split)
#   e = hunk を編集 (edit)
#   q = quit

# 3. staged hunk のみ commit
git commit -F /tmp/<prefix>-commit-msg-1.txt

# 4. 残り hunk を次の commit に
git add -p <file>
git commit -F /tmp/<prefix>-commit-msg-2.txt

# 5. 全 hunk staging 完了まで繰り返し
```

### 学習起点 (5 PR 反復)

- **PR #161** (orchestrator SKILL.md 包括更新): 改修候補 #3/#4/#6/#7 を 1 ファイル / 1 commit で実装、後続レビューで logical separator が読み取れない問題を Problem に記録
- **PR #162** (A3-8 implementation-workflow Skill 本格化): +278 / -26 を 1 commit、Phase 別 commit 分離が読み取れない問題を Problem に記録
- **PR #164** (A3-9 code-reviewer Skill 本格化): +215 / -36 を 1 commit、Coordinator ステップ別 / Gotchas セクション別の logical separator が読み取れない問題を Problem に記録
- **PR #165** (A3-12 roadmap-tracker Skill 本格化): +248 / -28 を 2 commits (initial + fix loop 1)、5 系統別の commit 分離が読み取れない問題を Problem に記録
- **PR #167** (A3-10 pr-retrospective Skill 本格化): +189 / -21 を 1 commit、Phase 別 commit 分離が読み取れない問題を Problem に記録

採用判定基準 1 (複数 PR 反復) を強化条件 (5 回反復) で満たすため、本 SoT 化で再発予防。

### Phase 別の適用タイミング

- **Phase 3 (実装 + Lint + Test)**: 実装中は logical 単位で commit、`./gradlew check` green を各 commit で担保 (途中 commit で fail でも OK だが、PR 起票前に green commit に rebase 整理)
- **Phase 5 (Draft PR 作成)**: PR 起票前に `git log --oneline origin/master..HEAD` で commit 構造を最終確認、各 commit が独立 revertable / 読み切り可能か検証
- **Phase 6 (code-reviewer 呼出)**: fix loop で追加 commit は logical separator 維持、Critical 修正は単独 commit (revert 容易化)
- **Phase 7 (squash merge)**: 本リポジトリは `--squash` 採用のため、merge commit としては単一 commit に圧縮されるが、PR 内 commit 履歴は `gh pr view --json commits` で残り、後続 retro / レビューが trace 可能

### 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `git log --oneline origin/master..HEAD` で commit 数 / 各 commit の touch 行数を計測、`logical separator なし` 疑い (1 ファイル PR + 1 commit + 100 行超) を warning 化
- **commit-msg hook 拡張**: Conventional Commits subject に logical 単位を示す scope 明示を推奨 (例: `feat(skill): orchestrator §Phase 6 セクション差し替え (改修候補 #3)`)

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
- **Phase 0 の `git worktree add` パスは絶対表記推奨** (PR #135 レトロ Try): cwd 依存の `..` 指定で nested path が生成されるケースを予防
- **Phase 0 で unstaged changes がある場合は `git stash push -u` 後 rebase** (PR #135 レトロ Try): `git rebase origin/master` の `error: cannot rebase: You have unstaged changes` を予防
- **Phase 0 と Phase 9 はペア**、Phase 9 を忘れると worktree が肥大化
- **Phase 3 fix loop 上限 3 回** (R-14)、超過時は blocked + 人間通知
- **Phase 4 の Scope 縮小 redirect は soft reset 3 段階で完全取り消し** (PR #129 レトロ Try): `git reset --soft HEAD~1` + `git restore --staged .` + `git restore <files>` の順、`--hard` は使わない
- **Phase 5 で commit message / PR body は `/tmp` ファイル経由** (PR #129 レトロ Try): HEREDOC ネストの quoting 事故予防、`git commit -F` / `gh pr edit --body-file` / `gh pr create --body-file` を採用
- **Phase 5 で `gh pr create --template <type>.md --body-file <path>` の同時指定は禁止** (PR #146 / #158 レトロ Try 反映): gh CLI 実仕様で排他 (`` `--template` is not supported when using `--body` or `--body-file` `` Exit 1)、本リポジトリは本格 PR description 起草必須のため **`--body-file` 一択** で `--template` は使用しない。詳細は §gh pr create 実行手順 参照
- **Phase 5 で `--body "$(cat <<EOF...)"` heredoc 直送は禁止** (PR #146 レトロ Try): cmux + zsh + heredoc の三重解釈で truncate / escape 不全リスク、`--body-file <path>` を使用
- **Phase 6 の code-reviewer は Claude API 直接呼び出しではなくサブエージェント並列** (R-37 / ADR 0017)
- **Phase 6 完了後に二段 fetch + `gh pr view --json mergeable,mergeStateStatus` 確認** (PR #123 / #125 / #126 レトロ Try): review 待ち中の master 再進化で発生する rebase 必要状態を事前検出
- **Phase 7 で auto-merge 禁止** (R-15)、orchestrator 明示承認テキストでの代替パスは許可
- **Phase 7 で classifier ブロック発生時は迂回せず人間判断を仰ぐ** (PR #125 / #129 レトロ Try): denied メッセージ全文報告 → ユーザー指示待ち、機械的迂回は禁止
- **Phase 8 で `roadmap-tracker` を呼ぶのは Epic 配下 PR / B-A-C フェーズ項目のみ**、Plan 単体は対象外 (R-34)
- **Phase 8 Step 2 (Plan/Epic frontmatter 同期) は本 rule の新責務、R-34 を侵さない**: `roadmap-tracker` は Plan 対象外 + Epic 本体逆同期禁止のため、`docs/plans/PLAN-NNN-*.md` / `docs/plans/INDEX.md` / Epic 配下全 PR merge 済時の `docs/epics/EPIC-NNN-*/README.md` の frontmatter 同期は本 Skill が担う。`roadmap-tracker` 片方向ミラー (`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` 専用) との責務分離を明示し、本 Skill から `docs/harness/plan.md` への書き戻しは禁止
- **Phase 8 Step 2 の関連 ID 抽出は 3 source 統合** (PR description `<!-- pr-frontmatter ... -->` の `related_plan` / `related_epic` + branch 名 prefix + merge commit subject 言及): いずれか 1 source から抽出できれば更新、3 source 全てから抽出不能な場合のみ no-op で抜ける (`harness/<purpose>` 系の純粋ハーネス改修 PR / mirror PR 等は通常 no-op)
- **Phase 8 Step 2 で書き換える status は `in-progress → completed` のみ** (誤検知防止の安全網): 現値が `proposed` / `abandoned` / `promoted` / `completed` / `blocked` の場合は本 Skill では絶対に書き換えず warning 出力、人間判断を仰ぐ
- **Phase 8 Step 2 の inline 同期 vs mirror PR 判定は touch ファイル数 > 30 が基準**: 並走 spawn 環境では mirror PR がトラブル少ない経験則あり (本セッション 2026-05-19 A5/A6 100+ ファイル並走で stale 5 件発生した事例、本拡張責務の起点)、orchestrator 判断で柔軟に切替可
- **Phase 8 Step 2 → Step 3 の順序依存**: `roadmap-tracker` は本 Skill が更新した Plan / Epic frontmatter を Read で取り込んで roadmap.md 完了根拠表に反映するため、必ず Step 2 完了後に Step 3 を起動 (順序逆転で frontmatter stale 状態を取り込むと roadmap.md 反映漏れが発生)
- **Phase 8 Step 2 で Epic README を更新するのは配下全 PR merge 済確定時のみ**: 部分完了時 (構成 PR 一部 open / Draft) は Epic 本体 frontmatter を触らない (`status: in-progress` 維持)、`completed_at` 候補フラグも全 PR merge 済 + 人間レビュー後に確定
- **Phase 9 の `branch -d` を先にトライし、失敗時のみ `-D` に切替** (PR #123 / #135 レトロ Try): squash merge / merge commit のいずれも `--merged origin/master` で検出されないケースがある。`gh pr view --json state=MERGED` 確認後に `-D` 許容、PR state 未確認のまま `-D` は禁止 (未マージ work 消滅リスク)
- **worktree path の slug は `branch-naming.md` 規約に厳密に従う**: スラッシュをハイフンに置換、特殊文字なし
- **並走中の rules-index.md / roadmap.md / progress.md の rebase 競合** は EPIC-A2 で頻発、mirror PR で統合解消するパターンを A2-2 / A2-4 / A2-5 で確立
- **single-file PR でも論理的に独立した変更は別 commit に分離** (PR #161/#162/#164/#165/#167 レトロ Try 反映、本 rule §commit 分離規範 参照): 改修候補 ID 別 / Phase 別 / frontmatter 単独 等の logical separator を `git add -p` 経由で実現、+200 行超の skeleton → active 本格化 PR は特に分離推奨

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提)
- ADR 0018 (10 フェーズ設計の SoT)
- `docs/harness/plan.md` §5.3 / §5.4.2 / R-14 / R-15 / R-34 / R-37
- `.claude/rules/{branch-naming,pr-template,pr-draft-policy,merge-readiness,code-reviewer-aspects,spec-living-sync,roadmap,pr-poller,harness-meta-criteria}.md`
- `.claude/rules/harness-meta-criteria.md` §分割粒度 (本 rule §commit 分離規範 と補完関係、ファイル数ベース粒度 vs single-file PR 内 logical separator の二重 SoT)
- `.claude/skills/implementation-workflow/SKILL.md`
- 学習起点: PR #161/#162/#164/#165/#167 retro Try (single-file PR commit logical separator 5 回反復、`docs/harness/learnings/2026-05-18-pr-{161,162,164,165,167}.md`)
