---
id: ADR-0027
title: docs 構造と命名規約と 5 行 summary と日本語化を統一する
status: accepted
date: 2026-05-17
related_epics:
  - EPIC-000
related_plans:
  - PLAN-001
related_specs: []
superseded_by: null
supersedes: null
---

# ADR-0027: docs 構造と命名規約と 5 行 summary と日本語化を統一する

> **5 行以内 summary**: AI 駆動実装の入力源として `docs/` を体系化する。
> ディレクトリ構造 (`adr/` / `requirements/` / `specifications/{basic,detail}/` /
> `architecture/` / `api/` / `security/` / `runbooks/` / `design/inventory/` /
> `harness/`)、識別子採番 (REQ-NNN / SPEC-<entity>-<seq> / EPIC-NNN / PLAN-NNN / ADR-NNNN)、
> 各 docs 冒頭 5 行 summary + lazy-load、ハーネス Markdown は全て日本語、を統一規約とする。

## ステータス

accepted

## コンテキスト

ColorMaster は AI 駆動のセルフ改善ループ (Spec Gen → Implementation → Evaluation →
Merge → Retrospection → Meta) を稼働させる予定で、その入力となる docs 構造が
未整備のままでは Skill が安定して動作しない。具体的には:

- docs ディレクトリ構造が不揃いだと Skill が glob 絞り込みに失敗する。
- 識別子採番 (REQ / SPEC / EPIC / PLAN / ADR) が揺れると `related_*` 参照が壊れ、
  `docs/traceability.md` の自動生成が成立しない。
- 各 docs が冒頭 summary を持たないと AI が全文 lazy-load し context window を圧迫する。
- ハーネス Markdown の言語が混在すると、人間レビュー時と AI 起草時の認知負荷が乖離する。

加えて、Google Stitch / Anthropic の AI 駆動 UI 開発でも Markdown ベースの仕様書が
de facto standard になりつつあり、AI が最も読みやすい形に揃える価値は高い。

## 決定

以下を統一規約とする。

### ディレクトリ構造

詳細は `docs/harness/plan.md` §4.0.1 / `.claude/rules/docs-structure.md`。要点:

- `DESIGN.md` (repo root): UI tokens + Rationale (ADR-0023)
- `docs/README.md`: AI 用エントリポイント (推奨読み順)
- `docs/{glossary, codebase-map, traceability}.md`: ドメイン用語 / 主要パス / 全体トレーサビリティ
- `docs/architecture/{overview, layers, data-flow, domain-model, state-machines, sequences, infrastructure}.md`
- `docs/api/`: OpenAPI 3.1 + auth/idols/me
- `docs/requirements/REQ-NNN-<slug>.md`
- `docs/specifications/{basic,detail}/SPEC-<entity-id>-<seq>-<slug>.md`
- `docs/adr/ADR-NNNN-<slug>.md`
- `docs/epics/EPIC-NNN-<slug>/{README, roadmap, open-questions, decisions, progress}.md`
- `docs/plans/PLAN-NNN-<slug>.md`
- `docs/harness/{plan, roadmap, learnings/, evolution-proposals/}.md`
- `docs/runbooks/<name>.md`
- `docs/design/{README.md, inventory/{screens,components,states,flows}/}`

基本設計と詳細設計は **物理的にサブディレクトリで分離** し、`related_basic` /
`related_detail` の双方向 frontmatter リンクで参照する。

### 命名規約

| 種別 | 形式 | 例 |
|---|---|---|
| ADR | `ADR-NNNN` (4 桁ゼロパディング) | `ADR-0001` |
| Epic | `EPIC-NNN` (3 桁ゼロパディング) | `EPIC-001` |
| Plan | `PLAN-NNN` (3 桁ゼロパディング、Epic と独立採番) | `PLAN-001` |
| 要件 | `REQ-NNN-<slug>` | `REQ-001-search-by-brand` |
| 仕様 | `SPEC-<entity-id>-<seq>` (basic/detail 共通) | `SPEC-IDOL-001-3` |

### 各 docs の構造

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

> **5 行以内 summary**: この docs が答える問い / 主読者 / 関連 docs

## 詳細

...
```

5 行 summary は **lazy-load の入口**: AI が summary だけ読んで本文が必要か判断できる
ようにし、context window を圧迫しない。

### frontmatter

- YAML、配列は **ブロック形式必須** (`- ADR-0001` 形式)、flow `[A, B]` は禁止
- 必須キー: `id` / `title` / `status` / `last_updated` (種別ごとに追加キーあり、
  `.claude/rules/docs-structure.md` 参照)

### 日本語化

ハーネスが生成・参照する全 Markdown は **日本語で記述** する。例外:

- YAML frontmatter のキー名 (`id` / `title` / `status` 等)
- ステータス値 (`proposed` / `accepted` / `in-progress` / `completed` 等)
- コマンド・ファイルパス・コード断片
- 識別子 (`SPEC-IDOL-001-3` / `EPIC-NNN` / `ADR 0001` 等)

ADR / Plan / Epic のタイトルは **日本語で簡潔・現在形・断定的** に記述する。

## 根拠

- **AI 駆動の入力源としての docs**: AI Coding Agent が最も信頼性高く動作するのは
  「構造化された Markdown + 識別子による相互参照」であり、Google Stitch / Anthropic /
  GitHub Copilot Workspace 等の事例で実証されている。
- **5 行 summary の効果**: context window が有限な LLM では「全文 vs 要約」の選択が
  パフォーマンスに直結する。5 行 summary を入口にすると、関連性判断 → 詳細 lazy-load の
  2 段階で context を節約できる。
- **物理ディレクトリ分離 (basic / detail)**: 段階的起票 (Plan で basic だけ書く、
  詳細は別 PR) のスコープが明確になり、AI の glob 絞り込みも単純化する。
- **識別子 4 桁 / 3 桁ゼロパディング**: lexical sort と自然順 sort が一致し、
  `ls` / glob / 機械検証スクリプトが扱いやすい。
- **日本語化**: 認知負荷が第一言語で最も低く、AI も日本語コンテキストで一貫させた方が
  ハルシネーション減少 (英訳・和訳の往復で意味ずれが起きるリスクを構造的に排除)。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 全英語化 | 国際的な共同開発に親和 | 主要開発者 (owner) の認知負荷増 + AI の往復翻訳でずれ | 個人プロジェクト規模、owner 単一なら和文で十分 |
| basic / detail を 1 ファイル統合 | 関連情報がまとまる | PR スコープが肥大化 + glob 絞り込み不能 | 物理分離 + frontmatter 双方向リンクを採用 |
| frontmatter なし (Markdown 単体) | シンプル | 機械検証不可、識別子参照を grep 頼みになる | frontmatter 強制で `docs/traceability.md` 自動生成を可能に |
| 5 行 summary なし | 起草コスト最小 | AI 全文ロードで context 圧迫 | 5 行 summary を機械検証 (A6) で強制 |

## 帰結

### Positive

- AI Skill が docs を体系的に参照可能、`docs/traceability.md` (A6 自動生成) の入力源として完成。
- 5 行 summary により context 効率化、複雑な要件・仕様 docs でも AI が判断容易。
- 日本語化により owner / AI 双方の認知負荷最小化、和訳往復によるハルシネーション排除。

### Negative / トレードオフ

- 英語話者の貢献者が将来加わる場合、翻訳コストが発生 → owner 単一ロールの間は許容、
  複数人体制になったら別 ADR で再評価。
- 5 行 summary を厳守する起草コスト → テンプレで強制、A6 で機械検証。

### Neutral / 将来の検討事項

- frontmatter 必須キー / 5 行 summary / 識別子参照実在 / 設計書コード断片不在の
  機械検証 (Gradle カスタムタスク、Kotlin、`org.commonmark` + snakeyaml 2.x) は
  A6 で導入。
- `docs/traceability.md` の自動生成 (Konsist + frontmatter parser join) も A6。
- 国際化が必要になったら ADR を改訂し、英訳と原文の同期方式 (例: `*.en.md` 並置) を決める。

## ADR 起票基準 (§4.5) の充足

- [x] 1. アーキテクチャパターン / 層分割 / モジュール構造に影響する (docs ディレクトリ構造)
- [x] 7. ハーネス本体の中核設計 (Skill の入力源としての docs 体系)
- [x] 8. 複数の代替案を比較した結果としての判断 (英語化 / 統合 / frontmatter 有無 / summary 有無)
- [x] 9. 元に戻すコストが高い決定 (一度採用すると全 docs が依存)
- [x] 10. 長期的な制約 (今後 1 年以上、全 Markdown 起草に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR が単なる Markdown 表記規約 (
      `.claude/rules/markdown.md` で済む話) に留まらず、docs アーキテクチャ全体に
      影響する決定であることを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0001 (ADR 運用基準)
- ADR-0023 (UI 凍結三本柱、DESIGN.md の位置付け)
- `.claude/rules/docs-structure.md` (docs 構造規約の Single Source of Truth)
- `.claude/rules/template-language.md` (日本語化規約の Single Source of Truth)
- `.claude/rules/markdown.md` (Markdown 表記規約、A6 で本格化)
- `docs/harness/plan.md` §4 / §5.5
- `docs/README.md` (AI 用エントリポイント)
