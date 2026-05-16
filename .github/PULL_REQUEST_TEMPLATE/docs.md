---
type: docs
related_plan: PLAN-NNN
related_epic: EPIC-NNN
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<ドキュメント変更のみ。1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN (該当時)
- Epic: EPIC-NNN (該当時)
- ADR: ADR-NNNN (該当時)

## 更新 docs パス一覧

| パス | 変更内容 |
|---|---|

## 影響を受ける Skill / rule

| Skill / rule | 影響 |
|---|---|

## 受け入れ基準 (AC)

- [ ] frontmatter 必須キーを設定済
- [ ] 冒頭 5 行 summary を維持 (ADR 0027 / `.claude/rules/docs-structure.md`)
- [ ] 設計書 (`docs/{requirements,specifications}/**`) の場合: **コード断片が混入していない** (§4.6 / `docs-structure.md`)
- [ ] 日本語見出し (ADR 0027 / `.claude/rules/template-language.md`)
- [ ] 関連 ID 参照の実在 (Plan / Epic / ADR / Spec)

## テスト

- [ ] markdownlint-cli2 グリーン (A6 以降)
- [ ] Gradle カスタムタスクの docs 検証グリーン (A6 以降)

## レビュー観点

<読みやすさ、構造の整合性、他 docs との重複・矛盾はないか>

## チェックリスト

- [ ] `.claude/rules/{docs-structure,template-language,markdown,adr,roadmap}.md` の該当規約を確認した
- [ ] (ADR 更新時) ステータスが `accepted` 以降の ADR は immutable なため新 ADR + `Superseded by` で更新済
- [ ] (roadmap.md / Epic roadmap 更新時) `roadmap-tracker` Skill 経由で更新したか、もしくは手動更新の理由を記載
- [ ] PII が含まれていない (`.claude/rules/pii.md`)
