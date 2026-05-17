---
id: learnings-index
title: KPT learnings 索引
status: skeleton
last_updated: 2026-05-17
---

# KPT learnings 索引

> **5 行以内 summary**: `pr-retrospective` Skill が生成する PR ごとの learning ファイル
> (`YYYY-MM-DD-pr-<n>.md`) の索引。1 PR = 1 ファイル、Single Source of Truth。
> ファイルフォーマットは `.claude/rules/retrospective-format.md` 参照。
> `harness-meta` Skill が後から `📝 harness-meta フィードバック` セクションを追記する。

## 索引 (起票時に追記)

| 日付 | PR # | 関連 Plan | 関連 Epic | actionable 提案数 | 採用提案数 |
|---|---|---|---|---|---|
| 2026-05-17 | [#117](https://github.com/subroh0508/colormaster/pull/117) | — | EPIC-000-harness-foundation | 15 | 11 (A2-1 PR で消化、本 PR `📝 harness-meta フィードバック` の採用 11 件と一致: rules-index 正規化 / template-language paths 削除 / harness.md PR テンプレ / mcp-usage Gotchas / db-protection `.dockerignore` TODO / code-reviewer SKILL.md PII redaction + visual-regression enable 手順 / commit-message rule 新規 + 50→72/100 緩和 / ADR README 索引拡充 / flaky-tests.md 起票 / roadmap rule 手動マージ時更新 / feature.md, bugfix.md 三層指標差分注記) |
| 2026-05-17 | [#119](https://github.com/subroh0508/colormaster/pull/119) | PLAN-001-adr-bootstrap | EPIC-000-harness-foundation | 11 (Recommended 10 + 三層指標 N/A) | 1 (A2-1 で ADR 索引起票根拠 §4.5 拡充済 ✅)、残 10 件は A2-2 / A2-3 / A6 で対応予定 |
| 2026-05-17 | [#121](https://github.com/subroh0508/colormaster/pull/121) | — (EPIC-A2 直下、1 PR 完結のため Plan 不要) | EPIC-A2-rules-docs-extension | 14 | 0 (本 PR が成果物のためまだ消化対象なし、A2-2 / A2-3 / A3 / A4 / A6 で順次消化見込み)、加えて A1 レトロ 11 件中 11 件を本 PR 内で消化済 ✅ |
| 2026-05-17 | [#123](https://github.com/subroh0508/colormaster/pull/123) | — (EPIC-A2 直下、1 PR 完結のため Plan 不要) | EPIC-A2-rules-docs-extension | 14 | 2 (Improvement #6 Gotchas → 注意事項、#7 持ち越し先 統一を本 PR 内で消化 ✅) + 1 (Improvement #8 `Open Questions` 翻訳は A2-3 持ち越し) + 5 (Improvement #1〜#5 は A2-3 / 後続 PR)。レトロ追加提案 14 件 (うち template-language / implementation-workflow / roadmap rule / docs-structure / pr-retrospective 自動 trigger / code-reviewer aspect サブセット選択等は A2-3 / A3 / A4 で消化見込み) |
| 2026-05-17 | [#125](https://github.com/subroh0508/colormaster/pull/125) | — (EPIC-A2 直下、Plan 不要) | EPIC-A2-rules-docs-extension | 13 (`[rule]` 7 / `[skill]` 5 / `[template]` 1) | 0 (本 PR がレトロ起票元、A2-3 / A3 / A4 / A6 で順次消化見込み)。A2-1 レトロ (PR #121) の 14 件中、本 PR で間接消化分 = rules-index status 正規化 (`stable (A2-2)` 追加) / `.dockerignore` 必須 5 項目を `db-protection.md` 機械検証セクションに引用 / firebase-boundary ⇄ no-firebase 二段運用 rule 実装 / `adr.md` SSoT 改訂規約 / `epic.md` decisions.md 規約 — 計 5 件 |
| 2026-05-17 | [#126](https://github.com/subroh0508/colormaster/pull/126) | — (EPIC-A2 直下、1 PR 完結のため Plan 不要) | EPIC-A2-rules-docs-extension | 14 (Recommended) | 0 (本 PR が成果物のためまだ消化対象なし、A2-2 / A2-3 / A3 / A4 / A6 / C5 で順次消化見込み)。Critical 4 件 (architecture aspect: DIP 違反方向 / `core/database` 未定義 / restore 主体誤り / overview Mermaid 非対称) は本 PR 内 fix loop commit `642c46e` で全件解消 ✅、classifier preemptive denial 1 件 (mirror PR の `docs/harness/roadmap.md` merge note 段落) は orchestrator 手動追記で補完予定 |

## 横断的 learnings (1 PR = 1 ファイルとは別建て)

| ファイル | 用途 |
|---|---|
| `flaky-tests.md` | 再現性が低いテストの発生条件・回避策・恒久対策メモを蓄積 (A1 レトロ Try で起票) |

## ファイル運用

- 1 PR = 1 ファイル (`YYYY-MM-DD-pr-<n>.md`)
- 自動生成: `pr-poller` → `pr-retrospective` 経路 (A4 で `CronCreate` / `ScheduleWakeup` 自動化完成)
- 集約 push: `harness/learnings-batch-YYYY-WW` ブランチ、週次 (or 件数到達時) に PR 起票
- `harness-meta` の後追記: feedback セクションを更新

## 関連

- `.claude/rules/retrospective-format.md`
- `.claude/skills/{pr-poller,pr-retrospective,harness-meta}/SKILL.md`
- `docs/harness/plan.md` §4.4
