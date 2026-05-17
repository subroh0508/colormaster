---
id: rules-pr-draft-policy
title: Draft PR ポリシー (Draft → Ready 昇格条件)
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/implementation-workflow/**"
related_adrs:
  - ADR-0018
related_plan: docs/harness/plan.md §5.4.2 / R-15
---

# pr-draft-policy.md — Draft PR ポリシー

> `implementation-workflow` Phase 5 で起票する PR は **既定で Draft** とし、code-reviewer 通過 +
> 人間 approve を経て Ready に昇格する。本 rule は Draft で push する状況と Ready 昇格条件、
> Draft 中の WIP commit ポリシーを規定。

## 既定: Phase 5 は `--draft` で起票

```bash
gh pr create --draft --template <type>.md ...
```

- Phase 5 (`implementation-workflow.md` 参照) で `--draft` を必ず付与
- 理由: code-reviewer (Phase 6) 通過前の状態を Ready で公開すると、orchestrator や他レビュアーが「もう merge 可」と誤認するリスク
- 例外: orchestrator が「Draft 不要、即 Ready で起票」と明示指示した場合のみ Ready で起票可

## Draft で push / 維持する状況

| 状況 | 説明 | 次のアクション |
|---|---|---|
| Phase 5 直後 | code-reviewer 未起動、初回 commit push のみ | Phase 6 を起動 |
| Phase 6 fix loop 中 | Critical 修正中 (1〜3 回目) | 修正 commit を追加 push、Phase 6 を再起動 |
| WIP / 進捗共有 | 大規模 PR で途中まで実装、レビュアーから方針 feedback を受けたい | description に `WIP: <現状>` セクションを追加、Ready 化は方針確定後 |
| セルフチェック中 | 自分の差分を `gh pr diff` で再確認 / commit 整理中 | `git rebase -i` 等で整理後、Phase 6 へ |
| CI 失敗修正中 | `./gradlew check` の lint / test 失敗を修正中 | 修正 commit push、Phase 6 へ |

## Ready 昇格条件 (3 条件、`merge-readiness.md` と整合)

| 条件 | 判定方法 |
|---|---|
| CI green | `gh pr checks <PR#>` で全 check pass / neutral |
| code-reviewer Critical = 0 | Phase 6 Coordinator 集約で全 aspect の Critical findings ゼロ |
| 人間 approve または orchestrator 事前承認 | `gh pr review --approve` または PR description / orchestrator 指示テキストに明示承認 |

3 条件充足後に:

```bash
gh pr ready <PR#>
```

で Ready 昇格。

## Draft 中の WIP commit ポリシー

- **WIP commit は許容** (`commit-message.md` 規約は満たしつつ、subject に `WIP:` プレフィックスを付与せず Conventional Commits 形式維持)
- **squash merge で WIP 履歴は集約される** ため、Draft 中の中間 commit は粒度を気にしすぎない
- **`--amend` は NG**: hook 失敗時も新規 commit を作成 (`commit-message.md` Gotchas と整合)
- **fixup commit (`git commit --fixup`) は許容**: squash merge で消えるため、PR 内部の整理用途として使ってよい
- **stash / WIP branch 切り替え** で他作業と並行する場合は worktree 並列を優先 (`branch-naming.md` / `implementation-workflow.md` Phase 0)

## Draft → Ready 昇格時のチェックリスト

```text
- [ ] CI 全 check pass / neutral (gh pr checks)
- [ ] code-reviewer Coordinator が「Critical = 0、Ready」と判定 (PR コメント post 済)
- [ ] PR description frontmatter (HTML コメント) の必須キーが埋まっている
- [ ] PR description 本文の必須セクション (type 別) が記入済
- [ ] 三層指標差分セクションが N/A 明記 (A7 完了前) または値が記入済 (A7 完了後)
- [ ] (refactor PR) Behavior Preservation 証拠記入済
- [ ] (Renovate PR) 互換性影響セクション記入済
- [ ] (Epic 配下 PR / mirror PR) 対象 Phase ID / 完了根拠表更新内容記入済
- [ ] commit-message subject が Conventional Commits 形式準拠 (機械検証は commit-msg hook で実施済のはず)
```

## 機械検証 (A6 で導入予定)

- **GitHub Actions**: PR が `ready_for_review` イベントを発火したときに上記チェックリストを検証、未充足項目があれば warning コメント + Ready 昇格を block するか継続するかを orchestrator 判断
- **`gh pr view --json isDraft`** を pr-poller が定期実行、Draft 状態が 7 日以上継続している PR をリストアップ (stale PR 検知)

## Gotchas

- **Draft の長期放置は避ける**: 7 日以上 Draft が続く PR は status (実装方針見直し / 着手停止 / 削除) を明示。pr-poller が stale 検出 (A4 / A6 で本格化)
- **`gh pr ready` を呼ぶ前に必ず 3 条件確認**: Ready 昇格後に Critical 発覚 → `gh pr ready --undo` で Draft に戻すのは可能だが、レビュアーの混乱を招く
- **Draft でも CI は走る**: Phase 6 と並行して CI green を確認、CI 失敗時は code-reviewer の前に Phase 3 fix loop に戻る
- **Draft PR にも `.github/PULL_REQUEST_TEMPLATE/<type>.md` が適用される**: `--template` 省略禁止は Draft / Ready 共通 (`pr-template.md` 規約)
- **mirror PR (`harness/roadmap-mirror-<phase-id>`) は Draft 経由を省略可**: 完了根拠表 + 着手順変更履歴の追記のみで実装コード変更ゼロのため、`--draft` なしで起票して即 Ready (orchestrator 事前承認による R-15 代替パス、`merge-readiness.md` 参照)

## 関連

- ADR 0018 (`implementation-workflow` 10 フェーズ設計、Phase 5 / Phase 7 の位置付け)
- `docs/harness/plan.md` §5.4.2 / R-15
- `.claude/rules/{implementation-workflow,merge-readiness,pr-template,code-reviewer-aspects,commit-message,branch-naming}.md`
- `.claude/skills/implementation-workflow/SKILL.md`
