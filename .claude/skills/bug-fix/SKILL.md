---
name: bug-fix
description: |
  bug 報告を入力に再現手順 / root cause / 仕様 gap を分析し、Plan 起票で完結する Skill。
  修正実装は本 Skill ではなく implementation-workflow に委譲。再現テスト案 + git blame 駆動
  root cause を Plan に含める。spec gap 発見時は docs/specifications/ 補強も起票。
status: skeleton
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §A3 / §5.3
related_rules:
  - .claude/rules/plan.md
  - .claude/rules/docs-structure.md
  - .claude/rules/spec-living-sync.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/template-language.md
  - .claude/rules/mcp-usage.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0024
  - ADR-0027
---

# bug-fix

> **5 行以内 summary**: bug 報告を入力に再現手順・root cause・仕様 gap を分析し、Plan 起票で
> 閉じる Spec Gen 系 Skill。修正実装には踏み込まず implementation-workflow に handoff し、
> Generator/Evaluator 独立性 (R-13) を維持。再現テスト案 + git blame 駆動 root cause + 仕様
> 補強要否を Plan に必須セクションとして含める。docs/specifications/ への gap 補強が必要な
> ケースは spec-living-sync 規約に従い同 PR or 後続 PR で起票する。

## 役割

- 人間 / 他 Skill (pr-poller / 他 retro) からの **bug 報告を入力** に受け取り、Spec Gen フェーズを担当
- **再現手順** を確立 (手動再現 + Kotest / Roborazzi など自動再現テスト案)
- **root cause 分析** (関連コード / Napier ログ / `git blame` / SPEC との突き合わせで原因特定)
- 既存仕様 (`docs/specifications/**`) との **gap 検出** → 仕様補強案を `docs/specifications/` 配下に起票
- 単一 PR で完結する修正なら `plan-author` Skill を呼んで Plan 起票 (再現 / root cause / 修正方針 / 仕様補強リンクを Plan に含める)
- **修正実装 / Lint / Test / Review / Merge は委譲**: 本 Skill は Plan 起票で停止し、後続の `implementation-workflow` (Plan を入力に Phase 0-9 自走) に handoff
- 修正範囲が複数 PR にまたがる規模 (`.claude/rules/plan.md` 昇格条件: > 1,000 行 / > 30 ファイル / 仕様横断 等) と判明したら `epic-author` Skill に委譲

## 入力

- **bug 報告**: 人間からの自然言語 (Slack / GitHub Issue / 対面) または別 Skill (`pr-retrospective` の Problem セクションから派生する後続 bug 等) からの起動 prompt
- **再現環境情報**: OS / ブラウザ / アプリ version / 触ったボタン / 入力データ / 観測した挙動 vs 期待挙動の差分
- **関連ログ / スタックトレース**: Napier 出力 / Cloud Run logs / Konsist 違反 / Roborazzi diff 等 (PII / Secrets redaction 後、`.claude/rules/pii.md` / `secrets.md` 準拠)
- **関連コード / git history**: `git blame` / `git log -p` / JetBrains MCP の IDE indexing (`.claude/rules/mcp-usage.md`) で探索
- **関連 SPEC / ADR**: `docs/specifications/**` の対応する SPEC-NNN-N、`docs/adr/**` の関連 ADR (必要に応じて `feature-request` Skill が過去に起票した spec も参照)
- **Context7 MCP**: 外部ライブラリ起因の bug の場合、該当 version 固有の挙動を `Context7 MCP` で確認 (`.claude/rules/mcp-usage.md`)

## 出力

- **`docs/plans/PLAN-NNN-fix-<slug>.md`** (新規 1 ファイル、`plan-author` Skill 経由): type=`bug-fix`、必須セクションは下記 §Plan 必須セクションテンプレ
- **`docs/specifications/{basic,detail}/SPEC-*-fix-N.md`** (任意、gap 検出時のみ): 既存 SPEC の補強 / 新規 SPEC、`spec-living-sync` 規約準拠
- **再現テスト案**: Kotest `DescribeSpec` または Roborazzi ケースのスニペット (Plan の §再現テスト案 に貼付、実装は implementation-workflow 側)
- **`docs/plans/INDEX.md` 追記**: `plan-author` が自動更新 (本 Skill が直接 touch しない)
- **handoff 通知**: orchestrator pane / 人間に「Plan #NNN 起票完了、implementation-workflow に渡せます」を 1 行で報告
- **副作用**: ロードマップ更新は行わない (Plan は `roadmap-tracker` 対象外、R-34)、PR 起票は行わない (Spec Gen フェーズで完結、後続 implementation-workflow が Draft PR 起票担当)

## フェーズ別動作

### Phase 1: bug 報告把握 + 関連コード / ログ / git blame 調査

- bug 報告本文を構造化 (現象 / 期待挙動 / 再現環境 / 観測ログ) し、不明点は orchestrator pane / 人間に確認 (cmux send 直送 200 字未満 OK、長文確認は `.claude/rules/orchestrator-criteria.md` プロンプト送信プロトコル準拠)
- JetBrains MCP の IDE indexing で関連 file / function を一次特定、`git blame` で **該当行の最終変更コミット** と PR を抽出
- Napier / Cloud Run / Konsist / Roborazzi 等のログ抜粋を収集する際は **PII / Secrets redaction を必ず通す** (`.claude/rules/pii.md` / `secrets.md`)
- 外部ライブラリ起因の疑いがある場合は Context7 MCP で該当 version の API 仕様を確認 (training data の古い情報や hallucination を回避)

### Phase 2: 再現手順確立 (手動 + 自動再現テスト案)

- **手動再現手順** を `1. ... → 2. ... → 3. 期待挙動 vs 観測挙動` 形式で確立、再現率 (常時 / N 回中 M 回 / 特定環境のみ) を併記
- **自動再現テスト案** を Kotest `DescribeSpec` (ViewModel / Repository / Backend layer) または Roborazzi (UI / screenshot regression) のスニペットで起草
  - テストはまだ書かない (実装は implementation-workflow が担当)、Plan に貼付する案として作成
  - 既存テスト規約は `.claude/rules/kotlin-test.md` / `.claude/rules/screenshot-test.md` 準拠
- 再現できない bug (intermittent / 環境依存 / 古い session のみ) の場合は **再現率 N/A + 根拠不明** と明示し、Phase 3 で root cause 推定の信頼度を低めに記録

### Phase 3: root cause 分析 (関連 commit / SPEC との照合)

- Phase 1 で抽出した `git blame` 結果と関連 PR 本文 / 関連 learning ファイル (`docs/harness/learnings/**`) を突き合わせて **直接原因** と **間接原因** を分離
- 仕様 (`docs/specifications/**`) との突き合わせで以下を判定:
  - **仕様通り実装されているが仕様自体に欠陥** → §Phase 4 で gap 補強起票
  - **仕様と実装が乖離** (実装側のバグ) → 修正方針を §Phase 5 Plan に記載、仕様補強は不要
  - **仕様未定義の挙動** → §Phase 4 で新規 SPEC 起票候補
- `git blame` の解釈注意点 (本 Skill §Gotchas 参照): 直前 PR 単独で原因確定せず、間接的に呼び出し元 / 依存先まで追う

### Phase 4: 仕様補強要否判定 (gap あれば docs/specifications/ 起票)

- Phase 3 で「仕様自体に欠陥」または「仕様未定義」と判定された場合のみ、`docs/specifications/{basic,detail}/` に補強案を起票
- 補強案は `spec-living-sync` 規約に従い:
  - 既存 SPEC 改修 → `SPEC-NNN-N` の対応セクションを編集 + frontmatter `updated_at` 更新
  - 新規 SPEC → `SPEC-NNN-N+1` を採番 (該当 docs/specifications サブツリーの最大番号 +1)
  - 設計書本文に **コード断片は書かない** (`.claude/rules/docs-structure.md` §4.6 のコード禁止原則)
- 補強案を `plan-author` Skill が起票する Plan の §関連 セクションに **`related_specs` として記載**
- gap なしの場合は本 Phase をスキップし Phase 5 へ

### Phase 5: Plan 起票 (再現 / root cause / 修正方針 / 仕様補強リンク)

- `plan-author` Skill を呼び出して `docs/plans/PLAN-NNN-fix-<slug>.md` を生成
- Plan の type 列は `bug-fix` 固定 (Conventional Commits の `fix` に対応、`.claude/rules/plan.md` §frontmatter 必須キー)
- Plan 必須セクション (下記テンプレに従う、`.claude/rules/plan.md` §本文構造 + bug-fix 固有拡張):
  - §再現手順 (Phase 2 で確立、手動 + 自動テスト案)
  - §root cause (Phase 3 で特定、直接 + 間接原因)
  - §修正方針 (実装に踏み込まない範囲で、touch 予定モジュール / 期待振る舞い)
  - §仕様補強リンク (Phase 4 の起票結果、`related_specs` 配列に列挙)
  - §受け入れ基準 (AC) (再現テストが green / 既存テストが regression なし / 仕様補強 PR がマージ済 等、検証可能な箇条書き)
- 規模が 1 PR を超える判明時は `epic-author` に委譲 (`.claude/rules/plan.md` §Epic 昇格条件)

### Phase 6: implementation-workflow に handoff

- Plan 起票完了を orchestrator pane / 人間に 1 行 print (例: `★ bug-fix: PLAN-NNN-<slug> 起票完了、implementation-workflow に handoff 可`)
- 本 Skill はここで停止、実装 / Lint / Test / Review / Merge には踏み込まない (Generator/Evaluator 独立性 R-13)
- 後続 implementation-workflow は Plan を入力に Phase 0-9 を自走 (`.claude/rules/implementation-workflow.md`)

## Plan 必須セクションテンプレ (bug-fix 固有拡張)

```markdown
# <bug-fix タイトル>

> **5 行以内 summary**: 現象 / root cause 一行要約 / 修正方針 / 影響範囲 / 関連 SPEC

## 目的
<bug 修正と仕様適合化の目標>

## 背景
<報告経緯 + 観測環境 + 関連 learning / 過去 retro>

## 再現手順
1. ...
2. ...
3. 期待挙動: ... / 観測挙動: ...
- 再現率: 常時 / N 回中 M 回 / 環境依存 (詳細)

### 自動再現テスト案
```kotlin
class FooViewModelSpec : DescribeSpec({
  describe("bug-fix PLAN-NNN") {
    it("<再現条件>") {
      // ...
    }
  }
})
```

## root cause
- 直接原因: ...
- 間接原因 / 連鎖: ...
- 関連 commit: `<sha>` (#NNN by <author>)

## 修正方針
- touch 予定: `<module path>`
- 期待振る舞い: ...
- スコープ外: ...

## 仕様補強リンク
- 既存 SPEC 改修: SPEC-NNN-N (`docs/specifications/...`)
- 新規 SPEC: SPEC-NNN-N+1 (該当時のみ)

## 受け入れ基準 (AC)
- [ ] AC-1: 自動再現テスト (Kotest / Roborazzi) が green
- [ ] AC-2: 既存テストが regression なし
- [ ] AC-3: 仕様補強 PR がマージ済 (該当時)
- [ ] AC-4: ...
```

## Gotchas

- **修正実装に踏み込まない**: Phase 5 で Plan 起票完了したら停止。コード編集 / Test 書き起こし / Lint 修正は implementation-workflow が担当 (Generator/Evaluator 独立性 R-13、ADR 0018 / `.claude/rules/implementation-workflow.md`)
- **再現できない bug の扱い**: 再現率 N/A の場合でも root cause 推定 + 仮説検証用テスト案は記載し、Plan §受け入れ基準 に「実地検証で再現確認」を含める。「再現不能」を理由に Plan を破棄せず、保留 (`status: in-progress` で `decisions.md` に保留理由を追記) で残す
- **`git blame` の解釈注意**: 直前 PR が「単に該当行を移動した」「format 修正で touch した」だけのケースあり。`git log -p -- <file>` で **意味的変更** が入った PR まで遡る。間接呼び出し元 / 依存ライブラリ更新が真の原因のケースも頻発
- **regression test 必須**: 再現できる bug は Plan §自動再現テスト案 を **必ず** 添付。テストなしで修正のみする PR は本 Skill の handoff 対象外 (implementation-workflow 側で Phase 6 reviewer の test-quality aspect が Critical 判定)
- **仕様 gap の見落とし**: 「仕様通り実装されているのに bug」のケースを「実装バグ」と誤認しない。Phase 3 で **必ず SPEC との突き合わせ** を行い、gap を見つけたら Phase 4 で補強起票
- **PII / Secrets redaction**: bug 報告 / Napier ログ / スタックトレース / `gh pr view` 出力に PII (display name / メール / GIS avatar URL / sub claim / IPv4/v6) や Secrets (API key / Bearer / JWT) が混入していないか **必ず redaction を通す** (`.claude/rules/pii.md` / `secrets.md`)。Plan / SPEC 補強 docs / handoff 通知に PII が漏出すると incident 扱い
- **昇格判定の閾値**: 修正範囲が 1,000 行超 / 30 ファイル超 / 仕様横断 / レビュー aspect 3+ 走査見込みなら **Epic 昇格** (`.claude/rules/plan.md` §Epic 昇格条件)。bug-fix も例外ではない (例: セキュリティ事象が複数モジュールに波及するケース)
- **dependency-upgrade との境界**: 依存ライブラリ起因の bug は本 Skill ではなく `dependency-upgrade` Skill が担当 (`pr-poller` が Renovate ラベル PR を検出して起動、`.claude/rules/pr-poller.md`)。ただし「依存更新済だが挙動 regression が残った」場合は本 Skill 起動が適切
- **再現環境の明示**: OS / ブラウザ / アプリ version は **必ず Plan に記載**。「特定環境でのみ再現」「複数環境で再現」の差で修正方針が変わる
- **classifier 迂回禁止**: 本 Skill が出力する Plan / SPEC 補強 docs に `--force-with-lease` 等の destructive 文言 / 「auto-merge」「self-merge」「force-merge」等のメタ言及語を含めない (`.claude/rules/orchestrator-criteria.md` §classifier 迂回 NG/OK 辞典)

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、GitHub Actions から Claude API 呼ばない)
- ADR 0018 (implementation-workflow 10 Phase 設計、Generator/Evaluator 独立性 R-13)
- ADR 0024 (MCP 採用、JetBrains MCP / Context7 MCP / `gh` CLI 優位)
- ADR 0027 (docs 構造 + 命名規約 + 日本語化方針)
- `docs/harness/plan.md` §A3 (Spec Gen 系 4 Skill 完成) / §5.3 (Skill 一覧) / §5.4 (ハーネスループ 6 フェーズ)
- `.claude/rules/plan.md` (Plan 命名規約 + Epic 昇格条件 + frontmatter 必須キー)
- `.claude/rules/docs-structure.md` (`docs/specifications/**` の構造 + 設計書本文のコード禁止原則)
- `.claude/rules/spec-living-sync.md` (実装中の仕様変更時の双方向同期 = 本 Skill Phase 4 の SoT)
- `.claude/rules/skill-authoring.md` (本 Skill が準拠する SKILL.md フォーマット / 100-point rubric)
- `.claude/rules/template-language.md` (日本語見出し必須、ADR 0027)
- `.claude/rules/mcp-usage.md` (JetBrains MCP / Context7 MCP の使い分け、PII / Secrets redaction)
- `.claude/rules/pii.md` / `.claude/rules/secrets.md` (redaction チェックポイント)
- `.claude/rules/kotlin-test.md` / `.claude/rules/screenshot-test.md` (再現テスト案の記述規約)
- `.claude/rules/pr-poller.md` (dependency-upgrade との境界 / Renovate 起動経路)
- `.claude/skills/plan-author/SKILL.md` (本 Skill が呼び出す Plan 起票責務)
- `.claude/skills/epic-author/SKILL.md` (Epic 昇格時の委譲先)
- `.claude/skills/pr-retrospective/SKILL.md` (post-mortem 構造 / KPT、本 Skill の Problem セクションを後続 bug の入力として参照)
- `.claude/skills/implementation-workflow/SKILL.md` (本 Skill が handoff する後続 Skill、Phase 0-9 自走)
- `.claude/skills/orchestrator/SKILL.md` (本 Skill を起動する上位 orchestration、プロンプト送信プロトコル準拠)
