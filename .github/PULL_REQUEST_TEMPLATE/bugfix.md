---
type: bugfix
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN
- Epic: EPIC-NNN (該当時)
- ADR: ADR-NNNN (該当時)
- 関連 Issue: #N
- 関連 learning: `docs/harness/learnings/YYYY-MM-DD-pr-N.md` (該当時)

## 再現手順

1. ...
2. ...
3. (期待: ...、実際: ...)

## ルートコーズ

<バグの根本原因を `file_path:line` 参照付きで記述>

## 変更内容

| 区分 | パス | 変更 |
|---|---|---|
| fix | `<file>:<line>` | <要約> |
| test | `<TestFile>.kt` | リグレッションテスト |

## リグレッションテスト

- [ ] テスト ID: `<TestSpec>::test_<scenario>` (再現条件を最小化)
- [ ] `@Spec("SPEC-NNN-N")` 付与 (該当時)

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>
- [ ] 上記再現手順で再現しなくなった

## テスト

- [ ] `./gradlew check` グリーン
- [ ] 三層指標差分: line/branch 差分 100%、Spec coverage 差分 100%、mutation score +N%
  - **注**: A7 完了 (Kover + Konsist Spec coverage + PITest 導入) までは本セクションは **空欄のままで OK** (誤った `N/A` 記入や 0% 詐称を避ける、A1 レトロ Try 対応)。A7 以降は必達ゲート対象
- [ ] (UI 変更時) Roborazzi baseline 比較グリーン or 承認済
  - **注**: A10 完了 (Roborazzi 導入 + baseline 生成) までは UI 変更時もスクリーンショット添付のみで可

## レビュー観点

<同様のバグが他箇所に潜んでないか確認すべき箇所>

## チェックリスト

- [ ] `.claude/rules/` の関連規約を確認した
- [ ] ルートコーズが「設計起因」なら ADR 起票検討 (`.claude/rules/adr.md` 起票基準)
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン)
