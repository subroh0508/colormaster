---
id: ADR-0013
title: Line と Branch coverage は段階達成で 100% を目指す
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

# ADR-0013: Line と Branch coverage は段階達成で 100% を目指す

> **5 行以内 summary**: テスト存在保証の指標として line / branch coverage を採用する。
> 既存コード一斉 100% 化は非現実的なため、段階 1 (Phase A 完了: Kover 計測可能)、
> 段階 2 (Phase C 各 PR: 差分 100% 必達ゲート)、段階 3 (Phase C 完了: 全体 100%) の
> 3 段階で達成する。除外対象は本 ADR で限定列挙し、追加は ADR 改訂必須。
> 詳細規約は `.claude/rules/coverage-100.md`、計測基盤は A7 で Kover 導入。

## ステータス

accepted

## コンテキスト

ColorMaster は AI Coding Agent によるテスト自動生成を前提とするため、「テストが存在しない
コード」を機械的に検出する 1 次フィルタが必要になる。一方で、coverage 単体では仕様適合性
(指標 B / ADR-0016) もテストの意味的強度 (指標 C / ADR-0015) も担保できない。Goodhart's law
を避けるためにも、coverage は「指標」ではなく「テスト存在を保証する制約」として位置付ける
必要がある (`docs/harness/plan.md` §3.10)。

既存コード規模では一斉 100% 化は現実的でなく、Phase A 開始時点で line / branch 双方を
即時 100% 強制するとマージ不能 PR が大量発生する。Kover は計測基盤として ColorMaster の
KMP 構成と相性が良いが、差分カバレッジゲートを CI に乗せる方式は複数候補がある。

## 決定

line / branch coverage を **段階達成** で 100% に到達させる。以下 3 段階を採用する。

### 段階 1 — Phase A 完了時点

Kover プラグインを導入し、`./gradlew check` の延長で `koverHtmlReport` / `koverXmlReport`
が出力できる状態を達成する。既存コードの未カバー分は本 ADR の除外リストで一時的に逃がす。
CI は計測結果を artifact 化するに留め、ゲートは設けない。

### 段階 2 — Phase C 各 PR

当該 PR で **新規追加・変更された Kotlin 行に対して line / branch ともに 100%** を必達
ゲートとする。差分カバレッジを満たさない PR はマージ不可。差分カバレッジゲートの実装方式
は A7 で次の 3 候補から確定する。

- (a) `koverVerify` の Coverage Rule に PR diff 由来の includes を渡す Gradle カスタム
  タスク
- (b) `codecov` の patch coverage
- (c) GitHub Actions の独自スクリプト

### 段階 3 — Phase C 完了時点

全体で line / branch ともに 100% を達成する。除外は本 ADR で限定列挙した対象のみとし、
段階 1 で一時的に許容していた「既存コードの未カバー分」は Phase C の各 Epic / Plan で
段階的に解消する。

### 除外対象 (最終達成 100% でも除外し続ける)

- 自動生成コード (Compose Compiler / KSP / SQLDelight 生成クラス、kotlinx.serialization
  生成クラス)
- `@Composable` Preview / `@PreviewParameter` のみのコード
- `fun main()` を含む CLI entrypoint (`MainKt` / `Application` / `MainActivity` 等)
- DI モジュール定義 (Koin の `module {}` ブロック)
- `expect` / `actual` declaration の片側 (テスト不能な合成宣言)
- 純粋な値クラス / sealed marker (テスト不可能な合成 toString / equals のみ)

除外対象を追加するには ADR 改訂が必須。Skill が勝手に除外を増やせないよう
`.claude/rules/coverage-100.md` で禁止規約として明文化する。

## 根拠

カバレッジ単体を「品質指標」と位置付けると Goodhart's law を招き、書式上はカバレッジを
満たすが assertion が薄いテスト (tautological テスト) を量産する力学が働く。一方で
カバレッジを廃止すると「テスト書き忘れ」「デッドコード」を機械的に検出する手段を失う。
よって本 ADR は coverage を **「テスト存在を保証する制約」** に再定義し、仕様適合性
(指標 B) と意味的強度 (指標 C) は別 ADR で担当する分担構造を採用する。

段階達成にする理由は、既存コードを一斉に 100% 化する PR は (1) 差分が肥大化し
`code-reviewer` が機能しない、(2) AI が低品質テストを大量に生成するリスクが高い、
(3) 仕様 ID 不在のテストが追加されると指標 B との整合性が崩れる、の 3 点による。

### 達成するアウトカム

- O1. テスト書き忘れの機械的検出 (特に AI 駆動で重要)
- O2. デッドコード / 到達不能コードの検出
- O3. リファクタリング時のセーフティネット
- O4. AI が「テストゼロのコード」を生成することの 1 次フィルタ
- O5. CI 上の欠陥検出効果との統計的相関 (statement coverage と mutation kill
  effectiveness の有意な正の相関)
- O6. コードレビュー支援 (新規行で未テストの箇所を CI が指摘)
- O7. 使用例ドキュメントとしての最低保証
- O8. テスト容易性を要求する設計圧
- O9. 新規モジュール導入時の基準明示

### 達成しないアウトカム

- 仕様適合性 (→ ADR-0016 / 指標 B)
- エッジケース・例外パスの網羅 (→ ADR-0016 / ADR-0015)
- テストの意味的強度 (→ ADR-0015 / 指標 C)

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 全体 100% 即時必達 | シンプル | 既存コードで PR がマージ不能、AI が低品質テストを量産 | 段階達成を採用 |
| 80% 等の閾値固定 | 目標がぶれない | Goodhart's law、未テスト 20% の品質保証が出来ない | カバレッジを「制約」に再定義 |
| カバレッジ廃止 | Goodhart's law 完全回避 | テスト書き忘れの 1 次フィルタを失う | 制約として最小限採用 |
| JaCoCo 採用 | 業界標準 | KMP の `commonMain` を直接計測できない | Kover が KMP と相性良 |

## 帰結

### Positive

- AI が生成したテストの「書き忘れ」を CI で機械的に検出可能。
- 差分カバレッジゲート (段階 2) により、PR 単位で確実にカバレッジを底上げできる。
- 除外対象が ADR で限定列挙されるため、Skill が勝手に除外を増やすことを構造的に防止。

### Negative / トレードオフ

- 段階 2 の差分カバレッジゲートは AI に対し「テスト書け」の圧力を生むため、tautological
  テストが増えるリスクがある。→ ADR-0015 (mutation) と ADR-0016 (Spec) で別軸の抑制効果。
- 既存コードの除外リストは Phase C 末まで残存し、その間「全体カバレッジ」表示は誤読を
  招きうる。→ 段階 3 達成時点で除外リストが ADR 0013 列挙分のみになっていることを確認。
- 差分カバレッジゲートの実装方式が A7 内で未確定 (3 候補)。→ A7 内で確定する。

### Neutral / 将来の検討事項

- 差分カバレッジゲートの実装方式は A7 で確定後、本 ADR は不変、選択結果は
  `.claude/rules/coverage-100.md` および runbook (`docs/runbooks/testing.md`) で記録。
- Compose UI モジュールの coverage は screenshot test との組合せで担保 (ADR 0023 と
  併読)。Compose UI Test + Roborazzi での到達範囲は §3.10 / §3.9 を参照。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 5. テスト戦略・品質指標の中核方針
- [x] 8. 複数の代替案を比較した結果としての判断 (全体即時 / 閾値固定 / 廃止 / JaCoCo の比較)
- [x] 9. 元に戻すコストが高い決定 (段階達成計画は全 Phase C Plan に影響)
- [x] 10. 長期的な制約 (Phase C 完了まで効力を持つ判断)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR が単なるカバレッジ閾値設定 (Plan / runbook で済む話) ではなく、テスト戦略
      の中核方針として ADR にふさわしい決定であることを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0004 (テスト戦略総論、三層指標のインデックス)
- ADR-0015 (Mutation testing / 指標 C)
- ADR-0016 (Spec coverage / 指標 B)
- ADR-0023 (UI 凍結三本柱、Roborazzi baseline)
- `.claude/rules/coverage-100.md` (Line/Branch 段階達成規約、A7 で導入)
- `.claude/rules/kotlin-test.md`
- `docs/harness/plan.md` §3.10 指標 A / §1.2 / §6.2 A7
- `docs/runbooks/testing.md` (三層指標の運用、Phase A で本格化)
