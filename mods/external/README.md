# mods/external

This folder contains references, supporting files, and credit records for third-party mods, assets, tutorials, and other external source materials used in the study setup and logging.

Third-party dependencies, mods, schematics, tutorials, downloaded models, textures, and related assets may remain under their respective licences and terms.

## Purpose

This folder keeps third-party materials separate from the custom study mod source code and serves as the main provenance and attribution register for external sources used in the project.

At the moment, it has four direct technical roles:

1. `libs/`
   - local third-party runtime jars used during development runs
   - these jars are copied into `mods/custom/run/mods/` by the Gradle build before the client starts

2. `blockbench/`
   - Blockbench-exported Java model source folders used as inputs for generated study creatures
   - current workflow uses the Blockbench **Fabric Modded Entity plugin**

3. `gltf/`
   - externally sourced GLTF creature folders and their textures
   - these are processed by the Python conversion pipeline into Java-backed Fabric creature models

4. `scripts/`
   - Python helper scripts for study-creature generation, GLTF conversion, texture baking, and code generation
   - these scripts are part of the working toolchain for the generated creature pipeline

In addition, this README is also used to record:

- external creature model sources and attribution links
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
| `external/libs/worldedit-fabric-mc26.1-7.4.2-SNAPSHOT-dist.jar` | Fabric mod jar | EngineHub / WorldEdit contributors | [EngineHub build server — WorldEdit Fabric mc/26.1 branch](https://builds.enginehub.org/job/worldedit?branch=mc/26.1) | [EngineHub / WorldEdit project](https://enginehub.org/worldedit/) | fabric-mc26.1-7.4.2-SNAPSHOT | Development-world editing and building support | Copied into `mods/custom/run/mods/` before `runClient` |

## Blockbench assets (`blockbench/`)

These files are source assets for custom study creatures. They are not loaded directly as mods.

| Path | File type | Creator(s) | Derived from / inspired by | Project / upstream | Used for | Notes |
|---|---|---|---|---|---|---|
| `external/blockbench/mantis/mantis.bbmodel` | Blockbench model project | Viewer contributor from Kaupenjoe community | [Kaupenjoe YouTube Fabric creature tutorial series](https://www.youtube.com/watch?v=oU8-qV-ZtUY&list=PLKGarocXCE1H_HxOYihQMq0mlpqiUJj4L) | [Kaupenjoe Fabric tutorial repository](https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X) | Source model for the custom mantis creature | Legacy learning/example asset kept in the repository |

## External creature model sources (`gltf/` and related folders)

| Creature id / folder | Local source format | Source | Credits                                                                                                                                                                           |
|---|---|---|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `abyss_deer` | GLTF | [Sketchfab — Abyss Deer](https://sketchfab.com/3d-models/abyss-deer-e594db88e6634388a7526ebd1786e0b1) | ["Abyss Deer"](https://skfb.ly/oo8Jw) by release is licensed under [Creative Commons Attribution-NonCommercial](http://creativecommons.org/licenses/by-nc/4.0/).                  |
| `allay` | GLTF | [Sketchfab — Allay Redesigned](https://sketchfab.com/3d-models/allay-redesigned-65a09bda6056476fa333d9cd4bed5920) | ["Allay (redesigned)"](https://skfb.ly/oKyzW) by Master Galanodel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                  |
| `amethyst_scarab` | GLTF | [Sketchfab — Amethyst Scarab Minecraft Mob](https://sketchfab.com/3d-models/amethyst-scarab-minecraft-mob-f149e2c3f0044528ade506051f70aafc) | "Amethyst Scarab (Minecraft Mob)" by Barkangel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                     |
| `ancient_city_guardian` | FBX | [Sketchfab — Ancient City Guardian](https://sketchfab.com/3d-models/ancient-city-guardian-4ae503b61d62493e98d00f523138c998) | "Ancient city Guardian" by 𝕲𝖔𝖑𝖉𝖊𝖓 𝕱𝖔𝖗𝖌𝖊𝖗 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                               |
| `axolotl_dragon` | GLTF | [Sketchfab — Custom Axolotl Dragon](https://sketchfab.com/3d-models/custom-axolotl-dragon-58ce3dd6f5854af5bef12d87159349d4) | ["Custom Axolotl Dragon"](https://skfb.ly/otxYJ) by Alx_Olotl (commissions opened) is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/). |
| `bread_monster` | GLTF | [Sketchfab — Bread Monster Model](https://sketchfab.com/3d-models/bread-monster-model-1a5dd20359f34b20ab572860a39db18a) | ["Bread monster model"](https://skfb.ly/oIGvY) by 80S5 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                             |
| `dragon` | GLTF | [Sketchfab — Dragon Minecraft](https://sketchfab.com/3d-models/dragon-minecraft-47463156f1e944b195fd21420d538ab7) | ["Dragon - minecraft"](https://skfb.ly/6YDxs) by !RadHer is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                           |
| `dummy_monster` | GLTF | [Sketchfab — Free Dummy Monster](https://sketchfab.com/3d-models/free-dummy-monster-246678f908b548feb0f4cccaeef78756) | ["[ FREE ] Dummy Monster"](https://skfb.ly/oXL9r) by iJUNE is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                         |
| `flying_bunny` | GLTF | [Sketchfab — Flying Bunny Minecraft Mob/Entity](https://sketchfab.com/3d-models/flying-bunny-minecraft-mobentity-f9200bd01c244ba29fd9dbe529431572) | ["Flying Bunny Minecraft Mob/Entity"](https://skfb.ly/6YST7) by VeganNatureQueen is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).   |
| `glyphid_grunt` | GLTF | [Sketchfab — Glyphid Grunt](https://sketchfab.com/3d-models/glyphid-grunt-0c01aeba94304772b617b023bcdd82e7) | ["Glyphid Grunt"](https://skfb.ly/ooIOB) by Z3nsiter is licensed under [Creative Commons Attribution-NonCommercial](http://creativecommons.org/licenses/by-nc/4.0/).              |
| `grand_grassling_father` | GLTF | [Sketchfab — Grand Grassling Father](https://sketchfab.com/3d-models/grand-grassling-father-be5bd961fc9644529eb8990044440818) | ["Grand Grassling Father"](https://skfb.ly/oRVBu) by 𝕲𝖔𝖑𝖉𝖊𝖓 𝕱𝖔𝖗𝖌𝖊𝖗 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).     |
| `grassling_spreder` | GLTF | [Sketchfab — Grassling Spreder](https://sketchfab.com/3d-models/grassling-spreder-e064b0ab73a34168ac2aa54e535875c1) | "Grassling Spreder" by 𝕲𝖔𝖑𝖉𝖊𝖓 𝕱𝖔𝖗𝖌𝖊𝖗 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                   |
| `greater_lurker` | GLTF | [Sketchfab — Greater Lurker](https://sketchfab.com/3d-models/greater-lurker-898a149ca85a41f2ac227fee40ea40f5) | "Greater Lurker" by lythron.x is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                      |
| `hay_golem` | GLTF | [Sketchfab — Hay Golem](https://sketchfab.com/3d-models/hay-golem-fc9dbaded6744bc2bf45b005edca3a26) | "Hay Golem" by Eqüietum is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                            |
| `hell_horse_yitrium` | GLTF | [Sketchfab — Minecraft Hell Horse Yitrium](https://sketchfab.com/3d-models/minecraft-hell-horse-yitrium-1f89edcf57bf43e9b65a67cff2355357) | ["Minecraft - Hell horse "Yitrium""](https://skfb.ly/o7s7X) by omargabagu is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).          |
| `ice_golem` | GLTF | [Sketchfab — Ice Golem](https://sketchfab.com/3d-models/ice-golem-169a8760eb0f423eab3da2186cdf1eec) | "Ice Golem" by CoolPixelpro is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                        |
| `invaderling_commander` | FBX | [Sketchfab — Invaderling Commander](https://sketchfab.com/3d-models/invaderling-commander-70b8c2995693445db6c99ad78c45322e) | ["Invaderling Commander"](https://skfb.ly/p8uPt) by 𝕲𝖔𝖑𝖉𝖊𝖓 𝕱𝖔𝖗𝖌𝖊𝖗 is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).      |
| `lizard_knight` | GLTF | [Sketchfab — Lizard Knight Minecraft](https://sketchfab.com/3d-models/lizard-knight-minecraft-c857c26aa44043ac86e56f00c0cc2d51) | ["Lizard Knight - minecraft"](https://skfb.ly/6YDx7) by !RadHer is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                    |
| `marshadow` | GLTF | [Sketchfab — Marshadow](https://sketchfab.com/3d-models/marshadow-9f89baae3355420290ffcd93ea9d8b99) | ["Marshadow"](https://skfb.ly/oAv9U) by Master Galanodel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                           |
| `mini_dragon` | GLTF | [Sketchfab — Mini Dragon Blockbench](https://sketchfab.com/3d-models/mini-dragon-blockbench-ae7014ee2158439fb956cdc20ffa376c) | ["Mini Dragon Blockbench"](https://skfb.ly/pytG7) by Anakin Skyrizzler is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).             |
| `mosshorn` | GLTF | [Sketchfab — Mosshorn from Hytale](https://sketchfab.com/3d-models/mosshorn-from-hytale-483b439ed6014fff84dd62586ba8fbff) | "Mosshorn from Hytale" by Memetheew is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                |
| `mushroom_bup` | GLTF | [Sketchfab — Mushroom Bup Minecraft Mob](https://sketchfab.com/3d-models/mushroom-bup-minecraft-mob-419123b9fc56403fb7f11c056515b64c) | ["Mushroom Bup (Minecraft Mob)"](https://skfb.ly/oPVHV) by Barkangel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).               |
| `netherite_golem` | GLTF | [Sketchfab — Minecraft Netherite Golem](https://sketchfab.com/3d-models/minecraft-netherite-golem-6629baeb2e234a9aacc04e45f9b12a8f) | "Minecraft - Netherite Golem" by Sam Torres is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                        |
| `orc` | GLTF | [Sketchfab — Minecraft Orc](https://sketchfab.com/3d-models/minecraft-orc-695c63864a194871ac6764f25ad7d9af) | ["Minecraft - Orc"](https://skfb.ly/oAGuN) by Sam Torres is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                           |
| `protogen` | GLTF | [Sketchfab — Minecraft Protogen Model](https://sketchfab.com/3d-models/minecraft-protogen-model-1cf00d1d4fda4a008036410fc213bdee) | "Minecraft Protogen Model" by Endles13 is licensed under [CC Attribution-NonCommercial-ShareAlike](http://creativecommons.org/licenses/by-nc-sa/4.0/).                            |
| `seraphim` | GLTF | [Sketchfab — Seraphim](https://sketchfab.com/3d-models/seraphim-686cb03daf6a404e9bff239eae0ef3e2) | "Seraphim" by CsDani50 is licensed under Free Standard                                                                                                                            |
| `smoliv` | GLTF | [Sketchfab — Smoliv](https://sketchfab.com/3d-models/smoliv-88b19ad5b9074ceea999e2a1b96e2e10) | ["Smoliv"](https://skfb.ly/oFIFu) by Squirmy Worm is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                  |
| `sniffer` | GLTF | [Sketchfab — Minecraft Sniffer](https://sketchfab.com/3d-models/minecraft-sniffer-876e01710c644b1ebe6481d6a2ca0f1a) | ["minecraft sniffer"](https://skfb.ly/oI7RD) by baba.pig is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                           |
| `sniffer_with_excalibur` | GLTF | [Sketchfab — Sniffer with Excalibur GLTF](https://sketchfab.com/3d-models/sniffer-with-excalibur-gltf-48d2608c6ab74d3896abad9ca3c1badf) | "Sniffer with Excalibur - GLTF" by ArtsByKev is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                       |
| `solgaleo` | GLTF | [Sketchfab — Solgaleo](https://sketchfab.com/3d-models/solgaleo-9ee02368ea2c462fb54a2e4211363a1e) | "Solgaleo" by Master Galanodel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                     |
| `soul_lizard` | GLTF | [Sketchfab — Soul Lizard](https://sketchfab.com/3d-models/soul-lizard-5fe71461b51b4deca112cb1e335807d3) | "Soul Lizard" by release is licensed under [Creative Commons Attribution-NonCommercial](http://creativecommons.org/licenses/by-nc/4.0/).                                          |
| `thanos_fish` | FBX | [Sketchfab — Thanos Fish](https://sketchfab.com/3d-models/thanos-fish-86483432c6c543bdb7b9ef8782e3765c) | "Thanos Fish" by Liron is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                                             |
| `tv_man` | GLTF | [Sketchfab — TV Man](https://sketchfab.com/3d-models/tv-man-8c0131e169b3412b9d1a47580f3720a8) | ["TV Man"](https://skfb.ly/oNzwP) by Elite Scientist Cameraman is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                     |
| `walking_robot_guy` | GLTF | [Sketchfab — Walking Robot Guy Blockbench Animation](https://sketchfab.com/3d-models/walking-robot-guy-blockbench-animation-7190ff66cb3d4e729a2ab95aeb9e797f) | ["Walking Robot Guy - Blockbench Animation"](https://skfb.ly/opEKx) by ArtsByKev is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).   |
| `wardigo` | GLTF | [Sketchfab — Wardigo Minecraft](https://sketchfab.com/3d-models/wardigo-minecraft-3718ff4b3e6d4335b02fff2e74aac9b7) | "Wardigo minecraft" by rocky1234handsome is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                           |
| `wendigo` | GLTF / Blockbench source reference | [Sketchfab — Free Wendigo for All (Very Old Model)](https://sketchfab.com/3d-models/free-wendigo-for-all-very-old-model-f0006885dac14bc690e8150902a670ef) | "Free Wendigo for all! (very old model)" by Willmood is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                               |
| `zapdos_galarian` | GLTF | [Sketchfab — Zapdos Galarian](https://sketchfab.com/3d-models/zapdos-galarian-589a2cefe1e442228f62292b8cb18772) | "Zapdos (Galarian)" by Master Galanodel is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/).                                            |

## Buildings, schematics, and adapted world content

| Repo path / reference | Creator(s) | Reference to source | Used for | Notes |
|---|---|---|---|---|
| `world/` museum area (adapted in study world) | Original PlanetMinecraft project creator(s) for the source build; study repository authors for the adapted study version | [Victorian Museum / Nordiska Museet — PlanetMinecraft](https://www.planetminecraft.com/project/victorian-museum-nordiska-museet/) | Basis for the museum area used in the study world | Downloaded and adapted for this project |

## Tutorials, workflows, and general technical references

| Topic | Creator(s) | Reference to source                                                                                                                                                                                                                                            | Used for | Notes |
|---|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---|---|
| General Minecraft Fabric modding learning and project setup reference | Kaupenjoe | [YouTube series: Fabric creature tutorial playlist](https://www.youtube.com/watch?v=oU8-qV-ZtUY&list=PLKGarocXCE1H_HxOYihQMq0mlpqiUJj4L) and [Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X](https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X) | Main starting point for learning Minecraft Fabric modding for this project, including project structure, development setup, configuration, general workflow, and the basic implementation of custom creatures | This was an important learning resource when the modding work for this repository began. It served as a broad technical foundation and reference point, rather than as a direct one-to-one source for every final implementation detail in this repository. |
| AI-assisted drafting and refinement of the external creature-generation scripts | OpenAI | [ChatGPT-5.4](https://developers.openai.com/api/docs/models/gpt-5.4) by OpenAI                                                                                                                                                                                 | Iterative drafting and refinement of the Python scripts in `external/scripts/`, especially the GLTF-to-Java generation workflow | The scripts in `external/scripts/` were iteratively generated and refined with ChatGPT 5.4 assistance, then tested and edited within this repository’s local development workflow. |

## Development notes

- `run/` is a generated development area and should not be treated as the source of truth.
- `external/libs/` is the source-of-truth location for manually managed third-party runtime jars.
- `external/blockbench/` is the source-of-truth location for Blockbench-exported Java model input folders.
- `external/gltf/` is the source-of-truth location for GLTF creature source folders and their textures.
- `external/scripts/` contains the Python generation pipeline used by the Gradle build for Java-backed study-creature generation.
- A fresh clone of the repository does not need a pre-existing `run/` folder. The Gradle run tasks recreate the required runtime structure and copy the external jars into place.