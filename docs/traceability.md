---
id: traceability
title: トレーサビリティ (Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4 / §5.2 / §6.2 A6
---

# トレーサビリティ

> **5 行以内 summary**: Plan / Epic / ADR / Spec / Kotlin 実装のクロスリンク表。
> A6 で Konsist (Kotlin 側の `@Spec` annotation 抽出) + Gradle カスタムタスク
> (Markdown frontmatter の `related_*` 抽出) を join して自動生成する。
> B0 時点では本ファイルは骨格 (構造のみ)、A6 以降は CI で全面再生成される。

## 構造 (A6 で自動生成される形式)

| Plan | Epic | ADR | Spec (basic/detail) | Kotlin 実装 (`@Spec`) | テスト |
|---|---|---|---|---|---|
| (生成例) PLAN-XXX | EPIC-YYY | ADR-ZZZZ | SPEC-NNN-1 (basic), SPEC-NNN-2 (detail) | `feature/home/HomeViewModel.kt:42` | `HomeViewModelSpec.kt:test_search_by_brand` |

## 生成元

- **Plan / Epic / ADR / Spec 側**: Markdown frontmatter の `related_*` キー (Gradle カスタムタスクが抽出)
- **Kotlin 実装側**: テストクラスの `@Spec("SPEC-NNN-N")` annotation (Konsist が抽出)
- **join 条件**: SPEC-ID をキーに左右両方向でクロスリンク

## A6 までの暫定運用

- 自動生成までは本ファイルは骨格のみ
- 手動で重要な対応を記載してもよいが、A6 で全面再生成されるため append-only にしない

## 関連

- `docs/harness/plan.md` §4 / §5.2 / §6.2 A6
- `.claude/rules/{docs-structure,roadmap,spec-traceability}.md` (spec-traceability.md は A7 で導入)
- `.claude/rules/code-reviewer-aspects.md` (spec-conformance aspect)
