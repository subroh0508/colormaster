---
id: requirements-index
title: 要件 (REQ-NNN) 一覧
status: living
last_updated: 2026-05-19
---

# 要件 (REQ-NNN) 一覧

> **5 行以内 summary**: 機能要件 (`REQ-NNN-<slug>.md`) の索引。1 機能 = 1 ファイルで管理し、
> 採番は連番 3 桁ゼロパディング。WHY / WHAT のみを記述し HOW (実装手段) は
> `docs/specifications/{basic,detail}/` に分離する。起票は `feature-request` Skill 経由、
> 詳細は `docs/requirements/README.md` を参照。

## 一覧

| REQ ID | タイトル | status | related_specs | related_plans | 起票日 |
|---|---|---|---|---|---|
| [REQ-001](REQ-001-imasparql-local-docker.md) | im@sparql ローカル Docker 環境 | proposed | SPEC-IMASPARQL-001-basic | PLAN-003 | 2026-05-19 |
| [REQ-002](REQ-002-imasparql-rdf-loading.md) | im@sparql RDF データ取得と Fuseki への load | proposed | SPEC-IMASPARQL-002-basic | PLAN-006 | 2026-05-19 |

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 / 実装着手前 |
| `in-progress` | 実装中 (Plan / Epic が `in-progress`) |
| `accepted` | 実装完了、Spec として安定運用 |
| `superseded` | 後継要件に置換 (frontmatter で後継 ID を指す) |
| `abandoned` | 取り下げ |

## 関連

- `docs/requirements/README.md` (テンプレ / 運用規約)
- `docs/requirements/template.md`
- `.claude/skills/feature-request/SKILL.md`
- `.claude/rules/docs-structure.md`
