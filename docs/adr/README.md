---
id: adr-readme
title: ADR (Architecture Decision Records) README
status: living
last_updated: 2026-05-19
related_plan: docs/harness/plan.md §4.5
---

# ADR (Architecture Decision Records)

> **5 行以内 summary**: 重要なアーキテクチャ決定の記録。Michael Nygard 原則
> ("Architecturally Significant Decisions" のみ記録) 準拠。起票基準は §4.5 と
> `.claude/rules/adr.md` を参照。連番 4 桁ゼロパディング、日本語、`accepted` 以降は
> immutable で変更時は新 ADR + `Superseded by` リンク。0001-0027 は A1 (PLAN-001) で起草、
> ADR-0028 は PLAN-002 / PR #171 で起草 (PR #129 で予約後見送られた 0028 番号を再割当)。

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

## ADR 0001-0028 一覧 (0001-0027 は A1 / PR #119、0028 は PLAN-002 / PR #171)

詳細は `docs/harness/plan.md` §4.0.3 参照。状態は全件 `accepted` (PLAN-001 PR で `proposed → accepted`
の中間遷移を経ずに `accepted` で起票、根拠は PLAN-001 のメモを参照)。

**起票根拠 (§4.5)** 列は §4.5 の起票基準 10 項目 (1: アーキ / 2: 主要ライブラリ / 3: 外部サービス /
4: データ永続化 / 5: テスト戦略 / 6: セキュリティ / 7: ハーネス中核 / 8: 代替案比較 / 9: 撤回コスト高 /
10: 長期制約) のうち、該当する番号を列挙する (2 つ以上満たすときに ADR を起こす規約、`.claude/rules/adr.md` 参照)。
**関連 rule** 列は `.claude/rules/<name>.md` を指し、未実装 (`planned (A2-2 / A2-3 / A7 / Phase C)`) のものを含む
(A2 完了後に実体化、`rules-index.md` 参照)。

| ADR | 状態 | 内容 | 起票根拠 (§4.5) | 関連 rule | ファイル |
|---|---|---|---|---|---|
| 0001 | accepted | ADR 運用基準・書式・起票判断フロー | 7, 9, 10 | `adr.md` | [ADR-0001](ADR-0001-adr-charter.md) |
| 0002 | accepted | Compose Multiplatform + 共通 ViewModel + Navigation 3 | 1, 2, 8, 10 | `viewmodel.md` / `composable.md` / `navigation.md` / `ui-state.md` | [ADR-0002](ADR-0002-compose-multiplatform-with-nav3.md) |
| 0003 | accepted | モジュール構造 feature-first | 1, 10 | `naming.md` | [ADR-0003](ADR-0003-feature-first-module-structure.md) |
| 0004 | accepted | テスト戦略総論 (三層指標 index) | 5 | `kotlin-test.md` / `coverage-100.md` / `spec-traceability.md` / `mutation-testing.md` | [ADR-0004](ADR-0004-test-strategy-overview.md) |
| 0005 | accepted | Decompose 撤去 | 1, 2, 9 | `navigation.md` / `removed-modules.md` | [ADR-0005](ADR-0005-decompose-removal.md) |
| 0006 | accepted | i18n compose-multiplatform-resources | 2 | `i18n.md` | [ADR-0006](ADR-0006-i18n-compose-resources.md) |
| 0007 | accepted | im@sparql upstream-driven 同期 | 3, 4 | `sync-job.md` / `sparql.md` | [ADR-0007](ADR-0007-imasparql-upstream-driven-sync.md) |
| 0008 | accepted | ユーザーデータ Backend SQLite + Litestream + R2 | 3, 4, 6 | `db-protection.md` / `r2-litestream.md` / `pii.md` | [ADR-0008](ADR-0008-user-data-backend-sqlite-litestream-r2.md) |
| 0009 | accepted | Backend ホスティングは Cloud Run | 3, 8, 10 | `cloud-run-deploy.md` | [ADR-0009](ADR-0009-backend-hosting-cloud-run.md) |
| 0010 | accepted | アイドル情報マスタ SQLite を repo 内 commit | 4, 10 | `sqlite-data-file.md` | [ADR-0010](ADR-0010-idol-master-sqlite-in-repo.md) |
| 0011 | accepted (★統合) | 認証スタック転換 (Firebase 廃止 + GIS 統一) | 2, 3, 6, 9 | `backend-auth.md` / `firebase-boundary.md` / `no-firebase.md` | [ADR-0011](ADR-0011-auth-stack-firebase-to-gis.md) |
| 0012 | accepted | js/app と関連 Web 配信構成を撤去 | 1, 2, 9 | `removed-modules.md` | [ADR-0012](ADR-0012-js-app-removal.md) |
| 0013 | accepted | Line/Branch coverage 段階達成 | 5 | `coverage-100.md` | [ADR-0013](ADR-0013-coverage-stepwise-100.md) |
| 0014 | accepted | im@sparql ローカル Docker (Fuseki) | 3, 7 | `sparql.md` / `sync-job.md` (runbook: `local-imasparql.md`) | [ADR-0014](ADR-0014-imasparql-local-fuseki.md) |
| 0015 | accepted | Mutation testing (PITest) | 2, 5 | `mutation-testing.md` | [ADR-0015](ADR-0015-mutation-testing-pitest.md) |
| 0016 | accepted | Spec coverage / `@Spec` annotation | 5, 7 | `spec-traceability.md` / `kotlin-test.md` | [ADR-0016](ADR-0016-spec-coverage-annotation.md) |
| 0017 | accepted | ハーネスローカル Claude Code ポーリング駆動 (GitHub Actions で Claude API 呼ばない) | 7, 9 | `pr-poller.md` / `harness-meta-criteria.md` | [ADR-0017](ADR-0017-local-claude-code-polling.md) |
| 0018 | accepted | `implementation-workflow` 10 フェーズ設計 (Phase 0 worktree / Phase 9 cleanup) | 7 | `implementation-workflow.md` / `branch-naming.md` | [ADR-0018](ADR-0018-implementation-workflow-10-phases.md) |
| 0019 | accepted | `code-reviewer` 8 aspect + Coordinator (Evaluator 独立性) | 7 | `code-reviewer-aspects.md` / `merge-readiness.md` | [ADR-0019](ADR-0019-code-reviewer-8-aspects.md) |
| 0020 | accepted | PII 保護と権限ロール (uid のみ DB 保存、owner 1 名) | 6, 10 | `pii.md` | [ADR-0020](ADR-0020-pii-protection-and-roles.md) |
| 0021 | accepted | Secrets 管理ポリシー (R2 token TTL 90 日、Secret Manager) | 6 | `secrets.md` | [ADR-0021](ADR-0021-secrets-management-policy.md) |
| 0022 | accepted | Cloudflare Pages + R2 (Cloud Run と組み合わせてハイブリッド) | 3, 8 | `cloudflare-pages.md` / `r2-litestream.md` | [ADR-0022](ADR-0022-cloudflare-pages-and-r2.md) |
| 0023 | accepted (★統合) | UI 凍結三本柱 (DESIGN.md + Inventory + Roborazzi) | 5, 7 | `design-tokens.md` / `ui-snapshot.md` / `ui-inventory.md` / `behavior-preservation.md` / `screenshot-test.md` | [ADR-0023](ADR-0023-ui-freeze-three-pillars.md) |
| 0024 | accepted | MCP サーバ採用 (JetBrains + Context7 + Cloudflare、GitHub MCP 不採用) | 7, 8 | `mcp-usage.md` | [ADR-0024](ADR-0024-mcp-server-adoption.md) |
| 0025 | accepted | Skill 作成は `example-skills:skill-creator` 経由 (公式準拠) | 7, 8 | `skill-authoring.md` | [ADR-0025](ADR-0025-skill-creator-via-example-skills.md) |
| 0026 | accepted | `harness-evolution` Skill 採用 (内部 KPT + 外部研究 二系統) | 7, 8 | `harness-evolution.md` / `harness-meta-criteria.md` | [ADR-0026](ADR-0026-harness-evolution-internal-external.md) |
| 0027 | accepted (★統合) | docs 構造 + 命名規約 + 5 行 summary + lazy-load + 日本語化 | 7, 10 | `docs-structure.md` / `template-language.md` / `markdown.md` | [ADR-0027](ADR-0027-docs-structure-and-japanese.md) |
| 0028 | accepted | ハーネス改修 PR の品質指標として 3 軸定量評価フレーム (改善度 / 再現性 / 副作用) を導入 (PR #129 で予約後見送られた 0028 番号を再割当) | 5, 7, 10 | `harness-meta-criteria.md` / `harness-evolution.md` | [ADR-0028](ADR-0028-3-axis-quantitative-eval.md) |

### ★統合の経緯

| 旧 ADR | 統合先 / 削除理由 |
|---|---|
| 旧 0004 state-and-uiaction-conventions | `.claude/rules/{viewmodel,ui-state}.md` に統合 |
| 旧 0011 firebase-removal-complete | 新 0011 (GIS と統合) |
| 旧 0020 template-language-japanese | 新 0027 (documentation-structure と統合) |
| 旧 0021 gis-unified-authentication | 新 0011 (Firebase 廃止と統合) |
| 旧 0025 ui-design-snapshot-before-refactor | 新 0023 (Roborazzi と統合) |
| 旧 0026 visual-regression-testing-roborazzi | 新 0023 (UI 凍結三本柱と統合) |
| 旧 0026 permission-roles-owner-only | 新 0020 内のセクション |
| 旧 0030 documentation-structure-for-ai-autonomy | 新 0027 (日本語化方針と統合) |

詳細は `docs/harness/plan.md` §4.0.3 「統合・削除の履歴」参照。

## 関連

- `.claude/rules/adr.md` (起票基準の Single Source of Truth)
- `docs/harness/plan.md` §4.5 (判断フロー Mermaid)
- `docs/adr/template.md`
