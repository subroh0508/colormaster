# .claude/locks/ — ハーネス Skill の排他制御ディレクトリ

> ローカル Claude Code 内で動く Skill (`pr-poller` / `harness-meta` 等) が二重起動を防ぐための
> ロックファイル / pid file / 前回実行時刻キャッシュを置く場所。SoT は `.claude/rules/pr-poller.md`。

## ロック形式

- **lock 本体**: `.claude/locks/<skill-name>.lock/` (ディレクトリ、mkdir 方式で原子的に取得)
  - 配下に `pid` (取得 PID) / `acquired_at` (ISO8601) / `route` (起動経路 `manual` / `cron` / `wakeup`) を平文配置
  - **stale 判定**: 取得から N 分以上経過した既存 lock は `rm -rf` で回収して再取得 (N は Skill ごとに既定、`pr-poller` は 30 分)
- **last-run キャッシュ**: `.claude/locks/<skill-name>.last-run` (前回正常完了時刻、ISO8601 1 行) — 次回起動時の `lookback` 起点
- **processed-cache**: `.claude/locks/<skill-name>.processed-cache` (直近 30 日分の処理済 PR# などを 1 行 1 項目で記録、dedup 高速化用)

## commit 方針

- ディレクトリ自体は `.gitkeep` で空コミット維持 (Skill が `mkdir` する前にディレクトリが存在するため)
- **配下のファイルは commit しない** (`.gitignore` 対象): `.claude/locks/*.lock` / `.claude/locks/*.last-run` / `.claude/locks/*.processed-cache` は手元固有の実行状態であり、共有すると別マシンで誤動作する
- `.gitignore` 既定パターン未登録の場合、本 README の追加と同 PR / 次 PR で `.gitignore` を更新する

## 関連

- `.claude/rules/pr-poller.md` §排他制御 (SoT)
- `.claude/skills/pr-poller/SKILL.md` §Phase 1 Lock 取得
- ADR-0017 (ローカル Claude Code ポーリング駆動)
