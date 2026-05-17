---
id: ADR-0001
title: ADR 運用基準と起票判断フローを定める
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

# ADR-0001: ADR 運用基準と起票判断フローを定める

> **5 行以内 summary**: ADR は Michael Nygard 原則 ("Architecturally Significant Decisions"
> のみ記録) に従い、§4.5 の起票基準 10 項目のうち 2 項目以上を満たすときに起こす。
> 採番は 4 桁ゼロパディング、ステータスは MADR 4 状態 (`proposed → accepted →
> deprecated | superseded`)、`accepted` 以降は immutable で変更時は新 ADR を起こす。
> 詳細規約は `.claude/rules/adr.md`、判断フロー Mermaid は `docs/adr/README.md`。

## ステータス

accepted

## コンテキスト

ColorMaster はこれまで ADR を運用していなかった。AI 駆動開発ハーネスを稼働させるには、
アーキテクチャ決定を機械可読な形で蓄積し、Plan / Epic / Skill / `.claude/rules/*` から
`related_adrs` 経由で逆引きできるようにする必要がある。

しかし全ての判断を ADR にすると蓄積が肥大化し、本来コーディング規約 (`.claude/rules/`) や
Plan ファイル、runbook、Epic の `open-questions.md` / `decisions.md` で扱うべき事柄も混在する。
これは Michael Nygard が原型を提示した時に明確に意図した「Architecturally Significant
Decisions」の趣旨から外れる。AWS / Microsoft / Google / Martin Fowler の ADR ガイドラインも、
起票基準を明示し他の記録方法と使い分けることを推奨している。

## 決定

以下の運用基準を採用する:

### 起票基準 — 10 項目のうち 2 項目以上を満たすときに ADR を起こす

1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
2. 主要なライブラリ / フレームワークの採用または撤去
3. 外部サービスの採用または変更 (DB / ホスティング / 認証 / CDN 等)
4. データ永続化 / 同期戦略 / バックアップ方式
5. テスト戦略・品質指標の中核方針
6. セキュリティ・プライバシー・ライセンスに関する方針
7. ハーネス本体の中核設計 (Skill 構成、ループ構造、ローカル vs サーバ実行)
8. 複数の代替案を比較した結果としての判断
9. 元に戻すコストが高い決定
10. 長期的な制約 (今後 1 年以上の判断のベースになるもの)

### 採番・命名

- 連番、4 桁ゼロパディング (`0001`, `0002`, ...)
- ファイル名: `ADR-NNNN-<kebab-case-title>.md` (kebab-case は英語)
- タイトル: 日本語、簡潔・現在形・断定的 (ADR-0027 / `.claude/rules/template-language.md`)

### ステータス遷移 (MADR 4 状態)

`proposed` → `accepted` → `deprecated` | `superseded by ADR-NNNN`

- `accepted` 以降は **immutable**。内容を改訂したい場合は新 ADR を起こし、
  旧 ADR を `superseded_by` で指す。
- 採番欠番は実装前なら整理可、運用後は番号を維持して `withdrawn` を許容。

### 他の記録方法との使い分け

| 種類 | 記録方法 |
|---|---|
| コーディング・命名・スタイル規約 | `.claude/rules/<rule>.md` |
| 1 PR で完結する判断 | `docs/plans/PLAN-NNN-*.md` |
| Epic 内の細粒度な保留 → 解決 | `docs/epics/<id>/{open-questions,decisions}.md` |
| 運用手順 | `docs/runbooks/<name>.md` |
| PR ごとの学び・改善案 | `docs/harness/learnings/YYYY-MM-DD-pr-N.md` |

判断フロー Mermaid と「ADR にすべき例 / すべきでない例」の具体例は
`docs/adr/README.md` および `.claude/rules/adr.md` に保持する。

## 根拠

- **Michael Nygard 原則準拠**: 業界で広く認知された ADR の原型に従うことで、外部から
  参加する開発者の認知負荷を最小化できる。
- **2 項目以上のルール化**: 単一基準 (例: 「重要だと感じたら起票」) は主観に依存して
  揺れるため、複数基準の組み合わせで客観性を担保する。
- **MADR 4 状態 + immutable 原則**: 過去の決定経緯を改ざんから守り、`superseded_by` で
  決定の系譜を辿れるようにする。
- **使い分けの明文化**: ADR / `.claude/rules/` / Plan / runbook / learning の 5 つの
  記録媒体を判断フローで分離することで、起票漏れと過剰起票の両方を防ぐ。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| MADR (Markdown ADR) フル準拠 | テンプレが豊富で項目細分化 | 中小規模では over-engineered | テンプレ簡素版を採用、4 状態は MADR 由来 |
| Nygard オリジナル (4 セクションのみ) | 軽量 | 起票基準 / 自己チェックを別途規約化する必要 | 本 ADR で起票基準と自己チェックを補足し採用 |
| Any Decision Record (全決定を ADR 化) | 漏れがない | ノイズ蓄積で参照価値が下がる | 起票基準で重要度フィルタを必須化、不採用 |

## 帰結

### Positive

- Plan / Epic / Skill / rule から `related_adrs` で機械的に逆引き可能。
- 採番ルールが固定されているため `docs/traceability.md` (A6 で自動生成) の入力として
  そのまま使える。
- `accepted` 以降 immutable のため、過去の決定経緯が改ざんに対して構造的に守られる。

### Negative / トレードオフ

- 起票判断に毎回フローを通す必要があるため、人間 / AI 双方に若干の認知負荷がかかる。
  → `.claude/rules/adr.md` と本 ADR 末尾の自己チェック表で機械化に近付ける。
- 27 件もの初回起票 (PLAN-001) で 1 PR の差分が大きくなる。
  → docs 中心の PR でコードロジック変更を含まないため、`code-reviewer` の負担は限定的。

### Neutral / 将来の検討事項

- 機械検証 (frontmatter JSON Schema、参照先実在チェック、5 行 summary 検証) は A6 で
  Gradle カスタムタスクとして導入予定。それまでは手動 Self-Verification で運用する。
- ADR 起票基準を満たさないが将来満たす可能性のある運用ルールが出てきた場合は、
  まず `.claude/rules/` に書き、再評価で基準を 2 項目以上満たすと判断したら ADR に
  格上げする (R-36)。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 7. ハーネス本体の中核設計 (ADR 運用そのものがハーネスのメタ設計)
- [x] 8. 複数の代替案を比較した結果としての判断 (MADR フル / Nygard / Any Decision Record の比較)
- [x] 9. 元に戻すコストが高い決定 (ADR 採番・命名・ステータス遷移は一度動かすと変更困難)
- [x] 10. 長期的な制約 (今後 1 年以上、本リポジトリの全 ADR 起票に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR がコーディング規約 / Plan で済む話 / runbook で済む話 ではないことを確認した。

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤構築)
- `.claude/rules/adr.md` (起票基準・例リスト・採番ポリシーの Single Source of Truth)
- `docs/adr/README.md` (判断フロー Mermaid、ADR 一覧)
- `docs/adr/template.md` (ADR テンプレート)
- `docs/harness/plan.md` §4.5 (起票基準と書式)
- ADR-0027 (docs 構造 + 日本語化方針、ADR タイトル・本文の言語規約)
