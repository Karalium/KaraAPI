package org.kerix.karaapi.api.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public final class YamlConfig {

    private final JavaPlugin hostPlugin;
    private final String fileName;
    private final String resourcePath;
    private final File file;

    private YamlConfiguration yaml;

    public YamlConfig(JavaPlugin hostPlugin, String fileName) {
        this(hostPlugin, fileName, fileName);
    }

    public YamlConfig(JavaPlugin hostPlugin, String fileName, String resourcePath) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.fileName = normalizeFileName(fileName);
        this.resourcePath = normalizeResourcePath(resourcePath);
        this.file = new File(hostPlugin.getDataFolder(), this.fileName);

        saveDefault();
        reload();
    }

    public void saveDefault() {
        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new ConfigException("Could not create config folder: " + parent.getPath());
        }

        try (InputStream stream = hostPlugin.getResource(resourcePath)) {
            if (stream != null) {
                copyResourceToFile(resourcePath, file);
                return;
            }
        } catch (IOException exception) {
            throw new ConfigException("Could not read default resource: " + resourcePath, exception);
        }

        try {
            if (!file.createNewFile()) {
                throw new ConfigException("Could not create config file: " + file.getPath());
            }
        } catch (IOException exception) {
            throw new ConfigException("Could not create config file: " + file.getPath(), exception);
        }
    }

    public void reload() {
        this.yaml = YamlConfiguration.loadConfiguration(file);
        this.yaml.options().copyDefaults(true);

        if (!resourceExists(resourcePath)) {
            return;
        }

        try (InputStream stream = hostPlugin.getResource(resourcePath)) {
            if (stream == null) {
                return;
            }

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);

            this.yaml.setDefaults(defaults);
        } catch (IOException exception) {
            throw new ConfigException("Could not load default resource: " + resourcePath, exception);
        }
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException exception) {
            throw new ConfigException("Could not save config file: " + file.getPath(), exception);
        }
    }

    public void reloadAndSaveDefaults() {
        reload();
        save();
    }

    public boolean contains(String path) {
        return yaml.contains(path);
    }

    public boolean isSet(String path) {
        return yaml.isSet(path);
    }

    public Object raw(String path) {
        return yaml.get(path);
    }

    public void raw(String path, Object value) {
        yaml.set(path, value);
    }

    public <T> T get(ConfigKey<T> key) {
        Objects.requireNonNull(key, "key");

        T value = key.type().read(yaml, key.path(), key.defaultValue());

        if (!yaml.contains(key.path()) && key.defaultValue() != null) {
            key.type().write(yaml, key.path(), key.defaultValue());
        }

        return value;
    }

    public <T> T require(ConfigKey<T> key) {
        Objects.requireNonNull(key, "key");

        if (!yaml.contains(key.path())) {
            throw new ConfigException(
                    "Missing required config value '" + key.path() + "' in " + fileName
            );
        }

        return key.type().read(yaml, key.path(), key.defaultValue());
    }

    public <T> void set(ConfigKey<T> key, T value) {
        Objects.requireNonNull(key, "key");
        key.type().write(yaml, key.path(), value);
    }

    public ConfigurationSection section(String path) {
        ConfigurationSection section = yaml.getConfigurationSection(path);

        if (section == null) {
            section = yaml.createSection(path);
        }

        return section;
    }

    public ConfigurationSection existingSection(String path) {
        ConfigurationSection section = yaml.getConfigurationSection(path);

        if (section == null) {
            throw new ConfigException(
                    "Missing config section '" + path + "' in " + fileName
            );
        }

        return section;
    }

    private void copyResourceToFile(String resourcePath, File target) {
        try (InputStream stream = hostPlugin.getResource(resourcePath)) {
            if (stream == null) {
                return;
            }

            File parent = target.getParentFile();

            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new ConfigException("Could not create config folder: " + parent.getPath());
            }

            Files.copy(stream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ConfigException("Could not copy default config resource: " + resourcePath, exception);
        }
    }

    public Set<String> keys(boolean deep) {
        return yaml.getKeys(deep);
    }

    public YamlConfiguration yaml() {
        return yaml;
    }

    public File file() {
        return file;
    }

    public String fileName() {
        return fileName;
    }

    public String resourcePath() {
        return resourcePath;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public void logLoaded() {
        hostPlugin.getLogger().log(
                Level.INFO,
                "[Config] Loaded " + fileName
        );
    }

    private boolean resourceExists(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }

        return hostPlugin.getResource(path) != null;
    }

    private static String normalizeFileName(String fileName) {
        Objects.requireNonNull(fileName, "fileName");

        String normalized = normalizeRelativePath(fileName);

        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) {
            normalized += ".yml";
        }

        return normalized;
    }

    private static String normalizeResourcePath(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return null;
        }

        return normalizeRelativePath(resourcePath);
    }

    private static String normalizeRelativePath(String path) {
        return getString(path);
    }

    @NonNull
    public static String getString(String path) {
        String normalized = path.trim().replace("\\", "/");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Path cannot be blank.");
        }

        if (normalized.startsWith("/") || normalized.contains(":")) {
            throw new IllegalArgumentException("Path must be relative: " + path);
        }

        for (String segment : normalized.split("/")) {
            if (segment.isBlank() || segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("Unsafe path: " + path);
            }
        }

        return normalized;
    }
}
