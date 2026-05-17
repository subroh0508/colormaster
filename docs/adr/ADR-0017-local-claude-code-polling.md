---
id: ADR-0017
title: ハーネスループはローカル Claude Code ポーリングで駆動する
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

# ADR-0017: ハーネスループはローカル Claude Code ポーリングで駆動する

> **5 行以内 summary**: ハーネスループ (`pr-poller` / `pr-retrospective` / `harness-meta` /
> `dependency-upgrade`) の起動は **ローカル Claude Code 内のポーリング** で駆動し、
> GitHub Actions 上では Claude API を一切呼ばない。`pr-poller` Skill が `CronCreate`
> 日次起動と `ScheduleWakeup` ループを組み合わせ、`gh` CLI で未処理 PR / Renovate ラベル
> PR を検出して下流 Skill を起動する。コスト・rate limit・PII 漏洩経路を構造的に排除する。

## ステータス

accepted

## コンテキスト

ColorMaster の AI 駆動ハーネスは Spec Gen → Implementation → Evaluation → Merge →
Retrospection → Meta の 6 段ループを継続稼働させる必要がある。ループ駆動には大別して
3 案がある:

1. GitHub Actions 上で Claude API を直接呼ぶ (workflow_run トリガ等)
2. ローカル Claude Code 内でポーリング駆動し、API 呼び出しは Claude Code の既存利用枠内に閉じる
3. 別途サーバ (Cloud Run 等) を立ててエージェントを常駐させる

GitHub Actions 上で Claude API を呼ぶ方式は、PR 件数の増加に従って API トークン消費が
線形に膨らみ、Anthropic 側 rate limit と GitHub Actions 同時実行枠の両方を圧迫する。
また、PR 本文 / diff / CI ログを Claude API に送る経路が CI 上に常設されるため、PII
redaction の責務が CI / Skill / API 三層に分散し、漏洩検出が難しくなる。

別サーバ常駐方式は Cloud Run / Cloudflare Workers いずれも導入コストが大きく、個人
プロジェクト規模 (owner 1 名、ADR 0020) では over-engineered。

一方、Claude Code は `CronCreate` と `ScheduleWakeup` をローカル提供しており、これを
組み合わせれば cron 駆動ポーリング + イベント駆動ループをローカル完結で実現できる。
GitHub 操作は `gh` CLI で実行できる (ADR 0024)。

## 決定

ハーネスループの駆動を **ローカル Claude Code ポーリング** に統一する。具体的には:

- **`pr-poller` Skill が起点**。3 系統の起動経路 (手動起動 / `CronCreate` 日次 09:00 JST /
  `ScheduleWakeup` ループ) を持ち、`.claude/locks/pr-poller.lock` で排他制御する
  (`.claude/rules/pr-poller.md`)
- **検出ロジック**: `gh pr list --state merged,closed` で未処理 PR を取得 →
  `docs/harness/learnings/` 配下に対応 learning ファイルが無いものを抽出 →
  `pr-retrospective` を起動。open PR で `labels:renovate` が付くものは `dependency-upgrade`
  を起動
- **`harness-meta` 起動閾値**: 未処理 learning 10 件 or 前回実行から 7 日経過で自動起動
  (`.claude/rules/harness-meta-criteria.md` で上書き可能)
- **`harness-evolution` は cron 不採用**。手動起動のみ (ADR-0026)
- **GitHub Actions では Claude API を呼ばない**。Actions 上で稼働させるのは Claude API
  不要な workflow (CI 検証 / trufflehog / upstream-driven sync / markdownlint) のみ

## 根拠

- **コスト構造**: Claude API トークン消費は Claude Code の既存利用枠内で完結し、Actions
  実行時間 (有料分) と API トークン (有料) の二重課金を回避できる
- **rate limit 分離**: Anthropic API rate limit と GitHub Actions 同時実行枠が独立に
  消費されるため、CI 詰まりと AI 遅延が連鎖しない
- **PII 漏洩経路の限定**: PR diff / CI ログを Claude API に送る経路をローカル 1 ヶ所に
  集約することで、redaction 責務が Skill 側だけで完結する (`.claude/rules/pii.md` /
  R-26)
- **ハーネス再帰性**: ローカル駆動は Claude Code の Skill ループ全体と整合し、
  `implementation-workflow` Phase 8 で `pr-poller` を即時起動する設計 (ADR-0018) と一致
- **`gh` CLI で十分**: GitHub MCP は token 効率で `gh` CLI に劣る (ADR-0024)、ローカル
  ポーリングと相性が良い

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| GitHub Actions 上で Claude API 直接呼び出し | CI トリガ即時、owner 不在でも稼働 | API コスト二重、rate limit 競合、PII 漏洩面拡大 | コスト・セキュリティの両面で不利、本 ADR で不採用 |
| 別サーバ (Cloud Run) 常駐エージェント | スケーラブル、owner 不在対応 | インフラ運用負荷、Backend と責務混在 | 個人プロジェクト規模で over-engineered |
| ローカル Claude Code ポーリング (採用) | コスト枠内完結、PII 経路集約、Skill ループと整合 | owner 不在時はループ停止 | owner 1 名運用 (ADR-0020) と整合、許容 |
| webhook + ローカル listener | 即時性が高い | port forwarding / 認証 / 常駐 listener が必要 | Claude Code の `ScheduleWakeup` で代替可能 |

## 帰結

### Positive

- API コストが Claude Code 既存利用枠内に収まり、Actions 課金との二重コスト回避
- PII redaction 責務が Skill 側 1 ヶ所に集約され、漏洩検出が単純化
- `pr-poller` / `harness-meta` / `dependency-upgrade` がローカル統合され、Skill ループ
  全体 (ADR-0018 / ADR-0019) と一貫した実行モデルになる

### Negative / トレードオフ

- **owner 不在時はループが停止**: 出張・長期休暇中は learning ファイル蓄積が遅れる →
  `pr-poller` のキャッチアップ動作 (R-11) で「最後の処理から N 日経過した PR を最優先」
  ロジックで補う
- **3 系統の起動経路で重複起動リスク**: `.claude/locks/pr-poller.lock` で排他制御し、
  N 分以内の既存ロックは no-op で終了 (`.claude/rules/pr-poller.md`)
- **CI 上での即時 AI レビューができない**: ローカル `code-reviewer` 起動まで遅延が発生
  → Draft PR で先行 lint/test を回し、AI レビューは Phase 6 で集約 (ADR-0019)

### Neutral / 将来の検討事項

- owner が複数人体制になった場合、別 ADR でハイブリッド方式 (Actions 側に軽量 trigger +
  ローカル fan-out) を再評価する
- Anthropic 側で Cloud Code エージェント常駐機能が公式提供されたら採用余地を見直す
  (`harness-evolution` で月次確認、ADR-0026)

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 6. セキュリティ・プライバシー (PII 漏洩経路を限定)
- [x] 7. ハーネス本体の中核設計 (ループ駆動方式の決定)
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (Skill 設計と CI workflow 双方に影響)
- [x] 10. 長期的な制約 (今後 1 年以上、ハーネス稼働モデル全体に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」リスト (「ハーネスループをローカル Claude
      Code ポーリングで駆動する」) と一致。コーディング規約 / Plan / runbook で済む話では
      ないことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0018 (`implementation-workflow` 10 フェーズ、Phase 8 で `pr-poller` 即時起動)
- ADR-0019 (`code-reviewer` 8 aspect、サブエージェント並列、API 直接呼び出し禁止)
- ADR-0024 (MCP サーバ採用、GitHub 操作は `gh` CLI)
- ADR-0026 (`harness-evolution` は手動起動のみ、cron 不採用)
- `.claude/rules/pr-poller.md` (3 系統起動経路 + 排他制御 + 検出ロジック)
- `.claude/rules/harness-meta-criteria.md` (起動閾値の上書き)
- `docs/harness/plan.md` §5.4 / R-17 / R-26 / R-37
