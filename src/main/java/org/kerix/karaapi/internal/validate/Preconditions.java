package org.kerix.karaapi.internal.validate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class Preconditions {

    private Preconditions() {
    }

    public static <T> T notNull(T value, String name) {
        return Objects.requireNonNull(value, name + " cannot be null.");
    }

    public static String notBlank(String value, String name) {
        notNull(value, name);

        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank.");
        }

        return value;
    }

    public static <T extends Collection<?>> T notEmpty(T collection, String name) {
        notNull(collection, name);

        if (collection.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty.");
        }

        return collection;
    }

    public static <K, V, M extends Map<K, V>> M notEmpty(M map, String name) {
        notNull(map, name);

        if (map.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty.");
        }

        return map;
    }

    public static int positive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive.");
        }

        return value;
    }

    public static long positive(long value, String name) {
        if (value <= 0L) {
            throw new IllegalArgumentException(name + " must be positive.");
        }

        return value;
    }

    public static int nonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative.");
        }

        return value;
    }

    public static long nonNegative(long value, String name) {
        if (value < 0L) {
            throw new IllegalArgumentException(name + " cannot be negative.");
        }

        return value;
    }

    public static int range(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    name + " must be between " + min + " and " + max + "."
            );
        }

        return value;
    }

    public static double range(double value, double min, double max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    name + " must be between " + min + " and " + max + "."
            );
        }

        return value;
    }

    public static void argument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void state(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
