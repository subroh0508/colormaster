---
id: rules-r2-litestream
title: R2 + Litestream replicate / restore 規約
status: stable
last_updated: 2026-05-17
paths:
  - "litestream.yml"
  - "scripts/cloud-run-start.sh"
  - "backend/**"
  - ".github/workflows/deploy-backend.yml"
related_adrs:
  - ADR-0008
  - ADR-0021
  - ADR-0022
---

# r2-litestream.md — R2 + Litestream replicate / restore 規約

> Backend SQLite (`users.db`) を **Litestream で Cloudflare R2 に continuous replicate** (ADR 0008)。
> 起動時に R2 から restore して disaster recovery を実現。本格化は Phase C5 だが本 rule で起草。
> **R2 token TTL 90 日** (ADR 0021)、bucket は private (`db-protection.md` 参照)。

## アーキテクチャ

```text
[Cloud Run Container]
    Backend (Ktor Server)
        ↓ writes
    /data/users.db (SQLite, WAL mode)
        ↑ Litestream WAL replication
[Cloudflare R2]
    bucket: colormaster-users-db-backup (private)
        objects: /users.db/wal-XXXXXXXX
```

- **Litestream**: SQLite WAL を S3 互換 storage に continuous replicate
- **R2**: S3 互換 API、egress 無料 (Cloudflare 内 Cloud Run からの read は別 region 経由でも安価)
- **Disaster Recovery**: Cloud Run コンテナ完全再作成時も R2 から restore で復旧

## Litestream 設定 (`litestream.yml`)

```yaml
dbs:
  - path: /data/users.db
    replicas:
      - type: s3
        endpoint: ${R2_ENDPOINT}
        bucket: colormaster-users-db-backup
        path: users.db
        region: auto
        access-key-id: ${R2_ACCESS_KEY_ID}
        secret-access-key: ${R2_SECRET_ACCESS_KEY}
        sync-interval: 10s
        retention: 720h         # 30 日保持
        retention-check-interval: 1h
```

- **`endpoint`**: R2 endpoint URL (例: `https://<account-id>.r2.cloudflarestorage.com`)
- **`region: auto`** (R2 は region 概念なし、auto で OK)
- **`sync-interval: 10s`**: WAL を 10 秒ごとに R2 へ flush (RPO ~10 秒)
- **`retention: 720h`**: 30 日保持、それ以前は削除 (cost control)

## R2 bucket 設定

| 項目 | 値 | 根拠 |
|---|---|---|
| Bucket name | `colormaster-users-db-backup` | 本番用 |
| Visibility | **private** | PII 含む、絶対公開禁止 (`db-protection.md` / `pii.md`) |
| Lifecycle rule | 90 日経過オブジェクト削除 | コスト管理 |
| CORS | 無効 | Backend のみアクセス、ブラウザ不要 |
| Bucket policy | Backend Service Token のみ allow | アクセス最小化 |

## R2 API Token

- **TTL 90 日** (ADR 0021)、定期ローテーション (`secrets.md` 参照)
- 権限: 該当 bucket の `Object Read / Write` のみ (他 bucket / dashboard 操作不可)
- Cloud Run 側は **Google Cloud Secret Manager** に格納、起動時に env var で参照 (`cloud-run-deploy.md`)

## restore フロー

```bash
# scripts/cloud-run-start.sh (cloud-run-deploy.md と統合)
litestream restore -if-replica-exists -config /etc/litestream.yml /data/users.db
```

- **`-if-replica-exists`**: 初回起動 (R2 に何もない) でも fail しない
- restore 完了後に Backend 起動
- restore 中の Backend 起動はしない (`start.sh` で sequential)

## replicate フロー

```bash
litestream replicate -config /etc/litestream.yml &
```

- Backend と並走 (background)
- Cloud Run コンテナ shutdown 時に SIGTERM を受信 → Litestream が graceful shutdown (WAL flush 完了まで待つ)

## 機械検証 (A6 / C5 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `litestream.yml` の bucket / endpoint が environment variable 経由 (hardcode 禁止)
  - `litestream.yml` 内に access key 直書きがない (Secret Manager 経由)
- **CI** で Litestream バイナリの SHA256 検証 (sig pinning、A6 で追加)

## バックアップ検証

- 月次で **Litestream restore リハーサル** を runbook (`docs/runbooks/release.md` または新規 `docs/runbooks/db-restore-drill.md`、Phase C5) で実施
- R2 → 別の test 環境に restore → schema / row count sanity check
- 検証結果は `docs/runbooks/db-restore-drill.md` の履歴セクションに記録

## 機械検証 (Phase C5 で導入)

- **Health check endpoint** (`/healthz`) で R2 connectivity check 含めるか検討
- Cloud Run 起動時 Litestream restore failure → Cloud Run の startup probe で fail させ、再起動

## Gotchas

- **WAL mode 必須**: SQLite を WAL mode で動かす (`PRAGMA journal_mode=WAL;`)、Litestream の前提
- **`users.db-shm` / `users.db-wal` は restore 時に自動再生成** される、別途扱う必要なし
- **`sync-interval` を短くしすぎると R2 PUT request 数が増えコスト増**、`10s` を初期値とし運用後に調整
- **R2 endpoint と access key の漏洩** は即座に bucket access が破綻するため、Secret Manager と GitHub Secrets の二重管理 + 90 日ローテ (`secrets.md`)
- **Litestream version pinning**: Dockerfile で固定 version (`v0.3.13` 等) を取得、auto-update は禁止 (compatibility 検証必須)
- **R2 → Cloud Run の egress**: 同 region でないため latency に注意 (~10ms 程度を見込む)

## 関連

- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0021 (R2 token TTL 90 日)
- ADR 0022 (Cloudflare Pages + R2)
- Litestream: https://litestream.io/
- Cloudflare R2: https://developers.cloudflare.com/r2/
- `.claude/rules/{db-protection,sqlite-data-file,cloud-run-deploy,secrets,backend-auth,cloudflare-pages}.md`
- `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)
