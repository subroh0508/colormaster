---
id: rules-merge-readiness
title: Merge 可否判定 (CI + Critical + 人間 approve の 3 条件)
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/code-reviewer/**"
  - ".claude/skills/implementation-workflow/**"
related_adrs:
  - ADR-0018
  - ADR-0019
related_plan: docs/harness/plan.md §5.4.3 / R-15
---

# merge-readiness.md — Merge 可否判定規約

> `implementation-workflow` Phase 7 で PR を Ready 昇格 → squash merge する前に
> 充足すべき 3 条件を規定。auto-merge 禁止 (R-15) と orchestrator 明示承認による
> R-15 代替パスの位置付けも明記。

## Ready 昇格の 3 条件 (すべて必須)

| 条件 | 判定方法 | 失敗時の対応 |
|---|---|---|
| **CI green** | `gh pr checks <PR#>` で全 check が `pass` または `neutral` | 失敗 check の logs を `gh run view --log` で取得 → Phase 3 fix loop に戻る |
| **code-reviewer Critical = 0** | Phase 6 の Coordinator 集約結果で全 aspect の Critical findings がゼロ | Critical を修正 → Phase 3 → Phase 6 を再実行 (fix loop) |
| **人間 approve** | orchestrator (subroh0508) または GitHub `Approve` review | approve 取得まで待機、自動マージ禁止 |

3 条件のうち 1 つでも欠けたら **Ready 昇格不可**。Draft で維持し Phase 3 / 6 に戻る。

## auto-merge 禁止 (R-15)

- **GitHub の `auto-merge` 機能を使わない**: PR が approve + check 通過時に自動マージされる挙動は採用しない
- 理由: GitHub Agentic Workflows 原則 (人間が最終判断、AI は補助)、Critical 0 でも人間が「もう少し見たい」と判断する余地を残す
- 機械強制: GitHub branch protection で `Require a pull request before merging` + `Dismiss stale pull request approvals` を有効化 (A6 で本格化)、`Allow auto-merge` は OFF
- 違反検出: `pr-poller` Phase 2 で「approve 後 N 分以内に自動マージされた PR」を検出して warning (A4 / A6 で導入)

## orchestrator 明示承認による R-15 代替パス

- **本 PR が「ハーネス改修 / レトロ起票 / EPIC 配下 PR / mirror PR」かつ orchestrator (subroh0508) が事前承認済み** の場合のみ、AI が `gh pr merge --squash` (または `--merge`) を直接実行することを許可
- 事前承認の形式: ユーザー指示テキスト中に「self-merge」「Phase 7 含む承認」「orchestrator 明示承認」等の文言を含む
- 実績: A2-2 (PR #125 `--merge`) / A2-4 (PR #123) / A2-5 (PR #126) で運用、本 rule の確立は本 PR (A2-3)
- 代替パスでも **3 条件 (CI green + Critical 0 + 事前承認テキスト) は全て必須**: 事前承認は 3 条件目「人間 approve」の同等物として扱う
- **GitHub Approve review は不要** (orchestrator がコメントで approve を兼ねる)、ただし PR description / merge commit message に「orchestrator 明示承認による R-15 代替」を必ず記録

## squash merge vs merge commit

| 種別 | コマンド | 採用基準 |
|---|---|---|
| **squash merge (既定)** | `gh pr merge --squash <PR#>` | 単一論点の Plan / Epic 配下 PR / 小型 harness PR (大多数のケース) |
| **merge commit** | `gh pr merge --merge <PR#>` | 複数 commit の独立性を保ちたい場合 (A2-2 PR #125 が `--merge` 採用、35 ファイル本格化の commit 履歴保持) |
| **rebase merge** | `gh pr merge --rebase <PR#>` | 採用しない (linear history は squash で十分) |

## merge 前のチェックリスト

```text
- [ ] CI 全 check が pass / neutral (gh pr checks)
- [ ] code-reviewer 4-8 aspect の Critical findings = 0 (Coordinator 集約結果)
- [ ] 人間 approve または orchestrator 事前承認テキストあり
- [ ] PR description frontmatter の type / related_plan / related_epic / related_specs / related_adrs / expected_modules が埋まっている
- [ ] commit-message subject が Conventional Commits 形式に準拠
- [ ] master との rebase 状況確認 (`gh pr view <PR#> --json mergeable`)
- [ ] (mirror PR の場合) 対象フェーズ ID / 完了根拠表更新内容を本文に記載
- [ ] (refactor PR の場合) Behavior Preservation 証拠 (`./gradlew check` + Roborazzi diff) を本文に記載
- [ ] (Renovate PR の場合) 互換性影響セクションが埋まっている
```

## merge 後の追跡アクション (Phase 8)

- **pr-poller 即時起動** → `pr-retrospective` Skill で learning ファイル生成 (`harness/learnings-batch-YYYY-WW` ブランチ)
- **roadmap-tracker 起動** (Epic 配下 PR / B-A-C フェーズ項目のみ、Plan 単体は対象外) → `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の完了根拠表 + 着手順変更履歴を更新
- **mirror PR がない場合は手動更新 PR を別途起票** (`harness/roadmap-mirror-<phase-id>`、A3 まで継続)
- 詳細は `.claude/rules/implementation-workflow.md` Phase 8 + `.claude/rules/roadmap.md` 参照

## 機械検証 (A6 で導入予定)

- **GitHub branch protection**: `Allow auto-merge` を OFF、`Require approvals: 1` (orchestrator)、`Require status checks to pass before merging` を有効化
- **GitHub Actions**: PR merge イベントで「auto-merge=true で merge された PR」を検出 → orchestrator に警告通知 (R-15 違反検出)
- **Gradle カスタムタスク**: `pr-poller` 起動時に最近 N 日の merged PR について「Critical = 0 のレビューが Coordinator から post されている」「人間 approve または orchestrator 事前承認テキストの存在」を二重チェック

## Gotchas

- **`Allow auto-merge` を OFF に保つ**: GitHub branch protection 設定変更時 (A6) も auto-merge は無効化のまま
- **Critical 0 でも保留判断は OK**: code-reviewer Coordinator が「Critical = 0 → Ready」と判定しても、人間が「もう少し見たい」と判断したら Ready 昇格を保留する
- **orchestrator 明示承認は本 PR の type で記録**: PR description に承認文言の引用 (orchestrator 指示テキストの抜粋) を残し、後追い監査可能性を担保
- **fix loop 上限超過 (3 回) で merge readiness 不可** (`implementation-workflow.md` R-14)、Plan status を `blocked` に書き換えて人間に通知
- **mirror PR は merge readiness の 3 条件が緩和される**: CI 対象が docs のみ (実装コード変更ゼロ) のため `pass / neutral` 判定、code-reviewer は spec-conformance / architecture / security の 3 aspect で十分 (visual-regression / design-tokens / performance / test-quality / code-quality は skip 妥当)、人間 approve は orchestrator 事前承認で代替可

## 関連

- ADR 0018 (`implementation-workflow` 10 フェーズ設計)
- ADR 0019 (`code-reviewer` 8 aspect + Coordinator + Merge readiness 判定)
- `docs/harness/plan.md` §5.4.3 / R-15
- `.claude/rules/{implementation-workflow,code-reviewer-aspects,pr-draft-policy,pr-template,roadmap}.md`
- `.claude/skills/{implementation-workflow,code-reviewer}/SKILL.md`
