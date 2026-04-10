package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central file for the creature cards (one per mob, incl. chapter, position, and spawn metadata)
 */
public final class StudyCreatureCards {
    private static final Map<String, CreatureCard> CUSTOM_CREATURE_CARDS = Map.ofEntries(
        Map.entry("abyss_deer", new CreatureCard(
                "Abyss deer",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a dark deer-like fantasy creature.",
                        "It has a calm stance and long legs.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("abyss_deer_a", -56, 63, 17, FacingDirection.NORTH)
                )
        )),
        Map.entry("amethyst_scarab", new CreatureCard(
                "Amethyst scarab",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a scarab-like fantasy creature.",
                        "Its theme is based on amethyst.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("amethyst_scarab_a", -56, 63, 20, FacingDirection.NORTH)
                )
        )),
        Map.entry("axolotl_dragon", new CreatureCard(
                "Axolotl dragon",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This creature mixes dragon features with an axolotl-like body.",
                        "It looks playful but unusual.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("axolotl_dragon_a", -56, 63, 23, FacingDirection.NORTH)
                )
        )),
        Map.entry("cave_dweller", new CreatureCard(
                "Cave dweller",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a cave-themed fantasy creature.",
                        "It has a dark and unsettling appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("cave_dweller_a", -56, 63, 29, FacingDirection.NORTH)
                )
        )),
        Map.entry("dragon", new CreatureCard(
                "Dragon",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a large dragon-like fantasy creature.",
                        "It has a strong body and a dramatic silhouette.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("dragon_a", -56, 63, 32, FacingDirection.NORTH)
                )
        )),
        Map.entry("ender_ape", new CreatureCard(
                "Ender ape",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is an ape-like fantasy creature with an end-themed design.",
                        "It looks agile, strong, and unusual.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("ender_ape_a", -56, 63, 38, FacingDirection.NORTH)
                )
        )),
        Map.entry("flying_bunny", new CreatureCard(
                "Flying bunny",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This creature looks like a rabbit with unusual flying features.",
                        "It has a light and whimsical shape.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("flying_bunny_a", -56, 63, 44, FacingDirection.NORTH)
                )
        )),
        Map.entry("glare", new CreatureCard(
                "Glare",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a small flying fantasy creature.",
                        "It has a plant-like and watchful appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("glare_a", -56, 63, 50, FacingDirection.NORTH)
                )
        )),
        Map.entry("grand_grassling_father", new CreatureCard(
                "Grand grassling father",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a large plant-themed fantasy creature.",
                        "Its design feels ancient and earthy.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("grand_grassling_father_a", -56, 63, 53, FacingDirection.NORTH)
                )
        )),
        Map.entry("grassling_spreder", new CreatureCard(
                "Grassling spreder",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a small grass-themed fantasy creature.",
                        "It looks like it belongs in an overgrown forest.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("grassling_spreder_a", -56, 63, 56, FacingDirection.NORTH)
                )
        )),
        Map.entry("greater_lurker", new CreatureCard(
                "Greater lurker",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a heavy fantasy creature with a lurking posture.",
                        "It appears broad and imposing.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("greater_lurker_a", -56, 63, 59, FacingDirection.NORTH)
                )
        )),
        Map.entry("hell_horse_yitrium", new CreatureCard(
                "Hell horse yitrium",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a fiery horse-like fantasy creature.",
                        "It has a sharp and dramatic profile.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("hell_horse_yitrium_a", -56, 63, 65, FacingDirection.NORTH)
                )
        )),
        Map.entry("ice_golem", new CreatureCard(
                "Ice golem",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a golem-like creature with an icy theme.",
                        "It looks solid, cold, and heavy.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("ice_golem_a", -56, 63, 68, FacingDirection.NORTH)
                )
        )),
        Map.entry("killer_crab", new CreatureCard(
                "Killer crab",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a crab-like fantasy creature.",
                        "It has a low body and large claws.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("killer_crab_a", -56, 63, 77, FacingDirection.NORTH)
                )
        )),
        Map.entry("lizard_knight", new CreatureCard(
                "Lizard knight",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This creature combines reptile features with a knightly shape.",
                        "It looks armored and alert.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("lizard_knight_a", -56, 63, 80, FacingDirection.NORTH)
                )
        )),
        Map.entry("mini_dragon", new CreatureCard(
                "Mini dragon",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a smaller dragon-like creature.",
                        "It keeps the classic dragon look in a compact form.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("mini_dragon_a", -56, 63, 86, FacingDirection.NORTH)
                )
        )),
        Map.entry("mushroom_bup", new CreatureCard(
                "Mushroom bup",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a mushroom-themed fantasy creature.",
                        "It has a rounded shape and a soft woodland feel.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("mushroom_bup_a", -56, 63, 89, FacingDirection.NORTH)
                )
        )),
        Map.entry("orc", new CreatureCard(
                "Orc",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a broad humanoid fantasy creature.",
                        "It has a rough and battle-ready look.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("orc_a", -56, 63, 95, FacingDirection.NORTH)
                )
        )),
        Map.entry("prototype_warden", new CreatureCard(
                "Prototype warden",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a warden-like fantasy creature in an experimental form.",
                        "It looks heavy, imposing, and unnatural.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("prototype_warden_a", -56, 63, 98, FacingDirection.NORTH)
                )
        )),
        Map.entry("retro_tv_robot", new CreatureCard(
                "Retro TV robot",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a robot-like creature with a retro television theme.",
                        "It has a mechanical and comic appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("retro_tv_robot_a", -56, 63, 101, FacingDirection.NORTH)
                )
        )),
        Map.entry("scrambler_king", new CreatureCard(
                "Scrambler king",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a fantasy creature with a kingly theme.",
                        "It looks strange, dramatic, and important.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("scrambler_king_a", -56, 63, 104, FacingDirection.NORTH)
                )
        )),
        Map.entry("soul_lizard", new CreatureCard(
                "Soul lizard",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a lizard-like creature with a spectral theme.",
                        "It looks agile and slightly eerie.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("soul_lizard_a", -56, 63, 107, FacingDirection.NORTH)
                )
        )),
        Map.entry("tv_man", new CreatureCard(
                "TV man",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a humanoid fantasy creature with a television-like head.",
                        "It has a strange and comic appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("tv_man_a", -56, 63, 113, FacingDirection.NORTH)
                )
        )),
        Map.entry("walking_robot_guy", new CreatureCard(
                "Walking robot guy",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a robot-like humanoid creature.",
                        "It has a mechanical body and a simple silhouette.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("walking_robot_guy_a", -56, 63, 116, FacingDirection.NORTH)
                )
        )),
        Map.entry("wardigo", new CreatureCard(
                "Wardigo",
                StudyChapter.CHAPTER_1,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a dark fantasy creature with a menacing shape.",
                        "It looks lean, tall, and unnatural.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("wardigo_a", -56, 63, 119, FacingDirection.NORTH)
                )
        ))
);

    private static final Map<EntityType<?>, CreatureCard> CARDS = Map.ofEntries(
            Map.entry(EntityType.COW, new CreatureCard(
                    "Cow",
                    StudyChapter.CHAPTER_1,
                    CreatureMovementMode.FIXED,
                    List.of(
                            "Cows are mammals.",
                            "They eat grass and other plants.",
                            "Calves drink milk from their mothers."
                    ),
                    List.of(
                            new CreatureSpawn("cow_a", -56, 63, -1, FacingDirection.NORTH)
                    )
            )),
            Map.entry(EntityType.SHEEP, new CreatureCard(
                    "Sheep",
                    StudyChapter.CHAPTER_1,
                    CreatureMovementMode.FIXED,
                    List.of(
                            "Sheep are covered in wool.",
                            "They are herbivores.",
                            "Wool helps keep them warm."
                    ),
                    List.of(
                            new CreatureSpawn("sheep_a", -56, 63, 3, FacingDirection.NORTH)
                    )
            )),
            Map.entry(EntityType.PIG, new CreatureCard(
                    "Pig",
                    StudyChapter.CHAPTER_1,
                    CreatureMovementMode.FIXED,
                    List.of(
                            "Pigs are intelligent animals.",
                            "They use their snouts to explore.",
                            "They eat many different foods."
                    ),
                    List.of(
                            new CreatureSpawn("pig_a", -56, 63, 7, FacingDirection.NORTH)
                    )
            )),
            Map.entry(EntityType.CHICKEN, new CreatureCard(
                    "Chicken",
                    StudyChapter.CHAPTER_1,
                    CreatureMovementMode.FIXED,
                    List.of(
                            "Chickens are birds.",
                            "They scratch the ground to look for food.",
                            "Hens lay eggs."
                    ),
                    List.of(
                            new CreatureSpawn("chicken_a", -56, 63, 11, FacingDirection.NORTH)
                    )
            ))
    );

    private StudyCreatureCards() {
    }

    public static CreatureCard get(EntityType<?> entityType) {
        StudyEntities.CustomCreatureDefinition customDefinition = StudyEntities.findDefinition(entityType);
        if (customDefinition != null) {
            return CUSTOM_CREATURE_CARDS.get(customDefinition.id());
        }
        return CARDS.get(entityType);
    }

    // Return whether an entity type is an allowed study creature.
    public static boolean isAllowedStudyCreature(EntityType<?> entityType) {
        return StudyEntities.findDefinition(entityType) != null || CARDS.containsKey(entityType);
    }

    // Return the full set of allowed study creature types.
    public static Set<EntityType<?>> allowedEntityTypes() {
        Set<EntityType<?>> allowed = new LinkedHashSet<>(CARDS.keySet());
        allowed.addAll(StudyEntities.customEntityTypes());
        return Collections.unmodifiableSet(allowed);
    }

    // Return all configured spawn assignments for one chapter.
    public static List<CreatureSpawnAssignment> spawnAssignmentsForChapter(StudyChapter chapter) {
        List<CreatureSpawnAssignment> assignments = new ArrayList<>();

        for (Map.Entry<EntityType<?>, CreatureCard> entry : CARDS.entrySet()) {
            addAssignments(assignments, entry.getKey(), entry.getValue(), chapter);
        }

        for (StudyEntities.CustomCreatureDefinition definition : StudyEntities.customCreatureDefinitions()) {
            CreatureCard card = CUSTOM_CREATURE_CARDS.get(definition.id());
            if (card == null) {
                continue;
            }

            EntityType<?> entityType = StudyEntities.byId(definition.id());
            if (entityType == null) {
                throw new IllegalStateException("Missing registered custom entity type for creature id: " + definition.id());
            }

            addAssignments(assignments, entityType, card, chapter);
        }

        return List.copyOf(assignments);
    }

    // Validate that every discovered custom creature has a participant-facing card.
    public static void validateCustomCreatureCards() {
        Map<String, String> missingCards = new LinkedHashMap<>();

        for (StudyEntities.CustomCreatureDefinition definition : StudyEntities.customCreatureDefinitions()) {
            if (!CUSTOM_CREATURE_CARDS.containsKey(definition.id())) {
                missingCards.put(definition.id(), definition.displayName());
            }
        }

        if (!missingCards.isEmpty()) {
            throw new IllegalStateException(
                    "Missing custom creature card entries in StudyCreatureCards for: " + missingCards.keySet()
            );
        }
    }

    // Validate that every creature card has exactly one usable chapter, one usable
    // movement mode, at least one spawn, and globally unique creature names.
    public static void validateCreaturePlacements() {
        Set<String> seenNames = new LinkedHashSet<>();

        for (Map.Entry<EntityType<?>, CreatureCard> entry : CARDS.entrySet()) {
            validateCreatureCard("vanilla:" + entry.getKey(), entry.getValue(), seenNames);
        }

        for (StudyEntities.CustomCreatureDefinition definition : StudyEntities.customCreatureDefinitions()) {
            CreatureCard card = CUSTOM_CREATURE_CARDS.get(definition.id());
            if (card == null) {
                continue;
            }

            validateCreatureCard("custom:" + definition.id(), card, seenNames);
        }
    }

    private static void addAssignments(List<CreatureSpawnAssignment> assignments,
                                       EntityType<?> entityType,
                                       CreatureCard card,
                                       StudyChapter chapter) {
        if (card.chapter() != chapter) {
            return;
        }

        for (CreatureSpawn spawn : card.spawns()) {
            assignments.add(new CreatureSpawnAssignment(entityType, card, spawn));
        }
    }

    private static void validateCreatureCard(String sourceKey,
                                             CreatureCard card,
                                             Set<String> seenNames) {
        if (card.chapter() == null) {
            throw new IllegalStateException("Creature card must have exactly one chapter: " + sourceKey);
        }

        if (card.movementMode() == null) {
            throw new IllegalStateException("Creature card must have one movement mode: " + sourceKey);
        }

        if (card.spawns() == null || card.spawns().isEmpty()) {
            throw new IllegalStateException("Creature card must define at least one spawn: " + sourceKey);
        }

        for (CreatureSpawn spawn : card.spawns()) {
            if (spawn == null) {
                throw new IllegalStateException("Creature card contains a null spawn: " + sourceKey);
            }

            if (spawn.uniqueName() == null || spawn.uniqueName().isBlank()) {
                throw new IllegalStateException("Creature spawn must have a unique non-blank name: " + sourceKey);
            }

            if (spawn.facing() == null) {
                throw new IllegalStateException("Creature spawn must define a facing direction: " + sourceKey);
            }

            if (!seenNames.add(spawn.uniqueName())) {
                throw new IllegalStateException("Duplicate creature spawn name detected: " + spawn.uniqueName());
            }
        }
    }

    public enum CreatureMovementMode {
        FIXED,
        FREE
    }

    public enum FacingDirection {
        NORTH(180.0F),
        EAST(-90.0F),
        SOUTH(0.0F),
        WEST(90.0F);

        private final float yawDegrees;

        FacingDirection(float yawDegrees) {
            this.yawDegrees = yawDegrees;
        }

        public float yawDegrees() {
            return yawDegrees;
        }
    }

    public record CreatureSpawn(
            String uniqueName,
            int x,
            int y,
            int z,
            FacingDirection facing
    ) {
        public double centerX() {
            return x + 0.5D;
        }

        public double centerZ() {
            return z + 0.5D;
        }
    }

    public record CreatureSpawnAssignment(
            EntityType<?> entityType,
            CreatureCard card,
            CreatureSpawn spawn
    ) {
    }
    public record CreatureCard(
            String displayName,
            StudyChapter chapter,
            CreatureMovementMode movementMode,
            List<String> facts,
            List<CreatureSpawn> spawns
    ) {
    }
}