---
id: epics-index
title: Epic 一覧
status: living
last_updated: 2026-05-17
---

# Epic 一覧

> **5 行以内 summary**: 複数 PR / 複数週にわたる大規模取り組みの一覧。Epic は
> `docs/epics/EPIC-NNN-<slug>/` 配下に 5 ファイル (`README.md` / `roadmap.md` /
> `open-questions.md` / `decisions.md` / `progress.md`) を持つ。新規 Epic 起票は
> `epic-author` Skill 経由。Plan からの昇格時は元 Plan の status を `promoted` に。

## 一覧

| EPIC ID | タイトル | status | 起票日 | 関連 |
|---|---|---|---|---|
| EPIC-000 | ハーネス基盤構築 (B0 + Phase A) | in-progress | 2026-05-17 | `docs/harness/plan.md` §6.1-6.2 |
| EPIC-A2 | `.claude/rules/*` 全ファイル本格化 + docs 全面拡充 | completed | 2026-05-17 | `docs/harness/plan.md` §6.2 A2 |

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 着手中 |
| `completed` | 完了 |
| `abandoned` | 取り下げ |

## 関連

- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
- `docs/epics/template/` (起票テンプレート)
- `.claude/skills/epic-author/SKILL.md`
- `.claude/rules/epic.md`
