---
id: ADR-0024
title: MCP サーバは JetBrains と Context7 と Cloudflare の 3 つを採用する
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

# ADR-0024: MCP サーバは JetBrains と Context7 と Cloudflare の 3 つを採用する

> **5 行以内 summary**: MCP サーバは **JetBrains MCP / Context7 MCP / Cloudflare MCP**
> の 3 つを採用し、GitHub MCP / Sourcegraph MCP / Serena MCP は採用見送り
> (代替手段が優位)。GitHub 操作は `gh` CLI、IDE 操作は JetBrains MCP、ライブラリ docs は
> Context7 MCP、Cloudflare 操作は `wrangler` CLI 優先で複雑な API のみ Cloudflare MCP、
> という使い分けに統一する。旧 `@jetbrains/mcp-proxy` npm パッケージは deprecated のため
> 使用しない。

## ステータス

accepted

## コンテキスト

MCP (Model Context Protocol) は 2025 年以降エコシステムが拡大し、Anthropic 公式 /
JetBrains / Cloudflare / Sourcegraph / Serena など多数のサーバが利用可能になった。
ColorMaster のハーネスは `implementation-workflow` / `code-reviewer` /
`dependency-upgrade` / `harness-evolution` 等から各種 MCP を呼ぶ必要があり、採用範囲を
絞らないとセットアップコスト / 認証情報管理 / 機能重複が膨らむ。

検討対象:

- **JetBrains MCP**: IntelliJ IDEA / Android Studio 2025.2+ にバンドル済、`JetBrains/
  mcp-server-plugin` (Marketplace 26071)。rename / inspection / IDE index 検索 /
  build / file analysis / refactoring。旧 `@jetbrains/mcp-proxy` npm パッケージは
  deprecated
- **Context7 MCP**: バージョン固有のライブラリ docs (Kotlin / Compose MP / Ktor /
  SQLDelight / Roborazzi) を LLM に注入、ハルシネーション抑止
- **Cloudflare MCP**: R2 / Pages / Workers / DNS / Secrets 管理
- **GitHub MCP**: PR / Issue / Actions 操作。ただし `gh` CLI が training data 内蔵で
  あり、実証ベンチマークで MCP は CLI より 10〜32 倍トークン消費
- **Sourcegraph MCP**: コード横断検索。JetBrains MCP の IDE indexing と機能重複
- **Serena MCP**: コードベース理解。Kotlin が "Indirect Support" にとどまり、JetBrains
  backend は有料、30 分セットアップ + 初回インデックス overhead

## 決定

採用 MCP は以下の **3 つに限定** する:

- **JetBrains MCP** (IDE 操作)
- **Context7 MCP** (ライブラリ docs 注入)
- **Cloudflare MCP** (R2 / Pages / Workers / DNS / Secrets)

採用見送り:

- **GitHub MCP** → `gh` CLI で代替 (token 効率 10〜32 倍優位、training data 内蔵)
- **Sourcegraph MCP** → JetBrains MCP の IDE indexing で代替 (機能重複)
- **Serena MCP** → JetBrains + Context7 で代替 (Kotlin Indirect Support / 有料 /
  セットアップ重)

使い分けルール:

- **GitHub 操作は原則 `gh` CLI**: PR list / view / diff / search / create / merge /
  comment / Actions logs / Issue 起票はすべて `gh`
- **IDE 操作は JetBrains MCP**: rename / inspection / file analysis / index 経由 regex
  search / build / run config 実行。手動 `git grep` の代わりに JetBrains MCP の検索を
  優先 (IDE index が高速・正確)
- **ライブラリ API 確認は Context7 MCP**: コード生成前に必ず Context7 を経由
  (training data の古い情報や hallucination を回避)
- **Cloudflare 操作**: `wrangler` CLI で済む場合 (`wrangler deploy` 等) は CLI 優先、
  複雑な API 操作 (R2 token ローテーション、bucket policy 更新) のみ Cloudflare MCP
- **MCP OAuth token はリポジトリ commit 禁止** (`.gitignore` で `.claude/oauth-tokens*`
  除外、ADR-0021)

旧 `@jetbrains/mcp-proxy` npm パッケージは deprecated のため使用しない。詳細セットアップ
は `docs/runbooks/mcp-setup.md`、運用規約は `.claude/rules/mcp-usage.md` を参照。

将来検討候補: Figma MCP (デザイン連携) / Code Pathfinder (静的解析) / Serena MCP
(IDE 非起動環境で必要時) / Sentry MCP (本番運用後)。

## 根拠

- **`gh` CLI の token 効率優位**: 実証ベンチマークで GitHub MCP は CLI より 10〜32 倍
  トークン消費。training data に gh 構文が大量に含まれており、Claude にとって
  CLI の方が hallucination リスクも低い
- **JetBrains MCP のバンドル性**: 2025.2+ で公式バンドル、セットアップ不要。IDE index
  経由検索は ripgrep / git grep より高速かつシンボル aware
- **Context7 の必須性**: Kotlin / Compose MP / Ktor 等のバージョン違いによる API drift
  はハルシネーションの主要原因。Context7 で版固有 docs を注入することで構造的に抑止
- **Cloudflare MCP の限定採用**: 単純デプロイは `wrangler` CLI で十分、MCP は複雑 API
  だけに限定して認証情報露出経路を最小化
- **Sourcegraph / Serena 不採用**: 機能重複 / Kotlin サポート不十分 / 有料 (Serena
  backend) / セットアップ重 (Serena 30 分) のいずれかに該当
- **`@jetbrains/mcp-proxy` 不使用**: deprecated の npm パッケージはセキュリティパッチ
  対象外、IDE バンドル版に統一

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| GitHub MCP 採用 | MCP 経由で型付きレスポンス | `gh` CLI より 10〜32 倍 token 消費、機能重複 | 不採用、`gh` CLI で代替 |
| Sourcegraph MCP 採用 | コード横断検索強力 | JetBrains IDE indexing と機能重複 | 不採用、JetBrains MCP で代替 |
| Serena MCP 採用 | LSP ベースでコードベース理解強い | Kotlin Indirect Support / backend 有料 / セットアップ 30 分 | 不採用、IDE 起動環境では JetBrains + Context7 で十分 |
| MCP 不採用 (全て CLI / ローカル script) | 認証情報露出ゼロ | IDE index / 版固有 docs を活かせない、hallucination 増 | 部分採用で 3 MCP 限定とトレードオフ |
| 全部入り採用 | 全機能アクセス可 | 認証管理コスト爆発、機能重複 | 3 MCP に絞り込み |

## 帰結

### Positive

- MCP 採用範囲が 3 つに固定され、認証情報管理 / セットアップ / 機能重複コストが最小化
- `gh` CLI への寄せが整理され、GitHub 操作の training data 活用度が最大化
- Context7 経由のライブラリ docs 注入で、版違いによるハルシネーションを構造的に抑止
  (`dependency-upgrade` Skill 等で必須)

### Negative / トレードオフ

- **IDE 非起動環境で JetBrains MCP が使えない**: CI / 別マシン / バックグラウンドサーバ
  上では fallback (`gh` CLI / `git grep`) に切り替える必要 (R-27)。長期化する場合は
  Serena MCP 採用を別 Plan で再評価
- **Context7 MCP の取得結果は外部依存**: AI が取得した内容を Konsist / detekt / 型
  チェッカーで二重検証する (R-28)
- **Cloudflare MCP の認証スコープ**: 対象 zone / bucket のみ allow する設計が必要
  (`.claude/rules/secrets.md` / ADR-0021)

### Neutral / 将来の検討事項

- Figma MCP は DESIGN.md / UI Inventory との連携余地あり、A10 完了後に再評価
- Sentry MCP は本番運用後に再評価 (Cloud Run + Litestream 稼働後)
- `harness-evolution` の月次手動実行で `anthropics/skills` / MCP spec 更新を確認し、
  新規 MCP 採用余地を `docs/harness/evolution-proposals/` に出力 (ADR-0026)

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 2. 主要なライブラリ / フレームワークの採用または撤去 (MCP サーバの採用 / 不採用)
- [x] 3. 外部サービスの採用または変更 (Context7 / Cloudflare MCP の外部依存)
- [x] 7. ハーネス本体の中核設計 (Skill が MCP を呼ぶ前提)
- [x] 8. 複数の代替案を比較した結果としての判断
- [x] 10. 長期的な制約 (今後 1 年以上、全 Skill の MCP 呼び出しに影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」(「MCP サーバは JetBrains + Context7 +
      Cloudflare の 3 つ」) と一致。Plan / runbook / コーディング規約で済む話ではない
      ことを確認した。

## 関連

- 関連 Plan: PLAN-001
- 関連 Epic: EPIC-000
- ADR-0017 (ローカル Claude Code ポーリング、`gh` CLI 主軸)
- ADR-0018 (`implementation-workflow` Phase 3 で JetBrains MCP / Context7 MCP を利用)
- ADR-0019 (`code-reviewer` architecture aspect が JetBrains MCP IDE indexing を活用)
- ADR-0021 (Secrets / MCP OAuth token 管理)
- ADR-0025 (Skill 作成、`example-skills:skill-creator` 経由)
- `.claude/rules/mcp-usage.md` (MCP 使い分けの Single Source of Truth)
- `docs/runbooks/mcp-setup.md` (3 接続方式 / OAuth フロー / port 競合)
- `docs/harness/plan.md` §5.6 / R-25 / R-27 / R-28
