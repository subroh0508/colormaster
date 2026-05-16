---
id: rules-skill-authoring
title: Skill 作成は example-skills:skill-creator 経由
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §5.3 / ADR 0025
related_adrs: [ADR-0025]
---

# skill-authoring.md — Skill 作成規約

> 新規 Skill 作成・既存 Skill 改修は Claude Code ユーザースコープの **`example-skills:skill-creator`**
> を呼び出して行う。本リポジトリには `skill-creator` を mirror しない。SKILL.md は Anthropic
> "Complete Guide to Building Skills for Claude" + AgentSkills 2026 spec + 100-point rubric 準拠。

## 起動方法

- 他 Skill (`harness-bootstrap` / `harness-meta` / `harness-evolution`) や人間からの「新規 Skill 作成 / 既存 Skill 改修」要求時に呼び出す
- 本リポジトリには `.claude/skills/skill-creator/` を配置しない (ユーザースコープにインストール済のため)

## SKILL.md フォーマット

```markdown
---
name: <skill-name>
description: |
  <trigger を含む 1-3 文の説明 — どんな状況で起動されるか / 何を達成するか>
status: skeleton | active | archived
phase: <導入フェーズ>
related_plan: <参照する plan.md 章番号>
related_rules:
  - .claude/rules/<rule1>.md
  - .claude/rules/<rule2>.md
---

# <skill-name>

## 役割
...

## 入力
...

## 出力
...

## Gotchas
- ...

## 関連
- ...
```

## description の書き方 (Anthropic 公式準拠)

- **description = trigger**。「いつ呼ばれるか」を明示。
- 1-3 文以内、200 文字以内推奨。
- 「~を作成する」「~を生成する」より「~の指示を受けたとき」「~を必要とする状況で」のように **起動契機を明示**。

## 必須項目

- **Gotchas セクション**: 罠 / 注意点 / 例外を必ず列挙
- **関連セクション**: 参照する rules / Plan 章 / 他 Skill を列挙
- **明示的な status**: `skeleton` / `active` / `archived`

## 禁止表現

- `MUST` / `ALWAYS` / `NEVER` を多用しない (Claude の挙動を硬直化させる)
- 代わりに「~するべき」「~を推奨」「~してはいけない (理由付き)」で記述

## 100-point rubric チェック (skill-creator が評価)

- description が trigger を明示している (20 点)
- Gotchas が具体的に列挙されている (15 点)
- 関連 rules / Plan 章へのリンクが正確 (15 点)
- 入力 / 出力が明示されている (10 点)
- ステータス値が定義済み (5 点)
- ...

## Gotchas

- **本リポジトリに `skill-creator` を mirror しない**: Claude Code ユーザースコープにインストール済 (ADR 0025)
- **公式アップデートに追従**: `anthropics/skills` の更新を `harness-evolution` Skill の手動実行時に確認 (R-30、月次頻度推奨)
- **既存 Skill 改修も `skill-creator` 経由**: 直接編集禁止ではないが、フォーマット drift を防ぐため経由を推奨

## 関連

- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.3 / R-30
- Anthropic "Complete Guide to Building Skills for Claude" (https://docs.anthropic.com/skills/)
- `.claude/skills/*/SKILL.md` (全 Skill の規範例)
