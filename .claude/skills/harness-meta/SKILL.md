---
name: harness-meta
description: |
  pr-retrospective が生成した learning ファイル群の「🤖 ハーネス改善提案」セクションを
  入力に、harness-meta-criteria の採用 / 見送り / 撤去 3 分岐で判定し、採用は改修 PR
  (rule / Skill / ADR 起票) 起票、見送りは元 learning に「📝 harness-meta フィードバック」
  追記、撤去は status removed → 物理削除の 2 段階 PR で実行する内部 KPT 駆動 Skill。
  dry-run 必須条件該当時は dry-run 先行。
status: active
phase: A3
last_updated: 2026-05-19
related_plan: docs/harness/plan.md §4.4 / §5.4.5 / R-29 / R-30 / R-31
related_rules:
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/retrospective-format.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/pr-poller.md
  - .claude/rules/harness-evolution.md
  - .claude/rules/template-language.md
  - .claude/rules/branch-naming.md
  - .claude/rules/pr-template.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
related_adrs:
  - ADR-0017
  - ADR-0024
  - ADR-0025
  - ADR-0026
  - ADR-0027
  - ADR-0028
---

# harness-meta

> **5 行以内 summary**: pr-retrospective が生成した learning ファイルの「🤖 ハーネス改善提案」
> セクション (`[rule]` / `[skill]` / `[template]` / `[remove]` プレフィックス) を集約 parse し、
> harness-meta-criteria の採用 1-5 / 見送り 1-5 / 撤去 3 項目で 3 分岐する内部 KPT 駆動 Skill。
> 採用は改修 PR 起票 (`harness/<purpose>` + `harness.md` テンプレ)、見送りは元 learning に feedback 追記、
> 撤去は 2 段階運用 (status removed → cooldown 1 週間 → 物理削除)。dry-run 必須条件該当時は先行。

## 役割

- **内部 KPT 駆動の集約 parse**: `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` の「🤖 ハーネス改善提案」セクションを横断走査し、4 プレフィックス (`[rule]` / `[skill]` / `[template]` / `[remove]`) に分類
- **採用 / 見送り / 撤去の 3 分岐判定**: `.claude/rules/harness-meta-criteria.md` §採用判定基準 / §見送り判定基準 / §撤去判定基準 を SoT として参照、各候補に基準該当箇所 (1〜5) を明示
- **dry-run trigger**: 採用候補が §dry-run 必須条件 (rule 全文書き換え / Skill 新規追加 / Skill フロー追加削除 / template 構造変更 / SoT 反転 / 採用基準改修) に該当する場合は `docs/harness/dry-runs/YYYY-MM-DD-pr-<n>.md` を先行起票
- **改修 PR 起票**: 採用候補は `harness/<purpose>` ブランチ + `harness.md` テンプレで起票、Skill 改修は `example-skills:skill-creator` 経由、rule / ADR / template は直接編集 PR
- **learning feedback 追記**: 見送り候補は元 learning 末尾の「📝 harness-meta フィードバック」3 表 (採用 / 見送り / 保留) に「提案 → 結果」往復ログを追記し、`retrospective-format.md` §フィードバックフォーマットに準拠
- **撤去 PR の 2 段階運用**: Step 1 (status `removed` + `rules-index.md` 注記) → 1 週間 / 1 PR サイクル cooldown → Step 2 (物理削除) の順で起票、1 段階運用は禁止
- **harness-evolution との重複検証**: 同一提案が `docs/harness/evolution-proposals/*.md` で既出なら見送り (R-31、harness-meta 優先 / harness-evolution 重複見送り)

`pr-retrospective` は 1 PR 単位の learning 生成、本 Skill は **複数 PR 集約** の改修駆動と責務を分離する (`.claude/rules/skill-authoring.md` Gotchas §Skill 間の責務 overlap 回避)。

## 入力

- **起動 prompt** (人間 / `pr-poller` / orchestrator pane から渡される): 集約対象 learning の範囲 (例: 「直近 7 日分」「PR #148〜#152」「learnings-batch-2026-W21」) / 採用基準 5 (orchestrator 明示) で扱う候補リスト
- **`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md`** 群: `retrospective-format.md` §本文構造の「🤖 ハーネス改善提案」セクションを parse 対象とする
- **`docs/harness/learnings/INDEX.md`**: 走査範囲を絞る索引、`harness/learnings-batch-YYYY-WW` ブランチ集約状況の確認に使用
- **`docs/harness/evolution-proposals/*.md`**: harness-evolution との重複検証 (R-31)
- **関連 rule / Skill / docs**:
  - `.claude/rules/harness-meta-criteria.md` (採用 / 見送り / 撤去判定基準、dry-run 必須条件、3 軸定量評価 SoT、即時消化 vs 持ち越し基準、分割粒度、classifier 迂回辞典)
  - `.claude/rules/retrospective-format.md` (learning ファイル正規構造、4 プレフィックス、フィードバック追記フォーマット)
  - `.claude/rules/skill-authoring.md` (`example-skills:skill-creator` 経由規約、100-point rubric)
  - `.claude/rules/pr-poller.md` (起動経路 + 閾値連携)
  - `.claude/rules/harness-evolution.md` (重複検証相手)
  - `docs/harness/dry-runs/golden-set.md` (基準シナリオ集 SoT、副作用軸検証用 K=5 シナリオ + iterative 更新フロー、ADR-0028 で導入)
- **`pr-poller` の閾値到達通知** (自動起動経路): 未処理 learning 10 件超過 / 前回実行から 7 日経過 (`harness-meta-criteria.md` §pr-poller 起動閾値)
- **`.claude/locks/harness-meta.lock`**: 排他制御 (二重起動防止、A4 で本格化、本 PR 時点では placeholder)

## 出力

- **改修 PR** (採用時): `harness/<purpose>` ブランチ + `harness.md` テンプレ + `feat(harness)` / `docs(harness)` commit。1 PR で 1〜5 ファイルを目安、複数 rule 改修は Epic に昇格 (`harness-meta-criteria.md` §改修 PR の品質基準)
- **撤去 PR** (撤去時、2 段階):
  - Step 1: 対象 rule / Skill の frontmatter `status` を `removed` に変更 + `rules-index.md` の対応行を「撤去予定」と注記する PR
  - Step 2: Step 1 から 1 週間以上経過後、rule / Skill ファイル物理削除 + `rules-index.md` 行削除 + 関連 ADR / `decisions.md` への削除理由記録 PR
- **dry-run ファイル** (dry-run 必須条件該当時): `docs/harness/dry-runs/YYYY-MM-DD-pr-<n>.md` (テンプレ: `docs/harness/dry-runs/template.md`、索引: `docs/harness/dry-runs/INDEX.md`)、before/after AI 出力差分 + 判定理由を記録
- **元 learning への追記** (見送り時): `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` 末尾の「📝 harness-meta フィードバック」3 表 (`YYYY-MM-DD <PR ID> で消化 (採用)` / `YYYY-MM-DD <PR ID> で見送り (後続フェーズへ)` / `YYYY-MM-DD <PR ID> で保留 (要再評価)`) に「提案 → 採用先 PR / 見送り理由 / 保留理由」行を追記。1 PR 内で「提案 → 結果」往復ログを完結 (R-12 learning ロスト対策)
- **改修 PR description の「採用根拠」セクション**: 対象 learning ファイル群へのリンク + 採用判定基準 1〜5 のどれを満たすかを明記 (`harness-meta-criteria.md` §改修 PR の品質基準)
- **副作用なし** (人間レビュー前のファイル変更まで): merge は人間 approve 後に orchestrator pane が代行 (R-15、`.claude/rules/merge-readiness.md`)

## フェーズ別動作

### Phase 1: learning ファイル走査 + 提案セクション抽出

- `docs/harness/learnings/` 配下を起動 prompt の集約範囲 (例: 直近 7 日 / 特定 batch ブランチ / 明示 PR 番号リスト) で絞り込み
- 各 learning ファイルの `## 🤖 ハーネス改善提案` セクションを正規表現で抽出 (`retrospective-format.md` §本文構造、4 プレフィックス `[rule]` / `[skill]` / `[template]` / `[remove]` + チェックボックス `[ ]`)
- 既に `[x]` 済 (前回 harness-meta で処理済) は skip、`[ ]` 未処理のみを candidate 集合に追加
- candidate 集合を `{learning ファイル, 提案テキスト, プレフィックス, consensus 表記 (該当時), 採用判定基準該当箇所 (該当時)}` の構造化リストに整形
- harness-evolution 重複検証: `docs/harness/evolution-proposals/*.md` に類似提案が存在するか grep ベースで照合 (R-31)
- PII / Secrets redaction: 抽出した提案テキストに `[REDACTED-*]` 漏れがないか `.claude/rules/pii.md` / `.claude/rules/secrets.md` の regex で再検証

### Phase 2: 採用 / 見送り / 撤去 判定

- candidate 集合の各項目について `.claude/rules/harness-meta-criteria.md` §採用判定基準 1-5 を順に評価:
  1. 複数 PR (>=2) の learning で同じ提案が反復 (構造的課題)
  2. A1 レトロ 15 提案のような ADR / Plan / Epic で予約された項目
  3. R-XX (規約 ID) と直接対応
  4. Critical findings から派生 (code-reviewer Critical の根本原因)
  5. orchestrator (subroh0508) が手動で「採用」と明示
- いずれにも該当しない場合は §見送り判定基準 1-5 を評価:
  1. 後続フェーズ移行 (A4 / A6 / A7 / A10 / Phase B / Phase C で対応予定)
  2. 提案重複 (既に他 learning で採用済 / 同 PR で対応済)
  3. コスト / 効果の不均衡
  4. harness-evolution との重複 (R-31)
  5. orchestrator 手動「見送り」明示
- `[remove]` プレフィックス候補は §撤去判定基準 3 項目全てを評価 (3 ヶ月未参照 / dangling 参照ゼロ / orchestrator 事前承認)、1 項目でも欠落なら見送りに格下げ
- `[mcp]` プレフィックスを受信した場合は §`[mcp]` プレフィックス受信ルール (採用判定 → ADR 起票判定 → harness-evolution 重複検証) に従う
- 各候補の判定結果を `{candidate, 判定 (採用/見送り/撤去), 該当基準 ID, 判定理由 (1-2 行)}` に整形

### Phase 3: dry-run 必須条件チェック + 3 軸定量評価 + dry-run 起票 (採用候補のみ、ADR-0028 で 3 軸定量化)

- 採用候補について `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 (PR #141 レトロ Try) を順に評価:
  - rule 全文書き換え (本文 50% 以上改変 / SoT 方向変更 / 核心セクション改修)
  - Skill 新規追加 (`.claude/skills/<new-name>/SKILL.md` 配置)
  - Skill フロー追加 / 削除 (入力 / 出力 / フェーズ別動作の改修)
  - template 構造変更 (セクション追加削除 / frontmatter 必須キー変更)
  - rule 間の SoT 反転
  - harness-meta 採用判定基準 / 撤去基準の改修
- §dry-run 不要条件 (typo / リンク追加 / 既存セクション例示追加 / frontmatter 値更新 / 索引行追加 / 撤回コスト低 3 条件) に該当する場合は dry-run スキップ可 (PR description の `## 3 軸定量評価` セクションは「dry-run skip 理由」のみ記入、スコア表 / 入力記録 / 9 通り指針 #N は空欄)
- 必須 / 不要のどちらにも明確に該当しない場合は orchestrator 判定委任 (採用判定基準 5 と同等のエスカレーション)、skip 理由を「📝 harness-meta フィードバック」§保留 表に明示
- **以下「dry-run 必須条件該当時の 3 軸定量評価フロー」 (7 step) は dry-run 必須時のみ実行する**。skip 該当時 (上記 dry-run 不要条件該当 / 必須不要どちらも該当しない場合) は本フローを skip し Phase 4 (改修 PR 起票) に直接遷移する
- **dry-run 必須条件該当時の 3 軸定量評価フロー** (ADR-0028 §決定、`.claude/rules/harness-meta-criteria.md` §dry-run 3 軸定量評価):
  1. **dry-run 入力記録 4 ブロックを subagent 起動前に固定 + 全文記録** (起動 Skill / Subagent プロンプト全文 / 実行環境 / 入力ファイル commit sha、入力記録不在 / 不完全の場合は再現性スコア算出を見送り 9 通り指針 #9 に分類)
  2. **改善度軸**: 関連 retrospective 直近 5-10 件の `⚠️ Problem` から M 件抽出 → 新版 dry-run subagent に投入 → Problem 再発率 算出 (≤ 30% で ✅)
  3. **再現性軸**: 新版 subagent に同一入力を **N=10 以上 (コスト制約で N=5 採用時は信頼区間明示必須)** 投入 → measurement target × メトリクス対応表 (set: Jaccard / scalar: 変動係数 / categorical: 完全一致率 / 自由文: LLM-as-judge) に従い算出 → calibration 由来閾値で判定 (初期 placeholder: Jaccard ≥ 0.80 / CV ≤ 0.15 / 完全一致率 ≥ 70% / LLM-as-judge ≥ 0.80)
  4. **副作用軸**: `docs/harness/dry-runs/golden-set.md` の **基準シナリオ集** (初期 K=5、iterative 拡張) を新版 subagent に投入 → 各シナリオを「改善 / 変化なし / 退化」3 値判定 → 退化率 + 新規 Critical findings 件数を集計 (退化率 ≤ 20% + Critical ≤ 1 件で ✅)
  5. **基準シナリオ集自動更新 trigger 評価**: 本 dry-run で抽出した Problem に「副作用候補」(旧版で動いていたが新版で誤動作型) が含まれる場合は基準シナリオ集追加候補 PR を起票 (`harness/golden-set-update-YYYY-MM-DD` ブランチ)
  6. **dry-run ファイル生成** (`docs/harness/dry-runs/YYYY-MM-DD-pr-NNN.md`、テンプレ: `docs/harness/dry-runs/template.md`): 4 ブロック入力記録 + 3 軸スコア表 + シナリオ別判定 + 9 通り組合せ別レビュー指針 #N 該当を記録 (verdict ラベルは廃止、ADR-0028 §決定 2)
  7. **PR description 転載**: Phase 4 改修 PR description の `## 3 軸定量評価` セクション (`.github/PULL_REQUEST_TEMPLATE/harness.md`) に **3 軸スコア表 + dry-run 入力記録の要約 + dry-run ファイルリンク + 9 通り指針 #N 該当の推奨アクション** を必須転載
- **判定結果による分岐**:
  - 9 通り指針 #1 (全軸 ✅) → Phase 4 改修 PR 起票に進む
  - #2 / #5 / #6 / #8 (副作用 ❌ 系統) → guardrail 違反 reject、変更を破棄 → retro `📝 harness-meta フィードバック` §保留 (要再評価) 表に記録、別案検討
  - #3 / #7 (再現性 ❌ 系統) → N を増やして再計測 → 改善後に #1 ルートへ、改善されない場合は #9 (人間判定要) に escalate
  - #4 (改善度 ❌ + 副作用 ✅ + 再現性 ✅) → orchestrator (subroh0508) 委任で「コスト vs 効果」再評価
  - #9 (測定不能) → orchestrator 委任、改修着手前に retry 必須

### Phase 4: 改修 PR 起票 (採用候補)

- ブランチ作成: `harness/<purpose>` 形式 (`.claude/rules/branch-naming.md` 準拠、例: `harness/feedback-batch-2026-W21` / `harness/rule-tighten-mcp-usage`)
- 改修種別ごとの起票プロセス:
  - **`[rule]` 採用**: 対象 `.claude/rules/<name>.md` を直接編集 + `rules-index.md` 状態欄 / lookup table を必要に応じて更新
  - **`[skill]` 採用**: `example-skills:skill-creator` 経由で SKILL.md 改修 (`skill-authoring.md` §起動方法 §100-point rubric、直接編集も禁止ではないが drift 防止のため経由推奨)
  - **`[template]` 採用**: 対象 template (`docs/plans/template.md` / `docs/epics/template/*.md` / `.github/PULL_REQUEST_TEMPLATE/*.md` / 等) を直接編集
  - **`[remove]` 採用**: Phase 6 (2 段階運用) に委譲
  - **ADR 化が妥当な決定**: `.claude/rules/adr.md` §起票基準で ADR 起票判定、2 項目以上充足なら ADR 起票 + 関連 rule / `decisions.md` 双方向リンク更新
- PR template: `.github/PULL_REQUEST_TEMPLATE/harness.md` を `--template harness.md` で指定 (`.claude/rules/pr-template.md`)
- PR body は `--body-file /tmp/pr-body-harness-meta-<purpose>-<timestamp>.md` で渡す (heredoc 直送禁止、`orchestrator-criteria.md` §プロンプト送信プロトコル §採用判断ライン に整合)
- PR description の「採用根拠」セクションに以下を含める:
  - 対象 learning ファイル群へのリンク (相対パス)
  - 採用判定基準 1〜5 のどれを満たすか
  - dry-run 実施有無 + 結果 (該当時)
  - 即時消化 vs 持ち越し判定 (`harness-meta-criteria.md` §即時消化 vs 持ち越し 判断基準)
- 改修ファイル数 1-5 が目安、6-20 ファイルなら 1 PR + commit 分離、21-30 ファイルは 1 PR 上限、31+ ファイルは Epic 昇格 (`harness-meta-criteria.md` §分割粒度)
- `code-reviewer` Skill を 4 aspect (spec-conformance / architecture / security / code-quality) で並列起動 (harness 改修は test-quality / performance / visual-regression / design-tokens skip 妥当、`code-reviewer-aspects.md` §動的選択ルール)
- Ready 化は `gh pr ready <PR#>`、merge は人間 approve 後に orchestrator pane が代行 (R-15)

### Phase 5: 見送り feedback 追記 (見送り候補)

- 各見送り候補について、提案の出元 learning (`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md`) を Edit ツールで開く
- 末尾の `## 📝 harness-meta フィードバック` セクションに以下を追記 (`retrospective-format.md` §フィードバックフォーマット準拠):
  - **採用時**: `### YYYY-MM-DD PR #<NNN> で消化 (採用)` 表に「提案テキスト | PR #<NNN> での消化内容」行を追加
  - **見送り時**: `### YYYY-MM-DD PR #<NNN> で見送り (後続フェーズへ)` 表に「提案テキスト | 見送り理由 / 移行先フェーズ」行を追加
  - **保留時**: `### YYYY-MM-DD PR #<NNN> で保留 (要再評価)` 表に「提案テキスト | 保留理由」行を追加
- 元 learning の「🤖 ハーネス改善提案」セクションのチェックボックスを `[x]` に更新 + 採用先 PR リンクを併記 (`retrospective-format.md` §🤖 ハーネス改善提案 §各項目は採用時に `[x]` + リンク追記)
- 追記は **1 PR 内で「提案 → 結果」往復ログを完結** (R-12)、別 PR で feedback 追記する 2 段階分離は禁止
- 過剰 feedback 追記の整理: 同一 learning に複数 PR からの feedback が累積する場合は date prefix で時系列順に保つ、古い feedback を削除しない (履歴保持)
- learning ファイルを更新する PR は **本 Skill の改修 PR と同一 PR に含める** (Phase 4 の改修 PR が learning 改修も併合)、別 PR 分離は禁止

### Phase 6: 撤去 PR (撤去候補、2 段階運用必須)

- 撤去候補は `.claude/rules/harness-meta-criteria.md` §撤去判定基準 3 項目全て (3 ヶ月未参照 / dangling 参照ゼロ / orchestrator 事前承認) を **再確認** (Phase 2 で評価済でも Phase 6 直前で再 grep / orchestrator 再確認)
- **Step 1 (status 変更 PR)**:
  - ブランチ: `harness/remove-step1-<target>-<timestamp>` (例: `harness/remove-step1-skill-foo-20260518`)
  - 変更内容: 対象 rule / Skill の frontmatter `status` を `removed` に変更 + `rules-index.md` の対応行に「撤去予定 (Step 2 で物理削除予定、cooldown YYYY-MM-DD まで)」注記
  - PR template: `harness.md`、PR body の「採用根拠」セクションに撤去判定 3 項目の充足根拠 + cooldown 期限 (1 週間後または 1 PR サイクル後の早い方) を明記
  - merge 後、cooldown 期間中に dangling 参照 / 復活要望 / 誤削除リスクを観察
- **Step 2 (物理削除 PR)**:
  - Step 1 merge から **1 週間以上 (または 1 PR サイクル以上) 経過** を確認、cooldown 中に異議申し立てなしを確認
  - ブランチ: `harness/remove-step2-<target>-<timestamp>`
  - 変更内容: rule / Skill ファイル物理削除 + `rules-index.md` 行削除 + 関連 ADR / `docs/epics/<id>/decisions.md` (該当時) に削除理由を記録 + 他 rule / Skill / docs の参照リンク張り替え
  - PR template: `harness.md`、PR body に Step 1 PR リンク + cooldown 経過確認 + dangling 参照ゼロ grep 結果を明記
- Step 1 と Step 2 を同一 PR に統合する 1 段階運用は **禁止** (誤削除時のロールバックコストが高い、`harness-meta-criteria.md` §撤去判定基準 末尾)
- 撤去 PR は `code-reviewer` Skill の architecture aspect に「dangling 参照ゼロ」「他 rule / Skill / docs への影響評価」を必ず通す

## Gotchas

- **採用判定の主観性**: 採用判定基準 1-5 は完全には機械化できない、特に基準 1 (反復) / 基準 4 (Critical 派生) は LLM の解釈に依存する。迷ったら orchestrator 判定委任 (基準 5 と同等のエスカレーション) に倒し、決定根拠を PR description の「採用根拠」セクションに明文化する。基準 2 / 3 (ADR 予約 / R-XX 直接対応) は ID 参照で検証可能、優先的に評価
- **dry-run skip リスク**: dry-run 必須条件該当 (rule 全文書き換え / Skill 新規追加 / Skill フロー追加削除 / template 構造変更 / SoT 反転 / 採用基準改修) を見落として直接 commit + push すると、AI 出力品質劣化を本番反映してから気付くリスクがある。Phase 3 で必須条件 6 項目を **チェックリスト形式で 1 項目ずつ評価**、該当ゼロを根拠付きで宣言する (`[ ] rule 全文書き換え該当なし: 改修対象 rule 本文の改変率 X% (< 50%)`) ことで skip 漏れを防ぐ
- **R-15 担保の徹底**: 本 Skill は改修 PR / 撤去 PR を **起票 + Ready 化まで** を責務とし、merge は人間 approve 後に orchestrator pane が代行する (`.claude/rules/merge-readiness.md` 3 条件: CI green + Critical 0 + 人間 approve)。本 Skill が `gh pr merge --merge` を直接実行することは禁止
- **learning ファイル過剰 feedback 追記の整理**: 同一 learning に複数の harness-meta PR からフィードバックが累積する場合、`### YYYY-MM-DD PR #<NNN>` の date prefix で時系列順を保つ。古い feedback を削除しない (履歴保持)、ただし表が 30 行を超える場合は別 docs (`docs/harness/learnings/<id>-feedback-archive.md` 等) へ分割を検討 (将来追加、A4 / A6 完了後の運用熟成時)
- **撤去判定の慎重さ + 2 段階運用必須**: 撤去は不可逆性が高い、Step 1 (status `removed`) → 1 週間 cooldown → Step 2 (物理削除) の 2 段階運用を **厳守**。1 段階運用 (status 変更 + 物理削除を同一 PR で統合) は `.claude/rules/harness-meta-criteria.md` §撤去判定基準 末尾で明示禁止、誤削除時のロールバックコストが高すぎる。撤去判定基準 3 項目 (3 ヶ月未参照 / dangling 参照ゼロ / orchestrator 事前承認) は Phase 2 で評価済でも Phase 6 直前で **再 grep + orchestrator 再確認**
- **harness-evolution との重複見送り (R-31)**: 同一提案が `docs/harness/evolution-proposals/*.md` で既出の場合は **harness-meta 側で見送り**、harness-evolution 側で扱う。逆方向 (harness-meta 採用案を harness-evolution が再提案) も同様に harness-evolution 側で見送り、内部 KPT (実体験ベース) を優先する
- **classifier 迂回時の中立表現**: PR body / commit message に「auto-merge」「self-merge」「force-merge」「admin override」「Safety-Check 回避」等の語を含めない (`.claude/rules/harness-meta-criteria.md` §classifier ブロック対応 迂回パターン辞典)。中立表現「都度承認の手間削減」「orchestrator 委任で R-15 代替」「out-of-band approval」等に書き換える
- **PII / Secrets redaction 必須**: learning ファイル / 改修 PR description / レビューコメントに PII (email / display name / GIS avatar URL / sub claim / IPv4/v6) や Secrets (API key / token / Bearer / JWT / GitHub PAT) を含めない (`.claude/rules/pii.md` / `.claude/rules/secrets.md` redaction 表)。Phase 1 の抽出時 + Phase 4 / 5 の出力前の 2 回 redaction check
- **`[mcp]` プレフィックスは harness-evolution 寄り**: `[mcp]` (MCP server 追加 / 設定変更) は harness-evolution 固有プレフィックスだが harness-meta 側で受信されることがある (`.claude/rules/harness-meta-criteria.md` §`[mcp]` プレフィックス受信ルール)。採用時は ADR 起票判定 + `.claude/mcp.json` 改修、見送り時は「`[mcp]` 提案は harness-evolution 側で扱う」と移行先を明示
- **新規 Skill 追加時の skill-creator 経由**: `[skill]` 採用で SKILL.md を新規追加 / 改修する場合は `example-skills:skill-creator` 経由を推奨 (`.claude/rules/skill-authoring.md` Gotchas §既存 Skill 改修も skill-creator 経由を推奨)。直接編集禁止ではないが、100-point rubric 不合格 / フォーマット drift を防ぐため経由する。本 SKILL.md 自体も skill-creator 経由 or 経由準拠で起票
- **本格化のフェーズ依存**: 本 Skill の自動化は **A4** (`pr-poller` 統合 + `.claude/locks/harness-meta.lock` 排他制御 + Skill 駆動 dry-run サブエージェント並列) で完了する。A3 (本 PR) 時点では SKILL.md の SoT 化 + 手動代替実行 (orchestrator / 人間が本 SKILL.md を参照して手動実行) が前提。Phase 3 dry-run サブエージェント並列は A4 で本格化、本 PR 時点では手動代替可
- **Self-meta 改修の循環防止**: 本 Skill 自身 (`harness-meta-criteria.md` / `.claude/skills/harness-meta/SKILL.md`) の改修も harness-meta フローで扱うとメタな自己改修ループになる。本 Skill / 本 rule の改修 PR は `dry-run 必須条件` の「採用判定基準 / 撤去基準の改修」に該当するため必ず dry-run 先行、A4 / A6 完了後に運用熟成

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、GitHub Actions から Claude API 呼ばない原則)
- ADR 0024 (MCP 採用、`gh` CLI 優位)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- ADR 0026 (harness-evolution Skill 採用、harness-meta との二系統補完)
- ADR 0027 (docs 構造 + 命名規約 + 日本語化方針)
- `docs/harness/plan.md` §4.4 (learning ファイル SoT) / §5.4.5 (ハーネス改善ループ 6 フェーズ) / R-12 / R-29 / R-30 / R-31
- `.claude/rules/harness-meta-criteria.md` (採用 / 見送り / 撤去判定基準 SoT、dry-run 必須条件、即時消化 vs 持ち越し、分割粒度、classifier 迂回辞典、`[mcp]` プレフィックス受信ルール、pr-poller 起動閾値)
- `.claude/rules/retrospective-format.md` (learning ファイル正規構造、4 プレフィックス、フィードバック追記フォーマット)
- `.claude/rules/skill-authoring.md` (`example-skills:skill-creator` 経由規約、100-point rubric)
- `.claude/rules/pr-poller.md` (起動経路 + 閾値連携)
- `.claude/rules/harness-evolution.md` (重複検証相手、R-31)
- `.claude/rules/template-language.md` (日本語化方針、固定セクション名例外)
- `.claude/rules/branch-naming.md` (`harness/<purpose>` ブランチ命名)
- `.claude/rules/pr-template.md` (`harness.md` テンプレ運用)
- `.claude/rules/merge-readiness.md` (R-15 3 条件、merge は orchestrator 代行)
- `.claude/skills/pr-retrospective/SKILL.md` (本 Skill が入力に取る learning ファイル生成元)
- `.claude/skills/pr-poller/SKILL.md` (起動経路 + 閾値連携)
- `.claude/skills/harness-evolution/SKILL.md` (重複検証相手)
- `.claude/skills/orchestrator/SKILL.md` (本 Skill の手動代替実行時の起動元、R-15 代行 merge)
- `.claude/skills/roadmap-tracker/SKILL.md` (改修 PR merge 後の roadmap mirror)
- `example-skills:skill-creator` (Skill 新規追加 / 改修時の経由先)
