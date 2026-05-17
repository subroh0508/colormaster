---
id: arch-sequences
title: 主要ユースケースのシーケンス
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.2 / §3.3 / §3.4
related_adrs:
  - ADR-0007
  - ADR-0008
  - ADR-0011
  - ADR-0014
---

# 主要ユースケースのシーケンス

> **5 行以内 summary**: ColorMaster の主要 4 ユースケース (ログイン / アイドル検索 /
> 担当追加 / 同期実行) と Cloud Run 起動 (Litestream restore) のシーケンスを Mermaid
> `sequenceDiagram` で集約する。Phase C 進行に応じて各 EPIC の詳細設計
> (`docs/specifications/detail/`) で更新、本ファイルは概観として残す。本格化は A2-5 + C3-C7。

## ユースケース 1: ログイン (GIS)

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Client as Client (Android/iOS/wasmJs)
    participant GIS as GIS
    participant Backend as Cloud Run (Ktor)
    participant JWKS as Google JWKS

    User->>Client: サインインボタンタップ
    Client->>GIS: requestIdToken()
    GIS-->>User: 同意画面 (Google アカウント選択)
    User->>GIS: 同意
    GIS-->>Client: ID Token (JWT, exp 1 時間)
    Client->>Client: secure storage に保存
    Client->>Backend: GET /api/me/profile\nAuthorization: Bearer <ID Token>
    Backend->>JWKS: fetch /oauth2/v3/certs (cache 6 時間)
    JWKS-->>Backend: 公開鍵セット
    Backend->>Backend: JWT 署名 + iss/aud/exp 検証
    Backend->>Backend: sub claim → uid 抽出
    Backend->>GIS: GET /oauth2/v3/userinfo (memory cache 15 分)
    GIS-->>Backend: {name, email, picture}
    Backend-->>Client: 200 {uid, name, picture}
    Client-->>User: ホーム画面 (uid 表示)
```

> 補足: `email` は **クライアントには返さない** (PII 最小化、ADR 0020)。`name` と `picture`
> は表示用に返却するが、Backend memory cache TTL 15 分のみで永続化しない。

## ユースケース 2: アイドル検索

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Screen as SearchScreen
    participant VM as SearchViewModel
    participant Repo as IdolRepository
    participant Net as ApiClient
    participant Backend as Cloud Run
    participant IdolsDb as idols.db

    User->>Screen: 検索クエリ入力 "はるか"
    Screen->>VM: onAction(OnQueryChanged("はるか"))
    VM->>VM: state = Loading
    VM->>Repo: search(query="はるか")
    Repo->>Repo: memory cache 確認 (miss)
    Repo->>Net: GET /api/idols/search?q=はるか
    Net->>Backend: HTTPS request (Cache-Control: public, max-age=86400)
    Backend->>IdolsDb: SELECT * FROM idols WHERE name LIKE ?
    IdolsDb-->>Backend: 3 rows
    Backend-->>Net: 200 {idols: [...]}
    Net-->>Repo: List<IdolDto>
    Repo->>Repo: DTO → Idol マッピング + cache 格納
    Repo-->>VM: List<Idol>
    VM->>VM: state = Loaded(idols)
    Screen-->>User: 検索結果リスト表示
```

> 認証不要 (`/api/idols/*` は公開マスタ)。Cloud Run の `Cache-Control: public, max-age=86400`
> で CDN レイヤがあれば自動キャッシュ、クライアント側も Repository memory cache。

## ユースケース 3: 担当追加

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Screen as IdolDetailScreen
    participant VM as IdolDetailViewModel
    participant Repo as MeRepository
    participant Net as ApiClient
    participant Backend as Cloud Run
    participant UsersDb as users.db
    participant LS as Litestream
    participant R2 as Cloudflare R2

    User->>Screen: 「担当に追加」ボタンタップ
    Screen->>VM: onAction(OnAddFavorite(idolId, kind=Tantou))
    VM->>VM: 楽観更新 (UI 即座に追加表示)
    VM->>Repo: addFavorite(idolId, Tantou)
    Repo->>Net: POST /api/me/favorites\nAuthorization: Bearer <ID Token>\nbody: {idolId, kind: "tantou"}
    Net->>Backend: HTTPS request
    Backend->>Backend: requireUid() (ID Token 検証 + uid 抽出)
    Backend->>UsersDb: INSERT INTO favorites VALUES (uid, idolId, now, 'tantou')
    UsersDb->>UsersDb: WAL 更新
    UsersDb-->>Backend: OK
    Backend-->>Net: 201 Created
    Net-->>Repo: Success
    Repo-->>VM: Success
    Note over LS,R2: 非同期 (background)
    UsersDb->>LS: WAL 変更通知
    LS->>R2: PUT WAL chunk
    R2-->>LS: 200 OK
    VM-->>Screen: 確定 (楽観更新が成功確定)
    Screen-->>User: トースト「担当に追加しました」
```

> 失敗時は楽観更新をロールバック (UI から削除) + エラー表示。Backend の `INSERT` が失敗した
> 場合 Litestream は発火しない (原子性は SQLite が保証)。

## ユースケース 4: im@sparql 同期 (GitHub Actions cron)

```mermaid
sequenceDiagram
    participant Cron as GitHub Actions cron
    participant GH as GitHub API
    participant State as data/.imasparql-sync-state.json
    participant Sparql as imas/imasparql endpoint
    participant Cli as backend/cli
    participant Repo as repo workspace
    participant PR as PR

    Cron->>GH: gh api repos/imas/imasparql/commits/master
    GH-->>Cron: {sha: "abc123..."}
    Cron->>State: read upstream_sha
    State-->>Cron: "def456..."
    alt SHA 一致
        Cron->>Cron: no-op (exit 0)
    else SHA 不一致
        Cron->>Cli: ./gradlew :backend:cli:fetchIdolColorsFromImasparql
        Cli->>Sparql: SPARQL query (Fuseki or production endpoint)
        Sparql-->>Cli: RDF triples
        Cli->>Cli: マッピング (RDF → Idol / Brand / ColorPalette)
        Cli->>Repo: write data/idols.db, idols.json
        Cli->>Cli: レコード数 ±X% チェック
        alt 異常 (大幅減 / 増)
            Cli-->>Cron: fail (exit 非ゼロ)
            Cron->>GH: gh issue create (異常検知)
        else 正常
            Cli->>State: write upstream_sha = "abc123..."
            Cron->>PR: gh pr create --head chore/sync-imasparql-abc1234
            PR-->>Cron: PR #N (human/AI review 必須)
        end
    end
```

> 詳細は `data-flow.md` および `docs/runbooks/sync-imasparql.md` (C6 で本格化) を参照。

## ユースケース 5: Cloud Run 起動 (Litestream restore)

```mermaid
sequenceDiagram
    participant CR as Cloud Run
    participant Img as コンテナイメージ
    participant Startup as scripts/startup.sh
    participant LS as Litestream
    participant R2 as Cloudflare R2
    participant Db as /data/users.db
    participant Server as Ktor Server

    Note over CR: 新リクエスト到来 (cold start)
    CR->>Img: pull (Artifact Registry から)
    Img->>Startup: container entrypoint
    Startup->>LS: litestream restore -o /data/users.db r2://bucket/users.db
    LS->>R2: GET WAL chunks
    R2-->>LS: WAL data
    LS->>Db: 再構築
    LS-->>Startup: restore 完了
    alt restore 成功
        Startup->>LS: litestream replicate (daemon 起動、background)
        Startup->>Server: Ktor server start
        Server-->>CR: listen on :8080
        CR-->>Server: HTTP traffic 開始
    else restore 失敗
        Startup-->>CR: exit 非ゼロ
        CR->>CR: retry (max 3 回) → 失敗時 revision rollback 検討
    end
```

> 起動順序の保証は ADR 0008 と `.claude/rules/cloud-run-deploy.md` で規約化 (A2-2 で本格化)。
> restore 失敗時の server 不起動は **必須** (空 DB で API を提供しない、PII 喪失リスク回避)。

## エラーパスの代表例

| ユースケース | 失敗箇所 | 応答 | クライアント挙動 |
|---|---|---|---|
| ログイン | JWKS 取得失敗 | 503 + Retry-After | クライアント側 exponential retry (3 回) |
| ログイン | ID Token 期限切れ | 401 | GIS で再取得 |
| 検索 | idols.db クエリ失敗 (broken DB) | 500 | エラー画面表示、運用通知 |
| 担当追加 | uid 不一致 (他人の uid に書こうとした) | 403 | ローカル状態を破棄、ログアウト誘導 |
| 同期 | im@sparql 5xx | sync workflow fail | Issue 起票、次回 cron で retry |
| Cloud Run 起動 | R2 unreachable | restore fail | Cloud Run retry → revision rollback |

## A2-5 + Phase C で本格化する内容

- 各ユースケースのエラーケース / リトライ / タイムアウトの分岐を Mermaid に明示
- 関連 Spec basic / detail へのリンク (REQ → SPEC → 実装のトレース)
- パフォーマンス目標 (検索 < 500ms、ログイン < 2s 等) の追記
- ユーザー削除フロー (`/api/me` DELETE + R2 WAL 期限切れ) の追加

## 関連

- `overview.md` / `layers.md` / `data-flow.md` / `infrastructure.md` / `state-machines.md`
- ADR 0007 (im@sparql upstream-driven 同期) / 0008 (Backend SQLite + Litestream + R2) / 0011 (GIS 統一) / 0014 (ローカル Fuseki)
- `docs/specifications/{basic,detail}/SPEC-*.md` (各機能の Spec、Phase C で個別作成)
- `docs/runbooks/{sync-imasparql,release,r2-litestream}.md` (C5-C7 で本格化)
- `../api/{auth,idols,me}.md` (API 詳細)
