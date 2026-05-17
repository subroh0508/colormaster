# AGENTS.md

> **5 行以内 summary**: Claude Code 以外の AI Coding Agent (Codex / Cursor / Cline / OpenCode 等)
> が ColorMaster で作業する場合のエントリポイント。**本リポジトリのハーネスは Claude Code に
> 最適化** されており、`.claude/` 配下の Skill / rules / MCP 設定をそのまま流用するのは難しい。
> 他 Agent からの利用は **限定的** とし、`CLAUDE.md` と `docs/harness/plan.md` を参照しながら
> 自プラットフォームの作法に翻訳して使う。

## 本リポジトリでの方針

- **Single Source of Truth は Claude Code 向け** (`CLAUDE.md` / `.claude/skills/` / `.claude/rules/`)
- 他 AI Agent からは **読み取り中心** で利用 (rules / Plan / Epic / ADR を参考にして実装提案する)
- **書き込み (PR 起票 / コミット) も技術的には可能** だが、Conventional Commits 検証 (Git hook) と PR テンプレート (`.github/PULL_REQUEST_TEMPLATE/`) には準拠する必要がある

## 他 AI Agent からの利用手順 (推奨)

1. `CLAUDE.md` を読む (lookup table / グローバルルール)
2. `docs/README.md` を読む (推奨読み順)
3. `docs/harness/plan.md` を読む (設計指針 / フェーズ)
4. タスク種別に応じて `docs/{requirements,specifications,adr,plans,epics}/` から関連ファイルを読む
5. 実装時は `.claude/rules/rules-index.md` から該当 rule を引いて従う
6. コミットは Conventional Commits (`.claude/rules/commit-message.md` / `scripts/install-git-hooks.sh` 経由)
7. PR は `.github/PULL_REQUEST_TEMPLATE/<type>.md` を選択

## ハーネス再現は別 Plan で

他 AI Agent (Codex / Cursor 等) で同等のハーネスを portable に動かしたい場合は、
**ADR 0024 の将来検討に該当**: Serena MCP 等の汎用 MCP 採用を別 Plan で再評価する (R-27)。

## 関連

- `CLAUDE.md` (Claude Code 用エントリポイント / lookup table)
- `docs/harness/plan.md`
- `docs/README.md`
- ADR 0024 (MCP サーバ採用、他クライアントへの portability)
