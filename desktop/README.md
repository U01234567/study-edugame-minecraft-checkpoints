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

### 3. Prepare the runtime payload

The payload scripts are Node scripts so they can run on both Windows and macOS.

For Windows x64:

```powershell
npm run payload:clean
npm run payload:prepare:win
```

For macOS Apple Silicon, run this on the Mac:

```bash
npm run payload:clean
npm run payload:prepare:mac
```

If `JAVA_HOME` is not set, pass the Java runtime explicitly:

```bash
npm run payload:prepare:mac -- --jre "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
```

### 4. Run the Tauri development app

From `desktop/`:

```powershell
npm run dev
```

To clean, prepare the Windows payload, and start the development app in one step:

```powershell
npm run app:dev:win
```

On macOS Apple Silicon:

```bash
npm run app:dev:mac
```

### 5. Build an installer/package

For Windows x64:

```powershell
npm run app:build:win
```

For macOS Apple Silicon, run this on the Mac:

```bash
npm run app:build:mac
```

Generated installers/packages should not be committed.

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