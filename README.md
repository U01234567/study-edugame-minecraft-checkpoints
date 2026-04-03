# study-edugame-minecraft-checkpoints

Repository for an educational-games study on checkpoints in Minecraft.

This repository contains:

* the Minecraft world used in the study
* the mods used for the study setup and logging
* the files and scripts used to run the world locally
* the statistics scripts used to analyse study results

This repository does **not** contain resulting datasets from the study.

## Scope

The repository is intended to preserve the materials, code, and documented external references required to reproduce the study setup and analysis workflow for this experiment.

## Repository structure

* `world/` — Minecraft world files for the study environment
* `mods/custom/` — Fabric-based custom study mod source code
* `mods/external/` — third-party runtime jars, external asset references, and source credits
* `server/` — local server setup and scripts for running the world
* `analysis/` — scripts for statistical analysis

## External sources and credits

This study uses third-party mods, tools, tutorials, external creative works, and other source materials.

The main credit and provenance register is here:

* [`mods/external/README.md`](mods/external/README.md)

That file should be treated as the source of truth for:

* third-party runtime jars
* external modelling and animation references
* adapted buildings, schematics, and world ideas
* tutorials, workflows, and general technical references

## Development environment and core dependencies

The table below documents the main software and versions used during development of this repository.

| Component | Version / value | Notes |
|---|---|---|
| Operating system during development | Windows 11 | Primary development environment |
| Java (JDK) | Eclipse Temurin JDK 25.0.2+10-LTS | Used for Gradle and mod development |
| IntelliJ IDEA | 2026.1 | Main IDE for mod development |
| Minecraft Launcher | v3.29.53-2.5.2 | Used for local development and testing |
| Minecraft | 26.1 | Project target version |
| Fabric Loader | 0.18.5 | Project dependency |
| Fabric API | 0.144.3+26.1 | Project dependency |
| Fabric Loom | 1.15-SNAPSHOT | Build tooling configured in the project |
| GeckoLib | 5.5 | Runtime dependency for animated study creatures |
| WorldEdit (Fabric) | `worldedit-fabric-mc26.1-7.4.2-SNAPSHOT-dist.jar` | Local development dependency |
| Blockbench | 5.1.1 | Used to author creature models |
| Blockbench plugin | GeckoLib Models & Animations v4.2.4 | Used to generate `.geo.json` and `.animation.json` from `.bbmodel` files |

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

If Gradle sync fails with an error stating that the build is using Java 17, change the Gradle JVM to **Temurin 25** or another **JDK 21+** installation.

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

* disabled - **Kotlin Programming Language**
* enabled - **Data Generation**
* disabled - **Split client and common sources**
* disabled - **Kotlin Build Script**

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

At the moment this includes at least:

* GeckoLib
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

On the first run after cloning the repository, when no mods/custom/run/ folder exists yet, Minecraft may still show its normal opening screen once so that local client settings such as sound and narrator can be adjusted. This should not happen again on later runs, because the generated runtime files will then already exist.

If `gradlew.bat` reports that Gradle is using Java 8 or Java 17, verify that your terminal environment, `JAVA_HOME`, Project SDK, and Gradle JVM all point to the installed JDK version above.

## Adding new creatures to the game
### Overview

A new custom study creature currently involves four parts:

1. create or edit the model in Blockbench
2. export the GeckoLib runtime files
3. place the source asset files in the expected external folder
4. add the creature metadata and spawn entries in `StudyCreatureCards.java`

### Naming rules

Use one short, stable creature id in lowercase, for example:

```text
mantis
fox
owl
stag_beetle
```

This id should be used consistently across:

* folder name
* model file base name
* animation file base name
* texture file base name
* Java-side custom creature id where applicable

### Required file layout for a custom creature

For a creature with id `mantis`, the editable source assets should live in:

```text
mods/external/blockbench/mantis/
```

Expected files:

```text
mods/external/blockbench/mantis/mantis.bbmodel
mods/external/blockbench/mantis/mantis.geo.json
mods/external/blockbench/mantis/mantis.animation.json
mods/external/blockbench/mantis/mantis.png
```

### Blockbench workflow

Use Blockbench together with the **GeckoLib Models & Animations** plugin.

Typical workflow:

1. create or edit `x.bbmodel`
2. use the GeckoLib plugin in Blockbench
3. export:

    * `x.geo.json`
    * `x.animation.json`
4. export or save the matching texture image:

    * `x.png`

Important:

* the base name should stay the same for all four files
* the creature folder name should also match that same id
* the `.bbmodel` file is the editable source file
* the `.geo.json` and `.animation.json` files are generated runtime assets

### Add the card entry in `StudyCreatureCards.java`

You must add a `CreatureCard` entry for every creature used in the study.

Current card fields are:

* display name
* exactly one chapter
* exactly one movement mode: `FIXED` or `FREE`
* fact list
* one or more named spawn points

Example for a vanilla creature:

```java
Map.entry(EntityType.COW, new CreatureCard(
        "Cow",
        StudyChapter.CHAPTER_1,
        CreatureMovementMode.FREE,
        List.of(
                "Cows are mammals.",
                "They eat grass and other plants.",
                "Calves drink milk from their mothers."
        ),
        List.of(
                new CreatureSpawn("cow_a", 67, 65, -236, FacingDirection.NORTH),
                new CreatureSpawn("cow_b", 69, 65, -234, FacingDirection.WEST)
        )
))
```

Example for a custom creature:

```java
Map.entry("mantis", new CreatureCard(
        "Praying mantis",
        StudyChapter.CHAPTER_3,
        CreatureMovementMode.FIXED,
        List.of(
                "A praying mantis is an insect.",
                "It holds its front legs folded in front of the body.",
                "It often stays still and ambushes other animals."
        ),
        List.of(
                new CreatureSpawn("mantis_a", 82, 65, -233, FacingDirection.WEST)
        )
))
```

### Meaning of movement mode

Use:

* `FIXED` — the creature should stay fixed in place, like a statue or display specimen
* `FREE` — the creature may move normally

### Spawn naming rules

Each configured spawn must have a unique name, for example:

* `cow_a`
* `cow_b`
* `mantis_a`

These names are used in logging and analysis, so they must be:

* unique across the whole study
* short and stable
* predictable to read

Avoid renaming an existing creature unless you also intend to change how it is tracked in logs and analysis.

### Chapter rules

Each creature belongs to exactly one chapter.

Do not assign a creature to multiple chapters in the card data. The current game logic expects one chapter per creature, and startup validation is intentionally strict.

### Checklist before committing a new creature

Before committing, verify all of the following:

* the folder name under `mods/external/blockbench/` matches the creature id
* the `.bbmodel`, `.geo.json`, `.animation.json`, and `.png` names all match the same id
* the creature has a `CreatureCard` entry
* the creature has exactly one chapter
* the creature has one movement mode
* all configured creature spawn names are globally unique
* all spawn coordinates and facing values are intentional
* the client starts with:

```powershell
.\gradlew.bat runClient
```

### Where to record external credit for a new creature

If the creature, model idea, texture, animation approach, tutorial, or other source was derived from an external source, add that attribution to:

* [`mods/external/README.md`](mods/external/README.md)

That includes:

* downloaded jars
* model tutorials
* animation tutorials
* general design references
* reused or adapted external files

## Local analysis

To generate the latest local session summary, run from `analysis/`:

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