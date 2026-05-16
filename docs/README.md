---
id: docs-readme
title: docs/ AI 用エントリポイント
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4
---

# docs/ AI 用エントリポイント

> **5 行以内 summary**: 本ファイルは AI が ColorMaster の docs を読む際の起点。
> 推奨読み順 / ディレクトリ責務 / 命名規約への索引を提供する。
> AI Coding Agent が新規タスクに着手する際は、CLAUDE.md → 本ファイル →
> タスク種別に応じた docs の順で context を組み立てる。
> 詳細な構造定義は `docs/harness/plan.md` §4 と `.claude/rules/docs-structure.md` を参照。

## AI が読む順序 (推奨)

1. **`CLAUDE.md`** (常時ロード) — lookup table で編集対象 ⇄ rules の対応
2. **本ファイル `docs/README.md`** — 全 docs 索引 + 推奨読み順
3. **`docs/glossary.md`** — ドメイン用語 (im@sparql / RDF / アイドル / ブランド / 担当 / 推し / SPARQL prefix)
4. **`docs/codebase-map.md`** — 主要パス → 責務 / 関連 SPEC-ID / 関連 ADR
5. **タスク種別に応じた docs**:
   - 新機能実装 → `docs/requirements/REQ-NNN-*.md` → `docs/specifications/basic/SPEC-NNN-*.md` → `docs/specifications/detail/SPEC-NNN-*.md`
   - リファクタ → `docs/architecture/{overview,layers,data-flow}.md` + `docs/design/inventory/`
   - API 変更 → `docs/api/colormaster-api.yaml` + `docs/api/{auth,idols,me}.md`
   - ハーネス改修 → `docs/harness/plan.md` + 関連 `.claude/rules/`
   - 設計判断 → `docs/adr/` (起票基準は `.claude/rules/adr.md`)

## ディレクトリ索引

| パス | 主目的 | 詳細 |
|---|---|---|
| `DESIGN.md` (repo root) | デザイントークン (Google Stitch 3 階層) + Rationale | A10 で本格生成 |
| `docs/README.md` | 本ファイル、AI 用エントリポイント | — |
| `docs/glossary.md` | ドメイン用語集 | A2 で本格化 |
| `docs/codebase-map.md` | 主要パス → 責務対応表 | A2 で本格化 |
| `docs/traceability.md` | Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装 のクロスリンク | A6 で自動生成 |
| `docs/architecture/` | モジュール依存 / 層別責務 / データフロー / 状態遷移 / シーケンス / インフラ構成 (7 ファイル) | A2 で本格化 |
| `docs/api/` | OpenAPI 3.1 + auth / idols / me 各エンドポイント詳細 | C5 で本格化 |
| `docs/security/README.md` | セキュリティ関連 ADR の索引 | A2 で本格化 |
| `docs/requirements/` | 機能要件 (REQ-NNN) | C3 以降で各機能ごとに追加 |
| `docs/specifications/basic/` | 基本設計 (SPEC-NNN-basic) | C3 以降で各機能ごとに追加 |
| `docs/specifications/detail/` | 詳細設計 (SPEC-NNN-detail) | C3 以降で各機能ごとに追加 |
| `docs/adr/` | Architecture Decision Records (0001-0027) | A1 で一括起草 |
| `docs/epics/` | 複数 PR の取り組み (EPIC-NNN-<slug>/ 配下に 5 ファイル) | EPIC-000 を B0 で起票 |
| `docs/plans/` | 単一 PR の取り組み (PLAN-NNN-*.md) | A1 以降で各 Plan ごとに追加 |
| `docs/harness/` | ハーネス本体: plan.md / roadmap.md / learnings/ / evolution-proposals/ | — |
| `docs/runbooks/` | 環境構築 / 同期 / デプロイ / secrets / MCP / テスト / i18n / トラブルシュート | B0 で 4 つ骨格、各フェーズで本格化 |
| `docs/design/` | DESIGN.md / Inventory / Baseline 運用ガイド + screens/components/states/flows/screenshots | A10 で本格化 |

## 命名規約

`.claude/rules/docs-structure.md` を参照。要点:

- ADR: `ADR-NNNN` (4 桁)
- Epic: `EPIC-NNN` (3 桁)
- Plan: `PLAN-NNN` (3 桁、Epic と独立採番)
- 要件: `REQ-NNN-<slug>`
- 仕様: `SPEC-<entity-id>-<seq>` (basic/detail 共通)

## 関連

- `docs/harness/plan.md` §4 (ドキュメント構造の Single Source of Truth)
- `.claude/rules/docs-structure.md` (構造規約 / 5 行 summary 必須 / 命名規約 / lazy-load)
- `CLAUDE.md` (Skill / 編集対象 ⇄ rules lookup table)
