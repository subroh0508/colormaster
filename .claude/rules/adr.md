---
id: rules-adr
title: ADR 起票基準と書式
status: stable
last_updated: 2026-05-17
paths:
  - "docs/adr/**/*.md"
  - ".claude/skills/adr-author/**"
related_adrs:
  - ADR-0001
  - ADR-0027
---

# adr.md — ADR 起票基準と書式

> Michael Nygard の原則 ("Architecturally Significant Decisions" のみ記録、
> "Any Decision Records" ではない) + AWS / Microsoft / Google / Martin Fowler の
> ADR ガイドラインに準拠。詳細は ADR 0001 を Single Source of Truth とする。
> Plan / Epic / 補助 rule との責務分離は本ルール §他の記録方法を参照。

## 起票基準: 以下の 2 つ以上を満たすときに ADR を起こす

1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
2. 主要なライブラリ / フレームワークの採用または撤去
3. 外部サービスの採用または変更 (DB / ホスティング / 認証 / CDN / etc.)
4. データ永続化 / 同期戦略 / バックアップ方式
5. テスト戦略・品質指標の中核方針
6. セキュリティ・プライバシー・ライセンスに関する方針
7. ハーネス本体の中核設計 (Skill 構成、ループ構造、ローカル vs サーバ実行)
8. 複数の代替案を比較した結果としての判断
9. 元に戻すコストが高い決定
10. 長期的な制約 (今後 1 年以上の判断のベースになるもの)

## ADR にすべき例 (本リポジトリの実例)

- Compose Multiplatform + 共通 ViewModel + Navigation 3 を採用する (ADR 0002)
- Decompose を撤去する (ADR 0005)
- Firebase を完全廃止して GIS に統一する (ADR 0011)
- Backend は Cloud Run (ADR 0009)、静的配信は Cloudflare Pages、Litestream バックアップ先は R2 (ADR 0022)
- アイドル情報を Git 内 SQLite に commit、ユーザーデータは Litestream で R2 にレプリケート (ADR 0008 / 0010)
- Line/Branch coverage を段階達成にする (ADR 0013)
- ハーネスループをローカル Claude Code ポーリングで駆動する (ADR 0017)
- implementation-workflow (ADR 0018) + code-reviewer (ADR 0019) の Generator/Evaluator 二段構成
- ハーネスを構成する Markdown は全て日本語で記述する (ADR 0027)
- PII の DB スキーマは `uid` のみ、権限ロールは当面 owner のみ (ADR 0020)
- UI/UX をリファクタ前に DESIGN.md + UI Inventory + Roborazzi baseline で凍結する (ADR 0023)
- MCP サーバは JetBrains + Context7 + Cloudflare の 3 つ (ADR 0024)
- Skill 作成は `example-skills:skill-creator` 経由 (ADR 0025)
- ハーネス進化は内部 (`harness-meta`) + 外部 (`harness-evolution`) の二系統 (ADR 0026)

## ADR にすべきでない例 (他の記録方法を使う)

| 例 | 適切な記録方法 |
|---|---|
| `@Composable` 関数の引数命名規約 | `.claude/rules/composable.md` |
| Kotlin の null 安全 / Result 型のコーディングルール | `.claude/rules/error-handling.md` |
| PR テンプレートに `Related plan` 行を追加 | `.claude/rules/pr-template.md` |
| `HomeViewModel.refresh()` 内のキャッシュ TTL を 5 分に変更 | Plan (`docs/plans/PLAN-NNN-*.md`) |
| EPIC-001 内で `SavedStateHandle` をどうシリアライズするか一時保留 | Epic の `open-questions.md` / 解決時は `decisions.md` |
| 単発のバグ修正の進め方 | Plan ファイル |
| テスト fixture に `@example.com` ドメインを使う規約 | `.claude/rules/pii.md` |
| Compose Preview の表記揺れを統一 | `.claude/rules/composable.md` (KPT 起点で追記) |
| Cloud Run の `min-instances` を 0 から 1 に変更 | runbook + Plan |
| 開発者がローカルで Fuseki Docker を起動する手順 | runbook (`docs/runbooks/local-imasparql.md`) |
| im@sparql 同期 PR の自動マージ可否 | sync workflow の設定 + 該当 Skill 規約 |
| `roadmap-tracker` Skill の操作規約 | `.claude/rules/roadmap.md` (補助 Skill、撤回コスト低のため ADR 不要) |

## 採番・命名・ステータス

- 連番、4 桁ゼロパディング (`0001`, `0002`, ...)
- ファイル名: `ADR-{NNNN}-{kebab-case-title}.md`
- タイトル: 簡潔・現在形・断定的
- ステータス: `proposed` → `accepted` → `deprecated` | `superseded by ADR-NNNN` (MADR 4 状態)
- `accepted` 以降は immutable。変更時は新 ADR を起こし旧 ADR を `Superseded by` でリンク
- 採番欠番は実装前なら整理可、運用後は番号維持で `withdrawn` を許容
- **言語は日本語** (ADR 0027)

## ADR ⇄ rule の SoT 方向 (PR #119 レトロ Try)

- **ADR = 決定の Single Source of Truth、rule = 運用詳細 (ADR を参照、逆方向 SSoT 宣言は禁止)**
- 各 rule の冒頭文言は「詳細は ADR NNNN を SoT とする」のように **rule → ADR の一方向** でのみ参照する
- ADR 側に「`.claude/rules/<name>.md` は本 ADR の SoT」のような **双方向 SSoT 宣言は書かない** (循環参照になり A6 機械検証時にトレーサビリティが弱まる)
- 例外: ADR が「運用詳細は rule に委譲する」と書くのは OK (委譲先の明示)、ただし「rule が SoT」とは書かない

## ADR 化見送りの理由テンプレ (PR #129 レトロ Try)

ADR 起票基準 (§起票基準 2 項目以上) を満たさないが意思決定の経緯を残したい場合、以下の 3 条件を満たすことで ADR 化を見送り、EPIC `decisions.md` / PR description / Plan で記録する:

| 条件 | チェック内容 |
|---|---|
| **撤回コスト低** | 1-2 PR で撤回可能、外部 service / DB schema / API 契約への影響なし |
| **scope が config N ファイル限定** | `.claude/settings.json` / `.github/workflows/**` / `.claude/mcp.json` 等の config 単位の改修で、複数 rule 横断改定なし |
| **既存 rule 本体の改定なし** | 既存 rule の規約変更が伴わない (rule の Gotchas / 関連リンクへの追記程度は OK) |

3 条件すべて満たす場合: 「ADR 起票基準 (§起票基準) を満たさないため見送り、撤回コスト低 / scope は `<files>` 限定 / 既存 rule 本体の改定なし」と PR description / EPIC `decisions.md` に明記し、ADR を起票しない。

実績: PR #129 (`.claude/settings.json` の `permissions.allow` 拡張) で本テンプレを適用、当時予約していた ADR-0028 番号での起票を見送り EPIC-A2 `decisions.md` に判断ログを記録 (本見送りで解放された ADR-0028 番号は PLAN-002 / PR #171 で **3 軸定量評価フレーム導入 ADR** に再割当)。

## 本文構造 (`docs/adr/template.md` と整合)

```markdown
---
id: ADR-NNNN
title: <日本語タイトル>
status: proposed | accepted | deprecated | superseded
date: YYYY-MM-DD
related_epics:
  - EPIC-NNN
related_plans:
  - PLAN-NNN
related_specs:
  - SPEC-NNN-N
superseded_by: ADR-NNNN | null
supersedes:
  - ADR-NNNN
---

# ADR-NNNN: <タイトル>

## ステータス

## コンテキスト

## 決定

## 影響

## 代替案
```

## 機械検証 (A6 で導入)

- Gradle カスタムタスクで以下を検証 (§5.2):
  - `id` の正規表現 (`^ADR-\d{4}$`) とファイル名整合
  - `superseded_by` / `supersedes` の相互参照が実在
  - `accepted` 以降のステータスで本文末尾の immutable 性 (git blame で改変検出は人間レビュー任せ)
  - `docs/adr/README.md` の索引行と本体 status / title の整合

## Gotchas

- **`accepted` 以降は本文を改変しない**。誤記訂正でも新 ADR を起こすか、`docs/adr/README.md` 側に正誤表を追記
- 補助 Skill / 撤回コスト低の方針は ADR 化見送り → `.claude/rules/*` で運用 (例: `roadmap.md` / `commit-message.md` 等)
- ADR 起票判断は `docs/harness/plan.md` §4.5 の判断フロー Mermaid に従う
- 関連 SPEC / Epic / Plan / ADR の双方向リンクは **frontmatter で管理**、本文に手書きしない (機械検証対象)

## 関連

- ADR 0001 (本ルールの Single Source of Truth)
- ADR 0027 (テンプレート言語 / docs 構造)
- `docs/adr/README.md` (ADR 0001-0027 索引)
- `docs/adr/template.md` (ADR 本体テンプレ)
- `docs/harness/plan.md` §4.5 (判断フロー Mermaid)
- `.claude/skills/adr-author/SKILL.md`
- `.claude/rules/{plan,epic,docs-structure}.md`
