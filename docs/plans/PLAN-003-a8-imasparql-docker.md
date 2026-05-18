---
id: PLAN-003
title: A8 im@sparql ローカル Docker 環境構築 (Fuseki container)
type: feature-request
status: in-progress
related_pr: null
related_epic: null
related_specs:
  - SPEC-IMASPARQL-001-basic
related_adrs:
  - ADR-0007
  - ADR-0014
  - ADR-0021
expected_modules:
  - docker-compose.yml
  - docs/runbooks/local-imasparql.md
  - data/imasparql/
  - .gitignore
  - .env.example
created_at: 2026-05-19
completed_at: null
promoted_to: null
---

# A8 im@sparql ローカル Docker 環境構築 (Fuseki container)

> **5 行以内 summary**: REQ-001 / SPEC-IMASPARQL-001-basic を実装する単一 PR。
> Apache Jena Fuseki container を `docker-compose.yml` で起動可能にし、RDF データディレクトリ
> placeholder と運用 runbook を整備する。Backend Kotlin module は touch せず、Backend
> endpoint 切替 / Testcontainers 統合 / RDF seed 自動取得は A8 後続 Plan に分離して
> スコープを 1 PR 完結に保つ。

## 目的

ADR-0014 を実体化する Phase A8 の最小 PR として、im@sparql 公開 endpoint 非依存で
SPARQL クエリを試行・テストできるローカル環境を構築する。本 PR は infra (compose / runbook)
のみで完結し、Kotlin module には触れない。

## 背景

`backend/cli/src/main/kotlin/net/subroh0508/colormaster/backend/cli/imasparql/Constants.kt:3`
で `HOSTNAME = "sparql.crssnky.xyz"` が hardcode されており、公開 endpoint への依存が
固定化されている。ADR-0014 が公開 endpoint からの切り離しを意思決定済 (2026-05-17)、
roadmap A8 として `docker-compose.yml` + `docs/runbooks/local-imasparql.md` + `backend/**`
の Fuseki ローカル環境を予定 (`docs/harness/plan.md:1540`, `docs/harness/roadmap.md` A8 行)。

本 PR では `backend/**` を意図的に touch しない。endpoint 切替 / Testcontainers 統合 /
seed スクリプト自動化は別 Plan に切り出し、本 PR を「ゼロから 5 分で Fuseki が起動できる」
状態に絞り込む。

## アプローチ

1. **要件 / 基本設計起票** (本 PR 前提): `docs/requirements/REQ-001-imasparql-local-docker.md` +
   `docs/specifications/basic/SPEC-IMASPARQL-001-basic.md` + `docs/requirements/INDEX.md`
   を新規配置。詳細設計は本 PR では起票しない (Kotlin module touch ゼロのため対象なし、
   §「単純な docker 環境構築は基本設計まで」判断)。
2. **docker-compose.yml 起草**: project root に Fuseki service 定義。image は
   `stain/jena-fuseki` 系 (Open Question で最終確定)、port `3030:3030`、`data/imasparql/`
   を read-only volume mount、`FUSEKI_ADMIN_PASSWORD` を環境変数経由で受領。
3. **.env.example 起草**: `FUSEKI_ADMIN_PASSWORD=changeme` 等 placeholder を配置。
   実値は `.gitignore` 対象の `.env` に開発者ごとに記録する規約。
4. **data/imasparql/ placeholder 配置**: `.gitkeep` + `README.md` (RDF データ取得の手引き)。
5. **`.gitignore` 拡張**: `data/imasparql/*.ttl` / `*.nq` / `*.rdf` / `*.nt` を除外、
   `!data/imasparql/.gitkeep` / `!data/imasparql/README.md` を whitelist として保持。
6. **runbook 起草**: `docs/runbooks/local-imasparql.md` に「前提 / 初回セットアップ /
   起動 / 接続確認 / dataset 投入 / 停止 / トラブルシュート / 関連リンク」の節を整備。
7. **local-development.md 参照更新**: §6 の「現時点では未整備」を「`docs/runbooks/local-imasparql.md` 参照」に置換。
8. **動作確認**: `docker-compose config 2>&1` で YAML syntax 検証 (実 `docker compose up`
   は CI 環境制約 / 実体取得 RDF 未配置のため skip、PR description に注記)。

## 受け入れ基準

- [ ] AC-1: `docker-compose config` が syntax error なしで成功する
- [ ] AC-2: `docker-compose.yml` に admin password / 認証情報の hardcode が含まれない
      (`grep -iE "password.*=.*[^$]" docker-compose.yml` で実値検出なし)
- [ ] AC-3: `data/imasparql/.gitkeep` と `data/imasparql/README.md` が配置され、git で追跡される
- [ ] AC-4: `.gitignore` に `data/imasparql/*.ttl` / `*.nq` / `*.rdf` / `*.nt` の除外パターンが追加され、
      `.gitkeep` / `README.md` の whitelist が live
- [ ] AC-5: `docs/runbooks/local-imasparql.md` が「前提 / 起動 / 接続確認 / dataset 投入 / 停止 / トラブルシュート」の節を備える
- [ ] AC-6: `backend/**` 配下の既存ファイルが一切 touch されない (`git diff backend/` が空)
- [ ] AC-7: `docs/runbooks/local-development.md` §6 から `docs/runbooks/local-imasparql.md` への参照リンクが live link になる
- [ ] AC-8: REQ-001 / SPEC-IMASPARQL-001-basic / PLAN-003 が双方向リンクで整合し、frontmatter `related_*` が block 形式で記述される

## スコープ外

- Backend / CLI `ImasparqlApiClient` の endpoint 切替実装 (`IMASPARQL_ENDPOINT_URL`)
- Testcontainers 統合 / Backend integration test の Fuseki 接続実装
- RDF 初期データ取得スクリプト (`scripts/seed-imasparql.sh`)、ライセンス確認 (`docs/harness/plan.md` R-9)
- CI 上での Fuseki 自動起動
- 公開 endpoint と Fuseki の自動 fallback ロジック
- 上記はすべて A8 後続 Plan に分離する (本 PR は 1 PR 完結維持のため範囲を明確化)

## ロールバック手順

外部依存は Docker image (`stain/jena-fuseki`) のみ。Image 取得は PR merge 後の
開発者ローカル操作のため、リポジトリ側は `git revert <merge-commit>` で完全復元可能。
復元後の確認:

1. `git revert <pr-merge-commit>` で本 PR の commit 群を打ち消し
2. `docker compose config 2>&1` を実行すると `docker-compose.yml` が無いため "no configuration file" となる
3. `data/imasparql/` ディレクトリ削除、`.gitignore` 該当行削除、runbook 削除を merge commit が含むため自動復元
4. 開発者ローカルの `.env` (個人作成) は git 管理外、必要に応じて手動削除
5. `docs/runbooks/local-development.md` §6 が「未整備」表記に戻ることを確認

## メモ

- 詳細設計 `SPEC-IMASPARQL-001-detail` を本 PR で起票しない判断: 本 PR は Kotlin module
  touch ゼロ / docker-compose + runbook のみで完結し、モジュール責務 / 状態遷移 / 例外
  パターンを Kotlin 視点で記述する対象が存在しないため (SPEC-IMASPARQL-001-basic §9 参照)。
  Phase B-C で Backend integration test + Testcontainers + endpoint 切替を本格化する
  別 Plan / SPEC で詳細設計を起こす方針。
- `docs/requirements/INDEX.md` を本 PR で新規作成 (旧来 README.md のみで運用、初回 REQ
  起票につき索引が必要、`.claude/skills/feature-request/SKILL.md` §Gotchas 末尾の
  「初回起動時に作成」規約に従う)。
- `.gitignore` の既存 `*.db` 規約 (`db-protection.md` 由来) と衝突しないことを確認:
  Fuseki TDB2 永続化は本 PR ではオプション扱い (default in-memory)、TDB2 を有効化する
  場合の data volume は `data/imasparql/tdb2/` 配下で完結し `*.db` 衝突なし。
- Renovate 連携: `docker-compose.yml` の image tag を明示固定 (`latest` 不使用) し、
  将来 Renovate `docker` manager で自動 PR 対象とできる形にしておく。
- code-reviewer security aspect: admin password hardcode 検出 / port localhost 限定の
  2 点を重点レビュー対象として明記。
