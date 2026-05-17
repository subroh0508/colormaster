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

> 本リポジトリの全 PR は `.github/PULL_REQUEST_TEMPLATE/<type>.md` から該当 type を
> 選択し、`gh pr create --template <type>.md` で起票する。デフォルト
> `.github/pull_request_template.md` は最小フォールバックのみ。詳細運用は本 rule で SoT 化。

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

```bash
gh pr create \
  --base master \
  --head <branch-name> \
  --title "<conventional-commits-subject>" \
  --template <type>.md \
  --draft
```

- **`--template` は必須**: 省略するとデフォルトテンプレが選ばれ、type 別の必須セクション (Behavior Preservation / レトロ要約等) が抜ける
- **`--draft` は Phase 5 の既定**: Draft → Ready 昇格は `.claude/rules/pr-draft-policy.md` 参照
- **`--body-file` 代替**: 大量の自動生成本文 (フェーズ ID / Plan ID / Epic ID 等の埋め込み済 body) を流し込む場合は `--body-file <path>` を `--template` 代わりに使用 (テンプレ構造は手動で揃える)

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

- **`--template` 省略は禁止**: デフォルトテンプレ (`.github/pull_request_template.md`) は最小骨格、type 別必須セクションが抜けるため
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
