---
id: rules-pr-template
title: PR テンプレート選択と gh pr create 運用規約
status: stable
last_updated: 2026-05-17
paths:
  - ".github/PULL_REQUEST_TEMPLATE/**"
  - ".github/pull_request_template.md"
  - ".claude/skills/implementation-workflow/**"
  - "scripts/install-git-hooks.sh"
related_adrs:
  - ADR-0024
  - ADR-0027
related_plan: docs/harness/plan.md §4.8 / §5.4.2
---

# pr-template.md — PR テンプレート選択と gh pr create 運用規約

> 本リポジトリの全 PR は `.github/PULL_REQUEST_TEMPLATE/<type>.md` の type 別必須セクション
> を満たす本格 PR description を起草し、`/tmp/<unique-prefix>-pr-body.md` 経由で
> `gh pr create --body-file <path>` で起票する (`--template` と `--body-file` は **排他**、
> 詳細は §gh pr create 必須パラメータ 参照)。デフォルト `.github/pull_request_template.md`
> は最小フォールバックのみ。詳細運用は本 rule で SoT 化。

## 6 種類のテンプレート

| ファイル | 用途 | ブランチ prefix | commit type | 関連 Plan type |
|---|---|---|---|---|
| `feature.md` | 新機能追加 | `feature/PLAN-NNN-*` または `feature/EPIC-NNN-*-pr-NN` | `feat` | `feature-request` |
| `bugfix.md` | バグ修正 | `fix/PLAN-NNN-*` | `fix` | `bug-fix` |
| `refactor.md` | リファクタ (振る舞い不変) | `refactor/PLAN-NNN-*` | `refactor` | `refactor` |
| `dependency-upgrade.md` | 依存更新 (Renovate / 手動) | `renovate/*` (自動) または `chore/<slug>` | `chore` / `build` | `dependency-upgrade` |
| `docs.md` | docs 単独更新 | `docs/PLAN-NNN-*` | `docs` | `docs` |
| `harness.md` | ハーネス改修 / レトロ起票 / mirror | `harness/<purpose>` | `feat(harness)` / `docs(harness)` | `harness` |

## 起票コマンド

**重要**: `gh pr create` は **`--template` と `--body-file` / `--body` を排他** とする (gh CLI 実仕様、同時指定すると Exit 1 でエラー `` `--template` is not supported when using `--body` or `--body-file` ``、PR #146 / #158 で実証)。本リポジトリの全 PR は本格 PR description 起草が必須 (§必須セクション参照) なので **`--body-file` 一択** で `--template` は使用しない。

```bash
# 既定 (本格 PR description 起草、本リポジトリ全 PR で採用)
gh pr create \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --body-file /tmp/<unique-prefix>-pr-body.md \
  --draft
```

- **`--body-file` 一択 (本リポジトリ既定)**: 本リポジトリの全 PR は本格 PR description 起草必須のため、`.github/PULL_REQUEST_TEMPLATE/<type>.md` の内容を `/tmp/<unique-prefix>-pr-body.md` にコピー → 該当 PR 用にカスタマイズ → `--body-file` で渡す (テンプレ構造は手動で揃える)
- **`--draft` は Phase 5 の既定**: Draft → Ready 昇格は `.claude/rules/pr-draft-policy.md` 参照
- **`--template <type>.md` 単独 (例外、trivial chore PR で body 起草を省く場合のみ)**: `--body-file` / `--body` を同時指定しない、テンプレの default 内容のままで起票

## gh pr create 必須パラメータ (PR #146 / #156 / #158 レトロ Try 反映、改修候補 #8 SoT 化)

`gh pr create` 実行時の必須パラメータ:

| パラメータ | 必須/任意 | 説明 |
|---|---|---|
| `--base <target-branch>` | 必須 | 通常 `master` |
| `--head <source-branch>` | 必須 | 本 PR の source branch (現在チェックアウト中の branch 名) |
| `--title "<...>"` | 必須 | Conventional Commits 形式 (`.claude/rules/commit-message.md` 準拠) |
| `--body-file <path>` | 必須 (本リポジトリ既定) | 本格 PR description を `/tmp/<unique-prefix>-pr-body.md` 経由で渡す。`--template` と排他 |
| `--draft` | 既定 (Phase 5) | `pr-draft-policy.md` 規約、orchestrator 明示指示時のみ即 Ready で起票可、mirror PR は省略可 |
| `--template <type>.md` | 例外時のみ | body 起草を省く trivial chore PR でのみ使用、`--body-file` と排他 |

**`--template` と `--body-file` の排他関係** (gh CLI 実仕様、PR #158 dogfooding で実証):

```text
$ gh pr create --base master --head <branch> --draft --template harness.md --title "..." --body-file /tmp/pr-body-...md
`--template` is not supported when using `--body` or `--body-file`
Exit code 1
```

per-task pane / orchestrator pane で `gh pr create` を実行する Skill (implementation-workflow Phase 5、orchestrator 共通テンプレ等) は、**`--body-file` 一択コマンド例** を共通 prompt テンプレで明示すること。

**完全コマンド例** (本リポジトリ既定、`--body-file` 一択):

```bash
gh pr create \
  --base master \
  --head <branch-name> \
  --draft \
  --title "<conventional-commits-subject>" \
  --body-file /tmp/<unique-prefix>-pr-body.md
```

PR 種別ごとの `<type>.md` 対応は §6 種類のテンプレート 参照、`--body-file` 渡しの body 文面は `.github/PULL_REQUEST_TEMPLATE/<type>.md` の内容を `/tmp/<unique-prefix>-pr-body.md` にコピー → カスタマイズして渡す。

## PR description frontmatter 規約

各テンプレ冒頭に以下の YAML frontmatter (コメント擬装の HTML フォーマット) を必須化:

```html
<!-- pr-frontmatter
type: feature | bugfix | refactor | dependency-upgrade | docs | harness
related_plan: PLAN-NNN | null
related_epic: EPIC-NNN | null
related_specs:
  - SPEC-NNN-N
related_adrs:
  - ADR-NNNN
expected_modules:
  - <touch 予定の glob>
-->
```

- frontmatter は HTML コメント (`<!-- pr-frontmatter ... -->`) として埋め込む (GitHub は YAML frontmatter を render しないため)
- `pr-poller` / `code-reviewer` / `roadmap-tracker` が parse 対象
- 配列は block 形式必須 (`docs-structure.md` と整合)

## 必須セクション (type 別)

### feature.md / bugfix.md / refactor.md

- 概要 (3 行以内 summary)
- 変更内容 (主要ファイル + 差分要約)
- 受け入れ基準 (AC) チェックリスト
- 三層指標差分 (Line / Branch / Spec coverage、Mutation score)
- レビュー観点 (人間レビュアー向け)
- 関連: Plan / Epic / ADR / SPEC リンク

### refactor.md (追加)

- **Behavior Preservation 証拠**: `./gradlew check` 結果 + `verifyRoborazziDebug` 結果 + visual-regression diff の human review 結果

### dependency-upgrade.md

- 更新パッケージ一覧 (before / after バージョン)
- 互換性影響 (Breaking change の有無)
- 検証手順 (`./gradlew check` + 影響範囲のスモークテスト)

### docs.md

- 更新 docs 一覧 (ファイル + 主旨)
- SoT 影響 (plan.md / ADR / rules への波及有無)
- レビュー観点 (技術的正確性 vs 表記揺れ)

### harness.md

- 対象 PR / KPT 要約 (レトロ起票時)
- ハーネス改善提案件数 (採用 / 見送り / 保留)
- 関連 learning ファイル / evolution proposal リンク
- mirror PR の場合: 対象フェーズ ID / 自動同期の手動代替である旨

## 三層指標差分セクションの空欄ルール

A7 完了前は **三層指標 (Kover / Konsist Spec coverage / PITest) が未導入** のため、`feature.md` / `bugfix.md` / `refactor.md` テンプレの「三層指標差分」セクションは:

- **A7 完了まで**: `N/A (Kover/Konsist Spec/PITest 未導入、A7 で導入予定)` と記載 (空欄禁止、誤読防止)
- **A10 完了まで**: UI 変更時は Roborazzi screenshot 添付のみで可、`visual-regression` aspect の binary checklist は skip 妥当
- **A7 完了後**: Before / After / Δ を Kover / Konsist / PITest の出力値で埋める

詳細は各テンプレ冒頭の注記参照。

## auto-merge 禁止

- **GitHub の auto-merge 機能は使わない** (R-15 / `.claude/rules/merge-readiness.md`)
- 人間 approve 後に `gh pr merge --squash` (または `--merge`) を手動実行
- orchestrator (subroh0508) からの明示的事前承認による R-15 代替は許可 (A2-2 / A2-5 の実績、本格化は A3 で `.claude/rules/merge-readiness.md` 参照)

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `.github/PULL_REQUEST_TEMPLATE/*.md` 各ファイルが必須セクション 5-7 種を含むか検証 (frontmatter 必須キー JSON Schema、本文 H2 リスト)
- **GitHub Actions**: PR open 時に body の `<!-- pr-frontmatter ... -->` を parse し、必須キー (`type` / `related_plan` または `related_epic`) の存在を warning コメント (A6 / detekt 統合と同時)

## Gotchas

- **`--template` と `--body-file` は排他** (PR #146 / #158 レトロ Try、gh CLI 実仕様): 同時指定は Exit 1、本リポジトリは本格 PR description 起草必須のため **`--body-file` 一択** で `--template` は使用しない。`<type>.md` のテンプレ内容は `/tmp/<unique-prefix>-pr-body.md` にコピー → カスタマイズして `--body-file` で渡す
- **`--body-file` 経由で渡す本文は `<type>.md` のテンプレ構造を維持**: §必須セクション (type 別) を満たすよう手動で揃える、`--template` 省略によって type 別必須セクション (Behavior Preservation / レトロ要約等) が抜けないこと
- **frontmatter は HTML コメント形式**: GitHub Markdown 仕様で YAML frontmatter を render しないため、`<!-- pr-frontmatter ... -->` で擬装
- **harness.md は branch-naming `harness/<purpose>` 専用**: feature/bugfix/refactor 等の Plan PR では使わない (`branch-naming.md` と整合)
- **三層指標差分セクションは A7 完了まで `N/A` 必須記載** (空欄での暗黙了解は禁止、A1 レトロ Problem の防止)
- **mirror PR は harness.md 必須**: `roadmap-tracker` Phase 8 自動同期の手動代替 PR (`harness/roadmap-mirror-<phase-id>`) は harness.md を選択、対象フェーズ ID と完了根拠表更新内容を本文に明記
- **draft / ready の昇格条件**: `.claude/rules/pr-draft-policy.md` 参照、本 rule では「Phase 5 で `--draft` 起票が既定」のみ規定
- **PR description frontmatter / `expected_modules` に PII / secrets / 追跡禁止パスを混入させない** (PR #135 レトロ Try): `expected_modules` 等の glob 欄に `data/users.db` / `.env*` / `*-credentials.json` 等を誤投入しないこと。詳細は `.claude/rules/pii.md` / `.claude/rules/secrets.md` 参照、`code-reviewer` security aspect でも検証

## 関連

- ADR 0024 (`gh` CLI 採用、PR 操作の SoT)
- ADR 0027 (テンプレ言語 / docs 構造)
- `docs/harness/plan.md` §4.8 / §5.4.2
- `.github/PULL_REQUEST_TEMPLATE/{feature,bugfix,refactor,dependency-upgrade,docs,harness}.md`
- `.claude/rules/{branch-naming,commit-message,pr-draft-policy,merge-readiness,implementation-workflow}.md`
