---
id: rules-wasm-compat
title: wasm-js 互換性規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/**/commonMain/**/*.kt"
  - "core/**/wasmJsMain/**/*.kt"
  - "feature/**/commonMain/**/*.kt"
  - "**/build.gradle.kts"
related_adrs:
  - ADR-0002
  - ADR-0012
---

# wasm-compat.md — wasm-js 互換性規約

> Compose Multiplatform の wasm-js target に対応するため、`commonMain` で
> Wasm 非互換 API (`okhttp3.*` / `java.awt.*` / `android.*` 等) を参照しない規約を強制する。
> 旧 Decompose 撤去 (ADR 0005) + 旧 JS 実装撤去 (ADR 0012) に伴い、`wasmJs` target に統一。

## Wasm 非互換 API (commonMain で禁止)

| カテゴリ | 禁止 import 例 | 理由 |
|---|---|---|
| OkHttp | `okhttp3.*` | Wasm 非サポート (JS Fetch 経由) |
| Android SDK | `android.content.*` / `android.os.*` / `android.util.*` | Android only |
| AWT / Swing | `java.awt.*` / `javax.swing.*` | Desktop only |
| Java IO (一部) | `java.io.File` / `java.nio.file.*` | Wasm fs 限定的サポート |
| Java reflection (一部) | `java.lang.reflect.*` | wasm-js は reflection サポート限定 |
| Coroutines `Dispatchers.IO` | `kotlinx.coroutines.Dispatchers.IO` | wasm-js は `IO` dispatcher 未提供、`Default` を使う |
| Thread | `java.lang.Thread` / `kotlin.concurrent.thread` | wasm-js は単一スレッド |

## プラットフォーム別の置き換え

| 機能 | commonMain (Wasm 互換) | プラットフォーム別 (`expect/actual`) |
|---|---|---|
| HTTP | `Ktor Client` (engine は expect) | OkHttp (Android) / JS Fetch (Wasm) / CIO (Desktop) |
| 永続化 | `SqlDelight` (`core/database`) | Driver: `AndroidSqliteDriver` / `WebWorkerDriver` (Wasm) / `JdbcSqliteDriver` (Desktop) |
| 設定保存 | `multiplatform-settings` | `SharedPreferencesSettings` (Android) / `StorageSettings` (Wasm) |
| 並行処理 | `kotlinx.coroutines` + `Dispatchers.Default` / `Dispatchers.Main` | (デフォルト共通) |
| ログ | `Napier` | platform-specific antilog |

## CoroutineDispatcher

- **`Dispatchers.IO` を `commonMain` で使わない** (wasm-js 非サポート)
- IO 系処理は `withContext(Dispatchers.Default)` で代替
- platform-specific に `IO` を使いたい場合は `expect val ioDispatcher: CoroutineDispatcher` を定義し、`Android`: `Dispatchers.IO` / `wasmJs`: `Dispatchers.Default` で actual 実装

## `expect/actual` 規約

```kotlin
// commonMain
expect class HttpClientFactory {
    fun create(): HttpClient
}

// androidMain
actual class HttpClientFactory {
    actual fun create(): HttpClient = HttpClient(OkHttp) { commonConfig() }
}

// wasmJsMain
actual class HttpClientFactory {
    actual fun create(): HttpClient = HttpClient(Js) { commonConfig() }
}
```

- `expect` は **interface ではなく class / fun** を推奨 (Compose Multiplatform 慣例)
- actual 実装は **platform 専用 API のみ参照**、commonMain の共通設定関数 (`commonConfig`) を呼ぶ

## Compose 互換性

- `@Composable` 関数本体は `commonMain` に置く
- `androidx.compose.ui.platform.LocalContext` 等の Android 専用 API を `commonMain` で参照しない (`expect/actual` で抽象化)
- Image / Font / Color resource は `compose-resources` 経由 (Wasm でも動く範囲、`i18n.md` 参照)

## Build 設定

```kotlin
kotlin {
    androidTarget()
    wasmJsTarget {
        browser {
            commonWebpackConfig {
                outputFileName = "colormaster.js"
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            // OkHttp は androidMain でのみ
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

- **`commonMain` には wasm 非互換 lib を含めない**
- platform-specific dependency は対応 source set のみ

## 機械検証 (A6 で導入)

- **Konsist** で `commonMain` 配下の Kotlin source に Wasm 非互換 import が含まれていない (R-22):
  - `okhttp3.*` / `java.awt.*` / `javax.swing.*` / `android.*` / `java.lang.Thread` / `Dispatchers.IO` の参照を warning または error
- **Gradle カスタムタスク** で build script の dependency 設定:
  - `commonMain.dependencies { okhttp(...) }` 等の誤配置を検出

## Gotchas

- **`String.format(...)` の locale**: wasm-js では `Locale.US` 等の locale 引数が無視される場合がある。`String.format(Locale.ROOT, ...)` でも安全のため明示
- **`System.currentTimeMillis()` ⇄ `kotlinx.datetime.Clock.System.now()`**: 後者を commonMain で使用、wasm-js は JS Date 経由
- **Coroutines `withTimeoutOrNull` は wasm-js でも動く** が、一部 cancellation 動作に差異がある。テストを multi-platform で実行 (`kotlin-test.md` 参照)
- **Reflection を必要とする lib (一部 DI / kotlinx-serialization の `serializer<T>()`) は wasm-js 制約あり**。`@Serializable` annotation + compile-time generated serializer で対応
- 旧 JS (`js/app/`) 実装は撤去済 (ADR 0012)、wasmJs に統一。再導入は別 ADR

## 関連

- ADR 0002 (Compose Multiplatform)
- ADR 0012 (旧 JS 実装撤去)
- Kotlin Multiplatform Wasm: https://kotlinlang.org/docs/wasm-overview.html
- `.claude/rules/{network-client,sql-delight,composable,kotlin-test}.md`
