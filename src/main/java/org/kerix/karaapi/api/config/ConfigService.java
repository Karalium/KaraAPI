package org.kerix.karaapi.api.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ConfigService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final Map<String, YamlConfig> configs = new LinkedHashMap<>();

    public ConfigService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public YamlConfig main() {
        return config("config.yml");
    }

    public YamlConfig config(String fileName) {
        return config(fileName, fileName);
    }

    public YamlConfig config(String fileName, String resourcePath) {
        String key = normalizeKey(fileName);

        return configs.computeIfAbsent(
                key,
                ignored -> new YamlConfig(hostPlugin, fileName, resourcePath)
        );
    }

    public YamlConfig get(String fileName) {
        YamlConfig config = configs.get(normalizeKey(fileName));

        if (config == null) {
            throw new ConfigException("Config file is not loaded: " + fileName);
        }

        return config;
    }

    public boolean isLoaded(String fileName) {
        return configs.containsKey(normalizeKey(fileName));
    }

    public Collection<YamlConfig> loadedConfigs() {
        return configs.values();
    }

    public void reloadAll() {
        for (YamlConfig config : configs.values()) {
            config.reload();
        }
    }

    public void saveAll() {
        for (YamlConfig config : configs.values()) {
            config.save();
        }
    }

    @Override
    public void stop() {
        configs.clear();
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    private static String normalizeKey(String fileName) {
        return getString(fileName);
    }

    @NonNull
    static String getString(String fileName) {
        Objects.requireNonNull(fileName, "fileName");

        String normalized = fileName.trim().replace("\\", "/");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Config file name cannot be blank.");
        }

        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) {
            normalized += ".yml";
        }

        return normalized;
    }
}
