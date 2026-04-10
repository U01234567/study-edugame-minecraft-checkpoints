package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.minecraft.core.registries.BuiltInRegistries;
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
    private static final Map<String, CreatureCard> CUSTOM_CREATURE_CARDS = orderedMap(
        Map.entry("abyss_deer", new CreatureCard(
                "Abyss deer",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a dark deer-like fantasy creature.",
                        "It has a calm stance and long legs.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("abyss_deer_a", 272, 71, -335, FacingDirection.WEST),
                        new CreatureSpawn("abyss_deer_b", 274, 71, -335, FacingDirection.EAST),
                        new CreatureSpawn("abyss_deer_c", 272, 71, -337, FacingDirection.NORTH),
                        new CreatureSpawn("abyss_deer_d", 275, 71, -336, FacingDirection.SOUTH),
                        new CreatureSpawn("abyss_deer_e", 273, 71, -338, FacingDirection.WEST),
                        new CreatureSpawn("abyss_deer_f", 276, 71, -337, FacingDirection.NORTH),
                        new CreatureSpawn("abyss_deer_g", 277, 71, -339, FacingDirection.EAST),
                        new CreatureSpawn("abyss_deer_h", 274, 71, -340, FacingDirection.SOUTH),
                        new CreatureSpawn("abyss_deer_i", 272, 71, -340, FacingDirection.EAST),
                        new CreatureSpawn("abyss_deer_j", 276, 71, -335, FacingDirection.WEST)
                )
        )),
        Map.entry("amethyst_scarab", new CreatureCard(
                "Amethyst scarab",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a scarab-like fantasy creature.",
                        "Its theme is based on amethyst.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("amethyst_scarab_a", 258, 66, 815, FacingDirection.NORTH),
                        new CreatureSpawn("amethyst_scarab_b", 221, 68, 656, FacingDirection.NORTH),
                        new CreatureSpawn("amethyst_scarab_c", 345, 64, 455, FacingDirection.NORTH)
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
                        new CreatureSpawn("axolotl_dragon_a", -147, 87, 55, FacingDirection.SOUTH)
                )
        )),
        Map.entry("cave_dweller", new CreatureCard(
                "Cave dweller",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a cave-themed fantasy creature.",
                        "It has a dark and unsettling appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("cave_dweller_a", 234, 72, -263, FacingDirection.EAST),
                        new CreatureSpawn("cave_dweller_b", 234, 72, -248, FacingDirection.NORTH),
                        new CreatureSpawn("cave_dweller_c", 220, 72, -249, FacingDirection.WEST)
                )
        )),
        Map.entry("ender_ape", new CreatureCard(
                "Ender ape",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is an ape-like fantasy creature with an end-themed design.",
                        "It looks agile, strong, and unusual.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("ender_ape_a", 225, 65, 852, FacingDirection.NORTH),
                        new CreatureSpawn("ender_ape_b", 283, 70, 657, FacingDirection.NORTH),
                        new CreatureSpawn("ender_ape_c", 266, 70, 542, FacingDirection.NORTH)
                )
        )),
        Map.entry("flying_bunny", new CreatureCard(
                "Flying bunny",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This creature looks like a rabbit with unusual flying features.",
                        "It has a light and whimsical shape.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("flying_bunny_a", 348, 69, -230, FacingDirection.WEST),
                        new CreatureSpawn("flying_bunny_b", 346, 70, -228, FacingDirection.EAST)
                )
        )),
        Map.entry("glare", new CreatureCard(
                "Glare",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a small flying fantasy creature.",
                        "It has a plant-like and watchful appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("glare_a", 171, 63, 837, FacingDirection.NORTH),
                        new CreatureSpawn("glare_b", 321, 72, 651, FacingDirection.NORTH),
                        new CreatureSpawn("glare_c", 315, 66, 520, FacingDirection.NORTH)
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
                        new CreatureSpawn("grand_grassling_father_a", -144, 71, 68, FacingDirection.EAST)
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
                        new CreatureSpawn("ice_golem_a", -152, 71, 68, FacingDirection.WEST)
                )
        )),
        Map.entry("killer_crab", new CreatureCard(
                "Killer crab",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a crab-like fantasy creature.",
                        "It has a low body and large claws.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("killer_crab_a", 248, 71, -177, FacingDirection.NORTH),
                        new CreatureSpawn("killer_crab_b", 233, 71, -192, FacingDirection.WEST),
                        new CreatureSpawn("killer_crab_c", 265, 71, -195, FacingDirection.WEST)
                )
        )),
        Map.entry("lizard_knight", new CreatureCard(
                "Lizard knight",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This creature combines reptile features with a knightly shape.",
                        "It looks armored and alert.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("lizard_knight_a", 139, 64, 783, FacingDirection.SOUTH),
                        new CreatureSpawn("lizard_knight_b", 225, 67, 714, FacingDirection.SOUTH),
                        new CreatureSpawn("lizard_knight_c", 267, 66, 472, FacingDirection.SOUTH)
                )
        )),
        Map.entry("mushroom_bup", new CreatureCard(
                "Mushroom bup",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a mushroom-themed fantasy creature.",
                        "It has a rounded shape and a soft woodland feel.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("mushroom_bup_a", 332, 63, -294, FacingDirection.WEST),
                        new CreatureSpawn("mushroom_bup_b", 342, 63, -301, FacingDirection.SOUTH),
                        new CreatureSpawn("mushroom_bup_c", 344, 63, -288, FacingDirection.NORTH),
                        new CreatureSpawn("mushroom_bup_d", 347, 63, -307, FacingDirection.EAST)
                )
        )),
        Map.entry("orc", new CreatureCard(
                "Orc",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a broad humanoid fantasy creature.",
                        "It has a rough and battle-ready look.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("orc_a", 114, 65, 820, FacingDirection.NORTH),
                        new CreatureSpawn("orc_b", 351, 64, 665, FacingDirection.NORTH),
                        new CreatureSpawn("orc_c", 372, 65, 600, FacingDirection.NORTH)
                )
        )),
        Map.entry("prototype_warden", new CreatureCard(
                "Prototype warden",
                StudyChapter.CHAPTER_3,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a warden-like fantasy creature in an experimental form.",
                        "It looks heavy, imposing, and unnatural.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("prototype_warden_a", 225, 66, 771, FacingDirection.SOUTH),
                        new CreatureSpawn("prototype_warden_b", 178, 64, 620, FacingDirection.SOUTH),
                        new CreatureSpawn("prototype_warden_c", 228, 65, 547, FacingDirection.SOUTH)
                )
        )),
        Map.entry("retro_tv_robot", new CreatureCard(
                "Retro TV robot",
                StudyChapter.CHAPTER_2,
                CreatureMovementMode.FIXED,
                List.of(
                        "This is a robot-like creature with a retro television theme.",
                        "It has a mechanical and comic appearance.",
                        "It should stay still for the museum-style display."
                ),
                List.of(
                        new CreatureSpawn("retro_tv_robot_a", 282, 72, -174, FacingDirection.NORTH)
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
                        new CreatureSpawn("scrambler_king_a", -143, 71, 160, FacingDirection.EAST)
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
                        new CreatureSpawn("walking_robot_guy_a", -153, 71, 161, FacingDirection.WEST)
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
                        new CreatureSpawn("wardigo_a", -147, 100, 50, FacingDirection.NORTH)
                )
        ))
);

    private static final Map<EntityType<?>, CreatureCard> CARDS = orderedMap(
            Map.entry(EntityType.COW, new CreatureCard(
                    "Cow",
                    StudyChapter.CHAPTER_0,
                    CreatureMovementMode.FREE,
                    List.of(
                            "Cows are mammals.",
                            "They eat grass and other plants.",
                            "Calves drink milk from their mothers."
                    ),
                    List.of(
                            new CreatureSpawn("cow_a", -79, 63, -515, FacingDirection.NORTH)
                    )
            )),
            Map.entry(EntityType.PIG, new CreatureCard(
                    "Pig",
                    StudyChapter.CHAPTER_0,
                    CreatureMovementMode.FREE,
                    List.of(
                            "Pigs are intelligent animals.",
                            "They use their snouts to explore.",
                            "They eat many different foods."
                    ),
                    List.of(
                            new CreatureSpawn("pig_a", -70, 64, -515, FacingDirection.EAST)
                    )
            )),
            Map.entry(EntityType.CHICKEN, new CreatureCard(
                    "Chicken",
                    StudyChapter.CHAPTER_0,
                    CreatureMovementMode.FREE,
                    List.of(
                            "Chickens are birds.",
                            "They scratch the ground to look for food.",
                            "Hens lay eggs."
                    ),
                    List.of(
                            new CreatureSpawn("chicken_a", -63, 65, -518, FacingDirection.SOUTH)
                    )
            ))
    );

    private StudyCreatureCards() {
    }

    public static void validateStudyHotbarConfiguration() {
        validateChapterZeroHotbarCount();

        for (StudyChapter chapter : List.of(StudyChapter.CHAPTER_1, StudyChapter.CHAPTER_2, StudyChapter.CHAPTER_3)) {
            int configuredCreatureCount = creatureCardsForChapter(chapter).size();

            if (StudyConfig.isTestingPhase()) {
                continue;
            }

            if (configuredCreatureCount != 6) {
                throw new IllegalStateException(
                        chapter.displayTitle()
                                + " must define exactly 6 creature cards, but found "
                                + configuredCreatureCount
                );
            }
        }
    }

    public static List<CreatureCardReference> hotbarCardsForChapter(StudyChapter chapter) {
        List<CreatureCardReference> chapterCards = creatureCardsForChapter(chapter);

        if (chapter == StudyChapter.CHAPTER_0) {
            validateChapterZeroHotbarCount();
            return chapterCards;
        }

        if (StudyConfig.isTestingPhase()) {
            return List.copyOf(chapterCards.subList(0, Math.min(6, chapterCards.size())));
        }

        return chapterCards;
    }

    public static String creatureIdForEntityType(EntityType<?> entityType) {
        StudyEntities.CustomCreatureDefinition customDefinition = StudyEntities.findDefinition(entityType);
        if (customDefinition != null) {
            return customDefinition.id();
        }

        if (!CARDS.containsKey(entityType)) {
            return null;
        }

        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
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

    private static List<CreatureCardReference> creatureCardsForChapter(StudyChapter chapter) {
        List<CreatureCardReference> cards = new ArrayList<>();

        for (Map.Entry<String, CreatureCard> entry : CUSTOM_CREATURE_CARDS.entrySet()) {
            addCardReference(cards, entry.getKey(), entry.getValue(), chapter);
        }

        for (Map.Entry<EntityType<?>, CreatureCard> entry : CARDS.entrySet()) {
            addCardReference(cards, creatureIdForEntityType(entry.getKey()), entry.getValue(), chapter);
        }

        return List.copyOf(cards);
    }

    private static void validateChapterZeroHotbarCount() {
        int chapterZeroCardCount = creatureCardsForChapter(StudyChapter.CHAPTER_0).size();
        if (chapterZeroCardCount < 1 || chapterZeroCardCount > 9) {
            throw new IllegalStateException(
                    StudyChapter.CHAPTER_0.displayTitle()
                            + " must define between 1 and 9 cards for the hotbar, but found "
                            + chapterZeroCardCount
            );
        }
    }

    private static void addCardReference(List<CreatureCardReference> cards,
                                         String creatureId,
                                         CreatureCard card,
                                         StudyChapter chapter) {
        if (card.chapter() != chapter || creatureId == null || creatureId.isBlank()) {
            return;
        }

        cards.add(new CreatureCardReference(creatureId, card));
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

    public record CreatureCardReference(
            String creatureId,
            CreatureCard card
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

    @SafeVarargs
    private static <K, V> Map<K, V> orderedMap(Map.Entry<K, V>... entries) {
        Map<K, V> ordered = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : entries) {
            if (ordered.put(entry.getKey(), entry.getValue()) != null) {
                throw new IllegalStateException("Duplicate creature card key detected: " + entry.getKey());
            }
        }

        return Collections.unmodifiableMap(ordered);
    }
}