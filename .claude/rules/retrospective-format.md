---
id: rules-retrospective-format
title: pr-retrospective が生成する learning ファイルの構造化フォーマット
status: stable
last_updated: 2026-05-17
paths:
  - "docs/harness/learnings/**/*.md"
  - ".claude/skills/pr-retrospective/**"
  - ".claude/skills/pr-poller/**"
  - ".claude/skills/harness-meta/**"
related_adrs:
  - ADR-0026
  - ADR-0027
related_plan: docs/harness/plan.md §4.4 / §5.4.5 / R-12
---

# retrospective-format.md — learning ファイル構造化フォーマット

> `pr-retrospective` Skill が `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を生成する際の
> 構造化フォーマット定義。`harness-meta` は本フォーマットに依存して提案セクションを parse する。
> harness-meta / harness-evolution / pr-poller の入力規格として SoT 化。

## ファイル名規約

```text
docs/harness/learnings/YYYY-MM-DD-pr-<n>.md
```

- `YYYY-MM-DD` は PR のマージ日 (UTC または JST、`generated_at` で明示)
- `<n>` は PR 番号 (`#NNN` の `NNN` 部分)
- 1 PR = 1 ファイル
- 補助ファイル: `docs/harness/learnings/INDEX.md` (索引) / `flaky-tests.md` (フレーキー再現条件蓄積、A2-1 で追加済)

## frontmatter (必須)

```yaml
---
id: learning-pr-NNN
title: PR #NNN レトロスペクティブ (<簡潔な要約>)
type: learning
status: draft | reviewed | actioned
related_pr: NNN
related_plan: PLAN-NNN | —
related_epic: EPIC-NNN | —
generated_at: YYYY-MM-DDTHH:MM:SSZ
generator: pr-retrospective Skill (vX.Y.Z) | pr-retrospective (skeleton、本ペインで手動代替実行)
---
```

- 配列は block 形式必須 (`docs-structure.md` frontmatter 規約)
- `related_plan` / `related_epic` は該当時のみ記入、不在は `—` で明示 (空欄禁止)
- `generator` 値は skeleton 段階 (A3 本格化前) は手動代替実行を明示

## 本文構造 (日本語見出し、§5.5 例)

```markdown
# PR #NNN レトロスペクティブ

> 生成: pr-retrospective Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 関連 Plan: PLAN-NNN / 関連 Epic: EPIC-NNN
> 対象 PR: [<commit subject> #NNN](https://github.com/<owner>/<repo>/pull/NNN) (squash merged at YYYY-MM-DDTHH:MM:SSZ、commit `<sha>`、from head `<sha>`)
> 差分: <ファイル数> ファイル / +<追加> / -<削除> (<diff 行数> diff 行)、commits: <初回> + <fix loop> (該当時)

## ✅ Keep (継続したいこと)

- ...

## ⚠️ Problem (詰まったこと / 制約)

- ...

## 🚀 Try (次回からの改善案)

- ...

## 📊 指標

| 指標 | Before | After | Δ | 備考 |
|---|---|---|---|---|
| Line coverage | 100.00% | 100.00% | ±0 | Kover (A7 完了後) / N/A (A7 完了前) |
| Branch coverage | 100.00% | 100.00% | ±0 | 同上 |
| Spec coverage | 98.4% | 100.0% | +1.6 | Konsist `@Spec` カバー率 (A7 完了後) / N/A |
| Mutation score | 86.2% | 88.1% | +1.9 | PITest (A7 完了後) / N/A |
| Lint 違反数 | 0 | 0 | ±0 | Spotless / ktlint / detekt / markdownlint-cli2 (A6 完了後) / N/A |
| (任意) ファイル数 / 行数 / 他 PR 固有指標 | ... | ... | ... | ... |

## 🤖 ハーネス改善提案

<!-- harness-meta が parse する正規構造 -->

- [ ] `[rule]` ...
- [ ] `[skill]` ...
- [ ] `[template]` ...
- [ ] `[remove]` ...

## 📝 harness-meta フィードバック

<!-- harness-meta が後から追記。提案 → 結果の往復ログを 1 ファイル内で完結 -->

### YYYY-MM-DD <PR ID> で消化 (採用)

| 提案 | <PR ID> での消化内容 |
|---|---|

### YYYY-MM-DD <PR ID> で見送り (後続フェーズへ)

| 提案 | 見送り理由 / 移行先 |
|---|---|

### YYYY-MM-DD <PR ID> で保留 (要再評価)

| 提案 | 保留理由 |
|---|---|
```

## 各セクションの規約

### ✅ Keep / ⚠️ Problem / 🚀 Try

- **Keep**: 継続したい運用 / 構造 / 判断 (成功要因の言語化)
- **Problem**: 詰まった点 / 制約 / latent contradiction (PR #121 plan.md L1125 / PR #117 「既存」誤宣言のような構造的課題)
- **Try**: 次回 PR / フェーズで実施したい改善 (Plan / Epic / ADR / rule 単位まで具体化)
- 各セクション最低 3 項目、推奨 5-10 項目
- 各項目に **consensus 表記 (どの aspect が指摘したか)** を併記推奨 (例: `consensus: spec-conformance + architecture`)

### 📊 指標

- 三層指標 (Line / Branch / Spec / Mutation) は A7 完了前は `N/A` 必須 (空欄禁止)
- Lint 違反数は A6 完了前は `N/A`
- PR 固有指標 (ファイル数 / 行数 / Critical findings / binary checklist 通過率) を任意で追加可
- Before / After / Δ / 備考 の 4 列固定

### 🤖 ハーネス改善提案

`harness-meta` は以下のプレフィックスを parse して分類する:

| プレフィックス | 意味 | 採用時の対応先 rule / Skill |
|---|---|---|
| `[rule]` | `.claude/rules/*.md` の新規追加・改修 | 該当 rule 直接編集 PR (`harness-meta-criteria.md` 採用判定) |
| `[skill]` | `.claude/skills/*/` の新規追加・改修 | `example-skills:skill-creator` 経由 (`skill-authoring.md`) |
| `[template]` | テンプレート Markdown 改修 | テンプレファイル直接編集 PR |
| `[remove]` | 未使用 rule / dormant Skill の撤去候補 | `harness-meta-criteria.md` 撤去基準充足時のみ |

- 各項目は **チェックボックス `[ ]` で起票**、`harness-meta` 採用時に `[x]` + 採用先 PR リンクを追記
- 各項目に **採用判定基準該当箇所 (1〜5、`harness-meta-criteria.md`)** を併記推奨

### 📝 harness-meta フィードバック

- 採用 / 見送り / 保留の 3 表を含む
- 各表は `harness-meta` Skill が追記、人間レビューでも追記可
- 1 ファイル内で「提案 → 結果」の往復ログを完結 (R-12 learning ロスト対策)

## redaction チェックポイント (Skill 出力前)

`pr-retrospective` Skill が learning ファイルを生成する直前に以下を確認:

- [ ] CI ログ抜粋 / `gh pr view` 出力 / MCP 結果に PII / secrets が含まれていない (`.claude/rules/pii.md` / `secrets.md` redaction)
- [ ] 「Reviewed by X」「Authored by Y」等の自然文に display name が混入していない
- [ ] スタックトレース / エラーメッセージに `sub` claim 値が含まれていない
- [ ] 「📝 harness-meta フィードバック」セクションは空でも見出しを残す (`harness-meta` が後追記する場所)
- [ ] frontmatter 配列が block 形式
- [ ] 5 行 summary が冒頭 blockquote (>) に存在

検出時は `[REDACTED-*]` プレースホルダ置換してから出力 (`pii.md` / `secrets.md` 同等)。

## PR コメント post の禁止

- **PR コメントとして retrospective を post しない**、learning ファイルが Single Source of Truth (§4.4)
- `code-reviewer` の Coordinator レビューコメントは別運用 (`code-reviewer-aspects.md` 参照)、retrospective は merge **後** に生成

## learning ファイルの集約と週次 PR

- 各 learning ファイルは `harness/learnings-batch-YYYY-WW` ブランチに集約 (`YYYY-WW` は ISO 週番号)
- 週次 (or 件数閾値到達時) に `harness.md` テンプレで PR 起票 → orchestrator approve → merge
- `harness-meta` 起動閾値 (10 件 / 7 日、`pr-poller.md` / `harness-meta-criteria.md`) と連動

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク** (commonmark + SnakeYAML): frontmatter 必須キー検証、5 行 summary 存在、ハーネス改善提案プレフィックス 4 種の正規表現マッチ、redaction 漏れ検出
- **GitHub Actions**: learning PR の merge 直前に上記検証を CI 実行 (A6 / detekt 統合と同時)
- **harness-meta Skill**: 提案セクションを parse して採用 / 見送り / 撤去判定 (A4 で本格化)

## Gotchas

- **PR コメントは出さない**、learning ファイルが SoT (§4.4)
- **PII / secrets redaction は必ず通す** (`.claude/rules/pii.md` / `secrets.md`)
- **`📝 harness-meta フィードバック` セクションは空でも見出しを残す** (harness-meta が後追記する場所)
- **三層指標 N/A 明記**: A7 完了前は空欄ではなく `N/A` 明示 (A1 レトロ Problem 防止)
- **consensus 表記推奨**: どの aspect が指摘したかを Keep / Problem 各項目に併記すると harness-meta の重み付け精度が上がる
- **手動代替実行時 (A3 本格化前) は `generator` 値で明示**: `pr-retrospective (skeleton、本ペインで手動代替実行)`
- **対象 PR の commit / merge 情報は冒頭 blockquote で必須記載**: PR URL / commit sha / マージ日時 / 差分ファイル数 + 行数

## 関連

- ADR 0026 (harness-evolution Skill 採用、retrospective format との連携)
- ADR 0027 (テンプレ言語、日本語化方針)
- `docs/harness/plan.md` §4.4 / §5.4.5 / R-12
- `.claude/rules/{pr-poller,harness-meta-criteria,harness-evolution,pii,secrets,docs-structure}.md`
- `.claude/skills/{pr-retrospective,pr-poller,harness-meta}/SKILL.md`
- `docs/harness/learnings/{INDEX,flaky-tests}.md` (補助ファイル)
