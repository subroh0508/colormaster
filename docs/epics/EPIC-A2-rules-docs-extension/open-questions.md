---
id: open-questions-EPIC-A2
title: EPIC-A2 未解決事項
status: living
last_updated: 2026-05-17
source_epic: EPIC-A2
---

# EPIC-A2 未解決事項

> **5 行以内 summary**: EPIC-A2 内の細粒度な保留事項。重要な決定に昇格したら ADR に
> 移行 (`docs/adr/`)、解決したら本ファイルに線引きして `decisions.md` に転記。
> append-only 運用とし、過去の問いを削除しない。

## 未解決一覧

| 起票日 | 内容 | 暫定方針 | 解決状態 | 解決時の移行先 |
|---|---|---|---|---|
| 2026-05-17 | `.claude/rules/firebase-boundary.md` (B0 で rules-index.md に名前あり) を `no-firebase.md` に改名するか、両方残すか | A2-2 で `firebase-boundary.md` を新規作成 (旧 import 検出ルール) + `no-firebase.md` を新規作成 (Phase A 以降の禁止規約) の二段運用を採用。改名は A3 で再評価 | 未解決 | — |
| 2026-05-17 | `commit-message.md` の subject 長制約を 50 字 / 72 字 / 制限なし のどれにするか (A1 レトロ Problem #9) | A2-1 で 72 字推奨 + 制限なしで採用 (Conventional Commits 公式は subject 長を規定しない)。`scripts/install-git-hooks.sh` の commit-msg hook を 72 字に緩和、または subject 長検証を外す方針を `commit-message.md` に明文化 | 未解決 | A2-1 で確定 |
| 2026-05-17 | `template-language.md` を真の常時ロードにするか、rules-index 側を「Markdown 全域」表記に分離するか (A1 レトロ Problem #2) | A2-1 で **paths を削除して真の常時ロード化** を採用 (CLAUDE.md「常時ロード」宣言と整合させやすい) | 未解決 | A2-1 で確定 |

## 解決済 (過去ログ)

| 起票日 | 内容 | 解決日 | 解決方法 | 移行先 |
|---|---|---|---|---|
