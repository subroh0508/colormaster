---
id: rules-sparql
title: SPARQL クエリ実装規約 (im@sparql)
status: stable
last_updated: 2026-05-17
paths:
  - "core/network/imasparql/**/*.kt"
  - "**/sparql/**/*.sparql"
  - "**/sparql/**/*.rq"
related_adrs:
  - ADR-0007
  - ADR-0014
---

# sparql.md — SPARQL クエリ実装規約 (im@sparql)

> アイドル情報 (THE iDOLM@STER ドメイン) の取得元として **im@sparql** (RDF / SPARQL endpoint) を使用 (ADR 0007 / 0014)。
> 本 rule は SPARQL クエリの prefix 規約・命名・テスト方針を規定する。
> ローカル開発は Fuseki Docker、本番は外部 im@sparql ホスティング (`docs/runbooks/sync-imasparql.md`)。

## prefix 規約

```sparql
PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
PREFIX schema: <https://schema.org/>
PREFIX imas:   <https://sparql.crssnky.xyz/imasrdf/RDFs/detail/>
PREFIX imass:  <https://sparql.crssnky.xyz/imasrdf/URIs/imas-schema.ttl#>
```

- 必須 prefix: `rdf` / `rdfs` / `schema` / `imas` / `imass`
- 追加 prefix が必要な場合は **ファイル先頭にまとめて宣言**、クエリ内に inline しない
- prefix URI は **im@sparql 公式定義に従う** (https://im-sparql.com/)、変更時は ADR 起票

## クエリ命名

```sparql
# search_idols_by_name.sparql
PREFIX ...

SELECT ?idol ?name ?nameKana ?brand ?color
WHERE {
    ?idol a imas:Idol ;
          schema:name ?name ;
          imass:nameKana ?nameKana ;
          imas:Brand ?brand ;
          imas:Color ?color .
    FILTER(CONTAINS(?nameKana, ?query))
}
ORDER BY ?nameKana
LIMIT 50
```

- ファイル名: **snake_case + .sparql / .rq** (`search_idols_by_name.sparql`)
- 配置: `core/network/imasparql/src/commonMain/resources/sparql/*.sparql`
- 1 ファイル 1 query を原則 (再利用性 / テスト容易性)

## SPARQL ベストプラクティス

- **SELECT 句で variable 名を明示** (`SELECT *` 禁止、結果型が変わる)
- **OPTIONAL** で必須 / 任意プロパティを区別
- **LIMIT 必須** (im@sparql endpoint の負荷軽減、デフォルト 50)
- **FILTER は最後** (絞り込み効率)、`?var = "value"` より `STR(?var) = "value"` で型不一致を回避
- **DISTINCT は必要時のみ** (高コスト)

## Kotlin からの呼び出し (Ktor)

```kotlin
class ImasparqlClient(private val httpClient: HttpClient) {

    suspend fun searchByName(query: String): List<IdolDto> {
        val sparql = loadSparqlResource("sparql/search_idols_by_name.sparql")
            .replace(":query", "\"$query\"")  // パラメータ置換

        val response = httpClient.get("$baseUrl/query") {
            parameter("query", sparql)
            header(HttpHeaders.Accept, "application/sparql-results+json")
        }
        return response.body<SparqlResultsDto>().toIdolDtos()
    }
}
```

- SPARQL 結果は **SPARQL 1.1 Results JSON** で受信 (`application/sparql-results+json`)
- パラメータは **文字列置換** (`SPARQL injection リスクあり`、エスケープ規約は本 rule §セキュリティ参照)
- Endpoint URL は `BuildKonfig.IMASPARQL_BASE_URL` から取得 (`network-client.md` 参照)

## セキュリティ (SPARQL injection 対策)

- ユーザー入力 (`query`) を SPARQL クエリに埋め込む際は **escape 必須**:
  - `"` → `\"`
  - `\` → `\\`
  - 改行 / タブ文字を除去
- 可能なら **prepared query** (im@sparql エンドポイント側の機能) を利用
- 不正な SPARQL syntax は endpoint 側で reject されるが、過度な負荷 (re-evaluation loop) を誘発する DoS 攻撃に注意 → `LIMIT` 必須

## テスト方針

- **ローカル開発**: Fuseki Docker (`docs/runbooks/local-imasparql.md`、A2-4 で本格化) を起動して fixtures データで実 SPARQL 実行
- **CI**: MockEngine (Ktor) で SPARQL Results JSON を mock 返却、Client の結果 mapping のみ検証
- 統合テスト (実 endpoint 叩く) は **手動 / 同期 PR の Renovate 確認時** に限定

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `core/network/imasparql/src/commonMain/resources/sparql/*.sparql` の prefix が必須セットを含む
  - `LIMIT` 句が必須クエリ (SELECT) に存在
  - parameter 置換用 placeholder (`:query` 等) が Kotlin source 側と整合

## Gotchas

- **im@sparql endpoint の rate limit** に注意 (1 req/sec 程度を推奨)、`core/network/imasparql` で client-side rate limiter (`Mutex` + delay) を実装
- **SPARQL Results JSON の `value` 型は string** だが、`type: "uri"` / `type: "literal"` の区別がある。mapping で考慮
- **`OPTIONAL` 句が深くネストすると遅い**。フラットに書く
- 同期 PR (アイドル情報の自動取り込み) のクエリは `sync-job.md` 規約に従う、結果は `idols.db` に commit

## 関連

- ADR 0007 (im@sparql upstream-driven 同期)
- ADR 0014 (im@sparql ローカル Fuseki)
- im@sparql: https://sparql.crssnky.xyz/
- SPARQL 1.1: https://www.w3.org/TR/sparql11-query/
- `.claude/rules/{network-client,sync-job,sqlite-data-file}.md`
- `docs/runbooks/local-imasparql.md` / `docs/runbooks/sync-imasparql.md`
