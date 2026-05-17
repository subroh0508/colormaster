---
id: api-auth
title: 認証 API (GIS 統一)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.2 / ADR 0011
related_adrs:
  - ADR-0011
  - ADR-0020
---

# 認証 API (GIS 統一)

> **5 行以内 summary**: 全 target で Google Identity Services (GIS) に統一。フロントが
> ID Token を取得 → Backend が JWKS で検証 → uid (sub claim) を抽出して認可に用いる。
> Firebase Auth は完全撤廃 (`dev.gitlive:firebase-*` / `core/network/auth/` を C5 で削除)。
> 本格実装は C5 (EPIC-003)。

## 認証フロー (骨格、C5 で本格化)

1. フロント (Android / iOS / wasmJs) で GIS ID Token を取得
2. Backend の `/api/me/*` エンドポイントに `Authorization: Bearer <ID Token>` で送信
3. Backend は JWKS (`https://www.googleapis.com/oauth2/v3/certs`) で署名検証
4. 検証成功なら `sub` claim (Google Account ID、内部的に `uid` と呼ぶ) を抽出
5. `requireUid()` ヘルパで uid を context として伝播

## エンドポイント

| パス | 用途 | 状態 |
|---|---|---|
| (フロント側 GIS フロー) | ID Token 取得 | 各 platform で実装 (C5/C8/C9) |
| (Backend 側 JWKS 検証) | 自動 (middleware) | C5 で実装 |
| `/api/me/profile` | uid から GIS userinfo を memory cache TTL 15 分付きで返す | C5 で実装 |

## エラーケース

- ID Token 期限切れ → 401 + クライアントに再取得を促す
- JWKS 取得失敗 → 503 (一時的) + リトライ案内
- `sub` claim 不在 → 401

## PII 取扱

- DB に保存する PII は **uid のみ** (`.claude/rules/pii.md` 参照)
- 表示用の display name / email / picture は GIS userinfo から都度取得 + memory cache TTL 15 分

## 関連

- ADR 0011 (GIS 統一認証 + Firebase 撤去)
- ADR 0020 (PII 保護: uid のみ DB 保存)
- `.claude/rules/{backend-auth,pii,no-firebase}.md`
- `docs/api/colormaster-api.yaml` (security scheme)
