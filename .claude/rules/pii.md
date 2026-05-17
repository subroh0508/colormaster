---
id: rules-pii
title: PII 保護とアクセス制御
status: stable
last_updated: 2026-05-17
# 注意: PII 保護は全 PR で必ず遵守すべき安全網のため `paths` を意図的に設定せず、
# Claude Code セッション起動時に常時ロードする (公式 docs: paths 未指定 = unconditional load)。
related_adrs:
  - ADR-0020
  - ADR-0011
---

# pii.md — PII 保護とアクセス制御

> 個人情報 (PII) の定義・最小化・redaction を強制する規約。
> 詳細は ADR 0020 を Single Source of Truth とする。
> **本 rule は安全網として常時ロード** (frontmatter `paths` を意図的に未設定、§5.1 参照)。

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

- **trufflehog** による全 PR 差分の secret-scan (`.github/workflows/secret-scan.yml`)
- Konsist で以下を検証 (Kotlin source 限定、R-21):
  - テスト fixture (`**/*Test.kt`, `**/*Spec.kt`) のメールアドレス文字列が `@example.com` ドメインで終わる
  - `data class User` 等の `users.db` スキーマ対応クラスに `email` / `displayName` / `picture` フィールドが存在しない (uid のみ)
- Gradle カスタムタスクで Markdown / Dockerfile を検証 (Konsist 不可):
  - `data/users.db*` の追跡禁止 (`git ls-files` チェック)
  - Dockerfile 内 `COPY data/users.db` パターン禁止
  - learning ファイル / PR description に redaction 漏れ検出 (`@(?!example\.com)` 等のパターン)

## Skill 出力前のチェックリスト

`code-reviewer` / `pr-retrospective` / `harness-meta` / `harness-evolution` が出力する前に以下を確認:

- [ ] CI ログ抜粋 / `gh pr view` 出力 / MCP 結果に PII が含まれていないか
- [ ] 「Reviewed by X」「Authored by Y」等の自然文に display name が混入していないか
- [ ] スタックトレース / エラーメッセージに `sub` claim 値が含まれていないか
- [ ] スクリーンショット (Roborazzi baseline) に表示名 / メール / アバター URL が含まれていないか

検出時は **`[REDACTED-*]` プレースホルダに置換** してから出力。

## 権限ロール

- 当面 **owner 1 名のみ** (ADR 0020)
- 複数人体制になったら別 ADR で `developer` / `releaser` を追加
- GitHub repo 設定 (Settings → Collaborators) と Cloudflare / GCP の IAM ロールも owner のみで運用、複数化時に別 runbook (`docs/runbooks/permissions.md`) を整備

## 関連

- ADR 0020 (PII 保護と権限ロール)
- `docs/harness/plan.md` §3.8 / R-20 / R-21 / R-26
- `.claude/rules/{secrets,db-protection}.md`
