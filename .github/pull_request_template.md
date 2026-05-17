<!--
このファイルは harness 用のデフォルト PR テンプレート。
他 type の PR を作るときは `gh pr create --template <type>.md` で明示指定:
  - feature   : 新機能追加
  - bugfix    : バグ修正
  - refactor  : 振る舞いを変えないコード整理
  - dependency-upgrade : Renovate 等の依存更新
  - docs      : ドキュメント変更のみ
詳細は .github/PULL_REQUEST_TEMPLATE/ を参照、規約は docs/harness/plan.md §4.8。
-->

---
type: harness
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN
- Epic: EPIC-NNN (該当時)
- ADR: ADR-NNNN (該当時)
- 関連 Issue: #N
- 関連 learning: `docs/harness/learnings/YYYY-MM-DD-pr-N.md` (該当時)
- harness-meta feedback リンク (該当時)

## 変更内容

| 区分 | パス | 変更 |
|---|---|---|
| <例: rule> | `.claude/rules/<rule>.md` | <要約> |
| <例: skill> | `.claude/skills/<skill>/SKILL.md` | <要約> |

## ハーネス改修テーマ

<harness-meta / harness-evolution / 手動改修いずれの起源か、改修の方向性>

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>

## テスト

- [ ] `./gradlew check` グリーン
- [ ] 三層指標差分: line/branch 差分 100%、Spec coverage 差分 100%、mutation score +N% (該当時)
- [ ] (UI 変更時) Roborazzi baseline 比較グリーン or 承認済

## レビュー観点

<重点的に見てほしい箇所、code-reviewer の特定 aspect で注意すべき点>

## チェックリスト

- [ ] `.claude/rules/` の関連規約を確認した
- [ ] frontmatter 必須キーを設定した (Markdown 変更時)
- [ ] 関連 docs (要件 / 基本設計 / 詳細設計 / ADR / roadmap) を更新した
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン)
