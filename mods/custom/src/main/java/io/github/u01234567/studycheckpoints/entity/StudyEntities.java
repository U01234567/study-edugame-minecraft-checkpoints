package io.github.u01234567.studycheckpoints.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.u01234567.studycheckpoints.StudyCheckpoints;
import io.github.u01234567.studycheckpoints.entity.custom.StudyCreatureEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central registry for custom study creature entity types, which is
 * produced from * ../external/blockbench/* by Gradle.
 *
 * Each creature folder is assumed to contain:
 * - {name}.geo.json
 * - {name}.animation.json
 * - {name}.png
 * - {name}.bbmodel (reference only, not used at runtime)
 */
public final class StudyEntities {
    private static final String GENERATED_CREATURE_MANIFEST =
            "assets/" + StudyCheckpoints.MOD_ID + "/study-creatures/creatures.json";

    private static final float DEFAULT_HITBOX_WIDTH = 2.8F;
    private static final float DEFAULT_HITBOX_HEIGHT = 1.8F;

    private static final Map<String, EntityType<StudyCreatureEntity>> REGISTERED_CREATURES =
            new LinkedHashMap<>();
    private static final Map<EntityType<?>, CustomCreatureDefinition> DEFINITIONS_BY_TYPE =
            new LinkedHashMap<>();

    private static boolean initialised = false;

    private StudyEntities() {
    }

    // Register all discovered custom study creatures.
    public static synchronized void initialise() {
        if (initialised) {
            return;
        }

        for (CustomCreatureDefinition definition : loadDefinitionsFromGeneratedManifest()) {
            EntityType<StudyCreatureEntity> entityType = registerCreature(definition);
            FabricDefaultAttributeRegistry.register(entityType, StudyCreatureEntity.createAttributes());
        }

        StudyCheckpoints.LOGGER.info("Registered {} custom study creature entity types.", REGISTERED_CREATURES.size());
        initialised = true;
    }

    // Return a custom study creature type by short id.
    public static EntityType<StudyCreatureEntity> byId(String id) {
        return REGISTERED_CREATURES.get(id);
    }

    // Return the definition for a custom study creature type.
    public static CustomCreatureDefinition findDefinition(EntityType<?> entityType) {
        return DEFINITIONS_BY_TYPE.get(entityType);
    }

    // Return all currently registered custom study creature entity types.
    public static Set<EntityType<?>> customEntityTypes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(DEFINITIONS_BY_TYPE.keySet()));
    }

    // Return all discovered custom creature definitions in registration order.
    public static List<CustomCreatureDefinition> customCreatureDefinitions() {
        return List.copyOf(DEFINITIONS_BY_TYPE.values());
    }

    private static EntityType<StudyCreatureEntity> registerCreature(CustomCreatureDefinition definition) {
        Identifier identifier = Identifier.fromNamespaceAndPath(StudyCheckpoints.MOD_ID, definition.id());
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, identifier);

        EntityType<StudyCreatureEntity> entityType = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                key,
                EntityType.Builder.<StudyCreatureEntity>of(StudyCreatureEntity::new, MobCategory.CREATURE)
                        .sized(definition.hitboxWidth(), definition.hitboxHeight())
                        .build(key)
        );

        REGISTERED_CREATURES.put(definition.id(), entityType);
        DEFINITIONS_BY_TYPE.put(entityType, definition);
        return entityType;
    }

    private static List<CustomCreatureDefinition> loadDefinitionsFromGeneratedManifest() {
        InputStream inputStream = StudyEntities.class.getClassLoader().getResourceAsStream(GENERATED_CREATURE_MANIFEST);

        if (inputStream == null) {
            StudyCheckpoints.LOGGER.warn("No generated study creature manifest found at {}", GENERATED_CREATURE_MANIFEST);
            return List.of();
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray creatureIds = root.getAsJsonArray("creature_ids");

            if (creatureIds == null) {
                return List.of();
            }

            List<CustomCreatureDefinition> definitions = new ArrayList<>();

            creatureIds.forEach(element -> {
                String id = element.getAsString().trim();
                if (!id.isBlank()) {
                    definitions.add(new CustomCreatureDefinition(
                            id,
                            toDisplayName(id),
                            DEFAULT_HITBOX_WIDTH,
                            DEFAULT_HITBOX_HEIGHT
                    ));
                }
            });

            return definitions;
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to read generated study creature manifest: {}", GENERATED_CREATURE_MANIFEST, e);
            return List.of();
        }
    }

    private static String toDisplayName(String id) {
        String[] parts = id.split("[-_]");
        StringBuilder displayName = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }

            if (!displayName.isEmpty()) {
                displayName.append(' ');
            }

            String lower = part.toLowerCase();
            displayName.append(Character.toUpperCase(lower.charAt(0)));
            if (lower.length() > 1) {
                displayName.append(lower.substring(1));
            }
        }

        return displayName.isEmpty() ? id : displayName.toString();
    }

    /**
     * Central metadata for one custom study creature.
     *
     * The id is also used as the asset base name:
     * - geckolib/models/entity/{id}.geo.json
     * - geckolib/animations/entity/{id}.animation.json
     * - textures/entity/{id}.png
     */
    public record CustomCreatureDefinition(
            String id,
            String displayName,
            float hitboxWidth,
            float hitboxHeight
    ) {
    }
}