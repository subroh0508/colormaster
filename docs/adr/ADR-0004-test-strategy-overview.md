---
id: ADR-0004
title: テスト戦略は三層指標 (line/branch と Spec と mutation) の併用とする
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

# ADR-0004: テスト戦略は三層指標 (line/branch と Spec と mutation) の併用とする

> **5 行以内 summary**: テスト品質を 3 つの独立した指標で多層検証する。指標 A
> (line/branch coverage) はテスト存在保証、指標 B (Spec coverage) は仕様適合性、
> 指標 C (mutation score) は意味的強度をそれぞれ別軸で担う。互いに代替不可、
> いずれも別 ADR で詳細化する。本 ADR は総論 index として位置付け、詳細は
> ADR-0013 (指標 A) / ADR-0016 (指標 B) / ADR-0015 (指標 C) を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster は AI 駆動テスト生成を前提とするため、テスト品質を 1 つの指標
(例: line coverage 100%) で測ろうとすると、AI が「指標を満たすだけのテスト」を
生成し、Goodhart's law (測定が目標化されて指標としての意味を失う) に陥る危険が
高い。実際、coverage 100% / mutation score 4% のような無意味テスト群は AI 駆動
開発のアンチパターンとして既知。

一方で、これら 3 つの指標は **互いに代替不可能**:

- line / branch coverage は「テストが存在するか?」しか測れない (仕様適合・意味的
  強度を保証しない)。
- Spec coverage は「仕様 ⇄ テスト対応」を保証するが、テストの中身が tautological
  でも通る。
- mutation score は「テストが意味的に効くか?」を測るが、計測コストが高く CI ゲート
  にすると AI が gaming する余地が大きい。

そのため、テスト戦略の総論として「3 指標を別軸で運用、それぞれが別アウトカムを
担う」ことを ADR で明示する必要がある。詳細な指標ごとの運用 (除外対象 / CI ゲート
条件 / 段階達成) は各論 ADR に委譲する。

## 決定

テスト戦略の中核として **三層指標 (指標 A / B / C) の併用** を採用し、本 ADR を
総論 index として位置付ける。各論は別 ADR で詳細化する。

- **指標 A — Line / Branch Coverage** (テスト存在保証): 段階達成。Phase A 完了時点
  で Kover 計測可能、Phase C 各 PR で差分 100%、Phase C 完了で全体 100%。詳細は
  ADR-0013。
- **指標 B — Spec Coverage** (仕様適合性): 新規機能で必達 CI ゲート、既存機能は
  Phase C 段階達成。`@Spec("SPEC-NNN-N")` annotation + Konsist で機械検証。詳細は
  ADR-0016。
- **指標 C — Mutation Score** (意味的強度): PR コメントで可視化のみ、必達 CI ゲート
  にしない (Goodhart's law 回避)。PITest + pitest-kotlin + gradle-pitest-plugin、
  JVM target 経由。詳細は ADR-0015。

3 指標はそれぞれが独立したアウトカムを担い、相互代替不可。指標 A だけで通る PR は
仕様 (B) / 意味 (C) を担保しない、指標 B だけで通る PR は実行範囲 (A) / 意味 (C) を
担保しない、という非可換性を運用前提とする。

## 根拠

- **Goodhart's law 回避**: 1 指標で測ると AI が gaming する。3 指標で多層化すると、
  どの軸でも gaming するコストの方がまっとうに書くコストを上回るため、自然と「意味
  ある」テストに向かう。
- **アウトカムの分担**: テスト存在 (A) / 仕様適合 (B) / 意味的強度 (C) は本質的に
  異なるアウトカム。1 指標で代替不可能。
- **CI ゲートの強度を分ける**: 計測コストと gaming リスクに応じて、指標 A は段階達成
  ゲート、指標 B は必達ゲート、指標 C は可視化のみ、と強度を変える。これにより
  Goodhart's law の影響を最小化しつつ、必要箇所での強制力を保つ。
- **総論を ADR 化する理由**: 個別指標 (A/B/C) の各論だけ ADR 化すると、3 つの間の
  関係性 (代替不可、CI ゲート強度の差) が明文化されない。総論 ADR を index として
  置くことで、各論を参照する Skill / Plan が一貫した文脈で扱える。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| line coverage のみで運用 | 設定が単純、ツール成熟 | 仕様適合・意味的強度を担保せず、AI による gaming が容易 | 1 指標で AI 駆動の品質保証は不可 |
| Spec coverage のみで運用 | 仕様 ⇄ テスト対応が明示 | 仕様 ID が振られない箇所を機械検出できず、tautological テストも通る | 仕様外の実装パスをカバーできない |
| mutation score を必達 CI ゲートにする | 意味的強度を強制 | 計測コスト大 + AI が mutation を回避するテストを書く gaming リスク | シグナル可視化に留め、強制は B 側で行う |
| coverage を「指標」ではなく「制約」と再定義 | Goodhart's law 回避の理論基盤 | 単体では仕様 / 意味を担保しない | 本 ADR で「制約 A + 指標 B + 指標 C」の総論として採用 |

## 帰結 (Consequences)

### Positive

- 1 指標 gaming の構造的回避。AI が 3 指標を同時に満たそうとすると、自然と「意味
  ある」テストを書くインセンティブが働く。
- 各論 ADR (0013 / 0015 / 0016) との関係性が index として明確化、Skill が一貫した
  文脈で指標を扱える。
- KPT (learning) で「mutation score が低い領域」「Spec coverage gap」等を 3 軸で
  別個に蓄積でき、改善方向が分散ノイズにならない。

### Negative / トレードオフ

- 3 指標の計測基盤 (Kover / Konsist + `@Spec` / PITest + pitest-kotlin) を全部
  整備する必要があり、Phase A の A7-A9 でまとめて投入するコストがある。
- 3 指標を AI に同時意識させると、PR description / レビューコメントで触れる項目が
  増え、`code-reviewer` 8 aspect の入力サイズも増える。

### Neutral / 将来の検討事項

- 将来 **MutFlow** (K2 compiler plugin ベース、KMP 全 target 適合の可能性) が
  Stable 化したら、指標 C の実装を PITest から MutFlow に置換する余地を残す
  (別 ADR で評価)。
- Phase A 完了時点では既存コードの未カバー分が指標 A の除外リストに含まれるが、
  Phase C で段階解消する (詳細 ADR-0013)。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 5. テスト戦略・品質指標の中核方針
- [x] 7. ハーネス本体の中核設計 (AI 駆動テスト生成と gaming 防止の構造)
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 10. 長期的な制約 (今後 1 年以上、全テスト規約の前提)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (`.claude/rules/adr.md` の「ADR にすべき例」に「Line/Branch coverage
      を段階達成にする (ADR 0013)」が明示、本 ADR は各論を束ねる総論 index として
      §4.5 項 5 / 7 / 8 / 10 を満たす)

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0013 (指標 A: Line / Branch Coverage 段階達成の詳細)
- ADR-0016 (指標 B: Spec Coverage の詳細)
- ADR-0015 (指標 C: Mutation Score の詳細)
- `.claude/rules/{coverage-100,spec-traceability,mutation-testing,kotlin-test,test-paired-class}.md`
- `docs/harness/plan.md` §3.10
