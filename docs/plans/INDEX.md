---
id: plans-index
title: Plan 一覧
status: skeleton
last_updated: 2026-05-17
---

# Plan 一覧

> **5 行以内 summary**: 単一 PR で完結する取り組みの一覧。Plan は `docs/plans/PLAN-NNN-*.md`
> として 1 ファイルで管理。Plan は **ロードマップ追跡対象外** (PR レビュー & merge で完結)。
> 起票は `plan-author` Skill 経由。Epic に昇格したら status: promoted + promoted_to: EPIC-NNN。

## 一覧

| PLAN ID | タイトル | type | status | related_epic | 起票日 |
|---|---|---|---|---|---|

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 着手中 |
| `completed` | 完了 (PR マージ済) |
| `abandoned` | 取り下げ |
| `promoted` | Epic に昇格 (frontmatter `promoted_to: EPIC-NNN`) |

## 関連

- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
- `docs/plans/template.md`
- `.claude/skills/plan-author/SKILL.md`
- `.claude/rules/plan.md`
