---
id: rules-template-language
title: テンプレート言語ポリシー (日本語必須)
status: skeleton
last_updated: 2026-05-17
# 注意: 全 Markdown 共通の規約のため `paths` を意図的に未設定 (常時ロード)。
# rules-index.md / CLAUDE.md の「常時ロード (paths 未設定の rule)」リストと整合させるため、
# `**/*.md` 等の path-scoped に戻さないこと (A2-1 で paths 削除、EPIC-A2 `decisions.md` 参照)。
related_plan: docs/harness/plan.md §5.5
related_adrs:
  - ADR-0027
---

# template-language.md — テンプレート言語ポリシー

> ハーネスが生成・参照する全ての Markdown テンプレートは **日本語で記述** する (ADR 0027)。
> AI 駆動でも人間レビューでも認知負荷を最小化し、ユーザーが第一言語で読み書きできるようにするため。
> **本 rule は安全網として常時ロード** (frontmatter `paths` を意図的に未設定、A1 レトロ Problem #2 の解消)。

## 日本語化対象

| カテゴリ | パス例 |
|---|---|
| ADR | `docs/adr/template.md` / 各 ADR 本体 |
| Epic | `docs/epics/template/{README,roadmap,open-questions,decisions,progress}.md` |
| Plan | `docs/plans/template.md` / 各 Plan 本体 |
| 要件定義 | `docs/requirements/template.md` / 各機能要件 md |
| 基本設計 / 詳細設計 | `docs/specifications/{basic,detail}/template.md` / 各仕様 md |
| Runbook | `docs/runbooks/template.md` / 各 runbook md |
| Learning | `pr-retrospective` Skill が生成する `docs/harness/learnings/YYYY-MM-DD-pr-N.md` |
| ロードマップ | `roadmap-tracker` Skill が更新する `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` |
| PR description テンプレ | `.github/pull_request_template.md` / `.github/PULL_REQUEST_TEMPLATE/*.md` |
| code-reviewer レビューコメント | `code-reviewer` Skill が PR に post する構造化コメント |
| ハーネス改修 PR description | `harness-meta` Skill が起票する PR の本文 |
| INDEX.md (Epic/Plan) | 見出しと説明列を日本語 |

## 例外 (英語のまま)

- YAML frontmatter のキー名 (`id`, `title`, `status`, `type`, `related_pr`, `related_epic`, `created_at`, `completed_at` 等)
- ステータス値 (`proposed`, `in-progress`, `completed`, `abandoned`, `promoted` 等)
- コマンド・ファイルパス・コード断片
- 識別子 (SPEC-IDOL-001-3, EPIC-NNN, PLAN-NNN, ADR 0001 等)

## 機械検証 (A6 で導入)

- Gradle カスタムタスク (Kotlin、`org.commonmark:commonmark` + `org.commonmark:commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で以下を検証 (§5.2):
  - 「frontmatter 外の見出しは日本語必須」
  - **「frontmatter の配列は block 形式必須」** (flow 形式 `[A, B]` を reject、`.claude/rules/docs-structure.md` の frontmatter 規約と統一)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない (§5.2)

## Gotchas

- ADR / Plan / Epic の **タイトル** は日本語で簡潔・現在形・断定的に記述。
- code 例 (Kotlin / Gradle DSL / SQL / シェル等) はそのまま英語。コメントは日本語可。
- 識別子 (SPEC-NNN-N 等) と enum 値 (status / type 等) を日本語訳しない (parse 不可能になる)。

## 関連

- ADR 0027 (docs 構造 + 命名規約 + 日本語化方針)
- `docs/harness/plan.md` §5.5 (テンプレート言語ポリシー)
- `.claude/rules/markdown.md` (Markdown 全般の表記規約)
