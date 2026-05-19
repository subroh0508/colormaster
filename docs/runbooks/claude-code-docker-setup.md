---
id: runbook-claude-code-docker-setup
title: Claude Code Docker CLI 分離 setup runbook
status: stable
last_updated: 2026-05-20
related_plan: docs/plans/PLAN-007-claude-code-docker-cli-isolation.md
related_rules:
  - .claude/rules/docker-cli.md
related_adrs:
  - ADR-0014
  - ADR-0021
related_specs: []
---

# Claude Code Docker CLI 分離 setup runbook

> **5 行以内 summary**: macOS Docker Desktop `credsStore: "desktop"` の Claude Code 非対話的 shell
> hang を回避する setup 手順 (PLAN-007)。ホスト側 `~/.docker-claude/config.json` の 1 回限り作成
> + repo 内 wrapper (`scripts/docker-claude.sh`) の **絶対パス強制** で呼出 (`./scripts/docker-claude.sh ...`)。
> 通常ターミナル / 人間手元実行は影響なし (`credsStore: "desktop"` 温存)。`~/.docker-claude/config.json`
> は git 管理外、private registry 利用時は §7 security note 参照。

## 1. 前提

| 項目 | 要求 | 確認コマンド |
|---|---|---|
| OS | macOS (Apple Silicon / Intel) | `uname -sm` |
| Docker Desktop | インストール済 + 起動済 | `docker info` (通常ターミナルで応答すれば OK) |
| Claude Code | v2.x (本リポジトリで利用) | `claude --version` (該当時) |
| repo clone | `colormaster` (本 repo) を clone 済 | `git rev-parse --show-toplevel` |
| `scripts/docker-claude.sh` | 本 PR-B merge 後の master に含まれる | `ls -l scripts/docker-claude.sh` で executable bit (`-rwxr-xr-x`) 確認 |

## 2. ホスト側 setup (1 回限り)

ホスト側 `~/.docker-claude/config.json` を 1 回限り作成する。`credsStore` を持たない
最小 config を配置することで credential helper を bypass する。

```bash
# 1. ディレクトリ作成 (既存なら no-op)
mkdir -p "$HOME/.docker-claude"

# 2. 最小 config.json 配置 (credsStore なし、auths 空)
cat > "$HOME/.docker-claude/config.json" <<'EOF'
{
  "auths": {}
}
EOF

# 3. 配置確認
cat "$HOME/.docker-claude/config.json"
ls -la "$HOME/.docker-claude/"
```

- **`~/.docker/config.json` (Docker Desktop の本来 config) には触らない** (`credsStore: "desktop"` 温存、通常ターミナル / Docker Desktop UI への影響ゼロ)
- **`~/.docker-claude/config.json` は git 管理外**: 本 runbook の手順は repo clone 後に各ホストで個別実行する初期 setup、commit / push 対象ではない
- **PC 復旧後 / 新マシン setup 時は本 §2 を毎回実施**: 自動化スクリプトの起票は本 PR scope 外 (将来別 PR で検討)

## 3. wrapper 利用方法

Claude Code セッションから Docker を実行する場合は repo 内 wrapper を **絶対パス** で叩く:

```bash
# repo root にいる前提
./scripts/docker-claude.sh ps
./scripts/docker-claude.sh pull stain/jena-fuseki:4.10.0
./scripts/docker-claude.sh compose up -d fuseki
./scripts/docker-claude.sh compose down
./scripts/docker-claude.sh compose logs fuseki
```

- **絶対パス強制** (`./scripts/docker-claude.sh ...` を repo root から実行): `.claude/rules/docker-cli.md` §運用 参照
- **PATH 通し / direnv / alias は本 runbook では言及しない**: 必要時は個人選択 / 別 PR で検討 (絶対パス確定で運用上は不要)
- **wrapper の中身は `DOCKER_CONFIG=$HOME/.docker-claude exec docker "$@"`**: 引数を完全 pass-through する単純な薄い shim
- **通常ターミナル / 人間手元実行は通常 `docker`**: wrapper を介さず Docker Desktop の Keychain integration を継続利用可

## 4. Claude Code への指示文例

Claude Code への prompt / Skill / rule 内記述で Docker 実行を指示する場合は、以下のような表現で
`./scripts/docker-claude.sh` を SoT 化する:

```text
Docker は ./scripts/docker-claude.sh で叩いてください (絶対パス)。
docker 直叩きは macOS Docker Desktop credsStore: "desktop" の hang を引き起こすため禁止。
詳細: .claude/rules/docker-cli.md
```

- **prompt 中で「`docker compose up -d`」と書く場合も「`./scripts/docker-claude.sh compose up -d`」に置換**
- **rule / Skill / SKILL.md 内で Docker 例示を書く場合**: 「Claude Code 経由実行時は `./scripts/docker-claude.sh` を使う」注記を入れる (`local-imasparql.md` §3 / §6 / §7 の注記が参考)
- **runbook 内の例示**: 人間手元実行も対象とする runbook では `docker compose` 直叩きの例示を残し、注記で「Claude Code 経由のみ wrapper」を明示 (`local-imasparql.md` の運用方針と整合)

## 5. 切り分けコマンド

wrapper が期待通り動作しているかの手動検証手順:

```bash
# 1. ~/.docker/config.json (Docker Desktop 本来 config) の credsStore を確認
cat "$HOME/.docker/config.json"
# 期待: "credsStore": "desktop" が設定されている (Docker Desktop 既定)

# 2. ~/.docker-claude/config.json (wrapper 用 config) を確認
cat "$HOME/.docker-claude/config.json"
# 期待: { "auths": {} } のみで credsStore は含まれない

# 3. wrapper を介して docker ps を実行 (Keychain prompt が出ないこと)
./scripts/docker-claude.sh ps
# 期待: 通常 ps 出力、hang なし

# 4. 手動で DOCKER_CONFIG を export して動作確認
DOCKER_CONFIG="$HOME/.docker-claude" docker ps
# 期待: wrapper と同様に hang なしで応答

# 5. wrapper の syntax check (CI 統合は A6 で予定、現状は手動)
bash -n scripts/docker-claude.sh
```

- **`docker info` / `docker images` が応答する** = Docker daemon 自体は健全 (Step A 観測済)
- **`docker pull` / `docker compose up -d` が 0% CPU で hang する** = credential helper 経由の Keychain prompt が原因 (本 setup の対象症状)

## 6. トラブルシュート

| 症状 | 原因 | 対処 |
|---|---|---|
| `./scripts/docker-claude.sh: No such file or directory` | repo clone 直後 / cwd が repo root 外 | `git rev-parse --show-toplevel` で repo root に `cd`、`ls -l scripts/docker-claude.sh` で executable bit 確認 |
| `./scripts/docker-claude.sh: Permission denied` | `chmod +x` が外れている (commit 時の executable bit 漏れ等) | `chmod +x scripts/docker-claude.sh` で再付与、本 PR-B では `git update-index --chmod=+x` 不要 (commit 時に bit 込みで `git add`) |
| wrapper 経由でも `docker pull` が hang | `~/.docker-claude/config.json` 未作成 / `credsStore` が混入 | 本 runbook §2 を再実施、`cat ~/.docker-claude/config.json` で `credsStore` キーが含まれないことを確認 |
| `DOCKER_CONFIG` が読まれない (wrapper 介しても通常 `~/.docker/` が参照される) | shell 環境で `DOCKER_CONFIG` が unset 以外の値で上書きされている | `unset DOCKER_CONFIG` 後に wrapper を再実行、または `DOCKER_CONFIG="$HOME/.docker-claude" docker ps` で手動指定して切り分け |
| private registry に `docker login` が必要 | `~/.docker-claude/` 側に auth が保存される (§7 参照) | `./scripts/docker-claude.sh login <registry>` で実行、`~/.docker-claude/config.json` の `auths` セクションに credentials が書き込まれる |
| 通常ターミナルでの `docker` が遅い / hang | wrapper とは別の Docker Desktop 側既存症状 | 本 runbook の対象外。Docker Desktop 再起動 / `docker logout` 等の標準対応を参照 |

## 7. private registry 取扱い security note

`./scripts/docker-claude.sh login <registry>` を実行すると `~/.docker-claude/config.json` の
`auths` セクションに credentials が書き込まれる (`credsStore` を持たないため Keychain には
保存されず、**plaintext base64 encoded** で書き込まれる)。

| 項目 | 取扱い |
|---|---|
| 保存先 | `~/.docker-claude/config.json` (`auths.<registry>.auth` フィールド、base64 encoded plaintext) |
| Keychain との関係 | wrapper 経由は Keychain を使わない (本 setup の目的)、通常ターミナルの `docker login` のみ Keychain 保存 |
| git 管理 | `~/.docker-claude/` 配下は **絶対に commit しない** (`.gitignore` 対象外だが、本 runbook の手順で repo 外に配置しているため commit リスクは構造的に低い) |
| ローテーション | `secrets.md` §ローテーション 参照、Cloudflare / GitHub PAT / GCP service account 等の token は 90 日 / 180 日周期、private registry token も同等の運用を推奨 |
| 漏洩検出 | `~/.docker-claude/config.json` を誤って repo に commit した場合は即時 `git filter-repo` 等で history rewrite + token ローテーション (`secrets.md` §漏洩検出 参照) |
| Claude Code への共有 | `~/.docker-claude/config.json` の `auths` セクションを Claude Code prompt / Skill 出力に含めない (`pii.md` / `secrets.md` redaction 強制と整合、`auth` フィールド値は `[REDACTED-SECRET]` に置換) |

- **`.docker/credentials` や `.docker-claude/credentials` 等の追加 credential ファイルは現状作成しない**: wrapper は `config.json` の `auths` のみ参照
- **本 setup で `credsStore` を意図的に外している**: Keychain integration を諦める代償として hang 回避を実現、機密度が高い private registry を頻繁に使う場合は通常ターミナル + Docker Desktop の Keychain integration を併用する運用も可 (Claude Code 経由のみ wrapper)

## 8. 関連

- `.claude/rules/docker-cli.md` (本 runbook の repo 側 SoT、wrapper 利用規約)
- `docs/runbooks/local-imasparql.md` (Fuseki Docker 運用、Claude Code 経由実行時の wrapper 注記)
- `docs/plans/PLAN-007-claude-code-docker-cli-isolation.md` (本 runbook の起票 Plan、AC-3)
- ADR-0014 (Fuseki Docker 採用、`docker-compose.yml` 配置根拠)
- ADR-0021 (Secrets 管理ポリシー、private registry token ローテーション)
- `.claude/rules/secrets.md` §ローテーション / §漏洩検出 (private registry token 取扱い)
- `.claude/rules/pii.md` §redaction (Skill 出力前の `auths` 漏洩防止)
- `scripts/docker-claude.sh` (wrapper 実体、`chmod +x` 済)
