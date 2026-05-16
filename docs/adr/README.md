---
id: adr-readme
title: ADR (Architecture Decision Records) README
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4.5
---

# ADR (Architecture Decision Records)

> **5 行以内 summary**: 重要なアーキテクチャ決定の記録。Michael Nygard 原則
> ("Architecturally Significant Decisions" のみ記録) 準拠。起票基準は §4.5 と
> `.claude/rules/adr.md` を参照。連番 4 桁ゼロパディング、日本語、`accepted` 以降は
> immutable で変更時は新 ADR + `Superseded by` リンク。0001-0027 は A1 で一括起草予定。

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

## ADR 0001-0027 一覧 (A1 で一括起草)

詳細は `docs/harness/plan.md` §4.0.3 参照。要約:

| ADR | 状態 | 内容 |
|---|---|---|
| 0001 | 既存 | ADR 運用基準・書式・起票判断フロー |
| 0002 | 既存 | Compose Multiplatform + 共通 ViewModel + Navigation 3 |
| 0003 | 既存 | モジュール構造 feature-first |
| 0004 | 既存 | テスト戦略総論 |
| 0005 | 既存 | Decompose 撤去 |
| 0006 | 既存 | i18n compose-multiplatform-resources |
| 0007 | 既存 | im@sparql upstream-driven 同期 |
| 0008 | 既存 | ユーザーデータ Backend SQLite + Litestream + R2 |
| 0009 | 既存 | Backend ホスティングは Cloud Run |
| 0010 | 既存 | アイドル情報マスタ SQLite を repo 内 commit |
| 0011 | 統合 | 認証スタック転換 (Firebase 廃止 + GIS 統一) |
| 0012 | 既存 | js/app 撤去 |
| 0013 | 既存 | Line/Branch coverage 段階達成 |
| 0014 | 既存 | im@sparql ローカル Docker (Fuseki) |
| 0015 | 既存 | Mutation testing (PITest) |
| 0016 | 既存 | Spec coverage / `@Spec` annotation |
| 0017 | 既存 | ハーネスローカル Claude Code ポーリング駆動 |
| 0018 | 既存 | `implementation-workflow` 10 フェーズ設計 |
| 0019 | 既存 | `code-reviewer` 8 aspect + Coordinator |
| 0020 | 既存 | PII 保護と権限ロール |
| 0021 | 既存 | Secrets 管理ポリシー |
| 0022 | 既存 | Cloudflare Pages + R2 |
| 0023 | 統合 | UI 凍結三本柱 (DESIGN.md + Inventory + Roborazzi) |
| 0024 | 既存 | MCP サーバ採用 |
| 0025 | 既存 | Skill 作成は `example-skills:skill-creator` 経由 |
| 0026 | 既存 | `harness-evolution` Skill 採用 |
| 0027 | 統合 | docs 構造 + 命名規約 + 5 行 summary + lazy-load + 日本語化 |

## 関連

- `.claude/rules/adr.md` (起票基準の Single Source of Truth)
- `docs/harness/plan.md` §4.5 (判断フロー Mermaid)
- `docs/adr/template.md`
