---
id: rules-index
title: rules 索引
status: skeleton
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
| `planned (X)` | まだ実体なし。フェーズ X (例: A2-2、A2-3、A3、A6、A7、Phase C) で新規作成予定 |
| `living` | 索引・ロードマップ等で常に更新される文書 |

## カテゴリ別索引

### 計画・記録

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `plan.md` | planned (A2-2) | Plan ファイルの命名規約 / 状態遷移 |
| `epic.md` | planned (A2-2) | Epic ディレクトリ構成 / 状態遷移 |
| `roadmap.md` | skeleton (B0、A2-3 で本格化) | `roadmap-tracker` Skill の操作規約 |
| `adr.md` | skeleton (B0、A2-2 で本格化) | ADR 起票基準 / 採番 / 起票すべき例・他の記録方法にすべき例 |

### アーキテクチャ層別

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `viewmodel.md` / `ui-state.md` / `composable.md` / `navigation.md` / `repository.md` / `network-client.md` | planned (A2-2) | 層ごとの実装規約 |

### 横断的関心事

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `naming.md` / `error-handling.md` / `logging.md` / `i18n.md` / `wasm-compat.md` / `firebase-boundary.md` | planned (A2-2) | 横断的なコーディング規約 |

### ファイルタイプ別 / テスト

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `gradle.md` / `kotlin-test.md` / `screenshot-test.md` / `sql-delight.md` / `sparql.md` / `test-paired-class.md` / `markdown.md` | planned (A2-2、Gradle / Konsist / markdownlint 設定の本格化は A6/A7) | 各ファイル種別の規約 |
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
| `markdown.md` | planned (A2-2、markdownlint-cli2 統合は A6) | Markdown 表記規約 |
| `template-language.md` | skeleton (B0、A2-1 で常時ロード化 / A2-3 で本格化) | 全テンプレ Markdown は日本語 (ADR 0027) |

### 同期 / Backend

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `sync-job.md` / `sqlite-data-file.md` / `cloud-run-deploy.md` / `removed-modules.md` / `backend-auth.md` | planned (A2-2、Skill 起草 / Phase C 実装で参照) | Backend / 同期 / 撤去モジュールの規約 |

### セキュリティ / 個人情報

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `pii.md` | skeleton (B0 常時ロード、A2-2 で本格化) | PII の定義・最小化・redaction 強制 |
| `secrets.md` | skeleton (B0 常時ロード、A2-2 で本格化) | Secrets 管理 (`.env` / GitHub Secrets / Secret Manager) |
| `db-protection.md` | skeleton (B0、A2-1 で `.dockerignore` TODO 追記 / A2-2 で本格化) | `users.db` の commit / イメージ焼込み禁止、R2 private |
| `no-firebase.md` | planned (A2-2) | Firebase 系の新規追加禁止規約 (既存 import 検出は `firebase-boundary.md`、責務分担は EPIC-A2 `decisions.md` 参照) |
| `cloudflare-pages.md` | planned (A2-2 で起草、C7 で本格化) | Cloudflare Pages デプロイ規約 |
| `r2-litestream.md` | planned (A2-2 で起草、C5 で本格化) | Litestream replicate / restore、R2 endpoint |

## 関連

- `docs/harness/plan.md` §5.0.3 (rules 一覧)
- `CLAUDE.md` §lookup-table (編集対象 ⇄ rules の対応)
