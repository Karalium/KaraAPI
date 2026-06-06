package org.kerix.karaapi.api.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.*;

@ManagedService(
        value = ManagedService.class,
        priority = 10,
        registerAnnotatedTicks = false
)
@MainThread
public final class ConfigService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final Map<String, YamlConfig> configs = new LinkedHashMap<>();

    private boolean stopped;

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
        ensureRunning();

        String key = normalizeKey(fileName);

        return configs.computeIfAbsent(
                key,
                ignored -> new YamlConfig(hostPlugin, fileName, resourcePath)
        );
    }

    public YamlConfig get(String fileName) {
        String key = normalizeKey(fileName);
        YamlConfig config = configs.get(key);

        if (config == null) {
            throw new ConfigException("Config file is not loaded: " + fileName);
        }

        return config;
    }

    public boolean loaded(String fileName) {
        return configs.containsKey(normalizeKey(fileName));
    }

    public Collection<YamlConfig> loadedConfigs() {
        return List.copyOf(configs.values());
    }

    public void reloadAll() {
        ensureRunning();

        for (YamlConfig config : configs.values()) {
            config.reload();
        }
    }

    public void saveAll() {
        for (YamlConfig config : configs.values()) {
            config.save();
        }
    }

    public void unload(String fileName, boolean save) {
        ensureRunning();

        YamlConfig config = configs.remove(normalizeKey(fileName));

        if (config != null && save) {
            config.save();
        }
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        saveAll();
        configs.clear();
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("ConfigService has already stopped.");
        }
    }

    private static String normalizeKey(String fileName) {
        Objects.requireNonNull(fileName, "fileName");

        String normalized = fileName
                .trim()
                .replace("\\", "/")
                .toLowerCase(Locale.ROOT);

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Config file name cannot be blank.");
        }

        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) {
            normalized += ".yml";
        }

        return normalized;
    }
}
