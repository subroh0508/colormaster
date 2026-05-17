---
id: api-idols
title: アイドル情報 API (/api/idols/* + /api/brands + /api/colors)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.3
related_adrs:
  - ADR-0007
  - ADR-0010
  - ADR-0014
---

# アイドル情報 API (/api/idols/* + /api/brands + /api/colors)

> **5 行以内 summary**: 公開マスタとしてアイドル / ブランド / イメージカラーを返す
> read-only API。データソースはコンテナイメージ焼込の `data/idols.db` (SQLite)、
> 上流は im@sparql (RDF / SPARQL)。認証不要、`Cache-Control: public, max-age=86400`
> で CDN レイヤを許容。本格実装は C3 (`feature/*` 再編) + C5 (Backend ハンドラ実装)。

## エンドポイント

| メソッド | パス | 用途 | 認証 | ステータス |
|---|---|---|---|---|
| GET | `/api/idols` | アイドル一覧 (ブランド / カラー フィルタ + ページネーション) | 不要 | C5 で本格実装 |
| GET | `/api/idols/{id}` | 個別アイドル詳細 | 不要 | C5 |
| GET | `/api/idols/search` | キーワード検索 (name / nameRuby / brand / colorName 全文) | 不要 | C5 |
| GET | `/api/brands` | ブランド一覧 | 不要 | C5 |
| GET | `/api/colors` | カラーパレット一覧 | 不要 | C5 |

スキーマ詳細は `colormaster-api.yaml` を参照。

## クエリパラメータ規約

### `/api/idols` (一覧 + フィルタ)

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|---|---|---|---|---|
| `brand` | string | 任意 | (未指定なら全件) | ブランド ID (765AS / CG / ML / SC / SS / SP 等) |
| `color` | string (`#RRGGBB`) | 任意 | (未指定なら全件) | 完全一致のイメージカラー (Hex 6 桁) |
| `page` | integer (>=1) | 任意 | 1 | ページ番号 (1-origin) |
| `pageSize` | integer (1-200) | 任意 | 50 | 1 ページあたり件数 |

- 不正値は `400 bad_request` + `details: { field: "...", reason: "..." }`
- `brand` と `color` は AND 結合 (両指定時は両方一致)

### `/api/idols/search`

| パラメータ | 型 | 必須 | 説明 |
|---|---|---|---|
| `q` | string (1-100 字) | **必須** | 検索クエリ。`name` / `nameRuby` / `brand.name` / `color.name` の **OR 全文** |
| `page` | integer | 任意 | 同上 |
| `pageSize` | integer | 任意 | 同上 |

- `q` が空 / 101 字以上 → `400 validation_failed`
- 検索アルゴリズム: SQLite `LIKE '%q%'` または FTS5 (C5 で決定、`.claude/rules/sql-delight.md` 準拠)
- 結果はスコアでソートせず、`nameRuby` 昇順固定 (将来 ADR で再検討)

### `/api/idols/{id}`

- `id` は IdolId (im@sparql RDF 識別子 slug、例: `imas_Idol_天海春香` → URL safe slug 化)
- 存在しない場合は `404 not_found`

## レスポンス形式

`colormaster-api.yaml` の `components/schemas` を参照:

- `IdolList` (idols + page + pageSize + total)
- `Idol` (id + brandId + name + nameRuby + color)
- `Brand` (id + name + nameEn)
- `ColorPalette` (primary + name? + onPrimary)

各 schema の意味とドメイン上の制約は `../architecture/domain-model.md` を参照。

## キャッシュ戦略

| レイヤ | 戦略 | TTL | 失効契機 |
|---|---|---|---|
| Backend → Client (HTTP) | `Cache-Control: public, max-age=86400` | 24 時間 | デプロイ (同期 PR merge → 新コンテナリリース) で URL ベース失効 |
| Backend memory | なし (idols.db を直接クエリ) | — | (DB 読み込みコストは無視できる程度) |
| クライアント Repository | memory cache (全件、起動時取得) | なし | 画面のリフレッシュアクション (将来導入) |
| CDN (Cloudflare 等) | 採用していない (現状) | — | 将来 ADR で検討 |

- **stale-while-revalidate** は使わない (シンプルな max-age のみ)
- **ETag / If-Modified-Since** は採用しない (Backend デプロイの SHA で URL は変わらないため意味薄)
- 必要があれば将来 `Cache-Control: public, max-age=86400, immutable` への昇格を検討

## データソースとライフサイクル

```
imas/imasparql (RDF)
   ↓ 日次 GitHub Actions cron + SHA 監視 (ADR 0007)
data/idols.db (リポジトリ commit)
   ↓ Dockerfile で COPY (ADR 0010)
Cloud Run コンテナイメージに焼込 (read-only)
   ↓ Ktor server がクエリ
HTTP レスポンス (Cache-Control 24 時間)
```

- 同期は `docs/runbooks/sync-imasparql.md` (C6 で本格化) と `.claude/rules/{sync-job,sparql}.md` を参照
- Cloud Run コンテナの `idols.db` は読み取り専用、書込みは `users.db` のみ (`me.md`)
- ローカル開発では Fuseki Docker (ADR 0014) で im@sparql の代替を立てられる (`docs/runbooks/local-imasparql.md`、C6)

## レスポンス例 (JSON、参考)

> 注: 完全なスキーマは `colormaster-api.yaml` を SoT とする。本ファイルでは可読性のために
> 短い参考例のみ示す (PR レビューでの構造把握用)。

```json
GET /api/idols?brand=765AS&pageSize=2

200 OK
Content-Type: application/json; charset=utf-8
Cache-Control: public, max-age=86400

{
  "idols": [
    {
      "id": "imas_Idol_天海春香",
      "brandId": "765AS",
      "name": "天海春香",
      "nameRuby": "あまみはるか",
      "color": {
        "primary": "#E22B30",
        "name": null,
        "onPrimary": "#FFFFFF"
      }
    },
    {
      "id": "imas_Idol_如月千早",
      "brandId": "765AS",
      "name": "如月千早",
      "nameRuby": "きさらぎちはや",
      "color": {
        "primary": "#2743D2",
        "name": null,
        "onPrimary": "#FFFFFF"
      }
    }
  ],
  "page": 1,
  "pageSize": 2,
  "total": 13
}
```

## エラーケース

| HTTP | `error.code` | 発生条件 |
|---|---|---|
| 400 | `bad_request` | クエリパラメータ型不正 (`page=abc` 等) |
| 400 | `validation_failed` | `q` が空 / 過長、`color` が `#RRGGBB` 形式違反 |
| 404 | `not_found` | `/api/idols/{id}` の id 不存在 |
| 500 | `internal_error` | DB 破損 / 想定外 exception (details は redaction) |

レート制限は現状なし (将来導入時は `429` 追加 + ADR 起票)。

## 機械検証 (`.claude/rules/`、A2-2 + A6 で本格化)

| 規約 | 検証手段 |
|---|---|
| `id` パラメータの slug 文字種 | Konsist でハンドラの正規表現マッチ要求 |
| Cache-Control 必須 | Konsist で `/api/idols/*` ハンドラの response 設定検証 |
| `details` フィールドに PII 含めない | Konsist + `.claude/rules/pii.md` redaction 規約 |
| `idols.db` への書込みなし (read-only) | Konsist で `INSERT / UPDATE / DELETE` 文を `idols.db` 接続から検出 |

## クライアント側の利用パターン (Repository + UseCase、C3 で本格化)

- `IdolRepository.findAll()`: 起動時に全件取得 + memory cache (R-3 越境ルール)
- `IdolRepository.findByBrand(brandId)`: cache hit 後にフィルタ
- `IdolRepository.search(query)`: cache が新鮮なら local search、stale なら `/api/idols/search` 呼出
- `IdolRepository.findById(id)`: cache hit、miss なら `/api/idols/{id}` 呼出

詳細は `../architecture/{data-flow,sequences}.md` の検索ユースケースを参照。

## 現状 (B0 段階、2026-05-17 時点)

- `backend/server/` の `/api/idols/*` ハンドラは **未実装** (C5)
- `data/idols.db` は repo に commit 済、im@sparql 由来データを保持
- 既存の `core/network/imasparql/` は SPARQL クライアントの骨格、本格化は C6
- 現存する `core/features/*` の旧画面は `core/network/firestore/` 経由で動作 (C3 + C5 で全置換予定)

## 関連

- ADR 0007 (im@sparql upstream-driven 同期) / 0010 (idols.db repo 内 commit) / 0014 (ローカル Fuseki)
- `colormaster-api.yaml` (`/api/idols/*` `/api/brands` `/api/colors` パスとスキーマ SoT)
- `../architecture/{data-flow,domain-model,sequences}.md`
- `docs/runbooks/sync-imasparql.md` (C6 で本格化) / `docs/runbooks/local-imasparql.md`
- `.claude/rules/{sync-job,sparql,sqlite-data-file,sql-delight,network-client}.md`
