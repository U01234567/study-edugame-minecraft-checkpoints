# study-edugame-minecraft-checkpoints

Repository for an educational-games study on checkpoints in Minecraft.

This repository contains:

* the Minecraft world used in the study
* the mods used for the study setup and logging
* the files and scripts used to run the world locally
* the statistics scripts used to analyse study results

This repository does **not** contain resulting datasets from the study.

## Scope

The repository is intended to preserve the materials and code required to reproduce the study setup and analysis workflow for this experiment.

## Repository structure

* `world/` — Minecraft world files for the study environment
* `mods/custom/` — Fabric-based custom study mod source code
* `mods/external/` — third-party mods and dependency notes
* `server/` — local server setup and scripts for running the world
* `analysis/` — scripts for statistical analysis

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

### 4. Generate the Fabric mod template

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

### 6. Run the Minecraft client for development

From `mods/custom/`, run the Fabric development client with:

```powershell
.\gradlew.bat runClient
```

If `gradlew.bat` reports that Gradle is using Java 8 or Java 17, verify that your terminal environment, `JAVA_HOME`, Project SDK, and Gradle JVM all point to the installed JDK version above.

## Licenses

Third-party dependencies, mods, and game-related assets may remain under their respective licenses and terms.

Unless otherwise noted, the original code and materials in this repository are licensed under the Apache License 2.0.
