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
| 2026-05-17 | [#129](https://github.com/subroh0508/colormaster/pull/129) | — (EPIC-A2 計画外挿入、A2-6 として roadmap 事後反映、1 PR 完結のため Plan 不要) | EPIC-A2-rules-docs-extension | 12 (`[rule]` 8 / `[skill]` 2 / `[template]` 1 + 再掲 1) | 0 (本 PR がレトロ起票元、A2-3 / A3 / A4 / A6 で順次消化見込み)。本 PR の特徴: ① 初期方針 (ADR-0028 起票 + R-15 緩和 + roadmap A2-6 正式項目追加) を redirect で settings.json 1 ファイル change のみに縮小、② classifier denial 2 件 (`git add && git commit` + `pbcopy` 経由迂回) を orchestrator pane の手動実行で迂回、③ mirror PR #131 (merge commit `e431eec`) は permission rule master 反映後で classifier ブロックゼロ自走完走 |
| 2026-05-17 | [#135](https://github.com/subroh0508/colormaster/pull/135) | — (EPIC-A2 直下、1 PR 完結のため Plan 不要) | EPIC-A2-rules-docs-extension | 21 (`[rule]` 17 / `[skill]` 3 / `[template]` 1) | 4 (code-reviewer Improvement 18 件中、stable 昇格と矛盾する SoT 性違反 4 件を本 PR 内 fix loop commit `23ac895` で即時消化 ✅: docs-structure MD040 / branch-naming 表記揺れ / CLAUDE.md lookup table 7 行追加 / merge-readiness mirror aspect セット記述統一)、残 14 件 + 本レトロ Try 7 件 = 計 21 件は A3 / A4 / A6 で順次消化見込み。EPIC-A2 配下最後の PR、本 PR + mirror PR #136 で **EPIC-A2 status を `completed` に昇格** (A2-1〜A2-6 全 6 PR merge 済)。A2-6 (PR #129) で permission 拡張済の効果で classifier ブロックゼロを実証 (`gh pr ready 135` / `gh pr merge 135 --merge` / `gh pr ready 136` / `gh pr merge 136 --merge` / `git push` 全て直接実行)。PR #121 レトロ Try 4 件 (Phase 0 fetch / binary checklist 5-7 項目 / NG 例コメント / commit-message subject セクション化) を全件反映 |
| 2026-05-17 | [#139](https://github.com/subroh0508/colormaster/pull/139) | — (EPIC-A2 完了後の harness-meta 相当タスク、Plan 不要) | EPIC-A2-rules-docs-extension | 11 (`[rule]` 5 / `[skill]` 4 / `[template]` 1 / `[rule]` 1 採用バイアス対策) + 8 retro × 採用 + 見送り 表 16 表 | 約 40 (8 retro 集約消化、dedupe 後 約 47 件中、rule 19 件改修 + template 3 件改修 + 横断 learnings 2 件新規 + 8 retro 📝 一括追記) + PR #140 で +4 採用 = 計 約 44 件採用 ✅。code-reviewer 4 aspect (Markdown only PR `code-reviewer-aspects.md` §aspect 動的選択ルール に従い 4/8 skip) 並列で Critical 0 / Improvement 2、即時消化 1 件 (architecture #1 / merge-readiness L57 クロスリンク、fix loop commit `c9a0972`) + 持ち越し 1 件 (code-quality #1 / `docs-structure.md:136-139` blockquote)、binary checklist 28/28 通過。orchestrator 事前承認テキスト (`Phase 1-9 含む self-merge を本指示で明示的に事前承認 (R-15 該当)`) で self-merge 完走、classifier ブロックゼロ。EPIC-A2 配下の 8 retro (#117 / 119 / 121 / 123 / 125 / 126 / 129 / 135) の未消化提案を 1 PR で集約消化、ADR 補強 (ADR-0020 / 0021 / 0023 / 0027) のみ PR #140 (PR-2) に分割 |
| 2026-05-17 | [#140](https://github.com/subroh0508/colormaster/pull/140) | — (PR #139 から分割した ADR 補強 PR、Plan 不要) | EPIC-A2-rules-docs-extension | 3 (`[rule]` 3、PR #139 本体 retro と一部重複) | 4 (ADR-0020 / 0021 / 0023 / 0027 補強、PR #119 レトロ Try 由来 4 件すべて消化)。code-reviewer 4 aspect 並列で Critical 0 / Improvement 0、binary checklist 21/21 通過、fix loop ゼロ完走。`accepted` ADR の補足追加 (Decision 改訂ではなく) を `.claude/rules/adr.md` §ADR 化見送りの理由テンプレ 3 条件 (撤回コスト低 / scope 限定 / 既存 rule 本体改定なし) で許容範囲化、新規 ADR (ADR-0028) 起票見送り。PR #119 レトロ Try 累計採用率 = 1 (A2-1) + 2 (PR #139) + 4 (PR #140) = 7/11 = 64%。本 retrospective は cross-reference 形式、本体は [`2026-05-17-pr-139.md`](./2026-05-17-pr-139.md) に集約 |

## 横断的 learnings (1 PR = 1 ファイルとは別建て)

| ファイル | 用途 |
|---|---|
| `flaky-tests.md` | 再現性が低いテストの発生条件・回避策・恒久対策メモを蓄積 (A1 レトロ Try で起票) |
| `classifier-denials.md` | classifier (Safety-Check layer) の block / preemptive denial 記録を蓄積、A3 / A6 で運用ガイダンス反映材料化 (PR #129 レトロ Try で起票) |
| `template.md` | 1 PR = 1 learning ファイルの暫定テンプレ (pr-retrospective Skill 本格化前、A3 完了で自動生成切替) |

## ファイル運用

- 1 PR = 1 ファイル (`YYYY-MM-DD-pr-<n>.md`)
- 自動生成: `pr-poller` → `pr-retrospective` 経路 (A4 で `CronCreate` / `ScheduleWakeup` 自動化完成)
- 集約 push: `harness/learnings-batch-YYYY-WW` ブランチ、週次 (or 件数到達時) に PR 起票
- `harness-meta` の後追記: feedback セクションを更新

## 関連

- `.claude/rules/retrospective-format.md`
- `.claude/skills/{pr-poller,pr-retrospective,harness-meta}/SKILL.md`
- `docs/harness/plan.md` §4.4
