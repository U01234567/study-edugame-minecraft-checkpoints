package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

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
		StudyEntities.initialise();
		StudyCreatureCards.validateCustomCreatureCards();
		StudyCreatureCards.validateCreaturePlacements();
		StudyCreatureCards.validateStudyHotbarConfiguration();
		StudyInteractionController.initializeCommon();
		registerTestingCommands();

		// LOG: player joins world
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			suppressAdvancementNotifications(server);
			StudyInteractionController.applyConfiguredPlayerMode(handler.player);
			StudyEventLog.logPlayerJoined(handler.player.getName().getString());
		});

		// LOG: player leaves world
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			StudyEventLog.logGameEnded(handler.player.getName().getString(), "left_world");
		});
	}

	private static void registerTestingCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("timetravel")
					.requires(source -> StudyConfig.isTestingPhase())
					.executes(context -> executeTimetravelCommand(context.getSource()))
			);
		});
	}

	private static int executeTimetravelCommand(CommandSourceStack source) {
		boolean requested = StudyFlowController.requestTestingSkipToLastRemainingMs(
				source.getTextName(),
				StudyConfig.getTestingSkipRemainingMs()
		);

		if (!requested) {
			source.sendFailure(Component.literal("Could not request the study testing skip."));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Study testing skip requested."), false);
		return 1;
	}

	// Suppress vanilla advancement announcements in chat for the study world.
	private static void suppressAdvancementNotifications(MinecraftServer server) {
		server.execute(() -> {
			try {
				server.getCommands().performPrefixedCommand(
						server.createCommandSourceStack(),
						"gamerule announceAdvancements false"
				);
			} catch (Exception e) {
				LOGGER.warn("Could not disable advancement announcements automatically.", e);
			}
		});
	}
}