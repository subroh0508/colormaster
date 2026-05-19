---
id: plans-index
title: Plan 一覧
status: living
last_updated: 2026-05-20
---

# Plan 一覧

> **5 行以内 summary**: 単一 PR で完結する取り組みの一覧。Plan は `docs/plans/PLAN-NNN-*.md`
> として 1 ファイルで管理。Plan は **ロードマップ追跡対象外** (PR レビュー & merge で完結)。
> 起票は `plan-author` Skill 経由。Epic に昇格したら status: promoted + promoted_to: EPIC-NNN。

## 一覧

| PLAN ID | タイトル | type | status | related_epic | 起票日 |
|---|---|---|---|---|---|
| PLAN-001 | ADR 0001-0027 一括起票 | chore | completed | EPIC-000 | 2026-05-17 |
| [PLAN-002](PLAN-002-3-axis-eval-for-harness-meta-evolution.md) | harness-meta / harness-evolution 改修 PR の 3 軸定量評価フレーム導入 | harness | completed | — | 2026-05-19 |
| [PLAN-003](PLAN-003-a8-imasparql-docker.md) | A8 im@sparql ローカル Docker 環境構築 (Fuseki container) | feature-request | completed | — | 2026-05-19 |
| [PLAN-004](PLAN-004-A5-removed-modules.md) | A5 不要モジュール撤去 (js / kotlin-js-store / Firebase Hosting 設定) | refactor | completed | — | 2026-05-19 |
| [PLAN-005](PLAN-005-A6-lint-format-foundation-step1.md) | A6 Lint/Format 基盤 step1 = Spotless + ktlint 最小統合 | harness | completed | — | 2026-05-19 |
| [PLAN-006](PLAN-006-imasparql-rdf-loading.md) | im@sparql RDF データ取得と Fuseki への load 計画立案 | feature-request | proposed | — | 2026-05-19 |
| [PLAN-007](PLAN-007-claude-code-docker-cli-isolation.md) | Claude Code Docker CLI 分離 (DOCKER_CONFIG + wrapper script) | harness | completed | — | 2026-05-20 |

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 着手中 |
| `completed` | 完了 (PR マージ済) |
| `abandoned` | 取り下げ |
| `promoted` | Epic に昇格 (frontmatter `promoted_to: EPIC-NNN`) |

## 関連

- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
- `docs/plans/template.md`
- `.claude/skills/plan-author/SKILL.md`
- `.claude/rules/plan.md`
