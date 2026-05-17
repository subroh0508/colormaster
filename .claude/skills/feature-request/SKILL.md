---
name: feature-request
description: |
  人間 / 他 Skill からの新機能要求を入力に要件 (REQ-NNN) と基本設計 / 詳細設計 (SPEC-NNN-basic / -detail)
  を起草し、単一 PR スコープなら plan-author、複数 PR スコープなら epic-author を呼んで Plan / Epic
  起票で完結する Spec Gen 専任 Skill。実装には踏み込まず、コード断片を docs/requirements /
  docs/specifications 本文に書かない (§4.6 コード禁止原則)。Plan / Epic 起票後は
  implementation-workflow にバトンタッチする。
status: active
phase: A3
last_updated: 2026-05-18
related_plan: docs/harness/plan.md §4.6 / §5.3 / §5.4 / §6.2 A3
related_rules:
  - .claude/rules/docs-structure.md
  - .claude/rules/plan.md
  - .claude/rules/epic.md
  - .claude/rules/adr.md
  - .claude/rules/template-language.md
  - .claude/rules/markdown.md
  - .claude/rules/mcp-usage.md
  - .claude/rules/pii.md
  - .claude/rules/secrets.md
related_adrs:
  - ADR-0017
  - ADR-0018
  - ADR-0025
  - ADR-0027
---

# feature-request

> **5 行以内 summary**: 人間 / 他 Skill からの新機能要求を入力に、要件定義 (REQ-NNN) → 基本設計
> (SPEC-NNN-basic) → 詳細設計 (SPEC-NNN-detail) を順に起草し、単一 PR スコープなら
> `plan-author`、複数 PR スコープなら `epic-author` を呼んで Plan / Epic 起票で完結する
> Spec Gen 専任 Skill。実装には一切踏み込まず、Plan / Epic 起票後は `implementation-workflow`
> にバトンタッチする。コード断片を設計書本文に書かない (§4.6 コード禁止原則) を強制する。

## 役割

- **Spec Gen 専任**: 要件定義 → 基本設計 → 詳細設計 → (必要時) ADR の起草と、Plan / Epic 起票までを担当
- **実装委譲**: 起票後の実装 PR 起票 / コード変更は `implementation-workflow` に委譲 (本 Skill は Plan / Epic を残して終了)
- **コード混入禁止の強制**: `docs/{requirements,specifications}/**` には Kotlin / SQL / Gradle DSL 等のコード断片を一切書かない (§4.6、`.claude/rules/docs-structure.md` 機械検証で A6 から CI 失敗扱い)
- **Plan vs Epic の判定**: 想定変更行数 / ファイル数 / 仕様波及範囲 / レビュー aspect の走り方 から単一 PR か複数 PR かを判定し、該当 Skill を呼ぶ
- **ADR 起票判定**: 要件定義 / 基本設計の中でアーキテクチャ的に不変な決定が発生したら `adr-author` を呼ぶ (`.claude/rules/adr.md` §起票基準)
- **責務分離**: 要件定義 (WHY / WHAT) と基本設計 (システム外形) と詳細設計 (モジュール責務) の境界を `.claude/rules/docs-structure.md` §4.6.2 と §4.6.3-4.6.5 のテンプレに従って厳守

implementation-workflow / code-reviewer / pr-retrospective 等の後続 Skill とは責務が重複しない (本 Skill は Plan / Epic 起票で終了)。

## 入力

- **起動 prompt** (人間または別 Skill から渡される): 新機能要求の文字列 (背景 / ターゲットユーザー / 達成したいこと / 制約)
- **関連 docs**: `docs/README.md` / `docs/glossary.md` / `docs/codebase-map.md` / 既存 `docs/requirements/REQ-NNN-*.md` / `docs/specifications/{basic,detail}/SPEC-*.md` / `docs/adr/ADR-*.md`
- **既存 Epic / Plan**: `docs/epics/INDEX.md` / `docs/plans/INDEX.md` で関連取り組みの有無を確認 (新規 REQ が既存 SPEC を補強するパターンを検出)
- **Context7 MCP 結果**: 既存ライブラリ依存に影響する要求の場合、Context7 で API / 制約を確認 (`.claude/rules/mcp-usage.md` 準拠、AI hallucination 抑止)
- **JetBrains MCP 結果** (該当時): 既存実装の対象モジュールを index 経由で特定 (`.claude/rules/mcp-usage.md` 「手動 git grep より JetBrains MCP 優先」)

## 出力

- **`docs/requirements/REQ-NNN-<slug>.md`** (常に生成): 機能要件 (FR) / 非機能要件 (NFR) / ユースケース概要 / AC を含む。`docs/requirements/template.md` 準拠、`.claude/rules/docs-structure.md` §4.6.3 主要セクション
- **`docs/specifications/basic/SPEC-NNN-<slug>.md`** (該当時): システム構成 / 業務フロー / 画面遷移 / データモデル (論理) / 外部 I/F 一覧 / エラーケース / AC ↔ テストマップ。`docs/specifications/basic/template.md` 準拠、`.claude/rules/docs-structure.md` §4.6.4
- **`docs/specifications/detail/SPEC-NNN-<slug>.md`** (該当時): モジュール配置 / 主要クラスの責務 / 状態遷移 / シーケンス / データ構造 / 例外 / 設定値 / テストパターン。`docs/specifications/detail/template.md` 準拠、§4.6.5
- **Plan or Epic** (常にどちらか): `plan-author` または `epic-author` Skill を呼んで起票
- **(該当時) ADR**: `.claude/rules/adr.md` 起票基準充足時に `docs/adr/template.md` を copy して `docs/adr/ADR-NNNN-<slug>.md` を直接起草 (本 Skill 自身が担当、`adr-author` Skill は A3 配下では未配置 / 将来配置後は呼び出しに切替予定)
- **副作用**:
  - `docs/requirements/INDEX.md` 追記 (本 Skill が直接書く)
  - `docs/plans/INDEX.md` or `docs/epics/INDEX.md` 追記 (`plan-author` / `epic-author` が書く)
- **本 Skill が生成しない**: 実装コード、テストコード、Draft PR 自体 (Plan / Epic 起票後の実装 PR は `implementation-workflow` 担当)

## フェーズ別動作

### Phase 1: 要求把握 + 関連 docs / ADR / SPEC 調査

- 起動 prompt を読み、要求の **WHY (背景・目的)** と **WHAT (達成したい状態)** を分離して把握
- `docs/README.md` / `docs/glossary.md` / `docs/codebase-map.md` を Read してドメイン用語 / 主要パスを把握
- `docs/requirements/INDEX.md` / `docs/epics/INDEX.md` / `docs/plans/INDEX.md` を走査して **重複・関連する既存 REQ / SPEC / Epic / Plan** を抽出
- 既存 SPEC を補強する要求なら `related_specs` で双方向リンクを張る前提でメモ、新規ドメインなら新規 SPEC-NNN を採番
- ライブラリ / API に依存する制約は **Context7 MCP** で公式 docs を確認、既存実装の対象モジュールは **JetBrains MCP** で index 経由 search

### Phase 2: 要件起草 (REQ-NNN)

- `docs/requirements/template.md` を copy して `docs/requirements/REQ-NNN-<slug>.md` を作成 (採番は `docs/requirements/INDEX.md` の連番)
- `.claude/rules/docs-structure.md` §4.6.3 の 11 セクションを順に埋める:
  1. 概要 / 目的 / 背景 — 自然言語 5 行以内 + 表 (HOW を書かない)
  2. ステークホルダー / アクター — 表
  3. スコープ — 含む / 含まない の箇条書き 2 段
  4. ユースケース概要 — Mermaid `graph` + UC 表 (UC 詳細フローは基本設計に委譲)
  5. 機能要件 (FR) — 表 (FR ID / 機能名 / 説明 / 優先度 must/should/could/won't / 関連 UC)
  6. 非機能要件 (NFR) — IPA 6 大項目に沿った表
  7. 制約 / 前提 — 箇条書き
  8. 用語定義 — 表 + `docs/glossary.md` への参照
  9. トレーサビリティ — 表 (FR ID / 関連 SPEC / 関連 EPIC / 関連 PLAN / 関連 ADR)
  10. 受け入れ基準 (AC) — チェックリスト (AC-NN / 検証可能な条件)
  11. Open Questions — 表 (空でも見出しは残す)
- frontmatter は `.claude/rules/docs-structure.md` §frontmatter 必須キー表 の「要件 (REQ-NNN)」行に準拠 (block 形式)
- 冒頭 5 行 summary を必置 (`> **5 行以内 summary**: ...` 形式)
- `docs/requirements/INDEX.md` に新規行を追記

### Phase 3: 基本設計起草 (SPEC-NNN-basic、該当時)

- **生成判定**: 要件に対する「システム全体像 / 構成 / 流れ」を文書化する必要があるか判断 (単純な文言修正 / 既存 UI のラベル変更等は SKIP 可、新機能 / 新 API / 新画面なら必須)
- 生成する場合は `docs/specifications/basic/template.md` を copy して `docs/specifications/basic/SPEC-NNN-<slug>.md` を作成
- `.claude/rules/docs-structure.md` §4.6.4 の 11 セクションを順に埋める:
  1. 概要 (5 行以内サマリ)
  2. システム構成 — Mermaid `graph LR` + 表
  3. 機能一覧と要件マッピング — 表 (SPEC-ID / 機能名 / 関連 FR ID / 関連 AC ID)
  4. 業務フロー — Mermaid `sequenceDiagram` で actor ⇄ system (クラス間呼び出し詳細は詳細設計に委譲)
  5. 画面遷移 — Mermaid `stateDiagram-v2` + 表
  6. データモデル (論理) — Mermaid `erDiagram` + 表 (物理 DDL / `*.sq` の中身は書かない)
  7. 外部 I/F 一覧 — 表 (詳細リクエスト / レスポンス JSON は `docs/api/colormaster-api.yaml` を SoT として参照)
  8. エラーケース / 例外パターン — 表 (try/catch の構文は書かない)
  9. 受け入れ基準 (AC) ↔ テストマップ — 表
  10. 関連 ADR / リスク — 箇条書き
  11. Open Questions — 表
- frontmatter は `related_requirements: [REQ-NNN]` と `related_detail: [SPEC-NNN-detail]` を必置 (block 形式、`.claude/rules/docs-structure.md` §frontmatter 必須キー表 の「基本設計」行準拠)
- Mermaid 種別の使い分けは `.claude/rules/docs-structure.md` §4.6.6 早見表を参照 (基本設計では `graph` / `sequenceDiagram` / `stateDiagram-v2` / `erDiagram` を使用、`gantt` は roadmap 専用のため使わない)

### Phase 4: 詳細設計起草 (SPEC-NNN-detail、該当時)

- **生成判定**: 詳細設計は AI (implementation-workflow / code-reviewer) が消費する主要文書のため、基本設計を生成したら **原則として詳細設計も生成する** (例外: 文書のみの追加 / glossary 拡張等で実装を伴わない場合は SKIP 可)
- 生成する場合は `docs/specifications/detail/template.md` を copy して `docs/specifications/detail/SPEC-NNN-<slug>.md` を作成
- `.claude/rules/docs-structure.md` §4.6.5 の 11 セクションを順に埋める:
  1. 概要 (5 行以内サマリ)
  2. モジュール / ファイル配置 — 表 (パス / 責務 / 関連 SPEC-ID / 関連 `.claude/rules/`) + Mermaid `graph TD`
  3. 主要クラスの責務 — 表 (クラス名 / 種別 / 責務 / 保持する状態 / 主要メソッドの責務、シグネチャ / 引数型 / 擬似コードは書かない)
  4. 状態遷移 — Mermaid `stateDiagram-v2` + 表
  5. シーケンス — Mermaid `sequenceDiagram` でモジュール間呼び出し (メソッド本文の処理ステップ列挙は書かない)
  6. データ構造 (論理スキーマ) — 表 (型は論理 String / Long / Instant 等、Kotlin `data class` 宣言 / SQL DDL は書かない)
  7. 例外 / リトライ / タイムアウト — 表
  8. 設定値 / 環境変数 — 表 (`.env` の中身は書かない)
  9. テストパターン — 表 (テスト ID / 観点 / パターン / 関連 AC ID / `@Spec` 予定 ID、テストコードは書かない)
  10. 関連 Plan / Epic / ADR / Rules — 箇条書き
  11. Open Questions — 表
- frontmatter は `related_requirements: [REQ-NNN]` と `related_basic: [SPEC-NNN-basic]` を必置 (block 形式)
- 基本設計 ⇄ 詳細設計のペア整合性 (basic 側の `related_detail` と detail 側の `related_basic` が双方向に成立) は機械検証 (A6) で担保されるため、起草時に必ず双方を更新

### Phase 5: Plan / Epic 起票判定 + 該当 Skill 呼出

- **判定基準** (`docs/harness/plan.md` §4.1 と `.claude/rules/plan.md` §Epic 昇格条件 を SoT とする):
  - **Plan (単一 PR スコープ)**: 想定変更ファイル数 ≤ 10 / 想定期間 ≤ 1 週間 / open question 想定なし / 単独 PR で完結見込み
  - **Epic (複数 PR スコープ)**: 想定変更ファイル数 > 10 / 想定期間 > 1 週間 / open question 想定あり / 仕様波及が複数 SPEC / 単独 PR で完結見込みが立たない (4 兆候のいずれかで Epic 検討、`.claude/rules/plan.md` §Epic 昇格条件 4 行表)
  - 迷ったら **Plan で起票** (Plan は後から `status: promoted` + `promoted_to: EPIC-NNN` で Epic に昇格可能、`.claude/rules/plan.md` §Plan ⇄ Epic ⇄ ADR の責務分離)
- **Plan 起票**: `plan-author` Skill を呼ぶ (起動コマンド例: `Skill skill="plan-author"`)、frontmatter `type` は要求種別 (`feature-request` / `bug-fix` / `refactor` / `dependency-upgrade` 等) に合わせる
- **Epic 起票**: `epic-author` Skill を呼ぶ、5 ファイル (README / roadmap / open-questions / decisions / progress) を template から生成、`docs/epics/INDEX.md` 更新、起票直後に `roadmap-tracker` Skill が自動起動
- **ADR 起票判定** (該当時): 要件 / 基本設計で「アーキテクチャ的に不変な決定」が発生したら `.claude/rules/adr.md` §起票基準 (SPEC を超えるアーキテクチャ層の意思決定 / 撤回コストが高い / 他複数の判断に影響等、2 項目以上充足) に基づき `docs/adr/template.md` を copy して **本 Skill 自身が ADR を起草** (`adr-author` Skill は A3 配下では未配置のため、本 Skill が直接担当)。将来 `adr-author` Skill 配置後は呼び出しに切替予定

### Phase 6: orchestrator または人間に handoff

- 生成した REQ-NNN / SPEC-NNN-basic / SPEC-NNN-detail / Plan or Epic のパスとサマリを print
- **本 Skill は実装 PR を起票しない**: Plan / Epic 起票で完結し、orchestrator (cmux 並列実装監督) または人間に **implementation-workflow 起動** をハンドオフ
- ハンドオフメッセージのテンプレ:

```text
✅ feature-request 完了
- REQ: docs/requirements/REQ-NNN-<slug>.md
- SPEC (basic): docs/specifications/basic/SPEC-NNN-<slug>.md (該当時)
- SPEC (detail): docs/specifications/detail/SPEC-NNN-<slug>.md (該当時)
- Plan or Epic: docs/plans/PLAN-NNN-<slug>.md or docs/epics/EPIC-NNN-<slug>/
- 次のステップ: implementation-workflow を起動して Phase 0-9 を実行してください (orchestrator pane 委譲推奨)
```

- 本 Skill 自体は Phase 6 完了後に終了、Plan / Epic ファイルが残るのが成果物

## Gotchas

- **実装に踏み込まない**: 設計書本文 (`docs/{requirements,specifications}/**`) にコード断片 (Kotlin / SQL / Gradle DSL / シェル等のフェンス付きコードブロック) を一切書かない (§4.6 コード禁止原則、A6 で Gradle カスタムタスクが CI 失敗にする)。例外として `file_path:line` 形式の参照は許容
- **frontmatter 配列は block 形式**: flow 形式 `[A, B]` は禁止 (`.claude/rules/docs-structure.md` §frontmatter 規約、A6 で reject)
- **冒頭 5 行 summary 必置**: 各 docs の H1 直後に `> **5 行以内 summary**: ...` を置く (R-32、A6 で機械検証)
- **見出しは日本語**: ADR 0027 / `.claude/rules/template-language.md` 準拠、固定セクション名 (`Open Questions` / `Gotchas` 等) は例外
- **基本設計 ⇄ 詳細設計のペア整合性**: basic 側の `related_detail` と detail 側の `related_basic` が双方向に成立、未生成側は frontmatter の該当キーを `[]` で空配列明示 (null と区別)
- **REQ ⇄ SPEC ⇄ EPIC ⇄ PLAN ⇄ ADR の ID 参照は実在チェック対象**: 参照する ID が `docs/{requirements,specifications,epics,plans,adr}/` に実在しなければ A6 機械検証で reject される
- **既存 SPEC との衝突回避**: 採番前に必ず `docs/specifications/{basic,detail}/INDEX.md` (または `ls` で列挙) で既存 SPEC-NNN を確認、新規エンティティなら新規連番、既存エンティティ補強なら既存 SPEC を更新 (新規採番しない)
- **Plan vs Epic 判定は保守的に**: 迷ったら Plan で起票し、後から `status: promoted` + `promoted_to: EPIC-NNN` で昇格 (`.claude/rules/plan.md` §Epic 昇格条件)、Epic は撤回コストが高いため軽率に起票しない
- **ADR 起票判定の閾値**: SPEC を超えるアーキテクチャ的決定 / 撤回コストが高い決定 / 他複数の判断に影響する決定のみ ADR、それ以外は Epic の `decisions.md` 追記 (`.claude/rules/adr.md` §起票基準)
- **PII / Secrets の混入禁止**: 起動 prompt に PII (実メール / 実 uid / IP 等) や Secrets (API key / token / Bearer / JWT 等) が含まれる場合、設計書本文に転載する前に `.claude/rules/pii.md` / `.claude/rules/secrets.md` の redaction パターンで置換する。テスト fixture のダミーメールは `@example.com` 限定
- **Mermaid の使い分け**: 基本設計 = `graph` / `sequenceDiagram` / `stateDiagram-v2` / `erDiagram`、詳細設計 = `graph TD` / `sequenceDiagram` / `stateDiagram-v2`、roadmap.md 専用の `gantt` は設計書では使わない (§4.6.6 早見表)
- **plan-author / epic-author の呼び分けミス防止**: 単一 PR スコープを epic-author で起票すると Epic ディレクトリが過剰生成され撤回困難。複数 PR スコープを plan-author で起票するとロードマップ追跡対象外 (R-34) になり進捗可視化が機能しない
- **コード断片混入の典型パターン**: 「擬似コード」「サンプル」「実装例」と称した Kotlin / SQL 等の断片は全て本 Skill の禁止対象、責務 / 制約 / I/O 表現を自然言語と Mermaid と表で記述する
- **詳細設計テストパターン表の `@Spec` 予定 ID は placeholder**: Phase 4 テストパターン表 (`.claude/rules/docs-structure.md` §4.6.5 #9) に書く `@Spec` 予定 ID は本 Skill 起草時点では暫定値 (SPEC-NNN-N の候補)、実際の Kotlin 側 `@Spec` annotation は `implementation-workflow` Phase 3 で確定する。SPEC ID 名称変更が発生したら spec-living-sync.md §同期パターン分類 (docs 軽微訂正) で同 PR 内で詳細設計側を更新する
- **`adr-author` Skill 未配置のため ADR 起草は本 Skill 直接**: A3 配下では `adr-author` Skill 未実装 (実体ファイルなし)、`.claude/rules/adr.md` §起票基準 充足時は本 Skill が `docs/adr/template.md` を copy して直接起草する。`adr-author` を将来配置するときは関連 ADR (例: A3 後続 PR) で「呼び出しに切替」を明示
- **`docs/requirements/INDEX.md` が未存在の場合は初回起動時に作成**: 採番 (REQ-001 等) の起点として INDEX.md を新規作成し、ヘッダ行 (`| REQ-NNN | タイトル | type | status | related_epic | 起票日 |` 形式) + 自 REQ-NNN 行を追記する

## 関連

- ADR 0017 (ローカルポーリング駆動、Skill 起動契機)
- ADR 0018 (Skill 駆動 KPT ループ、6 段階フローの Spec Gen 担当)
- ADR 0025 (Skill 作成は `example-skills:skill-creator` 経由、本 Skill 自体も rubric 評価対象)
- ADR 0027 (docs 構造 + 命名規約 + 5 行 summary + 日本語化 + コード禁止原則)
- `docs/harness/plan.md` §4.6 (設計書 3 種類の責務分担 + テンプレ + コード禁止原則)
- `docs/harness/plan.md` §5.3 (Skill 責務一覧、本 Skill の行)
- `docs/harness/plan.md` §5.4 (6 段階ループ、Spec Gen フェーズの位置づけ)
- `docs/harness/plan.md` §6.2 A3 (本 Skill 本格化フェーズ)
- `.claude/skills/plan-author/SKILL.md` (Plan 起票責務、本 Skill が呼ぶ)
- `.claude/skills/epic-author/SKILL.md` (Epic 起票責務、本 Skill が呼ぶ)
- `.claude/skills/implementation-workflow/SKILL.md` (Plan / Epic 起票後のバトンタッチ先)
- `.claude/rules/docs-structure.md` (要件 / 基本設計 / 詳細設計 テンプレ + frontmatter 規約 + Mermaid 早見表)
- `.claude/rules/{plan,epic,adr,template-language,markdown,mcp-usage,pii,secrets}.md`
