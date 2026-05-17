---
id: ADR-0003
title: モジュール構造を feature-first にする
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

# ADR-0003: モジュール構造を feature-first にする

> **5 行以内 summary**: `feature/<name>/{ViewModel, UiState, UiAction, Screen, di}.kt`
> の feature 単位フラット構造を採用する。横串の関心事 (network / data / di-core 等)
> は `core/<concern>/` に集約。1 feature = 1 ディレクトリ = 1 責務単位とすることで、
> Plan / PR の touch ファイルが最小化され、AI 駆動の差分レビューが容易になる。
> 詳細は `docs/harness/plan.md` §3.1 を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster の既存モジュール構成は層別 (`presentation/` / `domain/` / `data/`) と
feature 別が混在しており、1 つの機能を追加・変更する際に複数モジュール / 複数階層を
横断する必要があった。これは以下の問題を生む。

- 1 PR が触るファイルが分散し、`code-reviewer` (8 aspect 並列) の対象が広がる。
- AI Agent が「この feature の責務はどこ?」を判断するためにグローバル grep が必要。
- Plan の `expected_modules` 欄に書くパスが冗長になり、Spec ⇄ 実装の traceability も
  分散する。

ADR-0002 で Compose Multiplatform + 共通 ViewModel + Navigation 3 を採用したことに
より、1 feature を構成するファイル群は次の 4-5 種に絞り込める:

- `ViewModel.kt` (`commonMain`)
- `UiState.kt` (`commonMain`)
- `UiAction.kt` (`commonMain`)
- `Screen.kt` (`commonMain`、Composable + Nav 接続)
- `di.kt` (Koin module 定義、feature 内 DI が必要な場合のみ)

これらを 1 ディレクトリにまとめる構造の方が、責務の locality が高く AI / 人間
双方にとって読みやすい。

## 決定

以下のモジュール構造を採用する。

- **feature 層**: `feature/<name>/{ViewModel, UiState, UiAction, Screen, di}.kt` の
  feature 単位フラット構造。1 feature = 1 ディレクトリ = 1 責務単位。
- **core 層**: 横串の関心事は `core/<concern>/` に集約。具体例:
  - `core/network/` (`colormaster-api` クライアント等)
  - `core/data/` (Repository / DataSource)
  - `core/di/` (アプリ全体の Koin module 配線)
  - `core/model/` (ドメインモデル)
  - `core/resources/` (画像・色などの共有資源)
- **navigation**: 各 feature の `Screen.kt` から `Route.kt` を export し、上位の
  Nav graph 組立て (`composeApp` 相当の entry point) で集約する。

ファイル命名と feature の責務分解の詳細は `.claude/rules/{viewmodel,ui-state,composable,navigation}.md`
を Single Source of Truth とする。

## 根拠

- **責務の locality**: 1 feature を触る PR が 1 ディレクトリ内で完結し、`git log`
  / `git diff` / `code-reviewer` の対象範囲が直感的に把握できる。
- **AI の context 効率**: Skill が feature 単位に glob 絞り込み可能、`feature/<name>/**`
  だけ読めば足りるケースが増え、context window 圧迫が減る。
- **Plan の touch 範囲が明確**: Plan / Epic の `expected_modules` に
  `feature/<name>/` 1 行だけ書けば済むケースが多くなり、機械検証 (A6) が単純化する。
- **横串の関心事は core に隔離**: Repository / Network / DI は複数 feature から
  共通参照されるため `core/` に集約、循環依存を構造的に避ける。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 層別構造 (`presentation` / `domain` / `data` 別モジュール) | 層境界が物理分離 | 1 feature 変更で複数モジュール touch、AI の context 圧迫 | feature の locality を優先 |
| feature/ をさらに `feature/<name>/{ui,vm,domain}/` 等の階層化 | 役割境界が明示的 | 階層が深く、ファイル名重複時の grep が面倒、ボイラープレート増 | フラット構造の方が AI / 人間双方で扱いやすい |
| Multi-module Gradle で feature ごと別モジュール | ビルドキャッシュ最適化 | モジュール数爆発、Gradle 設定の保守コスト大 | KMP + Compose の規模ではフラット 1 モジュールで十分 |

## 帰結 (Consequences)

### Positive

- PR の touch 範囲が `feature/<name>/` に局所化、`code-reviewer` の aspect 並列が
  小さい入力で済む。
- AI Skill (`plan-author` / `implementation-workflow`) が `expected_modules` を
  予測しやすく、Spec ⇄ 実装の traceability が単純化する。
- 新 feature 追加時のテンプレ展開が `feature/<name>/` 1 ディレクトリのコピーで
  済む。

### Negative / トレードオフ

- ファイル名が短く (`ViewModel.kt` 等)、feature 名がパスに依存するため、IDE の
  「ファイル名で開く」検索では `<feature>/ViewModel.kt` の検索が必要。
  → `.claude/rules/naming.md` でクラス名は `<Feature>ViewModel` 等、prefix 付きを
    強制し、検索性を担保。
- feature をまたぐ共通 UI コンポーネントは `core/ui-components/` 相当に切り出す
  運用判断が随時必要。

### Neutral / 将来の検討事項

- feature 数が 30 を超えた場合、`feature/` 配下にカテゴリディレクトリ (例:
  `feature/onboarding/`, `feature/library/`) を導入する余地を残す。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 1. アーキテクチャパターン / 層分割 / モジュール構造に影響する
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 9. 元に戻すコストが高い決定 (一度全 feature を移行すると逆戻りコスト大)
- [x] 10. 長期的な制約 (今後 1 年以上、全 feature 実装に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (モジュール構造は §4.5 の項 1「アーキテクチャパターン / 層分割 /
      モジュール構造」に直接該当、`.claude/rules/` で済む話ではない)

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0002 (Compose Multiplatform + Navigation 3、本 ADR の前提)
- `.claude/rules/{viewmodel,ui-state,composable,navigation,repository,network-client,naming}.md`
- `docs/harness/plan.md` §3.1
