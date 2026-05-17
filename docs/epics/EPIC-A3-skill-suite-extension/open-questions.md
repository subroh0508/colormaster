---
id: open-questions-EPIC-A3
title: EPIC-A3 未解決事項
status: living
last_updated: 2026-05-18
source_epic: EPIC-A3
---

# EPIC-A3 未解決事項

> **5 行以内 summary**: EPIC-A3 内の細粒度な保留事項。重要な決定に昇格したら ADR に移行
> (`docs/adr/`)、解決したら本ファイルに線引きして `decisions.md` に転記。
> append-only 運用とし、過去の問いを削除しない。

## 未解決一覧

| 起票日 | 内容 | 暫定方針 | 解決状態 | 解決時の移行先 |
|---|---|---|---|---|
| 2026-05-18 | `code-reviewer` 8 aspect のうち `visual-regression` / `design-tokens` は A10 (UI/UX 現状記録 EPIC) 完了後に enable する想定だが、A3 で完全実装する場合に **「skeleton 状態で配置 + A10 で activation flag を切替」** と **「A10 で aspect 本体を追加実装」** のどちらが運用しやすいか | A3-9 で 6 aspect (spec-conformance / test-quality / architecture / security / performance / code-quality) のみ完全実装し、`visual-regression` / `design-tokens` は SKILL.md に「A10 完了後に enable、activation flag は `code-reviewer-aspects.md` 側で管理」と明記する skeleton 配置で進める | 未解決 | A10 着手時に再評価して decisions.md / ADR (該当時) |
| 2026-05-18 | `pr-poller` の Renovate ラベル PR 検出と `dependency-upgrade` 起動の連結方式 | A3-7 (`dependency-upgrade`) と A3-11 (`pr-poller`) を直列実装、`pr-poller` SKILL.md に「`gh pr list --label dependencies` 等で Renovate PR 検出 → `dependency-upgrade` Skill 起動」のフロー図 + lock ファイル排他制御を明記。本格自動化 (`CronCreate` + `ScheduleWakeup`) は A4 で完成 | 未解決 | A4 で再評価して `pr-poller.md` / `harness-meta-criteria.md` |
| 2026-05-18 | `harness-bootstrap` archived 移動時の `CLAUDE.md` / `rules-index.md` 参照削除タイミング | A3-14 で `.claude/skills/archived/harness-bootstrap/` 移動と同 PR で参照削除。A3-8 (`implementation-workflow` Phase 0-9 完全実装) マージ後に着手 (本格化された専用 Skill が稼働状態であることを確認してから archived 化) | 未解決 | A3-14 マージ時に decisions.md に記録 |
| 2026-05-18 | `roadmap-tracker` の自動起動フック (`epic-author` / `implementation-workflow` Phase 8) を A3-12 で実装した場合、本 Epic 配下の後続 PR (A3-13 / A3-14) は自動起動で `roadmap.md` 更新可能か (ドッグフード可否) | A3-12 マージ後の A3-13 / A3-14 で自動起動を試行、未稼働時は mirror PR (`harness/roadmap-mirror-a3-13` 等) で手動代替。実証ログは `progress.md` に記録 | 未解決 | A3-12 マージ後に試行結果を decisions.md に記録 |
| 2026-05-18 | A3-1 〜 A3-7 (Group 1 / 2 の 7 PR) を完全並列実行する場合、orchestrator pane (subroh0508) の同時管理可能 per-task pane 数 (現状実証済 3-4 PR) を超える可能性 | 並列度は 3-4 PR/wave とし、Group 1 → Group 2 → Group 3 の順に wave で実行。Group 内は完全並列、wave 切替時に orchestrator が前 wave の merge 完了を確認 | 未解決 | 実際の並列実行で per-task pane の管理負荷を計測、`orchestrator-criteria.md` に並列度上限を追記 |

## 解決済 (過去ログ)

| 起票日 | 内容 | 解決日 | 解決方法 | 移行先 |
|---|---|---|---|---|
