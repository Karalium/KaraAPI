package org.kerix.karaapi.internal.util;

import java.util.Locale;
import java.util.Objects;

public final class Strings {

    private Strings() {
    }

    public static String fallback(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value;
    }

    public static String normalizeKey(String value) {
        Objects.requireNonNull(value, "value");

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");
    }

    public static String normalizePath(String value) {
        Objects.requireNonNull(value, "value");

        return value
                .trim()
                .replace("\\", "/");
    }

    public static String ensureSuffix(String value, String suffix) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(suffix, "suffix");

        if (value.endsWith(suffix)) {
            return value;
        }

        return value + suffix;
    }

    public static String repeat(String value, int times) {
        Objects.requireNonNull(value, "value");

        if (times <= 0) {
            return "";
        }

        return value.repeat(times);
    }

    public static String joinNonBlank(String separator, String... values) {
        Objects.requireNonNull(separator, "separator");

        if (values == null || values.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }

            if (!builder.isEmpty()) {
                builder.append(separator);
            }

            builder.append(value);
        }

        return builder.toString();
    }
}
