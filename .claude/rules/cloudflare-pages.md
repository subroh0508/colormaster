---
id: rules-cloudflare-pages
title: Cloudflare Pages デプロイ規約
status: stable
last_updated: 2026-05-17
paths:
  - ".github/workflows/deploy-pages.yml"
  - "wrangler.toml"
  - "**/wasmJsMain/resources/**"
related_adrs:
  - ADR-0022
  - ADR-0012
---

# cloudflare-pages.md — Cloudflare Pages デプロイ規約

> Wasm/JS フロントエンド (Compose Multiplatform 配下 `wasmJsMain`) を **Cloudflare Pages**
> にデプロイ (ADR 0022)。Firebase Hosting からの移行 (ADR 0011)、本格化は Phase C7 だが
> 本 rule で起草。Cloudflare MCP (ADR 0024) でデプロイ操作 / ドメイン管理を行う。

## デプロイ構成

| 項目 | 値 | 根拠 |
|---|---|---|
| Project name | `colormaster` (Cloudflare Pages) | — |
| Production branch | `master` | — |
| Preview branches | PR ごとに自動生成 | レビュー時の動作確認 |
| Build command | `./gradlew :app:wasmJsBrowserDistribution` | Compose Multiplatform Wasm build |
| Build output | `app/build/dist/wasmJs/productionExecutable` | Compose Multiplatform 標準出力 |
| Functions | 不使用 (静的配信のみ) | Backend は Cloud Run 別運用 |
| Custom domain | `colormaster.subroh0508.net` (将来) | Phase C7 で確定 |

## CI / CD

```yaml
# .github/workflows/deploy-pages.yml (概略)
on:
  push:
    branches: [master]
    paths: ['app/**', 'core/**', '.github/workflows/deploy-pages.yml']
  pull_request:
    paths: ['app/**', 'core/**']

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - checkout
      - setup-jdk21
      - run: ./gradlew :app:wasmJsBrowserDistribution
      - uses: cloudflare/pages-action@v1
        with:
          apiToken: ${{ secrets.CLOUDFLARE_API_TOKEN }}
          accountId: ${{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          projectName: colormaster
          directory: app/build/dist/wasmJs/productionExecutable
          gitHubToken: ${{ secrets.GITHUB_TOKEN }}
```

- **`CLOUDFLARE_API_TOKEN`**: GitHub Secrets で管理 (90 日 ローテーション、`secrets.md`)
- **`CLOUDFLARE_ACCOUNT_ID`**: 同上 (公開可能だが Secrets 経由で統一)
- PR では preview deployment、master では production deployment
- **GitHub Actions で Claude API は呼ばない** (ADR 0017)

## Headers と Security

`public/_headers` (Cloudflare Pages の routing config) で以下を設定:

```text
/*
  Strict-Transport-Security: max-age=63072000; includeSubDomains; preload
  X-Content-Type-Options: nosniff
  X-Frame-Options: DENY
  Referrer-Policy: strict-origin-when-cross-origin
  Permissions-Policy: camera=(), microphone=(), geolocation=()
  Content-Security-Policy: default-src 'self'; script-src 'self' 'wasm-unsafe-eval'; connect-src 'self' https://<backend-cloudrun-url> https://accounts.google.com https://sparql.crssnky.xyz; img-src 'self' data: https://lh3.googleusercontent.com; style-src 'self' 'unsafe-inline'
```

- **CSP**: `wasm-unsafe-eval` 必須 (Compose Multiplatform Wasm の制約)
- `connect-src` に Backend (Cloud Run) と im@sparql endpoint を allowlist
- `img-src` に GIS userinfo の avatar URL を allowlist (PII 含むため `pii.md` で別途 redact)

## SPA routing

```text
# public/_redirects
/*  /index.html  200
```

- Compose Multiplatform Web は SPA、全 path を `index.html` に return (Navigation 3 で内部 routing)

## ローカル開発との切替

- ローカル: `./gradlew :app:wasmJsBrowserDevelopmentRun` で開発サーバ起動
- 本番: Cloudflare Pages の URL
- Backend URL は `BuildKonfig.BACKEND_BASE_URL` で切り替え (local: `http://localhost:8080` / prod: Cloud Run URL)

## Cloudflare MCP の活用 (ADR 0024)

- デプロイ前検証 / Pages project 設定変更 / DNS 操作は **Cloudflare MCP** (`mcp-usage.md`) 経由
- API Token は MCP OAuth フローで管理、`.claude/oauth-tokens*` に保存 (`secrets.md`)

## 機械検証 (A6 / C7 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `app/build/dist/wasmJs/productionExecutable` の SHA256 hash が再現可能
  - `_headers` に CSP / HSTS が含まれる
- **CI で Lighthouse 等の web vitals check** (将来、Phase C7)

## Gotchas

- **Cloudflare Pages の build minutes 上限** (free plan で月 500 build) に注意。`paths` filter で不要な build を抑制
- **Wasm artifact のサイズ膨張**: Compose Multiplatform Wasm bundle は MB 級になりやすい、`./gradlew :app:wasmJsBrowserDistribution --info` で size 確認
- **CSP `wasm-unsafe-eval`** は Compose Multiplatform の Wasm runtime に必須、外すと動かない
- **Preview deployment のドメイン**: `*.colormaster.pages.dev` 等、GIS Client ID の Authorized JavaScript origins に登録忘れに注意 (本番 / dev を別 Client ID にする運用と整合)
- 旧 Firebase Hosting 設定 (`firebase.json` / `.firebaserc`) は撤去 (`removed-modules.md`)

## 関連

- ADR 0022 (Cloudflare Pages + R2)
- ADR 0012 (旧 JS 実装撤去 → Wasm 統一)
- ADR 0024 (MCP 採用、Cloudflare MCP)
- `.claude/rules/{wasm-compat,r2-litestream,mcp-usage,secrets,removed-modules}.md`
- Cloudflare Pages: https://developers.cloudflare.com/pages/
