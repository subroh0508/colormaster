---
id: ADR-0021
title: Secrets を場所別管理と定期ローテーションで運用する
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

# ADR-0021: Secrets を場所別管理と定期ローテーションで運用する

> **5 行以内 summary**: API キー / token / 認証情報は用途別に保管場所を分離し、
> ローカル開発は `.env`、CI/CD は GitHub Secrets、本番 Backend は Google Cloud
> Secret Manager、Cloudflare 用は dashboard + GitHub Secrets、MCP OAuth は
> Claude Code 安全領域に保管する。R2 token は TTL 90 日で定期ローテーション、
> 全 PR 差分を trufflehog でスキャンし、Skill 出力は redaction 必須とする。

## ステータス

accepted

## コンテキスト

ColorMaster は GIS client secret / R2 access key / Cloudflare API token /
Cloud Run service account credentials / MCP OAuth token と、性質の異なる
Secrets を複数扱う。これらを単一の保管場所 (例: 全部 GitHub Secrets) に
集約すると、CI からの参照可否や本番 runtime からのアクセス経路、token
ローテーション単位の粒度が揃わず、漏洩時の影響範囲が肥大化する。

加えて、AI 駆動ハーネス (`pr-retrospective` / `code-reviewer` / `harness-meta`)
が CI ログ・MCP 結果・PR diff を解析する経路は、Secrets が文字列として
混入する余地がある。漏洩検出を CI に組み込まないと、incident 検知が
人間レビューに依存して時間的に遅延する。

R2 token (Litestream の write 用) は ColorMaster のユーザーデータ
バックアップ経路の核心であり (ADR-0008、ADR-0022)、流出すると users.db の
直接読み書きが可能になる。長期 token を発行し続けると侵害時の被害が
拡大するため、定期ローテーションが必須となる。

## 決定

### 保管場所の使い分け

| 種類 | 保管場所 | 取り扱い |
|---|---|---|
| ローカル開発用 | `.env` (`.gitignore` 対象) | 個人マシン外に絶対出さない、共有時は別チャネル + 人手で渡す |
| CI/CD 用 | **GitHub Secrets** | repo 設定で管理、Pull Request からは参照不可、Actions の `secrets.*` 経由のみ |
| 本番 Backend 用 | **Google Cloud Secret Manager** | Cloud Run service account 経由でアクセス、R2 access key / GIS client secret 等 |
| Cloudflare 用 (R2 token / Pages deploy key) | Cloudflare dashboard + GitHub Secrets | dashboard が原本、CI 連携は GitHub Secrets 経由、TTL 90 日でローテーション |
| Claude Code 内 (MCP OAuth) | ローカル Claude Code 安全領域 | リポジトリ commit 禁止、`.gitignore` で `.claude/oauth-tokens*` を除外 |

各保管場所の選択理由:

- **`.env` (ローカル)**: 個人マシン上の secret 管理ツールに統一すると依存が
  増えるため、`.gitignore` 対象の plain file で十分。例として
  `<GIS_CLIENT_SECRET>` 形式の placeholder を `.env.example` に列挙する。
- **GitHub Secrets (CI/CD)**: Actions の標準機構で、PR からは参照不可という
  保護モデルが組込み済み。fork からの secret 露出を構造的に防ぐ。
- **Cloud Run Secret Manager (本番)**: service account 経由のアクセス制御が
  IAM で完結し、Container イメージに焼込まずに runtime 注入できる。
- **Cloudflare dashboard + GitHub Secrets**: Cloudflare の API token 発行は
  dashboard でのみ可能で、CI 利用時は GitHub Secrets にコピーする 2 系統管理。
- **Claude Code 内 (MCP OAuth)**: MCP サーバ (JetBrains / Context7 /
  Cloudflare) は OAuth token をローカル安全領域に保管し、リポジトリには
  含めない。

### 絶対 commit してはいけないもの

以下のパスは `.gitignore` で除外し、Konsist / trufflehog で機械検証する:

- `.env*` (例: `.env`, `.env.local`, `.env.*.local`)
- `*-credentials.json`
- `users.db*` (例: `data/users.db`, `data/users.db-shm`, `data/users.db-wal`)
- `service-account*.json`
- `.claude/oauth-tokens*`
- `*.pem` / `*.key` / `*.p12`
- `.cloudflare/credentials` / `.gcloud/credentials`

詳細は `.gitignore` 最終形を Single Source of Truth とする。

### ローテーション

- **R2 token は TTL 90 日** で定期ローテーション。期限切れ前に新 token を
  発行し、GitHub Secrets / Cloud Run Secret Manager を更新後に旧 token を
  失効させる (ゼロダウンタイム手順)。
- **GIS client secret** は **Public Client Flow を採用するため使用しない** (ID Token
  検証のみで認証が成立、`.claude/rules/secrets.md` の「GIS Client Secret | ローテ不要
  (Public Client Flow)」と整合)。仮に backend で Client Secret を使う経路 (Refresh Token
  取得や Confidential Client Flow への切替等) を追加する場合は、Google Cloud Console での
  発行・失効に従い、TTL なし運用は Google 側のローテーション機構に委譲する。漏洩疑い時のみ
  Google Cloud Console で即時再発行 (PR #119 レトロ Try「R2 token (TTL 90 日) との
  ローテ非対称性の根拠 1 行欠落」を補強)。
- **GitHub Actions OIDC token** は Actions 実行ごとに自動発行・失効。
  手動ローテーション対象外。
- **漏洩時 / 退職時 / 漏洩疑い時** は即時ローテーションし、history に
  含まれる場合は `git filter-repo` で履歴除去する。
- ローテーション手順と履歴は `docs/runbooks/secrets-rotation.md` に整備
  する (Phase A〜C で本格化、A1 時点では雛形)。

### 漏洩検出

- **trufflehog** を A6 で CI 導入し、全 PR 差分をスキャンする。検出時は
  PR を block + immediate rotate + history rewrite (`git filter-repo`)。
- pre-commit hook でローカル段階のスキャンも追加検討 (A6 評価対象)。

### Skill 出力の redaction 必須

- `pr-retrospective` / `code-reviewer` / `harness-meta` が CI ログ / MCP
  結果 / PR diff を learning / レビューコメントに含める場合、Secrets 値を
  パターンマッチで検出し `[REDACTED-SECRET]` 等のプレースホルダに置換する。
- `.env.example` には **キー名のみ + placeholder 値** (例:
  `<GIS_CLIENT_SECRET>`) を記載し、実値は絶対に書かない。

## 根拠

- **場所別分離の必然性**: CI / 本番 runtime / ローカル開発 / Claude Code
  内部はアクセス経路と権限境界が異なる。単一保管庫に集約すると、最弱の
  経路 (例: PR fork からの参照) が全 Secrets の脆弱点になる。場所別に
  分離すれば被害も場所別に限定される。
- **R2 token TTL 90 日の根拠**: NIST SP 800-63B 等の業界推奨ローテーション
  間隔と、Cloudflare R2 token の発行・失効コストのバランス。短すぎると
  運用負荷が肥大化、長すぎると侵害時の被害期間が拡大する。
- **trufflehog の採用**: OSS で広く採用され、汎用 secret パターン
  (AWS / Google / GitHub / Slack 等) を網羅。CI 統合が軽量。
- **Skill 出力 redaction の機械化**: 人間レビューに依存すると Skill の
  自律性を損なう。Skill 内で redaction を必須化することで人手介入を最小化。

### 比較した代替案

| 代替案 | 利点 | 欠点 | 採用しなかった理由 |
|---|---|---|---|
| 全 Secrets を GitHub Secrets に集約 | 管理場所が 1 つで運用シンプル | 本番 runtime から GitHub Secrets を直接参照する経路がなく、結局別の場所が必要 | 本番 runtime は Cloud Run Secret Manager 必須、結果として場所別分離 |
| HashiCorp Vault / Doppler 等の SaaS 集約 | 統一インターフェース、強力な audit log | 個人プロジェクトでは over-engineered、月額コストが顕著 | GitHub Secrets + Cloud Run Secret Manager の組合せで十分 |
| Firebase の secret 管理を継続利用 | 既存導入済 | Firebase 廃止方針 (ADR-0011) と矛盾 | ADR-0011 で Firebase 完全廃止を決定済み |
| 長期 token (TTL 無期限) で運用 | ローテーション運用負荷ゼロ | 漏洩時の被害期間が無制限、incident response の難易度が跳ね上がる | TTL 90 日でローテーション、運用負荷を許容 |
| trufflehog 非導入、人間レビューに依存 | CI 導入コストゼロ | レビュー漏れで credentials がマージされるリスク | CI 検出を必須化、人間レビューはバックアップ |

## 帰結

### Positive

- 保管場所が経路ごとに分離され、単一漏洩の被害範囲が構造的に限定される。
- R2 token の TTL 90 日ローテーションで、侵害時の被害期間が上限される。
- trufflehog による全 PR スキャンで credentials の mistaken commit が
  CI 段階で検出され、history 汚染を未然に防ぐ。
- Skill 出力の redaction 必須化で、learning / レビューコメントへの
  Secrets 混入が構造的に塞がれる。

### Negative / トレードオフ

- 保管場所が複数になり、開発者は「どこに置くか」の判断が必要
  → `.claude/rules/secrets.md` に lookup table を集約し判断を機械化。
- R2 token のローテーション運用負荷が 90 日ごとに発生
  → `docs/runbooks/secrets-rotation.md` で手順を定型化、A6 で
  ローテーション通知の自動化も検討。
- trufflehog の false positive で PR が block される可能性
  → allowlist (例: `.env.example` の placeholder) を整備、レビュー対応。

### Neutral / 将来の検討事項

- `docs/runbooks/secrets-rotation.md` の本格化は Phase A〜C で段階対応。
  A1 時点では雛形のみ。
- pre-commit hook での trufflehog 実行は A6 で評価。CI 検出が主、ローカル
  検出は補助。
- 複数人体制移行時 (ADR-0020 の owner 単一を解除する別 ADR と連動)、
  Secrets 共有ポリシー (誰が何にアクセスできるか) を別途検討する。
- HashiCorp Vault 等の集約 SaaS への移行は、運用規模が個人プロジェクトを
  超えた時点で再評価する。

## ADR 起票基準 (§4.5) の充足

本 ADR が満たす起票基準 (2 項目以上で起票成立):

- [x] 6. セキュリティ・プライバシー・ライセンスに関する方針 (Secrets 管理が中核)
- [x] 8. 複数の代替案を比較した結果としての判断 (集約 / SaaS / Firebase / TTL / trufflehog の比較)
- [x] 9. 元に戻すコストが高い決定 (保管場所構成・ローテーション方式は一度動かすと撤回困難)
- [x] 10. 長期的な制約 (今後 1 年以上、全 Secrets 取扱と CI / runtime 構成に影響)

## ADR 化すべき例 / すべきでない例 (テンプレ末尾の自己チェック)

- [x] `.claude/rules/adr.md` の「ADR にすべき例」「ADR にすべきでない例」リストと照合し、
      本 ADR が単なる runbook (`docs/runbooks/secrets-rotation.md` で済む話) や
      コーディング規約に留まらず、Secrets 管理のアーキテクチャ全体方針であることを確認した。

## 関連

- 関連 Plan: PLAN-001 (本 ADR の起票 PR)
- 関連 Epic: EPIC-000 (ハーネス基盤構築)
- ADR-0001 (ADR 運用基準)
- ADR-0008 (ユーザーデータ Backend SQLite + Litestream + R2)
- ADR-0020 (PII 保護、本 ADR と同時防御)
- ADR-0022 (Cloudflare Pages + R2、R2 token 運用)
- ADR-0024 (MCP サーバ採用、MCP OAuth token の取扱)
- `.claude/rules/secrets.md` (Secrets 管理の Single Source of Truth)
- `.claude/rules/pii.md` / `.claude/rules/db-protection.md` / `.claude/rules/mcp-usage.md`
- `docs/harness/plan.md` §3.7 / §3.8
- `docs/runbooks/secrets-rotation.md` (Phase A〜C で本格化)
