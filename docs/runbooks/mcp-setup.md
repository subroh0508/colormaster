---
id: runbook-mcp-setup
title: MCP セットアップ (JetBrains / Context7 / Cloudflare)
status: living
last_updated: 2026-05-17
related_adrs:
  - ADR-0021
  - ADR-0024
---

# MCP セットアップ (JetBrains / Context7 / Cloudflare)

> **5 行以内 summary**: ColorMaster ハーネスが利用する 3 つの MCP サーバ (JetBrains MCP /
> Context7 MCP / Cloudflare MCP) の接続セットアップ手順、トラブルシュート、認証フロー
> を集約。詳細な使い分け規約は `.claude/rules/mcp-usage.md`、採用理由は ADR 0024 を参照。
> GitHub MCP は採用見送り (`gh` CLI で代替、token 効率 10〜32 倍優位)。

## 1. JetBrains MCP

### 前提

- **IntelliJ IDEA / Android Studio 2025.2 以降** が必要 (MCP Server プラグインがバンドルされたバージョン以降)
- 旧来の `@jetbrains/mcp-proxy` npm パッケージは **deprecated**、使用しない

### 有効化手順

1. IDE を起動
2. `Settings | Tools | MCP Server` を開く
3. **Enable MCP Server** を ON
4. **3 つの接続方式** のいずれかを選択して **Copy Config** を実行:
5. Copy した JSON を `.claude/mcp.json` の `jetbrains` セクションに貼り付け
6. Claude Code を再起動して `/mcp` で接続確認

### 接続方式の比較 (3 種類)

| 方式 | 通信 | 利点 | 欠点 | 推奨ケース |
|---|---|---|---|---|
| **SSE** (Server-Sent Events) | HTTP SSE | 軽量、開発機リソースを節約 | 一部の HTTP プロキシ環境で切断する場合あり | 既定の選択肢 (推奨) |
| **Stdio** | JVM-based proxy 経由 | 互換性が最も高く、ネットワーク制約に強い | proxy プロセスを 1 つ余分に起動するため起動が遅い | プロキシ環境 / SSE が不安定なとき |
| **HTTP Stream** | HTTP ストリーム | ファイアウォール越えが容易 | リソース消費が他方式より大きい | リモート IDE 経由の特殊運用 |

### `.claude/mcp.json` 設定例 (SSE)

IDE の `Copy SSE Config` で生成される JSON 例 (動的ポートのため値は IDE 側で確認):

```json
"jetbrains": {
  "type": "sse",
  "url": "http://localhost:<dynamic-port>/sse"
}
```

### 動的ポート対応

- IDE 再起動で動的ポートが変わる場合がある → `.claude/mcp.json` の URL を再貼付
- もしくは `claude mcp add --transport sse jetbrains <URL>` で再登録

### トラブルシュート

| 症状 | 原因 | 対処 |
|---|---|---|
| `/mcp` で `jetbrains: disconnected` | IDE 未起動 | IDE を起動 |
| `/mcp` で `jetbrains: disconnected` | MCP Server プラグイン未有効 | `Settings | Tools | MCP Server` で Enable |
| `/mcp` で `jetbrains: disconnected` | IDE バージョン 2025.2 未満 | IDE を 2025.2+ にアップグレード |
| 接続失敗 (ポート競合) | ローカルで別プロセスが同ポート使用 | IDE 再起動 or 別ポート設定 |
| 突然切断 | IDE 再起動で動的ポート変更 | URL 再貼付 or SSE → Stdio に変更 |
| SSE が安定しない | プロキシ環境 | Stdio 方式に切り替え |

長期的に IDE 非起動環境で運用する場合は **Serena MCP の採用を別 Plan で再評価** (R-27)。

## 2. Context7 MCP

### 接続情報

- URL: `https://mcp.context7.com/mcp`
- 認証: 不要 (公開ライブラリ docs サーバ)

### `.claude/mcp.json` 設定

```json
"context7": {
  "type": "http",
  "url": "https://mcp.context7.com/mcp"
}
```

### 用途

- バージョン固有のライブラリ docs を取得 (Kotlin / Compose Multiplatform / Ktor / SQLDelight / Roborazzi 等)
- LLM の training data が古い場合や hallucination のリスクが高い API を呼ぶ前に Context7 で確認
- Konsist / detekt / 型チェッカーで二重検証する (R-28)

## 3. Cloudflare MCP

### 接続情報

- URL: `https://mcp.cloudflare.com/mcp`
- 認証: **OAuth** (初回接続時にブラウザで認証フロー)

### `.claude/mcp.json` 設定

```json
"cloudflare": {
  "type": "http",
  "url": "https://mcp.cloudflare.com/mcp"
}
```

### 接続手順

1. `.claude/mcp.json` に Cloudflare 設定追加 (上記)
2. Claude Code 起動時に OAuth フローが開く
3. Cloudflare dashboard でアプリ承認
4. token はローカル Claude Code の安全領域に保存される (`.gitignore` で `.claude/oauth-tokens*` を除外、ADR 0021)
5. `/mcp` で接続確認

### 権限スコープ

- 対象 zone / bucket のみに権限を制限 (R-25)
- 不要な権限を付与しないように Cloudflare dashboard でアプリ権限を見直す
- token 漏洩疑い時は **即時ローテーション** (TTL 90 日、ADR 0021)

## 4. `/mcp` での接続確認

Claude Code で `/mcp` コマンドを実行し、3 つの MCP が `connected` 状態であることを確認。

接続状態出力例:

```
jetbrains:  connected (sse)
context7:   connected (http)
cloudflare: connected (http)
```

いずれかが `disconnected` の場合は §1〜§3 のトラブルシュート表に従う。

## 5. GitHub MCP は採用見送り

GitHub 操作は **`gh` CLI** で代替する (ADR 0024、token 効率 10〜32 倍優位)。

| GitHub 操作 | 代替手段 |
|---|---|
| PR list / view / diff | `gh pr list / view / diff` |
| PR create / merge | `gh pr create --draft / merge` |
| Issue 起票 | `gh issue create` |
| Actions logs | `gh run view --log` |
| Repository search | `gh search` |

## 6. HTTP 型 MCP の外部障害時の応急処置 (A1 レトロ Problem #11)

`.claude/mcp.json` で HTTP 型 (`context7` / `cloudflare`) として登録された MCP server は
Claude Code セッション起動時に接続試行する。**外部サービス側が応答不能の場合、接続タイムアウト
分だけセッション起動 latency が増える** (経験的に 1 サーバあたり数秒〜十数秒)。

応急処置:

1. `.claude/mcp.json` から該当エントリを **削除して Claude Code を再起動** (commit せず手元のみで変更)
2. 復旧確認後、`.claude/mcp.json` を元に戻す
3. Stdio 型である **JetBrains MCP は IDE 起動時に同 OS プロセス内で接続するため外部障害の影響は受けない**

Skill 側のタイムアウト fallback (N 秒以内に応答しない場合は Context7 / Cloudflare を skip
して `gh` CLI / `wrangler` / training data にフォールバック) は A3 で `feature-request` /
`dependency-upgrade` Skill 本格化時に検討。

## 関連

- ADR 0024 (MCP サーバ採用)
- ADR 0021 (Secrets 管理、OAuth token 取扱)
- `.claude/rules/mcp-usage.md`
- `.claude/mcp.json`
- `docs/harness/plan.md` §5.6 / R-25 / R-27 / R-28
- `docs/runbooks/local-development.md` (IDE / Docker / Node 等のローカル前提環境)
- `JetBrains/mcp-server-plugin` (https://github.com/JetBrains/mcp-server-plugin)
- `upstash/context7` (https://github.com/upstash/context7)
- Cloudflare MCP (https://mcp.cloudflare.com/mcp)
