package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.entity.StudyEntities;
import net.minecraft.world.entity.EntityType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central file for the creature cards (one per mob)
 */
public final class StudyCreatureCards {
    private static final Map<String, CreatureCard> CUSTOM_CREATURE_CARDS = Map.ofEntries(
            Map.entry("mantis", new CreatureCard(
                    "Praying mantis",
                    List.of(
                            "A praying mantis is an insect.",
                            "It holds its front legs folded in front of the body.",
                            "It often stays still and ambushes other animals."
                    )
            ))
    );

    private static final Map<EntityType<?>, CreatureCard> CARDS = Map.ofEntries(
            Map.entry(EntityType.COW, new CreatureCard(
                    "Cow",
                    List.of(
                            "Cows are mammals.",
                            "They eat grass and other plants.",
                            "Calves drink milk from their mothers."
                    )
            )),
            Map.entry(EntityType.SHEEP, new CreatureCard(
                    "Sheep",
                    List.of(
                            "Sheep are covered in wool.",
                            "They are herbivores.",
                            "Wool helps keep them warm."
                    )
            )),
            Map.entry(EntityType.PIG, new CreatureCard(
                    "Pig",
                    List.of(
                            "Pigs are intelligent animals.",
                            "They use their snouts to explore.",
                            "They eat many different foods."
                    )
            )),
            Map.entry(EntityType.CHICKEN, new CreatureCard(
                    "Chicken",
                    List.of(
                            "Chickens are birds.",
                            "They scratch the ground to look for food.",
                            "Hens lay eggs."
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

    public record CreatureCard(String displayName, List<String> facts) {
    }
}