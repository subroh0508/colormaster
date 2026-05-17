---
id: ADR-0009
title: Backend は Google Cloud Run でホストする
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

# ADR-0009: Backend は Google Cloud Run でホストする

> **5 行以内 summary**: ColorMaster の Backend (Ktor / Kotlin/JVM) は Google Cloud Run に
> ホストする。Cloud Run は request-based billing で idle 課金が発生せず、無料枠
> (月 200 万 req / 360,000 vCPU-sec / 180,000 GiB-sec) が同等の PaaS と比較して最も
> 厚いため、個人プロジェクト規模で完全無料運用が成立する。既存 Dockerfile
> (`amazoncorretto:22`) がそのまま流用でき、移行コストも最小。

## ステータス

accepted

## コンテキスト

ColorMaster Backend は Ktor + Kotlin/JVM で実装され、im@sparql から同期した
`data/idols.db` (read-only / Container 焼き込み、ADR-0010) とユーザーデータ
`users.db` (Litestream で R2 へレプリケート、ADR-0008) を扱う。配信先は個人開発の
無料運用が前提で、月数百〜数千 req 規模・cold start は許容される。Firebase は完全
廃止方針 (ADR-0011) のため Firebase Hosting / Cloud Functions は選択肢から外れる。

PaaS 業界では 2024 年後半以降、無料枠の縮小・廃止が相次いだ (Fly.io / Heroku /
Railway)。Cloudflare は 2026/4/13 に Containers を GA したが、Workers Paid plan
($5/月) が必須で完全無料運用に整合しない。一方 Google Cloud Run は 2024〜2026 を
通じ無料枠が安定して提供されており、JVM サポートも成熟している。

## 決定

Backend (Ktor / Kotlin/JVM) のホスティング先として **Google Cloud Run** を採用する。

- ランタイム: `amazoncorretto:22` ベースの Dockerfile を流用
- リージョン: `asia-northeast1` (Tokyo) を第一候補
- スケール: `min-instances=0` (idle 課金回避)、`max-instances` は無料枠内で運用
- 認証: 公開エンドポイント、リクエスト本体の Bearer (Google ID Token) を Backend で検証
- 静的配信 (wasmJs バンドル等) は Cloud Run ではなく **Cloudflare Pages** に分離 (ADR-0022)
- ユーザーデータ永続化 (Litestream バックアップ先) は **Cloudflare R2** (ADR-0022)
- Backend / 静的配信 / バックアップ先を分けたハイブリッド構成で運用する

代替候補としての **Koyeb** は ADR 本文の比較表に記録のみ留め、現時点では採用しない。

## 根拠

- **無料枠の厚さ**: Cloud Run の Free tier (月 200 万 req / 360,000 vCPU-sec /
  180,000 GiB-sec) は同価格帯 PaaS で最大級。ColorMaster の想定トラフィック
  (個人開発、月数百〜数千 req) では確実に無料枠に収まる。
- **idle 課金なし**: request-based billing により `min-instances=0` 時の保持コストが
  ゼロ。cold start (数秒) は許容できる仕様。
- **既存 Dockerfile 流用**: 現行の `amazoncorretto:22` Dockerfile が Cloud Run の
  Container Contract をそのまま満たすため、移行コストが最小。
- **JVM サポートの成熟度**: Cloud Run は Kotlin/JVM の本番運用事例が豊富で、ログ
  集約・メトリクス・トレースが Google Cloud Operations Suite と統合済み。
- **Cloudflare との組合せやすさ**: 静的配信を Cloudflare Pages、Litestream 先を R2
  に振ることで、egress 課金の発生しないハイブリッド構成が成立する (ADR-0022)。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| **Google Cloud Run (採用)** | 無料枠最大、idle 課金なし、Dockerfile 流用可、JVM 成熟 | GCP アカウントとプロジェクト設定が必要 | 全項目で最も無料運用に整合、本 ADR で採用 |
| Cloudflare Containers | 2026/4/13 GA、Kotlin/JVM 動作可、Pages / R2 と同一プラットフォーム | **Workers Paid plan $5/月 必須**で完全無料運用不可、年 $60 の固定費 | 個人プロジェクトに固定費は重い、不採用 |
| Fly.io | 軽量、グローバル分散、設定が直感的 | **無料枠廃止 (2024 後半)**、最低でも月数ドル発生 | 無料運用の前提を満たさない、不採用 |
| Render | UI が分かりやすく Git 連携が容易 | **無料 plan は sleep する**、初回リクエストで数十秒の cold start | cold start が許容範囲を超える、不採用 |
| Railway | 開発者体験が良好、Postgres 等の一体提供 | **実質有料化** (Free plan の制約が運用に耐えない) | 無料運用の前提を満たさない、不採用 |
| Koyeb | Free tier あり、Kotlin 動作実績あり | Cloud Run より無料枠が薄い、JVM サポートの実績情報が限定的 | 代替候補として記録、現時点では Cloud Run が優位 |

## 帰結 (Consequences)

### Positive

- 月額固定費ゼロで Backend が稼働可能。
- `min-instances=0` により無トラフィック時は完全に課金されない。
- 既存 Dockerfile / Gradle ビルド成果物がそのまま使用でき、移行リスクが小さい。
- Google Cloud Logging / Monitoring を Backend 観測の基盤として活用できる。
- ADR-0022 (Cloudflare Pages + R2) と組み合わせ、egress 課金が発生しない構成が
  実現できる。

### Negative / トレードオフ

- cold start による初回レスポンス遅延 (数秒) が発生する → 個人開発用途では許容。
  必要に応じて `min-instances=1` へ切替できるが、その時点で月額課金が発生する点に
  注意する。
- GCP 単一ベンダーロックインが Backend に残る → Cloudflare 側 (Pages / R2) と
  役割分担しているためフロント・バックアップ層は影響を受けない。
- Cloud Run の上限 (request timeout 60 分、メモリ最大 32 GiB) に依存する設計と
  なる → 現状の Backend ワークロードでは余裕。

### Neutral / 将来の検討事項

- トラフィック増 / SLO 要求変化に応じて `min-instances` 引き上げ、もしくは
  Cloud Run for Anthos / GKE Autopilot への移行を検討する。
- 代替候補 Koyeb は Cloud Run の無料枠が縮小された場合の退避先として継続観測する。
- リージョン (`asia-northeast1`) と Cloudflare edge との物理距離による latency は、
  実トラフィックで観測してから ADR を改訂する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 3. 外部サービスの採用または変更 (Backend ホスティング先の選定)
- [x] 4. データ永続化 / 同期戦略 / バックアップ方式 (Cloud Run + R2 のハイブリッド構成の Backend 側)
- [x] 8. 複数の代替案を比較した結果としての判断 (Cloudflare Containers / Fly.io / Render / Railway / Koyeb との比較)
- [x] 9. 元に戻すコストが高い決定 (一度本番稼働すると移行コストが大きい)
- [x] 10. 長期的な制約 (今後 1 年以上、Backend デプロイ・観測・課金に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

`.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
本 ADR がコーディング規約 / Plan で済む話 / runbook で済む話 ではなく、Backend
ホスティング先という外部サービス採用判断であることを確認した。`min-instances` 等の
個別パラメータ調整は runbook + Plan で扱う。

- [x] 確認済み

## 関連

- 関連 ADR: ADR-0008 (ユーザーデータ永続化 + Litestream)、ADR-0010 (`data/idols.db` を read-only で焼き込み)、ADR-0011 (Firebase 完全廃止 / GIS 統一)、ADR-0022 (Cloudflare Pages + R2 ハイブリッド)
- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- `.claude/rules/cloud-run-deploy.md`
- `.claude/rules/db-protection.md`
- `docs/harness/plan.md` §3.4 (ホスティング採用根拠)
