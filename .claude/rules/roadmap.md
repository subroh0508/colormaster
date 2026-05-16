---
id: rules-roadmap
title: ロードマップ Markdown 規約 (roadmap-tracker Skill 操作規約)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §5.3 / R-34 / R-35 / R-36
---

# roadmap.md — ロードマップ Markdown 規約

> 本ルールは `roadmap-tracker` Skill が `docs/harness/roadmap.md` および
> `docs/epics/<id>/roadmap.md` を生成・更新する際の操作規約。
> ADR 起票基準を満たさないため (補助 Skill / 撤回コスト低) ADR 化は見送り、
> 本 rules で運用する (R-36)。

## 入力スコープ

- **取り込む**: `docs/harness/plan.md` (B0/A1-A10/C1-C10 のフェーズ項目)、`docs/epics/EPIC-NNN-*/` (frontmatter から ID / タイトル / status / `expected_modules`)
- **取り込まない (走査対象外)**: `docs/plans/*.md` — Plan は 1 PR で完結するためロードマップ追跡対象外 (PR レビュー & merge で完結)

## ロードマップ Markdown の構造

```markdown
---
id: roadmap-harness | roadmap-EPIC-NNN
title: <ロードマップタイトル>
status: living
last_updated: YYYY-MM-DD
source_plan: docs/harness/plan.md (全体ロードマップのみ)
source_epic: EPIC-NNN (Epic 別ロードマップのみ)
---

# 概要 (5 行以内)

## 項目一覧

| ID | タイトル | status | expected_modules | 完了根拠 |
|---|---|---|---|---|
| B0 | ブートストラップ PR | in-progress | — | (未完) |
| A1 | ADR 0001-0027 一括起草 | proposed | docs/adr/** | — |
| ... | ... | ... | ... | ... |

## 完了根拠

| ID | PR 番号 | マージ日 | 主要ファイル |
|---|---|---|---|

## 着手順とブロック関係

```mermaid
gantt
    title 着手順
    ...
```

## 保留中の意思決定・不明事項 (Open Questions)

| 起票日 | 内容 | 暫定方針 | 解決状態 |
|---|---|---|---|

## 技術的障壁と回避策 (Blockers and Workarounds)

| 起票日 | 障壁 | 回避策 | 解決日 | 解決方法 |
|---|---|---|---|---|

## 着手順変更履歴 (append-only)

| 日付 | 変更内容 | 理由 |
|---|---|---|

## 次の推奨着手 (並行実装観点)

並行実装容易性に基づく top-N (各候補について依存解決済 + 現在 in-progress 項目との
`expected_modules` 重複が少ない順)。重複ゼロが複数あれば優先度 (must/should/could) で
二次ソート。`expected_modules` 未記入の候補は「並行可否不明 (要記入)」として warning 出力。
```

## ステータス語彙

| 値 | 意味 |
|---|---|
| `proposed` | 起票済み、未着手 |
| `in-progress` | 着手中 |
| `completed` | 完了 (完了根拠を記録) |
| `blocked` | 障壁により停止中 (Blockers セクションに記載) |
| `abandoned` | 取り下げ (理由を着手順変更履歴に記載) |

## 自動起動フック

- `epic-author` の Epic 起票直後 (**Plan は対象外**)
- `implementation-workflow` Phase 8 (Merge 直後、Epic 配下 PR または B-A-C フェーズ項目に該当時のみ)
- `pr-poller` の pending-fetch 再走査

## 手動起動契機

「ロードマップ更新」「進捗可視化」「着手順入れ替え」「障壁記録」「保留事項追加」「次の推奨着手を出して」等

## 重要原則

- **plan.md / Epic 本体への逆同期はしない** (片方向ミラー、R-34)。`roadmap-tracker` は Read のみで取り込み、進捗・完了根拠・障壁の記録は roadmap.md 側にのみ追記する。
- `gh pr view` 取得失敗時は `<!-- evidence:pending-fetch -->` コメントで暫定登録し、`pr-poller` が定期的に再走査する (R-35)。
- Open Questions / 障壁 / 着手順変更履歴は **append-only**。既存項目の削除は禁止 (解決時は別行に解決日と方法を追記)。
- 重大な運用方針変更が発生したら §4.5 ADR 起票基準を再評価し、基準を 2 項目以上満たす状態になったら新規 ADR を起こして格上げする (R-36)。

## 機械検証 (A6 で導入)

- Gradle カスタムタスク (Kotlin、`org.commonmark:commonmark` + `commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で「`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の項目 ID (`B0` / `A1` / `EPIC-NNN`) が plan.md / `docs/epics/` に実在する」を検証 (§5.2)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない

## 関連

- `docs/harness/plan.md` §5.3 (Skill の責務)
- `docs/harness/plan.md` R-34 / R-35 / R-36
- `.claude/skills/roadmap-tracker/SKILL.md`
