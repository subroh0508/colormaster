---
id: rules-adr
title: ADR 起票基準と書式
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4.5
related_adrs: [ADR-0001]
---

# adr.md — ADR 起票基準と書式

> Michael Nygard の原則 ("Architecturally Significant Decisions" のみ記録、
> "Any Decision Records" ではない) + AWS / Microsoft / Google / Martin Fowler の
> ADR ガイドラインに準拠。詳細は ADR 0001 を Single Source of Truth とする。

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
- ファイル名: `{NNNN}-{kebab-case-title}.md`
- タイトル: 簡潔・現在形・断定的
- ステータス: `proposed` → `accepted` → `deprecated` | `superseded by ADR-NNNN` (MADR 4 状態)
- `accepted` 以降は immutable。変更時は新 ADR を起こし旧 ADR を `Superseded by` でリンク
- 採番欠番は実装前なら整理可、運用後は番号維持で `withdrawn` を許容
- **言語は日本語** (ADR 0027)

## 関連

- ADR 0001 (本ルールの Single Source of Truth)
- `docs/adr/template.md`
- `docs/harness/plan.md` §4.5 (判断フロー Mermaid)
