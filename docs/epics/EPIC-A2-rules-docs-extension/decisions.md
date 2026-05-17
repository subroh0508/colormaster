---
id: decisions-EPIC-A2
title: EPIC-A2 細粒度決定の記録
status: living
last_updated: 2026-05-17
source_epic: EPIC-A2
---

# EPIC-A2 細粒度決定の記録

> **5 行以内 summary**: EPIC-A2 内で発生した細粒度の意思決定の記録。ADR に昇格するほどでは
> ない判断 (rule の分割粒度 / docs サブツリー間の責務境界 / テンプレート文言の最終決定等) を
> 蓄積。ADR 起票基準を満たすものは `docs/adr/` に昇格してリンクする。

## 決定一覧

| 決定日 | 決定内容 | 背景 | 影響範囲 | 関連 ADR (昇格時) |
|---|---|---|---|---|
| 2026-05-17 | A2 を 5 PR (A2-1〜A2-5) に分割 | B0 (96 files / +5408 行) のレビュー負荷上限に近く、A2 全体は B0 を超える規模が想定されるため。A1 レトロ Try「巨大 PR の aspect 並列 review における入力分割」と整合 | EPIC-A2 全体 | — (Epic 内分割でありアーキ判断ではない) |
| 2026-05-17 | A2-1 で `template-language.md` の paths を削除し真の常時ロード化 | A1 レトロ Problem #2 (paths 矛盾) の解消。rules-index / CLAUDE.md「常時ロード (paths 未設定)」宣言と整合 | `.claude/rules/template-language.md` / `rules-index.md` / `CLAUDE.md` | — |
| 2026-05-17 | A2-2 で `firebase-boundary.md` と `no-firebase.md` を **二段運用** で配置 | `firebase-boundary.md` は B0 rules-index に既出 (旧 Firebase 撤去用)、`no-firebase.md` は plan.md A3 で言及。両者の責務分担は (a) `firebase-boundary.md`: 既存 import 検出、Konsist の Kotlin source パターンで強制、(b) `no-firebase.md`: 新規追加禁止、Skill 起草時の事前ガード。改名は A3 で再評価 | `.claude/rules/{firebase-boundary,no-firebase}.md` | — |
| 2026-05-17 | A2-1 で `docs/adr/README.md` の ADR 索引を表形式 (ID / タイトル / 関連 rule / 起票根拠 §4.5 該当項目 / 状態) に拡充 | A1 PR #119 で ADR 0001-0027 が実体化済み。索引は人間レビューと AI 検索の双方を支援 | `docs/adr/README.md` | — |
