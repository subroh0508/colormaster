---
name: pr-poller
description: |
  ローカル Claude Code 内でポーリング起動し、gh CLI で merged/closed PR を取得 →
  未処理 PR があれば pr-retrospective を起動、Renovate ラベル PR があれば
  dependency-upgrade を起動する。3 系統の起動経路 (起動時 + CronCreate + ScheduleWakeup)
  に対応するため .claude/locks/pr-poller.lock で排他制御する。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §6.2 A4
related_rules:
  - .claude/rules/pr-poller.md
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/mcp-usage.md
---

# pr-poller (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。CronCreate / ScheduleWakeup による
> 自動化を含む本格実装は A4 で行う。B0 時点では人間が Claude Code を起動時に
> 手動で呼び出して使う運用とする。

## 役割

- gh CLI で merged/closed PR を取得 → 既に learning ファイルがある PR を除外 → 未処理 PR があれば `pr-retrospective` を起動
- 一定期間経過 (7 日) または未処理 learning 件数到達 (デフォルト 10 件) で `harness-meta` を起動
- open PR で `labels:renovate` が付くものを検出して `dependency-upgrade` を起動 (Claude API を GitHub Actions 上で呼ばないためのローカル化、ADR 0017)
- `roadmap-tracker` の pending-fetch 項目を再走査して完了根拠を後追い登録 (R-35)

## 起動経路 (3 系統)

| 経路 | 契機 | 状態 |
|---|---|---|
| 起動時 | ローカル Claude Code 起動直後の手動呼び出し | B0 から利用可 |
| CronCreate | 日次 09:00 JST | A4 で導入 |
| ScheduleWakeup | 継続ループ | A4 で導入 |

## Gotchas

- **3 系統で重複起動が発生し得る**ため起動時に `.claude/locks/pr-poller.lock` を排他取得 (PID 記録)。既存ロックが N 分以内なら no-op、N 分超過なら stale lock と判定して上書き取得。
- 長期不在後の再開で取りこぼしを防ぐため、起動時に「最後の処理から N 日経過した PR」を最優先で処理するキャッチアップ動作を組み込む (R-11)。
- gh CLI 失敗時のリトライ・フォールバック方針は `.claude/rules/pr-poller.md` を参照。

## 関連

- `docs/harness/plan.md` §5.3 / §6.2 A4
- `.claude/rules/pr-poller.md`
