---
id: rules-error-handling
title: エラーハンドリング規約
status: stable
last_updated: 2026-05-17
paths:
  - "core/**/*.kt"
  - "feature/**/*.kt"
  - "backend/**/*.kt"
related_adrs:
  - ADR-0002
---

# error-handling.md — エラーハンドリング規約

> Kotlin の `Result<T>` / `runCatching` の使い分け、例外伝播ポリシー、UI への表出方法を規定。
> Repository は例外を投げ、ViewModel が `runCatching` で wrap して UiState に反映する設計を強制。

## 層ごとの方針

| 層 | 方針 | 戻り値 |
|---|---|---|
| `core/network/*Client*` | `IOException` / `ResponseException` / `SerializationException` を投げる | DTO または `null` |
| `core/data/Default*Repository` | network 層の例外をそのまま伝播 (wrap しない) | ドメインモデル |
| `core/usecase` (将来) | 例外伝播のまま、業務例外 (`DomainException`) のみカスタム化 | ドメインモデル |
| ViewModel | `runCatching { repository.xxx() }` で wrap、UiState の `error: Throwable?` に保持 | UiState |
| `@Composable` Screen | UiState の `error` を Snackbar / Dialog で表示、`onAction(Retry)` で再試行 | Unit |

## `Result<T>` の使い分け

- **Repository は `Result<T>` を返さない** (例外を投げる)
- ViewModel 内で `runCatching { ... }.onSuccess { ... }.onFailure { ... }` を使う
- 業務例外 (`InvalidQueryException` 等) は `DomainException` 階層に定義し、ユーザー向けメッセージを `error.localizedMessage` で `stringResource` 経由のキーに mapping

## ViewModel のパターン

```kotlin
private fun handleSearchClicked() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        runCatching { repository.searchByName(_uiState.value.query) }
            .onSuccess { results ->
                _uiState.update { it.copy(isLoading = false, results = results) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
    }
}
```

- `isLoading` / `error` を毎回 reset
- `onFailure` で `error` を UiState に保持、UI 側でフィルタ / 表示

## Compose Screen のパターン

```kotlin
LaunchedEffect(uiState.error) {
    uiState.error?.let {
        snackbarHostState.showSnackbar(
            message = it.localizedMessageOrFallback(),
            actionLabel = stringResource(Res.string.retry),
        )
    }
}
```

- `LaunchedEffect(uiState.error)` で副作用化
- 表示後は `onAction(ErrorDismissed)` で `error: null` に戻す (UiAction 経由)

## Logging

- **例外は必ず Napier / 同等 logger で出力** (`logging.md` 参照)
- 業務例外 (`DomainException`) は `level: warn`、network / IO 例外は `level: error`
- stack trace は `Napier.e(throwable = e)` で post、PII / Secrets が含まれていないことを確認 (`pii.md` / `secrets.md` redaction)

## 禁止事項

- **`try { ... } catch (e: Exception) { /* ignore */ }`** (silent swallow 禁止)
- **`try { ... } catch (e: Throwable) { ... }`** (`Error` 系を catch しない、OOM / StackOverflow を握り潰さない)
- **`throw RuntimeException("...")`** 直書き (業務例外は `DomainException` 階層を定義、network は Ktor の `ResponseException` 等を使う)
- **`null` 戻りでエラー表現**: `findById(id: IdolId): Idol?` のように「見つからなかった = null」は OK、「ネットワーク失敗 = null」は禁止 (例外を投げる)

## Backend (Kotlin) のエラーハンドリング

- Ktor Server Application で `StatusPages` plugin により例外 → HTTP status 変換
- ドメイン例外 → 400 / 認証例外 → 401 / not found → 404 / それ以外 → 500
- レスポンス body は `{ error: { code: "...", message: "..." } }` 形式 (OpenAPI spec と整合)

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `core/model/**/*Repository.kt` の interface 戻り値型に `Result<*>` が含まれない
  - `core/data/**/Default*Repository.kt` 内に `try { ... } catch (e: Exception) {}` (空 catch) パターンが存在しない
  - top-level `throw RuntimeException(...)` の直書きが存在しない
- **Detekt** で `SwallowedException` / `TooGenericExceptionCaught` を有効化

## Gotchas

- **CoroutineExceptionHandler を `viewModelScope` に attach しない**。`viewModelScope` の親 `SupervisorJob` がキャンセル隔離するため、`runCatching` で各 launch 内に閉じ込めるのが正しい
- **`runCatching` は `CancellationException` も catch する**。`coroutineScope` でキャンセル伝播が必要な箇所では `coroutineScope { try { ... } catch (e: Exception) { ... } }` パターンで `CancellationException` を rethrow
- **wasm-js では `Throwable.printStackTrace()` 不可**。stacktrace は Napier 経由で扱う (`logging.md` 参照)
- ユーザー向けメッセージは i18n 必須 (`i18n.md` 参照)、技術的 detail は debug build のみ表示

## 関連

- ADR 0002 (Compose Multiplatform + Nav3 + 共通 ViewModel)
- `.claude/rules/{viewmodel,ui-state,repository,network-client,logging,i18n,pii,secrets}.md`
