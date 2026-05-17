---
id: glossary
title: ドメイン用語集
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0002
  - ADR-0006
  - ADR-0008
  - ADR-0011
  - ADR-0013
  - ADR-0015
  - ADR-0016
  - ADR-0022
  - ADR-0023
  - ADR-0027
---

# ドメイン用語集

> **5 行以内 summary**: ColorMaster が扱うアイドルマスターのドメイン用語、im@sparql / RDF
> 関連の技術用語、Kotlin Multiplatform / Compose Multiplatform 固有用語、テスト指標用語を
> 集約する。日本語 + 英訳 + 関連リンクを併記し、AI / 人間レビューの認知負荷を下げる。
> 個別 SPEC / Skill から `docs/glossary.md` を参照することで用語のブレを防止。Phase C 持ち越し
> は表末の TODO に固定。

## 1. アイドルマスター ドメイン用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| アイドル | Idol | 楽曲を歌い踊るキャラクター。本サービスの主要エンティティ | `core/model/`, `core/data/` |
| ブランド | Brand | アイドルマスターのブランド (765AS / CG / ML / SS / SC 等) | `core/model/` Brand 型 |
| 担当 | Tantou (Producer's Top Idol) | プレイヤーが特に応援するアイドル。`feature/myidols` の保存対象 | `core/features/myidols/` |
| 推し | Oshi | 「担当」のカジュアル表現。本サービスでは「お気に入り」と同義として扱う | — |
| イメージカラー | Image Color | 各アイドルに割り当てられた代表色 (16 進カラーコード) | `core/model/` ColorPalette 型 |
| 楽曲 | Song | アイドルが歌唱する楽曲。本サービスでは検索対象に含めない (現状) | C9 同期で追加検討 |
| ユニット | Unit | 複数アイドルで構成されるグループ (シャイニーカラーズの illumination STARS 等) | C9 同期で追加検討 |
| イベント | Event | ライブ / リリース / 周年などの時系列イベント | C9 同期で追加検討 |

## 2. im@sparql / RDF / SPARQL 関連

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| im@sparql | im@sparql | アイドルマスター情報の RDF/SPARQL エンドポイント (公開) | https://sparql.crssnky.xyz/imas/ |
| RDF | Resource Description Framework | im@sparql のデータ表現形式 (W3C 標準) | W3C 標準 |
| SPARQL | SPARQL | RDF クエリ言語 (W3C 標準)。`core/network/imasparql/` から SELECT 発行 | W3C 標準 |
| SPARQL prefix | — | `imas:` `imasrdf:` `schema:` `rdfs:` 等の名前空間プレフィックス | `core/network/imasparql/` |
| Apache Jena Fuseki | Fuseki | im@sparql ローカル Docker 用の SPARQL サーバ実装 | A8 で `docs/runbooks/local-imasparql.md` 本格化 |
| upstream-driven sync | — | `imas/imasparql` の SHA 監視で日次差分を取り込む同期戦略 | ADR 0007 |
| `idols.db` | — | アイドル情報マスタ (read-only、リポジトリ commit + イメージ焼込み可) | ADR 0008 / 0010 |
| `users.db` | — | ユーザーデータ (commit / イメージ焼込み禁止、R2 Litestream replicate) | ADR 0008 / 0020 |

## 3. Kotlin Multiplatform / Compose Multiplatform 用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| KMP | Kotlin Multiplatform | プラットフォーム横断のコード共有機構 | ADR 0002 / 0003 |
| CMP | Compose Multiplatform | JetBrains 提供の Compose 実装 (Android / iOS / Desktop / Web) | ADR 0002 |
| `commonMain` | — | 全プラットフォーム共通の Kotlin source set | `core/*/src/commonMain/` |
| `androidMain` / `jsMain` / `iosMain` | — | プラットフォーム固有の Kotlin source set | `core/*/src/{androidMain,jsMain}/` |
| `commonTest` | — | 全プラットフォーム共通のテスト source set | `core/*/src/commonTest/` |
| `expect` / `actual` | — | KMP の宣言/実装分離キーワード。`commonMain` で `expect`、各プラットフォームで `actual` | KMP 公式 |
| `composeResources` | — | CMP リソース管理機構 (文字列 / 画像 / フォント) | ADR 0006 |
| `@Preview` | — | Composable のデザインタイムプレビューアノテーション | `.claude/rules/composable.md` |
| `Modifier` | — | Compose の UI 装飾チェイン | Compose 公式 |
| `MutableStateFlow` | — | Coroutine ベースの状態管理プリミティブ (ViewModel 内で UiState 保持) | `.claude/rules/viewmodel.md` |

## 4. 内部実装の専門用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| UiState | — | 画面状態を不変データクラスで表現する型 (Loading / Loaded / Error 等) | `.claude/rules/ui-state.md` |
| UiAction | — | UI から ViewModel に送信されるユーザーアクション (sealed interface) | `.claude/rules/ui-state.md` |
| ViewModel | — | UiState 保持 + UiAction 処理 + Repository 呼び出しを担う層 (`androidx.lifecycle.ViewModel` ベース) | `.claude/rules/viewmodel.md` |
| Repository | — | データソース (Network / DB) の抽象化層。`core/data/**/*Repository.kt` | `.claude/rules/repository.md` |
| Route | — | Navigation 3 の画面識別子 (sealed class / type-safe) | `.claude/rules/navigation.md` |
| NavGraph | — | Navigation 3 のグラフ定義 | `.claude/rules/navigation.md` |
| Composable | — | `@Composable` 関数。Screen / Component / Atom 階層 | `.claude/rules/composable.md` |
| Result 型 | — | 成功 / 失敗を sealed で表す内部ヘルパー (`kotlin.Result` ベース) | `.claude/rules/error-handling.md` |

## 5. テスト / 品質指標用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| Line / Branch coverage | — | 行・分岐網羅率 (指標 A)。Kover で計測 | ADR 0013 / `.claude/rules/coverage-100.md` |
| Spec coverage | — | 仕様網羅率 (指標 B)。`@Spec("SPEC-NNN-N")` annotation + Konsist で検証 | ADR 0016 / `.claude/rules/spec-traceability.md` |
| Mutation score | — | 変異テストスコア (指標 C)。PITest で計測 (JVM target のみ、ゲートしない) | ADR 0015 / `.claude/rules/mutation-testing.md` |
| Konsist | — | Kotlin source 構造を検証する Lint ツール | A7 で導入 |
| Roborazzi baseline | — | Visual regression テストの基準画像 (mobile/desktop × Light/Dark の 4 パターン) | ADR 0023 / `.claude/rules/ui-snapshot.md` |
| `@Spec` annotation | — | テスト関数と SPEC-ID を紐付けるカスタムアノテーション (A7 で導入) | ADR 0016 |
| paired-class test | — | プロダクトコード 1 ファイルにつき同名 Test ファイルを 1 つ | `.claude/rules/test-paired-class.md` |
| ハーネス三層検証 | — | (A) coverage / (B) spec / (C) mutation の三層で多層検証する設計 | ADR 0013 / 0015 / 0016 |

## 6. インフラ / 外部サービス用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| GIS | Google Identity Services | 認証統一プロバイダ (Firebase Auth から移行) | ADR 0011 |
| Litestream | Litestream | SQLite を WAL 単位で S3 互換ストレージに replicate するツール | ADR 0008 |
| R2 | Cloudflare R2 | S3 互換オブジェクトストレージ (Litestream バックアップ先) | ADR 0022 |
| Cloud Run | Cloud Run | Google Cloud の serverless container 実行基盤 (Backend ホスト) | ADR 0009 |
| Cloudflare Pages | Cloudflare Pages | 静的サイト + Edge Functions ホスティング (フロント配信) | ADR 0022 |
| Secret Manager | Google Cloud Secret Manager | Cloud Run service account 経由で取得する secrets ストア | ADR 0021 |
| MCP | Model Context Protocol | Claude Code が外部システム (IDE / docs / Cloudflare) を呼び出す機構 | ADR 0024 |
| Context7 | Context7 MCP | バージョン固有のライブラリ docs を LLM に注入する MCP サーバ | ADR 0024 |

## 7. ハーネス固有用語

| 用語 | 英訳 / 表記 | 説明 | 関連 |
|---|---|---|---|
| Skill | — | ハーネスの責務単位 (`.claude/skills/<name>/SKILL.md`) | ADR 0025 |
| Rule | — | コーディング / プロセス / 命名 / セキュリティ規約 (`.claude/rules/<name>.md`) | `.claude/rules/rules-index.md` |
| Plan | — | 単一 PR の取り組み (`docs/plans/PLAN-NNN-*.md`) | `.claude/rules/plan.md` (A2-2 で本格化) |
| Epic | — | 複数 PR の取り組み (`docs/epics/EPIC-NNN-<slug>/`) | `.claude/rules/epic.md` (A2-2 で本格化) |
| ADR | Architecture Decision Record | 重要なアーキテクチャ決定 (`docs/adr/ADR-NNNN-*.md`) | ADR 0001 |
| Learning | — | PR レトロスペクティブ出力 (`docs/harness/learnings/YYYY-MM-DD-pr-N.md`) | `.claude/rules/retrospective-format.md` |
| harness-meta / harness-evolution | — | ハーネス自己進化の二系統 (内部 KPT / 外部研究) | ADR 0026 |

## Phase C 持ち越し (各機能実装で本格化)

| 持ち越し項目 | 持ち越し先フェーズ | 理由 |
|---|---|---|
| 楽曲 / ユニット / イベント系の im@sparql 用語 (具体エントリ名) | C9 (im@sparql 同期実装) | 同期実装で実際に扱うエンティティ確定後に追加 |
| `feature/` 配下の各画面固有用語 (Home / Search / MyIdols / Preview / Settings) | C3 (feature/ モジュール新設) | 画面責務確定後に固有用語を追記 |
| デザイントークン用語 (semantic role / color role / typography scale) | A10 (DESIGN.md 本格生成) | Stitch の 3 階層構造に合わせて命名規約と連動 |
| Backend 固有用語 (Ktor route / Pipeline / Plugin) | C5 (Backend 本格化) | Ktor 3.1.3 の DSL 用語をルーティング設計後に追記 |

## 関連

- `docs/harness/plan.md` §4
- `docs/codebase-map.md` (パス → 責務対応表)
- `docs/README.md` (AI 用エントリポイント)
- `.claude/rules/rules-index.md`
- im@sparql 公式: https://sparql.crssnky.xyz/imas/
