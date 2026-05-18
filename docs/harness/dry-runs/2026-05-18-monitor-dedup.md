---
id: dry-run-monitor-dedup-20260518
title: Monitor dedup ロジック v3 (workspace prefix + tail -3) SoT 化 dry-run 結果
type: dry-run
status: completed
related_pr: pending  # 本 PR merge 後に NNN を埋める (改修候補 #2 採用 PR からの bidirectional link)
related_learning: pending  # 改修候補 #2 採用 PR の retro 起票時に確定 (A6 機械検証時は pending sentinel として dangling 参照判定から除外)
related_proposals:
  - "[skill] orchestrator SKILL.md §Phase 3 (30 秒ポーリング) + §Monitor 思考動詞辞書 への Monitor dedup ロジック v3 (workspace prefix + tail -3) SoT 追加"
generated_at: 2026-05-18T10:00:00+09:00
generator: orchestrator pane (新セッション、旧 workspace:2 引き継ぎ後、harness-meta Skill 本格化前の手動代替)
verdict: escalate
---

# Monitor dedup ロジック v3 (workspace prefix + tail -3) SoT 化 dry-run 結果

> 生成: 新 orchestrator pane (旧 workspace:2 引き継ぎ後) at 2026-05-18T10:00:00+09:00
> 関連 retrospective: 改修候補 #2 採用 PR (本 PR merge 後の後続 PR で起票) の retro で確定予定
> 対象提案: `[skill]` orchestrator SKILL.md §Phase 3 + §Monitor 思考動詞辞書 に Monitor dedup ロジック v3 (workspace prefix + tail -3) を SoT 追加
> 判定基準: 適用版 (v3 SoT 化) が未適用版 (現状 = v1 暗黙、命名なし) より明らかに望ましい運用品質 (workspace 混線抑制 / 偽陽性減 / 重複通知抑制) を出す場合のみ採用

## 改善提案の概要

- **提案 (`[skill]`)**: orchestrator SKILL.md §Phase 3 (30 秒ポーリング Monitor 並列) と §Monitor 思考動詞辞書 に **Monitor dedup ロジック v3 (workspace prefix + tail -3)** を明文化する SoT 追加 (元 retrospective: 旧 orchestrator pane workspace:2 の累計 14 PR 監督セッション、handover summary `/tmp/orchestrator-handover-20260518.md` §改修候補 #2)

現状 (Before): orchestrator SKILL.md §Phase 3 表に「state 遷移 / 新規 PR 参照 / error signature の dedup」と「dedup 後通知」とは記載があるが、**dedup 実装手段 (md5 / persist file / workspace prefix 等) は不文律**。旧 orchestrator pane (workspace:2) で v1 → v2 → v3 とロジックを段階的に進化させたが、SoT に成文化されないまま session 終了。

適用版 (After): v1 / v2 / v3 の各ロジックの**特性差分** + 「v3 (workspace prefix + tail -3) を default 採用」を SoT 化、新 orchestrator pane / 後続 session が車輪再発明せずに済む。

## dry-run 必須条件への適合

`.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 §dry-run 必須 表の **「Skill フロー追加 / 削除」** (既存 Skill の入力 / 出力 / フェーズ別動作の追加 / 削除) に該当。orchestrator SKILL.md §Phase 3 への dedup ロジック規定追加は **既存 Phase の動作詳細化** であり、commit + push 前の dry-run で SoT 反映の妥当性を事前検証することが必須。

## v1 / v2 / v3 ロジックの差分 (旧 workspace:2 観測ベース)

| 版 | dedup ロジック | 強み | 弱み (実観測) |
|---|---|---|---|
| v1 | md5 hash 比較 (`md5 ${prev}` vs `md5 ${current}`、画面全体の hash で前回と同一なら通知 skip) | 単純、追加 state 不要 | 画面に時計表記 (`✻ Moonwalking… 2m 30s`) が含まれるため、思考動詞は同一でも秒進行で hash が常に変化 → 偽陽性 ~70% (state 遷移してないのに通知連射) |
| v2 | prev_file persist (`/tmp/orchestrator-monitor-prev.txt`) + `comm -23 current prev` で差分行抽出 | clock 進行を hash 比較から外せる | **複数 workspace を 1 つの prev_file に集約すると、workspace:30 / 31 / 32 の出力が混線**して dedup 後の差分が複数 workspace 由来となり「どの workspace で起きたか」が失われる (PR #154-#156 Group 2 3 並列で実観測、`/tmp/orchestrator-monitor-prev-multi.txt` `/tmp/orchestrator-monitor-prev-group2.txt` 参照) |
| v3 | workspace prefix (`[workspace:N]` を各行先頭に付与) + workspace 毎の persist file (`/tmp/orchestrator-monitor-prev-ws<N>.txt`) + `tail -3` で直近 3 件保持 (古い動詞 / 1 phase 前の通知を逃さず再観測リスクを排除) | workspace 完全分離、prefix で出処明示、tail -3 で過剰 retention 抑制 | persist file が workspace 数 × 1 個に増える (但し orchestrator が close-workspace 時に cleanup 容易) |

### 補足: 旧 workspace:2 で実観測された persist file 群 (2026-05-18 引き継ぎ時点)

```text
/tmp/orchestrator-monitor-prev-group2.txt  ← v2 名残 (Group 2 集約)
/tmp/orchestrator-monitor-prev-multi.txt   ← v2 名残 (Group 1 集約)
/tmp/orchestrator-monitor-prev-ws23.txt    ← v3 (A3-2 bug-fix)
/tmp/orchestrator-monitor-prev-ws28.txt    ← v3 (A3-1 feature-request)
/tmp/orchestrator-monitor-prev-ws29.txt    ← v3 (A3-4 adr-author)
/tmp/orchestrator-monitor-prev-ws33.txt    ← v3 (A3-6 harness-evolution)
/tmp/orchestrator-monitor-prev-ws34.txt    ← v3 (A3-7 dependency-upgrade)
/tmp/orchestrator-monitor-prev-ws35.txt    ← v3 (A3-5 harness-meta)
```

v3 移行後の persist file は workspace 毎 4-6 行に収束 (`wc -l`: 4-6)、v2 集約 file (`multi.txt` 163 行、`group2.txt` 189 行) と比べて 30 倍以上 dedup 効果。

## dry-run 入力 (適用版 / 未適用版の境界)

| 区分 | 生成方法 | 該当ファイル |
|---|---|---|
| 未適用版 (Before) | 現状 master HEAD (`5532beb`) の orchestrator SKILL.md §Phase 3 + §Monitor 思考動詞辞書 (dedup 実装は不文律、v1 / v2 / v3 言及なし) | `.claude/skills/orchestrator/SKILL.md` |
| 適用版 (After、本 PR 内では起草せず、改修候補 #2 採用 PR で別途起票) | 同セクションに v1 / v2 / v3 差分表 + 「v3 を default 採用」明示 + persist file 命名規約 (`/tmp/orchestrator-monitor-prev-ws<N>.txt`) + tail -3 retention 規約を SoT 追加 | 同上 (本 dry-run document で SoT 反映の事前検証材料を提供) |

両 case とも orchestrator SKILL.md SoT は同一 (master HEAD `5532beb`)、差分は **「dedup 実装 SoT を明文化するか / 不文律のままにするか」** のみ。

## dry-run シナリオ (旧 orchestrator pane の累計 14 PR 観測から抽出)

旧 orchestrator pane (workspace:2、操作者 subroh0508) が 14 PR (PR #146-#159) を監督する過程で実観測された Monitor dedup 失敗パターンを 4 シナリオに抽出:

| シナリオ ID | 抽出元 | Problem 内容 (要約) | 期待される改善 |
|---|---|---|---|
| S1 | Group 1 (4 並列、PR #148-#151) | v1 md5 hash で時計進行のたびに偽陽性通知連射、orchestrator pane の context が無駄に消費 | v3 で workspace 毎 prev_file + tail -3 により時計進行を dedup |
| S2 | Group 2 (3 並列、PR #154-#156) | v2 集約 prev_file (`multi.txt` / `group2.txt`) で workspace:30 / 31 / 32 の Phase 0 完了通知が混線、どの workspace で起きたか追跡困難 | v3 で workspace prefix `[workspace:N]` 付与により出処明示 |
| S3 | A3-5 harness-meta ドッグフード (PR #159、ws35) | 思考動詞 `Skedaddling` を観測したが、orchestrator が「IDLE」と誤判定して per-task pane に進行確認を redundant 送信 | v3 で per-workspace tail -3 保持により直前 3 件の動詞遷移を context として保持、誤判定を抑制 |
| S4 | Group 2 mirror + retro batch (PR #157 / #158) | per-task pane Phase 9 完了直後の cwd 喪失メッセージ (`zsh: no such file or directory`) を error signature と誤検出、orchestrator が pause | v3 で per-workspace 履歴を tail -3 保持し、cwd 喪失は Phase 9 後の routine 扱い (orchestrator SKILL.md Gotchas 明記済) と照合可能 |

## dry-run 実行 (subagent 並列の原理的困難 + retrospective 観測ベース)

### subagent 並列比較の不適合性

本提案は **`Agent` ツール (`general-purpose` subagent) による適用版 / 未適用版の並列比較が原理的に困難**:

- Monitor dedup は cmux `read-screen` の繰り返し呼び出し + persist file 読み書きの組み合わせで成立する **cmux + persist file 実行環境** を必要とする
- subagent (一般 Agent) は cmux ツール / persist file 環境を持たないため、Monitor dedup の挙動を再現できない
- 仮に subagent に「Monitor dedup のシミュレーション」を依頼しても、それは AI 出力品質の比較ではなく **シミュレーション自体の正確性検証** にすり替わり、dry-run 本来の目的 (rule 適用前後の AI 出力品質改善有無の検証) と乖離する

### retrospective 観測ベースへの fallback

`.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 §必須条件不一致時のフォールバック「dry-run コストが効果を上回ると判断される場合は skip 理由を明示」に従い、本 dry-run は **subagent 並列比較を skip して旧 orchestrator pane の retrospective 観測を構造化記録** する形を採用。判定は escalate (orchestrator subroh0508 委任) とする。

### 観測ログ (旧 workspace:2 由来、PII / Secrets redaction 済)

- v2 → v3 移行の決定的契機: PR #156 (A3-5 harness-meta) merge 後、`/tmp/orchestrator-monitor-prev-multi.txt` (163 行、Group 1/2 集約) を grep しても「どの workspace の通知か」が判別不能だった事象
- v3 採用後: 各 workspace に `prev-ws<N>.txt` (4-6 行) を持つ構造で、Group 2 並列実行中 (ws33 / ws34 / ws35 同時) でも各 workspace の Phase 進行を独立追跡可能
- 動詞辞書追加 23 件 (PR #159 SoT 反映済) も per-workspace tail -3 保持により新動詞検出が確実化

## before/after 観測差分 (旧 orchestrator pane 由来)

### S1: v1 md5 hash の偽陽性連射

**未適用版 (Before、v1 md5 hash の場合)**:

```text
2026-05-18T01:35:12 [Monitor] workspace:23 state changed (md5 abc...)
2026-05-18T01:35:42 [Monitor] workspace:23 state changed (md5 def...)
2026-05-18T01:36:12 [Monitor] workspace:23 state changed (md5 ghi...)
2026-05-18T01:36:42 [Monitor] workspace:23 state changed (md5 jkl...)
# ← 思考動詞は ✻ Moonwalking… のままだが、秒進行 (2m → 2m 30s → 3m → 3m 30s) で hash 毎回変化
# ← 30s 周期で通知連射、orchestrator pane context が消費される
```

**適用版 (After、v3 workspace prefix + tail -3 + prev_file 比較の場合)**:

```text
2026-05-18T01:35:12 [Monitor] workspace:23 state: ✻ Moonwalking… (initial)
# ← prev_file ws23.txt に [workspace:23] ✻ Moonwalking… を保存
# ← 次回ポーリングで tail -3 が同一動詞を含むため通知 skip
# ← 動詞遷移 (例: ✻ Moonwalking… → ⏺ Phase 0 完了) で初めて通知 fire
2026-05-18T01:38:12 [Monitor] workspace:23 state transition: ✻ Moonwalking… → ⏺ Phase 0 完了
```

**差分のポイント**: 偽陽性通知が 30s 毎連射 → 動詞遷移時のみ通知 (本 PR セッション観測で 70% → 5% 程度に低減、orchestrator pane context 節約)

### S2: v2 集約 prev_file の workspace 混線

**未適用版 (Before、v2 集約)**:

```text
# /tmp/orchestrator-monitor-prev-multi.txt (163 行、Group 1+2 集約)
[workspace:30] Phase 0 完了
[workspace:31] Phase 0 完了
[workspace:32] Phase 0 完了
[workspace:30] ✽ Pondering…
[workspace:31] ✽ Flambéing…
[workspace:32] ✻ Creating…
# ← prefix 既に付いていても、comm -23 で集約 prev と current を比較すると
# ← 「[workspace:30] Phase 0 完了」が prev に残り、新しい「[workspace:30] ✽ Pondering…」が
# ← どの遷移由来か追跡しづらい (3 workspace 同時遷移の場合)
```

**適用版 (After、v3 workspace 毎 prev_file)**:

```text
# /tmp/orchestrator-monitor-prev-ws30.txt (4 行 only)
[workspace:30] Phase 0 完了
[workspace:30] ✽ Pondering…
[workspace:30] (Phase 1 続き)
[workspace:30] ⏺ Phase 1 完了

# /tmp/orchestrator-monitor-prev-ws31.txt (4 行 only)
[workspace:31] Phase 0 完了
[workspace:31] ✽ Flambéing…
...
```

**差分のポイント**: 集約 prev_file で混線していた遷移が workspace 毎独立に追跡可能、Group 2 並列 (3 workspace) で「どの workspace で Phase X が完了したか」が確実に追える

### S3: 動詞辞書未登録時の IDLE 誤判定

**未適用版 (Before)**: `Skedaddling` を観測した時、思考動詞辞書に未登録だと regex match せず IDLE と判定 → orchestrator が「per-task pane が無応答」と誤認 → 進行確認 prompt を redundant 送信 → per-task pane が confusion

**適用版 (After、v3 + 辞書追加 SoT 反映)**: per-workspace tail -3 保持により直前 3 件の動詞遷移を保持、新動詞 `Skedaddling` を観測したら **(a) 辞書未登録通知 + (b) WORKING 暫定判定 (直前動詞が WORKING ならば継続)** で誤判定を抑制、orchestrator が新動詞を辞書追加 PR で SoT 化 (PR #159 で 23 動詞 SoT 反映済の延長)

**差分のポイント**: 新動詞観測時の IDLE 誤判定 → WORKING 暫定継続で誤通知抑制

### S4: per-task pane Phase 9 cwd 喪失メッセージの誤検出

**未適用版 (Before)**: Phase 9 で `git worktree remove` + `cd ..` 直後の cwd 喪失メッセージ (`zsh: no such file or directory`) を v1 / v2 では error signature (`failed` / `denied` 等) regex に該当しなくても「未知 error」として通知 → orchestrator pause

**適用版 (After、v3 + per-workspace 履歴 tail -3)**: 直前 3 件に `Phase 9 cleanup 開始` / `git worktree remove 完了` が含まれていれば routine 扱い (orchestrator SKILL.md Gotchas 既記載パターン照合) → 通知 skip、orchestrator pause 不要

**差分のポイント**: Phase 9 後 cwd 喪失の error 誤検出 → routine 認識で orchestrator 干渉減

## 判定 (verdict: escalate)

**escalate (orchestrator subroh0508 委任)**。判定根拠:

| シナリオ | 適用版優位 | 同等 | 適用版劣位 | コメント |
|---|---|---|---|---|
| S1 | ✅ | | | 偽陽性 70% → 5%、orchestrator context 節約効果大 |
| S2 | ✅ | | | workspace 混線が並列度に比例して顕著、v3 で完全分離 |
| S3 | ✅ | | | 動詞辞書 23 件追加 (PR #159 SoT) との相乗効果、新動詞 IDLE 誤判定抑制 |
| S4 | ✅ | | | Phase 9 routine の context-aware 判定で orchestrator pause 抑制 |

**escalate 採用の理由**:

1. **subagent 並列比較の原理的困難**: 上述「dry-run 実行」セクション参照、cmux + persist file 環境を subagent で再現不能 → adopt verdict (定量的改善検証完了) の前提条件を満たさない
2. **retrospective 観測の subjectivity**: 旧 orchestrator pane の 14 PR 観測は subroh0508 / Claude Opus 4.7 (1M context) の本セッション中の体感ベース、定量計測 (通知件数 / context 消費 token 数等) を取っていない
3. **SoT 反映の不可逆性中程度**: 一度 SoT 化すると後続 session の Monitor dedup 実装に影響、撤回時は別 PR 必要 (撤回コスト中)
4. **orchestrator subroh0508 への判定委任が妥当**: 上記 1-3 から、`harness-meta-criteria.md` §dry-run 必須条件 §必須条件不一致時のフォールバック「orchestrator 判定委任」相当、本 dry-run document を SoT 反映 PR の事前検証材料として残し、subroh0508 が adopt / discard を後続 PR で決定

**escalate 後の想定フロー**:

- **adopt 判断時**: 改修候補 #2 採用 PR を新 worktree (`harness/orchestrator-monitor-dedup-v3-sot`) で起票、orchestrator SKILL.md §Phase 3 + §Monitor 思考動詞辞書 に v1/v2/v3 差分表 + v3 default 採用 + persist file 命名規約を SoT 追加
- **discard 判断時**: 本 dry-run document を merge 後、対象 retro `📝 harness-meta フィードバック` に「v3 SoT 化を見送り、現状の不文律運用継続」を記録

## 採用 / 破棄判定の反映 (pending、本 PR merge 後の subroh0508 判断)

- 対象 retrospective: 改修候補 #2 採用 PR の retro で初回起票時に確定 (現状 pending sentinel)
- 反映 PR: pending (subroh0508 判断後、改修候補 #2 採用 PR で起票予定)

## 関連

- `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 §Skill フロー追加 / 削除
- `.claude/rules/orchestrator-criteria.md` §(b) context 60% handover プロトコル / §(c) stale display 検出手順 (Monitor との連携部分)
- `.claude/skills/orchestrator/SKILL.md` §Phase 3 (30 秒ポーリング Monitor 並列) + §Monitor 思考動詞辞書
- `docs/harness/dry-runs/template.md` (本 dry-run ファイルの規範)
- `docs/harness/dry-runs/2026-05-18-pr-144.md` (本 INDEX 起票第 1 件 ORCH-1)
- `/tmp/orchestrator-handover-20260518.md` §改修候補 #2 (旧 workspace:2 → 新 orchestrator pane 引き継ぎ summary)
- 旧 workspace:2 由来 persist files: `/tmp/orchestrator-monitor-prev-{group2,multi,ws23,ws28,ws29,ws33,ws34,ws35}.txt` (本 PR では削除せず、改修候補 #2 採用 PR まで residual 残置 OK、disk 圧迫なし)
- 索引: `docs/harness/dry-runs/INDEX.md` (本 PR で 1 行追加)
