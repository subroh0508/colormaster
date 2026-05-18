---
id: PLAN-002
title: harness-meta / harness-evolution 改修 PR の 3 軸定量評価フレーム導入
type: harness
status: proposed
related_pr: null
related_epic: null
related_specs: []
related_adrs:
  - ADR-0024
  - ADR-0025
  - ADR-0026
  - ADR-0027
  - ADR-0028
expected_modules:
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/harness-evolution.md
  - docs/harness/dry-runs/template.md
  - .github/PULL_REQUEST_TEMPLATE/harness.md
  - .claude/skills/harness-meta/SKILL.md
  - .claude/skills/harness-evolution/SKILL.md
  - docs/harness/dry-runs/golden-set.md
  - docs/adr/ADR-0028-3-axis-quantitative-eval.md
created_at: 2026-05-19
completed_at: null
promoted_to: null
---

# harness-meta / harness-evolution 改修 PR の 3 軸定量評価フレーム導入

> **5 行以内 summary**: harness-meta / harness-evolution が起票する改修 PR (Skill / rule / template 変更) に対し、
> 変更前後 dry-run の結果から **改善度 / 再現性 / 副作用** の 3 軸を定量スコア化する評価フレームを導入する。
> verdict 3 値 (adopt / discard / escalate) は廃止、3 軸スコア + 9 通り組合せ別レビュー指針で代替。
> dry-run 入力記録 4 ブロック必須化でレビュワーが追検証可能化、基準シナリオ集 (golden set) で副作用検証を iterative 化。
> 採用元: `docs/harness/evolution-proposals/2026-05-19.md` (EVO-2026-05-19-01)、想定 PR 7 ファイル / 単一 PR 完結。

## 目的

- ハーネス改修 PR (`harness-meta` / `harness-evolution` 起源) の verdict 判定を **subjective から定量 + hybrid (automation + human review) に転換**
- レビュワーが 3 軸スコア + dry-run 入力記録から **追検証可能な状態で Approve / Reject 判定** できる SoT 確立
- 既存 dry-run template の verdict 中間ラベルを廃止し、**3 軸スコアを直接 SoT 化**

## 背景

- **現状の問題**: 既存 dry-run template (`docs/harness/dry-runs/template.md`) の `## 判定理由` 表が「適用版優位 / 同等 / 適用版劣位」の 3 値定性判定のみで、定量根拠・閾値・集約方法が不在。`docs/harness/dry-runs/2026-05-18-monitor-dedup.md` が自身で「定量計測を取っていない」と明言し、verdict 根拠が subjective に依存
- **外部知見との乖離**: Anthropic / Braintrust / Evidently / LangChain / Langfuse / `anthropics/skills` skill-creator v5.0 が **A/B testing + guardrail metrics (副作用) + 統計集約 (LLM flakiness 対応) + 回帰テストセット** をベストプラクティス化、既存ハーネスとの gap が高
- **evolution-proposal の経緯**: 本ペインで `/harness-evolution` 起動 → Phase 1-5 完了 → 外部知見と照合した critical / high 7 件の修正 + verdict 3 値完全廃止 + 9 通り全列挙の組合せ別レビュー指針追加 + 基準シナリオ集 (golden set) 和訳 を反映済
- **dry-run 必須条件該当**: `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 の「Skill フロー追加 / 削除」「rule 全文書き換え」「`harness-meta` 採用判定基準 / 撤去基準の改修」に該当、本 Plan Phase 内で **self-bootstrap dry-run** (本提案の 3 軸定量評価を自己適用) を実施する必要あり
- **参照 ADR**: ADR-0024 (MCP) / ADR-0025 (skill-creator 経由) / ADR-0026 (harness-meta + harness-evolution 二系統補完、**本 Plan の SoT**) / ADR-0027 (docs 構造 + 日本語化)

## アプローチ

7 ファイル変更を **1 PR で commit 分離** して投入する (`harness-meta-criteria.md` §分割粒度 表の「6-20 ファイル: 1 PR + commit 分離」境界)。

1. **Phase 1 (Open questions の決着)**: ① 基準シナリオ集サイズ上限 (50 / 15-20 件) → 計測コスト試算後判定、② ADR 起票必要性 (新規 ADR-0028 vs ADR-0026 補足) → `.claude/rules/adr.md` §起票基準 2 項目以上充足判定、③ 既存 dry-run 2 件 (`pr-144.md` / `monitor-dedup.md`) の `verdict` field 扱い (削除 / レガシー保持) → 影響範囲確認後判定
2. **Phase 2 (rule 改修、4 件)**: `harness-meta-criteria.md` §dry-run 3 軸定量評価セクション新規追加 → `harness-evolution.md` §出力フォーマット拡張 → `dry-runs/template.md` の verdict 廃止 + 9 通り指針追加 + 入力記録 4 ブロック必須化 → `.github/PULL_REQUEST_TEMPLATE/harness.md` に 3 軸定量評価セクション追加
3. **Phase 3 (Skill 改修、2 件、`example-skills:skill-creator` 経由)**: `harness-meta/SKILL.md` Phase 3 に 3 軸スコア生成 + 入力記録 4 ブロック生成 step 追加 → `harness-evolution/SKILL.md` Phase 5 に 3 軸スコア表 + 入力記録要約の PR description 転載 step 追加
4. **Phase 4 (新規 docs 追加、1 件)**: `docs/harness/dry-runs/golden-set.md` 新規追加 (基準シナリオ集 SoT、初期 K=5、retro Problem 自動追加 trigger 規定、retire 条件)
5. **Phase 5 (self-bootstrap dry-run)**: 本 Plan の改修内容を **本 Plan 自身の 3 軸定量評価で評価** (循環構造の妥当性検証)。baseline は既存 `monitor-dedup` dry-run のレトロフィット適用と組み合わせて担保。dry-run ファイル `docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md` を生成、PR description に転載
6. **Phase 6 (PR description + レビュー)**: PR description に 3 軸スコア表 + 入力記録要約 + 9 通り指針 #N 該当を記入 → `code-reviewer` Skill を 4 aspect (spec-conformance / architecture / security / code-quality) で並列起動 → Critical 0 件で Ready 化 → 人間 (orchestrator subroh0508) approve 後に merge

## 受け入れ基準 (AC)

- [ ] **AC-1**: 7 ファイル変更が `harness/3-axis-eval-framework` ブランチで merge 済 (`expected_modules` 全件)
- [ ] **AC-2**: `code-reviewer` Skill 4 aspect 並列実行で Critical 0 件、harness 改修向けに test-quality / performance / visual-regression / design-tokens は skip 妥当を明示
- [ ] **AC-3**: self-bootstrap dry-run 結果 (`docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md`) が PR description に転載、3 軸全 ✅ かつ入力記録 4 ブロック完備 (9 通り指針 #1 該当)
- [ ] **AC-4**: dry-run 入力記録 4 ブロック (Skill 起動コマンド / Subagent プロンプト全文 / 実行環境 / 入力ファイル commit sha) が dry-run ファイルに記録済、レビュワーが追検証可能
- [ ] **AC-5**: `.claude/rules/harness-meta-criteria.md` の §dry-run 3 軸定量評価セクションが measurement target × メトリクス対応表 (set: Jaccard / scalar: 変動係数 / categorical: 完全一致率 / 自由文: LLM-as-judge) + 閾値型基準 (副作用 退化率 ≤ 20% + Critical ≤ 1 / 再現性 calibration 由来 / 改善度 Problem 再発率 ≤ 30%) + 9 通り組合せ別レビュー指針 を含む
- [ ] **AC-6**: `docs/harness/dry-runs/template.md` の frontmatter から `verdict` field 削除、`## 最終判定` セクション削除、`## 判定理由` を 3 軸定量スコア表に置換、用語を「適用版優位 / 同等 / 適用版劣位」→「改善 / 変化なし / 退化」に置換
- [ ] **AC-7**: `.github/PULL_REQUEST_TEMPLATE/harness.md` に `## 3 軸定量評価` セクション追加 (スコア表 + 入力記録要約 + 9 通り指針リンク、verdict 行なし、再現性 Before 列なし)
- [ ] **AC-8**: `docs/harness/dry-runs/golden-set.md` 新規追加、初期 K=5 シナリオ + 更新フロー SoT + retire 条件
- [ ] **AC-9**: PII / Secrets redaction 検証済 (`.claude/rules/pii.md` / `secrets.md` の regex で全 7 ファイル + dry-run ファイルを scan、検出 0)
- [ ] **AC-10**: merge 後 24 時間以内に `docs/harness/evolution-proposals/2026-05-19.md` の §採用提案表に PR リンク追記 + status `draft` → `actioned`、`roadmap-tracker` で `docs/harness/roadmap.md` 反映

## スコープ外

以下は本 Plan の対象外 (別 Plan or harness-meta フィードバックで扱う):

- **既存 dry-run 2 件 (`pr-144.md` / `monitor-dedup.md`) への 3 軸スコアレトロフィット**: proposal §採用提案 EVO-2026-05-19-02 で別 Plan 候補化、本 Plan merge 後の運用熟成段階で起票判断
- **skill-creator v5.0 (Anthropic 公式) との empirical eval 統合**: 重複 (本 Plan 3 軸 vs skill-creator subagent testing) 解消は別 ADR / Plan で検討、本 Plan は ColorMaster ハーネス独自 SoT 化に専念
- **pr-poller との連動 (3 軸 ❌ 時の自動 escalation 通知)**: pr-poller A4 本格化と統合改修、本 Plan は手動代替実行までを担保
- **過去 PR の遡及改修**: 本 Plan merge 後の harness-meta / harness-evolution 改修 PR から適用、既存 PR (PR #144 / monitor-dedup PR 等) の retrospective 形式は変更しない
- **Anthropic / Braintrust 等の外部知見の継続フォロー**: `harness-evolution` Skill の月次手動起動で吸収 (R-30)、本 Plan ではフォロー機構は組まない
- **基準シナリオ集の初期 K=5 シナリオ選定の具体内容**: Phase 4 で実装、シナリオ自体は monitor-dedup / orchestrator / harness-meta 起動時の retro Problem から抽出、Plan 本体には選定方針のみ記録

## Epic 昇格判定 (Phase 1 で再評価)

本 Plan 着手時に以下が判明したら **Epic 昇格** (`status: promoted` + `promoted_to: EPIC-NNN`、`epic-author` Skill で `docs/epics/EPIC-NNN-3-axis-eval-framework/` 生成):

- 過去 dry-run レトロフィット (`pr-144.md` / `monitor-dedup.md`) を本 Plan に統合する必要が判明 → +2 PR 想定で Epic 化
- skill-creator rubric (100-point) と 3 軸定量評価の統合検討が必要 → +1 PR 想定で Epic 化
- pr-poller との連動 (3 軸 ❌ 時の自動 escalation 通知) を本 Plan に統合する必要が判明 → +1 PR 想定で Epic 化
- Phase 5 self-bootstrap dry-run で「3 軸定量評価フレーム自体に欠陥がある」と判明 → proposal レビューに差し戻し、本 Plan を `abandoned` 化

## メモ

### Open questions の確定回答 (Phase 2 で決着、2026-05-19)

1. **基準シナリオ集サイズ上限**: **初期 K=5、運用熟成で 10 → 15-20 段階拡張可能**。Anthropic 公式「20-50 simple tasks」推奨は agent 全体 eval 向け、Skill 単体改修には過剰、5 PR サイクル毎に拡張判定を `harness-meta-criteria.md` §基準シナリオ集更新フロー に記載
2. **既存 dry-run 2 件 (`pr-144.md` / `monitor-dedup.md`) の verdict field 扱い**: **レガシー保持 + frontmatter に `legacy_verdict: <value>` marker 追加**。docs 履歴保持原則 (PR #135 レトロ Try)、新フォーマット移行を明示する marker で混乱予防、Phase 3 実装時に 2 件の frontmatter に marker 追加 (本 Plan の expected_modules 外、追加 touch 2 件)
3. **ADR 起票必要性**: **新規 ADR-0028 起票** ⚠️ scope 変更。`.claude/rules/adr.md` §起票基準 で 3 項目該当 (#5 テスト戦略・品質指標の中核方針 / #7 ハーネス本体の中核設計 / #10 長期的な制約)、§ADR 化見送りの理由テンプレ 3 条件のうち「scope が config N ファイル限定」「既存 rule 本体改定なし」を満たさないため見送り不可。ADR-0013 / 0015 / 0019 (品質指標系) と parallel な決定として ADR が妥当。本 Plan の `expected_modules` に `docs/adr/ADR-0028-3-axis-quantitative-eval.md` を追加 (+1 touch)
4. **Plan vs Epic 昇格再判定**: **Plan のまま続行**。ADR-0028 起票で touch +1 (合計 8 件 + seed 4 件 = 12 件)、Epic 閾値 (touch > 30 / 行数 > 1,000) に未到達。Phase 5 self-bootstrap dry-run の結果次第で Epic 昇格再評価可

### self-bootstrap dry-run の循環構造リスク緩和

- baseline 不在 (本 Plan が改革するメトリクス自体を本 Plan の評価に使う) のリスク → 既存 `monitor-dedup` dry-run のレトロフィット適用を併用、baseline を「旧 verdict 3 値方式の verdict 一致率」で代替
- 改善度の measurement target → `monitor-dedup` で「定量計測を取っていない」と明言された自己批判が本 Plan で解消されるか (= Problem 再発率 = 0/1 で改善度 100%)
- 再現性の measurement target → 本 Plan SKILL.md Phase 3 / Phase 5 の subagent 起動が N=10 で再現可能か
- 副作用の measurement target → 初期 K=5 基準シナリオ集 (旧 verdict 3 値方式で正常動作していた dry-run 2 件 + 他 3 件) で退化率 ≤ 20%

### Phase 0 (実装着手前のチェック)

- [ ] proposal §採用提案 で「Plan 起票推奨」と明示されていることを再確認 (`docs/harness/evolution-proposals/2026-05-19.md`)
- [ ] orchestrator (subroh0508) approve 確認 (本 Plan 起票自体の approve、本 PR メッセージ履歴 + 「Plan を起票してください」指示)
- [ ] 既存 7 ファイルの最新 commit sha 確定 (`git log -1 <file>` で記録)、dry-run 入力記録 §4 用

### 参考 (proposal §取得した外部知見)

- [Anthropic — Equipping agents for the real world with Agent Skills](https://www.anthropic.com/engineering/equipping-agents-for-the-real-world-with-agent-skills)
- [Anthropic — Demystifying evals for AI agents](https://www.anthropic.com/engineering/demystifying-evals-for-ai-agents)
- [Braintrust — A/B testing for LLM prompts](https://www.braintrust.dev/articles/ab-testing-llm-prompts)
- [Braintrust — LLM evaluation guide](https://www.braintrust.dev/articles/llm-evaluation-guide)
- [Evidently AI — Regression testing for LLMs](https://www.evidentlyai.com/blog/llm-regression-testing-tutorial)
- [LangChain — LLM Evals: Production Monitoring to Regression Tests](https://www.langchain.com/articles/llm-evals)
- [Langfuse — A/B Testing for prompts](https://langfuse.com/docs/prompt-management/features/a-b-testing)
- [anthropics/skills skill-creator SKILL.md](https://github.com/anthropics/skills/blob/main/skills/skill-creator/SKILL.md)
