package org.kerix.karaapi.api.config;

import java.util.List;
import java.util.Objects;

public record ConfigKey<T>(
        String path,
        T defaultValue,
        ConfigType<T> type
) {

    public ConfigKey {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(type, "type");

        if (path.isBlank()) {
            throw new IllegalArgumentException("Config path cannot be blank.");
        }
    }

    public static ConfigKey<String> string(String path, String defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.STRING);
    }

    public static ConfigKey<Integer> integer(String path, int defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.INT);
    }

    public static ConfigKey<Long> longNumber(String path, long defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.LONG);
    }

    public static ConfigKey<Double> decimal(String path, double defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.DOUBLE);
    }

    public static ConfigKey<Boolean> bool(String path, boolean defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.BOOLEAN);
    }

    public static <T> ConfigKey<T> of(String path, T defaultValue, ConfigType<T> type) {
        return new ConfigKey<>(path, defaultValue, type);
    }
    public static ConfigKey<List<String>> stringList(String path, List<String> defaultValue) {
        return new ConfigKey<>(path, defaultValue, ConfigTypes.STRING_LIST);
    }
}