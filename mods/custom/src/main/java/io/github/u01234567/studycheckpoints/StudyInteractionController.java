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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Central place for participant interaction restrictions. Instead of keeping the player
 * in creative mode, we enforce a restrictive adventure-style state and make the player
 * invulnerable so they cannot die.
 */
public final class StudyInteractionController {
    private static final long MOVEMENT_SAMPLE_INTERVAL_MS = 1_000L;
    private static final double MOB_TARGET_CLEAR_RADIUS = 24.0D;

    private static long serverTickCounter;

    private static String blockedScreenKey;
    private static long lastMovementSampleMs;
    private static double lastSampleX;
    private static double lastSampleY;
    private static double lastSampleZ;
    private static double totalDistance;
    private static double totalSprintDistance;
    private static boolean previousOnGround;
    private static StudyChapter trackedChapter;

    private StudyInteractionController() {
    }

    public static void initializeCommon() {
        ServerTickEvents.END_SERVER_TICK.register(StudyInteractionController::onEndServerTick);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            StudyCreatureCards.CreatureCard card = StudyCreatureCards.get(entity.getType());
            if (card == null) {
                return InteractionResult.PASS;
            }

            StudyEventLog.logCreatureCardOpened(
                    player.getName().getString(),
                    entityTypeId(entity),
                    card.displayName(),
                    entity.getUUID().toString(),
                    entity.blockPosition().toShortString()
            );
            return InteractionResult.SUCCESS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
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

            Minecraft client = Minecraft.getInstance();
            client.execute(() -> client.setScreen(new StudyCreatureInfoScreen(card)));
            return InteractionResult.SUCCESS;
        });
    }

    private static void onEndServerTick(MinecraftServer server) {
        serverTickCounter++;

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

        enforceClientPlayerState(client.player);
        blockForbiddenScreens(client);
        logBlockedKeyAttempts(client);
        sampleMovement(client.player);
    }

    private static void enforceServerPlayerState(ServerPlayer player) {
        setAdventureModeReflectively(player);

        player.setInvulnerable(true);
        player.clearFire();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.getAbilities().instabuild = false;
        invokeNoArgs(player, "onUpdateAbilities");
    }

    private static void enforceClientPlayerState(LocalPlayer player) {
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.getAbilities().instabuild = false;
        invokeNoArgs(player, "onUpdateAbilities");
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

    private static void blockForbiddenScreens(Minecraft client) {
        Screen screen = client.screen;
        if (screen == null) {
            blockedScreenKey = null;
            return;
        }

        if (screen instanceof StudyOverlayScreen
                || screen instanceof StudyCheckpointScreen
                || screen instanceof StudyPauseScreen
                || screen instanceof StudyCreatureInfoScreen) {
            blockedScreenKey = null;
            return;
        }

        String screenName = screen.getClass().getSimpleName();

        if (!StudyConfig.isEscapeMenuAllowed() && screenName.contains("PauseScreen")) {
            logBlockedScreenOnce(client, "escape_menu_blocked");
            client.setScreen(null);
            return;
        }

        if (isInventoryLikeScreen(screen)) {
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
        if (client.options.keyInventory.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "inventory_key_blocked", "key=inventory");
        }

        if (client.options.keyPickItem.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "pick_block_key_blocked", "key=pick_item");
        }

        if (client.options.keyDrop.consumeClick()) {
            StudyEventLog.logBlockedAction(client.player.getName().getString(), "drop_key_blocked", "key=drop");
        }

        if (client.options.keySwapOffhand.consumeClick()) {
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setAdventureModeReflectively(ServerPlayer player) {
        try {
            Class enumClass = Class.forName("net.minecraft.world.level.GameType");
            Object adventureValue = Enum.valueOf(enumClass, "ADVENTURE");
            Method method = player.getClass().getMethod("setGameMode", enumClass);
            method.invoke(player, adventureValue);
        } catch (Exception e) {
            // Keep the study running even if mappings differ.
                }
    }

    private static void invokeNoArgs(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception ignored) {
            // Best-effort only.
        }
    }
}