package io.github.u01234567.studycheckpoints;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.lwjgl.glfw.GLFW;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;

/**
 * Central controller for the current state of the study:
 * 1. show intro once
 * 2. wait for explicit consent
 * 3. run exactly one chapter at a time
 * 4. show manipulated checkpoint screens between chapters 1→2 and 2→3
 * 5. hand off to Qualtrics at the very end
 */
public final class StudyFlowController {
    private static final int TIMER_COLOR_NORMAL = 0xFFFFFFFF;
    private static final int TIMER_COLOR_WARNING = 0xFFFF4D4D;
    private static final long WARNING_THRESHOLD_MS = 15_000L;
    private static final long CHAPTER_WELCOME_DURATION_MS = 5_000L;
    private static final long QUESTIONNAIRE_CLOSE_DELAY_MS = 10_000L;
    private static final float CHAPTER_LOOK_PITCH_DEGREES = 45.0F;

    private static boolean introShown;
    private static boolean windowPrepared;

    private static StudyChapter activeChapter;
    private static long chapterDeadlineMs;
    private static String chapterWelcomeMessage;
    private static long chapterWelcomeDeadlineMs;
    private static long questionnaireFirstOpenedAtMs;

    private static StudyConditionAllocator.ConditionAssignment conditionAssignment;

    private StudyFlowController() {
    }

    public static void initializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(StudyFlowController::onEndClientTick);

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(StudyCheckpoints.MOD_ID, "study_timer"),
                StudyFlowController::renderTimerHud
        );
    }

    public static void acceptConsent(Minecraft client) {
        StudyEventLog.logConsentChoice("agree_and_continue");

        client.execute(() -> {
            client.setScreen(null);
            startChapter(client, StudyChapter.first());
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

    public static void continueFromFinalScreen(Minecraft client, StudyChapter completedChapter) {
        client.execute(() -> openQuestionnaire(client));
    }

    public static boolean canCloseQuestionnaireScreen() {
        return questionnaireFirstOpenedAtMs > 0L
                && nowMs() >= questionnaireFirstOpenedAtMs + QUESTIONNAIRE_CLOSE_DELAY_MS;
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
                "pause"
        );
        StudyEventLog.logCheckpointPauseStarted(
                completedChapter.chapterNumber(),
                nextChapter.chapterNumber(),
                pauseDurationMs
        );

        client.execute(() -> client.setScreen(new StudyPauseScreen(
                pauseDurationMs,
                () -> {
                    StudyEventLog.logCheckpointPauseFinished(
                            completedChapter.chapterNumber(),
                            nextChapter.chapterNumber()
                    );
                    advanceFromCompletedChapter(client, completedChapter);
                }
        )));
    }

    public static void renderTimerHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (activeChapter == null && chapterWelcomeMessage == null) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }

        if (activeChapter != null) {
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
    }

    private static void onEndClientTick(Minecraft client) {
        if (client == null || client.level == null || client.player == null) {
            return;
        }

        ensureFullscreenAndFocus(client);
        ensureConditionAssigned();

        if (!introShown) {
            introShown = true;
            client.setScreen(StudyOverlayScreen.createIntroScreen());
            return;
        }

        if (activeChapter != null && nowMs() >= chapterDeadlineMs) {
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

    private static void startChapter(Minecraft client, StudyChapter chapter) {
        activeChapter = chapter;
        chapterDeadlineMs = nowMs() + chapter.durationMs();
        chapterWelcomeMessage = "Welcome to " + chapter.displayTitle() + "!";
        chapterWelcomeDeadlineMs = nowMs() + CHAPTER_WELCOME_DURATION_MS;

        StudyInteractionController.prepareCreaturesForChapter(client, chapter);
        placePlayerForChapter(client, chapter);
        StudyEventLog.logChapterStarted(
                chapter.chapterNumber(),
                chapter.x() + "," + chapter.y() + "," + chapter.z(),
                chapter.facingLabel(),
                chapter.durationMs()
        );
    }

    private static void finishActiveChapter(Minecraft client) {
        if (activeChapter == null) {
            return;
        }

        StudyChapter completedChapter = activeChapter;
        activeChapter = null;

        StudyEventLog.logChapterCompleted(completedChapter.chapterNumber());

        if (completedChapter.next() == null) {
            client.setScreen(StudyOverlayScreen.createFinalQuestionnaireScreen(completedChapter));
            return;
        }

        if (shouldShowExperimentalCheckpoint(completedChapter)) {
            StudyEventLog.logCheckpointDisplayed(
                    completedChapter.chapterNumber(),
                    completedChapter.next().chapterNumber(),
                    getAssignedCondition().id()
            );
            client.setScreen(new StudyCheckpointScreen(completedChapter, getAssignedCondition()));
            return;
        }

        advanceFromCompletedChapter(client, completedChapter);
    }

    // There is a shared checkpoint between Ch 0 and 1, and manipulated
    // checkpoints between Ch 1 and 2, and Ch 2 and 3.
    private static boolean shouldShowExperimentalCheckpoint(StudyChapter completedChapter) {
        return completedChapter == StudyChapter.CHAPTER_0
                || completedChapter == StudyChapter.CHAPTER_1
                || completedChapter == StudyChapter.CHAPTER_2;
    }

    private static void advanceFromCompletedChapter(Minecraft client, StudyChapter completedChapter) {
        StudyChapter nextChapter = completedChapter.next();

        client.execute(() -> {
            client.setScreen(null);

            if (nextChapter == null) {
                openQuestionnaire(client);
                return;
            }

            startChapter(client, nextChapter);
        });
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
        });
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
            String url = template.replace("{MCID}", StudyEventLog.getSessionId());
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