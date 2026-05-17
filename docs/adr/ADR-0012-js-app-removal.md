---
id: ADR-0012
title: js/app と関連 Web 配信構成を撤去する
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

# ADR-0012: js/app と関連 Web 配信構成を撤去する

> **5 行以内 summary**: wasmJs ターゲット移行の方向性が決定済みのため、`js/app`、
> `js/material`、`kotlin-js-store/`、`.github/workflows/web-build-and-deploy.yml`、
> `public/` 内の js 専用ファイルを **即時撤去** する。後続リファクタが clean になる。
> Web 配信は wasmJs 完成後に Cloudflare Pages (ADR-0022) で再開。撤去作業の実装は
> Phase A の A5 で実施。詳細は `docs/harness/plan.md` §3.6 を Single Source of Truth とする。

## ステータス

accepted

## コンテキスト

ColorMaster は従来 Kotlin/JS (`js/app`) で Web 版を提供してきた。Compose
Multiplatform の Web 対応が wasmJs に主軸を移し、JetBrains 公式も wasmJs を推奨
ターゲットとして発信している現状で、`js/app` を残し続けると以下の問題が生じる。

- **二重メンテナンス**: 同一 feature を `commonMain` と `jsMain` の両方で動作確認
  する必要があり、撤去まで存続させると Phase B (リファクタ) / Phase C (機能拡張)
  の作業ボリュームが純増する。
- **依存ツリーの汚染**: `kotlin-js-store/` (`yarn.lock` 相当) と `js/app` 専用の
  npm 依存が `build.gradle.kts` に残り、ビルド依存解決の複雑度が上がる。
- **CI workflow の冗長**: `.github/workflows/web-build-and-deploy.yml` が JS ビルド
  と Firebase Hosting デプロイを実行しているが、Firebase Hosting は ADR (0011 系)
  で全廃方針が確定しており、撤去対象と整合しない。
- **wasmJs 移行を阻害**: js/app の存在は wasmJs 実装と機能重複を起こし、新規 wasmJs
  feature を書く際に「どっちに書く?」の判断が必要になる。

Web 配信そのものは将来 Cloudflare Pages (ADR-0022) + wasmJs バンドルで再開する
予定だが、移行期間中の Web 配信は **一時停止** することを許容する (ADR-0022 で記録
予定)。

## 決定

以下を **Phase A の A5 で即時撤去** する。

- **コードベース**:
  - `js/app/` ディレクトリ全体
  - `js/material/` ディレクトリ全体 (`js/app` 専用)
  - `kotlin-js-store/` ディレクトリ (wasmJs 移行時に再生成)
  - `public/` 内の js 専用ファイル (共有資源は `core/resources/` 等に退避)
- **CI / インフラ**:
  - `.github/workflows/web-build-and-deploy.yml` の workflow ファイル
  - `firebase.json` / `.firebaserc` (Firebase Hosting 全廃方針と一体)
- **依存**:
  - `js/app` 関連の `build.gradle.kts` ターゲット定義
  - Kotlin/JS 専用 npm 依存

撤去後の Web 配信再開は **wasmJs ターゲット完成後**、ADR-0022 (Cloudflare Pages)
に従って再構築する。本 ADR と ADR-0022 の間の期間、Web 版は提供しない。

## 根拠

- **後続リファクタが clean になる**: Phase B / C で feature-first 移行 (ADR-0003)
  / Decompose 撤去 (ADR-0005) を進める際、js/app を残したまま行うと「js/app に
  対しても同じ変更を当てるか?」の判断が毎 PR 発生する。先に撤去すると判断不要。
- **即時撤去の合理性**: Web 配信を一時停止するコストより、二重メンテで Phase B/C
  全体が遅延するコストの方が大きい。owner 単独運用で Web 版の利用統計が限定的な
  現状では、一時停止のユーザー影響は許容範囲。
- **Firebase 全廃と一体**: `web-build-and-deploy.yml` は Firebase Hosting 前提
  であり、Firebase 全廃方針 (ADR-0011 系) と整合させるためにも本撤去で同時に消す。
- **撤去 → 再開先が決まっている**: ADR-0022 (Cloudflare Pages) で Web 配信再開先
  が確定しているため、本撤去は「永続的な Web 配信中止」ではなく「移行のための一時
  停止」と位置付けられる。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| js/app を wasmJs 完成まで残す | Web 版が継続提供される | Phase B/C の作業ボリューム純増、二重メンテ | リファクタの妨げになるコストが、一時停止コストを上回る |
| js/app を段階的に撤去 (feature 単位) | リスク分散 | 段階移行中の状態が長期化、判断回数増 | 即時撤去の方が判断単純、Phase A 内で完結 |
| 撤去せず wasmJs と並行運用 | 移行リスク最小化 | 同上、依存ツリー汚染が永続化 | wasmJs 完成までの一時停止で十分 |

## 帰結 (Consequences)

### Positive

- Phase B / C のリファクタ・新規実装が `commonMain` + 4 target (Android / iOS /
  desktop / wasmJs) に集中でき、二重メンテの認知負荷がゼロになる。
- `kotlin-js-store/` 撤去で `yarn.lock` 相当の依存管理が不要になり、ビルドが軽くなる。
- Firebase Hosting 関連ファイル (`firebase.json`, `.firebaserc`) を同時撤去でき、
  Firebase 全廃方針との整合が取れる。

### Negative / トレードオフ

- **Web 版が wasmJs 完成まで一時停止** する。owner 単独運用 + Web 利用統計が限定的
  な状況なら許容範囲だが、ユーザー影響をゼロにはできない。
- 既存 js/app に固有の bug fix / feature が残っていた場合、それを wasmJs 移行時に
  改めて実装する必要がある (撤去前に未マージ branch がないことを確認する)。

### Neutral / 将来の検討事項

- wasmJs 実装は Phase B 以降で着手、ADR-0022 (Cloudflare Pages) と連動して進める。
- 撤去 PR で削除されたファイル一覧は EPIC-000 / PLAN-001 系の Plan に
  `expected_modules` で記録、後日の archeology を容易にする。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 1. アーキテクチャパターン / 層分割 / モジュール構造に影響する (js target 撤去)
- [x] 2. 主要なライブラリ / フレームワークの採用または撤去 (Kotlin/JS target の撤去)
- [x] 3. 外部サービスの採用または変更 (Firebase Hosting 関連の同時撤去)
- [x] 9. 元に戻すコストが高い決定 (撤去後の復活はコード削除を巻き戻す必要)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] 確認済み (主要ターゲット (Kotlin/JS) の撤去は §4.5 項 1 / 2 に直接該当、
      Plan 単独で済む単発バグ修正ではない)

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR、撤去の実装は別 Plan)
- 関連 Epic: EPIC-000 (ハーネス基盤構築、Phase A の一部として A5 で撤去実施)
- ADR-0022 (Cloudflare Pages、Web 配信再開先)
- ADR-0006 (wasmJs i18n、撤去後の移行先で利用)
- `.claude/rules/removed-modules.md`
- `docs/harness/plan.md` §3.6
