---
id: ADR-0019
title: code-reviewer Skill を 8 aspect と Coordinator 構成にする
status: accepted
date: 2026-05-17
related_epics:
  - EPIC-000
related_plans:
  - PLAN-001
related_specs: []
superseded_by: null
supersedes: null
---

# ADR-0019: code-reviewer Skill を 8 aspect と Coordinator 構成にする

> **5 行以内 summary**: Generator (`implementation-workflow`) と独立した **Evaluator**
> として、`code-reviewer` Skill を **8 aspect** (spec-conformance / test-quality /
> architecture / security / performance / code-quality / visual-regression /
> design-tokens) の binary eval checklist + Coordinator で構成する。各 aspect は
> ローカル Claude Code のサブエージェントで並列実行し、Claude API は直接呼び出さない。
> visual-regression と design-tokens は A10 (DESIGN.md + Roborazzi baseline 完成) 後に
> enable し、それまでは残り 6 aspect で稼働する。

## ステータス

accepted

## コンテキスト

Anthropic の Planner / Generator / Evaluator パターン、Cloudflare の specialized
reviewer + coordinator 構成、GitHub Agentic Workflows の human-in-the-loop 原則を
組み合わせると、AI 自身が書いたコードを **同じ Skill が評価する** 構造は Generator
バイアスを生む。

ColorMaster ではこれまで AI レビューを統合運用していなかったため、本ハーネス起動と
同時に独立 Evaluator を設計する必要がある。観点は以下の 8 軸に分解可能で、いずれも
binary yes/no eval checklist (最低 5 項目) に落とし込めば再現性を確保できる:

1. spec-conformance (仕様 ⇄ 実装の整合性)
2. test-quality (三層指標差分、tautological テスト検出)
3. architecture (層分割、依存方向、JetBrains MCP IDE indexing 活用)
4. security (PII / Secrets redaction)
5. performance (N+1 / 重い同期 IO / 不要な recomposition)
6. code-quality (Konsist / detekt / ktlint、Kotlin idiomatic)
7. visual-regression (Roborazzi diff)
8. design-tokens (DESIGN.md token 参照、hex / sp / dp ハードコード検出)

ただし visual-regression と design-tokens は DESIGN.md + UI Inventory + Roborazzi
baseline (A10) が揃わないと稼働不能。

加えて、Claude API への直接呼び出しを各 aspect が行うとコスト・rate limit の両方を
圧迫する (ADR-0017)。Claude Code のサブエージェント機能 (Agent ツール) を使えば、
ローカル Claude Code 内で並列実行できる (R-37)。

## 決定

`code-reviewer` Skill を Generator から独立した **Evaluator** として以下のように構成
する:

- **8 aspect**: spec-conformance / test-quality / architecture / security / performance
  / code-quality / **visual-regression** / **design-tokens** (末尾 2 つは A10 完了後に
  enable、それまで 6 aspect で稼働)
- **各 aspect は独立した system prompt** で動作 (Generator バイアス回避、R-13)。binary
  yes/no eval checklist を最低 5 項目持つ (`.claude/rules/code-reviewer-aspects.md`)
- **ローカル Claude Code のサブエージェント** で並列実行 (Claude API への直接呼び出しは
  禁止、ADR-0017 / R-37)
- **Coordinator** が同セッション内で重複指摘を排除し、Critical / Improvement に整理 →
  日本語の構造化レビューコメントを `gh pr comment` で post
- **Merge readiness 判定**: **Critical findings = 0** で Ready。Improvement は
  non-blocking
- **PII redaction を aspect ごとに必須化** (R-26)。PR 本文 / diff / CI ログを Skill 出力
  に含める場合は redaction フェーズを通過させる

A10 完了までは visual-regression / design-tokens を skip し、残り 6 aspect で稼働する。
A10 完了時に SKILL.md の status を更新する。

## 根拠

- **Anthropic Evaluator 独立性原則**: Generator バイアス回避には Skill そのものを分離
  するのが最も構造的。同一 system prompt 内で Self-Review させる方式は bias を持ち込む
- **aspect 単位の分割**: 8 軸に分解することで、各 aspect の system prompt を独立化でき、
  改善 PR も aspect 単位で打てる (例: security aspect だけ改修)
- **サブエージェント並列実行**: Claude Code の Agent ツールはローカル Claude Code 利用枠
  内で動作するため、Claude API 直接呼び出し (Actions 経由) と比べてコスト効率が高い
  (ADR-0017)
- **Critical = 0 の単一判定基準**: Improvement を non-blocking にすることで「指摘が多すぎて
  PR が永久に Ready に上がらない」事故を回避
- **段階的 enable**: visual-regression / design-tokens は A10 で前提資産 (DESIGN.md /
  Roborazzi baseline) を揃えてから稼働、それまでは 6 aspect 稼働で実用性を担保
- **PII redaction の aspect 内強制**: Skill が PR にコメント post する経路すべてに
  redaction を通すことで漏洩経路を構造的に限定 (`.claude/rules/pii.md`)

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Generator 内 Self-Review | Skill 数最小 | バイアス、Anthropic 原則違反 | 構造的に不採用 |
| 単一 aspect (汎用レビュー) | system prompt 1 つで済む | 軸ごとの専門性が落ち、改善 PR の粒度が粗くなる | 8 aspect 分割で採用 |
| 各 aspect が Claude API 直接呼び出し | 並列度高い | コスト二重、ADR-0017 と矛盾 | サブエージェント並列で代替 |
| Critical / Improvement の閾値を Critical > 0 で merge 不可、Improvement > 5 で警告 | 改善促進 | Improvement で永久に block されるリスク | Critical = 0 単一判定で簡素化 |
| visual-regression / design-tokens を初期から enable | UI 品質の早期検出 | 前提資産未整備で false positive 連発 | A10 完了後 enable に遅延 |

## 帰結

### Positive

- Generator / Evaluator 分離により Anthropic 原則準拠、bias 構造化排除
- aspect 単位で改善 PR を打てるため、`harness-meta` / `harness-evolution` からの
  Skill 改修提案が aspect 粒度に整理される
- サブエージェント並列実行でコスト枠内完結 (ADR-0017 と整合)

### Negative / トレードオフ

- **aspect ごとに system prompt を維持する設計コスト**: A3 で各 aspect ファイルに分割
  予定 (`.claude/rules/code-reviewer-aspects/<aspect>.md`)。B0 時点では単一ファイル
- **visual-regression / design-tokens の遅延**: A10 完了まで UI 品質レビューが手動運用
  → A10 で前提資産が揃った時点で SKILL.md status を `active` に更新
- **Critical / Improvement 判定の境界**: `code-reviewer-aspects.md` でチェックリストを
  確定する。曖昧判定は `harness-meta` の learning 集計で再評価

### Neutral / 将来の検討事項

- aspect 追加 (例: i18n 完備チェック、accessibility) は `harness-evolution` の提案
  経由で別 ADR 化を検討
- 人間レビュアーへの「code-reviewer の指摘で十分か?」文言の自動付与は
  `code-reviewer-aspects.md` の Coordinator 形式に組込済 (R-15)

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 5. テスト戦略・品質指標の中核方針 (test-quality / spec-conformance / mutation 等)
- [x] 6. セキュリティ・プライバシー (security aspect の PII redaction 強制)
- [x] 7. ハーネス本体の中核設計 (独立 Evaluator の構成)
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (Skill 全体に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」(「implementation-workflow +
      code-reviewer の Generator/Evaluator 二段構成」) と一致。Plan / runbook /
      コーディング規約で済む話ではないことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0017 (ローカル Claude Code ポーリング、サブエージェント並列の前提)
- ADR-0018 (`implementation-workflow` Phase 6 から呼出)
- ADR-0023 (UI 凍結三本柱、visual-regression / design-tokens の前提資産)
- ADR-0020 (PII 保護、security aspect の redaction 強制)
- ADR-0024 (MCP サーバ、architecture aspect が JetBrains MCP の IDE indexing を活用)
- `.claude/rules/code-reviewer-aspects.md` (8 aspect の eval checklist + Coordinator 形式)
- `.claude/rules/pii.md` (redaction 規約)
- `docs/harness/plan.md` §5.3 / §5.4.2 ③ / R-13 / R-15 / R-26 / R-37
