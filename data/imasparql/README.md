---
id: data-imasparql-readme
title: data/imasparql/ ディレクトリ
status: living
last_updated: 2026-05-19
related_adrs:
  - ADR-0007
  - ADR-0014
related_specs:
  - SPEC-IMASPARQL-001-basic
---

# data/imasparql/ ディレクトリ

> **5 行以内 summary**: ローカル Fuseki container (`docker-compose.yml`) が read-only でマウントする
> RDF 初期データの配置先。本ディレクトリには `.gitkeep` と本 README のみが git 追跡され、
> RDF データファイル (`*.ttl` / `*.nq` / `*.rdf` / `*.nt`) はライセンス未確認のため `.gitignore`
> で除外する (ADR-0014 §Negative)。開発者が手動で配置し、Fuseki 管理 UI 経由で load する。

## 役割

- ローカル開発時に Apache Jena Fuseki container に投入する RDF 初期データのステージング領域
- `docker-compose.yml` の `volumes` で `/staging:ro` (read-only) としてマウントされる
- placeholder として `.gitkeep` と本 README のみが git で追跡される

## 配置するファイル

| 種別 | パターン | git 追跡 | 用途 |
|---|---|---|---|
| Turtle | `*.ttl` | 除外 (`.gitignore`) | 主要な RDF 初期データ形式 |
| N-Quads | `*.nq` | 除外 | 名前空間付き quad 形式 |
| RDF/XML | `*.rdf` | 除外 | XML シリアライゼーション |
| N-Triples | `*.nt` | 除外 | プレーン triple 形式 |
| placeholder | `.gitkeep` | 追跡 | ディレクトリ存在保証 |
| README | `README.md` | 追跡 | 本ファイル |

## データ取得手順

本 PR (PLAN-003) では **データ取得スクリプトを提供しない**。開発者は以下のいずれかの手順で
データを配置する:

1. **im@sparql 公式 RDF (`imas/imasparql` GitHub repository 等) からの取得**: ライセンス確認後、
   `*.ttl` を本ディレクトリにコピー。ライセンス確認は ADR-0014 §Negative / `docs/harness/plan.md`
   R-9 を参照 (A8 後続 Plan で seed スクリプト整備時に再確認)。
2. **ダミー RDF データの自作**: SPARQL クエリ動作確認のみが目的なら、最小限の triple を
   `sample.ttl` 等として手動作成 (一部の `imas:Idol` インスタンスのみ)。
3. **公開 endpoint からの `CONSTRUCT` クエリ抽出**: 公開 endpoint
   (https://sparql.crssnky.xyz) に `CONSTRUCT { ?s ?p ?o } WHERE { ... }` を投げて結果を
   `.ttl` で保存。倫理的負荷の観点から多量取得は避ける。

詳細手順 (Fuseki 管理 UI 経由の load / API 経由の upload) は `docs/runbooks/local-imasparql.md`
§dataset 投入手順 を参照。

## ライセンス上の注意

- im@sparql の RDF データは **個別ライセンス確認が必要** (`docs/harness/plan.md` R-9)
- 本 PR ではライセンス未確認のため RDF データ自体を git 追跡しない方針
- 商用利用 / 二次配布の場合は元データの利用規約を確認

## 関連

- ADR-0007 (im@sparql upstream-driven 同期)
- ADR-0014 (Fuseki Docker 採用)
- SPEC-IMASPARQL-001-basic (本ディレクトリの位置付け)
- `docs/runbooks/local-imasparql.md` (起動 / 投入手順)
- `.claude/rules/sparql.md` (SPARQL クエリ実装規約)
