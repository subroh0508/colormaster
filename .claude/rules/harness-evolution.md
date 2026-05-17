---
id: rules-harness-evolution
title: harness-evolution Skill 運用規約 (外部情報源ホワイトリスト + 手動起動)
status: skeleton
last_updated: 2026-05-17
paths:
  - ".claude/skills/harness-evolution/**"
  - "docs/harness/evolution-proposals/**"
related_plan: docs/harness/plan.md §5.3 / §5.4.6 / ADR 0026
related_adrs:
  - ADR-0026
---

# harness-evolution.md — harness-evolution 運用規約

> 外部研究・ベストプラクティス駆動の改善ループを **手動起動のみ** で運用する規約。
> 内部 KPT 駆動の `harness-meta` と二系統補完で動作。

## 起動方式

- **手動起動のみ** (Claude API コスト抑制のため cron 不採用、ADR 0026)
- ユーザーが必要時に Claude Code で `harness-evolution` を呼び出す
- 月次相当の頻度を推奨 (`anthropics/skills` の更新確認を兼ねる、R-30)

## 外部情報源ホワイトリスト

| カテゴリ | 情報源 |
|---|---|
| Anthropic 公式 | Anthropic engineering blog / `anthropics/skills` GitHub / Claude Code docs |
| MCP 仕様 | MCP spec (modelcontextprotocol.io) |
| AI Coding Agent | awesome-harness-engineering / Augment Code / HumanLayer / Cognition (Devin) |
| 学術 | arxiv (AI agents / autonomous coding 系) |
| 業界ブログ | Martin Fowler / Red Hat Developer / GitHub blog |

ホワイトリスト追加は Plan 起票 + 人間 approve 必須。

## 出力フォーマット

```
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
  - { url: https://..., title: ..., accessed_at: YYYY-MM-DD }
  - { url: https://..., title: ..., accessed_at: YYYY-MM-DD }
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

## Gotchas

- **必ず出典 URL + 引用日付** を記録 (R-29、後追い検証可能性のため)
- **Context7 MCP で API 引用検証** (古い・存在しない API を提案しない、R-28)
- **harness-meta との提案重複防止**: 既に learning ファイルで指摘済の提案は harness-evolution 側を見送り (R-31)
- 改修 PR は `harness-meta` / `harness-evolution` の **ラベル分離** で運用、harness-meta 側を優先
- ホワイトリスト先頭に `anthropics/skills` の更新確認を組み込み (R-30)

## 関連

- ADR 0026 (harness-evolution Skill 採用)
- `docs/harness/plan.md` §5.4.6 / R-29 / R-30 / R-31
- `.claude/skills/harness-evolution/SKILL.md`
- `.claude/rules/{skill-authoring,mcp-usage}.md`
