package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.entity.StudyEntities;
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
    );

    private static final Map<EntityType<?>, CreatureCard> CARDS = Map.ofEntries(
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
            )),
            Map.entry(EntityType.SHEEP, new CreatureCard(
                    "Sheep",
                    StudyChapter.CHAPTER_2,
                    CreatureMovementMode.FIXED,
                    List.of(
                            "Sheep are covered in wool.",
                            "They are herbivores.",
                            "Wool helps keep them warm."
                    ),
                    List.of(
                            new CreatureSpawn("sheep_a", 73, 65, -236, FacingDirection.SOUTH)
                    )
            )),
            Map.entry(EntityType.PIG, new CreatureCard(
                    "Pig",
                    StudyChapter.CHAPTER_2,
                    CreatureMovementMode.FREE,
                    List.of(
                            "Pigs are intelligent animals.",
                            "They use their snouts to explore.",
                            "They eat many different foods."
                    ),
                    List.of(
                            new CreatureSpawn("pig_a", 76, 65, -235, FacingDirection.EAST)
                    )
            )),
            Map.entry(EntityType.CHICKEN, new CreatureCard(
                    "Chicken",
                    StudyChapter.CHAPTER_1,
                    CreatureMovementMode.FREE,
                    List.of(
                            "Chickens are birds.",
                            "They scratch the ground to look for food.",
                            "Hens lay eggs."
                    ),
                    List.of(
                            new CreatureSpawn("chicken_a", 64, 65, -235, FacingDirection.EAST)
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