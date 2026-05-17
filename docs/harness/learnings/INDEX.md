---
id: learnings-index
title: KPT learnings 索引
status: skeleton
last_updated: 2026-05-17
---

# KPT learnings 索引

> **5 行以内 summary**: `pr-retrospective` Skill が生成する PR ごとの learning ファイル
> (`YYYY-MM-DD-pr-<n>.md`) の索引。1 PR = 1 ファイル、Single Source of Truth。
> ファイルフォーマットは `.claude/rules/retrospective-format.md` 参照。
> `harness-meta` Skill が後から `📝 harness-meta フィードバック` セクションを追記する。

## 索引 (起票時に追記)

| 日付 | PR # | 関連 Plan | 関連 Epic | actionable 提案数 | 採用提案数 |
|---|---|---|---|---|---|
| 2026-05-17 | [#117](https://github.com/subroh0508/colormaster/pull/117) | — | EPIC-000-harness-foundation | 15 | 0 |

## ファイル運用

- 1 PR = 1 ファイル (`YYYY-MM-DD-pr-<n>.md`)
- 自動生成: `pr-poller` → `pr-retrospective` 経路 (A4 で `CronCreate` / `ScheduleWakeup` 自動化完成)
- 集約 push: `harness/learnings-batch-YYYY-WW` ブランチ、週次 (or 件数到達時) に PR 起票
- `harness-meta` の後追記: feedback セクションを更新

## 関連

- `.claude/rules/retrospective-format.md`
- `.claude/skills/{pr-poller,pr-retrospective,harness-meta}/SKILL.md`
- `docs/harness/plan.md` §4.4
