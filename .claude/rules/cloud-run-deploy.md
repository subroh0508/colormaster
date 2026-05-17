---
id: rules-cloud-run-deploy
title: Cloud Run デプロイ規約
status: stable
last_updated: 2026-05-17
paths:
  - "Dockerfile"
  - ".dockerignore"
  - "backend/**"
  - ".github/workflows/deploy-backend.yml"
related_adrs:
  - ADR-0009
  - ADR-0020
  - ADR-0021
  - ADR-0022
---

# cloud-run-deploy.md — Cloud Run デプロイ規約

> Backend は **Google Cloud Run** にデプロイ (ADR 0009)。コンテナ起動時に Litestream で
> R2 から `users.db` を restore し、Ktor Server で API を提供する。**`min-instances=0`**
> でコスト最小化、cold start ~5s を許容 (詳細は本 rule §パフォーマンス参照)。

## デプロイ構成

| 項目 | 値 | 根拠 |
|---|---|---|
| Region | `asia-northeast1` (東京) | 国内ユーザー向けレイテンシ |
| Min instances | 0 | コスト最小化 (cold start 許容) |
| Max instances | 5 | スパイクトラフィック上限 (Phase B-C で再評価) |
| Memory | 512 MiB | Ktor + SQLite + Litestream の最小構成 |
| CPU | 1 | 同上 |
| Concurrency | 80 | Cloud Run デフォルト |
| Service Account | `cloud-run-backend@<project>.iam.gserviceaccount.com` | R2 / Secret Manager アクセス用 |

## Dockerfile 規約

```dockerfile
FROM eclipse-temurin:21-jre-alpine AS runtime

# Litestream バイナリ取得
RUN apk add --no-cache curl bash && \
    curl -L https://github.com/benbjohnson/litestream/releases/download/v0.3.13/litestream-v0.3.13-linux-amd64.tar.gz \
        | tar xz -C /usr/local/bin/

WORKDIR /app

# アイドル情報 DB (read-only) は焼込み
COPY data/idols.db /app/data/idols.db

# Backend JAR
COPY backend/build/libs/backend-all.jar /app/backend.jar

# users.db は image に含めない (Litestream restore で hydrate)
# COPY data/users.db ❌ 禁止

# 起動スクリプト
COPY scripts/cloud-run-start.sh /app/start.sh
RUN chmod +x /app/start.sh

EXPOSE 8080
CMD ["/app/start.sh"]
```

- **`data/users.db` を COPY しない** (`.dockerignore` で除外、`db-protection.md` 必須項目)
- **Litestream バイナリ取得**: pin した version (`v0.3.13`) でハッシュ検証推奨 (将来追加)
- Base image は **`alpine` 系** で軽量化、JRE 21 を使用 (Kotlin 2.0+ 対応)

## 起動スクリプト (`scripts/cloud-run-start.sh`)

```bash
#!/bin/bash
set -e

# Litestream restore (R2 から users.db を hydrate)
litestream restore -if-replica-exists -config /etc/litestream.yml /data/users.db

# Backend 起動 (Litestream replicate を background で起動)
litestream replicate -config /etc/litestream.yml &

# Ktor Server 起動
exec java -jar /app/backend.jar
```

- restore 失敗時は Backend 起動を継続 (新規 DB として動作)
- Litestream config (`litestream.yml`) は **R2 endpoint + bucket** を環境変数経由で参照、詳細は `r2-litestream.md`

## 環境変数 / Secret Manager

| 変数 | 用途 | 取得元 |
|---|---|---|
| `R2_ACCESS_KEY_ID` | R2 認証 | Google Cloud Secret Manager |
| `R2_SECRET_ACCESS_KEY` | R2 認証 | 同上 |
| `R2_ENDPOINT` | R2 endpoint URL | 同上 |
| `IMASPARQL_BASE_URL` | im@sparql endpoint | Cloud Run env (公開可) |
| `GIS_CLIENT_ID` | Google Identity Services client ID | Cloud Run env (公開可) |
| `JWKS_URL` | GIS JWKS endpoint | Cloud Run env |

- Secret Manager 参照は Cloud Run の `--update-secrets` フラグ経由 (Service Account が `secretAccessor` ロール保持)
- 公開可能な設定 (URL 等) は env var、秘密情報は Secret Manager に分離

## CI / CD

```yaml
# .github/workflows/deploy-backend.yml (概略)
on:
  push:
    branches: [master]
    paths: ['backend/**', 'Dockerfile', '.github/workflows/deploy-backend.yml']

jobs:
  deploy:
    runs-on: ubuntu-latest
    permissions: { id-token: write, contents: read }  # Workload Identity Federation
    steps:
      - checkout
      - setup-gcloud (with WIF)
      - run: ./gradlew :backend:shadowJar
      - run: gcloud builds submit --tag asia-northeast1-docker.pkg.dev/...
      - run: gcloud run deploy backend --image=...
```

- **Workload Identity Federation** で GitHub Actions → GCP 認証 (Service Account key は使わない)
- 本番デプロイは **master ブランチ** のみ、PR では deploy しない
- **GitHub Actions で Claude API を呼ばない** (ADR 0017)

## パフォーマンス

- **Cold start**: ~5s (Litestream restore + Kotlin JVM 起動)
- **温まると ~200ms / request** を目標
- min-instances=0 でコスト最小化、トラフィック増時は max-instances=5 までスケール
- Phase B-C で **Cloud Run gen2** + **CPU always allocated** の有償オプション再評価

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `Dockerfile` 内に `COPY data/users.db` パターンが存在しない (`db-protection.md` 必須)
  - `.dockerignore` が必須項目 (`data/users.db*` / `.env*` / `*-credentials.json` / `service-account*.json` / `.claude/oauth-tokens*`) を含む
  - `Dockerfile` の base image が allowed list (`eclipse-temurin:21-jre-alpine` 等) 内
- **CI** で Docker image の secret scan (`trivy` / `dockle` 候補、A6 で導入)

## Gotchas

- **min-instances=0 の cold start は ~5s**、即時応答が必要な endpoint はキャッシュ層で吸収
- **Litestream restore 中に Backend 起動を待たない**ことに注意 (起動スクリプトで sequential 実行)
- **Cloud Run の `--allow-unauthenticated` を使う場合**は Ktor 側で GIS ID Token 検証必須 (`backend-auth.md`)
- **GitHub Actions の OIDC token を Service Account key で代用しない** (key 漏洩リスク、WIF が標準)
- ローカル開発は `docker compose up` で Backend + Fuseki を起動 (`docs/runbooks/local-development.md`、A2-4 で本格化)

## 関連

- ADR 0009 (Backend Cloud Run)
- ADR 0020 (PII 保護)
- ADR 0021 (Secrets 管理)
- ADR 0022 (Cloudflare Pages + R2)
- `.claude/rules/{db-protection,r2-litestream,backend-auth,secrets,sqlite-data-file}.md`
- `docs/runbooks/release.md` (B0 配置済、Phase B-C で本格化)
