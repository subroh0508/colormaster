---
name: refactor
description: |
  人間 / 他 Skill からの refactor 要求 (構造改善 / 命名変更 / 依存削減 / モジュール再編 等)
  を入力に、影響分析 + behavior preservation 検証点列挙 + 規模判定で Plan / Epic を起票する
  Skill。実装は後続の implementation-workflow に委譲する。touch ファイル / 既存テスト /
  既存 SPEC への影響 + Roborazzi screenshot baseline 等を Plan に明示し、振る舞いを変えない
  refactor のみを扱う。単一 PR 完結なら plan-author、複数 PR (touch ファイル > 30 等) なら
  epic-author を呼び出す。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §5.3 / §5.4 / R-22
related_rules:
  - .claude/rules/behavior-preservation.md
  - .claude/rules/ui-snapshot.md
  - .claude/rules/plan.md
  - .claude/rules/epic.md
  - .claude/rules/spec-living-sync.md
  - .claude/rules/branch-naming.md
  - .claude/rules/pr-template.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0023
  - ADR-0025
---

# refactor

> **5 行以内 summary**: refactor 要求を入力に、touch ファイル一覧 / 既存テスト / 既存 SPEC への
> 影響 + behavior preservation 検証点 + screenshot baseline 必要範囲 を分析し、単一 PR は
> plan-author、複数 PR (touch > 30 / 期間 > 1 週間 / 3 PR 以上分割が妥当) は epic-author を呼ぶ
> Skill。実装は本 Skill ではなく後続 implementation-workflow に委譲。behavior preservation
> 二本柱 (visual-regression + spec-conformance、ADR 0023) 準拠の検証点を Plan / Epic に明示する。

## 役割

- **影響分析**: refactor 対象モジュール / ディレクトリ / Composable から touch ファイル一覧と既存テスト / 既存 SPEC への波及を抽出
- **behavior preservation 検証点列挙**: `.claude/rules/behavior-preservation.md` の二本柱 (visual-regression + spec-conformance) 準拠で「振る舞いを変えない refactor」を保証する検証点を列挙
- **規模判定**: 単一 PR 完結か複数 PR 分割が妥当かを `.claude/rules/plan.md` §Epic 昇格条件 (touch > 30 / 1,000 行超 / 期間 > 1 週間 等) で判断し、`plan-author` または `epic-author` を呼ぶ
- **実装委譲**: 本 Skill 自身は実装に踏み込まない。Plan / Epic 起票完了時点で後続 `implementation-workflow` (Phase 0 worktree 〜 Phase 9 cleanup) にバトンタッチ
- **A10 完了前のリファクタ制約周知**: Roborazzi baseline 不在期間中は「可逆な内部リファクタのみ」「不可逆な構造変更 (モジュール削除 / API スキーマ変更等) を避ける」を Plan / Epic 本文に注記 (`.claude/rules/behavior-preservation.md` §A10 完了前のリファクタ制約 R-22 と整合)

## 入力

- **refactor 要求テキスト** (人間 / 他 Skill から渡される。例: 「`feature/idol-search` の重複 ViewModel を共通化」「`core/network` の `*Client.kt` 命名統一」「`androidApp` から Compose Multiplatform への共通化準備」)
- **対象モジュール / ディレクトリ globs** (要求から導出可能ならそのまま、曖昧なら JetBrains MCP / `git ls-files` で候補を抽出して要求者に確認)
- **既存テスト / 既存 SPEC** (`**/*Test.kt` / `**/*Spec.kt` / `docs/specifications/basic/SPEC-*.md` / `docs/specifications/detail/SPEC-*.md`)
- **UI 影響時の screenshot baseline 情報** (`docs/design/inventory/screenshots/<composable>-{mobile,desktop}-{light,dark}.png` の 4 パターン baseline 存在確認、`.claude/rules/ui-snapshot.md` Baseline マトリックス参照)
- **関連 ADR / Epic / Plan** (過去に同領域で起票された ADR-NNNN / EPIC-NNN / PLAN-NNN を `docs/{adr,epics,plans}/INDEX.md` から検索)

## 出力

- **Plan 起票** (単一 PR スコープ時): `plan-author` 経由で `docs/plans/PLAN-NNN-<slug>.md` を生成
  - frontmatter `type: refactor`、`expected_modules`、`related_specs`、`related_adrs` を埋める
  - 本文に **影響分析レポート** (touch ファイル一覧 / 既存テスト影響 / 既存 SPEC 影響) + **behavior preservation 検証点リスト** を含める
- **Epic 起票** (複数 PR スコープ時): `epic-author` 経由で `docs/epics/EPIC-NNN-<slug>/` (README / roadmap / open-questions / decisions / progress) を生成
  - README の `expected_modules` に touch 範囲を block 形式で記載 (`roadmap-tracker` の並行可否判定の入力)
  - decisions.md に refactor 単位の分割理由 + behavior preservation 担保策を記録
- **影響分析レポート** (Plan / Epic 本文の §影響分析 セクションとして書き込む):
  - touch ファイル一覧 (glob と件数)
  - 既存テスト (Kotest DescribeSpec 等) への波及
  - 既存 SPEC (`@Spec` annotation 経由のトレーサビリティ含む) への波及
  - UI 影響時の screenshot baseline 範囲 (4 パターン × 対象画面数)
- **behavior preservation 検証点リスト** (同じく §behavior-preservation セクション):
  - **入出力不変性**: refactor 前後で関数 / API の input → output が完全一致
  - **public API 不変性**: モジュール境界の public シグネチャ (Kotlin public 修飾子 / Compose の Composable 引数 / Ktor route schema) が不変
  - **UI screenshot 不変性**: 対象 Composable の 4 パターン baseline diff が `changeThreshold = 0.01` 内
  - **状態遷移不変性**: ViewModel の StateFlow 遷移 (Empty → Loading → Loaded / PartiallyLoaded / Error) が refactor 前後で完全一致
  - **エラーメッセージ不変性**: ユーザー可視文言 (`strings.xml` / compose-resources) が変わらない
- **Plan / Epic から後続 `implementation-workflow` への handoff**: Plan / Epic 起票完了の orchestrator / 人間への通知 + ブランチ命名 (`refactor/<slug>` または `feature/EPIC-NNN-*-pr-NN`、`.claude/rules/branch-naming.md` 準拠) の推奨

## フェーズ別動作

### Phase 1: refactor 要求把握 + 対象範囲確定

- 入力 refactor 要求テキストを解析し、対象モジュール / ディレクトリの globs を確定 (例: `feature/idol-search/**/*.kt`)
- 要求が曖昧な場合は JetBrains MCP の IDE 検索 / `git grep` / `find` で候補を抽出し、要求者に確認 (orchestrator 経由なら recommended 選択肢を提示)
- **「振る舞いを変えるかどうか」の事前判定** (`.claude/rules/behavior-preservation.md` §リファクタの定義 と整合):
  - 振る舞いを変える変更 (API レスポンスフィールド追加 / UI レイアウト変更 / 状態遷移追加 / エラーメッセージ変更 / アニメーション変更) は **refactor 外**、別途 `feature-request` / `bug-fix` Skill に委譲
  - 振る舞いを変えないコード整理のみを以降の Phase で扱う

### Phase 2: 既存テスト / SPEC / ADR との依存関係調査

- touch ファイル globs に対応する `**/*Test.kt` / `**/*Spec.kt` を `git grep '@Spec'` + JetBrains MCP の `find_files_by_glob` で抽出
- `docs/specifications/basic/SPEC-*.md` / `docs/specifications/detail/SPEC-*.md` を `related_specs` frontmatter / 本文の `file_path:line` 参照から逆引きし、波及する SPEC-ID をリストアップ
- `docs/adr/INDEX.md` / `docs/epics/INDEX.md` / `docs/plans/INDEX.md` から同領域で過去に起票された ADR / Epic / Plan を検索し、Plan / Epic の frontmatter `related_adrs` / `related_specs` / `related_epic` に記録
- **影響分析レポート** を構造化リストとして起草 (Plan / Epic 本文に書き込む)

### Phase 3: behavior preservation 検証点列挙

- `.claude/rules/behavior-preservation.md` の二本柱 (visual-regression + spec-conformance) と「リファクタ前後の検証チェックリスト」を参照し、本 refactor 固有の検証点を列挙
- **UI 影響あり** の場合:
  - 対象 Composable の `@Preview` 存在を JetBrains MCP / `git grep '@Preview'` で確認、不在なら `ui-snapshot` Skill 経由で Plan 起票が先決
  - 4 パターン baseline (`docs/design/inventory/screenshots/<composable>-{mobile,desktop}-{light,dark}.png`) の存在を確認、不在なら A10 完了前として PR description 「Behavior Preservation 証拠」セクションで手動検証主体とする
  - `changeThreshold` の方針 (core 画面 0.0 / 補助 0.01-0.05) を Plan に明示
- **UI 影響なし** (純粋なロジック / 命名 / 依存変更) の場合:
  - input → output 不変性 (Kotest 既存テストで担保) + public API 不変性 (Konsist / IDE inspection で検証) を検証点として列挙
- **A10 完了前の制約 (R-22)** の判定:
  - Roborazzi baseline 不在の領域では「可逆な内部リファクタ (private 関数分割 / 命名変更等) のみ」を許可、不可逆な構造変更 (モジュール削除 / API スキーマ変更 / DB マイグレーション) は A10 完了後に持ち越し
  - 該当時は Plan / Epic 本文に注記し、Phase C のリファクタ EPIC に積む

### Phase 4: 規模判定 + Plan or Epic 起票

- `.claude/rules/plan.md` §Epic 昇格条件 (touch > 30 / 1,000 行超 / 期間 > 1 週間 / 仕様変更が複数 SPEC 波及 / 単独 PR で完結見込み立たず) を Phase 2 の影響分析結果に当てはめて判定
- **単一 PR スコープ** と判定 → `plan-author` Skill を呼び出し
  - `Skill skill="plan-author" args="<起票理由 + expected_modules + 影響分析 + behavior preservation 検証点>"`
  - frontmatter `type: refactor` 必須、`expected_modules` block 形式
- **複数 PR スコープ** と判定 → `epic-author` Skill を呼び出し
  - `Skill skill="epic-author" args="<Epic slug + 起票理由 + expected_modules + 構成 PR の分割案>"`
  - 構成 PR の分割は「touch モジュール集合の重複が少ない順」(roadmap-tracker の並行実装容易性判定と整合) を考慮して提案
  - `epic-author` が `roadmap-tracker` を後段自動起動 (詳細は `.claude/rules/epic.md` §自動起動フック)
- **両 Skill とも `example-skills:skill-creator` 経由ではなく直接呼び出し** (本 Skill は Skill 起票ではなく Plan / Epic 起票が責務)

### Phase 5: implementation-workflow への handoff

- Plan / Epic 起票完了後、orchestrator または人間に「Plan / Epic ID + ブランチ命名提案 + implementation-workflow 起動を推奨」を通知
- ブランチ命名は `.claude/rules/branch-naming.md` 準拠:
  - 単一 PR Plan: `refactor/<PLAN-NNN-slug>` (例: `refactor/PLAN-042-extract-common-viewmodel`)
  - Epic 配下 PR: `feature/EPIC-NNN-<slug>-pr-NN` (例: `feature/EPIC-001-feature-restructure-pr-01`)
- PR テンプレートは `refactor.md` を `gh pr create --template refactor.md` で指定 (`.claude/rules/pr-template.md` §refactor.md 参照)
- 本 Skill はここで責務終了。以降の Phase 0-9 は `implementation-workflow` Skill が担当
- `implementation-workflow` が Phase 3 (実装) で `spec-living-sync` 起動条件に該当する場合 (SPEC docs の改修必要時) は同 Skill 側で対応 (`.claude/rules/spec-living-sync.md`)

## Gotchas

- **実装に踏み込まない**: 本 Skill は影響分析 + Plan/Epic 起票で完結。Kotlin source の編集 / `./gradlew check` の実行 / commit / PR 起票はすべて後続 `implementation-workflow` の責務 (`docs/harness/plan.md` §5.3 / R-37)
- **behavior 変更を含む変更は refactor 外**: API レスポンスフィールド追加 / UI レイアウト変更 / 状態遷移追加 / エラーメッセージ変更 / アニメーション変更が含まれる場合は `feature-request` / `bug-fix` Skill に委譲し、refactor PR と分離する (`.claude/rules/behavior-preservation.md` §リファクタの定義)
- **public API 変更時の breaking change 注意**: `core/network/*Client.kt` の関数シグネチャ変更 / `core/model/Repository.kt` interface 変更 / Ktor route の path / query schema 変更は public API 変更扱い。Plan / Epic 本文に「breaking change 影響範囲」セクションを追加し、Conventional Commits subject 末尾 `!` + body `BREAKING CHANGE:` を後続 PR で必須化 (`.claude/rules/commit-message.md` 整合)
- **screenshot diff 必須範囲**: UI 影響あり refactor は 4 パターン baseline (mobile × desktop × light × dark) を **必須**、1 パターンでも欠落すると visual-regression aspect の binary check が失敗する (`.claude/rules/ui-snapshot.md` §機械検証 A10、`.claude/rules/code-reviewer-aspects.md` visual-regression aspect)
- **A10 完了前の不可逆操作禁止**: モジュール削除 / API スキーマ変更 / DB マイグレーション / 大規模 rename はすべて A10 完了後の Phase C に持ち越す。A10 完了前は private 関数分割 / 内部命名変更 / コメント整理等の可逆操作のみ (R-22、`.claude/rules/behavior-preservation.md` §A10 完了前のリファクタ制約)
- **規模判定の境界はグレーゾーン**: touch 25-35 ファイル / 期間 5-10 日のような中間域は迷いやすい。`.claude/rules/plan.md` §Epic 昇格条件の **複数項目同時該当** で Epic 昇格を判断 (touch + 期間 + SPEC 波及で 2 つ以上該当なら Epic)、迷ったら Plan で起票して `status: promoted` で後から Epic 昇格させる方が安全
- **Plan 起票後の Epic 昇格は履歴保持**: 元 Plan を削除せず `status: promoted` + `promoted_to: EPIC-NNN` に更新 (`.claude/rules/plan.md` §Gotchas)
- **`expected_modules` の未記入禁止**: Epic 起票時に `expected_modules` を空にすると `roadmap-tracker` の並行実装容易性ロジックが機能しない (`.claude/rules/epic.md` §自動起動フック)。touch globs を必ず block 形式で記入
- **過去同領域 ADR / Epic の見落とし**: 同じモジュールで過去に refactor Plan / Epic が走った形跡がある場合は `decisions.md` / `learnings/YYYY-MM-DD-pr-N.md` を参照し、当時の判断 / 失敗事例を Plan / Epic の §背景 に引用 (二重実装防止)
- **MCP 経由検索の fallback**: JetBrains MCP が IDE 未起動で利用不可な場合は `git grep` / `find` / `gh` CLI で代替 (`.claude/rules/mcp-usage.md` R-27 Gotchas)
- **PII / Secrets 混入禁止**: 影響分析レポートに CI ログ / Stack trace 抜粋を含める場合は `.claude/rules/pii.md` / `.claude/rules/secrets.md` の redaction パターン (例: メール `@(?!example\.com)` / GitHub PAT `ghp_[0-9A-Za-z]{36}` / Bearer JWT) を Skill 出力前に検証し、`[REDACTED-*]` に置換

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Claude API on Actions 不採用)
- ADR 0018 (implementation-workflow 10 Phase SoT)
- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由、本 Skill 自身は同 ADR 準拠で `skill-creator` から生成された前提)
- `docs/harness/plan.md` §5.3 (Skill の責務、refactor は影響分析・Plan/Epic 起票まで) / §5.4 (Skill + KPT ループ Spec Gen フェーズ) / R-22 (A10 完了前のリファクタ制約)
- `.claude/rules/behavior-preservation.md` (refactor 時の振る舞い維持原則 SoT)
- `.claude/rules/ui-snapshot.md` (UI refactor 時の screenshot baseline 4 パターン、`changeThreshold` 既定値)
- `.claude/rules/plan.md` (Plan ファイル命名規約 / Epic 昇格条件)
- `.claude/rules/epic.md` (Epic ディレクトリ構成 / 自動起動フック)
- `.claude/rules/spec-living-sync.md` (実装中の仕様変更時の双方向同期、後続 implementation-workflow が起動)
- `.claude/rules/branch-naming.md` (`refactor/<slug>` / `feature/EPIC-NNN-*-pr-NN` の命名規約)
- `.claude/rules/pr-template.md` (`refactor.md` テンプレ選択 + Behavior Preservation 証拠セクション)
- `.claude/skills/plan-author/SKILL.md` (Plan 起票責務、本 Skill が呼ぶ依存先)
- `.claude/skills/epic-author/SKILL.md` (Epic 起票責務、複数 PR 規模の場合に呼ぶ)
- `.claude/skills/implementation-workflow/SKILL.md` (本 Skill から handoff される後続 Skill)
- `.claude/skills/ui-snapshot/SKILL.md` (UI refactor 時の `@Preview` 不在検出 + baseline 生成)
