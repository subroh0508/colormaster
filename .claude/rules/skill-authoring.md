---
id: rules-skill-authoring
title: Skill 作成は example-skills:skill-creator 経由
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/**/SKILL.md"
related_adrs:
  - ADR-0025
related_plan: docs/harness/plan.md §5.3 / R-30
---

# skill-authoring.md — Skill 作成規約

> 新規 Skill 作成・既存 Skill 改修は Claude Code ユーザースコープの **`example-skills:skill-creator`**
> を呼び出して行う。本リポジトリには `skill-creator` を mirror しない (ADR 0025)。
> SKILL.md は Anthropic "Complete Guide to Building Skills for Claude" + AgentSkills 2026 spec +
> 100-point rubric 準拠。

## 起動方法

- 他 Skill (`harness-bootstrap` / `harness-meta` / `harness-evolution`) や人間からの「新規 Skill 作成 / 既存 Skill 改修」要求時に呼び出す
- 本リポジトリには `.claude/skills/skill-creator/` を配置しない (ユーザースコープにインストール済のため、ADR 0025)
- 起動コマンド: `Skill skill="example-skills:skill-creator" args="<task description>"`

## SKILL.md フォーマット

```markdown
---
name: <skill-name>
description: |
  <trigger を含む 1-3 文の説明 — どんな状況で起動されるか / 何を達成するか>
status: skeleton | active | archived
phase: <導入フェーズ>
related_plan: <参照する plan.md 章番号>
related_rules:
  - .claude/rules/<rule1>.md
  - .claude/rules/<rule2>.md
related_adrs:
  - ADR-NNNN
---

# <skill-name>

## 役割

## 入力

## 出力

## フェーズ別動作 (該当時)

## Gotchas

## 関連
```

- frontmatter 配列は block 形式必須 (`docs-structure.md` 規約と整合)
- 各セクション最低 3 項目、推奨 5-10 項目
- 日本語見出し (ADR 0027 / `template-language.md`)

## description = trigger の書き分けパターン

- **description は trigger と等価**: 「いつ呼ばれるか」を明示
- 1-3 文以内、200 文字以内推奨 (Anthropic 公式準拠)
- 「~を作成する」「~を生成する」より「~の指示を受けたとき」「~を必要とする状況で」のように **起動契機を明示**
- 複数 trigger を持つ場合は **箇条書きまたは「または」接続** で並列化

### ✅ 良い例

```yaml
description: |
  Plan / Epic 確定後の実装着手 → Lint/Test → AI Review → マージ → レトロ起動 →
  worktree クリーンアップを 10 フェーズ (Phase 0-9) で統合管理するオーケストレーター。
  Phase 0 で git worktree を作成し、Phase 9 で削除することで複数 Claude Code セッションの
  並行実装を物理分離する。
```

→ Trigger 明示 (Plan/Epic 確定後の実装着手要求時) + 達成目標明示 (10 フェーズ統合管理 + worktree 並行) + 副次効果明示 (物理分離)

### ❌ 悪い例

```yaml
description: |
  10 フェーズで実装を管理する Skill。
```

→ Trigger 不明確 (いつ呼ばれるか書いていない) + 達成目標が曖昧

## 必須項目

| 項目 | 内容 |
|---|---|
| **Gotchas セクション** | 罠 / 注意点 / 例外を必ず列挙 (最低 3 項目) |
| **関連セクション** | 参照する rules / Plan 章 / 他 Skill を列挙 |
| **明示的な status** | `skeleton` / `active` / `archived` |
| **phase** | 導入フェーズ (`B0` / `A3` / `A4` / `A10` 等)、本格化 PR で更新 |
| **入力 / 出力** | 受け取る引数 / 生成するファイル / 副作用を明示 |
| **フェーズ別動作** (該当時) | `implementation-workflow` の 10 フェーズや `pr-poller` の 3 系統等、複数モードがある場合 |

## status 値の遷移

| 値 | 意味 | 遷移条件 |
|---|---|---|
| `skeleton` | B0 で配置済の骨格、本格動作は未稼働 | 該当フェーズ (A3 等) の本格化 PR で `active` 化 |
| `active` | 本格化済み、Skill 駆動で実行可能 | dormant 確定 (3 ヶ月未使用) で `archived` 化、または harness-meta 撤去判定で削除 |
| `archived` | 撤去候補、参照のみ可能、削除待ち | 削除 PR で物理削除 (`harness-meta-criteria.md` 撤去判定基準 3 項目充足時) |

## 禁止表現

- **`MUST` / `ALWAYS` / `NEVER` を多用しない** (Claude の挙動を硬直化させる)
- 代わりに「~するべき」「~を推奨」「~してはいけない (理由付き)」で記述
- 数値・閾値はそのまま記述 (例: 「fix loop 上限 3 回」)、「絶対に超えてはならない」のような断定は理由併記

## 100-point rubric チェック (skill-creator が評価)

`example-skills:skill-creator` が SKILL.md を生成 / 改修する際に以下の rubric を評価:

| 項目 | 配点 | 評価ポイント |
|---|---|---|
| description が trigger を明示 | 20 | 「いつ呼ばれるか」「何を達成するか」が 1-3 文以内で明確 |
| Gotchas が具体的に列挙 | 15 | 抽象論ではなく具体的な罠・例外を 3 項目以上列挙 |
| 関連 rules / Plan 章へのリンクが正確 | 15 | dangling 参照ゼロ、双方向リンク (該当時) |
| 入力 / 出力が明示 | 10 | 引数 / 副作用 / 生成ファイルが明示 |
| ステータス値が定義済み | 5 | frontmatter `status` が 3 値のいずれか + `phase` 記入 |
| フォーマット規範準拠 | 10 | template-language / docs-structure / frontmatter block 形式 |
| 禁止表現の回避 | 5 | MUST / ALWAYS / NEVER 多用なし |
| Skill 間の責務分離 | 10 | 他 Skill との overlap 最小化 (例: harness-meta vs harness-evolution) |
| 単独テスト可能性 | 5 | Skill 単体で起動可能 (依存 Skill の事前起動が前提化されていない) |
| 改修履歴の追跡 | 5 | last_updated / phase 更新時に履歴を残す (将来追加) |

合計 80 点以上で合格、不合格時は skill-creator が改善提案を出す。

## 公式アップデートへの追従

- **`anthropics/skills` GitHub repo の更新を `harness-evolution` Skill の手動実行時に確認** (R-30、月次頻度推奨)
- 更新事項を `docs/harness/evolution-proposals/YYYY-MM-DD.md` に取り込み、重要案を Plan / Epic 起票

## Gotchas

- **本リポジトリに `skill-creator` を mirror しない** (ADR 0025、ユーザースコープにインストール済)
- **公式アップデートに追従**: `anthropics/skills` の更新を harness-evolution 手動実行時に確認 (R-30、月次頻度推奨)
- **既存 Skill 改修も `skill-creator` 経由を推奨**: 直接編集禁止ではないが、フォーマット drift / 100-point rubric 不合格を防ぐため経由を推奨
- **frontmatter 配列は block 形式**: flow 形式 `[A, B]` は禁止 (`docs-structure.md` 規約)
- **description は trigger 明示**: 「~を作成する」のような目的説明だけでは Claude Code が起動契機を判定できない
- **status `archived` は削除待ち**: 物理削除は別 PR で行い、archived 期間中に dangling 参照を解消
- **Skill 間の責務 overlap 回避**: 例 `harness-meta` (内部 KPT 駆動) vs `harness-evolution` (外部研究駆動)、`pr-retrospective` (1 PR 単位) vs `harness-meta` (複数 PR 集約) の境界を明確化
- **新規 Skill は plan.md §5.3 に追加** + `.claude/skills/<name>/SKILL.md` 配置 + 関連 rules 双方向リンク同時更新

## 関連

- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.3 / R-30
- Anthropic "Complete Guide to Building Skills for Claude" (https://docs.anthropic.com/skills/)
- `anthropics/skills` GitHub (公式 Skill サンプル)
- `.claude/skills/*/SKILL.md` (本リポジトリ全 Skill の規範例)
- `.claude/rules/{harness-evolution,harness-meta-criteria,docs-structure,template-language}.md`
