---
id: ADR-0023
title: UI/UX をリファクタ前に DESIGN.md と UI Inventory と Roborazzi baseline で凍結する
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

# ADR-0023: UI/UX をリファクタ前に DESIGN.md と UI Inventory と Roborazzi baseline で凍結する

> **5 行以内 summary**: Phase C の大規模リファクタ (Decompose 撤去 / CMP Navigation 3 /
> Firebase 廃止 / wasmJs 化) が UI に与える影響を構造的にブロックするため、リファクタ前に
> 既存 UI/UX を機械検証可能な形で凍結する。三本柱は `DESIGN.md` (repo root の 3 階層
> design tokens) + UI Inventory (`docs/design/inventory/`) + Roborazzi baseline (mobile/desktop
> x Light/Dark の 4 パターン)。code-reviewer は 8 aspect に拡張し、本格化は A10 で実施する。

## ステータス

accepted

## コンテキスト

ColorMaster は Phase C で 4 つの大規模リファクタを並行実施する予定である:

- Decompose を撤去し Compose Multiplatform Navigation 3 に統一 (ADR-0002 / ADR-0005)
- Firebase を完全廃止して GIS に統一 (ADR-0011)
- wasmJs ターゲット化と Cloudflare Pages 配信 (ADR-0022)
- 共通 ViewModel 層への再編 (ADR-0002)

これら 4 つのリファクタはいずれも振る舞い (UI / API レスポンス / 状態遷移) を変えないことが
原則であるが、現状の ColorMaster には以下の構造的問題があり、Behavior Preservation を機械的に
保証できない:

- 色 / タイポ / スペーシング / radii が実コード内に hex / sp / dp としてハードコードされ、
  Single Source of Truth が存在しない
- 画面・コンポーネント・状態・フローの網羅的記録が無く、リファクタ前後の比較対象を人間の
  記憶に依存している
- visual regression test (screenshot test) が未導入で、リファクタによる微細な UI 退行を
  検出する機械的手段が無い
- code-reviewer の 6 aspect には UI に関わる評価軸が含まれない

Phase C のリファクタを着手する前に、UI/UX 現状を構造化データ (design tokens) + 構造化文書
(UI Inventory) + 構造化バイナリ (screenshot baseline) の三層で凍結し、リファクタ後に diff 0
であることを `code-reviewer` のサブエージェントで自動判定できるようにする必要がある。

本 ADR は、本リポジトリの草案段階に存在した 2 つの先行決定 (旧 "ui-inventory-management" と
旧 "roborazzi-visual-regression-baseline") を **統合した経緯** で起票する。前身 ADR は
物理化されていないため `supersedes` には記載しないが、UI Inventory (構造化文書) と Roborazzi
baseline (構造化バイナリ) は Behavior Preservation の機械検証を構成する 2 軸であり、別 ADR で
分割すると三層 (DESIGN.md + Inventory + Roborazzi) のどれかが欠落しても気付けない構造となるため、
本 ADR で一本化する。加えて code-reviewer aspect 拡張 (6 → 8) と `ui-snapshot` Skill 新設も
本 ADR の決定範囲に含め、三本柱と Evaluator 層 / 自動化 Skill 層を一体的に運用する
(PR #119 レトロ Try「★統合の経緯本文補足が ADR-0011 同水準まで薄め」を解消)。

## 決定

リファクタ着手前の Phase A 末尾 (A10) で、以下の三本柱と code-reviewer 拡張、`ui-snapshot`
Skill 新設を実施する。

### 三本柱

#### 1. `DESIGN.md` (リポジトリ root)

Google Stitch / Anthropic が AI 駆動 UI 開発の de facto standard としてプッシュしている
Markdown ベースの design tokens 仕様書を、ColorMaster でも採用する。

- 上部: machine-readable な design tokens を Primitive / Semantic / Component の 3 階層で記述
- 下部: human-readable な rationale (なぜその値か、どう適用するか、アクセシビリティ基準)
- Markdown は LLM が最も読みやすい形式で、AI コーディングツールが自動参照する
- Konsist で「DESIGN.md に存在しない hex code がコードに混入していない」を機械検証 (A6 で導入)

#### 2. UI Inventory (`docs/design/inventory/`)

Marcin Treder の "Interface Inventory" 手法に準拠し、全画面・全コンポーネント・全状態・
全フローを網羅キャプチャする。

- `screens/` / `components/` / `states/` / `flows/` の 4 軸でディレクトリを分割
- 各 Markdown は frontmatter で関連 SPEC-ID / 実装パス / related screenshots / related design
  tokens を記録
- 5 行 summary + 構成要素 + 状態遷移 + データソース + アクセシビリティ + 参考 screenshot +
  Open Questions の固定構造

#### 3. Visual Regression Baseline (Roborazzi)

採用ツールは Roborazzi。Compose Multiplatform 対応で、現時点で唯一 Compose Desktop に
対応している screenshot test ライブラリである。

- 主実行ランタイム: JVM (Compose Desktop)、補助: Android (Robolectric)
- 対象: commonMain の Composable (全 target 共通)
- 対象外: wasmJsMain / iosMain / androidMain の actual 実装 (Konsist + 単体テストで担保)
- 解像度マトリックス必須: モバイル (412 x 915 dp、Pixel 7 portrait 相当) と PC 16:9
  (1920 x 1080 dp、デスクトップブラウザ標準) の 2 種
- テーママトリックス必須: Light / Dark の 2 種
- 1 Composable あたり 4 パターン (mobile-light / mobile-dark / desktop-light / desktop-dark)
  が baseline として `docs/design/inventory/screenshots/` に格納される

### Roborazzi の wasmJs 未対応への対処

2026/5 時点で Roborazzi は wasmJs を未サポート (JVM 実装から Multiplatform 実装への移行中)。
本 ADR では以下の方針で対応する:

- commonMain の Composable は Compose Desktop (JVM) で screenshot 化可能。Compose は全 target
  で Skia ベースの同一レンダリングエンジンを用いるため、wasmJs 用に書いた commonMain コードも
  そのまま検証できる
- wasmJs ターゲット固有の actual 実装 (GIS 認証フロー等) は薄い層に閉じ込め、screenshot test
  の対象外として Konsist + 単体テストで担保する
- 将来 Roborazzi が wasmJs に公式対応したら本 ADR を改訂し、wasmJs ランタイムでの実機
  screenshot 化に切替を検討する

### code-reviewer aspect の拡張 (6 → 8)

既存 6 aspect (spec-conformance / test-quality / architecture / security / performance /
code-quality) に以下 2 aspect を追加する:

- **visual-regression**: Roborazzi の diff が 0 ピクセル (または許容しきい値内) であること。
  baseline 更新を伴う PR は意図的な UI 変更であることを PR description で明示し、human approve
  を必須化
- **design-tokens**: コード中の hex / sp / dp / radius がハードコードされておらず DESIGN.md の
  token を参照していること。新規 token 追加時は DESIGN.md と Rationale を同時更新

### 新規 Skill `ui-snapshot`

A10 EPIC 内の Plan、または Phase C 各リファクタ後の visual regression 検証から起動する Skill。
責務は以下:

1. Konsist で全 Composable をスキャンし、`@Preview` 不在を検出
2. Preview 追加 Plan を起票
3. Roborazzi の `recordRoborazziDebug` 相当タスクを実行して screenshot baseline 生成
4. DESIGN.md と UI Inventory のドラフト生成
5. hex / sp / dp ハードコードを検出して tokens 化提案

code-reviewer の visual-regression / design-tokens aspect と双方向参照する。

## 根拠

- **三層凍結による Behavior Preservation 構造化**: design tokens (構造化データ) + UI Inventory
  (構造化文書) + screenshot baseline (構造化バイナリ) を組み合わせることで、リファクタ前後の
  UI 同一性を 3 つの独立した検証軸から保証できる。単一軸では false-negative が残るが、
  三層で冗長化することで実用的な信頼度が得られる
- **Roborazzi 採用の必然性**: Compose Multiplatform プロジェクトで Compose Desktop に対応する
  screenshot test ライブラリは現時点で Roborazzi のみ。Paparazzi / Shot はいずれも Android
  専用で、本プロジェクトの主ターゲット (wasmJs / Desktop / Android) を網羅できない
- **DESIGN.md を root に置く理由**: Google Stitch / Anthropic が AI 駆動 UI 開発の標準として
  プッシュしており、AI Coding Agent が最初に参照する位置として最も発見性が高い。ADR-0027 の
  docs 構造規約とも整合する
- **解像度マトリックスを 2 種に絞る理由**: モバイル portrait + デスクトップ 16:9 で本プロジェクトの
  実利用シナリオ (PWA + デスクトップブラウザ) をカバーできる。タブレットや ultrawide は
  Phase C 完了後の拡張余地として保留
- **A10 で本格化する理由**: Phase C のリファクタは A10 完了が前提条件 (R-22)。A10 までに
  凍結を完了させ、Phase C 全 PR で visual-regression aspect を強制適用する

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Paparazzi | Android 環境で安定 | Compose Multiplatform 非対応 (Android のみ) | wasmJs / Desktop を網羅できないため不採用 |
| Shot | Android で実績 | Android のみ | 同上 |
| 手動スクリーンショット管理 (`docs/design/screenshots/`) | 導入コスト最小 | スケールしない、リファクタ前後の自動 diff 不可、human レビュー依存 | リファクタ規模に対して人間負荷が爆発するため不採用 |
| Visual regression を導入しない (DESIGN.md + Inventory のみ) | 学習コスト最小 | UI 退行を機械検出不能、リファクタ後の Behavior Preservation を主観判定に依存 | Phase C 4 リファクタの並行進行で Behavior Preservation が破綻するため不採用 |
| DESIGN.md を `docs/design/` 配下に置く | docs 配下に統一できる | repo root の発見性 (AI / 人間) が落ちる、Google Stitch 標準から外れる | AI コーディングツールの参照頻度を最大化するため root を採用 |

## 帰結

### Positive

- リファクタ前後の UI 同一性が 3 つの独立した軸 (tokens / Inventory / screenshot) で機械検証可能
- code-reviewer の 8 aspect 化により、UI に関わる退行を Generator/Evaluator 二段構成
  (ADR-0019) で自動ブロックできる
- DESIGN.md が root にあることで AI Coding Agent / 人間の双方が最初に発見でき、新規参加者の
  オンボーディングが平易化する
- Phase C の 4 リファクタを並行進行しても、各 PR が visual-regression aspect で個別に
  Behavior Preservation を保証されるため、結合時の UI 衝突リスクが極小化される

### Negative / トレードオフ

- screenshot baseline ファイル (PNG) が `docs/design/inventory/screenshots/` に大量 commit
  されるため、リポジトリサイズが増加する。Git LFS は未採用 (`docs/harness/plan.md` で別途
  判断、当面は通常 commit)
- Roborazzi 実行が CI 時間を延長する (commonMain Composable 数 x 4 パターン)。`./gradlew check`
  に追加されるため、CI ランタイムは段階的に最適化する (並列化 / 差分実行)
- 4 リファクタが UI に意図的変更を含む場合、baseline 更新コミットを別 PR で切り出す運用負荷が
  発生する。`.claude/rules/ui-snapshot.md` で運用フローを明文化して機械化する
- DESIGN.md 初版生成 (`ui-snapshot` Skill が A10 で自動生成) 後、human approve なしの編集を
  禁止するため、ブランドカラーの追加・修正にレビュー往復が必要になる

### Neutral / 将来の検討事項

- Roborazzi が wasmJs を公式サポートしたら本 ADR を改訂し、wasmJs ランタイムでの実機
  screenshot 化に切替を検討する
- タブレット / ultrawide 解像度の追加は Phase C 完了後の拡張案件として保留
- `changeThreshold` の許容しきい値 (誤検出抑制) は A10 の `ui-snapshot` Skill 実装時に
  Roborazzi デフォルトを基準に決定し、運用で調整する
- Component 階層の design tokens 命名規則 (アイドル別ブランドカラーの粒度) は A10 で初版を
  起こした後、Phase C の各リファクタ PR で実利用パターンを反映して洗練する

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 5. テスト戦略・品質指標の中核方針 (visual regression test の導入、code-reviewer 8 aspect 化)
- [x] 7. ハーネス本体の中核設計 (`ui-snapshot` Skill 新設、code-reviewer aspect 拡張)
- [x] 8. 複数の代替案を比較した結果としての判断 (Roborazzi / Paparazzi / Shot / 手動 / 不採用の比較)
- [x] 10. 長期的な制約 (今後 1 年以上、Phase C 全リファクタおよび Phase C 以降の UI 開発に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR が単なる Roborazzi 設定 (Plan で済む話) や DESIGN.md 表記規約
      (`.claude/rules/design-tokens.md` で済む話) に留まらず、Behavior Preservation を
      担保するハーネス構造そのものに影響する決定であることを確認した。

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤構築)
- ADR-0001 (ADR 運用基準)
- ADR-0002 (Compose Multiplatform + Navigation 3、リファクタ対象の前提)
- ADR-0006 (i18n compose resources、UI Inventory との連携)
- ADR-0019 (code-reviewer aspect 拡張、本 ADR で 6 → 8 に拡張)
- ADR-0027 (docs 構造 + 日本語化方針、DESIGN.md の位置付けと `docs/design/inventory/` の構造)
- `.claude/rules/design-tokens.md` (DESIGN.md の 3 階層構造、ハードコード禁止)
- `.claude/rules/ui-snapshot.md` (Roborazzi baseline 維持規約、4 パターンマトリックス)
- `.claude/rules/ui-inventory.md` (`docs/design/inventory/` の構造と更新規約)
- `.claude/rules/behavior-preservation.md` (リファクタ時の振る舞い維持原則)
- `.claude/rules/code-reviewer-aspects.md` (8 aspect の binary eval checklist)
- `docs/harness/plan.md` §3.9 / §6.2 A10
