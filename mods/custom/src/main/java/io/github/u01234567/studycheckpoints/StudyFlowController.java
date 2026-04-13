package io.github.u01234567.studycheckpoints;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.lwjgl.glfw.GLFW;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Central controller for the current state of the study:
 * 1. show intro once
 * 2. wait for explicit consent
 * 3. run exactly one chapter at a time
 * 4. show the two-slide intro transition before Chapter 0, then manipulated checkpoints between chapters 1→2 and 2→3
 * 5. hand off to Qualtrics at the very end
 */
public final class StudyFlowController {
    private static final int TIMER_COLOR_NORMAL = 0xFFFFFFFF;
    private static final int TIMER_COLOR_WARNING = 0xFFFF4D4D;
    private static final long WARNING_THRESHOLD_MS = 15_000L;
    private static final long RECOVERY_MESSAGE_DURATION_MS = 3_000L;
    private static final long CLIENT_CHAPTER_READY_STABILITY_MS = 500L;
    private static final long CHAPTER_WELCOME_DURATION_MS = 5_000L;
    private static final long CHAPTER_ZERO_COMPLETION_DELAY_MS = 5_000L;
    private static final double CHAPTER_ZERO_DEPTH_TARGET_Y = 59.0D;
    private static final long CHAPTER_ZERO_VALIDATION_MESSAGE_DURATION_MS = 10_000L;
    private static final long QUESTIONNAIRE_CLOSE_DELAY_MS = 10_000L;
    private static final float CHAPTER_LOOK_PITCH_DEGREES = 45.0F;
    private static final int RECOVERY_MESSAGE_COLOUR = 0xFFFFFFFF;
    private static final int INITIAL_CHECKPOINT_COMPLETED_CHAPTER_NUMBER = -1;

    private static boolean introShown;
    private static boolean windowPrepared;

    private static StudyChapter loadingChapter;
    private static long loadingScreenOpenedAtMs;
    private static long loadingClientReadySinceMs;
    private static boolean loadingPreparationComplete;

    private static StudyChapter activeChapter;
    private static long chapterDeadlineMs;
    private static String chapterWelcomeMessage;
    private static long chapterWelcomeDeadlineMs;
    private static String recoveryMessage;
    private static long recoveryMessageDeadlineMs;
    private static java.util.List<String> chapterZeroValidationMessages;
    private static long chapterZeroValidationMessageDeadlineMs;
    private static long questionnaireFirstOpenedAtMs;
    private static StudyChapter pausedCompletedChapter;
    private static long pauseDeadlineMs;
    private static boolean chapterZeroReachedDepth;
    private static long chapterZeroCompletionDeadlineMs;

    private static StudyConditionAllocator.ConditionAssignment conditionAssignment;

    private StudyFlowController() {
    }

    public static void initializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(StudyFlowController::onEndClientTick);

        // Remove vanilla survival HUD elements that are not relevant to the study:
        // - hearts
        // - hunger
        // - experience bar / level
        HudElementRegistry.removeElement(VanillaHudElements.HEALTH_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.FOOD_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.INFO_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.EXPERIENCE_LEVEL);

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(StudyCheckpoints.MOD_ID, "study_timer"),
                StudyFlowController::renderTimerHud
        );
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(StudyCheckpoints.MOD_ID, "study_chapter_hotbar_tracker"),
                StudyChapterHotbarTracker::render
        );
    }

    public static void acceptConsent(Minecraft client) {
        StudyEventLog.logConsentChoice("agree_and_continue");
        StudyInteractionController.resetInteractionProgress();

        client.execute(() -> {
            openInitialCheckpoint(client);
        });
    }

    public static void stopHere(Minecraft client) {
        StudyEventLog.logConsentChoice("stop_here");
        StudyEventLog.logGameEnded(playerName(client), "intro_declined");
        requestClientStop(client);
    }

    // Close the client from the final questionnaire screen.
    public static void closeAfterQuestionnaire(Minecraft client) {
        StudyEventLog.logGameEnded(playerName(client), "questionnaire_completed");
        requestClientStop(client);
    }

    public static StudyExperimentCondition getAssignedCondition() {
        ensureConditionAssigned();
        return conditionAssignment.condition();
    }

    public static boolean isChapterActive() {
        return activeChapter != null;
    }

    public static StudyChapter getActiveChapter() {
        return activeChapter;
    }

    // Press ESC to teleport back to the start location of the current chapter.
    // Also show a short action-bar message so the participant understands what happened.
    public static boolean returnPlayerToActiveChapterStart(Minecraft client, String triggerLabel) {
        if (client == null || activeChapter == null) {
            return false;
        }

        client.execute(() -> {
            StudyChapter chapterToRestore = activeChapter;

            client.setScreen(null);
            placePlayerForChapter(client, chapterToRestore);
            recoveryMessage = StudyConfig.getEscapeRecoveryMessage();
            recoveryMessageDeadlineMs = nowMs() + RECOVERY_MESSAGE_DURATION_MS;

            if (chapterToRestore == StudyChapter.CHAPTER_0) {
                clearChapterZeroValidationMessage();

                if (chapterZeroReachedDepth) {
                    StudyEventLog.logChapterZeroConditionSatisfied(
                            "pressed_escape_after_reaching_y_59_or_below",
                            playerName(client),
                            client.player != null ? client.player.getX() : 0.0D,
                            client.player != null ? client.player.getY() : 0.0D,
                            client.player != null ? client.player.getZ() : 0.0D
                    );
                }

                if (chapterZeroCompletionDeadlineMs == 0L) {
                    if (armChapterZeroCompletionIfReady(client)) {
                        clearChapterZeroValidationMessage();
                    } else {
                        showChapterZeroValidationMessage(buildChapterZeroMissingRequirementLines());
                    }
                }
            }

            StudyEventLog.logBlockedAction(
                    playerName(client),
                    "chapter_recovery_teleport",
                    "trigger=" + triggerLabel + ",chapter=" + chapterToRestore.chapterNumber()
            );
        });
        return true;
    }

    public static void continueFromInitialCheckpoint(Minecraft client) {
        StudyChapter firstChapter = StudyChapter.first();
        if (firstChapter == null) {
            return;
        }

        StudyEventLog.logCheckpointChoice(
                INITIAL_CHECKPOINT_COMPLETED_CHAPTER_NUMBER,
                firstChapter.chapterNumber(),
                getAssignedCondition().id(),
                "continue"
        );

        client.execute(() -> {
            client.setScreen(null);
            startChapter(client, firstChapter);
        });
    }

    public static void continueFromChapterZeroTransition(Minecraft client) {
        StudyChapter completedChapter = StudyChapter.CHAPTER_0;
        StudyChapter nextChapter = completedChapter.next();
        if (nextChapter == null) {
            return;
        }

        StudyEventLog.logCheckpointChoice(
                completedChapter.chapterNumber(),
                nextChapter.chapterNumber(),
                getAssignedCondition().id(),
                "continue"
        );

        client.execute(() -> {
            client.setScreen(null);
            startChapter(client, nextChapter);
        });
    }

    public static void continueFromFinalScreen(Minecraft client, StudyChapter completedChapter) {
        client.execute(() -> openQuestionnaire(client));
    }

    public static boolean canCloseQuestionnaireScreen() {
        return questionnaireFirstOpenedAtMs > 0L
                && nowMs() >= questionnaireFirstOpenedAtMs + QUESTIONNAIRE_CLOSE_DELAY_MS;
    }

    // Testing helper: shorten the currently active timed study phase to the last N milliseconds.
    // This is intended for local development only.
    public static boolean requestTestingSkipToLastRemainingMs(String playerName, long remainingMs) {
        if (!StudyConfig.isTestingPhase()) {
            return false;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return false;
        }

        client.execute(() -> applyTestingSkip(client, playerName, remainingMs));
        return true;
    }

    public static void continueFromCheckpoint(Minecraft client,
                                              StudyChapter completedChapter,
                                              StudyExperimentCondition condition) {
        StudyChapter nextChapter = completedChapter.next();
        if (nextChapter == null) {
            return;
        }

        StudyEventLog.logCheckpointChoice(
                completedChapter.chapterNumber(),
                nextChapter.chapterNumber(),
                condition.id(),
                "continue"
        );

        advanceFromCompletedChapter(client, completedChapter);
    }

    public static void startPauseFromCheckpoint(Minecraft client,
                                                StudyChapter completedChapter,
                                                StudyExperimentCondition condition) {
        StudyChapter nextChapter = completedChapter.next();
        if (nextChapter == null) {
            return;
        }

        long pauseDurationMs = StudyConfig.getCheckpointPauseDurationMs();

        StudyEventLog.logCheckpointChoice(
                completedChapter.chapterNumber(),
                nextChapter.chapterNumber(),
                condition.id(),
                "break"
        );
        StudyEventLog.logCheckpointPauseStarted(
                completedChapter.chapterNumber(),
                nextChapter.chapterNumber(),
                pauseDurationMs
        );

        client.execute(() -> openPauseScreen(client, completedChapter, pauseDurationMs));
    }

    public static void renderTimerHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (activeChapter == null && chapterWelcomeMessage == null && recoveryMessage == null) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }

        if (activeChapter != null && activeChapter != StudyChapter.CHAPTER_0) {
            long remainingMs = Math.max(0L, chapterDeadlineMs - nowMs());
            int totalSeconds = (int) Math.ceil(remainingMs / 1000.0D);
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            String timerText = String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
            int colour = remainingMs <= WARNING_THRESHOLD_MS ? TIMER_COLOR_WARNING : TIMER_COLOR_NORMAL;

            int x = client.getWindow().getGuiScaledWidth() - client.font.width(timerText) - 12;
            int y = 10;
            graphics.text(client.font, timerText, x, y, colour, true);
        }

        if (chapterWelcomeMessage != null && nowMs() < chapterWelcomeDeadlineMs) {
            int messageX = (client.getWindow().getGuiScaledWidth() - client.font.width(chapterWelcomeMessage)) / 2;
            graphics.text(client.font, chapterWelcomeMessage, messageX, 10, 0xFFFFFFFF, true);
        } else {
            chapterWelcomeMessage = null;
        }

        if (recoveryMessage != null && nowMs() < recoveryMessageDeadlineMs) {
            int messageX = (client.getWindow().getGuiScaledWidth() - client.font.width(recoveryMessage)) / 2;
            int messageY = 24;
            graphics.text(client.font, recoveryMessage, messageX, messageY, RECOVERY_MESSAGE_COLOUR, true);
        } else {
            recoveryMessage = null;
        }

        if (chapterZeroValidationMessages != null && nowMs() < chapterZeroValidationMessageDeadlineMs) {
            int messageY = 38;
            for (String line : chapterZeroValidationMessages) {
                int messageX = (client.getWindow().getGuiScaledWidth() - client.font.width(line)) / 2;
                graphics.text(client.font, line, messageX, messageY, RECOVERY_MESSAGE_COLOUR, true);
                messageY += 12;
            }
        } else {
            chapterZeroValidationMessages = null;
        }
    }

    private static void onEndClientTick(Minecraft client) {
        if (client == null || client.level == null || client.player == null) {
            return;
        }

        ensureFullscreenAndFocus(client);
        ensureConditionAssigned();

        if (!introShown) {
            StudyInteractionController.prewarmChapterArea(client, StudyChapter.first());
            introShown = true;
            client.setScreen(StudyOverlayScreen.createIntroScreen());
            return;
        }

        if (loadingChapter != null) {
            tickChapterLoading(client);
        }

        if (activeChapter == StudyChapter.CHAPTER_0) {
            tickChapterZeroProgress(client);
        } else if (activeChapter != null && nowMs() >= chapterDeadlineMs) {
            finishActiveChapter(client);
        }
    }

    private static void ensureConditionAssigned() {
        if (conditionAssignment != null) {
            return;
        }

        conditionAssignment = StudyConditionAllocator.assignConditionForSession(
                StudyConfig.getForcedExperimentCondition()
        );

        StudyEventLog.logExperimentConditionAssigned(
                conditionAssignment.condition().id(),
                conditionAssignment.source(),
                conditionAssignment.condition().indicatorColourName(),
                conditionAssignment.countsFile().toString()
        );
    }

    private static void openInitialCheckpoint(Minecraft client) {
        StudyChapter firstChapter = StudyChapter.first();
        if (firstChapter == null) {
            return;
        }

        StudyInteractionController.prewarmChapterArea(client, firstChapter);
        StudyEventLog.logCheckpointDisplayed(
                INITIAL_CHECKPOINT_COMPLETED_CHAPTER_NUMBER,
                firstChapter.chapterNumber(),
                getAssignedCondition().id()
        );
        client.setScreen(StudyCheckpointScreen.createInitialIntroCheckpoint(getAssignedCondition()));
    }

    private static void startChapter(Minecraft client, StudyChapter chapter) {
        if (chapter == null) {
            return;
        }

        loadingChapter = chapter;
        loadingScreenOpenedAtMs = nowMs();
        loadingClientReadySinceMs = 0L;
        loadingPreparationComplete = false;

        activeChapter = null;
        chapterDeadlineMs = 0L;
        chapterWelcomeMessage = null;
        chapterWelcomeDeadlineMs = 0L;
        clearChapterZeroValidationMessage();
        pausedCompletedChapter = null;
        pauseDeadlineMs = 0L;
        chapterZeroReachedDepth = false;
        chapterZeroCompletionDeadlineMs = 0L;

        StudyChapterHotbarTracker.reset();
        client.setScreen(new StudyLoadingScreen(StudyConfig.getChapterLoadingMessage()));
        StudyEventLog.logChapterLoadingStarted(chapter.chapterNumber(), chapter.displayTitle());

        StudyInteractionController.prepareChapterForStart(client, chapter, () -> {
            if (loadingChapter != chapter) {
                return;
            }

            placePlayerForChapter(client, chapter);
            loadingPreparationComplete = true;
        });
    }

    // Keep the loading screen visible until both conditions are true:
    // - server-side chapter preparation has finished
    // - the client can see that the destination chunk neighbourhood is present
    private static void tickChapterLoading(Minecraft client) {
        if (!loadingPreparationComplete || loadingChapter == null) {
            loadingClientReadySinceMs = 0L;
            return;
        }

        if (!isClientReadyForChapter(client, loadingChapter)) {
            loadingClientReadySinceMs = 0L;
            return;
        }

        long now = nowMs();
        if (loadingClientReadySinceMs == 0L) {
            loadingClientReadySinceMs = now;
        }

        if (now >= loadingScreenOpenedAtMs + StudyConfig.getChapterLoadingMinScreenMs()
                && now >= loadingClientReadySinceMs + CLIENT_CHAPTER_READY_STABILITY_MS) {
            finishChapterStart(client);
        }
    }

    private static void finishChapterStart(Minecraft client) {
        if (loadingChapter == null) {
            return;
        }

        StudyChapter chapter = loadingChapter;

        loadingChapter = null;
        loadingClientReadySinceMs = 0L;
        loadingScreenOpenedAtMs = 0L;
        loadingPreparationComplete = false;

        activeChapter = chapter;
        chapterDeadlineMs = chapter == StudyChapter.CHAPTER_0 ? 0L : nowMs() + chapter.durationMs();
        chapterWelcomeMessage = StudyConfig.getChapterWelcomeMessage(chapter);
        chapterWelcomeDeadlineMs = nowMs() + CHAPTER_WELCOME_DURATION_MS;
        pausedCompletedChapter = null;
        pauseDeadlineMs = 0L;

        StudyChapterHotbarTracker.startChapter(chapter);
        client.setScreen(null);

        StudyEventLog.logChapterLoadingFinished(chapter.chapterNumber(), chapter.displayTitle());
        StudyEventLog.logChapterStarted(
                chapter.chapterNumber(),
                chapter.x() + "," + chapter.y() + "," + chapter.z(),
                chapter.facingLabel(),
                chapter == StudyChapter.CHAPTER_0 ? 0L : chapter.durationMs()
        );

        StudyInteractionController.prewarmChapterArea(client, chapter.next());
    }

    // Check whether the client has the chapter start area available locally.
    private static boolean isClientReadyForChapter(Minecraft client, StudyChapter chapter) {
        if (client == null || client.level == null || client.player == null || chapter == null) {
            return false;
        }
        int chunkRadius = Math.max(1, StudyConfig.getChapterPrewarmChunkRadius());
        int centreChunkX = Math.floorDiv(chapter.x(), 16);
        int centreChunkZ = Math.floorDiv(chapter.z(), 16);

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int blockX = ((centreChunkX + dx) << 4) + 8;
                int blockZ = ((centreChunkZ + dz) << 4) + 8;
                if (!client.level.hasChunkAt(new BlockPos(blockX, chapter.y(), blockZ))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void finishActiveChapter(Minecraft client) {
        if (activeChapter == null) {
            return;
        }

        StudyChapter completedChapter = activeChapter;
        activeChapter = null;

        StudyChapterHotbarTracker.reset();
        StudyEventLog.logChapterCompleted(completedChapter.chapterNumber());

        if (completedChapter.next() == null) {
            client.setScreen(StudyOverlayScreen.createFinalQuestionnaireScreen(completedChapter));
            return;
        }

        if (completedChapter == StudyChapter.CHAPTER_0) {
            StudyEventLog.logCheckpointDisplayed(
                    completedChapter.chapterNumber(),
                    completedChapter.next().chapterNumber(),
                    getAssignedCondition().id()
            );
            StudyInteractionController.prewarmChapterArea(client, completedChapter.next());
            client.setScreen(StudyOverlayScreen.createChapterZeroTransitionScreen(completedChapter));
            return;
        }

        if (shouldShowExperimentalCheckpoint(completedChapter)) {
            StudyEventLog.logCheckpointDisplayed(
                    completedChapter.chapterNumber(),
                    completedChapter.next().chapterNumber(),
                    getAssignedCondition().id()
            );
            StudyInteractionController.prewarmChapterArea(client, completedChapter.next());
            client.setScreen(new StudyCheckpointScreen(completedChapter, getAssignedCondition()));
            return;
        }

        advanceFromCompletedChapter(client, completedChapter);
    }

    // The two-slide intro transition happens before Chapter 0.
    // Chapter 0 -> 1 uses a standard non-manipulated transition overlay.
    // The remaining manipulated checkpoints are between Ch 1 and 2, and Ch 2 and 3.
    private static boolean shouldShowExperimentalCheckpoint(StudyChapter completedChapter) {
        return completedChapter == StudyChapter.CHAPTER_1
                || completedChapter == StudyChapter.CHAPTER_2;
    }

    private static void advanceFromCompletedChapter(Minecraft client, StudyChapter completedChapter) {
        StudyChapter nextChapter = completedChapter.next();

        pausedCompletedChapter = null;
        pauseDeadlineMs = 0L;
        chapterZeroReachedDepth = false;
        chapterZeroCompletionDeadlineMs = 0L;
        clearChapterZeroValidationMessage();
        client.setScreen(null);

        if (nextChapter == null) {
            openQuestionnaire(client);
            return;
        }

        startChapter(client, nextChapter);
    }

    private static void openPauseScreen(Minecraft client,
                                        StudyChapter completedChapter,
                                        long pauseDurationMs) {
        StudyChapter nextChapter = completedChapter.next();
        if (nextChapter == null) {
            return;
        }

        pausedCompletedChapter = completedChapter;
        pauseDeadlineMs = nowMs() + pauseDurationMs;

        client.setScreen(new StudyPauseScreen(
                pauseDurationMs,
                () -> {
                    pausedCompletedChapter = null;
                    pauseDeadlineMs = 0L;
                    StudyEventLog.logCheckpointPauseFinished(
                            completedChapter.chapterNumber(),
                            nextChapter.chapterNumber()
                    );
                    advanceFromCompletedChapter(client, completedChapter);
                }
        ));
    }

    private static void applyTestingSkip(Minecraft client, String playerName, long requestedRemainingMs) {
        long now = nowMs();
        long targetRemainingMs = Math.max(0L, requestedRemainingMs);

        String phase = "none";
        long remainingBeforeMs = 0L;
        long remainingAfterMs = 0L;

        if (activeChapter != null) {
            remainingBeforeMs = Math.max(0L, chapterDeadlineMs - now);
            remainingAfterMs = Math.min(remainingBeforeMs, targetRemainingMs);
            chapterDeadlineMs = now + remainingAfterMs;
            phase = "chapter";
        } else if (client.screen instanceof StudyPauseScreen && pausedCompletedChapter != null) {
            remainingBeforeMs = Math.max(0L, pauseDeadlineMs - now);
            remainingAfterMs = Math.min(remainingBeforeMs, targetRemainingMs);
            openPauseScreen(client, pausedCompletedChapter, remainingAfterMs);
            phase = "pause";
        } else if (client.screen instanceof StudyCheckpointScreen checkpointScreen) {
            remainingBeforeMs = checkpointScreen.remainingPromptDelayMs();
            checkpointScreen.shortenRemainingPromptDelayTo(targetRemainingMs);
            remainingAfterMs = checkpointScreen.remainingPromptDelayMs();
            phase = "checkpoint_prompt";
        } else if (questionnaireFirstOpenedAtMs > 0L && client.screen instanceof StudyOverlayScreen) {
            long closeAllowedAtMs = questionnaireFirstOpenedAtMs + QUESTIONNAIRE_CLOSE_DELAY_MS;
            remainingBeforeMs = Math.max(0L, closeAllowedAtMs - now);
            remainingAfterMs = Math.min(remainingBeforeMs, targetRemainingMs);
            questionnaireFirstOpenedAtMs = now - (QUESTIONNAIRE_CLOSE_DELAY_MS - remainingAfterMs);
            phase = "questionnaire_close_delay";
        }

        StudyEventLog.logTestingTimeSkip(playerName, phase, remainingBeforeMs, remainingAfterMs);
    }

    private static void tickChapterZeroProgress(Minecraft client) {
        if (client == null || client.player == null || activeChapter != StudyChapter.CHAPTER_0) {
            return;
        }

        if (!chapterZeroReachedDepth && client.player.getY() <= CHAPTER_ZERO_DEPTH_TARGET_Y) {
            chapterZeroReachedDepth = true;
            StudyEventLog.logChapterZeroConditionSatisfied(
                    "reached_y_59_or_below",
                    playerName(client),
                    client.player.getX(),
                    client.player.getY(),
                    client.player.getZ()
            );
        }

        if (chapterZeroCompletionDeadlineMs > 0L && nowMs() >= chapterZeroCompletionDeadlineMs) {
            finishActiveChapter(client);
        }
    }

    private static boolean armChapterZeroCompletionIfReady(Minecraft client) {
        if (!chapterZeroReachedDepth
                || !StudyInteractionController.hasInteractedWithAnyCreature()
                || chapterZeroCompletionDeadlineMs > 0L) {
            return false;
        }

        chapterZeroCompletionDeadlineMs = nowMs() + CHAPTER_ZERO_COMPLETION_DELAY_MS;
        StudyEventLog.logChapterZeroCompletionArmed(playerName(client), CHAPTER_ZERO_COMPLETION_DELAY_MS);
        return true;
    }

    private static java.util.List<String> buildChapterZeroMissingRequirementLines() {
        java.util.List<String> missingRequirements = new java.util.ArrayList<>();

        if (!StudyInteractionController.hasInteractedWithAnyCreature()) {
            missingRequirements.add("- Interact with a creature");
        }

        if (!chapterZeroReachedDepth) {
            missingRequirements.add("- Jump in the hole");
        }

        if (missingRequirements.isEmpty()) {
            return java.util.List.of();
        }

        String summaryLine = missingRequirements.size() == 1
                ? "Make sure to pass this requirement (and then press ESC again) to finish:"
                : "Make sure to pass these requirements (and then press ESC again) to finish:";

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add(summaryLine);
        lines.addAll(missingRequirements);
        return java.util.List.copyOf(lines);
    }

    private static void showChapterZeroValidationMessage(java.util.List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            clearChapterZeroValidationMessage();
            return;
        }

        chapterZeroValidationMessages = java.util.List.copyOf(lines);
        chapterZeroValidationMessageDeadlineMs = nowMs() + CHAPTER_ZERO_VALIDATION_MESSAGE_DURATION_MS;
    }

    private static void clearChapterZeroValidationMessage() {
        chapterZeroValidationMessages = null;
        chapterZeroValidationMessageDeadlineMs = 0L;
    }

    // Place the participant in the correct chapter location.
    private static void placePlayerForChapter(Minecraft client, StudyChapter chapter) {
        if (client.player != null) {
            moveEntity(
                    client.player,
                    chapter.centerX(),
                    chapter.y(),
                    chapter.centerZ(),
                    chapter.yawDegrees(),
                    CHAPTER_LOOK_PITCH_DEGREES
            );
            client.player.setDeltaMovement(0.0D, 0.0D, 0.0D);
        }

        MinecraftServer server = client.getSingleplayerServer();
        UUID localPlayerId = client.player != null ? client.player.getUUID() : null;

        if (server == null || localPlayerId == null) {
            return;
        }

        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(localPlayerId);
            if (serverPlayer == null) {
                return;
            }

            moveEntity(
                    serverPlayer,
                    chapter.centerX(),
                    chapter.y(),
                    chapter.centerZ(),
                    chapter.yawDegrees(),
                    CHAPTER_LOOK_PITCH_DEGREES
            );
            serverPlayer.setDeltaMovement(0.0D, 0.0D, 0.0D);
            applySilentInfiniteNightVision(serverPlayer);
        });
    }

    private static void applySilentInfiniteNightVision(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return;
        }

        serverPlayer.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION,
                MobEffectInstance.INFINITE_DURATION,
                0,
                false,
                false,
                false
        ));
    }

    //Try to open in fullscreen + focused on this program.
    private static void ensureFullscreenAndFocus(Minecraft client) {
        if (windowPrepared) {
            return;
        }

        windowPrepared = true;
        Object window = client.getWindow();

        try {
            Boolean fullscreen = invokeBoolean(window, "isFullscreen", "isFullScreen");
            if (fullscreen == null || !fullscreen) {
                invokeNoArgs(window, "toggleFullScreen", "toggleFullscreen");
            }
        } catch (Exception e) {
            StudyCheckpoints.LOGGER.warn("Could not enable fullscreen automatically.", e);
        }

        try {
            Long handle = invokeLong(window, "getWindow", "getHandle");
            if (handle != null) {
                GLFW.glfwShowWindow(handle);
                GLFW.glfwFocusWindow(handle);
            }
        } catch (Exception e) {
            StudyCheckpoints.LOGGER.warn("Could not focus the Minecraft window automatically.", e);
        }
    }

    private static void openQuestionnaire(Minecraft client) {
        try {
            if (questionnaireFirstOpenedAtMs == 0L) {
                questionnaireFirstOpenedAtMs = nowMs();
            }

            String template = StudyConfig.getQualtricsUrlTemplate();
            String url = template
                    .replace("{MCID}", urlEncode(StudyEventLog.getSessionId()))
                    .replace("{CREATURES_SEEN}", urlEncode(buildCreaturesSeenPayload()));
            StudyEventLog.logQuestionnaireButtonPressed(url);

            if (openUrlInBrowser(url)) {
                StudyCheckpoints.LOGGER.info("Opened questionnaire URL: {}", url);
            } else {
                StudyCheckpoints.LOGGER.error("Failed to open Qualtrics URL: {}", url);
            }
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to open questionnaire URL.", e);
        }
    }

    private static String buildCreaturesSeenPayload() {
        return String.join(",", StudyInteractionController.interactedCreatureIdsSorted());
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static boolean openUrlInBrowser(String url) throws IOException {
        URI uri = URI.create(url);

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                    return true;
                }
            } catch (UnsupportedOperationException | IOException e) {
                StudyCheckpoints.LOGGER.warn("Desktop browse failed, trying OS fallback.", e);
            }
        }

        return openUrlWithOsCommand(url);
    }

    private static boolean openUrlWithOsCommand(String url) throws IOException {
        Process process;

        if (isWindows()) {
            process = new ProcessBuilder("cmd", "/c", "start", "", url).start();
        } else if (isMac()) {
            process = new ProcessBuilder("open", url).start();
        } else {
            process = new ProcessBuilder("xdg-open", url).start();
        }

        return process.isAlive() || process.exitValue() == 0;
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("win");
    }

    private static boolean isMac() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("mac");
    }

    private static long nowMs() {
        return System.currentTimeMillis();
    }

    private static void requestClientStop(Minecraft client) {
        try {
            invokeNoArgs(client, "scheduleStop", "stop");
        } catch (ReflectiveOperationException e) {
            StudyCheckpoints.LOGGER.error("Could not stop the client cleanly.", e);
        }
    }

    private static void moveEntity(Object entity, double x, double y, double z, float yaw, float pitch) {
        try {
            invokePosition(entity, "moveTo", x, y, z, yaw, pitch);
            return;
        } catch (ReflectiveOperationException ignored) {
            // Try the next candidate method name.
        }

        try {
            invokePosition(entity, "snapTo", x, y, z, yaw, pitch);
            return;
        } catch (ReflectiveOperationException ignored) {
            // Try the next candidate method name.
        }

        throw new IllegalStateException("No supported reposition method found.");
    }

    private static String playerName(Minecraft client) {
        return client != null && client.player != null
                ? client.player.getName().getString()
                : "unknown";
    }

    private static void invokeNoArgs(Object target, String... methodNames) throws ReflectiveOperationException {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                method.setAccessible(true);
                method.invoke(target);
                return;
            } catch (NoSuchMethodException ignored) {
                // Try the next candidate method name.
            }
        }

        throw new NoSuchMethodException("No matching no-arg method found.");
    }

    private static void invokePosition(Object target,
                                       String methodName,
                                       double x,
                                       double y,
                                       double z,
                                       float yaw,
                                       float pitch) throws ReflectiveOperationException {
        Method method = target.getClass().getMethod(
                methodName,
                double.class,
                double.class,
                double.class,
                float.class,
                float.class
        );
        method.setAccessible(true);
        method.invoke(target, x, y, z, yaw, pitch);
    }

    private static Boolean invokeBoolean(Object target, String... methodNames) throws ReflectiveOperationException {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                method.setAccessible(true);
                Object value = method.invoke(target);
                return value instanceof Boolean booleanValue ? booleanValue : null;
            } catch (NoSuchMethodException ignored) {
                // Try the next candidate method name.
            }
        }

        return null;
    }

    private static Long invokeLong(Object target, String... methodNames) throws ReflectiveOperationException {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                method.setAccessible(true);
                Object value = method.invoke(target);
                if (value instanceof Long longValue) {
                    return longValue;
                }
                if (value instanceof Number number) {
                    return number.longValue();
                }
            } catch (NoSuchMethodException ignored) {
                // Try the next candidate method name.
            }
        }

        return null;
    }
}