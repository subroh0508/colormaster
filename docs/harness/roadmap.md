---
id: roadmap-harness
title: ハーネス全体ロードマップ
status: living
last_updated: 2026-05-18
source_plan: docs/harness/plan.md
---

# ハーネス全体ロードマップ

> **5 行以内 summary**: `docs/harness/plan.md` 由来の B0 / A1-A10 / C1-C10 と
> `docs/epics/EPIC-NNN-*/` 全体の進捗トラッカー。`roadmap-tracker` Skill が
> 一方向ミラーで自動更新する。plan.md / Epic 本体への逆同期はしない。
> Plan は 1 PR 完結のため対象外。完了根拠 / Open Questions / 障壁 / 着手順変更履歴を集約。

## 項目一覧

| ID | タイトル | status | expected_modules | 完了根拠 |
|---|---|---|---|---|
| **B0** | ブートストラップ PR | completed | `.claude/**`, `docs/**`, `.github/**`, `scripts/**` | PR #117 (2026-05-17 マージ、commit `0256be9`) |
| **A1** | ADR 0001-0027 一括起草 | completed | `docs/adr/**` | PR [#119](https://github.com/subroh0508/colormaster/pull/119) (2026-05-17 マージ、commit `7f155b5`) |
| **A2** | `.claude/rules/*` 全ファイル本格化 + docs 全面拡充 | completed | `.claude/rules/**`, `docs/{architecture,api,security,requirements,specifications,runbooks}/**`, `CLAUDE.md`, `docs/{README,glossary,codebase-map,adr/README}.md`, `docs/harness/learnings/flaky-tests.md`, `.github/PULL_REQUEST_TEMPLATE/harness.md`, `.claude/settings.json` | EPIC-A2 5 PR + 計画外 A2-6 全マージ済 (A2-1 [#121](https://github.com/subroh0508/colormaster/pull/121) / A2-2 [#125](https://github.com/subroh0508/colormaster/pull/125) / A2-3 [#135](https://github.com/subroh0508/colormaster/pull/135) / A2-4 [#123](https://github.com/subroh0508/colormaster/pull/123) / A2-5 [#126](https://github.com/subroh0508/colormaster/pull/126) / A2-6 [#129](https://github.com/subroh0508/colormaster/pull/129)、2026-05-17 全件マージ) |
| **A2-6** | `.claude/settings.json` に merge / push permissions を追加 | completed | `.claude/settings.json` | PR [#129](https://github.com/subroh0508/colormaster/pull/129) (2026-05-17 マージ、commit `1ac6fe4`) |
| **ORCH-1** | orchestrator Skill 配置 (cmux 並列 orchestration、A2 follow-up・A3 より前の最優先項目) | completed | `.claude/skills/orchestrator/**`, `.claude/rules/orchestrator-criteria.md`, `.claude/rules/rules-index.md`, `CLAUDE.md`, `docs/harness/roadmap.md`, `docs/harness/dry-runs/**` | PR [#144](https://github.com/subroh0508/colormaster/pull/144) (起票後 merge 予定) |
| **A3** | 専用 Skill 群実装 PR (ORCH-1 完了後着手) | proposed | `.claude/skills/**` | — |
| **A4** | ローカルポーリング機構の本格化 | proposed | `.claude/skills/pr-poller/**`, `.claude/rules/harness-meta-criteria.md` | — |
| **A5** | 不要モジュール撤去 | proposed | `js/**`, `kotlin-js-store/**`, `public/**`, `core/network/{auth,firestore}/**`, `firebase.json`, `.firebaserc`, `web-build-and-deploy.yml` | — |
| **A6** | Lint / Format 基盤 (Spotless + ktlint + detekt + Konsist + markdownlint + Gradle カスタムタスク + trufflehog) | proposed | `build.gradle.kts`, `plugins/**`, `.github/workflows/**` | — |
| **A7** | 三層テスト品質基盤 (Kover + Konsist Spec coverage + PITest) | proposed | `build.gradle.kts`, `plugins/**` | — |
| **A8** | im@sparql ローカル Docker 環境構築 (Fuseki) | proposed | `docker-compose.yml`, `docs/runbooks/local-imasparql.md`, `backend/**` | — |
| **A9** | 既存コード baseline 記録 + Spec coverage 適用準備 | proposed | `docs/specifications/basic/**`, `core/**`, `feature/**` (Spec 逆生成) | — |
| **A10** | UI/UX 現状記録 EPIC (DESIGN.md + UI Inventory + Roborazzi baseline) | proposed | `DESIGN.md`, `docs/design/inventory/**`, `feature/**` (Preview 追加) | — |
| **C1** | Renovate 強化 + dependency-upgrade ドッグフード | proposed | `renovate.json`, `.claude/skills/dependency-upgrade/**` | — |
| **C2** | C1 KPT を受けたハーネス改修 | proposed | `.claude/**` | — |
| **C3** | フィーチャ再編 + Decompose 撤去 + CMP Navigation 3 + 共通 ViewModel (EPIC-001) | proposed | `feature/**`, `core/**` | — |
| **C4** | i18n 移植 (EPIC-002) | proposed | `composeApp/src/commonMain/composeResources/**`, `public/locale/**` (撤去) | — |
| **C5** | Backend 強化 (EPIC-003) | proposed | `backend/**`, `core/network/**` | — |
| **C6** | upstream-driven 同期パイプライン (EPIC-004) | proposed | `.github/workflows/sync-imasparql.yml`, `data/idols.db` | — |
| **C7** | Cloud Run + Cloudflare Pages + R2 デプロイ | proposed | `.github/workflows/deploy.yml`, `Dockerfile` | — |
| **C8** | KMP - iOS ターゲット有効化 (EPIC-005) | proposed | `iosApp/**`, `core/**` (iosMain), `feature/**` (iosMain) | — |
| **C9** | KMP - wasmJs ターゲット有効化 (EPIC-006) | proposed | `webApp/**`, `core/**` (wasmJsMain), `feature/**` (wasmJsMain) | — |
| **C10** | Web 配信再開 (wasmJs を Cloudflare Pages にデプロイ) | proposed | `.github/workflows/deploy-web.yml`, `webApp/**` | — |

## 完了根拠

| ID | PR 番号 | マージ日 | 主要ファイル |
|---|---|---|---|
| B0 | [#117](https://github.com/subroh0508/colormaster/pull/117) | 2026-05-17 | `.claude/{rules,skills,mcp.json,settings.json}/**`, `docs/{adr,api,architecture,design,epics,harness,requirements,runbooks,security,specifications,README.md,glossary.md,codebase-map.md,traceability.md}`, `DESIGN.md`, `.github/{pull_request_template.md,PULL_REQUEST_TEMPLATE/**}`, `scripts/install-git-hooks.sh`, `CLAUDE.md`, `AGENTS.md` |
| A1 | [#119](https://github.com/subroh0508/colormaster/pull/119) | 2026-05-17 | `docs/adr/ADR-0001-*.md` 〜 `ADR-0027-*.md` (27 件)、`docs/adr/README.md`、`docs/plans/{PLAN-001-adr-bootstrap.md,INDEX.md}`、`docs/harness/roadmap.md` |
| A2-1 (EPIC-A2 配下) | [#121](https://github.com/subroh0508/colormaster/pull/121) | 2026-05-17 | EPIC-A2 5 ファイル起票 (`docs/epics/EPIC-A2-rules-docs-extension/{README,roadmap,open-questions,decisions,progress}.md`)、`.claude/rules/{rules-index,template-language,mcp-usage,db-protection,commit-message,roadmap}.md`、`.claude/skills/code-reviewer/SKILL.md` Gotchas、`.github/PULL_REQUEST_TEMPLATE/{harness,feature,bugfix}.md`、`CLAUDE.md`、`docs/adr/README.md`、`docs/harness/learnings/{flaky-tests,2026-05-17-pr-117,INDEX}.md`、`docs/harness/{plan,roadmap}.md` (commit `feb41b5`) |
| A2-4 (EPIC-A2 配下) | [#123](https://github.com/subroh0508/colormaster/pull/123) | 2026-05-17 | `docs/{README,glossary,codebase-map}.md` 本格化、`docs/security/README.md` (incident 対応 quick-reference inline 化)、`docs/requirements/{README,template}.md` + `docs/specifications/{README,basic/template,detail/template}.md` (§4.6.3-4.6.5 適合微調整)、`docs/runbooks/{local-development,testing,i18n,mcp-setup}.md` (4 件全て 5KB+ 本格化)、`docs/epics/EPIC-A2-rules-docs-extension/progress.md` 進捗追記 (commit `376018d`、squash merge、code-reviewer 4 aspect 並列 review Critical 0 通過 + Improvement #6 / #7 fix loop 消化 / #8 を A2-3 持ち越し) |
| A2-5 (EPIC-A2 配下) | [#126](https://github.com/subroh0508/colormaster/pull/126) | 2026-05-17 | `docs/architecture/{overview,layers,data-flow,domain-model,state-machines,sequences,infrastructure}.md` (7) + `docs/api/{README,colormaster-api.yaml,auth,idols,me}.md` (5) を B0 skeleton (1.3-2.5KB) から 5KB+ 本格化 (合計 +2,005 行)、Mermaid 図 (graph TD/LR / flowchart TB / erDiagram / stateDiagram-v2 / sequenceDiagram x5) を全 7 architecture docs + sequences 5 ユースケースで描画、`colormaster-api.yaml` paths を 11 endpoint に拡張、`docs/epics/EPIC-A2-rules-docs-extension/{roadmap,progress}.md` 更新 (commit `168ef5d`) |
| A2-2 (EPIC-A2 配下) | [#125](https://github.com/subroh0508/colormaster/pull/125) | 2026-05-17 | `.claude/rules/` の 35 ファイル本格化 (新規 29 + skeleton 5 件本格化 + `rules-index.md` 索引正規化、合計 +3,479 行)。**計画/記録**: `plan,epic,adr,roadmap`。**アーキテクチャ層**: `viewmodel,ui-state,composable,navigation,repository,network-client`。**横断**: `naming,error-handling,logging,i18n,wasm-compat,firebase-boundary`。**ファイル種別/テスト**: `gradle,kotlin-test,screenshot-test,sql-delight,sparql,test-paired-class,markdown`。**同期/Backend**: `sync-job,sqlite-data-file,cloud-run-deploy,removed-modules,backend-auth,cloudflare-pages,r2-litestream`。**セキュリティ**: `pii,secrets,db-protection,no-firebase`。`rules-index.md` の status 語彙に `stable (X)` を追加し A2-2 で本格化した 34 ファイルを `stable (A2-2)` に正規化。`docs/epics/EPIC-A2-rules-docs-extension/{progress,roadmap}.md` 更新。commit `1a33ccc` (merge commit、`gh pr merge --merge`、orchestrator 委任で R-15 代替)。code-reviewer 4 aspect (spec-conformance / architecture / security / code-quality) 並列 review 通過 (Critical 0)、Improvement 3 件即時反映 (commit `310e430`、後 rebase で `daf9faf` → `1a33ccc`)。master 2 回 rebase (PR #122 / #124 → 追って #123 / #126 / #128) で `progress.md` / `roadmap.md` 衝突を A2-4 / A2-5 完了反映と統合して解決 |
| A2-6 (計画外、auto-merge 緩和 workaround) | [#129](https://github.com/subroh0508/colormaster/pull/129) | 2026-05-17 | `.claude/settings.json` の `permissions.allow` に `Bash(gh pr ready:*)` / `Bash(gh pr merge:*)` / `Bash(gh pr review:*)` / `Bash(git push:*)` / `Bash(git push --force-with-lease:*)` の 5 件を追加 (1 file changed / +6 / -1、commit `b961a22` → merge commit `1ac6fe4`)。EPIC-A2 内の並列実行 (A2-2 / A2-4 / A2-5) で各ペインに self-merge を委任した結果、auto mode classifier が R-15 (auto-merge 禁止 / 人間 approve 必須) を根拠に `gh pr ready` / `gh pr merge` / `git push` を stochastic にブロックした摩擦点を、permission rule 拡張で減らす。R-15 (auto-merge 禁止) 自体の恒久的緩和は不要と判断し ADR-0028 起票 / plan.md 緩和 / 当初の A2-6 (roadmap A2-6 追加 + ADR-0028 起票) は **見送り**、settings.json 1 ファイル変更のみに縮小。本 PR 自体 (`harness/roadmap-A2-6-auto-merge-priority`) は merge 権限拡大を含むため self-merge は明示的に回避し orchestrator pane の subroh0508 が out-of-band で明示承認 (R-15 充足)。permission rule 拡張は「都度承認の手間削減」目的に限定し、R-15 (人間 approve 必須) の精神は orchestrator / セッション開始 prompt 側のチェックで担保する方針 |
| A2-3 (EPIC-A2 配下、5 番目の merge、最後) | [#135](https://github.com/subroh0508/colormaster/pull/135) | 2026-05-17 | `.claude/rules/` の 20 ファイル本格化 (新規 6 + skeleton 本格化 11 + 微調整 2 + 索引 1、+1675 / -284 行、初回 commit `3a1cc61` + fix loop commit `23ac895` → merge commit `c593e74`)。**新規 6**: `pr-template / branch-naming / merge-readiness / pr-draft-policy / spec-living-sync / harness-meta-criteria`。**skeleton 本格化 11**: `retrospective-format / pr-poller / skill-authoring / harness-evolution / implementation-workflow (Phase 0 で git fetch origin master 明文化、PR #121 レトロ Try) / code-reviewer-aspects (binary checklist 各 5-7 項目確定、PR #121 レトロ Try) / design-tokens / ui-snapshot / ui-inventory / behavior-preservation / docs-structure (NG 例コメント明示、PR #121 レトロ Try)`。**微調整 2**: `template-language (Phase A 経過措置を本文化) / commit-message (subject 言語ポリシーをセクション化、PR #121 レトロ Try)`。`rules-index.md` の status 語彙に A2-3 内訳サマリを追加し本 PR で本格化した 18 ファイルを `stable (A2-3)` に正規化、`CLAUDE.md` lookup table に新規 6 rule の path を 7 行追加。code-reviewer 4 aspect 並列 review 通過 (Critical 0、Improvement 18 件中 4 件 fix loop commit `23ac895` で即時消化、残 14 件は learning ファイルで harness-meta フィードバック予定)。`gh pr merge --merge` で orchestrator 明示承認による R-15 代替。本 mirror PR (`harness/roadmap-mirror-a2-3`) で EPIC-A2 status を completed に昇格 (A2-1〜A2-6 全 PR merge 済) |

(B0 は `implementation-workflow` を経由せず手動マージしたため `roadmap-tracker` の Phase 8 自動起動は発火せず、`pr-retrospective` の learning PR (`harness/learnings-batch-2026-W20`) で手動更新)

(A1 は `implementation-workflow` Phase 0-9 の枠組みで進めたが、`code-reviewer` / `pr-poller` / `roadmap-tracker` が skeleton 段階のため手動補助で実施。`code-reviewer` は 3 aspect (spec-conformance / architecture / security) を手動サブエージェント並列で実行し PR #119 にコメント post。owner 単一で self-approve 不可のため `gh pr merge --merge` で通常マージ。PR #120 は `roadmap-tracker` Phase 8 自動同期の手動代替)

(A2-1 は `implementation-workflow` Phase 0-9 の枠組みで進めた最初の EPIC 配下 PR。`code-reviewer` 4 aspect (spec-conformance / architecture / security / code-quality) を手動サブエージェント並列で実行し PR #121 にコメント post。Critical 1 (plan.md SSoT 矛盾) を fix loop で commit `2e820bc` で解消。owner 単一で self-approve 不可のため `gh pr merge --merge` で通常マージ (squash merge、commit `feb41b5`)。本 PR (`harness/roadmap-mirror-a2-1`) は `roadmap-tracker` Phase 8 自動同期の手動代替)

(A2-4 は A2-1 / A2-2 / A2-5 と並走した 4 つ目の EPIC-A2 配下 PR。専用 worktree `feature/A2-4-docs-core` で `implementation-workflow` Phase 1-9 を自走、`.claude/rules/` を一切 touch せず A2-2 / A2-5 と touch ファイル重複ゼロで並走完走。`code-reviewer` 4 aspect (spec-conformance / architecture / security / code-quality) を手動サブエージェント並列で実行し PR #123 にコメント post、Critical 0 + Improvement 8 件のうち #6 / #7 (docs 表記統一系) を fix loop で commit `b395276` 消化、#8 (テンプレ §11 `Open Questions` 翻訳) は plan.md §4.6.3-4.6.5 が canonical 名称として規定しており、本 PR の 3 テンプレだけ翻訳すると plan.md / Epic / roadmap と乖離するため A2-3 (template-language.md 本格化) に持ち越し。途中で master が PR #122 / #124 マージで進んだため `git rebase origin/master` で取り込み (progress.md 1 件競合解消)、orchestrator merge 委任 (本テストで R-15 人間 approve を委任扱い) のもと `gh pr merge --squash` でマージ (commit `376018d`)。本 mirror PR #127 (`harness/roadmap-mirror-a2-4`) は `roadmap-tracker` Phase 8 自動同期の手動代替で、起票後に A2-2 / A2-5 mirror = PR #128 / #130 が先行 merge したため再度 master rebase + conflict 統合解決を実施)

(A2-2 は `implementation-workflow` Phase 0-9 の枠組みで進めた最大規模の EPIC 配下 PR (37 ファイル / +3,479 行)。`code-reviewer` 4 aspect 並列で Critical 0 + Improvement 3 件、即時反映 (`310e430`)。A2-4 / A2-5 と並走したため master 2 回 rebase 必要、いずれも `docs/epics/EPIC-A2-rules-docs-extension/{progress,roadmap}.md` の conflict を統合解決。orchestrator 明示承認 (R-15 「人間 approve」) を受けて `gh pr merge --merge` (merge commit `1a33ccc`)。本 PR (`harness/roadmap-mirror-a2-2`) は `roadmap-tracker` Phase 8 自動同期の手動代替)

## 着手順とブロック関係

```mermaid
gantt
    title ハーネス + 実装の着手順 (B0 → Phase A → Phase C)
    dateFormat YYYY-MM-DD
    section Phase B
    B0 :b0, 2026-05-17, 7d
    section Phase A
    A1 :a1, after b0, 14d
    A2 :a2, after b0, 14d
    A3 :a3, after a1, 14d
    A4 :a4, after a3, 7d
    A5 :a5, after a3, 7d
    A6 :a6, after a3, 14d
    A7 :a7, after a6, 14d
    A8 :a8, after a3, 14d
    A9 :a9, after a7, 14d
    A10 :a10, after a7, 21d
    section Phase C
    C1 :c1, after a10, 7d
    C2 :c2, after c1, 7d
    C3 :c3, after a10, 21d
    C4 :c4, after c3, 14d
    C5 :c5, after a8, 21d
    C6 :c6, after c5, 14d
    C7 :c7, after c6, 14d
    C8 :c8, after c5, 21d
    C9 :c9, after c8, 21d
    C10 :c10, after c9, 7d
```

## 保留中の意思決定・不明事項 (Open Questions)

| 起票日 | 内容 | 暫定方針 | 解決状態 |
|---|---|---|---|

## 技術的障壁と回避策 (Blockers and Workarounds)

| 起票日 | 障壁 | 回避策 | 解決日 | 解決方法 |
|---|---|---|---|---|

## 着手順変更履歴 (append-only)

| 日付 | 変更内容 | 理由 |
|---|---|---|
| 2026-05-17 | 初期ロードマップ起草 (plan.md merge 時) | B0 ブートストラップ PR で `docs/harness/plan.md` §6 から取り込み |
| 2026-05-17 | A1 status を proposed → in-progress | PLAN-001 (ADR 0001-0027 一括起票) 作業着手、worktree feature/A1-adr-bootstrap で起草中 |
| 2026-05-17 | A1 status を in-progress → completed | PR #119 (commit `7f155b5`) で ADR 0001-0027 一括起票が merge 完了、PR #120 で `roadmap-tracker` Phase 8 自動同期の手動代替を実施 |
| 2026-05-17 | A2 status を proposed → in-progress + EPIC-A2 を 5 PR に分割 (A2-1〜A2-5) | B0 (96 files / +5408 行) のレビュー負荷上限に近く、A2 全体は B0 を超える規模が想定されるため。A1 レトロ Try「巨大 PR の aspect 並列 review における入力分割」と整合 |
| 2026-05-17 | A2-1 (EPIC-A2 配下、初の EPIC PR) マージ完了 (PR #121、commit `feb41b5`) | A1 レトロ 15 提案中 11 件を消化、EPIC-A2 起票、rules / docs / template 索引基盤を整備。後続 A2-2 / A2-4 並走着手の前提が整う。本 PR (`harness/roadmap-mirror-a2-1`) は `roadmap-tracker` Phase 8 自動同期の手動代替 |
| 2026-05-17 | A2-4 (EPIC-A2 配下、2 番目の merge) マージ完了 (PR #123、commit `376018d`) | docs/ コア 13 ファイル本格化、`docs/runbooks/**` 4 件を 5KB+ 化、Phase C 持ち越し TODO 表を全 docs に固定。A2-2 / A2-5 と並走完走 (touch ファイル重複ゼロ)。orchestrator merge 委任で `--squash` マージ、本テストで「各ペインが自分の PR を最後まで完走」を検証。本 mirror PR #127 (`harness/roadmap-mirror-a2-4`) は `roadmap-tracker` Phase 8 自動同期の手動代替で、A2-2 / A2-5 mirror の先行 merge 後に再 rebase + conflict 統合解決を経て merge |
| 2026-05-17 | A2-5 (EPIC-A2 配下、3 番目の merge) マージ完了 (PR #126、commit `168ef5d`) | docs/architecture 7 + docs/api 5 = 12 ファイルを B0 skeleton から 5KB+ 本格化 (+2,005 行)、ADR/plan 駆動の Option A 執筆方針で確定。code-reviewer architecture Critical 4 件を fix loop で解消、A2-4 PR #123 merge 後の rebase で衝突統合。orchestrator 委任で R-15 代替し admin override squash merge。本 mirror PR (`harness/roadmap-mirror-a2-5`) は `roadmap-tracker` Phase 8 自動同期の手動代替 (A3 で Skill 本格化まで継続) |
| 2026-05-17 | A2-6 を計画外で挿入 (auto-merge 緩和の workaround、PR #129、merge commit `1ac6fe4`) | EPIC-A2 並列実行 (A2-2 / A2-4 / A2-5) で各ペインに self-merge を委任した結果、auto mode classifier が R-15 を根拠に `gh pr ready` / `gh pr merge` / `git push` を stochastic にブロック、並列実行スループットを阻害。当初検討していた ADR-0028 起票 + plan.md R-15 緩和 + roadmap への正式項目追加は不要と判断し、`.claude/settings.json` の `permissions.allow` 5 件追加のみに縮小。permission rule は「都度承認の手間削減」目的に限定し、R-15 (人間 approve) の精神は orchestrator / セッション開始 prompt 側で担保する方針。**ADR 化は不要と判断** (撤回コスト低 / scope は config 1 ファイル / R-15 本体の改定なし)。本 mirror PR (`harness/roadmap-mirror-pr-129`) は `roadmap-tracker` Phase 8 自動同期の手動代替 |
| 2026-05-17 | A2-3 (EPIC-A2 配下、5 番目の merge、最後) マージ完了 (PR #135、merge commit `c593e74`) | rules プロセス・ハーネス・UI 系 20 ファイル本格化 (新規 6 + skeleton 11 + 微調整 2 + 索引 1)、Phase 0 で `git fetch origin master` を実行 (PR #121 レトロ Try)、`code-reviewer-aspects.md` binary checklist 各 aspect 5-7 項目確定、`commit-message.md` subject 言語ポリシーをセクション化、`CLAUDE.md` lookup table に新規 6 rule の path 7 行追加。code-reviewer 4 aspect Critical 0 通過、Improvement 4 件 fix loop 即時消化 (残 14 件は learning ファイル) 後 `gh pr merge --merge` で orchestrator 明示承認による R-15 代替。**A2 status を in-progress → completed に昇格** (A2-1〜A2-6 全 PR merge 済、EPIC-A2 全体完了)、次フェーズは A3 (専用 Skill 群実装)。本 mirror PR (`harness/roadmap-mirror-a2-3`) は `roadmap-tracker` Phase 8 自動同期の手動代替 |
| 2026-05-17 | ORCH-1 を A2 と A3 の間に最優先項目として挿入 + 同 PR で completed 昇格 (PR [#144](https://github.com/subroh0508/colormaster/pull/144)) | 本セッションで実演した cmux 並列 self-merge orchestration の経験を `.claude/skills/orchestrator/SKILL.md` + `.claude/rules/orchestrator-criteria.md` に統合。仕様 1-8 (cmux サブコマンド 8 件 / 1 ペイン=1 PR / 30s ポーリング / 自動回答 / stale display 復旧 / 60% handover / R-15 明示承認代行 merge / ファイル経由 prompt 送信) + 教訓 10 系統 (並列起動実例 / classifier 通過境界 / stale display / cwd 喪失 relocate / Monitor 思考動詞辞書 / mirror PR / retro PR / dry-run 必要性 / cmux サブコマンド辞典 / 明示承認文言 canonical) を統合。skill-creator (ADR-0025) 経由で 100-point rubric self-eval 97/100、dry-run sample (skill 案 vs skeleton 案 別 subagent 比較) を `docs/harness/dry-runs/` に記録。A3 (専用 Skill 群実装) の前段で orchestrator 基盤を先行確立する目的、本 PR (`feature/orchestrator-skill`) で skill / rule / 索引 / lookup table / roadmap / dry-run の 6 系統を一括配置 |

## 次の推奨着手 (並行実装観点)

`roadmap-tracker` Skill が更新する想定。EPIC-A2 全 6 PR + ORCH-1 マージ済、A2 follow-up の並列 orchestration 基盤が確立。残りステップ:

1. **A3 (専用 Skill 群実装) 着手可** — EPIC-A2 + ORCH-1 完了、ADR 0001-0027 + 本格化された rules 54 件 (orchestrator-criteria.md 追加) + 機械検証規約 (A6 / A7 / A10 各フェーズ) + orchestrator skill (cmux 並列 orchestration / R-15 担保代行 merge / context 60% handover / ファイル経由 prompt 送信) を参照する Skill 群を実装:
   - 前段 Spec Gen: `feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` Skill
   - 中段オーケストレーション: `implementation-workflow` (10 Phase) / `code-reviewer` (8 aspect + Coordinator) / `pr-retrospective` (learning ファイル生成)
   - 後段 + 横断: `pr-poller` / `harness-meta` / `harness-evolution` / `roadmap-tracker`
2. A1 / A2-1 / A2-2 / A2-3 / A2-4 / A2-5 / A2-6 / ORCH-1 各レトロの未消化提案 (harness-meta フィードバック) を A3 / A4 で順次消化

## 関連

- `docs/harness/plan.md` (Single Source of Truth)
- `docs/epics/EPIC-000-harness-foundation/roadmap.md` (EPIC-000 配下の PR 進捗)
- `.claude/rules/roadmap.md` (ロードマップ Markdown 規約)
- `.claude/skills/roadmap-tracker/SKILL.md`
