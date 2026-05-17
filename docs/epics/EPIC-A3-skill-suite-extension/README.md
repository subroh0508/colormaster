---
id: EPIC-A3
title: 専用 Skill 群実装 (feature-request / bug-fix / refactor / dependency-upgrade / adr-author / harness-meta / harness-evolution の新規完成 + 6 Skill 本格化)
status: in-progress
created_at: 2026-05-18
completed_at: null
expected_modules:
  - .claude/skills/**
  - .claude/rules/**
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0025
related_specs: []
---

# EPIC-A3: 専用 Skill 群実装

> **5 行以内 summary**: B0 で配置した Skill 群の skeleton と `harness-bootstrap` 汎用 Skill を、
> ADR 0001-0027 + 本格化された `.claude/rules/` 54 件 + ORCH-1 (orchestrator) の基盤の上に
> **13 Skill** (新規完成 7 + 既存アップグレード 6) として本格実装する Epic。各 Skill は
> 専用 PR で実装し、グループ単位で並列実行 (Group 1-3) する。完了後に `harness-bootstrap`
> を `.claude/skills/archived/` へ移動し、汎用 Skill 経由の起票を終了する。

## 目的

- `docs/harness/plan.md` §6.2 A3 の完了条件達成
- B0 (PR #117) で配置した Skill skeleton 群 (`implementation-workflow` / `code-reviewer` / `pr-retrospective` / `pr-poller` / `ui-snapshot` / `roadmap-tracker`) を本格運用可能な状態にアップグレード
- 専用 Skill 7 件 (`feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` / `adr-author` / `harness-meta` / `harness-evolution`) を新規完成
- 完了後に `harness-bootstrap` を `archived/` へ移動 (汎用 Skill 経由起票の終了)、CLAUDE.md / `rules-index.md` 参照を削除
- A1 / A2-1〜A2-6 / ORCH-1 各レトロの未消化提案 (harness-meta フィードバック) を実装中に順次消化

## 背景

- B0 (PR #117) で Skill 12 ファイル + rule 19 ファイルの skeleton 配置
- A1 (PR #119) で ADR 0001-0027 起草 → Skill 実装時に参照可能
- A2 (PR #121 / #123 / #125 / #126 / #129 / #135) で `.claude/rules/` 53 件を本格化 → Skill 実装の規約基盤完成
- ORCH-1 (PR #144) で orchestrator Skill + `.claude/rules/orchestrator-criteria.md` を配置 → cmux 並列 self-merge orchestration 基盤確立
- A3 着手の前提 (ADR + rules + orchestrator) が揃ったため、専用 Skill 群の本格実装フェーズへ移行

## スコープ

### 含む

#### 新規完成 (7 Skill)

| Skill | 責務 |
|---|---|
| `feature-request` | 要件・仕様生成 + Plan / Epic 起票 (実装はしない) |
| `bug-fix` | 再現・root cause・仕様補強 + Plan 起票 |
| `refactor` | 影響分析 + Plan / Epic 起票 |
| `dependency-upgrade` | Renovate PR 検出後の処理 (`pr-poller` から起動) |
| `adr-author` | ADR テンプレ起草 + 関連 ADR リンク |
| `harness-meta` | 改修 PR 起票 + 元 learning ファイルへの feedback 追記 (内部 KPT) |
| `harness-evolution` | 外部研究駆動の改修提案 (手動起動のみ、Context7 MCP / ホワイトリスト) |

#### 既存アップグレード (6 Skill)

| Skill | 本格化内容 |
|---|---|
| `implementation-workflow` | Phase 0-9 完全実装 (Phase 0 worktree / Phase 9 cleanup / fix loop 上限 3 / spec-living-sync / merge-readiness) |
| `code-reviewer` | 8 aspect の binary eval checklist + Coordinator 完全実装 (`visual-regression` / `design-tokens` は A10 完了後 enable) |
| `pr-retrospective` | learning ファイル生成 + `harness-meta` フィードバック追記の本格実装 |
| `pr-poller` | Renovate ラベル PR 検出 + `dependency-upgrade` 起動を追加 (3 系統起動経路 + lock ファイル排他制御) |
| `ui-snapshot` | A10 完了後本格運用 (本 Epic では skeleton 拡張のみ、本格運用は A10) |
| `roadmap-tracker` | plan.md / Epic 走査 (Plan 単体対象外) + 自動起動フック完全実装 (`epic-author` / `implementation-workflow` Phase 8 から起動) |

#### 完了後の archived 化

- `harness-bootstrap` を `.claude/skills/archived/` へ物理移動
- `CLAUDE.md` ハーネス概要表から `harness-bootstrap` の行を削除
- `.claude/rules/rules-index.md` の関連表記更新

### 含まない (後続フェーズに持ち越し)

- ローカルポーリング機構の本格化 (A4) — `pr-poller` の `CronCreate` / `ScheduleWakeup` 自動設定、`harness-meta-criteria.md` の起動閾値設定は A4 で本格化
- 不要モジュール撤去 (A5) — `js/app` / `kotlin-js-store` / Firebase / `web-build-and-deploy.yml` 等の物理撤去
- Lint / Format 基盤 (A6) — Spotless / ktlint / detekt / Konsist / markdownlint-cli2 / Gradle カスタムタスク / trufflehog 統合
- 三層テスト品質基盤 (A7) — Kover / Konsist Spec coverage / PITest
- im@sparql ローカル Docker 環境 (A8) — Fuseki + Testcontainers
- UI/UX 現状記録 (A10) — Roborazzi + DESIGN.md + UI Inventory、code-reviewer `visual-regression` / `design-tokens` aspect の enable
- 実装コード変更 — `core/**` / `feature/**` / `build.gradle.kts` 等は触らない (本 Epic は Skill / rules のみ)

## 構成 PR (進捗)

| PR # | タイトル | status | merge 日 | 主要ファイル |
|---|---|---|---|---|
| [#146](https://github.com/subroh0508/colormaster/pull/146) (A3-0) | docs(harness): EPIC-A3 (専用 Skill 群実装 Epic) を起票 | in-progress | — | `docs/epics/EPIC-A3-skill-suite-extension/{README,roadmap,open-questions,decisions,progress}.md` / `docs/epics/INDEX.md` / `docs/harness/roadmap.md` (A3 in-progress) |
| (A3-1) | feat(harness): A3-1 feature-request Skill 完成 | proposed | — | `.claude/skills/feature-request/SKILL.md` (新規) |
| (A3-2) | feat(harness): A3-2 bug-fix Skill 完成 | proposed | — | `.claude/skills/bug-fix/SKILL.md` (新規) |
| (A3-3) | feat(harness): A3-3 refactor Skill 完成 | proposed | — | `.claude/skills/refactor/SKILL.md` (新規) |
| (A3-4) | feat(harness): A3-4 adr-author Skill 完成 | proposed | — | `.claude/skills/adr-author/SKILL.md` (新規) |
| (A3-5) | feat(harness): A3-5 harness-meta Skill 完成 | proposed | — | `.claude/skills/harness-meta/SKILL.md` (新規) |
| (A3-6) | feat(harness): A3-6 harness-evolution Skill 完成 | proposed | — | `.claude/skills/harness-evolution/SKILL.md` 本格化 |
| (A3-7) | feat(harness): A3-7 dependency-upgrade Skill 完成 | proposed | — | `.claude/skills/dependency-upgrade/SKILL.md` (新規) |
| (A3-8) | feat(harness): A3-8 implementation-workflow Phase 0-9 完全実装 | proposed | — | `.claude/skills/implementation-workflow/SKILL.md` 本格化 |
| (A3-9) | feat(harness): A3-9 code-reviewer 8 aspect binary checklist + Coordinator | proposed | — | `.claude/skills/code-reviewer/SKILL.md` 本格化 |
| (A3-10) | feat(harness): A3-10 pr-retrospective learning + harness-meta フィードバック | proposed | — | `.claude/skills/pr-retrospective/SKILL.md` 本格化 |
| (A3-11) | feat(harness): A3-11 pr-poller Renovate 検出 + 3 系統起動経路 | proposed | — | `.claude/skills/pr-poller/SKILL.md` 本格化 |
| (A3-12) | feat(harness): A3-12 roadmap-tracker plan.md / Epic 走査 + 自動起動フック | proposed | — | `.claude/skills/roadmap-tracker/SKILL.md` 本格化 |
| (A3-13) | feat(harness): A3-13 ui-snapshot skeleton 拡張 (A10 で本格運用) | proposed | — | `.claude/skills/ui-snapshot/SKILL.md` skeleton 拡張 |
| (A3-14) | refactor(harness): A3-14 harness-bootstrap を archived/ へ移動 + 参照削除 | proposed | — | `.claude/skills/harness-bootstrap/` → `.claude/skills/archived/harness-bootstrap/`、`CLAUDE.md`、`.claude/rules/rules-index.md` |

> **注**: 構成 PR は A3-1 〜 A3-14 の 14 PR に分割。グループ並列実行 (Group 1-3) の詳細は `decisions.md` 参照。本 Epic 起票 PR (A3-0) は EPIC ディレクトリ + roadmap / INDEX 更新のみ、Skill 本体は touch しない。

## 受け入れ基準 (Epic 全体)

- [ ] AC-1: A3-1 〜 A3-14 全 PR がマージ済み、各 PR 番号 + マージ日が `roadmap.md` の完了根拠表に記録
- [ ] AC-2: 13 Skill (新規 7 + 既存 6) が全て `Anthropic Complete Guide` 準拠の本格実装になっている (skill-creator 100-point rubric で 80 点以上)
- [ ] AC-3: `harness-bootstrap` が `.claude/skills/archived/harness-bootstrap/` へ移動済み、`CLAUDE.md` ハーネス概要表 / `.claude/rules/rules-index.md` から参照削除済み
- [ ] AC-4: `code-reviewer` 8 aspect のうち `visual-regression` / `design-tokens` 以外の 6 aspect が binary checklist + Coordinator で完全実装 (A10 完了後に残り 2 aspect enable)
- [ ] AC-5: `implementation-workflow` Phase 0-9 が完全実装されており、本 Epic 配下の A3-1 以降の PR で実際に Phase 0-9 を回せている (ドッグフード達成)
- [ ] AC-6: `pr-poller` が 3 系統起動経路 (起動時 / `CronCreate` / `ScheduleWakeup`) + lock ファイル排他制御を実装 (本格自動化は A4)
- [ ] AC-7: `roadmap-tracker` が `epic-author` 起票直後 + `implementation-workflow` Phase 8 マージ直後の自動起動フックを実装、`docs/harness/roadmap.md` / `docs/epics/<id>/roadmap.md` を自動更新
- [ ] AC-8: A1 / A2-1〜A2-6 / ORCH-1 の各 learning ファイルで `📝 harness-meta フィードバック` セクションに本 Epic で消化した提案を記録 (harness-meta Skill 完成後)

## 関連

- 関連 ADR: ADR-0017 (ローカルポーリング駆動) / ADR-0018 (Skill 駆動 KPT ループ) / ADR-0025 (Skill 作成は `example-skills:skill-creator` 経由)
- 関連 Phase: §6.2 A3 (本 Epic) / §6.2 A4 (ローカルポーリング機構本格化、本 Epic 完了が前提)
- `docs/harness/plan.md` §5.3 (Skill の責務) / §6.2 A3 (1535 行)
- `docs/harness/roadmap.md` (全体ロードマップ、A3 行)
- `roadmap.md` (本 Epic 配下のロードマップ)
- `open-questions.md` (未解決事項)
- `decisions.md` (分割方針 + 並列グルーピング)
- `progress.md` (時系列進捗ログ)
