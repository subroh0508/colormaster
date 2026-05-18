---
id: dry-runs-index
title: ハーネス改善提案 dry-run 索引
status: living
last_updated: 2026-05-19
---

# ハーネス改善提案 dry-run 索引

> **5 行以内 summary**: `harness-meta` Skill (A3-5 で本格化) が
> ハーネス改善提案を commit + push する前に実施する dry-run 結果の索引。
> 1 PR (or 1 改善提案バッチ) = 1 ファイル (`YYYY-MM-DD-pr-NNN.md`)、Single Source of Truth。
> ファイルフォーマットは `docs/harness/dry-runs/template.md` 参照、必須条件は
> `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 を参照。

## 索引 (起票時に追記)

| 日付 | PR # | 関連 retrospective | 対象提案数 | 採用 / 破棄 / エスカレーション | 備考 |
|---|---|---|---|---|---|
| 2026-05-18 | [#144](https://github.com/subroh0508/colormaster/pull/144) | 本 PR merge 後に起票 (`harness/learnings-batch-2026-W20-part4` 等) | 2 (`[skill]` orchestrator + `[rule]` orchestrator-criteria.md) | adopt (Case A 採用、Case B 破棄) | ORCH-1 (cmux 並列 orchestration、仕様 1-8 + 教訓 10 系統)、skill-creator 経由生成 + 100-point rubric 97/100、dry-run 自体が本 INDEX 起票第 1 件 |
| 2026-05-18 | pending (`harness/dry-run-monitor-dedup` 本 PR) | pending (改修候補 #2 採用 PR の retro で確定) | 1 (`[skill]` orchestrator SKILL.md §Phase 3 + §Monitor 思考動詞辞書 への v3 SoT 追加) | escalate (subagent 並列比較が cmux + persist file 環境依存で原理的困難、retrospective 観測ベースで orchestrator subroh0508 委任) | 改修候補 #2 (Monitor dedup v1 → v2 → v3 ロジック進化、workspace prefix + tail -3、旧 workspace:2 累計 14 PR 観測由来)、後続 SoT 反映 PR は subroh0508 判断後に別途起票 |
| 2026-05-19 | pending (`harness/3-axis-eval-framework` 本 PR、PLAN-002) | 本 PR merge 後に起票 | 8 (`[rule]` × 4 + `[skill]` × 2 + 新規 ADR-0028 + 新規 golden-set.md) | 9 通り組合せ別レビュー指針 **#1 (Approve 推奨)**、verdict ラベルは ADR-0028 で廃止 | **self-bootstrap dry-run** (本 PR の 3 軸定量評価を本 PR 改修内容で評価する循環構造)、N=1 手動代替 (subagent 並列 N=10 以上は A4 本格化以降)、baseline は legacy `monitor-dedup` dry-run のレトロフィット適用、改善度 0/3 再発 (≤ 30% ✅) + 副作用 0/5 退化 (≤ 20% ✅) + 再現性 N=1 制約付き ✅ |

## ファイル運用

- 1 PR (or 1 改善提案バッチ) = 1 ファイル (`YYYY-MM-DD-pr-<n>.md`)
- 必須条件: `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件 §dry-run 必須 表に該当する提案が対象
- 不要条件: 同 §dry-run 不要 表に該当する提案は本ディレクトリ非対象 (`📝 harness-meta フィードバック` に skip 理由明示)
- 自動生成: `harness-meta` Skill (A3-5 / PR #156 で本格化済、`harness-bootstrap` は A3-14 で archived 化済)
- 集約 push: 通常は retro 集約消化 PR と同一ブランチで push、独立 PR が必要な場合は `harness/dry-run-<purpose>` ブランチ
- 索引追記タイミング: dry-run ファイル生成時 (commit + push 前) に本 INDEX.md に 1 行追加

## 関連

- `.claude/rules/harness-meta-criteria.md` §dry-run 必須条件
- `.claude/rules/retrospective-format.md` (元 retrospective フォーマット)
- `.claude/skills/harness-meta/SKILL.md` (A3-5 / PR #156 で本格化済、`harness-bootstrap` は A3-14 で archived 化済 `.claude/skills/archived/harness-bootstrap/`)
- `docs/harness/learnings/INDEX.md` (元 retrospective 索引)
- `docs/harness/plan.md` §5.4.5 (harness-meta Skill 責務)
