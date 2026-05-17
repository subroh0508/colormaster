---
id: rules-code-reviewer-aspects
title: code-reviewer 8 aspect の binary eval checklist と Coordinator 形式
status: stable
last_updated: 2026-05-17
paths:
  - ".claude/skills/code-reviewer/**"
related_adrs:
  - ADR-0017
  - ADR-0019
related_plan: docs/harness/plan.md §5.3 / §5.4.3 / R-13 / R-37
---

# code-reviewer-aspects.md — 8 aspect 規約

> `code-reviewer` Skill の 8 aspect (`spec-conformance` / `test-quality` / `architecture` /
> `security` / `performance` / `code-quality` / `visual-regression` / `design-tokens`) の
> binary yes/no eval checklist と Coordinator のレビューコメント形式を規定。
> 各 aspect は **独立した system prompt** で動作 (R-13、Generator バイアス回避)。

## 8 aspect 概要

| aspect | 状態 | system prompt の独立観点 |
|---|---|---|
| `spec-conformance` | active (A3 前は手動代替) | `@Spec` annotation の存在、SPEC-ID 整合性、frontmatter `related_specs` 有効性、設計書 ⇄ 実装の対応 |
| `test-quality` | active (A3 前は手動代替) | 差分カバレッジ 100%、Spec coverage 差分 100%、mutation score 妥当性、tautological テスト検出 |
| `architecture` | active (A3 前は手動代替) | レイヤー依存方向 (feature → core → repository)、モジュール越境、命名規約、循環依存検出 |
| `security` | active (A3 前は手動代替) | PII redaction、secrets 取り扱い、認証境界 (`requireUid()` 強制)、Konsist 規約準拠 |
| `performance` | active (A3 前は手動代替) | N+1 クエリ、無駄な再描画 (`derivedStateOf`, `remember` 漏れ)、coroutine スコープ、不要な hot reload |
| `code-quality` | active (A3 前は手動代替) | error-handling (Result 型ラップ徹底)、命名規約、可読性、defensive 過剰禁止 |
| `visual-regression` | **A10 完了後 enable** | Roborazzi baseline diff (4 パターン: mobile/desktop × Light/Dark)、許容 threshold 内 |
| `design-tokens` | **A10 完了後 enable** | DESIGN.md に存在しない hex / sp / dp の混入なし、Primitive/Semantic/Component 3 階層整合 |

## 各 aspect の binary yes/no eval checklist (各 5 項目以上、R-13)

PR #121 レトロ Try「binary checklist 4 aspect × 7-8 項目 = 29 項目を A3 本格化時に確定」を反映。
本 rule で各 aspect の **最低 5 項目、推奨 7-8 項目** を確定 (PR #135 レトロ Try で `≥` Unicode を `5 項目以上` に置換、copy 時の escape リスク回避)。

### spec-conformance (7 項目)

- [ ] 新規追加・変更 Kotlin 行に対応する `@Spec("SPEC-NNN-N")` annotation がテスト側に存在 (A7 完了前は warning)
- [ ] `@Spec` で参照される SPEC-ID が `docs/specifications/{basic,detail}/` に実在
- [ ] PR description frontmatter `related_specs` の SPEC-ID が実在
- [ ] 基本設計 ⇄ 詳細設計の `related_basic` / `related_detail` ペアが整合
- [ ] 設計書本文にコード断片が混入していない (フェンス付き `kotlin/sh/sql` 等、`docs-structure.md` §4.6)
- [ ] frontmatter 配列が block 形式 (flow 形式 `[A, B]` が混入していない、`docs-structure.md` frontmatter 規約)
- [ ] spec-living-sync 発動時は PR description「仕様変更箇所」セクションに記載 (`spec-living-sync.md`)

### test-quality (7 項目)

- [ ] 差分 Line / Branch coverage が 100% (Kover 出力、A7 完了前は N/A 明記で skip 妥当)
- [ ] 差分 Spec coverage が 100% (Konsist `@Spec` カバー率、A7 完了前は N/A)
- [ ] mutation score が前回値以上 (PITest、A7 完了前は N/A)
- [ ] tautological / trivial テストが含まれていない (`assertEquals(x, x)` 等)
- [ ] テスト fixture のメールアドレスが `@example.com` ドメイン (`.claude/rules/pii.md`)
- [ ] Kotest DescribeSpec の `describe` / `it` 階層が `.claude/rules/kotlin-test.md` 規約に準拠
- [ ] `runTest` / `Dispatchers.setMain` が coroutine テストで適切に使用されている

### architecture (7 項目)

- [ ] レイヤー依存方向が `feature → core → repository → network` に従う (逆方向依存ゼロ)
- [ ] モジュール越境 (`feature/A` から `feature/B` の internal 参照等) がない
- [ ] 命名規約 (`.claude/rules/naming.md`) 準拠 (Composable は `*Screen` / `*Section`、ViewModel は `*ViewModel`、UiState は `*UiState`)
- [ ] 循環依存ゼロ (Konsist 検出、A6 で機械化)
- [ ] SoT (`docs/harness/plan.md` / ADR / `.claude/rules/*`) との矛盾なし (A1 レトロ Problem #1 / PR #121 plan.md L1125 と同様の latent contradiction 検出)
- [ ] 新規 ADR 起票基準 (`adr.md` §起票基準) を 2 項目以上満たすアーキ変更時は ADR が起票されている
- [ ] `rules-index.md` の status 表記が rule 実体と一致 (PR #117 「既存」誤宣言 13+ 件と同様の drift 検出)

### security (7 項目)

- [ ] PII (メール / display name / GIS avatar URL / IP / sub claim) が code / docs / PR description / commit message に混入していない (`.claude/rules/pii.md` redaction)
- [ ] secrets (API key / token / password / AWS access key / GitHub PAT / JWT) が混入していない (`.claude/rules/secrets.md` redaction)
- [ ] `users.db` / `service-account*.json` / `.env*` / `.claude/oauth-tokens*` が `.gitignore` 対象であり、commit に含まれていない (`.claude/rules/db-protection.md`)
- [ ] 認証境界 (`requireUid()`) が backend API endpoint で強制されている (`.claude/rules/backend-auth.md`)
- [ ] Konsist の Firebase 検出が 0 件 (`.claude/rules/{no-firebase,firebase-boundary}.md`)
- [ ] Dockerfile に `COPY data/users.db` パターンがない (`.claude/rules/db-protection.md`)
- [ ] `.dockerignore` が 5 必須項目 (`data/users.db*` / `.env*` / `*-credentials.json` / `service-account*.json` / `.claude/oauth-tokens*`) を含む (A6 で機械化、Dockerfile 配置時)

### performance (5 項目)

- [ ] N+1 クエリパターンがない (SQL / SPARQL、`.claude/rules/{sql-delight,sparql}.md`)
- [ ] Compose の無駄な再描画がない (`remember` 漏れ / `derivedStateOf` 未使用 / `mutableStateOf` の hoisting)
- [ ] coroutine スコープが適切 (`viewModelScope` / `lifecycleScope`、leaking なし)
- [ ] 不要な hot reload (Compose Desktop) が発生していない (`@Stable` / `@Immutable` 適切付与)
- [ ] 大量データ処理時に `Flow` / `Channel` の backpressure が適切に処理されている

### code-quality (7 項目)

- [ ] `Result<T>` / `runCatching` の error-handling が徹底 (`.claude/rules/error-handling.md`)
- [ ] 命名規約 (`.claude/rules/naming.md`) 準拠
- [ ] 可読性 (1 関数 50 行以内推奨、cyclomatic complexity 10 以下)
- [ ] defensive 過剰禁止 (フレームワーク保証範囲内の null チェック / バリデーション禁止、`docs/harness/plan.md` §システム指示原則)
- [ ] コメントは WHY のみ (WHAT を説明するコメント禁止、CLAUDE.md システム指示準拠)
- [ ] Markdown フェンス言語指定必須 (MD040、A6 で markdownlint-cli2 機械化)
- [ ] 日本語見出し / template-language 規約準拠 (ADR 0027 / `.claude/rules/{template-language,markdown}.md`)

### visual-regression (5 項目、A10 完了後 enable)

- [ ] 4 パターン baseline (mobile/desktop × Light/Dark) が `docs/design/inventory/screenshots/` に存在
- [ ] Roborazzi diff が `changeThreshold` 内 (誤検出抑制、`.claude/rules/ui-snapshot.md`)
- [ ] 意図的更新時は PR description に「visual regression 意図的更新」明記
- [ ] wasmJs 固有 actual は対象外 (`.claude/rules/wasm-compat.md` R-24)
- [ ] アニメーション停止 + 代表 brand color 固定の Preview パラメータ使用

### design-tokens (5 項目、A10 完了後 enable)

- [ ] Kotlin / Compose code に hex (`#XXXXXX` / `0xFFXXXXXX`) のハードコードなし (Konsist 検出)
- [ ] `sp` / `dp` の固定値がデザイントークン経由
- [ ] `Color.Red` 等の Material 直接参照なし (テーマ非対応回避)
- [ ] DESIGN.md の Primitive 値以外を実コードから参照していない
- [ ] テストコード (`*Test.kt` / `*Spec.kt`) と Roborazzi baseline 用 Preview パラメータは例外

## Coordinator の役割

- 各 aspect の binary yes/no eval 結果を集約
- 重複指摘を排除し、重要度別 (Critical / Improvement) に整理
- 日本語の構造化レビューコメントを PR に post (下記フォーマット)
- Merge readiness を判定: **Critical = 0 で Ready** (`merge-readiness.md` 3 条件と整合)
- PR コメント post 前に `.claude/rules/pii.md` / `.claude/rules/secrets.md` redaction を必ず通す (R-26)

## aspect 動的選択ルール (PR #126 / #125 / #135 レトロ Try)

PR の touch ファイル種別と規模に応じて aspect セットを動的に選択する。`merge-readiness.md` §大規模 PR (30+ ファイル) の aspect スコープ自動削減 と整合:

| touch ファイル分類 | 既定 aspect セット | skip aspect | 根拠 |
|---|---|---|---|
| **Markdown only** (`.claude/rules/**` / `docs/**`) | spec-conformance / architecture / security / code-quality (4 aspect) | test-quality / performance / visual-regression / design-tokens | 実装コード変更ゼロ、UI 変更ゼロのため適用外 |
| **Kotlin code touch あり (feature/core 配下、非 UI)** | 上記 + test-quality / performance (6 aspect) | visual-regression / design-tokens | A10 完了前は UI aspect 未 enable |
| **`feature/**` touch あり (UI 変更含む)** | 上記 + visual-regression / design-tokens (8 aspect) | — | A10 完了後の本格運用、現状は skeleton |
| **`.claude/settings.json` / `.github/workflows/**` touch** | spec-conformance / architecture / security (3 aspect) | code-quality / test-quality / performance / visual-regression / design-tokens | 権限改修 / CI 改修は code-quality / test より architecture / security 重視 |
| **mirror PR (roadmap docs のみ)** | spec-conformance / architecture / security (3 aspect) | code-quality / test-quality / performance / visual-regression / design-tokens | code-quality は本体 PR で実施済 |
| **OpenAPI yaml only** (`docs/api/colormaster-api.yaml`) | spec-conformance / architecture / security / code-quality (4 aspect) | test-quality / performance / visual-regression / design-tokens | DTO 同期は別 Plan、code-quality は yaml schema validity を含む |
| **build script only** (`build.gradle.kts` / `gradle/libs.versions.toml`) | architecture / security / code-quality (3 aspect) | spec-conformance / test-quality / performance / visual-regression / design-tokens | spec docs 変更なし、code-quality は version compatibility を含む |

Coordinator は PR の `--name-only` diff を grep して上記分類を自動判定、skip 対象を集約コメントに明示する (透明性確保)。

### MD040 / CommonMark / GFM 仕様の明示 (PR #125 レトロ Try)

`code-quality` aspect agent prompt に CommonMark / GFM の各 markdownlint rule 仕様を明示し、誤検出を予防 (PR #125 で MD040 35+ 件の誤検出が発生):

| markdownlint rule | 対象範囲 | 注意点 |
|---|---|---|
| MD040 (フェンス言語指定必須) | **開始フェンスのみ** | 閉じフェンス (` ``` ` 単独) は対象外、誤検出注意 |
| MD001 (見出しレベル飛ばし禁止) | 全 H1-H6 | frontmatter 後の H1 から開始、H1 → H3 のジャンプは違反 |
| MD024 (同一見出し重複禁止) | 全 H1-H6 | `siblings_only: true` で兄弟見出しのみ判定、別 H2 配下の H3 重複は OK |
| MD034 (裸 URL 禁止) | 本文 | inline code (` `https://...` `) や `<https://...>` は対象外 |
| MD041 (ファイル冒頭は H1 必須) | ファイル冒頭 | frontmatter ありの場合は frontmatter 直後の H1 が対象 |

各 aspect agent (`Agent({subagent_type: "Explore", ...})`) の system prompt 冒頭で本表を引用し、agent が markdownlint rule の対象範囲を誤判定しないようガードする。

## Coordinator のレビューコメント形式

```markdown
## 🔍 AI コードレビュー

> 生成: code-reviewer Skill (vX.Y.Z) at YYYY-MM-DDTHH:MM:SSZ
> 並列実行した aspect: spec-conformance, test-quality, architecture, security, performance, code-quality
> Skip aspect: visual-regression / design-tokens (A10 完了後 enable)

### サマリ

| 観点 | 結果 | 重大な指摘 | 改善提案 |
|---|---|---|---|
| 仕様適合性 (spec-conformance) | ✅ 合格 | 0 | 0 |
| テスト品質 (test-quality) | ✅ 合格 | 0 | 2 |
| アーキテクチャ (architecture) | ❌ 不合格 | 1 | 3 |
| セキュリティ (security) | ✅ 合格 | 0 | 1 |
| パフォーマンス (performance) | — | — | — (skip) |
| コード品質 (code-quality) | ✅ 合格 | 0 | 2 |

### マージ可否: ❌ まだ不可

### 重大な指摘 (merge ブロック)

1. **[architecture]** `core/data/Repository.kt:42` — 説明
   - 修正案: ...

### 改善提案 (non-blocking)

- **[test-quality]** `feature/home/HomeViewModelSpec.kt:15` — 説明

### Eval チェックリスト (binary yes/no)

#### spec-conformance (7/7 ✅)
- [x] 新規追加・変更 Kotlin 行に対応する `@Spec` annotation がテスト側に存在
- [x] `@Spec` で参照される SPEC-ID が実在
- [x] PR description frontmatter `related_specs` が実在
- [x] 基本設計 ⇄ 詳細設計の `related_*` ペア整合
- [x] 設計書本文にコード断片混入なし
- [x] frontmatter 配列が block 形式
- [x] spec-living-sync 発動時の記載

#### architecture (6/7 ❌、1 件 Critical)
- [x] レイヤー依存方向に従う
- [ ] **モジュール越境ゼロ** — `feature/home` から `feature/search/internal` を import (Critical)
- [x] 命名規約準拠
- [x] 循環依存ゼロ
- [x] SoT 矛盾なし
- [x] ADR 起票基準充足
- [x] rules-index status 整合

(...他 aspect 略)

### 人間レビュアー向け文言

> AI レビューの指摘で十分でしょうか? `architecture` aspect の Critical 1 件を解消した上で、
> 追加で確認すべき観点があればコメントください。`Critical = 0` 充足後に Ready 昇格します。
```

## fix loop 上限 (`implementation-workflow.md` R-14 と整合)

- code-reviewer Critical 修正の fix loop 上限: **3 回**
- 超過時は Plan / Epic decisions.md に `status: blocked` を記録し人間に通知
- fix loop 中の中間 commit は squash merge で集約されるため粒度を気にしすぎない (`pr-draft-policy.md` 参照)

## 機械検証 (A6 + A10 で段階導入)

- **A6**: Konsist で `@Spec` annotation の SPEC-ID 実在検証、Kotlin source の Firebase 検出 0 件、命名規約準拠、レイヤー依存方向検証
- **A6**: Gradle カスタムタスクで Markdown frontmatter 必須キー / 5 行 summary / 日本語見出し検証
- **A10**: `visual-regression` / `design-tokens` aspect の Coordinator 並列起動対象追加、Roborazzi diff の `changeThreshold` 確定、`.claude/rules/design-tokens.md` 検出パターン Konsist 化

## A10 完了後 enable 手順 (4 ステップ)

1. **DESIGN.md + UI Inventory + Roborazzi baseline の生成完了確認** (A10 完了マイルストーン)
2. **`code-reviewer/SKILL.md` の aspect status を更新**: `visual-regression` / `design-tokens` を `A10 完了後 enable` → `active`
3. **Coordinator 並列起動対象に追加**: `code-reviewer` Skill の system prompt + サブエージェント起動コマンド更新
4. **binary checklist 各 5 項目の確定**: 本 rule §visual-regression / §design-tokens の項目を Coordinator が parse して機械検証

## Gotchas

- **各 aspect は独立した system prompt** で動作 (Generator バイアス回避、R-13)
- **Claude API への直接呼び出しは禁止**、ローカル Claude Code のサブエージェントで並列実行 (R-37 / ADR 0017)
- **Critical = 0 のみ Ready 昇格**、Improvement は non-blocking
- **人間レビュアーには「code-reviewer の指摘で十分か?」を考えさせる文言** を PR コメントに含める (R-15、上記フォーマット参照)
- **PR コメント post 前に PII / secrets redaction を必ず通す** (R-26、`.claude/rules/pii.md` / `secrets.md`)
- **harness PR の既定 4 aspect** (`spec-conformance` / `architecture` / `security` / `code-quality`) で十分、他 4 aspect は skip 妥当な場合が多い (A2-1 / A2-2 / A2-4 / A2-5 実績)
- **mirror PR は spec-conformance / architecture / security の 3 aspect で十分** (`merge-readiness.md` Gotchas)

## 関連

- ADR 0017 (ローカルポーリング駆動、サブエージェント並列実行)
- ADR 0019 (`code-reviewer` 8 aspect + Coordinator)
- `docs/harness/plan.md` §5.4.3 / R-13 / R-37
- `.claude/skills/code-reviewer/SKILL.md`
- `.claude/rules/{merge-readiness,pr-draft-policy,implementation-workflow,pii,secrets,naming,spec-traceability,coverage-100,mutation-testing,design-tokens,ui-snapshot,behavior-preservation}.md`
