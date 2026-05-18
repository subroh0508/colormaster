---
name: pr-poller
description: |
  ローカル Claude Code 内でポーリング起動し、gh CLI で merged/closed PR を取得 →
  未処理 PR があれば pr-retrospective を起動、Renovate ラベル PR があれば
  dependency-upgrade を起動するハーネスループ起点 Skill。3 系統の起動経路 (起動時 +
  CronCreate + ScheduleWakeup) に対応するため .claude/locks/pr-poller.lock で排他制御し、
  pending-fetch 項目の再走査と harness-meta 自動起動の閾値判定も担当する。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §5.3 / §5.4 / §6.2 A3 / §6.2 A4
related_rules:
  - .claude/rules/pr-poller.md
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/retrospective-format.md
  - .claude/rules/mcp-usage.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
  - .claude/rules/roadmap.md
related_adrs:
  - ADR-0017
  - ADR-0024
  - ADR-0026
---

# pr-poller

> **5 行以内 summary**: ローカル Claude Code 内でポーリング起動し、`gh` CLI で merged/closed PR を
> 取得 → 未処理 PR があれば `pr-retrospective` を起動、`labels:renovate` 系 open PR があれば
> `dependency-upgrade` を起動する Skill。3 系統の起動経路 (起動時 / CronCreate / ScheduleWakeup)
> を `.claude/locks/pr-poller.lock` の mkdir 方式で排他制御し、`harness-meta` 自動起動閾値判定と
> `roadmap-tracker` pending-fetch 再走査も同 Phase 内で実施する (R-11 / R-12 / R-35、ADR-0017)。

## 役割

- **ハーネスループ起点**: Spec Gen → Implementation → Evaluation → Merge → **Retrospection** → Meta の Retrospection 入口を担い、後続 Skill (`pr-retrospective` / `dependency-upgrade` / `harness-meta`) を起動する責務単位
- **未処理 PR 検出 + pr-retrospective 起動**: `gh pr list --state merged,closed` の取得結果に対し `docs/harness/learnings/*.md` の存在で dedup → 未処理 PR があれば 1 件ずつ `pr-retrospective` Skill を起動 (R-12 ロスト対策の learning 生成 + push を後続 Skill が担当)
- **Renovate ラベル open PR 検出 + dependency-upgrade 起動**: `gh pr list --state open --label renovate` 等で Renovate 系 PR を取得 → 各 PR の number を `dependency-upgrade` Skill に渡す (R-37 / ADR-0017)
- **harness-meta 自動起動閾値判定**: 未処理 learning 件数 (デフォルト 10 件) / 前回 `harness-meta` 実行からの経過 (デフォルト 7 日) を `harness-meta-criteria.md` §pr-poller 起動閾値 から読込み、超過時に `harness-meta` を起動
- **roadmap-tracker pending-fetch 再走査**: `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の `<!-- evidence:pending-fetch -->` コメント有項目を再 `gh pr view`、成功時は完了根拠を埋める (R-35)
- **GitHub Actions では呼ばれない** (ADR-0017): 起動経路はローカル Claude Code 内のみ、CI 上での Claude API 呼び出しは禁止

責務境界: 本 Skill 自身は learning ファイル / PR コメント / Plan / Epic を **生成しない** (後続 Skill 担当)。本 Skill は「対象 PR を見つける + 排他制御 + 後続 Skill を呼ぶ + 起動結果を集計報告する」だけに専念する。

## 入力

- **起動経路 (auto-detect)**: 3 系統のいずれか
  - **manual (起動時)**: ローカル Claude Code 起動直後 / 人間 prompt 「pr-poller 走らせて」「未処理 PR を回収して」等
  - **cron (CronCreate)**: Claude Code routine の日次 09:00 JST 起動 (A4 で導入予定、本 Skill 側は経路識別子を受け取って動作差分)
  - **wakeup (ScheduleWakeup)**: `/loop` Skill 等から前回処理 N 時間後の自動再起動 (A4 導入予定、pending-fetch 再走査を優先実行)
  - 起動経路は引数 (`route=manual|cron|wakeup`) で渡される、未指定なら **manual** と扱う
- **lookback 期間** (default `24h`): `gh pr list --search "merged:>$LAST_RUN"` の検索範囲。`$LAST_RUN` は `.claude/locks/pr-poller.last-run` に記録、ファイル不在時は `lookback` から逆算 (`now - 7d` をフォールバック上限)
- **dry-run flag** (default `false`): true なら `gh pr list` のみ実行、後続 Skill 呼び出し + learning push + harness-meta 起動を skip + 標準出力に「起動予定 Skill リスト + 対象 PR#」を出す
- **harness-meta 起動閾値** (引数 or `harness-meta-criteria.md` 由来):
  - `unprocessed_learnings_threshold` (default 10 件)
  - `harness_meta_interval_days` (default 7 日)
  - `harness_meta_min_interval_hours` (default 24 時間)
- **lock 設定**: stale 判定閾値 (default 30 分)、PID file `.claude/locks/pr-poller.lock` 配下

## 出力

- **起動した Skill リスト + 処理済 PR# キャッシュ**: 標準出力に handoff サマリを 1 ブロックで出す
  ```text
  pr-poller 完了
  - 起動経路: manual / cron / wakeup
  - lookback: 24h (LAST_RUN: 2026-05-17T09:00:00+09:00)
  - merged/closed 検出: N 件 → pr-retrospective dispatch: M 件 (skipped: K 件 [既存 learning あり])
  - Renovate open 検出: N 件 → dependency-upgrade dispatch: M 件
  - pending-fetch 再走査: N 件 → 解消: M 件 (残: K 件)
  - harness-meta 自動起動: 起動 / skip (理由)
  - lock: 取得 → 解放 (経過 X 秒)
  ```
- **`.claude/locks/pr-poller.lock`** (mkdir 方式): ロックディレクトリ内に `pid` / `acquired_at` / `route` を平文ファイルで配置、正常終了時に削除
- **`.claude/locks/pr-poller.last-run`** (commit しない、`.gitignore` 対象): 前回正常完了時刻を ISO8601 で保存、次回 lookback の起点
- **`.claude/locks/pr-poller.processed-cache`** (commit しない、`.gitignore` 対象): 直近 30 日分の処理済 PR# を 1 行 1 PR で記録、dedup 高速化用 (`docs/harness/learnings/*.md` ファイル列挙のフォールバック)
- **本 Skill が生成しない** (責務境界):
  - learning ファイル → `pr-retrospective` 担当
  - PR コメント / Plan / Epic → `dependency-upgrade` / `plan-author` / `epic-author` 担当
  - 改修 PR → `harness-meta` 担当
  - 実 merge / approve ラベル付与 → 人間 approve (R-15、`merge-readiness.md` 3 条件)

## フェーズ別動作 (5 Phase)

### Phase 1: Lock 取得

- `.claude/locks/pr-poller.lock` を **mkdir 方式** で排他取得 (POSIX atomic、flock 非対応環境でも動作、pid file との二重チェック不要)
  ```bash
  mkdir .claude/locks/pr-poller.lock 2>/dev/null
  ```
  - mkdir 採用理由: シェルレベルで原子性が保証され、Claude Code がローカル shell で動かすときに最も移植性が高い (BSD `flock` 非対応、Linux/macOS 両対応、`mkdir` の EEXIST が排他保証)
  - 失敗時 (既存 lock あり) は lock 内の `acquired_at` を読んで stale 判定
- **stale 判定**: 既存 lock の `acquired_at` が **30 分以上前** (default、引数 `stale_threshold_minutes` で上書き可) なら stale と判定し、`rm -rf .claude/locks/pr-poller.lock` + 再 mkdir + warning ログ
- **30 分以内なら no-op で終了** (二重起動防止)、handoff メッセージに「既存 lock 検出、N 分以内なので skip」を 1 行で出力
- 取得成功時、lock 内に以下を配置:
  - `.claude/locks/pr-poller.lock/pid` (現在の PID)
  - `.claude/locks/pr-poller.lock/acquired_at` (ISO8601 タイムスタンプ)
  - `.claude/locks/pr-poller.lock/route` (`manual` / `cron` / `wakeup`)
- 異常終了で残った lock は次回 stale 判定で自動回収される設計 (cleanup hook 不要)

### Phase 2: 対象 PR 取得 (gh CLI)

`$LAST_RUN` を `.claude/locks/pr-poller.last-run` から読み込み (不在時は `now - 7d`)。`lookback` 引数で上書き可。`gh` CLI は **手動 git grep より優先 / GitHub MCP より優先** (ADR-0024)。

- **merged/closed PR** (Retrospection 対象):
  ```bash
  gh pr list --state merged --search "merged:>$LAST_RUN" --limit 50 \
    --json number,title,mergedAt,labels,author,baseRefName,headRefName
  gh pr list --state closed --search "closed:>$LAST_RUN" --limit 20 \
    --json number,title,closedAt,labels,author,baseRefName,headRefName
  ```
  - `closed` は `merged` で取得できない (人間が close した未マージ PR) 分の取りこぼし対策、件数は少なめに絞る
- **Renovate ラベル open PR** (dependency-upgrade 対象):
  ```bash
  gh pr list --state open --label renovate --limit 30 \
    --json number,title,labels,author,headRefName
  gh pr list --state open --label dependencies --limit 30 --json number,title,labels,author,headRefName
  gh pr list --state open --label renovate-bot --limit 10 --json number,title,labels,author,headRefName
  ```
  - 3 ラベルのいずれか付与で対象 (`.claude/rules/pr-poller.md` Phase 2 検出ロジック準拠)、UNION して dedup
- **gh CLI 失敗時のリトライ**: 指数バックオフ (1s → 2s → 4s)、最大 3 回。3 回失敗したら当該クエリを次回再試行に回し warning ログ + 連続 5 件失敗で本 Skill を緊急停止 + orchestrator 通知 (R-11)

### Phase 3: ラベル / 未処理判定 + dedup

- **merged/closed PR の未処理判定**:
  1. `docs/harness/learnings/*.md` を ls → frontmatter `related_pr: NNN` または ファイル名 `YYYY-MM-DD-pr-<N>.md` の `<N>` を抽出して処理済 set を構築
  2. `.claude/locks/pr-poller.processed-cache` も参照 (直近 30 日分、ファイル列挙のショートカット)
  3. Phase 2 で取得した PR# から処理済 set を差し引いて未処理リスト確定
- **Renovate ラベル PR の判定**:
  - `author.login == "renovate[bot]"` または `app/renovate` の場合は確実に Renovate PR
  - 人間が手動で `renovate` ラベルを付けた PR (Renovate 化途中) は author が異なるため、`dependency-upgrade` 側で再確認させる (本 Skill では「Renovate ラベルがあれば dispatch する」だけに留め、誤検出は下流が判断)
- **キャッチアップ動作 (R-11)**: `$LAST_RUN` が **3 日以上前** なら「最後の処理から経過した PR」を最優先で FIFO 処理。件数が 30 件超なら orchestrator に warning 通知 + 古い順から 10 件ずつ batch (本 Phase 内では並べ替えのみ、Phase 4 で dispatch)

### Phase 4: 後続 Skill dispatch

dispatch は順次 / 直列 (並列起動は Phase 4 内では行わない、各 Skill の touch ファイル衝突 / lock 衝突を予防):

1. **未処理 merged/closed PR 1 件ごとに `pr-retrospective` 起動**: 引数 `pr_number=<N>` を渡す。各 Skill の終了を待ち、learning ファイル生成 + `harness/learnings-batch-YYYY-WW` ブランチ push の成否を確認 (失敗時は次回再試行に回す)
2. **Renovate open PR 1 件ごとに `dependency-upgrade` 起動**: 引数 `pr_number=<N>` を渡す。各 Skill が `gh pr comment` + Plan / Epic 起票で完結する (本 Skill は完了報告だけ集約)
3. **pending-fetch 再走査** (`roadmap-tracker` の `<!-- evidence:pending-fetch -->`):
   - `docs/harness/roadmap.md` と `docs/epics/*/roadmap.md` を grep → pending PR# を抽出
   - 各 PR# に対し `gh pr view <N> --json mergedAt,state` を再試行
   - 成功時 (state=MERGED かつ mergedAt あり) は `roadmap-tracker` を起動して完了根拠を埋める / コメント削除 (R-35)
4. **harness-meta 自動起動閾値判定**:
   - 本セッションで生成された未処理 learning 件数 + 既存未処理 learning 件数を `harness-meta-criteria.md` §pr-poller 起動閾値 と比較
   - 未処理 learning >= 10 件 **または** 前回 `harness-meta` 実行から >= 7 日 **かつ** 最小実行間隔 24 時間を超えていれば `harness-meta` を起動
   - 起動時は `.claude/locks/harness-meta.lock` 排他制御 (`harness-meta` 側で実装、二重起動防止)
5. **dry-run mode の場合**: dispatch を全て **skip** + handoff サマリだけ出力 (Skill 起動なし、副作用なし)

### Phase 5: Lock 解放 + last-run 更新

- `.claude/locks/pr-poller.last-run` に **現在時刻 (ISO8601)** を上書き (lock 解放前に書くことで、次回起動時のキャッチアップ起点が確実に更新される)
- `.claude/locks/pr-poller.processed-cache` に本 Phase で処理した PR# を追記 (直近 30 日超の行は trim)
- `rm -rf .claude/locks/pr-poller.lock` で lock 解放
- handoff サマリを §出力 のフォーマットで標準出力に出す
- 異常終了時 (Phase 1-4 のいずれかで unrecoverable error): lock を解放してから orchestrator 通知 + 当該 PR を次回再試行に回す (lock 残留は次回 stale 判定で自動回収されるため強制不要)

## Gotchas

- **3 系統起動経路の二重起動防止**: `.claude/locks/pr-poller.lock` mkdir 排他で防御。`CronCreate` の 09:00 起動と `ScheduleWakeup` の前回 +N 時間起動が偶発的に重なる場合があり、後発側は 30 分以内 no-op で skip + 次サイクルに回す。stale 30 分は経験値で、長時間 dispatch を想定する場合は引数で延長可
- **Lock 取得失敗時のフォールバック**: `mkdir` が `EACCES` (権限) で失敗するケース (worktree 移動直後 / `.claude/locks/` 未作成) では handoff 「lock 取得不能、`.claude/locks/` の存在 + 書込権限を確認」を出力して即時終了。retry 無限ループ禁止
- **CronCreate と ScheduleWakeup の責務分離**: cron は「日次まとめて」、wakeup は「短時間の追従」。同 lookback で重複 dispatch しないよう `.claude/locks/pr-poller.last-run` で前進判定 (`mergedAt > LAST_RUN` を必ず通す)
- **Renovate ラベル PR の commit author 判定**: `renovate[bot]` (= App) と「人間が Renovate ラベルを手動付与したケース」が混在する。本 Skill は **ラベル付与のみで dispatch** を決め、author 確認 / 妥当性判定は `dependency-upgrade` 側に委ねる (本 Skill が誤検出を判断しない、責務境界を狭める)
- **PII / Secrets redaction を必ず通す** (R-26 / `.claude/rules/pii.md` / `.claude/rules/secrets.md`): handoff サマリ + processed-cache + last-run には PR title / author display name を含めない (PR# + メタ情報のみ)。`gh pr view --json` の `body` フィールドは本 Skill で直接扱わず後続 Skill に渡す (redaction 責務を `pr-retrospective` / `dependency-upgrade` 側に集約)
- **`.claude/locks/*.last-run` / `*.processed-cache` を絶対 commit しない**: `.gitignore` で除外 (`.claude/locks/README.md` 参照)。lock ディレクトリ自体は `.gitkeep` で空コミット維持し、配下ファイルは ignore
- **gh CLI 失敗時の連鎖停止**: 連続 5 件 (` gh pr list` 自体の不調) で本 Skill を緊急停止 + orchestrator 通知 (R-11)。retry 無限ループは資源浪費 + classifier 誤検知の原因
- **キャッチアップ動作中の新規 PR**: $LAST_RUN が 3 日以上前のキャッチアップ batch 処理中に、新たな merged PR が発生しても優先順位を変えず FIFO で処理 (古い PR の learning が後回しになると R-12 の趣旨 = レトロ取りこぼし防止 と矛盾)。batch 完走後の次サイクルで新規分を処理
- **GitHub Actions では呼ばない** (ADR-0017): 本 Skill 起動経路はローカル Claude Code 内のみ。`.github/workflows/pr-poller.yml` 等の workflow は作らない (Claude API コスト回避 + PII 漏洩経路集約、§5.4 / R-37)
- **harness-meta との二重起動防止**: 本 Skill が `harness-meta` を自動起動した直後に人間が手動起動するケースがある → `.claude/locks/harness-meta.lock` の排他制御を `harness-meta` Skill 側で担う。本 Skill では「閾値超過したら起動を試みる」だけで成否は問わない
- **dry-run と本走の取り違え防止**: dry-run flag が true でも `.claude/locks/pr-poller.lock` は取る (二重 dry-run も防ぐ)。ただし `.claude/locks/pr-poller.last-run` は **更新しない** (次回本走時の lookback を壊さない)
- **`harness-evolution` は本 Skill から起動しない** (ADR-0026): 外部研究駆動は手動起動のみ。本 Skill から自動起動すると内部 KPT (`harness-meta`) と外部研究の責務境界が曖昧化する
- **classifier 迂回 NG 表現の回避** (`.claude/rules/harness-meta-criteria.md` §classifier ブロック対応): handoff サマリ / commit message / PR description に「auto-merge」「self-merge」「force-merge」を書かない、「人間 approve 待ち」「orchestrator 委任」等の中立表現を使う

## 関連

- ADR-0017 (ローカル Claude Code ポーリング駆動、GitHub Actions で Claude API を呼ばない原則)
- ADR-0024 (`gh` CLI 採用、GitHub MCP 不採用、PR 操作の SoT)
- ADR-0026 (`harness-evolution` は手動起動のみ、本 Skill から自動起動しない根拠)
- `docs/harness/plan.md` §5.3 (Skill 責務一覧) / §5.4 (ハーネスループ Retrospection 入口) / §6.2 A3 / §6.2 A4 (本格化フェーズ)
- `.claude/rules/pr-poller.md` (3 系統起動経路 + 排他制御 + 検出ロジック SoT)
- `.claude/rules/harness-meta-criteria.md` §pr-poller 起動閾値 (10 件 / 7 日 / 24h の上書き可)
- `.claude/rules/retrospective-format.md` (`pr-retrospective` 生成フォーマット、本 Skill が dispatch する先)
- `.claude/rules/mcp-usage.md` (`gh` CLI 優位、Context7 / JetBrains MCP の使い分け)
- `.claude/rules/{pii,secrets}.md` (handoff / cache の redaction)
- `.claude/rules/roadmap.md` (pending-fetch 再走査の対象、R-35)
- `.claude/skills/pr-retrospective/SKILL.md` (dispatch 対象、A3-10 で本格化)
- `.claude/skills/dependency-upgrade/SKILL.md` (dispatch 対象、A3-7 完了)
- `.claude/skills/harness-meta/SKILL.md` (閾値超過時の自動起動先)
- `.claude/skills/roadmap-tracker/SKILL.md` (pending-fetch 再走査時に起動)
- `.claude/skills/implementation-workflow/SKILL.md` (Phase 8 で本 Skill を即時起動する上位フロー)
- `.claude/locks/README.md` (lock ディレクトリ運用ルール)
