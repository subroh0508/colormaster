---
id: ADR-0008
title: ユーザーデータは Backend 内蔵 SQLite と Litestream で R2 にレプリケートする
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

# ADR-0008: ユーザーデータは Backend 内蔵 SQLite と Litestream で R2 にレプリケートする

> **5 行以内 summary**: ユーザーデータ (担当 / 推し) の永続化は Backend (Cloud Run)
> 内蔵 SQLite `users.db` に行い、Litestream で Cloudflare R2 に WAL ストリーミング
> レプリケート + 起動時 restore する。リポジトリには絶対 commit しない (`.gitignore`
> で強制)。DB スキーマには `uid` (Google sub claim) のみ保存し、display name / email /
> picture は GIS userinfo endpoint から都度取得 + memory cache TTL 15 分で扱う (PII 最小化)。

## ステータス

accepted

## コンテキスト

ColorMaster は担当 / 推しといったユーザー固有データを保存する必要がある。ユーザーデータ
には Google アカウント由来の PII (display name / email / picture) が紐づく可能性があり、
永続化戦略は以下を同時に満たさなければならない:

- 個人プロジェクトとして **完全無料運用** を目指す (Cloud Run + Cloudflare R2 のハイブリッド)。
- Cloud Run の **ephemeral container** 特性に耐える (コンテナ再生成で local SQLite が
  失われる)。
- **PII 最小化** (ADR-0020) — DB が万一漏洩した場合に個人特定が困難な状態を維持。
- Firebase Auth / Firestore を **完全廃止** する方針 (ADR-0011) と整合させ、Firebase
  Storage 系の代替を別途用意する必要がある。

候補としては (a) Cloud Run + 内蔵 SQLite + Litestream で R2 にレプリケート、
(b) Cloud SQL / 外部 Postgres、(c) Firestore 継続、(d) Cloudflare D1、が挙がった。

## 決定

ユーザーデータの永続化を以下の構成で採用する。

- Backend (Cloud Run、Ktor / Kotlin/JVM) は内蔵 SQLite `users.db` をプロセスローカルに
  持つ。リポジトリには `users.db*` を絶対 commit せず、`.gitignore` で強制する
  (`data/users.db` / `users.db-shm` / `users.db-wal` / `data/*.db-journal`)。
- **Litestream** で `users.db` の WAL を Cloudflare R2 に **ストリーミングレプリケート**
  する (S3 互換 endpoint、egress 無料)。
- コンテナ起動時に Litestream の `restore` を実行して最新の WAL から `users.db` を
  復元する。これにより Cloud Run の ephemeral container 性をカバーする。
- DB スキーマには **`uid` (Google sub claim) のみ保存**。display name / email /
  picture は GIS userinfo endpoint から **都度取得 + memory cache TTL 15 分** で扱う
  (PII 最小化、ADR-0020)。
- Container イメージに `users.db` を焼き込むことは禁止 (`Dockerfile` 内の
  `COPY data/users.db` を Konsist で機械検証)。

## 根拠

- **無料運用**: Cloud Run Free tier + Cloudflare R2 (zero egress) の組み合わせで
  完全無料運用が成立する。Cloud SQL / Firestore は無料枠が薄い、または有料化リスクが
  ある (Firestore は 3,000 DAU 制限)。
- **ephemeral 耐性**: Litestream は v0.5.0 で R2 endpoint 自動検出に対応しており、
  WAL ストリーミング + 起動時 restore で Cloud Run の container 揮発性を実用上問題ない
  レベルで吸収できる。実例多数で本番運用レベルが確立済。
- **PII 最小化との整合**: SQLite スキーマを `uid` のみに絞ることで、万一 `users.db`
  または R2 backup が漏洩しても個人特定情報が出てこない構造になる。userinfo は
  GIS endpoint から都度取得し memory cache に短時間しか置かない (TTL 15 分) ことで、
  プロセス再起動で揮発する。
- **Firebase 廃止との整合**: ADR-0011 で GIS 統一・Firestore 撤去を決めており、本 ADR の
  Backend SQLite + Litestream + R2 はその代替永続化レイヤとして自然に成立する。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Cloud SQL (Postgres / MySQL) | 高可用性、運用知見豊富 | 無料枠なし、最低 $9/月程度の固定費 | 完全無料運用要件に反する |
| Firestore 継続 | 既存資産流用可、ephemeral 耐性問題なし | wasmJs 非対応、無料枠 3,000 DAU 制限、ADR-0011 で全廃 | Firebase 全廃方針に矛盾 |
| Cloudflare D1 | 同じ Cloudflare 上で完結、無料枠あり | Backend が Cloud Run (GCP) との往復で latency 増、JVM 直結のドライバが未成熟 | Backend と同居 SQLite + Litestream の方が単純で堅牢 |
| Cloud Run + Volume mount (Filestore / GCS Fuse) | 起動時 restore 不要 | コスト発生、ハイブリッド構成の旨味喪失 | egress 無料の R2 を活かせず劣後 |

## 帰結

### Positive

- 完全無料運用が成立し、個人プロジェクトの維持コストがゼロに近付く。
- DB スキーマが `uid` のみのため、漏洩時の個人特定リスクが構造的に低い。
- Litestream + R2 の組み合わせで、Cloud Run の ephemeral 性を意識せずアプリ実装に集中できる。
- Firebase 撤去 (ADR-0011) と一貫した方針が取れる。

### Negative / トレードオフ

- userinfo を毎リクエストで GIS から取得 (memory cache hit 時を除く) するため、
  認証経路にネットワーク往復が増える。memory cache TTL 15 分で十分緩和可能。
- Litestream の運用には R2 token (TTL 90 日) のローテーション運用が必要 (ADR-0021)。
- container 起動時の restore 時間が cold start に加算される (許容範囲、実例ベンチで数秒)。

### Neutral / 将来の検討事項

- ユーザー数が数万 DAU を超えた場合、SQLite 単一 instance の書き込みスループットが
  ボトルネック化する可能性がある。その時点で Cloud SQL / D1 への移行を再評価する。
- R2 backup の geo redundancy / 暗号化方針は別 ADR (ADR-0022) で詳述。
- userinfo memory cache の TTL は実運用負荷から再調整可能。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 3. 外部サービスの採用または変更 (Cloudflare R2 を採用、Firestore を撤去)
- [x] 4. データ永続化 / 同期戦略 / バックアップ方式 (Backend SQLite + Litestream + R2)
- [x] 6. セキュリティ・プライバシー・ライセンスに関する方針 (DB スキーマ `uid` のみ、PII 最小化)
- [x] 9. 元に戻すコストが高い決定 (永続化レイヤの選択は移行コストが大きい)
- [x] 10. 長期的な制約 (今後 1 年以上、ユーザーデータ保管の中核)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR がデータ永続化戦略の中核方針
      として ADR 化すべき (`runbook` / Plan で済む話ではない) ことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0009 (Backend ホスティング: Cloud Run)
- ADR-0011 (認証スタックを Firebase から GIS に統一)
- ADR-0020 (PII 保護と権限ロール、`uid` のみ保存の SSoT)
- ADR-0021 (Secrets 管理、R2 token のローテーション)
- ADR-0022 (Cloudflare R2 採用、Litestream バックアップ先)
- `docs/harness/plan.md` §3.2 / §3.7
- `.claude/rules/{db-protection,pii,secrets}.md`
