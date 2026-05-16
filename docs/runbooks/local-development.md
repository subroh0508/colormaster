---
id: runbook-local-development
title: ローカル開発環境構築
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4
---

# ローカル開発環境構築

> **5 行以内 summary**: ColorMaster をローカルで開発するために必要な前提環境
> (JDK / Gradle / IntelliJ / Docker / Node.js / wrangler / gcloud / Roborazzi 等) の
> セットアップ手順。本格化は A2 で実施 (環境バージョン具体化、検証スクリプト追加)。

## 前提環境 (骨格、A2 で本格化)

| ツール | バージョン (推奨) | 用途 |
|---|---|---|
| JDK | (TODO: A2 で確定) | Kotlin / Gradle 実行 |
| Gradle | リポジトリ同梱の `gradlew` を使用 | ビルド |
| IntelliJ IDEA / Android Studio | **2025.2+** (MCP Server プラグインバンドル要件) | コーディング + JetBrains MCP |
| Docker Desktop | (TODO: A2 で確定、A8 で im@sparql Fuseki 起動に必要) | ローカル im@sparql |
| Node.js | (TODO: A2 / A6 で確定、`markdownlint-cli2` 等で必要) | Markdown lint / wrangler |
| wrangler | (TODO: A2 で確定、C7 デプロイで必要) | Cloudflare Pages / R2 |
| gcloud CLI | (TODO: A2 で確定、C7 デプロイで必要) | Cloud Run デプロイ |

## 初回セットアップ手順 (骨格)

1. リポジトリ clone
2. `./scripts/install-git-hooks.sh` 実行 (commit-msg / pre-commit hook 配置)
3. IntelliJ IDEA で Gradle import
4. `./gradlew build` で初回ビルド (キャッシュ生成)
5. `./gradlew check` で全テスト実行

## MCP セットアップ

- `docs/runbooks/mcp-setup.md` に集約

## im@sparql Fuseki ローカル

- `docs/runbooks/local-imasparql.md` (A8 で本格化)

## トラブルシュート

- `docs/runbooks/troubleshooting.md` (将来作成)

## 関連

- `docs/harness/plan.md` §3.5 (Terraform 不使用)
- `docs/runbooks/{mcp-setup,local-imasparql,testing,i18n}.md`
- `scripts/install-git-hooks.sh`
