---
id: rules-no-firebase
title: Firebase 系の新規追加禁止
status: stable
last_updated: 2026-05-17
paths:
  - "build.gradle.kts"
  - "**/build.gradle.kts"
  - "gradle/libs.versions.toml"
  - ".claude/skills/feature-request/**"
  - ".claude/skills/dependency-upgrade/**"
related_adrs:
  - ADR-0011
---

# no-firebase.md — Firebase 系の新規追加禁止

> ADR 0011 (Firebase → GIS 移行) に基づき、Firebase 系の **新規追加を禁止** する規約。
> 既存 import 検出は `firebase-boundary.md` (Konsist の Kotlin source パターン) と
> **二段運用**: 本ファイルは Skill が依存追加 / 機能起草する **事前ガード**、
> `firebase-boundary.md` は CI / Konsist による **事後検証**。
> 責務分担と改名再評価方針は EPIC-A2 `decisions.md` 参照。

## 禁止対象

新規追加禁止 (build スクリプト / 依存 catalog / コード import):

| カテゴリ | 具体例 |
|---|---|
| Firebase Authentication | `com.google.firebase:firebase-auth-*` / `dev.gitlive:firebase-auth-*` (Kotlin Multiplatform Firebase) |
| Firebase Firestore / Realtime DB | `firebase-firestore-*` / `firebase-database-*` |
| Firebase Functions | `firebase-functions-*` |
| Firebase Analytics / Crashlytics / Performance | `firebase-analytics-*` / `firebase-crashlytics-*` / `firebase-perf-*` |
| Firebase Messaging | `firebase-messaging-*` |
| Firebase Remote Config | `firebase-config-*` |
| Firebase Hosting / Cloud Build for Firebase | `firebase.json` 配置、`firebase deploy` 等のスクリプト |
| Google Services plugin | `com.google.gms.google-services` |

## 代替案 (ADR 0011 に基づく置き換え)

| Firebase の用途 | 採用代替 | 関連 ADR |
|---|---|---|
| 認証 (Auth) | Google Identity Services (GIS) + Backend で ID Token 検証 | ADR 0011, 0008 |
| データストア (Firestore) | Backend SQLite + Litestream + R2 | ADR 0008 |
| 静的ホスティング (Hosting) | Cloudflare Pages | ADR 0022 |
| アナリティクス | (採用未定、必要時に別 ADR で再評価) | — |
| FCM (Push 通知) | (機能未要件化、必要時に別 ADR) | — |

## Skill 事前ガード

`feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` Skill は、**依存追加 / コード生成
の起草段階で本 rule を参照** し、以下を満たすか確認:

- [ ] 提案する build スクリプト変更に Firebase 系の add / implementation / api 行が含まれていないか
- [ ] 提案するコードに `import com.google.firebase.*` / `import dev.gitlive.firebase.*` が含まれていないか
- [ ] 「認証」「データ同期」を扱う場合、GIS / Backend / SQLite のいずれかを採用しているか

違反検出時は **起草段階で reject** (Plan / Epic 起票前)、代替案を提示する。

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証 (build script 系、§5.2):
  - `**/build.gradle.kts` / `gradle/libs.versions.toml` に Firebase 系の coordinate が出現していない
  - `gradle/libs.versions.toml` の `[libraries]` セクションに `firebase` 文字列を含む key が存在しない
  - `firebase.json` / `firestore.rules` 等のファイルがリポジトリに存在しない
- **Konsist** で Kotlin source の import 検証 (`firebase-boundary.md` 側で担保、本 rule では build 側のみ)

## 例外と再評価

- 既存の Firebase 関連参照は `firebase-boundary.md` でエスケープ用ホワイトリストを管理 (移行完了まで段階的)
- 将来必要になった機能 (FCM / Crashlytics 等) は **新 ADR を起こして禁止解除** を明示。本 rule の更新と build 検証の例外追加は ADR 採択後

## Gotchas

- **Firebase 系の lib を 1 行追加するだけでも Konsist / Gradle 検証が落ちる**。誤って `Renovate` PR で追加されないよう、`renovate.json5` の `packageRules` で Firebase 系を `disabled` にする (A6 / dependency-upgrade Skill 本格化時に実装)
- **`com.google.gms.google-services` plugin の `apply` も禁止**。Firebase なしで残ると build 失敗を誘発
- `dev.gitlive:firebase-*` (Kotlin Multiplatform 用 Firebase wrapper) も同様に禁止 (KMP プロジェクトで紛れ込みやすい)
- 本 rule は **新規追加禁止** のみ規定、既存 import の検出と削除は `firebase-boundary.md` の責務 (改名は A3 で再評価、EPIC-A2 `decisions.md` 参照)

## 関連

- ADR 0011 (Firebase → GIS 移行)
- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0022 (Cloudflare Pages + R2)
- `.claude/rules/firebase-boundary.md` (既存 import 検出、Konsist 検証)
- `.claude/rules/{gradle,backend-auth,removed-modules}.md`
- EPIC-A2 `decisions.md` (二段運用の判断記録)
