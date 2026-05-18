# archived/

引退した Skill を移動するディレクトリ。

- `harness-bootstrap` は A3 完了 (A3-14、2026-05-18) で archived 化、本格 Skill 群へ移行 (`docs/harness/plan.md` §6.2 A3 / R-6)
- ここに置かれた Skill は CLAUDE.md からの参照も外す
- 将来再利用する可能性がある場合は archived 配下に残し、`status: archived` を SKILL.md frontmatter に明記

## 一覧

| Skill | archived 日 | 撤去 PR | 撤去理由 (要約) | 代替先 |
|---|---|---|---|---|
| `harness-bootstrap` | 2026-05-18 | A3-14 | A3 で専用 Skill 群 13 件が出揃い、汎用起票・起草の責務が全て移行済 | `feature-request` / `bug-fix` / `refactor` / `adr-author` / `harness-meta` / `harness-evolution` / `dependency-upgrade` / `implementation-workflow` / `code-reviewer` / `pr-retrospective` / `pr-poller` / `roadmap-tracker` / `ui-snapshot` |
