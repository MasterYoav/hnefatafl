# Hnefatafl

Modern **Hnefatafl (Vikings Chess)** built with OpenClaw.

<p align="center">
  <img src="assets/icon-hnefatafl-tahoe26.svg" alt="Hnefatafl Icon" width="180" />
</p>

[![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?logo=kotlin&logoColor=white)](#)
[![ChatGPT](https://custom-icon-badges.demolab.com/badge/ChatGPT-74aa9c?logo=openai&logoColor=white)](#)
---

## Game Overview

Hnefatafl is an asymmetric Norse strategy game:
- **Blue Team (Defenders)** protects the King.
- **Red Team (Attackers)** attempts to capture the King.
- The King wins by escaping to a corner.

This implementation targets a modern "Tahoe 26" macOS-like visual style with responsive board scaling and configurable themes.

---

## Tech Stack

```text
Language: Kotlin 2.1
UI: Compose Multiplatform (Desktop)
Architecture: Shared game logic + shared UI + desktop host
Build: Gradle Kotlin DSL
Quality: ktlint + detekt + unit/integration tests
Packaging: Compose Native Distributions (DMG / EXE / DEB)
CI/CD: GitHub Actions release workflow
```

---

## Rules Implemented

- 11x11 board with correct initial spawn layout
- Orthogonal movement (rook-like), no jumping
- Only King may enter corner squares
- Pawn capture by sandwiching (including edge capture rule in this project)
- King escape win: reaches any corner
- Attacker win: king surrounded on 4 sides
- Turn-based flow + undo + match win counters

---

## Install & Play (No Java Required for End Users)

For players, use packaged releases from GitHub Releases:
- **macOS:** `.dmg`
- **Windows:** `.exe` / `.msi`
- **Linux:** `.deb`

These bundles include required runtime components.

### Download
1. Open GitHub Releases for this repo.
2. Download the installer for your OS.
3. Install and launch `Hnefatafl`.

---

## Developer Setup

```bash
./gradlew verify
./gradlew :app-desktop:run
```

### Build native package for your current OS

```bash
./gradlew :app-desktop:packageDistributionForCurrentOS
```

Output is generated under:
`app-desktop/build/compose/binaries/main/`

---

## Release 1.0 Workflow

Tag-based release is configured via GitHub Actions (`.github/workflows/release-desktop.yml`).

```bash
git tag v1.0.0
git push origin v1.0.0
```

This builds platform-native installers and attaches them to the GitHub Release.

---

## Repository Structure

- `shared/logic` — rules engine + tests
- `shared/ui` — shared Compose UI + settings system
- `app-desktop` — desktop window host, native integrations (file/color pickers, window controls)
- `app-android` — Android scaffold

---

## Status

**Current milestone:** `v1.0 release candidate`

Core gameplay, modern UI, customization, and desktop packaging are in place.
