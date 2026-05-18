---
name: roadmap-tracker
description: |
  plan.md と docs/epics/EPIC-NNN-*/ を入力に docs/harness/roadmap.md (全体) と
  docs/epics/<id>/roadmap.md (Epic 別) を生成・更新する。進捗・完了根拠・Open Questions・
  障壁・着手順変更履歴を一元管理し、並行実装容易性に基づく「次の推奨着手 (top-N)」を出力する。
  plan.md / Epic 本体への逆同期はしない (片方向ミラー、R-34)。Plan は 1 PR 完結のため対象外。
  epic-author の Epic 起票直後、implementation-workflow Phase 8 (Merge 直後)、
  pr-poller の pending-fetch 再走査、または「ロードマップ更新」「次の推奨着手を出して」
  「障壁記録」「保留事項追加」等の手動指示で起動される。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §5.3 / §6.2 A3 / R-34 / R-35 / R-36
related_rules:
  - .claude/rules/roadmap.md
  - .claude/rules/epic.md
  - .claude/rules/docs-structure.md
  - .claude/rules/template-language.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/markdown.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0024
  - ADR-0025
---

# roadmap-tracker

> **5 行以内 summary**: `docs/harness/plan.md` の B0 / A1-A10 / C1-C10 フェーズ項目と
> `docs/epics/EPIC-NNN-*/` を入力に `docs/harness/roadmap.md` (全体) と
> `docs/epics/<id>/roadmap.md` (Epic 別) を **片方向ミラー** で更新する Skill。
> 自動起動フック 2 系統 (epic-author 起票直後 / implementation-workflow Phase 8) +
> pr-poller pending-fetch 再走査 + 手動起動契機をサポート、plan.md / Epic 本体への
> 逆同期は禁止 (R-34)。詳細手順 SoT は `.claude/rules/roadmap.md`。

## 役割

- **片方向ミラー更新**: `docs/harness/plan.md` (B0 / A1-A10 / C1-C10) と `docs/epics/EPIC-NNN-*/` (frontmatter から ID / タイトル / status / `expected_modules`) を Read で取り込み、`docs/harness/roadmap.md` (全体) + `docs/epics/<id>/roadmap.md` (Epic 別) に追記する。plan.md / Epic 本体への書き戻しは禁止 (R-34)
- **進捗・完了根拠の集約**: マージ済 PR の番号 / マージ日 / 主要ファイル (merge commit hash を主、head commit hash を従) を完了根拠表に記録、status を `proposed → in-progress → completed` に遷移
- **並行実装容易性の判定**: 現在 `in-progress` な項目の `expected_modules` 集合と重複モジュールが少ない順 + Mermaid gantt の `after` 依存解決済の項目を「次の推奨着手 (top-N)」として出力 (full replacement で更新)
- **append-only セクションの蓄積**: Open Questions / 障壁 / 着手順変更履歴は **既存行を絶対に書き換えない**、解決時は別行に解決日と方法を追記
- **後続 Skill との連携**: `implementation-workflow` Phase 8 から呼ばれ完了根拠登録、`epic-author` 起票直後に Epic 別 roadmap.md 雛形生成、`pr-poller` の pending-fetch 再走査で `<!-- evidence:pending-fetch -->` コメント解消

## 入力

- **起動契機 (3 系統 + 手動)**:
  - `epic-author` Epic 起票直後 (自動): `<EPIC-NNN>` 指定 → Epic 別 roadmap.md 雛形 + 全体 roadmap.md の Epic 行追加
  - `implementation-workflow` Phase 8 (Merge 直後、自動): `<PR#>` + `<Phase ID>` (Epic 配下 PR / B-A-C フェーズ項目該当時のみ、Plan 単体は対象外、R-34)
  - `pr-poller` pending-fetch 再走査 (自動): `<!-- evidence:pending-fetch -->` コメント有の行を対象に `gh pr view --json` 再試行
  - 手動指示 (人間 / 他 Skill): 「ロードマップ更新」「進捗可視化」「着手順入れ替え」「障壁記録」「保留事項追加」「次の推奨着手を出して」等
- **走査対象 (Read のみ)**:
  - `docs/harness/plan.md` (§6 のフェーズ項目 B0 / A1-A10 / C1-C10)
  - `docs/epics/EPIC-NNN-*/README.md` (frontmatter `id` / `title` / `status` / `expected_modules` / `related_adrs` / `related_specs`)
  - `docs/epics/EPIC-NNN-*/decisions.md` (着手順変更履歴の入力候補)
  - **`docs/plans/*.md` は走査対象外** (R-34、Plan は 1 PR 完結で merge / close で完結、roadmap 追跡対象外)
- **PR メタ情報**: `gh pr view <PR#> --json state,mergedAt,mergeCommit,headRefName` (PR 番号 / merge commit hash / マージ日 / branch 名)
- **git / gh CLI 認証済環境**: `git log` / `gh pr view` の権限を持つアカウント
- **`/tmp` 書込権限** (該当時): mirror PR 起票時の PR body / commit message を `/tmp/<unique-prefix>-{commit-msg.txt,pr-body.md}` 経由で渡す

## 出力

- **更新ファイル (片方向ミラー、追記のみ)**:
  - `docs/harness/roadmap.md` (全体ロードマップ、frontmatter `id: roadmap-harness` / `status: living` / `source_plan: docs/harness/plan.md`)
  - `docs/epics/<id>/roadmap.md` (Epic 別ロードマップ、frontmatter `id: roadmap-EPIC-NNN` / `status: living` / `source_epic: EPIC-NNN`)
- **更新セクション** (`.claude/rules/roadmap.md` §ロードマップ Markdown の構造 準拠):
  - 項目一覧表 (ID / タイトル / status / `expected_modules` / 完了根拠)
  - 完了根拠表 (ID / PR 番号 / マージ日 / 主要ファイル + merge commit hash 主・head commit hash 従)
  - 着手順とブロック関係 (Mermaid gantt、依存関係の `after` 句)
  - 保留中の意思決定・不明事項 (Open Questions、append-only)
  - 技術的障壁と回避策 (Blockers and Workarounds、append-only)
  - 着手順変更履歴 (append-only)
  - 次の推奨着手 (並行実装観点、top-N、full replacement)
- **mirror PR (該当時)**: `implementation-workflow` を経由しない手動マージや、自動起動フック発火しなかったケースでは `harness/roadmap-mirror-<phase-id>` ブランチで mirror PR 起票 (本体 PR merge から 30 分以内起票 / 60 分以内 merge の SLA を目標、`.claude/rules/roadmap.md` §mirror PR 起票 SLA)
- **pending-fetch 暫定登録 (該当時)**: `gh pr view` 取得失敗時は完了根拠表行に `<!-- evidence:pending-fetch -->` コメントで暫定登録 → `pr-poller` 再走査で解消 (R-35)
- **副作用 (禁止項目)**:
  - `docs/harness/plan.md` への書き戻し禁止 (R-34、片方向ミラー)
  - `docs/epics/EPIC-NNN-*/README.md` 本体への書き戻し禁止 (補助 `roadmap.md` 経由のみ、`epic.md` §補助ファイルの役割)
  - `docs/plans/*.md` の走査・書き換え禁止 (R-34、Plan は対象外)

## フェーズ別動作 (5 系統 + 手動)

### 系統 1: epic-author Epic 起票直後 (自動)

`epic-author` Skill が `docs/epics/EPIC-NNN-<slug>/README.md` 等 5 ファイル生成直後に本 Skill を呼び出す:

1. Epic README frontmatter Read (`id` / `title` / `status` / `expected_modules` / `related_adrs`)
2. **`docs/epics/<id>/roadmap.md` 雛形生成**:
   - frontmatter `id: roadmap-EPIC-NNN` / `title: EPIC-NNN ロードマップ` / `status: living` / `last_updated: <today>` / `source_epic: EPIC-NNN`
   - 5 行以内 summary + 項目一覧表 (Epic の構成 PR を A3-1 / A3-2 / ... のように plchldr 行で展開、構成 PR は `README.md` §構成 PR を参照)
   - 完了根拠表 (空)
   - 着手順とブロック関係 (Mermaid gantt、`decisions.md` の並列グルーピングと整合)
   - Open Questions / Blockers / 着手順変更履歴 (空、append-only)
   - 次の推奨着手 (`decisions.md` Group 1-N の構成に基づく)
3. **`docs/harness/roadmap.md` の項目一覧表に Epic 行追加**:
   - ID = `EPIC-NNN` (フェーズ ID 形式 `EPIC-A2` も許容)
   - status = `proposed` (Epic README frontmatter と同期)
   - `expected_modules` = Epic README frontmatter から転記
   - 完了根拠 = `—` (未完)
4. **着手順変更履歴に「Epic 起票」行追加** (append-only)
5. 双方向リンク確認: `docs/harness/roadmap.md` の Epic 行が `docs/epics/<id>/roadmap.md` 雛形と整合

### 系統 2: implementation-workflow Phase 8 (Merge 直後、自動)

`implementation-workflow` Phase 8 が PR merge 直後に本 Skill を呼び出す (**Epic 配下 PR / B-A-C フェーズ項目該当時のみ**、Plan 単体は対象外、R-34):

1. `gh pr view <PR#> --json state,mergedAt,mergeCommit,headRefName,title` で PR メタ情報取得
   - `state: MERGED` + `mergedAt` non-null 確認
   - 取得失敗時は `<!-- evidence:pending-fetch -->` コメント付きで暫定登録 (R-35)、`pr-poller` が再走査
2. 対象 Phase ID 判定:
   - Epic 配下 PR: branch 名 `feature/EPIC-NNN-*` または `feature/<phase-id>-*` から Epic ID 抽出
   - B-A-C フェーズ項目: branch 名 `harness/<purpose>` + PR description frontmatter `<!-- pr-frontmatter ... -->` の `related_plan` / `related_epic` から判定
   - Plan 単体 (`feature/PLAN-NNN-*` / `fix/PLAN-NNN-*` 等): **対象外、何もしない** (R-34)
3. **当該 Epic の `roadmap.md` 完了根拠表に行追記** (chronological insert、merge 日順):
   - `| <Phase ID> | [#<PR#>](URL) | <mergedAt> | <主要ファイル> (merge commit \`<oid>\`、head commit \`<head>\`) |`
   - merge commit hash は `gh pr view --json mergeCommit` の `mergeCommit.oid` を主、head commit は `gh pr view --json commits` の最後または `git log <branch>` 最終 commit を従で併記 (`.claude/rules/roadmap.md` §完了根拠表の commit 引用基準)
   - squash merge の場合は merge commit hash で OK、head commit は省略可
4. **項目一覧表の status 更新**:
   - 当該 Phase / Epic の status を `in-progress → completed` (Epic 配下 PR が全て merge 済の場合)
   - 部分完了時は `in-progress` 維持、`完了根拠` 列に最新 PR 番号のみ追記
5. **着手順変更履歴に「<Phase ID> マージ完了」行追加** (append-only):
   - `| <today> | <Phase ID> (<タイトル>) マージ完了 (PR #<N>、commit \`<oid>\`) | <根拠> |`
6. **次の推奨着手 (top-N) の再計算** (full replacement):
   - 依存関係 (Mermaid gantt の `after` 句) が解決済の項目を抽出
   - 現在 `in-progress` な項目の `expected_modules` 集合と **重複モジュールが少ない順** にソート
   - 重複ゼロが複数あれば優先度 (must/should/could、`docs/harness/plan.md` §6 の各フェーズ優先度) で二次ソート
   - `expected_modules` 未記入は warning 出力 (`epic.md` §機械検証)
7. mirror PR が必要な場合 (本 Skill が `implementation-workflow` 経由でなく単独起動された場合 / pr-poller pending-fetch 再走査): `harness/roadmap-mirror-<phase-id>` ブランチで mirror PR 起票

### 系統 3: pr-poller pending-fetch 再走査 (自動)

`pr-poller` が定期実行時に `<!-- evidence:pending-fetch -->` コメント有の行を検出して本 Skill を呼び出す:

1. `docs/harness/roadmap.md` + `docs/epics/<id>/roadmap.md` を grep して暫定登録行を抽出
2. 各 PR について `gh pr view <PR#> --json state,mergedAt,mergeCommit` を再試行
3. 取得成功時は本来の完了根拠行に置換、`<!-- evidence:pending-fetch -->` コメントを削除
4. 取得失敗が継続する場合 (例: PR 削除 / API rate limit) は warning 出力、`Blockers and Workarounds` 表に「PR# fetch 不能」を append (append-only)

### 系統 4: 手動起動 (人間 / 他 Skill 指示)

以下の指示語を受けたら起動:

| 指示語 | 動作 |
|---|---|
| 「ロードマップ更新」 | 全体 roadmap.md + 対象 Epic roadmap.md を最新 PR 状態に同期 (系統 2 と同等を全 in-progress 項目について実行) |
| 「進捗可視化」 | 現状 status + Mermaid gantt の表示確認、必要に応じて gantt の `after` 依存を更新 |
| 「着手順入れ替え」 | 着手順変更履歴に append + Mermaid gantt の `after` 依存を更新 + 次の推奨着手 (top-N) を再計算 |
| 「障壁記録」 | Blockers and Workarounds 表に append (起票日 / 障壁 / 回避策、解決時は別行で append) |
| 「保留事項追加」 | Open Questions 表に append (起票日 / 内容 / 暫定方針 / 解決状態、解決時は別行で append) |
| 「次の推奨着手を出して」 | 並行実装容易性ロジック (依存解決 + `expected_modules` 重複ゼロ + 優先度) で top-N を再計算して提示 |

### 系統 5: 手動マージ時の同 PR 更新 (`pr-retrospective` learning PR 連携)

`implementation-workflow` を経由しないマージ (B0 のような手動マージ、急ぎの hotfix 等) では Phase 8 自動起動フックが発火しないため、進捗ロストのリスクが残る:

- **手動マージしたら、その PR の `pr-retrospective` learning PR (`harness/learnings-batch-YYYY-WW` ブランチ) と同じ PR で `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の手動更新も実施する** (1 PR でレトロ起票 + ロードマップ更新を完結させ、二重 PR 化や更新漏れを回避、`.claude/rules/roadmap.md` §手動マージ時の同 PR 更新ルール)
- 手動更新内容: 対象 PR の項目を `in-progress` → `completed` に変更、`完了根拠` 表に PR 番号 + マージ日 + 主要ファイルを追記、`着手順変更履歴` に「手動マージで Phase 8 自動同期発火せず、本 PR で手動更新」と記録
- `implementation-workflow` 経由マージとの判別: PR description type が `harness` / `feature` / `bugfix` / `refactor` / `dependency-upgrade` で `roadmap-tracker` 自動起動済み行に `<!-- roadmap-tracker:auto -->` コメント有 → 自動更新済。コメント無 → 手動更新が必要

## ステータス語彙

`docs/harness/plan.md` §6 のフェーズ ID と Epic README frontmatter の status を以下の語彙で同期:

| 値 | 意味 | 遷移条件 |
|---|---|---|
| `proposed` | 起票済み、未着手 | 着手 PR の Draft 起票時に `in-progress` 化 |
| `in-progress` | 着手中 | 全構成 PR merge 完了で `completed` 化 |
| `completed` | 完了 (完了根拠を記録) | 完了根拠表に PR 番号 + マージ日 + merge commit hash を必ず記録 |
| `blocked` | 障壁により停止中 | Blockers and Workarounds 表に記載、解決日記入で `proposed` または `in-progress` に戻す |
| `abandoned` | 取り下げ | 着手順変更履歴に理由記載、`docs/epics/<id>/decisions.md` にも記録 (Epic 配下時) |

## セクション別の競合解消ポリシー (並走 PR の rebase 時)

`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` のセクションごとに方針が異なる (`.claude/rules/roadmap.md` §セクション別の競合解消ポリシー):

| セクション | 解消ポリシー | 根拠 |
|---|---|---|
| 完了根拠 表 | **chronological insert** (merge 日順) | 新しい PR を時系列に正しい位置に挿入、過去 PR の混入は避ける |
| 着手順変更履歴 | **append-only** (新行を末尾に追加) | 履歴系セクションは既存行を絶対に書き換えない |
| 本文段落 (概要 / 補足) | **append-only** (新段落を最後に追加) | 既存段落は SoT として維持、新情報は最終段落として追記 |
| 次の推奨着手 | **full replacement** (最新状態に書き換え) | 並列実行可能性は時々刻々変動するため、merge 順の競合解消ではなく最終状態に置換 |
| Open Questions / Blockers | append-only (解決時は別行に解決日と方法を追記) | 重要原則と整合、既存行の書き換え禁止 |

判断ロジックは「履歴 / 時系列を保持するか」「最新状態を反映するか」の 2 軸。conflict marker (`<<<<<<<` / `>>>>>>>`) のあるファイルを開いたら、まずセクション種別を確認してから解消。

## mirror PR 起票 SLA

mirror PR (`harness/roadmap-mirror-<phase-id>` ブランチ) は本体 PR merge 直後の起票 → review → merge を目指す (`.claude/rules/roadmap.md` §mirror PR 起票 SLA):

- **目標 SLA**: 本体 PR merge から **30 分以内** に mirror PR 起票、**60 分以内** に merge 完了
- **理由**: mirror PR が長く open のままだと他並走 PR (他 mirror PR / 本体 PR) の merge により再 rebase が必要になる (PR #123 で実証、2 回 rebase 発生)
- **再 rebase 回数最小化**: 上記 SLA を守ることで mirror PR の rebase 回数を 0-1 回に抑制可能
- **例外**: 並走 PR が一斉に merge する流れの中では SLA を守れない場合あり (PR #123 mirror PR #127 は A2-5 / A2-2 mirror が先行 merge で 2 回 rebase)、その場合は本 §セクション別の競合解消ポリシー に従って整合解消

## merge note 段落テンプレ

mirror PR で `docs/harness/roadmap.md` 完了根拠表直下に追加する「merge note 段落」のテンプレ (`.claude/rules/roadmap.md` §merge note 段落テンプレ):

```markdown
> 注: <Phase ID> (<Phase 名>) は orchestrator (subroh0508) 委任で R-15 代替し
> `gh pr merge --<squash|merge>` を実行 (commit `<merge-commit-sha>`)。`<theme>` Skill の
> 手動代替運用 (A3 / A4 Skill 本格化前) の暫定パターン。
```

- 「admin override」「self-merge」「force-merge」等のメタ言及語は使わず、`commit-message.md` §メタ言及語の classifier トリガー回避 のニュートラル表現を採用 (PR #126 で classifier denied 経験あり)
- merge commit hash は `gh pr view <PR#> --json mergeCommit` の `mergeCommit.oid` を引用

## 完了根拠表の commit 引用基準

完了根拠表の `主要ファイル` 列または隣接の補助情報には **merge commit hash を主、head commit hash を従** で併記する (`.claude/rules/roadmap.md` §完了根拠表の commit 引用基準):

| 項目 | 取得方法 | 役割 |
|---|---|---|
| merge commit hash (主) | `gh pr view <PR#> --json mergeCommit` の `mergeCommit.oid` | master 反映後の SoT、後追い `git log master` で辿れる |
| head commit hash (従、必要時) | `git log <branch>` 最終 commit、または `gh pr view --json commits` の最後の commit | rebase / fix loop で hash が変動するため参考扱い |

引用形式の例:

```markdown
| A2-6 | [#129](URL) | 2026-05-17 | `.claude/settings.json` (merge commit `1ac6fe4`、head commit `b961a22`) |
```

- 主のみ記録する場合は merge commit hash で OK、head commit は省略可
- squash merge の場合 head commit は新規生成された squashed commit を指すため、`gh pr view` の `mergeCommit.oid` が SoT

## 並行実装容易性ロジック (次の推奨着手 top-N)

依存解決済 + `expected_modules` 重複ゼロ + 優先度の 3 軸で top-N を再計算:

1. **依存解決済の候補抽出**: Mermaid gantt の `after <id>` 句で参照される全先行項目が `completed` か確認
2. **`expected_modules` 重複モジュール数を計算**: 各候補について、現在 `in-progress` な全項目の `expected_modules` 集合との重複モジュール数を計算 (glob マッチで判定)
3. **昇順ソート**: 重複モジュール数が少ない順に並べ替え、重複ゼロが複数あれば優先度 (must/should/could) で二次ソート
4. **`expected_modules` 未記入候補は warning**: 「並行可否不明 (要記入)」として top-N に含めずに別行で警告出力
5. **top-N の N は文脈依存** (通常 3-5、orchestrator 経由時は per-task pane spawn 可能数に応じて調整)

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク** (Kotlin、`org.commonmark:commonmark` + `commonmark-ext-yaml-front-matter` + `org.yaml:snakeyaml` 2.x) で以下を検証 (`docs/harness/plan.md` §5.2):
  - `docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` の項目 ID (`B0` / `A1` / `EPIC-NNN`) が plan.md / `docs/epics/` に実在する
  - frontmatter `id: roadmap-harness` / `roadmap-EPIC-NNN` / `status: living` / `source_plan` / `source_epic` が必須キー充足
  - 完了根拠表の merge commit hash が `git log master` で実在する commit を指す
  - `expected_modules` 未記入の Epic は warning (`roadmap-tracker` の並行可否判定が機能しない)
- Konsist は Kotlin file 専用のため Markdown 検証には使えない (§5.2)

## Gotchas

- **plan.md / Epic 本体への逆同期はしない** (R-34、片方向ミラー): `roadmap-tracker` は Read のみで取り込み、進捗・完了根拠・障壁の記録は roadmap.md 側にのみ追記する。`docs/harness/plan.md` / `docs/epics/EPIC-NNN-*/README.md` を本 Skill から書き換えてはいけない
- **`docs/plans/*.md` は走査対象外** (R-34): Plan は 1 PR で完結するためロードマップ追跡対象外 (PR レビュー & merge で完結)、`gh pr view` 結果から Plan 単体 PR と判別したら何もせずに return
- **`gh pr view` 取得失敗時は `<!-- evidence:pending-fetch -->` コメントで暫定登録** (R-35): `pr-poller` が定期的に再走査して解消、暫定登録なしで roadmap 行を空欄にしない (進捗ロスト防止)
- **Open Questions / 障壁 / 着手順変更履歴は append-only**: 既存項目の削除は禁止 (解決時は別行に解決日と方法を追記)、`<<<<<<<` / `>>>>>>>` conflict marker のある rebase 時もこのポリシーを優先 (§セクション別の競合解消ポリシー)
- **次の推奨着手は full replacement**: 並列実行可能性は時々刻々変動するため、merge 順の競合解消ではなく最終状態に置換 (履歴を残さない)、過去の推奨着手は着手順変更履歴に反映されているため roadmap 本文で履歴管理不要
- **mirror PR 起票 SLA 30 分 / 60 分を守る**: 守れないと他並走 mirror PR の merge による再 rebase が発生 (PR #123 / #127 で 2 回 rebase 実証)、SLA 超過時は §セクション別の競合解消ポリシー で整合解消
- **merge note 段落のメタ言及語禁止**: 「admin override」「self-merge」「force-merge」等は classifier denied リスク (PR #126 経験)、`commit-message.md` §メタ言及語の classifier トリガー回避 のニュートラル表現 (「orchestrator 委任で R-15 代替し `gh pr merge` を実行」) を採用
- **完了根拠表は merge commit hash 主・head commit hash 従で併記** (PR #129 レトロ Try): merge commit が SoT、head commit は rebase / fix loop で変動するため参考扱い。squash merge の場合 head commit は新規生成された squashed commit を指すため `mergeCommit.oid` が SoT
- **重大な運用方針変更時は `§4.5` ADR 起票基準を再評価して格上げ検討** (R-36): 本 Skill は補助 Skill / 撤回コスト低のため現状 ADR 化見送り、運用方針の本質的変更 (片方向ミラー → 双方向同期等) が発生したら ADR 起票を検討
- **mirror PR の type は `harness`**: `harness/roadmap-mirror-<phase-id>` ブランチで `.github/PULL_REQUEST_TEMPLATE/harness.md` を `/tmp/<unique-prefix>-pr-body.md` にコピー → カスタマイズ → `--body-file` で起票 (`pr-template.md` §gh pr create 必須パラメータ、`--template` と排他)
- **PII / Secrets を完了根拠表 / 着手順変更履歴に混入させない**: PR description / commit message に display name / メール / sub claim が含まれていないか出力前にチェック (`.claude/rules/pii.md` / `secrets.md` redaction 表参照)、Skill 出力前のチェックリスト 4 項目を必ず実施
- **Epic 配下 PR / B-A-C フェーズ項目該当時のみ起動** (R-34): `implementation-workflow` Phase 8 から呼ばれた際、Plan 単体 PR (`feature/PLAN-NNN-*` / `fix/PLAN-NNN-*` 等) は対象外、何もせずに return

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Generator/Evaluator 分離の前提)
- ADR 0018 (`implementation-workflow` 10 フェーズ設計、Phase 8 から本 Skill が呼ばれる)
- ADR 0024 (`gh` CLI 採用、PR メタ情報取得の SoT)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由)
- `docs/harness/plan.md` §5.3 (Skill の責務) / §6.2 A3 / R-34 / R-35 / R-36
- `.claude/rules/roadmap.md` (本 Skill の詳細手順 SoT、ロードマップ Markdown 規約)
- `.claude/rules/epic.md` (Epic ディレクトリ構成 / 状態遷移、本 Skill が roadmap.md を更新する Epic 別 docs の規約)
- `.claude/rules/{docs-structure,template-language,skill-authoring,markdown}.md`
- `.claude/skills/{epic-author,implementation-workflow,pr-poller,pr-retrospective}/SKILL.md` (本 Skill が起動 / 連携される Skill 群)
- `docs/harness/roadmap.md` (本 Skill が更新する全体ロードマップ)
- `docs/epics/EPIC-NNN-*/roadmap.md` (本 Skill が更新する Epic 別ロードマップ)
- `.github/PULL_REQUEST_TEMPLATE/harness.md` (mirror PR 起票テンプレ)
