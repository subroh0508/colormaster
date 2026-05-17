---
id: ADR-0026
title: ハーネス進化は harness-meta と harness-evolution の二系統で駆動する
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

# ADR-0026: ハーネス進化は harness-meta と harness-evolution の二系統で駆動する

> **5 行以内 summary**: ハーネスを継続的に改善する仕組みを **二系統補完** で構成する。
> 内部 KPT 駆動の `harness-meta` (週次 cron + 閾値到達 + 手動起動) と、外部研究駆動の
> `harness-evolution` (**手動起動のみ**、cron 不採用、Claude API コスト抑制のため) を
> 並走させる。両者は重複しても許容し、両方から同じ改修案が出れば優先度を引き上げる。
> 重要案は `example-skills:skill-creator` 経由で Skill scaffold / Plan / EPIC 起票し、
> 人間 approve を必須とする。

## ステータス

accepted

## コンテキスト

ハーネスは構築して終わりではなく、PR 単位の学び (learning) と外部のベストプラクティス
更新を取り込んで継続改善する必要がある。Anthropic 公式 / `anthropics/skills` / MCP spec
/ awesome-harness-engineering 等の外部情報源は月次レベルで更新があり、内部 KPT だけでは
取りこぼしが発生する。

逆に、外部研究駆動だけでは「本リポジトリ固有の詰まり (例: fix loop 上限 3 回が短すぎた、
特定 aspect の検出漏れ)」が拾えない。

加えて、外部情報源を毎日 cron で取りに行く運用は WebSearch / WebFetch / Context7 MCP
の呼び出しコストが線形に膨らみ、Claude API トークン消費が見合わない (ADR-0017)。

## 決定

ハーネス進化を **二系統補完** で構成する:

- **`harness-meta`** (内部 KPT 駆動):
  - 入力: `docs/harness/learnings/*.md` の Suggested harness changes / Try セクション
  - 起動契機: 週次 cron + 閾値到達 (未処理 learning 10 件 or 前回実行から 7 日経過) +
    手動起動
  - 出力: 改修 PR 群 (1 改修テーマ = 1 PR)、見送り提案は元 learning ファイルに feedback
    セクション追記、撤去候補は月次まとめて cleanup PR
- **`harness-evolution`** (外部研究 / ベストプラクティス駆動):
  - 入力: WebSearch / WebFetch + Context7 MCP で取得した外部情報 (ホワイトリスト:
    Anthropic engineering blog / `anthropics/skills` GitHub / Claude Code docs / MCP
    spec (modelcontextprotocol.io) / awesome-harness-engineering / Augment Code /
    HumanLayer / Cognition (Devin) / arxiv AI agents / Martin Fowler / Red Hat
    Developer / GitHub blog)
  - 起動契機: **手動起動のみ** (Claude API コスト抑制のため cron 不採用)、月次相当の
    頻度を推奨
  - 出力: `docs/harness/evolution-proposals/YYYY-MM-DD.md` (出典 URL + 引用日付必須)、
    重要案は `example-skills:skill-creator` 経由で Skill scaffold / Plan / EPIC 起票
    (人間 approve 必須)

両者は補完関係 (内部学び vs 外部研究) で、重複しても許容する。両方から同じ改修案が出れば
優先度を引き上げる。提案重複防止のため、既に learning ファイルで指摘済の提案は
`harness-evolution` 側を見送り、改修 PR は `harness-meta` / `harness-evolution` の
**ラベル分離** で運用し、`harness-meta` 側を優先する (R-31)。

`anthropics/skills` の更新確認をホワイトリスト先頭に組み込み、月次手動実行で公式追従
パスを確保する (R-30、ADR-0025)。

## 根拠

- **二系統補完の理由**: 内部 KPT は本リポジトリ固有の詰まりに強く、外部研究は新規ベスト
  プラクティスや新規 MCP / Skill 候補に強い。両者は責務が異なるため重複可
- **`harness-evolution` 手動起動のみ**: WebSearch / WebFetch / Context7 は毎回 API
  トークン消費が大きい。cron 化すると無人時にトークン枯渇するリスク。月次手動実行で
  owner が判断するモデルに統一 (ADR-0017 のローカル駆動方針と整合)
- **ホワイトリスト + 出典明記**: 不正確 / 古い情報源を構造的に排除。Context7 MCP で
  API 引用を二重検証 (R-28)
- **重要案は scaffold 起票 + 人間 approve**: 自動マージは絶対禁止 (GitHub Agentic
  Workflows 原則、R-15)。`example-skills:skill-creator` 経由で SKILL.md フォーマットを
  公式準拠に保つ (ADR-0025)
- **ラベル分離 + harness-meta 優先**: 提案重複時は内部学び (実際の詰まりベース) を優先
  することで、改修コストを実需に揃える

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 単一 Skill (内部 + 外部統合) | Skill 数最小 | 責務混在、入力源 / 出力先 / 起動契機が異なるため設計が複雑化 | 二系統補完で責務分離 |
| `harness-evolution` を cron 駆動 | 自動的に外部追従 | API トークン消費が線形拡大、無人時に枯渇リスク | 手動起動のみで採用 |
| 外部情報源ホワイトリスト無し (自由収集) | 情報網が広い | 不正確 / 偏った情報源混入、出典追跡不能 | ホワイトリスト + 出典明記で採用 |
| 重要案も自動マージ可 | 高速 | 人間ゲート喪失、GitHub Agentic Workflows 原則違反 | 人間 approve 必須 |
| 内部 KPT のみ採用 (`harness-meta` 単独) | 単純 | 外部ベストプラクティス取りこぼし、公式 update 追従不能 | 二系統補完で採用 |

## 帰結

### Positive

- 内部学び + 外部研究の両輪でハーネス改善が継続駆動される
- `harness-evolution` 手動起動により API コスト枠が予測可能
- `anthropics/skills` 公式更新への追従パスが月次相当で確保される (ADR-0025 と整合)

### Negative / トレードオフ

- **`harness-evolution` 手動忘れリスク**: cron 不採用のため owner が起動を忘れると外部
  追従が止まる → `harness-meta` の月次 cron 実行時に「今月 `harness-evolution` 走らせた
  か」を確認する notice を入れる (`.claude/rules/harness-meta-criteria.md`)
- **提案重複の調整コスト**: `harness-meta` / `harness-evolution` の両方から似た提案が
  出た場合の見送り判定は owner の手動判断 → ラベル分離 + `harness-meta` 優先で機械化
- **外部情報源の偏り**: ホワイトリスト依存のため、リスト外の良質情報を取り逃す → ホワイト
  リスト追加は Plan 起票 + 人間 approve 必須で柔軟運用

### Neutral / 将来の検討事項

- `harness-evolution` の cron 化を再評価する場合、API トークン消費の見積もりを取って
  別 ADR で対応
- ホワイトリスト拡張 (例: Figma / Sentry / Linear ブログ) は `harness-evolution` の
  evolution-proposal で提案 → Plan 起票

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 7. ハーネス本体の中核設計 (進化ループの構成)
- [x] 8. 複数の代替案を比較した結果としての判断 (単一統合 / cron 化 / ホワイトリスト
      有無 / 自動マージ可否)
- [x] 9. 元に戻すコストが高い決定 (2 Skill 構成で全 learning / evolution-proposal の
      入出力が固まる)
- [x] 10. 長期的な制約 (今後 1 年以上、ハーネス改善駆動方式に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」(「ハーネス進化は内部 (`harness-meta`)
      + 外部 (`harness-evolution`) の二系統」) と一致。Plan / runbook / コーディング
      規約で済む話ではないことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0017 (ローカル Claude Code ポーリング、`harness-evolution` 手動起動と整合)
- ADR-0024 (Context7 MCP 採用、外部情報源 API 検証に利用)
- ADR-0025 (Skill 作成は `example-skills:skill-creator` 経由、重要案の起票で利用)
- `.claude/rules/harness-evolution.md` (外部情報源ホワイトリスト + 出力フォーマット +
  responsabilities 分離の Single Source of Truth)
- `.claude/rules/harness-meta-criteria.md` (起動閾値 + 重複提案の調整ルール)
- `docs/harness/plan.md` §5.3 / §5.4.6 / R-29 / R-30 / R-31
