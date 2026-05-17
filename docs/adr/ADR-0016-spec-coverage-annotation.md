---
id: ADR-0016
title: 仕様適合性を @Spec annotation と Konsist で追跡する
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

# ADR-0016: 仕様適合性を @Spec annotation と Konsist で追跡する

> **5 行以内 summary**: 仕様適合性 (Acceptance criteria の充足) は line / branch
> coverage でも mutation score でも担保不能なため、テストに `@Spec(SpecId)` annotation を
> 付与し、Konsist で「全 Acceptance criteria に対応する `@Spec` 付きテストが存在する」
> ことと「実装 ⇄ テスト ⇄ 仕様 ID の双方向トレーサビリティ」を機械検証する。
> 新規機能は Spec coverage 100% を必達ゲート、既存機能は Phase C 段階達成とする。

## ステータス

accepted

## コンテキスト

ADR-0013 (line / branch coverage 段階達成 / 指標 A) と ADR-0015 (mutation testing /
指標 C) を組合せても、テストが **仕様に対応しているか** は担保できない。具体的には:

- 全行をテストしているがユーザー要件と無関係なケースのみ検証している
- mutation を殺せているが Acceptance criteria を 1 つも反映していない
- 仕様変更時に「どのテストを修正すれば影響範囲を網羅できるか」が grep に依存

AI による自動テスト生成では、coverage / mutation を満たすが仕様 ID と紐付かないテストを
量産するリスクが特に高い。`docs/harness/plan.md` §3.10 では、これを「仕様の指標」として
分離し、ユーザーモチベーションの直接実現を保証する独立指標 (指標 B) に位置付ける必要が
あると整理されている。

加えて、ColorMaster は `docs/traceability.md` を A6 で自動生成する計画があり、その入力源
として実装 / テスト / 仕様の 3 者を機械可読な ID で接続する仕組みが要る。

## 決定

仕様適合性を **`@Spec(SpecId)` annotation と Konsist 検証** で担保する。

### `SpecId` の形式

`SPEC-<entity>-<seq>-<criterion>` 形式 (例: `SPEC-IDOL-001-3`)。

- `<entity>`: ドメインエンティティ (`IDOL` / `BRAND` / `AUTH` 等)
- `<seq>`: 同一エンティティ内の連番 (3 桁)
- `<criterion>`: 当該基本設計内の Acceptance criteria 連番

基本設計 (`docs/specifications/basic/SPEC-<entity>-<seq>-<slug>.md`) の Acceptance
criteria に `SPEC-<entity>-<seq>-<criterion>` 形式で ID を付与し、`SpecId` と一意対応
させる。

### テスト側の `@Spec` 付与

各テスト関数に `@Spec("SPEC-IDOL-001-3")` 形式で annotation を付与する。1 テストが
複数の criterion を検証する場合は複数 ID を列挙する。

### Konsist 検証 (2 系統)

(a) **Spec coverage**: 各 Acceptance criteria に対応する `@Spec` 付きテストが少なくとも
1 つ存在することを Konsist で機械検証。**新規機能は Spec coverage = 100% を必達ゲート**
とする。既存機能は Phase C で段階的に達成する。

(b) **双方向トレーサビリティ**: 実装側で参照される `SpecId` と、テスト側 `@Spec` で参照
される `SpecId` の両方向の存在を検証し、`docs/traceability.md` を Gradle カスタムタスク
で自動生成する。

### 達成するアウトカム

- 仕様 ⇄ テスト ⇄ 実装の双方向リンク
- 要件変更時の影響範囲特定 (`SpecId` から実装とテストの両方を逆引き可能)
- `docs/traceability.md` 自動生成の入力源
- AI が「カバレッジを満たすだけのテスト」を書いても価値が出ない構造 (仕様 ID が振れない
  限り meaningful にならない)

### 達成しないアウトカム

- テスト存在保証 (→ ADR-0013 / 指標 A)
- テストの意味的強度 (→ ADR-0015 / 指標 C)

## 根拠

カバレッジは「実行したか」を計測し、mutation score は「意味的に効くか」を計測する。
しかし「ユーザーが要件として表明した動作と一致しているか」はどちらにも含まれない。
仕様適合性は本来、要件定義 / 基本設計の Acceptance criteria と直接結ばれて初めて意味を
持つ。`@Spec(SpecId)` annotation は、テスト関数を機械可読な仕様 ID にバインドする最も軽量
かつ言語非依存に近い手法であり、Konsist の lint で網羅性を強制できる。

新規機能のみ Spec coverage を必達ゲートにする理由は、既存コードを一斉に対応させると差分
肥大化で `code-reviewer` が機能しないため。Phase C の各 Epic / Plan で対象モジュールを
段階的に attach する運用とする。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 仕様 ID をテスト関数名に埋め込む | 追加ライブラリ不要 | 関数名が長くなる、複数 ID 持てない、機械検証が grep 頼り | annotation 方式が堅牢 |
| `@DisplayName` のメタデータで管理 | JUnit 標準 | KMP 全 target 対応に難、検証ツール独自 | `@Spec` 自前定義の方が KMP 横断 |
| Gherkin / Cucumber 等の BDD | 仕様駆動の業界実例多数 | DSL 学習コスト、Kotlin Multiplatform 適合性低 | 既存 kotlin-test スタックを維持 |
| coverage / mutation のみで運用 | 指標が 2 つで済む | 仕様適合性が担保されない | 仕様適合性を独立指標 (指標 B) として導入 |

## 帰結

### Positive

- 要件変更時に `SpecId` 一発で影響範囲 (実装 / テスト) を逆引き可能。
- `docs/traceability.md` の自動生成が成立し、Plan ⇄ Epic ⇄ ADR ⇄ Spec ⇄ 実装の
  クロスリンクが機械維持される。
- AI が「カバレッジだけ稼ぐテスト」を書いても、仕様 ID と紐付かない限り Konsist で
  検出されるため、無意味テストへの抑制圧が働く。

### Negative / トレードオフ

- 全テスト関数に `@Spec` 付与の手間が発生する。→ 新規機能のみ必達、既存は Phase C 段階
  達成。
- 基本設計の Acceptance criteria に ID 採番運用が要る。→ `.claude/rules/docs-structure.md`
  と spec template でフォーマット強制。
- 仕様 ID が変わると影響範囲が広い。→ ID は immutable 運用 (旧 ID は廃止せず deprecated
  として残す)、変更が必要なら新 ID を採番。

### Neutral / 将来の検討事項

- `@Spec` annotation 自体の定義配置 (`commonMain` の共通 testing util モジュール想定)
  および Konsist 検証ロジックの実装詳細は A7 で確定し、`.claude/rules/spec-traceability.md`
  に明文化する。
- `docs/traceability.md` 自動生成 (Konsist + frontmatter parser join) は A6 で導入予定。
- 仕様 ID の renaming 機械支援 (IDE refactoring) が将来必要になったら JetBrains MCP
  経由のカスタム inspection を検討する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 5. テスト戦略・品質指標の中核方針 (三層指標の指標 B)
- [x] 8. 複数の代替案を比較した結果としての判断 (関数名埋め込み / `@DisplayName` /
      Gherkin / coverage と mutation のみの 4 案比較)
- [x] 9. 元に戻すコストが高い決定 (`@Spec` annotation は全 spec / テスト / 実装に波及)
- [x] 10. 長期的な制約 (`docs/traceability.md` 自動生成の前提として継続)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` のリストと照合し、本 ADR が単なる annotation 命名規約
      (`.claude/rules/spec-traceability.md` で済む話) に留まらず、仕様 ⇄ テスト ⇄ 実装
      の中核トレーサビリティ機構として ADR にふさわしい決定であることを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0004 (テスト戦略総論、三層指標のインデックス)
- ADR-0013 (Line / Branch coverage 段階達成 / 指標 A)
- ADR-0015 (Mutation testing / 指標 C)
- ADR-0027 (docs 構造 / 命名規約 / 仕様 ID の採番方針)
- `.claude/rules/spec-traceability.md` (`@Spec` annotation / Spec coverage 規約、
  A7 で導入)
- `.claude/rules/docs-structure.md`
- `docs/harness/plan.md` §3.10 指標 B / §6.2 A7
- `docs/traceability.md` (A6 で自動生成導入)
