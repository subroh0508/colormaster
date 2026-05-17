---
id: ADR-0006
title: i18n には compose-multiplatform-resources を採用する
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

# ADR-0006: i18n には compose-multiplatform-resources を採用する

> **5 行以内 summary**: wasmJs 対応の必要性から、i18n は
> `composeResources/values-<locale>/strings.xml` (compose-multiplatform-resources) を
> 採用する。JS/Wasm でも非同期に resource を読める設計が公式に明示されており、
> Lyricist / i18n4k は wasmJs 公式対応の言及なし。
> 詳細は `docs/harness/plan.md` §3.1 を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster は Android / iOS / desktop / wasmJs の複数 target を持つ KMP プロジェクト
であり、Web 配信は ADR-0012 で js/app を撤去後、wasmJs に移行する方針が決まっている。
これにより i18n (国際化) の実装方式は **wasmJs 公式対応** を必須要件とする。

KMP 向け i18n ライブラリの選択肢:

- **compose-multiplatform-resources**: JetBrains 公式、`composeResources/values-<locale>/strings.xml`
  形式。JS/Wasm でも非同期に resource を読める設計が公式に明示されている。
- **Lyricist**: Android 中心の DSL ベース i18n、KMP 対応はあるが wasmJs 公式対応の
  言及なし。
- **i18n4k**: KMP 対応の i18n ライブラリ、 wasmJs 公式対応の言及なし。

加えて、ADR-0002 で Compose Multiplatform 全 target 採用が決定しているため、
CMP 公式が提供する resource システムに合わせる方が以下の点で有利:

- Compose で `stringResource(Res.string.<key>)` の統一 API を全 target で使える。
- CMP のバージョンアップに自動追随、外部ライブラリのメンテナンスリスクなし。
- Wasm 対応の非同期 resource ロードが組み込み済み (suspend / async API を意識せず
  使える)。

## 決定

i18n に **compose-multiplatform-resources** を採用する。

- **配置**: `<module>/src/commonMain/composeResources/values-<locale>/strings.xml`
  形式で各ロケールの文字列を定義する。
- **アクセス**: Composable 内では `stringResource(Res.string.<key>)`、非 Composable
  からは生成された `Res` クラス経由で suspend 関数を呼ぶ。
- **対象 target**: Android / iOS / desktop / wasmJs 全 target で同一の resource
  ファイルを共有する。

文字列キー命名規約 / locale fallback / pluralization 等の運用詳細は
`.claude/rules/i18n.md` を Single Source of Truth とする。

## 根拠

- **wasmJs 公式対応**: JS / Wasm でも非同期に resource を読める設計が JetBrains
  公式 docs に明示されており、Lyricist / i18n4k に対して明確な差別化要因となる。
- **CMP との一体化**: ADR-0002 で CMP 全 target 採用を決めた以上、CMP 公式が提供する
  resource システムに乗る方が依存ツリーが小さく、CMP のメジャーアップにも追随しやすい。
- **`stringResource()` API の統一**: 全 target で同じ Composable 内 API を使えるため、
  AI Skill / 人間レビューの認知負荷が下がる。
- **Lyricist / i18n4k は wasmJs 公式対応の言及なし**: 採用するとリスクとして
  wasmJs 移行時に置換が必要になる可能性がある。先回りで CMP Resources に統一する
  方が長期保守性で有利。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Lyricist (DSL ベース) | DSL が型安全で使いやすい | wasmJs 公式対応の言及なし、サードパーティ依存 | wasmJs 必須要件を満たさない |
| i18n4k | KMP 対応、コード生成あり | wasmJs 公式対応の言及なし、サードパーティ依存 | 同上 |
| 自前実装 (`Map<String, String>` を locale 別に保持) | 依存ゼロ、シンプル | locale fallback / pluralization / 大規模時のメンテで荒れる | CMP Resources で十分賄える |
| Android resources (`strings.xml`) + iOS Strings 別管理 | 各 native の慣習に従う | 同一文字列を二重管理、wasmJs 対応コードを別途書く必要 | CMP Resources で全 target 統一できるため不要 |

## 帰結 (Consequences)

### Positive

- 全 target で同一の resource ファイル + 同一 API (`stringResource`) を使えるため、
  AI / 人間双方の認知負荷が最小化される。
- wasmJs 移行時 (ADR-0012 後) に i18n 実装を書き直す必要なし、Wasm 対応が組み込み
  済み。
- locale 追加が `composeResources/values-<locale>/strings.xml` 1 ファイルの追加で
  完結し、Plan の `expected_modules` も予測しやすい。

### Negative / トレードオフ

- Compose Multiplatform のバージョンアップに依存するため、resource システムの破壊
  的変更があった場合は全 target に影響する。
- 文字列のホット reload が CMP の resource 生成タイミングに依存し、開発時の DX
  はネイティブ Android resources よりわずかに劣る場合がある。

### Neutral / 将来の検討事項

- 翻訳ワークフロー (Crowdin / Lokalise 等の外部サービス連携) は将来別途検討。
  現時点は owner 単独運用のため、`strings.xml` 直接編集で十分。
- Pluralization (`plurals.xml`) / 引数フォーマット (`%s` / `%d`) の運用規約は
  `.claude/rules/i18n.md` で確定する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 2. 主要なライブラリ / フレームワークの採用または撤去
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (resource ファイルの形式変更は全文字列に波及)
- [x] 10. 長期的な制約 (今後 1 年以上、全 feature の文字列定義に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (i18n ライブラリ選定は §4.5 項 2「主要なライブラリの採用」に直接
      該当、`.claude/rules/i18n.md` のコーディング規約だけでは依存選定の判断記録に
      ならない)

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0002 (CMP 採用、本 ADR の前提)
- ADR-0012 (js/app 撤去後の wasmJs 移行、本 ADR の wasmJs 要件根拠)
- `.claude/rules/i18n.md`
- `docs/harness/plan.md` §3.1
