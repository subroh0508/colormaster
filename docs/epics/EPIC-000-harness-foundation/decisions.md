---
id: decisions-EPIC-000
title: EPIC-000 細粒度決定の記録
status: living
last_updated: 2026-05-17
source_epic: EPIC-000
---

# EPIC-000 細粒度決定の記録

> **5 行以内 summary**: ハーネス基盤構築 Epic 内で発生した細粒度の意思決定の記録。
> ADR に昇格するほどではない判断 (rules の具体的なリスト / Skill 雛形の最小構成 / etc.)
> を蓄積する。

## 決定一覧

| 決定日 | 決定内容 | 背景 | 影響範囲 | 関連 ADR (昇格時) |
|---|---|---|---|---|
| 2026-05-17 | B0 で配置する Skill 雛形は 10 個 (harness-bootstrap / plan-author / epic-author / pr-poller / pr-retrospective / implementation-workflow / code-reviewer / ui-snapshot / harness-evolution / roadmap-tracker) | `docs/harness/plan.md` §6.1 B0 仕様。専用 Skill (feature-request / bug-fix / refactor / dependency-upgrade / adr-author / harness-meta) は A3 で本格実装するため B0 では雛形配置しない | `.claude/skills/**` | — |
| 2026-05-17 | B0 で配置する rules は 19 ファイル (rules-index / retrospective-format / pr-poller / template-language / implementation-workflow / code-reviewer-aspects / pii / secrets / db-protection / adr / design-tokens / ui-snapshot / ui-inventory / behavior-preservation / mcp-usage / skill-authoring / harness-evolution / docs-structure / roadmap) | plan.md §6.1 B0 仕様の列挙そのまま | `.claude/rules/**` | — |
| 2026-05-17 | `harness-meta` / `feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` / `adr-author` の Skill 雛形は B0 では配置しない (A3 で本格実装するため) | plan.md §6.1 B0 仕様。本格実装に近い形でないと雛形だけ置いても価値が低いため | `.claude/skills/**` | — |
| 2026-05-17 | `skill-creator` は本リポジトリに配置しない (Claude Code ユーザースコープの `example-skills:skill-creator` を参照) | plan.md §5.3 / ADR 0025 | `.claude/skills/**` | ADR 0025 |
| 2026-05-17 | `harness-meta-criteria.md` rule は B0 では配置しない (A4 で本格化、起動閾値は B0 時点では `pr-poller.md` 内に記載) | plan.md §6.1 B0 仕様。デフォルト値 (未処理 learning 10 件 / 7 日経過) は `pr-poller.md` に既記載 | `.claude/rules/**` | — |
| 2026-05-17 | `.claude/rules/*` の各 rule に **`paths` frontmatter (block 形式)** を付与してコンテキスト効率化を図る。`pii.md` / `secrets.md` / `rules-index.md` / `template-language.md` のみ `paths` 未設定 = 常時ロード (安全網 / 全 Markdown 共通) | [Claude Code 公式 docs](https://code.claude.com/docs/en/memory#organize-rules-with-claude/rules/) の path-scoped rules 仕様に従い、19 rule を起動時無条件ロードから path-scoped ロードに変更。Skill / module / フェーズ別の rule は該当ファイル編集時のみロード | `.claude/rules/**` | — |
| 2026-05-17 | **frontmatter の YAML 配列は block 形式必須** (`- A` インデント表記)、flow 形式 (`[A, B]`) は禁止。1 要素でも block 形式、空配列のみ `[]` を許容 | 行差分の可読性向上、PR レビューでの追加・削除の把握容易性、A6 で導入する Gradle カスタムタスクでの機械検証準備 (`docs-structure.md` 規約 + `template-language.md` 機械検証で明文化) | 全 Markdown (`.claude/rules/**` / `docs/**` / `.github/PULL_REQUEST_TEMPLATE/**` / SKILL.md) | — |
