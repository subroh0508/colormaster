---
id: dry-run-pr-NNN
title: PR #NNN ハーネス改善提案 dry-run 結果 (<改善対象の簡潔な要約>)
type: dry-run
status: draft
related_pr: NNN
related_learning: docs/harness/learnings/YYYY-MM-DD-pr-NNN.md
related_proposals:
  - "[skill] <提案 1>"
  - "[rule] <提案 2>"
  - "[template] <提案 3>"
generated_at: YYYY-MM-DDTHH:MM:SSZ
generator: harness-meta Skill (vX.Y.Z)
verdict: adopt | discard | escalate
---

# PR #NNN ハーネス改善提案 dry-run 結果

> 生成: harness-meta Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 関連 retrospective: [`YYYY-MM-DD-pr-NNN.md`](../learnings/YYYY-MM-DD-pr-NNN.md)
> 対象提案: <提案 N 件、各提案のプレフィックス [rule] / [skill] / [template] と該当ファイルパス>
> 判定基準: 適用版が未適用版より明らかに望ましい出力 (誤検知減 / 規約遵守率向上 / 提案精度向上) を出す場合のみ採用

## 改善提案の概要

<!-- 対象提案を要約。元 retrospective `🤖 ハーネス改善提案` セクションへのリンクを含む。 -->

- **提案 1 (`[skill]` / `[rule]` / `[template]`)**: <提案内容の 1-2 文要約> (元 retrospective: `docs/harness/learnings/YYYY-MM-DD-pr-NNN.md` `🤖 ハーネス改善提案`)
- **提案 2**: ...
- **提案 3**: ...

## dry-run 入力 (適用版 / 未適用版の境界)

<!-- 改善提案を適用した版と未適用の既存版をどう用意したか。両 commit / branch / worktree のどれを使ったか明示。 -->

| 区分 | commit / branch / worktree | 該当ファイル |
|---|---|---|
| 未適用版 (Before) | `<base-branch>` / `<commit-sha>` | `<改善対象ファイル>` |
| 適用版 (After) | `<feature-branch>` / `<commit-sha>` | 同上 (改善提案適用後) |

## dry-run シナリオ (`⚠️ Problem` 抽出)

<!-- 過去 retrospective の `⚠️ Problem` セクションから抽出した、改善効果を判定できるシナリオ。 -->

| シナリオ ID | 抽出元 retrospective | Problem 内容 (要約) | 期待される改善 |
|---|---|---|---|
| S1 | PR #NNN | <Problem 要約> | <改善されるべき AI 出力の特性> |
| S2 | PR #NNN | ... | ... |

## dry-run 実行 (subagent 並列)

<!-- Generator 独立性 (R-13) を保つため、適用版 / 未適用版それぞれに別 system prompt の subagent を立てて、上記シナリオを投げる。 -->

### Subagent 構成

- **適用版 subagent**: `Agent(subagent_type="general-purpose", prompt=<シナリオ + 適用版ファイル>)`
- **未適用版 subagent**: `Agent(subagent_type="general-purpose", prompt=<シナリオ + 未適用版ファイル>)`
- **system prompt 独立性**: 両 subagent の system prompt は本セッションの context を含まない、Generator-Evaluator 独立性 (R-13) と整合

### Subagent 実行ログ (要約、PII / Secrets redaction 済)

- 適用版 subagent 出力: `<要約 or リンク>`
- 未適用版 subagent 出力: `<要約 or リンク>`

## before/after AI 出力差分

<!-- シナリオごとに適用版 / 未適用版の AI 出力を並列比較。差分 hunk または要約形式。 -->

### S1: <シナリオ要約>

**未適用版 (Before)**:

```text
<AI 出力のサンプル>
```

**適用版 (After)**:

```text
<AI 出力のサンプル>
```

**差分のポイント**: <誤検知減 / 規約遵守率向上 / 提案精度向上 のどれが観測されたか、定量 or 定性で記述>

### S2: ...

...

## 判定理由

<!-- 各シナリオの判定を集約し、最終的な採用 / 破棄 / エスカレーションの根拠を記述。 -->

| シナリオ | 適用版優位 | 同等 | 適用版劣位 | コメント |
|---|---|---|---|---|
| S1 | ✅ | | | <観測結果> |
| S2 | | ✅ | | <観測結果> |

### 最終判定 (frontmatter `verdict` と整合)

- **adopt (採用)**: 全シナリオで適用版が同等以上 + 過半で適用版優位 → commit + push に進む
- **discard (破棄)**: 1 シナリオでも適用版劣位 (退化) → 変更を破棄、retro `📝 harness-meta フィードバック` の「保留 (要再評価)」表に記録、別案検討
- **escalate (エスカレーション)**: 判定が拮抗 / dry-run コスト > 効果と判断 → orchestrator (subroh0508) に判定委任 (`harness-meta-criteria.md` §dry-run 必須条件 §必須条件不一致時のフォールバック)

## 採用 / 破棄判定の反映

<!-- 判定結果を retro / PR にどう反映したかの実施記録。 -->

- 対象 retrospective `📝 harness-meta フィードバック` セクションの追記内容:
  - 「採用」表: <該当提案 + 反映 PR 番号>
  - 「見送り」表: <該当提案 + 移行先>
  - 「保留」表: <該当提案 + 再評価条件>
- 反映 PR: [<PR title> #NNN](https://github.com/subroh0508/colormaster/pull/NNN) (該当時)

## 関連

- 元 retrospective: [`docs/harness/learnings/YYYY-MM-DD-pr-NNN.md`](../learnings/YYYY-MM-DD-pr-NNN.md)
- 関連 rule: `.claude/rules/{harness-meta-criteria,retrospective-format}.md`
- 関連 Skill: `.claude/skills/harness-meta/SKILL.md` (A3-5 / PR #156 で本格化済、`harness-bootstrap` は A3-14 で archived 化済)
- 索引: `docs/harness/dry-runs/INDEX.md`
