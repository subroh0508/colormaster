---
id: rules-epic
title: Epic ディレクトリ構成 / 状態遷移
status: stable
last_updated: 2026-05-17
paths:
  - "docs/epics/**"
  - ".claude/skills/epic-author/**"
related_adrs:
  - ADR-0027
---

# epic.md — Epic ディレクトリ構成 / 状態遷移

> Epic は **複数 PR にまたがる構造化された取り組み** を 1 ディレクトリで管理する記録。
> Plan (1 PR 完結) との分担は `plan.md` 参照。起票は `epic-author` Skill 経由、
> ロードマップ追跡対象 (`roadmap-tracker` が `docs/epics/<id>/roadmap.md` を更新)。

## いつ Epic を起こすか

- 複数 PR (目安: 3 PR 以上) に分割した方がレビュー / 並走 / リスク分散の観点で有利な取り組み
- フェーズ全体 (A1-A10 / C1-C10) のうち、複数アーティファクトを跨ぐもの (例: EPIC-A2 = rules + docs)
- 単一 PR で完結する見込みが立たない単一 Plan の昇格 (`plan.md` §Epic 昇格条件 参照)

## ディレクトリ構成 (必須 5 ファイル)

```text
docs/epics/EPIC-NNN-<kebab-case-slug>/
  README.md          # Epic 本体: 目的 / 背景 / スコープ / 構成 PR / AC
  roadmap.md         # PR 進捗トラッカー (roadmap-tracker が更新)
  open-questions.md  # 未解決事項 (append-only)
  decisions.md       # 細粒度決定の記録 (ADR 昇格未満)
  progress.md        # 時系列の進捗ログ
```

`docs/epics/template/` に各ファイルのテンプレ配置済。新規 Epic 起票時はテンプレを copy し frontmatter を埋める。

## 命名規約

- ディレクトリ名: `EPIC-NNN-<kebab-case-slug>` (3 桁ゼロパディング、Plan と独立採番)
- 例: `EPIC-000-harness-foundation/` / `EPIC-A2-rules-docs-extension/` (フェーズ ID も許容)
- `EPIC-NNN` 形式は連番、フェーズ ID (A2 / B0 / C1 等) 形式は plan.md §6 のフェーズと対応
- スラグはタイトルから kebab-case で導出 (動詞は省略可、要件のキーワードを含める)

## ステータス語彙 (`README.md` frontmatter `status`)

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 構成 PR の少なくとも 1 つが進行中 |
| `completed` | 構成 PR が全てマージ済み、AC 達成 |
| `blocked` | 障壁により停止中 (`roadmap.md` の Blockers セクション参照) |
| `abandoned` | 取り下げ (理由を `decisions.md` に記載) |

## 必須 frontmatter (`README.md`)

```yaml
---
id: EPIC-NNN
title: <日本語タイトル>
status: proposed | in-progress | completed | blocked | abandoned
created_at: YYYY-MM-DD
completed_at: YYYY-MM-DD | null
expected_modules:
  - <touch 予定の glob>
related_adrs:
  - ADR-NNNN
related_specs:
  - SPEC-IDOL-001-3
---
```

各補助ファイル (`roadmap.md` / `open-questions.md` / `decisions.md` / `progress.md`) の frontmatter は `source_epic: EPIC-NNN` を必須、`status: living` で固定。

## INDEX.md 更新規約

- 起票時 / status 変更時に `docs/epics/INDEX.md` を更新
- `epic-author` Skill が自動更新
- 行構造: `| EPIC ID | タイトル | status | 構成 PR 数 | 起票日 | 完了日 |`

## 自動起動フック (roadmap-tracker)

- Epic 起票直後 (`epic-author` 経由) → `docs/epics/<id>/roadmap.md` 雛形生成 + `docs/harness/roadmap.md` の Epic 行追加
- 構成 PR マージ直後 (`implementation-workflow` Phase 8) → 当該 Epic の `roadmap.md` 完了根拠表に PR 番号 + マージ日を追記、status を `in-progress` → `completed` (全 PR マージ済時)
- 詳細は `.claude/rules/roadmap.md` §自動起動フック 参照

## 補助ファイルの役割

| ファイル | 役割 | 更新規約 |
|---|---|---|
| `README.md` | 目的 / 背景 / スコープ / 構成 PR / AC | 構成 PR 増減時に更新、AC 達成チェック |
| `roadmap.md` | PR 進捗 / 完了根拠 / 着手順 / Open Questions / Blockers | `roadmap-tracker` が自動更新、手動マージ時は同 PR で手動更新 |
| `open-questions.md` | 未解決事項 (起票日 / 内容 / 暫定方針 / 解決状態) | **append-only**、解決時は別行に解決日と方法を追記 |
| `decisions.md` | ADR 昇格未満の細粒度決定 (rule の分割粒度 / docs 責務境界 / テンプレ文言) | append-only、ADR 起票基準を満たすものは `docs/adr/` に昇格してリンク |
| `progress.md` | 時系列の進捗ログ (日付 / 出来事 / 関連 PR) | 日次 / 週次レベルの記録、マイルストーン表も含む |

## Epic ⇄ Plan ⇄ ADR の責務分離

`.claude/rules/plan.md` §責務分離表 参照。Epic は「複数 PR の構造化」、Plan は「単一 PR」、ADR は「アーキテクチャ的に不変な決定」。

## 機械検証 (A6 で導入)

- Gradle カスタムタスクで以下を検証 (§5.2):
  - `docs/epics/EPIC-NNN-*/` ディレクトリ内に必須 5 ファイルが存在
  - 各補助ファイルの `source_epic` が親ディレクトリ ID と一致
  - `INDEX.md` 行と README の status 整合
  - `expected_modules` 未記入の Epic は warning (`roadmap-tracker` の並行可否判定が機能しない)

## Gotchas

- **`open-questions.md` / `decisions.md` は append-only**。既存項目の削除は禁止 (解決時は別行に追記)
- **Epic 配下 PR は Plan を起こさない**。Epic ロードマップの構成 PR 行で AC 管理
- **status: abandoned 時もディレクトリを削除しない**。古い PR / ADR の参照が壊れるため、`decisions.md` に取り下げ理由を残す
- フェーズ ID 形式 (`EPIC-A2`) と連番形式 (`EPIC-001`) は混在可能だが、混乱を避けるため **フェーズ ID 形式は plan.md §6 のフェーズと厳密に対応** させること

## 関連

- `docs/harness/plan.md` §4.1 (Epic と Plan の区別)
- `docs/epics/INDEX.md`
- `docs/epics/template/` (5 ファイルテンプレ)
- `.claude/skills/epic-author/SKILL.md`
- `.claude/skills/roadmap-tracker/SKILL.md`
- `.claude/rules/{plan,adr,roadmap,docs-structure}.md`
