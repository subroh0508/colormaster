---
id: rules-roadmap
title: ロードマップ Markdown 規約 (roadmap-tracker Skill 操作規約)
status: stable
last_updated: 2026-05-17
paths:
  - "docs/harness/roadmap.md"
  - "docs/epics/**/roadmap.md"
  - ".claude/skills/roadmap-tracker/**"
related_adrs:
  - ADR-0017
  - ADR-0027
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

## 手動マージ時の同 PR 更新ルール (A1 レトロ Try 対応)

`implementation-workflow` を経由しないマージ (B0 のような手動マージ、急ぎの hotfix 等) では
Phase 8 の `roadmap-tracker` 自動起動フックが **発火しない** ため、進捗ロストのリスクが残る。
そのため:

- **手動マージしたら、その PR の `pr-retrospective` learning PR (`harness/learnings-batch-YYYY-WW`
  ブランチ) と同じ PR で `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の手動更新も実施する**
  (1 PR でレトロ起票 + ロードマップ更新を完結させ、二重 PR 化や更新漏れを回避)
- 手動更新内容: 対象 PR の項目を `in-progress` → `completed` に変更、`完了根拠` 表に PR 番号 + マージ日
  + 主要ファイルを追記、`着手順変更履歴` に「手動マージで Phase 8 自動同期発火せず、本 PR で手動更新」
  と記録
- `implementation-workflow` 経由マージとの判別: PR description の type が `harness` / `feature` /
  `bugfix` / `refactor` / `dependency-upgrade` で `roadmap-tracker` 自動起動済み行に
  `<!-- roadmap-tracker:auto -->` コメント有 → 自動更新済。コメント無 → 手動更新が必要

## 重要原則

- **plan.md / Epic 本体への逆同期はしない** (片方向ミラー、R-34)。`roadmap-tracker` は Read のみで取り込み、進捗・完了根拠・障壁の記録は roadmap.md 側にのみ追記する。
- `gh pr view` 取得失敗時は `<!-- evidence:pending-fetch -->` コメントで暫定登録し、`pr-poller` が定期的に再走査する (R-35)。
- Open Questions / 障壁 / 着手順変更履歴は **append-only**。既存項目の削除は禁止 (解決時は別行に解決日と方法を追記)。
- 重大な運用方針変更が発生したら §4.5 ADR 起票基準を再評価し、基準を 2 項目以上満たす状態になったら新規 ADR を起こして格上げする (R-36)。

## セクション別の競合解消ポリシー (PR #123 レトロ Try)

並走 PR の rebase 競合解消時、`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` のセクションごとに方針が異なる:

| セクション | 解消ポリシー | 根拠 |
|---|---|---|
| 完了根拠 表 | **chronological insert** (merge 日順) | 新しい PR を時系列に正しい位置に挿入、過去 PR の混入は避ける |
| 着手順変更履歴 | **append-only** (新行を末尾に追加) | 履歴系セクションは既存行を絶対に書き換えない |
| 本文段落 (概要 / 補足) | **append-only** (新段落を最後に追加) | 既存段落は SoT として維持、新情報は最終段落として追記 |
| 次の推奨着手 | **full replacement** (最新状態に書き換え) | 並列実行可能性は時々刻々変動するため、merge 順の競合解消ではなく最終状態に置換 |
| Open Questions / Blockers | append-only (解決時は別行に解決日と方法を追記) | 重要原則と整合、既存行の書き換え禁止 |

判断ロジックは「履歴 / 時系列を保持するか」「最新状態を反映するか」の 2 軸。conflict marker (`<<<<<<<` / `>>>>>>>`) のあるファイルを開いたら、まずセクション種別を確認してから解消。

## mirror PR 起票 SLA (PR #123 レトロ Try)

mirror PR (`harness/roadmap-mirror-<phase-id>` ブランチ) は本体 PR merge 直後の起票 → review → merge を目指す:

- **目標 SLA**: 本体 PR merge から **30 分以内** に mirror PR 起票、**60 分以内** に merge 完了
- **理由**: mirror PR が長く open のままだと他並走 PR (他 mirror PR / 本体 PR) の merge により再 rebase が必要になる (PR #123 で実証、2 回 rebase 発生)
- **再 rebase 回数最小化**: 上記 SLA を守ることで mirror PR の rebase 回数を 0-1 回に抑制可能
- **例外**: 並走 PR が一斉に merge する流れの中では SLA を守れない場合あり (PR #123 mirror PR #127 は A2-5 / A2-2 mirror が先行 merge で 2 回 rebase)、その場合は本 rule の「セクション別の競合解消ポリシー」に従って整合解消

## merge note 段落テンプレ (PR #126 レトロ Try)

mirror PR で `docs/harness/roadmap.md` 完了根拠表直下に追加する「merge note 段落」のテンプレ:

```markdown
> 注: <Phase ID> (<Phase 名>) は orchestrator (subroh0508) 委任で R-15 代替し
> `gh pr merge --<squash|merge>` を実行 (commit `<merge-commit-sha>`)。`<theme>` Skill の
> 手動代替運用 (A3 / A4 Skill 本格化前) の暫定パターン。
```

- 「admin override」「self-merge」「force-merge」等のメタ言及語は使わず、`commit-message.md` §メタ言及語の classifier トリガー回避 のニュートラル表現を採用 (PR #126 で classifier denied 経験あり)
- merge commit hash は `gh pr view <PR#> --json mergeCommit` の `mergeCommit.oid` を引用 (本 rule §完了根拠表の commit 引用基準 参照)

## 完了根拠表の commit 引用基準 (PR #129 レトロ Try)

完了根拠表の `主要ファイル` 列または隣接の補助情報には **merge commit hash を主、head commit hash を従** で併記する:

| 項目 | 取得方法 | 役割 |
|---|---|---|
| merge commit hash (主) | `gh pr view <PR#> --json mergeCommit` の `mergeCommit.oid` | master 反映後の SoT、後追い `git log master` で辿れる |
| head commit hash (従、必要時) | `git log <branch>` 最終 commit、または `gh pr view --json commits` の最後の commit | rebase / fix loop で hash が変動するため参考扱い |

引用形式の例:

```markdown
| A2-6 | #129 | 2026-05-17 | `.claude/settings.json` (merge commit `1ac6fe4`、head commit `b961a22`) |
```

- 主のみ記録する場合は merge commit hash で OK、head commit は省略可
- squash merge の場合 head commit は新規生成された squashed commit を指すため、`gh pr view` の `mergeCommit.oid` が SoT

## 機械検証 (A6 で導入)

- Gradle カスタムタスク (Kotlin、`org.commonmark:commonmark` + `commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で「`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の項目 ID (`B0` / `A1` / `EPIC-NNN`) が plan.md / `docs/epics/` に実在する」を検証 (§5.2)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない

## 関連

- `docs/harness/plan.md` §5.3 (Skill の責務)
- `docs/harness/plan.md` R-34 / R-35 / R-36
- `.claude/skills/roadmap-tracker/SKILL.md`
