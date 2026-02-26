#!/usr/bin/env bash
set -euo pipefail

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI not found. Use .autonomy/outbox PR draft markdown instead."
  exit 0
fi

if ! gh auth status >/dev/null 2>&1; then
  echo "gh auth not configured. Use .autonomy/outbox PR draft markdown instead."
  exit 0
fi

TITLE="${1:-chore: autonomous incremental improvement}"
BODY_FILE="${2:-}"

if [[ -n "$BODY_FILE" && -f "$BODY_FILE" ]]; then
  gh pr create --title "$TITLE" --body-file "$BODY_FILE"
else
  gh pr create --title "$TITLE" --fill
fi
