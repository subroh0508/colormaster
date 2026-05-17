---
id: rules-retrospective-format
title: pr-retrospective が生成する learning ファイルの構造化フォーマット
status: skeleton
last_updated: 2026-05-17
paths:
  - "docs/harness/learnings/**/*.md"
  - ".claude/skills/pr-retrospective/**"
  - ".claude/skills/pr-poller/**"
  - ".claude/skills/harness-meta/**"
related_plan: docs/harness/plan.md §4.4 / §5.5
---

# retrospective-format.md — learning ファイル構造化フォーマット

> `pr-retrospective` Skill が `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を生成する際の
> 構造化フォーマット定義。`harness-meta` は本フォーマットに依存して提案セクションを parse する。

## ファイル名規約

```
docs/harness/learnings/YYYY-MM-DD-pr-<n>.md
```

`YYYY-MM-DD` は PR のマージ日。`<n>` は PR 番号。1 PR = 1 ファイル。

## frontmatter

```yaml
---
id: learning-pr-NNN
title: PR #NNN レトロスペクティブ
type: learning
status: draft | reviewed | actioned
related_pr: NNN
related_plan: PLAN-NNN
related_epic: EPIC-NNN
generated_at: YYYY-MM-DDTHH:MM:SSZ
generator: pr-retrospective Skill (vX.Y.Z)
---
```

## 本文フォーマット (日本語見出し、§5.5 例)

```markdown
# PR #NNN レトロスペクティブ

> 生成: pr-retrospective Skill (v1.0.0) at YYYY-MM-DDTHH:MM:SSZ
> 関連 Plan: PLAN-NNN / 関連 Epic: EPIC-NNN

## ✅ Keep (継続したいこと)
- ...

## ⚠️ Problem (詰まったこと / 制約)
- ...

## 🚀 Try (次回からの改善案)
- ...

## 📊 指標
| 指標 | Before | After | Δ |
|---|---|---|---|
| Line coverage | 100.00% | 100.00% | ±0 |
| Branch coverage | 100.00% | 100.00% | ±0 |
| Spec coverage | 98.4% | 100.0% | +1.6 |
| Mutation score | 86.2% | 88.1% | +1.9 |

## 🤖 ハーネス改善提案
<!-- harness-meta が parse する正規構造 -->
- [ ] `[rule]` ...
- [ ] `[skill]` ...
- [ ] `[template]` ...
- [ ] `[remove]` ...

## 📝 harness-meta フィードバック
<!-- harness-meta が後から追記。提案 → 結果の往復ログを 1 ファイル内で完結 -->
```

## ハーネス改善提案のプレフィックス

`harness-meta` は以下のプレフィックスを parse して分類する:

| プレフィックス | 意味 |
|---|---|
| `[rule]` | `.claude/rules/*.md` の新規追加・改修 |
| `[skill]` | `.claude/skills/*/` の新規追加・改修 |
| `[template]` | テンプレート Markdown (PR / Plan / Epic / Learning 等) の改修 |
| `[remove]` | 未使用 rule / dormant Skill の撤去候補 |

## Gotchas

- **PR コメントは出さない**。learning ファイルが Single Source of Truth (§4.4)。
- PII / secrets が含まれないよう `.claude/rules/pii.md` の redaction を必ず通す。
- `📝 harness-meta フィードバック` セクションは空でも見出しを残す (harness-meta が後追記する場所)。

## 関連

- `docs/harness/plan.md` §4.4 / §5.5
- `.claude/skills/pr-retrospective/SKILL.md`
- `.claude/skills/harness-meta/SKILL.md` (A3 で配置予定)
