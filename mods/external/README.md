# mods/external

This folder contains references, supporting files, and credit records for third-party mods, assets, tutorials, and other external source materials used in the study setup and logging.

Third-party dependencies, mods, schematics, tutorials, downloaded models, textures, and related assets may remain under their respective licences and terms.

## Purpose

This folder keeps third-party materials separate from the custom study mod source code and serves as the main provenance and attribution register for external sources used in the project.

It has two direct technical roles:

1. `libs/`
   - local third-party runtime jars used during development runs
   - these jars are copied into `mods/custom/run/mods/` by the Gradle build before the client starts

2. `gltf/`
   - external creature source/reference folders and their textures
   - these folders are kept for provenance, attribution, inspection, and manual reference
   - they are **not** a live code-generation input for the current mod build

In addition, this README is also used to record:

- external creature model sources and attribution links
- external schematics and building references
- tutorials and workflow references
- design inspirations and technical ideas
- AI-assisted development notes relevant to external creature preparation and earlier conversion work

## How to use this file

When adding a new external source, record it here even if:

- the source is not stored directly inside `mods/external/`
- the source was used only as a tutorial or technical reference
- the source contributed only an idea, workflow, or starting point

## Runtime libraries (`libs/`)

These jars are treated as external runtime dependencies for local development.

| Path | File type | Creator(s) | Acquired from | Project / upstream | Version used | Used for | Notes |
|---|---|---|---|---|---|---|---|
| `external/libs/worldedit-fabric-mc26.1-7.4.2-SNAPSHOT-dist.jar` | Fabric mod jar | EngineHub / WorldEdit contributors | [EngineHub build server — WorldEdit Fabric mc/26.1 branch](https://builds.enginehub.org/job/worldedit?branch=mc/26.1) | [EngineHub / WorldEdit project](https://enginehub.org/worldedit/) | fabric-mc26.1-7.4.2-SNAPSHOT | Development-world editing and building support | Copied into `mods/custom/run/mods/` before `runClient` |

## External creature model sources (`gltf/` and related folders)

| Creature id / folder | Local source format | Source | Credits                                                                                                                                                                                 |
|---|---|---|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `abyss_deer` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/abyss-deer-e594db88e6634388a7526ebd1786e0b1) | ["Abyss Deer"](https://skfb.ly/oo8Jw) by release is licensed under [Creative Commons Attribution-NonCommercial](http://creativecommons.org/licenses/by-nc/4.0/).                        |
| `amethyst_scarab` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/amethyst-scarab-minecraft-mob-f149e2c3f0044528ade506051f70aafc) | "Amethyst Scarab (Minecraft Mob)" by Barkangel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                           |
| `axolotl_dragon` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/custom-axolotl-dragon-58ce3dd6f5854af5bef12d87159349d4) | ["Custom Axolotl Dragon"](https://skfb.ly/otxYJ) by Alx_Olotl (commissions opened) is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).       |
| `cave_dweller` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/cave-dweller-fa84110ab12e4fb9afe6a4c123cbed06) | ["Cave Dweller"](https://skfb.ly/pwHOJ) by Dweller export is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                |
| `ender_ape` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/ender-ape-9585e03209974f2f9b0f543bac79a1d7) | ["Ender ape"](https://skfb.ly/oKAHX) by kingillager is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                      |
| `flying_bunny` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/flying-bunny-minecraft-mobentity-f9200bd01c244ba29fd9dbe529431572) | ["Flying Bunny Minecraft Mob/Entity"](https://skfb.ly/6YST7) by VeganNatureQueen is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).         |
| `glare` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/glare-from-minecraft-2021-mobvote-9fa6eedaefb44968b1c4bf3f15ceb70c) | ["Glare from Minecraft 2021 mobvote"](https://skfb.ly/pzCCC) by Emerald_knight is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).           |
| `grand_grassling_father` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/grand-grassling-father-be5bd961fc9644529eb8990044440818) | ["Grand Grassling Father"](https://skfb.ly/oRVBu) by 𝕲𝖔𝖑𝖉𝖊𝖓 𝕱𝖔𝖗𝖌𝖊𝖗 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).           |
| `ice_golem` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/ice-golem-169a8760eb0f423eab3da2186cdf1eec) | "Ice Golem" by CoolPixelpro is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                              |
| `killer_crab` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/minecraft-killer-crab-145f5c8940da49ff8f5b278199f44064) | ["Новый моб для Minecraft: Killer Crab"](https://skfb.ly/oCnHX) by Space Kyyyst is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).          |
| `lizard_knight` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/lizard-knight-minecraft-c857c26aa44043ac86e56f00c0cc2d51) | ["Lizard Knight - minecraft"](https://skfb.ly/6YDx7) by !RadHer is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                          |
| `mushroom_bup` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/mushroom-bup-minecraft-mob-419123b9fc56403fb7f11c056515b64c) | ["Mushroom Bup (Minecraft Mob)"](https://skfb.ly/oPVHV) by Barkangel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                     |
| `orc` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/minecraft-orc-695c63864a194871ac6764f25ad7d9af) | ["Minecraft - Orc"](https://skfb.ly/oAGuN) by Sam Torres is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                 |
| `prototype_warden` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/prototype-warden-d95914562672432eb207ecdbc9cc63f4) | ["Prototype Warden"](https://skfb.ly/oKvtS) by funt198206082004 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                          |
| `retro_tv_robot` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/retro-tv-robot-animation-ready-blockbench-file-574e006a4e50408d9565e82fafe8ef19) | ["Retro TV Robot - Animation Ready Blockbench File"](https://skfb.ly/6W7GG) by ArtsByKev is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/). |
| `scrambler_king` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/scrambler-king-054d8f1ac2f04921aaa066fc26052a87) | ["Scrambler King"](https://skfb.ly/pqMQH) by Seektheseeker546 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                            |
| `walking_robot_guy` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/walking-robot-guy-blockbench-animation-7190ff66cb3d4e729a2ab95aeb9e797f) | ["Walking Robot Guy - Blockbench Animation"](https://skfb.ly/opEKx) by ArtsByKev is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).         |
| `wardigo` | GLTF | [Sketchfab](https://sketchfab.com/3d-models/wardigo-minecraft-3718ff4b3e6d4335b02fff2e74aac9b7) | "Wardigo minecraft" by rocky1234handsome is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                 |

## Buildings, schematics, and adapted world content

| Repo path / reference | Creator(s) | Reference to source                                                                                                               | Used for | Notes |
|---|---|-----------------------------------------------------------------------------------------------------------------------------------|---|---|
| `world/` museum area (adapted in study world) | Original PlanetMinecraft project creator(s) for the source build; study repository authors for the adapted study version | [Victorian Museum / Nordiska Museet — PlanetMinecraft](https://www.planetminecraft.com/project/victorian-museum-nordiska-museet/) | Basis for the museum area used in the study world | Downloaded and adapted for this project |
| Medieval Bridge (adapted in study world) | Original creator: Munkei | [Pinterest post](https://pinterest.com/pin/254805291413716333/) and [Patreon page](https://www.patreon.com/cw/Munkei/posts)       | Chapter 2 (The Farm) | |
| Windmill (adapted in study world) | Original creator: Goldrobin | [YouTube video](https://www.youtube.com/watch?v=CDEDBIGCRDY)                                                                      | Chapter 2 (The Farm) | |
| Large Greenhouse (adapted in study world) | Original creator: SheraNom | [YouTube video](https://www.youtube.com/watch?v=gcKYJBeUkn4)                                                                      | Chapter 2 (The Farm) | | 
| Horse Stable (adapted in study world) | Original creator: yas xx | [Pinterest post](https://pinterest.com/pin/986429124611563006/)                                                                   | Chapter 2 (The Farm) | | 
| Farm ideas | Original creator: Cryptozoology | [Instagram post](https://www.instagram.com/p/CkYXMgsMg0y) and [YouTube channel](https://www.youtube.com/@Cryptozoology)                                                 | Chapter 2 (The Farm) | | 

## Tutorials, workflows, and general technical references

| Topic | Creator(s) | Reference to source | Used for | Notes                                                               |
|---|---|---|---|---------------------------------------------------------------------|
| General Minecraft Fabric modding learning and project setup reference | Kaupenjoe | [YouTube series: Fabric creature tutorial playlist](https://www.youtube.com/watch?v=oU8-qV-ZtUY&list=PLKGarocXCE1H_HxOYihQMq0mlpqiUJj4L) and [Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X](https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X) | Main starting point for learning Minecraft Fabric modding for this project, including project structure, development setup, configuration, general workflow, and the basic implementation of custom creatures | Important learning resource and technical reference during the project |
| AI-assisted iterative development of creature-conversion and preparation workflows | OpenAI | [ChatGPT-5.4](https://developers.openai.com/api/docs/models/gpt-5.4) by OpenAI | Iterative drafting, revision, and refinement of parts of the former external creature-conversion workflow and related helper logic |                                                                     |

## Development notes

- `run/` is a generated development area and should not be treated as the source of truth.
- `external/libs/` is the source-of-truth location for manually managed third-party runtime jars.
- `external/gltf/` is the source-of-truth location for external creature source/reference folders and their textures.
- the mod runtime uses checked-in Java classes, textures, and creature manifests under `mods/custom/src/main/`
- a fresh clone of the repository does not need a pre-existing `run/` folder. The Gradle run tasks recreate the required runtime structure and copy the external jars into place.
