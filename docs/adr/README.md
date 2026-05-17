---
id: adr-readme
title: ADR (Architecture Decision Records) README
status: living
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4.5
---

# ADR (Architecture Decision Records)

> **5 行以内 summary**: 重要なアーキテクチャ決定の記録。Michael Nygard 原則
> ("Architecturally Significant Decisions" のみ記録) 準拠。起票基準は §4.5 と
> `.claude/rules/adr.md` を参照。連番 4 桁ゼロパディング、日本語、`accepted` 以降は
> immutable で変更時は新 ADR + `Superseded by` リンク。0001-0027 は A1 (PLAN-001) で起草済。

## ステータス遷移 (MADR 4 状態)

`proposed` → `accepted` → `deprecated` | `superseded by ADR-NNNN`

## ADR 化すべき例 / すべきでない例

詳細は `.claude/rules/adr.md` 参照。要点:

### ADR 化すべき (起票基準 10 項目のうち 2 つ以上を満たす)

- アーキテクチャパターン / 主要ライブラリ / 外部サービス / データ永続化 / テスト戦略 / セキュリティ / ハーネス中核設計 / 複数案比較 / 撤回コスト高 / 長期制約

### ADR 化すべきでない (他の記録方法を使う)

- コーディング・命名規約 → `.claude/rules/`
- Epic 内の細粒度な保留→解決 → Epic の `open-questions.md` / `decisions.md`
- 1 PR で完結する判断 → Plan
- 運用手順 → runbook
- PR ごとの学び・改善案 → `docs/harness/learnings/`

## ADR 0001-0027 一覧 (A1 = PLAN-001 で起草済)

詳細は `docs/harness/plan.md` §4.0.3 参照。状態は全件 `accepted` (PLAN-001 PR で `proposed → accepted`
の中間遷移を経ずに `accepted` で起票、根拠は PLAN-001 のメモを参照)。

| ADR | 状態 | 内容 | ファイル |
|---|---|---|---|
| 0001 | accepted | ADR 運用基準・書式・起票判断フロー | [ADR-0001](ADR-0001-adr-charter.md) |
| 0002 | accepted | Compose Multiplatform + 共通 ViewModel + Navigation 3 | [ADR-0002](ADR-0002-compose-multiplatform-with-nav3.md) |
| 0003 | accepted | モジュール構造 feature-first | [ADR-0003](ADR-0003-feature-first-module-structure.md) |
| 0004 | accepted | テスト戦略総論 (三層指標 index) | [ADR-0004](ADR-0004-test-strategy-overview.md) |
| 0005 | accepted | Decompose 撤去 | [ADR-0005](ADR-0005-decompose-removal.md) |
| 0006 | accepted | i18n compose-multiplatform-resources | [ADR-0006](ADR-0006-i18n-compose-resources.md) |
| 0007 | accepted | im@sparql upstream-driven 同期 | [ADR-0007](ADR-0007-imasparql-upstream-driven-sync.md) |
| 0008 | accepted | ユーザーデータ Backend SQLite + Litestream + R2 | [ADR-0008](ADR-0008-user-data-backend-sqlite-litestream-r2.md) |
| 0009 | accepted | Backend ホスティングは Cloud Run | [ADR-0009](ADR-0009-backend-hosting-cloud-run.md) |
| 0010 | accepted | アイドル情報マスタ SQLite を repo 内 commit | [ADR-0010](ADR-0010-idol-master-sqlite-in-repo.md) |
| 0011 | accepted (★統合) | 認証スタック転換 (Firebase 廃止 + GIS 統一) | [ADR-0011](ADR-0011-auth-stack-firebase-to-gis.md) |
| 0012 | accepted | js/app と関連 Web 配信構成を撤去 | [ADR-0012](ADR-0012-js-app-removal.md) |
| 0013 | accepted | Line/Branch coverage 段階達成 | [ADR-0013](ADR-0013-coverage-stepwise-100.md) |
| 0014 | accepted | im@sparql ローカル Docker (Fuseki) | [ADR-0014](ADR-0014-imasparql-local-fuseki.md) |
| 0015 | accepted | Mutation testing (PITest) | [ADR-0015](ADR-0015-mutation-testing-pitest.md) |
| 0016 | accepted | Spec coverage / `@Spec` annotation | [ADR-0016](ADR-0016-spec-coverage-annotation.md) |
| 0017 | accepted | ハーネスローカル Claude Code ポーリング駆動 | [ADR-0017](ADR-0017-local-claude-code-polling.md) |
| 0018 | accepted | `implementation-workflow` 10 フェーズ設計 | [ADR-0018](ADR-0018-implementation-workflow-10-phases.md) |
| 0019 | accepted | `code-reviewer` 8 aspect + Coordinator | [ADR-0019](ADR-0019-code-reviewer-8-aspects.md) |
| 0020 | accepted | PII 保護と権限ロール | [ADR-0020](ADR-0020-pii-protection-and-roles.md) |
| 0021 | accepted | Secrets 管理ポリシー | [ADR-0021](ADR-0021-secrets-management-policy.md) |
| 0022 | accepted | Cloudflare Pages + R2 | [ADR-0022](ADR-0022-cloudflare-pages-and-r2.md) |
| 0023 | accepted (★統合) | UI 凍結三本柱 (DESIGN.md + Inventory + Roborazzi) | [ADR-0023](ADR-0023-ui-freeze-three-pillars.md) |
| 0024 | accepted | MCP サーバ採用 (JetBrains + Context7 + Cloudflare) | [ADR-0024](ADR-0024-mcp-server-adoption.md) |
| 0025 | accepted | Skill 作成は `example-skills:skill-creator` 経由 | [ADR-0025](ADR-0025-skill-creator-via-example-skills.md) |
| 0026 | accepted | `harness-evolution` Skill 採用 (内部 + 外部 二系統) | [ADR-0026](ADR-0026-harness-evolution-internal-external.md) |
| 0027 | accepted (★統合) | docs 構造 + 命名規約 + 5 行 summary + lazy-load + 日本語化 | [ADR-0027](ADR-0027-docs-structure-and-japanese.md) |

## 関連

- `.claude/rules/adr.md` (起票基準の Single Source of Truth)
- `docs/harness/plan.md` §4.5 (判断フロー Mermaid)
- `docs/adr/template.md`
