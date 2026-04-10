package io.github.u01234567.studycheckpoints;

/**
 * Fixed chapter definitions for the study.
 * Each chapter knows:
 * - its ordinal number
 * - where the player should be placed
 * - which yaw corresponds to the requested facing direction
 * - how long the chapter lasts
 * - which transition text to show afterwards
 */

public enum StudyChapter {
    CHAPTER_0(
            0,
            -77, 63, -524, -90.0F, "east",
            200_000L,
            "Ch0 complete. Move to Ch1."
    ),
    CHAPTER_1(
            1,
            -126, 69, 114, 90.0F, "west",
            480_000L,
            "Ch1 complete. Move to Ch2."
    ),
    CHAPTER_2(
            2,
            286, 75, -240, 90.0F, "west",
            480_000L,
            "Ch2 complete. Move to Ch3."
    ),
    CHAPTER_3(
            3,
            171, 64, 652, -90.0F, "east",
            480_000L,
            "Ch3 complete. Click below to answer the questionnaire."
    );

    private final int chapterNumber;
    private final int x;
    private final int y;
    private final int z;
    private final float yawDegrees;
    private final String facingLabel;
    private final long durationMs;
    private final String completionMessage;

    StudyChapter(int chapterNumber,
                 int x,
                 int y,
                 int z,
                 float yawDegrees,
                 String facingLabel,
                 long durationMs,
                 String completionMessage) {
        this.chapterNumber = chapterNumber;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yawDegrees = yawDegrees;
        this.facingLabel = facingLabel;
        this.durationMs = durationMs;
        this.completionMessage = completionMessage;
    }

    public int chapterNumber() { return chapterNumber; }
    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }
    public float yawDegrees() { return yawDegrees; }
    public String facingLabel() { return facingLabel; }
    public long durationMs() { return durationMs; }
    public String completionMessage() { return completionMessage; }

    // Chapter titles
    public String displayTitle() {
        return StudyConfig.getChapterDisplayTitle(this);
    }

    // Teleport player in center of spawn point
    public double centerX() { return x + 0.5D; }
    public double centerZ() { return z + 0.5D; }

    public static StudyChapter first() {
        return CHAPTER_0;
    }

    public StudyChapter next() {
        StudyChapter[] values = values();
        int nextIndex = this.ordinal() + 1;
        return nextIndex < values.length ? values[nextIndex] : null;
    }
}
