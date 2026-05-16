---
id: rules-pr-poller
title: pr-poller ローカルポーリング規約
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §5.3 / §6.2 A4 / ADR 0017
---

# pr-poller.md — ローカルポーリング規約

> `pr-poller` Skill の動作規約。3 系統の起動経路に対応した排他制御と
> 未処理 PR 検出ロジックを規定。

## 起動経路 (3 系統)

| 経路 | 契機 | 導入時期 |
|---|---|---|
| 手動起動 | ローカル Claude Code 起動直後のユーザー指示 | **B0 から利用可** |
| `CronCreate` | 日次 09:00 JST のスケジュール起動 | A4 で導入 |
| `ScheduleWakeup` | 継続ループ (前回処理から N 時間後) | A4 で導入 |

## 排他制御

- 起動時に `.claude/locks/pr-poller.lock` を排他取得 (PID + 取得時刻を記録)
- 既存ロックが **N 分以内** (デフォルト 30 分) なら no-op で終了
- N 分超過なら stale lock と判定して上書き取得
- 正常終了時に lock を削除

## 検出ロジック

1. `gh pr list --state merged,closed --search "merged:>$LAST_RUN"` で未処理候補 PR を取得
2. `docs/harness/learnings/YYYY-MM-DD-pr-<N>.md` の存在をチェックして処理済 PR を除外
3. 残りに対して `pr-retrospective` Skill を順次起動
4. open PR で `labels:renovate` が付くものを別途検出 → `dependency-upgrade` Skill を起動 (R-37 / ADR 0017)
5. `roadmap-tracker` の `<!-- evidence:pending-fetch -->` 項目を再走査 (R-35)

## harness-meta 起動閾値

以下のいずれかを満たしたら `harness-meta` を自動起動 (デフォルト値、`harness-meta-criteria.md` で上書き可能):

- 未処理 learning が **10 件** 蓄積
- 前回 `harness-meta` 実行から **7 日経過**

## キャッチアップ動作 (R-11 対策)

長期不在後の再開で取りこぼしを防ぐため、起動時に「最後の処理から N 日経過した PR」を
最優先で処理する。

## Gotchas

- **GitHub Actions では呼ばない** (Claude API コスト回避、ADR 0017)。
- gh CLI 失敗時はリトライ (指数バックオフ、最大 3 回)、それでも失敗したら警告を出して当該 PR を次回再試行に回す。
- learning ファイル生成と同時に `harness/learnings-batch-YYYY-WW` ブランチへ push (R-12 ロスト対策)。

## 関連

- `docs/harness/plan.md` §5.3 / §6.2 A4 / R-11 / R-12
- ADR 0017 (ローカル Claude Code ポーリング駆動)
- `.claude/skills/pr-poller/SKILL.md`
- `.claude/rules/harness-meta-criteria.md` (A4 で本格化、起動閾値を上書き可能)
