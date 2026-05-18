---
id: PLAN-004
title: A5 不要モジュール撤去 (js / kotlin-js-store / Firebase Hosting 設定)
type: refactor
status: in-progress
related_pr: null
related_epic: null
related_specs: []
related_adrs:
  - ADR-0005
  - ADR-0011
  - ADR-0012
  - ADR-0022
expected_modules:
  - "js/**"
  - "kotlin-js-store/**"
  - "firebase.json"
  - ".firebaserc"
  - ".github/workflows/web-build-and-deploy.yml"
  - "settings.gradle.kts"
  - "gradle/libs.versions.toml"
  - "build.gradle.kts"
  - "core/common/src/jsMain/kotlin/net/subroh0508/colormaster/common/firebase.kt"
  - ".claude/rules/removed-modules.md"
created_at: 2026-05-19
completed_at: null
promoted_to: null
---

# A5 不要モジュール撤去 (js / kotlin-js-store / Firebase Hosting 設定)

> **5 行以内 summary**: ロードマップ A5 に基づき、撤去確定済 module (旧 JS app `js/app`,
> `js/material`, `kotlin-js-store`, Firebase Hosting 設定 `firebase.json` / `.firebaserc`,
> dead workflow `web-build-and-deploy.yml`) を物理削除し、libs catalog から旧 JS app 専用依存
> (Decompose / kotlin-wrappers / npm-material-component-web / kotlin-js plugin /
> google-services plugin) を除去する単一 PR スコープのリファクタ。core/network/auth / firestore
> など behavior 影響のある module は Phase B/C 持ち越し (R-22 整合)。

## 目的

旧 JS app / Firebase Hosting / Decompose 系の dead code を物理削除して build context を clean に保ち、
ADR 0005 (Decompose 撤去) / ADR 0011 (Firebase → GIS 移行) / ADR 0012 (旧 JS 実装撤去) /
ADR 0022 (Cloudflare Pages 移行) の宣言済撤去を実体に反映する。

## 背景

`.claude/rules/removed-modules.md` で「撤去済 / 残骸あり」と明記されている対象が物理ファイルとして
残っており、`docs/harness/roadmap.md` の A5 タスク (EPIC-A0 配下) で撤去が確定済。
A2-1〜A2-3 でのドキュメント / rule 整備が完了したため、今回は **不可逆な物理削除** の段階。

撤去対象の残骸:

- `js/app/**` (99 ファイル: 旧 React + MDC for Web ベースの JS UI、Decompose 使用)
- `js/material/**` (同上、MDC for Web wrapper)
- `kotlin-js-store/yarn.lock` (kotlin-js project の Yarn lock、`:js:*` 撤去で不要)
- `firebase.json` / `.firebaserc` (Firebase Hosting 設定、ADR 0022 で Cloudflare Pages 移行済)
- `.github/workflows/web-build-and-deploy.yml` (`:js:app:jsBrowserDistribution` を
  Firebase Hosting に deploy する dead workflow、Cloudflare Pages 移行で不要)
- `settings.gradle.kts:21-22` の `:js:app` / `:js:material` include
- `gradle/libs.versions.toml` の旧 JS app 専用 catalog エントリ
  - `decompose-core` / `decompose-compose-jb` (`js/app/build.gradle.kts:50` 参照)
  - `kotlin-wrappers-bom` / `kotlin-wrappers-js` (`js/app/build.gradle.kts` 参照)
  - `kotlin-js` plugin (`build.gradle.kts:10` で `apply false`、利用なし)
  - `google-services` plugin / `google-services-plugin` library
    (`build.gradle.kts:9` で `apply false`、`android/app/build.gradle.kts:27` で
    commented out、actual apply なし)
  - `npm-material-component-web` version (`js/material/build.gradle.kts` のみで使用)
  - `decompose` version
- `core/common/src/jsMain/kotlin/net/subroh0508/colormaster/common/firebase.kt`
  (唯一の caller が `js/app/src/jsMain/kotlin/main.kt` のため `:js:app` 撤去と同時に dead)
- `build.gradle.kts:9-10` の `alias(libs.plugins.google.services) apply false` /
  `alias(libs.plugins.kotlin.js) apply false`

## アプローチ

R-22 (A10 完了前のリファクタ制約: 可逆な内部リファクタのみ) と緊張関係にあるが、本作業は
`.claude/rules/removed-modules.md` で **すでに撤去確定済** + 関連 ADR (0005 / 0011 / 0012 / 0022)
で撤去方針が決定済、UI / 振る舞いに影響しない (削除対象は production code path に入っていない)
ため、ADR 整合性の物理反映と位置づけて実施する。

不可逆だが behavior preservation の二本柱 (visual-regression + spec-conformance) に違反しない:

- visual-regression: production target (`android:app` / `backend:server` / `backend:cli` /
  `core/features/**` / `core/data/**`) の Composable / API レスポンスに影響なし
- spec-conformance: `:js:app` / `:js:material` は SPEC docs に出てこない (kmp 化前の遺物)

### 段階的削除手順 (`removed-modules.md` Gotchas §1 「lib 依存 → import 残骸 → module ディレクトリ → 設定 file の順」)

1. **Phase A**: `git rm -r js/ kotlin-js-store/` + `git rm firebase.json .firebaserc` +
   `git rm .github/workflows/web-build-and-deploy.yml` +
   `git rm core/common/src/jsMain/kotlin/net/subroh0508/colormaster/common/firebase.kt`
2. **Phase B**: `settings.gradle.kts` から `:js:app` / `:js:material` の include 削除
3. **Phase C**: `gradle/libs.versions.toml` から旧 JS app 専用 catalog 削除 (上記列挙)
4. **Phase D**: `build.gradle.kts` から `google.services` / `kotlin.js` plugin alias 削除
5. **Phase E**: `android/app/build.gradle.kts:27` のコメントアウト行 (`//apply(plugin = "com.google.gms.google-services")`) 削除
6. **Phase F**: `.claude/rules/removed-modules.md` の「撤去対象一覧」表で「残骸」列を更新
   (旧 JS app / Firebase Hosting / Decompose / google-services plugin を「なし (撤去完了)」)
7. **Phase G**: `./gradlew tasks` / `./gradlew :backend:server:check` を確認 (full build は CI に委ねる)

### スコープ縮小判断 (Phase B/C 持ち越し)

以下は `core/data/**` (production) が参照しているため、本 PR では touch しない:

- `core/network/auth/**` (`core/data/DefaultAuthRepository.kt` 経由で `AuthClient` / `FirebaseUser` 参照)
- `core/network/firestore/**` (`core/data/DefaultMyIdolsRepository.kt` 等が `UserDocument` 参照)
- `firebase-app` / `firebase-auth` / `firebase-firestore` の libs 行 (上記 module で使用中)
- `core/test/src/.../FirebaseUser.kt` / `FakeFirestoreClient.kt` (上記 production の test double)

これらは Backend GIS 移行 EPIC (Phase B 以降) で撤去予定。本 PR では `.claude/rules/removed-modules.md`
の「残骸 (撤去進行中)」列を **そのまま残す** (Firebase Auth / Firestore module)。

## 受け入れ基準

- [ ] `js/` / `kotlin-js-store/` / `firebase.json` / `.firebaserc` / `.github/workflows/web-build-and-deploy.yml`
      が `git ls-files` で出現しない
- [ ] `core/common/src/jsMain/kotlin/.../firebase.kt` が削除されている
- [ ] `settings.gradle.kts` に `:js:app` / `:js:material` の include が含まれない
- [ ] `gradle/libs.versions.toml` から `decompose` / `kotlin-wrappers` / `npm-material-component-web` /
      `google-services-plugin` (version + library) / `kotlin-js` plugin / `google-services` plugin /
      `decompose-core` / `decompose-compose-jb` / `kotlin-wrappers-bom` / `kotlin-wrappers-js` が削除されている
- [ ] `build.gradle.kts` に `libs.plugins.google.services` / `libs.plugins.kotlin.js` の alias が含まれない
- [ ] `.claude/rules/removed-modules.md` の表で「旧 JS app」「Firebase Hosting」「Decompose」「google-services plugin」が「なし (撤去完了)」
- [ ] `./gradlew tasks --no-daemon` がエラーなく実行できる (Gradle 構成 valid)
- [ ] CI (`./gradlew check`) が green (ローカル full build 未実行の場合)

## behavior preservation 検証点 (R-22 / `.claude/rules/behavior-preservation.md`)

- **入出力不変性**: 撤去対象は production code path に呼び出されていない (`grep -r "decompose\|js.material\|js.app"` で
  `js/` 配下以外に参照なし) → 関数 / API の input → output は完全不変
- **public API 不変性**: `core/features/**` / `core/data/**` / `core/network/imasparql/**` / `backend/**` /
  `android/app/**` の public シグネチャは touch しない
- **UI screenshot 不変性**: `android:app` (production UI target) は touch しない、Roborazzi baseline 範囲外
- **状態遷移不変性**: ViewModel / StateFlow を touch しない
- **エラーメッセージ不変性**: `strings.xml` / compose-resources を touch しない
- **ロールバック容易性**: `git revert <merge-commit>` で完全復元、worktree から原ファイル取得可能

## スコープ外

- `core/network/auth/**` の撤去 (Backend GIS 移行 EPIC、Phase B 以降)
- `core/network/firestore/**` の撤去 (Backend SQLite + Litestream 完成 EPIC、Phase C5)
- `firebase-app` / `firebase-auth` / `firebase-firestore` libs 行の削除 (上記 EPIC 配下)
- `core/test/src/.../FirebaseUser.kt` / `FakeFirestoreClient.kt` の削除 (上記と連動)
- `kotlinx-coroutines-js` / `ktor-client-js` / `ktor-client-json-js` / `ktor-serialization-js` libs 行の削除
  (将来 wasmJs target 拡張で再利用可能性、`.claude/rules/wasm-compat.md` 整合)
- A5 完了後の Backend SQLite + Litestream 移行 (別 Plan / Epic)

## ロールバック手順

1. `git revert <merge-commit>` で本 PR を逆適用
2. または `git checkout master -- js/ kotlin-js-store/ firebase.json .firebaserc .github/workflows/web-build-and-deploy.yml core/common/src/jsMain/kotlin/net/subroh0508/colormaster/common/firebase.kt` で原ファイル復元
3. `settings.gradle.kts` / `gradle/libs.versions.toml` / `build.gradle.kts` を `git checkout` で復元
4. `.claude/rules/removed-modules.md` の「残骸」列を `git checkout` で復元
5. `./gradlew tasks` で構成 valid 確認

## メモ

- `android/app/build.gradle.kts:27` のコメントアウト `//apply(plugin = "com.google.gms.google-services")`
  は ADR 0011 (Firebase → GIS 移行) で plugin 撤去確定済のため除去
- `js/app/build.gradle.kts:50` で参照される `libs.kotlin.wrappers.js` のような
  `libs.versions.toml` の `kotlin-wrappers-js` (BOM 経由でバージョン解決) も同時撤去
- 過去レトロ (`docs/harness/learnings/` 配下) で同領域の撤去事例なし (本 PR が A5 初動)
- 本 Plan は `refactor` Skill (`.claude/skills/refactor/SKILL.md`) → `plan-author` Skill 経由で起票
- 後続 `implementation-workflow` Phase 6 で `code-reviewer` Skill が
  spec-conformance / architecture / security / code-quality 4 aspect を並列実行
