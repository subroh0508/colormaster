---
id: rules-logging
title: ログ出力規約 (Napier)
status: stable
last_updated: 2026-05-17
paths:
  - "core/**/*.kt"
  - "feature/**/*.kt"
  - "backend/**/*.kt"
related_adrs:
  - ADR-0002
  - ADR-0020
---

# logging.md — ログ出力規約 (Napier)

> Compose Multiplatform 配下で **Napier** を共通ロガーに採用 (`io.github.aakira:napier`)。
> Android / iOS / Wasm / Desktop / Backend で同じ API でログを出力でき、
> PII / Secrets を含めない redaction を強制する。

## ログレベル

| level | 用途 | 例 |
|---|---|---|
| `Napier.v` (verbose) | 詳細トレース、debug build のみ有効化想定 | viewmodel dispatch 開始 |
| `Napier.d` (debug) | 開発時の確認用、debug build のみ | HTTP request body |
| `Napier.i` (info) | 通常運用で残したい情報 | ユーザーログイン成功 (uid のみ) |
| `Napier.w` (warn) | 業務例外 / リトライ可能なエラー | network timeout、retry |
| `Napier.e` (error) | リトライ不可能なエラー / バグ | SerializationException、unexpected null |

## 出力先設定

| build | 設定 | 詳細 |
|---|---|---|
| debug | `Napier.base(DebugAntilog())` | logcat (Android) / console (Wasm/Desktop) に全 level 出力 |
| release | `Napier.base(CrashReportingAntilog())` (将来) | `warn` 以上のみ収集、PII 除外 |
| Backend | `Napier.base(BackendAntilog())` (stdout JSON) | Cloud Run の Cloud Logging に流す |

- 初期化は App エントリポイント (`MainActivity` / `wasmJsMain` / `main()`) で `Napier.base(...)` を 1 回呼ぶ
- 切り替えは `BuildKonfig.IS_DEBUG` 等で判定

## ログメッセージ規約

```kotlin
Napier.d("Search query=$query results=${results.size}")
Napier.w("Network retry attempt=$attempt", throwable = e)
Napier.e("Unexpected null in repository response", throwable = e)
```

- **PII / Secrets を埋め込まない** (`pii.md` / `secrets.md` 参照)
  - メールアドレス / 表示名 / IP / Authorization header / API キー → 禁止
  - uid (PII 同等扱い) も避ける、必要時は `uid.take(6) + "..."` で truncate
- 業務上の数値 / フラグは OK (`results.size` / `attempt` / `isOnline`)
- ロケール固定 (`Locale.ROOT`) で文字列フォーマット、`String.format("%.2f", ...)` 等

## tag 規約 (Napier)

- Napier はデフォルトで呼び出し元クラス名を tag に使う
- 明示時は `Napier.d("...", tag = "SearchVm")` のように短い大文字始まり identifier
- module 横断的な subsystem (`Sync`、`Auth`) は専用 tag で grep しやすくする

## Backend のログ規約

- Cloud Run の structured logging (JSON) に流す
- 各 log entry は以下を含む:
  - `severity` (Napier level → Cloud Logging severity)
  - `message` (本文)
  - `trace_id` (request scope の trace ID、Ktor middleware で attach)
- レスポンス body / request body のフルダンプは `level: debug` のみ、PII 含む field は redact

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `println(...)` / `System.out.println(...)` / `print(...)` の直接呼び出しが production source に存在しない (テスト除外)
  - `kotlin.io.println` / `java.util.logging.Logger` を import していない
  - `Napier.*` 呼び出しでメールアドレス文字列が含まれていない (regex `@(?!example\.com)` で warning)

## Gotchas

- **wasm-js では `printStackTrace()` 不可**。`Napier.e("...", throwable = e)` 経由で扱う、Napier の wasm 実装は stacktrace を string 化する
- **logcat (Android) は release build で多くが filter される**。重要なログは `level: info` 以上に
- **redaction は呼び出し側責任**。Napier 自体は filter しない、書く時に注意 (`pii.md` / `secrets.md` 参照)
- **`Napier.base()` を複数回呼ぶと全部 attach される**。multi-target でも 1 回のみが原則
- Cloud Run 側で `severity: ERROR` の log が Cloud Logging に流れた場合 Slack alert が飛ぶ運用を将来検討 (Phase C)

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- ADR 0020 (PII 最小化)
- Napier: https://github.com/AAkira/Napier
- `.claude/rules/{error-handling,pii,secrets,backend-auth}.md`
