package io.github.u01234567.studycheckpoints;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

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
		StudyInteractionController.initializeCommon();

		// LOG: player joins world
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			StudyEventLog.logSessionHeader();
			StudyEventLog.logPlayerJoined(handler.player.getName().getString());
		});

		// LOG: player leaves world
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			StudyEventLog.logGameEnded(handler.player.getName().getString(), "left_world");
		});
	}
}