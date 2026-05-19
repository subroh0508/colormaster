---
id: rules-plan
title: Plan ファイル命名規約 / 状態遷移
status: stable
last_updated: 2026-05-19
paths:
  - "docs/plans/*.md"
  - ".claude/skills/plan-author/**"
related_adrs:
  - ADR-0027
---

# plan.md — Plan ファイル命名規約 / 状態遷移

> Plan は **単一 PR で完結する取り組み** の意思決定・進捗を 1 ファイルで管理する記録。
> Epic と異なり「複数 PR にまたがる構造化された取り組み」は対象外 (`epic.md` 参照)。
> 起票は `plan-author` Skill 経由、ロードマップ追跡対象外 (R-34、PR レビュー & merge で完結)。

## いつ Plan を起こすか

- 1 PR で完結する単機能追加・バグ修正・リファクタ・依存更新等
- 複数 PR が必要だと判明したら **Epic に昇格** (`status: promoted` + `promoted_to: EPIC-NNN`)
- 単発の commit で済むタイポ修正・コメント追加等は Plan 不要

## いつ Epic に昇格させるか

| 兆候 | 対応 |
|---|---|
| 期待される変更行数が 1,000 行を超える / 触るファイル数が 30 を超える | Epic 昇格を検討 |
| レビュー aspect (spec / arch / test / security / etc.) のうち 3 以上が大きく走る | Epic 昇格 |
| 仕様変更が複数 SPEC に波及 | Epic 昇格 |
| 単独 PR で完結する見込みが立たない | Epic 昇格 |

昇格時は元 Plan の `status` を `promoted`、`promoted_to: EPIC-NNN` に更新し、`docs/epics/EPIC-NNN-<slug>/` を `epic-author` Skill で生成。Plan ファイル自体は履歴保持のため削除しない。

## 命名規約

- ファイル名: `PLAN-NNN-<kebab-case-slug>.md` (3 桁ゼロパディング、Epic と独立採番)
- 例: `PLAN-001-adr-0001-0027-batch-draft.md` / `PLAN-007-add-search-by-brand.md`
- 採番は `docs/plans/INDEX.md` で連番管理、欠番は実装前なら整理可

## 採番ライフサイクル (予約 → 起票 → 衝突解消) (PR #175/#176 レトロ Try)

並走 PR 間の Plan 番号衝突を予防する運用 SoT。`adr.md` §採番・命名・ステータス と同様の運用方針を Plan 側でも明示する。

### 1. 予約 (Plan 起票前)

- `plan-author` Skill 起動時 or 手動 Plan 起票時に **最初に `docs/plans/INDEX.md` の最終行 PLAN 番号 + 1** を予約番号とする
- 予約直後に `docs/plans/INDEX.md` へ **placeholder 行を 1 commit で挿入** (`| PLAN-NNN | (起票中) | <type> | proposed | — | YYYY-MM-DD |`)
- placeholder 行を含む commit は **Plan 本体起票 commit と同一ブランチ + 同一 PR** に含める (`docs/plans/INDEX.md` 行と Plan 本体ファイルが揃って merge される)

### 2. 起票 (Plan 本体作成)

- 予約番号で `docs/plans/PLAN-NNN-<slug>.md` を作成、frontmatter `id: PLAN-NNN` + `status: proposed` で記述
- `docs/plans/INDEX.md` の placeholder 行を実体 (タイトル / type / status / 関連 Epic / 起票日) に置換
- 同一 PR 内で **placeholder 挿入 commit → 本体起票 commit** の logical separator を分けても、まとめて 1 commit にしても可 (`implementation-workflow.md` §commit 分離規範 参照)

### 3. 衝突解消 (並走 PR で同一番号取得時)

並走 PR (`A8` と `A5` 等の並列 spawn) が **同時刻に同じ予約番号** を取得した場合、merge 順序によって後発 PR が rename を強制される。衝突を検知した時点で以下を実施:

| 検知タイミング | 対応 |
|---|---|
| 先行 PR が merge される前 (Draft / Ready 両 PR open) | 後発 PR の per-task pane / orchestrator pane で **`docs/plans/INDEX.md` を rebase 取得 → 次の番号に rename** (`git mv` + frontmatter `id` 更新 + INDEX.md 行の rebuild) |
| 先行 PR が merge された後 (後発 PR が rebase / merge 失敗) | 後発 PR を **orchestrator 代行 fix で rename**: `git fetch origin master` → `git rebase master` で INDEX.md conflict を解消 → 後発 PR の Plan 番号を最終行 + 1 に rename (`git mv docs/plans/PLAN-NNN-<slug>.md docs/plans/PLAN-MMM-<slug>.md` + frontmatter `id` 更新) |
| 既に PR description / commit message に古い番号が混入 | rebase 中の reword 制約で commit message の旧番号表記が残る場合は、**実体ファイル名と frontmatter `id` を最終 SoT とする** (`commit-message.md` §subject 経過措置と整合)。後続 retro / harness-meta で「commit message 番号 vs 実体番号の不一致」が観測されたら learning ファイルに記録 |

### 衝突予防のための運用指針

- **並列 spawn 起動時に Plan 起票が複数ある場合**: orchestrator pane 側で予約番号を **事前ブロードキャスト** (per-task pane prompt に「PLAN-N1 を A8 / PLAN-N2 を A5 で予約」と明記) して衝突を予防
- **placeholder 行を早期 push する**: per-task pane の Plan 本体作成完了を待たず、placeholder 行のみを先に小さな commit で push しても良い (`docs/plans/INDEX.md` 1 行追加 + frontmatter なしのため lightweight)
- **連番欠番の許容**: Plan が `abandoned` で取り下げられた場合は **番号維持** (`adr.md` §採番 と同様)、欠番埋め直しは禁止 (古い PR / retro / learning の参照リンクが壊れる)

### 運用例

- **PR #175 (A8) と PR #176 (A5) の PLAN-003 衝突 (2026-05-19)**: 並走 spawn で 両 PR が PLAN-003 を予約 → PR #175 (A8) が先に merge され PLAN-003 を取得 → PR #176 (A5) は `git rebase master` で INDEX.md conflict を解消し、PLAN-003 → PLAN-004 へ rename (`git mv` + frontmatter `id` 更新 + INDEX.md 行 rebuild)。`commit 9290d12` の subject には rebase reword 制約で旧 PLAN-003 表記が残るが、実体は PLAN-004 で確定 (PR #176 retro Problem 参照、本セクション SoT 化の起点)

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 着手中 (Draft PR open など) |
| `completed` | 完了 (PR マージ済) |
| `abandoned` | 取り下げ (理由を Plan 本体に記載) |
| `promoted` | Epic に昇格 (frontmatter `promoted_to: EPIC-NNN` 必須) |

## frontmatter 必須キー

```yaml
---
id: PLAN-NNN
title: <日本語タイトル>
type: feature-request | bug-fix | refactor | dependency-upgrade | chore | docs | harness
status: proposed | in-progress | completed | abandoned | promoted
related_pr: <PR 番号、起票時は null>
related_epic: EPIC-NNN | null
related_specs:
  - SPEC-IDOL-001-3
related_adrs:
  - ADR-0017
expected_modules:
  - <touch 予定のファイル / ディレクトリ glob>
created_at: YYYY-MM-DD
completed_at: YYYY-MM-DD | null
promoted_to: EPIC-NNN | null
---
```

block 形式必須 (`.claude/rules/docs-structure.md` frontmatter 規約と整合)。要素ゼロのみ `[]` 許容。

## 本文構造 (docs/plans/template.md と整合)

```markdown
# <タイトル>

> **5 行以内 summary**: 目的 / 主な変更 / 影響範囲

## 目的

## 背景

## アプローチ

## 受け入れ基準 (AC)

- [ ] AC-1: <検証可能な条件>

## スコープ外

## メモ
```

## INDEX.md 更新規約

- 起票時 / status 変更時 / Epic 昇格時に `docs/plans/INDEX.md` を更新
- `plan-author` Skill が自動更新するが、手動編集も許容
- 行構造: `| PLAN-NNN | タイトル | type | status | related_epic | 起票日 |`

## Plan ⇄ Epic ⇄ ADR の責務分離

| 種別 | スコープ | 主な記録 |
|---|---|---|
| Plan | 1 PR | アプローチ / AC / 単独の意思決定 |
| Epic | 複数 PR | 構成 PR 一覧 / 横断 AC / 細粒度決定 (`decisions.md`) / Open Questions |
| ADR | 不変 | アーキテクチャ的に重要な決定 (`.claude/rules/adr.md` §起票基準) |

「Epic にすべきか Plan に留めるか迷ったら Plan で起票し、必要に応じて promote」を基本方針とする。

## 機械検証 (A6 で導入)

- Gradle カスタムタスクで以下を検証 (§5.2):
  - frontmatter 必須キーの存在 (`id`, `title`, `type`, `status`, `created_at` 等)
  - `id` の正規表現 (`^PLAN-\d{3}$`)
  - `INDEX.md` 行と Plan 本体の status 整合
  - `promoted` 時に `promoted_to` が `EPIC-NNN` 形式で実在
- `template-language.md` の日本語必須も合わせて適用

## Gotchas

- **Plan は `roadmap-tracker` の取り込み対象外** (R-34)。`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` には Plan を列挙しない
- **Epic 配下 PR は Plan を起こさない**。Epic ロードマップの構成 PR 行で管理し、PR description で AC を記述する
- `status: promoted` 時の Plan ファイルは履歴として残す。**ファイル削除や rename は禁止** (古い PR の参照が壊れるため)
- type 列の値は `.claude/rules/commit-message.md` の Conventional Commits type と一致させる (`feat` → `feature-request`、`fix` → `bug-fix` 等のマッピングは PR description の type で揃える)

## 関連

- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
- `docs/plans/INDEX.md`
- `docs/plans/template.md` (Plan 本体テンプレ)
- `.claude/skills/plan-author/SKILL.md`
- `.claude/rules/{epic,adr,roadmap,docs-structure,commit-message}.md`
