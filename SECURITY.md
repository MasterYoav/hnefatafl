# Security & Credentials

This repository is managed with **least privilege** automation.

## Token policy

- Never commit real secrets.
- Use short-lived tokens where possible.
- Keep Telegram and GitHub tokens separate.

## Telegram

- Use a dedicated bot token only.
- Rotate token immediately if exposed.

## GitHub

Prefer a **fine-grained PAT** with access only to `MasterYoav/hnefatafl` and permissions limited to:
- Pull requests: Read/Write
- Contents: Read/Write (required for branch push)
- Metadata: Read

Avoid broad org/account scopes. Do not grant admin/delete scopes.

## Local safety wrapper

Use `scripts/safe-autonomy.sh` for autonomous actions:
- confines execution to this repository
- blocks risky shell patterns by default
- logs activity to `logs/autonomy.log`
