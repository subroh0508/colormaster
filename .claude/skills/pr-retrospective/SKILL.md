---
name: pr-retrospective
description: |
  対象 PR の diff / comments / reviews / CI ログ / Skill 実行ログ / 三層指標差分 / 関連 Plan・Epic
  を収集し、docs/harness/learnings/YYYY-MM-DD-pr-<n>.md を日本語の構造化フォーマットで生成する。
  harness/learnings-batch-YYYY-WW ブランチに集約し、週次 (or 件数到達時) に PR として起票する。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §4.4 / §5.3
related_rules:
  - .claude/rules/retrospective-format.md
  - .claude/rules/pii.md
  - .claude/rules/template-language.md
---

# pr-retrospective (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。

## 役割

- PR メタ情報・diff・comments・CI ログ・Skill 実行ログ・三層指標差分 (Kover / Konsist / PITest) を収集
- 関連 Plan / Epic / ADR の情報を統合
- `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を `.claude/rules/retrospective-format.md` の構造化フォーマット (日本語見出し) で生成
- `harness/learnings-batch-YYYY-WW` ブランチに集約 push、週次 (or 件数到達時) に PR としてまとめて起票

## 出力フォーマット (§5.5 例)

- `## ✅ Keep (継続したいこと)`
- `## ⚠️ Problem (詰まったこと / 制約)`
- `## 🚀 Try (次回からの改善案)`
- `## 📊 指標` (Before/After 差分テーブル)
- `## 🤖 ハーネス改善提案` (harness-meta が parse する正規構造、`[rule] / [skill] / [template] / [remove]` プレフィックス)
- `## 📝 harness-meta フィードバック` (空、harness-meta が後から追記)

## Gotchas

- **PR コメントは出さない**。Single Source of Truth は learning ファイル (§4.4)。
- PII / secrets が含まれないよう `.claude/rules/pii.md` の redaction を必ず通す。
- ファイル生成と同時に push する (R-12 の learning ロスト対策)。

## 関連

- `docs/harness/plan.md` §4.4 / §5.4.5
- `.claude/rules/retrospective-format.md`
