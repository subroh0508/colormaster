---
id: rules-pr-poller
title: pr-poller ローカルポーリング規約
status: stable
last_updated: 2026-05-19
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

- **A3 (完了、PR #167 / #168)**: Phase 1 (merged PR → `pr-retrospective`) + Phase 2 (Renovate ラベル PR → `dependency-upgrade`) を Skill 駆動で本格化
- **A4 (本 PR で本格化)**: 3 系統起動経路 (SessionStart hook / CronCreate / ScheduleWakeup) の実運用稼働 + harness-meta 自動起動閾値の runtime override + dogfood 観測値の記録枠 (`.claude/rules/harness-meta-criteria.md` §実行時パラメータ 参照)
- **A6 (機械化予定)**: 本 rule の閾値表を parse + Gradle カスタムタスクで stale lock 検出 + GitHub Actions で learning 起票検証 (R-12 ロスト検出)

## 実運用稼働 Q&A (A4 で本格化)

A4 PR (本 PR) 以降の実運用で発生し得る well-known 失敗パターンと回避策。`.claude/skills/pr-poller/SKILL.md` §実運用稼働手順 / §Gotchas と整合 (SoT は本 rule、SKILL.md は手順書側)。

### Q1. lock 二重取得 (`mkdir` が EEXIST で失敗する)

**症状**: `mkdir .claude/locks/pr-poller.lock` が `mkdir: cannot create directory '.claude/locks/pr-poller.lock': File exists` で失敗、Phase 1 で skip 判定。

**原因 (well-known)**:

- 直前の `pr-poller` 異常終了で lock が残留 (`acquired_at` が 30 分以内なら正規 skip、超えてたら stale 回収対象)
- 3 系統 (SessionStart / CronCreate / ScheduleWakeup) のいずれかが同時刻起動して片方が EEXIST 失敗
- 別ペイン (orchestrator pane / per-task pane) が同時刻に手動起動

**回避策**:

1. `cat .claude/locks/pr-poller.lock/acquired_at` で取得時刻を確認、30 分以内なら正規 skip (二重起動防止が機能している)
2. 30 分以上前なら stale 判定 → `rm -rf .claude/locks/pr-poller.lock` + `pr-poller` 再起動
3. `cat .claude/locks/pr-poller.lock/route` で起動経路 (manual / cron / wakeup) を確認、想定外の経路なら orchestrator (subroh0508) に通知

### Q2. CronCreate 起動で `gh auth status` 失敗 (環境ロード未済)

**症状**: CronCreate で起動した `pr-poller` の Phase 2 で `gh pr list` が `gh: To get started with GitHub CLI, please run: gh auth login` で失敗、連続 5 件失敗で緊急停止。

**原因 (well-known)**:

- Claude Code routine の起動時に `gh` の token cache が読み込まれていない (親シェルの環境変数継承漏れ)
- GitHub token が TTL 切れ (90 日ローテーション、`.claude/rules/secrets.md` §ローテーション 参照)

**回避策**:

1. Skill の Phase 2 冒頭で `gh auth status` を最初に走らせて token 有効性を確認 (失敗時は連続 5 件失敗扱いで緊急停止経路に乗せる)
2. CronCreate routine の cron 式起動コマンドに `gh auth login` を前置せず、**ローカル Claude Code セッション内で 1 度認証を済ませる** (Claude Code 内 OAuth は `.claude/oauth-tokens*` に保存、`.gitignore` で除外)
3. TTL 切れ検出時は `docs/runbooks/secrets-rotation.md` の手順で再認証 (A6 で本格運用)

### Q3. dispatch 重複 (3 系統が同 lookback で重なる)

**症状**: SessionStart hook で起動した直後に CronCreate routine が 09:00 JST 起動、同じ未処理 PR# を 2 回 `pr-retrospective` に dispatch して learning ファイルが重複生成される。

**原因 (well-known)**:

- `.claude/locks/pr-poller.last-run` が前者で更新される前に後者が起動 (lock 排他は機能していても last-run の前進判定が間に合わない)
- `docs/harness/learnings/*.md` の dedup が「ファイル存在チェック」のみで、in-flight (生成中) ファイルを処理済 set に含めていない

**回避策**:

1. **Phase 1 lock 排他で防御**: 後発側が `mkdir` EEXIST で 30 分 no-op、Phase 5 で前者が last-run 更新するまで待つ
2. **Phase 5 で `.claude/locks/pr-poller.last-run` を lock 解放前に書く** (`.claude/skills/pr-poller/SKILL.md` §Phase 5 順序が SoT)、次回起動時の lookback 起点を確実に前進させる
3. **dedup を 2 経路で行う**: `docs/harness/learnings/*.md` ファイル列挙 + `.claude/locks/pr-poller.processed-cache` の直近 30 日分の処理済 PR# 記録 (`SKILL.md` §Phase 3 1-2)

### Q4. harness-meta 二重起動 (pr-poller 自動起動 + 人間手動起動)

**症状**: 未処理 learning が閾値 10 件を超えて `pr-poller` が `harness-meta` を自動起動した直後に、orchestrator (subroh0508) が手動で `harness-meta` を起動 → `harness-meta` の改修 PR 起票処理が衝突して二重 PR 起票や提案セクション parse の race condition が発生。

**原因 (well-known)**:

- `harness-meta` 側の lock 排他 (`.claude/locks/harness-meta.lock`) が pr-poller 側からは見えず、pr-poller が「閾値超過 → 即起動」した直後に人間が二重起動
- `harness-meta-criteria.md` §実行時パラメータ の `harness_meta_min_interval_hours` (24h) が「pr-poller 自動起動」のみ参照、人間手動起動は無視

**回避策**:

1. **`harness-meta` Skill 側で `.claude/locks/harness-meta.lock` 排他制御を担う**: pr-poller は「起動を試みる」だけで成否を問わない (`.claude/skills/pr-poller/SKILL.md` §Phase 4-4)
2. **人間手動起動時も `harness-meta-criteria.md` §連続実行間隔 (24h) を確認**: orchestrator (subroh0508) が手動起動する前に `git log -- .claude/rules/harness-meta-criteria.md | head` で前回実行時刻を確認、24h 未満なら起動見送り or `harness_meta_min_interval_hours=6` 等の上書きを明示
3. **pr-poller が `harness-meta` 起動結果を handoff サマリに記録**: 「起動済」「skip (lock 競合)」「skip (閾値未到達)」の 3 値で出力、人間手動起動判断の材料化

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
