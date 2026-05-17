---
id: ADR-0005
title: Decompose を撤去する
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

# ADR-0005: Decompose を撤去する

> **5 行以内 summary**: CMP Navigation 3 が全 target 対応となり、Decompose の独自
> ナビゲーションを残す積極的理由が消失した。Decompose 依存と関連コードを撤去し、
> Navigation 3 + 共通 ViewModel (ADR-0002) に統一する。撤去作業の実装は Phase C
> (EPIC-001) で実施。撤去後の構造は ADR-0003 (feature-first) に従う。
> 詳細は `docs/harness/plan.md` §3.1 / §3.6 を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster はこれまで Decompose を採用し、`ComponentContext` / `Router` の独自
抽象でナビゲーションを実装してきた。これは Compose Multiplatform の公式 navigation
が当時 web 非対応だったための妥当な選択だった。

しかし以下の変化により、Decompose を残す積極的理由が消失した:

- **CMP Navigation 3 が 1.10 以降で全 target (Android / iOS / desktop / wasmJs)
  対応となり Stable 化** (ADR-0002 で採用決定)。
- 共通 ViewModel が `androidx.lifecycle:lifecycle-viewmodel` で KMP 全 target 提供。
- Decompose と Navigation 3 の機能重複により、両者を併存させるとナビゲーション層が
  2 種類の抽象に分断される (新規 feature は Nav 3、既存は Decompose という混在)。
- Decompose の依存とビルド設定 (`com.arkivanov.decompose:*` 各種) が `build.gradle.kts`
  に残り続けると、依存解決とビルド時間が悪化する。

`docs/harness/plan.md` §3.6 で「Decompose 撤去」が撤去対象として明示されており、
本 ADR で正式に判断記録を残す。

## 決定

以下を採用する。

- **Decompose 依存を撤去する**: `com.arkivanov.decompose:*` および関連の essenty 等
  サブモジュール依存を `build.gradle.kts` から削除する。
- **Decompose 関連コードを撤去する**: `ComponentContext` / `Router` / `RootComponent`
  / 各 feature の `*Component.kt` 等の Decompose 由来コードを撤去し、ADR-0002 で
  採用した CMP Navigation 3 + 共通 ViewModel + feature-first 構造 (ADR-0003) に
  置き換える。
- **実装時期**: 撤去作業は **Phase C (EPIC-001)** で実施。Phase A / B では本 ADR
  による方針確定のみを行い、コードベース改修は Phase C で計画的に進める。

`navigation` パッケージや `Route.kt` 等の Navigation 3 移行先の構造は ADR-0003
(feature-first) および `.claude/rules/navigation.md` を参照。

## 根拠

- **公式 Stable に揃える** (ADR-0002 の判断と一体): Navigation 3 が全 target Stable
  化した時点で、サードパーティ抽象を残す理由は公式 API より将来安定するという
  根拠が必要だが、Decompose にその優位性はない。
- **混在の回避**: Nav 3 と Decompose を併存させると、新規 feature と既存 feature で
  ナビゲーション層が分断され、`code-reviewer` / Skill / 人間レビュー全てに認知負荷
  が乗る。撤去で「全 feature が同じパターン」を実現できる。
- **依存ツリーの簡素化**: `build.gradle.kts` から Decompose 関連依存を削除すること
  で、ビルド時間 / 依存解決の複雑度が下がる。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Decompose を継続採用 | 既存コードの変更ゼロ | 公式 Nav 3 との機能重複、依存維持コスト、混在発生 | ADR-0002 で公式 Nav 3 採用を決定済みで、両立する積極的理由なし |
| Decompose と Nav 3 を併存 (新規のみ Nav 3) | 段階移行が緩やか | 2 種類の抽象が永続的に残る、AI / 人間双方の認知負荷増 | 中長期で必ず統一が必要、先延ばしの利点が乏しい |
| Voyager 等の別 navigation lib に乗り換え | 既存 Decompose 撤去とほぼ同コスト | 結局サードパーティ依存、公式 Nav 3 より将来性で劣る | 公式 Nav 3 が Stable 化した以上、別ライブラリを選ぶ理由なし |

## 帰結 (Consequences)

### Positive

- ナビゲーション層が CMP Navigation 3 1 種に統一、AI / 人間双方の認知負荷が下がる。
- `build.gradle.kts` の依存リストが簡素化、ビルド時間 / 依存解決コストが減る。
- ADR-0003 (feature-first) の `Screen.kt` / `Route.kt` 構造に統一でき、Plan の
  `expected_modules` も予測しやすくなる。

### Negative / トレードオフ

- Phase C で大規模 refactor PR が発生する。EPIC-001 として段階的に進める計画が
  必要 (1 PR で全撤去は危険、feature 単位で順次移行)。
- Decompose 由来のテスト (Component の単体テスト等) は ViewModel ベースに書き直し
  が必要。
- 撤去途中の Phase B 期間中は Decompose と Nav 3 が一時的に併存する可能性があり、
  その期間の規約は EPIC-001 内 `decisions.md` で個別管理する。

### Neutral / 将来の検討事項

- Decompose 撤去の実装 PR は EPIC-001 内 Plan で feature 単位に分割。
- 撤去完了後、`.claude/rules/navigation.md` を Nav 3 単独前提に書き直す。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
- [x] 2. 主要なライブラリ / フレームワークの採用または撤去
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (一度撤去するとコードベースから完全に消える)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (`.claude/rules/adr.md` の「ADR にすべき例」に「Decompose を撤去する
      (ADR 0005)」が明示されており、本 ADR はその起票)

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤) / EPIC-001 (撤去作業の実装 Epic、Phase C で起票)
- ADR-0002 (CMP + Navigation 3 採用、本 ADR の前提)
- ADR-0003 (feature-first モジュール構造、撤去後の到達構造)
- `.claude/rules/navigation.md`
- `docs/harness/plan.md` §3.1 / §3.6
