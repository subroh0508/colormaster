---
name: epic-author
description: |
  複数 PR にまたがる Epic について docs/epics/EPIC-NNN-<slug>/ を template から
  生成し、docs/epics/INDEX.md を更新する。生成直後に roadmap-tracker を起動して
  全体ロードマップに新規 Epic を取り込む。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §4.1 / §5.3
related_rules:
  - .claude/rules/epic.md
  - .claude/rules/roadmap.md
  - .claude/rules/template-language.md
  - .claude/rules/docs-structure.md
---

# epic-author (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。

## 役割

`docs/epics/template/` を雛形として `docs/epics/EPIC-NNN-<slug>/` 配下に
README.md / roadmap.md / open-questions.md / decisions.md / progress.md を生成し、
`docs/epics/INDEX.md` に追記する。Plan からの昇格にも対応する。

## 入力

- Epic 名 (slug)
- 起票理由 (Plan からの昇格、または新規大規模取り組み)
- 想定変更モジュール (`expected_modules`、frontmatter に記録)

## 出力

- `docs/epics/EPIC-NNN-<slug>/{README,roadmap,open-questions,decisions,progress}.md`
- `docs/epics/INDEX.md` 追記
- 生成直後に **`roadmap-tracker` を起動** して `docs/harness/roadmap.md` 全体ロードマップに新規 Epic を取り込む

## Gotchas

- Plan からの昇格時は元 Plan の `status` を `promoted` にし、`promoted_to: EPIC-NNN` を frontmatter に追加 (§4.1)。
- `expected_modules` は `roadmap-tracker` の並行実装容易性ロジックの入力となるため未記入禁止。
- Epic 配下で複数 PR を出す場合の個別 Plan は `docs/plans/` に一元化し `related_epic: EPIC-NNN` で紐付ける (§4.2)。

## 関連

- `docs/epics/template/` (B0 で配置)
- `docs/harness/plan.md` §4.1 / §4.2
