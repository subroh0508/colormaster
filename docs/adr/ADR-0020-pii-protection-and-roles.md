---
id: ADR-0020
title: PII 保護を最小化原則と単一 owner ロールで構造化する
status: accepted
date: 2026-05-17
related_epics:
  - EPIC-000
related_plans:
  - PLAN-001
related_specs: []
superseded_by: null
supersedes: null
---

# ADR-0020: PII 保護を最小化原則と単一 owner ロールで構造化する

> **5 行以内 summary**: ColorMaster は Google アカウント由来の個人情報 (PII) を扱うため、
> DB スキーマには `uid` (sub claim) のみを保存し、display name / email / picture は
> GIS userinfo endpoint から都度取得 + memory cache TTL 15 分で揮発させる。13 種の
> 漏洩経路を `.gitignore` / Konsist / detekt / trufflehog / redaction で多層に塞ぎ、
> 権限ロールは当面 owner 単一、複数人体制への拡張は別 ADR で再評価する。

## ステータス

accepted

## コンテキスト

ColorMaster は Google Identity Services (GIS、ADR-0011) で認証し、ユーザーごとの
担当アイドル・推し情報を Backend SQLite (`users.db`、ADR-0008) に永続化する。
GIS は userinfo endpoint からメールアドレス・display name・プロフィール画像 URL を
返却するため、これらを無造作に保存すると `users.db` の漏洩が即座に個人特定可能な
PII 流出に発展する。

加えて、AI 駆動ハーネス (Spec Gen → Implementation → Evaluation → Merge →
Retrospection → Meta) では `pr-retrospective` / `code-reviewer` / `harness-meta` が
CI ログ・PR diff・GitHub コメントを横断して解析し、生成物として learning や
PR コメントを出力する。これらの経路は CI ログ中の Stack Trace や fixture 由来の
ダミーが混入する余地があり、構造的に redaction を要する。

漏洩経路は単一の防御では塞げず、リポジトリ commit・Container 焼込み・R2 バケット
直読・token 流出・API 越境取得・ログ出力・エラー応答漏洩・GIS 過剰保存・コンソール
直接アクセス・Skill 出力混入と多岐にわたる。本 ADR でこれらを 13 経路に正規化し、
それぞれに防御層を割り当てる。

## 決定

### PII 最小化原則

- `users.db` に保存する PII は **`uid` (Google sub claim) のみ**。
- display name / email / picture は **GIS userinfo endpoint から都度取得**、
  Backend memory cache TTL **15 分** に揮発保持。永続化しない。
- `users.db` が万一漏洩しても、`uid` 単体では Google アカウントの個人特定は
  困難な状態を構造的に維持する。

### PII の定義

| 項目 | PII 扱い | DB 保存 |
|---|---|---|
| メールアドレス | ✅ | ❌ (GIS userinfo から都度取得 + memory cache TTL 15 分) |
| Google Account ID (sub claim = `uid`) | 内部識別子 (PII 同等取扱) | ✅ (`users.db` の `uid` カラムのみ) |
| Display Name | ✅ | ❌ |
| プロフィール画像 URL | ✅ | ❌ |
| IP アドレス | ✅ | ❌ |

表記は `.claude/rules/pii.md` §PII の定義 と完全整合 (PII 扱いは `✅` または `内部識別子 (PII 同等取扱)` の 1 系統に統一、PR #119 レトロ Try で指摘された二系統表記の解消)。

ダミーデータは `@example.com` ドメイン (RFC 2606 予約) と `test-uid-001` 等の連番
文字列に限定する。実 PII の fixture commit はインシデント扱いとし、history 除去 +
R2 token 即時ローテーションで対応する。

### 13 漏洩経路と多層防御

| # | 漏洩経路 | 防御 |
|---|---|---|
| 1 | リポジトリへの直接 commit | `.gitignore` で `data/users.db*` を除外 + Konsist で追跡禁止検証 |
| 2 | Container イメージへの焼込み | Dockerfile で `COPY data/users.db` 禁止 + Konsist で文字列検証 |
| 3 | R2 バケットからの直接読出し | バケットを private、Backend Container の R2 token のみ allow |
| 4 | R2 token の流出 | Secrets 管理 + TTL 90 日で定期ローテーション (ADR-0021) |
| 5 | リポジトリ内の credentials コミット | trufflehog による CI スキャン + `.gitignore` |
| 6 | PR diff からの credentials 漏洩 | trufflehog secret-scan workflow を全 PR に発火 |
| 7 | Backend API 経由で他人のデータ取得 | ID Token 検証 + `uid` フィルタ、Konsist で `/api/me/*` ハンドラの `requireUid()` 呼出を強制 |
| 8 | ログ / モニタリングへの PII 出力 | `.claude/rules/logging.md` + `.claude/rules/pii.md` で禁止、detekt カスタムルールで `Logger` 系への PII フィールド渡しを検出 |
| 9 | エラー応答に PII を含める | エラー schema に PII フィールド禁止 + Konsist 検証 |
| 10 | GIS から取得した userinfo の過剰保存 | DB スキーマは `uid` のみ、display name / email / picture は memory cache TTL 15 分 |
| 11 | GCP / Cloudflare コンソールへの不正アクセス | owner 単一ロール、Secrets ローテーション運用 (ADR-0021) |
| 12 | `pr-retrospective` / KPT learning への PII 混入 | Skill 出力前の redaction 強制、テスト fixture の非 `@example.com` を Konsist で検出 |
| 13 | `code-reviewer` が CI ログから PII を漏らす | aspect ごとに PII redaction の前処理を必須化 (ADR-0019) |

### 権限ロール

- 当面 **owner 1 名のみ** で運用する (個人プロジェクト想定)。
- owner の権限: 全権限、Secrets ローテーション、GCP / Cloudflare コンソール
  operator、master ブランチへのマージ権限。
- 複数人体制になったら `developer` / `releaser` ロールを **別 ADR で改訂・追加**
  する。本 ADR は単一 owner 前提でのみ有効。

### Skill ループにおける redaction 必須

- `pr-retrospective` / `code-reviewer` / `harness-meta` は、CI ログ・diff・PR
  コメントから PII を間接的に拾う可能性があるため、出力前に必ず redaction
  フェーズを通す。
- redaction 対象とプレースホルダ: メールアドレス (除く `@example.com`) →
  `[REDACTED-EMAIL]`、`https://lh*.googleusercontent.com/...` →
  `[REDACTED-AVATAR-URL]`、sub claim 値 → `[REDACTED-UID]`、IP アドレス →
  `[REDACTED-IP]`。
- テスト fixture は `@example.com` ドメインのみ使用。Konsist で機械検証する。

## 根拠

- **最小化原則の構造的優位**: 「保存しなければ漏れない」が最強の防御。GIS が
  userinfo を都度返却する仕様を活用し、`users.db` を `uid` だけのテーブルに
  圧縮することで漏洩時の被害を構造的に限定する。
- **多層防御の必然性**: 単一防御 (例: `.gitignore` のみ) では Container 焼込み /
  ログ出力 / Skill 出力 を塞げない。経路ごとに防御層を割り当てて初めて全経路を
  カバーできる。
- **owner 単一の暫定化**: ロール設計は実装コストが高く、撤回も難しい。複数人
  体制の必要性が出てから別 ADR で増やすことで、初期の YAGNI を維持する。
- **Skill 出力 redaction の機械化**: 人間レビューに依存すると見落としが
  避けられない。プレースホルダ置換のパターンマッチを Skill 内で自動化する。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| Firebase Auth + Firestore に依存し続ける | PII 管理を Google に委譲、実装コスト低 | Firebase 廃止方針 (ADR-0011) と矛盾、Firestore のスキャン料金が個人プロジェクトでも顕著 | ADR-0011 で Firebase 完全廃止を決定済み、PII 最小化と独立して撤去 |
| display name / email も DB 保存 (キャッシュ用) | API 呼出減で性能良 | 漏洩時の被害が個人特定可能まで拡大、最小化原則と矛盾 | 性能要件は memory cache TTL 15 分で十分、永続化しない |
| 単一防御 (`.gitignore` のみ) | 実装最小 | Container 焼込み / ログ / Skill 出力を塞げない | 多層防御を採用、Konsist + detekt + trufflehog + redaction を組合せ |
| owner / developer / releaser を初期から定義 | 将来拡張に備える | 個人プロジェクトでは over-engineered、ロール RBAC 実装コスト | owner 単一で開始、複数人体制時に別 ADR で追加 |

## 帰結

### Positive

- `users.db` 漏洩時も `uid` のみで個人特定が困難、被害が構造的に限定される。
- 13 漏洩経路の対応が明文化され、CI / Konsist / detekt / trufflehog で機械検証
  可能。レビュー負荷が低減する。
- Skill 出力の redaction が必須化され、learning / レビューコメントへの PII 混入
  が構造的に塞がれる。

### Negative / トレードオフ

- userinfo 取得が都度 API call となり、Backend のレイテンシが微増する
  → memory cache TTL 15 分で大半をヒットさせ実害を抑える。
- redaction パターンマッチは正規表現ベースで完全網羅は困難
  → A6 で `trufflehog` 同等の汎用パターンを追加導入し、Skill 単位の網羅率を
  継続的に改善する。
- owner 単一は bus factor 1、owner が稼働不能になると運用停止
  → 個人プロジェクト前提で許容、複数人体制移行時に別 ADR で再評価。

### Neutral / 将来の検討事項

- 機械検証 (Konsist で `users.db*` 追跡禁止 / Dockerfile 内 `COPY data/users.db`
  禁止 / `requireUid()` 呼出強制 / fixture の `@example.com` 限定 / エラー
  schema の PII フィールド禁止) は A6 で本格化する。
- detekt カスタムルール (Logger 系への PII フィールド渡し検出) も A6 で導入。
- 複数人体制移行時の `developer` / `releaser` 追加は別 ADR で対応。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 6. セキュリティ・プライバシー・ライセンスに関する方針 (PII 最小化と redaction が中核)
- [x] 8. 複数の代替案を比較した結果としての判断 (Firebase 継続 / 保存方式 / 防御層 / ロール設計の比較)
- [x] 9. 元に戻すコストが高い決定 (DB スキーマ・ロール設計・防御層構成は一度動かすと撤回困難)
- [x] 10. 長期的な制約 (今後 1 年以上、全 PII 取扱コードと Skill 出力に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR がコーディング規約 (`.claude/rules/pii.md` で済む話) や Plan で済む話 ではなく、
      PII 保護のアーキテクチャ全体方針であることを確認した。

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤構築)
- ADR-0001 (ADR 運用基準)
- ADR-0008 (ユーザーデータ Backend SQLite + Litestream + R2)
- ADR-0011 (認証スタック転換、Firebase 廃止 + GIS 統一)
- ADR-0019 (`code-reviewer` 8 aspect + Coordinator)
- ADR-0021 (Secrets 管理ポリシー、本 ADR と同時防御)
- `.claude/rules/pii.md` (PII 最小化と redaction の Single Source of Truth)
- `.claude/rules/secrets.md` / `.claude/rules/db-protection.md` / `.claude/rules/logging.md`
- `docs/harness/plan.md` §3.7 / §3.8
