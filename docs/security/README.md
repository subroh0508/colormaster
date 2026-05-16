---
id: security-readme
title: セキュリティ ADR 索引
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.8
---

# セキュリティ ADR 索引

> **5 行以内 summary**: ColorMaster のセキュリティ・プライバシー関連 ADR の索引。
> 個別 ADR の本体は `docs/adr/`、ハンズオン手順は `docs/runbooks/`、機械検証ルールは
> `.claude/rules/{pii,secrets,db-protection,mcp-usage,r2-litestream}.md` を参照。

## 関連 ADR

| ADR | 内容 |
|---|---|
| ADR 0011 | 認証スタック転換 (Firebase 廃止 + GIS 統一) |
| ADR 0020 | PII 保護と権限ロール (uid のみ DB 保存、owner 1 名運用) |
| ADR 0021 | Secrets 管理ポリシー (R2 token TTL 90 日、Secret Manager / GitHub Secrets / .env の使い分け) |
| ADR 0024 | MCP サーバ採用 (JetBrains + Context7 + Cloudflare、token 取扱) |
| ADR 0025 | Skill 作成は `example-skills:skill-creator` 経由 (SKILL.md 仕様統一) |
| ADR 0027 | docs 構造 + 命名規約 + 日本語化 (情報漏洩や読み取りミスを構造で防ぐ) |

## 関連 rules (機械検証)

- `.claude/rules/pii.md`
- `.claude/rules/secrets.md`
- `.claude/rules/db-protection.md`
- `.claude/rules/mcp-usage.md`
- `.claude/rules/r2-litestream.md` (C5 で本格化)

## 関連 runbook

- `docs/runbooks/secrets-rotation.md`
- `docs/runbooks/r2-litestream.md`
- `docs/runbooks/user-deletion.md` (将来作成)

## 関連

- `docs/harness/plan.md` §3.8 (PII 保護とアクセス制御)
