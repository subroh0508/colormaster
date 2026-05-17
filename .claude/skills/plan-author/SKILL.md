---
name: plan-author
description: |
  単一 PR スコープの取り組みについて docs/plans/PLAN-NNN-*.md を 1 ファイル生成し、
  docs/plans/INDEX.md を更新する。feature-request / bug-fix / refactor が
  「単一 PR スコープ」と判定したときに呼ばれる。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §4.1 / §5.3
related_rules:
  - .claude/rules/plan.md
  - .claude/rules/template-language.md
  - .claude/rules/docs-structure.md
---

# plan-author (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。

## 役割

`docs/plans/template.md` を雛形として 1 Plan ファイルを生成し、`docs/plans/INDEX.md` に追記する。
Plan は **1 PR で完結するボリューム** であり、ロードマップ追跡対象外 (R-34 / `roadmap-tracker` は Plan を走査しない)。

## 入力

- 起票理由 (人間指示 or 他 Skill からの委譲)
- 想定変更ファイル数・期間 (Epic と Plan の判定根拠、§4.1)

## 出力

- `docs/plans/PLAN-NNN-<slug>.md` (新規 1 ファイル)
- `docs/plans/INDEX.md` 追記

## Gotchas

- 想定変更ファイル数 > 10 または想定期間 > 1 週間 / open question 想定ありなら **Epic** として `epic-author` に委譲する (§4.1 判定しきい値)。
- Plan の Notes は自由記述。蓄積したら template に反映する。
- Plan は **ロードマップ追跡対象外**。`roadmap-tracker` は呼び出さない。
- frontmatter は §5.5 例 Plan テンプレート参照、日本語見出し必須 (ADR 0027)。

## 関連

- `docs/plans/template.md` (B0 で配置)
- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
