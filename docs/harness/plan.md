# AI ハーネス組み込み計画

> Status: **proposed** (本ドキュメントの merge をもって計画として確定。実装は B0 PR から段階的に着手)
> Last updated: 2026-05-16

本ドキュメントは ColorMaster リポジトリに AI 駆動の開発ハーネスを段階的に組み込むための計画書である。Skill 起点で機能追加・バグ修正・リファクタリングを駆動し、PR マージ後の KPT を経由してハーネス自身を継続改善するループを構築することを目的とする。

実装はまだ着手していない。本ドキュメントの確定後、`B0` ブートストラップ PR からフェーズを順に進める。

---

## 1. 目的と達成基準

### 1.1 目的

- 機能追加・バグ修正・リファクタリングを AI Skill 起点でスタートさせ、要件・仕様策定からマージまで AI 駆動で進められるようにする。
- 大規模・小規模いずれのスコープでも、計画・進捗・後回しにした意思決定・解決手法をリポジトリ内 Markdown にトレース可能な形で残す。
- PR マージ後の KPT を通じて、ハーネス（CLAUDE.md / `.claude/rules` / `.claude/skills` / Lint / テスト）自体を継続改善する。
- 依存ライブラリのバージョンアップを、リリースノート確認とテストによる影響評価まで含めて自動化する。
- 公開データソース (im@sparql) の停止に影響されない構成にする。

### 1.2 達成基準

- 通常の機能追加・バグ修正は B0 完了後、Skill 起動から PR マージまで人間の介入を最小化できる。
- すべての PR がマージ後に KPT を生成し、`docs/harness/learnings/` に蓄積される。
- 月次（または閾値超過時）に KPT を集約したハーネス改修 PR が自動起票される。
- `imas/imasparql` の更新を 1 日 1 回検知し、差分があれば PR が自動作成される。サーバ停止時もアプリは継続稼働する。
- Kotlin Multiplatform で Android / iOS / wasmJs / JVM (backend) の 4 ターゲットに対応する。
- Backend 経由化により Wasm でも Firestore 上のユーザーデータを読み書きできる。
- **テスト品質は 3 種類の指標で多層検証する**:
  - **Line / Branch Coverage 100%** (テスト存在の保証、CI 必達ゲート)
  - **Spec Coverage 100%** (Acceptance criteria ⇄ テストのトレーサビリティ、CI 必達ゲート)
  - **Mutation Score** (テストの意味的強度の計測、PR コメントで可視化、ゲートにはしない)
- カバレッジの 100% は「テストが存在することの指標」、Spec coverage は「仕様適合性の指標」、Mutation Score は「テストの意味的強度の指標」。三層は別々のアウトカムを目指し、互いに代替不可能。
- 除外対象は ADR 0013 で限定列挙し、それ以外は AI による自動テスト生成で必ず充足する。
- Phase C (本格運用) 着手時点で、テスト基盤・三層指標の達成・im@sparql ローカル Docker 環境が完全に整っている。

---

## 2. 現状調査サマリ

### 2.1 リポジトリ構成

- **Kotlin Multiplatform** プロジェクト、Compose Multiplatform 採用。
- 主要モジュール: `core/{common,model,data,network/*,features/*,test}`, `backend/{server,cli}`, `js/{app,material}`, `android/app`, `public/`, `plugins/`。
- 現状の KMP target: **androidTarget + js(IR)** のみ。**iOS / wasmJs は未対応**。
- バージョン: Kotlin 2.1.21 / AGP 8.9.0 / Gradle 8.14 / Compose Multiplatform 1.8.0-alpha04 / Ktor 3.1.3 / Koin 4.0.4 / kotest 6.0.0.M1。
- Decompose 1.0.0 が依存に含まれている。

### 2.2 Firebase 利用

- `dev.gitlive:firebase-{app,auth,firestore}:2.1.0` を使用。
- Firebase Auth (Google / 匿名サインイン)、Firestore (`users` コレクションで担当アイドル・お気に入りを永続化)。
- Wasm 非対応のため、フロントエンドからの直接呼び出しは Wasm 化するモジュールで使えない。

### 2.3 Backend / im@sparql

- `backend/cli` が im@sparql に SPARQL クエリを送信、結果を SQLite (SQLDelight) に格納。
- `backend/server` (Ktor) が SQLite を読み出して REST API で配信。
- ローカル/Docker で im@sparql を立てる手順は未整備（SPARQL 互換サーバなら Apache Jena Fuseki が候補）。
- データソースは `imas/imasparql` GitHub リポジトリ。

### 2.4 テスト / Lint / CI

- テスト: `core/data` 中心に 23 ファイル（Kotest 6.0.0.M1）。`backend/server`, `android/app`, `js/app` はテストゼロ。
- Lint / format ツール: 未導入（ktlint / detekt / spotless いずれも未設定）。
- カバレッジ計測: 未導入。
- pre-commit hook: 未設定。
- GitHub Actions: `ci.yml` (Android JVM テスト + JS テスト) と `web-build-and-deploy.yml` (Firebase Hosting デプロイ)。
- Renovate: `config:recommended` のみ。

### 2.5 ドキュメント / ハーネス

- CLAUDE.md / AGENTS.md / ADR / 要件仕様 Markdown は **存在しない**。
- `.claude/` ディレクトリ・Agent Skills も未導入。

---

## 3. 設計指針

### 3.1 アプリケーション設計

| 領域 | 採用 |
|---|---|
| アーキテクチャ | **Compose Multiplatform + 共通 ViewModel + Navigation 3** |
| 状態管理 | `StateFlow<UiState>` + `onAction(UiAction)` + `Channel<UiEffect>` の軽量 UDF |
| モジュール構造 | **feature-first**: `feature/<name>/{ViewModel, UiState, UiAction, Screen, di}.kt` |
| i18n | **compose-multiplatform-resources** (`composeResources/values-<locale>/strings.xml`) |
| Decompose | **撤去** |
| DI | Koin 継続 (4.0.4) |

採用根拠:

- Compose Multiplatform Navigation 3 が 1.10 以降で Android / iOS / desktop / web 全プラットフォーム対応となり、共通 ViewModel も lifecycle-viewmodel 経由で KMP 全 target 提供される。Decompose の独自ナビゲーションを残す積極的理由が小さくなった。
- Compose for iOS が 1.8.0 で Stable 到達。
- compose-multiplatform-resources は JS/Wasm でも非同期に resource を読める設計が公式に明示されており、Wasm 対応 i18n として最有力。Lyricist / i18n4k は wasmJs 公式対応の言及なし。

### 3.2 データ / 認証設計

| 関心事 | 設計 |
|---|---|
| アイドル情報マスタ | リポジトリ内 SQLite (`data/idols.db`) + JSON snapshot (`data/idols.json`) + 同期 state (`data/.imasparql-sync-state.json`)。Container イメージに焼き込み、read-only。Litestream の対象外 |
| ユーザーデータ (担当 / 推し) | **Backend (Cloud Run) 内蔵 SQLite `users.db`** に保存。Litestream で **Cloudflare R2** に WAL ストリーミングレプリケート + 起動時 restore。**リポジトリには絶対 commit しない** (`.gitignore` で強制) |
| ユーザーデータの PII | **DB スキーマには `uid` (Google sub claim) のみ保存**。display name / email / picture は GIS userinfo endpoint から都度取得し、Backend memory に短時間 (TTL 15 分) キャッシュ。万一 `users.db` が漏洩しても個人特定が困難な状態を維持 |
| 認証 | **全 target で Google Identity Services (GIS)** に統一。フロント (Android / iOS / wasmJs) で GIS から ID Token を取得 → Backend に Bearer 送信 → Backend で Google の JWKS で検証 → uid 抽出。Firebase Auth / firebase-admin SDK は使用しない |
| Firebase 依存 | **完全廃止**。`dev.gitlive:firebase-{app,auth,firestore}` を依存から削除、`core/network/{auth,firestore}` は撤去または `core/network/colormaster-api` に統合 |

採用根拠:

- アイドル情報マスタを Git に乗せることで、Cloud Run コンテナはステートレスを保ち、im@sparql ダウン時もアプリは前回正常データで稼働可能。
- 同期失敗を PR レビューでブロックでき、予期せぬデータ消失を防げる。
- **Firebase Auth / Firestore は wasmJs 非対応**、また Firebase 無料枠の縮小傾向 (Cloud Storage の Spark plan 除外、Firestore の 3,000 DAU 制限) もあり、依存撤廃が中長期保守性に有利。
- GIS による全 target 統一で SDK 依存が激減、Wasm 対応の expect/actual 切替が不要に。
- ユーザーデータは Backend SQLite + Litestream で永続化、Cloud Run の ephemeral 性は R2 への WAL レプリケートでカバー。
- PII 最小化により `users.db` 漏洩時の影響を構造的に下げる。

### 3.3 同期戦略 — upstream-driven sync

1 日 1 回の GitHub Actions cron で以下を実行:

1. `gh api repos/imas/imasparql/commits/master` で upstream の latest SHA を取得。
2. `data/.imasparql-sync-state.json` の `upstream_sha` と比較。
3. 一致 → no-op で正常終了。
4. 不一致 → `./gradlew :backend:cli:fetchIdolColorsFromImasparql` で取得し、`data/idols.db` と `data/idols.json` を更新。レコード数 ±X% の異常検知あり。
5. 正常完了 → `chore/sync-imasparql-<short-sha>` ブランチで PR 作成 (人間 or AI レビュー必須、自動 merge しない)。
6. im@sparql サーバが 5xx を返す等の異常 → Issue 自動起票、次回リトライ。

利点:

- upstream 未更新時のコストは 1 API 呼び出しのみ。短い cron でも GitHub Actions 無料枠を圧迫しない。
- 変更検知は GitHub API のみに依存し、im@sparql の稼働状況とは切り離せる。

### 3.4 ホスティング

| 役割 | 採用サービス |
|---|---|
| **Backend (Ktor / Kotlin/JVM)** | **Google Cloud Run** |
| **静的配信 (wasmJs バンドル等)** | **Cloudflare Pages** |
| **ユーザーデータ永続化 (Litestream バックアップ先)** | **Cloudflare R2** (S3 互換、egress fee 無料) |
| 代替候補 (Backend) | Koyeb (ADR 0009 で記録) |
| 不採用 (Backend) | Cloudflare Containers (Workers Paid plan $5/月必須でコスト劣後)、Fly.io (無料枠廃止)、Render (sleep)、Railway (実質有料) |
| 不採用 (Hosting) | Firebase Hosting (10GB bandwidth 上限、Firebase 全廃方針に整合しない)、Vercel (Hobby は商用不可)、Netlify (Cloudflare Pages の方が unlimited bandwidth で優位) |

採用根拠:

- **Cloud Run**: 無料枠が最も厚い (200 万 req / 360,000 vCPU-sec / 180,000 GiB-sec)、request-based billing で idle 課金なし。既存 Dockerfile (amazoncorretto:22) がそのまま流用可能。
- **Cloudflare Pages**: **無制限 bandwidth**、300+ edge locations、Git 連携でデプロイ自動化容易、Firebase Hosting より明確に優位。
- **Cloudflare R2**: S3 互換 + zero egress、Litestream v0.5.0 で endpoint URL から自動検出 (追加設定ほぼ不要)、実例多数で本番運用レベル確立済。
- **ハイブリッド構成のメリット**: Backend は GCP (Cloud Run の成熟した JVM サポート)、Storage / Hosting / CDN は Cloudflare (egress 無料・unlimited bandwidth)、両者のいいとこ取り。完全無料運用が可能 (個人プロジェクト規模)。
- **Cloudflare Containers を不採用とした理由**: Containers は 2026/4/13 GA で技術的には Kotlin/JVM 動作可能だが、**Workers Paid plan $5/月 必須** で完全無料運用ができない (Cloud Run の Free tier に対して明確に劣る)。年 $60 の固定費は個人プロジェクトには重い。

### 3.5 Terraform / IaC

- 本計画では Terraform を採用しない。Cloud Run / Artifact Registry / Firebase の設定は GitHub Actions スクリプトと `gcloud` CLI で管理する。
- 採用しない理由: 管理対象リソースが少なく、Terraform を投入する旨味より学習・保守コストが上回ると判断。ADR で記録する。

### 3.6 撤去対象

| 対象 | 理由 |
|---|---|
| `js/app/` | wasmJs 移行先決定済み、即時撤去で後続リファクタが clean になる |
| `js/material/` | `js/app` 専用、`js/app` 撤去と同時に削除 |
| `kotlin-js-store/` | wasmJs 移行時に再生成 |
| `.github/workflows/web-build-and-deploy.yml` | Web 配信は wasmJs 完成後に再開 (Cloudflare Pages 経由で再構築) |
| `public/` の js/app 専用ファイル | js/app 撤去と同時に整理。共有資源は `core/resources/` 等に退避 |
| **`dev.gitlive:firebase-app/auth/firestore` 依存** | Firebase 完全廃止 (ADR 0011 / 0022 / 0023)。GIS + Backend SQLite + Cloudflare Pages に置換 |
| **`core/network/auth`、`core/network/firestore`** | Firebase 廃止に伴い `core/network/colormaster-api` に統合または撤去 |
| **`firebase.json`、`.firebaserc`** | Cloudflare Pages 移行後は不要 |

Web 配信は wasmJs ターゲット完成まで **一時停止** することを許容し、再開時は **Cloudflare Pages** にデプロイする。

### 3.7 .gitignore に必須で含める項目

PII 保護および credentials 漏洩防止のため、以下を `.gitignore` に明示する (ADR 0020 / 0024):

```
# ユーザーデータ (PII を含む、絶対 commit 禁止)
data/users.db
data/users.db-shm
data/users.db-wal
data/*.db-journal

# Secrets
.env
.env.local
.env.*.local
*.pem
*.key
*.p12
*-credentials.json
.cloudflare/credentials
.gcloud/credentials
```

Konsist で「`data/users.db*` がリポジトリに含まれていないこと」「`Dockerfile` 内で `COPY data/users.db` が存在しないこと」を機械検証する。

### 3.8 PII 保護とアクセス制御

ユーザーデータには Google アカウント由来の個人情報が含まれうるため、漏洩経路を構造的に塞ぐ多層防御を採用する (ADR 0020)。

#### 漏洩経路と防御対応

| # | 漏洩経路 | 防御 |
|---|---|---|
| 1 | リポジトリへの直接 commit | `.gitignore` で `data/users.db*` を除外 + Konsist 検証 |
| 2 | Container イメージへの焼き込み | Dockerfile で `users.db` を COPY しない + Konsist 検証 |
| 3 | R2 バケットからの読出し | バケットは **private**、Backend Container の R2 token のみ allow |
| 4 | R2 token の流出 | Secrets で管理、TTL **90 日** で定期ローテーション (ADR 0021) |
| 5 | リポジトリ内の credentials コミット | trufflehog による CI スキャン + `.gitignore` |
| 6 | PR diff からの credentials 漏洩 | trufflehog の secret-scan workflow を全 PR に発火 |
| 7 | Backend API 経由で他人のデータ取得 | ID Token 検証 + `uid` フィルタ、Konsist で `/api/me/*` ハンドラの `requireUid()` 呼出を強制 |
| 8 | ログ / モニタリングへの PII 出力 | rules/logging.md + rules/pii.md で禁止、detekt カスタムルールで `Logger.*(user.email/...)` 検出 |
| 9 | エラー応答に PII 含める | エラーレスポンス schema に PII フィールド禁止、Konsist 検証 |
| 10 | GIS から取得した userinfo の過剰保存 | DB スキーマに保存するのは `uid` のみ (display name / email は都度取得 + memory cache TTL 15 分) |
| 11 | GCP / Cloudflare コンソールへのアクセス | リリース権限を持つ単一 owner ロールのみ (ADR 0020) |
| 12 | `pr-retrospective` / KPT learning への PII 混入 | rules/pii.md で Skill 出力前の redaction 強制、Konsist でテスト fixture の非 `@example.com` domain 検出 |
| 13 | `code-reviewer` が CI ログから PII を漏らす | aspect ごとに PII redaction の前処理を必須化 |

#### PII の定義

メールアドレス / Google Account ID (sub claim 以外) / Display Name / プロフィール画像 URL / IP アドレス。`uid` (Google sub claim) は内部識別子として扱い PII 同等の取扱いとする。

#### 権限ロール (ADR 0020)

- **owner**: 全権限、Secrets ローテーション、GCP / Cloudflare コンソール operator、master マージ権限
- 当面は owner ロール 1 名のみで運用 (個人プロジェクト想定)
- 将来複数人体制になったら `developer` / `releaser` を別 ADR 改訂で追加

#### Skill ループにおける PII 配慮

`pr-retrospective` / `code-reviewer` / `harness-meta` は、CI ログ・diff・PR コメント等から PII を間接的に拾う可能性があるため、出力前に必ず redaction フェーズを通す。テストフィクスチャは `@example.com` ドメインのみ使用し、Konsist で機械検証する。

#### Secrets 管理 (ADR 0021)

- ローカル: `.env.local` (`.gitignore` で除外)
- CI/CD: **GitHub Secrets**
- Runtime: **Cloud Run Secret Manager** (R2 access key, GIS client secret 等)
- TTL: R2 token は **90 日**、定期ローテーション (`docs/runbooks/secrets-rotation.md` に履歴を記録)
- Skill は secret の値を出力に含めない (redaction 必須)

### 3.9 UI/UX デザインの現状記録 (Behavior Preservation)

Phase C の大規模リファクタ (Decompose 撤去 / CMP Navigation 3 / Firebase 廃止 / wasmJs 化) が UI に与える影響を構造的にブロックするため、**リファクタ前に既存 UI/UX を凍結 (freeze)** し、機械的に検証可能な状態にする (ADR 0023)。

#### 三本柱

1. **`DESIGN.md` (リポジトリ root)**
   - Google Stitch / Anthropic が de facto としてプッシュしている AI 駆動 UI 開発の標準
   - 上部: machine-readable な design tokens (Primitive / Semantic / Component の 3 階層)
   - 下部: human-readable な rationale (なぜその値か、どう適用するか、アクセシビリティ基準)
   - Markdown は LLM が最も読みやすく、AI コーディングツールが自動参照する
   - Konsist で「DESIGN.md に存在しない hex code がコードに混入していない」を機械検証

2. **UI Inventory (`docs/design/inventory/`)**
   - Marcin Treder の "Interface Inventory" 手法に準拠
   - 全画面・全コンポーネント・全状態を網羅キャプチャ
   - `screens/` / `components/` / `states/` / `flows/` の 4 軸
   - 各 Markdown は frontmatter で関連 SPEC-ID / 実装パスを記録

3. **Visual Regression Baseline (Roborazzi)**
   - **採用ツール: Roborazzi** (Compose Multiplatform 対応、唯一の Compose-Desktop 対応)
   - 主実行ランタイム: **JVM (Compose Desktop)**、補助: Android (Robolectric)
   - 対象: **commonMain の Composable** (全 target 共通)
   - 対象外: `wasmJsMain` / `iosMain` / `androidMain` の actual 実装 (Konsist + 単体で担保)
   - **解像度マトリックス必須化**:
     - **モバイル (412 x 915 dp)**: Pixel 7 portrait 相当
     - **PC 16:9 (1920 x 1080 dp)**: デスクトップブラウザ標準
   - **テーママトリックス必須化**: Light / Dark
   - → 1 Composable あたり **4 パターン (mobile-light / mobile-dark / desktop-light / desktop-dark)** が baseline

#### Roborazzi の wasmJs 未対応への対処

2026/5 時点で Roborazzi は wasmJs を未サポート (JVM → Multiplatform 実装に移行中)。ただし:

- **commonMain の Composable は Compose Desktop (JVM) で screenshot 化可能**、これは wasmJs 用に書いた commonMain コードもそのまま検証できる (Compose は全 target で Skia ベースのレンダリング)
- wasmJs ターゲット固有の `actual` 実装 (GIS 認証フロー等) は薄い層で、screenshot test の対象外として Konsist + 単体テストで担保
- 将来 Roborazzi が wasmJs に公式対応したら ADR 0023 を改訂し、wasmJs ランタイムでの実機 screenshot に切替を検討

#### code-reviewer aspect の拡張 (6 → 8)

| 既存 6 aspect | 新規追加 |
|---|---|
| spec-conformance / test-quality / architecture / security / performance / code-quality | **visual-regression**: Roborazzi の diff が 0 ピクセル (or 許容しきい値内)、baseline 更新時は意図的な UI 変更であるか確認 |
| | **design-tokens**: コード中の hex / sp / dp / radius がハードコードされておらず DESIGN.md の token を参照、新規 token 追加時に DESIGN.md と Rationale を同時更新 |

#### Skill: `ui-snapshot` (新規)

| 項目 | 内容 |
|---|---|
| 起動契機 | A10 EPIC 内の Plan、または Phase C 各リファクタ後の visual regression 検証 |
| 主な動作 | (1) Konsist で全 Composable をスキャン → `@Preview` 不在の検出、(2) Preview 追加 Plan を起票、(3) Roborazzi screenshot baseline 生成 (`./gradlew recordRoborazziDebug` 等)、(4) `DESIGN.md` と UI Inventory のドラフト生成、(5) hex / sp / dp のハードコードを検出して tokens 化提案 |
| 連携 | code-reviewer の `visual-regression` aspect、`design-tokens` aspect と双方向参照 |

#### 関連規約

- `.claude/rules/design-tokens.md`: DESIGN.md の構造、3 階層、ハードコード禁止
- `.claude/rules/ui-snapshot.md`: Preview + screenshot baseline 維持、baseline 更新は human approve 必須
- `.claude/rules/ui-inventory.md`: UI Inventory のファイル構造と更新規約
- `.claude/rules/behavior-preservation.md`: リファクタ時の振る舞い維持原則 (visual regression + spec-conformance の両輪)
- `.claude/rules/composable.md` 既存: 全 Screen Composable に `@Preview` 必須を追加、PreviewParameter で全 UiState バリエーション網羅
- `.claude/rules/screenshot-test.md` 既存: Roborazzi 運用規約を本格化、解像度マトリックスとテーママトリックスを明文化

### 3.10 テスト品質方針 — 三層指標の併用

AI による自動テスト生成を前提とし、テスト品質は **3 つの独立した指標** で多層検証する。それぞれが異なるアウトカムを目指し、互いに代替不可能。

#### 指標 A. Line / Branch Coverage 100% (CI 必達ゲート)

**目的**: テストが存在することの保証

**達成するアウトカム**:

- O1. テスト書き忘れの機械的検出 (特に AI 駆動で重要)
- O2. デッドコード / 到達不能コードの検出
- O3. リファクタリング時の実行可能性セーフティネット
- O4. AI が「テストゼロのコード」を生成することの 1 次フィルタ
- O5. CI 上の欠陥検出効果との統計的相関 (Statement coverage と mutation kill effectiveness の有意な正の相関)
- O6. コードレビュー支援 (新規行で未テストの箇所を CI が指摘)
- O7. 使用例ドキュメントとしての最低保証
- O8. テスト容易性を要求する設計圧
- O9. 新規モジュール導入時の基準明示

**達成しないアウトカム**:

- 仕様適合性 (→ 指標 B で担保)
- エッジケース・例外パスの網羅 (→ 指標 B / C で担保)
- テストの意味的強度 (→ 指標 C で担保)

**強制方法**: `koverVerify` の minBounds で `minValue = 100`、counter は `LINE` と `BRANCH` の両方。100% を満たさない PR はマージ不可。新規ファイルも例外なし。

**除外対象** (ADR 0013 で限定列挙):

- エントリポイント (`MainKt`, `Application`, `MainActivity` 等)
- DI モジュール定義 (Koin の `module {}` ブロック)
- 自動生成コード (SQLDelight 生成クラス、kotlinx.serialization 生成クラス、Compose Compiler 生成コード)
- 純粋な値クラス / sealed marker (テスト不可能な合成 toString/equals のみ)

除外対象を増やすには ADR 改訂が必須。Skill が勝手に除外を増やせないよう、`.claude/rules/coverage-100.md` で禁止規約として明文化する。

#### 指標 B. Spec Coverage 100% (CI 必達ゲート)

**目的**: 仕様適合性の保証 (ユーザーモチベーションの直接実現)

**達成するアウトカム**:

- 全 Acceptance criteria に対応するテストの存在
- 仕様 ⇄ テストの双方向トレーサビリティ
- AI が「カバレッジを満たすだけのテスト」を書いても価値が出ない構造 (仕様 ID が振れない限り meaningful にならない)

**実現方法**:

- `docs/specifications/<id>.md` の Acceptance criteria を `SPEC-NNN-N` 形式で ID 付与
- テスト関数に `@Spec("SPEC-NNN-N")` annotation を付与
- Konsist で「全 Acceptance criteria に対応する `@Spec` annotation が存在する」ことを機械検証
- 詳細は ADR 0016、規約は `.claude/rules/spec-traceability.md`

#### 指標 C. Mutation Score (シグナル可視化、CI ゲートにしない)

**目的**: テストの意味的強度の計測

**達成するアウトカム**:

- tautological / assertion-less テストの検出
- AI が code と test 双方で共有する盲点の発見 (Meta JiTTests 研究で defect 検出 4x の効果)
- 「100% coverage / 4% mutation score」のような無意味テストの可視化

**実現方法**:

- **PITest + pitest-kotlin + gradle-pitest-plugin** を採用
- KMP の **JVM target 経由** で `commonMain` + `jvmMain` (backend) + `androidMain` のクラスを mutate
- `jsMain` / `wasmJsMain` / `iosMain` の actual 実装は PITest 対象外 (これらは Konsist + 通常単体テストで担保)
- 詳細は ADR 0015、規約は `.claude/rules/mutation-testing.md`
- 将来検討: **MutFlow** (2026 年登場の K2 compiler plugin ベース、KMP 全 target 適合の可能性) を別 ADR で評価する余地として記録

**CI での扱い**:

- PR コメントで mutation score を可視化
- 必達ゲートにはしない (Goodhart's law を避ける)
- KPT で「mutation score が低い領域」を learnings に蓄積し、`.claude/rules/kotlin-test.md` を強化していくフォールバックループで対処

#### 三層指標が共に必要な理由

| 観点 | 指標 A (Coverage) | 指標 B (Spec) | 指標 C (Mutation) |
|---|---|---|---|
| 答える問い | 「テストが書かれているか?」 | 「仕様が表現されているか?」 | 「テストが意味的に効くか?」 |
| 計測対象 | コード実行範囲 | 仕様 ⇄ テストの対応関係 | 実装の意味的変更の検出力 |
| 失敗時のシグナル | テスト未記述の箇所が存在 | 仕様未実装/未検証の項目が存在 | テストが tautological |
| CI 扱い | 必達ゲート | 必達ゲート | シグナル可視化 |

カバレッジは **「指標」ではなく「テスト存在を保証する制約」** と再定義することで、Goodhart's law (測定が目標化されて指標としての意味を失う) を回避する。「仕様の指標」は指標 B が、「意味の指標」は指標 C が直接担当する分担構造。

---

## 4. ドキュメント構造

> AI が自律的に実装を進めるために必要な情報を `docs/` に体系化する (ADR 0027)。各 docs は **冒頭 5 行以内の summary + 詳細 lazy-load** の構造を必須化し、AI のコンテキストを圧迫しないようにする (R-32)。重複・矛盾は `traceability.md` と Konsist で機械検証 (R-33)。

```
DESIGN.md                       ← リポジトリ root に配置 (Google Stitch 標準準拠)
                                  上部: design tokens (machine-readable, 3 階層)
                                  下部: rationale (human-readable, アクセシビリティ含む)
CHANGELOG.md                    ← (将来) 運用後追加、dependency-upgrade Skill が更新

docs/
  README.md                     ─ ★AI 用エントリポイント。全 docs 索引 + 推奨読み順 (CLAUDE.md → このファイル → 関連ディレクトリ)
  glossary.md                   ─ ★新規 ドメイン用語集 (im@sparql / RDF / アイドル / ブランド / カラー / 担当 / 推し / SPARQL prefix 等。日本語 + 英訳 + 関連リンク)
  codebase-map.md               ─ ★新規 主要パス → 責務 / 関連 SPEC-ID / 関連 ADR の対応表 (rules-index と相補)
  traceability.md               ─ ★新規 Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装 のクロスリンク表。A6 で Konsist による自動生成導入

  architecture/                 ─ ★大幅拡充 (旧 overview.md 1 つから分割)
    overview.md                 ─ モジュール依存図 (Mermaid)
    layers.md                   ─ ★新規 層別責務 (feature/* / core/* / backend/*)
    data-flow.md                ─ ★新規 im@sparql → backend → frontend の流れ + Litestream/R2
    domain-model.md             ─ ★新規 RDF (im@sparql) ⇄ SQLite ⇄ Kotlin model の三層マッピング
    state-machines.md           ─ ★新規 主要 UiState 遷移 (Mermaid stateDiagram)
    sequences.md                ─ ★新規 認証 / API 呼出 / 同期パイプライン (Mermaid sequence)
    infrastructure.md           ─ ★新規 Cloud Run + Cloudflare Pages + R2 + GIS の構成図

  api/                          ─ ★新規ディレクトリ
    README.md                   ─ API 全体像
    colormaster-api.yaml        ─ OpenAPI 3.1 (機械可読、code-reviewer の spec-conformance aspect が参照)
    auth.md                     ─ GIS ID Token 検証フロー詳細
    idols.md                    ─ /api/idols/* (アイドルマスタ参照)
    me.md                       ─ /api/me/* (ユーザーデータ CRUD、PII redaction 規約)

  security/                     ─ ★新規ディレクトリ
    README.md                   ─ ADR 0011 (Firebase 廃止) / 0023 (PII + 権限ロール) / 0024 (Secrets) / 0028 (Skill authoring) / 0030 (docs structure) の索引

  requirements/                 ─ 機能要件
    README.md                   ─ ★新規 命名規約 (REQ-NNN) + SPEC-ID 採番ルール + 状態遷移
    template.md                 ─ ★新規 日本語テンプレ
    REQ-NNN-<slug>.md           ─ 機能ごと

  specifications/               ─ 仕様詳細
    README.md                   ─ ★新規 基本設計 / 詳細設計の使い分けガイド
    basic-template.md           ─ ★新規 基本設計テンプレ (Acceptance criteria + SPEC-NNN-N 採番)
    detail-template.md          ─ ★新規 詳細設計テンプレ (実装パス + シーケンス + データ構造)
    SPEC-<id>-basic.md          ─ 機能ごと、基本設計
    SPEC-<id>-detail.md         ─ 機能ごと、詳細設計

  adr/                          ─ Architecture Decision Records (起票基準は ADR 0001 と .claude/rules/adr.md に明文化)
    README.md                   ─ ADR 起票基準と運用ガイド (判断フロー図、ADR にすべき例/他の方法にすべき例)
    template.md                 ─ MADR + 日本語化された雛形 (巻末に「ADR 化すべき例」「他の記録方法にすべき例」を列挙)
    0001-record-architecture-decisions.md                       ─ ADR 運用基準・書式・起票判断フロー (本計画 4.5 節を要約)
    0002-app-architecture-cmp-viewmodel-nav3.md
    0003-module-structure-feature-first.md
    0004-test-strategy-and-coverage.md                          ─ テスト戦略総論 (三層指標へのインデックス)
    0005-decompose-removal.md
    0006-i18n-compose-resources.md
    0007-imasparql-sync-upstream-driven.md
    0008-user-data-backend-sqlite-litestream-r2.md
    0009-backend-hosting-cloud-run.md                           ─ Backend ホスティングは Cloud Run、代替候補 Koyeb、Cloudflare Containers は $5/月で不採用
    0010-idol-master-sqlite-in-repo.md
    0011-auth-stack-migration-from-firebase-to-gis.md           ─ ★統合 (旧 0011 Firebase 廃止 + 旧 0021 GIS 統一)。認証スタック転換を 1 ADR で表裏一体に記録、dev.gitlive:firebase-* と core/network/{auth,firestore} を撤去し全 target で GIS に統一、Backend で JWKS 検証
    0012-remove-js-app.md
    0013-line-branch-coverage-100-percent.md
    0014-imasparql-local-docker-fuseki.md
    0015-mutation-testing-pitest.md
    0016-spec-coverage-traceability.md
    0017-harness-loop-local-polling.md
    0018-implementation-workflow.md                             ─ implementation-workflow Skill の 8 フェーズ設計 (Generator 側)
    0019-code-review-aspects-coordinator.md                     ─ code-reviewer Skill の 8 aspect + Coordinator 設計 (Evaluator 側)
    0020-pii-protection-and-permission-roles.md
    0021-secrets-management-policy.md
    0022-cloudflare-pages-and-r2.md                             ─ 静的配信は Cloudflare Pages、Litestream バックアップ先は R2。Cloud Run (ADR 0009) と組み合わせてハイブリッドホスティングを構成
    0023-ui-design-freeze-with-visual-regression.md             ─ ★統合 (旧 0025 UI 凍結三本柱 + 旧 0026 Roborazzi)。DESIGN.md + UI Inventory + Roborazzi baseline、Compose Desktop で commonMain を screenshot、解像度マトリックス (mobile + PC 16:9) × Light/Dark、wasmJs 未対応への対処
    0024-mcp-servers-jetbrains-context7-cloudflare.md           ─ JetBrains MCP + Context7 MCP + Cloudflare MCP の採用、GitHub MCP は gh CLI で代替、Sourcegraph / Serena MCP は不採用、Figma / Sentry は将来検討
    0025-skill-authoring-with-skill-creator.md                  ─ Skill 作成は example-skills:skill-creator を経由、Anthropic Complete Guide 準拠 (description=trigger, Gotchas 必須, MUST/ALWAYS/NEVER 禁止)
    0026-harness-evolution-from-external-research.md            ─ 外部研究 / ベストプラクティス駆動の改善ループ、手動起動のみ、harness-meta (内部 KPT) と二系統補完
    0027-documentation-structure-and-language.md                ─ ★統合 (旧 0030 documentation-structure + 旧 0020 template-language)。AI 駆動実装の docs 構造 + 命名規約 + 5 行 summary + lazy-load + **ハーネスを構成する Markdown は全て日本語で記述する** 方針
    # 統合・削除の履歴
    #   旧 0004 state-and-uiaction-conventions     → .claude/rules/{viewmodel,ui-state}.md に統合
    #   旧 0011 firebase-removal-complete           → 新 0011 (GIS と統合)
    #   旧 0020 template-language-japanese          → 新 0027 (documentation-structure と統合)
    #   旧 0021 gis-unified-authentication          → 新 0011 (Firebase 廃止と統合)
    #   旧 0025 ui-design-snapshot-before-refactor  → 新 0023 (Roborazzi と統合)
    #   旧 0026 visual-regression-testing-roborazzi → 新 0023 (UI 凍結三本柱と統合)
    #   旧 0026 permission-roles-owner-only         → 新 0020 内のセクション
    #   旧 0030 documentation-structure-for-ai-autonomy → 新 0027 (日本語化方針と統合)
    # 一度統合したが取り消したもの (現在は独立維持)
    #   旧 0009 (Cloud Run) と 旧 0022 (Cloudflare Pages + R2) は独立 ADR (ベンダー別、片方の変更が他方に波及しない)
    #   旧 0018 (implementation-workflow) と 旧 0019 (code-reviewer) は独立 ADR (各論が大きく独立 ADR の方が読みやすい)
  epics/                        ─ 複数 PR の取り組み (1 epic = 1 ディレクトリ)
    INDEX.md
    template/
      README.md
      roadmap.md
      open-questions.md
      decisions.md
      progress.md
    EPIC-000-harness-foundation/
      README.md
      roadmap.md
      open-questions.md
      decisions.md
      progress.md
    archive/
  plans/                        ─ 単一 PR の取り組み (1 plan = 1 ファイル)
    INDEX.md
    template.md
    archive/
  harness/                      ─ ハーネス本体に関するドキュメント
    plan.md                     ─ 本ドキュメント
    overview.md
    workflow.md
    skills.md
    kpt-template.md
    learnings/                  ─ PR ごとの KPT 出力 (1 PR = 1 ファイル、Single Source of Truth)
      YYYY-MM-DD-pr-NNN.md      ─ pr-retrospective が生成、harness-meta が "feedback" セクションを追記
    evolution-proposals/        ─ ★新規 harness-evolution が生成する外部研究駆動の改善提案
      INDEX.md
      YYYY-MM-DD.md             ─ 1 実行 = 1 ファイル、出典 URL + 引用日付 + 構造化改善案 (採用 → Plan/EPIC 起票へリンク)
  runbooks/
    local-development.md        ─ ★新規 環境構築 (JDK / Gradle / IntelliJ 2025.2+ / Docker / Node.js / wrangler / gcloud / Roborazzi 等の前提環境 + setup 手順)
    local-imasparql.md          ─ Fuseki Docker でローカル im@sparql 起動 (A8 で本格化)
    sync-imasparql.md           ─ 同期パイプライン操作・手動再実行・失敗対応
    release.md                  ─ Cloud Run + Cloudflare Pages + R2 デプロイ手順 (B0 骨格、C7 で本格化)
    secrets-rotation.md         ─ R2 token / GIS Client Secret 等のローテーション履歴
    mcp-setup.md                ─ JetBrains / Context7 / Cloudflare MCP の接続手順
    testing.md                  ─ ★新規 三層指標の運用 + Roborazzi baseline 操作
    i18n.md                     ─ ★新規 composeResources 運用、文言 ID 命名、locale 追加手順
    troubleshooting.md          ─ (将来追加) 既知問題 / エラーカタログ / FAQ
  design/                       ─ ★新規 UI/UX デザインの現状記録 (ADR 0023 で定義)
    README.md                   ─ DESIGN.md / Inventory / Baseline の運用ガイドと参照リンク
    inventory/
      INDEX.md
      screens/                  ─ 画面ごと (home.md, search.md, preview.md, myidols.md, ...)
      components/               ─ コンポーネントごと (idol-card.md, brand-chip.md, ...)
      states/                   ─ 状態別パターン (empty.md, loading.md, error.md, ...)
      flows/                    ─ ユーザーフロー (login.md, add-favorite.md, ...)
      screenshots/              ─ Roborazzi が生成する baseline PNG 群
        <composable>-mobile-light.png
        <composable>-mobile-dark.png
        <composable>-desktop-light.png
        <composable>-desktop-dark.png
```

### 4.1 Epic と Plan の区別

| 項目 | Epic | Plan |
|---|---|---|
| スコープ | 複数 PR、複数週 | 単一 PR、短期 |
| 配置 | `docs/epics/EPIC-NNN-<slug>/` (5 ファイル) | `docs/plans/PLAN-NNN-*.md` (1 ファイル) |
| 採番 | EPIC-NNN (連番) | PLAN-NNN (連番、Epic とは独立) |
| open-questions / decisions / progress | 個別ファイルで持つ | 持たない (PR 本文と KPT learning で代替) |
| Notes | progress.md などに記録 | Plan ファイル内に自由記述 (構造が見えたら template に反映) |
| 昇格 | (Epic は昇格元) | Plan → Epic に昇格可能。元 Plan は `status: promoted` + `promoted_to: EPIC-NNN` |

判定しきい値 (Skill 内で判断、rules で上書き可能):

- **Plan**: 想定変更ファイル数 ≤ 10、想定期間 ≤ 1 週間、open question 想定なし。
- **Epic**: 上記を超える、または最初から複数マイルストーン明示。
- 迷ったら **Plan で開始し、必要に応じて昇格**。

### 4.2 Epic 紐付き Plan

Epic 配下で複数 PR を出す場合の個別 Plan は、`docs/plans/` に一元化し `related_epic: EPIC-NNN` で紐付ける。`docs/epics/<id>/plans/` には置かない。

### 4.3 ADR と Epic / Plan の関係

- 設計判断が発生したら ADR を起こす (`adr-author` Skill)。
- Epic 配下の `decisions.md` は ADR より細粒度な「保留 → 解決」のトレース。重要な決定は ADR に昇格させ、`decisions.md` から ADR をリンクする。
- 各 ADR は関連 Epic / Plan / PR をフッターで参照する。

### 4.4 KPT learnings との関係

- **Single Source of Truth はファイル**: 各 PR マージ / クローズ後に `pr-retrospective` Skill が `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を生成 (PR コメントは出さない)。
- ファイル内に Keep / Problem / Try / Metrics / Suggested harness changes のセクションを構造化フォーマットで記録 (`.claude/rules/retrospective-format.md`)。
- **生成タイミング**: ローカル Claude Code 上で `pr-poller` Skill がポーリング起動 (詳細は 5.3 節)。
- **harness-meta による後追記**: `harness-meta` Skill が改修 PR を起票するたびに、元 learning ファイルの末尾に `## 📝 harness-meta feedback` セクションを追記 (採用/見送り/保留の判定理由)。これにより learning ファイルが「提案 → 結果」の往復ログを担う Single Source of Truth として完結する。
- ファイル commit 方法: `pr-retrospective` が `harness/learnings-batch` 等のブランチに集約し、週次 (or 一定件数到達時) に PR としてまとめて起票。人間レビューを通してマージ。

### 4.5 ADR の起票基準と書式

Michael Nygard の原則 ("Architecturally Significant Decisions" のみ記録、"Any Decision Records" ではない) + AWS / Microsoft / Google / Martin Fowler の ADR ガイドラインに準拠する。本節の内容は ADR 0001 と `.claude/rules/adr.md` に明文化する。

#### 起票基準: 以下の 2 つ以上を満たすとき ADR を起こす

1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
2. 主要なライブラリ / フレームワークの採用または撤去
3. 外部サービスの採用または変更 (DB / ホスティング / 認証 / CDN / etc.)
4. データ永続化 / 同期戦略 / バックアップ方式
5. テスト戦略・品質指標の中核方針
6. セキュリティ・プライバシー・ライセンスに関する方針
7. ハーネス本体の中核設計 (Skill 構成、ループ構造、ローカル vs サーバ実行)
8. 複数の代替案を比較した結果としての判断
9. 元に戻すコストが高い決定
10. 長期的な制約 (今後 1 年以上の判断のベースになるもの)

#### ADR にすべき例 (本リポジトリの実例)

- Compose Multiplatform + 共通 ViewModel + Navigation 3 を採用する (ADR 0002)
- Decompose を撤去する (ADR 0005)
- Firebase を完全廃止して GIS に統一する (ADR 0011)
- Backend は Cloud Run (ADR 0009)、静的配信は Cloudflare Pages、Litestream バックアップ先は R2 (ADR 0024) — 2 ADR の組合せで一体運用
- アイドル情報を Git 内 SQLite に commit、ユーザーデータは Litestream で R2 にレプリケート (ADR 0008 / 0010)
- Line/Branch coverage 100% を必達ゲートにする (ADR 0013)
- ハーネスループをローカル Claude Code ポーリングで駆動する (GitHub Actions で Claude API を呼ばない) (ADR 0017)
- implementation-workflow (ADR 0018) + code-reviewer (ADR 0020) の Generator/Evaluator 二段構成、code-reviewer は 8 aspect + Coordinator
- ハーネスを構成する Markdown は全て日本語で記述する (ADR 0027 に統合、旧 0020 単独 ADR は廃止して新 0025 内のセクションに)
- PII の DB スキーマは `uid` のみ、権限ロールは当面 owner のみ (ADR 0020)
- UI/UX をリファクタ前に DESIGN.md + UI Inventory + Roborazzi baseline で凍結する (ADR 0023)
- **MCP サーバは JetBrains MCP + Context7 MCP + Cloudflare MCP の 3 つを採用** (ADR 0024):
  - **JetBrains MCP** (IntelliJ IDEA 2025.2+ 組み込み): Skill が IDE 経由で rename / inspection / IDE index 検索 / build を操作
  - **Context7 MCP**: Kotlin / Compose MP / Ktor / SQLDelight / Roborazzi 等のバージョン固有ドキュメントを LLM に注入、ハルシネーション抑止
  - **Cloudflare MCP**: R2 / Pages / DNS / Secrets 管理
  - **GitHub MCP は採用見送り**、`gh` CLI で代替 (実証ベンチマークで MCP は CLI より 10〜32 倍トークン消費、ColorMaster 用途では CLI が優位)
  - **Sourcegraph MCP は採用見送り**、JetBrains MCP の IDE indexing で代替
  - **Serena MCP は採用見送り**、JetBrains MCP + Context7 MCP で代替 (Kotlin は "Indirect Support" にとどまり Compose MP / KMP / wasmJs 解析で IntelliJ Kotlin Plugin に劣る、JetBrains backend は有料、30 分のセットアップ + 初回インデックスのオーバーヘッド)。ただし IDE 非起動環境 (CI 上 agent) で fallback が必要になったら別 Plan で再評価
  - 将来検討: Figma MCP (Figma 資産を作る場合) / Code Pathfinder (高度なコード検索が必要なら) / Serena MCP (上記条件成立時) / Sentry MCP (本番後)
- **Skill 作成は `example-skills:skill-creator` 経由**: Claude Code ユーザースコープにインストール済の Anthropic 公式 Skill を使用、本リポジトリには配置しない。SKILL.md は Anthropic "Complete Guide to Building Skills for Claude" 準拠 (description=trigger、Gotchas 必須、MUST/ALWAYS/NEVER 禁止)、AgentSkills 2026 spec + 100-point rubric (ADR 0025)
- **ハーネス進化は内部 + 外部の二系統**: `harness-meta` (内部 KPT 駆動、週次 cron + 閾値 + 手動) と `harness-evolution` (外部研究 / ベストプラクティス駆動、**手動起動のみ**、Claude API コスト抑制のため cron 不採用) を補完併用 (ADR 0026)。harness-evolution の出力は `docs/harness/evolution-proposals/YYYY-MM-DD.md`
- **GitHub Actions 上での Claude API 実行はゼロ**: 既存方針 (pr-poller / pr-retrospective / harness-meta のローカル化) に加え、**`dependency-upgrade` Skill 起動も pr-poller がローカルで Renovate ラベル PR を検出して実行** に統一。`upstream-driven 同期`・CI・trufflehog secret-scan は Claude API を使わないため GitHub Actions のまま運用
- **AI 駆動実装のための docs 構造**: `docs/` に **glossary / codebase-map / traceability / architecture (6 分割) / api (OpenAPI 3.1) / security / requirements / specifications (basic + detail)** を体系化 (ADR 0027)。各 docs は **冒頭 5 行 summary + 詳細 lazy-load** 構造、命名規約 (REQ-NNN / SPEC-NNN-N / EPIC-NNN / PLAN-NNN / ADR-NNNN) を `.claude/rules/docs-structure.md` で明文化。`docs/traceability.md` は A6 で Konsist 自動生成。`docs/README.md` を **AI 用エントリポイント** として推奨読み順を明記

#### ADR にすべきでない例 (他の記録方法を使う)

| 例 | 適切な記録方法 |
|---|---|
| `@Composable` 関数の引数命名規約 | `.claude/rules/composable.md` |
| Kotlin の null 安全 / Result 型のコーディングルール | `.claude/rules/error-handling.md` |
| PR テンプレートに `Related plan` 行を追加する | `.claude/rules/pr-template.md` |
| `HomeViewModel.refresh()` 内のキャッシュ TTL を 5 分に変更する | Plan (`docs/plans/PLAN-NNN-*.md`) |
| EPIC-001 内で `SavedStateHandle` をどうシリアライズするか一時保留 | Epic の `open-questions.md` / 解決時は `decisions.md` |
| 単発のバグ修正の進め方 | Plan ファイル |
| テスト fixture に `@example.com` ドメインを使う規約 | `.claude/rules/pii.md` |
| Compose Preview の表記揺れを統一する | `.claude/rules/composable.md` (KPT 起点で追記) |
| 「Skill 実行時のログを `.claude/logs/` に追記する」運用 | `.claude/rules/pr-poller.md` などの該当 Skill 規約 |
| Cloud Run の `min-instances` を 0 から 1 に変更する | runbook (`docs/runbooks/release.md`) + Plan |
| 開発者がローカルで Fuseki Docker を起動する手順 | runbook (`docs/runbooks/local-imasparql.md`) |
| im@sparql 同期 PR の自動マージ可否 | sync workflow の設定 + 該当 Skill 規約 |

#### 採番・命名・ステータス

- 連番、4 桁ゼロパディング (`0001`, `0002`, ...)
- ファイル名: `{NNNN}-{kebab-case-title}.md`
- タイトル: 簡潔・現在形・断定的
- ステータス: `proposed` → `accepted` → `deprecated` | `superseded by ADR-NNNN` (MADR 4 状態)
- `accepted` 以降は immutable。変更時は新 ADR を起こし、旧 ADR を `Superseded by` でリンク
- 採番欠番は実装前なら整理可、運用後は番号を維持して `withdrawn` を許容
- 言語は日本語 (ADR 0027)

#### ADR と他ドキュメントの使い分け

判断フロー (`docs/adr/README.md` にも記載):

```
新しい決定が発生
   │
   ├─ ADR 起票基準の 2 項目以上を満たす? ──── Yes ──→ ADR を起こす (adr-author Skill)
   │
   ├─ コーディング/スタイル/命名規約? ───── Yes ──→ .claude/rules/*.md に追記
   │
   ├─ Epic 内の細粒度な保留→解決? ─────── Yes ──→ Epic の open-questions / decisions.md
   │
   ├─ 1 PR で完結する判断? ────────────── Yes ──→ Plan or PR description
   │
   ├─ 運用手順? ──────────────────────── Yes ──→ docs/runbooks/
   │
   └─ PR ごとの学びや改善案? ─────────── Yes ──→ docs/harness/learnings/ (pr-retrospective)
```

#### `.claude/rules/adr.md` の責務

- 上記の起票基準・例リスト・採番ポリシー・ステータス遷移・判断フローを保持
- `adr-author` Skill が新規 ADR 起草時に必ず Read する
- ADR テンプレ (`docs/adr/template.md`) の巻末にも「ADR 化すべき例 / 他の記録方法にすべき例」を簡潔に列挙し、人間が ADR を書くときも基準にアクセスしやすくする

---

## 5. ハーネス構造 (`.claude/`)

```
.claude/
  settings.json
  mcp.json                      ─ ★新規 MCP サーバ接続定義 (GitHub / Cloudflare、Figma は将来)
  skills/
    harness-bootstrap/          B0 の唯一の Skill。A3 完了後に archived/ へ
    plan-author/                B0 から導入
    epic-author/                B0 から導入
    pr-retrospective/           B0 から最小版で稼働 (1 PR = 1 learning ファイル生成)
    pr-poller/                  B0 から導入 (ローカル Claude Code 内でポーリング、未処理 PR を検出して pr-retrospective を起動)
    implementation-workflow/    B0 で雛形、A3 で本格化 (要件読込 → 実装 → Lint/Test → AI Review → マージ判断 → レトロ起動を 8 フェーズでオーケストレート)
    code-reviewer/              B0 で雛形、A3 で本格化 (独立 Evaluator、★8 aspect 並列レビュー + Coordinator で構造化コメント)
    ui-snapshot/                B0 で雛形、A10 で本格化 (Preview スキャン、Roborazzi baseline 生成、DESIGN.md / UI Inventory 起草)
    feature-request/            A3 で完成 (要件・仕様生成と Plan/Epic 起票まで、実装はしない)
    bug-fix/                    A3 で完成 (再現・ルートコーズ・仕様補強・Plan 起票まで)
    refactor/                   A3 で完成 (影響分析・Plan/Epic 起票まで)
    dependency-upgrade/         A3 で完成 (pr-poller がローカルで Renovate PR を検出して起動)
    adr-author/                 A3 で完成
    harness-meta/               A3 で完成 (改修 PR 起票 + 元 learning ファイルへの feedback 追記、内部 KPT 駆動)
    harness-evolution/          A3 で完成 (★新規、外部研究 / ベストプラクティス駆動、手動起動のみ)
    # skill-creator は本プロジェクトには配置しない (Claude Code ユーザースコープ example-skills:skill-creator を参照)
    archived/                   引退した Skill (harness-bootstrap など)
  rules/
    rules-index.md
    # 計画・記録
    plan.md
    epic.md
    adr.md                      ─ ADR 起票基準 / 採番 / ステータス遷移 / ADR 化すべき例・他の記録方法にすべき例の列挙 (4.5 節を要約)
    # アーキテクチャ層別
    viewmodel.md
    ui-state.md
    composable.md
    navigation.md
    repository.md
    network-client.md
    # 横断的関心事
    naming.md
    error-handling.md
    logging.md
    i18n.md
    wasm-compat.md
    firebase-boundary.md
    # ファイルタイプ別 / テスト
    gradle.md
    kotlin-test.md
    screenshot-test.md         ─ Roborazzi 運用規約: JVM (Compose Desktop) + Android、解像度マトリックス (mobile + PC 16:9) × Light/Dark、wasmJs 固有 actual は対象外
    coverage-100.md            ─ Line/Branch 100% 規約 (指標 A)
    spec-traceability.md       ─ @Spec annotation / Spec coverage 規約 (指標 B)
    mutation-testing.md        ─ PITest 運用規約 (指標 C)
    test-paired-class.md
    sql-delight.md
    sparql.md
    # UI/UX デザイン (新規)
    design-tokens.md           ─ DESIGN.md 構造 (Primitive/Semantic/Component 3 階層)、hex/sp/dp ハードコード禁止
    ui-snapshot.md             ─ Preview + screenshot baseline 維持、baseline 更新は human approve 必須
    ui-inventory.md            ─ docs/design/inventory/ のファイル構造と更新規約 (screens/components/states/flows)
    behavior-preservation.md   ─ リファクタ時の振る舞い維持原則 (visual regression + spec-conformance 両輪)
    # MCP (新規)
    mcp-usage.md               ─ MCP サーバ (GitHub / Cloudflare / 将来 Figma) の使い分け、認証情報の取り扱い、Skill 別の使用パターン
    # プロセス
    pr-template.md
    commit-message.md
    branch-naming.md
    # ハーネス改善ループ
    retrospective-format.md    ─ pr-retrospective が出力する learning ファイルの構造化フォーマット
    pr-poller.md               ─ ローカルポーリング規約 (CronCreate/ScheduleWakeup、未処理 PR 判定、Renovate ラベル PR の検出と dependency-upgrade 起動も担当)
    harness-meta-criteria.md   ─ 改修 PR 採用/見送り/撤去の判定基準 (Anthropic harness 原則を明文化)
    skill-authoring.md         ─ ★新規 Skill 作成は example-skills:skill-creator を経由、SKILL.md 構造 (description trigger / Gotchas 必須 / MUST/ALWAYS/NEVER 禁止) を Anthropic Complete Guide 準拠
    harness-evolution.md       ─ ★新規 外部情報源ホワイトリスト、出力フォーマット、harness-meta との責務分離、出典 URL + 引用日付の必須化、Context7 MCP で API 引用検証
    docs-structure.md          ─ ★新規 docs/ の歩き方 (AI が読む順序)、各ディレクトリの責務、命名規約、5 行 summary + lazy-load 構造、code-reviewer / implementation-workflow が参照する場面
    # 実装ワークフロー
    implementation-workflow.md  ─ 8 フェーズの手順、fix loop の上限 (デフォルト 3 回)、Phase 失敗時の Plan 修正提案
    code-reviewer-aspects.md    ─ 8 aspect (spec-conformance / test-quality / architecture / security / performance / code-quality / visual-regression / design-tokens) の binary eval checklist、coordinator の構造化コメント形式
    merge-readiness.md          ─ Merge 可否の判定基準 (CI green + 全 aspect pass + 人間 approve、auto-merge 禁止)
    pr-draft-policy.md          ─ Draft → Ready for review の昇格条件
    spec-living-sync.md         ─ 実装中の仕様変更時の双方向同期手順 (Spec Kit / Intent 由来)
    # ドキュメント表記
    markdown.md                 ─ Markdown 表記規約 (テンプレート言語ポリシーを含む)
    template-language.md        ─ ★新規 全テンプレート Markdown は日本語で記述 (ADR 0027 を要約)
    # 同期 / Backend
    sync-job.md
    sqlite-data-file.md
    backend-auth.md             ─ GIS ID Token 検証 + JWKS + uid 抽出 規約
    cloud-run-deploy.md
    removed-modules.md
    # セキュリティ / 個人情報
    pii.md                      ─ ★新規 PII の定義・最小化・redaction 強制
    secrets.md                  ─ ★新規 Secrets 管理 (.env / GitHub Secrets / Secret Manager)
    db-protection.md            ─ ★新規 users.db の commit / イメージ焼込み禁止、R2 private、access policy
    no-firebase.md              ─ ★新規 (旧 firebase-boundary.md を改名) Firebase 系 import 禁止
    cloudflare-pages.md         ─ ★新規 Cloudflare Pages デプロイ規約
    r2-litestream.md            ─ ★新規 Litestream replicate / restore、R2 endpoint、TTL ローテーション
```

### 5.1 ルール参照の階層構造

CLAUDE.md (常時ロード) に lookup table を持ち、編集対象ファイルに対応する rules を必ず Read する規約とする。

```
| パターン                                       | 参照する rules                          |
| feature/**/*ViewModel.kt                       | viewmodel.md, ui-state.md, repository.md |
| feature/**/*Screen.kt                          | composable.md, i18n.md, navigation.md   |
| feature/**/*UiState.kt, *UiAction.kt           | ui-state.md                             |
| **/Route.kt                                    | navigation.md                           |
| **/composeResources/**/strings.xml             | i18n.md                                 |
| core/data/**/*Repository*.kt                   | repository.md, error-handling.md        |
| core/network/**/*Client*.kt                    | network-client.md, error-handling.md    |
| **/*.sq                                        | sql-delight.md                          |
| **/*Spec.kt, **/*Test.kt                       | kotlin-test.md                          |
| **/build.gradle.kts                            | gradle.md                               |
| docs/adr/*.md                                  | adr.md                                  |
| docs/{requirements,specifications,architecture,api,security}/**  | docs-structure.md                        |
| docs/api/colormaster-api.yaml                  | docs-structure.md, network-client.md, backend-auth.md |
| docs/glossary.md, docs/codebase-map.md, docs/traceability.md     | docs-structure.md                        |
| docs/epics/**/                                 | epic.md                                 |
| docs/plans/*.md                                | plan.md                                 |
```

### 5.2 機械的ガード (二重化)

- **Konsist**: 構造的制約をテスト化。
  - `feature/**/*ViewModel.kt` は `androidx.lifecycle.ViewModel` を継承する。
  - `feature/**/*Screen.kt` の最上位 `@Composable fun *Screen(` 引数に `uiState: *UiState` を含む。
  - `core/network/{auth,firestore}/` 以外で `dev.gitlive.firebase.*` の import を禁止。
- **detekt**: 文字列リテラル直書き検出、命名規約。
- **Gradle カスタムタスク**: `firebase-boundary.md` 等の境界違反を git grep で簡易チェック。

これにより「AI がルールを読んだ/読まない」に依存しない安全網を作る。

### 5.3 Skill の責務

| Skill | 起動契機 | 主な動作 |
|---|---|---|
| `harness-bootstrap` | B0 のみ。手動起動 | 専用 Skill 群が揃うまでの汎用 Skill。タスク種別 (ADR 起草 / rules 追加 / docs 拡充 / モジュール撤去 / Lint 導入) に応じた汎用手順を実行。A3 完了後に `archived/` へ |
| `plan-author` | feature-request / bug-fix / refactor が単一 PR スコープと判定 | `docs/plans/PLAN-NNN-*.md` を 1 ファイル生成、INDEX.md 更新 |
| `epic-author` | 同上、複数 PR スコープと判定。または Plan 昇格時 | `docs/epics/EPIC-NNN-<slug>/` を template から生成、INDEX.md 更新 |
| **`pr-poller`** | ローカル Claude Code 起動時 + `CronCreate` 日次スケジュール + `ScheduleWakeup` ループ | gh CLI で merged / closed PR を取得 → 既に learning ファイルがある PR を除外 → 未処理 PR があれば `pr-retrospective` を起動 → 一定期間経過 (7 日) または未処理 learning 件数閾値到達で `harness-meta` を起動。**さらに open PR で `labels:renovate` が付くものを検出して `dependency-upgrade` Skill を起動** (GitHub Actions で Claude API を呼ばないためのローカル化)。詳細規約は `.claude/rules/pr-poller.md` |
| **`pr-retrospective`** (旧 `kpt-retrospective`) | `pr-poller` から自動起動 / 手動起動 | 対象 PR の diff (`gh pr diff`) / comments / reviews / CI ログ / Skill 実行ログ / 三層指標差分 (Kover / Konsist / PITest) / 関連 Plan・Epic を収集。`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を `.claude/rules/retrospective-format.md` の構造化フォーマット (**日本語見出し**) で生成。`harness/learnings-batch-YYYY-WW` ブランチに集約し、週次 (or 件数到達時) に PR としてまとめて起票 |
| **`implementation-workflow`** | Plan / Epic 確定後、ユーザー指示で起動 | **新規 (オーケストレーター)**: 8 フェーズで全工程を統合管理 — (1) 要件/基本設計/詳細設計 Markdown 読込、(2) Spec 整合性チェック、(3) 実装 + Lint/Test 実行 + fix loop (上限 3 回)、(4) Self-Verification、(5) Draft PR 作成、(6) `code-reviewer` 呼出 → Evaluator フェーズ、(7) 人間 approve → squash merge、(8) `pr-poller` 即時起動。詳細規約は `.claude/rules/implementation-workflow.md`、`merge-readiness.md`、`pr-draft-policy.md`、`spec-living-sync.md` |
| **`code-reviewer`** | `implementation-workflow` Phase 6 から呼出 / 手動起動 | **新規 (独立 Evaluator)**: 8 aspect を並列実行 — spec-conformance / test-quality / architecture / security / performance / code-quality / **visual-regression** / **design-tokens** (後ろ 2 つは A10 完了後に有効化)。各 aspect が binary yes/no eval checklist を実行。Coordinator が重複排除して **日本語の構造化レビューコメント** を PR に post し、Merge readiness を判定。詳細規約は `.claude/rules/code-reviewer-aspects.md`。Anthropic Evaluator 独立性原則に従い、aspect ごとに別の system prompt を持たせる |
| **`ui-snapshot`** | A10 内の Plan、Phase C 各リファクタ後の visual regression 検証 | **新規**: Konsist で Composable をスキャン → `@Preview` 不在を検出 → Plan 起票、Roborazzi で 4 パターン (mobile/desktop × Light/Dark) baseline 生成、DESIGN.md と UI Inventory のドラフトを起草、hex / sp / dp ハードコードを検出して tokens 化提案 |
| `feature-request` | ユーザー指示 or Issue | 要件 → 基本設計 → (必要なら詳細設計) → ADR (必要時) → Plan / Epic 起票 **まで** (実装はしない、`implementation-workflow` にバトンタッチ) |
| `bug-fix` | Issue / 障害報告 | 再現 → ルートコーズ分析 → 仕様補強 → ADR (設計起因なら) → Plan 起票 **まで** |
| `refactor` | 改善提案 | 影響範囲分析 → リスク評価 → Plan or Epic 起票 **まで** |
| `dependency-upgrade` | **`pr-poller` が `labels:renovate` 付き open PR を検出** (ローカル Claude Code) / 手動起動 | Context7 MCP でリリースノート fetch → JetBrains MCP で影響モジュール特定 → `./gradlew check` でテスト → `gh pr comment` で結果サマリ投稿 → 安全なら `approve` ラベル付与、危険なら downgrade 提案 / Plan 起票 |
| **`skill-creator`** | 他 Skill (harness-bootstrap / harness-meta / harness-evolution) や人間からの「新規 Skill 作成 / 既存 Skill 改修」要求 | **Claude Code のユーザースコープにインストールされている `example-skills:skill-creator`** を呼び出して使用 (本プロジェクトリポジトリ内に mirror しない)。SKILL.md scaffolding、description を trigger 含めて適正化、Gotchas セクション必須化、AgentSkills 2026 spec + Anthropic "Complete Guide to Building Skills for Claude" 準拠、100-point rubric 評価 |
| **`harness-evolution`** | **手動起動のみ** (ユーザーが必要時に Claude Code で起動、cron は採用しない) | (1) WebSearch / WebFetch + Context7 MCP で外部ベストプラクティス取得 (Anthropic engineering blog / anthropics/skills の更新 / Claude Code docs / MCP spec / awesome-harness-engineering / arxiv / Martin Fowler / Red Hat Developer / HumanLayer / Augment Code 等のホワイトリスト)、(2) 既存 `.claude/skills/` / `.claude/rules/` / `docs/` と gap 分析、(3) 改善提案 (新規 Skill / 既存 Skill 改修 / rules 強化 / 廃止候補 / 新 MCP 採用余地) を構造化リスト化、(4) `docs/harness/evolution-proposals/YYYY-MM-DD.md` 生成、(5) 重要案は `skill-creator` 経由で Skill scaffold or Plan/EPIC 起票 (人間 approve 必須) |
| `adr-author` | 他 Skill から呼ばれる | ADR テンプレに沿って起草、関連 ADR をリンク |
| `harness-meta` | 月次 cron or learnings 閾値超過 | learnings の `Try` / `Suggested harness changes` を集約 → ハーネス改修 PR を起票 |

### 5.4 Skill + KPT ループ (Spec Gen / Implementation / Evaluation / Merge / Retrospection / Meta)

Anthropic の Planner / Generator / Evaluator パターン + Cloudflare の specialized reviewer + coordinator + GitHub Agentic Workflows の human-in-the-loop に準拠した 6 段階ループ。

```
┌── ① Spec Gen フェーズ ───────────────────────────────────┐
│ [feature-request / bug-fix / refactor / dependency-upgrade] │
│    → 要件/基本設計/詳細設計 生成 (docs/requirements,        │
│      specifications)                                       │
│    → ADR (必要時) / Plan / Epic 起票                       │
│    実装は次のフェーズへ                                     │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼ Plan / Epic を引き継ぐ
┌── ② Implementation フェーズ ─────────────────────────────┐
│ [implementation-workflow]  (オーケストレーター)            │
│   Phase 1: 要件/基本設計/詳細設計 Markdown を Read         │
│   Phase 2: Spec 整合性チェック (SPEC-ID 採番確認)           │
│   Phase 3: rules-index → 実装 + Lint + Test (fix loop ≤3) │
│   Phase 4: Self-Verification (三層指標 + rules)            │
│   Phase 5: Draft PR 作成 (gh pr create --draft)            │
│   Skill 実行ログを .claude/logs/ に逐次追記                │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼ (Generator と独立した Evaluator を呼ぶ)
┌── ③ Evaluation フェーズ ─────────────────────────────────┐
│ [code-reviewer]  (独立 Evaluator)                          │
│   spec-conformance / test-quality / architecture /         │
│   security / performance / code-quality を並列実行         │
│   Coordinator が日本語の構造化レビューコメントを PR に post │
│   Merge readiness を判定                                    │
│   - Critical findings = 0 → Ready                          │
│   - ある場合 → implementation-workflow Phase 3 に戻る       │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼ Ready で人間 approve を待つ
┌── ④ Merge フェーズ ───────────────────────────────────────┐
│ [implementation-workflow Phase 7]                          │
│   CI green + 全 aspect pass + 人間 approve                 │
│   → gh pr merge --squash                                   │
│   Auto-merge は禁止 (GitHub Agentic Workflows 原則)        │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼ implementation-workflow Phase 8 で即時起動
┌── ⑤ Retrospection フェーズ ──────────────────────────────┐
│ [pr-poller]                                                │
│   gh pr list --state closed,merged                         │
│   未処理 PR を検出 (learning ファイルが未生成)             │
│       │                                                    │
│       ▼                                                    │
│ [pr-retrospective]                                         │
│   PR 情報・CI・Skill ログ・指標差分を収集                  │
│   docs/harness/learnings/YYYY-MM-DD-pr-N.md を生成         │
│   (日本語の構造化フォーマット)                              │
│   harness/learnings-batch-YYYY-WW ブランチに集約           │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼ (一定期間経過 / 件数閾値で pr-poller が起動)
┌── ⑥ Meta フェーズ (内部 + 外部の二系統) ────────────────┐
│ [harness-meta] (内部 KPT 駆動、週次 cron + 閾値 + 手動)    │
│   docs/harness/learnings/*.md を直接 Read                  │
│   Suggested harness changes を集計・優先度付け             │
│   1 改修テーマ = 1 PR で複数起票 (テーマ別粒度)             │
│   見送り提案: 元 learning ファイルに feedback セクション追記 │
│   撤去候補: 月次まとめて cleanup PR を別建てで起票          │
│                                                            │
│ [harness-evolution] (外部研究駆動、手動起動のみ)           │
│   WebSearch/WebFetch + Context7 MCP で外部ベストプラクティス取得 │
│   既存 ハーネスと gap 分析 → docs/harness/evolution-proposals/ 出力 │
│   重要案は example-skills:skill-creator 経由で Skill scaffold │
│   または Plan/EPIC 起票 (人間 approve 必須)                 │
│                                                            │
│ [skill-creator] (ユーザースコープ example-skills:skill-creator) │
│   新規/改修 Skill の SKILL.md scaffolding、Gotchas 必須化、 │
│   Anthropic Complete Guide + 100-point rubric 準拠         │
└────────────────────────────────────────────────────────────┘
                       │
                       ▼
                改修 PR 群 → ① Spec Gen フェーズへ
                (再帰的セルフ改善ループ完成)
```

ポイント:

- **6 フェーズで責務分離**: Spec Gen / Implementation / Evaluation / Merge / Retrospection / Meta。各フェーズに専用 Skill (or オーケストレーター) を割り当て
- **Meta フェーズは二系統**: 内部 (harness-meta、KPT 駆動) と 外部 (harness-evolution、外部研究駆動、**手動起動のみ**) を補完併用。Skill 作成は **`example-skills:skill-creator`** (Claude Code ユーザースコープ) を経由
- **Generator と Evaluator は構造的に分離** (Anthropic 原則): `implementation-workflow` は自分で書いたコードを自分で評価しない。独立した `code-reviewer` Skill が aspect ごとに別 system prompt で動く
- **Lint/Test pass まで Draft PR を Ready にしない** (GitHub Agentic Workflows 原則)
- **人間 approve なしに merge しない** (GitHub Agentic Workflows 原則)
- **GitHub Actions で Claude API を呼ばない**: ローカル Claude Code 内の `CronCreate` / `ScheduleWakeup` でポーリング駆動。API コストはユーザーの既存利用枠内で完結
- **Learning ファイルが Single Source of Truth**: PR コメントは出さない。`docs/harness/learnings/` の Markdown が `pr-retrospective` の出力先かつ `harness-meta` の入力源
- **対話的フィードバック**: 採用見送りや保留は元 learning ファイルに `harness-meta feedback` セクションを追記、提案 → 結果の往復ログが 1 ファイル内で完結
- **テンプレート言語は日本語**: ADR / Plan / Epic / 要件 / 仕様 / runbook / learning / レビューコメント の全テンプレ Markdown は日本語見出し・日本語例文で記述 (ADR 0027、`.claude/rules/template-language.md`)。frontmatter のキー (`id`, `status`, `type` 等) やコマンド文字列は英語のまま

### 5.5 テンプレート言語ポリシー

ハーネスが生成・参照する全ての Markdown テンプレートは **日本語で記述** する (ADR 0027)。AI 駆動でも人間レビューでも認知負荷を最小化し、ユーザーが第一言語で読み書きできるようにするため。

#### 日本語化対象

| カテゴリ | パス例 |
|---|---|
| ADR | `docs/adr/template.md`、各 ADR 本体 |
| Epic | `docs/epics/template/{README, roadmap, open-questions, decisions, progress}.md` |
| Plan | `docs/plans/template.md`、各 Plan 本体 |
| 要件定義 | `docs/requirements/template.md`、各機能要件 md |
| 基本設計 / 詳細設計 | `docs/specifications/template.md`、各仕様 md |
| Runbook | `docs/runbooks/template.md`、各 runbook md |
| Learning | `pr-retrospective` Skill が生成する `docs/harness/learnings/YYYY-MM-DD-pr-N.md` (フォーマット定義 `retrospective-format.md`) |
| PR description テンプレ | `.github/pull_request_template.md` |
| code-reviewer レビューコメント | `code-reviewer` Skill が PR に post する構造化コメント (フォーマット定義 `code-reviewer-aspects.md`) |
| ハーネス改修 PR description | `harness-meta` Skill が起票する PR の本文 |
| INDEX.md (Epic/Plan) | 見出しと説明列を日本語 |

#### 例外 (英語のまま)

- YAML frontmatter のキー名 (`id`, `title`, `status`, `type`, `related_pr`, `related_epic`, `created_at`, `completed_at` 等)
- ステータス値 (`proposed`, `in-progress`, `completed`, `abandoned`, `promoted`)
- コマンド・ファイルパス・コード断片
- 識別子 (SPEC-IDOL-001-3, EPIC-NNN, PLAN-NNN, ADR 0001 等)

#### 規約と検証

- `.claude/rules/template-language.md` で本ポリシーを明文化
- Konsist (markdown lint) で「frontmatter 外の見出しは日本語必須」をテスト化
- harness-bootstrap および各 Skill のテンプレート生成は本ポリシーに従う

#### 例: Plan テンプレートの日本語版イメージ

```markdown
---
id: PLAN-NNN
title: <短いタイトル>
type: feature | bug-fix | refactor | dep-upgrade | chore
status: proposed | in-progress | completed | abandoned | promoted
related_pr: null
related_epic: null
created_at: YYYY-MM-DD
completed_at: null
---

## 目的
<この PR で達成すること、1-3 行>

## 背景
<なぜ必要か、現状の問題、関連 Issue/レポート>
<該当コードを file_path:line で参照>

## アプローチ
<実装方針、最小限のステップ>
1. ...
2. ...

## 受け入れ基準
- [ ] <検証可能な条件>
- [ ] <テスト>
- [ ] <ドキュメント更新>

## スコープ外
<やらないこと、Epic に昇格する境界>

## メモ
<実装中に発見した非自明な点。詳細は decisions.md ではなくここに残す>
```

#### 例: pr-retrospective が生成する learning ファイルの日本語見出し

```markdown
# PR #NNN レトロスペクティブ

> 生成: pr-retrospective Skill (v1.0.0) at 2026-05-17T10:00:00Z
> 関連 Plan: PLAN-NNN / 関連 Epic: EPIC-NNN

## ✅ Keep (継続したいこと)
- ...

## ⚠️ Problem (詰まったこと / 制約)
- ...

## 🚀 Try (次回からの改善案)
- ...

## 📊 指標
| 指標 | Before | After | Δ |
|---|---|---|---|
| Line coverage | 100.00% | 100.00% | ±0 |
| Branch coverage | 100.00% | 100.00% | ±0 |
| Spec coverage | 98.4% | 100.0% | +1.6 |
| Mutation score | 86.2% | 88.1% | +1.9 |

## 🤖 ハーネス改善提案
<!-- harness-meta が parse する正規構造 -->
- [ ] `[rule]` ...
- [ ] `[skill]` ...
- [ ] `[template]` ...
- [ ] `[remove]` ...

## 📝 harness-meta フィードバック
<!-- harness-meta が後から追記 -->
- 2026-06-01: 提案 1 採用 (PR #150)
- 2026-06-01: 提案 2 見送り (理由: ...)
```

#### 例: code-reviewer のレビューコメント日本語版イメージ

```markdown
## 🔍 AI コードレビュー

> 生成: code-reviewer Skill (v1.0.0) at 2026-05-17T11:30:00Z
> 並列実行した aspect: spec-conformance, test-quality, architecture, security, performance, code-quality

### サマリ
| 観点 | 結果 | 重大な指摘 | 改善提案 |
|---|---|---|---|
| 仕様適合性 (spec-conformance) | ✅ 合格 | 0 | 0 |
| テスト品質 (test-quality) | ⚠️ 警告 | 0 | 2 |
| アーキテクチャ (architecture) | ✅ 合格 | 0 | 0 |
| セキュリティ (security) | ✅ 合格 | 0 | 0 |
| 性能 (performance) | ⚠️ 警告 | 0 | 1 |
| コード品質 (code-quality) | ❌ 不合格 | 1 | 3 |

### マージ可否: ❌ まだ不可
- `code-quality` の重大な指摘により merge をブロック

### 重大な指摘 (merge ブロック)
1. **[code-quality]** `feature/home/HomeViewModel.kt:42` — エラーハンドリングは `Result<T>` 型で統一する規約 (rules/error-handling.md L18)。現状は try/catch で例外を握り潰している
   - 修正案: ...

### 改善提案 (non-blocking)
- ...

### Eval チェックリスト (binary yes/no)
- [x] 仕様適合性: 全 Acceptance criteria に @Spec タグ付きテストが存在
- [x] 仕様適合性: SPEC-ID 参照のない実装がない
- [x] テスト品質: koverVerify が pass
- [ ] コード品質: try-catch で Result<T> へラップせず例外を握り潰している箇所がある
- ...
```

### 5.6 MCP サーバ構成

Skill が IDE / ライブラリドキュメント / Cloudflare を操作するために MCP (Model Context Protocol) サーバを接続する (ADR 0024)。**GitHub 操作は `gh` CLI で代替**、コード検索は **JetBrains MCP の IDE indexing で代替**するため、これらの MCP は採用しない。

#### 採用する MCP (B0 で導入する 3 つ)

| MCP | 提供元 | 接続方式 | 用途 | 連携 Skill |
|---|---|---|---|---|
| **JetBrains MCP** (IntelliJ IDEA / Android Studio 2025.2+ にバンドルされる **MCP Server プラグイン** [`JetBrains/mcp-server-plugin`](https://github.com/JetBrains/mcp-server-plugin)) | JetBrains 公式 | **SSE (HTTP) / Stdio (JVM-based proxy) / HTTP Stream のいずれか** を IDE 内蔵 MCP server から「Copy Config」して `.claude/mcp.json` に貼り付け | **コーディングエージェントから IntelliJ IDEA を操作**: rename symbol / lint inspection / regex search via IDE index / run configuration / build / file analysis / refactoring | implementation-workflow (実装中の rename・inspection)、code-reviewer (architecture aspect で IDE 検索)、refactor (影響範囲分析)、ui-snapshot (Composable / Preview スキャン) |
| **Context7 MCP** | Upstash (`upstash/context7`) | Remote (HTTP) | **バージョン固有のライブラリドキュメントを LLM に注入**: Kotlin / Compose Multiplatform / Compose Navigation 3 / Ktor / Koin / SQLDelight / kotlinx.serialization / kotlinx.coroutines / Kover / Konsist / Roborazzi / compose-multiplatform-resources / Apache Jena Fuseki / Litestream / Firebase Admin SDK (撤去対象なので Plan のみで参照) 等。"no hallucinated APIs / no outdated code generation" | feature-request / bug-fix / refactor / implementation-workflow / dependency-upgrade (全実装系 Skill が利用、ハルシネーション抑止) |
| **Cloudflare MCP Server** | Cloudflare 公式 | Remote (`mcp.cloudflare.com/mcp`、OAuth) | R2 / Pages / Workers / DNS / Secrets の管理。2,500+ API endpoints を `search()` / `execute()` の 2 tool で操作 (Codemode による Worker sandbox 実行) | C7 デプロイ Plan、secrets-rotation runbook |

#### 採用見送り (gh CLI / IDE indexing で代替)

| MCP | 代替手段 | 採用見送りの理由 |
|---|---|---|
| **GitHub MCP Server** | **`gh` CLI** | 実証ベンチマーク (mariozechner.at / scalekit) で **MCP は CLI より 10〜32 倍トークン消費**、初期化に約 55,000 tokens (90+ tools schema)。`gh` は Claude の training data に含まれハルシネーション少なく 1-shot で正確、bash / CI / CD パイプラインと一体化。MCP の優位性 (structured JSON / guardrails) は ColorMaster 用途では不要 |
| **Sourcegraph MCP** | **JetBrains MCP** | JetBrains MCP の IDE indexing で代替可能、機能重複。ColorMaster は単一 repo で Sourcegraph の cross-repo 検索能力を必要としない |
| **Serena MCP** (oraios/serena) | **JetBrains MCP + Context7 MCP** | (1) Serena LSP backend での Kotlin は **"Indirect Support via multilspy (community tested)"** で、`jdtls` (Eclipse JDT LS) 経由。Compose MP / KMP / wasmJs 特有の解析で IntelliJ の Kotlin Plugin に劣る。(2) Serena JetBrains backend は **有料プラグイン**で JetBrains MCP と機能ほぼ同一、二重投資。(3) **30 分の初期セットアップ + monorepo は初回インデックス数分**のオーバーヘッド。(4) Serena の強み「symbol-level retrieval」は JetBrains MCP の IDE index 検索で十分に達成可能、token 効率も rules ベースの参照と組み合わせれば差は決定的でない |

#### 将来必要時に Plan で追加 (B0 では入れない)

| MCP | 採用条件 |
|---|---|
| **Figma MCP Server** (公式 Remote) | Figma プロジェクト資産を作るタイミング (現状 DESIGN.md は A10 で実コードから抽出する方針なので不要) |
| **Code Pathfinder / codesearch MCP** | JetBrains MCP の検索性能で不足を感じた場合、Kotlin AST 検索 / call graph 分析が必要になった場合 |
| **Serena MCP** | **(a) IntelliJ IDEA が起動できない環境で Skill を動かす必要が発生** (CI 上の agent 実行など、R-27 のフォールバック) / **(b) 非 Kotlin コード (Markdown / Gradle DSL / YAML / SPARQL etc.) の symbol-level 操作頻度が増えた** / **(c) Claude Desktop / Codex / OpenCode 等の他 MCP クライアントでも同じハーネスを portable に動かしたい** ─ のいずれかが発生したら別 Plan で再評価 |
| **Sentry MCP** | 本番稼働後にエラートラッキングが必要になった場合 |
| Linear / Slack / Notion MCP | 個人プロジェクト規模では不要 |

#### 接続情報 (`.claude/mcp.json`)

```json
{
  "mcpServers": {
    "jetbrains": {
      "type": "sse",
      "url": "http://localhost:<IDE-dynamic-port>/sse"
    },
    "context7": {
      "type": "http",
      "url": "https://mcp.context7.com/mcp"
    },
    "cloudflare": {
      "type": "http",
      "url": "https://mcp.cloudflare.com/mcp"
    }
  }
}
```

##### JetBrains MCP の接続詳細

> **重要**: 旧来の `@jetbrains/mcp-proxy` npm パッケージ (旧リポジトリ `JetBrains/mcp-jetbrains`) は **deprecated**。「The core functionality has been integrated into all IntelliJ-based IDEs since version 2025.2. The built-in functionality works with SSE and JVM-based proxy (for STDIO) so this NPM package is no longer required」(公式 README) 。本計画では **IDE 内蔵 MCP Server プラグイン ([`JetBrains/mcp-server-plugin`](https://github.com/JetBrains/mcp-server-plugin), Marketplace ID [26071-mcp-server](https://plugins.jetbrains.com/plugin/26071-mcp-server)) を使用**。

**前提条件**:

- **IntelliJ IDEA / Android Studio 2025.2 以降** が必須 (内蔵 MCP Server プラグインがバンドル済 + 既定有効)
- IDE 側で `Settings | Tools | MCP Server` を開いて MCP server を有効化
- IDE が起動中であること (起動していなければ JetBrains MCP は接続失敗、CLI fallback へ — R-27)
- **Node.js 不要** (npx proxy は deprecated 方式、内蔵 SSE/Stdio/HTTP Stream で完結)

**接続モードの選択 (IDE 側で 3 種から Copy)**:

`Settings | Tools | MCP Server` 画面に以下のボタンがあり、それぞれの接続定義をクリップボードに取得できる:

| ボタン | 形式 | 性質 | `.claude/mcp.json` での記載例 |
|---|---|---|---|
| **Copy SSE Config** (推奨) | Server-Sent Events (HTTP) | 軽量、再接続容易、URL は動的ポート | `{ "type": "sse", "url": "http://localhost:<port>/sse" }` |
| **Copy Stdio Config** | JVM-based proxy を IDE が spawn | プロセス分離、Node.js 不要 | `{ "command": "<IDE が指定するパス>", "args": [...] }` |
| **Copy HTTP Stream Config** | HTTP Streaming | SSE の代替、大量ストリーム時 | `{ "type": "http", "url": "http://localhost:<port>/mcp" }` |

**ColorMaster の推奨は SSE**: 軽量 / 動的ポートの管理が IDE 任せ / 再接続が容易。動的ポートは IDE 起動ごとに変わるので、**Copy SSE Config の値を runbook の手順に従って `.claude/mcp.json` に貼り直す** 運用 (or 後述の `claude mcp add` コマンド経由)。

**自動登録の代替**:

IDE の `Settings | Tools | MCP Server` 画面に「Claude Code に自動登録」のような連携 UI が用意される場合もある。または Claude Code の `claude mcp add --transport sse jetbrains <URL>` コマンドでローカル設定に登録可能。手順は runbook に記載。

##### Context7 MCP の接続詳細

Remote HTTP、認証不要 (公開ライブラリ docs)。URL の正確な値は公式ドキュメント (`upstash/context7`) に従う。API key を要するプランも存在するが、ColorMaster 用途では公開 docs アクセスのみで足りる。

##### Cloudflare MCP の接続詳細

Remote HTTP + OAuth。初回接続時に Claude Code がブラウザで認証フローを開き、token はローカル Claude Code の安全領域に保存される。

##### 詳細セットアップ手順は runbook 参照

`docs/runbooks/mcp-setup.md` に以下を記載 (B0 で作成):

- IntelliJ IDEA 2025.2+ の MCP Server プラグイン有効化手順
- Node.js 18+ のインストール / バージョン確認
- 3 つの接続方式 (npx proxy / SSE / Stdio / HTTP Stream) のそれぞれの設定例とトレードオフ
- Context7 MCP / Cloudflare MCP の OAuth フロー
- 接続確認コマンド (Claude Code 内で `/mcp` などの diagnostic)
- 接続失敗時のトラブルシュート (IDE 未起動、port 競合、Node.js バージョン不整合等)

#### MCP 利用規約 (`.claude/rules/mcp-usage.md`)

- **GitHub 操作は原則 `gh` CLI**: PR list / view / diff / search / create / merge / comment 追加 / Actions logs 取得 / Issue 起票はすべて `gh`。MCP は不採用 (token コスト・ハルシネーション抑止の観点で CLI が優位)
- **IDE 操作は JetBrains MCP**: rename / inspection / file analysis / index 経由 regex search / build / run config 実行。手動で `git grep` する代わりに JetBrains MCP の検索を優先 (IDE index が高速・正確)
- **ライブラリの API 確認は Context7 MCP**: コード生成前に「`androidx.lifecycle.ViewModel` の `viewModelScope` の API」のような確認を行うときは必ず Context7 を経由 (training data の古い情報や hallucination を回避)
- **Cloudflare 操作**: `wrangler` CLI で済む場合 (`wrangler deploy` 等) は CLI を優先、複雑な API 操作 (R2 token ローテーション、bucket policy 更新等) は Cloudflare MCP を使う
- **MCP 認証情報の取扱**: OAuth token はローカル Claude Code 管理、リポジトリには絶対 commit しない (Secrets 管理規約 ADR 0021 の対象)
- **Skill が MCP 結果を learning / レビューコメントに含める場合**: PII redaction フェーズで token / API キー類を除去 (PII 規約 ADR 0020 と同等)
- **権限スコープ**: Cloudflare MCP は対象 zone / bucket のみ allow、JetBrains MCP は IDE のプロジェクトスコープに自動的に制限される

---

## 6. フェーズ順序

### 6.1 Phase B — ブートストラップ (手作業、1 PR のみ)

| # | 内容 |
|---|---|
| **B0** | 最小ブートストラップ PR。CLAUDE.md 骨格 / AGENTS.md 骨格 / `.claude/settings.json` / **`.claude/mcp.json` で JetBrains MCP + Context7 MCP + Cloudflare MCP の接続定義** / `.claude/skills/{harness-bootstrap, plan-author, epic-author, pr-poller, pr-retrospective, implementation-workflow, code-reviewer, ui-snapshot, harness-evolution}` の最小版 (**`skill-creator` は Claude Code ユーザースコープの `example-skills:skill-creator` を参照、本リポジトリには配置しない**) / `.claude/rules/{rules-index, retrospective-format, pr-poller, template-language, implementation-workflow, code-reviewer-aspects, pii, secrets, db-protection, adr, design-tokens, ui-snapshot, ui-inventory, behavior-preservation, mcp-usage, skill-authoring, harness-evolution, docs-structure}.md` 骨格 (`adr.md` は **ADR 起票基準と例列挙を含む**、`mcp-usage.md` は **GitHub は gh CLI / IDE 操作は JetBrains MCP / ライブラリ docs は Context7 / Cloudflare 管理は Cloudflare MCP の使い分けを規定**、`skill-authoring.md` は **example-skills:skill-creator 経由 + Anthropic Complete Guide 準拠**、`harness-evolution.md` は **外部情報源ホワイトリスト + 手動起動のみ + harness-meta との責務分離**、`docs-structure.md` は **AI が docs を読む順序 + 命名規約 + 5 行 summary + lazy-load 構造**) / `docs/{adr, epics, plans, harness/learnings, harness/evolution-proposals, runbooks, requirements, specifications, design/inventory, architecture, api, security}` スケルトン (テンプレートは全て**日本語**、`docs/README.md` は **AI 用エントリポイント** として推奨読み順を明記、`docs/{glossary, codebase-map, traceability}.md` の骨格を配置、`docs/architecture/{overview, layers, data-flow, domain-model, state-machines, sequences, infrastructure}.md` 骨格、`docs/api/{README, colormaster-api.yaml, auth, idols, me}.md` 骨格、`docs/security/README.md` で ADR 索引、`docs/requirements/{README, template}.md`、`docs/specifications/{README, basic-template, detail-template}.md`、`docs/adr/{README,template}.md` に **ADR 化すべき例 / 他の記録方法にすべき例** を列挙、`docs/design/README.md` に DESIGN.md / Inventory / Baseline 運用ガイド) / **`DESIGN.md` の骨格を repo root に配置** (tokens セクションは空、A10 で生成) / EPIC-000-harness-foundation 起票 / **`.gitignore` 最終形 (`data/users.db*`, `.env*`, `*-credentials.json` 等を網羅)** / **`docs/runbooks/{local-development, testing, i18n, mcp-setup}.md` を新規追加 (骨格)**。`mcp-setup.md` は以下を記載: (1) IntelliJ IDEA / Android Studio **2025.2+ にバンドル済の MCP Server プラグイン** (`JetBrains/mcp-server-plugin`、Marketplace 26071) を `Settings | Tools | MCP Server` で有効化、(2) **旧 `@jetbrains/mcp-proxy` npm パッケージは deprecated なので使用しない** こと、(3) JetBrains MCP の **3 つの接続方式** (Copy SSE Config [推奨] / Copy Stdio Config / Copy HTTP Stream Config) の選択基準と `.claude/mcp.json` への貼り付け手順、(4) IDE 再起動で動的ポートが変わる場合の再貼り付け手順 (or `claude mcp add` 経由の登録)、(5) Context7 / Cloudflare の OAuth 接続フロー、(6) `/mcp` での接続確認、(7) トラブルシュート (IDE 未起動 / MCP Server プラグイン未有効 / ポート競合 / IDE バージョン 2025.2 未満)。**GitHub Actions の post-merge workflow は導入しない** (KPT ループはローカル Claude Code ポーリングで駆動)、**`dependency-upgrade-check.yml` も導入しない** (Renovate PR の検証は pr-poller がローカルで dependency-upgrade Skill を起動して実施、Claude API の GitHub Actions 上実行を回避) |

B0 完了時点で Skill ループが稼働開始する。以降の全 PR が Skill 駆動 + KPT 生成の対象となる。`pr-poller` はローカル Claude Code 起動時に手動起動可能とし、A4 で `CronCreate` / `ScheduleWakeup` による自動化を完成させる。

### 6.2 Phase A — Skill 駆動による基盤完成

Phase A は **「実装フェーズ前にテストカバレッジ 100% と im@sparql ローカル Docker を含む全ての基盤が整っている」** ことを完了条件とする。Phase C はこの完了をもって初めて着手する。

| # | 単位 | 起動 Skill | 内容 |
|---|---|---|---|
| **A1** | Plan | `harness-bootstrap` | ADR 0001-0027 を一括起草する PR (全て日本語) |
| **A2** | EPIC-A2 | `harness-bootstrap` | **`.claude/rules/*` 全ファイル本格化 + `docs/` 全面拡充**: docs/README.md (AI エントリポイント・推奨読み順)、glossary.md、codebase-map.md (初版、A10 / Phase C で随時更新)、architecture/{layers, data-flow, domain-model, state-machines, sequences, infrastructure}.md、api/README.md + colormaster-api.yaml (骨格、内容は C5 で本格化)、security/README.md (ADR 索引)、requirements/{README, template}.md、specifications/{README, basic-template, detail-template}.md、runbooks/{local-development, testing, i18n}.md を完成。各 docs は冒頭 5 行 summary + 詳細 lazy-load 構造 (ADR 0027) |
| **A3** | Plan | `harness-bootstrap` | 専用 Skill 群実装 PR (feature-request, bug-fix, refactor, dependency-upgrade, adr-author, harness-meta、および pr-retrospective / pr-poller / **implementation-workflow** / **code-reviewer** / **ui-snapshot** / **harness-evolution** の本格版へのアップグレード、**`skill-creator` は Claude Code ユーザースコープの `example-skills:skill-creator` を採用しリポジトリには配置しない**)。implementation-workflow は 8 フェーズの fix loop / spec-living-sync / merge-readiness を完全実装。code-reviewer は 8 aspect の binary eval checklist + coordinator を完全実装 (visual-regression / design-tokens は A10 完了後に enable)。pr-poller は **Renovate ラベル PR の検出と dependency-upgrade 起動** も担当。マージ後、`harness-bootstrap` は `archived/` へ |
| **A4** | Plan | `feature-request` | **ローカルポーリング機構の本格化**: `pr-poller` Skill が `CronCreate` (日次 09:00 JST) と `ScheduleWakeup` (継続ループ) を自動設定する仕組みを実装。`harness-meta-criteria.md` を完成させ、harness-meta の起動閾値 (例: 未処理 learning が 10 件 or 7 日経過) を `pr-poller.md` に明文化。GitHub Actions による Claude API 呼び出しは行わない (コスト回避方針 / ADR で記録) |
| **A5** | Plan | `refactor` | 不要モジュール撤去 (`js/app`, `js/material`, `kotlin-js-store`, `web-build-and-deploy.yml`, `public/` 内 js 専用ファイル、**`dev.gitlive:firebase-*` 依存、`core/network/{auth,firestore}`、`firebase.json`、`.firebaserc`**) |
| **A6** | Plan | `feature-request` | Lint / Format 基盤 (Spotless + ktlint + detekt + Konsist + lefthook + **trufflehog による secret-scan workflow**)。Konsist で「`data/users.db*` の追跡禁止」「Dockerfile 内 `COPY data/users.db` 禁止」「`feature/**`・`core/**` で `dev.gitlive.firebase.*` import 禁止」「`/api/me/*` ハンドラに `requireUid()` 強制」「**`docs/traceability.md` を Konsist で自動生成** (Plan/Epic/ADR/Spec/実装ファイルのクロスリンク表)」「**docs/ の各 Markdown が冒頭 5 行以内の summary を持つ**」を検証 |
| **A7** | Plan | `feature-request` | **三層テスト品質基盤の導入**:<br>● **指標 A**: Kover 導入 + `koverVerify minValue=100` 必達化 (ADR 0013 除外列挙のみ許可)<br>● **指標 B**: `@Spec` annotation の Kotlin 定義 + Konsist による Spec coverage 検証ルール導入 (ADR 0016、`.claude/rules/spec-traceability.md`)<br>● **指標 C**: PITest + pitest-kotlin + gradle-pitest-plugin 導入。JVM target 経由で `commonMain` + `jvmMain` + `androidMain` を mutate。PR コメントで mutation score 可視化 (ADR 0015、`.claude/rules/mutation-testing.md`)<br>本 PR 時点では既存コードの未充足は除外リストで一旦逃がし、A9 完了までに全モジュールに展開する旨を rules に明記 |
| **A8** | Plan | `feature-request` | **im@sparql ローカル Docker 環境構築** (Apache Jena Fuseki + RDF データ初期投入スクリプト + `docker-compose.yml` + integration test 基盤 + Testcontainers 規約)。`docs/runbooks/local-imasparql.md` を整備し、backend のローカル開発・テストを Fuseki に対して実行できる状態にする |
| **A9** | **EPIC-A9** | `refactor` | **既存コード全体に対する三層指標の達成**。モジュールごとに段階 Plan (PLAN-NNN × 多数) を発行:<br>● 指標 A: line / branch 100% カバレッジを全モジュールで達成<br>● 指標 B: 既存機能の Acceptance criteria を `docs/specifications/<id>.md` に逆生成、テストに `@Spec` annotation を付与、Spec coverage 100% 達成<br>● 指標 C: 各モジュールの初回 mutation score をベースラインとして記録、明らかな tautological テストは learnings に蓄積し改善<br>Konsist の「実装クラス ⇄ テストクラス対応」検証を本 EPIC 完了時に enforce。backend/server / android/app / core/* / data/ 全モジュールが対象。A7 で導入した除外リストは ADR 0013 列挙分のみに整理する |
| **A10** | **EPIC-A10** | `ui-snapshot` + `refactor` | **UI/UX 現状記録 EPIC (Phase C のリファクタ前に Behavior Preservation を確立)**:<br>● Roborazzi (Compose Desktop + Android Robolectric) を導入<br>● Konsist で全 Composable をスキャン、`@Preview` 不在を検出して Plan で追加 (段階的)<br>● 各 Preview に対応する screenshot baseline を **4 パターン** (mobile-light/mobile-dark/desktop-light/desktop-dark) で生成し `docs/design/inventory/screenshots/` に commit<br>● **`DESIGN.md` を repo root に生成**: 色・タイポ・スペーシング・radii を実コードから抽出 + Rationale を AI 起草 → 人間レビュー必須<br>● **UI Inventory** (`docs/design/inventory/{screens,components,states,flows}/*.md`) を全件生成<br>● `.claude/rules/{design-tokens,ui-snapshot,ui-inventory,behavior-preservation}.md` を本格化、Konsist で「実装クラスに対応する Preview と screenshot baseline がある」「DESIGN.md に存在しない hex code がコードに混入していない」を機械検証<br>● code-reviewer の **visual-regression / design-tokens aspect を enable** |

**Phase A 完了条件**:

- 全モジュールで `./gradlew check koverVerify` がグリーン (指標 A: line / branch ともに 100%、除外は ADR 0013 列挙分のみ)。
- Konsist の Spec coverage 検証がグリーン (指標 B: 全 Acceptance criteria に対応する `@Spec` 付きテストが存在)。
- `./gradlew pitest` が JVM target で実行可能、初回 mutation score が記録されている (指標 C)。
- `docker compose up imasparql` でローカル Fuseki が起動し、backend integration test が Fuseki に対して実行可能。
- **`./gradlew verifyRoborazziDebug` (or 相当) がグリーン**: 全 Composable に対する 4 パターンの screenshot baseline が一致 (A10 完了)。
- **`DESIGN.md` が repo root に存在し、UI Inventory が全画面・全主要コンポーネント・全状態を網羅** (A10 完了)。
- **code-reviewer の visual-regression / design-tokens aspect が有効化済み** (A10 完了)。
- `harness-bootstrap` は `archived/` へ移動済み、全専用 Skill が稼働。
- KPT ループが post-merge workflow + 月次 harness-meta で完全自動稼働。

### 6.3 Phase C — 本格運用 (Epic / Plan 管理 + Skill ループ + KPT)

Phase A 完了後の本格運用フェーズ。すべての PR は **100% カバレッジ達成を前提条件として書く**。新規ファイルは作成と同時にテストが書かれている状態でないとマージ不可。

| # | 単位 | 起動 Skill | 内容 |
|---|---|---|---|
| C1 | Plan | `dependency-upgrade` | Renovate 強化 (groupName, dependencyDashboard, approval gate)、ドッグフード |
| C2 | Plan | `harness-meta` | C1 KPT を受けたハーネス改修 |
| C3 | **EPIC-001** | `refactor` | フィーチャ再編 + Decompose 撤去 + CMP Navigation 3 + 共通 ViewModel |
| C4 | **EPIC-002** | `refactor` | i18n 移植 (`public/locale/` → compose-multiplatform-resources) |
| C5 | **EPIC-003** | `feature-request` | Backend 強化 (`colormaster-api` モジュール、**GIS ID Token 検証 (JWKS)**、**Backend 内蔵 SQLite に `users.db` 追加 (uid のみ保存)**、**Litestream + R2 レプリケート**、`/api/me/*` エンドポイント、PII redaction、`requireUid()` ヘルパ) |
| C6 | **EPIC-004** | `feature-request` | upstream-driven 同期パイプライン (`imas/imasparql` SHA 監視、日次 cron、初期データ投入) |
| C7 | Plan | `feature-request` | **Cloud Run デプロイ + Cloudflare Pages デプロイ + R2 バケット作成** (GitHub Actions + gcloud CLI + wrangler / Pages CLI、Artifact Registry、Secret Manager 連携、R2 token のローテーション runbook) |
| C8 | **EPIC-005** | `feature-request` | KMP - iOS ターゲット有効化 + Xcode プロジェクト雛形 + GIS iOS 実装 |
| C9 | **EPIC-006** | `feature-request` | KMP - wasmJs ターゲット有効化 + GIS wasmJs 実装 (Firebase 切り離しは EPIC-003 で完了済み) |
| C10 | Plan | `feature-request` | Web 配信再開 (**wasmJs ビルドを Cloudflare Pages にデプロイ**) |

旧計画にあった「C5 im@sparql ローカル Docker」「C12 テストカバレッジ段階引き上げ」は本改訂で **Phase A (A7-A9)** に前倒した結果、Phase C からは消えている。

依存順序の根拠:

- C3 を先頭に: 以降の実装が `feature/*` 構造前提になる。
- C4 を C3 直後に: 新 `feature/*` に composeResources を組み込みたい。
- C5 (Backend 強化) を C6/C7/C8/C9 より前に: **GIS 検証エンドポイントと Backend SQLite (users.db + Litestream/R2) が揃わないと Cloud Run デプロイと iOS/wasmJs クライアントの開発が進まない**。Backend integration test は A8 で整備済の Fuseki 環境 + Testcontainers R2 emulator (MinIO) を利用。
- C6 (同期パイプライン) を C7 (デプロイ) より前に: `data/idols.db` 初期データがリポジトリに乗らないと Cloud Run コンテナイメージが空になる。
- C8 (iOS) を C9 (wasmJs) より前に: 先に iOS でアーキ全体を検証してから wasmJs の重い切替に進む方が安全。**Firebase 切り離しは C5 で既に完了している** ため、C9 は wasmJs ターゲット有効化に集中可能。

---

## 7. 依存ライブラリのバージョンアップ自動化

- Renovate 強化 (C1):
  - `groupName` で kotlin / ktor / compose / sqldelight 等を束ねる。
  - `prCreation: approval`、`dependencyDashboard: true`、`extends: [:semanticCommits]`。
- **`dependency-upgrade` Skill の起動は GitHub Actions ではなく `pr-poller` (ローカル Claude Code) から行う**。GitHub Actions 上で Claude API を呼ぶと課金が発生するため、ローカル Claude Code 内で完結させる方針 (ADR 0017 / 0027 のローカルポーリング駆動と一貫)。
  - `pr-poller` がローカルポーリングで **Renovate ラベル付き open PR** を検出 → `dependency-upgrade` Skill を起動。
  - `dependency-upgrade` Skill は **Context7 MCP** で最新リリースノート取得、**JetBrains MCP** で影響モジュール特定、**`gh` CLI** で PR 状態取得、`./gradlew check` でテスト、結果サマリを `gh pr comment` で投稿。安全と判定したら `approve` ラベル付与、危険なら downgrade 提案 / Plan 起票。
- GitHub Actions では Renovate 起点 PR に対する **CI (Lint / Test / Roborazzi) の通常実行のみ**。Claude API は呼ばない。

---

## 8. テストカバレッジ戦略

テスト品質は **3.10 節で定義した三層指標** で多層検証する。詳細は 3.10 節を参照。本節では運用面の補足を記す。

- **指標 A (Line/Branch Coverage 100%)**: Kover を導入、`./gradlew check` に `koverXmlReport` / `koverVerify` を組み込む。`koverVerify` の minBounds: `minValue = 100`、counter = `LINE` と `BRANCH` の両方を必達ゲートとする。除外対象は ADR 0013 で限定列挙のみ、除外追加は ADR 改訂が必須。
- **指標 B (Spec Coverage 100%)**: `@Spec("SPEC-NNN-N")` annotation をテスト関数に付与し、`docs/specifications/<id>.md` の Acceptance criteria と双方向対応させる。Konsist で「全 Acceptance criteria に対応する `@Spec` が存在する」「`@Spec` ID が specifications に実在する」を機械検証。詳細は ADR 0016。
- **指標 C (Mutation Score)**: PITest + pitest-kotlin + gradle-pitest-plugin を採用し、JVM target 経由で `commonMain` + `jvmMain` + `androidMain` を mutate。`jsMain` / `wasmJsMain` / `iosMain` の actual 実装は対象外 (これらは Konsist + 通常単体テストで担保)。CI ではゲートにせず PR コメントで可視化。詳細は ADR 0015。
- **Konsist でペアリング検証**: 各実装クラスに対応するテストクラス (`*Spec.kt` / `*Test.kt` / `*ScreenshotTest.kt`) の存在を機械的に強制する (`.claude/rules/test-paired-class.md`)。
- **UI モジュール (Compose)** は **Compose UI Test + screenshot test (Paparazzi / Roborazzi)** で指標 A を達成。
- **AI 自動生成テストの質の担保**: 指標 B (仕様トレーサビリティ) と指標 C (mutation score) がそれぞれ別軸で抑制効果を持つ。さらに Konsist で「テストクラスは最低 1 つの assert を含む」「Mock のみで実装を呼ばないテストは禁止」等のメタ規約を加える。KPT で発見された無意味テストパターンは harness-meta が `kotlin-test.md` ルールに追加していくフォールバックループで対処。
- **Phase A (A7-A9) で全モジュールに三層指標を導入** し、Phase C 以降は維持 + 新規分の即時 100% / Spec coverage 100% を CI で必達とする。

---

## 9. リスクと未解決事項

| ID | リスク / 論点 | 暫定方針 |
|---|---|---|
| R-1 | iOS Compose Multiplatform は Stable 到達済みだが、scroll physics 等の挙動差異が残る可能性 | C8 で限定機能から検証、ADR に「実験的採用」を残す |
| R-2 | wasmJs での GIS 実装パターンの公式リファレンスが薄い | C9 で spike PR を最初に切る。Open question として EPIC-006 に記録 |
| R-3 | `data/idols.db` のバイナリ管理によるリポジトリサイズ膨張 | アイドル情報は数百〜千行のため当面は通常 commit。MB 級になったら Git LFS 移行を別 ADR で |
| R-4 | Cloud Run JVM コールドスタート | Phase C 完了後に GraalVM Native Image を別 ADR で検討 |
| R-7 | AI 自動生成テストが「網羅性はあるが意味のないテスト」になる可能性 | 三層指標で多層対処: 指標 B (Spec coverage) で仕様トレーサビリティを強制、指標 C (Mutation score) で意味的強度を計測可視化、Konsist メタ規約 + KPT 学習 + `kotlin-test.md` 強化のフォールバックループで担保 (3.10 節参照) |
| R-8 | A9 (既存コード三層指標達成 EPIC) の作業量が想定を超える可能性 | モジュール単位で Plan を切り、PR を細粒度に分割。完了見込みが立たない場合は除外対象の見直しを ADR 改訂で対応 (ただし安易な除外追加は禁止)。指標 C は初回 baseline 記録のみで完了とし、改善は継続課題に |
| R-9 | Fuseki に投入する RDF データの著作権・ライセンス確認 | A8 でデータ取得元 (`imas/imasparql` リポジトリ) のライセンスを確認し ADR 0014 に明記。条件次第ではダミー RDF + テスト専用データ構成にする |
| R-10 | PITest が KMP の JS/Wasm/iOS actual 実装を mutate できない | これらは expect/actual の薄い層で本質的にロジックが薄いため実害が小さいと判断。Konsist (テスト存在) と通常単体テストで担保。将来 MutFlow (K2 compiler plugin、KMP 全 target 適合の可能性) を別 ADR で評価可能性として記録 (ADR 0015) |
| R-11 | ローカル Claude Code ポーリング駆動のため、ユーザーがしばらく Claude Code を起動しないと KPT ループが停止する | `pr-poller` Skill 起動時に「最後の処理から N 日経過した PR」を最優先で処理するキャッチアップ動作を組み込む (ADR 0017)。長期不在後の再開で取りこぼしを防ぐ |
| R-12 | learning ファイルが PR にまとまる前にロストするリスク (ローカル commit のみで push 忘れ) | `pr-retrospective` は ファイル生成と同時に `harness/learnings-batch-YYYY-WW` ブランチに push する。週次の learnings PR でまとめて起票するが、push 自体は逐次行う |
| R-13 | code-reviewer の aspect が、code を書いた Generator と同じバイアスを共有するリスク | Anthropic Evaluator 独立性原則に従い、aspect ごとに別の system prompt を持たせる (`code-reviewer-aspects.md` に明文化)。さらに各 aspect は binary yes/no eval checklist を最低 5 項目持ち、yes/no 判定を強制 |
| R-14 | implementation-workflow の fix loop が無限に回るリスク | 上限 3 回 (デフォルト) で停止し、Plan に `status: blocked` を記録、人間に通知 (`implementation-workflow.md` に明文化) |
| R-15 | code-reviewer の自動レビューに人間が依存しすぎ、本質的な見落としを許すリスク | GitHub Agentic Workflows 原則「人間 approve なしに merge しない」を必達。code-reviewer の Ready 判定は merge を許可するだけで自動 merge しない。人間レビュアーには「code-reviewer の指摘で十分か?」を考えさせる文言を PR コメントに含める |
| R-17 | Cloud Run の JVM Container は Cold Start に数秒かかる可能性 | C7 で cold start 計測、許容不可なら GraalVM Native Image 化を別 ADR で検討。read-heavy API なので初回のみ影響、トラフィック想定では問題小さい見込み |
| R-18 | R2 + Litestream の実環境動作未検証 | C5 内で Testcontainers の MinIO (S3 互換) と本番 R2 の両方で integration test を実施。runbook `docs/runbooks/r2-litestream.md` に手順を残す |
| R-19 | R2 token 流出による `users.db` 漏洩 | R2 bucket private + bucket policy で Backend Service Token のみ allow、token TTL 90 日で定期ローテーション (ADR 0021)、漏洩時のローテーション runbook を `docs/runbooks/secrets-rotation.md` に整備。DB スキーマで PII を `uid` のみに最小化することで漏洩時の影響を構造的に下げる |
| R-20 | PR diff / リポジトリ履歴に PII / credentials が混入するリスク | trufflehog による全 PR 差分のスキャン (A6 で導入)、`.gitignore` で `data/users.db*` / `.env*` / `*-credentials.json` 等を除外、Konsist で `data/users.db*` の追跡禁止を検証 |
| R-21 | Skill (code-reviewer / pr-retrospective / harness-meta) が PII を出力に転載するリスク | `.claude/rules/pii.md` で Skill 出力前の redaction を強制、Konsist でテストフィクスチャの `@example.com` ドメイン以外の検出、Skill 設計で CI ログ等の取り込み時に redaction フェーズを必須化 |
| R-22 | Preview 未整備の Composable が多く A10 の baseline 生成が想定より長期化 | モジュール単位で段階 Plan に分割。重要画面 (Home/Search/Preview/MyIdols) を最優先で baseline 化、補助コンポーネントは Phase C 内で追加することも許容 (ADR 0023 で記録) |
| R-23 | 動的色 (アイドル brand color) で Roborazzi の diff が誤検出される | Preview ではアニメーション停止 + 代表 brand color 固定パラメータ、別途 brand-color バリエーション Preview を作成して網羅。Roborazzi `changeThreshold` の許容しきい値も併用 (ADR 0023) |
| R-24 | Roborazzi が wasmJs を未サポート、wasmJs 固有レンダリング差異を検出できない | commonMain は JVM (Compose Desktop) で screenshot test、wasmJs 固有 actual は Konsist + 単体テストで担保。将来 Roborazzi が wasmJs 対応したら ADR 0023 改訂で乗り換え (ADR 0023) |
| R-25 | MCP の OAuth token がローカル Claude Code から流出するリスク | token はローカル Claude Code の安全領域のみに保存、リポジトリ commit 禁止 (`.gitignore` で `.claude/oauth-tokens*` 等を除外)。Cloudflare MCP は対象 zone / bucket のみに権限スコープを制限、JetBrains MCP は IDE プロジェクトスコープに自動制限 (ADR 0024) |
| R-26 | Skill が MCP の結果に含まれる secret / PII を learning / レビューコメントに転載 | `.claude/rules/mcp-usage.md` で出力前の redaction を強制、Konsist で Skill 実装ファイル内の secret パターン (`/^GHP_/`, `/^sk_/` 等) を検出 (ADR 0020 / 0027) |
| R-27 | IntelliJ IDEA が起動していない / バージョンが 2025.2 未満 / MCP Server プラグインが無効 / 動的ポートが IDE 再起動で変わって `.claude/mcp.json` が古い、で JetBrains MCP が利用不可 | `docs/runbooks/mcp-setup.md` に IDE バージョン要件 (2025.2+) と MCP Server プラグイン有効化手順、Copy SSE Config の再貼り付け / `claude mcp add` 経由の再登録手順を明記。Skill 起動時に JetBrains MCP の接続失敗を検出したら、警告を出しつつ `gh` CLI / `git grep` などの代替手段にフォールバック。長期的・恒常的に IDE 非起動環境 (CI 上 agent 実行など) で運用したい場合は **Serena MCP の採用を別 Plan で再評価** (ADR 0024 の将来検討に記載) |
| R-28 | Context7 MCP が古い / 取得不能 / 別のライブラリの API を返すリスク | Context7 は公開ライブラリ docs のため、AI が取得した内容を Konsist / detekt / 型チェッカーで二重検証。「Context7 で確認した」だけで実装を確定させない (rules/mcp-usage.md 明記) |
| R-29 | harness-evolution が古い / 信頼性の低い情報源を追従するリスク | 情報源は `.claude/rules/harness-evolution.md` の **ホワイトリスト方式**、提案には必ず出典 URL + 引用日付、Context7 MCP で API 引用検証 (ADR 0026) |
| R-30 | `example-skills:skill-creator` の公式アップデートに追従できないと SKILL.md 仕様 drift する | harness-evolution の手動実行時に `anthropics/skills` の更新を確認する責務に組み込み (`.claude/rules/harness-evolution.md` のホワイトリスト先頭に明記)。月次相当の頻度で手動実行を奨励 |
| R-31 | harness-evolution が harness-meta と提案重複 | 重複検出ルール (`.claude/rules/harness-evolution.md`): 既に learning ファイルで指摘済の提案は harness-evolution 側を見送り。改修 PR は `harness-meta` / `harness-evolution` の **ラベル分離** で運用、harness-meta 側を優先 |
| R-32 | docs/ が肥大化して AI のコンテキストを圧迫 | 各 docs に **冒頭 5 行以内の summary + 詳細 lazy-load** 構造を必須化、`.claude/rules/docs-structure.md` で規定、Konsist で機械検証 (ADR 0027 / A6) |
| R-33 | 複数 docs 間の重複・矛盾 (例: api.md と OpenAPI yaml、spec と requirements、ADR と rules の重複指摘) | `docs/traceability.md` を Konsist で自動生成し Plan/Epic/ADR/Spec/実装のクロスリンクを機械維持 (A6)。code-reviewer の architecture aspect で docs 間の矛盾も検出対象に |
| R-5 | Skill が rules を読み飛ばすリスク | Konsist / detekt / Gradle カスタムタスクで機械的ガードを二重化 |
| R-6 | harness-bootstrap が万能になりすぎると専用 Skill 化が遅れる | A3 完了で必ず `archived/` へ移動、CLAUDE.md からも参照を外す |

---

## 10. 確認済みの確定事項一覧

本計画策定セッションで合意した事項を集約:

- アーキテクチャは Compose Multiplatform + 共通 ViewModel + Navigation 3。Decompose は撤去。
- i18n は compose-multiplatform-resources。
- アイドル情報はリポジトリ内 SQLite + JSON snapshot、Container イメージ焼込み、read-only、Litestream 対象外。
- **ユーザーデータは Backend (Cloud Run) 内蔵 SQLite `users.db`、Litestream で Cloudflare R2 に WAL レプリケート + 起動時 restore**。リポジトリには絶対 commit しない (`.gitignore` 強制)。
- **DB スキーマには `uid` (Google sub claim) のみ保存**。display name / email / picture は GIS userinfo endpoint から都度取得 + memory cache TTL 15 分。
- 同期は `imas/imasparql` SHA 監視、1 日 1 回。差分時のみ PR 自動作成。
- **認証は全 target で GIS (Google Identity Services) に統一**。フロントが ID Token 取得 → Backend が JWKS で検証 → uid 抽出。Firebase Auth は使用しない。
- **Firebase 依存は完全廃止** (`dev.gitlive:firebase-{app,auth,firestore}`、`core/network/{auth,firestore}`、`firebase.json`、`.firebaserc` を全て撤去)。
- **Backend は Cloud Run**、**静的配信は Cloudflare Pages**、**Litestream バックアップ先は Cloudflare R2** のハイブリッド構成。完全無料運用が可能。
- Cloudflare Containers は不採用 (Workers Paid plan $5/月必須でコスト劣後)。
- Terraform 不使用。デプロイは GitHub Actions + gcloud CLI + wrangler。
- `js/app` / `js/material` / `kotlin-js-store` / `web-build-and-deploy.yml` は即時撤去。
- Web 配信は wasmJs 完成まで一時停止を許容、再開は **Cloudflare Pages** にデプロイ。
- 大規模な取り組みは Epic (`docs/epics/EPIC-NNN-<slug>/`)、単一 PR は Plan (`docs/plans/PLAN-NNN-*.md`)。
- Epic / Plan は独立採番。Epic 紐付き Plan も `docs/plans/` に一元化。
- Plan の Notes は自由記述、蓄積したら template に反映。
- 昇格時のステータスは `promoted`。
- B0 のみ手作業、A1 以降は Skill ループ駆動。
- **ADR の起票基準**: 4.5 節の 10 項目のうち 2 つ以上を満たすときに ADR を起こす。コーディング/命名規約は `.claude/rules/`、PR 単位の判断は Plan、Epic 内の保留→解決は `decisions.md`、運用手順は runbook で扱う。ADR 0001 と `.claude/rules/adr.md`、`docs/adr/{README,template}.md` に **ADR 化すべき例 / 他の記録方法にすべき例** を列挙。
- **ADR 精査結果**: 初期 ADR は **24 個** (旧 0004 state-and-uiaction-conventions は規約レベルにつき rules に統合、旧 0026 permission-roles-owner-only は ADR 0020 PII に統合)。連番に再採番済み。
- harness-bootstrap は A3 後に `archived/` へ移動。
- **KPT は全 PR で生成**:
  - 出力先は **ファイル** (`docs/harness/learnings/YYYY-MM-DD-pr-N.md`)、PR コメントは出さない
  - 駆動方式は **ローカル Claude Code ポーリング** (`pr-poller` Skill が `CronCreate` / `ScheduleWakeup` で起動)。GitHub Actions では Claude API を呼ばない (コスト回避、ADR 0017)
  - Skill 構成: `pr-poller` (オーケストレーター) + `pr-retrospective` (Evaluator、旧 kpt-retrospective) + `harness-meta` (Meta-Generator)
- **harness-meta による改修 PR**:
  - 改修テーマごとに **個別 PR** を起票 (1 改修テーマ = 1 PR)
  - 見送り提案は元 learning ファイルに `## 📝 harness-meta feedback` セクションを追記、提案 → 結果の往復ログがファイル内で完結
  - 未使用 rule / dormant Skill は **月次 cleanup PR** で別建てで撤去候補レビュー
- **コード実装ワークフロー**:
  - 新規 Skill `implementation-workflow` が 8 フェーズで Plan/Epic 確定後の実装着手 → Lint/Test → AI Review → マージ判断 → レトロ起動を統合管理
  - 新規 Skill `code-reviewer` が **独立 Evaluator** として 8 aspect (spec-conformance / test-quality / architecture / security / performance / code-quality / visual-regression / design-tokens) を並列レビュー、Coordinator が日本語の構造化レビューコメントを PR に post し Merge readiness を判定
  - 既存 `feature-request` / `bug-fix` / `refactor` の責務は **Plan / Epic 起票まで** に縮小、その後 implementation-workflow にバトンタッチ
  - Fix loop 上限はデフォルト 3 回、超過したら Plan に `status: blocked`
  - **人間 approve なしの auto-merge は禁止** (GitHub Agentic Workflows 原則準拠)
- **テンプレート言語**:
  - 全 Markdown テンプレート (ADR / Epic / Plan / 要件 / 仕様 / runbook / learning / PR description / レビューコメント等) は **日本語見出し・日本語例文** で記述 (ADR 0027)
  - 例外: YAML frontmatter のキー、ステータス値、コマンド・パス・コード断片、識別子 (SPEC-ID / EPIC-NNN / PLAN-NNN / ADR 番号等) は英語のまま
  - Konsist で「frontmatter 外の見出しは日本語必須」を機械検証
- **PII 保護とアクセス制御**:
  - PII の定義: メアド / Google Account ID / Display Name / プロフィール画像 URL / IP アドレス。`uid` は内部識別子だが PII 同等の取扱
  - DB に保存する PII は `uid` のみ、それ以外は GIS userinfo から都度取得 + memory cache TTL 15 分
  - `.gitignore` で `data/users.db*` / `.env*` / `*-credentials.json` 等を除外、Konsist で機械検証
  - **trufflehog** による secret-scan workflow を A6 で導入、全 PR 差分をスキャン
  - **R2 token TTL 90 日 + 定期ローテーション** (ADR 0021)、漏洩時の runbook を整備
  - Skill (code-reviewer / pr-retrospective / harness-meta) は出力前に PII redaction フェーズを通す
  - 権限ロールは **当面 owner 1 名のみ** (ADR 0020)、複数人体制になったら別 ADR で `developer` / `releaser` を追加
- テスト品質は **三層指標** で多層検証する:
  - 指標 A: **Line / Branch coverage 100%** (テスト存在の保証、CI 必達ゲート、`koverVerify minValue=100`)
  - 指標 B: **Spec coverage 100%** (仕様適合性の保証、`@Spec` annotation + Konsist 検証で CI 必達ゲート)
  - 指標 C: **Mutation score** (テスト意味的強度の計測、PITest + JVM target 経由、PR コメントで可視化、ゲートにはしない)
- カバレッジは「指標」ではなく「テスト存在を保証する制約」と再定義し、Goodhart's law を回避。仕様適合性は指標 B、意味的強度は指標 C が直接担当する分担構造。
- 除外対象 (指標 A) は ADR 0013 で限定列挙。Mutation testing 対象外スコープ (JS/Wasm/iOS actual 実装) は ADR 0015 で明示。
- im@sparql ローカル Docker (Fuseki) は **Phase A (A8) で整備**。Phase C 着手前に backend integration test が Fuseki に対して実行可能な状態にする。
- 既存コードの三層指標達成は **Phase A (A9) を EPIC として実施**。Phase C はこの完了をもって着手。

---

## 11. 次のアクション

本ドキュメントの merge を計画確定の起点とする。次のセッションで以下を実施:

1. **B0 (ブートストラップ PR) を 1 本作成**
   - ブランチ: `harness/bootstrap`
   - 内容: 第 4 章 ドキュメント構造 + 第 5 章 ハーネス構造のスケルトン + EPIC-000-harness-foundation 起票
   - レビュー: 唯一の例外として人間レビュー (Skill ループ外)
2. B0 マージ後、A1 (ADR 起草) から Skill 駆動 + KPT ループを稼働させる。
3. **Phase C 着手は A9 完了 (既存コード三層指標 (line/branch 100% + spec coverage 100% + mutation score baseline) 達成 EPIC 完了 + im@sparql ローカル Docker 整備 + Lint/Test 基盤完成) 後**。これにより Phase C の実装作業は最初からセーフティネットと統合テスト環境の上で進む。
