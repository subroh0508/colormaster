---
id: PLAN-001
title: ADR 0001-0027 一括起票
type: chore
status: completed
related_pr: 119
related_epic: EPIC-000
related_specs: []
related_adrs:
  - ADR-0001
  - ADR-0002
  - ADR-0003
  - ADR-0004
  - ADR-0005
  - ADR-0006
  - ADR-0007
  - ADR-0008
  - ADR-0009
  - ADR-0010
  - ADR-0011
  - ADR-0012
  - ADR-0013
  - ADR-0014
  - ADR-0015
  - ADR-0016
  - ADR-0017
  - ADR-0018
  - ADR-0019
  - ADR-0020
  - ADR-0021
  - ADR-0022
  - ADR-0023
  - ADR-0024
  - ADR-0025
  - ADR-0026
  - ADR-0027
expected_modules:
  - docs/adr/**
  - docs/plans/**
  - docs/harness/roadmap.md
created_at: 2026-05-17
completed_at: 2026-05-17
promoted_to: null
---

# ADR 0001-0027 一括起票

> **5 行以内 summary**: B0 で merge 済み `docs/harness/plan.md` (commit 0256be9) に
> 記載済みの 27 件のアーキテクチャ決定を `docs/adr/ADR-NNNN-<kebab>.md` として
> 物理化する。全 ADR を `accepted` で起票し、`docs/adr/README.md` の状態列と
> `docs/harness/roadmap.md` の A1 行ステータスも更新する。Phase A の起点。
> 影響範囲: `docs/adr/**`、`docs/plans/**`、`docs/harness/roadmap.md`。

## 目的

- `docs/harness/plan.md` §6.2 A1 を満たす。
- 既存 plan.md に記載済みのアーキテクチャ決定 (Compose Multiplatform / Backend SQLite / GIS 認証 /
  ハーネスループ / MCP / UI 凍結 / docs 構造 等) を ADR 形式で参照可能にし、
  以降の Plan / Epic / Skill から `related_adrs` を介して機械的に追跡できるようにする。

## 背景

- `.claude/rules/adr.md` および `docs/adr/README.md` に「A1 で一括起草」と明記済み。
- B0 PR (#117、commit 0256be9) で `docs/adr/{README,template}.md` を配置済みだが、
  ADR 本体ファイルは未作成。
- 27 件の決定は plan.md §3 (設計指針) / §4 (docs 構造) / §5 (ハーネス構造) に集約されており、
  ADR 本体は要約 + Single Source of Truth (plan.md 章) への参照で構成する。

## アプローチ

1. ADR 0001 (運用基準) と ADR 0027 (docs 構造) を先行起草し、他 ADR が参照する
   メタ ADR を確定。
2. グループ別に並列起草:
   - G2 アプリ設計 (0002 / 0003 / 0004 / 0005 / 0006 / 0012)
   - G3 データ・認証・同期 (0007 / 0008 / 0010 / 0011 / 0014)
   - G4 ホスティング (0009 / 0022)
   - G5 テスト品質 (0013 / 0015 / 0016)
   - G6 セキュリティ (0020 / 0021)
   - G7 ハーネス中核 (0017 / 0018 / 0019 / 0024 / 0025 / 0026)
   - G8 UI/UX (0023)
3. 各 ADR は `docs/adr/template.md` 準拠、ステータスは `accepted`、起票日 2026-05-17、
   5 行 summary + コンテキスト + 決定 + 根拠 + 帰結 + 起票基準充足チェックを含む。
4. クロスリファレンス整合: `related_adrs` / `related_specs` / `related_epics` /
   `related_plans` の参照先実在を手動で確認。
5. 副次更新:
   - `docs/adr/README.md` の状態列を「既存 / ★統合」→「★起草済 (accepted)」に更新。
   - `docs/harness/roadmap.md` の A1 行 status を `proposed` → `in-progress`。
   - `docs/plans/INDEX.md` に PLAN-001 行を追加。
6. Self-Verification: frontmatter 必須キー / 日本語見出し / 5 行 summary 存在 /
   コード断片不在 / 識別子参照実在を全 ADR で確認。
7. `feature/A1-adr-bootstrap` ブランチで commit、`gh pr create --draft
   --template docs.md` で Draft PR 起票、`code-reviewer` 起動 (Phase 6)、
   人間 approve 後に squash merge (Phase 7)。

## 受け入れ基準

- [ ] `docs/adr/ADR-0001-*.md` から `docs/adr/ADR-0027-*.md` まで 27 ファイルが存在する。
- [ ] 全 ADR の frontmatter が `.claude/rules/docs-structure.md` の必須キーを満たす
      (`id` / `title` / `status` / `date` / `related_epics` / `related_plans` /
      `related_specs` / `superseded_by` / `supersedes`)。
- [ ] 全 ADR が冒頭 5 行以内の summary を持つ (ADR 0027 / docs-structure.md)。
- [ ] 全 ADR が日本語見出し (ADR 0027 / template-language.md)。
- [ ] 全 ADR の `related_*` 参照先が実在する (相互参照のあるものは双方向で確認)。
- [ ] `docs/adr/README.md` の状態一覧表が更新済み (27 行全てに「★起草済 (accepted)」を反映)。
- [ ] `docs/harness/roadmap.md` の A1 行 status が `in-progress` (本 PR 着手) に更新済み。
- [ ] `docs/plans/INDEX.md` に PLAN-001 行が追加済み。
- [ ] ADR 本文に Kotlin / Gradle DSL / SQL 等のコード断片を含まない (§4.6 のコード禁止原則)。
- [ ] PII / Secrets が diff に含まれない (`.claude/rules/{pii,secrets}.md`)。

## スコープ外

- A2 (`.claude/rules/*` 全ファイル本格化 + docs 拡充): rules の skeleton → 本格版への昇格は
  別 PR で実施。本 PR は rule への参照を ADR 内から張るのみ。
- A3 (専用 Skill 群実装): `feature-request` / `bug-fix` / `refactor` / `adr-author` /
  `harness-meta` の実装は別 PR。
- 各 ADR が記述する決定の **実装** は Phase C の各 Epic / Plan で実施。
- ADR 内容の機械検証 (frontmatter JSON Schema / 5 行 summary 検証 / 識別子実在検証):
  A6 で Gradle カスタムタスクとして導入。本 PR では手動 Self-Verification のみ。

## メモ

- ADR ステータスを `proposed` ではなく `accepted` で起票する判断:
  本 PR は B0 で merge 済み plan.md に既に記載された決定を ADR ファイルに物理化する
  フェーズであり、決定自体は plan.md レビュー時点で accept 済。`proposed → accepted` の
  遷移を同 PR 内で行うのは冗長。後で revoke が必要になれば `.claude/rules/adr.md`
  §採番・命名・ステータスの規約通り **新 ADR + `Superseded by`** で対応する。
- Plan の `type` 値が template.md 上 `feature | bug-fix | refactor | dep-upgrade | chore`
  となっており `docs` が無いため、本 Plan は `type: chore` で起票。PR テンプレート側は
  `.github/PULL_REQUEST_TEMPLATE/docs.md` を使用する (Plan type と PR type は別系統)。
  Plan template の type 一覧に `docs` を追加するかは A2 で `.claude/rules/plan.md` 拡充時に判断。
- `related_adrs` に 0001-0027 を全て列挙: 本 PR の影響範囲を明示し、A6 の
  `docs/traceability.md` 自動生成で全 ADR が PLAN-001 から逆引きできるようにする。
