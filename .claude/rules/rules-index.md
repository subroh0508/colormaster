---
id: rules-index
title: rules 索引
status: living
last_updated: 2026-05-17
# 注意: 索引そのものは小さく、AI が他 rule を発見する起点として
# 起動時に常時ロードしておくのが望ましいため `paths` を意図的に未設定。
---

# rules 索引

`.claude/rules/` 配下に置かれるルールファイルの一覧。各 rule は frontmatter の
**`paths` フィールド** (block 形式) で対象ファイルパターンを指定し、Claude Code が
該当ファイルを Read するときだけ自動ロードされる ([公式 docs](https://code.claude.com/docs/en/memory#organize-rules-with-claude/rules/) 参照)。
`paths` 未指定の rule は起動時に **無条件ロード** されるため、安全網として常時必要な
場合 (PII / Secrets / rules-index / 全 Markdown 共通の template-language) に限定する。

詳細な参照パターンマッチング (人間 / AI 向け索引) は `CLAUDE.md` の lookup table を参照。

## `paths` 設計方針

- **常時ロード (paths 未設定)**: `pii.md` / `secrets.md` / `rules-index.md` / `template-language.md` (安全網または全 Markdown 共通の規約のため、起動時に無条件ロード)
- **広範囲スコープ**: `docs-structure.md` (`docs/**/*.md` + ルート 3 ファイル)
- **モジュール / フェーズ別**: 残りの rule は各 Skill ディレクトリ / 対象ソース / docs サブツリーに限定
- **glob はブロック形式必須** (`.claude/rules/docs-structure.md` frontmatter 規約):

  ```yaml
  paths:
    - "feature/**/*.kt"
    - "core/**/*.kt"
  ```

## status 表記の語彙

`状態` 列の語彙を以下で統一する (A1 レトロ Problem #1 「『既存』ラベル誤宣言 13+ 件」の解消):

| 値 | 意味 |
|---|---|
| `skeleton (B0)` | B0 (PR #117) で配置済の骨格。本文薄め、Gotchas / 関連リンクは揃っている。本格化フェーズで肉付け |
| `stable (X)` | フェーズ X (例: A2-1、A2-2) で本格化済み。実装・運用に耐える本文と Gotchas が揃っている |
| `planned (X)` | まだ実体なし。フェーズ X (例: A2-3、A3、A6、A7、Phase C) で新規作成予定 |
| `living` | 索引・ロードマップ等で常に更新される文書 |

## カテゴリ別索引

### 計画・記録

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `plan.md` | stable (A2-2) | Plan ファイルの命名規約 / 状態遷移 / Epic 昇格条件 |
| `epic.md` | stable (A2-2) | Epic ディレクトリ構成 / 状態遷移 / 補助ファイル責務 |
| `roadmap.md` | stable (A2-2) | `roadmap-tracker` Skill の操作規約 |
| `adr.md` | stable (A2-2) | ADR 起票基準 / 採番 / 起票すべき例・他の記録方法にすべき例 |

### アーキテクチャ層別

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `viewmodel.md` | stable (A2-2) | `androidx.lifecycle.ViewModel` 継承 / StateFlow 公開 / SavedStateHandle |
| `ui-state.md` | stable (A2-2) | `*UiState` data class / `*UiAction` sealed interface |
| `composable.md` | stable (A2-2) | Preview 必須 / `*Screen(uiState, onAction)` 引数規約 / design tokens 強制 |
| `navigation.md` | stable (A2-2) | Navigation 3 / `Route.kt` / `@Serializable` type-safe args |
| `repository.md` | stable (A2-2) | interface in `core/model` / impl in `core/data` |
| `network-client.md` | stable (A2-2) | Ktor Client / OkHttp / JS Fetch / TLS / timeout |

### 横断的関心事

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `naming.md` | stable (A2-2) | Kotlin / Compose / Test 命名 / suffix 規約 |
| `error-handling.md` | stable (A2-2) | `Result<T>` / `runCatching` / 例外伝播ポリシー |
| `logging.md` | stable (A2-2) | Napier / level / PII redaction |
| `i18n.md` | stable (A2-2) | `strings.xml` / compose-resources / 日本語 first |
| `wasm-compat.md` | stable (A2-2) | wasmJs 制約 / Coroutines `Dispatchers.Default` / OkHttp 除外 |
| `firebase-boundary.md` | stable (A2-2) | 既存 Firebase import 検出 / Konsist 検証 (二段運用、`no-firebase.md` 参照) |

### ファイルタイプ別 / テスト

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `gradle.md` | stable (A2-2、Gradle カスタムタスクの本格化は A6) | `build.gradle.kts` 規約 / version catalog / convention plugin |
| `kotlin-test.md` | stable (A2-2、Konsist 統合は A6) | Kotest DescribeSpec / fixture / `runTest` |
| `screenshot-test.md` | stable (A2-2、Roborazzi 設定の本格化は A10) | 4 パターン baseline (mobile/desktop × Light/Dark) |
| `sql-delight.md` | stable (A2-2) | `*.sq` ファイル規約 / migration / driver `expect/actual` |
| `sparql.md` | stable (A2-2) | SPARQL prefix / im@sparql クエリ規約 / injection 対策 |
| `test-paired-class.md` | stable (A2-2、Konsist 統合は A6) | 実装 ⇄ テストクラスペアリング検証 |
| `markdown.md` | stable (A2-2、markdownlint-cli2 統合は A6) | Markdown 表記規約 |
| `coverage-100.md` | planned (A7) | Line/Branch 段階達成規約 (指標 A) |
| `spec-traceability.md` | planned (A7) | `@Spec` annotation / Spec coverage 規約 (指標 B) |
| `mutation-testing.md` | planned (A7) | PITest 運用規約 (指標 C) |

### UI/UX デザイン

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `design-tokens.md` | skeleton (B0、A2-3 で本格化) | DESIGN.md 3 階層構造、hex/sp/dp ハードコード禁止 |
| `ui-snapshot.md` | skeleton (B0、A2-3 で本格化) | Preview + screenshot baseline 維持、human approve 必須 |
| `ui-inventory.md` | skeleton (B0、A2-3 で本格化) | `docs/design/inventory/` の構造と更新規約 |
| `behavior-preservation.md` | skeleton (B0、A2-3 で本格化) | リファクタ時の振る舞い維持原則 |

### MCP

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `mcp-usage.md` | skeleton (B0、A2-1 で Gotchas 追記 / A2-3 で本格化) | MCP サーバの使い分け、認証情報の取り扱い |

### プロセス

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `pr-template.md` | planned (A2-3) | PR テンプレート選択 / `gh pr create --template <type>.md` 運用規約 |
| `commit-message.md` | skeleton (A2-1 で新規作成、A2-3 で本格化) | Conventional Commits 規約 / subject 長 / Co-Authored-By 必須 |
| `branch-naming.md` | planned (A2-3) | ブランチ命名規約 (feature / epic / harness / chore / fix) |

### ハーネス改善ループ

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `retrospective-format.md` | skeleton (B0、A2-3 で本格化) | learning ファイル構造化フォーマット |
| `pr-poller.md` | skeleton (B0、A2-3 で本格化、Skill 本格実装は A3/A4) | ローカルポーリング規約、排他制御 |
| `harness-meta-criteria.md` | planned (A2-3、Skill 統合は A4) | 改修 PR 採用/見送り/撤去の判定基準 |
| `skill-authoring.md` | skeleton (B0、A2-3 で本格化) | Skill 作成は `example-skills:skill-creator` 経由 |
| `harness-evolution.md` | skeleton (B0、A2-3 で本格化) | 外部情報源ホワイトリスト、出力フォーマット |
| `docs-structure.md` | skeleton (B0、A2-3 で本格化) | `docs/` の歩き方、命名規約、5 行 summary + lazy-load |

### 実装ワークフロー

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `implementation-workflow.md` | skeleton (B0、A2-3 で本格化、Skill 本格実装は A3) | 10 フェーズ手順、fix loop 上限、worktree 未マージ検知 |
| `code-reviewer-aspects.md` | skeleton (B0、A2-3 で本格化、Skill 本格実装は A3) | 8 aspect の binary eval checklist、Coordinator 形式 |
| `merge-readiness.md` | planned (A2-3) | Merge 可否判定 |
| `pr-draft-policy.md` | planned (A2-3) | Draft → Ready 昇格条件 |
| `spec-living-sync.md` | planned (A2-3) | 実装中の仕様変更時の双方向同期 |

### ドキュメント表記

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `markdown.md` | stable (A2-2、markdownlint-cli2 統合は A6) | Markdown 表記規約 |
| `template-language.md` | skeleton (B0、A2-1 で常時ロード化 / A2-3 で本格化) | 全テンプレ Markdown は日本語 (ADR 0027) |

### 同期 / Backend

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `sync-job.md` | stable (A2-2、Phase B-C で workflow 実装) | im@sparql 同期 workflow + 生成 PR ラベル |
| `sqlite-data-file.md` | stable (A2-2) | `data/idols.db` (commit 可) vs `users.db` (禁止) の境界 |
| `cloud-run-deploy.md` | stable (A2-2、Phase C7 で本格デプロイ) | Cloud Run service / Dockerfile / WIF |
| `removed-modules.md` | stable (A2-2) | 撤去 module 一覧 (Decompose / Firebase / 旧 JS app) |
| `backend-auth.md` | stable (A2-2) | GIS ID Token / JWKS / uid 抽出 |

### セキュリティ / 個人情報

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `pii.md` | stable (A2-2、常時ロード) | PII の定義・最小化・redaction 強制 |
| `secrets.md` | stable (A2-2、常時ロード) | Secrets 管理 (`.env` / GitHub Secrets / Secret Manager) |
| `db-protection.md` | stable (A2-2) | `users.db` の commit / イメージ焼込み禁止、R2 private |
| `no-firebase.md` | stable (A2-2) | Firebase 系の新規追加禁止 (Skill 事前ガード、`firebase-boundary.md` と二段運用、EPIC-A2 `decisions.md` 参照) |
| `cloudflare-pages.md` | stable (A2-2、C7 で本格デプロイ) | Cloudflare Pages デプロイ規約 |
| `r2-litestream.md` | stable (A2-2、C5 で本格運用) | Litestream replicate / restore、R2 endpoint、TTL ローテーション |

## 関連

- `docs/harness/plan.md` §5.0.3 (rules 一覧)
- `CLAUDE.md` §lookup-table (編集対象 ⇄ rules の対応)
- EPIC-A2 `README.md` / `decisions.md` (A2-1〜A2-5 分割、本格化のフェーズ計画)
