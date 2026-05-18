# Minecraft Study Desktop App

Tauri desktop application for launching the Minecraft Study participant app.

This folder contains the desktop application shell. It is developed with Tauri, npm, vanilla JavaScript, and Rust.

## Scope

The desktop app is responsible for the participant-facing local application.

The intended participant flow is:

```text
download app -> open app -> read briefing -> start Minecraft -> play study -> app collects logs -> app uploads encrypted logs -> app opens questionnaire
```

The app itself opens as a normal desktop window. Minecraft runs fullscreen through the game/mod configuration.

Participants should not need to install Java, Python, Rust, Node.js, Gradle, or Minecraft modding tools.

## Folder structure

```text
desktop/
  README.md
  package.json
  package-lock.json
  src/
  src-tauri/
    Cargo.toml
    tauri.conf.json
    build.rs
    capabilities/
    icons/
    resources/
    src/
  scripts/
    clean-payload.mjs
    prepare-payload.mjs
```

Generated build output, payloads, installers, logs, and private configuration files should not be committed.

## Development environment and dependencies

The table below documents the main software used for this desktop app.

| Component          | Version / value                           | Notes                                                    |
| ------------------ | ----------------------------------------- | -------------------------------------------------------- |
| Desktop framework  | Tauri 2                                   | Cross-platform desktop app shell                         |
| Frontend           | Vanilla JavaScript                        | No frontend framework is currently used                  |
| Package manager    | npm                                       | Used for Tauri CLI dependency management                 |
| Rust toolchain     | Stable MSVC toolchain                     | Used by the Tauri backend                                |
| JavaScript runtime | Node.js                                   | Used during development only                             |
| Java               | JDK 25                                    | Used for Minecraft/Fabric development and packaging work |
| WebView runtime    | Microsoft Edge WebView2                   | Required for Tauri on Windows                            |
| C++ build tools    | Microsoft C++ Build Tools / Visual Studio | Required for Rust/Tauri builds on Windows                |

## Optional VS Code extension

We use the **Tauri VS Code Extension** during development.

This extension is optional. It is useful for:

* Tauri command shortcuts;
* `tauri.conf.json` validation;
* autocompletion for Tauri configuration.

The app can still be built and run from the terminal without the extension.

## Get started for developers

This section assumes that you have already opened the repository in your editor and are working from the repository root.

### 1. Check required tools

```powershell
node -v
npm -v
rustc -V
cargo -V
java -version
javac -version
```

### 2. Install desktop app dependencies

From this folder:

```powershell
cd desktop
npm install
```

Run `npm install` again after editing `package.json` so that `package-lock.json` stays in sync.

### 3. Prepare the standalone runtime payload

The release payload is assembled at build time from the repository source. The script runs the Gradle/Fabric build in `mods/custom/`, copies the prepared Minecraft runtime folder, copies the study world and external runtime jars, creates a portable Java launch argfile, and copies a participant Java runtime into the packaged payload.

Participant installs should not require Java, Python, Rust, Node.js, Gradle, the source repository, or Minecraft modding tools. The MCID and condition assignment are still created at runtime for each participant run.

Target-specific participant Java runtime variables:

```bash
JAVA_RUNTIME_WIN_X64=...
JAVA_RUNTIME_MAC_ARM64=...
JAVA_RUNTIME_MAC_X64=...
```

`BUILD_JAVA_HOME` may be set separately when the JDK used to build the Fabric mod differs from the participant Java runtime that will be bundled. If omitted, the script falls back to `JAVA_HOME`.

For Windows x64, run on Windows:

```powershell
npm run payload:clean
npm run payload:prepare:win
```

For macOS Apple Silicon, run on a Mac:

```bash
npm run payload:clean
npm run payload:prepare:mac-arm64
```

For macOS Intel, run on a Mac:

```bash
npm run payload:clean
npm run payload:prepare:mac-x64
```

You can also pass the participant runtime directly:

```bash
npm run payload:prepare:mac-arm64 -- --jre "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
```

### 4. Run the Tauri app locally

Local runs use the same packaged payload path as release builds. Prepare the payload first, then run the app.

From `desktop/`:

```powershell
npm run app:dev:win
```

or on macOS Apple Silicon:

```bash
npm run app:dev:mac
```

### 5. Build an installer/package

For Windows x64, run on Windows:

```powershell
npm run app:build:win
```

For macOS Apple Silicon, run on macOS:

```bash
rustup target add aarch64-apple-darwin
npm run app:build:mac-arm64
```

For macOS Intel, run on macOS:

```bash
rustup target add x86_64-apple-darwin
npm run app:build:mac-x64
```

macOS packages are generated on a Mac. One properly configured Mac can build both Mac targets, but both outputs should still be tested on matching clean target machines before study deployment. Generated installers/packages should not be committed.

### 6. Inspect the Tauri environment

From `desktop/`:

```powershell
npm run tauri -- info
```

This should report the available Tauri, Rust, Node/npm, WebView2, and MSVC environment.

## Naming rule

Do not use `checkpoints` in participant-facing app names, package names, window titles, installer names, or generated runtime names.

Reason: checkpoints/pause design is part of the study manipulation and should not be exposed unnecessarily to participants.

Use:

```text
minecraft-study
Minecraft Study
io.github.u01234567.minecraftstudy
```

Avoid:

```text
study-checkpoints
Study Checkpoints
studycheckpoints
```

Internal Java package names or mod IDs may still contain `studycheckpoints` if they are not participant-facing.

## Privacy and generated files

Do not commit:

* `.env`;
* production URLs if generated into config files;
* private encryption keys;
* participant logs;
* encrypted upload packages;
* generated payloads;
* installers;
* build outputs.

## Licences

This desktop app is part of the larger Minecraft Study repository.

The root repository contains the main project licence for original project code and materials, unless otherwise noted.

Tauri is dual-licensed under:

```text
Apache-2.0 OR MIT
```

In this project, the Tauri dependency licence metadata can be found in:

```text
desktop/package-lock.json
```

After running `npm install`, the installed Tauri package files can also be inspected under:

```text
desktop/node_modules/@tauri-apps/cli/
```

Third-party dependencies, external mods, assets, tutorials, and model sources may have their own licences and attribution requirements. Keep those records in:

```text
../mods/external/README.md
```

Do not copy third-party licence text into this README. Add or copy licence files only when a dependency, asset, runtime, or external creator requires that the licence text be included with redistribution.