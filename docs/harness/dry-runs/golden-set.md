---
id: golden-set
title: 基準シナリオ集 (golden set) — 副作用軸検証用の固定 + iterative テストセット SoT
status: living
last_updated: 2026-05-19
related_adrs:
  - ADR-0028
related_plans:
  - PLAN-002
related_specs: []
---

# 基準シナリオ集 (golden set) — 副作用軸検証用テストセット

> **5 行以内 summary**: ADR-0028 で導入した 3 軸定量評価フレームの **副作用軸 (Side-Effect Score)** で
> 使用する、旧版で正常動作する既知シナリオ K 件を固定保持したテストセット (LLM eval 業界での *golden set*)。
> 初期 K=5、retro `⚠️ Problem` / harness-meta `📝 保留` / orchestrator 手動追加 で iterative 拡張。
> 3 PR サイクル連続で退化発生ゼロ + 対象 Skill / rule 廃止時に retire 可。
> 更新は `harness/golden-set-update-YYYY-MM-DD` ブランチで起票、SoT は本ファイル + ADR-0028。

## 基準シナリオ集とは

**基準シナリオ集** = 旧版 (改修前) で正常動作する既知シナリオを固定保持したテストセット。
LLM eval / regression testing 業界で **golden set / golden dataset** と呼ばれるもの。

新版 (改修後) に同じ K 件を投入し、各シナリオの出力を「改善 / 変化なし / 退化」の 3 値で判定する。**退化件数** + **新規 Critical findings 件数** を集計し、`.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価 §副作用軸 の閾値 (退化率 ≤ 20% + 新規 Critical ≤ 1 件) と比較して合否判定する。

## 運用パラメータ (本ファイル + ADR-0028 で SoT)

| パラメータ | 値 | 根拠 |
|---|---|---|
| 初期サイズ K | **5 件** | Anthropic 公式「20-50 simple tasks」推奨を Skill 改修向けに圧縮、計測コスト vs 検証カバレッジ trade-off の初期点 |
| 段階拡張 | K=5 → 10 → 15-20 | 5 PR サイクル毎に拡張判定、運用熟成データから上限調整 |
| 上限 | **20 件** (暫定) | Anthropic 推奨上限 50 件を Skill 単体改修向けに 1/2 に圧縮、計測時間 (N=10 × K=20 = 200 試行) を 1 PR 内で完結可能な範囲に制限 |
| 計測時間 (1 シナリオあたり) | 推定 30 秒 - 2 分 (Skill 起動 + subagent 並列) | 実測ベースで運用熟成中に調整 |

## 初期 K=5 シナリオ (PLAN-002 で配置)

以下 5 件は **既存の retrospective / dry-run / harness-meta フィードバック から抽出した「旧版で正常動作していた」シナリオ**。本 Plan merge 後の初回 dry-run で baseline 計測する。

### S1: monitor-dedup v2 (workspace prefix + tail -3) による重複通知抑制

- **出典**: `docs/harness/dry-runs/2026-05-18-monitor-dedup.md` Phase 2 観測
- **シナリオ内容**: orchestrator skill の Monitor で workspace prefix + tail -3 logic が複数 workspace の累計通知件数を正しく重複除去できるか
- **期待出力 (旧版正常動作)**: 4 workspace 並列実行時、各 workspace の最新 3 件のみ通知、過去通知は重複扱いで抑制
- **退化判定基準**: 重複通知発生件数が 1 件でも増加、または抑制すべき通知が誤って通る場合

### S2: orchestrator Skill の Phase 4 自動回答 (classifier pause 判定ライン)

- **出典**: `.claude/rules/orchestrator-criteria.md` §自動回答 / pause 判定ライン
- **シナリオ内容**: per-task pane が classifier ブロックされた際に、orchestrator が「明示承認文言 canonical」を判定して自動で承認するか / 人間判断に escalation するかの境界
- **期待出力 (旧版正常動作)**: NG パターン (auto-merge / force-merge / admin override 等) は escalation、OK パターン (out-of-band approval / orchestrator 委任で R-15 代替 等) は自動承認
- **退化判定基準**: NG パターンが誤って自動承認、または OK パターンが誤って escalation される場合

### S3: harness-meta `[mcp]` プレフィックス受信 (採用判定 → ADR 起票判定 → harness-evolution 重複検証)

- **出典**: `.claude/rules/harness-meta-criteria.md` §`[mcp]` プレフィックス受信ルール
- **シナリオ内容**: pr-retrospective が `[mcp]` 改善提案を generate した時、harness-meta が採用判定基準 1-5 → ADR 起票判定 → harness-evolution 重複検証 の 3 段判定を正しく適用するか
- **期待出力 (旧版正常動作)**: `[mcp]` 提案を採用判定基準で評価 → 採用なら ADR / `.claude/mcp.json` 改修判定、見送りなら harness-evolution 側への移行先明示
- **退化判定基準**: 3 段判定のいずれかをスキップ、または harness-evolution 重複検証が漏れた場合

### S4: pr-poller 3 系統起動経路 (起動時 + CronCreate + ScheduleWakeup)

- **出典**: `.claude/rules/pr-poller.md` §3 系統起動経路
- **シナリオ内容**: pr-poller が起動時 / CronCreate / ScheduleWakeup の 3 系統で起動された時、`.claude/locks/pr-poller.lock` で排他制御されて二重起動を防ぐか
- **期待出力 (旧版正常動作)**: 3 系統同時起動でもロック獲得は 1 系統のみ、残り 2 系統は既存ロックを検出して exit
- **退化判定基準**: ロック獲得が 2 系統以上で成立、または 1 系統も獲得失敗する場合

### S5: code-reviewer 4 aspect 並列 (harness 改修向け subset)

- **出典**: `.claude/rules/code-reviewer-aspects.md` §動的選択ルール
- **シナリオ内容**: harness 改修 PR (Kotlin code / UI / test を直接触らない PR) で code-reviewer が 4 aspect (spec-conformance / architecture / security / code-quality) を並列起動し、test-quality / performance / visual-regression / design-tokens は skip 妥当を Coordinator が明示するか
- **期待出力 (旧版正常動作)**: 4 aspect 並列実行 + skip 妥当性のレビューコメント明示
- **退化判定基準**: 4 aspect 以外を起動 (skip 漏れ)、または skip 妥当性の明示が欠落した場合

## 更新フロー (iterative 拡張)

以下の場面で **追加候補化**:

1. **新規 retro の `⚠️ Problem` 発生時**: `pr-retrospective` Skill が生成する `docs/harness/learnings/YYYY-MM-DD-pr-NNN.md` の Problem セクションから「副作用候補」(例: 「旧版で動いていたが新版で誤動作」型) を自動抽出 → 本ファイルに追加候補 PR 起票
2. **`harness-meta` の `📝 harness-meta フィードバック` `保留` 表に「副作用懸念」理由で新規追加された場合**: 自動的に追加候補化
3. **orchestrator (subroh0508) 手動追加**: 「これは恒久的に検証すべき」と判断したシナリオを直接追加

## retire 条件 (古い基準シナリオの除外)

以下のすべてを満たす場合に基準シナリオ集から除外可:

1. **3 PR サイクル連続で 改善 / 変化なし のみ (退化発生ゼロ)**: 安定して退化しないシナリオは検証価値が低下
2. **対象 Skill / rule が廃止 (archived) されている**: 元シナリオの参照対象が消失
3. **orchestrator (subroh0508) の明示承認**: 削除は不可逆性が中程度のため明示承認必須

retire 時は本ファイルから該当シナリオを削除 + 削除履歴を本ファイル末尾 §retire 履歴 に追記 (将来追加)。

## 更新 PR の起票プロセス

- **ブランチ名**: `harness/golden-set-update-YYYY-MM-DD` (`.claude/rules/branch-naming.md` 規約に従う)
- **PR template**: `harness.md` (`--template harness.md`)
- **PR description**: 追加 / retire の理由 + 出典 retro / harness-meta フィードバック / orchestrator 指示 のリンク
- **レビュー**: `code-reviewer` Skill 4 aspect 並列、本ファイル変更は spec-conformance + architecture の比重大
- **merge**: 人間 (orchestrator subroh0508) approve 後 (R-15)、本 SoT 更新が反映される

## Open questions

- **シナリオ計測コストの実測**: 5 PR サイクル後に実測データを取得し、上限 K=20 が適切かを再評価
- **過去 retrospective からの追加候補抽出の自動化**: 現状は手動 + harness-meta フィードバック経由、`pr-retrospective` Skill 内で自動抽出 trigger を将来追加 (A4 以降)
- **シナリオの参照対象 Skill / rule が廃止された場合の retire 自動化**: 現状は手動 + orchestrator 承認、`harness-meta` Skill 内で dormant 検出時に retire 候補化 trigger を将来追加

## 関連

- ADR-0028 (本ファイルの SoT、3 軸定量評価フレーム)
- `.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価 §副作用軸 (本ファイルの参照元)
- `docs/harness/dry-runs/template.md` (本ファイルを参照する dry-run 本体テンプレ)
- `docs/plans/PLAN-002-3-axis-eval-for-harness-meta-evolution.md` (本ファイルを新規追加した Plan)
- `docs/harness/evolution-proposals/2026-05-19.md` (本ファイルの根拠 evolution-proposal、EVO-2026-05-19-01)
- `docs/harness/dry-runs/INDEX.md` (本ファイルへのリンクを索引に追加予定、Phase 3 完了時)

## retire 履歴 (将来追加)

| 日付 | シナリオ ID | retire 理由 | orchestrator 承認 |
|---|---|---|---|
| — | — | — | — |
