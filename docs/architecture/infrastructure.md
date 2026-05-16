---
id: arch-infrastructure
title: インフラ構成 (Cloud Run + Cloudflare Pages + R2 + GIS)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.4 / §3.5
related_adrs: [ADR-0008, ADR-0009, ADR-0011, ADR-0022]
---

# インフラ構成

> **5 行以内 summary**: ColorMaster の本番インフラはハイブリッド構成 — Backend は
> Cloud Run (Google Cloud)、静的配信は Cloudflare Pages、Litestream バックアップ先は
> Cloudflare R2、認証は GIS (Google Identity Services)。Terraform は不使用 (デプロイは
> GitHub Actions + gcloud CLI + wrangler)。本格化は A2 + C7 デプロイ Plan。

## 構成要素

| 要素 | 用途 | デプロイ手段 | 関連 ADR |
|---|---|---|---|
| **Cloud Run** | Ktor Backend のホスティング | GitHub Actions + gcloud CLI + Artifact Registry | ADR 0009 |
| **Cloudflare Pages** | wasmJs 静的配信 | wrangler / Pages CLI | ADR 0022 |
| **Cloudflare R2** | Litestream バックアップ先 (`users.db` の WAL) | wrangler / Cloudflare MCP | ADR 0008 / 0022 |
| **GIS** (Google Identity Services) | 認証統一プロバイダ | GCP コンソール (Client ID 発行) | ADR 0011 |
| **Secret Manager** | 本番 secrets (R2 token / GIS Client Secret) | gcloud CLI、Cloud Run service account から参照 | ADR 0021 |
| **Artifact Registry** | Backend コンテナイメージ保管 | GitHub Actions + gcloud CLI | ADR 0009 |

## 採用見送り

- **Cloudflare Containers**: Workers Paid plan $5/月必須でコスト劣後 (ADR 0009)
- **Terraform**: 構成が小さく学習コスト・運用コストの方が大きい。GitHub Actions スクリプト + CLI で管理 (`docs/harness/plan.md` §3.5)

## デプロイフロー (C7 で本格化)

1. master push → GitHub Actions が `./gradlew check` を実行
2. Backend イメージビルド → Artifact Registry へ push
3. `gcloud run deploy` で Cloud Run 更新
4. wasmJs ビルド → Cloudflare Pages へ wrangler 経由でデプロイ
5. デプロイ完了通知

## 関連

- `docs/harness/plan.md` §3.4 / §3.5
- ADR 0008 / 0009 / 0011 / 0022
- `docs/runbooks/release.md` (C7 で本格化)
- `docs/runbooks/secrets-rotation.md`
- `docs/runbooks/r2-litestream.md`
