package io.github.u01234567.studycheckpoints;

import net.minecraft.world.entity.EntityType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central file for the creature cards (one per mob)
 */
public final class StudyCreatureCards {
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
        return CARDS.get(entityType);
    }

    // Return whether an entity type is an allowed study creature.
    public static boolean isAllowedStudyCreature(EntityType<?> entityType) {
        return CARDS.containsKey(entityType);
    }

    // Return the full set of allowed study creature types.
    public static Set<EntityType<?>> allowedEntityTypes() {
        return Collections.unmodifiableSet(CARDS.keySet());
    }

    public record CreatureCard(String displayName, List<String> facts) {
    }
}