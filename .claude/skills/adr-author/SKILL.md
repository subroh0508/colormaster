---
name: adr-author
description: |
  アーキテクチャ決定の ADR 起票を担当する Skill。起票基準判定 + 採番 + テンプレ起草 +
  関連 ADR 双方向リンク + INDEX 更新を実施。起票基準を満たさない決定は
  rules / epic decisions.md / learning への記録を推奨し、本 Skill は起草で完了する。
  議論 / approve / merge は人間レビューに委ねる (Skill は merge を実行しない)。
status: active
phase: A3
related_plan: docs/harness/plan.md §A3 / §4.5
related_rules:
  - .claude/rules/adr.md
  - .claude/rules/docs-structure.md
  - .claude/rules/template-language.md
  - .claude/rules/markdown.md
related_adrs:
  - ADR-0001
  - ADR-0027
---

# adr-author

> **5 行以内 summary**: アーキテクチャ決定の ADR を `docs/adr/template.md` から起草する
> Skill。`.claude/rules/adr.md` §起票基準 (10 項目のうち 2 つ以上) を判定し、満たさない
> 決定は別記録方法 (rules / Epic decisions.md / Plan / learning / runbook) を推奨して
> 停止する。採番 + frontmatter 埋め + 本文起草 + `related_adrs` / `supersedes` /
> `superseded_by` の双方向リンク + `docs/adr/README.md` INDEX 更新まで実施し、
> Phase 6 で人間レビューに handoff する。議論 / approve / merge は人間が担当。

## 役割

- ADR 起票要求 (人間または他 Skill) の入力を受け取り、`.claude/rules/adr.md` §起票基準
  10 項目のうち 2 つ以上を満たすか判定
- 起票基準を満たさない場合は別記録方法 (rules / Epic decisions.md / Plan / learning /
  runbook) を提案し、ADR を起草せず停止
- 起票基準を満たす場合は連続 4 桁ゼロパディングで採番 (既存 ADR 一覧を走査して最大番号 + 1)
- `docs/adr/template.md` を base に `docs/adr/ADR-NNNN-<kebab-slug>.md` を起草、frontmatter +
  本文 (ステータス / コンテキスト / 決定 / 根拠 / 帰結 / §4.5 起票基準充足チェック / 関連) を埋める
- 関連 ADR の双方向リンクを解析・追加 (`related_adrs` / `supersedes` / `superseded_by`)、
  supersede 関係がある場合は対向 ADR の frontmatter にも `superseded_by` を書き込む
- `docs/adr/README.md` の一覧テーブルに新行を追加 (ADR ID / 状態 / 内容 / 起票根拠 (§4.5) /
  関連 rule / ファイル)
- 起草で本 Skill の責務完了、議論 / approve / 状態遷移 (`proposed` → `accepted`) /
  PR merge は人間レビューに委ねる

## 入力

- **起動 prompt** (人間 / 他 Skill から): 決定のタイトル候補 / コンテキスト / 採用方針 /
  関連 ADR (supersede / 参照) / 関連 SPEC / Plan / Epic
- **既存 ADR 一覧** (`docs/adr/ADR-*.md` + `docs/adr/README.md` 一覧テーブル): 採番衝突回避 /
  双方向リンクの対向 ADR 解析に使用
- **起票基準の SoT** (`.claude/rules/adr.md` §起票基準): 10 項目のうち 2 つ以上の充足判定に使用
- **テンプレ** (`docs/adr/template.md`): frontmatter / 本文構造の雛形
- **関連 docs** (任意): SPEC / Plan / Epic の本文を参照して背景情報を抽出

## 出力

- **新規 ADR ファイル**: `docs/adr/ADR-NNNN-<kebab-slug>.md`
  - frontmatter: `id` / `title` / `status: proposed` (起草時は固定) / `date` / `related_epics` /
    `related_plans` / `related_specs` / `superseded_by` / `supersedes`
  - 本文: 冒頭 5 行以内 summary / ステータス / コンテキスト / 決定 / 根拠 (比較した代替案表
    含む、該当時) / 帰結 (Positive / Negative / Neutral) / §4.5 起票基準充足チェック /
    ADR 化すべき例・すべきでない例の自己チェック / 関連
- **INDEX 更新**: `docs/adr/README.md` 一覧テーブルに新行追加
  (`| NNNN | proposed | <内容> | <起票根拠 §4.5> | <関連 rule> | [ADR-NNNN](ADR-NNNN-<slug>.md) |`)
- **対向 ADR の双方向リンク更新** (該当時): 新 ADR が `supersedes: ADR-MMMM` を宣言する場合、
  ADR-MMMM 側の frontmatter `superseded_by` を新 ADR ID に書き換え + 状態を `superseded` に更新
- **起票見送り時の出力**: ADR 起草・INDEX 更新を行わず、推奨記録方法を人間に報告

## フェーズ別動作

### Phase 1: 入力把握 + ADR 起票基準判定

- 起動 prompt から決定の核 (採用方針 / 代替案 / 影響範囲) を抽出
- `.claude/rules/adr.md` §起票基準 10 項目を逐一チェック、2 つ以上満たすか判定
- 満たさない場合は §他の記録方法 (rules / Epic decisions.md / Plan / learning / runbook) から
  最適な記録先を提案し、本 Skill は停止 (ADR ファイル / INDEX 更新は行わない)
  - 補助判定: §ADR 化見送りの理由テンプレ (3 条件: 撤回コスト低 / config N ファイル限定 /
    既存 rule 本体改定なし) に該当するなら EPIC `decisions.md` 記録を推奨
- 満たす場合は Phase 2 へ進む

### Phase 2: 採番 + テンプレ copy + frontmatter 埋め

- 既存 ADR 一覧 (`docs/adr/ADR-*.md` ファイル名 + `docs/adr/README.md` 一覧テーブル) を走査して
  最大番号を取得、+1 で新 ADR 番号を確定 (4 桁ゼロパディング、`ADR-NNNN`)
- 同番号の race 検出: 直前に他 Skill / 他セッションが採番した可能性を考慮し、確定前に
  `git status` / `git log --oneline -5 docs/adr/` で直近の差分を確認
- タイトルから kebab-case slug を導出 (動詞は省略可、要件のキーワードを含める)
- `docs/adr/template.md` を copy して `docs/adr/ADR-NNNN-<slug>.md` を作成、frontmatter の
  プレースホルダ (`ADR-NNNN` / `<タイトル>` / `YYYY-MM-DD` / `related_*` / `superseded_by` /
  `supersedes`) を実値で埋める
- `status` は `proposed` 固定 (起草時、`accepted` 遷移は人間レビュー後の別操作)

### Phase 3: 本文起草

- テンプレ §コンテキスト / §決定 / §根拠 / §帰結 を以下の指針で埋める:
  - **コンテキスト**: 決定が必要となった背景・現状の問題・制約・関連する他の決定。事実ベース、
    判断は §根拠 へ
  - **決定**: 「~を採用する」「~を撤去する」「~に統一する」等の断定的記述
  - **根拠**: 複数代替案を比較した場合は §比較した代替案 表 (代替案 / 利点 / 欠点 /
    採用しなかった理由) を埋める
  - **帰結**: Positive / Negative / Neutral の 3 区分で列挙、トレードオフを明示
- 冒頭 5 行以内 summary (`> **5 行以内 summary**: ...`) を必須記入 (`.claude/rules/docs-structure.md` §各 docs 構造)
- §4.5 起票基準充足チェック表で該当項目に `[x]` をマーク (2 つ以上充足が条件)
- §ADR 化すべき例 / すべきでない例 の自己チェックボックスにも `[x]` を付与
- 言語は日本語 (ADR 0027 / `.claude/rules/template-language.md`)、コード断片は英語 / プレーン
  テキストは ` ```text ` 言語指定 (`.claude/rules/markdown.md`)

### Phase 4: 関連 ADR 双方向リンク

- 起動 prompt + コンテキスト分析から `related_adrs` / `supersedes` / `superseded_by` を抽出
- 新 ADR の frontmatter に block 形式で記入 (要素ゼロは `[]` 明示、`docs-structure.md`
  frontmatter 規約)
- `supersedes: ADR-MMMM` を宣言する場合、対向 ADR (ADR-MMMM) の frontmatter `superseded_by`
  を新 ADR ID に書き換え + `status` を `superseded` に更新
- `related_adrs` は双方向リンクが望ましいが、対向 ADR が `accepted` 以降で immutable な場合は
  対向側を改変せず、新 ADR 側のみリンク (`.claude/rules/adr.md` §Gotchas)

### Phase 5: INDEX 更新

- `docs/adr/README.md` の一覧テーブルに新行を追加:
  `| NNNN | proposed | <内容簡潔> | <起票根拠 §4.5 の該当番号カンマ区切り> | <関連 rule のリスト> | [ADR-NNNN](ADR-NNNN-<slug>.md) |`
- テーブルは ADR 番号昇順を維持
- 統合 / 撤去履歴がある場合は §★統合の経緯 表に行追加 (該当時)
- last_updated 日付を更新

### Phase 6: 人間レビュー handoff

- 起草内容のサマリ (ADR 番号 / タイトル / 起票根拠 / 関連 ADR) を print して停止
- `status: proposed` のまま停止し、人間レビュー → `accepted` 遷移 → PR レビュー → merge は
  人間が担当
- 議論 / 修正要求があれば本 Skill を再起動して該当 ADR を更新 (`accepted` 以降の immutable
  原則に注意、§Gotchas 参照)

## Gotchas

- **起票基準を満たさない決定を ADR 化しない**: `.claude/rules/adr.md` §起票基準 10 項目のうち
  2 つ以上を満たさない場合は ADR 起草を見送り、別記録方法 (rules / Epic decisions.md /
  Plan / learning / runbook) を提案。撤回コスト低 / config N ファイル限定 / 既存 rule 本体
  改定なしの 3 条件を満たすなら §ADR 化見送りの理由テンプレ に従う (PR #129 で実績、ADR-0028
  起票を見送り EPIC-A2 `decisions.md` に記録)
- **双方向リンク漏れ防止**: `supersedes: ADR-MMMM` 宣言時は対向 ADR の `superseded_by` も同 PR
  内で更新。片方向だけ書くと A6 機械検証 (相互参照の実在チェック) で reject される
- **`superseded_by` の整合性**: 対向 ADR の `status` も `superseded` に更新が必要。`accepted`
  のまま `superseded_by` を埋めると status 語彙と矛盾 (MADR 4 状態の遷移ルール違反)
- **採番の race 注意**: 別セッション / 別 Skill が同時に採番すると番号衝突。Phase 2 で
  `git status` + `git log --oneline -5 docs/adr/` を確認、可能なら `git fetch origin master`
  で latest と同期してから採番
- **`accepted` 以降は本文を改変しない**: 既存 ADR を `accepted` から書き換える要求が来ても、
  本 Skill は新 ADR (supersede) を提案して旧 ADR は immutable に保つ。誤記訂正のみ
  `docs/adr/README.md` 側に正誤表を追記
- **status 語彙準拠**: `proposed` / `accepted` / `deprecated` / `superseded by ADR-NNNN` の
  MADR 4 状態以外を使わない (`.claude/rules/adr.md` §採番・命名・ステータス)
- **rule への双方向 SoT 宣言は書かない**: ADR 側に「`.claude/rules/<name>.md` が SoT」と書くと
  循環参照になり A6 トレーサビリティ機械検証が弱まる (`.claude/rules/adr.md` §ADR ⇄ rule の
  SoT 方向、PR #119 レトロ Try)。委譲先の明示 (「運用詳細は rule に委譲」) は OK
- **本文へのコード断片混入**: 設計書本文ではないが、ADR 本文に長いコード断片を書くと可読性が
  下がる。`file_path:line` 参照 / 30 行以内の最小例 / ` ```text ` 言語指定など
  `.claude/rules/markdown.md` の規約に従う
- **frontmatter 配列は block 形式必須**: `related_adrs: [ADR-0001, ADR-0011]` の flow 形式は
  禁止 (`.claude/rules/docs-structure.md` §配列はブロック形式を強制)。要素ゼロのみ `[]` 許容
- **title と H1 の表記方針**: frontmatter `title` ⊇ H1 (完全一致または短縮形許容)。別表現は
  NG (`.claude/rules/docs-structure.md` §title と H1 の表記方針、PR #126 レトロ Try)
- **議論 / approve / merge は人間レビュー**: 本 Skill は起草で完了。`status: proposed` →
  `accepted` 遷移は人間が議論結果を反映して別操作で行う。auto-merge は禁止 (R-15)

## 関連

- ADR-0001 (ADR 運用基準・書式・起票判断フロー、本 Skill の SoT)
- ADR-0027 (docs 構造 + 命名規約 + 5 行 summary + lazy-load + 日本語化)
- `.claude/rules/adr.md` (ADR 起票基準 + 採番規約 + status 語彙 + ADR ⇄ rule SoT 方向)
- `.claude/rules/docs-structure.md` (frontmatter 規約 / 命名規約 / 5 行 summary)
- `.claude/rules/template-language.md` (日本語必須、Phase A 経過措置)
- `.claude/rules/markdown.md` (見出し / frontmatter / code block / 表記規約)
- `docs/adr/template.md` (本 Skill が copy する雛形)
- `docs/adr/README.md` (本 Skill が更新する INDEX)
- `docs/harness/plan.md` §4.5 (ADR 判断フロー Mermaid) / §A3 (本 Skill の責務 SoT)
- `.claude/skills/plan-author/SKILL.md` (テンプレ起票責務の構造参考)
- `.claude/skills/epic-author/SKILL.md` (同上)
