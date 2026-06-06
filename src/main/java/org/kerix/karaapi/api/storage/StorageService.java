package org.kerix.karaapi.api.storage;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class StorageService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final Map<String, Repository<?, ?>> repositories = new LinkedHashMap<>();

    private boolean stopped;

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
        ensureRunning();

        String key = normalizeName(name);

        if (repositories.containsKey(key)) {
            throw new StorageException("Repository already exists: " + key);
        }

        Repository<K, V> repository = new YamlFileRepository<>(
                hostPlugin,
                folderPath,
                idToFileName,
                fileNameToId,
                codec
        );

        repositories.put(key, repository);

        return repository;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Repository<K, V> get(String name) {
        String key = normalizeName(name);

        Repository<?, ?> repository = repositories.get(key);

        if (repository == null) {
            throw new StorageException("Repository not found: " + key);
        }

        return (Repository<K, V>) repository;
    }

    public boolean registered(String name) {
        return repositories.containsKey(normalizeName(name));
    }

    public Set<String> repositoryNames() {
        return Set.copyOf(repositories.keySet());
    }

    public void unregister(String name) {
        ensureRunning();
        repositories.remove(normalizeName(name));
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        repositories.clear();
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("StorageService has already stopped.");
        }
    }

    private static String normalizeName(String name) {
        Objects.requireNonNull(name, "name");

        String normalized = name
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Repository name cannot be blank.");
        }

        return normalized;
    }
}
