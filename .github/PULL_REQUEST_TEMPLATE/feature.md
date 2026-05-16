---
type: feature
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

## 変更内容

| 区分 | パス | 変更 |
|---|---|---|
| feat | `feature/<画面>/<Class>.kt` | <要約> |
| test | `feature/<画面>/<Class>Spec.kt` | <観点> |

## 受け入れ基準 (AC)

- [ ] AC-NN: <検証手段>

## テスト

- [ ] `./gradlew check` グリーン
- [ ] 三層指標差分: line/branch 差分 100%、Spec coverage 差分 100%、mutation score +N%
- [ ] Roborazzi baseline 比較グリーン or 承認済 (UI 変更時)

## スクリーンショット (UI 変更時)

<Before / After を貼付、または `docs/design/inventory/screenshots/` への参照>

## 新規 API エンドポイント (該当時)

- [ ] `docs/api/colormaster-api.yaml` を更新済 (リクエスト/レスポンス JSON 定義)
- [ ] `docs/api/{auth,idols,me}.md` のいずれかを更新済 (使い方 / 設計判断 / 例外パターン)

## レビュー観点

<重点的に見てほしい箇所、code-reviewer の特定 aspect で注意すべき点>

## チェックリスト

- [ ] `.claude/rules/` の関連規約を確認した
- [ ] 要件 (REQ-NNN) / 基本設計 (SPEC-NNN-basic) / 詳細設計 (SPEC-NNN-detail) を起票・更新済
- [ ] `@Spec("SPEC-NNN-N")` annotation 付きテストを追加 (A7 以降)
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン)
- [ ] roadmap-tracker で完了根拠登録 (Epic 配下 PR の場合)
