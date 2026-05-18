---
id: ADR-0028
title: ハーネス改修 PR の品質指標として 3 軸定量評価フレーム (改善度 / 再現性 / 副作用) を導入する
status: accepted
date: 2026-05-19
related_epics: []
related_plans:
  - PLAN-002
related_specs: []
superseded_by: null
supersedes: []
related_adrs:
  - ADR-0013
  - ADR-0015
  - ADR-0017
  - ADR-0019
  - ADR-0025
  - ADR-0026
---

# ADR-0028: ハーネス改修 PR の品質指標として 3 軸定量評価フレーム (改善度 / 再現性 / 副作用) を導入する

> **5 行以内 summary**: harness-meta / harness-evolution が起票する改修 PR の品質判定に
> **改善度 (旧版 Problem の再発率) / 再現性 (同一入力 N 回試行の安定性) / 副作用 (回帰シナリオ退化率)** の
> 3 軸定量スコアを SoT 化する。verdict 3 値ラベル (adopt / discard / escalate) は廃止し、
> 3 軸スコア + 9 通り組合せ別レビュー指針で代替 (Anthropic / Braintrust の hybrid 原則準拠)。
> dry-run 入力記録 4 ブロック (起動 Skill / プロンプト / 環境 / 入力ファイル) でレビュワー追検証可能化。

## ステータス

accepted (2026-05-19、PLAN-002 で実装)。採用元 evolution-proposal: `docs/harness/evolution-proposals/2026-05-19.md` (EVO-2026-05-19-01)。

## コンテキスト

### 既存 dry-run 運用の問題

`docs/harness/dry-runs/template.md` の `## 判定理由` 表は「適用版優位 / 同等 / 適用版劣位」の 3 値定性判定のみで、定量根拠 / 閾値 / 集約方法が不在だった。実際の運用ファイル `docs/harness/dry-runs/2026-05-18-monitor-dedup.md` は自身で「定量計測を取っていない」と明言、verdict 根拠が subjective に依存していた。

verdict 3 値ラベル (`adopt` / `discard` / `escalate`) は「3 軸スコアの aggregate label」でしかなく、3 軸スコア表が直接 SoT として機能できる場合は中間表現として冗長。

### 外部知見との乖離

ADR-0024 (MCP 採用) で導入された WebFetch / WebSearch / Context7 MCP 経由で取得した外部知見 (Anthropic engineering blog / `anthropics/skills` v5.0 / Braintrust / Evidently / LangChain / Langfuse) は、本 ADR が解消すべき gap を明示していた:

- Braintrust: **guardrail metric** 概念 (goal metric 改善でも guardrail 退化なら reject)、threshold-based 判定推奨、fixed numeric values 非推奨
- Anthropic: 「hybrid grading: code-based + model-based + human」、「20-50 simple tasks drawn from real failures is a great start」、 「eval suites are living artifacts that need ongoing attention」
- Evidently: 「lte = 0.1 / 10% acceptable fail rate」等の threshold-based、「start small and keep adding examples」
- LangChain: 「LLM flakiness を統計集約で吸収」、temperature=0 でも完全な決定性は得られない

### 既存品質指標 ADR 群との関係

ADR-0013 (Coverage 段階達成) / ADR-0015 (Mutation testing) / ADR-0019 (code-reviewer 8 aspect) はそれぞれ静的コード品質 / mutation 耐性 / レビュー観点を ADR で SoT 化している。本 ADR は **ハーネス自身の改修品質** に対応する品質指標を導入する parallel な決定。

## 決定

### 1. 3 軸定量評価フレームを SoT 化する

| 軸 | 計測対象 | 閾値 (初期 placeholder、calibration で調整) |
|---|---|---|
| **改善度** (Improvement) | 旧版で発生していた Problem (関連 retrospective から抽出) の新版再発率 | Problem 再発率 ≤ 30% (= 改善率 ≥ 70%) |
| **再現性** (Reproducibility) | 同一入力 N=10 以上 (コスト制約で N=5 採用時は信頼区間明示) の重要セクション安定性、データ型ごとに別メトリクス (set: Jaccard / scalar: 変動係数 / categorical: 完全一致率 / 自由文: LLM-as-judge) | Jaccard ≥ 0.80 / CV ≤ 0.15 / 完全一致率 ≥ 70% / LLM-as-judge ≥ 0.80 (N=20 baseline から median - 1σ で運用熟成調整) |
| **副作用** (Side-Effect) | 旧版で正常出力していた回帰シナリオ集 (基準シナリオ集 = golden set、初期 K=5、iterative 拡張) の退化率 + 新規 Critical findings 件数 | 退化率 ≤ 20% + 新規 Critical findings ≤ 1 件 |

詳細運用規約は `.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価 に委譲する。

### 2. verdict 3 値ラベル (adopt / discard / escalate) を廃止する

dry-run template の frontmatter `verdict` field と `## 最終判定` セクションを削除。3 軸スコア表が直接 SoT として機能するため、中間 aggregate label を排除。

Anthropic / Braintrust 公式の hybrid 原則 (automation + periodic human review) に整合: 自動 verdict 判定は human-in-the-loop と矛盾するため、3 軸スコア + 9 通り組合せ別レビュー指針 (推奨アクションを示すがラベル化しない) で代替する。

### 3. dry-run 入力記録 4 ブロックを必須化する

3 軸スコアの算出根拠が再現可能であることを担保するため、dry-run ファイルに以下 4 ブロックを必須記録:

1. Skill 起動コマンド (起動形式 / Skill 名 / args 全文 / 起動者 / 起動日時)
2. Subagent 投入プロンプト (Before / After の user prompt 全文、完全一致原則)
3. 実行環境 (Model ID / Temperature / MCP 接続 / Permission モード / Worktree+branch / Skill version / prior context)
4. 入力ファイル (subagent が Read したファイル一覧 + 適用版/未適用版の commit sha)

入力記録不在 / 不完全の場合は再現性スコア算出を見送り、9 通り組合せ別レビュー指針 #9 (人間判定要) に分類する。

### 4. 基準シナリオ集 (golden set) を iterative 化する

副作用軸の検証用テストセット (旧版で正常動作する既知シナリオ K 件) を、初期 K=5 で開始し、retro Problem 発生時 / harness-meta 保留表 / orchestrator 手動追加 で iterative 拡張する。古いシナリオは 3 PR サイクル連続で退化発生ゼロ + 対象 Skill / rule 廃止時に retire 可。

詳細は `docs/harness/dry-runs/golden-set.md` (SoT、PLAN-002 で新規追加) を参照。

### 5. PR description への 3 軸スコア + 入力記録要約 + 9 通り指針 #N の必須転載

`.github/PULL_REQUEST_TEMPLATE/harness.md` に `## 3 軸定量評価` セクションを追加、harness-meta / harness-evolution 改修 PR では本セクションの記入を必須化する。レビュワーは PR description の 3 軸スコア + 9 通り指針リンクから approve / reject 判定の reference を得る。最終判断は human review (orchestrator subroh0508) が下す (R-15)。

## 影響

### 影響を受ける rule / Skill / template / docs (PLAN-002 で実装)

| ファイル | 改修内容 |
|---|---|
| `.claude/rules/harness-meta-criteria.md` | §dry-run 3 軸定量評価セクション新規追加 + 閾値型基準 + measurement target × メトリクス対応表 + 基準シナリオ集更新フロー + 9 通り組合せ別レビュー指針 + 入力記録 4 ブロック必須化規定 |
| `.claude/rules/harness-evolution.md` | §出力フォーマットに `## 3 軸定量評価` セクション追加 + PR description 転載規定 |
| `docs/harness/dry-runs/template.md` | frontmatter `verdict` field 削除 + `## 最終判定` 削除 + `## 判定理由` を 3 軸定量スコア表に置換 + 用語「適用版優位 / 同等 / 適用版劣位」→「改善 / 変化なし / 退化」 + `## dry-run 入力記録` 4 ブロック + `## 3 軸結果の組合せ別レビュー指針 (9 通り全列挙)` 追加 |
| `.github/PULL_REQUEST_TEMPLATE/harness.md` | `## 3 軸定量評価` セクション追加 (スコア表 + 入力記録要約 + 9 通り指針リンク) |
| `.claude/skills/harness-meta/SKILL.md` | Phase 3 改修 (3 軸スコア生成 + 入力記録 4 ブロック生成 step 追加) |
| `.claude/skills/harness-evolution/SKILL.md` | Phase 5 改修 (PR description 転載手順追加) |
| `docs/harness/dry-runs/golden-set.md` | 新規追加 (基準シナリオ集 SoT、初期 K=5) |
| 既存 dry-run 2 件 (`pr-144.md` / `monitor-dedup.md`) | frontmatter に `legacy_verdict: <value>` marker 追加 (履歴保持 + 新フォーマット移行明示) |

### 既存運用への影響

- **本 ADR merge 以降の harness-meta / harness-evolution 改修 PR は 3 軸定量評価が必須** (PR description + dry-run ファイル両方)
- **既存 dry-run 2 件は legacy_verdict marker 付加でレガシー保持**、3 軸スコアへの遡及移行は本 ADR スコープ外 (将来別 Plan で扱う可能性あり)
- **harness 以外の PR (feature / bug-fix / refactor / dependency-upgrade / docs / chore) には 3 軸定量評価を適用しない**、本 ADR スコープは harness 改修 PR のみ

### 撤回コスト

ADR-0028 自体を deprecated 化する場合、PLAN-002 で改修した 8 ファイルを revert + 既存 dry-run 2 件の legacy marker 削除で対応可能。撤回コストは 1-2 PR、外部 service / DB schema / API 契約への影響なし。本 ADR は ADR 化見送り 3 条件のうち「撤回コスト低」は満たすが、「scope が config N ファイル限定」「既存 rule 本体改定なし」を満たさないため ADR 化が妥当 (起票見送りは不適切)。

## 代替案

### 代替 1: verdict 3 値ラベルを残し、3 軸スコアは補助情報として並置

却下。3 軸スコアが直接 SoT として機能する場合、verdict は冗長な中間表現。Anthropic / Braintrust の hybrid 原則 (automation + periodic human review) と矛盾する自動 verdict 判定を残すリスクが大きい。

### 代替 2: skill-creator (ADR-0025) の 100-point rubric を拡張して 3 軸定量評価を統合

却下。skill-creator rubric は静的品質 (SKILL.md 単体スコア)、3 軸は動的回帰品質 (改修前後の運用品質)、評価対象が異なる。統合すると評価軸が混在して可読性が低下。本 ADR は skill-creator rubric と **補完関係** (静的 + 動的) で並走する。

### 代替 3: zero-tolerance threshold (退化件数 0 件 + 新規 Critical 0 件) を採用

却下。Evidently 公式が「lte = 0.1 / 10% acceptable fail rate」等の threshold-based を推奨、LangChain が「temperature=0 でも完全な決定性は得られない」と LLM flakiness を指摘。zero-tolerance は LLM 出力に対して非現実的で、全件 escalate に倒れる懸念。本 ADR は退化率 ≤ 20% + Critical ≤ 1 件の閾値型を採用。

### 代替 4: 固定閾値 (Jaccard ≥ 0.85 等) を採用

却下。Braintrust が「fixed numeric values は非推奨、real-world requirements から calibration」を推奨。固定閾値は運用熟成で陳腐化、初期段階の根拠も弱い。本 ADR は「N=20 baseline 計測 → median - 1σ」の calibration 由来閾値を採用 (初期 placeholder のみ提示)。

### 代替 5: N=3-5 試行で十分とする (低コスト)

却下。Anthropic 公式が「20-50 simple tasks」推奨、LangChain が「sample size should be large enough to detect meaningful differences」と統計的有意性を指摘。N=3 では Jaccard 平均値の信頼区間が ±0.3 程度に開き、偶然変動と本質退化の分離不可。本 ADR は N=10 以上を初期推奨、N=5 採用時は信頼区間明示を必須化。

## 関連

- ADR-0013 (Coverage 段階達成、品質指標系の先行 ADR)
- ADR-0015 (Mutation testing、品質指標系の先行 ADR)
- ADR-0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提)
- ADR-0019 (code-reviewer 8 aspect、品質指標系の先行 ADR)
- ADR-0025 (skill-creator 100-point rubric、本 ADR と補完関係)
- ADR-0026 (harness-evolution Skill 採用、harness-meta + harness-evolution 二系統補完、本 ADR の SoT 上位)
- `.claude/rules/harness-meta-criteria.md` (本 ADR の運用詳細委譲先、§dry-run 3 軸定量評価)
- `.claude/rules/harness-evolution.md` (本 ADR の運用詳細委譲先、§出力フォーマット §3 軸定量評価)
- `docs/harness/dry-runs/template.md` / `docs/harness/dry-runs/golden-set.md` (本 ADR の運用詳細委譲先)
- `docs/plans/PLAN-002-3-axis-eval-for-harness-meta-evolution.md` (本 ADR の実装 Plan)
- `docs/harness/evolution-proposals/2026-05-19.md` (本 ADR の採用元 evolution-proposal、EVO-2026-05-19-01)
- 参考 (proposal §取得した外部知見、Anthropic / Braintrust / Evidently / LangChain / Langfuse / `anthropics/skills` skill-creator v5.0 の 8 出典)
