package org.kerix.karaapi.api.logic;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Logic {

    private Logic() {
    }

    public static void when(boolean condition, Runnable action) {
        if (condition) {
            action.run();
        }
    }

    public static void unless(boolean condition, Runnable action) {
        if (!condition) {
            action.run();
        }
    }

    public static <T> void whenNotNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <T> T fallback(T value, T fallback) {
        return value == null ? fallback : value;
    }

    public static <T> T fallbackGet(T value, Supplier<T> fallback) {
        return value == null ? fallback.get() : value;
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        Objects.requireNonNull(values, "values");

        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    public static boolean all(BooleanSupplier... suppliers) {
        for (BooleanSupplier supplier : suppliers) {
            if (!supplier.getAsBoolean()) {
                return false;
            }
        }

        return true;
    }

    public static boolean any(BooleanSupplier... suppliers) {
        for (BooleanSupplier supplier : suppliers) {
            if (supplier.getAsBoolean()) {
                return true;
            }
        }

        return false;
    }

    public static boolean none(BooleanSupplier... suppliers) {
        return !any(suppliers);
    }

    public static int clamp(int value, int min, int max) {
        return Math.clamp(value, min, max);
    }

    public static long clamp(long value, long min, long max) {
        return Math.clamp(value, min, max);
    }

    public static double clamp(double value, double min, double max) {
        return Math.clamp(value, min, max);
    }
}
