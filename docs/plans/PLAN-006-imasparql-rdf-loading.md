---
id: PLAN-006
title: im@sparql RDF データ取得と Fuseki への load 計画立案
type: feature-request
status: proposed
related_pr: null
related_epic: null
related_specs:
  - SPEC-IMASPARQL-002-basic
related_adrs:
  - ADR-0007
  - ADR-0014
  - ADR-0021
expected_modules:
  - docs/requirements/REQ-002-imasparql-rdf-loading.md
  - docs/specifications/basic/SPEC-IMASPARQL-002-basic.md
  - docs/plans/PLAN-006-imasparql-rdf-loading.md
  - docs/requirements/INDEX.md
  - docs/plans/INDEX.md
created_at: 2026-05-19
completed_at: null
promoted_to: null
---

# im@sparql RDF データ取得と Fuseki への load 計画立案

> **5 行以内 summary**: REQ-002 / SPEC-IMASPARQL-002-basic を起票し、im@sparql RDF データ
> 取得 → Fuseki load → SPARQL クエリ動作確認の一連プロセスを計画立案する単一 PR。
> 実体実装 (スクリプト / runbook 拡充 / Kotlin code touch) は **本 PR scope 外** で、
> 後続 Plan-A / Plan-B / Plan-C に分離。新規 5 ファイル (REQ + SPEC + 本 Plan + INDEX 2 件)、
> 既存 docs touch ゼロで 1 PR 完結を保ち、ロールバックは `git revert` で完全復元可能。

## 目的

REQ-001 / SPEC-IMASPARQL-001-basic / PLAN-003 (PR #175) で Docker Fuseki 構成は配置済だが
dataset placeholder のため、実 RDF データ取得 / load / 動作確認の一連プロセスを **計画立案** で
固める。本 PR は REQ-002 + SPEC-IMASPARQL-002-basic + 本 Plan の 3 docs を起票し、後続実装は
別 Plan に分離する。

## 背景

PR #175 (A8、merge commit `7add15b`) で配置済の構成:

- `docker-compose.yml` (Fuseki container `stain/jena-fuseki:4.10.0`、port `127.0.0.1:3030` 限定 bind)
- `data/imasparql/.gitkeep` + `data/imasparql/README.md` (RDF 取得手順 + ライセンス注意の placeholder)
- `docs/runbooks/local-imasparql.md` (§1-§10 で起動 / 接続確認 / 停止 / トラブルシュート)
- REQ-001 + SPEC-IMASPARQL-001-basic + PLAN-003

未確立 (本 Plan で計画立案するべき領域):

1. **実 RDF データ取得手段**: upstream URL + ライセンス遵守 + 取得スクリプト (`scripts/fetch-imasparql-data.sh`)
2. **Fuseki への data load 方式**: bind mount 自動 load (推奨) / Admin API 経由 upload (補足) / TDB2 変換 (将来)
3. **接続テスト + SPARQL query 動作確認**: 検証スクリプト (`scripts/test-imasparql-query.sh`) と `ASK` / `SELECT` 最小クエリ

本 Plan は **計画立案のみ** (§4.6 コード禁止原則)、実体スクリプト / runbook 拡充 / Kotlin code touch は
後続 Plan に委ねる。Step A (実環境 `docker compose up -d` 動作確認) は orchestrator pane 側で
Docker Desktop credential helper hang 復旧後に並行進行、Step B (本 Plan) は Step A 結果に
関わらず先行して計画立案する。

## アプローチ

1. **REQ-002 起票**: `docs/requirements/REQ-002-imasparql-rdf-loading.md` に WHY / WHAT (FR-1〜FR-7、UC-1〜UC-4、NFR) を記述、`docs/requirements/INDEX.md` に行追加
2. **SPEC-IMASPARQL-002-basic 起票**: `docs/specifications/basic/SPEC-IMASPARQL-002-basic.md` にデータフロー (Mermaid)、取得スクリプト / 検証スクリプトの **識別子レベル定義** (§7.1 / §7.2)、Fuseki load 方式選定 (§7.3、bind mount 自動 load 推奨)、動作確認クエリ (`ASK` + `SELECT` LIMIT 5、`.claude/rules/sparql.md` prefix 整合) を記述
3. **本 Plan 起票**: `docs/plans/PLAN-006-imasparql-rdf-loading.md` (本ファイル) を新規作成、`docs/plans/INDEX.md` に行追加 (`plan.md` §採番ライフサイクル §1 予約 → §2 起票 SoT 準拠、PLAN-005 の次 = PLAN-006)
4. **詳細設計 SPEC-IMASPARQL-002-detail は本 Plan scope 外**: 本 PR は docs 起票のみ / Kotlin module touch ゼロのため対象なし。後続 Plan-A / Plan-B 完走後または Phase C5 着手時に起票
5. **既存 docker-compose.yml / data/imasparql/README.md / docs/runbooks/local-imasparql.md / backend/** は touch しない**: 本 Plan scope 外、後続 Plan-A / Plan-B が `docs/runbooks/local-imasparql.md` に § RDF データ取得 / § 動作確認 セクションを追加する
6. **commit 分離**: `implementation-workflow.md` §commit 分離規範 + PR #174 §commit 分離規範 準拠で 3 commit (REQ / SPEC / Plan)

## 受け入れ基準

- [ ] AC-1: `docs/requirements/REQ-002-imasparql-rdf-loading.md` が 11 セクション (概要 / アクター / スコープ / UC / FR / NFR / 制約 / 用語 / トレーサビリティ / AC / Open Questions) で起票される
- [ ] AC-2: `docs/specifications/basic/SPEC-IMASPARQL-002-basic.md` が 11 セクションで起票され、upstream URL 候補 + 取得スクリプト / 検証スクリプト識別子レベル定義 + Fuseki load 方式選定 (bind mount 推奨) を含む
- [ ] AC-3: `docs/plans/PLAN-006-imasparql-rdf-loading.md` (本ファイル) が起票され、後続 Plan-A / Plan-B / Plan-C を placeholder で列挙する
- [ ] AC-4: `docs/requirements/INDEX.md` に REQ-002 行が追加され、`docs/plans/INDEX.md` に PLAN-006 行が追加される
- [ ] AC-5: ライセンス CC BY-NC-SA 4.0 が REQ-002 / SPEC-IMASPARQL-002-basic の双方で明示される (`grep -c "CC BY-NC-SA"` で REQ + SPEC 合算 4 以上)
- [ ] AC-6: 設計書本文 (`docs/requirements/REQ-002-*.md` / `docs/specifications/basic/SPEC-IMASPARQL-002-basic.md`) にコード断片が含まれない (§4.6.1、Mermaid と表のみ)
- [ ] AC-7: 本 PR では `docker-compose.yml` / `data/imasparql/README.md` / `docs/runbooks/local-imasparql.md` / `backend/**` を一切 touch しない (`git diff` で確認)
- [ ] AC-8: REQ-002 / SPEC-IMASPARQL-002-basic / PLAN-006 の frontmatter `related_*` が **block 形式** で記述され、双方向リンクが整合する (`docs-structure.md` §frontmatter 規約)

## スコープ外

- 取得スクリプト (`scripts/fetch-imasparql-data.sh`) の実体実装 → **後続 Plan-A**
- 検証スクリプト (`scripts/test-imasparql-query.sh`) の実体実装 → **後続 Plan-B**
- `docs/runbooks/local-imasparql.md` の § RDF データ取得 / § 動作確認 セクション追加 → **後続 Plan-A / Plan-B**
- Backend / CLI `ImasparqlApiClient` の endpoint 切替実装 (`IMASPARQL_ENDPOINT_URL`) → **後続 Plan-C**
- Testcontainers 統合 / CI での Fuseki 自動起動 → Phase B-C で本格化、別 Plan
- 公開 endpoint と Fuseki の自動 fallback ロジック
- 詳細設計 `SPEC-IMASPARQL-002-detail` の起票 → 後続 Plan-A / Plan-B 完走後 or Phase C5
- A8 PR #175 で残された Improvement (SPEC-001 `related_detail: []` の更新、Testcontainers / endpoint 切替の後続 Plan backlink、`.dockerignore` 配置 TODO、`docker-compose.yml` image tag Renovate 対応注記) → 本 Plan は計画立案のみのため touch しない、後続 Plan / A6 / C7 で扱う

## ロールバック手順

外部依存ゼロ。本 PR は docs 5 ファイル追加のみ (REQ + SPEC + Plan + INDEX 2 件)。

1. `git revert <pr-merge-commit>` で本 PR の commit 群を打ち消し
2. `docs/requirements/REQ-002-*.md` / `docs/specifications/basic/SPEC-IMASPARQL-002-basic.md` / `docs/plans/PLAN-006-*.md` が削除される
3. `docs/requirements/INDEX.md` / `docs/plans/INDEX.md` の REQ-002 / PLAN-006 行が removed
4. backend / runbook / docker-compose / data/imasparql/ は本 PR で touch していないため復元対象なし
5. revert 後、後続 Plan-A / Plan-B / Plan-C は再起票が必要 (placeholder 参照が失われるため)

## 後続 Plan placeholder

本 Plan の `completed` 後に起票する後続 Plan を以下に列挙する (各 Plan の expected_modules
は non-overlap、独立 PR で merge 可能、`plan.md` §採番ライフサイクル §1 予約に従って起票時に
連番採番)。

### 後続 Plan-A: RDF データ取得スクリプト実装 + runbook § RDF データ取得 セクション追加

- expected_modules: `scripts/fetch-imasparql-data.sh` / `docs/runbooks/local-imasparql.md` (§ RDF データ取得 追記)
- 主タスク: upstream URL の確定 (SPEC-002-basic §7 候補 1 を Phase 1 で実走、配布元廃止時は候補 2 にフォールバック) / retry / timeout / ライセンス確認対話 / 動作確認結果 (RDF サイズ / 取得時間) の SPEC NFR 表反映
- AC 例: スクリプトを `scripts/fetch-imasparql-data.sh` として実装、`data/imasparql/imasparql.ttl` が配置される、runbook §5 dataset 投入手順を § RDF データ取得 / § dataset 投入 に分割 / 拡充
- 起票判定: 本 Plan merge 後、SPEC-002-basic §11 Open Question (upstream URL 確定 / bind mount 自動 load 設定) の resolve タイミングで起票

### 後続 Plan-B: 検証スクリプト実装 + runbook § 動作確認 セクション追加 + Step A 動作確認結果

- expected_modules: `scripts/test-imasparql-query.sh` / `docs/runbooks/local-imasparql.md` (§ 動作確認 追記)
- 主タスク: `ASK` / `SELECT` LIMIT 5 クエリ実装 (`.claude/rules/sparql.md` prefix 整合) / Fuseki dataset 件数検証 / Step A (orchestrator pane で実環境 `docker compose up -d` 動作確認) の結果反映
- AC 例: スクリプトを `scripts/test-imasparql-query.sh` として実装、`docker compose up -d fuseki` + bind mount load + 検証スクリプト exit 0 までを runbook §動作確認 で記述
- 起票判定: 後続 Plan-A merge 後、Step A 完走 (Docker Desktop credential helper 復旧後の `docker compose up -d` 成功) のタイミング

### 後続 Plan-C: Backend Kotlin module への接続実装 (`ImasparqlApiClient` endpoint 切替)

- expected_modules: `backend/cli/src/main/kotlin/net/subroh0508/colormaster/backend/cli/imasparql/Constants.kt` / `gradle/libs.versions.toml` (環境変数化向け) / 関連テスト
- 主タスク: hardcode された `HOSTNAME = "sparql.crssnky.xyz"` を `IMASPARQL_ENDPOINT_URL` 環境変数化、ローカル Fuseki と公開 endpoint の切替実装
- AC 例: integration test で Fuseki / 公開 endpoint を切替可能、Testcontainers 統合の足掛かりを作る
- 起票判定: Phase C5 (Litestream 本格運用) と合わせて Epic 化判定、touch ファイル数 > 30 / 仕様波及が複数 SPEC に及ぶ場合は `plan.md` §Epic 昇格条件 4 行表に従って Epic 化

## メモ

- 本 Plan は **計画立案のみ** (§4.6 コード禁止原則、`feature-request` Skill SoT 準拠): 設計書本文 (REQ-002 / SPEC-IMASPARQL-002-basic) にコード断片を一切書かない、Mermaid と表で表現。`file_path:line` 参照のみ許容
- 既存 5 docs (REQ-001 / SPEC-IMASPARQL-001-basic / PLAN-003 / `data/imasparql/README.md` / `docs/runbooks/local-imasparql.md`) は本 PR で **touch しない**: 後続 Plan-A / Plan-B が runbook を拡充する責務を負う
- A8 PR #175 で残された Improvement (SPEC-001 `related_detail: []` の更新、Testcontainers / endpoint 切替の後続 Plan backlink、`.dockerignore` 配置 TODO、`docker-compose.yml` image tag Renovate 対応注記) は本 Plan の SoT 外 (`feature-request` Skill SoT 準拠の計画立案のみ)、後続 Plan-A / A6 (Lint / Format 本格化) / C7 (Cloud Run デプロイ) で扱う
- `docs/requirements/INDEX.md` / `docs/plans/INDEX.md` は本 PR で 1 行ずつ追記 (placeholder 予約 → 本体起票を 1 PR 内で完結、`plan.md` §採番ライフサイクル §2 起票)
- 詳細設計 `SPEC-IMASPARQL-002-detail` を本 PR で起票しない判断: SPEC-IMASPARQL-001-basic §9 と同じ基準 (本 PR は docs 起票のみ / Kotlin module touch ゼロ / モジュール責務 / 状態遷移 / 例外パターンを Kotlin 視点で記述する対象が存在しないため)
- ライセンス遵守: CC BY-NC-SA 4.0 を REQ / SPEC で明示、`data/imasparql/` の git 追跡除外維持を確認 (REQ-001 / PLAN-003 で配置済の `.gitignore` を本 PR で touch しない)
- code-reviewer 重点 aspect: spec-conformance (§4.6 コード禁止原則遵守 / REQ ⇄ SPEC ⇄ Plan 双方向リンク整合) / architecture (Fuseki load 方式選定の SoT 整合) / security (ライセンス遵守記述 / `data/imasparql/` git 追跡除外 / credentials 漏洩なし) / code-quality (5 行 summary / 日本語見出し / Mermaid / 関連リンク)
