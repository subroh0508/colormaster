---
name: harness-bootstrap
description: |
  Phase A の A1〜A10 を進める汎用 Skill。専用 Skill 群が揃うまで、入力パスから
  タスク種別 (ADR 起草 / rules 拡充 / docs 拡充 / Skill 実装 / 撤去 / Lint 導入) を
  自動判定して該当ファイルを起草する。A3 完了後に archived/ へ移動。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §6.2
related_rules:
  - .claude/rules/adr.md
  - .claude/rules/docs-structure.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/template-language.md
---

# harness-bootstrap (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は Phase A 内 (主に A1-A3) で行う。

## 役割

専用 Skill 群 (`feature-request` / `bug-fix` / `refactor` / `adr-author` / `harness-meta` 等) が
A3 で揃うまで、`harness-bootstrap` が一時的に汎用 Skill として A1〜A10 の起票・起草を担当する。
A3 完了後に `.claude/skills/archived/` へ移動し、CLAUDE.md からの参照も外す。

## タスク種別の自動判定 (入力パスベース)

| 入力パスのパターン | モード |
|---|---|
| `docs/adr/*.md` を作成・更新 | ADR 起草モード |
| `.claude/rules/*.md` を作成・更新 | rules 拡充モード |
| `docs/{requirements,specifications,architecture,api,security,runbooks}/**` を作成・更新 | docs 拡充モード |
| `.claude/skills/*/` を作成 | Skill 実装モード (`example-skills:skill-creator` を呼び出す) |
| モジュールディレクトリ削除指示 | 撤去モード |
| `build.gradle.kts` / lint 設定追加 | Lint 導入モード |

複数該当時はユーザーに確認する。

## Gotchas

- 本 Skill は B0 時点では雛形のみ。本格動作は Phase A で実装する。
- ADR 起草モードでは `.claude/rules/adr.md` の起票基準を必ず参照する。
- Skill 実装モードでは新規 Skill を本リポジトリに作成せず、Claude Code ユーザースコープの `example-skills:skill-creator` を呼び出すこと (ADR 0025)。

## 関連

- `docs/harness/plan.md` §5.3 (Skill の責務)
- `docs/harness/plan.md` §6.2 A1-A3 (Phase A での本格動作内容)
