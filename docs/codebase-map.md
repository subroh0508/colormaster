---
id: codebase-map
title: コードベースマップ (主要パス → 責務 / 関連 SPEC-ID / 関連 ADR)
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §4
---

# コードベースマップ

> **5 行以内 summary**: 主要パスとその責務 / 関連 SPEC-ID / 関連 ADR の対応表。
> AI が「このディレクトリは何のためにあるか」「触ったら何が壊れるか」を一発で把握する
> ためのインデックス。`.claude/rules/rules-index.md` と相補。本格拡充は A2 で実施
> (Phase A〜C の進行に応じて追記)。

## 主要パス対応表 (骨格)

| パス | 責務 | 関連 SPEC | 関連 ADR | 関連 rules |
|---|---|---|---|---|
| `android/` | Android アプリエントリポイント (`MainActivity`) | TBD (A2/C3 で追記) | ADR 0002 | composable.md, navigation.md |
| `backend/` | Ktor 製 Backend (Cloud Run デプロイ対象、現状は最小) | TBD (C5 で追記) | ADR 0009 / 0011 | backend-auth.md, cloud-run-deploy.md |
| `buildSrc/` | Gradle 共通ビルドロジック (KMP plugin 定義) | — | ADR 0003 | gradle.md |
| `core/` | プラットフォーム横断のドメイン / データ / ネットワーク層 | TBD (C5 で追記) | ADR 0002 / 0003 | repository.md, network-client.md, error-handling.md |
| `core/network/auth/` (撤去対象) | Firebase Auth 連携 (旧) | — | ADR 0011 | no-firebase.md |
| `core/network/firestore/` (撤去対象) | Firestore 連携 (旧) | — | ADR 0011 | no-firebase.md |
| `data/` | アイドル情報 SQLite (`idols.db`、commit 対象) + (将来) `users.db` (commit 禁止) | TBD | ADR 0008 / 0010 / 0020 | sqlite-data-file.md, db-protection.md |
| `Dockerfile` | Backend Cloud Run コンテナ定義 | — | ADR 0009 / 0010 | cloud-run-deploy.md, db-protection.md |
| `js/app/` (撤去対象) | Kotlin/JS フロントエンド (旧) | — | ADR 0012 | removed-modules.md |
| `js/material/` (撤去対象) | Kotlin/JS Material UI ラッパー (旧) | — | ADR 0012 | removed-modules.md |
| `kotlin-js-store/` (撤去対象) | Kotlin/JS lock ファイル (旧) | — | ADR 0012 | removed-modules.md |
| `plugins/` | カスタム Gradle plugin | — | ADR 0003 | gradle.md |
| `public/` (撤去対象) | Firebase Hosting 静的配信ファイル (旧) | — | ADR 0011 / 0022 | removed-modules.md |
| `firebase.json` / `.firebaserc` (撤去対象) | Firebase 設定 (旧) | — | ADR 0011 | no-firebase.md |
| `web-build-and-deploy.yml` (撤去対象) | Firebase Hosting 用 CI (旧) | — | ADR 0011 / 0012 / 0022 | removed-modules.md |
| `feature/` (将来) | 画面別 feature モジュール (C3 で新設) | EPIC-001 | ADR 0002 / 0003 | viewmodel.md, composable.md |
| `core/design-system/` (将来) | デザイントークン参照層 (A10 / C3 で新設) | — | ADR 0023 | design-tokens.md |
| `docs/` | 全ドキュメント | — | ADR 0027 | docs-structure.md |
| `.claude/` | ハーネス本体 (Skill / rules / settings / MCP) | — | — | rules-index.md |

## A2 での本格拡充項目 (TODO)

- `core/` 配下の各サブモジュール詳細 (data / domain / network / model)
- `android/` 内の各 Composable と画面遷移
- `backend/` の Ktor ルーティング構造 (C5 後)
- `feature/` 配下のモジュール一覧 (C3 後)
- `data/` 配下の SQL スキーマ (`*.sq` ファイル)

## 関連

- `docs/harness/plan.md` §4 / §3.6 (撤去対象一覧)
- `docs/glossary.md` (ドメイン用語)
- `docs/traceability.md` (A6 で自動生成)
- `.claude/rules/rules-index.md`
