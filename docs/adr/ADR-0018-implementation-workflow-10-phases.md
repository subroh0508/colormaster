---
id: ADR-0018
title: implementation-workflow Skill を 10 フェーズ構成にする
status: accepted
date: 2026-05-17
related_epics:
  - EPIC-000
related_plans:
  - PLAN-001
related_specs: []
superseded_by: null
supersedes: null
---

# ADR-0018: implementation-workflow Skill を 10 フェーズ構成にする

> **5 行以内 summary**: Plan / Epic 確定後の実装着手から Lint/Test、AI レビュー、人間
> approve、squash merge、レトロ起動、worktree クリーンアップまでを **10 フェーズ
> (Phase 0-9)** で統合管理する Generator として `implementation-workflow` Skill を採用する。
> Phase 0 で `git worktree add`、Phase 9 で `git worktree remove` をペア化し、複数
> Claude Code セッションの並行実装を物理的に分離する。fix loop は上限 3 回、超過時は
> Plan を `status: blocked` にして人間に通知する。

## ステータス

accepted

## コンテキスト

ColorMaster の Spec Gen フェーズ (`feature-request` / `bug-fix` / `refactor` /
`dependency-upgrade`) は Plan / Epic 起票までで責務が完結する。その後の実装着手 →
Lint/Test → AI レビュー → マージ → レトロ → 後始末は別フェーズで担当する必要がある。

これまでは Skill が個別に呼ばれる前提で、フェーズ間の引き継ぎ・lint 失敗時の
fix loop・worktree 管理・`code-reviewer` 起動契機が暗黙だった。複数 Claude Code
セッションを並行起動する場合 (例: PLAN-007 と EPIC-001 PR-03 を同時進行) に、
同じ作業ディレクトリで複数セッションが衝突する事故が起きやすい。

加えて、Anthropic の Planner / Generator / Evaluator パターンに照らすと、Generator
側の責務 (実装してコミットして Draft PR を作る) と Evaluator 側の責務 (レビューする)
は **別 Skill で独立** させる必要があり、両者を統合する **オーケストレーター** が
求められる。

## 決定

`implementation-workflow` Skill を **オーケストレーター** とし、10 フェーズ
(Phase 0-9) で全工程を統合管理する。

- **Phase 0**: Worktree 作成 (`git worktree add ../<repo-name>-worktrees/<branch-slug>
  -b <branch-name>`、`.claude/rules/branch-naming.md` 準拠、以降の全 Phase はこの
  worktree 内で実行)
- **Phase 1**: 要件 / 基本設計 / 詳細設計 Markdown を Read (frontmatter `related_*` を
  辿って ADR / Epic / Plan を再帰 Read)
- **Phase 2**: Spec 整合性チェック (SPEC-ID 採番重複なし、`related_basic` /
  `related_detail` 双方向リンク有効、frontmatter 必須キー検証)
- **Phase 3**: rules-index → 実装 + `./gradlew check` + Lint + Test (fix loop **上限
  3 回**、超過時は Plan を `status: blocked` にして人間に通知)
- **Phase 4**: Self-Verification (三層指標 / rules 違反自己チェック / PII / secrets /
  設計書コード断片混入の検出)
- **Phase 5**: Draft PR 作成 (`gh pr create --draft --template <type>.md
  --body-file <draft>`、type は feature / bugfix / refactor / dependency-upgrade /
  harness / docs のいずれか)
- **Phase 6**: `code-reviewer` 呼出 (8 aspect 並列、Coordinator、ADR-0019)
- **Phase 7**: CI green + 全 aspect pass + **人間 approve** の 3 条件で `gh pr merge
  --squash`。**auto-merge は禁止** (GitHub Agentic Workflows 原則、R-15)
- **Phase 8**: `pr-poller` 即時起動 (learning ファイル生成) + `roadmap-tracker` で完了
  根拠登録 (Epic 配下 PR / B-A-C フェーズ項目のみ、Plan 単体は対象外)
- **Phase 9**: `git branch --merged main` 確認 → `git worktree remove` + `git branch
  -d`。**未マージなら停止して人間に通知** (worktree / branch を残す)

Phase 0 と Phase 9 はペア (worktree 作成と削除)。Phase 6 の Critical findings あり時は
Phase 3 に戻り fix loop を回す (上限 3 回)。

## 根拠

- **並行実装の物理分離**: 複数 Claude Code セッションが別々の worktree で動けば、
  作業中のファイル衝突 / index 競合 / branch 切り替え事故が構造的に排除される。
  `roadmap-tracker` の「並行実装容易性に基づく次の推奨着手 (top-N)」(`expected_modules`
  の重複が少ない順) と組み合わせて、安全な並行ループを成立させる
- **Generator / Evaluator 分離 (Anthropic 原則)**: Phase 6 で `code-reviewer` を独立
  Skill として呼ぶ。Generator 自身がレビューしないことで bias を回避 (R-13)
- **fix loop 上限 3 回**: 際限ない自動修正は失敗パターン蓄積につながる → 3 回で諦めて
  Plan を `blocked` にし、人間判断にエスカレーション (R-14)
- **auto-merge 禁止**: GitHub Agentic Workflows の human-in-the-loop 原則。AI の判断を
  最終承認する人間ゲートを必ず保持 (R-15)
- **Phase 8 の roadmap 更新条件**: Plan は 1 PR で完結するためロードマップ追跡対象外
  (R-34)、Epic 配下 PR や B-A-C フェーズ項目のみが進捗ビューに値する

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 単一 Skill で実装 + レビュー一体化 | 起動回数が少ない | Generator バイアス、Anthropic Evaluator 独立性原則違反 | bias 構造化のため不採用 |
| worktree なし、`git checkout` 切り替え | 単純 | 並行 Claude Code セッションで衝突 | 並行性が成立しないため不採用 |
| fix loop 無制限 | 自動修復力高い | 無限ループリスク、cost 暴走 | 上限 3 回で打ち切り、blocked エスカレーション採用 |
| auto-merge 有効化 | 高速 | 人間ゲート喪失、GitHub Agentic Workflows 原則違反 | R-15 で禁止、不採用 |
| 5 フェーズ統合 (実装 / レビュー / merge / retro / cleanup) | 軽量 | worktree / Self-Verification / Draft 昇格の境界が曖昧 | 10 フェーズで責務分離、採用 |

## 帰結

### Positive

- 複数 Claude Code セッションが安全に並行実行可能 (worktree 物理分離)
- 各 Phase の責務が明確で、Skill 実行ログ (`.claude/logs/`) からフェーズ別に進捗追跡可能
- Generator / Evaluator が独立し、Anthropic 原則に準拠 (ADR-0019 と整合)

### Negative / トレードオフ

- **10 フェーズはステップ数が多い**: 単純な 1 行修正 PR でも全フェーズを通す必要 →
  Skill 内でフェーズスキップ判定を持たない代わりに、各 Phase の処理が軽量なら 1 ループ
  あたりのオーバーヘッドは限定的
- **worktree 削除忘れリスク**: Phase 9 を飛ばすと worktree が肥大化 → fix loop 上限超過 /
  人間中断時の停止条件を明示し、Phase 9 をスキップしない設計
- **fix loop 上限 3 回の打ち切り**: AI が自力解決できない複雑バグは人間判断にエスカレ →
  blocked 通知から人間が拾うフローが必須

### Neutral / 将来の検討事項

- fix loop 上限 (デフォルト 3 回) は `harness-meta` の learning 集計で再評価する余地
  あり (例: 3 回打ち切り率が高すぎたら 5 回に拡張)
- Phase 8 の roadmap 更新条件 (Plan 除外) は `roadmap-tracker` 規約 (`.claude/rules/
  roadmap.md`) と整合性を保つ
- IDE 統合 (JetBrains MCP) を Phase 3 の Lint / 構造解析に組み込む余地は ADR-0024 で
  別途扱う

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 1. アーキテクチャパターン (Generator / Evaluator 分離、worktree 物理分離)
- [x] 7. ハーネス本体の中核設計 (実装ワークフローの 10 フェーズ構成)
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (`implementation-workflow` Skill 全体に依存)
- [x] 10. 長期的な制約 (今後 1 年以上、全実装 PR の駆動方式に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」(「implementation-workflow + code-reviewer
      の Generator/Evaluator 二段構成」) と一致。Plan / runbook / コーディング規約で済む
      話ではないことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0017 (ローカル Claude Code ポーリング駆動、Phase 8 で `pr-poller` 即時起動)
- ADR-0019 (`code-reviewer` 8 aspect + Coordinator、Phase 6 で呼出)
- ADR-0024 (MCP サーバ、Phase 3 で JetBrains MCP / Context7 MCP を利用)
- ADR-0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `.claude/rules/implementation-workflow.md` (10 フェーズ手順の Single Source of Truth)
- `.claude/rules/branch-naming.md` / `.claude/rules/merge-readiness.md` /
  `.claude/rules/pr-draft-policy.md` / `.claude/rules/spec-living-sync.md`
- `docs/harness/plan.md` §5.3 / §5.4.2 / R-13 / R-14 / R-15 / R-34
