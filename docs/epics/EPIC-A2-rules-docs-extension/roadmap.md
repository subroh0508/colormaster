---
id: roadmap-EPIC-A2
title: EPIC-A2 ロードマップ
status: living
last_updated: 2026-05-17
source_epic: EPIC-A2
---

# EPIC-A2 ロードマップ

> **5 行以内 summary**: EPIC-A2 (`.claude/rules/*` 全ファイル本格化 + docs 全面拡充)
> 配下の PR 進捗トラッカー。A2-1 〜 A2-5 の 5 PR を対象とし、Plan 単体は列挙しない
> (R-34)。`roadmap-tracker` Skill が自動更新する。`docs/harness/roadmap.md` の A2 行と
> 整合する。Open Questions / 障壁 / 着手順変更履歴は append-only。

## 項目一覧

| ID | タイトル | status | expected_modules | 完了根拠 |
|---|---|---|---|---|
| **A2-1** | A1 レトロ即時消化 + ハーネス即時改善 | completed | EPIC-A2 起票 + `CLAUDE.md` / `.claude/rules/{rules-index,template-language,mcp-usage,db-protection,commit-message,roadmap}.md` / `.claude/skills/code-reviewer/SKILL.md` / `.github/PULL_REQUEST_TEMPLATE/{harness,feature,bugfix}.md` / `docs/adr/README.md` / `docs/harness/{plan,learnings/flaky-tests,learnings/INDEX,learnings/2026-05-17-pr-117,roadmap}.md` / `scripts/install-git-hooks.sh` | PR [#121](https://github.com/subroh0508/colormaster/pull/121) (2026-05-17 マージ、commit `feb41b5`) |
| **A2-2** | rules 実装・コード系本格化 | in-progress | `.claude/rules/{plan,epic,adr,roadmap,viewmodel,ui-state,composable,navigation,repository,network-client,naming,error-handling,logging,i18n,wasm-compat,firebase-boundary,no-firebase,gradle,kotlin-test,screenshot-test,sql-delight,sparql,test-paired-class,markdown,pii,secrets,db-protection,sync-job,sqlite-data-file,cloud-run-deploy,removed-modules,backend-auth,cloudflare-pages,r2-litestream,rules-index}.md` | (本 PR #125 で更新) |
| **A2-3** | rules プロセス・ハーネス・UI系本格化 | proposed | `.claude/rules/{pr-template,branch-naming,merge-readiness,pr-draft-policy,spec-living-sync,harness-meta-criteria,retrospective-format,pr-poller,skill-authoring,harness-evolution,implementation-workflow,code-reviewer-aspects,design-tokens,ui-snapshot,ui-inventory,behavior-preservation,docs-structure,template-language,rules-index}.md` | — |
| **A2-4** | docs/ コア + runbooks 拡充 | proposed | `docs/{README,glossary,codebase-map}.md`, `docs/security/README.md`, `docs/requirements/{README,template}.md`, `docs/specifications/{README,basic/template,detail/template}.md`, `docs/runbooks/{local-development,testing,i18n,mcp-setup}.md` | — |
| **A2-5** | docs/architecture + api 拡充 | completed | `docs/architecture/{overview,layers,data-flow,domain-model,state-machines,sequences,infrastructure}.md`, `docs/api/{README,colormaster-api.yaml,auth,idols,me}.md` | PR [#126](https://github.com/subroh0508/colormaster/pull/126) (2026-05-17 マージ、commit `168ef5d`) |

## 完了根拠

| ID | PR 番号 | マージ日 | 主要ファイル |
|---|---|---|---|
| A2-1 | [#121](https://github.com/subroh0508/colormaster/pull/121) | 2026-05-17 | EPIC-A2 5 ファイル起票 (`docs/epics/EPIC-A2-rules-docs-extension/{README,roadmap,open-questions,decisions,progress}.md`)、`docs/epics/INDEX.md` 更新、`.claude/rules/{rules-index,template-language,mcp-usage,db-protection,commit-message (新規),roadmap}.md`、`.claude/skills/code-reviewer/SKILL.md` Gotchas、`.github/PULL_REQUEST_TEMPLATE/{harness (新規),feature,bugfix}.md`、`CLAUDE.md` lookup table 注記、`docs/adr/README.md` 索引拡充、`docs/harness/learnings/{flaky-tests.md (新規),2026-05-17-pr-117.md,INDEX.md}`、`docs/harness/{plan,roadmap}.md` (commit `feb41b5`、squash merge、code-reviewer 4 aspect 並列 review 通過 + fix loop で Critical 1 解消) |
| A2-5 | [#126](https://github.com/subroh0508/colormaster/pull/126) | 2026-05-17 | `docs/architecture/{overview,layers,data-flow,domain-model,state-machines,sequences,infrastructure}.md` (7) + `docs/api/{README,colormaster-api.yaml,auth,idols,me}.md` (5) を B0 skeleton (1.3-2.5KB) から 5KB+ 本格化 (合計 +2,005 行)。Mermaid 図 (graph TD/LR / flowchart TB / erDiagram / stateDiagram-v2 / sequenceDiagram x5) を全 7 architecture docs + sequences 5 ユースケースで描画。`colormaster-api.yaml` paths を 11 endpoint に拡張、`components/schemas` で Idol/Brand/ColorPalette/Profile/Favorite/Health/Error を定義。Option A (ADR/plan 駆動執筆) 方針。`docs/epics/EPIC-A2-rules-docs-extension/{roadmap,progress}.md` 更新。commit `168ef5d` (squash merge、admin override / orchestrator 委任で R-15 代替)。code-reviewer 4 aspect (spec-conformance / architecture / security / code-quality) 並列 review 通過 + fix loop (commit `de2b1f8` → rebase で `642c46e`) で architecture Critical 4 件 (DIP 違反方向 / `core/database` 未定義 / restore 主体誤り / overview Mermaid 非対称) 解消 |

## 着手順とブロック関係

```mermaid
gantt
    title EPIC-A2 着手順
    dateFormat YYYY-MM-DD
    section A2
    A2-1 :a21, 2026-05-17, 3d
    A2-2 :a22, after a21, 5d
    A2-3 :a23, after a22, 5d
    A2-4 :a24, after a21, 5d
    A2-5 :a25, after a24, 5d
```

A2-1 完了後、A2-2/A2-3 (rules 系) と A2-4/A2-5 (docs 系) は **並走可** (`.claude/rules/`
と `docs/` で touch ファイル分離)。A2-2 と A2-3 は `rules-index.md` の連続編集になるため
直列。A2-4 と A2-5 は `docs/` 内の異なるサブツリーで並走可。

## 保留中の意思決定・不明事項 (Open Questions)

| 起票日 | 内容 | 暫定方針 | 解決状態 |
|---|---|---|---|

## 技術的障壁と回避策 (Blockers and Workarounds)

| 起票日 | 障壁 | 回避策 | 解決日 | 解決方法 |
|---|---|---|---|---|

## 着手順変更履歴 (append-only)

| 日付 | 変更内容 | 理由 |
|---|---|---|
| 2026-05-17 | EPIC-A2 起票 + A2 を 5 PR に分割 | B0 (96 files / +5408 行) のレビュー負荷が上限近く、A2 全体は B0 を超える規模が想定されるため。A1 レトロ Try「巨大 PR の aspect 並列 review における入力分割」と整合 |
| 2026-05-17 | A2-1 着手 (現 worktree `feature/A2-rules-docs-extension` を reuse) | A1 レトロ 15 提案のうち消化可能項目を最優先で消化、後続 PR の規約・索引基盤を整える |
| 2026-05-17 | A2-1 status を in-progress → completed (PR #121 マージ、commit `feb41b5`) | A1 レトロ 15 提案中 11 件を消化、後続 A2-2 / A2-4 並走着手の前提が整う。本 PR は `roadmap-tracker` Phase 8 自動同期の手動代替 (A3 で Skill 本格化まで継続) |
| 2026-05-17 | A2-1 PR #121 マージ後、A2-2 / A2-4 / A2-5 を並走着手 (本 PR は A2-2、新規 worktree `feature/A2-2-rules-impl`) | A2-2 と A2-4 / A2-5 は touch ファイル重複ゼロ (`.claude/rules/` vs `docs/`)、A2-3 は rules-index.md 連続編集回避のため A2-2 マージ後に着手 |
| 2026-05-17 | A2-5 着手 (worktree `feature/A2-5-docs-arch-api`) | A2-1 マージ後、A2-2 / A2-4 と並行で docs/architecture + api サブツリーを 12 ファイル 5KB+ に拡充 (touch ファイル重複ゼロ) |
| 2026-05-17 | A2-5 status を in-progress → completed (PR #126 マージ、commit `168ef5d`) | docs/architecture 7 + docs/api 5 を 5KB+ 本格化、code-reviewer architecture Critical 4 件 fix loop で解消、A2-4 PR #123 merge 後の rebase で `progress.md` / `roadmap.md` 衝突を統合解決。本 PR は `roadmap-tracker` Phase 8 自動同期の手動代替 (A3 で Skill 本格化まで継続) |

## 次の推奨着手 (並行実装観点)

A2-1 / A2-4 / A2-5 マージ済 (PR #121 / #123 / #126)。残りステップ:

1. **A2-2 (rules 実装・コード系本格化)** — PR #125 進行中 (`feature/A2-2-rules-impl`)。Ready 昇格 + code-reviewer → merge 中
2. **A2-3 (rules プロセス・ハーネス・UI 系本格化)** — A2-2 完了後着手 (`.claude/rules/rules-index.md` の連続編集回避のため)
3. EPIC-A2 完了は A2-2 / A2-3 マージ後 (4/5 → 5/5 で `completed` 昇格、別 mirror PR で記録)
4. A3 (専用 Skill 群実装) 着手は EPIC-A2 完了後 (ADR + 本格化された rules を参照する Skill 群実装のため)

## 関連

- `docs/epics/EPIC-A2-rules-docs-extension/README.md`
- `docs/harness/roadmap.md` (全体ロードマップ、A2 行)
- `docs/harness/plan.md` §6.2 A2
- `.claude/rules/roadmap.md`
