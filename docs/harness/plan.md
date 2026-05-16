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
- **テストカバレッジは line coverage / branch coverage いずれも 100%**。除外対象は ADR 0014 で限定列挙し、それ以外は AI による自動テスト生成で必ず充足する。
- Phase C (本格運用) 着手時点で、テスト基盤・カバレッジ 100% の達成・im@sparql ローカル Docker 環境が完全に整っている。

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
| アイドル情報マスタ | リポジトリ内 SQLite (`data/idols.db`) + JSON snapshot (`data/idols.json`) + 同期 state (`data/.imasparql-sync-state.json`) |
| ユーザーデータ (担当 / 推し) | Firestore 永続化を継続。ただし **Backend 経由化** (Firebase Admin SDK で代理アクセス) |
| 認証 | Firebase Auth で取得した ID Token を Backend で検証 (firebase-admin SDK)。wasmJs では Firebase JS SDK が動かないため **Google Identity Services (GIS)** で直接 ID Token を取得する代替実装を用意 |
| 既存 `core/network/{auth,firestore}` | Android / JS では当面残す。wasmJs では colormaster-api 経由に切替。最終的には全 target を colormaster-api 経由に統一 |

採用根拠:

- アイドル情報マスタを Git に乗せることで、Cloud Run コンテナはステートレスを保ち、im@sparql ダウン時もアプリは前回正常データで稼働可能。
- 同期失敗を PR レビューでブロックでき、予期せぬデータ消失を防げる。
- ユーザーデータの Firestore 利用は実績があるため継続。ただし Wasm 対応のため Backend 経由化は必須。

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

| サービス | 採用 |
|---|---|
| Backend | **Google Cloud Run** |
| 代替候補 | Koyeb (ADR で記録) |

採用根拠:

- 無料枠が最も厚い (200 万 req / 360,000 vCPU-sec / 180,000 GiB-sec)。request-based billing で idle 課金なし。
- Artifact Registry / Firebase Admin SDK / Google アカウント認証連携が容易。
- 既存 Dockerfile (amazoncorretto:22 ベース) が Cloud Run 互換。

不採用:

- **Fly.io**: 無料枠が 2024 年に廃止された。
- **Render**: 無料 Web Service は sleep するためコールドスタートが長い。
- **Railway**: $5 初月 + $1/月クレジットで実質有料。

### 3.5 Terraform / IaC

- 本計画では Terraform を採用しない。Cloud Run / Artifact Registry / Firebase の設定は GitHub Actions スクリプトと `gcloud` CLI で管理する。
- 採用しない理由: 管理対象リソースが少なく、Terraform を投入する旨味より学習・保守コストが上回ると判断。ADR で記録する。

### 3.6 撤去対象

| 対象 | 理由 |
|---|---|
| `js/app/` | wasmJs 移行先決定済み、即時撤去で後続リファクタが clean になる |
| `js/material/` | `js/app` 専用、`js/app` 撤去と同時に削除 |
| `kotlin-js-store/` | wasmJs 移行時に再生成 |
| `.github/workflows/web-build-and-deploy.yml` | Web 配信は wasmJs 完成後に再開 |
| `public/` の js/app 専用ファイル | js/app 撤去と同時に整理。共有資源は `core/resources/` 等に退避 |

Web 配信は wasmJs ターゲット完成まで **一時停止** することを許容する。

### 3.7 テストカバレッジ方針 — 即時 100% 必達

AI による自動テスト生成を前提とし、**段階目標は採用しない**。Phase A 終了時点でリポジトリ全体が以下を満たす状態にする:

- **Line coverage / branch coverage いずれも 100%** を CI 上で `koverVerify` が強制する (minValue = 100、counter = LINE / BRANCH 両方)。
- 100% を満たさない PR はマージ不可。新規ファイルも例外なし。
- **除外対象は ADR 0014 で限定列挙** し、それ以外は必ずテストを書く:
  - エントリポイント (`MainKt`, `Application`, `MainActivity` 等)
  - DI モジュール定義 (Koin の `module {}` ブロック)
  - 自動生成コード (SQLDelight 生成クラス、kotlinx.serialization 生成クラス、Compose Compiler 生成コード)
  - 純粋な値クラス / sealed marker（テスト不可能な合成 toString/equals のみ）
- 除外対象を増やすには ADR 改訂が必須。Skill が勝手に除外を増やせないよう、`.claude/rules/coverage-100.md` で禁止規約として明文化。
- UI 層 (Compose) は **Compose UI Test + Roborazzi / Paparazzi (screenshot test)** で達成。ViewModel / Repository / Network Client / Mapper は通常の Kotest で達成。
- **Konsist でペアリング検証**: 実装クラスごとに対応する `*Spec.kt` / `*Test.kt` の存在を機械的にチェックする。

採用根拠:

- AI による自動生成があるため「テストを書くコストが高いから段階目標に」という前提が成り立たない。むしろ初期に 100% を取り切る方が、その後のリファクタや機能追加のリグレッション検出が即時かつ厳密に効く。
- Phase C で大規模リファクタ (Decompose 撤去、フィーチャ再編、Firebase 切り離し) を行うため、その前にセーフティネットを完成させておく価値が大きい。
- 線形カバレッジが 100% でも、AI 生成テストは「網羅性はあるが意味のないテスト」になる可能性がある。これに対しては **Konsist でアサート数や前提条件の存在を機械検証**、KPT で「無意味テスト」を learnings 蓄積し harness-meta が `.claude/rules/kotlin-test.md` を強化していくフォールバックループで担保する。

---

## 4. ドキュメント構造

```
docs/
  README.md
  architecture/
    overview.md                 ─ モジュール依存図 (Mermaid)
  requirements/                 ─ 機能要件 (機能ごとに 1 md)
  specifications/               ─ 仕様詳細 (機能ごとに 1 md)
  adr/                          ─ Architecture Decision Records
    template.md
    0001-record-architecture-decisions.md
    0002-app-architecture-cmp-viewmodel-nav3.md
    0003-module-structure-feature-first.md
    0004-state-and-uiaction-conventions.md
    0005-test-strategy-and-coverage.md
    0006-decompose-removal.md
    0007-i18n-compose-resources.md
    0008-imasparql-sync-upstream-driven.md
    0009-user-data-backend-proxy.md
    0010-backend-hosting-cloud-run.md
    0011-sqlite-file-in-repo.md
    0012-firebase-boundary.md
    0013-remove-js-app.md
    0014-test-coverage-100-percent.md
    0015-imasparql-local-docker-fuseki.md
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
    learnings/                  ─ PR ごとの KPT 出力
  runbooks/
    local-imasparql.md
    sync-imasparql.md
    release.md
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

- 各 PR マージ後に `kpt-retrospective` Skill が `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を生成。
- 月次 (または閾値超過時) に `harness-meta` Skill が learnings を集約し、CLAUDE.md / rules / skills / Lint への改修 PR を起票。

---

## 5. ハーネス構造 (`.claude/`)

```
.claude/
  settings.json
  skills/
    harness-bootstrap/          B0 の唯一の Skill。A3 完了後に archived/ へ
    plan-author/                B0 から導入
    epic-author/                B0 から導入
    kpt-retrospective/          B0 から最小版で稼働
    feature-request/            A3 で完成
    bug-fix/                    A3 で完成
    refactor/                   A3 で完成
    dependency-upgrade/         A3 で完成
    adr-author/                 A3 で完成
    harness-meta/               A3 で完成
    archived/                   引退した Skill (harness-bootstrap など)
  rules/
    rules-index.md
    # 計画・記録
    plan.md
    epic.md
    adr.md
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
    # ファイルタイプ別
    gradle.md
    kotlin-test.md
    screenshot-test.md
    coverage-100.md
    test-paired-class.md
    sql-delight.md
    sparql.md
    markdown.md
    # プロセス
    pr-template.md
    commit-message.md
    branch-naming.md
    # 同期 / Backend
    sync-job.md
    sqlite-data-file.md
    backend-auth.md
    cloud-run-deploy.md
    removed-modules.md
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
| `kpt-retrospective` | PR マージ後の workflow から自動起動 | 直近 PR の diff / コメント / CI ログ / Skill 実行ログを集約し `docs/harness/learnings/` に出力 |
| `feature-request` | ユーザー指示 or Issue | 要件 → 仕様 → ADR (必要時) → Plan / Epic 起票 → 実装 → PR |
| `bug-fix` | Issue / 障害報告 | 再現 → ルートコーズ分析 → ADR (設計起因なら) → 修正 → 回帰テスト → PR |
| `refactor` | 改善提案 | 影響範囲分析 → リスク評価 → 段階的リファクタ → PR |
| `dependency-upgrade` | Renovate PR | リリースノート fetch → 影響モジュール特定 → テスト → 安全なら approve、危険なら downgrade 提案 |
| `adr-author` | 他 Skill から呼ばれる | ADR テンプレに沿って起草、関連 ADR をリンク |
| `harness-meta` | 月次 cron or learnings 閾値超過 | learnings の `Try` / `Suggested harness changes` を集約 → ハーネス改修 PR を起票 |

### 5.4 Skill + KPT ループ

```
[Skill 起動]
   → 要件/仕様生成 (docs/requirements, specifications)
   → Plan or Epic 起票 (docs/plans or docs/epics)
   → 実装 → ./gradlew check + koverVerify
   → PR 作成 → CI green → review → merge
   → kpt-retrospective が docs/harness/learnings/YYYY-MM-DD-pr-N.md 出力
   → 月次で harness-meta が集約 → ハーネス改修 PR
```

---

## 6. フェーズ順序

### 6.1 Phase B — ブートストラップ (手作業、1 PR のみ)

| # | 内容 |
|---|---|
| **B0** | 最小ブートストラップ PR。CLAUDE.md 骨格 / AGENTS.md 骨格 / `.claude/settings.json` / `.claude/skills/{harness-bootstrap, plan-author, epic-author, kpt-retrospective}` の最小版 / `.claude/rules/rules-index.md` 骨格 / `docs/{adr, epics, plans, harness, runbooks, ...}` スケルトン / EPIC-000-harness-foundation 起票 / `.github/workflows/kpt-retrospective.yml` |

B0 完了時点で Skill ループが稼働開始する。以降の全 PR が Skill 駆動 + KPT 生成の対象となる。

### 6.2 Phase A — Skill 駆動による基盤完成

Phase A は **「実装フェーズ前にテストカバレッジ 100% と im@sparql ローカル Docker を含む全ての基盤が整っている」** ことを完了条件とする。Phase C はこの完了をもって初めて着手する。

| # | 単位 | 起動 Skill | 内容 |
|---|---|---|---|
| **A1** | Plan | `harness-bootstrap` | ADR 0001-0015 を一括起草する PR |
| **A2** | Plan | `harness-bootstrap` | `.claude/rules/*` 全ファイル + `docs/` 拡充 (architecture/, requirements/, specifications/ テンプレ等) |
| **A3** | Plan | `harness-bootstrap` | 専用 Skill 群実装 PR (feature-request, bug-fix, refactor, dependency-upgrade, adr-author, harness-meta、および kpt-retrospective の本格版へのアップグレード)。マージ後、`harness-bootstrap` は `archived/` へ |
| **A4** | Plan | `feature-request` | post-merge workflow 本格化 (`kpt-retrospective.yml` 実装一致 / `harness-meta.yml` 月次 cron) |
| **A5** | Plan | `refactor` | 不要モジュール撤去 (`js/app`, `js/material`, `kotlin-js-store`, `web-build-and-deploy.yml`, `public/` 内 js 専用ファイル) |
| **A6** | Plan | `feature-request` | Lint / Format 基盤 (Spotless + ktlint + detekt + Konsist + lefthook) |
| **A7** | Plan | `feature-request` | **Kover 導入 + 100% 強制設定の最初のバージョン** (除外対象は ADR 0014 で定義済みのものに限定、CI で `koverVerify` を必須化)。この PR 時点では既存コードの未充足は除外リストで一旦逃がすが、A8 完了までに全モジュールへ展開する旨を rules に明記 |
| **A8** | Plan | `feature-request` | **im@sparql ローカル Docker 環境構築** (Apache Jena Fuseki + RDF データ初期投入スクリプト + `docker-compose.yml` + integration test 基盤 + Testcontainers 規約)。`docs/runbooks/local-imasparql.md` を整備し、backend のローカル開発・テストを Fuseki に対して実行できる状態にする |
| **A9** | **EPIC-A9** | `refactor` | **既存コード全体に対する 100% line / branch coverage 達成**。モジュールごとに段階 Plan (PLAN-NNN × 多数) を発行し、すべてグリーン化。Konsist の「実装クラス ⇄ テストクラス対応」検証を本 EPIC 完了時に enforce。backend/server / android/app / core/* / data/ 全モジュールが対象。A7 で導入した除外リストは ADR 0014 列挙分のみに整理する |

**Phase A 完了条件**:

- 全モジュールで `./gradlew check koverVerify` がグリーン (line / branch ともに 100%、除外は ADR 0014 列挙分のみ)。
- `docker compose up imasparql` でローカル Fuseki が起動し、backend integration test が Fuseki に対して実行可能。
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
| C5 | **EPIC-003** | `feature-request` | Backend 強化 (`colormaster-api` モジュール、Firebase Admin SDK、ID Token 検証、`/api/me/*` エンドポイント) |
| C6 | **EPIC-004** | `feature-request` | upstream-driven 同期パイプライン (`imas/imasparql` SHA 監視、日次 cron、初期データ投入) |
| C7 | Plan | `feature-request` | Cloud Run デプロイ (GitHub Actions + gcloud CLI、Artifact Registry) |
| C8 | **EPIC-005** | `feature-request` | KMP - iOS ターゲット有効化 + Xcode プロジェクト雛形 |
| C9 | **EPIC-006** | `feature-request` | KMP - wasmJs + GIS 認証代替 + Firebase 切り離し完成 |
| C10 | Plan | `feature-request` | Web 配信再開 (wasmJs ビルドを Firebase Hosting にデプロイ) |

旧計画にあった「C5 im@sparql ローカル Docker」「C12 テストカバレッジ段階引き上げ」は本改訂で **Phase A (A7-A9)** に前倒した結果、Phase C からは消えている。

依存順序の根拠:

- C3 を先頭に: 以降の実装が `feature/*` 構造前提になる。
- C4 を C3 直後に: 新 `feature/*` に composeResources を組み込みたい。
- C5 (Backend 強化) を C6/C7/C9 より前に: Firestore 経由化エンドポイントが揃わないと Cloud Run と wasmJs の意味が薄い。Backend integration test は A8 で整備済の Fuseki 環境を利用。
- C6 (同期パイプライン) を C7 (Cloud Run デプロイ) より前に: `data/idols.db` 初期データがリポジトリに乗らないと Cloud Run コンテナイメージが空になる。
- C8 (iOS) を C9 (wasmJs) より前に: iOS は Firebase JS SDK の制約に当たらず、先に iOS でアーキ全体を検証してから wasmJs の重い切り離しに進む方が安全。

---

## 7. 依存ライブラリのバージョンアップ自動化

- Renovate 強化 (C1):
  - `groupName` で kotlin / ktor / compose / firebase / sqldelight 等を束ねる。
  - `prCreation: approval`、`dependencyDashboard: true`、`extends: [:semanticCommits]`。
- GitHub Actions の `dependency-upgrade-check.yml`:
  - Renovate 起点 PR (`labels:renovate`) で発火。
  - `dependency-upgrade` Skill を起動し、リリースノートを WebFetch、影響モジュールを git grep、テストを実行、結果サマリを PR コメントに記録。
  - 安全と判定したら `approve` ラベル付与、危険なら downgrade 提案。

---

## 8. テストカバレッジ戦略

- Kover を導入、`./gradlew check` に `koverXmlReport` / `koverVerify` を組み込む。
- **「即時 100%」を目的化する**。段階目標は採用しない (ADR 0005 / 0014)。
  - `koverVerify` の minBounds: `minValue = 100`、counter = `LINE` と `BRANCH` の両方。
  - 除外対象は ADR 0014 で限定列挙 (エントリポイント / DI モジュール / 自動生成コード / 純データクラス)。除外追加は ADR 改訂が必須。
  - UI モジュール (Compose) は **Compose UI Test + screenshot test (Paparazzi / Roborazzi)** で 100% を達成。
  - line / branch coverage いずれも 100% を必達とする (`branch + 重要パス` のような曖昧な目標は採らない)。
- **Konsist でペアリング検証**: 各実装クラスに対応するテストクラス (`*Spec.kt` / `*Test.kt` / `*ScreenshotTest.kt`) の存在を機械的に強制する (`.claude/rules/test-paired-class.md`)。
- **AI 自動生成テストの質の担保**: Konsist で「テストクラスは最低 1 つの `should` / `test` / `assert*` を含む」「Mock のみで実装を呼ばずに通過するテストは禁止」等のメタ規約を加え、無意味テストの混入を防ぐ。KPT で発見された無意味テストパターンは harness-meta が `kotlin-test.md` ルールに追加していく。
- **Phase A (A7-A9) で全モジュールを 100% 化** し、Phase C 以降は維持 + 新規分の即時 100% を CI で必達とする。

---

## 9. リスクと未解決事項

| ID | リスク / 論点 | 暫定方針 |
|---|---|---|
| R-1 | iOS Compose Multiplatform は Stable 到達済みだが、scroll physics 等の挙動差異が残る可能性 | C9 で限定機能から検証、ADR に「実験的採用」を残す |
| R-2 | wasmJs での Firebase Auth 代替 (GIS) は実装パターンの公式リファレンスが薄い | C10 で spike PR を最初に切る。Open question として EPIC-006 に記録 |
| R-3 | `data/idols.db` のバイナリ管理によるリポジトリサイズ膨張 | アイドル情報は数百〜千行のため当面は通常 commit。MB 級になったら Git LFS 移行を別 ADR で |
| R-4 | Cloud Run JVM コールドスタート | Phase C 完了後に GraalVM Native Image を別 ADR で検討 |
| R-7 | AI 自動生成テストが「網羅性はあるが意味のないテスト」になる可能性 | Konsist のメタ規約 + KPT 学習 + `kotlin-test.md` 強化のフォールバックループで担保 (3.7 節参照) |
| R-8 | A9 (既存コード 100% 達成 EPIC) の作業量が想定を超える可能性 | モジュール単位で Plan を切り、PR を細粒度に分割。完了見込みが立たない場合は除外対象の見直しを ADR 改訂で対応 (ただし安易な除外追加は禁止) |
| R-9 | Fuseki に投入する RDF データの著作権・ライセンス確認 | A8 でデータ取得元 (`imas/imasparql` リポジトリ) のライセンスを確認し ADR 0015 に明記。条件次第ではダミー RDF + テスト専用データ構成にする |
| R-5 | Skill が rules を読み飛ばすリスク | Konsist / detekt / Gradle カスタムタスクで機械的ガードを二重化 |
| R-6 | harness-bootstrap が万能になりすぎると専用 Skill 化が遅れる | A3 完了で必ず `archived/` へ移動、CLAUDE.md からも参照を外す |

---

## 10. 確認済みの確定事項一覧

本計画策定セッションで合意した事項を集約:

- アーキテクチャは Compose Multiplatform + 共通 ViewModel + Navigation 3。Decompose は撤去。
- i18n は compose-multiplatform-resources。
- アイドル情報はリポジトリ内 SQLite + JSON snapshot、ユーザーデータは Backend 経由の Firestore。
- 同期は `imas/imasparql` SHA 監視、1 日 1 回。差分時のみ PR 自動作成。
- 認証は Firebase Auth + GIS (wasmJs 代替)、Backend で ID Token 検証。
- Backend は Cloud Run。Terraform 不使用。
- `js/app` / `js/material` / `kotlin-js-store` / `web-build-and-deploy.yml` は即時撤去。
- Web 配信は wasmJs 完成まで一時停止を許容。
- 大規模な取り組みは Epic (`docs/epics/EPIC-NNN-<slug>/`)、単一 PR は Plan (`docs/plans/PLAN-NNN-*.md`)。
- Epic / Plan は独立採番。Epic 紐付き Plan も `docs/plans/` に一元化。
- Plan の Notes は自由記述、蓄積したら template に反映。
- 昇格時のステータスは `promoted`。
- B0 のみ手作業、A1 以降は Skill ループ駆動。
- harness-bootstrap は A3 後に `archived/` へ移動。
- KPT は全 PR で生成、harness-meta は月次集約。
- テストカバレッジは **line / branch ともに即時 100%**。段階目標は採用しない。除外対象は ADR 0014 で限定列挙。
- im@sparql ローカル Docker (Fuseki) は **Phase A (A8) で整備**。Phase C 着手前に backend integration test が Fuseki に対して実行可能な状態にする。
- 既存コードの 100% 達成は **Phase A (A9) を EPIC として実施**。Phase C はこの完了をもって着手。

---

## 11. 次のアクション

本ドキュメントの merge を計画確定の起点とする。次のセッションで以下を実施:

1. **B0 (ブートストラップ PR) を 1 本作成**
   - ブランチ: `harness/bootstrap`
   - 内容: 第 4 章 ドキュメント構造 + 第 5 章 ハーネス構造のスケルトン + EPIC-000-harness-foundation 起票
   - レビュー: 唯一の例外として人間レビュー (Skill ループ外)
2. B0 マージ後、A1 (ADR 起草) から Skill 駆動 + KPT ループを稼働させる。
3. **Phase C 着手は A9 完了 (既存コード 100% カバレッジ達成 EPIC 完了 + im@sparql ローカル Docker 整備 + Lint/Test 基盤完成) 後**。これにより Phase C の実装作業は最初からセーフティネットと統合テスト環境の上で進む。
