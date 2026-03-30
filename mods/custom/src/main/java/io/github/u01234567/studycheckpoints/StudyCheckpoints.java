package io.github.u01234567.studycheckpoints;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyCheckpoints implements ModInitializer {
	public static final String MOD_ID = "study-checkpoints";

	// Runtime save folder
	public static final String STUDY_WORLD_SAVE_NAME = "experiment-world";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Study Checkpoints mod initialized.");

		// LOG: player joins world
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			StudyEventLog.logSessionHeader();
			StudyEventLog.logPlayerJoined(handler.player.getName().getString());
		});

		// LOG: player leaves world
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			StudyEventLog.logGameEnded(handler.player.getName().getString(), "left_world");
		});

		// LOG: right-clicks on cow
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			// Restrict: ignore the client-side
			if (world.isClientSide()) {
				return InteractionResult.PASS;
			}

			// Restrict: ignore off-hand interactions
			if (hand != InteractionHand.MAIN_HAND) {
				return InteractionResult.PASS;
			}

			if (entity.getType() == EntityType.COW) {
				StudyEventLog.logCowClick(
						player.getName().getString(),
						"use",
						entity.getUUID().toString(),
						entity.blockPosition().toShortString()
				);
			}

			return InteractionResult.PASS;
		});

		// LOG: left-clicks on cow
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			// Restrict: ignore the client-side
			if (world.isClientSide()) {
				return InteractionResult.PASS;
			}

			// Restrict: ignore off-hand interactions
			if (hand != InteractionHand.MAIN_HAND) {
				return InteractionResult.PASS;
			}

			if (entity.getType() == EntityType.COW) {
				StudyEventLog.logCowClick(
						player.getName().getString(),
						"attack",
						entity.getUUID().toString(),
						entity.blockPosition().toShortString()
				);
			}

			return InteractionResult.PASS;
		});
	}
}