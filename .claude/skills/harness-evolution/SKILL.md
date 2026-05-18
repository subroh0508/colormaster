---
name: harness-evolution
description: |
  外部研究 / ベストプラクティス駆動の改善ループを担う Skill。手動起動のみで cron 不採用 (ADR 0026)。
  WebSearch / WebFetch + Context7 MCP でホワイトリスト外部情報源を取得し、既存ハーネス
  (`.claude/skills/` / `.claude/rules/` / `docs/`) と gap 分析を行い、
  `docs/harness/evolution-proposals/YYYY-MM-DD.md` を出力する。重要案は
  `example-skills:skill-creator` 経由で Skill scaffold / Plan / EPIC 起票し、人間 approve を必須とする。
status: active
phase: A3
last_updated: 2026-05-19
related_plan: docs/harness/plan.md §5.3 / §5.4.6 / R-29 / R-30 / R-31
related_rules:
  - .claude/rules/harness-evolution.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/mcp-usage.md
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/retrospective-format.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
  - .claude/rules/docs-structure.md
  - .claude/rules/template-language.md
related_adrs:
  - ADR-0024
  - ADR-0025
  - ADR-0026
  - ADR-0027
  - ADR-0028
---

# harness-evolution

> **5 行以内 summary**: 外部研究 / ベストプラクティス駆動の改善ループ Skill。手動起動のみで
> cron 不採用 (ADR 0026、Claude API コスト抑制)。WebSearch / WebFetch + Context7 MCP で
> ホワイトリスト外部情報源を取得し、既存ハーネスと gap 分析して
> `docs/harness/evolution-proposals/YYYY-MM-DD.md` を出力する。重要案は
> `example-skills:skill-creator` 経由で Skill scaffold / Plan / EPIC 起票し、人間 approve を必須化。
> 内部 KPT 駆動の `harness-meta` (R-31) と二系統補完で運用し、提案重複は harness-meta 側を優先する。

## 役割

- **外部研究駆動の改善提案**: Anthropic engineering blog / `anthropics/skills` / Claude Code docs / MCP spec / AI Coding Agent (Augment / HumanLayer / Cognition) / 学術 (arxiv) / 業界ブログ / ライブラリ docs 等のホワイトリスト外部情報源から最新ベストプラクティスを取得
- **手動起動のみ**: cron / ScheduleWakeup / CronCreate を採用しない (ADR 0026、Claude API コスト抑制)。ユーザーが必要時に Claude Code から `Skill skill="harness-evolution" args="<focus area>"` で呼び出す
- **gap 分析**: 既存の `.claude/skills/` / `.claude/rules/` / `docs/` と外部知見を突き合わせ、新規 Skill / 既存 Skill 改修 / 新規 rule / 既存 rule 強化 / 廃止候補 / 新 MCP 採用余地を構造化リスト化
- **Context7 MCP 引用検証 (R-28)**: 取得した外部知見のうちライブラリ API / バージョン情報は Context7 MCP で 1 次資料を引き直し、古い API / 存在しない API / hallucination 起源の提案を抑止
- **提案起票 (人間 approve 必須)**: 重要案は `example-skills:skill-creator` 経由で Skill scaffold、または `plan-author` / `epic-author` 経由で Plan / EPIC 起票。本 Skill は **起票 trigger までを担い、merge は人間 approve に委ねる**
- **harness-meta との重複防止 (R-31)**: 既に `docs/harness/learnings/*.md` の `🤖 ハーネス改善提案` で指摘済の提案は **本 Skill 側を見送り**、内部実体験ベースの harness-meta を優先

`harness-meta` (内部 KPT 駆動) と本 Skill (外部研究駆動) は二系統補完で動作し (ADR 0026)、責務 overlap は R-31 のラベル分離 (`harness-meta` vs `harness-evolution`) + 重複判定で回避する。

## 入力

- **起動 prompt** (人間からの手動起動): focus topic 文字列 (`Skill (1)` 中の `args`)、例: `"Skill 設計のベストプラクティス"` / `"MCP server 採用余地"` / `"general"` (focus 指定なし)
- **既存ハーネス資産** (現状把握):
  - `.claude/skills/*/SKILL.md` 全件 (各 Skill の責務 / 起動契機 / 出力)
  - `.claude/rules/*.md` 全件 (rule 索引は `.claude/rules/rules-index.md`)
  - `docs/harness/plan.md` (Single Source of Truth)
  - `docs/harness/learnings/*.md` (直近 N 件、重複判定用)
- **外部情報源** (ホワイトリスト、`.claude/rules/harness-evolution.md` §外部情報源ホワイトリスト):
  - Anthropic 公式: Anthropic engineering blog / `anthropics/skills` GitHub / Claude Code docs (WebFetch + WebSearch)
  - MCP 仕様: <https://modelcontextprotocol.io> (WebFetch + Context7 MCP)
  - AI Coding Agent: awesome-harness-engineering / Augment Code / HumanLayer / Cognition (Devin) blog (WebSearch + WebFetch)
  - 学術: arxiv の AI agents / autonomous coding 系 (WebSearch + WebFetch)
  - 業界ブログ: Martin Fowler / Red Hat Developer / GitHub blog (WebFetch)
  - ライブラリ docs: Kotlin / Compose MP / Ktor / SQLDelight / Roborazzi 等のバージョン固有 API (**Context7 MCP** で引用検証、R-28)
- **MCP 結果**:
  - Context7 MCP: ライブラリ API の 1 次資料引用検証
  - JetBrains MCP: 既存実装の対象モジュール index 検索 (該当時)

## 出力

- **`docs/harness/evolution-proposals/YYYY-MM-DD.md`** (常に生成): フォーマットは `.claude/rules/harness-evolution.md` §出力フォーマット を Single Source of Truth とする。frontmatter `sources` (URL / title / accessed_at) は block 形式、改善提案プレフィックスは `[skill]` / `[rule]` / `[remove]` / `[mcp]` の 4 種
- **Skill scaffold (重要案のみ)**: `example-skills:skill-creator` を呼び出して `.claude/skills/<new-skill>/SKILL.md` の draft を生成。本 Skill は起票 trigger のみ担い、merge は人間 approve に委ねる
- **Plan / EPIC 起票 (重要案のみ)**: 単一 PR スコープなら `plan-author` 経由で `docs/plans/PLAN-NNN-<slug>.md`、複数 PR スコープなら `epic-author` 経由で `docs/epics/EPIC-NNN-<slug>/` を起票
- **Side effect**: `.claude/rules/harness-evolution.md` §採用提案 表に Plan / EPIC リンクを追記し、proposal の `status` を `draft` → `actioned` に更新 (人間 approve 後)
- **本 Skill は merge を実行しない** (R-15 + 人間 approve 必須、ADR 0026)

## フェーズ別動作

### Phase 1: focus topic 把握 + 既存ハーネス現状把握

- 起動 prompt から focus topic を抽出 (`general` 指定時は全領域を等しく扱う)
- `.claude/skills/*/SKILL.md` / `.claude/rules/*.md` / `docs/harness/plan.md` を Read して既存資産を整理
- 直近 N 件 (推奨 10 件) の `docs/harness/learnings/*.md` から `🤖 ハーネス改善提案` セクションを抽出し、harness-meta で既出の提案リストを構築 (Phase 3 / 4 の重複判定で使用)
- 出力: 既存ハーネス snapshot (Skill 一覧 / rule 一覧 / 直近 learning 提案リスト)

### Phase 2: ホワイトリスト外部情報源の取得 + Context7 MCP 引用検証

- `.claude/rules/harness-evolution.md` §外部情報源ホワイトリスト の表に沿って WebSearch / WebFetch で取得
- **ホワイトリスト外 URL を取得しない** (Plan 起票 + 人間 approve なしでの追加禁止、§ホワイトリスト追加手順)
- ライブラリ API / バージョン情報が含まれる場合は **必ず Context7 MCP で 1 次資料を引き直す** (R-28)。Context7 MCP が該当 API を返さない場合はその提案を見送り、proposal に「Context7 引けず見送り」と明記
- 各情報源について URL / title / accessed_at を記録 (frontmatter `sources` 構築用)
- 出力: 構造化された外部知見テーブル (出典 / 引用日 / 主旨 / Context7 検証結果)

### Phase 3: gap 分析

- Phase 1 の既存ハーネス snapshot と Phase 2 の外部知見テーブルを突き合わせ
- 既存 Skill / rule / docs と外部ベストプラクティスの **差分を構造化リスト化** (新規追加 / 既存改修 / 廃止候補 / 新 MCP 採用余地)
- 各 gap について **重大度 (高 / 中 / 低)** を付与 (高 = 機能欠落 / 中 = 改善余地 / 低 = nice-to-have)
- **重複判定**: Phase 1 で抽出した harness-meta 既出提案リストと突き合わせ、重複時は本 Skill 側を見送り (R-31)、proposal に「harness-meta で既出のため見送り、出典のみ記録」と明記

### Phase 4: 提案起票 (`docs/harness/evolution-proposals/YYYY-MM-DD.md`)

- ファイルパス: `docs/harness/evolution-proposals/YYYY-MM-DD.md` (`YYYY-MM-DD` は生成日)
- フォーマット: `.claude/rules/harness-evolution.md` §出力フォーマット を Single Source of Truth とする
- 必須セクション: 概要 (5 行以内) / 取得した外部知見 (表) / 既存ハーネスとの gap 分析 (表) / 改善提案 (構造化リスト) / 採用提案 (重要案を Plan / EPIC 起票)
- 改善提案プレフィックスは **`[skill]` / `[rule]` / `[remove]` / `[mcp]`** の 4 種 (`[mcp]` は本 Skill 固有、新規 MCP 採用余地検討用)
- frontmatter `sources` は block 形式必須 (`docs-structure.md` 規約、URL / title / accessed_at の 3 キー)
- 出力前に **PII / Secrets redaction を必ず通す** (`pii.md` / `secrets.md` §redaction 強制、R-26)。外部情報源の本文にメール / token 等が混入する可能性あり

### Phase 5: 重要案の Skill / Plan / EPIC 起票 trigger + 3 軸定量評価 PR description 転載 (人間 approve 待ち、ADR-0028 で 3 軸定量化)

- Phase 4 の `改善提案` のうち重大度「高」を中心に **重要案を抽出**
- 新規 Skill 提案 → `example-skills:skill-creator` を呼び出して `.claude/skills/<new-skill>/SKILL.md` の draft を生成 (`.claude/rules/skill-authoring.md` 100-point rubric 準拠)
- Plan / EPIC 起票判定 (`plan.md` §Epic 昇格条件、`>=2 PR` 想定なら EPIC、単一 PR で完結なら Plan):
  - 単一 PR スコープ → `plan-author` を呼び出して `docs/plans/PLAN-NNN-<slug>.md` 起票
  - 複数 PR スコープ → `epic-author` を呼び出して `docs/epics/EPIC-NNN-<slug>/` 起票
- 起票後は `docs/harness/evolution-proposals/YYYY-MM-DD.md` の `## 採用提案` 表に Plan / EPIC リンクを追記、proposal の status は **`draft` のまま**。人間 approve + merge 後に **別 PR で `actioned` に更新**
- **3 軸定量評価 + 入力記録要約の PR description 転載手順** (ADR-0028 §決定 5、`.claude/rules/harness-evolution.md` §3 軸定量評価 §Plan / EPIC 起票時の転載手順):
  1. **Plan / Epic 本体側 (`docs/plans/` / `docs/epics/`) に dry-run ファイルへのリンクを必須記載** (本 Skill 経由起票時の dry-run ファイル `docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md` への相対パスリンク)
  2. **後続実装 PR description 転載** (実装着手は `implementation-workflow` Skill が担う): `.github/PULL_REQUEST_TEMPLATE/harness.md` §3 軸定量評価 セクションに **3 軸スコア表 + dry-run 入力記録の要約 + dry-run ファイル詳細リンク + 9 通り組合せ別レビュー指針 #N 該当の推奨アクション** を必須転載
  3. **入力記録 4 ブロック (harness-evolution 固有)** (`.claude/rules/harness-evolution.md` §harness-evolution 固有の dry-run 入力記録仕様):
     - ブロック 1: focus topic 文字列を args として記録
     - ブロック 2: 外部情報源 URL 一覧 + Context7 MCP 引用検証 (R-28) の対象 API / バージョン を user prompt 内に明示
     - ブロック 4: 外部情報源 WebFetch の accessed_at 日付を frontmatter `sources` と整合させる (出典記録の必須要件、R-29)
- **本 Skill は `gh pr merge` を実行しない** (R-15 + 人間 approve 必須、auto-merge 禁止)

## Gotchas

- **手動起動のみ (cron 不採用)**: cron / ScheduleWakeup / CronCreate / 自動 wakeup 系を本 Skill から起動しない。ADR 0026 は Claude API コスト抑制を目的に明示的に「手動起動のみ」と規定。月次相当の頻度を推奨 (`anthropics/skills` の更新確認を兼ねる、R-30)
- **ホワイトリスト外 URL の取得禁止**: WebFetch / WebSearch のクエリは `.claude/rules/harness-evolution.md` §外部情報源ホワイトリスト の表に列挙された情報源に限定。ホワイトリスト追加には Plan 起票 + orchestrator (subroh0508) approve が必要 (§ホワイトリスト追加手順)
- **Context7 MCP 引用検証を必ず通す (R-28)**: ライブラリ API / バージョン情報を含む提案は 1 次資料引用検証必須。Context7 MCP が該当 API を返さない場合は **その提案を見送る** (古い API / 存在しない API / hallucination 起源の提案を抑止)。検証失敗は proposal に「Context7 引けず見送り」と明記
- **harness-meta との提案重複防止 (R-31)**: 直近 N 件 (推奨 10 件) の `docs/harness/learnings/*.md` の `🤖 ハーネス改善提案` を Read し、重複時は本 Skill 側を見送り (内部実体験ベースの harness-meta を優先)。重複時は proposal に「harness-meta で既出のため見送り、出典のみ記録」と明記
- **自動起動禁止**: 本 Skill は ScheduleWakeup / CronCreate / `cmux new-workspace --start-command` 等の自動起動経路から呼ばない。ユーザーが Claude Code から `Skill skill="harness-evolution"` で明示的に呼び出した場合のみ動作
- **提案の独立性**: 本 Skill が起票する Plan / EPIC は `harness-meta` 起票分と **ラベル分離** (`harness-evolution` vs `harness-meta`、R-31)、PR description / commit message にも `[harness-evolution]` 接頭辞を付与して識別可能化を推奨
- **PII / Secrets redaction を必ず通す (R-26)**: 外部情報源 (blog / arxiv / GitHub Issues 等) の本文にメール / display name / token / API key が混入する可能性あり。proposal 出力前に `pii.md` / `secrets.md` §redaction 強制 のパターンで必ずマスク
- **5 行 summary は `# 概要 (5 行以内)` 直下に記述**: 本 Skill が生成する `docs/harness/evolution-proposals/YYYY-MM-DD.md` は冒頭 blockquote (`>`) ではなく `# 概要 (5 行以内)` セクション直下に概要を書く (`.claude/rules/harness-evolution.md` §出力フォーマット 固有)
- **新規 MCP 採用検討は `[mcp]` プレフィックス**: 既存 3 MCP (JetBrains / Context7 / Cloudflare、ADR 0024) との overlap / コスト / セキュリティ境界を判断材料に明記。新規 MCP 採用は本 Skill 単独で決定せず、ADR 起票 (`adr-author`) + 人間 approve 経由
- **`MUST` / `ALWAYS` / `NEVER` の多用回避**: `.claude/rules/skill-authoring.md` §禁止表現 に従い、本 SKILL.md / 生成する proposal でも「~するべき」「~を推奨」「~してはいけない (理由付き)」表記を優先

## 関連

- ADR 0024 (MCP 採用、Context7 MCP / WebFetch / WebSearch の使い分け)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- ADR 0026 (`harness-evolution` Skill 採用、二系統補完設計の Single Source of Truth)
- ADR 0027 (docs 構造 + 命名規約 + 日本語化方針)
- `docs/harness/plan.md` §5.3 (Skill 一覧) / §5.4.6 (Meta フェーズ二系統) / R-29 / R-30 / R-31
- `.claude/rules/harness-evolution.md` (外部情報源ホワイトリスト + 出力フォーマット + Context7 引用検証 の Single Source of Truth)
- `.claude/rules/skill-authoring.md` (Skill 作成 100-point rubric、`example-skills:skill-creator` 経由)
- `.claude/rules/mcp-usage.md` (Context7 MCP / JetBrains MCP / Cloudflare MCP の使い分け)
- `.claude/rules/harness-meta-criteria.md` (内部 KPT 駆動 harness-meta との責務分離、ラベル分離)
- `.claude/rules/retrospective-format.md` (learning ファイル `🤖 ハーネス改善提案` セクションの構造、重複判定の入力)
- `.claude/rules/{pii,secrets,docs-structure,template-language}.md` (redaction / frontmatter block 形式 / 日本語見出し)
- `.claude/skills/harness-meta/SKILL.md` (内部 KPT 駆動の対比、A3-5 で並走実装)
- `.claude/skills/orchestrator/SKILL.md` (Skill フォーマット参考、§プロンプト送信プロトコル仕様 8)
- `example-skills:skill-creator` (新規 Skill scaffold の起票先)
- `docs/harness/evolution-proposals/` (proposal 出力先ディレクトリ)
