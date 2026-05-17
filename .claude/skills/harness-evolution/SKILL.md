---
name: harness-evolution
description: |
  外部研究 / ベストプラクティス駆動の改善ループ。手動起動のみ (cron 不採用)。
  WebSearch / WebFetch + Context7 MCP で外部情報源 (ホワイトリスト) を取得 → 既存ハーネスと
  gap 分析 → docs/harness/evolution-proposals/YYYY-MM-DD.md 出力 → 重要案は
  example-skills:skill-creator 経由で Skill scaffold / Plan / EPIC 起票 (人間 approve 必須)。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §5.4.6 / ADR 0026
related_rules:
  - .claude/rules/harness-evolution.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/mcp-usage.md
---

# harness-evolution (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。

## 役割

- **手動起動のみ** (Claude API コスト抑制のため cron 不採用、ADR 0026)
- 外部情報源 (ホワイトリスト方式) から最新ベストプラクティスを取得:
  - Anthropic engineering blog / `anthropics/skills` の更新 / Claude Code docs / MCP spec
  - awesome-harness-engineering / arxiv / Martin Fowler / Red Hat Developer / HumanLayer / Augment Code 等
- 既存 `.claude/skills/` / `.claude/rules/` / `docs/` と gap 分析
- 改善提案 (新規 Skill / 既存 Skill 改修 / rules 強化 / 廃止候補 / 新 MCP 採用余地) を構造化リスト化
- `docs/harness/evolution-proposals/YYYY-MM-DD.md` を生成 (出典 URL + 引用日付 + 構造化改善案 必須)
- 重要案は `example-skills:skill-creator` 経由で Skill scaffold または Plan / EPIC 起票 (人間 approve 必須)

## Gotchas

- **情報源は必ずホワイトリスト方式**、提案には **出典 URL + 引用日付** 必須 (R-29)。
- Context7 MCP で API 引用検証 (古い・存在しない API を提案しない)。
- `harness-meta` (内部 KPT 駆動) と提案重複しないよう、既に learning ファイルで指摘済の提案は harness-evolution 側を見送り (R-31)。
- 改修 PR は `harness-meta` / `harness-evolution` のラベル分離で運用、harness-meta 側を優先。
- `anthropics/skills` の更新確認はホワイトリスト先頭、月次相当の頻度で手動実行を奨励 (R-30)。

## 関連

- `docs/harness/plan.md` §5.4.6 (Meta フェーズ二系統)
- ADR 0026 (harness-evolution 採用)
- `.claude/rules/harness-evolution.md`
