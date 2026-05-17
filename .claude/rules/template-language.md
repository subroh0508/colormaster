---
id: rules-template-language
title: テンプレート言語ポリシー (日本語必須)
status: stable
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
- Conventional Commits の type / scope (`feat` / `fix` / `refactor` / `docs(harness)` 等、`.claude/rules/commit-message.md` 参照)
- ブランチ名 prefix (`feature/` / `harness/` / `chore/` 等、`.claude/rules/branch-naming.md` 参照)
- **固定セクション名 (harness 内で安定して使われる section 名)** — 以下を英語のまま許容 (PR #123 レトロ Try):
  - `Open Questions` (`docs/harness/plan.md` §4.6.3-4.6.5 で canonical 規定、Epic / roadmap / template でも使用)
  - `Gotchas` (各 rule / Skill / docs の末尾セクション、`.claude/rules/markdown.md` 等で canonical)
  - `Keep` / `Problem` / `Try` (`pr-retrospective` learning ファイルの KPT セクション、`.claude/rules/retrospective-format.md` で canonical)
  - `Blockers` / `Workarounds` (`docs/harness/roadmap.md` 等の障壁記録セクション)
  - 上記の section 名は日本語訳すると plan.md / template / 他 docs との表記分裂を生むため英訳しない (人間 / AI の認知負荷増を予防)

## Phase A 期間中の経過措置

- **commit message subject**: 英語推奨だが Phase A (A1〜A10) 期間中は日本語混在を許容 (`.claude/rules/commit-message.md` §subject 言語ポリシー参照)
- **PR title**: commit subject と同じポリシー (Conventional Commits 形式 + 日本語混在許容)
- **PR description body**: 日本語推奨 (本 rule の日本語化対象に含まれる)、コード断片は英語
- **GitHub Issues / Discussions**: 日本語推奨、外部コラボレーション時のみ英語可

## 機械検証 (A6 で導入)

- Gradle カスタムタスク (Kotlin、`org.commonmark:commonmark` + `org.commonmark:commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で以下を検証 (§5.2):
  - 「frontmatter 外の見出しは日本語必須」
  - **「frontmatter の配列は block 形式必須」** (flow 形式 `[A, B]` を reject、`.claude/rules/docs-structure.md` の frontmatter 規約と統一)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない (§5.2)

## Gotchas

- **ADR / Plan / Epic のタイトル** は日本語で簡潔・現在形・断定的に記述
- **code 例 (Kotlin / Gradle DSL / SQL / シェル等) はそのまま英語**、コメントは日本語可
- **識別子 (SPEC-NNN-N 等) と enum 値 (status / type 等) を日本語訳しない** (parse 不可能になる)
- **`paths` 未設定で常時ロード**: A2-1 で `paths: ["**/*.md"]` 削除、rules-index.md / CLAUDE.md の「常時ロード (paths 未設定の rule)」リストと整合 (PR #117 A1 レトロ Problem #2 解消)
- **本 rule は安全網として PII / Secrets / rules-index と同じ常時ロード群** に含まれる: 全 Markdown 共通の規約のため、起動時に無条件ロード
- **過去コミット (A1 / A2-1 等) の日本語混在 subject** は Phase A 経過措置で許容、Phase B (A6 で `commit-msg` hook 拡張時) に英語強制を再評価
- **Markdown 表記規約は `.claude/rules/markdown.md`** が担当、本 rule は言語ポリシー (日本語必須 + 例外列挙) に集中

## 関連

- ADR 0027 (docs 構造 + 命名規約 + 日本語化方針)
- `docs/harness/plan.md` §5.5 (テンプレート言語ポリシー)
- `.claude/rules/{markdown,docs-structure,commit-message,branch-naming,pr-template}.md`
- `docs/epics/EPIC-A2-rules-docs-extension/decisions.md` (A2-1 paths 削除の判断記録)
