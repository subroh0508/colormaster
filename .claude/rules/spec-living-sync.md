---
id: rules-spec-living-sync
title: 実装中の仕様変更時の双方向同期規約
status: stable
last_updated: 2026-05-17
paths:
  - "docs/requirements/**"
  - "docs/specifications/**"
  - ".claude/skills/implementation-workflow/**"
related_adrs:
  - ADR-0027
related_plan: docs/harness/plan.md §4.6 / R-32 / R-33
---

# spec-living-sync.md — 実装中の仕様変更時の双方向同期規約

> 実装フェーズ (Phase 3) で要件 / 基本設計 / 詳細設計に齟齬が見つかった場合に、
> 実装と spec docs を **同 PR 内で同時更新** するルール。docs の SoT 性を維持しつつ、
> living docs として実装中の発見を取り込む手順を規定。

## 基本原則

- **docs は SoT、実装はその実体化** (ADR 0027): 実装中に「docs の記述が誤っている」「不足している」「曖昧」と判明したら docs を先に修正
- **同 PR 内で同時更新が既定**: 別 PR に分けると docs と実装の乖離期間が発生する
- **大規模な仕様変更 (REQ-NNN / SPEC-NNN-N 1 件の章立てを書き換える等) は別 Plan / Epic に切り出す**: 1 PR で完結しないと判明したら Phase 3 を中断、Plan / Epic の昇格 (`plan.md` §Epic 昇格条件)

## 同期パターン分類

| パターン | 例 | 対応 |
|---|---|---|
| **docs 軽微訂正** | 用語の typo / 曖昧な表現の明確化 / SPEC 内のサンプル ID 修正 | 同 PR で docs 修正、PR description「仕様変更箇所」セクションに記載 |
| **docs 内容追加** | 実装中に発見した追加 AC / エラーケースの記述 | 同 PR で docs 追加 (REQ / SPEC の Acceptance criteria に行追加)、PR description 記載 |
| **docs 内容削除** | 実装で不要と判明した AC / フィールド | 同 PR で docs 削除 + ADR 起票要否を `adr.md` §起票基準で判定、Plan の Scope 内変更で済むなら ADR 不要 |
| **アーキテクチャ的変更** | レイヤー越境 / 既存 ADR と矛盾 | **同 PR は中断**、ADR 起票 (新規 or supersedes) → Plan / Epic 昇格 |
| **API 仕様変更 (Breaking)** | OpenAPI スキーマ変更 / 認証境界変更 | 別 Plan / Epic に切り出す (PR 分割) |

## PR description「仕様変更箇所」セクション

仕様変更を含む PR は description に以下を必ず記載:

```markdown
## 仕様変更箇所 (spec-living-sync)

| 変更前 | 変更後 | 影響範囲 (REQ/SPEC ID) | 同 PR で対応 | 別 PR 切り出し |
|---|---|---|---|---|
| `SPEC-IDOL-001-3` の Acceptance criteria 4 「name 検索で大文字小文字区別」 | 大文字小文字区別なし (デフォルト) | SPEC-IDOL-001-3 / REQ-001 | ✅ | — |
| ... | ... | ... | ... | ... |
```

- **変更前 / 変更後 は SPEC-ID + 行参照** (`file_path:line` 形式) で明示
- **影響範囲** は REQ / SPEC ID リストで網羅
- **同 PR で対応 / 別 PR 切り出し** のいずれか必須選択

## 双方向リンクの維持

- REQ ⇄ SPEC-basic ⇄ SPEC-detail の frontmatter `related_*` リンクを実装と同期
- 例: SPEC-IDOL-001-3 に Acceptance criteria を追加したら `related_basic` / `related_detail` ペアの整合を確認
- 詳細は `docs-structure.md` §frontmatter 必須キー表 + `docs/traceability.md` (A6 自動生成) 参照

## 実装と spec の対応付け (`@Spec` annotation)

- 実装変更時に対応する `@Spec("SPEC-NNN-N")` annotation をテスト側に追加 / 更新 (`spec-traceability.md` A7 で本格化)
- code-reviewer spec-conformance aspect が検証 (`code-reviewer-aspects.md` 参照)
- PR description「仕様変更箇所」表で「対応 `@Spec` テストファイル」列を追加可

## Phase 3 → Phase 5 の流れ (spec-living-sync 発動時)

1. **Phase 3 実装中に齟齬発見** → 種別判定 (軽微訂正 / 追加 / 削除 / アーキテクチャ / Breaking)
2. **軽微訂正 / 追加 / 削除**: 同 PR で docs 修正、テスト + 実装と一緒に commit
3. **アーキテクチャ / Breaking**: Phase 3 中断、ADR 起票 → Plan / Epic 昇格 → 新 Plan / Epic で再着手
4. **Phase 5 PR 起票時**: description「仕様変更箇所」セクション記入
5. **Phase 6 code-reviewer**: spec-conformance aspect が SPEC-ID 整合 / frontmatter リンク有効性を検証

## 機械検証 (A6 で導入予定)

- **Gradle カスタムタスク**: REQ ⇄ SPEC-basic ⇄ SPEC-detail の frontmatter `related_*` 双方向参照を検証 (片方向リンクは warning)
- **Konsist**: `@Spec("SPEC-NNN-N")` annotation の SPEC-ID が `docs/specifications/{basic,detail}/` に実在することを検証
- **docs/traceability.md 自動生成** (A6): REQ / SPEC / EPIC / PLAN / ADR / 実装ファイルの対応表を機械生成、`spec-living-sync` の同期漏れを検知

## Gotchas

- **docs を後追い更新する誘惑を断つ**: 「実装が先、docs は次の PR で」は SoT 性を壊す。Phase 3 で発見したら同 PR で docs 修正
- **設計書本文にコード断片を書かない** (`docs-structure.md` §4.6 のコード禁止原則): docs 修正時もコード断片を混入させず、`file_path:line` 参照で代替
- **アーキテクチャ変更は ADR 起票が前提**: ADR を起票せずに plan.md / SPEC の根幹を変えると SoT が二重化する (PR #119 レトロ Problem #1 と同様の循環参照リスク)
- **PR description「仕様変更箇所」セクション空欄禁止**: 「軽微訂正なし」の場合も「N/A」と明示 (A1 レトロ Problem #4 と同様、誤読防止)
- **Epic 配下 PR は decisions.md にも記録**: EPIC 配下 PR で仕様変更を行った場合、`docs/epics/<id>/decisions.md` にも判断ログを残す (`epic.md` 参照)
- **複数 PR にまたがる spec drift は traceability.md で検出** (A6 完了後): 同期漏れは A6 機械検証で検知、それ以前は人間レビュー + code-reviewer spec-conformance aspect 任せ

## 関連

- ADR 0027 (docs 構造 + 5 行 summary + 日本語化、docs SoT 性の根拠)
- `docs/harness/plan.md` §4.6 / R-32 / R-33
- `.claude/rules/{docs-structure,implementation-workflow,code-reviewer-aspects,adr,plan,epic}.md`
- `.claude/rules/spec-traceability.md` (A7 で本格化、`@Spec` annotation 規約)
- `docs/traceability.md` (A6 自動生成、双方向リンク機械検証)
