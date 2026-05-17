---
id: api-readme
title: API 概要 (colormaster-api)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4
related_adrs:
  - ADR-0011
---

# API 概要

> **5 行以内 summary**: ColorMaster Backend (`colormaster-api`) は GIS ID Token 認証下で
> `/api/idols/*` (read-only マスタ) と `/api/me/*` (ユーザー個別データ) を提供する。
> 詳細仕様の Single Source of Truth は `colormaster-api.yaml` (OpenAPI 3.1)。
> 個別エンドポイントの解説は `auth.md` / `idols.md` / `me.md` を参照。本格化は C5。

## エンドポイント分類

| グループ | 認証 | 詳細 |
|---|---|---|
| `/api/idols/*` | 不要 (公開マスタ) | `idols.md` |
| `/api/me/*` | GIS ID Token (Bearer) 必須 | `me.md` |
| `/auth/*` | GIS フロー | `auth.md` |

## Single Source of Truth

- **`colormaster-api.yaml`** (OpenAPI 3.1) — 全リクエスト/レスポンス JSON スキーマ
- 各 `<endpoint>.md` は **使い方 / 設計判断 / 例外パターン** を散文で記述 (リクエスト/レスポンス JSON は yaml 側に集約)

## 関連

- ADR 0011 (GIS 統一認証 + Firebase 撤去)
- ADR 0009 (Cloud Run デプロイ)
- `docs/architecture/{overview,data-flow,infrastructure}.md`
- `docs/specifications/basic/SPEC-*.md` (C5 で個別作成)
