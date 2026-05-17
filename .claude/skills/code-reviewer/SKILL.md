---
name: code-reviewer
description: |
  Generator から独立した Evaluator として 8 aspect (spec-conformance / test-quality /
  architecture / security / performance / code-quality / visual-regression / design-tokens)
  をローカル Claude Code のサブエージェントで並列実行し、Coordinator が日本語の構造化
  レビューコメントを PR に post して Merge readiness を判定する。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §5.4.3 / ADR 0019
related_rules:
  - .claude/rules/code-reviewer-aspects.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pii.md
---

# code-reviewer (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。
> visual-regression / design-tokens の 2 aspect は **A10 完了後に有効化** (DESIGN.md / Roborazzi baseline が揃ってから)。

## 8 aspect

| aspect | 状態 | 主観点 |
|---|---|---|
| spec-conformance | B0 で雛形 / A3 で本格化 | `@Spec` annotation の存在、SPEC-ID 整合性、frontmatter `related_specs` の有効性 |
| test-quality | 同上 | 差分カバレッジ、Spec coverage、mutation score |
| architecture | 同上 | レイヤー依存方向、モジュール越境、命名規約 |
| security | 同上 | PII redaction、secrets 取り扱い、認証境界 |
| performance | 同上 | N+1、無駄な再描画、coroutine スコープ |
| code-quality | 同上 | error-handling、命名、可読性 |
| visual-regression | **A10 完了後 enable** | Roborazzi baseline diff |
| design-tokens | **A10 完了後 enable** | DESIGN.md に存在しない hex/sp/dp の混入 |

## Coordinator の役割

- 各 aspect の binary yes/no eval checklist 結果を集約
- 重複指摘を排除し、重要度別 (Critical / Improvement) に整理
- 日本語の構造化レビューコメントを PR に post (§5.5 例)
- Merge readiness を判定 (Critical = 0 で Ready)

## Gotchas

- **Generator (`implementation-workflow`) と独立した system prompt** で動作 (Anthropic Evaluator 独立性原則、R-13)。
- **Claude API への直接呼び出しは禁止**。ローカル Claude Code のサブエージェント (Agent ツール) で並列実行する (R-37 / ADR 0017)。
- Coordinator も同セッション内で動作。
- 各 aspect は **binary yes/no eval checklist を最低 5 項目** 持つ (R-13)。

## 関連

- `docs/harness/plan.md` §5.4.3 (Evaluation フェーズ)
- ADR 0019 (`code-reviewer` Skill の 8 aspect + Coordinator 設計)
- `.claude/rules/code-reviewer-aspects.md`
