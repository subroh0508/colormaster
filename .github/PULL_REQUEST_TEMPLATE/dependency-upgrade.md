---
type: dependency-upgrade
related_plan: PLAN-NNN
related_epic: null
related_adrs: []
related_specs: []
expected_modules: []
---

## 概要

<Renovate 等によるライブラリバージョンアップ。1-3 行で「何を / なぜ」>

## 関連

- Plan: PLAN-NNN (該当時)
- Renovate ダッシュボード: <link>
- 元 PR (Renovate 自動起票): #N

## 変更内容

| ライブラリ | Before | After | semver 種別 (major/minor/patch) |
|---|---|---|---|

## Renovate リリースノート要約

<Context7 MCP で取得した最新リリースノートの主要変更点を 3-5 点で要約>

## 影響 API 差分

| API / クラス | 変更内容 (deprecate / signature 変更 / 新規) | 影響モジュール |
|---|---|---|

## Context7 で検証した内容

- [ ] 新バージョンの API シグネチャを Context7 MCP で取得して確認
- [ ] 削除・名称変更 API が本リポジトリで利用されていないか確認
- [ ] migration guide が提供されている場合は記載

## 受け入れ基準 (AC)

- [ ] `./gradlew check` グリーン
- [ ] dependency-upgrade Skill (`pr-poller` がローカルで起動) が `approve` ラベル付与
- [ ] 危険な変更があれば downgrade or Plan 起票

## テスト

- [ ] `./gradlew check` グリーン (CI)
- [ ] Roborazzi baseline (UI 関連ライブラリの場合) グリーン or 承認済

## レビュー観点

<挙動変更が紛れていないか、特に Kotlin / Compose / Ktor / SQLDelight 系は注意>

## チェックリスト

- [ ] semver major の場合は ADR 起票 (`.claude/rules/adr.md` 起票基準 2)
- [ ] PII / Secrets が diff に含まれていない (trufflehog グリーン)
- [ ] **GitHub Actions で Claude API を呼んでいない** (本タスクは pr-poller がローカルで dependency-upgrade Skill を起動、ADR 0017)
