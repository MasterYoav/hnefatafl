# hnefatafl (Vikings Chess Next)

Kotlin Compose Multiplatform foundation for a modern Vikings Chess app, managed in:

- https://github.com/MasterYoav/hnefatafl

## Modules

- `shared/logic` — pure Kotlin game logic (11x11 board prototype)
- `shared/ui` — Compose Multiplatform board UI + view model
- `app-desktop` — runnable desktop target
- `app-android` — Android scaffold target
- iOS targets scaffolded in KMP modules (`iosX64`, `iosArm64`, `iosSimulatorArm64`)

## Quick start

### macOS

1. Install Java 17+ (Temurin recommended).
2. From repo root:

```bash
./gradlew verify
./gradlew :app-desktop:run
```

If `gradlew` is not executable:

```bash
chmod +x gradlew
```

### Windows (PowerShell)

1. Install Java 17+ (Temurin recommended).
2. From repo root:

```powershell
.\gradlew.bat verify
.\gradlew.bat :app-desktop:run
```

### Notes

- First run may take longer while Gradle downloads dependencies.
- If `JAVA_HOME` is missing on Windows, set it to your JDK path and reopen terminal.

## Architecture

- `GameEngine` encapsulates game state transitions.
- `BoardViewModel` adapts engine state for UI interactions.
- UI renders an 11x11 grid with selectable placeholders (A/D/K).

## Quality gates

- Unit tests in `shared/logic`, `shared/ui`
- Integration-style boundary test in `shared/logic`
- Ktlint + Detekt
- Compile/test checks unified under `./gradlew verify`

## Autonomous workflow

See [AUTONOMY.md](AUTONOMY.md).
