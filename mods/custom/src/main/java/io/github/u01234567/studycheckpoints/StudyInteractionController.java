package io.github.u01234567.studycheckpoints;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Central place for participant interaction restrictions and managed study-creature state.
 * In the real study, the player is kept in a restrictive adventure-style state and made
 * invulnerable. In TESTING_PHASE, the player is switched to creative mode once on join
 * and most participant restrictions are relaxed for local development.
 */
public final class StudyInteractionController {
    private static final long MOVEMENT_SAMPLE_INTERVAL_MS = 1_000L;
    private static final double MOB_TARGET_CLEAR_RADIUS = 24.0D;

    // Runtime roster of the study creatures that currently exist in the world (based on chapter).
    private static final Map<UUID, SpawnedStudyCreature> ACTIVE_STUDY_CREATURES = new HashMap<>();

    // To log which uniquely named creatures the participant has already clicked at least once.
    private static final Set<String> INTERACTED_CREATURE_NAMES = new HashSet<>();

    private static long serverTickCounter;

    private static String blockedScreenKey;
    private static boolean alternatePauseMenuOpen;
    private static long lastMovementSampleMs;
    private static double lastSampleX;
    private static double lastSampleY;
    private static double lastSampleZ;
    private static double totalDistance;
    private static double totalSprintDistance;
    private static boolean previousOnGround;
    private static StudyChapter trackedChapter;
    private static StudyChapter prewarmedChapter;

    private StudyInteractionController() {
    }

    public static void initializeCommon() {
        ServerTickEvents.END_SERVER_TICK.register(StudyInteractionController::onEndServerTick);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            StudyCreatureCards.CreatureCard card = StudyCreatureCards.get(entity.getType());
            SpawnedStudyCreature spawnedStudyCreature = ACTIVE_STUDY_CREATURES.get(entity.getUUID());

            if (card == null || spawnedStudyCreature == null) {
                return InteractionResult.PASS;
            }

            boolean interactedBefore = INTERACTED_CREATURE_NAMES.contains(spawnedStudyCreature.creatureName());
            INTERACTED_CREATURE_NAMES.add(spawnedStudyCreature.creatureName());

            StudyEventLog.logCreatureCardOpened(
                    player.getName().getString(),
                    entityTypeId(entity),
                    card.displayName(),
                    spawnedStudyCreature.creatureName(),
                    entity.getUUID().toString(),
                    entity.blockPosition().toShortString(),
                    interactedBefore
            );
            return InteractionResult.SUCCESS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }
            if (StudyConfig.isTestingPhase()) {
                return InteractionResult.PASS;
            }

            StudyEventLog.logBlockedAction(
                    player.getName().getString(),
                    "attack_entity_blocked",
                    "entity_type=" + entityTypeId(entity)
            );
            return InteractionResult.FAIL;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            if (StudyConfig.isTestingPhase()) {
                return InteractionResult.PASS;
            }

            StudyEventLog.logBlockedAction(
                    player.getName().getString(),
                    "attack_block_blocked",
                    "block_pos=" + pos.toShortString()
            );
            return InteractionResult.FAIL;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }
            if (StudyConfig.isTestingPhase()) {
                return InteractionResult.PASS;
            }

            StudyEventLog.logBlockedAction(
                    player.getName().getString(),
                    "use_block_blocked",
                    "block_pos=" + hitResult.getBlockPos().toShortString()
            );
            return InteractionResult.FAIL;
        });
    }

    public static void initializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(StudyInteractionController::onEndClientTick);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            StudyCreatureCards.CreatureCard card = StudyCreatureCards.get(entity.getType());
            if (card == null) {
                return InteractionResult.PASS;
            }

            String creatureId = StudyCreatureCards.creatureIdForEntityType(entity.getType());
            if (creatureId != null) {
                StudyChapterHotbarTracker.markCreatureInteracted(creatureId);
            }

            Minecraft client = Minecraft.getInstance();
            client.execute(() -> client.setScreen(new StudyCreatureInfoScreen(card)));
            return InteractionResult.SUCCESS;
        });
    }

    private static void onEndServerTick(MinecraftServer server) {
        serverTickCounter++;

        enforceNoFreeMobSpawning(server);
        enforceEntityWhitelist(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            enforceServerPlayerState(player);

            if (serverTickCounter % 10L == 0L) {
                clearNearbyMobTargets(player);
            }
        }
    }

    private static void onEndClientTick(Minecraft client) {
        if (client == null || client.player == null || client.level == null) {
            return;
        }

        handleStudyHotkeys(client);
        blockForbiddenScreens(client);
        logBlockedKeyAttempts(client);
        sampleMovement(client.player);
    }

    public static void applyConfiguredPlayerMode(ServerPlayer player) {
        if (StudyConfig.isTestingPhase()) {
            setGameModeReflectively(player, "CREATIVE");
        } else {
            setGameModeReflectively(player, "ADVENTURE");
        }
    }

    private static void enforceServerPlayerState(ServerPlayer player) {
        if (StudyConfig.isTestingPhase()) {
            return;
        }

        player.setInvulnerable(true);
        player.clearFire();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
    }

    /**
     * Server becomes source of truth for creature existence:
     * - optionally keep the next chapter area warm before the participant gets there
     * - when starting a chapter, prepare that target area on the server first
     * - only then notify the client to reveal the scene
     */
    public static void prewarmChapterArea(Minecraft client, StudyChapter chapter) {
        if (client == null || chapter == null) {
            return;
        }
        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) {
            return;
        }

        server.execute(() -> prewarmChapterArea(server, chapter));
    }

    public static void prepareChapterForStart(Minecraft client, StudyChapter chapter, Runnable onPrepared) {
        if (client == null || chapter == null || onPrepared == null) {
            return;
        }

        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) {
            StudyCheckpoints.LOGGER.error("Could not prepare the chapter because the integrated server is unavailable.");
            client.execute(onPrepared);
            return;
        }

        server.execute(() -> {
            prewarmChapterArea(server, chapter);
            prepareCreaturesForChapter(server, chapter);
            client.execute(onPrepared);
        });
    }

    private static void prewarmChapterArea(MinecraftServer server, StudyChapter chapter) {
        ServerLevel level = server.overworld();
        if (level == null || chapter == null) {
            return;
        }

        if (prewarmedChapter == chapter) {
            return;
        }

        if (prewarmedChapter != null) {
            setChapterChunksForced(level, prewarmedChapter, false);
        }

        setChapterChunksForced(level, chapter, true);
        prewarmedChapter = chapter;
    }

    private static void prepareCreaturesForChapter(MinecraftServer server, StudyChapter chapter) {
        clearAllNonPlayerLivingCreatures(server);
        ACTIVE_STUDY_CREATURES.clear();

        ServerLevel level = server.overworld();
        if (level == null) {
            throw new IllegalStateException("Could not find the overworld while preparing chapter creatures.");
        }

        for (StudyCreatureCards.CreatureSpawnAssignment assignment : StudyCreatureCards.spawnAssignmentsForChapter(chapter)) {
            boolean spawned = spawnConfiguredCreature(level, assignment);
            if (!spawned) {
                throw new IllegalStateException("Failed to spawn configured study creature: " + assignment.spawn().uniqueName());
            }
        }
    }

    private static void setChapterChunksForced(ServerLevel level, StudyChapter chapter, boolean forced) {
        int chunkRadius = Math.max(0, StudyConfig.getChapterPrewarmChunkRadius());
        int centreChunkX = Math.floorDiv(chapter.x(), 16);
        int centreChunkZ = Math.floorDiv(chapter.z(), 16);

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int chunkX = centreChunkX + dx;
                int chunkZ = centreChunkZ + dz;
                level.setChunkForced(chunkX, chunkZ, forced);

                if (forced) {
                    level.getChunk(chunkX, chunkZ);
                }
            }
        }
    }

    private static void clearAllNonPlayerLivingCreatures(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> entitiesToRemove = new ArrayList<>();

            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ServerPlayer) {
                    continue;
                }

                if (entity instanceof LivingEntity) {
                    entitiesToRemove.add(entity);
                }
            }

            for (Entity entity : entitiesToRemove) {
                ACTIVE_STUDY_CREATURES.remove(entity.getUUID());
                entity.discard();
            }
        }
    }

    private static boolean spawnConfiguredCreature(ServerLevel level,
                                                   StudyCreatureCards.CreatureSpawnAssignment assignment) {
        Entity entity = assignment.entityType().create(level, EntitySpawnReason.COMMAND);
        String configuredBlockPos = assignment.spawn().x()
                + "," + assignment.spawn().y()
                + "," + assignment.spawn().z();

        if (entity == null) {
            StudyEventLog.logStudyCreatureSpawned(
                    entityTypeId(assignment.entityType()),
                    assignment.card().displayName(),
                    assignment.spawn().uniqueName(),
                    "not_created",
                    assignment.card().chapter().displayTitle(),
                    configuredBlockPos,
                    assignment.spawn().facing().name().toLowerCase(Locale.ROOT),
                    assignment.card().movementMode().name().toLowerCase(Locale.ROOT),
                    false
            );
            return false;
        }

        moveEntityReflectively(
                assignment.spawn().centerX(),
                assignment.spawn().y(),
                assignment.spawn().centerZ(),
                assignment.spawn().facing().yawDegrees(),
                0.0F,
                entity
        );
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        entity.setInvulnerable(true);

        SpawnedStudyCreature spawnedStudyCreature = new SpawnedStudyCreature(
                assignment.card().displayName(),
                assignment.spawn().uniqueName(),
                entity.getUUID().toString(),
                assignment.card().chapter(),
                assignment.card().movementMode(),
                assignment.spawn().facing(),
                configuredBlockPos
        );

        applyManagedCreatureState(entity, spawnedStudyCreature);

        boolean added = level.addFreshEntity(entity);
        if (added) {
            ACTIVE_STUDY_CREATURES.put(entity.getUUID(), spawnedStudyCreature);
            keepStudyCreatureAlive(entity, spawnedStudyCreature);
        }

        StudyEventLog.logStudyCreatureSpawned(
                entityTypeId(assignment.entityType()),
                assignment.card().displayName(),
                assignment.spawn().uniqueName(),
                entity.getUUID().toString(),
                assignment.card().chapter().displayTitle(),
                configuredBlockPos,
                assignment.spawn().facing().name().toLowerCase(Locale.ROOT),
                assignment.card().movementMode().name().toLowerCase(Locale.ROOT),
                added
        );

        return added;
    }

    private static void enforceNoFreeMobSpawning(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            disableDoMobSpawningReflectively(level, server);
        }
    }

    /**
     * Enforce a strict world whitelist:
     * - keep players
     * - keep only the current study roster
     * - remove all other living entities
     */
    private static void enforceEntityWhitelist(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> entitiesToRemove = new ArrayList<>();

            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ServerPlayer) {
                    continue;
                }

                if (!(entity instanceof LivingEntity)) {
                    continue;
                }

                SpawnedStudyCreature spawnedStudyCreature = ACTIVE_STUDY_CREATURES.get(entity.getUUID());
                if (spawnedStudyCreature != null) {
                    keepStudyCreatureAlive(entity, spawnedStudyCreature);
                    continue;
                }

                entitiesToRemove.add(entity);
            }

            for (Entity entity : entitiesToRemove) {
                entity.discard();
            }
        }
    }

    /**
     * Keep spawned study creatures persistent and safe:
     * - make them invulnerable
     * - extinguish fire
     * - restore health
     * - enforce the configured FIXED/FREE movement rule
     */
    private static void keepStudyCreatureAlive(Entity entity, SpawnedStudyCreature spawnedStudyCreature) {
        entity.setInvulnerable(true);
        entity.clearFire();

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }

        if (entity instanceof Mob mob) {
            mob.setPersistenceRequired();
            applyManagedCreatureState(entity, spawnedStudyCreature);
        }
    }

    private static void applyManagedCreatureState(Entity entity, SpawnedStudyCreature spawnedStudyCreature) {
        if (!(entity instanceof Mob mob)) {
            return;
        }

        if (spawnedStudyCreature.movementMode() == StudyCreatureCards.CreatureMovementMode.FIXED) {
            applyManagedCreatureFacing(mob, spawnedStudyCreature.facing());
            mob.setNoAi(true);
            mob.setTarget(null);
            mob.getNavigation().stop();
            mob.setDeltaMovement(0.0D, 0.0D, 0.0D);
        } else {
            mob.setNoAi(false);
        }
    }

    private static void applyManagedCreatureFacing(Mob mob, StudyCreatureCards.FacingDirection facing) {
        float yaw = facing.yawDegrees();
        mob.setYRot(yaw);
        mob.yRotO = yaw;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
        mob.setYHeadRot(yaw);
        mob.yHeadRotO = yaw;
        mob.setYBodyRot(yaw);
        mob.yBodyRotO = yaw;
    }

    private static void clearNearbyMobTargets(ServerPlayer player) {
        for (Mob mob : player.level().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(MOB_TARGET_CLEAR_RADIUS)
        )) {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
        }
    }

    /**
     * Handle study-specific keyboard behaviour:
     * - Escape always becomes a recovery action back to the active chapter start.
     * - The alternate menu key uses the player-list binding, which is Tab by default.
     * - The alternate menu key only opens the menu when TESTING_PHASE is true.
     */
    private static void handleStudyHotkeys(Minecraft client) {
        Screen screen = client.screen;

        if (screen == null) {
            alternatePauseMenuOpen = false;

            if (client.options.keyPlayerList.consumeClick()) {
                if (StudyConfig.isTestingPhase()) {
                    alternatePauseMenuOpen = openAllowedPauseMenu(client);
                } else {
                    StudyEventLog.logBlockedAction(
                            client.player.getName().getString(),
                            "alternate_menu_key_blocked",
                            "key=player_list_default_tab"
                    );
                }
            }
            return;
        }

        // Never interfere with the study-owned overlays.
        if (screen instanceof StudyOverlayScreen
                || screen instanceof StudyCheckpointScreen
                || screen instanceof StudyPauseScreen
                || screen instanceof StudyLoadingScreen
                || screen instanceof StudyCreatureInfoScreen) {
            alternatePauseMenuOpen = false;
            return;
        }

        // Escape cannot be disabled reliably, so treat any vanilla pause screen as an
        // Escape-triggered recovery request and immediately return the participant.
        if (isVanillaPauseScreen(screen)) {
            if (alternatePauseMenuOpen && StudyConfig.isTestingPhase()) {
                return;
            }

            alternatePauseMenuOpen = false;
            client.setScreen(null);

            boolean moved = StudyFlowController.returnPlayerToActiveChapterStart(client, "escape");
            if (!moved) {
                StudyEventLog.logBlockedAction(
                        client.player != null ? client.player.getName().getString() : "unknown",
                        "escape_recovery_ignored",
                        "reason=no_active_chapter"
                );
            }
            return;
        }

        alternatePauseMenuOpen = false;
    }

    private static boolean isVanillaPauseScreen(Screen screen) {
        return screen != null && "PauseScreen".equals(screen.getClass().getSimpleName());
    }

    /**
     * Open the vanilla pause menu reflectively so the study code stays resilient if the
     * exact constructor signature changes between mappings or minor versions.
     */
    private static boolean openAllowedPauseMenu(Minecraft client) {
        try {
            Class<?> pauseScreenClass = Class.forName("net.minecraft.client.gui.screens.PauseScreen");

            Object screenInstance;
            try {
                Constructor<?> constructor = pauseScreenClass.getConstructor(boolean.class);
                constructor.setAccessible(true);
                screenInstance = constructor.newInstance(true);
            } catch (NoSuchMethodException ignored) {
                Constructor<?> constructor = pauseScreenClass.getConstructor();
                constructor.setAccessible(true);
                screenInstance = constructor.newInstance();
            }

            if (screenInstance instanceof Screen screen) {
                client.setScreen(screen);
                return true;
            }

            throw new IllegalStateException("PauseScreen did not produce a Screen instance.");
        } catch (Exception e) {
            StudyCheckpoints.LOGGER.warn("Could not open the alternate pause menu key.", e);
        }

        return false;
    }

    private static void blockForbiddenScreens(Minecraft client) {
        Screen screen = client.screen;
        if (screen == null) {
            blockedScreenKey = null;
            alternatePauseMenuOpen = false;
            return;
        }

        if (screen instanceof StudyOverlayScreen
                || screen instanceof StudyCheckpointScreen
                || screen instanceof StudyPauseScreen
                || screen instanceof StudyLoadingScreen
                || screen instanceof StudyCreatureInfoScreen) {
            blockedScreenKey = null;
            alternatePauseMenuOpen = false;
            return;
        }

        if (isVanillaPauseScreen(screen)) {
            if (alternatePauseMenuOpen && StudyConfig.isTestingPhase()) {
                blockedScreenKey = null;
                return;
            }

            alternatePauseMenuOpen = false;
            logBlockedScreenOnce(client, "escape_menu_blocked");
            client.setScreen(null);
            return;
        }

        if (!StudyConfig.isTestingPhase() && isChatLikeScreen(screen)) {
            logBlockedScreenOnce(client, "chat_screen_blocked");
            client.setScreen(null);
            return;
        }

        if (!StudyConfig.isTestingPhase() && isInventoryLikeScreen(screen)) {
            logBlockedScreenOnce(client, "inventory_screen_blocked");
            client.setScreen(null);
            return;
        }

        blockedScreenKey = null;
    }

    private static boolean isInventoryLikeScreen(Screen screen) {
        String screenName = screen.getClass().getSimpleName();
        return screenName.contains("Inventory")
                || screenName.contains("Container")
                || screenName.contains("CreativeMode");
    }

    private static boolean isChatLikeScreen(Screen screen) {
        String screenName = screen.getClass().getSimpleName();
        return screenName.contains("Chat");
    }

    private static void logBlockedScreenOnce(Minecraft client, String action) {
        if (action.equals(blockedScreenKey)) {
            return;
        }

        blockedScreenKey = action;
        StudyEventLog.logBlockedAction(
                client.player != null ? client.player.getName().getString() : "unknown",
                action,
                "screen_closed_immediately"
        );
    }

    private static void logBlockedKeyAttempts(Minecraft client) {
        if (!StudyConfig.isTestingPhase() && client.options.keyChat.consumeClick()) {
            StudyEventLog.logBlockedAction(
                    client.player.getName().getString(),
                    "chat_key_blocked",
                    "key=chat"
            );
        }

        if (!StudyConfig.isTestingPhase() && client.options.keyInventory.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "inventory_key_blocked", "key=inventory");
        }

        if (!StudyConfig.isTestingPhase() && client.options.keyPickItem.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "pick_block_key_blocked", "key=pick_item");
        }

        if (!StudyConfig.isTestingPhase() && client.options.keyDrop.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "drop_key_blocked", "key=drop");
        }

        if (!StudyConfig.isTestingPhase() && client.options.keySwapOffhand.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "swap_offhand_key_blocked", "key=swap_offhand");
        }
    }

    private static void sampleMovement(LocalPlayer player) {
        if (!StudyFlowController.isChapterActive()) {
            trackedChapter = null;
            lastMovementSampleMs = 0L;
            totalDistance = 0.0D;
            totalSprintDistance = 0.0D;
            return;
        }

        StudyChapter activeChapter = StudyFlowController.getActiveChapter();
        long now = System.currentTimeMillis();

        if (trackedChapter != activeChapter || lastMovementSampleMs == 0L) {
            trackedChapter = activeChapter;
            lastMovementSampleMs = now;
            lastSampleX = player.getX();
            lastSampleY = player.getY();
            lastSampleZ = player.getZ();
            totalDistance = 0.0D;
            totalSprintDistance = 0.0D;
            previousOnGround = player.onGround();
            return;
        }

        double dxTick = player.getX() - lastSampleX;
        double dzTick = player.getZ() - lastSampleZ;
        double sampleDistance = Math.sqrt((dxTick * dxTick) + (dzTick * dzTick));

        totalDistance += sampleDistance;
        if (player.isSprinting()) {
            totalSprintDistance += sampleDistance;
        }

        if (previousOnGround && !player.onGround() && player.getDeltaMovement().y > 0.0D) {
            StudyEventLog.logJump(
                    player.getName().getString(),
                    activeChapter.displayTitle(),
                    player.getX(),
                    player.getY(),
                    player.getZ()
            );
        }
        previousOnGround = player.onGround();

        if (now - lastMovementSampleMs >= MOVEMENT_SAMPLE_INTERVAL_MS) {
            StudyEventLog.logMovementSample(
                    player.getName().getString(),
                    activeChapter.displayTitle(),
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    sampleDistance,
                    totalDistance,
                    totalSprintDistance,
                    player.isSprinting(),
                    player.onGround()
            );

            lastMovementSampleMs = now;
            lastSampleX = player.getX();
            lastSampleY = player.getY();
            lastSampleZ = player.getZ();
        }
    }

    private static String entityTypeId(Entity entity) {
        return String.valueOf(entity.getType()).toLowerCase(Locale.ROOT);
    }

    private static String entityTypeId(EntityType<?> entityType) {
        return String.valueOf(entityType).toLowerCase(Locale.ROOT);
    }

    private static void moveEntityReflectively(double x,
                                               double y,
                                               double z,
                                               float yaw,
                                               float pitch,
                                               Entity entity) {
        try {
            Method moveToMethod = entity.getClass().getMethod(
                    "moveTo",
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class
            );
            moveToMethod.setAccessible(true);
            moveToMethod.invoke(entity, x, y, z, yaw, pitch);
            return;
        } catch (Exception ignored) {
            // Try the next candidate method name.
        }

        try {
            Method snapToMethod = entity.getClass().getMethod(
                    "snapTo",
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class
            );
            snapToMethod.setAccessible(true);
            snapToMethod.invoke(entity, x, y, z, yaw, pitch);
        } catch (Exception e) {
            throw new IllegalStateException("Could not position spawned study creature.", e);
        }
    }

    private static void disableDoMobSpawningReflectively(ServerLevel level, MinecraftServer server) {
        try {
            Object gameRules = level.getGameRules();
            Class<?> gameRulesClass = gameRules.getClass();

            Field ruleField = gameRulesClass.getDeclaredField("RULE_DOMOBSPAWNING");
            ruleField.setAccessible(true);
            Object ruleKey = ruleField.get(null);

            Method getRuleMethod = gameRulesClass.getMethod("getRule", ruleField.getType());
            Object ruleValue = getRuleMethod.invoke(gameRules, ruleKey);

            Method getMethod = ruleValue.getClass().getMethod("get");
            Object currentValue = getMethod.invoke(ruleValue);

            if (!(currentValue instanceof Boolean booleanValue) || !booleanValue) {
                return;
            }

            for (Method method : ruleValue.getClass().getMethods()) {
                if (!method.getName().equals("set") || method.getParameterCount() != 2) {
                    continue;
                }

                method.setAccessible(true);
                method.invoke(ruleValue, false, server);
                return;
            }
        } catch (Exception e) {
            StudyCheckpoints.LOGGER.warn("Could not disable doMobSpawning reflectively.", e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setGameModeReflectively(ServerPlayer player, String gameTypeName) {
        try {
            Class enumClass = Class.forName("net.minecraft.world.level.GameType");
            Object gameTypeValue = Enum.valueOf(enumClass, gameTypeName);
            Method method = player.getClass().getMethod("setGameMode", enumClass);
            method.invoke(player, gameTypeValue);
        } catch (Exception e) {
            // Keep the study running even if mappings differ.
        }
    }

    private record SpawnedStudyCreature(
            String creatureLabel,
            String creatureName,
            String creatureUuid,
            StudyChapter chapter,
            StudyCreatureCards.CreatureMovementMode movementMode,
            StudyCreatureCards.FacingDirection facing,
            String configuredBlockPos
    ) {
    }
}