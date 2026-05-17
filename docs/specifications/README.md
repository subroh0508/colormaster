---
id: specifications-readme
title: 仕様 (基本設計 / 詳細設計) README
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0027
related_plan: docs/harness/plan.md §4.6
---

# 仕様 (基本設計 / 詳細設計) README

> **5 行以内 summary**: 基本設計 (`basic/SPEC-<id>-<slug>.md`) と詳細設計
> (`detail/SPEC-<id>-<slug>.md`) を **物理的にサブディレクトリで分離** して管理する。
> ペア参照は frontmatter `related_basic` / `related_detail` で **双方向リンク** (A6 で
> Gradle カスタムタスクが実在を機械検証)。本文に **コード断片を一切含めない** (§4.6.1)。
> 識別子 / `file_path:line` 参照のみ許容。個別 SPEC は Phase C で `feature-request` Skill が起票。

## 物理分離の理由

| 観点 | 効果 |
|---|---|
| 責務の物理分離 | 基本設計と詳細設計が混在せず、AI と人間どちらも読みやすい |
| 段階的起票 PR スコープ明確化 | 「basic だけ先に起票」「basic 確定後に detail を起票」が一目瞭然 |
| AI の glob 絞り込み | `docs/specifications/basic/**` のように検索範囲を限定可能 |

## 3 種の責務分担 (§4.6.2)

| 文書 | 主目的 | 抽象度 | 主読者 |
|---|---|---|---|
| **要件定義** (`docs/requirements/REQ-NNN`) | WHY / WHAT | 業務 / ユーザー視点 | 人間 + AI (feature-request Skill) |
| **基本設計** (`basic/SPEC-NNN-basic`) | システム外形 / 業務フロー | アーキ視点 | 人間 + AI (implementation-workflow Skill) |
| **詳細設計** (`detail/SPEC-NNN-detail`) | モジュール責務 / 状態 / 例外 | モジュール視点 | 主に AI (implementation-workflow / code-reviewer) |

## ペア参照 (双方向リンク必須)

- basic 側 frontmatter は block 形式で詳細設計を参照:

  ```yaml
  related_detail:
    - SPEC-IDOL-001-detail
  ```

- detail 側 frontmatter は block 形式で基本設計を参照:

  ```yaml
  related_basic:
    - SPEC-IDOL-001-basic
  ```

- 1 つの basic が複数の detail に展開される場合は `related_detail` 配列に列挙
- detail から要件への遡及は `related_requirements: [REQ-NNN]` を併記し、basic を経由しない直接参照も可
- A6 で Gradle カスタムタスクが「basic ↔ detail の双方向リンクの実在」+ 「REQ ⇄ SPEC ⇄ EPIC ⇄ PLAN ⇄ ADR の ID 参照実在」を機械検証

## テンプレート

| 用途 | パス |
|---|---|
| 基本設計テンプレ | `basic/template.md` (§4.6.4 の 11 セクション構造) |
| 詳細設計テンプレ | `detail/template.md` (§4.6.5 の 11 セクション構造) |

## AI 向け quick-reference

- basic は **業務 / システム外形視点**、detail は **モジュール責務 / 状態 / 例外視点**
- basic の §7 「外部 I/F 一覧」は概要のみ。リクエスト/レスポンス JSON 本体は **`docs/api/colormaster-api.yaml` (OpenAPI 3.1) を Single Source of Truth** として参照
- detail の §6 「データ構造」は **論理スキーマ** (型は `String` / `Long` / `Instant` 等の論理表記)。物理スキーマは `*.sq` (SQLDelight) / OpenAPI yaml が SoT
- **コード断片は禁止** (§4.6.1)。関数名 / クラス名 / `file_path:line` は ID 参照として許容
- Mermaid (graph / sequenceDiagram / stateDiagram-v2 / erDiagram) はコードではなく図表として許容

## 関連

- `docs/harness/plan.md` §4.6 (設計ドキュメントポリシー)
- `.claude/rules/docs-structure.md`
- `docs/requirements/template.md`
- `docs/traceability.md` (A6 で自動生成)
- `docs/api/colormaster-api.yaml` (A2-5 で本格化、外部 I/F の Single Source of Truth)
- `docs/glossary.md`
