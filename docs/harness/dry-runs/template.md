---
id: dry-run-pr-NNN
title: PR #NNN ハーネス改善提案 dry-run 結果 (<改善対象の簡潔な要約>)
type: dry-run
status: draft
related_pr: NNN
related_learning: docs/harness/learnings/YYYY-MM-DD-pr-NNN.md
related_proposals:
  - "[skill] <提案 1>"
  - "[rule] <提案 2>"
  - "[template] <提案 3>"
related_adrs:
  - ADR-0028
generated_at: YYYY-MM-DDTHH:MM:SSZ
generator: harness-meta Skill (vX.Y.Z)
---

# PR #NNN ハーネス改善提案 dry-run 結果

> 生成: harness-meta Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 関連 retrospective: [`YYYY-MM-DD-pr-NNN.md`](../learnings/YYYY-MM-DD-pr-NNN.md)
> 対象提案: <提案 N 件、各提案のプレフィックス [rule] / [skill] / [template] と該当ファイルパス>
> 判定方法: 3 軸定量評価 (改善度 / 再現性 / 副作用) の閾値判定 + 9 通り組合せ別レビュー指針 (ADR-0028)

## 改善提案の概要

<!-- 対象提案を要約。元 retrospective `🤖 ハーネス改善提案` セクションへのリンクを含む。 -->

- **提案 1 (`[skill]` / `[rule]` / `[template]`)**: <提案内容の 1-2 文要約> (元 retrospective: `docs/harness/learnings/YYYY-MM-DD-pr-NNN.md` `🤖 ハーネス改善提案`)
- **提案 2**: ...
- **提案 3**: ...

## dry-run 入力記録 (4 ブロック必須、ADR-0028 §決定 3 / `.claude/rules/harness-meta-criteria.md` §dry-run 入力記録仕様)

3 軸スコアの算出根拠が再現可能であることを担保するため、以下 4 ブロックを **必ず全項目記入** する。レビュワーは本ブロックを根拠に「同一入力で本当に再現できるか」「3 軸スコアが妥当か」を判定する。入力記録不在 / 不完全の場合は再現性スコア算出を見送り、§3 軸結果の組合せ別レビュー指針 #9 (人間判定要) に分類する。

### 1. Skill 起動コマンド (Harness Invocation)

- **起動形式**: スラッシュコマンド / `Skill` ツール / 手動代替実行 のいずれか
- **Skill 名**: `<skill-name>` (例: `harness-evolution` / `harness-meta`)
- **args (引数全文、PII / Secrets redaction 済)**:

  ```text
  <args の全文を 1 文字も省略せずに記録>
  ```

- **起動者**: human (subroh0508) / orchestrator pane / pr-poller 自動 / 他 Skill 連鎖 のいずれか
- **起動日時 (JST)**: `YYYY-MM-DDTHH:MM:SS+09:00`

### 2. Subagent 投入プロンプト (新版 = After / 旧版 = Before の両方)

#### 2-1. 旧版 (Before) プロンプト

- **subagent_type**: `general-purpose` 等
- **system prompt 差分**: 改修前 rule / template / SKILL.md を参照する旨
- **user prompt 全文** (PII / Secrets redaction 済):

  ```text
  <旧版 subagent に渡した user prompt の全文>
  ```

#### 2-2. 新版 (After) プロンプト

- **subagent_type**: 同上
- **system prompt 差分**: 改修後 rule / template / SKILL.md を参照する旨
- **user prompt 全文** (PII / Secrets redaction 済):

  ```text
  <新版 subagent に渡した user prompt の全文>
  ```

#### 2-3. 差分の正当化

- 2-1 と 2-2 の **user prompt は完全一致** であるべき (system prompt 側で旧版 / 新版を切り替える)
- 完全一致でない場合は **「user prompt が同一でない正当な理由」** を本ブロック末尾に明記 (例: 新版で追加した規約 ID を参照させる必要があり prompt にも ID 言及が追加された)

### 3. 実行環境 (Runtime Environment)

| 項目 | 値 |
|---|---|
| Model ID | `claude-opus-4-7` / `claude-sonnet-4-6` / 等 (実行時の正確な ID) |
| Temperature | 0 / 0.0-1.0 のいずれか (設定値、デフォルトは 1.0 で flakiness 大、本評価では明示推奨) |
| MCP 接続 | 有効な MCP 一覧 (`JetBrains` / `Context7` / `Cloudflare` のうち実行時に接続成立していたもの) |
| Permission モード | acceptEdits / plan / default のいずれか |
| Worktree / branch | 実行時の git 状態 (`branch` + `HEAD commit sha`) |
| Skill バージョン | 該当 Skill SKILL.md の `phase` / `last_updated` |
| 同 conversation 内の prior context | 「fresh / 継続セッション」、継続なら直前 N ターンの要旨 |
| N (試行回数) | 推奨 N=10 以上、コスト制約で N=5 採用時は信頼区間明示必須 |

### 4. 入力ファイル (subagent が Read したファイル一覧 + 該当 commit sha)

| ファイル | 新版 (After) commit sha | 旧版 (Before) commit sha |
|---|---|---|
| `.claude/rules/<rule>.md` | `<sha-after>` | `<sha-before>` |
| `.claude/skills/<skill>/SKILL.md` | `<sha-after>` | `<sha-before>` |
| `docs/harness/<doc>.md` | `<sha-after>` | `<sha-before>` |

- **commit sha の不一致が必要なファイル** = 本改修で実際に書き換えるファイル
- **commit sha が一致するべきファイル** = 改修対象外、両 subagent が同じ内容を参照すべき
- 不一致 / 一致が想定と違う場合は本表末尾に正当化を明記

## dry-run シナリオ (`⚠️ Problem` 抽出 + 基準シナリオ集)

<!-- 改善度軸用の Problem 抽出シナリオ + 副作用軸用の基準シナリオ集 (golden set) を併記。 -->

### 改善度軸用 (旧版 Problem の再発検証)

| シナリオ ID | 抽出元 retrospective | Problem 内容 (要約) | 期待される改善 |
|---|---|---|---|
| I1 | PR #NNN | <Problem 要約> | <改善されるべき AI 出力の特性> |
| I2 | PR #NNN | ... | ... |

### 副作用軸用 (基準シナリオ集 = golden set、`docs/harness/dry-runs/golden-set.md` SoT)

| シナリオ ID | 出典 | シナリオ内容 (要約) | 期待出力 (旧版正常動作) |
|---|---|---|---|
| S1 | golden-set.md S1 | <シナリオ要約> | <期待出力要約> |
| S2 | golden-set.md S2 | ... | ... |

## dry-run 実行 (subagent 並列、Generator 独立性 R-13 維持)

<!-- 新版 / 旧版それぞれに別 system prompt の subagent を立てて、上記シナリオを投げる。 -->

### Subagent 構成

- **新版 (After) subagent**: `Agent(subagent_type="general-purpose", prompt=<シナリオ + 新版ファイル>)` × N (推奨 10 以上)
- **旧版 (Before) subagent**: `Agent(subagent_type="general-purpose", prompt=<シナリオ + 旧版ファイル>)` × N (基準シナリオ集の baseline 取得用、副作用軸のみ)
- **system prompt 独立性**: 両 subagent の system prompt は本セッションの context を含まない、Generator-Evaluator 独立性 (R-13) と整合

### Subagent 実行ログ (要約、PII / Secrets redaction 済)

- 新版 subagent 出力 (N 回試行): `<要約 or リンク>`
- 旧版 subagent 出力 (基準シナリオ集 baseline): `<要約 or リンク>`

## before/after AI 出力差分 (シナリオ別、「改善 / 変化なし / 退化」3 値判定)

<!-- シナリオごとに新版 / 旧版の AI 出力を並列比較。差分 hunk または要約形式。 -->

### S1: <シナリオ要約>

**旧版 (Before)**:

```text
<AI 出力のサンプル>
```

**新版 (After)**:

```text
<AI 出力のサンプル>
```

**差分のポイント**: <誤検知減 / 規約遵守率向上 / 提案精度向上 のどれが観測されたか、定量 or 定性で記述>

**判定**: 改善 / 変化なし / 退化 のいずれか (`改善`=新版が明らかに良化 / `変化なし`=出力 diff なしまたは無視できる差 / `退化`=旧版より明らかに劣化)

### S2: ...

...

## 3 軸定量スコア (ADR-0028 §決定 1 / `.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価)

| 軸 | 計測方法 | Before (旧版) | After (新版) | 閾値 (calibration 由来) | 判定 |
|---|---|---|---|---|---|
| 改善度 | 関連 retrospective の Problem 再発率 (M 件中 X 件再発) | M/M (100%) | X/M (X%) | Problem 再発率 ≤ 30% | ✅ / ❌ |
| 再現性 | 新版に同一入力 N 回試行、target × メトリクス対応表に従い算出 (set: Jaccard / scalar: 変動係数 / categorical: 完全一致率 / 自由文: LLM-as-judge) | — (再現性は新版のみ計測) | Jaccard 平均 0.ZZ / CV / 完全一致率 / LLM-as-judge | 初期 placeholder: Jaccard ≥ 0.80 / CV ≤ 0.15 / 完全一致率 ≥ 70% / LLM-as-judge ≥ 0.80 | ✅ / ❌ |
| 副作用 | 基準シナリオ集 (golden set) K 件の退化率 + 新規 Critical findings 件数 | (旧版は基準シナリオ集として定義済) | 退化率 X% + Critical Y 件 | 退化率 ≤ 20% + 新規 Critical ≤ 1 件 | ✅ / ❌ |

### シナリオ別判定集約

| シナリオ | 改善 | 変化なし | 退化 | コメント |
|---|---|---|---|---|
| I1 | ✅ | | | <観測結果> |
| I2 | | ✅ | | <観測結果> |
| S1 | | ✅ | | <観測結果> |
| S2 | | | ❌ | <観測結果、Critical 起因なら別記> |

## 3 軸結果の組合せ別レビュー指針 (本 dry-run の該当 #N、`.claude/rules/harness-meta-criteria.md` §3 軸結果の組合せ別レビュー指針 参照)

本 dry-run の 3 軸スコアから該当する組合せ # を決定し、推奨アクションを記入する。verdict ラベルは廃止、本 # は **レビュワーが最終判断する際の reference**、機械的に approve / reject を決定する SoT ではない (Anthropic / Braintrust の hybrid 原則準拠、ADR-0028)。

- **該当 #**: <1-9 のいずれか>
- **推奨アクション**: <`.claude/rules/harness-meta-criteria.md` §3 軸結果の組合せ別レビュー指針 表の #N 行の推奨アクションを記入>
- **レビュワー最終判断**: 本指針は reference であり、最終 approve / reject は human review (subroh0508) が下す (R-15)

## 採用 / 破棄判定の反映

<!-- 判定結果を retro / PR にどう反映したかの実施記録。 -->

- 対象 retrospective `📝 harness-meta フィードバック` セクションの追記内容:
  - 「採用」表: <該当提案 + 反映 PR 番号>
  - 「見送り」表: <該当提案 + 移行先>
  - 「保留」表: <該当提案 + 再評価条件>
- 反映 PR: [<PR title> #NNN](https://github.com/subroh0508/colormaster/pull/NNN) (該当時)

## 関連

- ADR-0028 (本テンプレートの SoT、3 軸定量評価フレーム導入)
- 元 retrospective: [`docs/harness/learnings/YYYY-MM-DD-pr-NNN.md`](../learnings/YYYY-MM-DD-pr-NNN.md)
- 基準シナリオ集 SoT: [`docs/harness/dry-runs/golden-set.md`](golden-set.md)
- 関連 rule: `.claude/rules/{harness-meta-criteria,harness-evolution,retrospective-format}.md`
- 関連 Skill: `.claude/skills/harness-meta/SKILL.md` (A3-5 / PR #156 で本格化済、PLAN-002 で Phase 3 改修) / `.claude/skills/harness-evolution/SKILL.md` (PLAN-002 で Phase 5 改修)
- 索引: `docs/harness/dry-runs/INDEX.md`
