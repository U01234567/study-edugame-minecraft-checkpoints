package io.github.u01234567.studycheckpoints;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Client-side hotbar progress tracker for the current study chapter.
 *
 * Each tracked creature card is assigned to one hotbar slot for the chapter.
 * The slot starts as a grey circle and turns green once the participant has
 * interacted with any spawn of that creature.
 */
public final class StudyChapterHotbarTracker {
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_INNER_OFFSET = 1;
    private static final int HOTBAR_SLOT_SIZE = 20;
    private static final int HOTBAR_Y_OFFSET = 22;
    private static final double CIRCLE_RADIUS = 6.0D;
    private static final int INCOMPLETE_COLOUR = 0xFF7F7F7F;
    private static final int COMPLETE_COLOUR = StudyExperimentCondition.CONTINUE.continueButtonColour();

    private static List<TrackedHotbarSlot> activeSlots = List.of();
    private static StudyChapter activeChapter;

    private StudyChapterHotbarTracker() {
    }

    public static void reset() {
        activeChapter = null;
        activeSlots = List.of();
    }

    public static void startChapter(StudyChapter chapter) {
        if (chapter == null) {
            reset();
            return;
        }

        List<StudyCreatureCards.CreatureCardReference> selectedCards = StudyCreatureCards.hotbarCardsForChapter(chapter);
        List<Integer> slotOrder = new ArrayList<>();
        for (int i = 0; i < selectedCards.size(); i++) {
            slotOrder.add(i);
        }
        Collections.shuffle(slotOrder, ThreadLocalRandom.current());

        List<TrackedHotbarSlot> slots = new ArrayList<>();
        for (int i = 0; i < selectedCards.size(); i++) {
            StudyCreatureCards.CreatureCardReference cardReference = selectedCards.get(i);
            slots.add(new TrackedHotbarSlot(
                    cardReference.creatureId(),
                    slotOrder.get(i),
                    false
            ));
        }

        activeChapter = chapter;
        activeSlots = List.copyOf(slots);
    }

    public static void markCreatureInteracted(String creatureId) {
        if (creatureId == null || creatureId.isBlank() || activeSlots.isEmpty()) {
            return;
        }

        List<TrackedHotbarSlot> updatedSlots = new ArrayList<>(activeSlots.size());
        boolean changed = false;

        for (TrackedHotbarSlot slot : activeSlots) {
            if (!slot.creatureId().equals(creatureId) || slot.completed()) {
                updatedSlots.add(slot);
                continue;
            }

            updatedSlots.add(slot.withCompleted(true));
            changed = true;
        }

        if (changed) {
            activeSlots = List.copyOf(updatedSlots);
        }
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!StudyFlowController.isChapterActive() || activeSlots.isEmpty()) {
            return;
        }

        if (StudyFlowController.getActiveChapter() != activeChapter) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }

        int hotbarLeft = (client.getWindow().getGuiScaledWidth() - HOTBAR_WIDTH) / 2;
        int hotbarTop = client.getWindow().getGuiScaledHeight() - HOTBAR_Y_OFFSET;

        for (TrackedHotbarSlot slot : activeSlots) {
            int cellLeft = hotbarLeft + HOTBAR_INNER_OFFSET + (slot.hotbarSlotIndex() * HOTBAR_SLOT_SIZE);
            int cellTop = hotbarTop + HOTBAR_INNER_OFFSET;
            int colour = slot.completed() ? COMPLETE_COLOUR : INCOMPLETE_COLOUR;

            drawCenteredCircle(graphics, cellLeft, cellTop, colour);
        }
    }

    private static void drawCenteredCircle(GuiGraphicsExtractor graphics, int cellLeft, int cellTop, int colour) {
        double cellCenter = HOTBAR_SLOT_SIZE / 2.0D;

        for (int localY = 0; localY < HOTBAR_SLOT_SIZE; localY++) {
            double yDistance = (localY + 0.5D) - cellCenter;
            if (Math.abs(yDistance) > CIRCLE_RADIUS) {
                continue;
            }

            double xExtent = Math.sqrt((CIRCLE_RADIUS * CIRCLE_RADIUS) - (yDistance * yDistance));
            int startX = (int) Math.ceil(cellCenter - xExtent - 0.5D);
            int endX = (int) Math.floor(cellCenter + xExtent - 0.5D);

            graphics.fill(cellLeft + startX, cellTop + localY, cellLeft + endX + 1, cellTop + localY + 1, colour);
        }
    }

    private record TrackedHotbarSlot(
            String creatureId,
            int hotbarSlotIndex,
            boolean completed
    ) {
        private TrackedHotbarSlot withCompleted(boolean completed) {
            return new TrackedHotbarSlot(creatureId, hotbarSlotIndex, completed);
        }
    }
}