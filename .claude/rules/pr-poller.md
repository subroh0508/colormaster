---
id: rules-pr-poller
title: pr-poller ローカルポーリング規約
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/pr-poller/**"
  - ".claude/locks/**"
related_adrs:
  - ADR-0017
related_plan: docs/harness/plan.md §5.3 / §6.2 A4 / R-11 / R-12
---

# pr-poller.md — ローカルポーリング規約

> `pr-poller` Skill の動作規約。3 系統の起動経路に対応した排他制御と
> 未処理 PR 検出ロジック、Renovate 系 PR の自動判別、キャッチアップ動作を規定。

## 3 系統の起動経路

| 経路 | 契機 | 導入時期 |
|---|---|---|
| **手動起動** | ローカル Claude Code 起動直後のユーザー指示 (`/pr-poller` 等) | **B0 から利用可** |
| **CronCreate (日次)** | 09:00 JST のスケジュール起動 (Claude Code routine) | A4 で導入 |
| **ScheduleWakeup (継続ループ)** | 前回処理から N 時間後の自動再起動 (`/loop` skill 経由) | A4 で導入 |

## 排他制御

- 起動時に `.claude/locks/pr-poller.lock` を排他取得
- ロックファイル内容: `<PID>\n<取得時刻 ISO8601>\n<起動経路 (manual/cron/wakeup)>`
- 既存ロックが **N 分以内** (デフォルト 30 分) なら no-op で終了 (二重起動防止)
- N 分超過なら stale lock と判定して上書き取得 + warning ログ出力
- 正常終了時に lock を削除、異常終了 (SIGKILL 等) で残ったロックは次回 stale 判定で自動削除

## 検出ロジック (Phase 1: merged/closed PR)

1. **`gh pr list --state merged,closed --search "merged:>$LAST_RUN"`** で未処理候補 PR を取得
   - `$LAST_RUN` は前回処理時刻 (`.claude/locks/pr-poller.last-run` に記録、なければ 7 日前)
2. `docs/harness/learnings/YYYY-MM-DD-pr-<N>.md` の存在をチェックして処理済 PR を除外
3. 残りに対して `pr-retrospective` Skill を順次起動 (`retrospective-format.md` 規約)
4. learning ファイル生成と同時に `harness/learnings-batch-YYYY-WW` ブランチへ push (R-12 ロスト対策)

## 検出ロジック (Phase 2: Renovate ラベル open PR)

5. **`gh pr list --state open --label renovate`** で Renovate 系 open PR を取得
6. 残りに対して `dependency-upgrade` Skill を起動 (R-37 / ADR 0017、A4 で本格化)
   - `dependency-upgrade.md` PR テンプレ + `dependency-upgrade` Plan type と整合 (`pr-template.md` / `plan.md`)
7. **Renovate ラベルの自動判別**: `labels:renovate` / `dependencies` / `renovate-bot` のいずれか付与で対象

## 検出ロジック (Phase 3: pending-fetch 再走査)

8. `roadmap-tracker` の `<!-- evidence:pending-fetch -->` コメント有項目を再走査 (R-35)
9. 該当 PR の `gh pr view` 取得を再試行、成功時は roadmap.md の完了根拠を埋める + コメントを削除

## harness-meta 起動閾値

以下のいずれかを満たしたら `harness-meta` Skill を自動起動 (デフォルト値、`harness-meta-criteria.md` で上書き可能):

| 条件 | デフォルト値 |
|---|---|
| 未処理 learning が蓄積 | **10 件** |
| 前回 `harness-meta` 実行からの経過 | **7 日** |
| 連続実行間隔 (最小) | 24 時間 |

上書きは `harness-meta-criteria.md` §pr-poller 起動閾値 (本格化済) または `pr-poller` 起動引数で指定。

## キャッチアップ動作 (R-11 対策)

長期不在後の再開で取りこぼしを防ぐため、起動時に以下を実行:

1. `$LAST_RUN` が **3 日以上前** なら「最後の処理から経過した PR」を最優先で処理
2. 件数が 30 件超なら orchestrator に warning 通知 + 古い順から 10 件ずつ batch 処理
3. batch 処理中に新たな merged PR を発見しても優先順位は変えず (FIFO)

## gh CLI 失敗時のリトライ

- 指数バックオフ (1s → 2s → 4s)、最大 3 回
- それでも失敗したら当該 PR を次回再試行に回し warning ログ
- 連続 5 件失敗 (`gh` CLI 自体の不調) で `pr-poller` を緊急停止、orchestrator に通知

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `.claude/locks/pr-poller.lock` の stale 検出 (60 分超過は orchestrator 通知)
- **GitHub Actions**: merged PR について N 日以内に learning ファイルが起票されているか check (R-12 ロスト検出)
- **`pr-poller` Skill 自体の self-check**: 起動時に `.claude/skills/pr-poller/SKILL.md` の status と本 rule の整合確認

## A3 / A4 で本格化する MVP

- **A3**: Phase 1 (merged PR → pr-retrospective) を Skill 駆動で本格化、現状は手動代替 (PR #117 / #119 / #121 の learning は手動生成)
- **A4**: Phase 2 (Renovate ラベル PR → dependency-upgrade) を Skill 駆動で本格化、CronCreate / ScheduleWakeup の自動起動経路を追加
- **A4**: harness-meta 自動起動閾値の機械化 (本 rule の閾値表を parse)

## Gotchas

- **GitHub Actions では呼ばない** (Claude API コスト回避、ADR 0017)、ローカル Claude Code 内のみで動作
- **gh CLI 失敗時はリトライ + 当該 PR を次回再試行に回す**、無限ループ禁止
- **learning ファイル生成と `harness/learnings-batch-YYYY-WW` ブランチへの push は同時実行** (R-12 ロスト対策、片方の失敗で他方も rollback)
- **`harness-meta` の二重起動防止**: pr-poller が `harness-meta` 自動起動した直後に手動起動された場合、`.claude/locks/harness-meta.lock` で排他制御 (A4 で導入)
- **PII / secrets redaction を必ず通す** (`.claude/rules/pii.md` / `secrets.md`)、`gh pr view` 出力 / CI ログ抜粋 / MCP 結果に注意
- **キャッチアップ動作中の新規 PR**: 優先順位を変えず FIFO で処理、batch 完走後に新規分を次回処理
- **`labels:renovate` 判別** は `dependencies` / `renovate-bot` ラベルも対象 (Renovate 設定変更時に追加可能)

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、GitHub Actions から呼ばない原則)
- `docs/harness/plan.md` §5.3 / §6.2 A4 / R-11 / R-12 / R-35
- `.claude/rules/{retrospective-format,harness-meta-criteria,harness-evolution,roadmap}.md`
- `.claude/skills/{pr-poller,pr-retrospective,harness-meta,dependency-upgrade}/SKILL.md`
- `docs/harness/learnings/INDEX.md` (learning 索引)
