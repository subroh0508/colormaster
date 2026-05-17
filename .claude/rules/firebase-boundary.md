---
id: rules-firebase-boundary
title: 既存 Firebase import の境界検出
status: stable
last_updated: 2026-05-17
paths:
  - "**/*.kt"
  - "**/*.kts"
  - "firebase.json"
  - ".firebaserc"
  - "**/google-services.json"
related_adrs:
  - ADR-0011
---

# firebase-boundary.md — 既存 Firebase import の境界検出

> ADR 0011 (Firebase → GIS 移行) に伴う **既存 Firebase 依存の検出と段階的撤去** を担う規約。
> 新規追加禁止は `.claude/rules/no-firebase.md` の責務 (Skill 事前ガード)、本 rule は
> **既存 import の検出と移行進捗の管理** (Konsist による事後検証) を担当する。
> 二段運用と改名再評価方針は EPIC-A2 `decisions.md` 参照。

## 既存 Firebase 関連の所在 (移行対象)

| 種別 | 場所 | 対応 |
|---|---|---|
| Firebase Authentication (旧 SDK) | `core/network/auth/*` (一部) | GIS に置き換え (ADR 0011)、`core/network/auth/` 内の Firebase 依存を撤去 |
| Firebase Firestore | `core/network/firestore/*` | Backend SQLite + Litestream + R2 に置き換え (`removed-modules.md`) |
| `firebase.json` / `.firebaserc` | リポジトリ root | Cloud Run + Cloudflare Pages 移行完了時に削除 (ADR 0022, 0009) |
| `google-services.json` | `android/app/` (もし存在) | Firebase SDK 撤去時に削除 |
| `com.google.gms.google-services` plugin | `android/app/build.gradle.kts` | 同上 |

## 検出パターン (Konsist)

`core/**/*.kt` / `feature/**/*.kt` で以下の import が存在する箇所を **エラーとして検出**:

| パターン | 例 |
|---|---|
| `com.google.firebase.*` | `import com.google.firebase.auth.FirebaseAuth` |
| `dev.gitlive.firebase.*` | `import dev.gitlive.firebase.auth.FirebaseAuth` (KMP Firebase wrapper) |
| `com.google.android.gms.tasks.*` | (Firebase の Task API) |

## エスケープ (移行進行中の例外)

完全撤去までの間、以下を **ホワイトリストとして許容**:

```yaml
# .claude/rules/firebase-boundary.allowlist.yaml (将来配置候補)
allowlist:
  - module: core/network/firestore
    reason: ADR 0011 移行中、Phase C5 (Litestream 完成) で撤去予定
    until: 2026-12-31
```

- ホワイトリストは **撤去予定日 (`until`) と理由必須**
- Konsist 検証で allowlist 外の import を error、allowlist 内は warning (期限切れ時 error 化)
- ホワイトリスト追加は人間レビューで承認必須 (auto-approve 禁止)

## 撤去フロー

1. 撤去対象 module を確定 (例: `core/network/firestore`)
2. 代替実装を `core/data/*` または別 module に作成
3. 既存 ViewModel / Repository の依存を切り替え
4. 旧 module の `build.gradle.kts` から Firebase dependency 削除
5. import 残骸を Konsist で検証
6. ADR 0011 / `removed-modules.md` を更新
7. PR で削除確定

## 機械検証 (A6 で導入)

- **Konsist** で以下を検証 (R-22):
  - `core/**/*.kt` / `feature/**/*.kt` の import 文に `com.google.firebase.*` / `dev.gitlive.firebase.*` が含まれない (allowlist 例外あり)
- **Gradle カスタムタスク** で:
  - `firebase.json` / `.firebaserc` / `google-services.json` の存在検出 (撤去 PR まで warning、撤去後は error)
  - `gradle/libs.versions.toml` に Firebase coordinate が含まれない (`no-firebase.md` と統合)

## `no-firebase.md` との責務分担

| ファイル | 責務 | 検出タイミング |
|---|---|---|
| `no-firebase.md` | **新規追加禁止** (build 系 / Skill 起草時の事前ガード) | Skill 起草段階 / build script 検証 |
| `firebase-boundary.md` (本 rule) | **既存 import 検出** (Kotlin source / 設定ファイル) | Konsist による事後検証 |

二段運用と改名再評価方針は EPIC-A2 `decisions.md` 参照。

## Gotchas

- **Konsist は Kotlin file 専用** (`docs/harness/plan.md` §5.2)、`.json` / `.kts` の検出は Gradle カスタムタスクに分離
- **import alias で偽装した Firebase 参照も検出**: `import com.google.firebase.auth.FirebaseAuth as FA` 形式も Konsist で検出可能
- **transitively pulled Firebase**: 他 lib (Crashlytics 等) が Firebase を transitive dependency として持ち込むケースは `./gradlew :module:dependencies` で確認
- 撤去完了後は本 rule の `paths` から該当 file pattern を削除可能 (`firebase.json` 等が存在しない状態を期待)

## 関連

- ADR 0011 (Firebase → GIS 移行)
- `.claude/rules/{no-firebase,removed-modules,backend-auth}.md`
- EPIC-A2 `decisions.md` (二段運用の判断記録)
- `docs/architecture/infrastructure.md` (A2-5 で本格化、Firebase 撤去後の構成図)
