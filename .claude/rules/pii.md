---
id: rules-pii
title: PII 保護とアクセス制御
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.8 / ADR 0020
related_adrs: [ADR-0020]
---

# pii.md — PII 保護とアクセス制御

> 個人情報 (PII) の定義・最小化・redaction を強制する規約。
> 詳細は ADR 0020 を Single Source of Truth とする。

## PII の定義

| 項目 | PII 扱い | DB 保存 |
|---|---|---|
| メールアドレス | ✅ | ❌ (GIS userinfo から都度取得 + memory cache TTL 15 分) |
| Google Account ID (sub claim = uid) | 内部識別子 (PII 同等取扱) | ✅ (`users.db` の `uid` カラムのみ) |
| Display Name | ✅ | ❌ |
| プロフィール画像 URL | ✅ | ❌ |
| IP アドレス | ✅ | ❌ |

## 最小化原則

- DB に保存する PII は **`uid` のみ**
- それ以外 (display name / email / picture) は GIS userinfo endpoint から **都度取得 + memory cache TTL 15 分**
- ログ・テスト fixture・PR 本文・learning ファイル・review コメントには PII を絶対に転載しない

## redaction 強制 (Skill 出力前)

`code-reviewer` / `pr-retrospective` / `harness-meta` などの Skill が出力する際は、
以下のパターンを検出してマスクする (`[REDACTED-PII]` 等のプレースホルダに置換):

| パターン | 置換例 |
|---|---|
| メールアドレス (除く `@example.com`) | `[REDACTED-EMAIL]` |
| `https://lh*.googleusercontent.com/...` | `[REDACTED-AVATAR-URL]` |
| `sub` claim の値 (典型的に数字 21 桁) | `[REDACTED-UID]` |
| IP アドレス | `[REDACTED-IP]` |

## テスト fixture の規約

- ダミーメールは **`@example.com` ドメイン限定** (RFC 2606 予約済)
- ダミー uid は連番文字列 (`test-uid-001` 等)
- 実 PII を fixture に commit したらコード化された incident として扱い、即時 history から除去 + R2 token ローテーション

## 機械検証 (A6 で導入)

- **trufflehog** による全 PR 差分の secret-scan
- Konsist で「テスト fixture の `@example.com` ドメイン以外の検出」(R-21)
- Gradle カスタムタスクで「`data/users.db*` の追跡禁止」「Dockerfile 内 `COPY data/users.db` 禁止」

## 権限ロール

- 当面 **owner 1 名のみ** (ADR 0020)
- 複数人体制になったら別 ADR で `developer` / `releaser` を追加

## 関連

- ADR 0020 (PII 保護と権限ロール)
- `docs/harness/plan.md` §3.8 / R-20 / R-21 / R-26
- `.claude/rules/{secrets,db-protection}.md`
