---
id: EPIC-000
title: ハーネス基盤構築 (B0 + Phase A)
status: in-progress
created_at: 2026-05-17
completed_at: null
expected_modules:
  - .claude/**
  - docs/**
  - .github/**
  - scripts/**
related_adrs: []
related_specs: []
---

# ハーネス基盤構築 (B0 + Phase A)

> **5 行以内 summary**: AI 駆動の開発ハーネス (Skill / rules / docs / MCP / Git hook /
> PR テンプレート / Roborazzi 等) を段階的に構築する Epic。B0 (本 PR) で雛形を配置、
> A1〜A10 で本格化。完了時点で Skill ループ (Spec Gen → Implementation → Evaluation
> → Merge → Retrospection → Meta) が自律稼働し、Phase C の本格運用に入る。

## 目的

- `docs/harness/plan.md` §6.1-6.2 (B0 + Phase A) の達成
- Phase C 本格運用の前提条件である「計測可能な状態 + 差分カバレッジゲート稼働 + im@sparql ローカル + DESIGN.md + UI Inventory + Roborazzi baseline」を確立

## 背景

- 既存リポジトリにはハーネス・ADR・要件・基本/詳細設計が未整備
- AI 駆動でセルフ改善ループを回すには rules / Skill / docs / MCP / Git hook / PR テンプレートが必要
- 一度に全機能実装は不可能 → B0 で骨格、Phase A で本格化、Phase C で実装と並行運用

## スコープ

### 含む

- B0: 雛形配置 (本 PR で実施)
- A1: ADR 0001-0027 一括起草
- A2: `.claude/rules/*` 全ファイル本格化 + `docs/` 全面拡充
- A3: 専用 Skill 群実装 (`feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` / `adr-author` / `harness-meta` ＋ `harness-bootstrap` の archived 化)
- A4: ローカルポーリング自動化 (`CronCreate` / `ScheduleWakeup`)
- A5: 不要モジュール撤去 (Firebase 系)
- A6: Lint / Format 基盤 + secret-scan workflow
- A7: 三層テスト品質基盤 (Kover + Konsist Spec coverage + PITest)
- A8: im@sparql ローカル Docker (Fuseki)
- A9: 既存コード baseline 記録 + Spec coverage 適用準備
- A10: UI/UX 現状記録 (DESIGN.md + UI Inventory + Roborazzi baseline)

### 含まない (Phase C へ持ち越し)

- Cloud Run / Cloudflare Pages 本番デプロイ (C7)
- フィーチャ再編 / Decompose 撤去 (C3 = EPIC-001)
- Backend 強化 / GIS 認証 / Litestream + R2 (C5 = EPIC-003)
- iOS / wasmJs ターゲット (C8 = EPIC-005 / C9 = EPIC-006)
- i18n 移植 (C4 = EPIC-002)
- 同期パイプライン (C6 = EPIC-004)

## 構成 PR (進捗)

| PR # | タイトル | status | merge 日 | 主要ファイル |
|---|---|---|---|---|
| (本 PR) | `B0: ブートストラップ` (本 PR の番号は merge 時に追記) | in-progress | — | `.claude/**`, `docs/**`, `.github/**`, `scripts/**`, `DESIGN.md`, `CLAUDE.md`, `AGENTS.md` |

A1 以降は Phase A の各 Plan / Epic で PR 起票時に追記。

## 受け入れ基準 (Epic 全体)

- [ ] B0 マージ済み: 本 Epic README に PR # 追記
- [ ] A1〜A10 マージ済み: 各 PR 番号 + マージ日が完了根拠として記録
- [ ] `./gradlew check` がグリーン (A6 完了時点で基盤、A7 完了で三層指標稼働)
- [ ] `koverHtmlReport` / `koverXmlReport` 出力可能 + 差分カバレッジゲート稼働 (A7)
- [ ] `docker compose up imasparql` でローカル Fuseki が起動可能 (A8)
- [ ] `verifyRoborazziDebug` グリーン + DESIGN.md + UI Inventory 完備 (A10)
- [ ] `harness-bootstrap` Skill が archived 済み、全専用 Skill が稼働 (A3 完了)
- [ ] KPT ループが動作している (`pr-retrospective` 自動起動 + `harness-meta` 改修 PR 実績)

## 関連

- `docs/harness/plan.md` §6.1-6.2 (B0 + Phase A)
- `roadmap.md` (EPIC-000 配下の PR 進捗トラッカー)
- `open-questions.md`
- `decisions.md`
- `progress.md`
- 全 ADR (0001-0027、A1 で一括起草)
