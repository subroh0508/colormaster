---
id: runbook-local-imasparql
title: im@sparql ローカル Fuseki Docker 環境
status: living
last_updated: 2026-05-19
related_adrs:
  - ADR-0007
  - ADR-0014
  - ADR-0021
related_specs:
  - SPEC-IMASPARQL-001-basic
---

# im@sparql ローカル Fuseki Docker 環境

> **5 行以内 summary**: 公開 im@sparql endpoint (https://sparql.crssnky.xyz) 非依存で SPARQL
> クエリを試行できるローカル Fuseki 環境の起動 / 停止 / 接続確認 / トラブルシュート手順。
> `docker-compose.yml` (project root) と `data/imasparql/` (RDF 初期データ配置先) と
> 本 runbook の 3 点で運用が完結する (REQ-001 / SPEC-IMASPARQL-001-basic / PLAN-003)。
> Backend / CLI の endpoint 切替 / Testcontainers 統合は本 runbook のスコープ外。

## 1. 前提環境

| ツール | バージョン (要求) | 確認コマンド |
|---|---|---|
| Docker Desktop (または互換ランタイム) | 24.0 以降推奨 | `docker --version` |
| docker compose | v2 系 (`docker compose` サブコマンド形式) | `docker compose version` |
| 空きポート | `127.0.0.1:3030` | `lsof -i :3030` (macOS / Linux) |

詳細な開発環境一覧は `docs/runbooks/local-development.md` §1 を参照。

## 2. 初回セットアップ

1. リポジトリ root に移動

   ```bash
   cd <colormaster repo root>
   ```

2. `.env` ファイル作成 (`FUSEKI_ADMIN_PASSWORD` 設定)

   ```bash
   cp .env.example .env
   # エディタで .env を開き FUSEKI_ADMIN_PASSWORD を任意の十分長い文字列に変更
   ```

   - `.env` は `.gitignore` 対象、個人マシン外に出さない (ADR-0021 / `.claude/rules/secrets.md`)
   - 本番 / stage の admin password を絶対に流用しない (`@example.com` ドメインの fixture 規約と同等の隔離)

3. RDF 初期データ配置 (任意、空でも Fuseki は起動)

   ```bash
   # 例: ダミー RDF を data/imasparql/sample.ttl に配置
   # *.ttl / *.nq / *.rdf / *.nt は .gitignore 対象 (ライセンス未確認、PLAN-003 / ADR-0014)
   ```

   詳細は `data/imasparql/README.md` を参照 (取得手順 / ライセンス注意)。

## 3. 起動

```bash
docker compose up -d fuseki
```

- 初回起動は image pull で数十秒〜数分かかる (image: `stain/jena-fuseki:4.10.0` を pin)
- 2 回目以降は数秒で起動完了
- 起動完了は `docker compose ps` の `STATUS` が `Up (healthy)` になるまで

## 4. 接続確認

### 4.1. 管理 UI

ブラウザで以下を開く:

- `http://localhost:3030/`
- 管理者ログインは user `admin` + `FUSEKI_ADMIN_PASSWORD` で行う

### 4.2. SPARQL クエリ (curl)

```bash
curl -G http://localhost:3030/imasparql/query \
  --data-urlencode 'query=PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 5' \
  --header 'Accept: application/sparql-results+json'
```

- dataset 名は `docker-compose.yml` の `FUSEKI_DATASET_1` で指定した値 (default: `imasparql`)
- データ未投入なら結果は 0 件、Fuseki 自体の応答確認に利用可能
- SPARQL prefix / 規約は `.claude/rules/sparql.md` を参照

### 4.3. ping endpoint (ヘルスチェック)

```bash
curl -fsS http://localhost:3030/$/ping
```

- 200 OK で `pong\n` 相当の応答 (Fuseki version により差異あり)

## 5. dataset 投入手順

### 5.1. Fuseki 管理 UI 経由 (推奨)

1. `http://localhost:3030/` にログイン
2. `manage datasets` で `imasparql` dataset を選択
3. `upload data` から `*.ttl` ファイルを選択して投入

### 5.2. Fuseki Admin API 経由 (CLI)

```bash
curl -X POST http://localhost:3030/imasparql/data \
  -u admin:"$FUSEKI_ADMIN_PASSWORD" \
  -H 'Content-Type: text/turtle' \
  --data-binary @data/imasparql/sample.ttl
```

- `Content-Type` は形式に応じて変更 (`text/turtle` / `application/n-quads` / `application/rdf+xml`)
- `data/imasparql/` 配下は container 内では `/staging` にマウント済 (read-only)、ホスト経由 POST と Fuseki container 内 load の両方が可能

## 6. 停止

```bash
docker compose down
```

- in-memory dataset (default) は停止で揮発、再起動時は再投入が必要
- TDB2 永続化を有効化していれば `data/imasparql/tdb2/` 配下にデータが残る (TDB2 設定は本 runbook §8 オプションを参照)

## 7. トラブルシュート

| 症状 | 原因 | 対処 |
|---|---|---|
| `docker compose up` でポート競合 | `127.0.0.1:3030` が他プロセス占有 | `lsof -i :3030` で原因 PID を確認、競合プロセスを停止するか `docker-compose.yml` の `ports` を `127.0.0.1:3031:3030` 等にオーバーライドする `docker-compose.override.yml` を作成 (個人ローカル、`.gitignore` 対象推奨) |
| `FUSEKI_ADMIN_PASSWORD must be set` エラー | `.env` 未作成 or 未読込 | `.env.example` をコピーして `.env` を作成、`docker compose --env-file .env up -d fuseki` 形式の明示指定でも可 |
| image pull 失敗 (DNS / proxy) | ネットワーク障害 / corporate proxy | `docker logout && docker login` で資格情報更新 / `~/.docker/config.json` の proxy 設定確認 / 一時的にフォールバックして公開 endpoint (https://sparql.crssnky.xyz) で代替 |
| SPARQL クエリで 401 Unauthorized | 管理 API へ未認証アクセス | `query` endpoint は default 公開、`update` / dataset 管理 API は admin 認証必須。`curl -u admin:$FUSEKI_ADMIN_PASSWORD` 形式で再試行 |
| dataset 未作成 (404) | `FUSEKI_DATASET_1` 未設定 / image 起動オプション未対応 | `docker compose logs fuseki` で起動ログ確認、必要なら管理 UI から手動 dataset 作成 |
| データ load 後もクエリ結果が空 | dataset 名 / graph 名のミスマッチ | `SELECT ?g (COUNT(*) AS ?c) WHERE { GRAPH ?g { ?s ?p ?o } } GROUP BY ?g` で graph 一覧確認、`FROM` / `GRAPH` 句で正しい graph を指定 |
| 起動後すぐ unhealthy 表示 | `start_period` 未経過 | `docker-compose.yml` の `start_period: 30s` を待つ。30 秒経過後も unhealthy なら `docker compose logs fuseki` で原因確認 |

## 8. オプション設定

### 8.1. TDB2 永続化 (将来の Plan で本格化)

本 PR (PLAN-003) では in-memory dataset を default としているが、再起動間でデータを保持したい場合は
`docker-compose.override.yml` (個人ローカル、`.gitignore` 推奨) で以下のように上書きする
(具体的な YAML は Fuseki image の TDB2 オプションに応じて記述):

- volume を `./data/imasparql/tdb2:/fuseki/databases:rw` に変更
- `FUSEKI_DATASET_1` を TDB2 用フラグ付きで指定

TDB2 ディレクトリ (`data/imasparql/tdb2/`) は `.gitignore` で除外済 (本 PR で追加)。

### 8.2. 公開 endpoint との切替

`backend/cli/src/main/kotlin/net/subroh0508/colormaster/backend/cli/imasparql/Constants.kt:3` で
hardcode された `HOSTNAME = "sparql.crssnky.xyz"` を環境変数化する作業は **本 runbook のスコープ外**。
A8 後続 Plan で `IMASPARQL_ENDPOINT_URL` 等の環境変数経由の切替を実装する (ADR-0014 §決定)。

## 9. CI 上での起動可否

本 PR (PLAN-003) では CI で Fuseki を起動しない方針:

- 現状は ColorMaster の CI (`.github/workflows/`) に Backend integration test が未配置
- A8 後続 Plan で Testcontainers + Fuseki integration test を本格化する際に、CI でも
  `docker compose up` 相当を起動する方針を再評価

## 10. 関連リンク

- ADR-0007 (im@sparql upstream-driven 同期)
- ADR-0014 (Fuseki Docker 採用、本 runbook の意思決定根拠)
- ADR-0021 (Secrets 管理、admin password 環境変数化)
- REQ-001 (`docs/requirements/REQ-001-imasparql-local-docker.md`)
- SPEC-IMASPARQL-001-basic (`docs/specifications/basic/SPEC-IMASPARQL-001-basic.md`)
- PLAN-003 (`docs/plans/PLAN-003-a8-imasparql-docker.md`)
- `data/imasparql/README.md` (RDF データ取得 / ライセンス注意)
- `.claude/rules/sparql.md` (SPARQL クエリ実装規約)
- `.claude/rules/secrets.md` (`.env` / `.env.example` 規約)
- `docs/runbooks/local-development.md` §6 (本 runbook へのリンク元)
- Apache Jena Fuseki: https://jena.apache.org/documentation/fuseki2/
- stain/jena-fuseki Docker image: https://hub.docker.com/r/stain/jena-fuseki
