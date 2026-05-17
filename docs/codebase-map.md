---
id: codebase-map
title: コードベースマップ (主要パス → 責務 / 関連 SPEC-ID / 関連 ADR)
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0002
  - ADR-0003
  - ADR-0008
  - ADR-0009
  - ADR-0010
  - ADR-0011
  - ADR-0012
  - ADR-0020
  - ADR-0022
  - ADR-0023
  - ADR-0027
---

# コードベースマップ

> **5 行以内 summary**: 主要パスとその責務 / 関連 SPEC-ID / 関連 ADR / 関連 rules の対応表。
> AI が「このディレクトリは何のためにあるか」「触ったら何が壊れるか」を一発で把握する
> ためのインデックス。`.claude/rules/rules-index.md` (規約索引) と相補。
> 撤去対象 (Firebase / Kotlin/JS 系、ADR 0011 / 0012) は表内で明示。
> Phase C 持ち越しは末尾の TODO 表に固定。

## 1. アプリ層 (画面 / 機能)

| パス | 責務 | 関連 SPEC | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `android/app/` | Android アプリエントリポイント (`MainActivity`)、Compose Multiplatform の Android ターゲット | TBD (C3 で追記) | ADR 0002 | composable.md, navigation.md |
| `core/features/home/` | Home 画面 (現状の旧実装、C3 で `feature/home/` に再構成予定) | TBD (C3) | ADR 0002 / 0011 | viewmodel.md, composable.md |
| `core/features/myidols/` | 担当 (お気に入り) 画面 | TBD (C3) | ADR 0002 | viewmodel.md, ui-state.md |
| `core/features/search/` | アイドル検索画面 | TBD (C3) | ADR 0002 | viewmodel.md, composable.md |
| `core/features/preview/` | アイドル詳細プレビュー画面 | TBD (C3) | ADR 0002 | composable.md |

## 2. プラットフォーム横断層 (core/)

| パス | 責務 | 関連 SPEC | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `core/common/` | 全層共通のユーティリティ・拡張関数 (`commonMain` / `androidMain` / `jsMain`) | — | ADR 0002 / 0003 | naming.md, wasm-compat.md |
| `core/model/` | ドメインモデル (Idol / Brand / ColorPalette 等の不変データクラス) | TBD (C5) | ADR 0002 | naming.md |
| `core/data/` | Repository 実装、ローカル DB アクセス、キャッシュ層 | TBD (C5) | ADR 0002 / 0008 | repository.md, error-handling.md, sql-delight.md |
| `core/network/imasparql/` | im@sparql エンドポイントへの SPARQL クライアント | TBD (C5) | ADR 0007 | network-client.md, sparql.md |
| `core/network/auth/` (**撤去対象**) | Firebase Auth 連携 (旧) | — | ADR 0011 | no-firebase.md, removed-modules.md |
| `core/network/firestore/` (**撤去対象**) | Firestore 連携 (旧) | — | ADR 0011 | no-firebase.md, removed-modules.md |
| `core/test/` | テスト共通ユーティリティ (`commonMain` / `androidMain` / `jsMain`) | — | ADR 0013 / 0016 | kotlin-test.md, test-paired-class.md |

## 3. Backend (Cloud Run デプロイ対象)

| パス | 責務 | 関連 SPEC | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `backend/server/` | Ktor 製 HTTP サーバ (Cloud Run コンテナ起動エントリポイント) | TBD (C5) | ADR 0009 / 0011 | backend-auth.md, cloud-run-deploy.md |
| `backend/cli/` | Backend CLI (sync 実行 / バックアップ操作等) | TBD (C5) | ADR 0007 / 0008 | sync-job.md |
| `Dockerfile` | Cloud Run コンテナ定義 (Stage 1: cache / Stage 2: build / Stage 3: Amazon Corretto 22 runtime) | — | ADR 0009 / 0010 | cloud-run-deploy.md, db-protection.md |
| `.dockerignore` | コンテナイメージへの PII / Secrets 焼込み禁止 | — | ADR 0020 / 0021 | db-protection.md (A6 で本格化) |

## 4. データファイル

| パス | 責務 | 関連 SPEC | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `data/idols.db` (将来配置) | アイドル情報マスタ (read-only、リポジトリ commit + イメージ焼込み可) | TBD (C5) | ADR 0008 / 0010 | sqlite-data-file.md |
| `data/users.db*` (commit 禁止) | ユーザーデータ (uid のみ、PII 最小化、R2 へ Litestream replicate) | — | ADR 0008 / 0020 / 0021 | db-protection.md, pii.md, secrets.md |
| `**/*.sq` (将来配置) | SQLDelight スキーマ定義 (`.sq` ファイル) | — | ADR 0008 | sql-delight.md |
| `**/*.rq` (将来配置) | SPARQL クエリ (`.rq` ファイル、`core/network/imasparql/` で参照) | — | ADR 0007 | sparql.md |

## 5. ビルド / プラグイン

| パス | 責務 | 関連 ADR | 関連 rules |
|---|---|---|---|
| `build.gradle.kts` (root) | ルートビルド定義 (plugins alias only、`apply false`) | ADR 0003 | gradle.md |
| `settings.gradle.kts` | サブモジュール宣言 + version catalog | ADR 0003 | gradle.md |
| `gradle/libs.versions.toml` | バージョンカタログ (Kotlin 2.1.21 / AGP 8.9.0 / Compose 1.8.0-alpha04 / Ktor 3.1.3 / SQLDelight 2.1.0 / kotest 6.0.0.M1) | ADR 0003 | gradle.md |
| `gradle.properties` | JVM オプション (`-Xmx4096m`)、AndroidX 有効化 | ADR 0003 | gradle.md |
| `plugins/` | カスタム Gradle plugin (`colormaster.primitive.*`、JDK 17 target) | ADR 0003 | gradle.md |
| `gradle/wrapper/` | Gradle Wrapper | ADR 0003 | — |

## 6. 撤去対象 (Firebase / Kotlin/JS / Hosting 系、ADR 0011 / 0012)

| パス | 責務 (旧) | 撤去フェーズ | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `js/app/` | Kotlin/JS フロントエンド (旧) | (撤去 Plan で削除) | ADR 0012 | removed-modules.md |
| `js/material/` | Kotlin/JS Material UI ラッパー (旧) | (撤去 Plan で削除) | ADR 0012 | removed-modules.md |
| `kotlin-js-store/` | Kotlin/JS lock ファイル (旧) | (撤去 Plan で削除) | ADR 0012 | removed-modules.md |
| `public/` | Firebase Hosting 静的配信ファイル (旧) | (撤去 Plan で削除) | ADR 0011 / 0022 | removed-modules.md |
| `firebase.json` / `.firebaserc` | Firebase 設定 (旧) | (撤去 Plan で削除) | ADR 0011 | no-firebase.md |
| `web-build-and-deploy.yml` | Firebase Hosting 用 CI (旧) | (撤去 Plan で削除) | ADR 0011 / 0012 / 0022 | removed-modules.md |

## 7. CI / スクリプト

| パス | 責務 | 関連 ADR | 関連 rules |
|---|---|---|---|
| `.github/workflows/**` | GitHub Actions CI (`./gradlew check` 実行)。Claude API 直接呼び出し禁止 (ADR 0017) | ADR 0017 | gradle.md |
| `.github/PULL_REQUEST_TEMPLATE/` | PR テンプレ (`feature.md` / `bugfix.md` / `harness.md` 等) | ADR 0027 | pr-template.md (A2-3) |
| `scripts/install-git-hooks.sh` | git hook (commit-msg / pre-commit) 配置スクリプト | ADR 0001 | commit-message.md |
| `renovate.json` | Renovate Bot 設定 (dependency PR の自動起票) | — | (dependency-upgrade Skill が処理、A3) |

## 8. ドキュメント / ハーネス

| パス | 責務 | 関連 ADR | 関連 rules |
|---|---|---|---|
| `DESIGN.md` (repo root) | デザイントークン (Google Stitch 3 階層) + Rationale | ADR 0023 / 0027 | design-tokens.md |
| `docs/` | 全ドキュメント (要件 / 設計 / API / ADR / Epic / Plan / Harness / Runbooks / Design) | ADR 0027 | docs-structure.md |
| `.claude/skills/` | ハーネス Skill 群 (起動契機別の責務単位) | ADR 0025 / 0026 | skill-authoring.md |
| `.claude/rules/` | コーディング / プロセス / 命名 / セキュリティ規約 | — | rules-index.md |
| `.claude/mcp.json` | JetBrains MCP / Context7 MCP / Cloudflare MCP の接続定義 | ADR 0024 | mcp-usage.md |
| `.claude/settings.json` / `.claude/settings.local.json` | Claude Code 設定 (hooks / permissions / env) | — | (update-config Skill が編集) |
| `CLAUDE.md` (常時ロード) | AI 用エントリ + ファイル種別 ⇄ rules lookup table | ADR 0027 | docs-structure.md |
| `AGENTS.md` | Claude Code 以外の AI Coding Agent 向けエントリポイント | ADR 0027 | docs-structure.md |

## Phase C 持ち越し (機能実装で本格化)

| 持ち越し項目 | 持ち越し先 | 理由 |
|---|---|---|
| `feature/` 配下の各モジュール詳細 (Home / Search / MyIdols / Preview / Settings) | C3 (feature/ モジュール再構成) | C3 で旧 `core/features/**` を `feature/**` へ移行 |
| `core/data/**/*Repository.kt` の各 Repository 責務一覧 | C5 (`core/data` 本格化) | Repository / Datasource の責務確定後 |
| `backend/server/` の Ktor ルーティング構造 (Routes / Plugins / Pipeline) | C5 (Backend 本格化) | ルーティング設計確定後 |
| `data/idols.db` / `data/users.db` の SQLDelight スキーマ (`*.sq` ファイル一覧) | C5 (`core/data` 本格化) | スキーマ確定後 |
| `core/network/imasparql/**/*.rq` の SPARQL クエリ一覧と責務 | C9 (im@sparql 同期実装) | クエリセット確定後 |
| `core/design-system/` (デザイントークン参照層) | A10 / C3 (DESIGN.md 本格生成後) | A10 で DESIGN.md 確定後 |

## 注意事項

- **撤去対象** の `js/` / `public/` / `firebase.json` などを編集する PR は `.claude/rules/removed-modules.md` (A2-2 で本格化) の認定を経て撤去 Plan として起票する
- `core/network/auth/` / `core/network/firestore/` は **新規 import 禁止** (`.claude/rules/no-firebase.md`)、Konsist の `firebase-boundary.md` で検出
- `data/users.db*` を Dockerfile で COPY するのは **禁止** (`.claude/rules/db-protection.md`)。`.dockerignore` で除外を機械検証 (A6)

## 関連

- `docs/harness/plan.md` §4 / §3.6 (撤去対象一覧)
- `docs/glossary.md` (ドメイン用語)
- `docs/traceability.md` (A6 で自動生成)
- `.claude/rules/rules-index.md`
- `CLAUDE.md` (ファイル種別 ⇄ rules lookup table)
