---
id: requirements-readme
title: 要件定義 README
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0027
related_plan: docs/harness/plan.md §4.6
---

# 要件定義 README

> **5 行以内 summary**: 機能要件 (`REQ-NNN-<slug>.md`) のルートディレクトリ。WHY と WHAT
> の境界画定が主目的、HOW は `docs/specifications/{basic,detail}/` に分離。テンプレート
> (`template.md`) は §4.6.3 の 11 セクション構造に従う。個別 REQ ファイルは Phase C で
> 各機能ごとに `feature-request` Skill が起票。本ファイル + テンプレが Phase A 時点で
> 提供する全て (個別 REQ は未配置)。

## ディレクトリ運用

- 1 機能 = 1 ファイル (`REQ-NNN-<slug>.md`)
- 採番は連番 3 桁ゼロパディング、`feature-request` Skill が次番号を割り当て
- 起票言語は日本語 (ADR 0027)
- frontmatter 必須キー: `id`, `title`, `status`, `related_*`, `created_at`, `updated_at`

## テンプレート

- `template.md` — 11 セクション構造 (`docs/harness/plan.md` §4.6.3 参照)
- 起票時にコピー: `cp docs/requirements/template.md docs/requirements/REQ-<NNN>-<slug>.md`
- frontmatter 必須キー: `id` / `title` / `status` / `related_specs` / `related_epics` / `related_plans` / `related_adrs` / `created_at` / `updated_at`
- `created_at` は起票日 (immutable)、`updated_at` は内容変更ごとに ISO 日付で更新

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

## AI 向け quick-reference

- WHY と WHAT のみを書き、HOW (実装手段) は **絶対に書かない** (§4.6.1 コード禁止原則)
- 必要に応じて `docs/glossary.md` を参照、用語のブレを起こさない
- ID 参照 (FR-N / UC-N / AC-N) は本ファイル内で完結させ、SPEC 側との対応は §9 トレーサビリティ表で明示
- 一機能 1 ファイル原則。複数機能を 1 ファイルに混ぜない (`feature-request` Skill が機能を分離)

## 関連

- `docs/harness/plan.md` §4.6.3 (要件定義テンプレ詳細)
- `.claude/rules/docs-structure.md`
- `.claude/rules/spec-living-sync.md` (A2-3 で本格化、実装中の要件変更時の双方向同期)
- `.claude/skills/feature-request/SKILL.md` (A3 で本格化)
- `docs/specifications/README.md` (basic / detail への展開)
- `docs/glossary.md`
