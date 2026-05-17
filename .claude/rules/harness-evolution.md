---
id: rules-harness-evolution
title: harness-evolution Skill 運用規約 (外部情報源ホワイトリスト + 手動起動)
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/harness-evolution/**"
  - "docs/harness/evolution-proposals/**"
related_adrs:
  - ADR-0026
related_plan: docs/harness/plan.md §5.3 / §5.4.6 / R-29 / R-30 / R-31
---

# harness-evolution.md — harness-evolution 運用規約

> 外部研究・ベストプラクティス駆動の改善ループを **手動起動のみ** で運用する規約。
> 内部 KPT 駆動の `harness-meta` と二系統補完で動作 (ADR 0026)。
> 出典 URL + 引用日付の記録、Context7 MCP 引用検証、harness-meta との重複回避を明文化。

## 起動方式

- **手動起動のみ** (Claude API コスト抑制のため cron 不採用、ADR 0026)
- ユーザーが必要時に Claude Code で `harness-evolution` を呼び出す
- 月次相当の頻度を推奨 (`anthropics/skills` の更新確認を兼ねる、R-30)
- 起動コマンド: `Skill skill="harness-evolution" args="<focus area or 'general'>"`

## 外部情報源ホワイトリスト

| カテゴリ | 情報源 | アクセス手段 |
|---|---|---|
| Anthropic 公式 | Anthropic engineering blog / `anthropics/skills` GitHub / Claude Code docs | WebFetch + WebSearch |
| MCP 仕様 | MCP spec (https://modelcontextprotocol.io) | WebFetch + Context7 MCP |
| AI Coding Agent | awesome-harness-engineering / Augment Code / HumanLayer / Cognition (Devin) blog | WebSearch + WebFetch |
| 学術 | arxiv (AI agents / autonomous coding 系) | WebSearch + WebFetch |
| 業界ブログ | Martin Fowler / Red Hat Developer / GitHub blog | WebFetch |
| ライブラリ docs | Kotlin / Compose MP / Ktor / SQLDelight / Roborazzi 等のバージョン固有 API | **Context7 MCP** (R-28) |

ホワイトリスト追加は Plan 起票 + 人間 approve 必須 (orchestrator subroh0508 判断)。

## ホワイトリスト追加手順

1. 新規情報源候補を `docs/plans/PLAN-NNN-<slug>.md` で起票 (`type: harness`)
2. 採用根拠: 信頼性 / 更新頻度 / 引用可能性 / ライセンス
3. orchestrator approve → Plan completed → 本 rule のホワイトリスト表に行追加
4. `harness-evolution` Skill の system prompt にも反映 (`.claude/skills/harness-evolution/SKILL.md` 改修)

## 出力フォーマット

```text
docs/harness/evolution-proposals/YYYY-MM-DD.md
```

```markdown
---
id: evolution-YYYY-MM-DD
title: <要約タイトル>
type: evolution-proposal
status: draft | reviewed | actioned
generator: harness-evolution Skill (vX.Y.Z)
generated_at: YYYY-MM-DDTHH:MM:SSZ
sources:
  - url: https://...
    title: ...
    accessed_at: YYYY-MM-DD
  - url: https://...
    title: ...
    accessed_at: YYYY-MM-DD
---

# 概要 (5 行以内)

## 取得した外部知見

| 出典 | 引用日 | 主旨 |
|---|---|---|

## 既存ハーネスとの gap 分析

| 既存 | 提案改善 | gap の重大度 (高/中/低) |
|---|---|---|

## 改善提案 (構造化リスト)

- [skill] 新規 Skill `<name>` の追加 — 目的 / 入力 / 出力
- [skill] 既存 Skill `<name>` の改修 — Before / After / 検証方法
- [rule] 新規 rule `<name>` の追加
- [rule] 既存 rule `<name>` の強化
- [remove] Skill / rule の廃止候補 — 理由
- [mcp] 新規 MCP 採用余地 — 候補 / 採用判断材料

## 採用提案 (重要案を Plan / EPIC 起票)

| 提案 ID | 起票先 | リンク |
|---|---|---|
```

- frontmatter `sources` は block 形式必須 (URL + title + accessed_at の 3 キー)
- 5 行 summary は冒頭 blockquote (>) ではなく `# 概要 (5 行以内)` 直下に記述 (本フォーマット固有)
- 改善提案プレフィックスは `[skill]` / `[rule]` / `[remove]` に加え **`[mcp]`** を追加 (harness-evolution 固有、新規 MCP 採用余地検討用)

## Context7 MCP による引用検証 (R-28)

- AI が取得した外部知見のうち **ライブラリ API / バージョン情報** は **必ず Context7 MCP で再検証**
- 古い API / 存在しない API / hallucination 起源の API を提案するリスクを抑制
- 例: 「Kotlin Coroutines の `Flow.combine` の signature」を arxiv で見かけた場合、Context7 MCP で Kotlin 公式 docs を引いて確認
- 検証失敗 (Context7 MCP が該当 API を返さない) 時はその提案を見送り、`docs/harness/evolution-proposals/YYYY-MM-DD.md` に「Context7 引けず見送り」と明記

## harness-meta との重複防止 (R-31)

- 既に `docs/harness/learnings/*.md` の `🤖 ハーネス改善提案` で指摘済の提案は **harness-evolution 側を見送り**
- 重複判定方法: harness-evolution Skill 起動時に直近 N 件の learning ファイルを Read、提案セクションを diff
- 重複時の処理: `docs/harness/evolution-proposals/YYYY-MM-DD.md` に「harness-meta で既出のため見送り、出典のみ記録」と明記
- 改修 PR のラベル分離: harness-meta 採用 PR は `harness-meta` ラベル、harness-evolution 採用 PR は `harness-evolution` ラベル
- 優先度: **harness-meta を優先** (内部実体験ベース、R-31)

## 採用提案の Plan / Epic 起票

- 重要案は `docs/plans/PLAN-NNN-<slug>.md` (1 PR スコープ) または `docs/epics/EPIC-NNN-<slug>/` (複数 PR スコープ) で起票
- 起票判定: `plan.md` §Epic 昇格条件 (`>=2 PR` 想定なら Epic、単一 PR で完結なら Plan)
- 起票後は本ファイル `## 採用提案` 表に Plan / Epic リンクを追記、status を `actioned` に更新

## 出典記録の必須要件 (R-29)

- **URL + 引用日付 (accessed_at) を必ず記録**: 後追い検証可能性のため
- アーカイブ推奨: Wayback Machine / archive.today で snapshot を取得して URL を frontmatter `archive_url` (任意キー) に追記
- ライセンス確認: 引用元のライセンス (MIT / Apache 2.0 / CC-BY 等) を `sources` 表のメモ列で記録

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `docs/harness/evolution-proposals/*.md` の frontmatter `sources` 必須キー (URL / title / accessed_at) 検証、ホワイトリスト URL prefix マッチ
- **GitHub Actions**: evolution-proposal PR の merge 直前に URL 到達性 check + Context7 MCP 引用検証ログの存在確認
- **harness-evolution Skill 自体**: 出力前に `pii.md` / `secrets.md` redaction を必ず通す (R-26)

## Gotchas

- **必ず出典 URL + 引用日付** を記録 (R-29、後追い検証可能性のため)
- **Context7 MCP で API 引用検証** (古い・存在しない API を提案しない、R-28)
- **harness-meta との提案重複防止**: 既に learning ファイルで指摘済の提案は harness-evolution 側を見送り (R-31)
- **改修 PR のラベル分離** (`harness-meta` vs `harness-evolution`)、harness-meta 側を優先
- **ホワイトリスト先頭に `anthropics/skills` の更新確認** を組み込み (R-30、月次推奨)
- **手動起動のみ** (cron / wakeup 不採用、ADR 0026、Claude API コスト抑制)
- **PII / secrets redaction を必ず通す**: 外部情報源にも個人情報が混入する可能性 (`pii.md` / `secrets.md`)
- **新規 MCP 採用検討は `[mcp]` プレフィックス**: 既存 3 MCP (JetBrains / Context7 / Cloudflare) との overlap / コスト / セキュリティ境界を判断材料に明記

## 関連

- ADR 0026 (harness-evolution Skill 採用、二系統補完設計の SoT)
- `docs/harness/plan.md` §5.4.6 / R-29 / R-30 / R-31
- `.claude/skills/harness-evolution/SKILL.md`
- `.claude/rules/{harness-meta-criteria,skill-authoring,mcp-usage,retrospective-format,pii,secrets}.md`
- `docs/harness/evolution-proposals/` (出力先ディレクトリ)
