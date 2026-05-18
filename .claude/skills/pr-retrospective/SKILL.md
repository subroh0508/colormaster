---
name: pr-retrospective
description: |
  対象 PR の diff / comments / reviews / CI ログ / Skill 実行ログ / 三層指標差分 / 関連 Plan・Epic
  を収集し、docs/harness/learnings/YYYY-MM-DD-pr-<n>.md を日本語の構造化フォーマットで生成する。
  harness/learnings-batch-YYYY-WW ブランチに集約し、週次 (or 件数到達時) に PR として起票する。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §4.4 / §5.3 / §5.4.5 / R-12
related_rules:
  - .claude/rules/retrospective-format.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/template-language.md
  - .claude/rules/docs-structure.md
  - .claude/rules/pr-poller.md
  - .claude/rules/harness-meta-criteria.md
  - .claude/rules/branch-naming.md
  - .claude/rules/pr-template.md
  - .claude/rules/merge-readiness.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0026
  - ADR-0027
---

# pr-retrospective

> **5 行以内 summary**: マージ済 PR 1 件を入力に diff / comments / reviews / CI ログ / Skill 実行
> ログ / 三層指標差分 / 関連 Plan・Epic を収集し、`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md`
> を `retrospective-format.md` の日本語構造化フォーマットで生成する Retrospection Skill。
> 生成した learning は `harness/learnings-batch-YYYY-WW` ブランチに集約 push し、件数閾値到達
> または週次で PR 起票判定する。PR コメント post は禁止、SoT は learning ファイル (§4.4)。

## 役割

- **1 PR 単位の retrospective**: 1 PR = 1 learning ファイル (`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md`) を `retrospective-format.md` §本文構造 の日本語見出しで生成
- **入力ソース横断収集**: diff / commits / reviews / comments / CI ログ / Skill 実行ログ / 三層指標 (Kover / Konsist / PITest、A7 以降) / 関連 Plan・Epic・ADR を `gh` CLI と JetBrains MCP / Read で収集
- **KPT 分析**: ✅ Keep / ⚠️ Problem / 🚀 Try を各最低 3 / 推奨 5-10 項目で言語化、可能なら consensus 表記 (どの aspect 指摘か) を併記
- **「🤖 ハーネス改善提案」セクション生成**: `[rule]` / `[skill]` / `[template]` / `[remove]` プレフィックス + チェックボックス `[ ]` で起票し、後段の `harness-meta` が parse する正規構造を満たす
- **「📝 harness-meta フィードバック」placeholder**: 空でも見出し + 3 表 (採用 / 見送り / 保留) を残し、`harness-meta` が後追記する場所を確保 (R-12 learning ロスト対策)
- **redaction 強制**: 出力前に `.claude/rules/pii.md` / `.claude/rules/secrets.md` の正規表現で PII / Secrets を `[REDACTED-*]` に置換
- **batch ブランチ集約 + PR 起票判定**: `harness/learnings-batch-YYYY-WW` (ISO 週番号) に commit + push、件数閾値到達 (デフォルト 10 件) または週次 (ISO 週終端、日曜深夜) で PR 起票

`pr-retrospective` は 1 PR 単位、`harness-meta` は複数 PR 集約と責務を分離する (`.claude/rules/skill-authoring.md` Gotchas §Skill 間の責務 overlap 回避)。merge 自体は人間 approve 後に orchestrator pane が代行 (R-15)、本 Skill は merge を行わない。

## 入力

- **起動 prompt** (`pr-poller` / 人間 / orchestrator から渡される):
  - **PR 番号** (`#NNN`、明示時): 単一 PR の learning 生成
  - **auto-detect モード** (PR 番号未指定時): `gh pr list --state merged --search "merged:>=<last-run-timestamp>" --json number,mergedAt,labels,headRefName,baseRefName,title,url` で未処理 PR を列挙
  - **batch ブランチ名** (任意): `harness/learnings-batch-YYYY-WW` を明示指定、未指定なら現在の ISO 週番号で自動算出
  - **dry-run フラグ** (任意): commit / push / PR 起票を skip し、生成 learning ファイル内容と起票判定のみを表示
- **対象 PR の生データ** (`gh` CLI で取得、`.claude/rules/mcp-usage.md` §gh CLI 優位):
  - `gh pr view <N> --json number,title,body,state,author,mergedAt,mergeCommit,baseRefName,headRefName,labels,additions,deletions,changedFiles,commits,url`
  - `gh pr diff <N>` (差分本文)
  - `gh pr view <N> --comments` (comments / reviews / review comments)
  - `gh run list --branch <head> --json conclusion,name,url,databaseId,headSha` + `gh run view <id> --log-failed` (CI ログ、失敗時のみ抜粋)
- **Skill 実行ログ** (該当時): `.claude/locks/*.lock` / `docs/harness/dry-runs/*.md` / 関連 Skill が PR description / commit body に残したログ抜粋
- **三層指標** (A7 完了後): Kover / Konsist `@Spec` / PITest の出力ファイル (現時点は `N/A` 必須、空欄禁止、`retrospective-format.md` §📊 指標)
- **関連 Plan / Epic / ADR**: PR body / commit message / branch 名から `PLAN-NNN` / `EPIC-NNN` / `ADR-NNNN` を抽出して該当ファイルを Read
- **既存 learning との重複チェック**: `docs/harness/learnings/INDEX.md` を Read して同 PR の learning が既存なら skip (idempotent)
- **`.claude/locks/pr-retrospective.lock`** (A4 で本格化): 排他制御 placeholder、本 PR 時点では `pr-poller` 側の lock に乗る

## 出力

- **`docs/harness/learnings/YYYY-MM-DD-pr-<n>.md`** (1 PR = 1 ファイル):
  - frontmatter は `retrospective-format.md` §frontmatter 必須キー準拠 (block 形式、`id` / `title` / `type: learning` / `status: draft` / `related_pr` / `related_plan` / `related_epic` / `generated_at` / `generator`)
  - 冒頭 5 行 summary を `> **5 行以内 summary**: ...` で必置
  - 本文構造: `## ✅ Keep` / `## ⚠️ Problem` / `## 🚀 Try` / `## 📊 指標` / `## 🤖 ハーネス改善提案` / `## 📝 harness-meta フィードバック` (空でも見出し残す)
  - 対象 PR の URL / commit sha / マージ日時 / 差分ファイル数 + 行数を冒頭 blockquote で必須記載 (`retrospective-format.md` §本文構造 例)
- **`docs/harness/learnings/INDEX.md`** 追記: 索引行を 1 行追加 (PR 番号 / タイトル / 生成日 / 関連 Plan / Epic)
- **`harness/learnings-batch-YYYY-WW`** ブランチへの commit + push:
  - 1 learning = 1 commit (`docs(harness): PR #NNN レトロ起票`)
  - branch 不在なら `git switch -c harness/learnings-batch-YYYY-WW` で新規作成 (`.claude/rules/branch-naming.md` §harness/<purpose>)
  - 既存 branch なら fetch + rebase してから push (競合検出時は Gotchas §batch ブランチ更新競合 参照)
- **batch PR 起票** (件数閾値 / 週次到達時のみ):
  - `gh pr create --base master --template harness.md --title "docs(harness): learnings batch YYYY-WW (N 件)" --body-file /tmp/pr-body-retro-batch-YYYY-WW-<timestamp>.md` で Draft 起票 → Ready 化は `gh pr ready <PR#>`
  - PR body は対象 learning 一覧 + ハイライト Try / Problem を集約
- **副作用なし**: PR コメント post は禁止 (`retrospective-format.md` §PR コメント post の禁止)、`gh pr merge` は実行禁止 (R-15、merge は orchestrator 代行)
- **dry-run モード時**: ファイル書き出しと commit / push / PR 起票を skip し、生成内容と起票判定 (件数 / 週次到達有無) のみを stdout に出す

## フェーズ別動作

### Phase 1: PR メタ情報収集 + 既存 learning 重複チェック

- 起動 prompt の PR 番号 (auto-detect モードなら `gh pr list --state merged --search "merged:>=<last>"`) を確定
- `gh pr view <N> --json ...` で PR メタ (title / body / mergedAt / mergeCommit / baseRefName / headRefName / labels / additions / deletions / changedFiles / commits / url) を取得
- PR body / commit message / branch 名から関連 `PLAN-NNN` / `EPIC-NNN` / `ADR-NNNN` ID を抽出 (regex `(PLAN|EPIC)-\d{3}` / `ADR-\d{4}`)
- `docs/harness/learnings/INDEX.md` を Read して同 PR の learning ファイル既存有無を確認 (`related_pr: NNN` の行があるか grep)
- 既存ありなら skip して終了 (idempotent、本 Skill 二重起動防止)、なければ Phase 2 へ
- マージ日 (UTC) を `YYYY-MM-DD` 形式に変換、ファイル名 `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を確定

### Phase 2: diff / comments / reviews 収集

- `gh pr diff <N>` で差分本文を取得 (`additions` / `deletions` / `changedFiles` 合計を blockquote 用に保持)
- `gh pr view <N> --comments` で comments / reviews / review comments を JSON 取得
- review (state: APPROVED / CHANGES_REQUESTED / COMMENTED) と inline review comments を分離して整形
- `code-reviewer` Skill が post した Coordinator コメントがあれば 8 aspect (該当時は 4 aspect) の Critical / High 件数 + binary checklist 通過率を抽出
- consensus 表記用に「どの aspect / どの reviewer が指摘したか」を構造化リストに保持 (例: `{aspect: spec-conformance, severity: Critical, location: file:line, note: ...}`)
- PII / Secrets を含む review コメントは Phase 6 redaction 対象としてマーキング

### Phase 3: CI ログ + Skill 実行ログ + 三層指標差分収集

- `gh run list --branch <headRefName> --json conclusion,name,url,databaseId,headSha,workflowName` で対象 PR の CI run を取得
- 失敗 run のみ `gh run view <id> --log-failed` でログ抜粋 (成功 run はメタ情報のみ)、失敗箇所を 10-30 行抜粋
- Skill 実行ログの収集元:
  - `.claude/locks/*.lock` (`pr-poller` / `harness-meta` 等の起動履歴、A4 以降)
  - `docs/harness/dry-runs/*.md` (該当 PR で dry-run 実施有)
  - PR description / commit body の「## Skill 実行ログ」セクション (該当時)
- 三層指標 (A7 完了後):
  - Kover (Line / Branch coverage): 該当 CI artifact から取得、A7 完了前は `N/A` 明示
  - Konsist `@Spec` (Spec coverage): 同上
  - PITest (Mutation score): 同上
  - Before / After / Δ / 備考 の 4 列で `retrospective-format.md` §📊 指標 表に整形
- Lint 違反数 (A6 完了後、Spotless / ktlint / detekt / markdownlint-cli2 合算): A6 完了前は `N/A`
- PR 固有指標 (ファイル数 / 行数 / Critical findings 数 / binary checklist 通過率) を任意で追加

### Phase 4: KPT 分析

- Phase 2 / Phase 3 の収集結果を以下の 3 セクションに分類:
  - **✅ Keep**: 継続したい運用 / 構造 / 判断 (成功要因の言語化、例「Phase 0 で `git fetch origin master` を実行することで stale base 問題を回避できた」)
  - **⚠️ Problem**: 詰まった点 / 制約 / latent contradiction (例「dry-run skip 漏れで AI 出力品質劣化を本番反映してから気付いた」「frontmatter 配列の flow 形式違反を 3 件残した」)
  - **🚀 Try**: 次回 PR / フェーズで実施したい改善 (Plan / Epic / ADR / rule 単位まで具体化、例「Phase 3 dry-run チェックリストを SKILL.md 本文に追加して skip 漏れ防止」)
- 各セクション最低 3 項目、推奨 5-10 項目 (`retrospective-format.md` §各セクションの規約)
- consensus 表記推奨: 「どの aspect が指摘したか」を併記 (例: `consensus: spec-conformance + architecture`、Phase 2 で抽出した構造化リストを使用)
- 関連 Plan / Epic / ADR の Open Questions / decisions.md と矛盾しないかクロスチェック

### Phase 5: 「🤖 ハーネス改善提案」セクション生成

- KPT の 🚀 Try と ⚠️ Problem から **ハーネス側 (`.claude/rules/` / `.claude/skills/` / templates) で改善可能な項目** を抽出し、4 プレフィックスで起票:
  - **`[rule]`**: `.claude/rules/*.md` の新規追加 / 改修 (例「`[rule]` `pii.md` の redaction 表に GIS avatar URL パターンを追加」)
  - **`[skill]`**: `.claude/skills/*/SKILL.md` の新規追加 / 改修 (例「`[skill]` `code-reviewer` Phase 4 fix loop 上限を 3 → 2 に絞る dry-run」)
  - **`[template]`**: テンプレート Markdown 改修 (例「`[template]` `docs/plans/template.md` の §Open Questions セクションに保留理由列を追加」)
  - **`[remove]`**: 未使用 rule / dormant Skill の撤去候補 (例「`[remove]` `.claude/rules/foo.md` は 3 ヶ月未参照 + dangling 参照ゼロ」)
- 各項目はチェックボックス `[ ]` で起票 (`harness-meta` 採用時に `[x]` + 採用先 PR リンク追記、`retrospective-format.md` §🤖 ハーネス改善提案)
- 採用判定基準該当箇所 (1〜5、`harness-meta-criteria.md`) を併記推奨 (例: `[rule] ... (採用判定基準 1: 複数 PR で反復)`)
- 1 PR あたり提案数の目安は 3-10 件、12 件超過は粒度が細かすぎる兆候 (consolidate を検討)
- `[mcp]` プレフィックス (MCP server 追加 / 設定変更) は harness-evolution 側で扱う領域だが、PR 内で発生したら本セクションに記載しておく (`harness-meta-criteria.md` §`[mcp]` プレフィックス受信ルール で振り分け)

### Phase 6: redaction + learning ファイル書き出し

- Phase 1-5 で組み立てた本文 + frontmatter を 1 文字列に集約し、出力前に redaction を実施:
  - **PII** (`.claude/rules/pii.md` §redaction 強制 表): メールアドレス (`@example.com` 以外) / GIS avatar URL / sub claim (21 桁数字) / IPv4 / IPv6 を `[REDACTED-EMAIL]` / `[REDACTED-AVATAR-URL]` / `[REDACTED-UID]` / `[REDACTED-IP]` に置換
  - **Secrets** (`.claude/rules/secrets.md` §redaction 強制 表): API key / token / password (regex `(?i)(api[-_]?key|token|secret|password)\s*[:=]\s*["']?\S+`) / AWS key (`AKIA[0-9A-Z]{16}`) / GitHub PAT (`ghp_[0-9A-Za-z]{36}`) / Bearer JWT (`eyJ...`) を `[REDACTED-SECRET]` / `[REDACTED-AWS-KEY]` / `[REDACTED-GH-PAT]` / `[REDACTED-JWT]` に置換
  - **display name / Reviewed by**: 「Reviewed by X」「Authored by Y」等の自然文に display name が混入していないか目視 + regex
  - **スタックトレース**: `sub` claim 値 / 環境変数値 / アクセストークンが混入していないか確認
- redaction チェックポイント (`retrospective-format.md` §redaction チェックポイント) を逐次評価:
  - `[ ]` frontmatter 配列が block 形式
  - `[ ]` 5 行 summary が冒頭 blockquote (`>`) に存在
  - `[ ]` 「📝 harness-meta フィードバック」セクションは空でも見出しを残す
- `docs/harness/learnings/YYYY-MM-DD-pr-<n>.md` を Write で生成
- `docs/harness/learnings/INDEX.md` に索引行を追記 (Edit)

### Phase 7: batch ブランチ集約 + PR 起票判定

- batch ブランチ名を ISO 週番号で算出 (`date +%G-W%V`、例: `2026-W21`) → `harness/learnings-batch-YYYY-WW`
- ブランチ不在なら `git fetch origin && git switch -c harness/learnings-batch-YYYY-WW origin/master`、既存なら `git switch harness/learnings-batch-YYYY-WW && git pull --rebase origin <branch>` で同期
- learning ファイル + INDEX.md 更新を 1 commit (`docs(harness): PR #NNN レトロ起票` で commit、`.claude/rules/commit-message.md` 準拠) → `git push origin harness/learnings-batch-YYYY-WW`
- **PR 起票判定** (2 条件のいずれかで起票):
  - **件数閾値**: branch 内未マージ learning が 10 件以上 (`harness-meta-criteria.md` §pr-poller 起動閾値と連動)
  - **週次到達**: ISO 週終端 (日曜深夜 UTC) または前回 PR 起票から 7 日以上経過
- 起票時:
  - PR body を `/tmp/pr-body-retro-batch-YYYY-WW-<timestamp>.md` に書き出し (heredoc 直送禁止、`.claude/rules/orchestrator-criteria.md` §プロンプト送信プロトコル と整合)
  - body には対象 learning 一覧 (相対パスリンク) + ハイライト Try / Problem 5-10 件 + 件数 / 週次到達理由を明記
  - `gh pr create --base master --draft --template harness.md --title "docs(harness): learnings batch YYYY-WW (N 件)" --body-file <path>` で Draft 起票
  - `code-reviewer` Skill を 2 aspect (spec-conformance / architecture、harness 系のため test-quality / performance / security / visual-regression / design-tokens skip 妥当) で並列起動、Critical 0 / High 0 まで fix loop (上限 3)
  - `gh pr ready <PR#>` で Ready 化、merge は人間 approve 後に orchestrator pane が代行 (R-15、本 Skill は `gh pr merge` 禁止)
- **dry-run モード時**: commit / push / PR 起票を skip し、件数 / 週次到達判定と PR body 試作のみを stdout 出力

## Gotchas

- **PR コメントは出さない**: `retrospective-format.md` §PR コメント post の禁止 / §4.4 で明示禁止。Single Source of Truth は learning ファイル。`code-reviewer` の Coordinator コメントは別運用 (merge **前**)、retrospective は merge **後** に生成
- **PII / Secrets redaction の漏れ防止**: Phase 2 (review コメント) / Phase 3 (CI ログ抜粋) / Phase 6 (出力前) の 3 段階で redaction check を通す。`.claude/rules/pii.md` / `secrets.md` の正規表現を全て適用し、検出時は `[REDACTED-*]` プレースホルダに置換。1 段階だけでは GIS avatar URL や sub claim の漏れリスクが残る
- **batch ブランチ更新競合**: 並列 retrospective Skill 実行で同 `harness/learnings-batch-YYYY-WW` を更新すると push 競合が起こる。Phase 7 で `git pull --rebase origin <branch>` を必ず通し、競合検出時は (a) 別 learning なら rebase 続行、(b) 同 learning なら idempotent skip。`.claude/locks/pr-retrospective.lock` 排他制御は A4 で本格化
- **harness-meta との重複防止 (R-31)**: 本 Skill は 1 PR 単位 / `harness-meta` は複数 PR 集約と責務分離。本 Skill は「🤖 ハーネス改善提案」を起票するのみで採用 / 見送り判定は行わない。`[x]` 化と feedback 追記は `harness-meta` の責務。本 Skill が直接 rule / Skill / template を改修する PR を起票してはいけない
- **「📝 harness-meta フィードバック」は空でも見出しを残す**: harness-meta が後追記する placeholder のため、見出しと 3 つの空 H3 (`採用` / `見送り` / `保留`) を必ず残す (`retrospective-format.md` §フィードバックフォーマット)
- **三層指標 N/A 明記**: A7 完了前は Line / Branch / Spec / Mutation を `N/A` 明示 (空欄禁止)。A6 完了前は Lint 違反数も `N/A`。空欄にすると機械検証 (A6 で Gradle カスタムタスク) で reject
- **冒頭 blockquote の必須項目**: 対象 PR の URL / commit sha (mergeCommit) / マージ日時 (UTC または JST、`generated_at` で明示) / 差分ファイル数 + 行数 / commits 数 (初回 + fix loop) を必ず記載 (`retrospective-format.md` §本文構造 例)。欠落は A1 レトロ Problem 級
- **frontmatter `generator` 値**: A3 本格化前 (手動代替実行) は `pr-retrospective (skeleton、本ペインで手動代替実行)`、A3 本格化後 (本 Skill 駆動) は `pr-retrospective Skill (vX.Y.Z)` で明示 (`retrospective-format.md` §frontmatter)
- **idempotent 性**: 同 PR の learning が既存なら Phase 1 で skip。再生成したい場合は明示フラグで誤上書き防止 (本 PR 時点では未実装、A4 で `--force` フラグ追加予定)
- **件数閾値到達 + 週次到達の同時発火**: 両条件成立時は単一 PR で起票 (二重起票しない)、PR body に「件数 X 件 + 週次到達」を併記
- **classifier 迂回時の中立表現**: PR body / commit message に「auto-merge」「self-merge」「force-merge」「admin override」「Safety-Check 回避」等を含めない (`.claude/rules/harness-meta-criteria.md` §classifier ブロック対応 迂回パターン辞典)。中立表現「orchestrator 委任で R-15 代替」「out-of-band approval」等に書き換える
- **起動経路 (3 系統対応 placeholder)**: `pr-poller` 自動起動 / 手動「PR #N のレトロ起票」/ orchestrator pane 委譲。本 PR 時点では `pr-poller` が skeleton (A4 で本格化) のため、当面は手動起動が主、`pr-poller` 完了後に自動経路が enable される
- **本格化のフェーズ依存**: 本 Skill の自動化は A3 (本 PR) で SoT 化、`pr-poller` 統合 + `.claude/locks/pr-retrospective.lock` 排他制御 + 三層指標自動取得は A4 / A6 / A7 で順次本格化。本 PR 時点では「手動起動 + 三層指標 N/A + lock placeholder」前提

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、GitHub Actions から Claude API 呼ばない原則)
- ADR 0018 (Skill 駆動 KPT ループ、6 段階フローの Retrospection 担当)
- ADR 0026 (harness-evolution Skill 採用、harness-meta との二系統補完、retrospective format との連携)
- ADR 0027 (docs 構造 + 命名規約 + 5 行 summary + 日本語化方針)
- `docs/harness/plan.md` §4.4 (learning ファイル SoT) / §5.3 (Skill 責務一覧) / §5.4.5 (ハーネス改善ループ 6 フェーズ Retrospection) / R-12 (learning ロスト対策)
- `.claude/rules/retrospective-format.md` (learning ファイル正規構造、4 プレフィックス、フィードバック追記フォーマット、redaction チェックポイント)
- `.claude/rules/pii.md` (PII redaction 強制、Skill 出力前チェックリスト)
- `.claude/rules/secrets.md` (Secrets redaction 強制、`*.pem` / `*.key` 等の禁止)
- `.claude/rules/skill-authoring.md` (`example-skills:skill-creator` 経由規約、100-point rubric)
- `.claude/rules/template-language.md` (日本語化方針、固定セクション名例外)
- `.claude/rules/docs-structure.md` (frontmatter block 形式 / 5 行 summary / 命名規約)
- `.claude/rules/pr-poller.md` (起動経路 + 閾値連携、3 系統対応)
- `.claude/rules/harness-meta-criteria.md` (採用 / 見送り / 撤去判定基準、`[mcp]` プレフィックス受信ルール、pr-poller 起動閾値)
- `.claude/rules/branch-naming.md` (`harness/<purpose>` / `harness/learnings-batch-YYYY-WW`)
- `.claude/rules/pr-template.md` (`harness.md` テンプレ運用)
- `.claude/rules/merge-readiness.md` (R-15 3 条件、merge は orchestrator 代行)
- `.claude/skills/pr-poller/SKILL.md` (起動経路 + 閾値連携)
- `.claude/skills/harness-meta/SKILL.md` (本 Skill が入力を渡す対向、R-12 共有、📝 フィードバック追記の主体)
- `.claude/skills/code-reviewer/SKILL.md` (Coordinator コメントを Phase 2 で参照、本 Skill が batch PR 起票時にも呼び出す)
- `.claude/skills/orchestrator/SKILL.md` (R-15 代行 merge、手動代替実行時の起動元)
- `.claude/skills/implementation-workflow/SKILL.md` (Phase 8 で本 Skill を起動するバトンタッチ元)
- `docs/harness/learnings/{INDEX,flaky-tests}.md` (補助ファイル)
