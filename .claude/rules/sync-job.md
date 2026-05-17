---
id: rules-sync-job
title: im@sparql 同期ジョブ規約
status: stable
last_updated: 2026-05-17
paths:
  - ".github/workflows/sync-imasparql.yml"
  - "scripts/sync-imasparql/**"
  - "data/idols.db"
related_adrs:
  - ADR-0007
  - ADR-0010
  - ADR-0014
---

# sync-job.md — im@sparql 同期ジョブ規約

> im@sparql endpoint からアイドル情報を取得し、`data/idols.db` (SqlDelight 用 SQLite ファイル) を
> 更新する **upstream-driven 同期** (ADR 0007) の運用規約。GitHub Actions の scheduled workflow
> で動作し、生成された PR を **Renovate ラベル** で `pr-poller` 経由 `dependency-upgrade` Skill が処理。

## 同期トリガー

| トリガー | 頻度 | workflow |
|---|---|---|
| Scheduled (cron) | 週次 (日曜 02:00 JST) | `.github/workflows/sync-imasparql.yml` |
| Manual (`workflow_dispatch`) | 必要時 | 同上 (手動 dispatch) |
| im@sparql 側更新検知 (将来) | webhook | (Phase C で検討) |

## ジョブ構成

```yaml
# .github/workflows/sync-imasparql.yml (概略)
on:
  schedule:
    - cron: '0 17 * * 6'  # 土曜 17:00 UTC = 日曜 02:00 JST
  workflow_dispatch:

jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - checkout
      - run: scripts/sync-imasparql/run.sh
      - name: Create PR
        if: ${{ env.has_diff == 'true' }}
        uses: peter-evans/create-pull-request@v6
        with:
          branch: sync/imasparql-${{ env.timestamp }}
          labels: renovate, sync-imasparql
          title: "sync(imasparql): weekly idol data update"
```

- スクリプト本体は `scripts/sync-imasparql/run.sh` (新規追加予定、Phase B-C で本格化)
- 生成 PR には **`renovate` + `sync-imasparql` ラベル** を付与し、`pr-poller` の `dependency-upgrade` 経由で AI レビュー

## 同期処理ステップ

1. `core/network/imasparql` の SPARQL クエリを起動し、`Idol` / `Brand` / `Color` 全件取得
2. ローカル一時 SQLite DB を作成 (`/tmp/idols-new.db`)
3. 既存 `data/idols.db` と差分比較 (SQL diff)
4. 差分があれば `data/idols.db` を上書き → `git add data/idols.db`
5. PR 作成 (差分 summary は workflow log + PR description)

## アイドル情報 SQLite (`idols.db`) の取り扱い

- **リポジトリに commit OK** (read-only データ、ADR 0010)
- **コンテナイメージにも焼込 OK** (Dockerfile で `COPY data/idols.db /app/data/`)
- スキーマ migration は **不要** (sync で全件再生成 = drop & recreate)
- ユーザーデータ DB (`users.db`) との明確な区別は `db-protection.md` / `sqlite-data-file.md` 参照

## 同期 PR のレビューフロー

1. `pr-poller` (ローカル Claude Code) が `renovate` ラベル PR を検出
2. `dependency-upgrade` Skill が PR 内容を解析
   - diff のサイズ / 影響アイドル数 / Brand 追加検知
3. 自動承認可能な変更 (アイドル追加のみ等) → `Approve` & merge 待ち
4. 注意必要な変更 (既存アイドル削除 / Brand カラー変更等) → 人間承認待ちでコメント post

## 機械検証 (A6 で導入)

- **Gradle カスタムタスク** で以下を検証:
  - `data/idols.db` のスキーマが `core/database/src/commonMain/sqldelight/.../*.sq` と整合
  - sync 後の DB に `Idol` テーブルが空でない (smoke check)
- **Workflow** 内で `sqlite3 data/idols.db "SELECT COUNT(*) FROM Idol"` で件数 sanity check (0 件なら fail)

## Gotchas

- **im@sparql endpoint の rate limit** を遵守 (1 req/sec)、複数クエリは sequential 実行
- **sync で `users.db` を touch しない**。ジョブの権限スコープから明示的に除外 (`db-protection.md` 厳守)
- **diff が大きすぎる PR は warning** (例: 10% 以上のアイドル削除) → 人間レビュー強制
- **同期失敗時の通知**: workflow failure → GitHub Issues 自動起票 (将来、Phase C で実装)
- ローカル開発で sync 試験する場合は **Fuseki Docker** (`docs/runbooks/local-imasparql.md`、A2-4 で本格化) を使用

## 関連

- ADR 0007 (im@sparql upstream-driven 同期)
- ADR 0010 (アイドル情報 SQLite in-repo)
- ADR 0014 (im@sparql ローカル Fuseki)
- `.claude/rules/{sparql,sqlite-data-file,db-protection,pr-poller}.md`
- `.claude/skills/dependency-upgrade/SKILL.md`
- `docs/runbooks/sync-imasparql.md` (A2-4 で本格化)
