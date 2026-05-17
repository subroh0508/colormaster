---
id: rules-code-reviewer-aspects
title: code-reviewer 8 aspect の binary eval checklist と Coordinator 形式
status: skeleton
last_updated: 2026-05-17
paths:
  - ".claude/skills/code-reviewer/**"
related_plan: docs/harness/plan.md §5.3 / §5.4.3 / ADR 0019
---

# code-reviewer-aspects.md — 8 aspect 規約

> `code-reviewer` Skill の 8 aspect (spec-conformance / test-quality / architecture / security /
> performance / code-quality / visual-regression / design-tokens) のチェック項目と
> Coordinator のレビューコメント形式を規定。詳細実装は A3 で本格化。

## 8 aspect 概要

| aspect | 状態 | system prompt の独立観点 |
|---|---|---|
| `spec-conformance` | B0 雛形 / A3 本格化 | `@Spec` annotation の存在、SPEC-ID 整合性、frontmatter `related_specs` 有効性、設計書 ⇄ 実装の対応 |
| `test-quality` | 同上 | 差分カバレッジ 100%、Spec coverage 差分 100%、mutation score 妥当性、tautological テスト検出 |
| `architecture` | 同上 | レイヤー依存方向 (feature → core → repository)、モジュール越境、命名規約、循環依存検出 |
| `security` | 同上 | PII redaction、secrets 取り扱い、認証境界 (`requireUid()` 強制)、Konsist 規約準拠 |
| `performance` | 同上 | N+1 クエリ、無駄な再描画 (`derivedStateOf`, `remember` 漏れ)、coroutine スコープ、不要な hot reload |
| `code-quality` | 同上 | error-handling (Result 型ラップ徹底)、命名規約、可読性、defensive 過剰禁止 |
| `visual-regression` | **A10 完了後 enable** | Roborazzi baseline diff (4 パターン: mobile/desktop × Light/Dark)、許容 threshold 内 |
| `design-tokens` | **A10 完了後 enable** | DESIGN.md に存在しない hex / sp / dp の混入なし、Primitive/Semantic/Component 3 階層整合 |

## 各 aspect の binary yes/no eval checklist (最低 5 項目)

各 aspect は **binary yes/no eval checklist を最低 5 項目** 持つ (R-13)。
詳細は A3 で各 aspect 個別ファイルとして拡充予定 (`.claude/rules/code-reviewer-aspects/<aspect>.md` の分割案)。
B0 時点では本ファイルにまとめ、A3 で分割する。

### 例: spec-conformance

- [ ] 新規追加・変更 Kotlin 行に対応する `@Spec("SPEC-NNN-N")` annotation がテスト側に存在
- [ ] `@Spec` で参照される SPEC-ID が `docs/specifications/{basic,detail}/` に実在
- [ ] PR description frontmatter `related_specs` の SPEC-ID が実在
- [ ] 基本設計 ⇄ 詳細設計の `related_basic` / `related_detail` ペアが整合
- [ ] 設計書本文にコード断片が混入していない (フェンス付き `kotlin/sh/sql` 等)

## Coordinator の役割

- 各 aspect の binary yes/no eval 結果を集約
- 重複指摘を排除し、重要度別 (Critical / Improvement) に整理
- 日本語の構造化レビューコメントを PR に post (§5.5 例)
- Merge readiness を判定: **Critical = 0 で Ready**

## Coordinator のレビューコメント形式 (§5.5 例)

```markdown
## 🔍 AI コードレビュー

> 生成: code-reviewer Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 並列実行した aspect: spec-conformance, test-quality, architecture, security, performance, code-quality

### サマリ
| 観点 | 結果 | 重大な指摘 | 改善提案 |
|---|---|---|---|
| 仕様適合性 (spec-conformance) | ✅ 合格 | 0 | 0 |
| ...

### マージ可否: ✅ 可 | ❌ まだ不可
### 重大な指摘 (merge ブロック)
1. **[aspect]** `path:line` — 説明
   - 修正案: ...

### 改善提案 (non-blocking)
- ...

### Eval チェックリスト (binary yes/no)
- [x] 仕様適合性: 全 Acceptance criteria に @Spec タグ付きテストが存在
- [ ] ...
```

## Gotchas

- **各 aspect は独立した system prompt** で動作 (Generator バイアス回避、R-13)。
- **Claude API への直接呼び出しは禁止**、ローカル Claude Code のサブエージェントで並列実行 (R-37)。
- **Critical = 0 のみ Ready 昇格**、Improvement は non-blocking。
- **人間レビュアーには「code-reviewer の指摘で十分か?」を考えさせる文言** を PR コメントに含める (R-15)。

## 関連

- `docs/harness/plan.md` §5.4.3
- ADR 0019 (`code-reviewer` 8 aspect + Coordinator)
- `.claude/skills/code-reviewer/SKILL.md`
- `.claude/rules/merge-readiness.md` (A3 で本格化)
