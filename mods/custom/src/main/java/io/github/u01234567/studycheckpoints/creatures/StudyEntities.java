package io.github.u01234567.studycheckpoints.creatures;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.u01234567.studycheckpoints.StudyCheckpoints;
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
 * Central registry for packaged custom study creature entity types.
 *
 * Definitions are loaded from the baked creature manifest under
 * src/main/resources/assets/study-checkpoints/study-creatures/creatures.json.
 * Model classes are checked in under the generated creature package in src/main/java.
 */
public final class StudyEntities {
    private static final String CREATURE_MANIFEST =
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

    public static synchronized void initialise() {
        if (initialised) {
            return;
        }

        for (CustomCreatureDefinition definition : loadDefinitionsFromPackagedManifest()) {
            EntityType<StudyCreatureEntity> entityType = registerCreature(definition);
            FabricDefaultAttributeRegistry.register(entityType, StudyCreatureEntity.createAttributes());
        }

        StudyCheckpoints.LOGGER.info("Registered {} Java-backed custom study creature entity types.", REGISTERED_CREATURES.size());
        initialised = true;
    }

    public static EntityType<StudyCreatureEntity> byId(String id) {
        return REGISTERED_CREATURES.get(id);
    }

    public static CustomCreatureDefinition findDefinition(EntityType<?> entityType) {
        return DEFINITIONS_BY_TYPE.get(entityType);
    }

    public static Set<EntityType<?>> customEntityTypes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(DEFINITIONS_BY_TYPE.keySet()));
    }

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

    private static List<CustomCreatureDefinition> loadDefinitionsFromPackagedManifest() {
        InputStream inputStream = StudyEntities.class.getClassLoader().getResourceAsStream(CREATURE_MANIFEST);

        if (inputStream == null) {
            StudyCheckpoints.LOGGER.warn("No packaged study creature manifest found at {}", CREATURE_MANIFEST);
            return List.of();
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray creatures = root.getAsJsonArray("creatures");

            if (creatures == null) {
                return List.of();
            }

            List<CustomCreatureDefinition> definitions = new ArrayList<>();

            creatures.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                String id = object.get("id").getAsString().trim();

                if (id.isBlank()) {
                    return;
                }

                definitions.add(new CustomCreatureDefinition(
                        id,
                        object.has("display_name")
                                ? object.get("display_name").getAsString().trim()
                                : toDisplayName(id),
                        object.has("hitbox_width")
                                ? object.get("hitbox_width").getAsFloat()
                                : DEFAULT_HITBOX_WIDTH,
                        object.has("hitbox_height")
                                ? object.get("hitbox_height").getAsFloat()
                                : DEFAULT_HITBOX_HEIGHT,
                        object.get("model_class_name").getAsString().trim(),
                        object.has("outer_model_class_name") && !object.get("outer_model_class_name").isJsonNull()
                                ? object.get("outer_model_class_name").getAsString().trim()
                                : null,
                        object.has("animation_class_name") && !object.get("animation_class_name").isJsonNull()
                                ? object.get("animation_class_name").getAsString().trim()
                                : null,
                        object.get("texture_resource_path").getAsString().trim(),
                        object.has("outer_texture_resource_path") && !object.get("outer_texture_resource_path").isJsonNull()
                                ? object.get("outer_texture_resource_path").getAsString().trim()
                                : null
                ));
            });

            return definitions;
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to read packaged study creature manifest: {}", CREATURE_MANIFEST, e);
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

    public record CustomCreatureDefinition(
            String id,
            String displayName,
            float hitboxWidth,
            float hitboxHeight,
            String modelClassName,
            String outerModelClassName,
            String animationClassName,
            String textureResourcePath,
            String outerTextureResourcePath
    ) {
    }
}