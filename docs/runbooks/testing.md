---
id: runbook-testing
title: テスト実行と三層指標の運用
status: living
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.10 / §6.2 A7
related_adrs:
  - ADR-0013
  - ADR-0015
  - ADR-0016
  - ADR-0023
---

# テスト実行と三層指標の運用

> **5 行以内 summary**: ColorMaster のテストは **三層指標** (A. Line/Branch coverage /
> B. Spec coverage / C. Mutation score) で多層検証する。本ファイルは「現時点で実行可能な
> コマンド」と「A7 / A10 で追加するコマンド」を二分し、Phase A〜C の各段階で何ができて
> 何が未着手かを明確化する。fix-loop 上限は `implementation-workflow` Phase 4 参照。

## 1. 三層指標の概要

| 指標 | 計測ツール | ゲート | 関連 ADR | 本格化フェーズ |
|---|---|---|---|---|
| **A. Line / Branch coverage** | Kover (`koverHtmlReport` / `koverXmlReport`) | Phase A: 計測可能 / Phase C 各 PR: 差分 100% / Phase C 完了: 全体 100% | ADR 0013 | A7 (Gradle 統合) / Phase C (達成) |
| **B. Spec coverage** | `@Spec` annotation + Konsist | 新規機能: 差分 100% / 既存: Phase C 段階達成 | ADR 0016 | A7 (Konsist rule 追加) / Phase C (達成) |
| **C. Mutation score** | PITest + pitest-kotlin + gradle-pitest-plugin (JVM target) | 計測のみ、ゲートしない、PR コメントで可視化 | ADR 0015 | A7 (Gradle 統合) |
| **D. Visual regression** (参考) | Roborazzi (`verifyRoborazziDebug` 等) | UI 凍結三本柱の 1 つ | ADR 0023 | A10 (baseline 生成) |

## 2. 現時点 (Phase A 進行中) で実行可能なコマンド

```bash
./gradlew check                  # 通常テスト (Lint + Unit + Konsist) — A2 完了後に全面有効化
./gradlew test                   # ユニットテストのみ
./gradlew :core:common:check     # 特定モジュールのみ
./gradlew --refresh-dependencies # 依存キャッシュ再構築
```

kotest 6.0.0.M1 / kotest-runner-junit5 を採用 (`gradle/libs.versions.toml`)。
テスト実装規約は `.claude/rules/kotlin-test.md` (A2-2 で本格化)、paired-class 規約は
`.claude/rules/test-paired-class.md` を参照。

## 3. A7 で追加されるコマンド

```bash
./gradlew koverHtmlReport        # 指標 A — HTML レポート (Kover)
./gradlew koverXmlReport         # 指標 A — CI 用 XML
./gradlew koverVerify            # 指標 A — Coverage Rule (差分 / 全体) を検証
./gradlew pitest                 # 指標 C — Mutation testing (JVM target)
./gradlew konsistTest            # 指標 B — Spec coverage / 構造 lint (Konsist)
```

A7 までの暫定運用:

- **指標 A**: Kover は Gradle に未統合。手動で `./gradlew test` の結果を確認するに留める
- **指標 B**: `@Spec` annotation 未導入。仕様 ↔ テストの紐付けは PR description に手書きで記載
- **指標 C**: PITest は Gradle に未統合。Mutation testing は実施しない

## 4. 差分カバレッジゲート (A7 で実装)

PR で新規追加・変更された Kotlin 行に対して **line / branch 100%** を要求。
実装方式は A7 内で確定 (候補: koverVerify Coverage Rule + diff includes /
codecov patch coverage / GitHub Actions 独自スクリプト)。

## 5. 既存コードの段階達成 (Phase C)

- Phase A 完了時点では既存未カバー分は **ADR 0013 の除外リスト** で暫定許容
- Phase C 各 Epic / Plan で対象モジュールの既存コードを段階的にカバー
- Phase C 完了時に **全モジュールで line / branch 100%** + ADR 0013 列挙分のみ除外

## 6. Roborazzi baseline (A10 以降)

- 4 パターン (mobile / desktop × Light / Dark) を `docs/design/inventory/screenshots/` に commit
- 更新時は **human approve 必須** (`.claude/rules/ui-snapshot.md`)
- baseline ファイルが PR diff に大量に出る場合は別コミットに分離して review 容易化

```bash
./gradlew verifyRoborazziDebug   # baseline と比較 (A10 以降)
./gradlew recordRoborazziDebug   # baseline 再記録 (人間 approve 後のみ実行)
```

## 7. fix-loop の上限 (`implementation-workflow` Phase 4)

`implementation-workflow` Skill (A3) は実装 → テスト → 失敗 → 修正の自動ループを回すが、
**fix-loop の上限を 3 回** に設定する。3 回失敗時は **人間にエスカレーション**
(詳細は `.claude/rules/implementation-workflow.md` 参照、A2-3 で本格化)。

## 8. CI 上のテスト実行

- GitHub Actions は `./gradlew check` を実行 (ADR 0017、Claude API 直接呼び出し禁止)
- A7 以降は `./gradlew koverVerify` / `./gradlew konsistTest` / `./gradlew pitest` を CI に追加
- ワークフロー定義: `.github/workflows/**`

## 9. テスト fixture の規約

- ダミーメールは **`@example.com` ドメイン限定** (RFC 2606、`.claude/rules/pii.md`)
- ダミー uid は連番文字列 (`test-uid-001` 等)
- 実 PII を fixture に commit したらコード化された incident として扱い、即時 history から除去 + R2 token ローテーション

## Phase A〜C 持ち越し (本格化フェーズ)

| 持ち越し項目 | 持ち越し先 | 理由 |
|---|---|---|
| Kover Gradle 統合 + `koverVerify` Coverage Rule | A7 | 指標 A の機械ゲート確立 |
| `@Spec` annotation 実装 + Konsist rule | A7 | 指標 B (Spec coverage) のトレーサビリティ確立 |
| PITest Gradle 統合 + PR コメント可視化 | A7 | 指標 C の計測導入 |
| Roborazzi baseline 4 パターンの初期生成 | A10 | UI Inventory + DESIGN.md 確定後に baseline 凍結 |
| 既存モジュールの段階的 100% カバレッジ達成 | Phase C 各 Epic | 各機能本格実装と連動 |
| Konsist Spec coverage rule のメンテナンス手順 | A7 で確定後 | rule 安定後にメンテ runbook 追加 |
| Mutation testing の baseline / 改善目標 | A7 + Phase C | 計測値の蓄積後に方針確立 |

## 関連

- `docs/harness/plan.md` §3.10 / §6.2 A7 / A9 / A10
- ADR 0013 / 0015 / 0016 / 0023
- `.claude/rules/{coverage-100,spec-traceability,mutation-testing,ui-snapshot,screenshot-test,kotlin-test,test-paired-class}.md`
- `.claude/rules/implementation-workflow.md` (A2-3 で本格化、fix-loop 上限)
- `gradle/libs.versions.toml` (kotest 6.0.0.M1 / SQLDelight 2.1.0)
