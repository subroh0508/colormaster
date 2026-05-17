---
id: docs-readme
title: docs/ AI 用エントリポイント
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0027
related_plan: docs/harness/plan.md §4
---

# docs/ AI 用エントリポイント

> **5 行以内 summary**: 本ファイルは AI が ColorMaster の docs を読む際の起点。推奨読み順 /
> ディレクトリ責務 / 命名規約への索引を提供する。AI Coding Agent が新規タスクに着手する際は、
> `CLAUDE.md` → 本ファイル → タスク種別に応じた docs の順で context を組み立てる。
> 詳細な構造定義は `docs/harness/plan.md` §4 と `.claude/rules/docs-structure.md` を参照。
> 各 docs の本格化状況は「ディレクトリ索引」表の `状態` 列で示す (B0 / A2 / A6 / A10 / C* 等)。

## AI が読む順序 (推奨)

1. **`CLAUDE.md`** (常時ロード) — lookup table で編集対象 ⇄ rules の対応
2. **本ファイル `docs/README.md`** — 全 docs 索引 + 推奨読み順
3. **`docs/glossary.md`** — ドメイン用語 (im@sparql / RDF / アイドル / ブランド / 担当 / 推し / SPARQL prefix / KMP / CMP / 内部実装用語 / テスト指標)
4. **`docs/codebase-map.md`** — 主要パス → 責務 / 関連 SPEC-ID / 関連 ADR / 関連 rules
5. **タスク種別に応じた docs** (下記)

## タスク別 lazy-load tree

### 新機能実装

```
docs/requirements/REQ-NNN-*.md
  → docs/specifications/basic/SPEC-<entity>-<seq>-*.md
    → docs/specifications/detail/SPEC-<entity>-<seq>-*.md
      → 該当 .claude/rules/<layer>.md
      → 該当 docs/architecture/<file>.md (層構造の整合確認)
```

### バグ修正

```
docs/codebase-map.md (該当パスから関連 SPEC / ADR を特定)
  → 該当 docs/specifications/detail/SPEC-*.md
    → .claude/rules/{error-handling,kotlin-test,test-paired-class}.md
```

### リファクタリング

```
docs/architecture/{overview,layers,data-flow}.md (A2-5 で本格化)
  → docs/design/inventory/ (A10 で本格化)
  → .claude/rules/{behavior-preservation,viewmodel,ui-state,repository}.md
```

### API 変更 / 追加

```
docs/api/colormaster-api.yaml (A2-5 で本格化、OpenAPI 3.1)
  → docs/api/{auth,idols,me}.md (A2-5 で本格化)
  → .claude/rules/{network-client,backend-auth}.md
```

### ハーネス改修

```
docs/harness/plan.md (Single Source of Truth)
  → docs/harness/roadmap.md (進捗ビュー)
  → 該当 .claude/rules/<rule>.md
  → 該当 .claude/skills/<skill>/SKILL.md
```

### 設計判断 (新規 ADR 起票)

```
.claude/rules/adr.md (起票基準 §4.5 該当 2 項目以上を確認)
  → docs/adr/template.md
  → 既存 ADR との superseded リンク
```

## ディレクトリ索引

| パス | 主目的 | 状態 | 詳細リンク |
|---|---|---|---|
| `DESIGN.md` (repo root) | デザイントークン (Google Stitch 3 階層) + Rationale | skeleton (A10 で本格化) | ADR 0023 |
| `docs/README.md` | 本ファイル、AI 用エントリポイント | living | — |
| `docs/glossary.md` | ドメイン用語集 (アイドル / RDF / KMP / 内部実装 / テスト / インフラ / ハーネス) | living (本 PR で本格化) | [docs/glossary.md](glossary.md) |
| `docs/codebase-map.md` | 主要パス → 責務 / 関連 SPEC-ID / 関連 ADR / 関連 rules | living (本 PR で本格化) | [docs/codebase-map.md](codebase-map.md) |
| `docs/traceability.md` | Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装 のクロスリンク | skeleton (A6 で自動生成) | [docs/traceability.md](traceability.md) |
| `docs/architecture/` | モジュール依存 / 層別責務 / データフロー / 状態遷移 / シーケンス / インフラ構成 (7 ファイル) | skeleton (A2-5 で本格化) | — |
| `docs/api/` | OpenAPI 3.1 + auth / idols / me 各エンドポイント詳細 | skeleton (A2-5 で本格化) | — |
| `docs/security/README.md` | セキュリティ関連 ADR 索引 + incident 対応 quick-reference | living (本 PR で本格化) | [docs/security/README.md](security/README.md) |
| `docs/requirements/` | 機能要件 (REQ-NNN-`<slug>`) + テンプレ | テンプレ living (本 PR) / 個別 REQ は Phase C | [docs/requirements/README.md](requirements/README.md) |
| `docs/specifications/basic/` | 基本設計 (SPEC-NNN-basic) + テンプレ | テンプレ living (本 PR) / 個別 SPEC は Phase C | [docs/specifications/README.md](specifications/README.md) |
| `docs/specifications/detail/` | 詳細設計 (SPEC-NNN-detail) + テンプレ | テンプレ living (本 PR) / 個別 SPEC は Phase C | [docs/specifications/README.md](specifications/README.md) |
| `docs/adr/` | Architecture Decision Records (0001-0027 一括起草済) | living (A1 完了) | [docs/adr/README.md](adr/README.md) |
| `docs/epics/` | 複数 PR の取り組み (EPIC-NNN-`<slug>`/ 配下に 5 ファイル) | living (EPIC-000 / EPIC-A2 起票済) | [docs/epics/INDEX.md](epics/INDEX.md) |
| `docs/plans/` | 単一 PR の取り組み (PLAN-NNN-*.md) | living (A1 以降で各 Plan ごとに追加) | [docs/plans/INDEX.md](plans/INDEX.md) |
| `docs/harness/` | ハーネス本体: plan.md / roadmap.md / learnings/ / evolution-proposals/ | living | [docs/harness/plan.md](harness/plan.md) |
| `docs/runbooks/local-development.md` | ローカル開発環境構築 (JDK 17 / Kotlin 2.1.21 / AGP 8.9.0 / IDE 2025.2+) | living (本 PR で本格化) | [docs/runbooks/local-development.md](runbooks/local-development.md) |
| `docs/runbooks/testing.md` | テスト実行と三層指標の運用 (Phase A 暫定 + A7 本格化) | living (本 PR で本格化) | [docs/runbooks/testing.md](runbooks/testing.md) |
| `docs/runbooks/i18n.md` | i18n (compose-multiplatform-resources) 運用 | living (本 PR で本格化、C4 で再本格化) | [docs/runbooks/i18n.md](runbooks/i18n.md) |
| `docs/runbooks/mcp-setup.md` | MCP セットアップ (JetBrains / Context7 / Cloudflare) | living (本 PR で本格化) | [docs/runbooks/mcp-setup.md](runbooks/mcp-setup.md) |
| `docs/runbooks/{secrets-rotation,r2-litestream,user-deletion,backend-local,cloudflare-pages,cloud-run-deploy,local-imasparql,troubleshooting}.md` | 各種運用手順 | 未配置 (各 Phase で本格化) | — |
| `docs/design/` | DESIGN.md 補助 + inventory/ (screens / components / states / flows / screenshots) | skeleton (A10 で本格化) | — |

## 命名規約

詳細は `.claude/rules/docs-structure.md`。要点:

| 種別 | 形式 | 例 |
|---|---|---|
| ADR | `ADR-NNNN` (4 桁) | `ADR-0001` |
| Epic | `EPIC-NNN` (3 桁) | `EPIC-001` |
| Plan | `PLAN-NNN` (3 桁、Epic と独立採番) | `PLAN-001` |
| 要件 | `REQ-NNN-<slug>` | `REQ-001-search-by-brand` |
| 仕様 | `SPEC-<entity-id>-<seq>` (basic/detail 共通) | `SPEC-IDOL-001-3` |

## 全 docs 共通の構造規約

- 冒頭 **5 行以内 summary** 必須 (ADR 0027、lazy-load の入口)
- frontmatter の配列は **ブロック形式必須** (`- ADR-0001` 形式、flow `[A, B]` は禁止)
- ハーネスが生成・参照する全 Markdown は **日本語** (例外: frontmatter キー名 / status 値 / 識別子 / コード断片)
- 設計書本文 (`docs/{requirements,specifications}/**`) には **コード断片を含めない** (§4.6.1)

## 関連

- `docs/harness/plan.md` §4 (ドキュメント構造の Single Source of Truth)
- `.claude/rules/docs-structure.md` (構造規約 / 5 行 summary 必須 / 命名規約 / lazy-load)
- `.claude/rules/template-language.md` (日本語化規約)
- `CLAUDE.md` (Skill / 編集対象 ⇄ rules lookup table)
- `AGENTS.md` (Claude Code 以外の AI Coding Agent 向けエントリポイント)
