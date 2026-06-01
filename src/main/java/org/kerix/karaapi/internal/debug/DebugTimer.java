package org.kerix.karaapi.internal.debug;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

public final class DebugTimer {

    private final String name;
    private final Instant startedAt;

    private DebugTimer(String name) {
        this.name = Objects.requireNonNull(name, "name");
        this.startedAt = Instant.now();
    }

    public static DebugTimer start(String name) {
        return new DebugTimer(name);
    }

    public String name() {
        return name;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Duration elapsed() {
        return Duration.between(startedAt, Instant.now());
    }

    public long elapsedMillis() {
        return elapsed().toMillis();
    }

    public String summary() {
        return name + " took " + elapsedMillis() + "ms";
    }

    public static <T> T measure(String name, Supplier<T> supplier, java.util.function.Consumer<String> output) {
        DebugTimer timer = start(name);

        try {
            return supplier.get();
        } finally {
            output.accept(timer.summary());
        }
    }

    public static void measure(String name, Runnable runnable, java.util.function.Consumer<String> output) {
        DebugTimer timer = start(name);

        try {
            runnable.run();
        } finally {
            output.accept(timer.summary());
        }
    }
}
