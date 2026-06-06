package org.kerix.karaapi.api.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.kerix.karaapi.api.config.YamlConfig.getString;

public final class YamlFileRepository<K, V> implements Repository<K, V> {

    private final JavaPlugin hostPlugin;
    private final File folder;
    private final Function<K, String> idToFileName;
    private final Function<String, K> fileNameToId;
    private final StorageCodec<V> codec;

    public YamlFileRepository(
            JavaPlugin hostPlugin,
            String folderPath,
            Function<K, String> idToFileName,
            Function<String, K> fileNameToId,
            StorageCodec<V> codec
    ) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.folder = new File(hostPlugin.getDataFolder(), normalizeFolderPath(folderPath));
        this.idToFileName = Objects.requireNonNull(idToFileName, "idToFileName");
        this.fileNameToId = Objects.requireNonNull(fileNameToId, "fileNameToId");
        this.codec = Objects.requireNonNull(codec, "codec");

        if (!folder.exists() && !folder.mkdirs()) {
            throw new StorageException("Could not create storage folder: " + folder.getPath());
        }
    }

    @Override
    public synchronized void save(K id, V value) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(value, "value");

        File file = file(id);
        YamlConfiguration yaml = new YamlConfiguration();

        codec.write(yaml, value);

        try {
            yaml.save(file);
        } catch (IOException exception) {
            throw new StorageException("Could not save storage file: " + file.getPath(), exception);
        }
    }

    @Override
    public synchronized Optional<V> load(K id) {
        Objects.requireNonNull(id, "id");

        File file = file(id);

        if (!file.exists()) {
            return Optional.empty();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        return Optional.of(codec.read(yaml));
    }

    @Override
    public synchronized boolean exists(K id) {
        Objects.requireNonNull(id, "id");
        return file(id).exists();
    }

    @Override
    public synchronized void delete(K id) {
        Objects.requireNonNull(id, "id");

        File file = file(id);

        if (file.exists() && !file.delete()) {
            throw new StorageException("Could not delete storage file: " + file.getPath());
        }
    }

    @Override
    public synchronized List<K> ids() {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return List.of();
        }

        List<K> ids = new ArrayList<>();

        for (File file : files) {
            String name = file.getName();

            if (name.endsWith(".yml")) {
                name = name.substring(0, name.length() - 4);
            }

            ids.add(fileNameToId.apply(name));
        }

        return List.copyOf(ids);
    }

    @Override
    public synchronized List<V> loadAll() {
        List<V> values = new ArrayList<>();

        for (K id : ids()) {
            load(id).ifPresent(values::add);
        }

        return List.copyOf(values);
    }

    public File folder() {
        return folder;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    private File file(K id) {
        String fileName = normalizeFileName(idToFileName.apply(id));

        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }

        return new File(folder, fileName);
    }

    private static String normalizeFolderPath(String path) {
        if (path == null || path.isBlank()) {
            return "storage";
        }

        return normalizeRelativePath(path);
    }

    private static String normalizeFileName(String fileName) {
        Objects.requireNonNull(fileName, "fileName");

        String normalized = normalizeRelativePath(fileName);

        if (normalized.contains("/")) {
            throw new IllegalArgumentException("Storage file name cannot contain folders: " + fileName);
        }

        return normalized;
    }

    private static String normalizeRelativePath(String path) {
        return getString(path);
    }
}
