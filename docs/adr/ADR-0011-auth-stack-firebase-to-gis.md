---
id: ADR-0011
title: 認証スタックを Firebase から Google Identity Services に統一する
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

# ADR-0011: 認証スタックを Firebase から Google Identity Services に統一する

> **5 行以内 summary**: Firebase Auth / Firestore / firebase-admin SDK を完全廃止し、
> 全 target (Android / iOS / wasmJs) で Google Identity Services (GIS) に統一する。
> フロントで GIS から ID Token を取得 → Backend に Bearer 送信 → Backend で Google JWKS
> 検証 → uid 抽出。`dev.gitlive:firebase-{app,auth,firestore}` を依存から削除し、
> `core/network/{auth,firestore}` / `firebase.json` / `.firebaserc` を撤去する (Phase A5)。

## ステータス

accepted

## コンテキスト

ColorMaster はこれまで Firebase Auth + Firestore + firebase-admin SDK の構成で
認証とユーザーデータを扱ってきた。Compose Multiplatform への移行を進める中で、
次の制約が顕在化した:

- **Firebase Auth / Firestore は wasmJs 非対応**。`dev.gitlive:firebase-*` 系も
  wasmJs ターゲットを公式サポートしない。Web 配信を Cloudflare Pages + wasmJs で再開
  する方針 (ADR-0022) と矛盾する。
- **Firebase 無料枠の縮小傾向**: Cloud Storage が Spark plan から除外され、Firestore
  の Spark plan は 3,000 DAU 上限が課されるなど、長期的に個人プロジェクトの完全無料
  運用が困難になりつつある。
- フロントは Android / iOS / wasmJs の 3 ターゲットで、Firebase SDK の expect/actual
  切替が wasmJs 不可なため共通化が破綻する。
- Backend ホスティングを Cloud Run (ADR-0009)、ユーザーデータを Backend SQLite +
  Litestream + R2 (ADR-0008) に移す決定により、Firebase に残る役割が認証のみとなった。

本 ADR は、本リポジトリの草案段階に存在した 2 つの先行決定 (旧 "firebase-removal-complete"
と旧 "gis-unified-authentication") を **統合した経緯** で起票する。前身 ADR は物理化
されていないため `supersedes` には記載しないが、認証撤去と GIS 採用は同一の撤去 PR で
扱う方が一貫した移行計画になるため、本 ADR で一本化する。

## 決定

認証スタックを以下の構成で統一する。

- **Firebase Auth / Firestore / firebase-admin SDK を完全廃止**。`dev.gitlive:firebase-app`
  / `dev.gitlive:firebase-auth` / `dev.gitlive:firebase-firestore` を依存から削除する。
- 全 target (Android / iOS / wasmJs) で **Google Identity Services (GIS)** に統一。
  フロントは GIS から ID Token を取得し、Backend へ Bearer Authorization ヘッダで送信する。
- Backend は Google JWKS endpoint (`https://www.googleapis.com/oauth2/v3/certs`) で
  ID Token を検証し、`sub` claim を `uid` として抽出する (ADR-0020)。
- `core/network/auth` / `core/network/firestore` モジュールは撤去または
  `core/network/colormaster-api` に統合する。
- ルート設定ファイル `firebase.json` / `.firebaserc` も撤去する。
- 撤去作業は **Phase A の A5** で実施する (`docs/harness/plan.md` §3.6)。
- 撤去対象モジュールの保護規約は `.claude/rules/removed-modules.md` / `no-firebase.md`
  (旧 `firebase-boundary.md` を改名予定) で機械検証する。

## 根拠

- **wasmJs 全 target 統一**: GIS は Web (wasmJs 含む) / Android / iOS 全てで使える
  唯一の Google 認証選択肢であり、expect/actual 切替不要で SDK 依存が激減する。
- **Firebase 無料枠の縮小回避**: Firestore 3,000 DAU 制限 / Cloud Storage Spark
  除外などの制約から、個人プロジェクトの長期無料運用に Firebase は適さない。
- **Backend 認証パスの単純化**: ID Token を Bearer で送り JWKS で検証する流れは標準的な
  OIDC パターンで、firebase-admin SDK 依存を避けられる。Kotlin/JVM の `nimbus-jose-jwt`
  等で実装可能。
- **PII 最小化との整合**: ADR-0020 で「DB に保存するのは `uid` のみ」と決めた方針が
  GIS の `sub` claim 抽出にそのまま乗る。
- **撤去 PR の一貫性**: Firebase 撤去と GIS 採用を別 ADR にすると、撤去後 GIS 移行
  完了前の中間状態が生まれてしまう。同一 ADR で扱うことで「撤去 = 移行完了」を表現する。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Firebase Auth を残し Firestore のみ撤去 | 認証移行コストゼロ | wasmJs 非対応問題が残る、SDK 依存も残存 | Web ターゲットで破綻、不採用 |
| 自前 OAuth 2.0 サーバ (Keycloak / Ory) | 認証ベンダー非依存 | 運用コスト膨大、無料運用不可 | 個人プロジェクト規模に過剰 |
| Auth0 / Clerk 等の SaaS | フロント SDK が wasmJs サポート進展中 | 無料枠ユーザー数制限あり、ベンダーロックイン | GIS と比較してコスト劣後 |
| Sign in with Apple 等の併用 | ユーザー選択肢拡張 | 認証経路が複数化、管理コスト増 | 初期は Google 単一で十分、必要なら別 ADR で追加 |

## 帰結

### Positive

- wasmJs ターゲット含む全 target で認証経路が統一され、共通 ViewModel から
  expect/actual 切替なしで利用可能。
- Firebase 無料枠縮小リスクから完全に解放される。
- Backend が firebase-admin SDK 依存ゼロになり、Kotlin/JVM 標準ライブラリ群で完結。
- DB スキーマが `uid` のみ (ADR-0020) と整合し、PII 最小化が成立。

### Negative / トレードオフ

- 既存ユーザー (Firebase Auth 上のアカウント) の uid と GIS `sub` claim は同じ値の
  はずだが、移行時にデータ整合性の検証が必要 (Plan で確認手順を整備)。
- Firestore からユーザーデータを移行する作業が一時的に発生する (ADR-0008 の
  Backend SQLite + Litestream に移行)。
- 認証経路が GIS 単一ベンダー依存になる (Google アカウントなしのユーザーは利用不可) →
  個人プロジェクトの想定ユーザー層では許容、必要時に別 ADR で追加プロバイダ検討。

### Neutral / 将来の検討事項

- GIS の deprecation 動向 (例: One Tap の API 変更) を監視し、影響時は別 ADR で対応。
- ID Token の clock skew 許容秒数や JWKS キャッシュ TTL は Backend の runbook で
  運用調整可能。
- マルチプロバイダ対応 (Apple / GitHub 等) が必要になった場合は別 ADR で追加。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 2. 主要なライブラリ / フレームワークの採用または撤去 (Firebase SDK 群を撤去、GIS を採用)
- [x] 3. 外部サービスの採用または変更 (Firebase Auth / Firestore 廃止 → GIS)
- [x] 6. セキュリティ・プライバシー・ライセンスに関する方針 (認証経路の根幹)
- [x] 8. 複数の代替案を比較した結果としての判断 (Firebase 部分残存 / 自前 OAuth / SaaS と比較)
- [x] 9. 元に戻すコストが高い決定 (認証スタック移行は全 target に波及)
- [x] 10. 長期的な制約 (今後 1 年以上、全 target の認証経路を規定)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「Firebase を完全廃止して GIS に統一する」例と一致し、
      本 ADR がコーディング規約 / Plan で済む範囲を超える方針転換であることを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0008 (ユーザーデータを Backend SQLite + Litestream + R2、本 ADR と表裏)
- ADR-0009 (Backend ホスティング: Cloud Run、JWKS 検証の実行先)
- ADR-0020 (PII 保護、`uid` 抽出と userinfo 都度取得)
- ADR-0012 (js/app 撤去、本 ADR と同時期に Phase A5 で実施)
- `docs/harness/plan.md` §3.2 / §3.6
- `.claude/rules/{removed-modules,no-firebase,backend-auth}.md`
