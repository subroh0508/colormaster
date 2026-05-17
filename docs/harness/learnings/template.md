---
id: learning-pr-NNN
title: PR #NNN レトロスペクティブ (<簡潔な要約>)
type: learning
status: draft
related_pr: NNN
related_plan: PLAN-NNN
related_epic: EPIC-NNN
generated_at: YYYY-MM-DDTHH:MM:SSZ
generator: pr-retrospective Skill (vX.Y.Z) | pr-retrospective (skeleton、本ペインで手動代替実行)
---

# PR #NNN レトロスペクティブ

> 生成: pr-retrospective Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 関連 Plan: PLAN-NNN / 関連 Epic: EPIC-NNN
> 対象 PR: [<commit subject> #NNN](https://github.com/<owner>/<repo>/pull/NNN) (squash merged at YYYY-MM-DDTHH:MM:SSZ、commit `<sha>`、from head `<sha>`)
> 差分: <ファイル数> ファイル / +<追加> / -<削除> (<diff 行数> diff 行)、commits: <初回> + <fix loop> (該当時)

## ✅ Keep (継続したいこと)

<!-- 継続したい運用 / 構造 / 判断 (成功要因の言語化)。最低 3 項目、推奨 5-10 項目。consensus 表記 (どの aspect が指摘したか) を併記推奨。 -->

- ...
- ...
- ...

## ⚠️ Problem (詰まったこと / 制約)

<!-- 詰まった点 / 制約 / latent contradiction。最低 3 項目、推奨 5-10 項目。consensus 表記併記推奨。 -->

- ...
- ...
- ...

## 🚀 Try (次回からの改善案)

<!-- 次回 PR / フェーズで実施したい改善 (Plan / Epic / ADR / rule 単位まで具体化)。最低 3 項目、推奨 5-10 項目。 -->

- ...
- ...
- ...

## 📊 指標

| 指標 | Before | After | Δ | 備考 |
|---|---|---|---|---|
| Line coverage | N/A | N/A | — | Kover (A7 完了後) / N/A (A7 完了前) |
| Branch coverage | N/A | N/A | — | 同上 |
| Spec coverage | N/A | N/A | — | Konsist `@Spec` カバー率 (A7 完了後) / N/A |
| Mutation score | N/A | N/A | — | PITest (A7 完了後) / N/A |
| Lint 違反数 | N/A | N/A | — | Spotless / ktlint / detekt / markdownlint-cli2 (A6 完了後) / N/A |

## 🤖 ハーネス改善提案

<!-- harness-meta が parse する正規構造。プレフィックス 4 種: [rule] / [skill] / [template] / [remove] -->

- [ ] `[rule]` ...
- [ ] `[skill]` ...
- [ ] `[template]` ...

## 📝 harness-meta フィードバック

<!-- harness-meta が後から追記。提案 → 結果の往復ログを 1 ファイル内で完結。空でも見出しを残す。 -->

### YYYY-MM-DD <PR ID> で消化 (採用)

| 提案 | <PR ID> での消化内容 |
|---|---|

### YYYY-MM-DD <PR ID> で見送り (後続フェーズへ)

| 提案 | 見送り理由 / 移行先 |
|---|---|

### YYYY-MM-DD <PR ID> で保留 (要再評価)

| 提案 | 保留理由 |
|---|---|
