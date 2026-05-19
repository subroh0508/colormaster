---
id: rules-docker-cli
title: Docker CLI 利用規約 (Claude Code Docker CLI 分離)
status: stable
last_updated: 2026-05-20
paths:
  - ".claude/skills/**/SKILL.md"
  - "docs/runbooks/local-imasparql.md"
  - "docs/runbooks/local-development.md"
  - "docs/runbooks/claude-code-docker-setup.md"
  - "docker-compose.yml"
  - "scripts/docker-claude.sh"
related_adrs:
  - ADR-0014
  - ADR-0021
related_plan: docs/plans/PLAN-007-claude-code-docker-cli-isolation.md
---

# Docker CLI 利用規約 (Claude Code Docker CLI 分離)

> **5 行以内 summary**: Claude Code セッションから Docker を実行する場合は
> `./scripts/docker-claude.sh` (`DOCKER_CONFIG=$HOME/.docker-claude` を export して
> `exec docker "$@"` する wrapper) を **絶対パス強制** で叩く。macOS Docker Desktop
> `credsStore: "desktop"` が非対話的 shell で Keychain helper を呼び出して hang する症状を
> 回避する。通常ターミナル / 人間手元実行 / CI は影響なし (通常 `docker` のまま)。

## 基本方針

- **Claude Code セッションから Docker を実行する場合は `./scripts/docker-claude.sh`** を絶対パスで叩く
- `docker` 直叩きは **禁止** (`credsStore: "desktop"` hang リスクあり)
- 通常ターミナル / 人間手元実行 / CI は通常 `docker` を継続利用 (本 rule の対象外)
- wrapper は repo 内 commit (`scripts/docker-claude.sh`、`chmod +x` 済)、絶対パス呼出に統一

## 背景

macOS Docker Desktop の `~/.docker/config.json` に `credsStore: "desktop"` が設定されると、
`docker-credential-desktop` helper が credential 操作 (`pull` / `push` / `compose up -d` 等の
内部 auth lookup を含む) で Keychain にアクセスしようとする。Claude Code の非対話的 shell では
Keychain prompt が処理できず、コマンドが 0% CPU で hang する事象が PLAN-007 §背景の Step A で
発生した。

回避策として `DOCKER_CONFIG` を `~/.docker-claude` 等の代替パスに分離し、その配下に
`credsStore` を持たない `config.json` を配置することで credential helper を bypass する。
Docker daemon 自体は健全 (`docker info` / `docker images` は応答済) であり、credential helper
のみが症状の原因。

関連 upstream issue:

| 出典 | 概要 |
|---|---|
| docker/for-mac #7209 | "Verifying credentials failed" hang、Docker Desktop 起動直後 |
| docker/docker-credential-helpers #319 | macOS Sonoma + Apple Silicon、login Keychain prompt 連発 |
| docker/docker-credential-helpers #65 | docker login が random Keychain entry を読みに行く |
| docker/for-mac #3774 | "Securely store Docker logins in macOS keychain" 設定変更で login 失敗 |

## 運用

| 項目 | 確定方針 |
|---|---|
| wrapper 配置 | `scripts/docker-claude.sh` (repo 内 commit、`chmod +x` 済) |
| 呼出 | **絶対パス強制** (`./scripts/docker-claude.sh ...`) |
| PATH 通し / direnv / alias | 本 rule では言及しない (必要時に別 PR で検討) |
| `DOCKER_CONFIG` の `.zshrc` export | しない (wrapper のみで完結、Claude Code 起動時の自動 export は副作用大) |
| 通常ターミナル / 人間手元実行 | 通常 `docker` を継続利用 (本 rule の対象外、`credsStore: "desktop"` 温存) |

## 設定手順

ホスト側 `~/.docker-claude/config.json` の 1 回限り作成手順は
`docs/runbooks/claude-code-docker-setup.md` を Single Source of Truth として参照。
本 rule では repo 側 SoT (`scripts/docker-claude.sh` + 呼出規約) のみ扱う。

## 適用範囲

| 対象 | wrapper 利用 | 備考 |
|---|---|---|
| Claude Code セッション経由の `docker` / `docker compose` | ✅ 必須 (`./scripts/docker-claude.sh`) | 本 rule の主対象 |
| 通常ターミナル / 人間手元実行 | ❌ 利用不要 (通常 `docker` で OK) | `credsStore: "desktop"` 温存、Docker Desktop UX は変えない |
| CI (`.github/workflows/**`) | ❌ 利用不要 (GitHub Actions runner は Keychain なし、credsStore 影響なし) | 通常 `docker` で OK、CI で wrapper を強制しない |
| Cloud Run / Backend サービス (本番) | ❌ 利用不要 (本番 runtime は Docker CLI を叩かない) | `cloud-run-deploy.md` 参照 |

## 機械検証 (A6 で導入)

本 rule の機械検証は A6 step5 / step6 で別途実装予定 (本 PR では placeholder 予約):

- **step5 (frontmatter 整合検証、Gradle カスタムタスク)**:
  - `.claude/rules/docker-cli.md` の `paths` 配列に列挙されたファイルパターンが repo 内に実在することを検証
  - frontmatter `related_adrs` / `related_plan` のリンク先実在性検証 (`docs-structure.md` §機械検証 と統合)
- **step6 (shellcheck CI 統合)**:
  - `scripts/docker-claude.sh` 及び `scripts/*.sh` 全件に対し shellcheck を `.github/workflows/` で実行
  - trufflehog (`secrets.md` §機械検証) と並列構成、`secret-scan.yml` と同 workflow か別 workflow かは A6 で決定
- **(参考) `bash -n` syntax check は本 PR 起票時に手動実行済** (`scripts/docker-claude.sh` AC-7、`docs/plans/PLAN-007-claude-code-docker-cli-isolation.md` §受け入れ基準 参照)

## Gotchas

- **`./scripts/docker-claude.sh` 忘れ → 通常 `docker` が hang する**: Claude Code セッションから `docker compose up -d` 等を直叩きすると `credsStore: "desktop"` 経由の Keychain prompt で 0% CPU hang。**絶対パス強制で wrapper を叩く**
- **private registry login 利用時は `~/.docker-claude/` 側に auth が保存される**: `./scripts/docker-claude.sh login` を実行すると `~/.docker-claude/config.json` の `auths` セクションに credentials が書き込まれる。Docker Desktop の Keychain integration は使われないため、機密度に応じて手動ローテーション必要。詳細は `docs/runbooks/claude-code-docker-setup.md` §private registry 取扱い security note 参照
- **`~/.docker-claude/config.json` を repo に commit しない**: ホスト側のローカル設定であり `.gitignore` で除外 (auths 漏洩防止、`secrets.md` 絶対 commit 禁止リストと整合)
- **wrapper の `set -euo pipefail`** は failure を早期検出するための定型。`exec docker "$@"` で PID 引き継ぎ + 引数 pass-through を確実に行う
- **`DOCKER_CONFIG` の既定値 fallback**: wrapper 内 `${DOCKER_CONFIG:-$HOME/.docker-claude}` により、呼出側が `DOCKER_CONFIG` を明示 export した場合はそれを尊重 (テスト用に一時パスに切り替えるユースケース等)
- **CI / 本番では wrapper を使わない**: 本 rule §適用範囲 参照、`credsStore: "desktop"` は Docker Desktop 固有の症状であり Linux runner / Cloud Run には存在しない
- **本 rule は shell コマンド例示を許容** (`docs-structure.md` §4.6.1 コード禁止原則は `docs/{requirements,specifications}/**` のみ対象、rule / runbook は対象外)

## 関連

- `docs/runbooks/claude-code-docker-setup.md` (ホスト側 setup 手順 SoT、`~/.docker-claude/config.json` 作成 / private registry 取扱い)
- `docs/runbooks/local-imasparql.md` (Fuseki Docker 運用、Claude Code 経由実行時の wrapper 利用注記)
- `docs/plans/PLAN-007-claude-code-docker-cli-isolation.md` (本 rule の起票 Plan、AC-1〜AC-8)
- ADR-0014 (Fuseki Docker 採用、`docker-compose.yml` 配置根拠)
- ADR-0021 (Secrets 管理ポリシー、private registry login 取扱いで参照)
- `.claude/rules/{sparql,cloud-run-deploy,db-protection,secrets,pii}.md` (Docker 関連 rule、SoT cross-link で本 rule を参照する想定、gradual rollout は別 PR)
- `scripts/docker-claude.sh` (wrapper 実体、`chmod +x` 済)
