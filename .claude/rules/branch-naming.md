---
id: rules-branch-naming
title: ブランチ命名規約 (feature / epic / harness / chore / fix / docs)
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/implementation-workflow/**"
  - "scripts/install-git-hooks.sh"
related_adrs:
  - ADR-0017
  - ADR-0018
related_plan: docs/harness/plan.md §4.7 / §5.4.2
---

# branch-naming.md — ブランチ命名規約

> 本リポジトリで作成する全ブランチは `<種別>/<ID or purpose>-<slug>` 形式で命名する。
> `implementation-workflow` Phase 0 (`git worktree add` + `-b`) でこの規約に従い
> worktree path も自動導出する (`branch-slug` への変換ルール参照)。

## 種別 prefix 一覧

| prefix | 用途 | ID / purpose | スラグ例 |
|---|---|---|---|
| `feature/` | 単一機能 Plan (`type: feature-request`) / Epic 配下 PR | `PLAN-NNN-<slug>` / `EPIC-NNN-<slug>-pr-NN` | `feature/PLAN-007-add-search` / `feature/A2-3-rules-process` |
| `epic/` | Epic 配下 PR (旧形式、`feature/EPIC-NNN-*` への移行を推奨) | `EPIC-NNN-<slug>-pr-NN` | `epic/EPIC-001-feature-restructure-pr-01` |
| `fix/` | バグ修正 Plan (`type: bug-fix`) | `PLAN-NNN-<slug>` または `<slug>` | `fix/PLAN-012-search-flaky` |
| `refactor/` | リファクタ Plan (`type: refactor`) | `PLAN-NNN-<slug>` | `refactor/PLAN-020-extract-repo` |
| `docs/` | docs 単独 Plan (`type: docs`) | `PLAN-NNN-<slug>` | `docs/PLAN-005-adr-bootstrap` |
| `harness/` | ハーネス改修・レトロ起票・ロードマップ mirror | `<purpose>` (自由形) | `harness/learnings-batch-2026-W20` / `harness/roadmap-mirror-a2-4` |
| `chore/` | 軽微な雑務 (`.gitignore` 更新、設定ファイル等) | `<purpose>` (自由形) | `chore/install-git-hooks` |
| `dependency/` | Renovate 系 (将来検討、現状は `renovate/*` が Renovate 側で自動付与) | — | `renovate/koin-major` (Renovate 自動) |

## ID と slug の構築規約

- **ID 部**: `PLAN-NNN` / `EPIC-NNN` / フェーズ ID (`A2-3`) を **大文字 + ハイフン** で記述、ゼロパディングを保持
- **slug 部**: kebab-case (小文字 + ハイフン)、動詞は省略可、要件のキーワードを含める
- **長さ**: ブランチ名全体で **60 文字以内推奨、80 文字 hard limit** (GitHub UI 表示の閾値)
- **禁止文字**: 半角スペース / `_` / 大文字英字 (ID 部除く) / `\` / `:` / `?` / `*` / `[` / `]` / 日本語

例:

| ✅ OK | ❌ NG |
|---|---|
| `feature/PLAN-007-add-search-by-brand` | `feature/PLAN-007-Add Search By Brand` (空白 / 大文字) |
| `epic/EPIC-001-feature-restructure-pr-03` | `epic/epic-001-feature-restructure-pr-3` (ID 小文字 / ゼロパディングなし) |
| `harness/roadmap-mirror-a2-4` | `harness/roadmap_mirror_a2_4` (アンスコ) |
| `feature/A2-3-rules-process` | `feature/A2.3-rules-process` (ドット) |

## worktree path 規約 (slug 導出)

- worktree path: `../<repo-name>-worktrees/<branch-slug>`
- `<branch-slug>` はブランチ名のスラッシュをハイフンに置換した文字列

| ブランチ名 | worktree path |
|---|---|
| `feature/PLAN-007-add-search` | `../colormaster-worktrees/feature-PLAN-007-add-search` |
| `epic/EPIC-001-feature-restructure-pr-01` | `../colormaster-worktrees/epic-EPIC-001-feature-restructure-pr-01` |
| `harness/roadmap-mirror-a2-4` | `../colormaster-worktrees/harness-roadmap-mirror-a2-4` |
| `feature/A2-3-rules-process` | `../colormaster-worktrees/feature-A2-3-rules-process` |

詳細は `.claude/rules/implementation-workflow.md` Phase 0 参照。

## Plan / Epic ⇄ ブランチ ⇄ PR の対応

| Plan / Epic 種別 | ブランチ prefix | PR テンプレ | commit type |
|---|---|---|---|
| Plan `type: feature-request` | `feature/PLAN-NNN-*` | `feature.md` | `feat` |
| Plan `type: bug-fix` | `fix/PLAN-NNN-*` | `bugfix.md` | `fix` |
| Plan `type: refactor` | `refactor/PLAN-NNN-*` | `refactor.md` | `refactor` |
| Plan `type: dependency-upgrade` | Renovate 自動 (`renovate/*`) | `dependency-upgrade.md` | `chore` / `build` |
| Plan `type: docs` | `docs/PLAN-NNN-*` | `docs.md` | `docs` |
| Epic 配下 PR | `feature/EPIC-NNN-*-pr-NN` または `feature/<phase-id>-<slug>` | Epic 性質に応じて (`feature.md` / `harness.md` 等) | Epic 性質に応じて |
| ハーネス改修 / レトロ起票 / mirror | `harness/<purpose>` | `harness.md` | `feat` / `docs` (scope `harness`) |
| 雑務 | `chore/<purpose>` | `feature.md` を流用 (chore 専用テンプレなし) | `chore` |

## 自動検証 (A6 で導入予定)

- **Git hook (`scripts/install-git-hooks.sh` 拡張)**: `pre-push` で現在ブランチが正規表現 `^(feature|fix|refactor|docs|chore|harness|epic|renovate|master|main)/.+$` (または `master` / `main` 自体) に合致するかチェック
- **GitHub Actions**: PR open 時に `head_ref` を検証、`branch-naming.md` の prefix allow リストに合致しない場合は warning コメント (A6 / detekt 導入と同時)
- **JetBrains MCP**: rename refactoring で生成されるブランチ名候補に上記 prefix を提案 (将来検討)

## Gotchas

- **`master` / `main` 直 push 禁止**: 全変更は PR 経由 (R-15 と整合、GitHub branch protection 併用予定 A6)
- **長すぎるブランチ名**: 80 文字超過時は slug 部を短縮、PR description に full slug を記載
- **ブランチ命名と Conventional Commits type の整合**: `feature/` ↔ `feat`、`fix/` ↔ `fix`、`refactor/` ↔ `refactor`、`docs/` ↔ `docs`、`chore/` ↔ `chore`、`harness/` は `feat(harness)` / `docs(harness)` のいずれかを subject に明示
- **Renovate ブランチは Renovate 側で自動命名** (`renovate/<package>-<version>`)、本 rule の prefix allow リストに含める
- **EPIC 配下 PR のブランチは `feature/EPIC-NNN-*-pr-NN` または `feature/<phase-id>-<slug>`** (実例: EPIC-A2 配下は `feature/A2-1-*` / `feature/A2-2-*` / ... を採用)
- **mirror PR (`roadmap-tracker` Phase 8 自動同期の手動代替) は `harness/roadmap-mirror-<phase-id>`** を慣例 (A2-2 / A2-4 / A2-5 mirror の実績)

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、ブランチ運用の位置付け)
- ADR 0018 (`implementation-workflow` 10 フェーズ設計)
- `docs/harness/plan.md` §4.7 / §5.4.2
- `.claude/rules/{implementation-workflow,commit-message,plan,epic,pr-template}.md`
- `scripts/install-git-hooks.sh` (Git hook 実装、A6 で `pre-push` 拡張予定)
