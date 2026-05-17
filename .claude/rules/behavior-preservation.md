---
id: rules-behavior-preservation
title: リファクタ時の振る舞い維持原則
status: skeleton
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "core/**/*.kt"
  - "composeApp/**/*.kt"
  - "docs/design/inventory/**"
related_plan: docs/harness/plan.md §3.9 / ADR 0023
related_adrs:
  - ADR-0023
---

# behavior-preservation.md — リファクタ時の振る舞い維持原則

> Phase C で大規模リファクタ (EPIC-001 〜 EPIC-006) を行う際に、ユーザーから見える振る舞い
> (UI / API レスポンス / 状態遷移) を変えないことを保証する規約。
> `code-reviewer` の visual-regression と spec-conformance の二本柱で検証する。

## 二本柱

| 検証軸 | 担当 aspect | 担保するもの |
|---|---|---|
| **Visual regression** | `code-reviewer` visual-regression aspect (A10 完了後 enable) | UI 見た目の不変性 (Roborazzi baseline diff) |
| **Spec conformance** | `code-reviewer` spec-conformance aspect | 機能仕様の不変性 (`@Spec` + Acceptance criteria が全て pass) |

## リファクタの定義 (Phase C)

「**振る舞いを変えないコード整理**」のみリファクタと呼ぶ。以下はリファクタではなく feature 追加とみなす:

- API レスポンスのフィールド追加・削除
- UI レイアウトの変更 (色 / 余白 / フォントサイズ含む)
- 状態遷移の追加・削除
- エラーメッセージの変更

これらが必要なときは `feat` / `fix` で別 Plan / Epic を立てる。

## Phase C リファクタの順序

1. **A10 で凍結**: DESIGN.md + UI Inventory + Roborazzi baseline で現状を記録
2. **Phase C で構造変更**: リファクタ Plan / Epic を実装
3. **検証**: `./gradlew check` + `./gradlew verifyRoborazziDebug` + `code-reviewer` の visual-regression / spec-conformance aspect が全て green
4. **PR description に "Behavior Preservation 証拠" を必須化** (§4.8.3 refactor type)

## 例外的に baseline 更新を許可するケース

- A10 凍結時点でバグがあった UI の修正 (PR description に修正理由を明記、別 Plan で実施)
- アクセシビリティ改善 (contentDescription 追加等、visual diff 無し)
- パフォーマンス改善 (アニメーション最適化等、目視で同じに見える)

これらは visual-regression が false-positive を出すため、PR description で意図的更新と明示 + human approve 必須。

## Gotchas

- **A10 完了前のリファクタは behavior preservation 検証ができない**ため、Phase C 着手前提条件として A10 完了が必須 (R-22)
- spec-conformance も A10 と並行して `docs/specifications/basic/SPEC-NNN-*.md` の逆生成 (A9) が必要
- リファクタで「ついでに API レスポンスを変える」を絶対許可しない (PR を分割する)

## 関連

- ADR 0023 (UI 凍結三本柱)
- `docs/harness/plan.md` §3.9 / §6.2 A9 / A10 / §6.3 Phase C
- `.claude/rules/{ui-snapshot,ui-inventory,design-tokens,code-reviewer-aspects}.md`
