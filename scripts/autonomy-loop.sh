#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

TASK="$(scripts/pick-task.sh)"
STAMP="$(date +%Y%m%d-%H%M%S)"
BRANCH="autonomy/${STAMP}"

scripts/safe-autonomy.sh "git checkout -b ${BRANCH}"

cat > .autonomy/outbox/pr-${STAMP}.md <<PR
# PR Draft

## Task
${TASK}

## Summary of changes
- _Fill after implementation_

## Rationale
- Keep changes small, reversible, and PR-focused.

## Tests
\`\`\`
./gradlew verify
\`\`\`

## Risk notes
- Scoped to repository only.
- No destructive operations.
PR

scripts/safe-autonomy.sh "./gradlew verify"

echo "Prepared branch ${BRANCH} and draft PR at .autonomy/outbox/pr-${STAMP}.md"
