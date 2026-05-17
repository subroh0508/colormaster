---
id: ADR-0014
title: im@sparql のローカル環境を Fuseki Docker で構築する
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

# ADR-0014: im@sparql のローカル環境を Fuseki Docker で構築する

> **5 行以内 summary**: Backend integration test とローカル開発を im@sparql 公開
> endpoint 依存から切り離すため、Apache Jena Fuseki Docker でローカル SPARQL endpoint を
> 立ち上げる。`docker-compose.yml` と RDF 初期データ投入スクリプト、Testcontainers
> 統合の規約を整備し、`docs/runbooks/local-imasparql.md` で運用手順を定める。
> 本格実装は Phase A の A8。

## ステータス

accepted

## コンテキスト

ColorMaster は im@sparql の公開 SPARQL endpoint からアイドル情報マスタを同期する
(ADR-0007 / ADR-0010)。Backend の integration test とローカル開発で公開 endpoint を
直接叩く構成には次の問題がある:

- im@sparql 公開 endpoint は個人運営のため、稼働率と応答速度に揺れがある。
  test が外部稼働状況に依存して flaky 化する。
- ローカル開発で大量の SPARQL クエリを試行すると、公開 endpoint に余分な負荷を
  かける。
- オフライン環境 (移動中等) で integration test を回せない。
- test fixture を im@sparql スキーマに固定したい場合、公開 endpoint のスキーマ変更が
  test を破壊しうる。

候補としては (a) Apache Jena Fuseki Docker、(b) im@sparql 公開 endpoint 直接利用、
(c) インメモリ TripleStore (RDF4J 等) を test 内で起動、が挙がった。

## 決定

ローカル SPARQL endpoint を以下の構成で構築する。

- **Apache Jena Fuseki** の公式 Docker イメージを採用し、`docker-compose.yml` で
  起動できるようにする。
- 起動時に RDF 初期データ (im@sparql の主要 dataset の subset または full snapshot)
  を投入するスクリプトを `scripts/seed-imasparql.sh` 等で用意する。
- Backend の integration test は **Testcontainers** を経由して Fuseki コンテナを
  起動し、test 内から `http://localhost:<port>/sparql` 形式でアクセスする。
- ローカル開発者向け運用手順は `docs/runbooks/local-imasparql.md` に整備する
  (起動 / 停止 / 初期データ再投入 / 公開 endpoint との切替方法)。
- 本格実装は **Phase A の A8** で行う (`docs/harness/plan.md` §6.2)。
- 公開 endpoint との切替は環境変数 (例: `IMASPARQL_ENDPOINT_URL`) で行い、
  default はローカル Fuseki にする。

## 根拠

- **test の安定化**: 公開 endpoint の稼働揺れから完全に切り離せるため、CI / ローカル
  ともに integration test が deterministic になる。
- **Apache Jena Fuseki の成熟度**: Fuseki は SPARQL 1.1 完全準拠の公式実装で、
  Docker イメージも公式提供されている。im@sparql 本家も Jena 系 stack のため、
  クエリの方言差異が最小。
- **Testcontainers との親和性**: Kotlin/JVM の test framework から Testcontainers
  経由でコンテナライフサイクルを管理でき、test 並列実行も問題ない。
- **オフライン開発**: ローカル Fuseki により、移動中などオフライン環境でも開発と test
  が継続可能。
- **公開 endpoint への負荷軽減**: 開発時の試行錯誤クエリを公開 endpoint に飛ばさず、
  個人運営の im@sparql に負担をかけない倫理的姿勢。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| im@sparql 公開 endpoint 直接利用 | セットアップ不要 | test が flaky 化、稼働依存、オフライン不可、外部負荷 | 上記制約で実用不可 |
| インメモリ TripleStore (RDF4J 等) を test 内で起動 | Docker 不要、起動高速 | im@sparql の dataset / 方言再現性に難、prod と差異が大 | 再現性とスキーマ互換性で劣後 |
| Mock HTTP server で SPARQL レスポンスを返す | test 単独で完結 | SPARQL クエリ実行ロジックを test できない | integration test の意義を損なう |
| クラウド上に共有 Fuseki を立ち上げ | チーム全体で共有可能 | 個人プロジェクトに過剰、コスト発生 | 個人プロジェクト規模に不適合 |

## 帰結

### Positive

- integration test が公開 endpoint 状況から独立し、deterministic に動作する。
- ローカル開発でオフライン作業可能、クエリ試行錯誤の自由度が高まる。
- 公開 endpoint への余分なアクセスを構造的に避けられる。
- Testcontainers 経由で test 並列実行 / CI 統合が容易。

### Negative / トレードオフ

- 開発者は Docker 環境のセットアップが必要 (`docs/runbooks/local-imasparql.md` で
  手順整備)。
- ローカル Fuseki と公開 endpoint のデータ鮮度に乖離が出るため、ADR-0007 の同期 PR
  経由で定期的に snapshot を更新する運用が必要。
- Fuseki Docker イメージのバージョン管理が増える (Renovate で自動 PR、A9 で本格化)。

### Neutral / 将来の検討事項

- 初期データ投入スクリプトのフォーマット (`.ttl` / `.nq` / SPARQL UPDATE 等) は
  Phase A8 の runbook で確定。
- CI で Fuseki コンテナを並列起動する際のリソース使用量を計測し、必要に応じて
  test profile を分割する。
- 将来 im@sparql の方言差異が拡大した場合は、custom function などの設定を Fuseki
  Docker に追加する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 3. 外部サービスの採用または変更 (im@sparql 公開 endpoint を test/開発から切り離し)
- [x] 5. テスト戦略・品質指標の中核方針 (integration test の安定化と Testcontainers 統合)
- [x] 8. 複数の代替案を比較した結果としての判断 (公開 endpoint / インメモリ / Mock / 共有 Fuseki と比較)
- [x] 10. 長期的な制約 (今後 1 年以上、test と開発の SPARQL endpoint 構成を規定)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR が単なるローカル開発手順
      (`docs/runbooks/local-imasparql.md` で済む話) ではなく、test 戦略と開発環境
      の中核方針として ADR 化すべきことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0007 (im@sparql upstream-driven 同期、本 ADR のローカル test 対象)
- ADR-0010 (アイドル情報マスタ SQLite、ローカル同期 test の入力)
- `docs/harness/plan.md` §6.2 (Phase A8)
- `docs/runbooks/local-imasparql.md` (運用手順、Phase A8 で整備)
- `.claude/rules/kotlin-test.md`
