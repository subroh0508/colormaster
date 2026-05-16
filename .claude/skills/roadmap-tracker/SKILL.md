---
name: roadmap-tracker
description: |
  plan.md と docs/epics/EPIC-NNN-*/ を入力に docs/harness/roadmap.md (全体) と
  docs/epics/<id>/roadmap.md (Epic 別) を生成・更新する。進捗・完了根拠・Open Questions・
  障壁・着手順変更履歴を一元管理し、並行実装容易性に基づく「次の推奨着手 (top-N)」を出力する。
  plan.md / Epic 本体への逆同期はしない (片方向ミラー)。Plan は 1 PR 完結のため対象外。
status: skeleton
phase: B0
related_plan: docs/harness/plan.md §5.3 / §6.2 A3
related_rules:
  - .claude/rules/roadmap.md
  - .claude/rules/template-language.md
  - .claude/rules/docs-structure.md
---

# roadmap-tracker (B0 骨格)

> 本ファイルは B0 で配置する **最小スケルトン**。本格実装は A3 で行う。

## 役割

1. 計画 Markdown (`docs/harness/plan.md` 等) を読み取り B0 / A1-A10 / C1-C10 等の項目を抽出
2. `docs/epics/EPIC-NNN-*/` を走査して frontmatter から ID / タイトル / status / **想定変更モジュール (`expected_modules`)** を読む (**`docs/plans/` は走査対象外**、Plan は 1 PR 完結のためロードマップ追跡しない)
3. `docs/harness/roadmap.md` または `docs/epics/<id>/roadmap.md` を更新。完了根拠は `gh pr view` で PR メタ情報を取得して PR 番号 + マージ日 + 主要ファイルパスを記録
4. **並行実装容易性に基づく次の推奨着手 (top-N) を出力**:
   - 依存関係 (Mermaid gantt の `after` 句) が解決済みであること
   - 現在 `in-progress` な項目の `expected_modules` 集合と **重複モジュールが少ない順** に並べ替え
   - 完全重複ゼロが複数あれば優先度 (must/should/could) で二次ソート
   - `expected_modules` 未記入は warning 出力
5. Open Questions / 障壁 / 着手順変更履歴は **append-only** で蓄積

## 自動起動フック

- `epic-author` の Epic 起票直後 (**Plan は対象外**)
- `implementation-workflow` Phase 8 (Merge 直後、Epic 配下 PR または B-A-C フェーズ項目に該当時のみ)
- `pr-poller` の pending-fetch 再走査

## 手動起動契機

「ロードマップ更新」「進捗可視化」「着手順入れ替え」「障壁記録」「保留事項追加」「次の推奨着手を出して」等の人間 / 他 Skill からの指示

## Gotchas

- **plan.md / Epic 本体への逆同期はしない** (片方向ミラー、R-34)。
- `gh pr view` 取得失敗時は `<!-- evidence:pending-fetch -->` コメントで暫定登録し、`pr-poller` が再走査する (R-35)。
- ステータス語彙: `proposed / in-progress / completed / blocked / abandoned`。
- 重大な運用方針変更時は §4.5 ADR 起票基準を再評価して格上げ検討 (R-36)。

## 関連

- `docs/harness/plan.md` §5.3 / §6.2 A3
- `.claude/rules/roadmap.md`
