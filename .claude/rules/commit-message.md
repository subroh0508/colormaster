---
id: rules-commit-message
title: コミットメッセージ規約 (Conventional Commits)
status: skeleton
last_updated: 2026-05-17
paths:
  - "scripts/install-git-hooks.sh"
  - ".git/hooks/commit-msg"
related_plan: docs/harness/plan.md §4.7
related_adrs:
  - ADR-0017
  - ADR-0027
---

# commit-message.md — コミットメッセージ規約

> Conventional Commits ベース。Renovate `extends: [:semanticCommits]` と整合し、
> 機械検証 (`scripts/install-git-hooks.sh` 配置の `.git/hooks/commit-msg`) で強制する。
> 詳細仕様は `docs/harness/plan.md` §4.7 を Single Source of Truth とする。
> **本ファイルは A2-1 で新規作成、A2-3 で本格化予定**。

## 形式

```text
<type>(<scope>): <subject>

<body>

<footer>
```

## type 一覧

| type | 用途 |
|---|---|
| `feat` | 新機能追加 (要件定義 / 基本設計 / 詳細設計に対応する実装) |
| `fix` | バグ修正 |
| `refactor` | 振る舞いを変えないコード整理 |
| `test` | テスト追加・修正のみ |
| `docs` | ドキュメント更新 (plan.md / ADR / requirements / specifications / runbooks / rules 本体) |
| `chore` | 雑務 (`.gitignore` / 依存追加なし設定変更等) |
| `build` | ビルド設定 (`build.gradle.kts`、Dockerfile) |
| `ci` | CI 設定 (`.github/workflows/*`) |
| `perf` | 性能改善 (振る舞いは維持) |
| `style` | フォーマットのみ (空白 / セミコロン / コメント整理等) |
| `revert` | revert コミット |

破壊的変更は subject 末尾に `!` を付与 (`feat(api)!: drop /v1/users endpoint`) し、body に `BREAKING CHANGE:` セクションを必ず含める。

## 各要素の規約

| 要素 | 規約 |
|---|---|
| `<scope>` | 影響範囲。Skill 名 (`roadmap-tracker`)、モジュール (`core/data`)、機能領域 (`api`, `auth`)、フェーズ ID (`A6`, `A2-1`) 等。複数横断時は省略可。空 `()` は不可 |
| `<subject>` | **英語推奨** (Phase A 期間中は日本語混在を許容、詳細は Gotchas)、現在形・命令形動詞で開始 (`Add` / `Drop` / `Fix` / `Trim` / `起草` / `消化` 等)。**72 文字以内推奨、100 文字 hard limit**、末尾ピリオドなし。固有名詞 (パス / 識別子) はそのまま |
| `<body>` | 1 行空けて記述。「何を変えたか」より「**なぜ変えたか / どんなトレードオフを選んだか**」を主軸。日本語可 (本計画の他 Markdown と整合)。72 文字で改行推奨。複数段落可 |
| `<footer>` | `Refs: PLAN-NNN / EPIC-NNN / ADR-NNNN / SPEC-NNN-N` (該当時、複数可) + `Co-Authored-By: <AI モデル名> <noreply@anthropic.com>` (AI が commit した場合必須) |

## subject 長について (A1 レトロ Problem #9 / EPIC-A2 decisions.md)

- **72 文字以内推奨、100 文字 hard limit** で運用する (A2-1 で旧 50 字 hard limit を緩和)
- 根拠: Conventional Commits 公式は subject 長を規定しない。`scope` を Skill 名 / モジュール / フェーズ ID で書くと型 + scope だけで 25-30 字消費し、現実的に意味のある subject を書くと 50 字超過が頻発する (例: `feat(roadmap-tracker): add concurrency-aware ranking` = 51 字)
- 72 字は GitHub web UI / `git log --oneline` での表示崩れ閾値 (`columns - 4` ≒ 72)。視認性は維持しつつ実用的
- **hard limit 100 字**は「サマリとして長すぎる」と判定可能な絶対上限。それ以上は body に移譲

## Co-Authored-By の必須化

- **AI 駆動コミット (Claude Code / 他 AI Coding Agent) は footer に必ず付与**:

  ```text
  Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
  ```

- 人間単独コミットでは不要。AI と人間のペアプロでも、AI がコード生成を主導した場合は付与
- 検証は `.git/hooks/commit-msg` の **将来拡張範囲** (A2-1 時点は subject 形式のみ検証、Co-Authored-By チェックは A3 / A6 で追加検討)

## 例

```text
feat(roadmap-tracker): add concurrency-aware ranking

Epic frontmatter に `expected_modules` を追加し、現在 in-progress 項目との
重複が少ない順に「次の推奨着手 (並行実装観点)」を top-N で提示する。
`expected_modules` 未記入は warning に。

Refs: EPIC-A3 / ADR 0017
Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
```

```text
docs(harness): A2-1 A1 レトロ即時消化 + ハーネス即時改善

A1 PR #117 レトロ提案 15 件のうち、rules-index / CLAUDE.md の status
正規化、template-language.md の常時ロード化、PR テンプレ harness.md
追加、mcp-usage / db-protection / commit-message Gotchas 追記、
docs/adr/README ADR 索引、flaky-tests.md などを消化。

Refs: EPIC-A2
Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
```

## 機械検証

- `commit-msg` Git hook (`scripts/install-git-hooks.sh` 配下) で **Conventional Commits パーサー** により subject 形式を検証
- 検証項目: type が allow リスト内 / scope 不在 (省略可だが空文字列は不可) / subject が 100 字以内 / 末尾ピリオド無し / 72 字超過時は warning (fail しない)
- 破壊的変更時の body 内 `BREAKING CHANGE:` 必須化は **A3 / A6 で追加検討** (現状は人間レビュー任せ)
- `Co-Authored-By` 行の AI 駆動コミット時必須化は **A3 / A6 で追加検討** (現状は人間レビュー任せ)

## Gotchas

- **`--no-verify` で commit hook をスキップしない** (R-26 同等)。hook 失敗は新規 commit で修正、`--amend` 禁止 (Co-Authored-By が消える)
- **Merge / Revert / fixup! / squash! コミットは検証スキップ** (`commit-msg` hook 冒頭で除外)
- subject 言語ポリシー:
  - **英語推奨** (Conventional Commits 公式 / Renovate 自動 PR との整合 / GitHub web UI での視認性)
  - **Phase A (A1〜A10) 期間中は日本語混在を許容** (経過措置): 過去コミット `feat(harness): A1 ADR 0001-0027 一括起草` / `feat(harness): A2-1 A1 レトロ即時消化` 等の実体と整合。本 rule は Phase A の期間中、subject 中の英語動詞 (`add` / `drop` / `fix` 等) の代わりに日本語動詞・名詞 (`〜起草` / `〜消化` / `〜本格化` 等) を含むことを許容する
  - **Phase B (A6 で `commit-msg` hook 拡張時) に英語強制を機械検証で本格化** する判断は A6 着手時に再評価。一律強制が現実的でなければ、`docs(harness)` / `feat(harness)` の `harness` scope のみ日本語可など段階導入も検討
- body は日本語可。AI 駆動の場合は body にも「なぜこの判断にしたか」を残すこと (人間レビュアーが PR description だけで完結しない場合の補助情報)
- subject 長 50 → 72/100 字緩和は A2-1 マージ時に `scripts/install-git-hooks.sh` も同時更新済 (整合性確保)

## 関連

- `docs/harness/plan.md` §4.7 (Single Source of Truth)
- `scripts/install-git-hooks.sh` (commit-msg hook 実装)
- ADR 0017 (ローカル Claude Code ポーリング駆動、AI コミットの位置付け)
- ADR 0027 (テンプレート言語、subject は英語固定の例外)
- `.claude/rules/{branch-naming,pr-template}.md` (A2-3 で本格化)
- EPIC-A2 `decisions.md` (subject 長 50 → 72/100 緩和の判断記録)
