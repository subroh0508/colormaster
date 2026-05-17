---
id: rules-docs-structure
title: docs/ 構造と命名規約、AI が読む順序
status: stable
last_updated: 2026-05-17
paths:
  - "docs/**/*.md"
  - "DESIGN.md"
  - "CLAUDE.md"
  - "AGENTS.md"
related_plan: docs/harness/plan.md §4 / ADR 0027
related_adrs:
  - ADR-0027
---

# docs-structure.md — docs/ 構造規約

> AI が自律的に実装を進めるために必要な情報を `docs/` に体系化する規約 (ADR 0027)。
> 各 docs は **冒頭 5 行以内の summary + 詳細 lazy-load** 構造を必須化、命名規約と
> AI 用エントリポイント (推奨読み順) を明文化する。

## ディレクトリ構造 (B0 で配置)

詳細骨格は `docs/harness/plan.md` §4.0.1 を参照。主要ディレクトリ:

```
DESIGN.md                  ★ repo root
docs/
  README.md                ★ AI 用エントリポイント
  glossary.md              ★ ドメイン用語集
  codebase-map.md          ★ 主要パス → 責務
  traceability.md          ★ Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装 (A6 自動生成)
  architecture/            ★ 7 ファイル (overview/layers/data-flow/domain-model/state-machines/sequences/infrastructure)
  api/                     ★ OpenAPI 3.1 + auth/idols/me
  security/                ★ ADR 索引
  requirements/            ★ REQ-NNN-<slug>.md
  specifications/basic/    ★ SPEC-<id>-<slug>.md (基本設計)
  specifications/detail/   ★ SPEC-<id>-<slug>.md (詳細設計)
  adr/                     既存 ADR-NNNN
  epics/                   既存 EPIC-NNN-<slug>/
  plans/                   既存 PLAN-NNN-*.md
  harness/                 既存 + roadmap.md (★) + evolution-proposals/ (★)
  runbooks/                local-development.md (★), testing.md (★), i18n.md (★), mcp-setup.md (★) + 既存
  design/                  ★ DESIGN.md 補助 + inventory/
```

## 命名規約

| 種別 | 形式 | 例 |
|---|---|---|
| ADR | `ADR-NNNN` (4 桁ゼロパディング) | `ADR-0001` |
| Epic | `EPIC-NNN` (3 桁ゼロパディング) | `EPIC-001` |
| Plan | `PLAN-NNN` (3 桁ゼロパディング、Epic と独立採番) | `PLAN-001` |
| 要件 | `REQ-NNN-<slug>` | `REQ-001-search-by-brand` |
| 仕様 | `SPEC-<entity-id>-<seq>` (basic/detail 共通) | `SPEC-IDOL-001-3` |

## AI が読む順序 (推奨)

1. **`CLAUDE.md`** (常時ロード) — lookup table で編集対象 ⇄ rules の対応
2. **`docs/README.md`** — 全 docs 索引 + 推奨読み順
3. **`docs/glossary.md`** — ドメイン用語 (im@sparql / RDF / アイドル / ブランド / 担当 / 推し)
4. **`docs/codebase-map.md`** — 主要パス → 責務 / 関連 SPEC-ID / 関連 ADR
5. **タスク種別に応じた読み込み**:
   - 新機能実装 → `docs/requirements/REQ-NNN-*.md` → `docs/specifications/basic/SPEC-NNN-*.md` → `docs/specifications/detail/SPEC-NNN-*.md`
   - リファクタ → `docs/architecture/{overview,layers,data-flow}.md` + `docs/design/inventory/`
   - API 変更 → `docs/api/colormaster-api.yaml` + `docs/api/{auth,idols,me}.md`
   - ハーネス改修 → `docs/harness/plan.md` + 関連 `.claude/rules/`

## 各 docs ファイルの構造 (必須)

```markdown
---
id: <id>
title: <タイトル>
status: <ステータス>
last_updated: YYYY-MM-DD
related_adrs:
  - ADR-NNNN
related_specs:
  - SPEC-NNN-N
---

# <タイトル>

> **5 行以内の summary**: この docs が答える問い / 主読者 / 関連 docs

## 詳細

...
```

## frontmatter 規約

### 配列はブロック形式を強制

YAML frontmatter の配列は **必ずブロック形式** (`-` インデント表記) で記述する。
flow 形式 (`[A, B, C]`) は **禁止**。

理由:

- 行差分が読みやすく、PR レビューで追加・削除が把握しやすい
- Gradle カスタムタスク (A6 で導入予定) の YAML パーサーがどちらも受理するが、人間レビューの一貫性のため block を強制
- 1 要素でも block 形式に揃える (`[ADR-0023]` ではなく `- ADR-0023`)

例:

```yaml
# ✅ OK (block 形式) — 本リポジトリ全 docs / rule / Skill SKILL.md でこの形式を採用
related_adrs:
  - ADR-0001
  - ADR-0011
related_specs:
  - SPEC-IDOL-001-3

# ❌ NG (flow / inline 形式) — 機械検証で reject 予定 (A6)
# 以下はあくまで「禁止例」として記載しており、本リポジトリの実体には混入させないこと
related_adrs: [ADR-0001, ADR-0011]
related_specs: [SPEC-IDOL-001-3]
```

> **注**: 上記の `❌ NG` ブロックは比較のための参考表記であり、A6 で機械検証 (Gradle カスタム
> タスク) を導入した時点で flow 形式の検出は CI 失敗となる。PR #121 レビュー
> (spec-conformance aspect) で「`docs-structure.md:116-117` の NG 例にコメント注記なし」
> と指摘されたため A2-3 で明示化。

### 空配列の表記

要素ゼロの場合のみ `[]` を許容する (block 形式では「null」と区別できないため):

```yaml
related_adrs: []   # OK (要素ゼロ)
related_adrs:      # NG (これは null 扱いになる)
```

### 必須キー (各 docs 種別ごと)

| 種別 | 必須 |
|---|---|
| 全 docs | `id`, `title`, `status`, `last_updated` |
| 要件 (REQ-NNN) | 上記 + `related_specs`, `related_epics`, `related_plans`, `related_adrs`, `created_at`, `updated_at` |
| 基本設計 (SPEC-NNN-basic) | 上記 + `related_requirements`, `related_detail` |
| 詳細設計 (SPEC-NNN-detail) | 上記 + `related_requirements`, `related_basic` |
| ADR | `id`, `title`, `status`, `date`, `related_epics`, `related_plans`, `related_specs`, `superseded_by`, `supersedes` |
| Epic README | 上記 + `created_at`, `completed_at`, `expected_modules`, `related_adrs`, `related_specs` |
| Plan | 上記 + `type`, `related_pr`, `related_epic`, `related_specs`, `related_adrs`, `expected_modules`, `created_at`, `completed_at`, `promoted_to` |
| Learning | 上記 + `type: learning`, `related_pr`, `related_plan`, `related_epic`, `generated_at`, `generator` |
| ロードマップ | 上記 + `source_plan` または `source_epic` |

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** (Kotlin、`org.commonmark:commonmark` + `commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で以下を検証 (§5.2):
  - frontmatter 必須キー JSON Schema 検証
  - 冒頭 5 行以内の summary 存在
  - frontmatter 外の見出しは日本語必須 (ADR 0027)
  - ID 参照の実在 (REQ ⇄ SPEC ⇄ EPIC ⇄ PLAN ⇄ ADR)
  - 設計書本文にコード断片混入禁止 (§4.6)

## Gotchas

- **docs 肥大化対策**: 冒頭 5 行 summary は厳守 (R-32)
- **重複・矛盾検出**: `docs/traceability.md` を A6 で自動生成して機械維持 (R-33)
- **Konsist は Kotlin file 専用**、Markdown 検証には使えない (§5.2)
- 設計書本文 (`docs/{requirements,specifications}/**`) には **コード断片を一切含めない** (§4.6 のコード禁止原則)。識別子参照 / `file_path:line` のみ許容

## 関連

- ADR 0027 (docs 構造 + 命名規約 + 5 行 summary + lazy-load + 日本語化)
- `docs/harness/plan.md` §4
- `.claude/rules/{template-language,markdown,roadmap,adr,plan,epic,spec-living-sync}.md`
- `docs/README.md` (AI 用エントリポイント)
- `docs/traceability.md` (A6 自動生成、双方向リンク機械検証)
