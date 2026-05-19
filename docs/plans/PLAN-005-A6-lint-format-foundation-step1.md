---
id: PLAN-005
title: A6 Lint/Format 基盤 step1 = Spotless + ktlint 最小統合
type: harness
status: completed
related_pr: 182
related_epic: null
related_specs: []
related_adrs:
  - ADR-0017
expected_modules:
  - gradle/libs.versions.toml
  - plugins/build.gradle.kts
  - plugins/src/main/kotlin/net/subroh0508/colormaster/primitive/spotless/SpotlessPlugin.kt
  - build.gradle.kts
  - .editorconfig
  - .github/workflows/ci.yml
created_at: 2026-05-19
completed_at: 2026-05-19
promoted_to: null
---

# A6 Lint/Format 基盤 step1 = Spotless + ktlint 最小統合

> **5 行以内 summary**: A6 全体 (Spotless / ktlint / detekt / Konsist / markdownlint-cli2 /
> Gradle カスタムタスク / trufflehog) のうち **最小スコープ 1 PR** として
> Spotless + ktlint のみを導入する。後続 step2-6 は別 Plan で順次起票。
> CI に format check job を追加し、`./gradlew spotlessApply` で既存 Kotlin
> コードに初回 format を適用する (大量差分は別 commit に分離)。

## 目的

A6 Lint/Format 基盤を **Spotless + ktlint** で最小着手する。Kotlin / Kotlin DSL の format/lint
基盤を 1 PR で完結させ、後続の detekt / Konsist / markdownlint-cli2 / Gradle カスタムタスク
(frontmatter / 5 行 summary / 日本語見出し / 配列 block 形式 / status 語彙正規化 検証) /
trufflehog は **別 Plan** として placeholder 列挙のみ残す。

## 背景

- `docs/harness/roadmap.md` A6 の expected_modules: `build.gradle.kts`, `plugins/**`, `.github/workflows/**`
- A6 全体スコープを 1 PR で導入すると 40+ ファイル想定でレビュー負荷が上限超
- Spotless + ktlint だけなら touch 範囲が明確: `build.gradle.kts` + `plugins/` + `.github/workflows/ci.yml` + `.editorconfig`
- 既存 convention plugin 命名規約 (`colormaster.primitive.<purpose>` / `colormaster.convention.<purpose>`) に乗る形で `colormaster.primitive.spotless` を追加
- A5 で Test/Web 撤去済 (PR #176)、CI job は Test/Android のみ → format check job を追加

## アプローチ

1. `gradle/libs.versions.toml` に `[versions]` `spotless = "7.0.4"` + `ktlint = "1.5.0"` を追加し `[plugins]` セクションに `spotless` alias 登録
2. `plugins/src/main/kotlin/net/subroh0508/colormaster/primitive/spotless/SpotlessPlugin.kt` を新規作成 (primitive plugin として):
   - subproject 全体に Spotless 適用 (kotlin / kotlinGradle target)
   - ktlint version 固定、editorconfig override 未使用 (.editorconfig をプロジェクト直配置)
   - target globs: `**/*.kt` (build / generated 除外) / `**/*.kts`
3. `plugins/build.gradle.kts` の `gradlePlugin { plugins { ... } }` ブロックに `register("spotless") { id = "colormaster.primitive.spotless"; implementationClass = "net.subroh0508.colormaster.primitive.spotless.SpotlessPlugin" }` 追加
4. `plugins/build.gradle.kts` に Spotless plugin の依存を追加 (`implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.4")`)
5. root `build.gradle.kts` で `id("colormaster.primitive.spotless")` を適用 (全 subproject に伝播)
6. `.editorconfig` を repo root に新規配置 (Kotlin 標準 + ktlint_official + max_line_length 等)
7. `.github/workflows/ci.yml` に **format check 別 job** (`Lint / Format Check`) を追加: `./gradlew spotlessCheck --stacktrace`
8. `./gradlew spotlessApply` を実行し、既存コード format を初回適用 (差分は **別 commit** に分離)

## 受け入れ基準

- [ ] AC-1: `gradle/libs.versions.toml` に `spotless` (`com.diffplug.spotless`) plugin と ktlint version が追加されている
- [ ] AC-2: `SpotlessPlugin.kt` が primitive plugin として登録され、root `build.gradle.kts` から適用されている
- [ ] AC-3: `./gradlew spotlessCheck` が CI で実行され、`Lint / Format Check` job が追加されている
- [ ] AC-4: `./gradlew spotlessApply` 適用後、`./gradlew spotlessCheck` が pass する (CI green)
- [ ] AC-5: 後続 step2-6 (detekt / Konsist / markdownlint-cli2 / Gradle カスタムタスク / trufflehog) が本 Plan 末尾と PR description に placeholder として列挙されている
- [ ] AC-6: 振る舞い変更なし (Spotless は format のみ、build / test の結果は不変)

## スコープ外 (後続 Plan として placeholder)

- **step2**: detekt 統合 + baseline (既存違反は baseline 化、新規違反のみ fail)
- **step3**: Konsist 統合 (`firebase-boundary.md` / `naming.md` / `test-paired-class.md` 等の Kotlin source 構造検証)
- **step4**: markdownlint-cli2 統合 (`docs-structure.md` / `markdown.md` SoT 検証)
- **step5**: Gradle カスタムタスク (frontmatter 必須キー / 5 行 summary / 日本語見出し / 配列 block 形式 / status 語彙正規化 検証)
- **step6**: trufflehog 統合 (`.github/workflows/secret-scan.yml`、PR 差分 secret-scan)

各 step は別 Plan として起票し、step1 完了後に 1 つずつ着手する。

## ロールバック手順

本 PR の commit を `git revert <merge-commit>` で完全復元可能。touch ファイルは限定的 (plugin
登録 + 設定ファイル + CI job) で副作用なし。spotlessApply の format 差分 commit を含む全 commit を
revert すれば format 前の状態に戻る。

## メモ

- **Spotless 7.0.4** (2025-04 リリース、Gradle 7.5+ / JDK 11+ 必須、本リポ JDK 17 環境で動作)
- **ktlint 1.5.0** (Kotlin 2.1 系対応の安定版)
- **convention 階層ではなく primitive 階層**: 全 subproject 一律適用なので module type 依存なし
- **`.editorconfig` は ktlint と直接連動**: ktlint_official ruleset で max_line_length / indent_size 等を読み取り
- **subjective format 変更は禁止** (Kotlin 公式 ktlint ルール準拠、custom rule 押し付けは別 Plan で議論)
- **JDK 25 vs AGP 8.9.0 互換性問題** (PR #176 既知) が再現する場合、ローカル build は skip して CI に委ねる旨を PR description に注記
