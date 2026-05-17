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

```text
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
| 仕様 (連番形式) | `SPEC-<entity-id>-<seq>` (basic/detail 共通、suffix なし) | `SPEC-IDOL-001-3` |
| 仕様 (basic/detail suffix 形式) | `SPEC-<entity-id>-<seq>(-basic\|-detail)?` (ペア参照時の省略形 / 明示形) | `SPEC-IDOL-001-basic` / `SPEC-IDOL-001-detail` |

**仕様 SPEC suffix 形式の使い分け** (PR #123 レトロ Try):

- **連番形式** (`SPEC-IDOL-001-3`): 単一仕様 ID を独立に参照する場合の主形式
- **basic/detail suffix 形式** (`SPEC-IDOL-001-basic` / `SPEC-IDOL-001-detail`): 1 つのエンティティに対する basic / detail のペア参照を強調したい場合の省略形
- 両形式は同一エンティティの仕様を指すため互換性あり、frontmatter / 本文では一方に統一推奨

**title と H1 の表記方針** (PR #126 レトロ Try):

- frontmatter `title` = H1 の **完全一致** を原則とする
- 例外として `title` ⊇ H1 (frontmatter は完全形、H1 は短縮形) を許容、その場合は frontmatter title の冒頭から H1 の文字列が含まれる形にする
  - OK: `title: データフロー (im@sparql → Backend → Client)` + `# データフロー (im@sparql → Backend → Client)`
  - OK (短縮許容): `title: データフロー (im@sparql → Backend → Client)` + `# データフロー`
  - NG: `title: データフロー` + `# 詳細フロー` (frontmatter / H1 で別表現)

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
| 全 docs | `id`, `title`, `status`, `last_updated`, `related_specs` (該当なしは `[]` 明示、PR #126 レトロ Try) |
| 要件 (REQ-NNN) | 上記 + `related_specs`, `related_epics`, `related_plans`, `related_adrs`, `created_at`, `updated_at` |
| 基本設計 (SPEC-NNN-basic) | 上記 + `related_requirements`, `related_detail` |
| 詳細設計 (SPEC-NNN-detail) | 上記 + `related_requirements`, `related_basic` |
| ADR | `id`, `title`, `status`, `date`, `related_epics`, `related_plans`, `related_specs`, `superseded_by`, `supersedes` |
| Epic README | 上記 + `created_at`, `completed_at`, `expected_modules`, `related_adrs`, `related_specs` |
| Plan | 上記 + `type`, `related_pr`, `related_epic`, `related_specs`, `related_adrs`, `expected_modules`, `created_at`, `completed_at`, `promoted_to` |
| Learning | 上記 + `type: learning`, `related_pr`, `related_plan`, `related_epic`, `generated_at`, `generator` |
| ロードマップ | 上記 + `source_plan` または `source_epic` |

### related_plan の単複表記 (PR #123 レトロ Try)

- **単数 PLAN-NNN を指す場合も `related_plans:` (複数形 block) で統一**: スカラー単数形 `related_plan: PLAN-NNN` は **non-recommended** (旧形式、A2-4 以前の docs で混在)
- **`related_plan: —` (該当なし) は許容**: Learning など Plan と無関係な docs では単数 `related_plan` でハイフン (`—`) 明示も可
- **移行手順** (PR #123 由来 4 docs 等): スカラー単数形 → 複数形 block への移行は別 PR で順次対応、機械検証 (A6) で複数形強制を検証する際は単数形も grace 期間中は warning に留める

## 「現状: B0 段階 / 本格実装: Phase C」明示パターン参考例 (PR #126 レトロ Try)

実装が薄い領域 (Backend ほぼ空 / feature-first 未再編 / Firebase 撤去未済) を docs 化する際、「将来形 SoT として記述 + 現状差分を honest に明示」パターンを採用すると Phase C で同 docs を書き直す必要を最小化できる:

```markdown
## ストレージ層

### 将来形 (Phase C 完了後の SoT)

- Cloud Run + R2 + Litestream 構成 (`docs/runbooks/r2-litestream.md` 参照)
- SQLite WAL モード、Litestream replicate / restore で R2 に継続バックアップ

### 現状 (B0 段階)

- Cloud Run / R2 / Litestream は未配置 (Phase C5 / C7 で本格化予定)
- ローカル開発時は `data/idols.db` を直接マウント、`data/users.db` は `.gitignore` で除外
- 移行タイミング: Phase C5 着手時に Dockerfile + Litestream sidecar 構成を導入
```

- 「将来形」「現状」セクションを **明示的に分離**、混在記述は避ける
- 「現状」セクション末尾に **持ち越し TODO 表** (`| 持ち越し項目 | 持ち越し先 | 理由 |`) を配置して Phase 進捗追跡に資する

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
