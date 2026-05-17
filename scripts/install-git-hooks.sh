#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# install-git-hooks.sh
#
# .git/hooks/commit-msg と .git/hooks/pre-commit を配置する。
#   - commit-msg : Conventional Commits 形式の subject 検証 (§4.7)
#   - pre-commit : ./gradlew check の実行 (A6 で本格化、B0 時点は雛形のみ)
#
# 使い方:
#   ./scripts/install-git-hooks.sh
#
# B0 時点では commit-msg のみ最小実装。pre-commit は A6 で本格化。
# ----------------------------------------------------------------------------
set -euo pipefail

# git worktree 配下でも正しく hooks ディレクトリを解決するため、
# --git-path hooks を使う (worktree の .git はファイルになるため
# 単純な .git/hooks 連結では失敗する)。
HOOKS_DIR="$(git rev-parse --git-path hooks)"

if [[ ! -d "${HOOKS_DIR}" ]]; then
  echo "Error: ${HOOKS_DIR} does not exist. Are you in a git repository?" >&2
  exit 1
fi

# ----------------------------------------------------------------------------
# commit-msg: Conventional Commits 検証 (§4.7)
#
# 形式: <type>(<scope>): <subject>
#   - type allow リスト: feat / fix / refactor / test / docs / chore /
#                        build / ci / perf / style / revert
#   - 破壊的変更は <type>(<scope>)!: <subject>
#   - subject は英語・現在形・命令形動詞で開始、**72 字推奨 / 100 字 hard limit**、末尾ピリオドなし
#     (A2-1 で旧 50 字制限を緩和、EPIC-A2 decisions.md 参照)
#   - scope は省略可だが空文字列 () は不可
# ----------------------------------------------------------------------------
cat > "${HOOKS_DIR}/commit-msg" <<'COMMIT_MSG_HOOK'
#!/usr/bin/env bash
# Conventional Commits 形式の commit subject 検証 (§4.7)
set -euo pipefail

COMMIT_MSG_FILE="$1"
SUBJECT="$(head -n 1 "${COMMIT_MSG_FILE}")"

# Merge commit / fixup / squash はスキップ
case "${SUBJECT}" in
  Merge*|Revert*|fixup!*|squash!*) exit 0 ;;
esac

# Conventional Commits パターン
# <type>(<scope>)!?: <subject>  または  <type>!?: <subject>
PATTERN='^(feat|fix|refactor|test|docs|chore|build|ci|perf|style|revert)(\([a-zA-Z0-9_./-]+\))?!?: .+'

if [[ ! "${SUBJECT}" =~ ${PATTERN} ]]; then
  cat >&2 <<EOF
Error: commit subject が Conventional Commits 形式に違反しています。

入力: "${SUBJECT}"

形式: <type>(<scope>): <subject>
  type 一覧: feat / fix / refactor / test / docs / chore / build / ci / perf / style / revert
  scope は省略可だが、書く場合は空文字列禁止
  破壊的変更は <type>(<scope>)!: <subject> + body に "BREAKING CHANGE:"

例:
  feat(roadmap-tracker): add concurrency-aware ranking
  fix(core/network): handle 401 retry boundary
  docs(adr): add ADR 0023 about UI freezing
  refactor(feature/home)!: drop legacy ViewModel

詳細: .claude/rules/commit-message.md / docs/harness/plan.md §4.7
EOF
  exit 1
fi

# subject 長: 72 字推奨 / 100 字 hard limit
# (A2-1 で旧 50 字制限を緩和、Conventional Commits 公式は subject 長を規定しない、
#  EPIC-A2 decisions.md 参照)
if [[ ${#SUBJECT} -gt 100 ]]; then
  cat >&2 <<EOF
Error: commit subject が 100 文字を超えています (${#SUBJECT} 文字、hard limit)。

入力: "${SUBJECT}"

100 文字以内に収め、詳細は body (空行を挟んで 72 文字推奨) に書いてください。
詳細: .claude/rules/commit-message.md / docs/harness/plan.md §4.7
EOF
  exit 1
fi

if [[ ${#SUBJECT} -gt 72 ]]; then
  cat >&2 <<EOF
Warning: commit subject が 72 文字を超えています (${#SUBJECT} 文字、推奨を超過)。

入力: "${SUBJECT}"

GitHub web UI / git log --oneline での視認性を考慮し 72 文字以内を推奨。
詳細は body (空行を挟んで 72 文字推奨) に書いてください。
詳細: .claude/rules/commit-message.md / docs/harness/plan.md §4.7
EOF
  # 72 字超過は warning のみで継続 (fail しない)
fi

# 末尾ピリオド禁止
if [[ "${SUBJECT}" =~ \.$ ]]; then
  cat >&2 <<EOF
Error: commit subject の末尾にピリオドを付けないでください。

入力: "${SUBJECT}"

詳細: .claude/rules/commit-message.md / docs/harness/plan.md §4.7
EOF
  exit 1
fi

exit 0
COMMIT_MSG_HOOK

chmod +x "${HOOKS_DIR}/commit-msg"
echo "Installed: ${HOOKS_DIR}/commit-msg"

# ----------------------------------------------------------------------------
# pre-commit: ./gradlew check 実行 (A6 で本格化、B0 時点は雛形のみ)
# ----------------------------------------------------------------------------
cat > "${HOOKS_DIR}/pre-commit" <<'PRE_COMMIT_HOOK'
#!/usr/bin/env bash
# pre-commit: ./gradlew check 実行 (A6 で本格化)
#
# B0 時点では雛形のみ。Spotless / ktlint / detekt / Konsist / markdownlint-cli2 /
# Gradle カスタムタスク (Markdown 検証) の集約は A6 で完成する。
# 一時的にスキップしたい場合は git commit --no-verify (利用は最小限に)。
set -euo pipefail

# A6 で完成: ./gradlew check が Spotless + ktlint + detekt + Konsist + markdownlint
#            + Gradle カスタムタスク (Markdown 検証) を一括実行
#
# 現在は雛形のため何もしない。
echo "pre-commit hook: (skeleton — A6 で ./gradlew check を本格化、現在は no-op)"
exit 0
PRE_COMMIT_HOOK

chmod +x "${HOOKS_DIR}/pre-commit"
echo "Installed: ${HOOKS_DIR}/pre-commit (skeleton)"

echo ""
echo "Done. .git/hooks/commit-msg と .git/hooks/pre-commit を配置しました。"
echo ""
echo "次のステップ:"
echo "  1. 試しに git commit -m \"hello\" すると commit-msg が拒否することを確認"
echo "  2. git commit -m \"docs(harness): add bootstrap notes\" のような形式なら成功"
echo "  3. A6 で pre-commit が ./gradlew check を実行するように本格化"
