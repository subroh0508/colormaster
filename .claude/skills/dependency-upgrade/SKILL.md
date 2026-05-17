---
name: dependency-upgrade
description: |
  pr-poller がローカルで検出した Renovate labeled open PR の number を入力に、依存変更内容 /
  上流 changelog / 破壊的変更 / 影響範囲を解析し、解析結果サマリを PR に gh pr comment で
  post + 必要時に plan-author (単一 PR 完結) / epic-author (major version bump で multi-file
  refactor が必要) を呼んで Plan / Epic 起票で Spec Gen を引き継ぐ Skill。自動 merge / approve
  ラベル付与は行わず (R-15 人間 approve 必須)、approve 推奨度判定までを担当する。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §5.3 / §6.2 A3 / §6.2 A4
related_rules:
  - .claude/rules/pr-poller.md
  - .claude/rules/plan.md
  - .claude/rules/epic.md
  - .claude/rules/network-client.md
  - .claude/rules/gradle.md
  - .claude/rules/no-firebase.md
  - .claude/rules/mcp-usage.md
  - .claude/rules/merge-readiness.md
  - .claude/rules/pr-template.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0024
  - ADR-0025
  - ADR-0027
---

# dependency-upgrade

> **5 行以内 summary**: pr-poller がローカル Claude Code 内で検出した Renovate labeled open PR
> の number を入力に、依存変更内容 / 上流 changelog / 破壊的変更 / 影響範囲を解析し、解析サマリを
> `gh pr comment` で post + 単一 PR 完結なら plan-author、major version bump で multi-file
> refactor が必要なら epic-author を呼んで Spec Gen を引き継ぐ Skill。**自動 merge / approve
> ラベル付与は行わない** (R-15 / `.claude/rules/merge-readiness.md`)、approve 推奨度判定までを担当する。

## 役割

- **pr-poller 起動後の処理担当**: Renovate ラベル付き open PR の number を入力に、依存変更解析 → 解析コメント post → Plan / Epic 起票 までを 1 Skill で完結する
- **changelog 解析**: 上流公式 changelog / GitHub Releases / Renovate PR description / Context7 MCP のいずれかから semver 種別 (patch / minor / major) + 主要変更点 + 破壊的変更 / 重大バグ修正 / セキュリティ修正の有無を抽出
- **影響範囲分析**: 変更されたライブラリ coordinate に依存する touch ファイル一覧 (Kotlin import 経由 / `build.gradle.kts` / `gradle/libs.versions.toml` / `*.sq` driver) と関連テストを JetBrains MCP の IDE indexing で抽出
- **Plan / Epic 起票判定**: 単一 PR 完結なら `plan-author`、major version bump で multi-file refactor が必要 / 仕様補強が複数 SPEC に波及 なら `epic-author` を呼ぶ
- **approve 推奨度判定 (実 merge / ラベル付与はしない)**: 解析結果から `approve 推奨` / `要 review` / `要 downgrade / Plan 起票` の 3 段階で判定し、PR コメントに明示。**実 merge / `approve` ラベル付与は行わず**、人間 approve に委ねる (R-15)
- **GitHub Actions では呼ばれない**: 本 Skill 起動経路はローカル Claude Code 内の `pr-poller` のみ (ADR 0017)、CI 上での Claude API 呼び出しは禁止

implementation-workflow / code-reviewer / pr-retrospective 等の後続 Skill とは責務が重複しない (本 Skill は解析コメント post + Plan / Epic 起票で終了)。

## 入力

- **起動 prompt**: `pr-poller` から渡される Renovate labeled open PR の number (例: `123`)、または人間からの手動起動 (`/dependency-upgrade <PR#>`)
- **PR metadata**: `gh pr view <PR#> --json number,title,body,labels,headRefName,baseRefName,files,changedFiles,additions,deletions,commits,author,createdAt` で取得 (PR description / Renovate コメント / 変更ファイル一覧 / コミット履歴 / 作者 = `renovate[bot]`)
- **変更 diff**: `gh pr diff <PR#>` で差分取得 (主に `gradle/libs.versions.toml` / `**/build.gradle.kts` / `package.json` 等の依存記述ファイル)
- **上流 changelog**: 以下のいずれかから取得:
  - PR description の Renovate 自動生成「Release Notes」「Changelog」セクション (1 次情報、`gh pr view --json body` の本文 parse)
  - 上流 GitHub Releases (`gh release view <tag> --repo <owner>/<lib>`)
  - **Context7 MCP** (`mcp-usage.md` 準拠): バージョン固有の API シグネチャ / migration guide / 削除 API 一覧を取得 (training data の古い情報 / hallucination 抑止、R-28)
- **関連 docs / SPEC / ADR**: `docs/specifications/**` / `docs/adr/**` / `docs/epics/**` で関連取り組みの有無を確認 (例: 既存 ADR-0011 Firebase 撤去方針 / ADR-0008 Backend SQLite / ADR-0023 behavior preservation との整合性)
- **JetBrains MCP 結果**: 変更ライブラリの import 検索を IDE indexing 経由で実施 (`mcp-usage.md` 「手動 git grep より JetBrains MCP 優先」)、`gh pr diff` で得られない間接的な影響箇所 (transitive dependency / 同 module 他クラスからの呼出) を補完抽出

## 出力

- **PR への解析サマリコメント** (常に post): `gh pr comment <PR#> --body-file /tmp/dependency-upgrade-<PR#>-summary.md` で post、本文構造は §解析サマリコメントテンプレ
  - 解析結果の semver 種別 / 破壊的変更有無 / 影響モジュール件数 / approve 推奨度を構造化 (Markdown 表 + チェックリスト)
  - **PII / Secrets redaction を必ず通す** (`.claude/rules/pii.md` / `.claude/rules/secrets.md`、CI ログ / changelog 引用に access token / メール / GIS userinfo が含まれる場合)
- **Plan or Epic 起票** (該当時): `plan-author` または `epic-author` Skill を呼ぶ (常時生成ではなく、§Plan 起票判定 を満たした時のみ)
  - Plan の type 列は `dependency-upgrade` 固定 (`.claude/rules/plan.md` §frontmatter 必須キー / `.claude/rules/commit-message.md` Conventional Commits `chore` / `build` と対応)
  - frontmatter `expected_modules` は §Phase 3 影響範囲分析 で抽出した touch ファイル glob を block 形式で記載 (`roadmap-tracker` の並行可否判定の入力)
- **`docs/plans/INDEX.md` / `docs/epics/INDEX.md` 追記**: `plan-author` / `epic-author` が自動更新 (本 Skill は直接 touch しない)
- **handoff 通知** (orchestrator pane または人間へ): 「PR #N に解析コメント post 完了、approve 推奨度 = X、Plan/Epic = Y、人間 approve を待つ」を 1 行で報告
- **本 Skill が生成しない**:
  - **`approve` / `auto-merge` / `merge` 等のラベル付与** (R-15 / `.claude/rules/merge-readiness.md` 3 条件のうち人間 approve は必須、Skill が代行しない)
  - **`gh pr merge` 実行** (人間判断、orchestrator pane / 本人が手動実行)
  - 実装コード変更 (`implementation-workflow` 担当)
  - learning ファイル (`pr-retrospective` 担当、merge 後)

## フェーズ別動作

### Phase 1: PR metadata + changed dependency + version diff 取得

- `pr-poller` から渡された PR number に対し `gh pr view <PR#> --json number,title,body,labels,headRefName,baseRefName,files,changedFiles,additions,deletions,commits,author,createdAt` を実行
- 作者が `renovate[bot]` (or 同等) であることを確認 (手動起動時は作者チェックを緩める)
- 変更ファイル一覧を取得し、依存記述ファイル (`gradle/libs.versions.toml` / `**/build.gradle.kts` / `package.json` 等) に変更が含まれていることを確認 (依存記述以外のみの変更なら no-op で終了 + warning コメント post)
- `gh pr diff <PR#>` で diff を取得し、変更されたライブラリ coordinate (group:name) + before / after version + semver 種別 (patch / minor / major) を表形式で抽出
- 同一 PR で複数ライブラリが束ねられている場合 (Renovate の `groupName` 設定) は **個別ライブラリごとに以下 Phase 2-4 を繰り返し**、合算結果を Phase 5 でまとめる
- **Renovate ラベル判別**: `labels` に `renovate` / `dependencies` / `renovate-bot` のいずれかが含まれていること (`.claude/rules/pr-poller.md` Phase 2 検出ロジック準拠)

### Phase 2: 破壊的変更 / 重大バグ修正 / セキュリティ修正の有無判定

- PR description の Renovate 自動生成「Release Notes」「Changelog」セクションを最優先で parse (1 次情報、Renovate が上流公式 changelog を埋め込む)
- 不足分は `gh release view <tag> --repo <owner>/<lib>` で上流 GitHub Releases を取得
- バージョン固有の API シグネチャ / migration guide / 削除 API 一覧は **Context7 MCP** で取得 (`mcp-usage.md` / R-28、AI hallucination 抑止)
- 以下の 3 観点で判定 (どれか 1 つでも検出されたら approve 推奨度を下げる):
  1. **破壊的変更** (Breaking Change): API 削除 / signature 変更 / public method の rename / deprecation / 必須引数の追加 / default 挙動変更 / minimum runtime version 変更 (JVM target / Kotlin version / Android minSdk 等) / TLS / 認証フロー変更
  2. **重大バグ修正** (Critical Bugfix): security advisory との関連 / data loss を伴う bug / CI grueen が崩れるレベルの regression / merge を急ぐべき内容
  3. **セキュリティ修正** (Security Patch): CVE / GHSA ID 付き fix / `Severity: High / Critical` / supply chain 系 (transitive dependency の悪意ある package 排除等)
- **changelog 不在時の保守的判断** (上流が CHANGELOG / Release notes を提供していない / `gh release view` で notes 空 / Context7 でも該当バージョン未収録): 「changelog 不在 → 保守的に approve 推奨度を下げる (要 review 以下)」+ 「人間レビュー時に上流 GitHub repo 直接確認を依頼」とコメントに明示
- semver 種別と上記 3 観点を組み合わせて Phase 5 の approve 推奨度を後段で算出

### Phase 3: 影響範囲分析 (touch ファイル + 関連テスト + 既存 SPEC 照合)

- 変更ライブラリ coordinate (例: `io.ktor:ktor-client-core`) を **JetBrains MCP の IDE indexing** で検索し、import 経由で依存している Kotlin source file 一覧を抽出 (`mcp-usage.md` 「手動 git grep より JetBrains MCP 優先」、R-28)
- 関連テスト (`**/*Test.kt` / `**/*Spec.kt`) を同様に抽出し、テストが影響範囲を覆っているかを推定
- 既存 SPEC (`docs/specifications/{basic,detail}/SPEC-*.md`) との照合: 該当ライブラリが SPEC で言及されているか / 仕様補強が必要かを判定
- **インフラ依存変更時の Cloud Run / Backend 影響を特に確認**:
  - **TLS / 認証フロー** (Ktor / OkHttp / JWKS / GIS Client) → `backend-auth.md` / `network-client.md` 参照、Cloud Run service 再デプロイ要否を Plan 本文で言及
  - **SQLDelight driver / SQLite version** → `r2-litestream.md` の Litestream replicate / restore への影響、`db-protection.md` の `users.db` 保護方針との整合
  - **WIF / `service-account*.json`** → `.claude/rules/secrets.md` 「絶対 commit してはいけないもの」と Cloud Run / Cloudflare 認証情報の TTL ローテーション影響
  - **Compose Multiplatform / wasmJs / iosX64 等 KMP target** → `wasm-compat.md` の制約 (Coroutines `Dispatchers.Default` / OkHttp 除外等) と整合性確認
- **Firebase 系の Renovate 自動追加検出** (`no-firebase.md` 事前ガード): 変更 diff に `com.google.firebase:*` / `dev.gitlive:firebase-*` / `com.google.gms.google-services` 等が含まれる場合、即時 reject + downgrade / 別 Plan で代替案検討をコメントに明示 (ADR-0011 Firebase → GIS 移行方針との衝突)

### Phase 4: 単一 PR 完結 vs Epic 規模判定 + Plan / Epic 起票

- **判定基準** (`docs/harness/plan.md` §4.1 / `.claude/rules/plan.md` §Epic 昇格条件 SoT):
  - **起票不要**: patch / minor + 破壊的変更なし + 影響モジュール 0-2 件 + 既存テストが影響範囲を覆っている → Plan / Epic 起票せず、PR コメントのみで完結 (approve 推奨)
  - **Plan 起票** (単一 PR スコープ): 想定変更ファイル数 ≤ 10 / 想定期間 ≤ 1 週間 / migration が局所的 / open question 想定なし → `plan-author` を呼ぶ
  - **Epic 起票** (複数 PR スコープ): major version bump で multi-file refactor 必要 / 想定変更ファイル数 > 30 / 仕様波及が複数 SPEC / インフラ依存変更で Cloud Run 再デプロイ要 / 期間 > 1 週間 → `epic-author` を呼ぶ
  - 迷ったら **Plan で起票** (Plan は後から `status: promoted` + `promoted_to: EPIC-NNN` で Epic 昇格可能、`.claude/rules/plan.md` §Plan ⇄ Epic ⇄ ADR の責務分離)
- **Plan 起票時の必須セクション** (Plan 本文の `plan-author` template に渡す):
  - 概要 / 背景 / アプローチ (migration 手順 / 影響範囲 / 既存テスト追加要否)
  - 受け入れ基準 (AC): `./gradlew check` グリーン / Roborazzi baseline グリーン or 承認済 / 削除 API 利用箇所の代替案適用済
  - スコープ外: 関連ライブラリの追従更新 / UI 大幅刷新等
  - メモ: changelog 引用 (PII / Secrets redaction 後) / migration guide リンク
- **Epic 起票時**: README の `expected_modules` に touch 範囲を block 形式で記載 (`roadmap-tracker` 並行可否判定の入力)、decisions.md に「major version bump 採用根拠」+ 「behavior preservation 担保策」を記録
- **ADR 起票判定** (該当時): semver **major** で `.claude/rules/adr.md` 起票基準 (アーキテクチャ的に不変な決定 / 撤回コストが高い / 他複数の判断に影響、2 項目以上充足) を満たす場合のみ、Plan / Epic から `adr-author` Skill を呼ぶ (本 Skill 自身は ADR を直接起草しない、Plan / Epic 経由で `adr-author` に委譲)

### Phase 5: 解析サマリコメント post + approve 推奨度判定

- 解析結果を §解析サマリコメントテンプレ に従って `/tmp/dependency-upgrade-<PR#>-summary.md` に Write
- `gh pr comment <PR#> --body-file /tmp/dependency-upgrade-<PR#>-summary.md` で PR に post
- **approve 推奨度** を以下の 3 段階で明示:
  - ✅ **approve 推奨**: patch / minor + 破壊的変更なし + 影響モジュール 0-2 件 + 既存テストが影響範囲を覆っている + changelog 明示
  - ⚠️ **要 review** (人間判断): minor + 破壊的変更あり / major + 影響モジュール局所的 / changelog 不在 / Context7 で確認したいシグネチャ あり
  - 🛑 **要 downgrade / Plan 起票** (危険): major + multi-file refactor 必要 / Firebase 系 (no-firebase.md 違反) / TLS / 認証フロー破壊 / Cloud Run 再デプロイ要 + Litestream / R2 認証情報 TTL に影響 等
- **本 Skill は `approve` / `auto-merge` 等のラベル付与を行わない** (R-15、`.claude/rules/merge-readiness.md` 3 条件のうち人間 approve は必須、Skill が代行しない)
- **本 Skill は `gh pr merge` 実行を行わない** (人間判断、orchestrator pane / 本人手動)
- handoff メッセージのテンプレ:

  ```text
  ✅ dependency-upgrade 完了
  - 対象 PR: #NNN (<library coordinate>: <before> → <after>)
  - semver 種別: patch / minor / major
  - approve 推奨度: ✅ approve 推奨 / ⚠️ 要 review / 🛑 要 downgrade
  - Plan or Epic: docs/plans/PLAN-NNN-<slug>.md or docs/epics/EPIC-NNN-<slug>/ (起票時のみ)
  - 次のステップ: 人間 review → `gh pr merge --squash` (orchestrator pane / 本人手動)
  ```

## 解析サマリコメントテンプレ

`/tmp/dependency-upgrade-<PR#>-summary.md` に書き出して `gh pr comment --body-file` で post する Markdown:

```markdown
## 🤖 dependency-upgrade Skill 解析結果

### 変更ライブラリ

| ライブラリ | Before | After | semver 種別 |
|---|---|---|---|

### Renovate / 上流 changelog 要約 (主要 3-5 点)

- ...

### 破壊的変更 / 重大バグ修正 / セキュリティ修正

- [ ] 破壊的変更 (Breaking Change): あり / なし / changelog 不在 → 保守的に「あり」扱い
- [ ] 重大バグ修正 (Critical Bugfix): あり / なし
- [ ] セキュリティ修正 (Security Patch / CVE / GHSA): あり / なし
- Context7 MCP で確認した API 削除 / signature 変更:

### 影響範囲

| 種別 | 件数 | 主要パス |
|---|---|---|
| 直接 import (Kotlin source) | N | ... |
| 関連テスト | N | ... |
| 既存 SPEC 言及 | N | ... |
| インフラ依存 (TLS / 認証 / Litestream / WIF / wasm-compat) | N | ... |

### approve 推奨度

- 推奨度: ✅ approve 推奨 / ⚠️ 要 review / 🛑 要 downgrade / Plan 起票
- 根拠: ...

### 後続アクション

- [ ] Plan 起票: PLAN-NNN-<slug> (該当時)
- [ ] Epic 起票: EPIC-NNN-<slug> (該当時)
- [ ] ADR 起票検討: ADR-NNNN-<slug> (semver major + 起票基準 2 項目以上充足時)

### Skill 制約

- 本 Skill は `approve` / `auto-merge` ラベル付与 / `gh pr merge` 実行を行わない (R-15、人間 approve 必須)
- 本コメントは pr-poller がローカル Claude Code 内で起動した dependency-upgrade Skill の出力 (GitHub Actions では実行していない、ADR-0017)
```

## Gotchas

- **自動 merge / approve ラベル付与は禁止** (R-15 / `.claude/rules/merge-readiness.md` 3 条件): 解析結果が ✅ approve 推奨 でも、`approve` ラベル付与 / `gh pr merge` 実行は **本 Skill では行わない**、人間 / orchestrator pane に委ねる。「auto-merge」「self-merge」「force-merge」をコメント本文 / commit message に書かない (classifier denied リスク、`.claude/rules/commit-message.md` メタ言及語回避)
- **changelog 不在時の保守的判断**: 上流が CHANGELOG / Release notes を提供していない場合、approve 推奨度を「⚠️ 要 review 以下」に下げる + 「人間レビューで上流 repo 直接確認を依頼」をコメントに明示。「changelog なしだが patch だから safe」と独断しない
- **major version bump の慎重さ**: major bump は API 削除 / signature 変更 / default 挙動変更が混在する典型パターン。Plan 単独でなく Epic 起票 + ADR 検討を優先 (`.claude/rules/adr.md` §起票基準 2 項目以上充足時)、Context7 MCP で migration guide を必ず確認 (R-28)
- **インフラ依存変更時の Cloud Run 影響**: TLS / 認証 / SQLDelight driver / WIF / `service-account*.json` 関連の更新は **Cloud Run service 再デプロイ要否** を Plan 本文で言及。`backend-auth.md` / `network-client.md` / `r2-litestream.md` / `db-protection.md` / `secrets.md` への波及を必ず確認
- **wasm-compat 違反**: KMP target (wasmJs / jsBrowser) で利用されるライブラリの major bump は `wasm-compat.md` の制約 (Coroutines `Dispatchers.Default` / OkHttp 除外 / 等) と衝突しやすい。Compose Multiplatform / Ktor / SQLDelight 系は特に注意
- **Firebase 系の Renovate 自動追加検出**: `no-firebase.md` 事前ガードで「変更 diff に `com.google.firebase:*` / `dev.gitlive:firebase-*` / `com.google.gms.google-services` を含む場合は即時 reject」を強制。Renovate 設定 (`renovate.json5`) で Firebase 系を `disabled` にする (A6 / 本 Skill 本格化時) のフィードバックも Plan / Epic 起票時に推奨アクションとして記載
- **PII / Secrets redaction**: PR description / `gh pr view` 出力 / changelog 引用に access token / メール / GIS userinfo が含まれる場合、`gh pr comment` post 前に `.claude/rules/pii.md` / `.claude/rules/secrets.md` の redaction パターンで置換 (R-26)。trufflehog secret-scan (A6) と二重で防御
- **GitHub Actions では呼ばない**: 本 Skill 起動経路は `pr-poller` (ローカル Claude Code) からのみ (ADR-0017)。`.github/workflows/dependency-upgrade-check.yml` 等の CI ワークフローを新規追加しない (Claude API コスト回避、`docs/harness/plan.md` §B0 / §A4 の方針)
- **groupName 設定で複数ライブラリ束ね PR の取扱**: Renovate `groupName` で 1 PR に複数ライブラリが含まれる場合、Phase 1-4 を **ライブラリ単位で繰り返し** + Phase 5 で合算サマリを post。1 ライブラリでも 🛑 要 downgrade なら PR 全体の approve 推奨度を下げる (最弱原則)
- **`adr-author` Skill 呼び出し**: ADR 起票判定 (`.claude/rules/adr.md` §起票基準 2 項目以上充足) を満たした場合、本 Skill 自身が ADR を直接起草せず、Plan / Epic 経由で `adr-author` Skill に委譲 (`.claude/skills/adr-author/SKILL.md`、A3-4 で active 化済)
- **plan-author / epic-author の呼び分けミス防止**: 単一 PR スコープを `epic-author` で起票すると Epic ディレクトリが過剰生成され撤回困難、複数 PR スコープを `plan-author` で起票するとロードマップ追跡対象外 (R-34) になり進捗可視化が機能しない。判定基準は `.claude/rules/plan.md` §Epic 昇格条件 と `docs/harness/plan.md` §4.1 を SoT とし、迷ったら Plan + 後から promoted
- **Context7 MCP の外部障害時フォールバック** (`.claude/rules/mcp-usage.md` Gotchas 参照): Context7 が応答しない場合は `gh release view` / 上流 GitHub repo 直接確認にフォールバック + コメント本文に「Context7 障害により公式 release notes を直接参照」と明示 (検証根拠の透明性確保)
- **PR コメントは 1 PR につき 1 コメント原則**: 同 PR に対して本 Skill が複数回起動された場合 (Renovate が PR を rebase / 強制 push で更新したとき等)、過去コメントは残しつつ最新解析を新コメントで追記。古いコメントを削除しない (履歴保持)
- **解析サマリコメント本文に「auto-merge」「self-merge」「force-merge」を含めない** (classifier denied リスク、`.claude/rules/commit-message.md` メタ言及語回避 / `.claude/rules/orchestrator-criteria.md` §classifier ブロック対応): 「人間 approve を待つ」「orchestrator pane の手動 merge」等の中立表現で記述

## 関連

- ADR 0017 (ローカルポーリング駆動、GitHub Actions から Claude API を呼ばない / 本 Skill 起動経路の SoT)
- ADR 0018 (Skill 駆動 KPT ループ、6 段階フローの Spec Gen 担当の 1 種)
- ADR 0024 (`gh` CLI 採用、PR 操作の SoT)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由、本 Skill 自体も rubric 評価対象)
- ADR 0027 (docs 構造 + 命名規約 + 日本語化 + コード禁止原則)
- `docs/harness/plan.md` §5.3 (Skill 責務一覧、本 Skill の行) / §6.2 A3 (本 Skill 本格化フェーズ) / §6.2 A4 (`pr-poller` 3 系統起動経路本格化)
- `.claude/skills/pr-poller/SKILL.md` (本 Skill を起動する Skill、Renovate 検出ロジック / 3 系統起動経路)
- `.claude/skills/plan-author/SKILL.md` (Plan 起票責務、本 Skill が呼ぶ)
- `.claude/skills/epic-author/SKILL.md` (Epic 起票責務、本 Skill が呼ぶ)
- `.claude/skills/adr-author/SKILL.md` (ADR 起票責務、Plan / Epic 経由で本 Skill から間接的に呼ばれる)
- `.claude/skills/implementation-workflow/SKILL.md` (Plan / Epic 起票後のバトンタッチ先)
- `.claude/rules/pr-poller.md` (Renovate 検出 + 起動経路 SoT)
- `.claude/rules/plan.md` (Plan ⇄ Epic ⇄ ADR の責務分離、Epic 昇格条件)
- `.claude/rules/epic.md` (Epic ディレクトリ構成、`expected_modules` 必須)
- `.claude/rules/network-client.md` (Ktor / OkHttp / TLS / timeout の影響範囲、依存変更時に必須確認)
- `.claude/rules/gradle.md` (`build.gradle.kts` / version catalog の依存変更パターン)
- `.claude/rules/no-firebase.md` (Firebase 系の Renovate 自動追加 reject ガード)
- `.claude/rules/mcp-usage.md` (Context7 MCP / JetBrains MCP の使い分け + 外部障害フォールバック)
- `.claude/rules/merge-readiness.md` (R-15 3 条件、本 Skill が auto-merge を行わない根拠)
- `.claude/rules/pr-template.md` (`.github/PULL_REQUEST_TEMPLATE/dependency-upgrade.md` 必須セクション)
- `.claude/rules/{pii,secrets}.md` (changelog 引用 / PR コメント post 前の redaction)
- `.github/PULL_REQUEST_TEMPLATE/dependency-upgrade.md` (Renovate PR の description テンプレ)
