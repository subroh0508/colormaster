---
id: learnings-flaky-tests
title: フレーキーテスト記録
status: living
last_updated: 2026-05-17
---

# フレーキーテスト記録

> **5 行以内 summary**: 再現性が低いテスト (Flaky tests) の発生条件・回避策・恒久対策
> メモを蓄積する場所。1 PR = 1 ファイルの learning ファイルとは別建て (横断的な参照を
> 容易にするため)。`pr-retrospective` Skill が PR レトロで Flaky を検出したら本ファイルに
> 追記し、対象テストの恒久対策 PR を起票したら「対策日」と PR 番号を記録する。
> A1 レトロ Try (DefaultIdolColorsRepositorySpec のフレーキー対策メモ蓄積) で起票。

## 運用ルール

- **追記 only**: 既存記録の削除は禁止 (歴史的経緯を辿るため)。解決時は同じ行に「対策日」「対策 PR」「再発有無」を追記。
- **記録粒度**: テストクラス + テストケース名で 1 行。同一クラスでも別ケースは別行に。
- **再現条件**: CI 環境 (Android / JVM / wasmJs) / 同時実行有無 / コードベース時点 (commit SHA) / 失敗確率 (再実行で PASS した割合) を記録。
- **暫定回避策**: 「再実行で PASS」「Test fixture 修正」「`runTest` 仮想時間導入」等を記録。
- **恒久対策**: 別 PR で `runTest` 化 / モック書き換え / アサーション緩和 / テスト分割等を実施。

## フレーキー一覧

| 起票日 | テストクラス | テストケース | 再現条件 | 暫定回避策 | 恒久対策 PR | 対策日 | 再発有無 |
|---|---|---|---|---|---|---|---|
| 2026-05-17 | `core.data.DefaultIdolColorsRepositorySpec` (`:core:data:testDebugUnitTest`) | `#search(by name): when lang = 'en'` | B0 PR #117 マージ前の CI Android で初回 FAILED → 再実行で PASS。本体はモック (`mockIdolSearch`) 基盤で外部依存なし。Flow 経由の emission ordering / `flowToList` のタイミング起因が疑わしい | CI 再実行 (1 サイクル待機) | (未起票、A9 baseline 記録時 or A6 Lint 導入時に対策検討) | — | 未確認 (再発状況は本テーブル append で追記) |

## 分析メモ

### `DefaultIdolColorsRepositorySpec > #search(by name): when lang = 'en'` の分析

- 観点 1: **Flow / coroutine の collection 完了待ち**: `flowToList` がテスト側で `.first()` / `.toList()` を取得する際の completion 待ちが不足している可能性。`runTest` の仮想時間に切り替えれば deterministic に。
- 観点 2: **モック `mockIdolSearch` の expected response 設定タイミング**: `every { ... } returns ...` の前に search が呼ばれている可能性。`coEvery` への置換 + `runTest` の組み合わせで解消の可能性。
- 観点 3: **同テストクラス内の状態共有**: テスト間で repository instance が共有されている場合、前テストの cache が影響している可能性。`@BeforeEach` で repository を都度生成しているか要確認。

### Konsist / Gradle カスタムタスクとの連携 (A6 / A7 で導入時)

- A6 で Konsist 規約 (`@TestPair` / `@Spec`) を導入する際、**Flaky 検出規約** (`@Flaky("flaky-tests.md#L<line>")` annotation) も検討
- A7 で Kover / PITest 導入後、Flaky テストは **mutation score 計算から除外** するか別カウンタで扱うかを ADR 改訂で決定

## 関連

- `docs/harness/learnings/2026-05-17-pr-117.md` (B0 レトロ、Flaky 検出の出典)
- `.claude/rules/kotlin-test.md` (A2-2 で本格化、`runTest` / 仮想時間 / モック規約)
- `.claude/rules/retrospective-format.md` (PR レトロでの Flaky 検出フォーマット)
- `docs/runbooks/testing.md` (テスト運用、A2-4 で本格化)
