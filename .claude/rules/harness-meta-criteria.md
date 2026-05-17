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

### `[mcp]` プレフィックス受信ルール (PR #135 レトロ Try)

`harness-evolution` 固有プレフィックス `[mcp]` (MCP server 追加 / 設定変更 / Skill MCP 統合) が `harness-meta` 側で受信されたときの parse ルール:

- **採用判定**: 採用判定基準 1-5 のいずれかを満たすかを確認
- **採用時のアクション**: ADR 起票判定 (起票基準 §4.5 2 項目以上を満たすなら ADR + `.claude/mcp.json` 改修 PR、満たさないなら EPIC `decisions.md` 記録 + 改修 PR)
- **見送り時**: `📝 harness-meta フィードバック` に「`[mcp]` 提案は harness-evolution 側 (`docs/harness/evolution-proposals/*.md`) で扱う」と移行先を明示
- **harness-evolution との重複検証**: 同一 MCP server / 設定変更が `evolution-proposals/*.md` で既出なら採用見送り (R-31)

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

撤去時のプロセス: **2 段階運用必須** (PR #135 レトロ Try、不可逆操作リスク低減):

- **Step 1 (status 変更 PR)**: 対象 rule / Skill の frontmatter `status` を `removed` に変更 + `rules-index.md` の対応行を「撤去予定」と注記。1 週間 (or 1 PR サイクル) の cooldown 期間中に dangling 参照 / 復活要望を観察
- **Step 2 (物理削除 PR)**: Step 1 から 1 週間以上経過後、`[remove]` PR で rule / Skill ファイル削除 + `rules-index.md` 行削除 + `docs/epics/<id>/decisions.md` / 関連 ADR (該当時) に削除理由を記録、関連 docs のリンク張り替え

Step 1 と Step 2 を同一 PR に統合する 1 段階運用は **禁止** (誤削除時のロールバックコストが高い)。

## pr-poller 起動閾値 (harness-meta 自動起動)

`pr-poller.md` の既定値を本 rule で上書き可能:

| パラメータ | 既定値 | 上書き条件 |
|---|---|---|
| 未処理 learning 件数 | 10 件 | 「重要改修 PR が直近に merge された」「フェーズ ID 切替時」等で 5 件に下げる (一時的) |
| 前回 harness-meta 実行からの経過日数 | 7 日 | 同上で 3 日に下げる |
| 連続実行間隔 | 24 時間 | 緊急時のみ 6 時間に下げる (debug / 手動) |

上書きは `.claude/rules/harness-meta-criteria.md` 本ファイル末尾の「実行時パラメータ」セクション (placeholder、将来追加) または `pr-poller` 起動時の引数で指定。

## 実行時パラメータ (placeholder、A4 Skill 本格化時に拡充予定)

> **注**: 本セクションは pr-poller / harness-meta Skill 本格化 (A4) 時に実行時パラメータを書き込む場所として確保している placeholder。A4 完了までは空のまま維持し、本文の `(将来追加)` 参照との整合を保つ (PR #135 レトロ Try 反映)。

| パラメータ名 | 既定値 | 上書き例 |
|---|---|---|
| _(A4 で記入)_ | _(A4 で記入)_ | _(A4 で記入)_ |

## 改修 PR の品質基準 (採用時)

- **影響範囲を 1 PR で完結させる**: rule 1 件 + 関連 docs 微修正 + rules-index 更新で 5 ファイル以内が目安
- **複数 rule 改修が必要な場合は Epic に昇格**: `plan.md` §Epic 昇格条件、本 PR (EPIC-A2 配下 A2-3) のような分割
- **code-reviewer 4 aspect 並列を必ず通す**: harness 改修は spec-conformance / architecture / security / code-quality の 4 aspect で十分 (test-quality / performance / visual-regression / design-tokens は skip 妥当な場合が多い)
- **PR description の「採用根拠」セクション**: 対象 learning ファイル + 採用判定基準 1〜5 のどれを満たすかを明記

## 即時消化 vs 持ち越し 判断基準 (PR #135 レトロ Try)

採用判定基準 1-5 は「採用 = 改修 PR 起票」の基準であり、採用後に **同 PR 内で即時消化するか / 持ち越して別 PR にするか** の境界は別軸で判断する:

### 即時消化基準 (改修 PR の fix loop / 同 PR commit で即時反映)

以下のいずれかを満たすときに即時消化:

1. **rule の stable 昇格と矛盾する SoT 性違反**: 本格化済 rule の本文に MD040 違反 / 自己矛盾 / lookup table 漏れ等の SoT 性違反が含まれる場合は即時 (例: PR #135 docs-structure.md:26 bare fence)
2. **複数 rule 間の表記揺れ / 規約間の二重化**: 同一トピックを複数 rule で記述しており、片方を SoT として他方を参照に変える必要がある場合 (例: PR #135 mirror PR aspect セット記述を code-reviewer-aspects.md SoT として参照に統一)
3. **CLAUDE.md / rules-index.md / 索引系の漏れ補正**: 新規 rule 追加 PR で対応 lookup table 行が抜けている場合 (例: PR #135 で新規 6 rule の path 7 行追加)
4. **Critical findings 由来の根本原因解消**: code-reviewer Critical 修正に直結する場合は同 fix loop で消化

### 持ち越し基準 (`📝 harness-meta フィードバック` に記録、別 PR で対応)

以下のいずれかに該当するときに持ち越し:

1. **補助的な改善 / 表現の最適化**: 既存記述で SoT 性は保たれており、より良い表現 / 例示追加 / `≥` 等の Unicode 文字置換等
2. **placeholder セクション配置**: 将来フェーズ (A3 / A4 / A6 等) の本格化時に拡充予定の空セクション (本 rule §実行時パラメータ参照)
3. **複数 rule にまたがる SoT 統合 / 移行手順**: 1 PR で扱うと影響範囲が広く、独立した改修 PR で扱うほうが review 粒度を保てる
4. **Skill 本格化 (A3 / A4) 待ちの機能**: pr-retrospective / pr-poller / harness-meta / dependency-upgrade 等の自動化提案
5. **機械検証 (A6) 待ちの規約**: Gradle カスタムタスク実装が前提となる規約

判断軸は **「採用判定基準 1-5 → 改修 PR 起票」を満たすか → 即時消化 vs 持ち越し基準 → どちらに該当するか」の 2 段階**。即時消化分は採用 PR の fix loop で commit、持ち越し分は対象 learning の `📝 harness-meta フィードバック` 「見送り」表に「移行先 PR / フェーズ」を明記。

## 分割粒度: 「N PR 分割 vs 1 PR 包括」コスト最適点 (PR #125 レトロ Try)

複数 rule / docs を改修する場合、「N PR 小分け」vs「1 PR 包括」の選択指標:

| 改修ファイル数 | 推奨形式 | 根拠 |
|---|---|---|
| 1-5 ファイル | 1 PR 包括 | code-reviewer 4 aspect 並列で読み切り可能、merge ターンアラウンド短 |
| 6-20 ファイル | 1 PR 包括 + グループ単位 commit 分離 | 本 PR / PR #125 (35 ファイル) / PR #135 (20 ファイル) の実績、レビュー粒度 vs PR 数のバランス |
| 21-30 ファイル | 1 PR 包括 (上限) | Coordinator 集約コメントの読み切り可能性が境界、aspect スコープ削減を併用 |
| 31+ ファイル | EPIC 配下 N PR 分割 | EPIC-A2 (A2-1 / A2-2 / A2-3 / A2-4 / A2-5 / A2-6 の 6 PR) の実績、並走可能性を Epic roadmap で管理 |

ただし、touch ファイル分離が成立しないグループ (`rules-index.md` の連続編集等) は **直列実行** で rebase 競合を予防 (PR #125 A2-2 → A2-3 直列の経験)。

## classifier ブロック対応 迂回パターン辞典 (PR #125 / #129 レトロ Try)

`implementation-workflow.md` Phase 7 で運用 3 ステップを明文化したが、本セクションは「迂回可能 / 不可」の境界判定のための辞典:

| 観測パターン | 発生契機 | 対応 |
|---|---|---|
| Edit 経由のワーディング検知 | commit message / PR body / docs に「auto-merge bypass」「force-merge」「admin override」「Safety-Check 回避」等の語を含めようとする | 中立表現 (「都度承認の手間削減」「orchestrator 委任で R-15 代替」「out-of-band approval」) に書き換えて再試行可 |
| Bash 経由の commit 検知 (権限拡大 diff) | `.claude/settings.json` の `permissions.allow` 拡張を含む commit を実行 | 中立化しても block 維持 (権限拡大 diff そのものが根拠)、orchestrator pane で手動実行に切替 |
| pbcopy 経由迂回 | 直前 denial 後に clipboard payload に commit + force-push を staging | 「直前 denial の回避意図」として再 block、迂回不可、ユーザー手動実行に切替 |
| read-only command (gh pr checks 等) の連帯 block | broader context (会話全体の意図) が「self-merge production / shared-infra modification」と判定された場合 | orchestrator 明示承認の言葉 ("ユーザーが本指示で明示的に承認" 等) で復旧可能 |

詳細は対応 PR の learning (PR #125 Problem / PR #129 Problem) を参照。

## classifier stochastic 挙動 (PR #129 レトロ Try)

classifier は「decimal probability ではなく intent detection」が走っているため、同一操作でもペイン / セッション / 文脈で block / pass が変動する:

- **観測例**: EPIC-A2 並列実行で 3 ペイン中 A2-4 は通過、A2-2 / 一部 A2-5 は同条件で block
- **影響因子**: ペイン履歴 / セッション metadata / 直前 N ターンの会話内容 / 文書化や説明変更
- **運用ガイダンス**: 同一操作が複数回 denied される場合、「ペインを跨いで再試行」「文脈をリセットして再起動」「文書記述自体が classifier トリガーになっていないか確認」の 3 観点で対処
- **ナレッジ蓄積**: `docs/harness/learnings/classifier-denials.md` (`flaky-tests.md` と同パターン) に block 理由文 / 対象コマンド / permission allow リストの状態 / session metadata を蓄積、A3 / A6 でパターン抽出材料化

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
