---
type: harness
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<ハーネス改修 / レトロ集約 / KPT 反映 / Skill 改善等。1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN (該当時)
- Epic: EPIC-NNN (該当時、ハーネス系 Epic では `EPIC-A2` / `EPIC-A9` / `EPIC-A10` 等)
- ADR: ADR-NNNN (該当時、特に ADR 0017 / 0024 / 0025 / 0026 / 0027)
- 元 learnings: `docs/harness/learnings/YYYY-MM-DD-pr-N.md` (該当時)
- 元 evolution-proposals: `docs/harness/evolution-proposals/YYYY-MM-DD.md` (該当時)

## PR の種別

- [ ] **A1 レトロ 等の即時消化フォロー PR** (rules / template / docs 即時修正)
- [ ] **rules / Skill 本格化** (B0 雛形 → 本文充実)
- [ ] **ハーネス改修 PR** (`harness-meta` Skill 起票、KPT ベース)
- [ ] **外部研究駆動の改修 PR** (`harness-evolution` Skill 起票)
- [ ] **レトロ集約 PR** (`harness/learnings-batch-YYYY-WW` ブランチ、週次 / 件数到達時)
- [ ] **その他** (chore / 撤去 / migration 等)

## 対象 PR (KPT / 改修起点)

`harness-meta` / `harness-evolution` / レトロ集約 PR で記入。各 PR について採用判定基準該当 (1-5、`.claude/rules/harness-meta-criteria.md` §採用判定基準 参照) も明記:

| 元 PR | KPT 要点 | 本 PR で消化する提案 | 採用判定基準該当 (1-5) | mirror PR か | rebase 回数 | classifier ブロック有無 |
|---|---|---|---|---|---|---|

記入例 (PR #135 レトロ Try):

| 元 PR | KPT 要点 | 本 PR で消化する提案 | 採用判定基準該当 (1-5) | mirror PR か | rebase 回数 | classifier ブロック有無 |
|---|---|---|---|---|---|---|
| #129 | classifier ブロック迂回パターン辞典 | `harness-meta-criteria.md` §classifier ブロック対応 追加 | 1 (PR #125 / #129 で反復) | — | 0 | 0 |
| #126 | mirror PR merge note 段落テンプレ | `roadmap.md` §merge note 段落テンプレ 追加 | 4 (Critical 派生) | — | 0 | 0 |

## 変更内容

| 区分 | パス | 変更内容 | 持ち越し Improvement |
|---|---|---|---|
| rule | `.claude/rules/<name>.md` | <追加 / 改修 / 削除> | <code-reviewer Improvement 番号、本 PR で消化 / 後続持ち越し> |
| skill | `.claude/skills/<name>/SKILL.md` | <追加 / 改修 / archived 化> | — |
| template | `.github/PULL_REQUEST_TEMPLATE/<type>.md` 等 | <追加 / 改修> | — |
| docs | `docs/harness/**` / `docs/adr/**` 等 | <追加 / 改修> | — |
| script | `scripts/install-git-hooks.sh` 等 | <整合性更新> | — |

> **注**: 「持ち越し Improvement」列 (PR #123 レトロ Try) は `code-reviewer` Coordinator の Improvement 番号 (例: spec-conformance #2 / architecture #4) を記載。本 PR 内消化 / 後続 PR へ持ち越しを `📝 harness-meta フィードバック` 表と紐付ける。

## 3 軸定量評価 (harness-meta / harness-evolution 改修 PR は必須、ADR-0028)

<!--
本セクションは harness-meta / harness-evolution 由来の改修 PR で必須記入。
それ以外 (template 改修のみ等で dry-run 不要条件該当) は「dry-run skip 理由」のみ記入し本表 / 入力記録は空欄可。
詳細運用規約は `.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価 を SoT として参照。
-->

### スコア表

| 軸 | 計測方法 | Before (旧版) | After (新版) | 閾値 (calibration 由来) | 判定 |
|---|---|---|---|---|---|
| 改善度 | 関連 retrospective の Problem 再発率 (M 件中 X 件再発) | M/M (100%) | X/M (X%) | Problem 再発率 ≤ 30% | ✅ / ❌ |
| 再現性 | 新版に同一入力 N 回試行、target × メトリクス対応表に従い算出 | — (再現性は新版のみ計測) | Jaccard 平均 0.ZZ / CV / 完全一致率 / LLM-as-judge | 初期 placeholder: Jaccard ≥ 0.80 / CV ≤ 0.15 / 完全一致率 ≥ 70% / LLM-as-judge ≥ 0.80 | ✅ / ❌ |
| 副作用 | 基準シナリオ集 (golden set) K 件の退化率 + 新規 Critical findings 件数 | (旧版は基準シナリオ集として定義済) | 退化率 X% + Critical Y 件 | 退化率 ≤ 20% + 新規 Critical ≤ 1 件 | ✅ / ❌ |

### dry-run 入力記録 (要約、詳細は dry-run ファイル参照)

| 項目 | 値 |
|---|---|
| 起動 Skill / args | `Skill skill="<name>" args="<args の冒頭 100 字>..."` (全文は dry-run ファイル §1) |
| Subagent プロンプト要旨 | Before / After 共通の user prompt の要旨 1-2 行 (全文は dry-run ファイル §2) |
| Model / Temperature / N | `claude-opus-4-7` / `temp=0` / `N=10` 等 (詳細は dry-run ファイル §3) |
| 入力ファイル (改修対象) | `<file 1>` / `<file 2>` (commit sha は dry-run ファイル §4) |

### dry-run ファイル + 9 通り組合せ別レビュー指針

- **dry-run 結果 (4 ブロック入力記録 + 3 軸スコア算出根拠)**: [`docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md`](../../docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md)
- **本 PR の該当 #** (`.claude/rules/harness-meta-criteria.md` §3 軸結果の組合せ別レビュー指針 9 通り表): <#1-9 のいずれか>
- **推奨アクション**: <該当 # 行の推奨アクション (例: #1 = Approve 推奨 / #2 = Reject 推奨 (guardrail 優先) / #9 = 人間判定要)>
- **レビュワー最終判断**: 本指針は reference、最終 approve / reject は human review (subroh0508、R-15) が下す

### dry-run skip 該当時

dry-run 不要条件 (`.claude/rules/harness-meta-criteria.md` §dry-run 不要条件) 該当時は本表 / 入力記録を空欄とし、以下のみ記入:

- **skip 理由**: <typo 修正 / リンク追加 / frontmatter 値更新 / 索引行追加 / 撤回コスト低 3 条件 のいずれかを明示>

## ハーネス改善提案件数 (KPT / harness-meta フィードバック)

| 観点 | 採用 | 見送り | 保留 | 撤去 |
|---|---|---|---|---|
| `[rule]` |  |  |  |  |
| `[skill]` |  |  |  |  |
| `[template]` |  |  |  |  |
| `[remove]` |  |  |  |  |

採用しなかった提案は元 learning ファイル `📝 harness-meta フィードバック` セクションに理由を追記する (`.claude/rules/retrospective-format.md` 参照)。

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>
- [ ] (本 PR でルール改定がある場合) 既存 rule / Skill / 関連 ADR との不整合がない
- [ ] (status ラベル変更がある場合) `.claude/rules/rules-index.md` / `CLAUDE.md` の lookup table が整合
- [ ] (Skill archived 化がある場合) CLAUDE.md からの参照削除、archived/ への物理移動が完了

## テスト

- [ ] markdownlint-cli2 グリーン (A6 以降)
- [ ] Gradle カスタムタスクの docs 検証グリーン (A6 以降)
- [ ] (`commit-msg` hook 変更時) `./scripts/install-git-hooks.sh` 再実行 + 試し commit で検証

## レビュー観点

<重点的に見てほしい箇所、特に既存 rule / Skill との整合性>

- ハーネス中核 Skill (`implementation-workflow` / `code-reviewer` / `roadmap-tracker` / `pr-poller`) に影響するか
- ADR 起票基準 (`§4.5` 2 項目以上) を新たに満たすか (該当する場合は ADR 昇格を提案)
- 撤回コスト: 本改修を取り消す手順は明確か

## チェックリスト

- [ ] `.claude/rules/{rules-index,docs-structure,template-language,markdown}.md` の規約を確認した
- [ ] Skill 改修の場合: `.claude/rules/skill-authoring.md` 経由 (Anthropic Complete Guide 準拠、ADR 0025)
- [ ] `auto-merge` を有効化していない (R-15、人間 approve 必須)
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン、A6 以降)
- [ ] (Epic 配下 PR の場合) `roadmap-tracker` で完了根拠登録、または手動更新の理由を本文に記載
- [ ] (status ラベル変更時) `rules-index.md` の status 語彙 (`skeleton (B0)` / `planned (X)` / `living`) を遵守
