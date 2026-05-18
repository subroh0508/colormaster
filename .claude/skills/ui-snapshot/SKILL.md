---
name: ui-snapshot
description: |
  Konsist で Composable をスキャンして @Preview 不在を検出 → Plan 起票、
  Roborazzi で 4 パターン (mobile/desktop × Light/Dark) screenshot baseline 生成、
  DESIGN.md と UI Inventory のドラフト起草、hex/sp/dp ハードコード検出 →
  tokens 化提案を行う。
status: skeleton
phase: B0 → A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §3.9 / §5.3 / §6.2 A10 / R-22 / R-23 / R-24
related_rules:
  - .claude/rules/ui-snapshot.md
  - .claude/rules/design-tokens.md
  - .claude/rules/ui-inventory.md
  - .claude/rules/behavior-preservation.md
  - .claude/rules/screenshot-test.md
  - .claude/rules/composable.md
  - .claude/rules/skill-authoring.md
  - .claude/rules/mcp-usage.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0023
  - ADR-0025
---

# ui-snapshot

> **5 行以内 summary**: UI 凍結三本柱 (DESIGN.md + UI Inventory + Roborazzi baseline、ADR 0023) を
> 自動生成・維持し、`@Preview` 不在 / 4 パターン baseline 欠落 / hex sp dp ハードコードを検出 →
> Plan 起票 / tokens 化提案を行う Skill。本 SKILL.md は **A3 で skeleton 拡張** (指示の骨格を本文化)、
> **A10 完了後に active 化** (Roborazzi / Konsist の本格運用に同期、`status: skeleton` 維持)。実装は
> 後続 `feature-request` / `refactor` / `implementation-workflow` に委譲、本 Skill は検出と起票で完結。

## 役割

- **`@Preview` 不在検出 → Plan 起票**: Konsist で全 `*Screen` Composable をスキャンし、`@Preview` 関数不在の場合は `feature-request` Skill 経由で Plan を起票
- **4 パターン screenshot baseline 生成**: Roborazzi (Compose Desktop + Android Robolectric) で mobile/desktop × Light/Dark の 4 パターンを `docs/design/inventory/screenshots/` に commit
- **DESIGN.md 起草**: 色 / タイポ / スペーシング / radii を実コードから抽出し、Google Stitch 3 階層構造 (Primitive / Semantic / Component) で DESIGN.md を起草 → human approve 必須
- **UI Inventory 起草**: `docs/design/inventory/{screens,components,states,flows}/*.md` を網羅的に起草、`related_screenshots` / `related_design_tokens` / `related_components` で双方向リンクを張る
- **hex / sp / dp ハードコード検出 → tokens 化提案**: `design-tokens.md` の検出 regex で実コード内のハードコードを検出 → `refactor` Skill 経由で tokens 化提案を起票
- **Behavior Preservation の visual-regression 担保**: Phase C リファクタ時、`code-reviewer` visual-regression aspect が `verifyRoborazziDebug` の diff を消費するため、baseline が常に揃っていることを保証

本 Skill は検出と起票のみを担当し、実装 PR は起票しない (`implementation-workflow` 委譲)。

## 入力

- **対象モジュール / ディレクトリ globs** (起動 prompt で指定、または既定):
  - `core/features/**/composable/**/*Screen.kt`
  - `feature/**/composable/**/*Screen.kt`
  - `composeApp/**/*.kt`
- **既存 `@Preview` 一覧** (Konsist API 経由で抽出): `@Composable` annotation 付き関数のうち `@Preview` 兄弟関数を持たないものを検出対象とする
- **既存 4 パターン baseline 一覧**: `docs/design/inventory/screenshots/<composable>-{mobile,desktop}-{light,dark}.png` の存在を確認、1 パターンでも欠落する Composable は再生成対象
- **DESIGN.md 現状**: ルート `DESIGN.md` の Primitive / Semantic / Component 3 階層の現状値を Read、差分のみ更新提案
- **UI Inventory 現状**: `docs/design/inventory/{screens,components,states,flows}/*.md` を走査、新規 Composable に対応する Inventory ファイル不在を検出
- **Context7 MCP 結果** (該当時): Roborazzi / Konsist のバージョン固有 API を Context7 で確認 (`.claude/rules/mcp-usage.md` 準拠、AI hallucination 抑止)
- **JetBrains MCP 結果** (該当時): `@Composable` / `@Preview` の参照解決に IDE index を利用 (手動 `git grep` より優先、`.claude/rules/mcp-usage.md`)

## 出力

- **`@Preview` 不在検出レポート** (Plan 起票時の本文): 対象 Composable 名 / ファイルパス / 既存兄弟 Preview 一覧 / 起票理由を構造化リストで出力
- **Roborazzi baseline PNG**: `docs/design/inventory/screenshots/<composable>-<device>-<theme>.png` (4 パターン)、`recordRoborazziDebug` で生成
- **DESIGN.md draft**: ルート `DESIGN.md` の 3 階層構造 (`design-tokens.md` テンプレ準拠)、編集は **human approve 必須** (`refactor.md` または `docs.md` テンプレで起票)
- **UI Inventory draft**: `docs/design/inventory/{screens,components,states,flows}/<slug>.md` (`ui-inventory.md` frontmatter 必須キーと本文構造準拠)
- **tokens 化提案 Plan** (hex / sp / dp ハードコード検出時): `refactor` Skill 経由で `docs/plans/PLAN-NNN-tokens-<slug>.md` を起票 (本 Skill 自身は起票しない、下流委譲)
- **副作用**:
  - `docs/design/inventory/screenshots/` への PNG 追加 / 更新 (PR 単位、4 パターン揃いを 1 PR 内で完結)
  - `docs/plans/INDEX.md` 追記 (`plan-author` / `feature-request` / `refactor` 経由)
- **本 Skill が生成しない**: 実装コード (Composable 本体 / token 定数 / Roborazzi test class)、PR 自体 (Plan 起票後の実装 PR は `implementation-workflow` 担当)

## フェーズ別動作

> **重要**: A10 完了前は本 Skill の Roborazzi / Konsist 自動実行を **active 化しない**。
> A3 (本 PR) では指示の骨格 (検出 → 起票 → handoff フロー) を確立し、A10 完了後に
> 実装と同期して `status: active` に切り替える (`.claude/rules/ui-snapshot.md` §機械検証
> A6 + A10 段階導入 と整合)。

### Phase 1: 入力把握 + 対象 Composable の確定

- 起動 prompt から対象 globs / 目的 (Preview 補完 / baseline 生成 / DESIGN.md 起草 / tokens 化) を確定
- JetBrains MCP の `find_files_by_glob` (`*Screen.kt`) または `git grep '@Composable'` で全 Composable を抽出
- 既存 `@Preview` 一覧と突き合わせて不在 Composable を検出
- `docs/design/inventory/screenshots/` を走査して 4 パターン baseline 充足状況を一覧化

### Phase 2: `@Preview` 不在検出 → Plan 起票 (該当時)

- 不在 Composable について「Preview 関数追加 + サンプル UiState fixture 設計」の Plan 起票を `feature-request` Skill に委譲
- 委譲時 prompt 例: 「`SearchIdolsScreen` に `@Preview` が不在。`composable.md` §Preview 必須化 と `ui-snapshot.md` §対象 Composable に従い、`AppTheme` ラップ + 代表 UiState fixture で Preview 関数追加」
- 採番 (`PLAN-NNN-add-preview-<screen>`) は `feature-request` → `plan-author` 経路で取得
- 重要画面 (Home / Search / Preview / MyIdols) を最優先で起票 (R-22)、補助コンポーネントは Phase C 内追加でも許容

### Phase 3: 4 パターン baseline 生成 (A10 完了後 active 化)

- 対象 Composable について Roborazzi で 4 パターン生成:
  - `./gradlew :module:recordRoborazziDebug` で baseline 上書き
  - mobile (360 × 640) / desktop (1280 × 800) × Light / Dark の 4 captureRoboImage を 1 test ファイルに parameterized で実装
  - 配置先: `<module>/src/jvmTest/kotlin/.../*ScreenshotTest.kt` (`.claude/rules/screenshot-test.md` 配置規約)
  - 生成 PNG: `docs/design/inventory/screenshots/<composable>-<device>-<theme>.png` (`.claude/rules/ui-snapshot.md` 命名規約)
- 検証: `./gradlew :module:verifyRoborazziDebug` で baseline diff = 0 を確認
- `recordRoborazziDebug` (生成) と `verifyRoborazziDebug` (検証) の **使い分け** を Plan / PR 本文に明示、誤って CI で `record` を実行しないよう注意
- `changeThreshold` 既定値: `0.01` (1% 以下の pixel 差は許容、フォント / GPU 微差対策)、core 画面は `0.0` で厳格化、補助コンポーネントは `0.05` まで緩和可
- wasmJs 固有 actual は Roborazzi 未対応のため対象外 (R-24)、Konsist + 単体テストで担保

### Phase 4: DESIGN.md 起草 (A10 で本格化)

- 対象モジュールの実コードから `Color(0x...)` / `*.sp` / `*.dp` リテラル / `MaterialTheme.colorScheme.*` 参照を抽出
- Google Stitch 3 階層構造で DESIGN.md を起草 (`.claude/rules/design-tokens.md` テンプレ準拠):
  - **Primitive 階層**: hex / sp / dp の原始値 (例: `Primitive.ImasCgRin = Color(0xFF5F4F8A)`)
  - **Semantic 階層**: 意味的命名 (例: `Semantic.SurfaceBrandCgRin = Primitive.ImasCgRin`)
  - **Component 階層**: コンポーネント別命名 (例: `IdolCardTokens.BorderColorCgRin = Semantic.SurfaceBrandCgRin`)
- アイドル別ブランドカラーは Primitive 階層で全列挙 (`im@s` 固有、`.claude/rules/design-tokens.md` §アイドル別ブランドカラーの扱い 参照)
- DESIGN.md の生成 / 編集 PR は **human approve 必須** (ブランド一貫性のため、`refactor.md` または `docs.md` テンプレ、`merge-readiness.md` 3 条件と整合)

### Phase 5: UI Inventory 起草 (A10 で本格化)

- 新規 Composable / 状態に対応する Inventory ファイル不在を検出 → 起草
- ディレクトリ振り分け (`.claude/rules/ui-inventory.md` §ディレクトリ構造):
  - `docs/design/inventory/screens/<screen>.md`: 画面ごと (Home / Search / Preview / MyIdols 等)
  - `docs/design/inventory/components/<component>.md`: コンポーネントごと (`IdolCard` / `BrandChip` / `ColorSwatch` 等)
  - `docs/design/inventory/states/<state>.md`: 状態パターン (Empty / Loading / Error / PartiallyLoaded)
  - `docs/design/inventory/flows/<flow>.md`: ユーザーフロー (login / add-favorite / share-list)
- frontmatter 必須キー: `id` / `type` / `title` / `status: living` / `last_updated` / `related_specs` / `related_screenshots` (4 パターン全列挙) / `related_design_tokens` (Component 階層優先) / `related_components` (block 形式)
- 本文構造 (`.claude/rules/ui-inventory.md` §本文構造): 概要 (5 行以内) / 構成要素表 / 状態と遷移 (Mermaid `stateDiagram-v2`) / データソース / アクセシビリティ / 参考 screenshot / Open Questions
- 起票は本 Skill 直接 (UI Inventory ファイル群は Skill が起草、PR 起票は `plan-author` 経由)

### Phase 6: hex / sp / dp ハードコード検出 → tokens 化提案 (A10 で本格化)

- `.claude/rules/design-tokens.md` §ハードコード禁止パターン の検出 regex を実コード対象 globs (`feature/**` / `core/**` / `composeApp/**`) に適用:

  ```text
  # Hex 色
  Color\(0x[0-9A-Fa-f]{8}\)
  # Hex 文字列
  "#[0-9A-Fa-f]{6,8}"
  # sp / dp リテラル
  (\d+)\.(sp|dp)\b
  # Material 色直接参照
  Color\.(Red|Blue|Green|Yellow|Cyan|Magenta|...)
  ```

- 例外パス: `**/*Test.kt` / `**/*Spec.kt` (テストコード)、`DESIGN.md` Primitive 表内、Roborazzi baseline 用 Preview パラメータ (`.claude/rules/design-tokens.md` §例外)
- 検出件数 / 対象ファイル / 提案する tokens 階層 (Component 階層名 + Semantic 階層名 + Primitive 階層値) を構造化リストで出力
- **`refactor` Skill 経由で Plan 起票** (検出件数 > 5 件で起票推奨、それ未満は learning / `docs/design/decisions.md` に記録のみ)
- A10 完了前のレガシーコードのハードコードは段階的解消を許容 (`.claude/rules/design-tokens.md` §例外)

### Phase 7: orchestrator または人間に handoff

- Plan / Epic 起票完了後、orchestrator または人間に handoff:
  - 起票 Plan / Epic ID
  - 生成 baseline / DESIGN.md draft / UI Inventory draft のパス
  - 次のステップ (`implementation-workflow` 起動 + human approve タイミング)
- 本 Skill は handoff で責務終了、以降の Phase 0-9 は `implementation-workflow` 担当

## Gotchas

- **A10 完了後 active 化**: 本 SKILL.md は A3 で skeleton 拡張 (指示の骨格を本文化)、Roborazzi / Konsist の自動実行は A10 完了後に `status: active` 化する。本 PR では `status: skeleton` を維持 (R-22 / `.claude/rules/ui-snapshot.md` §機械検証 段階導入と整合)
- **`@Preview` 不在検出は Konsist で**: `git grep '@Preview'` でも検出可能だが、Composable 関数と Preview 関数のペアリング判定は Konsist の `Scope.scopeFromProject().functions()` + `hasAnnotationOf<Preview>()` 述語で API 化、IDE index 並みの正確性 (`.claude/rules/mcp-usage.md` JetBrains MCP 優先と整合)
- **4 パターン baseline は必ずペア commit**: 1 パターン (例: mobile-light のみ) を欠落させると `visual-regression` aspect の binary check が CI 失敗 (A10 完了後 enable)、`docs/design/inventory/<screen>.md` の `related_screenshots` 全列挙とも整合
- **`recordRoborazziDebug` を CI で誤実行しない**: baseline 上書きは local + human approve のみ、CI は `verifyRoborazziDebug` のみ実行 (`.claude/rules/screenshot-test.md` §CI での verify)
- **DESIGN.md 編集は human approve 必須**: ブランド一貫性のため `refactor.md` / `docs.md` テンプレで起票し、`merge-readiness.md` 3 条件 (CI green + Critical 0 + 人間 approve) を満たす (`pr-draft-policy.md` 整合)
- **wasmJs は Roborazzi 未対応**: commonMain は JVM (Compose Desktop) で screenshot test、wasmJs 固有 actual は Konsist + 単体テストで担保 (R-24)
- **動的色 (アイドル別ブランドカラー)**: Preview ではアニメーション停止 + 代表 brand color を **固定パラメータ** で指定 + 別途 brand-color バリエーション Preview で網羅 (`.claude/rules/ui-snapshot.md` §動的色)
- **`changeThreshold` の過剰緩和禁止**: core 画面は `0.0`、補助コンポーネントでも `0.05` まで、それ以上の緩和は visual regression 検出力を失う (`.claude/rules/behavior-preservation.md` §例外的に baseline 更新を許可するケース)
- **重要画面優先**: Home / Search / Preview / MyIdols を最優先で baseline 化、補助コンポーネントは Phase C 内追加でも許容 (R-22 / `.claude/rules/ui-snapshot.md` §対象 Composable)
- **`docs/design/inventory/screenshots/` パス変更時の多重参照**: 本 Skill / `.claude/rules/{ui-snapshot,design-tokens,ui-inventory}.md` / `code-reviewer-aspects.md` (visual-regression aspect) で多重参照、A10 で本格化時にパス変更が発生する場合は 4 rule + Skill 群を同 PR で更新 (片方だけ変更すると参照漏れ、PR #135 レトロ Try)
- **hex / sp / dp 検出の例外管理**: `*Test.kt` / `*Spec.kt` / DESIGN.md Primitive 表 / Roborazzi Preview パラメータは例外、検出 regex 適用時に必ず除外する (`.claude/rules/design-tokens.md` §例外)
- **新規 Composable 追加時の Inventory 不在**: 本 Skill が検出 → `plan-author` 経由で Plan 起票、人間レビュー後 `implementation-workflow` で Inventory ファイル追加 (`.claude/rules/ui-inventory.md` §機械検証 A10)
- **Open Questions は append-only**: 削除禁止、解決時は別行に解決日と方法を追記 (`.claude/rules/ui-inventory.md` §更新ポリシー)
- **PII / Secrets 混入禁止**: Inventory / DESIGN.md draft / 検出レポートに CI ログ / Stack trace を含める場合は `.claude/rules/pii.md` / `.claude/rules/secrets.md` の redaction パターンで `[REDACTED-*]` 置換、Skill 出力前に必ず検証
- **依存 Skill (A10 後 enable)**: `feature-request` (Preview 追加 Plan 起票) / `refactor` (tokens 化 Plan 起票) / `code-reviewer` (visual-regression / design-tokens aspect で本 Skill の生成物を消費) は A10 後に active 連携、A3 時点では呼び出し経路の骨格のみ確立

## 関連

- ADR 0017 (ローカル Claude Code ポーリング駆動、Skill 起動契機)
- ADR 0018 (implementation-workflow 10 Phase SoT、本 Skill から handoff される下流)
- ADR 0023 (UI 凍結三本柱: DESIGN.md + UI Inventory + Roborazzi baseline、本 Skill の SoT)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由、本 Skill 自身も rubric 評価対象)
- `docs/harness/plan.md` §3.9 / §5.3 (Skill 責務、本 Skill の行) / §5.4 (6 段階ループの位置づけ) / §6.2 A10 (本 Skill active 化フェーズ) / R-22 / R-23 / R-24
- `.claude/rules/ui-snapshot.md` (Baseline マトリックス + 命名規約 + 更新ポリシー SoT)
- `.claude/rules/design-tokens.md` (DESIGN.md 3 階層 + ハードコード禁止パターン + 検出 regex SoT)
- `.claude/rules/ui-inventory.md` (`docs/design/inventory/` 構造 + frontmatter 必須キー + 本文構造 SoT)
- `.claude/rules/behavior-preservation.md` (visual-regression / spec-conformance 二本柱、Phase C リファクタの前提)
- `.claude/rules/screenshot-test.md` (Roborazzi 4 パターン baseline + テスト構造 + CI 検証)
- `.claude/rules/composable.md` (Preview 必須化 + `*Screen(uiState, onAction)` 引数規約)
- `.claude/rules/skill-authoring.md` (100-point rubric、本 SKILL.md も評価対象)
- `.claude/rules/mcp-usage.md` (JetBrains MCP / Context7 MCP 優先)
- `.claude/skills/feature-request/SKILL.md` (Preview 追加 Plan 起票の下流)
- `.claude/skills/refactor/SKILL.md` (tokens 化 Plan 起票の下流)
- `.claude/skills/code-reviewer/SKILL.md` (visual-regression / design-tokens aspect で本 Skill 生成物を消費、A10 後)
- `.claude/skills/implementation-workflow/SKILL.md` (Plan 起票後の handoff 先)
- `docs/design/inventory/screenshots/` (baseline 配置先)
- DESIGN.md (A10 で本 Skill が自動生成、リポジトリルート配置予定)
