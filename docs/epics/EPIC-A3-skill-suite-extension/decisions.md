---
id: decisions-EPIC-A3
title: EPIC-A3 細粒度決定の記録
status: living
last_updated: 2026-05-18
source_epic: EPIC-A3
---

# EPIC-A3 細粒度決定の記録

> **5 行以内 summary**: EPIC-A3 内で発生した細粒度の意思決定の記録。ADR に昇格するほどでは
> ない判断 (Skill 分割粒度 / 並列グルーピング / 実装順依存) を蓄積。ADR 起票基準を
> 満たすものは `docs/adr/` に昇格してリンクする。

## 決定一覧

| 決定日 | 決定内容 | 背景 | 影響範囲 | 関連 ADR (昇格時) |
|---|---|---|---|---|
| 2026-05-18 | A3 を 14 PR (A3-0 起票 + A3-1 〜 A3-14) に分割 | A2 (5 PR + 計画外 A2-6) のレビュー負荷実績 (1 PR = 35 ファイル / +3,479 行が上限近く) を踏まえ、本 Epic は 1 PR = 1 Skill 単位に分割し、レビュー負荷を平準化。A1 レトロ Try「巨大 PR の aspect 並列 review における入力分割」と整合 | EPIC-A3 全体 | — (Epic 内分割でありアーキ判断ではない) |
| 2026-05-18 | A3 配下 14 PR を **3 グループ並列実行** (Group 1: feature-request / bug-fix / refactor / adr-author、Group 2: harness-meta / harness-evolution / dependency-upgrade、Group 3: implementation-workflow / code-reviewer / pr-retrospective / pr-poller / ui-snapshot / roadmap-tracker) | 各 Skill は独立した `.claude/skills/<name>/SKILL.md` ファイルを生成・編集するため touch ファイル重複ゼロで並走可能。orchestrator pane (subroh0508) の同時管理可能 per-task pane 数 (現状実証済 3-4 PR) を踏まえ、グループ内は完全並列、グループ間は wave 切替 (前 wave の merge 完了確認) で進める | EPIC-A3 全体、特に並列実行スケジューリング | — |
| 2026-05-18 | A3-10 (`pr-retrospective`) は A3-5 (`harness-meta`) 完成後に着手 (直列依存) | `pr-retrospective` の learning 生成ロジックは `harness-meta` のフィードバック追記ロジックと SKILL.md レベルで連動。A3-5 完成前に A3-10 を実装すると harness-meta フィードバックフローが不整合になる可能性が高い | A3-10 着手タイミング | — |
| 2026-05-18 | A3-11 (`pr-poller`) は A3-7 (`dependency-upgrade`) 完成後に着手 (直列依存) | `pr-poller` の Renovate ラベル PR 検出 → `dependency-upgrade` 起動フローは A3-7 完成前に実装すると起動先 Skill が存在しないため、テストできない | A3-11 着手タイミング | — |
| 2026-05-18 | A3-14 (`harness-bootstrap` archived 化) は A3-8 (`implementation-workflow` Phase 0-9 完全実装) 完成後に着手 (直列依存) | `harness-bootstrap` archived 化は「本格化された専用 Skill が稼働状態」が前提。A3-8 で `implementation-workflow` が完全実装されていれば、A3 配下の後続 PR (A3-9 以降) を `implementation-workflow` Phase 0-9 で実装できる状態を確認できる。逆に A3-8 未完成のまま archived 化すると、汎用 Skill 失効 + 専用 Skill 未完成の隙間が発生 | A3-14 着手タイミング | — |
| 2026-05-18 | `code-reviewer` 8 aspect のうち `visual-regression` / `design-tokens` は **skeleton 状態で配置 + A10 完了後に enable** で進める | A10 で Roborazzi baseline + DESIGN.md + UI Inventory が揃わないと `visual-regression` / `design-tokens` aspect は実行不可。skeleton 状態で配置しておくことで、A10 完了後の activation 切替 (SKILL.md の `enabled: false` → `true`) が最小差分で済む | `.claude/skills/code-reviewer/SKILL.md`、`.claude/rules/code-reviewer-aspects.md` | — |
| 2026-05-18 | `harness-bootstrap` archived 化時、`.claude/skills/archived/README.md` に「`harness-bootstrap` は A3 完了 (PR #NNN) で archived 化、本格 Skill 群へ移行」を 1 行追記 | A2 archived 化実績は無し (本 Epic が初の archived 化)。後続 archived 化 (将来 Skill 撤去時) のテンプレ確立も兼ねる | `.claude/skills/archived/README.md` | — |
