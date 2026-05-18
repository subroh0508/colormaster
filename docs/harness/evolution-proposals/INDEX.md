---
id: evolution-proposals-index
title: ハーネス進化提案索引
status: living
last_updated: 2026-05-19
---

# ハーネス進化提案索引

> **5 行以内 summary**: `harness-evolution` Skill が外部研究・ベストプラクティスを取得して
> 生成する改善提案 (`YYYY-MM-DD.md`) の索引。手動起動のみ。1 実行 = 1 ファイル。
> 出典 URL + 引用日付 + 構造化改善案を必須とする (R-29)。
> 採用された提案は Plan / EPIC へリンク。

## 索引 (起票時に追記)

| 日付 | 主旨 | 提案数 | 採用 → 起票 |
|---|---|---|---|
| [2026-05-19](2026-05-19.md) | harness-meta / harness-evolution 改修 PR の 3 軸定量評価フレーム導入 (改善度 / 再現性 / 副作用) | 6 (`[rule]` × 4 + `[skill]` × 2) | Plan 起票推奨 (本提案 §採用提案、`harness/3-axis-eval-framework` ブランチ想定、起票時に PLAN リンク追記) |

## ファイル運用

- 1 実行 = 1 ファイル (`YYYY-MM-DD.md`)
- フォーマットは `.claude/rules/harness-evolution.md` 参照
- 出典 URL + 引用日付必須
- 重要案は `example-skills:skill-creator` 経由で Skill scaffold or Plan / EPIC 起票 (人間 approve 必須)

## 関連

- `.claude/rules/harness-evolution.md`
- `.claude/skills/harness-evolution/SKILL.md`
- ADR 0026
