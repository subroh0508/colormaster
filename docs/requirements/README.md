---
id: requirements-readme
title: 要件定義 README
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4.6
---

# 要件定義 README

> **5 行以内 summary**: 機能要件 (REQ-NNN-<slug>.md) のルートディレクトリ。WHY と WHAT
> の境界画定が主目的。テンプレート (`template.md`) は §4.6.3 のセクション構造に従う。
> 個別 REQ ファイルは Phase C で各機能ごとに作成 (`feature-request` Skill 経由)。

## ディレクトリ運用

- 1 機能 = 1 ファイル (`REQ-NNN-<slug>.md`)
- 採番は連番 3 桁ゼロパディング、`feature-request` Skill が次番号を割り当て
- 起票言語は日本語 (ADR 0027)
- frontmatter 必須キー: `id`, `title`, `status`, `related_*`, `created_at`, `updated_at`

## テンプレート

- `template.md` — 11 セクション構造 (`docs/harness/plan.md` §4.6.3 参照)

## 主要セクション (§4.6.3)

| # | セクション | 表現方法 |
|---|---|---|
| 1 | 概要 / 目的 / 背景 | 自然言語 (5 行以内) + 表 |
| 2 | ステークホルダー / アクター | 表 |
| 3 | スコープ (含む / 含まない) | 箇条書き 2 段 |
| 4 | ユースケース概要 | Mermaid `graph` + UC 表 |
| 5 | 機能要件 (FR) | 表 |
| 6 | 非機能要件 (NFR) | IPA 非機能要求グレード 6 大項目 |
| 7 | 制約 / 前提 | 箇条書き |
| 8 | 用語定義 | 表 + glossary.md 参照 |
| 9 | トレーサビリティ | 表 (FR ID / SPEC / EPIC / PLAN / ADR) |
| 10 | 受け入れ基準 (AC) | チェックリスト |
| 11 | Open Questions | 表 |

## 関連

- `docs/harness/plan.md` §4.6.3 (要件定義テンプレ詳細)
- `.claude/rules/docs-structure.md`
- `.claude/skills/feature-request/SKILL.md` (A3 で本格化)
