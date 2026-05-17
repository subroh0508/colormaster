---
id: runbook-local-development
title: ローカル開発環境構築
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0002
  - ADR-0003
  - ADR-0009
  - ADR-0022
  - ADR-0024
---

# ローカル開発環境構築

> **5 行以内 summary**: ColorMaster をローカルで開発するために必要な前提環境
> (JDK / Gradle / IntelliJ / Docker / Node.js / wrangler / gcloud / Roborazzi 等) の
> セットアップ手順。バージョンは `gradle/libs.versions.toml` / `plugins/` / `Dockerfile`
> を Single Source of Truth として参照。Fuseki Docker 等の Phase A〜C 持ち越し項目は
> 末尾の TODO 表に固定し、各フェーズの runbook が確定したら本ファイルからリンク。

## 1. 前提環境 (バージョン)

| ツール | バージョン (要求) | 確認コマンド | Source of Truth |
|---|---|---|---|
| **JDK** | **17** (LTS) | `java -version` | `plugins/src/main/kotlin/.../AndroidDsl.kt` (`JavaVersion.VERSION_17`) |
| Gradle | 同梱の `./gradlew` を使用 (root ビルド) | `./gradlew --version` | `gradle/wrapper/gradle-wrapper.properties` |
| Kotlin | 2.1.21 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `kotlin` |
| AGP (Android Gradle Plugin) | 8.9.0 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `agp` |
| Compose Multiplatform | 1.8.0-alpha04 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `compose` |
| Ktor | 3.1.3 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `ktor` |
| SQLDelight | 2.1.0 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `sqldelight` |
| kotest | 6.0.0.M1 | (Gradle 経由で自動取得) | `gradle/libs.versions.toml` `kotest` |
| **IntelliJ IDEA / Android Studio** | **2025.2 以降** | (IDE 起動画面) | MCP Server プラグインバンドル要件 (ADR 0024) |
| Docker Desktop | Stage 1/2 で `gradle:latest` イメージを利用 | `docker --version` | `Dockerfile` |
| Amazon Corretto (Backend ランタイム) | 22 | — | `Dockerfile` (Stage 3) |
| Node.js | LTS 系 (A2/A6 で確定) | `node --version` | `markdownlint-cli2` 統合 (A6 で決定) |
| wrangler (Cloudflare CLI) | (C7 デプロイで確定) | `wrangler --version` | C7 で `docs/runbooks/cloudflare-pages.md` 起票 |
| gcloud CLI | (C7 デプロイで確定) | `gcloud --version` | C7 で `docs/runbooks/cloud-run-deploy.md` 起票 |
| Claude Code | — | — | https://code.claude.com/ |

注: ローカルでは JDK 17 をビルド・テスト用に利用するが、Backend ランタイムイメージは
**Amazon Corretto 22** で起動するため、本番動作時の JVM ターゲット互換性に注意
(Cloud Run 上は JDK 17 ビルド成果物が Corretto 22 で実行される構成)。

## 2. 初回セットアップ手順

1. リポジトリ clone
   ```bash
   git clone git@github.com:subroh0508/colormaster.git
   cd colormaster
   ```
2. git hook 配置 (commit-msg / pre-commit)
   ```bash
   ./scripts/install-git-hooks.sh
   ```
3. IntelliJ IDEA / Android Studio で **Gradle import** (`File > Open` → リポジトリルート選択)
4. 初回ビルド (依存解決 + キャッシュ生成)
   ```bash
   ./gradlew build
   ```
5. 全 Gradle タスク一覧確認
   ```bash
   ./gradlew tasks --all
   ```
6. 全テスト実行 (Lint + Unit + Konsist)
   ```bash
   ./gradlew check
   ```

## 3. Smoke test (動作確認)

セットアップ完了後の動作確認:

```bash
./gradlew help                   # Gradle が起動できるか
./gradlew tasks                  # 主要タスクが表示されるか
./gradlew :backend:server:tasks  # Backend モジュールが認識されているか
./gradlew :core:common:check     # 共通モジュールがテスト実行できるか
```

エラーが出た場合は `./gradlew --refresh-dependencies build` で依存キャッシュを再構築。

## 4. JVM ヒープ設定

`gradle.properties` で JVM ヒープを `-Xmx4096m` に設定済 (`org.gradle.jvmargs=-Xms2048m -Xmx4096m`)。
ローカル環境のメモリ不足で OOM が頻発する場合は **`~/.gradle/gradle.properties`** (個人マシン
固有) で上書き設定し、本リポジトリ側は変更しないこと。

## 5. MCP セットアップ

`docs/runbooks/mcp-setup.md` を参照 (JetBrains MCP / Context7 MCP / Cloudflare MCP の 3 つ)。

## 6. im@sparql Fuseki ローカル起動

`docs/runbooks/local-imasparql.md` (A8 で本格化)。現時点では未整備。

## 7. Backend ローカル起動

`docs/runbooks/backend-local.md` (C5 で起票予定)。現時点では `./gradlew :backend:server:run`
で起動可能だが、`users.db` の Litestream restore 等は C5 まで未整備。

## 8. トラブルシュート

| 症状 | 原因 | 対処 |
|---|---|---|
| `./gradlew build` で OOM | JVM ヒープ不足 | `~/.gradle/gradle.properties` で `-Xmx8192m` 等に増加 |
| `./gradlew check` で Konsist エラー | Kotlin source の構造違反 | エラーメッセージ + `.claude/rules/<該当 rule>.md` を確認 |
| IDE で MCP 接続失敗 | IDE バージョン不足 / プラグイン未有効 | `docs/runbooks/mcp-setup.md` §1 のトラブルシュート表 |
| Android emulator 起動失敗 | KVM / Hyper-V 未有効 | `~/.android/avd/` を確認、AGP 8.9.0 対応の SDK レベルか確認 |
| commit-msg hook が動かない | hook 未配置 | `./scripts/install-git-hooks.sh` を再実行 |

詳細は将来 `docs/runbooks/troubleshooting.md` (未作成) に集約予定。

## Phase A〜C 持ち越し (各 runbook 確定で本格化)

| 持ち越し項目 | 持ち越し先 | 理由 |
|---|---|---|
| `docs/runbooks/local-imasparql.md` (Apache Jena Fuseki Docker 起動手順) | A8 | im@sparql 同期 Skill 本格化と連動 |
| `docs/runbooks/backend-local.md` (Backend ローカル起動 + users.db 復元手順) | C5 | Backend 本格化と連動 |
| `docs/runbooks/cloudflare-pages.md` (wrangler 認証 / preview デプロイ手順) | C7 | 静的配信デプロイ実装と連動 |
| `docs/runbooks/cloud-run-deploy.md` (gcloud 認証 / Cloud Run デプロイ手順) | C7 | Backend デプロイ実装と連動 |
| `docs/runbooks/troubleshooting.md` (横断的トラブルシュート集約) | A6〜A10 | Lint / Format 基盤 / UI Inventory 確定後に集約 |
| Node.js LTS バージョン確定 | A6 | `markdownlint-cli2` 統合と連動 |

## 関連

- `docs/harness/plan.md` §3.5 (Terraform 不使用) / §6.2 A〜C
- `docs/runbooks/{mcp-setup,testing,i18n}.md`
- ADR 0002 / 0003 (Kotlin Multiplatform + Compose Multiplatform + Gradle build 構成)
- ADR 0009 / 0022 (Cloud Run + Cloudflare Pages デプロイ先)
- ADR 0024 (MCP サーバ採用、IDE バージョン要件)
- `scripts/install-git-hooks.sh`
- `gradle/libs.versions.toml`
- `plugins/`
