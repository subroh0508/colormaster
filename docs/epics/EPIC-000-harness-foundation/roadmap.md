---
id: roadmap-EPIC-000
title: EPIC-000 ハーネス基盤構築 ロードマップ
status: living
last_updated: 2026-05-17
source_epic: EPIC-000
---

# EPIC-000 ロードマップ

> **5 行以内 summary**: EPIC-000 (ハーネス基盤構築) 配下の PR 進捗トラッカー。
> B0 + A1〜A10 を対象とし、Plan 単体は列挙しない (R-34)。`roadmap-tracker` Skill が
> 自動更新する。`docs/harness/roadmap.md` (全体ロードマップ) と整合する。
> Open Questions / 障壁 / 着手順変更履歴は append-only。

## 項目一覧

| ID | タイトル | status | expected_modules | 完了根拠 |
|---|---|---|---|---|
| **B0** | ブートストラップ PR (本 PR) | in-progress | `.claude/**`, `docs/**`, `.github/**`, `scripts/**`, `DESIGN.md`, `CLAUDE.md`, `AGENTS.md` | (本 PR で更新) |
| **A1** | ADR 0001-0027 一括起草 | proposed | `docs/adr/**` | — |
| **A2** | `.claude/rules/*` 全ファイル本格化 + docs 全面拡充 | proposed | `.claude/rules/**`, `docs/{architecture,api,security,requirements,specifications,runbooks}/**` | — |
| **A3** | 専用 Skill 群実装 PR | proposed | `.claude/skills/**` | — |
| **A4** | ローカルポーリング機構の本格化 | proposed | `.claude/skills/pr-poller/**`, `.claude/rules/harness-meta-criteria.md` | — |
| **A5** | 不要モジュール撤去 (Firebase 系) | proposed | `js/**`, `kotlin-js-store/**`, `public/**`, `core/network/{auth,firestore}/**`, `firebase.json`, `.firebaserc`, `web-build-and-deploy.yml` | — |
| **A6** | Lint / Format 基盤 + trufflehog | proposed | `build.gradle.kts`, `plugins/**`, `.github/workflows/**` | — |
| **A7** | 三層テスト品質基盤 (Kover + Konsist Spec coverage + PITest) | proposed | `build.gradle.kts`, `plugins/**` | — |
| **A8** | im@sparql ローカル Docker (Fuseki) | proposed | `docker-compose.yml`, `docs/runbooks/local-imasparql.md`, `backend/**` | — |
| **A9** | 既存コード baseline 記録 + Spec coverage 適用準備 | proposed | `docs/specifications/basic/**`, `core/**`, `feature/**` (Spec 逆生成) | — |
| **A10** | UI/UX 現状記録 EPIC (DESIGN.md + UI Inventory + Roborazzi baseline) | proposed | `DESIGN.md`, `docs/design/inventory/**`, `feature/**` (Preview 追加) | — |

## 完了根拠

| ID | PR 番号 | マージ日 | 主要ファイル |
|---|---|---|---|

(本 PR (B0) マージ時に B0 行を `completed` に更新し、PR 番号 + マージ日 + 主要ファイルを追記)

## 着手順とブロック関係

```mermaid
gantt
    title EPIC-000 着手順
    dateFormat YYYY-MM-DD
    B0 :b0, 2026-05-17, 7d
    A1 :a1, after b0, 14d
    A2 :a2, after b0, 14d
    A3 :a3, after a1, 14d
    A4 :a4, after a3, 7d
    A5 :a5, after a3, 7d
    A6 :a6, after a3, 14d
    A7 :a7, after a6, 14d
    A8 :a8, after a3, 14d
    A9 :a9, after a7, 14d
    A10 :a10, after a7, 21d
```

## 保留中の意思決定・不明事項 (Open Questions)

| 起票日 | 内容 | 暫定方針 | 解決状態 |
|---|---|---|---|

## 技術的障壁と回避策 (Blockers and Workarounds)

| 起票日 | 障壁 | 回避策 | 解決日 | 解決方法 |
|---|---|---|---|---|

## 着手順変更履歴 (append-only)

| 日付 | 変更内容 | 理由 |
|---|---|---|
| 2026-05-17 | 初期ロードマップ起草 (B0 PR 内で) | EPIC-000 起票と同時 |

## 次の推奨着手 (並行実装観点)

B0 マージ後の手動更新例:

1. **A1 (ADR 0001-0027 一括起草)** — `docs/adr/**` のみ touch
2. **A2 (rules 本格化 + docs 拡充)** — A1 と並行可 (`docs/adr/` と `docs/{architecture,api,...}/` で touch ファイル分離)
3. A3 着手は A1 完了後 (ADR を参照する Skill 群実装)

## 関連

- `docs/epics/EPIC-000-harness-foundation/README.md`
- `docs/harness/roadmap.md` (全体ロードマップ)
- `docs/harness/plan.md` §6.1-6.2
- `.claude/rules/roadmap.md`
