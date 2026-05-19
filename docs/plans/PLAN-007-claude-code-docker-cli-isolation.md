---
id: PLAN-007
title: Claude Code Docker CLI 分離 (DOCKER_CONFIG + wrapper script)
type: harness
status: proposed
related_pr: null
related_epic: null
related_specs: []
related_adrs:
  - ADR-0014
  - ADR-0021
expected_modules:
  - scripts/docker-claude.sh
  - .claude/rules/docker-cli.md
  - docs/runbooks/claude-code-docker-setup.md
  - docs/runbooks/local-imasparql.md
  - CLAUDE.md
  - .claude/rules/rules-index.md
created_at: 2026-05-20
completed_at: null
promoted_to: null
---

# Claude Code Docker CLI 分離 (DOCKER_CONFIG + wrapper script)

> **5 行以内 summary**: macOS Docker Desktop `credsStore: "desktop"` が Claude Code 非対話的 shell で
> Keychain helper 呼出により hang する症状を解消するため、`DOCKER_CONFIG` 分離 + wrapper script
> (案 A+A2) を採用。`scripts/docker-claude.sh` を repo 内 commit し、絶対パス強制 (`./scripts/docker-claude.sh`)
> で運用。本 PR は **Plan 起票 (2 ファイル)** で完結、実装 (script + rule + runbook 6 ファイル) は
> 後続 PR-B (`harness/claude-code-docker-cli-isolation` branch) で実施し Step A blocker を解消する。

## 目的

Claude Code セッションから実行する Docker コマンドが credential helper を経由せず安定動作する
再現可能な手順を SoT 化し、本セッションの Step A (im@sparql Docker 実環境動作確認) で発生した
blocker (`docker pull` / `docker compose up -d` の 0% CPU hang) を解消する。

## 背景

本セッションの Step A 実行中、`docker compose up -d` および `docker pull stain/jena-fuseki:4.10.0`
が 0% CPU で hang する事象が発生。原因は macOS Docker Desktop の `credsStore: "desktop"`
(`docker-credential-desktop`) が Claude Code セッションの非対話的 shell で Keychain にアクセス
しようとして固まる症状で、Docker daemon 自体は健全 (`docker info` / `docker images` は応答)。

既知の関連 GitHub Issues:

| 出典 | 概要 |
|---|---|
| docker/for-mac #7209 | "Verifying credentials failed" hang、Docker Desktop 起動直後 |
| docker/docker-credential-helpers #319 | macOS Sonoma + Apple Silicon、login Keychain prompt 連発 |
| docker/docker-credential-helpers #65 | docker login が random Keychain entry を読みに行く |
| docker/for-mac #3774 | "Securely store Docker logins in macOS keychain" 設定変更で login 失敗 |

ColorMaster repo の Docker 言及範囲 audit:

- Docker 言及 60 ファイル / 95+ 箇所
- 現状 actual Docker コマンド利用は `docker-compose.yml` + Fuseki runbook の `docker compose` のみ
- Dockerfile / `.dockerignore` は未配置 (Phase C5 で本格化予定)
- 影響 rule (現実利用): `local-imasparql.md` / `local-development.md` / `sparql.md`
- 影響 rule (将来言及): `cloud-run-deploy.md` / `db-protection.md` / `sqlite-data-file.md` / `sql-delight.md` / `pii.md` / `network-client.md` / `sync-job.md` 他

本 PR は本 hang 症状の workaround 手順を SoT 化する。本問題の直接トリガは PR #175
(A8 Docker Fuseki 配置)、直接前段は PR #186 (Step B 計画立案)、Step A の blocker 解消で Task #11
を completed に昇格する道筋を整える。

## アプローチ

**案 A+A2 (DOCKER_CONFIG 分離 + wrapper script) を採用**。比較検討した 5 案 (A / A2 / B / C / D / E)
のうち、撤回コスト最小・影響範囲限定・Docker Desktop Keychain 連携温存・公式仕様準拠・dogfood
検証可の 5 観点で最良と判断した (詳細は `/Users/subroh_0508/.claude/plans/wise-sniffing-seal.md` §2-§3
で起草、本 Plan の §メモ で要約引用)。

### 採用ポイント (確定方針、2026-05-20 ユーザー確認済)

| 項目 | 確定内容 |
|---|---|
| wrapper 名 | `docker-claude` |
| 配置 | `scripts/docker-claude.sh` (repo 内 commit、`scripts/install-git-hooks.sh` と同列) |
| 呼出方法 | **絶対パス強制** (`./scripts/docker-claude.sh ...`) |
| PATH 通し / direnv / alias | 本 PR では言及しない (将来必要時に別 PR) |
| PR #175 Improvement 4 (Renovate image tag) | 本 PR scope 外 |
| A6 機械検証 | 本 PR で placeholder 予約 (`.claude/rules/docker-cli.md` §機械検証 (A6 で導入)) |

### 本 PR (PR-A) の scope

本 Plan 起票 PR (PR-A) は **2 ファイル touch** で完結:

- 新規: `docs/plans/PLAN-007-claude-code-docker-cli-isolation.md` (本 Plan 本体)
- 更新: `docs/plans/INDEX.md` (PLAN-007 行追加、`last_updated: 2026-05-20`)

### 後続 PR-B の scope (本 Plan 配下、実装)

- branch: `harness/claude-code-docker-cli-isolation`
- 6 ファイル touch (`expected_modules` 参照)
- commit logical separator (`implementation-workflow.md` §commit 分離規範 準拠)
  で 4-5 commit に分離 (script / rule / setup runbook / local-imasparql 更新 / CLAUDE+rules-index)

### 受け入れ基準と後続 Step の関係

本 Plan の §受け入れ基準 (AC-1〜AC-8) は **後続 PR-B (実装)** で達成すべき条件を定義する。
本 PR-A は Plan 本体 + INDEX 起票のみで、AC-1〜AC-8 の達成は PR-B の merge を以て確認する。

## 受け入れ基準

後続 PR-B (`harness/claude-code-docker-cli-isolation` branch) で以下を達成:

- [ ] **AC-1**: `scripts/docker-claude.sh` 新規起票 (`#!/usr/bin/env bash` + `DOCKER_CONFIG="$HOME/.docker-claude"`
      の export + `exec docker "$@"` を含む wrapper、`chmod +x` 済で commit、`bash -n` syntax check pass)
- [ ] **AC-2**: `.claude/rules/docker-cli.md` 新規起票 (status: `stable (Phase A、本 PR で起票)`、
      frontmatter `paths` で適用範囲指定、wrapper 利用規約 + `DOCKER_CONFIG` 分離方針 + 絶対パス強制呼出 SoT 化 +
      §機械検証 (A6 で導入) placeholder)
- [ ] **AC-3**: `docs/runbooks/claude-code-docker-setup.md` 新規起票 (ホスト側 `~/.docker-claude/config.json`
      作成手順 + repo 内 wrapper 利用 + Claude Code への指示文例 + 切り分けコマンド + private registry
      取扱い security note)
- [ ] **AC-4**: `docs/runbooks/local-imasparql.md` に「Claude Code 経由実行時は
      `./scripts/docker-claude.sh` を使う」注記追加 (§3 / §6 / トラブルシュート 3 箇所)
- [ ] **AC-5**: `CLAUDE.md` の「グローバルルール」セクション末尾に Docker wrapper 利用ルール追加 +
      lookup table に `docker-cli.md` の path 行追加
- [ ] **AC-6**: `.claude/rules/rules-index.md` 索引「プロセス」カテゴリに `docker-cli` 行追加
- [ ] **AC-7**: `scripts/docker-claude.sh` の動作確認 (shellcheck pass、`bash -n` syntax check pass)
- [ ] **AC-8**: 本 PR-B の設計書本文 (`.claude/rules/docker-cli.md` / `docs/runbooks/claude-code-docker-setup.md`)
      は Markdown + shell コマンド例のみ、Kotlin / Gradle コード断片を含まない
      (`docs-structure.md` §4.6.1 コード禁止原則整合、runbook における shell コマンド例は許容)

## スコープ外

- **PR #175 Improvement 4** (`docker-compose.yml` image tag の Renovate 対応): 後続 Plan で対応
- **Step 1 ホスト setup** (`~/.docker-claude/config.json` 作成): PC 復旧後 1 回限りの人手 setup、
  別 Step で実行 (本 Plan は repo 側 SoT 化のみ)
- **Step 3 dogfood 検証** (`./scripts/docker-claude.sh pull stain/jena-fuseki:4.10.0` 実行):
  PC 復旧後の Step A 再実行で別 Step、Task #11 を completed 昇格させる
- **private registry login / `docker login` の取扱い**: 必要時は別 PR (`~/.docker-claude/` 側で
  `./scripts/docker-claude.sh login`、security note は runbook で明示)
- **shellcheck の CI 統合**: A6 step6 (trufflehog と並列で検討)、本 PR-B は `bash -n` syntax check のみ
- **その他 Docker 言及 rule** (`sparql.md` / `cloud-run-deploy.md` / `db-protection.md` 等)
  への wrapper 利用注記: gradual rollout で別 PR、本 PR-B は SoT cross-link
  (`.claude/rules/docker-cli.md` 参照) で導線確保のみ
- **`alias docker-claude='./scripts/docker-claude.sh'`** を `~/.zshrc` 等に書く運用: 絶対パス
  確定により不要、個人選択 (本 Plan では言及しない)
- **`DOCKER_CONFIG` の export を `.zshrc` に書く**: wrapper のみで完結 (Claude Code 起動時の
  自動 export は副作用大、明示的に `scripts/docker-claude.sh` 経由で運用)

## ロールバック手順

PR-A (本 Plan 起票): `git revert <PR-A-merge-commit>` で 2 ファイル (Plan 本体 + INDEX 行) を削除。

PR-B (実装): `git revert <PR-B-merge-commit>` で 6 ファイル変更を完全 revert。外部依存ゼロ
(host 側 `~/.docker-claude/config.json` は別 Step で人手作成のため repo 側は touch なし)、
通常ターミナルの Docker Desktop 利用は影響を受けない (`credsStore: "desktop"` 温存)。

## 後続 Step

| Step | 内容 | 実行タイミング |
|---|---|---|
| Step 1 | ホスト側 `~/.docker-claude/config.json` 作成 (1 回限り、`{ "auths": {} }`) | PC 復旧後、人手で実行 |
| Step 2 (本 Plan) | repo 側 PR-A (本 Plan 起票) → PR-B (実装、6 ファイル) | PR-A 本 PR、PR-B は merge 後着手 |
| Step 3 | dogfood 検証 (`./scripts/docker-claude.sh pull` + Fuseki 起動確認 + `curl localhost:3030`) | PR-B merge 後、Step A 再実行で Task #11 completed 昇格 |

## メモ

### 採用比較サマリ (Plan 起草元 `wise-sniffing-seal.md` §2-§3 を要約引用)

`wise-sniffing-seal.md` は Claude Code plan workflow 内部 plan file (repo 外、orchestrator pane
が plan mode で起草) のため repo 内 cross-link 不可。本 Plan §メモ で要約引用する。

| 案 | 概要 | 撤回コスト | 採否 |
|---|---|---|---|
| A | `DOCKER_CONFIG` 分離 (`~/.docker-claude/config.json` + 環境変数) | 低 | 採用 (A2 と併用) |
| A2 | wrapper script (`docker-claude`) で `DOCKER_CONFIG` 設定 + `exec docker` | 低 | 採用 (A と併用、repo 内 script 配置) |
| B | `credsStore` を `osxkeychain` に切替 | 中 | 不採用 (通常ターミナル影響) |
| C | `credsStore` 削除 (グローバル) | 高 | 不採用 (ChatGPT 明示の非推奨) |
| D | Colima 置換 | 非常に高 | 不採用 (scope 拡大、overkill) |
| E | `docker login --password-stdin` で auth inline 化 | 中 | 不採用 (security 不利) |

### Open Questions (確定 4 件 + 暫定 4 件、2026-05-20)

確定 4 件 (本 Plan 確定方針として §アプローチ に反映済):

| 項目 | 確定内容 |
|---|---|
| wrapper 名 (`scripts/<name>.sh`) | `docker-claude` → `scripts/docker-claude.sh` |
| PATH 通し手段 | 絶対パスで叩く (`./scripts/docker-claude.sh` 単独推奨)、`.zshrc` 追加 / direnv / alias は本 PR で言及しない |
| PR #175 Improvement 4 | 本 PR scope 外、後続 Plan で対応 |
| A6 機械検証 placeholder | 本 PR-B で placeholder 予約 (`.claude/rules/docker-cli.md` §機械検証) |

暫定 4 件 (本 Plan では決定保留、後続検討):

| 項目 | 暫定方針 |
|---|---|
| `alias docker-claude='./scripts/docker-claude.sh'` を `~/.zshrc` に書くか | 本 PR では言及しない (絶対パス確定により不要)、必要時は個人選択 |
| `DOCKER_CONFIG` の export を `.zshrc` に書くか、wrapper のみで完結か | wrapper のみで完結 (Claude Code 起動時の自動 export は副作用大) |
| private registry / `docker login` の取扱い | 本 PR scope 外、必要時は `~/.docker-claude/` 側で `./scripts/docker-claude.sh login` (security note は runbook で明示) |
| `scripts/docker-claude.sh` の shellcheck CI 統合 | 本 PR-B では `bash -n` syntax check のみ AC-7、shellcheck CI 統合は A6 step6 (trufflehog 並列) で扱う、`.claude/rules/docker-cli.md` §機械検証 placeholder に明示 |

### 本 PR-A merge 後の harness 連携

`implementation-workflow.md` Phase 8 拡張 (PR #184) により、本 PR-A merge 後の Phase 8 で
本 Plan の frontmatter (`related_pr` / `status: in-progress` 等) が自動同期される想定。
後続 PR-B merge 時には Plan `status: completed` + `completed_at: <merge 日>` が同期される。

`roadmap-tracker` は **R-34 (Plan は対象外)** により本 Plan を取り込まない。Plan ⇄ Epic ⇄ ADR の
責務分離 (`.claude/rules/plan.md` §Plan ⇄ Epic ⇄ ADR の責務分離 参照) に従い、Plan ロードマップ
ミラーは更新されない。

### 関連参照

- `docs/runbooks/local-imasparql.md` (本 Plan で更新対象、PR-B AC-4)
- `docs/runbooks/local-development.md` (Docker Desktop セットアップ要件、補助)
- `.claude/rules/cloud-run-deploy.md` (将来の Dockerfile 規約、本 PR では touch せず gradual rollout 対象)
- PR #175 (A8 Docker Fuseki 配置、本問題の直接トリガ)
- PR #186 (Step B 計画立案、本 Plan の直接前段)
- PR #184 (`implementation-workflow` Phase 8 拡張、本 PR merge 後の frontmatter 同期と連携)
- Task #11 (Step A、本 Plan 配下 PR-B で blocker 解消)
- ADR-0014 (`harness/plan.md` 設計原則、本 Plan の harness 改修対応)
- ADR-0021 (Secrets 管理ポリシー、private registry login 取扱いで参照)
