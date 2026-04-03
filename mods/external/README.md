# mods/external

This folder contains references, supporting files, and credit records for third-party mods, assets, tutorials, and other external source materials used in the study setup and logging.

Third-party dependencies, mods, schematics, tutorials, and related assets may remain under their respective licences and terms.

## Purpose

This folder keeps third-party materials separate from the custom study mod source code and serves as the main provenance and attribution register for external sources used in the project.

At the moment, it has two direct technical roles:

1. `libs/`
    - local third-party runtime jars used during development runs
    - these jars are copied into `mods/custom/run/mods/` by the Gradle build before the client starts

2. `blockbench/`
    - source asset files used to build custom study creatures
    - these are processed by the Gradle build into generated runtime resources

In addition, this README is also used to record:

- external schematics and building references
- tutorials and workflow references
- design inspirations and technical ideas
- other external materials that should be acknowledged clearly for scientific transparency

## How to use this file

When adding a new external source, record it here even if:

- the source is not stored directly inside `mods/external/`
- the source was used only as a tutorial or technical reference
- the source contributed only an idea, workflow, or starting point

## Runtime libraries (`libs/`)

These jars are treated as external runtime dependencies for local development.

| Path | File type | Creator(s) | Acquired from | Project / upstream | Version used | Used for | Notes |
|---|---|---|---|---|---|---|---|
| `external/libs/geckolib-fabric-26.1-5.5.jar` | Fabric mod jar | GeckoLib contributors | [Modrinth — GeckoLib 5.5](https://modrinth.com/mod/geckolib/version/5.5) | [GeckoLib GitHub repository](https://github.com/bernie-g/geckolib/) | 5.5 | Runtime dependency for custom animated creature entities | Copied into `mods/custom/run/mods/` before `runClient` |
| `external/libs/worldedit-fabric-mc26.1-7.4.2-SNAPSHOT-dist.jar` | Fabric mod jar | EngineHub / WorldEdit contributors | [EngineHub build server — WorldEdit Fabric mc/26.1 branch](https://builds.enginehub.org/job/worldedit?branch=mc/26.1) | [EngineHub / WorldEdit project](https://enginehub.org/worldedit/) | fabric-mc26.1-7.4.2-SNAPSHOT | Development-world editing and building support | Copied into `mods/custom/run/mods/` before `runClient` |

## Blockbench assets (`blockbench/`)

These files are source assets for custom study creatures. They are not loaded directly as mods.

| Path | File type | Creator(s)                                  | Derived from / inspired by | Project / upstream | Used for | Notes |
|---|---|---------------------------------------------|---|---|---|---|
| `external/blockbench/mantis/mantis.bbmodel` | Blockbench model project | Viewer contributor from Kaupenjoe community | [Kaupenjoe YouTube Fabric creature tutorial series](https://www.youtube.com/watch?v=oU8-qV-ZtUY&list=PLKGarocXCE1H_HxOYihQMq0mlpqiUJj4L) | [Kaupenjoe Fabric tutorial repository](https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X) | Source model for the custom mantis creature | The `.geo.json` and `.animation.json` runtime files were generated from the `.bbmodel` using the Blockbench GeckoLib plugin |

## Buildings, schematics, and adapted world content

| Repo path / reference | Creator(s) | Reference to source | Used for | Notes |
|---|---|---|---|---|
| `world/` museum area (adapted in study world) | Original PlanetMinecraft project creator(s) for the source build; study repository authors for the adapted study version | [Victorian Museum / Nordiska Museet — PlanetMinecraft](https://www.planetminecraft.com/project/victorian-museum-nordiska-museet/) | Basis for the museum area used in the study world | Downloaded and adapted for this project |

## Tutorials, workflows, and general technical references

| Topic | Creator(s) | Reference to source | Used for | Notes |
|---|---|---|---|---|
| General Minecraft Fabric modding learning and project setup reference | Kaupenjoe | [YouTube series: Fabric creature tutorial playlist](https://www.youtube.com/watch?v=oU8-qV-ZtUY&list=PLKGarocXCE1H_HxOYihQMq0mlpqiUJj4L) and [Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X](https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X) | Main starting point for learning Minecraft Fabric modding for this project, including project structure, development setup, configuration, general workflow, and the basic implementation of custom creatures | This was an important learning resource when the modding work for this repository began. It served as a broad technical foundation and reference point, rather than as a direct one-to-one source for every final implementation detail in this repository. |

## Development notes

- `run/` is a generated development area and should not be treated as the source of truth.
- `external/libs/` is the source-of-truth location for manually managed third-party runtime jars.
- `external/blockbench/` is the source-of-truth location for editable custom creature asset files.
- A fresh clone of the repository does not need a pre-existing `run/` folder. The Gradle run tasks recreate the required runtime structure and copy the external jars into place.