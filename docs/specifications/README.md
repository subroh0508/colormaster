---
id: specifications-readme
title: 仕様 (基本設計 / 詳細設計) README
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4.6
---

# 仕様 (基本設計 / 詳細設計) README

> **5 行以内 summary**: 基本設計 (`basic/SPEC-<id>-<slug>.md`) と詳細設計
> (`detail/SPEC-<id>-<slug>.md`) を **物理的にサブディレクトリで分離** して管理する。
> ペア参照は frontmatter `related_basic` / `related_detail` で双方向リンク。
> 本文に **コード断片を一切含めない** (§4.6.1)。識別子 / `file_path:line` 参照のみ許容。

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

## ペア参照

- basic 側 frontmatter は block 形式で詳細設計を参照:

  ```yaml
  related_detail:
    - SPEC-NNN-detail
  ```

- detail 側 frontmatter は block 形式で基本設計を参照:

  ```yaml
  related_basic:
    - SPEC-NNN-basic
  ```
- A6 で Gradle カスタムタスクが双方向リンクの実在を機械検証

## テンプレート

| 用途 | パス |
|---|---|
| 基本設計テンプレ | `basic/template.md` (§4.6.4 の 11 セクション構造) |
| 詳細設計テンプレ | `detail/template.md` (§4.6.5 の 11 セクション構造) |

## 関連

- `docs/harness/plan.md` §4.6 (設計ドキュメントポリシー)
- `.claude/rules/docs-structure.md`
- `docs/requirements/template.md`
- `docs/traceability.md` (A6 で自動生成)
