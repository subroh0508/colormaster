---
type: refactor
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<1-3 行で「何を / なぜ」 — リファクタは「振る舞いを変えないコード整理」のみ>

## 関連

- Plan: PLAN-NNN
- Epic: EPIC-NNN (該当時)
- ADR: ADR-NNNN (該当時)

## 変更内容

| 区分 | パス | 変更 |
|---|---|---|
| refactor | `<file>` | <変更内容、構造変更のみ> |

## 影響範囲モジュール一覧

- ...
- ...

## Behavior Preservation 証拠

A10 完了後は以下が必須:

- [ ] **visual regression**: `./gradlew verifyRoborazziDebug` がグリーン (差分なし、または意図的更新を別 PR で実施済)
- [ ] **spec-conformance**: `code-reviewer` の spec-conformance aspect がグリーン
- [ ] **既存テスト全て pass**: 既存仕様の振る舞い不変

A10 前は以下で代替:

- [ ] 既存ユニットテスト + 手動検証で振る舞い不変を確認

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>

## テスト

- [ ] `./gradlew check` グリーン
- [ ] 三層指標差分: line/branch 差分 100%、Spec coverage 差分 100%、mutation score +N%
- [ ] Roborazzi baseline 比較グリーン (A10 完了後)

## レビュー観点

<構造変更が意図通りか、隠れた挙動変更がないか>

## チェックリスト

- [ ] `.claude/rules/{behavior-preservation,refactor 関連}.md` を確認した
- [ ] **API レスポンスフィールドの追加・削除なし** (あれば feat / fix の別 PR に分割)
- [ ] **UI レイアウト変更なし** (あれば feat の別 PR に分割)
- [ ] **状態遷移の追加・削除なし**
- [ ] **エラーメッセージ変更なし**
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン)
- [ ] roadmap-tracker で完了根拠登録 (Epic 配下 PR の場合)
