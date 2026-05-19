#!/usr/bin/env bash
# Claude Code Docker CLI 分離 wrapper
# PLAN-007: macOS Docker Desktop credsStore: "desktop" の Claude Code 非対話的 shell hang 回避
# 詳細: docs/runbooks/claude-code-docker-setup.md / .claude/rules/docker-cli.md
set -euo pipefail
export DOCKER_CONFIG="${DOCKER_CONFIG:-$HOME/.docker-claude}"
exec docker "$@"
