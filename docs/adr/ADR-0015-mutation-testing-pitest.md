---
id: ADR-0015
title: テスト意味的強度を PITest による mutation testing で計測する
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

# ADR-0015: テスト意味的強度を PITest による mutation testing で計測する

> **5 行以内 summary**: line / branch 100% でも assertion 不足の tautological テストが
> 残るリスクを構造的に検出するため、PITest + pitest-kotlin + gradle-pitest-plugin を
> 採用する。KMP の JVM target 経由で `commonMain` / `jvmMain` / `androidMain` を mutate
> し、PR コメントで mutation score を可視化する。**CI ゲートにはせず**、改善は KPT 起点で
> 継続課題化する。A7 で本格化、A9 で baseline を記録する。

## ステータス

accepted

## コンテキスト

ADR-0013 で line / branch coverage を段階達成 100% に到達させる方針を採用したが、
カバレッジは「テストが存在すること」しか保証しない。実際には以下の劣化パターンが残る:

- assertion を持たないテスト (`fun test() { service.run() }` のみ等)
- mock 過多で実装を全く呼ばないテスト
- catch 節を握り潰すテスト
- ハッピーパス偏重で分岐の意味を検証しないテスト

これらは line / branch 上は 100% でも、実装の意味的な変更を検出できない (= tautological)。
AI 駆動で大量にテストが生成される環境では、生成テストが「カバレッジを満たすが意味的に
効かない」状態に陥るリスクが特に高い。Meta JiTTests 研究では mutation testing 併用で
欠陥検出効果が約 4x になることが示されており、coverage と独立した「意味の指標」が必要。

Kotlin 対応の mutation testing ツールには複数候補があるが、本格運用に耐える mature な
選択肢は PITest + pitest-kotlin に限られる。Stryker は Kotlin 未対応で TS 中心、Pitest
フォークは実験的、手動レビューは機械化されない。

## 決定

ColorMaster のテスト意味的強度の計測手段として **PITest + pitest-kotlin +
gradle-pitest-plugin** を採用する。

### 計測対象

- KMP の **JVM target 経由** で `commonMain` + `jvmMain` (backend) + `androidMain` の
  クラスを mutate
- `jsMain` / `wasmJsMain` / `iosMain` の actual 実装は PITest 対象外。これらは Konsist
  + 通常単体テストで担保する

### 運用方針

- PR コメントで mutation score を可視化
- **CI ゲートにはしない** (Goodhart's law を避け、シグナル可視化に留める)
- mutation score が低い領域は KPT で learning に蓄積し、`.claude/rules/kotlin-test.md`
  を強化していくフォールバックループで対処
- Phase A の A7 で本格化、A9 で baseline 記録 (将来比較の基準点)

### 達成するアウトカム

- assertion 不足の検出 (line / branch coverage では捕捉不可能)
- テストの意味的強度シグナル (PR レビューの参考情報)
- 明らかな tautological テストの learning 蓄積による、ルール側の改善ループ起動

### 達成しないアウトカム

- テスト存在の保証 (→ ADR-0013 / 指標 A)
- 仕様適合性 (→ ADR-0016 / 指標 B)

## 根拠

mutation testing を **CI 必達ゲートにしない** 判断は、Goodhart's law (測定が目標化される
と指標としての意味を失う) を避けるため。mutation score を必達にすると AI は mutation を
殺すだけの assertion を量産し、仕様適合性と無関係な「形だけの強化」が増える。
仕様適合性は ADR-0016 (指標 B) が直接担当し、本 ADR は「無意味テストのシグナル可視化」と
ルール改善ループの起点に責務を限定する。

PITest を選定した理由は Kotlin 対応 mature ツールが事実上 PITest のみで、JVM target
経由で `commonMain` を mutate する経路が確立しているため。MutFlow (2026 年登場の K2
compiler plugin ベース、KMP 全 target 適合の可能性) は将来別 ADR で評価する余地として
記録する。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Stryker | mutation testing の有名実装 | Kotlin 未対応、TS 中心 | 言語適合性が無い |
| PITest フォーク | Kotlin 特化 | 実験的、メンテ不安定 | mature な PITest 本体を採用 |
| 手動コードレビューのみ | 導入コストゼロ | 機械化されない、AI 駆動と非整合 | 機械シグナルが必須 |
| mutation testing 必達ゲート化 | 強い強制力 | Goodhart's law、assertion 水増しを誘発 | シグナル可視化に留める |
| MutFlow (将来候補) | KMP 全 target 適合の可能性 | 2026 年新規、評価データ不足 | 将来別 ADR で評価 |

## 帰結

### Positive

- line / branch 100% でも残る tautological テストを構造的に検出可能。
- AI 生成テストの assertion 不足を mutation score で可視化、KPT 起点で
  `.claude/rules/kotlin-test.md` 改善ループに接続できる。
- CI ゲート化を回避するため、Goodhart's law を構造的に防止。

### Negative / トレードオフ

- 必達ゲートでないため、mutation score が低いまま放置されるリスクがある。→ KPT で
  learning 蓄積 + ルール強化のフォールバックループで対処。
- PITest 実行時間が長い (commonMain + jvmMain + androidMain を JVM 経由で mutate)。
  → PR 毎ではなく週次バッチや changed-files limited 実行など、A7 で運用方式を確定。
- `jsMain` / `wasmJsMain` / `iosMain` は計測対象外。→ Konsist + 通常単体テストで補完。

### Neutral / 将来の検討事項

- A9 で baseline mutation score を記録し、Phase C 各 PR の傾向比較に用いる。
- MutFlow が安定化すれば KMP 全 target 対応に向けて別 ADR で再評価。
- mutation score 低下が継続的に観測される領域は `harness-meta` 経由でルール強化提案を
  起票する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 2. 主要なライブラリ / フレームワークの採用 (PITest + pitest-kotlin +
      gradle-pitest-plugin)
- [x] 5. テスト戦略・品質指標の中核方針 (三層指標の指標 C)
- [x] 8. 複数の代替案を比較した結果としての判断 (Stryker / PITest フォーク / 手動 /
      必達ゲート化 / MutFlow の比較)
- [x] 10. 長期的な制約 (Phase A 以降のテスト評価軸として継続)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR が単なるツール導入手順
      (runbook で済む話) ではなく、テスト戦略の意味的強度指標として ADR にふさわしい
      決定であることを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0004 (テスト戦略総論、三層指標のインデックス)
- ADR-0013 (Line / Branch coverage 段階達成 / 指標 A)
- ADR-0016 (Spec coverage / 指標 B)
- `.claude/rules/mutation-testing.md` (PITest 運用規約、A7 で導入)
- `.claude/rules/kotlin-test.md`
- `docs/harness/plan.md` §3.10 指標 C / §6.2 A7 / A9
- `docs/runbooks/testing.md` (三層指標の運用)
