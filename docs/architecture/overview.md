---
id: arch-overview
title: アーキテクチャ概要
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3
related_adrs: [ADR-0002, ADR-0003]
---

# アーキテクチャ概要

> **5 行以内 summary**: ColorMaster は Compose Multiplatform + 共通 ViewModel +
> Navigation 3 を採用した KMP プロジェクト。Android / iOS / wasmJs / JVM の 4 ターゲット
> を持ち、Backend は Ktor を Cloud Run にデプロイする。詳細は他 6 ファイル
> (layers / data-flow / domain-model / state-machines / sequences / infrastructure) を参照。

## 構成 (A2 で本格化)

A2 で各ファイル (`layers.md` / `data-flow.md` / `domain-model.md` / `state-machines.md` / `sequences.md` / `infrastructure.md`) を本格化する。本ファイルは索引と全体像のみ。

### モジュール俯瞰 (TODO: A2 で Mermaid 図化)

```
android (Android App)
  ↓
feature/* (画面別モジュール、C3 で新設)
  ↓
core/data, core/domain, core/network
  ↓
backend (Ktor on Cloud Run)
  ↓
im@sparql (外部) / GIS (Google Identity Services) / R2 (Litestream)
```

### Backend と外部依存

- **Backend** (`backend/`): Ktor、Cloud Run デプロイ、内蔵 SQLite + Litestream (`users.db`) / アイドル情報 SQLite (`idols.db`、コンテナイメージ焼込)
- **GIS**: 認証統一 (Firebase Auth から移行、ADR 0011)
- **im@sparql**: アイドル情報の上流ソース、upstream-driven sync (ADR 0007)
- **R2**: Litestream バックアップ先 (ADR 0022)
- **Cloudflare Pages**: 静的配信 (wasmJs 完成後、ADR 0022)

## 詳細 (他ファイル)

| ファイル | 内容 |
|---|---|
| `layers.md` | 層別責務 (feature → core → repository → network) |
| `data-flow.md` | データフロー (im@sparql → backend → client) |
| `domain-model.md` | ドメインモデル (アイドル / ブランド / カラー) |
| `state-machines.md` | UiState 状態遷移 |
| `sequences.md` | 主要ユースケースのシーケンス |
| `infrastructure.md` | ホスティング / IaC 構成 (Cloud Run + Cloudflare Pages + R2 + GIS) |

## 関連

- `docs/harness/plan.md` §3 (設計指針)
- ADR 0002 (Compose Multiplatform + 共通 ViewModel + Navigation 3)
- ADR 0003 (モジュール構造 feature-first)
- ADR 0009 / 0011 / 0022 (Backend / 認証 / 静的配信)
- `docs/codebase-map.md`
