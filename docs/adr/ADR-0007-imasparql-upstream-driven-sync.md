---
id: ADR-0007
title: im@sparql は upstream-driven 同期で更新する
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

# ADR-0007: im@sparql は upstream-driven 同期で更新する

> **5 行以内 summary**: アイドル情報マスタを最新化する手段として、im@sparql の公開
> endpoint を毎回叩く schedule 駆動ではなく、upstream リポジトリ (`imas/imasparql`) の
> latest commit SHA を 1 日 1 回 GitHub Actions cron で取得し、`data/.imasparql-sync-state.json`
> の `upstream_sha` と比較して差分時のみ Gradle タスクで同期 + PR 作成する
> upstream-driven 方式を採用する。自動 merge は禁止 (人間 or AI レビュー必須)。

## ステータス

accepted

## コンテキスト

ColorMaster はアイドル情報マスタ (色 / ブランド / 所属) を im@sparql という外部 SPARQL
endpoint から取得し、リポジトリ内に `data/idols.db` (SQLite) と `data/idols.json` として
コミットして利用している (ADR-0010)。マスタを最新化する同期戦略には次の制約がある:

- im@sparql の公開 endpoint は個人運営であり、稼働率は GitHub と比較して不安定。
  毎時 SPARQL クエリを投げると、サーバ側に負荷をかけるうえ 5xx で同期が失敗する確率も
  上がる。
- アイドル情報の更新頻度は高くなく、`imas/imasparql` リポジトリの commit 自体が
  数日〜数週間に 1 度程度のペース。schedule 駆動で毎日全件取得し直すのは過剰。
- 同期結果は repo commit (`data/idols.db` の更新) として残るため、誤った同期を
  そのまま master に取り込むと historical な汚染になる。レビューを必須化する手段が要る。

加えて、ハーネスでは GitHub Actions の Claude API 直接呼び出しを禁止しており
(ADR-0017)、同期 workflow も人間 / AI のレビュー後にマージする運用と整合させる必要がある。

## 決定

im@sparql の同期は **upstream-driven sync** で実装する。

1. 1 日 1 回の GitHub Actions cron が `gh api repos/imas/imasparql/commits/master` で
   upstream の latest commit SHA を取得する。
2. リポジトリにコミットされた `data/.imasparql-sync-state.json` の `upstream_sha`
   フィールドと比較する。
3. 一致した場合は no-op で正常終了する (API 呼び出し 1 回のみ)。
4. 不一致の場合のみ `./gradlew :backend:cli:fetchIdolColorsFromImasparql` を実行し、
   `data/idols.db` / `data/idols.json` / `data/.imasparql-sync-state.json` を更新する。
   レコード数の前回比 ±X% を超える変動を異常検知として fail させる。
5. 正常完了したら `chore/sync-imasparql-<short-sha>` ブランチを切って PR を作成する。
   **自動 merge は禁止**、人間または AI レビュー (`code-reviewer`) の approve を経て
   squash merge する。
6. im@sparql サーバが 5xx を返すなどの異常時は Issue を自動起票し、次回 cron で
   リトライする。

## 根拠

- **コスト**: upstream が更新されていない大半のケースで API 呼び出しは 1 回のみで済み、
  GitHub Actions の無料枠 / im@sparql 公開 endpoint の負荷ともに最小化できる。
- **可用性の分離**: 変更検知は GitHub API にのみ依存するため、im@sparql 公開 endpoint
  の稼働状況に同期トリガが影響されない。im@sparql がダウンしていても upstream SHA は
  取得可能であり、同期不要と即座に判定できる。
- **レビュー可能性**: 同期結果が必ず PR 経由で master に取り込まれるため、レコード数の
  大幅な増減・スキーマ変化・不正データ混入を人間 / AI レビューでブロックできる。
- **ハーネス整合**: 自動 merge 禁止 / 人間 approve 必須 (R-15) という ColorMaster の
  ハーネス原則に同期 workflow をそのまま乗せられる。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| schedule 駆動 (毎日 SPARQL を全件再取得) | 実装単純、im@sparql 公開 endpoint だけで完結 | im@sparql 5xx 時に常に失敗、更新の有無に関わらず重い query を毎回投げる | コスト / 負荷 / 可用性すべてで劣後 |
| webhook 駆動 (`imas/imasparql` の push を受信) | 即時性が最も高い | upstream リポジトリ管理者ではないため webhook を仕込めない | 仕組み上不可能 |
| 手動同期のみ (CLI を owner が叩く) | 不要な PR を生まない | 同期忘れが発生し古いデータで稼働するリスク | 自動化価値が大きい、cron + PR 必須 |
| 自動 merge 付き同期 | 完全自動化、手間ゼロ | 異常データが PR レビューを経ずに master に入る | R-15 の auto-merge 禁止原則に違反、不採用 |

## 帰結

### Positive

- upstream が変わらない限り GitHub Actions の実行時間は 1 API 呼び出し分のみ。
  Actions 無料枠を圧迫しない。
- 同期 PR が必ずレビューを通るため、im@sparql 側の予期せぬスキーマ変更や巨大な差分が
  master に流入するのを構造的に防げる。
- `data/.imasparql-sync-state.json` に upstream SHA / 前回同期日時 / レコード数を
  残すため、再現性のあるトレーサビリティが得られる。

### Negative / トレードオフ

- upstream の commit から最大で約 1 日の同期遅延が発生する。アイドル情報マスタの
  更新頻度を考えると許容範囲。
- レコード数 ±X% の閾値設定が運用上必要 (初期値は別途 runbook で定義)。
- PR レビューが滞ると同期 PR が積み上がる可能性がある → 1 日 1 PR の上限で運用、
  ハーネスの `pr-poller` が滞留を検知できる。

### Neutral / 将来の検討事項

- im@sparql 側にスキーマバージョン情報があれば、SHA 比較に加えてスキーマ互換性
  チェックも導入できる (将来検討)。
- レコード数異常検知の閾値 X% は実運用ログから調整 (Phase A8 以降の runbook で確定)。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 3. 外部サービスの採用または変更 (im@sparql 公開 endpoint の利用方式を定める)
- [x] 4. データ永続化 / 同期戦略 / バックアップ方式 (アイドル情報マスタ同期戦略の中核)
- [x] 8. 複数の代替案を比較した結果としての判断 (schedule / webhook / 手動 / 自動 merge 付きと比較)
- [x] 10. 長期的な制約 (今後 1 年以上、アイドル情報マスタ更新の中核ベース)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR が単なる sync workflow の設定 (workflow YAML + runbook で済む話) に
      留まらず、外部データ同期戦略の中核方針として ADR 化すべきと確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0010 (アイドル情報マスタ SQLite を repo commit、本 ADR の同期対象)
- ADR-0014 (im@sparql のローカル Fuseki 環境、開発時の代替 endpoint)
- ADR-0009 (Cloud Run、同期結果のデプロイ先)
- `docs/harness/plan.md` §3.3 (upstream-driven sync)
- `.claude/rules/adr.md`
