---
id: ADR-0002
title: Compose Multiplatform と共通 ViewModel と Navigation 3 を採用する
status: accepted
date: 2026-05-17
related_epics:
  - EPIC-000
related_plans:
  - PLAN-001
related_specs: []
superseded_by: null
supersedes: null
---

# ADR-0002: Compose Multiplatform と共通 ViewModel と Navigation 3 を採用する

> **5 行以内 summary**: UI / 状態管理 / Navigation を全 target で共通化するため、
> Compose Multiplatform (CMP) + 共通 ViewModel + Navigation 3 を採用する。
> 状態管理は `StateFlow<UiState>` + `onAction(UiAction)` + `Channel<UiEffect>` の軽量
> UDF、DI は Koin 4.0.4 を継続。Android / iOS / desktop / wasmJs を単一実装で支える。
> 詳細は `docs/harness/plan.md` §3.1 を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster は Android / iOS / web (旧 js/app) / desktop の複数 target を持つ
Kotlin Multiplatform プロジェクトであり、これまで UI 層は Compose Multiplatform を
採用しつつもナビゲーションは Decompose に依存していた。状態管理パターンも
feature ごとに揺れがあり、共通化されていない。

近年の CMP エコシステムには以下の変化があった:

- **CMP Navigation 3** が 1.10 以降で Android / iOS / desktop / web 全 target に
  対応 (公式 Stable)。これまで「web 非対応」を理由に Decompose を残していた根拠が
  消失した。
- **共通 ViewModel** は `androidx.lifecycle:lifecycle-viewmodel` 経由で KMP 全 target に
  提供され、`SavedStateHandle` も含めて共通化可能。
- **Compose for iOS** が 1.8.0 で Stable 到達 (production-ready 表明)。
- **Koin 4.0.4** が KMP 全 target を継続サポート、`koin-compose` で `@Composable` 内
  注入も成熟。

これらにより「UI / ViewModel / Navigation を 1 セットの共通コードで全 target に
出す」が現実的になり、Decompose の独自抽象を維持するコストの方が大きくなった。

## 決定

以下の構成を採用する。

- **UI**: Compose Multiplatform を全 target (Android / iOS / desktop / wasmJs) で採用。
- **共通 ViewModel**: `androidx.lifecycle:lifecycle-viewmodel` の KMP target を採用、
  `commonMain` に ViewModel を置く。
- **Navigation**: CMP Navigation 3 を採用、`commonMain` で全 target 共通の Nav graph
  を記述する。Decompose は撤去する (詳細 ADR-0005)。
- **状態管理**: `StateFlow<UiState>` + `fun onAction(UiAction)` +
  `Channel<UiEffect>` の軽量 UDF パターンを feature 単位で統一。
- **DI**: Koin 4.0.4 を継続採用、ViewModel と Repository を Koin module で配線。

ファイル配置と命名は ADR-0003 (feature-first モジュール構造) に従う。

## 根拠

- **全 target 1 実装で済む**: Navigation 3 + 共通 ViewModel の組合せで、Android/iOS/
  desktop/wasmJs の各 target に固有のナビゲーション層を書く必要がなくなる。Decompose
  の `ComponentContext` / `Router` 抽象を維持するメンテコストを丸ごと削除可能。
- **公式 Stable に揃える**: Navigation 3 と Compose for iOS が共に Stable 到達した
  タイミングで、サードパーティ抽象 (Decompose) より公式 API に賭ける方が将来の
  破壊的変更に追随しやすい。
- **UDF パターン統一**: `UiState` / `UiAction` / `UiEffect` の 3 つで責務を明確化、
  AI 駆動のテスト生成・コードレビューが構造化された対象を扱える。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Decompose を継続 | 既存コードの変更最小 | web 対応を独自に維持、CMP Navigation 3 と機能重複 | 公式 Navigation 3 が全 target Stable になった以上、独自抽象を残す積極的理由なし |
| Voyager (Compose 用 navigation lib) | API が簡潔 | Navigation 3 とエコシステム重複、公式サポートで劣後 | 公式 Navigation 3 に統一する方が長期保守性で有利 |
| MVI フル実装 (Orbit / MVIKotlin) | 状態管理が形式化 | ボイラープレート過多、AI が読み解きにくい | 軽量 UDF (State + Action + Effect) で十分、フル MVI は over-engineered |
| 全 target を Compose 化せず iOS は SwiftUI 維持 | iOS native UX 最大化 | 二重実装、デザイントークン同期コスト大 | Compose for iOS が Stable 到達、二重実装の理由は弱い |

## 帰結 (Consequences)

### Positive

- 全 target を単一の Composable + ViewModel + Nav graph で記述でき、feature の
  追加コストが劇的に下がる。
- UDF パターン統一により、テストパターン (`ViewModelSpec`, `ScreenSnapshotTest` 等)
  が画一化、AI 自動生成テストの精度が上がる。
- Decompose 依存撤去で `build.gradle.kts` の依存ツリーが簡素化、ビルド時間も短縮。

### Negative / トレードオフ

- 既存 Decompose 依存コードの撤去 (ADR-0005 / EPIC-001) が必要、大規模 refactor PR が
  発生する。
- iOS で Compose を採用するため、iOS ネイティブ UX (UIKit ベースの細部) を再現する
  際にカスタム実装が必要なケースがある。
- `androidx.lifecycle:lifecycle-viewmodel` の KMP target は依然として比較的新しく、
  edge case の bug 報告が今後発生する可能性がある (公式 issue tracker を monitoring)。

### Neutral / 将来の検討事項

- Navigation 3 の API は 1.x のうちは互換性が保たれる前提だが、2.x で破壊的変更が
  入る場合は本 ADR を改訂する。
- Compose UI Test / Roborazzi のスナップショット基盤は ADR-0023 と連動して整備。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
- [x] 2. 主要なライブラリ / フレームワークの採用または撤去
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定
- [x] 10. 長期的な制約 (今後 1 年以上、全 feature 実装に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (`.claude/rules/adr.md` の「ADR にすべき例」に「Compose Multiplatform +
      共通 ViewModel + Navigation 3 を採用する (ADR 0002)」が明示されており、本 ADR
      はその起票)

## 関連

- 関連 Plan: PLAN-001 (ADR 起草 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤構築)
- ADR-0003 (feature-first モジュール構造、本 ADR と一体運用)
- ADR-0005 (Decompose 撤去、本 ADR を受けた具体策)
- ADR-0006 (i18n、CMP Resources を採用する根拠と連動)
- `.claude/rules/{viewmodel,ui-state,composable,navigation}.md`
- `docs/harness/plan.md` §3.1
