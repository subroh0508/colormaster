---
id: glossary
title: ドメイン用語集
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4
---

# ドメイン用語集

> **5 行以内 summary**: ColorMaster が扱うアイドルマスターのドメイン用語と im@sparql / RDF
> 関連の技術用語を集約する。日本語 + 英訳 + 関連リンクを併記して AI / 人間レビューの
> 認知負荷を下げる。本格拡充は A2 で実施。

## 用語表 (骨格)

| 用語 | 英訳 | 説明 | 関連 |
|---|---|---|---|
| アイドル | Idol | 楽曲を歌い踊るキャラクター。本サービスの主要エンティティ | `core/data/Idol*.kt` |
| ブランド | Brand | アイドルマスターのブランド (765AS / CG / ML / SS / SC 等) | `core/data/Brand*.kt` |
| 担当 | Tantou | プレイヤーが特に応援するアイドル | `feature/myidols` |
| 推し | Oshi | 「担当」のカジュアル表現。本サービスでは「お気に入り」と同義 | — |
| イメージカラー | Image Color | 各アイドルに割り当てられた代表色 (16 進カラーコード) | `core/data/ColorPalette*.kt` |
| im@sparql | im@sparql | アイドルマスター情報の RDF/SPARQL エンドポイント | https://sparql.crssnky.xyz/imas/ |
| RDF | Resource Description Framework | im@sparql のデータ表現形式 | W3C 標準 |
| SPARQL | SPARQL | RDF クエリ言語 | W3C 標準 |
| SPARQL prefix | — | `imas:` `imasrdf:` 等の名前空間プレフィックス | `core/data/sparql/*.rq` |
| Apache Jena Fuseki | Fuseki | im@sparql ローカル Docker 用の SPARQL サーバ | A8 で導入 |
| GIS | Google Identity Services | 認証統一プロバイダ (Firebase Auth から移行) | ADR 0011 |
| Litestream | Litestream | SQLite を WAL 単位で S3 互換ストレージに replicate するツール | ADR 0008 |
| R2 | Cloudflare R2 | S3 互換オブジェクトストレージ (Litestream バックアップ先) | ADR 0022 |
| Cloud Run | Cloud Run | Google Cloud の serverless container 実行基盤 | ADR 0009 |
| Cloudflare Pages | Cloudflare Pages | 静的サイト + Edge Functions ホスティング | ADR 0022 |
| upstream-driven sync | — | `imas/imasparql` の SHA 監視で日次差分を取り込む同期戦略 | ADR 0007 |

## A2 での本格拡充項目 (TODO)

- 楽曲・ユニット関連の用語 (シャイニーカラーズ / シャニマス / アイカツ等の隣接ドメインとの区別含む)
- 内部実装の専門用語 (UiState / UiAction / Repository / Route / NavGraph)
- Compose Multiplatform 固有用語 (composeResources / @Preview / actual/expect)
- テスト用語 (Spec coverage / Mutation testing / Roborazzi baseline)

## 関連

- `docs/harness/plan.md` §4
- `docs/codebase-map.md` (パス → 責務対応表)
- im@sparql 公式: https://sparql.crssnky.xyz/imas/
