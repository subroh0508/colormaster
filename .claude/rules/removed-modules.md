---
id: rules-removed-modules
title: 撤去 module 一覧と再導入禁止規約
status: stable
last_updated: 2026-05-19
paths:
  - "**/build.gradle.kts"
  - "settings.gradle.kts"
  - "**/*.kt"
  - ".firebaserc"
  - "firebase.json"
related_adrs:
  - ADR-0005
  - ADR-0011
  - ADR-0012
---

# removed-modules.md — 撤去 module 一覧と再導入禁止規約

> 本プロジェクトで **明示的に撤去された module / lib** の一覧、撤去理由、関連 ADR、
> 再導入時の手続きを規定。撤去状態を機械的に維持し、誤って復活させないためのガード。

## 撤去対象一覧

| 名称 | 撤去理由 | 撤去 ADR | 残骸 (撤去進行中) | 検出 rule |
|---|---|---|---|---|
| Decompose (`com.arkivanov.decompose:*`) | KMP Navigation の代替を `androidx.navigation` (Nav3) に統一 | ADR 0005 | なし (撤去完了、PLAN-003 / 2026-05-19) | `viewmodel.md` / `navigation.md` |
| 旧 JS app (`js/app/`, `js/material/`) | Wasm 統一でブラウザ target を 1 つに | ADR 0012 | なし (撤去完了、PLAN-003 / 2026-05-19) | `wasm-compat.md` |
| `kotlin-js-store/` (Yarn lock) | `:js:*` 撤去で kotlin-js project ロックが不要 | ADR 0012 | なし (撤去完了、PLAN-003 / 2026-05-19) | `wasm-compat.md` |
| Firebase Authentication SDK | GIS (Google Identity Services) + Backend 検証に移行 | ADR 0011 | `core/network/auth/` 内に一部依存残存 (Backend GIS 移行 EPIC で撤去予定) | `firebase-boundary.md` / `no-firebase.md` |
| Firebase Firestore | Backend SQLite + Litestream + R2 に移行 | ADR 0008 / 0011 | `core/network/firestore/` 残存 (Phase C5 で撤去予定) | `firebase-boundary.md` / `no-firebase.md` |
| Firebase Hosting | Cloudflare Pages に移行 | ADR 0011 / 0022 | なし (撤去完了、PLAN-003 / 2026-05-19) | `cloudflare-pages.md` |
| Firebase 系全般 (Analytics / Crashlytics / Messaging / Config / Functions) | 採用方針外 | ADR 0011 | (もし import 残存していれば) | `no-firebase.md` / `firebase-boundary.md` |
| `com.google.gms.google-services` plugin | Firebase SDK 撤去に伴い不要 | ADR 0011 | なし (撤去完了、PLAN-003 / 2026-05-19、`android/app/build.gradle.kts` の commented out 行 + catalog 行を削除) | `no-firebase.md` |
| `kotlin-wrappers` (`org.jetbrains.kotlin-wrappers:*`) | 旧 JS app 専用、Wasm 統一で不要 | ADR 0012 | なし (撤去完了、PLAN-003 / 2026-05-19) | `wasm-compat.md` |
| `@material/*` (npm MDC for Web) | 旧 JS app 専用、Compose Multiplatform に統一 | ADR 0012 | なし (撤去完了、PLAN-003 / 2026-05-19、`npm-material-component-web` catalog 削除) | `wasm-compat.md` |
| `org.jetbrains.kotlin.js` plugin | `:js:*` 撤去で不要 (KMP 経由の Kotlin/JS target は維持) | ADR 0012 | なし (撤去完了、PLAN-003 / 2026-05-19、`build.gradle.kts` の `apply false` + catalog 行を削除) | `wasm-compat.md` |
| Firebase Hosting deploy workflow (`.github/workflows/web-build-and-deploy.yml`) | `:js:app:jsBrowserDistribution` の Firebase Hosting deploy が dead | ADR 0011 / 0022 | なし (撤去完了、PLAN-003 / 2026-05-19) | `cloudflare-pages.md` |

## 撤去進行中 module の追跡

撤去完了まで以下を実施:

1. 該当 module / lib への `@Deprecated` annotation 付与 (Kotlin source の場合)
2. `firebase-boundary.md` の allowlist に **期限付き** で追加
3. 代替実装の Plan / Epic を起票 (`PLAN-NNN-remove-*` または `EPIC-NNN-firebase-migration` 等)
4. 撤去 PR 内で本 rule の一覧と allowlist を更新
5. 削除完了後、本 rule の「残骸」列を「なし」に変更

## 再導入時の手続き

**撤去された module を再導入する場合は以下を必須**:

1. **新 ADR を起こす** (再導入理由 + 代替案再評価 + 影響範囲)
2. 元の撤去 ADR を `superseded by` でリンク
3. 撤去 rule (`no-firebase.md` / `firebase-boundary.md` 等) の例外条項を更新
4. 撤去 module 一覧から削除 (本 rule)
5. 撤去検証ロジック (`firebase-boundary.md` Konsist test) を緩和または削除
6. 人間 approve 必須、auto-merge 禁止 (`docs/harness/plan.md` R-15)

「便利だから戻す」では再導入させない。**「以前撤去した時より状況が変わった」根拠の明示** が必要。

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証:
  - `import com.arkivanov.decompose.*` が production source に存在しない
  - `import com.google.firebase.*` / `import dev.gitlive.firebase.*` が production source に存在しない (`firebase-boundary.md` allowlist 例外あり)
- **Gradle カスタムタスク** で以下を検証 (§5.2):
  - `gradle/libs.versions.toml` に Decompose / Firebase 系 coordinate が含まれない
  - `js/app/` ディレクトリが存在しない (撤去完了後)
  - `firebase.json` / `.firebaserc` / `google-services.json` が存在しない (撤去完了後)

## Gotchas

- **撤去 PR 内で「全削除」しない**。段階的に (lib 依存 → import 残骸 → module ディレクトリ → 設定 file の順) 削除し、各段階で動作確認
- **transitively pulled な撤去 lib** に注意 (例: 他 lib が Decompose を transitive dependency として持つ)。`./gradlew :module:dependencies` で確認
- **撤去 ADR を `deprecated` にしない**。撤去後も「過去にこれを使っていた、こう撤去した」記録として `accepted` 状態を保つ
- 一度撤去した module を blame 履歴から消すために `git filter-repo` 等を使わない (履歴改変は別 ADR 必須)

## 関連

- ADR 0005 (Decompose 撤去)
- ADR 0011 (Firebase → GIS 移行)
- ADR 0012 (旧 JS 実装撤去)
- `.claude/rules/{no-firebase,firebase-boundary,navigation,cloudflare-pages,backend-auth}.md`
- `docs/adr/README.md` (ADR 索引)
