---
id: ADR-0010
title: アイドル情報マスタ SQLite をリポジトリに commit する
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

# ADR-0010: アイドル情報マスタ SQLite をリポジトリに commit する

> **5 行以内 summary**: アイドル情報マスタ (`data/idols.db` + `data/idols.json` +
> `data/.imasparql-sync-state.json`) を Git にコミットし、Container イメージに焼き込んで
> read-only でデプロイする。Litestream の対象外。利点は Cloud Run コンテナのステートレス
> 維持、im@sparql ダウン時もアプリは前回正常データで稼働可能、同期失敗を PR レビューで
> ブロック可能、予期せぬデータ消失の防止。ユーザーデータ (ADR-0008) とは保管方式を分離する。

## ステータス

accepted

## コンテキスト

ColorMaster はアイドル情報マスタ (色 / ブランド / 所属 / 名前) を外部 SPARQL endpoint
im@sparql から取得して利用する。マスタの保管方式には次の制約がある:

- アイドル情報は **read-only かつ全ユーザー共有** のデータであり、ユーザー固有データ
  (ADR-0008 の `users.db`) とは性質が異なる。
- im@sparql 公開 endpoint は個人運営で稼働率に不安があり、ランタイムで毎回問い合わせる
  と im@sparql ダウン時にアプリが機能不全になる。
- Cloud Run は ephemeral container のため、ランタイムでマスタを生成する設計だと
  コンテナ再起動のたびに同期コストが発生する。
- 同期処理に不具合があった場合 (レコード数の異常変動 / スキーマずれ等) を、運用前に
  検出できる仕組みが必要。

候補としては (a) Git commit + Container 焼込み、(b) Litestream で R2 にレプリケート、
(c) ランタイム同期 + memory cache、(d) 別途 read-only ストレージ、が挙がった。

## 決定

アイドル情報マスタを以下の構成で扱う。

- `data/idols.db` (SQLite)、`data/idols.json` (snapshot)、`data/.imasparql-sync-state.json`
  (同期 state) を **Git リポジトリにコミット** する。
- Container イメージビルド時に焼き込み、ランタイムは **read-only** で参照する。
- **Litestream の対象外** とする (ユーザーデータ `users.db` とは保管方式を分離)。
- マスタの更新は ADR-0007 の upstream-driven sync で行い、PR レビューを経て master に
  取り込む。ランタイムでの DB 書き換えは行わない。

## 根拠

- **ステートレス維持**: Cloud Run コンテナは `data/idols.db` を read-only で参照する
  だけで済み、起動時 restore / runtime fetch が一切不要。コンテナのステートレス性を
  完全に保てる。
- **im@sparql 可用性の分離**: im@sparql 公開 endpoint がダウンしていても、アプリは
  前回 master 同期時点のデータで通常通り稼働できる。可用性の境界が明確。
- **PR レビューによる安全網**: アイドル情報マスタの更新は必ず PR を経るため、レコード数
  の異常変動 / スキーマ崩れ / 不正データ混入を人間 / AI レビューでブロックできる。
- **データ消失耐性**: Git history に全更新が残るため、誤った同期も `git revert` で
  即座に戻せる。Litestream / R2 のような外部依存ゼロ。
- **ユーザーデータとの分離**: read-only マスタと書き込み頻度の高いユーザーデータを
  別保管にすることで、それぞれに最適なバックアップ戦略を選択できる。マスタは Git、
  ユーザーデータは Litestream + R2 (ADR-0008)。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Litestream で R2 にレプリケート | ユーザーデータと同方式で統一 | read-only マスタに WAL レプリケート不要、R2 token / endpoint への余分な依存 | read-only / 共有 / 不変履歴の特性に Git の方が適合 |
| ランタイム同期 + memory cache | コンテナイメージサイズ小 | im@sparql ダウン時に機能不全、cold start でクエリ実行 | 可用性とコスト両面で劣後 |
| 外部 read-only ストレージ (GCS / R2) | コンテナイメージサイズ小 | ストレージ依存、PR レビューで止められない | レビューの安全網が失われ不採用 |
| Git commit + ランタイム書き換え可 | 部分修正容易 | コンテナ間で状態不一致、ステートレス性喪失 | Cloud Run の ephemeral 性と矛盾 |

## 帰結

### Positive

- Cloud Run コンテナはステートレスを保ち、cold start が高速。
- im@sparql 公開 endpoint がダウンしても、アプリは前回正常データで稼働継続可能。
- 同期失敗 / 異常データを PR レビューで構造的にブロックできる。
- 過去の全マスタ状態が Git history として保存され、`git revert` で即座にロールバック可能。

### Negative / トレードオフ

- `data/idols.db` のサイズが Git リポジトリに加算される (現状数 MB 想定、許容範囲)。
- マスタ更新のたびに PR と Container イメージリビルドが発生する (ADR-0007 の同期 PR
  経由で自動化済み)。

### Neutral / 将来の検討事項

- マスタサイズが数百 MB クラスに肥大化した場合、Git LFS や別配信方式を再評価する。
- アイドル情報以外の read-only マスタ (例: 楽曲データ) を追加する際は、同方式を踏襲できる。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 4. データ永続化 / 同期戦略 / バックアップ方式 (read-only マスタの保管方式の中核)
- [x] 8. 複数の代替案を比較した結果としての判断 (Git / Litestream / ランタイム同期 / 外部 read-only を比較)
- [x] 9. 元に戻すコストが高い決定 (一度方式を採用すると Container ビルド / 同期 workflow / レビュー体制が依存する)
- [x] 10. 長期的な制約 (今後 1 年以上、アイドル情報マスタ保管の中核)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR がデータ保管方式の中核方針として
      ADR 化すべき (`runbook` / Plan で済む話ではない) ことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0007 (im@sparql upstream-driven 同期、本 ADR の更新トリガ)
- ADR-0014 (im@sparql のローカル Fuseki 環境、開発時の同期テスト)
- ADR-0009 (Backend ホスティング: Cloud Run、焼き込み先)
- ADR-0008 (ユーザーデータの Litestream 保管、本 ADR とは分離)
- `docs/harness/plan.md` §3.2 / §3.3
- `.claude/rules/{db-protection,sqlite-data-file}.md`
