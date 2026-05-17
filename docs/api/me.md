---
id: api-me
title: ユーザーデータ API (/api/me/*)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.2 / §3.8
related_adrs:
  - ADR-0008
  - ADR-0011
  - ADR-0020
---

# ユーザーデータ API (/api/me/*)

> **5 行以内 summary**: 認証済ユーザーの担当・推し一覧 / プロフィール参照を扱う。GIS ID Token
> Bearer 認証必須、Backend 内蔵 SQLite (`users.db`) に uid のみ保存 (PII 最小化)。
> Litestream で R2 に WAL replicate、起動時 restore。本格実装は C5 (EPIC-003)。

## エンドポイント (骨格、本格化は C5)

| メソッド | パス | 用途 |
|---|---|---|
| GET | `/api/me/profile` | uid + GIS userinfo (memory cache TTL 15 分) |
| GET | `/api/me/favorites` | ユーザーの担当・推し一覧 |
| POST | `/api/me/favorites` | 担当・推し追加 |
| DELETE | `/api/me/favorites/{idolId}` | 担当・推し削除 |

## 認可

- 全エンドポイントで `Authorization: Bearer <GIS ID Token>` 必須
- Backend 側で **`requireUid()` ヘルパ** を呼び出さない実装は Konsist 規約違反 (A6 で機械化、`.claude/rules/backend-auth.md`)
- uid 単位での所有権チェック (自分以外の uid のデータには触れない)

## DB スキーマ (PII 最小化、ADR 0020)

| テーブル | カラム | 説明 |
|---|---|---|
| `users` | `uid` (PK) | Google Account ID (sub claim)。他の PII は保存しない |
| `favorites` | `uid`, `idol_id`, `added_at` | 担当・推し一覧 |

## ユーザー削除

- `/api/me` への DELETE で `users` + `favorites` を物理削除 + R2 上の WAL も古いものから期限切れで自然消滅
- 削除 runbook は `docs/runbooks/user-deletion.md` (将来作成)

## 関連

- ADR 0008 (Backend SQLite + Litestream + R2)
- ADR 0011 (GIS 統一)
- ADR 0020 (PII 保護: uid のみ DB 保存)
- `.claude/rules/{backend-auth,pii,db-protection,r2-litestream}.md`
- `docs/api/colormaster-api.yaml`
