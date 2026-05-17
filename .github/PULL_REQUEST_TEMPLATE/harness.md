---
type: harness
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<ハーネス改修 / レトロ集約 / KPT 反映 / Skill 改善等。1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN (該当時)
- Epic: EPIC-NNN (該当時、ハーネス系 Epic では `EPIC-A2` / `EPIC-A9` / `EPIC-A10` 等)
- ADR: ADR-NNNN (該当時、特に ADR 0017 / 0024 / 0025 / 0026 / 0027)
- 元 learnings: `docs/harness/learnings/YYYY-MM-DD-pr-N.md` (該当時)
- 元 evolution-proposals: `docs/harness/evolution-proposals/YYYY-MM-DD.md` (該当時)

## PR の種別

- [ ] **A1 レトロ 等の即時消化フォロー PR** (rules / template / docs 即時修正)
- [ ] **rules / Skill 本格化** (B0 雛形 → 本文充実)
- [ ] **ハーネス改修 PR** (`harness-meta` Skill 起票、KPT ベース)
- [ ] **外部研究駆動の改修 PR** (`harness-evolution` Skill 起票)
- [ ] **レトロ集約 PR** (`harness/learnings-batch-YYYY-WW` ブランチ、週次 / 件数到達時)
- [ ] **その他** (chore / 撤去 / migration 等)

## 対象 PR (KPT / 改修起点)

`harness-meta` / `harness-evolution` / レトロ集約 PR で記入:

| 元 PR | KPT 要点 | 本 PR で消化する提案 |
|---|---|---|

## 変更内容

| 区分 | パス | 変更内容 |
|---|---|---|
| rule | `.claude/rules/<name>.md` | <追加 / 改修 / 削除> |
| skill | `.claude/skills/<name>/SKILL.md` | <追加 / 改修 / archived 化> |
| template | `.github/PULL_REQUEST_TEMPLATE/<type>.md` 等 | <追加 / 改修> |
| docs | `docs/harness/**` / `docs/adr/**` 等 | <追加 / 改修> |
| script | `scripts/install-git-hooks.sh` 等 | <整合性更新> |

## ハーネス改善提案件数 (KPT / harness-meta フィードバック)

| 観点 | 採用 | 見送り | 保留 | 撤去 |
|---|---|---|---|---|
| `[rule]` |  |  |  |  |
| `[skill]` |  |  |  |  |
| `[template]` |  |  |  |  |
| `[remove]` |  |  |  |  |

採用しなかった提案は元 learning ファイル `📝 harness-meta フィードバック` セクションに理由を追記する (`.claude/rules/retrospective-format.md` 参照)。

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>
- [ ] (本 PR でルール改定がある場合) 既存 rule / Skill / 関連 ADR との不整合がない
- [ ] (status ラベル変更がある場合) `.claude/rules/rules-index.md` / `CLAUDE.md` の lookup table が整合
- [ ] (Skill archived 化がある場合) CLAUDE.md からの参照削除、archived/ への物理移動が完了

## テスト

- [ ] markdownlint-cli2 グリーン (A6 以降)
- [ ] Gradle カスタムタスクの docs 検証グリーン (A6 以降)
- [ ] (`commit-msg` hook 変更時) `./scripts/install-git-hooks.sh` 再実行 + 試し commit で検証

## レビュー観点

<重点的に見てほしい箇所、特に既存 rule / Skill との整合性>

- ハーネス中核 Skill (`implementation-workflow` / `code-reviewer` / `roadmap-tracker` / `pr-poller`) に影響するか
- ADR 起票基準 (`§4.5` 2 項目以上) を新たに満たすか (該当する場合は ADR 昇格を提案)
- 撤回コスト: 本改修を取り消す手順は明確か

## チェックリスト

- [ ] `.claude/rules/{rules-index,docs-structure,template-language,markdown}.md` の規約を確認した
- [ ] Skill 改修の場合: `.claude/rules/skill-authoring.md` 経由 (Anthropic Complete Guide 準拠、ADR 0025)
- [ ] `auto-merge` を有効化していない (R-15、人間 approve 必須)
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン、A6 以降)
- [ ] (Epic 配下 PR の場合) `roadmap-tracker` で完了根拠登録、または手動更新の理由を本文に記載
- [ ] (status ラベル変更時) `rules-index.md` の status 語彙 (`skeleton (B0)` / `planned (X)` / `living`) を遵守
