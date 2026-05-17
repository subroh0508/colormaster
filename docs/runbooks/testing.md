---
id: runbook-testing
title: テスト実行と三層指標の運用
status: skeleton
last_updated: 2026-05-17
related_plan: docs/harness/plan.md §3.10 / §6.2 A7
related_adrs:
  - ADR-0013
  - ADR-0015
  - ADR-0016
---

# テスト実行と三層指標の運用

> **5 行以内 summary**: ColorMaster のテストは **三層指標** (Line/Branch coverage /
> Spec coverage / Mutation score) で多層検証する。本格化は A7 (計測基盤導入) +
> A9 (baseline 記録) + Phase C 各 Epic (差分 100% + 既存段階達成)。
> Roborazzi baseline 操作も本ファイルに集約 (A10 以降)。

## 三層指標 (骨格、A7 で本格化)

| 指標 | 計測ツール | ゲート | 関連 ADR |
|---|---|---|---|
| **A. Line / Branch coverage** | Kover (`koverHtmlReport` / `koverXmlReport`) | Phase A: 計測可能 / Phase C 各 PR: 差分 100% / Phase C 完了: 全体 100% | ADR 0013 |
| **B. Spec coverage** | `@Spec` annotation + Konsist | 新規機能: 差分 100% / 既存: Phase C 段階達成 | ADR 0016 |
| **C. Mutation score** | PITest + pitest-kotlin + gradle-pitest-plugin | 計測のみ、ゲートしない、PR コメントで可視化 | ADR 0015 |

## 実行コマンド (A7 後)

```bash
./gradlew check                # 通常テスト (Lint + Unit + Konsist)
./gradlew koverHtmlReport      # 指標 A
./gradlew pitest               # 指標 C (JVM target)
./gradlew verifyRoborazziDebug # 指標 (visual regression、A10 以降)
```

## 差分カバレッジゲート (A7 で実装)

PR で新規追加・変更された Kotlin 行に対して line / branch 100% を要求。
実装方式は A7 内で確定 (候補: koverVerify Coverage Rule + diff includes / codecov patch coverage / GitHub Actions 独自スクリプト)。

## 既存コードの段階達成 (Phase C)

- Phase A 完了時点では既存未カバー分は **ADR 0013 の除外リスト** で暫定許容
- Phase C 各 Epic / Plan で対象モジュールの既存コードを段階的にカバー
- Phase C 完了時に **全モジュールで line / branch 100%** + ADR 0013 列挙分のみ除外

## Roborazzi baseline (A10 以降)

- 4 パターン (mobile / desktop × Light / Dark) を `docs/design/inventory/screenshots/` に commit
- 更新時は **human approve 必須** (`.claude/rules/ui-snapshot.md`)
- baseline ファイルが PR diff に大量に出る場合は別コミットに分離

## 関連

- `docs/harness/plan.md` §3.10 / §6.2 A7 / A9 / A10
- ADR 0013 / 0015 / 0016 / 0023
- `.claude/rules/{coverage-100,spec-traceability,mutation-testing,ui-snapshot,screenshot-test}.md`
