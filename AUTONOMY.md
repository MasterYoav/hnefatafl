# Autonomous PR-only Improvement Loop

Repository: `MasterYoav/hnefatafl`

## Goal
Continuous small, safe improvements delivered as PRs only.

## Loop
1. Pick one small task from `TODO.md` / `ROADMAP.md` / issues.
2. Create branch `autonomy/<timestamp>`.
3. Implement minimal scoped change.
4. Run `./gradlew verify`.
5. Prepare PR body including:
   - diff summary
   - rationale
   - test output
   - risk notes
6. Open PR (if `gh` auth exists) or store draft in `.autonomy/outbox/`.
7. Human review over Telegram; merge only after approval.

## Scripts

- `scripts/safe-autonomy.sh '<command>'` — safe execution wrapper
- `scripts/pick-task.sh` — picks next small task
- `scripts/autonomy-loop.sh` — end-to-end local PR-prep flow
- `scripts/open-pr.sh` — optional PR open via gh CLI

## Safety constraints

- Repo confinement enforced by wrapper.
- Risky shell patterns blocked by default.
- All actions logged to `logs/autonomy.log`.
- No privileged tokens required for scheduled checks.
