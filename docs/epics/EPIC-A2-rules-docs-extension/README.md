---
id: EPIC-A2
title: .claude/rules/* 全ファイル本格化 + docs 全面拡充
status: in-progress
created_at: 2026-05-17
completed_at: null
expected_modules:
  - .claude/rules/**
  - docs/architecture/**
  - docs/api/**
  - docs/security/**
  - docs/requirements/**
  - docs/specifications/**
  - docs/runbooks/**
  - docs/README.md
  - docs/glossary.md
  - docs/codebase-map.md
  - docs/adr/README.md
  - docs/harness/learnings/flaky-tests.md
  - .github/PULL_REQUEST_TEMPLATE/harness.md
  - .claude/skills/code-reviewer/SKILL.md
  - CLAUDE.md
related_adrs:
  - ADR-0001
  - ADR-0017
  - ADR-0020
  - ADR-0021
  - ADR-0024
  - ADR-0025
  - ADR-0027
related_specs: []
---

# EPIC-A2: `.claude/rules/*` 全ファイル本格化 + `docs/` 全面拡充

> **5 行以内 summary**: B0 で配置した rules / docs の skeleton を本格化し、AI が
> 自律実装に必要な情報を `docs/` に体系化する Epic。A1 (ADR 0001-0027) と並行可能で、
> A3 (専用 Skill 群実装) の前提となる規約・docs を整える。5 PR (A2-1 〜 A2-5) に
> 分割。完了時点で `.claude/rules/` 全規約と `docs/{architecture,api,security,
> requirements,specifications,runbooks}/**` の主要本文が揃った状態を達成。

## 目的

- `docs/harness/plan.md` §6.2 A2 の完了条件達成
- `.claude/rules/*` の全 rule ファイルを「実体あり + 本格的な本文」状態にし、CLAUDE.md / rules-index.md の宣言と実体を一致させる (A1 レトロ Problem #1 / R-32 関連)
- `docs/{architecture,api,security,requirements,specifications,runbooks}/**` を AI が自律実装に必要な粒度まで拡充 (ADR 0027)
- A1 レトロ 15 件のハーネス改善提案のうち A2 スコープ該当分 (rules / template / docs/adr/README / learnings/flaky-tests など) を消化

## 背景

- B0 (PR #117) で rules 19 ファイル + docs 各サブツリーの skeleton を配置
- B0 マージ後の A1 レトロ (PR #117) で **「既存」誤宣言 13+ 件** / **template-language.md の paths 矛盾** / **code-reviewer/SKILL.md PII redaction 欠落** / **PR テンプレ harness.md 欠落** など 15 件の改修提案が起票
- A1 (PR #119) で ADR 0001-0027 を一括起草済 → 各 rule / docs が ADR を参照できる状態
- 本 Epic で rules / docs を本格化し、A3 以降の Skill 本格実装で「規約・docs 参照」が成立する基盤を確立

## スコープ

### 含む

- A1 レトロ提案のうち A2 スコープ該当分 (A2-1 で消化)
- `.claude/rules/*` の全 rule (新規 ~36 件 + skeleton 本格化 ~19 件 = 計 ~55 件) (A2-2 / A2-3)
- `docs/{architecture,api,security,requirements,specifications,runbooks}/**` の本格化 (A2-4 / A2-5)
- `docs/{README,glossary,codebase-map}.md` 拡充 (A2-4)
- `docs/adr/README.md` の ADR 0001-0027 索引化 (A2-1)
- `docs/harness/learnings/flaky-tests.md` 新規追加 (A2-1)
- `.github/PULL_REQUEST_TEMPLATE/harness.md` 新規追加 (A2-1)
- `.claude/skills/code-reviewer/SKILL.md` の Gotchas に PII redaction / visual-regression aspect 有効化手順を追記 (A2-1)
- `CLAUDE.md` lookup table の status ラベル正規化 (A2-1)

### 含まない (後続フェーズに持ち越し)

- 専用 Skill 群実装 (A3) — feature-request / bug-fix / refactor / dependency-upgrade / adr-author / harness-meta の本格実装
- A1 レトロ提案のうち Skill 本格化系 (pr-retrospective / pr-poller / code-reviewer 8 aspect 完全実装等) → A3 で消化
- Lint / Format 基盤 (A6) — `.claude/rules/markdown.md` の本格化はするが、markdownlint-cli2 統合は A6
- 三層テスト品質基盤 (A7) — `.claude/rules/{coverage-100,spec-traceability,mutation-testing}.md` は本 Epic で起草、Gradle 設定は A7
- 実装コード変更 — `core/**` / `feature/**` / `build.gradle.kts` 等は触らない (本 Epic は rules / docs のみ)
- DESIGN.md / UI Inventory 実体 (A10) — rules (`design-tokens` / `ui-snapshot` / `ui-inventory` / `behavior-preservation`) は本 Epic で本格化、実体生成は A10

## 構成 PR (進捗)

| PR # | タイトル | status | merge 日 | 主要ファイル |
|---|---|---|---|---|
| (A2-1) | feat(harness): A2-1 A1 レトロ即時消化 + ハーネス即時改善 | in-progress | — | EPIC-A2 起票 + `CLAUDE.md` / rules-index 正規化 / `template-language` / `mcp-usage` / `db-protection` / `commit-message` (新規) / `code-reviewer/SKILL.md` / harness.md PR テンプレ / `docs/adr/README.md` / `docs/harness/learnings/flaky-tests.md` |
| (A2-2) | feat(harness): A2-2 rules 実装・コード系本格化 | proposed | — | `.claude/rules/{plan,epic,adr,roadmap,viewmodel,ui-state,composable,navigation,repository,network-client,naming,error-handling,logging,i18n,wasm-compat,firebase-boundary,no-firebase,gradle,kotlin-test,screenshot-test,sql-delight,sparql,test-paired-class,markdown,pii,secrets,db-protection,sync-job,sqlite-data-file,cloud-run-deploy,removed-modules,backend-auth,cloudflare-pages,r2-litestream,rules-index}.md` |
| (A2-3) | feat(harness): A2-3 rules プロセス・ハーネス・UI系本格化 | proposed | — | `.claude/rules/{pr-template,branch-naming,merge-readiness,pr-draft-policy,spec-living-sync,harness-meta-criteria,retrospective-format,pr-poller,skill-authoring,harness-evolution,implementation-workflow,code-reviewer-aspects,design-tokens,ui-snapshot,ui-inventory,behavior-preservation,docs-structure,template-language,commit-message,rules-index}.md` |
| (A2-4) | docs(harness): A2-4 docs/ コア + runbooks 拡充 | proposed | — | `docs/{README,glossary,codebase-map}.md`, `docs/security/README.md`, `docs/requirements/{README,template}.md`, `docs/specifications/{README,basic/template,detail/template}.md`, `docs/runbooks/{local-development,testing,i18n,mcp-setup}.md` |
| (A2-5) | docs(harness): A2-5 docs/architecture + api 拡充 | proposed | — | `docs/architecture/{overview,layers,data-flow,domain-model,state-machines,sequences,infrastructure}.md`, `docs/api/{README,colormaster-api.yaml,auth,idols,me}.md` |

## 受け入れ基準 (Epic 全体)

- [ ] A2-1〜A2-5 の全 PR がマージ済み、各 PR 番号 + マージ日が `roadmap.md` の完了根拠に記録
- [ ] `.claude/rules/rules-index.md` の status ラベルと実体が一致 (「既存」誤宣言ゼロ)
- [ ] `.claude/rules/*.md` が全 rule で `paths` フィールドを意図して設定済 (常時ロード群 4 件 + path-scoped 残り)
- [ ] 各 rule ファイルが「責務 + 規約本文 + Gotchas + 関連」の構造を満たす (skeleton 状態ゼロ)
- [ ] `docs/{architecture,api,runbooks}/**` の各ファイルが冒頭 5 行 summary + 本文 5KB+ を満たす
- [ ] `docs/requirements/template.md` / `docs/specifications/{basic,detail}/template.md` が §4.6 の章立てを反映
- [ ] `docs/adr/README.md` に ADR 0001-0027 索引 (タイトル + 関連 rule + 起票根拠) が記載
- [ ] `.github/PULL_REQUEST_TEMPLATE/harness.md` が配置され `branch-naming` の `harness/<purpose>` 認定と整合
- [ ] A1 レトロ提案 15 件のうち本 Epic スコープ該当分が消化済 (`docs/harness/learnings/2026-05-17-pr-117.md` の `📝 harness-meta フィードバック` に採用結果を追記)

## 関連

- `docs/harness/plan.md` §6.2 A2
- `docs/harness/roadmap.md` (全体ロードマップ)
- `docs/harness/learnings/2026-05-17-pr-117.md` (A1 レトロ 15 提案の出典)
- `roadmap.md` (本 Epic 配下のロードマップ)
- `open-questions.md`
- `decisions.md`
- `progress.md`
- 関連 ADR: ADR-0017 (ローカルポーリング駆動) / ADR-0024 (MCP 採用) / ADR-0025 (Skill 作成) / ADR-0027 (docs 構造 + 日本語化)
- 関連 Phase: §6.2 A2 (本 Epic) / §6.2 A3 (専用 Skill 群実装、本 Epic 完了が前提)
