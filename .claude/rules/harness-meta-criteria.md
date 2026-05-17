---
id: rules-harness-meta-criteria
title: harness-meta 採用 / 見送り / 撤去判定基準 + pr-poller 起動閾値
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/harness-meta/**"
  - ".claude/skills/pr-poller/**"
related_adrs:
  - ADR-0026
related_plan: docs/harness/plan.md §5.4.5 / R-31
---

# harness-meta-criteria.md — harness-meta 採用 / 見送り / 撤去判定基準

> `harness-meta` Skill (A4 で本格化) が `pr-retrospective` の learning ファイルから集約した
> ハーネス改善提案について **採用 (=> 改修 PR 起票) / 見送り / 撤去** を判定する基準を規定。
> `pr-poller` の起動閾値もここで上書き可能。

## harness-meta vs harness-evolution の責務分離

| 観点 | `harness-meta` | `harness-evolution` |
|---|---|---|
| 駆動契機 | 内部 KPT (learning ファイル) | 外部研究 / ベストプラクティス |
| 起動 | `pr-poller` から自動起動 (閾値到達時) | **手動起動のみ** (`harness-evolution.md` 参照) |
| 入力 | `docs/harness/learnings/*.md` の `🤖 ハーネス改善提案` セクション | ホワイトリスト情報源 + Context7 MCP |
| 出力 | 改修 PR description / `harness-meta フィードバック` 追記 | `docs/harness/evolution-proposals/YYYY-MM-DD.md` + 重要案の Plan / Epic 起票 |
| ラベル | `harness-meta` (PR) | `harness-evolution` (PR) |
| 優先度 | **harness-meta を優先** (内部実体験ベース、R-31) | harness-meta との重複を見送り (R-31) |

## 改善提案の 4 プレフィックス (`retrospective-format.md` と整合)

| プレフィックス | 意味 | 採用時のアクション |
|---|---|---|
| `[rule]` | `.claude/rules/*.md` の新規追加・改修 | rule ファイル直接編集 PR (本 PR のような形式) |
| `[skill]` | `.claude/skills/*/` の新規追加・改修 | `example-skills:skill-creator` 経由で SKILL.md 改修 (`skill-authoring.md` 参照) |
| `[template]` | テンプレート Markdown (PR / Plan / Epic / Learning 等) の改修 | テンプレファイル直接編集 PR |
| `[remove]` | 未使用 rule / dormant Skill の撤去候補 | rule / Skill 削除 PR + `rules-index.md` / `docs/epics/EPIC-A2-rules-docs-extension/decisions.md` 等に削除理由記録 |

## 採用判定基準 (採用 = 改修 PR 起票)

以下のいずれかを満たすときに採用:

1. **複数 PR (>=2) の learning で同じ提案が反復**: 一過性の問題ではなく構造的課題
2. **A1 レトロ 15 提案のような ADR / Plan / Epic で明示的に予約された項目**: 起票根拠が明確
3. **R-XX (`docs/harness/plan.md` の規約 ID) と直接対応**: 規約整合化の必要性
4. **Critical findings から派生**: code-reviewer Critical の根本原因解消につながる提案
5. **orchestrator (subroh0508) が手動で「採用」と明示**: 内部レビューで採用判断

採用時の起票プロセス: 改修 PR (`harness/<purpose>` ブランチ、`harness.md` テンプレ) → `code-reviewer` Skill → Ready 昇格 → merge → `pr-retrospective` の `📝 harness-meta フィードバック` セクションに採用結果を追記

## 見送り判定基準 (見送り = `📝 harness-meta フィードバック` セクションに保留 / 見送り記録)

以下のいずれかを満たすときに見送り:

1. **後続フェーズに移行**: A3 / A4 / A6 / A7 / A10 / Phase B / Phase C で対応予定の項目 (例: A1 レトロの `pr-retrospective` Skill 本格化は A3 へ)
2. **提案重複**: 既に他 learning で採用済 / 同 PR で対応済
3. **コスト / 効果の不均衡**: 改修コストが高く、現状運用で代替可能 (例: `Co-Authored-By` の機械検証は A3 / A6 で実装、現状は人間レビュー任せ)
4. **harness-evolution との重複**: 外部研究側で既に提案済 (R-31)
5. **orchestrator (subroh0508) が手動で「見送り」と明示**: 内部判断

見送り時の記録: 対象 learning の `📝 harness-meta フィードバック` セクション「見送り (後続フェーズへ)」表に「提案 → 移行先フェーズ / 見送り理由」を記録

## 撤去判定基準 (撤去 = rule / Skill 削除 PR)

以下のすべてを満たすときに撤去:

1. **対象 rule / Skill が直近 3 ヶ月の learning / commit / PR で参照されていない**: dormant 確定
2. **撤去によって他 rule / Skill / docs に dangling 参照が発生しない**: grep-based / Konsist で確認
3. **orchestrator (subroh0508) の事前承認**: 削除は不可逆性が高いため明示承認必須

撤去時のプロセス: `[remove]` PR で rule / Skill ファイル削除 + `rules-index.md` / `docs/epics/<id>/decisions.md` / 関連 ADR (該当時) に削除理由を記録、関連 docs のリンク張り替え

## pr-poller 起動閾値 (harness-meta 自動起動)

`pr-poller.md` の既定値を本 rule で上書き可能:

| パラメータ | 既定値 | 上書き条件 |
|---|---|---|
| 未処理 learning 件数 | 10 件 | 「重要改修 PR が直近に merge された」「フェーズ ID 切替時」等で 5 件に下げる (一時的) |
| 前回 harness-meta 実行からの経過日数 | 7 日 | 同上で 3 日に下げる |
| 連続実行間隔 | 24 時間 | 緊急時のみ 6 時間に下げる (debug / 手動) |

上書きは `.claude/rules/harness-meta-criteria.md` 本ファイル末尾の「実行時パラメータ」セクション (将来追加) または `pr-poller` 起動時の引数で指定。

## 改修 PR の品質基準 (採用時)

- **影響範囲を 1 PR で完結させる**: rule 1 件 + 関連 docs 微修正 + rules-index 更新で 5 ファイル以内が目安
- **複数 rule 改修が必要な場合は Epic に昇格**: `plan.md` §Epic 昇格条件、本 PR (EPIC-A2 配下 A2-3) のような分割
- **code-reviewer 4 aspect 並列を必ず通す**: harness 改修は spec-conformance / architecture / security / code-quality の 4 aspect で十分 (test-quality / performance / visual-regression / design-tokens は skip 妥当な場合が多い)
- **PR description の「採用根拠」セクション**: 対象 learning ファイル + 採用判定基準 1〜5 のどれを満たすかを明記

## 機械検証 (A6 で導入予定)

- **`pr-poller` Skill の閾値判定**: `harness-meta-criteria.md` 本ファイルの「pr-poller 起動閾値」表を parse して既定値を上書き
- **Gradle カスタムタスク**: `📝 harness-meta フィードバック` セクションの構造化テーブルが retrospective-format.md の規格に合致するか検証 (A6 / Markdown 機械検証と統合)
- **GitHub Actions**: harness 改修 PR が `code-reviewer` 4 aspect を通過しているか check 連携 (A4 で Skill 統合後)

## Gotchas

- **採用基準 5 / 見送り基準 5 / 撤去基準 3 の「orchestrator 明示」は最終判断**: AI が自律判定する範囲は基準 1-4 まで、5 (撤去は 3) は人間判断
- **改修 PR は harness.md テンプレ必須** (`pr-template.md` / `branch-naming.md` と整合): `harness/<purpose>` ブランチ + `harness.md` テンプレ + `feat(harness)` / `docs(harness)` commit
- **撤去 PR は不可逆性が高いため細心の注意**: dangling 参照検証を grep-based で確認、`rules-index.md` の status を `removed` に変更してから次 PR で削除する 2 段階運用を推奨 (将来追加)
- **harness-meta vs harness-evolution の提案重複**: harness-evolution 側で出力 (`docs/harness/evolution-proposals/YYYY-MM-DD.md`) を確認してから採用判定 (R-31)
- **本 rule の閾値変更も harness-meta-criteria.md 改修 PR として扱う**: メタな自己改修ループ、A4 / A6 完了後に運用熟成

## 関連

- ADR 0026 (harness-evolution Skill 採用、harness-meta との二系統補完)
- `docs/harness/plan.md` §5.4.5 / R-29 / R-30 / R-31
- `.claude/rules/{pr-poller,retrospective-format,harness-evolution,skill-authoring,rules-index}.md`
- `.claude/skills/{harness-meta,pr-poller,pr-retrospective}/SKILL.md`
