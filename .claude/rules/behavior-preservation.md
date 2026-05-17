---
id: rules-behavior-preservation
title: リファクタ時の振る舞い維持原則
status: stable
last_updated: 2026-05-17
paths:
  - "feature/**/*.kt"
  - "core/**/*.kt"
  - "composeApp/**/*.kt"
  - "docs/design/inventory/**"
related_adrs:
  - ADR-0023
related_plan: docs/harness/plan.md §3.9 / §6.2 A9 / A10 / §6.3 Phase C / R-22
---

# behavior-preservation.md — リファクタ時の振る舞い維持原則

> Phase C で大規模リファクタ (EPIC-001 〜 EPIC-006) を行う際に、ユーザーから見える振る舞い
> (UI / API レスポンス / 状態遷移) を変えないことを保証する規約。
> `code-reviewer` の visual-regression と spec-conformance の二本柱で検証する (ADR 0023)。

## 二本柱

| 検証軸 | 担当 aspect | 担保するもの | enable 時期 |
|---|---|---|---|
| **Visual regression** | `code-reviewer` visual-regression aspect | UI 見た目の不変性 (Roborazzi baseline diff) | A10 完了後 |
| **Spec conformance** | `code-reviewer` spec-conformance aspect | 機能仕様の不変性 (`@Spec` + Acceptance criteria が全て pass) | A7 完了後 (現状は手動代替) |

詳細は `.claude/rules/code-reviewer-aspects.md` 参照。

## リファクタの定義 (Phase C)

「**振る舞いを変えないコード整理**」のみリファクタと呼ぶ。以下はリファクタではなく feature 追加とみなす:

- API レスポンスのフィールド追加・削除
- UI レイアウトの変更 (色 / 余白 / フォントサイズ含む)
- 状態遷移の追加・削除
- エラーメッセージの変更
- アニメーション速度 / イージング曲線の変更

これらが必要なときは `feat` / `fix` で別 Plan / Epic を立てる (`branch-naming.md` / `pr-template.md` と整合)。

## Phase C リファクタの順序

1. **A10 で凍結**: DESIGN.md + UI Inventory + Roborazzi baseline で現状を記録
2. **Phase C で構造変更**: リファクタ Plan / Epic を実装
3. **検証**: `./gradlew check` + `./gradlew verifyRoborazziDebug` + `code-reviewer` の visual-regression / spec-conformance aspect が全て green
4. **PR description に "Behavior Preservation 証拠" を必須化** (`pr-template.md` refactor.md セクション参照)

## Behavior Preservation 証拠の必須セクション (refactor PR)

PR description に以下を必ず含める (`pr-template.md` refactor.md 規約):

```markdown
## Behavior Preservation 証拠

### Visual regression (Roborazzi)

- [x] `./gradlew verifyRoborazziDebug` 完走 (4 パターン × N 画面)
- [x] baseline diff = 0 (意図的更新なし)
- [x] (意図的更新あり) PR description「visual regression 意図的更新」セクションに更新内容と理由を記載

### Spec conformance

- [x] 既存 `@Spec` annotation 全件 pass
- [x] SPEC 本体 (`docs/specifications/{basic,detail}/*.md`) に変更なし
- [x] (spec-living-sync 発動なし) PR description「仕様変更箇所」セクション = N/A

### 手動検証 (refactor PR で実施)

- [x] 主要画面 (Home / Search / Preview / MyIdols) で目視動作確認 (mobile / desktop × Light / Dark)
- [x] 状態遷移 (Empty → Loading → Loaded / PartiallyLoaded / Error) 全パターン目視確認
- [x] アクセシビリティ (TalkBack / VoiceOver) 主要画面で動作確認
```

## リファクタ前後の検証チェックリスト

- [ ] 触る予定のコードに対応する Inventory ファイル (`docs/design/inventory/`) が存在
- [ ] 既存 `@Spec` annotation 一覧を grep で取得、リファクタ後も同じ SPEC-ID を pass
- [ ] Roborazzi baseline 4 パターン × N 画面が PR 起票前に存在
- [ ] `code-reviewer` visual-regression aspect の binary checklist 5 項目を確認 (`code-reviewer-aspects.md`)
- [ ] `code-reviewer` spec-conformance aspect の binary checklist 7 項目を確認
- [ ] PR description Behavior Preservation 証拠セクション 3 ブロック全て記入
- [ ] `pr-template.md` refactor.md テンプレ選択 (`gh pr create --template refactor.md`)
- [ ] `branch-naming.md` `refactor/<slug>` または `feature/EPIC-NNN-*-pr-NN` (Epic 配下) ブランチ命名

## 例外的に baseline 更新を許可するケース

| ケース | 説明 | 対応 |
|---|---|---|
| **A10 凍結時点でバグがあった UI の修正** | 凍結後に判明したバグの修正 | PR description に修正理由を明記、別 Plan で実施 (`fix/<slug>`) |
| **アクセシビリティ改善** | contentDescription 追加等、visual diff 無し | visual-regression は false-positive を出すため `changeThreshold` 緩和 + 意図的更新明示 |
| **パフォーマンス改善** | アニメーション最適化等、目視で同じに見える | 同上、Roborazzi `changeThreshold` 緩和 + 意図的更新明示 |
| **フォントレンダリング差** | OS バージョン更新等で発生する微差 | `changeThreshold = 0.01` 範囲内で許容、超過時は意図的更新明示 |

これらは visual-regression が false-positive を出すため、PR description で意図的更新と明示 + human approve 必須 (`merge-readiness.md` 3 条件と整合)。

## A10 完了前のリファクタ制約

- **A10 完了前のリファクタは behavior preservation 検証ができない** (Roborazzi baseline 不在のため)、Phase C 着手前提条件として A10 完了が必須 (R-22)
- A10 完了前に行うリファクタは:
  - **本質的に不可逆な構造変更を避ける** (例: モジュール削除、API スキーマ変更)
  - **可逆な内部リファクタのみ** (例: private 関数の分割、命名変更)
  - **spec conformance は A7 完了後** に厳格化 (現状は手動代替で `@Spec` 整合確認)

## 機械検証 (A6 + A7 + A10 で段階導入)

- **A6**: Konsist で `refactor.md` テンプレ選択 PR の touch ファイル種別を検証 (Kotlin source / config / docs 比率の妥当性)
- **A7**: `code-reviewer` spec-conformance aspect で `@Spec` 整合を機械化
- **A10**: `code-reviewer` visual-regression aspect で Roborazzi baseline diff の `changeThreshold` 内完走を機械化
- **A10**: PR description「Behavior Preservation 証拠」セクション 3 ブロック全記入を GitHub Actions で check (Gradle カスタムタスク連携)

## Gotchas

- **A10 完了前のリファクタは behavior preservation 検証ができない**、Phase C 着手前提条件として A10 完了が必須 (R-22)
- **spec-conformance も A10 と並行して `docs/specifications/basic/SPEC-NNN-*.md` の逆生成 (A9) が必要**: A9 完了後に SPEC docs が揃い、spec-conformance aspect の厳格化が成立
- **リファクタで「ついでに API レスポンスを変える」を絶対許可しない**: PR を分割 (`refactor/<slug>` + `feat/<slug>`)、 commit message も分離
- **Behavior Preservation 証拠セクション 3 ブロック全記入**: 「N/A」明示も許容 (空欄禁止)、CI 必須化は A10 完了後
- **アクセシビリティ改善は visual diff なし** だが PR description に明示 (false-positive 抑制と監査可能性の両立)
- **`changeThreshold` 緩和は core 画面で 0.02 まで、補助コンポーネントで 0.05 まで**: 過剰緩和は visual regression 検出力を失う
- **A10 完了前の可逆リファクタ**: モジュール削除 / API スキーマ変更 / DB マイグレーション等の不可逆操作を避け、内部命名変更 / private 関数分割等に限定

## 関連

- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline)
- `docs/harness/plan.md` §3.9 / §6.2 A9 / A10 / §6.3 Phase C / R-22
- `.claude/rules/{ui-snapshot,ui-inventory,design-tokens,code-reviewer-aspects,pr-template,branch-naming,spec-living-sync,coverage-100,spec-traceability}.md`
- `.claude/skills/{ui-snapshot,code-reviewer}/SKILL.md`
- `docs/design/inventory/` (UI Inventory 配置先)
