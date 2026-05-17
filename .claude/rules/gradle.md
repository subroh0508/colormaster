---
id: rules-gradle
title: Gradle ビルド設定規約
status: stable
last_updated: 2026-05-17
paths:
  - "**/build.gradle.kts"
  - "settings.gradle.kts"
  - "gradle/libs.versions.toml"
  - "gradle.properties"
  - "buildSrc/**"
  - "plugins/**"
related_adrs:
  - ADR-0002
  - ADR-0003
  - ADR-0017
---

# gradle.md — Gradle ビルド設定規約

> Kotlin DSL (`*.gradle.kts`) + Version Catalog (`libs.versions.toml`) + Convention plugin
> (`plugins/` または `buildSrc/`) でビルド設定を集約。
> Compose Multiplatform + KMP 構造 (ADR 0002 / 0003) に対応。

## ビルドファイル構造

```text
gradle/libs.versions.toml       # 単一 source of truth: version + alias
settings.gradle.kts             # subproject 列挙、include
build.gradle.kts (root)         # 共通 buildscript / plugin
core/<module>/build.gradle.kts  # KMP target 設定 + dependency
plugins/src/main/kotlin/        # Convention plugin (再利用ロジック)
```

## Version Catalog 規約

```toml
[versions]
kotlin = "2.0.0"
compose-multiplatform = "1.6.10"
ktor = "2.3.10"
sqldelight = "2.0.1"
napier = "2.7.1"

[libraries]
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
napier = { module = "io.github.aakira:napier", version.ref = "napier" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

- **`libs.versions.toml` を Single Source of Truth** とし、build.gradle.kts に version 直書き禁止
- alias は kebab-case (TOML 仕様)
- version は `versions` セクションで集約、`version.ref` で参照

## KMP target 設定 (Compose Multiplatform module)

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget()
    jvm("desktop")
    wasmJsTarget { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}
```

- target: `androidTarget()` + `jvm("desktop")` + `wasmJsTarget { browser() }` を基本構成
- Wasm 非互換 lib は対応 source set のみに配置 (`wasm-compat.md` 参照)
- Backend module (`backend/`) は Cloud Run 用に JVM only

## Convention plugin

```kotlin
// plugins/src/main/kotlin/net/subroh0508/colormaster/primitive/compose/ComposeDsl.kt
fun KotlinMultiplatformExtension.composeTargets(...) { ... }
```

- 複数 module で共通の設定 (Android SDK version / Compose target / Kotest 設定 等) は Convention plugin に集約
- 配置: `plugins/src/main/kotlin/net/subroh0508/colormaster/primitive/`
- Custom plugin の `id` は `net.subroh0508.colormaster.primitive.<purpose>`

## gradle.properties

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
kotlin.code.style=official
android.useAndroidX=true
```

- **`configuration-cache=true`** を必須 (build 高速化、ADR 推奨)
- `parallel=true` + `caching=true` で multi-module build を高速化
- 秘匿情報 (API key 等) は `gradle.properties` に書かない (`.env` / GitHub Secrets で管理、`secrets.md` 参照)

## CI 起動

```bash
./gradlew check                  # lint + test 全 module
./gradlew :module:compileKotlin  # 個別 module の compile 確認
./gradlew assembleDebug          # Android Debug build
```

- CI (`.github/workflows/ci.yml`) で `./gradlew check` を起動 (Konsist + Detekt + Kotest 含む)
- **GitHub Actions で Claude API は呼ばない** (ADR 0017、コスト回避)

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証 (§5.2):
  - `build.gradle.kts` 内に version 直書き (`"2.0.0"`) が存在しない (Version Catalog 経由に強制)
  - Firebase 関連 coordinate が含まれない (`no-firebase.md` と統合)
  - 不要な repository (`mavenLocal()` 等) が含まれない
- **Renovate** で依存更新自動化、PR ラベル `renovate` を `pr-poller` が検出して `dependency-upgrade` Skill 起動

## Gotchas

- **`implementation` / `api` の使い分け**: 公開 API として transitively 露出させたい場合のみ `api`、通常は `implementation`
- **`ksp` plugin の version 整合**: KSP plugin version は Kotlin version と厳密に対応 (`2.0.0-1.0.21` 形式)、`libs.versions.toml` で managed
- **Wasm target 設定変更後は `wasm-d8` の cache が古いと build 失敗**することがある。`./gradlew clean` を実行
- **configuration-cache 有効時の build script 制約**: closure 内で `Project` を参照しない、`providers.gradleProperty(...)` を使う
- buildSrc は configuration-cache と相性に注意、`plugins/` (Convention plugin) を優先

## 関連

- ADR 0002 / 0003 / 0017
- Kotlin Gradle DSL: https://kotlinlang.org/docs/gradle.html
- Version Catalog: https://docs.gradle.org/current/userguide/platforms.html
- `.claude/rules/{wasm-compat,no-firebase,kotlin-test,secrets,cloud-run-deploy}.md`
