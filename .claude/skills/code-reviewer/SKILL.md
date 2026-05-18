---
name: code-reviewer
description: |
  Generator から独立した Evaluator として 8 aspect (spec-conformance / test-quality /
  architecture / security / performance / code-quality / visual-regression / design-tokens)
  をローカル Claude Code のサブエージェントで並列実行し、Coordinator が日本語の構造化
  レビューコメントを PR に post して Merge readiness を判定する。implementation-workflow
  Phase 6 (Evaluation) から呼び出される、または orchestrator pane / 人間から「PR レビューして」
  と指示されたときに本 Skill に従って動作する。R-13 (Generator/Evaluator 独立性) と
  R-37 (Claude API 直接呼び出し禁止、サブエージェント並列) を維持。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §5.3 / §5.4.3 / R-13 / R-15 / R-37
related_rules:
  - .claude/rules/code-reviewer-aspects.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/implementation-workflow.md
related_adrs:
  - ADR-0017
  - ADR-0019
---

# code-reviewer

> **5 行以内 summary**: implementation-workflow Phase 6 から呼ばれる Evaluator Skill。Coordinator が PR の touch
> ファイル分類に応じて 3-8 aspect (spec-conformance / test-quality / architecture / security / performance /
> code-quality / visual-regression / design-tokens) のサブエージェントを並列起動し、binary yes/no checklist
> 結果を集約 → 日本語の構造化レビューコメントを `gh pr comment` で post → Critical 0 で Merge readiness を判定。
> R-13 (Generator/Evaluator 独立性) / R-37 (Claude API 直接呼び出し禁止) / R-26 (PII/Secrets redaction) を維持。

## 役割

- **Generator から独立した Evaluator**: implementation-workflow (Generator) と別 system prompt のサブエージェントとして 8 aspect を並列評価、生成バイアス回避 (R-13、ADR 0019)
- **動的 aspect スコープ判定**: PR 種別 / touch ファイル分類に応じて 3-8 aspect を選択 (harness: 4 / feature: 6 / A10 完了後 UI 変更: 8 / Markdown only: 4 / 権限改修: 3 / mirror: 3 / build script: 3)
- **binary yes/no checklist 評価**: 各 aspect 最低 5 項目 (推奨 7-8 項目) を PASS/FAIL で評価、`code-reviewer-aspects.md` を SoT
- **3 severity 分類**: Critical (merge blocker) / High (重要だが non-blocking) / Improvement (改善提案) で集約、Critical = 0 が R-15 第 2 条件 (`merge-readiness.md`)
- **Coordinator 集約 + PR post**: サブエージェント結果を Coordinator が集約 → 重複指摘排除 → 日本語の構造化レビューコメントを `gh pr comment` で post (`code-reviewer-aspects.md` §Coordinator のレビューコメント形式)
- **Merge readiness 判定**: Critical = 0 で「Ready 推奨」、Critical ≥ 1 で「fix loop 必須」、人間レビュアー向け文言を必ず添える (R-15、人間の最終判断責務を残す)

`implementation-workflow` Phase 6 から呼び出されるのが標準フロー。orchestrator / 人間から直接「PR #NNN をレビュー」と指示されたときも本 Skill に従って動作する。

## 入力

- **対象 PR#**: `gh pr view <PR#>` で取得可能な open / draft / ready PR (closed / merged PR の事後レビューも可能だが推奨は merge 前)
- **起動契機**: implementation-workflow Phase 6 自動呼出、または orchestrator / 人間から「PR #NNN をレビュー」「code-reviewer 起動」等の指示
- **gh CLI / Agent ツール認証済環境**: `gh pr view` / `gh pr diff` / `gh pr comment` / Agent (`subagent_type: general-purpose`) 起動権限
- **参照 rules** (Coordinator + サブエージェント prompt 構築時に Read):
  - `code-reviewer-aspects.md` (8 aspect binary checklist の SoT)
  - `merge-readiness.md` (R-15 3 条件)
  - `pii.md` / `secrets.md` (redaction 規約、R-26)
  - 各 aspect が参照する横断 rule (例: spec-conformance → `docs-structure.md` / `spec-living-sync.md`、architecture → `naming.md` / `viewmodel.md` / `composable.md` 等)
- **PR description frontmatter**: `<!-- pr-frontmatter ... -->` の `type` / `related_plan` / `related_epic` / `related_specs` / `related_adrs` / `expected_modules` を parse して aspect スコープ判定の補助に使用

## 出力

- **Coordinator レビューコメント**: `gh pr comment <PR#> --body-file /tmp/code-reviewer-<PR#>-<timestamp>.md` で post する日本語構造化コメント (`code-reviewer-aspects.md` §Coordinator のレビューコメント形式)
- **Merge readiness 判定**: 「Critical = 0 → Ready 推奨」「Critical ≥ 1 → fix loop 必須」を Coordinator コメントに明示、implementation-workflow Phase 6 への return value として扱う
- **サブエージェント実行ログ**: 各 aspect の Agent 結果 (`subagent_type: general-purpose` の return) は Coordinator が parse して集約、debug 用に `/tmp/code-reviewer-<PR#>-aspect-<name>.md` に残置可
- **副作用**: PR コメントのみ (commit / push / merge 等の git 操作は本 Skill では行わない、Phase 7 の implementation-workflow が担当)
- **redaction 済み出力**: PII / Secrets が含まれていれば `[REDACTED-*]` に置換した上で post (`pii.md` / `secrets.md` redaction 規約、R-26)

## フェーズ別動作 (Coordinator 5 ステップ)

### Step 1: aspect スコープ判定

PR の touch ファイル一覧と PR 種別から評価対象 aspect セットを決定。`code-reviewer-aspects.md` §aspect 動的選択ルール + `merge-readiness.md` §大規模 PR (30+ ファイル) の aspect スコープ自動削減 を SoT として参照。

```bash
# PR 種別判定 (frontmatter type + touch ファイル分類)
gh pr view <PR#> --json body,files,headRefName
gh pr diff <PR#> --name-only
```

| touch ファイル分類 | 既定 aspect セット | aspect 数 | 根拠 |
|---|---|---|---|
| **harness PR** (`.claude/rules/**` / `.claude/skills/**` / `docs/harness/**` 主体) | spec-conformance / architecture / security / code-quality | 4 | 実装コード変更ゼロ、harness 規約準拠が中心 |
| **feature / bugfix / refactor PR** (Kotlin code touch あり、UI 変更なし) | 上記 + test-quality / performance | 6 | A10 完了前は visual-regression / design-tokens skip |
| **feature PR (UI 変更含む、A10 完了後)** | 上記 + visual-regression / design-tokens | 8 | A10 完了後の本格運用、現状 skeleton |
| **Markdown only PR** (`docs/**` 単独) | spec-conformance / architecture / security / code-quality | 4 | 実装コード変更ゼロ |
| **`.claude/settings.json` / `.github/workflows/**` PR** | spec-conformance / architecture / security | 3 | 権限改修 / CI 改修は architecture / security 重視 |
| **mirror PR** (`harness/roadmap-mirror-*`) | spec-conformance / architecture / security | 3 | code-quality は本体 PR で実施済 |
| **OpenAPI yaml only** (`docs/api/colormaster-api.yaml`) | spec-conformance / architecture / security / code-quality | 4 | DTO 同期は別 Plan |
| **build script only** (`build.gradle.kts` / `gradle/libs.versions.toml`) | architecture / security / code-quality | 3 | spec docs 変更なし |

skip aspect は **Coordinator コメント冒頭に明示** (透明性確保、`code-reviewer-aspects.md` 規約)。

### Step 2: aspect サブエージェント並列起動 (R-13 / R-37)

Coordinator が `Agent` ツール (`subagent_type: general-purpose`) を **aspect 数だけ並列起動**。各サブエージェントは独立した system prompt を持ち、Generator (implementation-workflow) のバイアスを受けない (R-13、ADR 0019)。

各 aspect サブエージェントの prompt テンプレ:

```text
あなたは PR #<PR#> の <aspect-name> aspect 専任レビュアー (Evaluator) です。
Generator (implementation-workflow) と独立した system prompt で動作し、
binary yes/no checklist を厳格に評価してください。

参照 SoT:
- .claude/rules/code-reviewer-aspects.md §<aspect-name>
- 横断 rule (例: <aspect 固有の関連 rule リスト>)

評価対象:
- PR diff: gh pr diff <PR#>
- 変更ファイル一覧: gh pr view <PR#> --json files
- frontmatter: gh pr view <PR#> --json body (HTML コメント pr-frontmatter を parse)

検証項目 (binary yes/no checklist、最低 5 項目):
1. [ ] <code-reviewer-aspects.md §<aspect-name> から 1 項目目>
2. [ ] <2 項目目>
...
N. [ ] <N 項目目>

各項目に PASS / FAIL を判定し、FAIL は severity (Critical / High / Improvement) を付与:
- Critical: merge ブロック (R-15 第 2 条件不充足)
- High: 重要だが non-blocking (次 PR 修正可)
- Improvement: 改善提案 (任意対応)

CommonMark / GFM 仕様 (`code-reviewer-aspects.md` §MD040 / CommonMark / GFM 仕様の明示) を踏まえ、
markdownlint rule の対象範囲を誤判定しないこと (MD040 は開始フェンスのみ対象、閉じフェンス対象外 等)。

出力フォーマット (Coordinator が parse):
- 検証項目チェックリスト (N/N PASS/FAIL)
- FAIL 項目の詳細 (file:line + severity + 修正案)
- 総合判定 (PASS / FAIL + severity 集計)
```

並列起動の実装上の注意:

- `code-quality` aspect prompt 冒頭で `code-reviewer-aspects.md` §MD040 / CommonMark / GFM 仕様の明示 セクションを引用 (PR #125 レトロ Try、MD040 35+ 件誤検出再発防止)
- `visual-regression` / `design-tokens` aspect は **`status != "active"` で skip** (A10 完了前のガード、§visual-regression / design-tokens の enable フラグ 参照)

### Step 3: Coordinator 集約 + redaction

各サブエージェント return 結果を Coordinator が集約:

1. **重複指摘排除**: 同一 `file:line` への複数 aspect 指摘を Critical > High > Improvement 順で merge
2. **severity 集計**: Critical / High / Improvement の総数を aspect 別 + 全体で集計
3. **redaction 通過 (必須、R-26)**: `pii.md` / `secrets.md` の redaction パターンで全文を scan → `[REDACTED-EMAIL]` / `[REDACTED-SECRET]` / `[REDACTED-AVATAR-URL]` / `[REDACTED-UID]` / `[REDACTED-IP]` / `[REDACTED-AWS-KEY]` / `[REDACTED-GH-PAT]` / `[REDACTED-JWT]` に置換
4. **構造化コメント生成**: `code-reviewer-aspects.md` §Coordinator のレビューコメント形式 のフォーマットに沿って Markdown 生成 → `/tmp/code-reviewer-<PR#>-<timestamp>.md` に Write

### Step 4: PR コメント post

```bash
gh pr comment <PR#> --body-file /tmp/code-reviewer-<PR#>-<timestamp>.md
```

- post 前に redaction 通過済を確認 (Step 3 で実施済)
- post 後、`/tmp/code-reviewer-<PR#>-<timestamp>.md` は debug 用に残置可 (PII / Secrets を含まない前提)
- post 失敗時 (`gh pr comment` Exit 1) は Coordinator が error 報告 → implementation-workflow Phase 6 が fix loop or 中断判断

### Step 5: Merge readiness 判定

| Critical 件数 | 判定 | 次アクション |
|---|---|---|
| 0 | Ready 推奨 (R-15 第 2 条件充足) | implementation-workflow Phase 7 へ進む (CI green + 人間 approve と合わせて 3 条件評価) |
| ≥ 1 | fix loop 必須 | implementation-workflow Phase 3 に戻る (累計 fix loop 上限 3 回、R-14、超過時は `blocked` + 人間通知) |

判定結果を Coordinator コメントの「マージ可否」セクションに明示 (`code-reviewer-aspects.md` §Coordinator のレビューコメント形式)。**人間レビュアー向け文言** (「AI レビューの指摘で十分でしょうか?」) を必ず添える (R-15、人間の最終判断責務を残す、auto-merge 禁止と整合)。

## aspect 別 binary checklist 数 (`code-reviewer-aspects.md` SoT)

| aspect | 項目数 | 主観点 |
|---|---|---|
| spec-conformance | 7 | `@Spec` annotation、SPEC-ID 整合、設計書コード断片混入なし、spec-living-sync 記載 |
| test-quality | 7 | 差分カバレッジ、Spec coverage、mutation score、tautological テスト、fixture `@example.com` |
| architecture | 7 | レイヤー依存方向、モジュール越境、命名規約、循環依存、SoT 矛盾、ADR 起票基準、rules-index status 整合 |
| security | 7 | PII / Secrets redaction、`.gitignore` 対象、認証境界、Firebase 検出、Dockerfile users.db、.dockerignore |
| performance | 5 | N+1、Compose 再描画、coroutine スコープ、hot reload、Flow/Channel backpressure |
| code-quality | 7 | error-handling、命名、可読性、defensive 過剰、WHY コメント、Markdown フェンス言語、日本語見出し |
| visual-regression (A10 後 enable) | 5 | 4 パターン baseline、Roborazzi changeThreshold、意図的更新明記、wasmJs actual 例外、アニメ停止 |
| design-tokens (A10 後 enable) | 5 | hex hardcode、sp/dp 固定値、Material 直接参照、DESIGN.md 整合、テスト / Preview 例外 |

各項目の正確な文言は `code-reviewer-aspects.md` を SoT とし、本 SKILL.md では数のみ示す (drift 予防)。

## visual-regression / design-tokens の enable フラグ (A10 完了後)

A10 完了 (DESIGN.md + UI Inventory + Roborazzi baseline 揃う) 前は **`status != "active"` で skip** する。enable 手順 (`code-reviewer-aspects.md` §A10 完了後 enable 手順 4 ステップ):

1. **A10 完了マイルストーン確認**: DESIGN.md + UI Inventory + Roborazzi baseline 生成済
2. **本 SKILL.md の aspect status 更新**: `code-reviewer-aspects.md` §8 aspect 概要 表で該当 aspect を `A10 完了後 enable` → `active` に書き換え
3. **Coordinator 並列起動対象に追加**: §Step 1 の aspect スコープ判定表の「feature PR (UI 変更含む、A10 完了後)」を有効化、`status == "active"` ガードを通過させる
4. **binary checklist 各 5 項目の確定**: `code-reviewer-aspects.md` §visual-regression / §design-tokens の項目を Coordinator が parse して機械検証

A10 完了前に誤って enable しないよう、Coordinator は Step 1 の判定で `visual-regression` / `design-tokens` が aspect セットに含まれる場合 `code-reviewer-aspects.md` の該当行 status を確認し、`active` でなければ skip + Coordinator コメントに「A10 完了後 enable のため skip」を明示する。

## 3 severity 分類詳細

| severity | 定義 | merge 影響 | 例 |
|---|---|---|---|
| **Critical** | R-15 第 2 条件 (Critical = 0) を直接ブロック、merge してはいけない | merge ブロック | PII / Secrets 混入、モジュール越境、認証境界破壊、SoT 矛盾、設計書本文へのコード断片混入 |
| **High** | 重要だが non-blocking、次 PR / fix loop で修正推奨 | non-blocking (人間判断) | Spec annotation 不足 (A7 完了前は warning)、Markdown フェンス言語指定漏れ、命名規約軽微違反 |
| **Improvement** | 改善提案、任意対応 | non-blocking | コメント追加、可読性向上、defensive 過剰削減 |

Coordinator は集約コメントで severity 別にセクション分けし、Critical を最上部に配置 (`code-reviewer-aspects.md` §Coordinator のレビューコメント形式)。

## fix loop との連携 (R-14、implementation-workflow.md と整合)

- code-reviewer Critical 修正の fix loop 上限: **3 回** (`code-reviewer-aspects.md` §fix loop 上限)
- 累計 fix loop (Phase 3 + Phase 6) 3 回超過時は implementation-workflow が Plan / Epic decisions.md に `status: blocked` を記録し人間に通知
- 本 Skill は fix loop 自体を実行しない (implementation-workflow Phase 3 の責務)、Critical 0 まで再 invoke される受け身の立場

## Gotchas

- **Generator (`implementation-workflow`) と独立した system prompt** で動作 (R-13、ADR 0019): 各 aspect サブエージェントは Agent ツール (`subagent_type: general-purpose`) で起動し、Generator のコンテキストを引き継がない
- **Claude API への直接呼び出しは禁止** (R-37、ADR 0017): ローカル Claude Code のサブエージェント (Agent ツール) で並列実行、GitHub Actions / CI からは絶対に Claude API を呼ばない
- **Coordinator も同セッション内で動作**: Coordinator はサブエージェント結果を集約する役割で、別 Claude API session ではない (R-37 と整合)
- **各 aspect は binary yes/no eval checklist を最低 5 項目** 持つ (R-13、`code-reviewer-aspects.md` §各 aspect の binary yes/no eval checklist): 主観評価ではなく PASS/FAIL の二択
- **PR コメント post 前に PII / Secrets redaction を必ず通す** (R-26、`.claude/rules/pii.md` / `.claude/rules/secrets.md`): CI ログ / diff 内に含まれるメアド / `googleusercontent.com` URL / IP / `sub` claim 値 / API key / token / JWT を `[REDACTED-*]` で置換してから `gh pr comment` する。redaction を通さずに post してしまった場合は **即時編集 or 削除し、漏洩した PII を learnings に記録** (`docs/harness/learnings/<date>-pr-<n>.md` Problem セクション)
- **`code-quality` aspect agent prompt に CommonMark / GFM 仕様の明示参照** (PR #125 レトロ Try、MD040 35+ 件誤検出再発防止): 各 markdownlint rule の対象範囲を agent prompt 冒頭で明文化、特に **MD040 は開始フェンスのみ対象、閉じフェンス (バッククォート 3 つ単独) は対象外**
- **`visual-regression` / `design-tokens` aspect は A10 完了後に有効化**: A10 完了前は Coordinator が `status != "active"` で skip する gating を実装、誤 enable 防止
- **aspect 動的選択ルールに従う** (`code-reviewer-aspects.md` §aspect 動的選択ルール): harness PR は 4 aspect / feature PR は 6 aspect / mirror PR は 3 aspect / 権限改修 PR は 3 aspect、skip aspect は Coordinator コメントに明示 (透明性確保)
- **Critical = 0 で Ready 推奨だが auto-merge 禁止** (R-15、`merge-readiness.md`): 3 条件目の人間 approve (または orchestrator 事前承認テキスト) は別途必要、本 Skill は merge を実行しない
- **Coordinator は人間レビュアー向け文言を必ず添える** (R-15、`code-reviewer-aspects.md` §Coordinator のレビューコメント形式): 「AI レビューの指摘で十分でしょうか?」を末尾に配置、人間の最終判断責務を残す
- **PR 番号未指定で本 Skill を起動しない**: `gh pr view <PR#>` で取得できる open / draft / ready PR のみ評価対象、closed / merged PR の事後レビューは推奨しない (fix loop が実行不能)
- **Coordinator コメントは `/tmp/code-reviewer-<PR#>-<timestamp>.md` 経由で post** (PR #146 レトロ Try と整合): `--body "$(cat <<EOF...)"` heredoc 直送は禁止、`--body-file` で渡す
- **redaction 通過のタイミングは Coordinator 集約後 / PR post 前**: サブエージェント return 結果に PII / Secrets が含まれていても redaction 前なので、Coordinator が必ず Step 3 で全文 scan
- **本 Skill 単独で起動可能** (`skill-authoring.md` §単独テスト可能性): implementation-workflow Phase 6 経由が標準だが、人間 / orchestrator が直接「PR #NNN をレビュー」と指示しても動作する
- **harness-meta / harness-evolution との責務分離** (`skill-authoring.md` §Skill 間の責務 overlap 回避): 本 Skill は単一 PR の Evaluator、harness-meta は learning ファイル群を入力とする内部 KPT、harness-evolution は外部研究駆動の改善提案、3 者は独立した責務

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提、サブエージェント並列実行の根拠)
- ADR 0019 (`code-reviewer` Skill の 8 aspect + Coordinator + Merge readiness 判定 SoT)
- ADR 0024 (`gh` CLI 採用、PR コメント post の SoT)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.3 (Skill の責務) / §5.4.3 (Evaluation フェーズ) / R-13 / R-15 / R-37
- `.claude/rules/code-reviewer-aspects.md` (8 aspect binary checklist + Coordinator レビューコメント形式 + aspect 動的選択ルール + A10 完了後 enable 手順の SoT)
- `.claude/rules/merge-readiness.md` (R-15 3 条件 + 大規模 PR aspect スコープ削減 + classifier ブロック対応)
- `.claude/rules/pii.md` / `.claude/rules/secrets.md` (Skill 出力前の redaction 強制、R-26)
- `.claude/rules/skill-authoring.md` (本 Skill が準拠する SKILL.md フォーマット + 100-point rubric)
- `.claude/rules/implementation-workflow.md` (本 Skill を呼び出す Phase 6 の SoT)
- `.claude/rules/{design-tokens,ui-snapshot,ui-inventory,behavior-preservation}.md` (visual-regression / design-tokens aspect の前提、A10 完了で active 化)
- `.claude/skills/implementation-workflow/SKILL.md` (本 Skill を呼び出す Generator 側)
- `.claude/skills/orchestrator/SKILL.md` (本 Skill を per-task pane 経由で呼び出す上位レイヤ)
