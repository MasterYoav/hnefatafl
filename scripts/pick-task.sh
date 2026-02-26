#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

for file in TODO.md ROADMAP.md; do
  if [[ -f "$file" ]]; then
    task="$(grep -E '^- \[ \]|^- TODO:|^- ' "$file" | head -n 1 || true)"
    if [[ -n "$task" ]]; then
      echo "$task"
      exit 0
    fi
  fi
done

echo "TODO: improve tests or docs incrementally"
