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

- **常時ロード (paths 未設定)**: `pii.md` / `secrets.md` / `rules-index.md` / `template-language.md`
- **広範囲スコープ**: `docs-structure.md` (`docs/**/*.md` + ルート 3 ファイル)
- **モジュール / フェーズ別**: 残り 15 rule は各 Skill ディレクトリ / 対象ソース / docs サブツリーに限定
- **glob はブロック形式必須** (`.claude/rules/docs-structure.md` frontmatter 規約):

  ```yaml
  paths:
    - "feature/**/*.kt"
    - "core/**/*.kt"
  ```

## カテゴリ別索引

### 計画・記録

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `plan.md` | 既存 | Plan ファイルの命名規約 / 状態遷移 |
| `epic.md` | 既存 | Epic ディレクトリ構成 / 状態遷移 |
| `roadmap.md` | **B0 雛形** | `roadmap-tracker` Skill の操作規約 |
| `adr.md` | **B0 雛形** | ADR 起票基準 / 採番 / 起票すべき例・他の記録方法にすべき例 |

### アーキテクチャ層別

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `viewmodel.md` / `ui-state.md` / `composable.md` / `navigation.md` / `repository.md` / `network-client.md` | 既存 (A2 で本格化) | 層ごとの実装規約 |

### 横断的関心事

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `naming.md` / `error-handling.md` / `logging.md` / `i18n.md` / `wasm-compat.md` / `firebase-boundary.md` | 既存 (A2 で本格化) | 横断的なコーディング規約 |

### ファイルタイプ別 / テスト

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `gradle.md` / `kotlin-test.md` / `screenshot-test.md` / `sql-delight.md` / `sparql.md` / `test-paired-class.md` | 既存 (A6/A7 で本格化) | 各ファイル種別の規約 |
| `coverage-100.md` | A7 で導入 | Line/Branch 段階達成規約 (指標 A) |
| `spec-traceability.md` | A7 で導入 | `@Spec` annotation / Spec coverage 規約 (指標 B) |
| `mutation-testing.md` | A7 で導入 | PITest 運用規約 (指標 C) |

### UI/UX デザイン

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `design-tokens.md` | **B0 雛形** | DESIGN.md 3 階層構造、hex/sp/dp ハードコード禁止 |
| `ui-snapshot.md` | **B0 雛形** | Preview + screenshot baseline 維持、human approve 必須 |
| `ui-inventory.md` | **B0 雛形** | `docs/design/inventory/` の構造と更新規約 |
| `behavior-preservation.md` | **B0 雛形** | リファクタ時の振る舞い維持原則 |

### MCP

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `mcp-usage.md` | **B0 雛形** | MCP サーバの使い分け、認証情報の取り扱い |

### プロセス

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `pr-template.md` / `commit-message.md` / `branch-naming.md` | 既存 (A3 で本格化) | PR / コミット / ブランチ命名規約 |

### ハーネス改善ループ

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `retrospective-format.md` | **B0 雛形** | learning ファイル構造化フォーマット |
| `pr-poller.md` | **B0 雛形** | ローカルポーリング規約、排他制御 |
| `harness-meta-criteria.md` | 既存 (A4 で本格化) | 改修 PR 採用/見送り/撤去の判定基準 |
| `skill-authoring.md` | **B0 雛形** | Skill 作成は `example-skills:skill-creator` 経由 |
| `harness-evolution.md` | **B0 雛形** | 外部情報源ホワイトリスト、出力フォーマット |
| `docs-structure.md` | **B0 雛形** | `docs/` の歩き方、命名規約、5 行 summary + lazy-load |

### 実装ワークフロー

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `implementation-workflow.md` | **B0 雛形** | 10 フェーズ手順、fix loop 上限、worktree 未マージ検知 |
| `code-reviewer-aspects.md` | **B0 雛形** | 8 aspect の binary eval checklist、Coordinator 形式 |
| `merge-readiness.md` | 既存 (A3 で本格化) | Merge 可否判定 |
| `pr-draft-policy.md` | 既存 (A3 で本格化) | Draft → Ready 昇格条件 |
| `spec-living-sync.md` | 既存 (A3 で本格化) | 実装中の仕様変更時の双方向同期 |

### ドキュメント表記

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `markdown.md` | 既存 (A6 で本格化) | Markdown 表記規約 |
| `template-language.md` | **B0 雛形** | 全テンプレ Markdown は日本語 (ADR 0027) |

### 同期 / Backend

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `sync-job.md` / `sqlite-data-file.md` / `cloud-run-deploy.md` / `removed-modules.md` / `backend-auth.md` | 既存 (Phase C で本格化) | Backend / 同期 / 撤去モジュールの規約 |

### セキュリティ / 個人情報

| ファイル | 状態 | 主な責務 |
|---|---|---|
| `pii.md` | **B0 雛形** | PII の定義・最小化・redaction 強制 |
| `secrets.md` | **B0 雛形** | Secrets 管理 (`.env` / GitHub Secrets / Secret Manager) |
| `db-protection.md` | **B0 雛形** | `users.db` の commit / イメージ焼込み禁止、R2 private |
| `no-firebase.md` | A3 で導入 | Firebase 系 import 禁止 (旧 `firebase-boundary.md` 改名予定) |
| `cloudflare-pages.md` | C7 で本格化 | Cloudflare Pages デプロイ規約 |
| `r2-litestream.md` | C5 で本格化 | Litestream replicate / restore、R2 endpoint |

## 関連

- `docs/harness/plan.md` §5.0.3 (rules 一覧)
- `CLAUDE.md` §lookup-table (編集対象 ⇄ rules の対応)
