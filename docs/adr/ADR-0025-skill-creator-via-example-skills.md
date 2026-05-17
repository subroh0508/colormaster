---
id: ADR-0025
title: Skill 作成は example-skills:skill-creator 経由とする
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

# ADR-0025: Skill 作成は example-skills:skill-creator 経由とする

> **5 行以内 summary**: 新規 Skill 作成・既存 Skill 改修は **Claude Code ユーザースコープ
> の `example-skills:skill-creator`** を呼び出して行う。本リポジトリには
> `skill-creator` を mirror しない。SKILL.md は Anthropic "Complete Guide to Building
> Skills for Claude" + AgentSkills 2026 spec + 100-point rubric 準拠とし、必須項目
> (description = trigger / Gotchas / 関連 / status) と禁止表現 (MUST / ALWAYS / NEVER
> の多用) を規約化する。

## ステータス

accepted

## コンテキスト

ColorMaster のハーネスは複数の Skill (`feature-request` / `implementation-workflow` /
`code-reviewer` / `harness-meta` / `harness-evolution` 等) を継続的に追加・改修する
前提で設計されている。Skill ファイル (SKILL.md) のフォーマット drift が起きると、
Skill 起動時の trigger 解釈が不安定になり、ハーネス全体の再現性が崩れる。

Anthropic は 2025 末に "Complete Guide to Building Skills for Claude" を公開し、
AgentSkills 2026 spec として SKILL.md の構造 / description (= trigger) / Gotchas 必須 /
100-point rubric 評価を整備した。`example-skills:skill-creator` (Claude Code ユーザー
スコープ) はこれらに準拠した scaffolding を提供している。

選択肢:

1. 本リポジトリに `skill-creator` を mirror して使う
2. ユーザースコープの `example-skills:skill-creator` を呼び出して使う
3. 手書きで SKILL.md を作成し、フォーマットは規約 (`.claude/rules/skill-authoring.md`)
   のみで担保

## 決定

新規 Skill 作成・既存 Skill 改修は **`example-skills:skill-creator` 経由** とする。
具体的には:

- **本リポジトリに `skill-creator` を mirror しない**: ユーザースコープにインストール
  済のものを利用する
- **SKILL.md フォーマット必須項目**:
  - frontmatter: `name` / `description` (= trigger を含む 1-3 文、200 文字以内推奨) /
    `status` (skeleton | active | archived) / `phase` / `related_plan` /
    `related_rules`
  - 本文: `## 役割` / `## 入力` / `## 出力` / `## Gotchas` / `## 関連` の 5 セクションを
    必ず含める
- **description の書き方**: 「~を作成する」「~を生成する」より、「~の指示を受けたとき」
  「~を必要とする状況で」のように **起動契機を明示** する
- **禁止表現**: `MUST` / `ALWAYS` / `NEVER` の多用は避ける (Claude の挙動を硬直化させる)。
  代わりに「~するべき」「~を推奨」「~してはいけない (理由付き)」で記述
- **公式アップデートに追従**: `anthropics/skills` の更新を `harness-evolution` Skill
  の手動実行時に確認 (月次推奨、ADR-0026)
- **既存 Skill 改修も `skill-creator` 経由**を推奨 (直接編集禁止ではないが、フォーマット
  drift を防ぐため)
- **本リポジトリ `.claude/skills/skill-creator/` は配置しない** (ユーザースコープと
  二重化させない)

フォーマット構造を例示するために短い fenced block を SKILL.md / frontmatter フィールド
規約箇所で許容する (本 ADR 本文では §4.6 のコード断片禁止対象外):

```yaml
name: <skill-name>
description: <trigger を含む 1-3 文>
status: skeleton | active | archived
```

## 根拠

- **Anthropic 公式 scaffolding の活用**: `example-skills:skill-creator` は Complete
  Guide + AgentSkills 2026 spec + 100-point rubric に準拠した SKILL.md を生成する。
  公式準拠を機械的に担保できる
- **mirror しない理由**: Claude Code ユーザースコープに既にインストール済のため、
  リポジトリ内に mirror すると公式更新との同期コストが発生し drift する
- **description = trigger の明示**: Skill 起動条件は description で判定される。
  「いつ呼ばれるか」を明示することで誤起動 / 起動漏れを防ぐ
- **禁止表現**: `MUST` / `ALWAYS` / `NEVER` の多用は LLM の挙動を硬直化させ、例外
  ケースで適切に判断できなくなる。理由付き表現で柔軟性を確保
- **公式アップデート追従**: `anthropics/skills` は継続更新されるため、`harness-evolution`
  の月次手動実行で確認するルーチンを組み込む (R-30)

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 本リポジトリに `skill-creator` を mirror | バージョン固定可、オフライン動作 | 公式更新との drift、二重管理コスト | 不採用、ユーザースコープに統一 |
| 手書き SKILL.md + 規約のみ | scaffolding コスト不要 | フォーマット drift が起きやすい、rubric 適合確認が手動 | 不採用、scaffolding 経由で構造担保 |
| GitHub Copilot Workspace / 他社製 scaffolder | エコシステム広い | Anthropic Complete Guide / AgentSkills 2026 spec への準拠が保証されない | 不採用、Anthropic 公式に統一 |
| `MUST` / `ALWAYS` / `NEVER` を許容 | 強制力が明確 | LLM 挙動硬直、例外対応不能 | 理由付き表現を採用 |

## 帰結

### Positive

- SKILL.md フォーマットが Anthropic 公式準拠で統一され、Skill 起動の再現性が向上
- 公式アップデート (AgentSkills spec の改訂) への追従パスが `harness-evolution` 経由で
  明確化
- `MUST` / `ALWAYS` / `NEVER` を避けることで、Skill 内記述が柔軟性を保つ

### Negative / トレードオフ

- **オフライン環境で `skill-creator` を呼べない**: ユーザースコープ未インストール時は
  Skill 作成不能 → 開発前提として「Claude Code 利用環境では `example-skills:skill-creator`
  がインストール済」を `docs/runbooks/local-development.md` (Phase A〜C で本格化) に
  明記
- **手書き SKILL.md の比較対象がないと品質判定が難しい**: 100-point rubric を
  `.claude/rules/skill-authoring.md` に明示し、`harness-evolution` が evolution-proposals
  で SKILL.md の品質チェックを補う

### Neutral / 将来の検討事項

- AgentSkills 2026 spec が大きく改訂された場合、本 ADR を superseded にして新 ADR で
  対応 (ADR-0001 のステータス遷移ルールに従う)
- 既存 Skill (`harness-bootstrap` 等) を `skill-creator` 経由でリファクタするのは
  `harness-meta` の learning 集計次第 (任意)

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 7. ハーネス本体の中核設計 (Skill 作成プロセスの統一)
- [x] 8. 複数の代替案を比較した結果としての判断 (mirror / 手書き / scaffolder の比較)
- [x] 9. 元に戻すコストが高い決定 (全 Skill のフォーマットに影響)
- [x] 10. 長期的な制約 (今後 1 年以上、全新規 Skill の作成方式に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」(「Skill 作成は
      `example-skills:skill-creator` 経由」) と一致。Plan / runbook / コーディング規約で
      済む話ではないことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0017 (ローカル Claude Code ポーリング、ユーザースコープ Skill 呼び出し前提)
- ADR-0026 (`harness-evolution` で公式アップデート追従、`skill-creator` 経由で改修起票)
- ADR-0027 (テンプレ言語ポリシー、SKILL.md の日本語化)
- `.claude/rules/skill-authoring.md` (Skill 作成規約の Single Source of Truth)
- Anthropic "Complete Guide to Building Skills for Claude"
  (https://docs.anthropic.com/skills/)
- `docs/harness/plan.md` §5.3 / R-30
