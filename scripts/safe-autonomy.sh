#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_FILE="$REPO_ROOT/logs/autonomy.log"
ALLOW_RISKY="${ALLOW_RISKY:-0}"

mkdir -p "$(dirname "$LOG_FILE")"

audit() {
  printf '[%s] %s\n' "$(date -u +"%Y-%m-%dT%H:%M:%SZ")" "$*" | tee -a "$LOG_FILE"
}

ensure_repo_scope() {
  local current
  current="$(pwd)"
  if [[ "$current" != "$REPO_ROOT"* ]]; then
    audit "BLOCKED: attempted run outside repo ($current)"
    echo "Refusing to run outside repo root: $REPO_ROOT" >&2
    exit 2
  fi
}

contains_risky() {
  local cmd="$*"
  local patterns=(
    "rm -rf" "sudo rm" "chmod -R" "chown -R" "mkfs" "dd if="
    "security find-generic-password" "gh auth login" "brew install" "npm login"
    "docker system prune -a" "git push --force"
  )
  for p in "${patterns[@]}"; do
    if [[ "$cmd" == *"$p"* ]]; then
      return 0
    fi
  done
  return 1
}

run_cmd() {
  ensure_repo_scope
  local cmd="$*"
  if contains_risky "$cmd" && [[ "$ALLOW_RISKY" != "1" ]]; then
    audit "BLOCKED risky command: $cmd"
    echo "Blocked risky command. Re-run with ALLOW_RISKY=1 only after review." >&2
    exit 3
  fi
  audit "RUN: $cmd"
  bash -lc "$cmd" 2>&1 | tee -a "$LOG_FILE"
}

if [[ $# -eq 0 ]]; then
  echo "Usage: scripts/safe-autonomy.sh '<command>'"
  exit 1
fi

cd "$REPO_ROOT"
run_cmd "$*"
