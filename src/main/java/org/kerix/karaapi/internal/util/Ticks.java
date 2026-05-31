package org.kerix.karaapi.internal.util;

import java.time.Duration;

public final class Ticks {

    public static final long TICKS_PER_SECOND = 20L;
    public static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60L;
    public static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60L;

    private Ticks() {
    }

    public static long seconds(long seconds) {
        return seconds * TICKS_PER_SECOND;
    }

    public static long minutes(long minutes) {
        return minutes * TICKS_PER_MINUTE;
    }

    public static long hours(long hours) {
        return hours * TICKS_PER_HOUR;
    }

    public static long fromDuration(Duration duration) {
        if (duration == null) {
            return 0L;
        }

        return Math.max(0L, duration.toMillis() / 50L);
    }

    public static Duration toDuration(long ticks) {
        return Duration.ofMillis(Math.max(0L, ticks) * 50L);
    }
}
