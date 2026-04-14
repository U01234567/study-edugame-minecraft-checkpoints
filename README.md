# study-edugame-minecraft-checkpoints

Repository for an educational-games study on checkpoints in Minecraft.

This repository contains:

* the Minecraft world used in the study
* the mods used for the study setup and logging
* the files and scripts used to run the world locally
* the statistics scripts used to analyse study results

This repository does **not** contain resulting datasets from the study.

## Scope

The repository preserves the materials, code, runtime dependencies, and documented external references required to reproduce the study setup and analysis workflow for this experiment.

## Repository structure

* `world/` — Minecraft world files for the study environment
* `mods/custom/` — Fabric-based custom study mod source code
* `mods/external/` — third-party runtime jars, external creature source/reference assets, and source credits
* `server/` — local server setup and scripts for running the world
* `analysis/` — scripts for statistical analysis
* `materials/` — screenshots of the world and mods

## External sources and credits

This study uses third-party mods, tools, tutorials, external creative works, and other source materials.

The main credit and provenance register is here:

* [`mods/external/README.md`](mods/external/README.md)

That file should be treated as the source of truth for:

* third-party runtime jars
* external creature model sources
* creature-source provenance and attribution
* adapted buildings, schematics, and world ideas
* tutorials, workflows, and general technical references

## Development environment and core dependencies

The table below documents the main software and versions used during development of this repository.

| Component | Version / value                                | Notes |
|---|------------------------------------------------|---|
| Operating system during development | Windows 11                                     | Primary development environment |
| Java (JDK) | Eclipse Temurin JDK 25.0.2+10-LTS              | Used for Gradle and mod development |
| IntelliJ IDEA | 2026.1                                         | Main IDE for mod development |
| Minecraft Launcher | v3.29.53-2.5.2                                 | Used for local development and testing |
| Minecraft | 26.1                                           | Project target version |
| Fabric Loader | 0.18.5                                         | Project dependency |
| Fabric API | 0.144.3+26.1                                   | Project dependency |
| Fabric Loom | 1.15.5                                         | Build tooling configured in the project |
| WorldEdit (Fabric) | `worldedit-fabric-mc26.1-7.4.2-SNAPSHOT-dist.jar` | Local development dependency |
| Python | Python 3                                       | Required for automatic and manual local session summary generation |
| Blockbench | 5.1.1                                          | Used to inspect and prepare creature source assets |

## Get started for developers

This section is intended for contributors who want to build, run, or modify the study setup locally.

### 1. Install Java (JDK)

To work on the Fabric-based mod code in this repository, install a Java Development Kit (JDK).

Check whether Java and the Java compiler are available:

```bash
java -version
javac -version
```

If either command is missing, or if the reported version does not match this project’s setup, install a JDK. A separate JRE is not required.

For this repository, we use:

* **Eclipse Temurin JDK 25.0.2+10-LTS**

### 2. Install IntelliJ IDEA

For mod development in this repository, we use:

* **IntelliJ IDEA 2026.1**

Important: IntelliJ can use a different Java runtime for Gradle than for the project itself. Make sure that both the **Project SDK** and the **Gradle JVM** are set to the installed JDK version above.

If Gradle sync fails with an error stating that the build is using an older Java version, change the Gradle JVM to **Temurin 25** or another compatible **JDK 25** installation.

### 3. Install Minecraft Launcher

If not already installed, install the official Minecraft Launcher.

From this point onward, we assume that:

* you have a valid Minecraft license
* you are signed in to Minecraft Launcher with the Microsoft account used for development and testing

This repository was developed and tested with:

* **Minecraft Launcher v3.29.53-2.5.2**
* **Minecraft version 26.1**

### 4. (Once, at start of study) Generate the Fabric mod template

The Fabric mod scaffold used for this project was generated from Fabric’s official template generator:

* [https://fabricmc.net/develop/template/](https://fabricmc.net/develop/template/)

Recommended template values:

* **Mod Name:** `Study Checkpoints`
* **Mod ID:** `study-checkpoints`
* **Package Name:** `<extension>.<your-domain>.studycheckpoints`
* **Minecraft version:** `26.1`

Advanced options used:

* disabled — **Kotlin Programming Language**
* enabled — **Data Generation**
* disabled — **Split client and common sources**
* disabled — **Kotlin Build Script**

*Note.* Keep the Java package name short and stable. The repository name is intentionally descriptive, but the Java package should remain concise for maintainability.

### 5. Clone the repository and open the mod project

Clone this repository locally, then open the Fabric mod project in IntelliJ IDEA from:

* `mods/custom/`

Wait until Gradle initialisation and project import have finished.

### 6. External runtime jars

Development runs use local third-party jars stored under:

```text
mods/external/libs/
```

At the moment this includes:

* WorldEdit

These jars are copied automatically into:

```text
mods/custom/run/mods/
```

when the development client is started.

Important:

* do **not** treat `mods/custom/run/` as the source of truth
* do **not** manually maintain long-term dependency copies only inside `run/mods/`
* place managed third-party runtime jars in `mods/external/libs/`

### 7. Run the Minecraft client for development

From `mods/custom/`, run the Fabric development client with:

```powershell
.\gradlew.bat runClient
```

For normal development runs, `runClient` on its own is sufficient. You can also use `clean runClient` when you explicitly want a fresh Gradle build state first. 

At the end of a study session, the mod also tries to generate the latest local HTML session summary automatically by running:

```text
analysis/main.py sum_last
```

That automatic summary step requires Python 3 to be available on your machine. The code currently tries these launcher commands in order:

* on Windows: `py -3`, then `python`, then `python3`
* on non-Windows systems: `python3`, then `python`

If you use a virtual environment, activate it before starting Gradle so that the intended Python interpreter is the one those commands resolve to. Or use:

```powershell
.\gradlew.bat runClient -PstudyPythonCommand="<path-to-your-python.exe>"
```

On the first run after cloning the repository, when no `mods/custom/run/` folder exists yet, Minecraft may still show its normal opening screen once so that local client settings such as sound and narrator can be adjusted. This should not happen again on later runs, because the generated runtime files will then already exist.

If `gradlew.bat` reports that Gradle is using the wrong Java version, verify that your terminal environment, `JAVA_HOME`, Project SDK, and Gradle JVM all point to the installed JDK version above.

## Local analysis

The latest local session summary is generated automatically when a study session ends, provided that Python 3 is available as described above.

To regenerate the latest local session summary manually, run from `analysis/`:

```bash
python main.py sum_last
```

This reads the latest local study log and writes the HTML summary to:

```text
analysis/output/last_session_summary.html
```

## Licences

Third-party dependencies, mods, tutorials, schematics, and game-related assets may remain under their respective licences and terms.

Unless otherwise noted, the original code and materials in this repository are licensed under the Apache License 2.0.