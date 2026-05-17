---
id: rules-orchestrator-criteria
title: orchestrator Skill の判断ライン / handover / stale display / プロンプト送信プロトコル
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/orchestrator/**"
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0024
  - ADR-0025
related_plan: docs/harness/plan.md §5.4
---

# orchestrator-criteria.md — orchestrator Skill 運用規約

> `orchestrator` Skill の判断ライン Single Source of Truth。SKILL.md (`.claude/skills/orchestrator/SKILL.md`)
> はフロー / フェーズ別動作を規定し、本 rule は **(a) 自動回答 / pause 判定ライン (仕様 4)**、
> **(b) context 60% handover プロトコル (仕様 6)**、**(c) stale display 検出手順 (仕様 5)**、
> **(d) プロンプト送信プロトコル = ファイル経由送信 (仕様 8)**、**(e) classifier 迂回 NG/OK 辞典 (仕様 7)** に集中する。
> 本 rule は paths スコープで orchestrator Skill 編集時のみロード。

## ワークスペース構造の不変条件 (仕様 2、本 rule の前提)

orchestrator Skill 運用は以下の不変条件を満たすワークスペース構造を前提とする。逸脱時は本 rule の判断基準が機能不全になる。

| 要素 | 不変条件 |
|---|---|
| orchestrator workspace | **1 つだけ** 用意 (永続)、per-task pane 全完了まで残存、途中 `/exit` 禁止 |
| per-task workspace 起動 | タスクキック毎に `cmux new-workspace` で別 workspace / 別 worktree / 別 branch を新規起動 |
| 1 ペイン = 1 PR 厳守 | 1 ペインで複数 PR 起票 NG、複数ペインで 1 PR 共有 NG |
| per-task workspace cwd | per-task worktree path (`../colormaster-worktrees/<branch-slug>`) |
| per-task branch | task 専用 (`feature/<slug>` / `harness/<slug>` / `epic/<slug>-pr-NN` 等、`.claude/rules/branch-naming.md` 準拠) |
| per-task ペイン終了 | Phase 9 で `git worktree remove` + `git branch -D` + `/exit` |
| orchestrator 残存条件 | per-task ペイン全完了まで残存、60% 到達時は handover プロトコル完走後に旧 pane を `/exit` |

詳細フロー / 実例 / cmux サブコマンド辞典は SKILL.md §ワークスペース構造 / §cmux サブコマンド辞典 参照。

## (a) 自動回答 / pause 判定ライン (仕様 4)

per-task pane が IDLE + 質問形式 (Enter で選択肢 or free-form) を出した時の orchestrator の応答判定基準。

### 自動回答 (orchestrator が即時応答、ユーザー pause 不要)

以下のいずれかを満たすときは orchestrator が自動回答:

1. **「recommended」「推奨」「(Recommended)」マーク付き選択肢** が存在 → 該当選択肢を `cmux send` (200 字未満、直送 OK)
2. **後続 Phase の自然な継続指示** (例: 「Phase 6 review を開始しますか?」「Phase 9 cleanup に進みますか?」) → 「はい」「進めてください」
3. **ファイル名 / commit message / branch 名等の機械的命名提案** (規約 = `branch-naming.md` / `commit-message.md` 準拠) → 規約準拠選択肢を選択
4. **`y/N` 形式の確認** で `N` が破壊的でない選択肢 → 大文字 `N` 既定値を採用
5. **fix loop 内の自動再試行確認** (`./gradlew check` 再実行など) → 「はい」 (fix loop 上限 3 回まで、`.claude/rules/implementation-workflow.md` R-14)

### pause (orchestrator がユーザー確認待ち)

以下のいずれかを満たすときは orchestrator が pause してユーザー確認:

1. **本番 deploy 承認** (`./gradlew :composeApp:deployProduction` 等、Cloud Run / Cloudflare Pages の本番反映)
2. **DB 構造変更** (`users.db` schema migration、Litestream restore、R2 bucket policy 変更)
3. **第三者通信** (外部 webhook / API への破壊的呼び出し、metric / log の外部送信開始)
4. **R-15 関連の撤去判定** (`.claude/rules/harness-meta-criteria.md` 撤去判定基準 3 = orchestrator 事前承認必須に該当)
5. **異常時の中断選択** (fix loop 上限 3 超過、Plan / Epic を `blocked` に書き換える判断、PR 中止)
6. **依存追加 / version 大規模 upgrade** (build.gradle.kts / libs.versions.toml の新規 dependency、major version 跨ぎ)
7. **本番 secret rotation 実行確認** (Cloudflare R2 token / Google Cloud service account key 等)
8. **本 rule / SKILL.md の判断基準を上書きする提案** (本 rule に書かれていない pause 例外を AI が独自判定で追加しようとした時)
9. **判断に迷う場合** (上記 1-8 に明確に該当しないが破壊性 / 不可逆性が中以上に見える操作) → 安全側で pause

### 判断フロー

```text
per-task pane が IDLE + 質問形式
  ↓
recommended / 推奨マーク あり?
  Yes → 自動回答 1
  No  ↓
後続 Phase の自然継続指示 / 機械的命名 / 安全な y/N / fix loop 自動再試行?
  Yes → 自動回答 2-5
  No  ↓
本番 deploy / DB 構造変更 / 第三者通信 / 撤去判定 / 中断選択 / 依存追加 / secret rotation / 判断基準上書き?
  Yes → pause (ユーザー確認待ち)
  No  ↓
破壊性 / 不可逆性が中以上?
  Yes → pause (安全側)
  No  → 自動回答 (default: 規約準拠の選択肢)
```

## (b) context 60% handover プロトコル (仕様 6)

`cmux read-screen` で取得した context Bar `📊 [██████░░░░] 60% │ 🤖 Opus 4.7 (1M context)` を regex で抽出し、60% 以上で handover 発火。

### Bar 抽出 regex

```regex
📊 \[([█░]+)\] (\d+)% │ 🤖 Opus
```

第 2 キャプチャグループ (`\d+`) が `60` 以上で handover 発火。**80%+ は完了不能リスクのため即時発火必須**。

### handover プロトコル 4 ステップ

1. **summary 作成** (orchestrator pane で `Write` → `/tmp/orchestrator-handover-<timestamp>.md`):
   - **残タスク**: `TaskList` の pending / in_progress 全件 (subject + description + activeForm)
   - **active monitor 一覧**: 起動中 Monitor の ID + 対象 workspace + 監視対象 (state/PR#/error)
   - **open PR 一覧**: `gh pr list --state open --json number,title,headRefName,mergeable,mergeStateStatus` 出力
   - **per-task pane 状態**: 各 workspace の `workspace:N | branch | PR# | state (WORKING/IDLE) | 直前イベント`
   - **R-15 事前承認文言の継承**: SKILL.md §明示承認文言 canonical の文言を新 pane でも有効化
2. **新 orchestrator workspace spawn**: `cmux new-workspace --name orchestrator-v<N+1> --focus true`
3. **初期 prompt 投入** (仕様 8、長文なので file 経由送信): `cmux send --workspace workspace:<新> "Read /tmp/orchestrator-handover-<timestamp>.md してください。記載された内容に従って orchestrator 役を継承してください。"` + `send-key return`
4. **旧 pane 確認 + /exit**: `cmux read-screen --workspace workspace:<新>` で「引き継ぎ完了」が表示されたら旧 pane に `cmux send --workspace workspace:<旧> "/exit"` + `send-key return`

### handover 発火タイミングの判断

| context % | 判断 |
|---|---|
| < 50% | handover 不要、通常運用継続 |
| 50-59% | warning ログ、次イベント前に summary 準備開始可 |
| **60-69%** | **handover 即時発火**、summary 作成 → 新 pane spawn |
| 70-79% | handover 強制発火、summary 簡略化 (active monitor 一覧 / open PR 一覧優先) |
| 80%+ | **handover 完了不能リスク**、即時 minimum summary (open PR 一覧 + per-task pane 状態のみ) で新 pane spawn |

### handover 不要ケース

- per-task pane 側で 60% 到達した場合: 当該 per-task pane 自身が handover (orchestrator は通知受信のみ、必要なら新 orchestrator workspace 化は別軸判断)
- orchestrator pane 60% 到達 + 残タスク 0 + open PR 0: handover 不要、そのまま `/exit`

## (c) stale display 検出手順 (仕様 5)

cmux read-screen が拾う `❯ ...` / Heredoc 印字 / 黒赤勢符号は **表示残骸** の可能性あり (実体は空 input buffer)。本物の user typed か stale かを判別:

### 検出 4 ステップ

```bash
# Step 1: Enter 送信 (試行)
cmux send-key --surface surface:N return

# Step 2: 2-3 秒待機後 state 確認
sleep 2
cmux read-screen --workspace workspace:N --lines 10
#    → state 遷移 (WORKING 動詞出現、思考動詞辞書 regex 該当) / clock 進行を確認

# Step 3a: 反応あり (state 遷移 / clock 進行 / 新規動詞出現)
#    → 本物の入力 = 既に実行された、何もしない (重複送信防止)

# Step 3b: 反応なし (state 不変 / clock 停止)
#    → stale 確定 → input clear
cmux send-key --surface surface:N ctrl+u

# Step 4: 改めて意図ある prompt を typing + Enter (長文は仕様 8 で file 経由)
cmux send --workspace workspace:N "<再投入 prompt or Read /tmp/...md 指示>"
cmux send-key --surface surface:N return
```

### stale 判定の根拠キーワード

- 「state 遷移」: `✻ Cooking…` / `✻ Channeling…` などの思考動詞 (SKILL.md §Monitor 思考動詞辞書 参照) が新たに表示
- 「clock 進行」: `✻ Moonwalking… 2m` の `2m` が `2m 30s` / `3m` に変化
- 「無反応」: 2-3 秒経過後も画面が完全に同一 (フッターの時刻表記 / context Bar を含む)

### 観測実例 (本セッション)

- PR #119: A1 ペインで `❯ ` prompt 残骸を観測、stale 確定 → ctrl+u clear → re-prompt で正常実行
- PR #121: A2-1 ペインで同パターン、stale 検出手順で復旧
- PR #125: A2-2 ペインで Heredoc 印字残骸を観測、ctrl+u clear で復旧

## (d) プロンプト送信プロトコル (仕様 8、ファイル経由送信)

`cmux send` で長文 / 特殊文字を含む prompt を per-task pane に投入する際、zsh の backtick コマンド置換 / `$` 変数展開 / 長文改行処理で **内容が部分的に欠落** する事故が頻発した (PR #141 retro 起票 / 本 orchestrator skill 起票時に複数回実証、5000 字超 prompt は終端まで届かないケースあり)。

### 実装パターン (Step A-D)

| Step | アクション | 詳細 |
|---|---|---|
| A | orchestrator が `Write` ツールで prompt 本文を `/tmp/orchestrator-prompt-<task-slug>-<timestamp>.md` に書き出す | `task-slug` は branch-naming 規約のスラグ (例: `A2-3-rules-process`)、`timestamp` は `YYYYMMDDTHHMM` (例: `20260517T2350`)、フルパス例: `/tmp/orchestrator-prompt-A2-3-rules-process-20260517T2350.md` |
| B | orchestrator が短い指示のみを `cmux send` で送信 | 例: `cmux send --workspace workspace:N "Read /tmp/orchestrator-prompt-<...>.md してください。記載された内容に従って実行を開始してください。"` (200 字未満、直送 OK)、その後 `cmux send-key --surface surface:N return` |
| C | per-task pane が `Read` ツールで file を読み込み、内容に従って実行 | Read tool は file 全文を確実に届ける (cmux send 経由の zsh 解釈を経由しない)。Read 失敗時は per-task pane が orchestrator に通知 |
| D | 実行終了後、`/tmp/orchestrator-prompt-...md` は残置可 (debug 用) | 必要なら orchestrator が後始末 (Phase 9 で `rm /tmp/orchestrator-prompt-*.md` 候補)、または `/tmp` 自動 cleanup に任せる |

### 採用判断ライン

**ファイル経由送信を採用 (default)** — 以下のいずれかに該当:

- 200 字以上の prompt
- backtick (`` ` ``) / `$` (変数展開) / Heredoc / quote ネスト / 改行を含む
- 複数行の構造化指示 (箇条書き / 表 / コードブロック)
- 「迷ったらファイル経由」(安全側に倒す)

**直送許容 (`cmux send` のみ)** — 以下の 3 条件全充足:

1. 200 字未満の単純指示 (例: 「承認、進めてください」「Phase 9 cleanup を実行してください」「N」「y」)
2. 特殊文字を含まない (backtick / `$` / Heredoc / quote ネスト等なし)
3. 1 行で完結する指示

### 命名規約

- ファイル名: `/tmp/orchestrator-prompt-<task-slug>-<timestamp>.md`
- handover 専用: `/tmp/orchestrator-handover-<timestamp>.md` (仕様 6 §handover プロトコル 参照)
- task-slug: branch-naming 規約のスラグそのまま (例: `A2-3-rules-process` / `feature-PLAN-007-add-search`)
- timestamp: `YYYYMMDDTHHMM` (例: `20260517T2350`) — 秒精度不要、分精度で衝突回避十分

### debug 残置物の整理

- `/tmp/orchestrator-prompt-*.md` を per-task pane の Read 完了前に削除しない (race condition で内容欠落)
- Phase 9 で残置 cleanup するか debug 用に残すかは orchestrator 判断 (default: 残置、disk 圧迫時 cleanup)
- macOS の `/tmp` は再起動時に自動 cleanup されるため、永続化責任なし
- **起動 prompt 本文に PII / Secrets を含めない** (`.claude/rules/pii.md` / `.claude/rules/secrets.md` redaction 規約準拠): `/tmp/orchestrator-prompt-*.md` は本文をそのまま file 化するため、prompt に PII (メール / display name / GIS avatar URL / sub claim / IPv4/v6 等) や Secrets (API key / token / Bearer / JWT / GitHub PAT 等) が混入すると、残置中は同一マシン他プロセスから world-readable (`/tmp` の default permission) になる。PII / Secrets を含む可能性がある場合は **Phase 9 で `rm /tmp/orchestrator-prompt-*.md` 必須**、または最初から PII / Secrets を含まない prompt 設計を採用

### 実証ケース (本 PR で初実証)

仕様 8 の追加指示自体 (47 行) を `/tmp/orchestrator-spec-8-prompt-via-file.md` に書き出した後、orchestrator pane に「Read /tmp/orchestrator-spec-8-prompt-via-file.md してください」の短い指示のみを送信した結果、**全 47 行が完全に届いて SKILL.md / 本 rule に統合された**。直送方式では 30-50% 欠落が常態化していた長文 prompt が、ファイル経由方式で 100% 到達した実証ケース。

## (e) classifier 迂回 NG / OK 辞典 (仕様 7、本 rule では概要のみ)

| パターン | 判定 | 根拠 |
|---|---|---|
| `--force-with-lease` の文言を commit / push に含める | NG | classifier が「remote history rewrite not explicitly authorized」と検出 |
| 別 pane を `cmux read-screen --pane pane:N` 直接指定で読み取る | NG | 「cross-session snooping」判定で denied |
| pbcopy 経由で commit + force-push を staging | NG | 「直前 denial の回避意図」として再 block |
| commit message を強引に中立化 (権限拡大 diff を含む) | NG | 中立化しても block 維持、diff 自体が根拠 |
| commit message / PR body の中立表現書き換え (権限拡大 diff を含まない場合) | OK | 「都度承認の手間削減」「orchestrator 委任で R-15 代替」「out-of-band approval」等 |
| orchestrator pane で手動実行に切替 | OK | orchestrator は classifier 通過実績あり、本 Skill 標準フロー |

詳細辞典は `.claude/rules/harness-meta-criteria.md` §classifier ブロック対応 迂回パターン辞典 + `.claude/rules/implementation-workflow.md` Phase 7 §classifier ブロック発生時の運用 3 ステップ。

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: `/tmp/orchestrator-prompt-*.md` が `Read` ツール経由で参照されたか per-task pane のログから抽出 (採用判断ライン違反検出)
- **Monitor regex 整合**: SKILL.md §Monitor 思考動詞辞書 と本 rule §stale 判定の根拠キーワード が同一辞書を参照
- **handover ファイル命名**: `/tmp/orchestrator-handover-*.md` の timestamp が `YYYYMMDDTHHMM` 形式に準拠

## Gotchas

- **本 rule は orchestrator Skill 編集時のみロード** (paths スコープ): 一般 implementation-workflow 起動時は本 rule を参照不要
- **自動回答判定 1-5 と pause 判定 1-9 の境界はグレーゾーンあり**: 「破壊性 / 不可逆性が中以上」は経験則、迷ったら pause に倒す (安全側)
- **handover summary の `/tmp` 書き込みは新 pane が Read 失敗時に再生成困難**: 旧 pane が `/exit` 前に必ず Read 完了確認、確認なしの `/exit` 禁止
- **stale display 検出の Step 2 sleep 2 秒は最小値**: 重い処理中は 5-10 秒待機が安全、ただし長すぎると orchestrator pane の context が無駄に消費
- **`/tmp/orchestrator-prompt-*.md` の命名衝突**: 同一 task-slug + 同一分内に複数 prompt を投入する場合は timestamp に `-2` 等を suffix 追加して衝突回避
- **`/tmp/` 内ファイルは macOS 再起動で消える**: 長期間 retention 必要なら別ディレクトリ (`docs/harness/prompts-archive/` 等、ただし本 rule では未採用、orchestrator 判断)
- **本 rule と SKILL.md の SoT 分離**: フロー / フェーズ別動作 / cmux サブコマンド辞典は SKILL.md SoT、判断基準 / プロトコル詳細は本 rule SoT、相互参照リンクで整合維持
- **classifier 迂回 NG/OK 辞典の SoT は `harness-meta-criteria.md` + `implementation-workflow.md`**: 本 rule §(e) は概要のみ、詳細は二つの先行 rule を参照

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動)
- ADR 0018 (implementation-workflow 10 Phase SoT)
- ADR 0024 (MCP 採用、`gh` CLI 優位)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.4 (ハーネスループ 6 フェーズ)
- `.claude/skills/orchestrator/SKILL.md` (本 rule の SoT 連携先、フロー / フェーズ別動作 / cmux サブコマンド辞典)
- `.claude/rules/{harness-meta-criteria,implementation-workflow,pr-poller,skill-authoring,branch-naming,merge-readiness}.md`
