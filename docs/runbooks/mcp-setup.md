---
id: runbook-mcp-setup
title: MCP セットアップ (JetBrains / Context7 / Cloudflare)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §5.6 / ADR 0024
related_adrs: [ADR-0024]
---

# MCP セットアップ (JetBrains / Context7 / Cloudflare)

> **5 行以内 summary**: ColorMaster ハーネスが利用する 3 つの MCP サーバ (JetBrains MCP /
> Context7 MCP / Cloudflare MCP) の接続セットアップ手順、トラブルシュート、認証フロー
> を集約する。詳細な使い分け規約は `.claude/rules/mcp-usage.md` を参照。

## 1. JetBrains MCP

### 前提

- **IntelliJ IDEA / Android Studio 2025.2+** が必要 (MCP Server プラグインがバンドルされたバージョン以降)
- 旧来の `@jetbrains/mcp-proxy` npm パッケージは **deprecated**、使用しない

### 有効化手順

1. IDE 起動
2. `Settings | Tools | MCP Server` を開く
3. **Enable MCP Server** を ON
4. **3 つの接続方式** のいずれかを選択して **Copy Config**:
   - **Copy SSE Config** (推奨): HTTP SSE 経由、軽量
   - **Copy Stdio Config**: JVM-based proxy 経由、互換性最高
   - **Copy HTTP Stream Config**: HTTP ストリーム経由
5. Copy した JSON を `.claude/mcp.json` の `jetbrains` セクションに貼り付け
6. Claude Code を再起動して `/mcp` で接続確認

### 動的ポート対応

- IDE 再起動で動的ポートが変わる場合がある → `.claude/mcp.json` の URL を再貼付
- もしくは `claude mcp add --transport sse jetbrains <URL>` で再登録

### トラブルシュート

| 症状 | 原因 | 対処 |
|---|---|---|
| 接続失敗 | IDE 未起動 | IDE を起動 |
| 接続失敗 | MCP Server プラグイン未有効 | `Settings | Tools | MCP Server` で Enable |
| 接続失敗 | IDE バージョン 2025.2 未満 | IDE を 2025.2+ にアップグレード |
| 接続失敗 | ポート競合 | IDE 再起動 or 別ポート設定 |
| 突然切断 | IDE 再起動で動的ポート変更 | URL 再貼付 |

長期的に IDE 非起動環境で運用する場合は **Serena MCP の採用を別 Plan で再評価** (R-27)。

## 2. Context7 MCP

### 接続情報

- URL: `https://mcp.context7.com/mcp`
- 認証: 不要 (公開ライブラリ docs)

### `.claude/mcp.json` 設定

```json
"context7": {
  "type": "http",
  "url": "https://mcp.context7.com/mcp"
}
```

## 3. Cloudflare MCP

### 接続情報

- URL: `https://mcp.cloudflare.com/mcp`
- 認証: **OAuth** (初回接続時にブラウザで認証フロー)

### 接続手順

1. `.claude/mcp.json` に Cloudflare 設定追加 (上記)
2. Claude Code 起動時に OAuth フローが開く
3. Cloudflare dashboard でアプリ承認
4. token はローカル Claude Code の安全領域に保存される (`.gitignore` で `.claude/oauth-tokens*` を除外、ADR 0021)
5. `/mcp` で接続確認

### 権限スコープ

- 対象 zone / bucket のみに権限を制限 (R-25)
- 不要な権限を付与しないように Cloudflare dashboard でアプリ権限を見直す

## 4. `/mcp` での接続確認

Claude Code で `/mcp` コマンド (または相当の確認方法) で 3 つの MCP が `connected` 状態であることを確認。

## 5. GitHub MCP は採用見送り

GitHub 操作は **`gh` CLI** で代替する (ADR 0024、token 効率 10〜32 倍優位)。

## 関連

- ADR 0024 (MCP サーバ採用)
- `.claude/rules/mcp-usage.md`
- `.claude/mcp.json`
- `docs/harness/plan.md` §5.6 / R-25 / R-27
- `JetBrains/mcp-server-plugin` (https://github.com/JetBrains/mcp-server-plugin)
- `upstash/context7` (https://github.com/upstash/context7)
- Cloudflare MCP (https://mcp.cloudflare.com/mcp)
