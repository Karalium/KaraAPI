package org.kerix.karaapi.api.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        this.hostPlugin = hostPlugin;
        this.folder = new File(hostPlugin.getDataFolder(), folderPath);
        this.idToFileName = idToFileName;
        this.fileNameToId = fileNameToId;
        this.codec = codec;

        this.folder.mkdirs();
    }

    @Override
    public void save(K id, V value) {
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
    public Optional<V> load(K id) {
        File file = file(id);

        if (!file.exists()) {
            return Optional.empty();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        return Optional.of(codec.read(yaml));
    }

    @Override
    public boolean exists(K id) {
        return file(id).exists();
    }

    @Override
    public void delete(K id) {
        File file = file(id);

        if (file.exists() && !file.delete()) {
            throw new StorageException("Could not delete storage file: " + file.getPath());
        }
    }

    @Override
    public List<K> ids() {
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

        return ids;
    }

    @Override
    public List<V> loadAll() {
        List<V> values = new ArrayList<>();

        for (K id : ids()) {
            load(id).ifPresent(values::add);
        }

        return values;
    }

    public File folder() {
        return folder;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    private File file(K id) {
        String fileName = idToFileName.apply(id);

        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }

        return new File(folder, fileName);
    }
}
