---
id: ADR-0022
title: 静的配信を Cloudflare Pages、バックアップ先を R2 にする
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

# ADR-0022: 静的配信を Cloudflare Pages、バックアップ先を R2 にする

> **5 行以内 summary**: ColorMaster の静的配信 (wasmJs バンドル等) は **Cloudflare
> Pages**、ユーザーデータ永続化 (Litestream バックアップ先) は **Cloudflare R2** を
> 採用する。Backend (Cloud Run / ADR-0009) と組み合わせたハイブリッド構成で、
> Pages の無制限 bandwidth + R2 の zero egress により、個人プロジェクト規模の
> 完全無料運用と本番運用レベルの耐久性を両立する。

## ステータス

accepted

## コンテキスト

ColorMaster は Backend を Cloud Run (ADR-0009) にホストする方針だが、以下の役割は
Backend ではなく専用サービスに分離する必要がある:

- **静的配信**: wasmJs バンドル / 画像 / フォント等のフロントエンドアセットを CDN
  経由で世界中に配信したい。Cloud Run で配信すると vCPU-sec を消費して無料枠を
  圧迫する。
- **ユーザーデータ永続化**: Cloud Run はステートレスで filesystem が ephemeral の
  ため、`users.db` の永続化には外部ストレージが必要。Litestream で WAL を S3 互換
  ストレージへストリーミングレプリケートする戦略 (ADR-0008) を採るが、egress 課金が
  発生するストレージはバックアップ運用に向かない。

加えて Firebase は完全廃止方針 (ADR-0011) のため Firebase Hosting / Cloud Storage
は選択肢から外れる。Web 配信は wasmJs ターゲット完成後に再開する (ADR-0012) ため、
新しい配信先を本 ADR で確定する必要がある。

## 決定

ColorMaster の **静的配信を Cloudflare Pages**、**Litestream バックアップ先を
Cloudflare R2** に統一する。Backend (Cloud Run / ADR-0009) と組み合わせた
ハイブリッドホスティングを構成する。

- **Cloudflare Pages**: wasmJs バンドル + 静的アセットの配信。Git 連携で自動デプロイ。
- **Cloudflare R2**: `users.db` の WAL を Litestream v0.5.0 で連続レプリケート。
  Backend 起動時に R2 から restore して局所ディスクの `users.db` を復元する。
- R2 アクセストークンは TTL 90 日でローテーション (ADR-0021)。
- バケットは private、Backend Service Account の credential のみアクセス可能。

## 根拠

- **Cloudflare Pages の無制限 bandwidth**: 静的配信の egress を月次上限なしで処理
  できる唯一の主要 CDN。300+ edge locations による低レイテンシ配信。
- **Git 連携の自動デプロイ**: PR ごとに preview deployment、main マージで本番
  デプロイが自動化されており、CI スクリプトが最小で済む。
- **R2 の zero egress**: S3 互換でありながら egress 課金が発生しないため、Cloud Run
  起動時の restore (R2 → Backend) や Litestream の reverse-sync コストがゼロ。
- **Litestream v0.5.0 との親和性**: endpoint URL を指定するだけで R2 を自動検出し、
  追加設定をほぼ要しない。コミュニティの本番運用事例も多く、SQLite + Litestream +
  R2 の組合せは確立済みパターン。
- **ハイブリッド構成の合理性**: Backend は GCP (JVM 成熟)、配信 / ストレージは
  Cloudflare (unlimited bandwidth + zero egress) と役割分担することで、両者の弱点を
  相互補完しつつ完全無料運用が実現する。

### 比較した代替案

#### 静的配信先

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| **Cloudflare Pages (採用)** | 無制限 bandwidth、300+ edge、Git 連携、無料運用 | Cloudflare アカウントが必要 | 全項目で優位、本 ADR で採用 |
| Firebase Hosting | Google エコシステム統一、設定容易 | **10GB / 月 bandwidth 上限**、Firebase 全廃方針 (ADR-0011) に整合しない | Firebase 廃止と bandwidth 上限のため、不採用 |
| Vercel | DX 良好、preview deployment 自動化 | **Hobby plan は商用利用不可**、Pro plan は月額固定費 | ライセンス制約と固定費、不採用 |
| Netlify | Build pipeline 機能が充実 | bandwidth は 100GB / 月で上限あり、Cloudflare Pages の unlimited に劣後 | bandwidth で Cloudflare Pages に劣る、不採用 |
| AWS S3 + CloudFront | エンタープライズ実績豊富 | **egress 課金あり**、CloudFront 設定が複雑、IaC 前提 | 無料運用に整合せず設定コスト過大、不採用 |

#### Litestream バックアップ先

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| **Cloudflare R2 (採用)** | S3 互換 + **zero egress**、Litestream v0.5.0 自動検出、本番運用実例多数 | Cloudflare アカウントが必要 | 全項目で優位、本 ADR で採用 |
| AWS S3 | Litestream 公式リファレンス、長期耐久性 | **egress 課金あり**、restore のたびに従量課金が発生 | egress コストが Cloud Run 起動時に効いてくる、R2 に劣後で不採用 |
| Google Cloud Storage | GCP 統一でアクセス制御が容易 | egress 課金あり、Litestream の S3 互換層を経由する必要 | egress 課金と互換層の二重コスト、不採用 |
| Backblaze B2 | egress が一定量無料、S3 互換 | エコシステムが Cloudflare ほど成熟していない | R2 の方が無料枠と統合性で優位、不採用 |

## 帰結 (Consequences)

### Positive

- 静的配信の bandwidth が事実上無制限となり、トラフィック増でも無料運用を維持できる。
- R2 の zero egress により、Backend cold start 時の restore コストがゼロ。
- Cloud Run (ADR-0009) と組み合わせて完全無料のハイブリッドホスティングが成立する。
- Cloudflare Pages の Git 連携で wasmJs ビルドの本番デプロイが自動化される。
- Firebase 完全廃止 (ADR-0011) と整合し、Firebase 依存をプラットフォーム層から
  根絶できる。

### Negative / トレードオフ

- ホスティング先が GCP / Cloudflare の 2 ベンダーに分散する → 監視・運用手順が
  2 系統になるが、役割が明確に分離されているため認知負荷は限定的。
- Cloudflare 障害時には静的配信とバックアップが同時に影響を受ける → Backend
  自体は Cloud Run で稼働継続、`users.db` は局所ディスクで読み書き継続できるため
  即時影響は限定的。
- R2 access token の管理が必要 → ADR-0021 の TTL 90 日ローテーションで運用化する。

### Neutral / 将来の検討事項

- Cloudflare Pages の Build minutes 制限に到達した場合は GitHub Actions で
  事前ビルド → アーティファクトを Pages にアップロードする方式へ切替を検討する。
- R2 の地域配置 (現状 auto) は、Cloud Run リージョン (`asia-northeast1`) との
  レイテンシ実測値に応じて固定リージョンに切替する余地がある。
- Litestream v0.5.x 以降のメジャーバージョンアップ時は restore 互換性を確認する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 3. 外部サービスの採用または変更 (静的配信 + バックアップ先の選定)
- [x] 4. データ永続化 / 同期戦略 / バックアップ方式 (Litestream + R2 によるレプリケート)
- [x] 8. 複数の代替案を比較した結果としての判断 (Firebase Hosting / Vercel / Netlify / S3+CloudFront / S3 / GCS / B2 と比較)
- [x] 9. 元に戻すコストが高い決定 (Pages / R2 を稼働させると移行コストが大きい)
- [x] 10. 長期的な制約 (今後 1 年以上、配信・バックアップ運用に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

`.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
本 ADR がコーディング規約 / Plan で済む話 / runbook で済む話 ではなく、配信・
バックアップという外部サービスの中核採用判断であることを確認した。R2 token の
具体的なローテーション手順は runbook、Pages の preview deployment 設定変更は Plan
で扱う。

- [x] 確認済み

## 関連

- 関連 ADR: ADR-0006 (wasmJs i18n と Web ターゲット復帰)、ADR-0008 (Litestream + R2 で `users.db` レプリケート)、ADR-0009 (Cloud Run と組合せたハイブリッド)、ADR-0011 (Firebase 完全廃止)、ADR-0012 (Web 配信再開先)、ADR-0021 (R2 token TTL 90 日ローテーション)
- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- `.claude/rules/cloudflare-pages.md`
- `.claude/rules/r2-litestream.md`
- `.claude/rules/secrets.md`
- `docs/harness/plan.md` §3.4 (ホスティング採用根拠)、§3.2 (Litestream + R2 設計)
