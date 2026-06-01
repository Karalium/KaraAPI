package org.kerix.karaapi.api.storage;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class StorageService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final Map<String, Repository<?, ?>> repositories = new LinkedHashMap<>();

    public StorageService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public <K, V> Repository<K, V> yamlRepository(
            String name,
            String folderPath,
            Function<K, String> idToFileName,
            Function<String, K> fileNameToId,
            StorageCodec<V> codec
    ) {
        if (repositories.containsKey(name)) {
            throw new StorageException("Repository already exists: " + name);
        }

        Repository<K, V> repository = new YamlFileRepository<>(
                hostPlugin,
                folderPath,
                idToFileName,
                fileNameToId,
                codec
        );

        repositories.put(name, repository);

        return repository;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Repository<K, V> get(String name) {
        Repository<?, ?> repository = repositories.get(name);

        if (repository == null) {
            throw new StorageException("Repository not found: " + name);
        }

        return (Repository<K, V>) repository;
    }

    @Override
    public void stop() {
        repositories.clear();
    }
}
