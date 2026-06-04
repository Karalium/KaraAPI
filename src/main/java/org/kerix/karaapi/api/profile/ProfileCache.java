package org.kerix.karaapi.api.profile;

import org.kerix.karaapi.api.storage.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProfileCache<T> {

    private final String name;
    private final Repository<UUID, T> repository;
    private final ProfileFactory<T> factory;
    private final ConcurrentMap<UUID, T> loaded = new ConcurrentHashMap<>();

    private volatile boolean stopped;

    public ProfileCache(
            String name,
            Repository<UUID, T> repository,
            ProfileFactory<T> factory
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public T load(UUID uuid) {
        return getOrLoad(uuid);
    }

    public T get(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");

        T profile = loaded.get(uuid);

        if (profile == null) {
            throw new IllegalStateException(
                    "Profile " + uuid + " is not loaded in cache '" + name + "'."
            );
        }

        return profile;
    }

    public T getOrLoad(UUID uuid) {
        ensureRunning();
        Objects.requireNonNull(uuid, "uuid");

        return loaded.computeIfAbsent(
                uuid,
                id -> repository.load(id).orElseGet(() -> factory.create(id))
        );
    }

    public Optional<T> find(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return Optional.ofNullable(loaded.get(uuid));
    }

    public boolean loaded(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return loaded.containsKey(uuid);
    }

    public void save(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");

        T profile = loaded.get(uuid);

        if (profile != null) {
            repository.save(uuid, profile);
        }
    }

    public void saveAndUnload(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");

        save(uuid);
        loaded.remove(uuid);
    }

    public void unload(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        loaded.remove(uuid);
    }

    public T reload(UUID uuid, boolean saveCurrent) {
        ensureRunning();
        Objects.requireNonNull(uuid, "uuid");

        if (saveCurrent) {
            save(uuid);
        }

        T profile = repository.load(uuid)
                .orElseGet(() -> factory.create(uuid));

        loaded.put(uuid, profile);
        return profile;
    }

    public void saveAll() {
        loaded.forEach(repository::save);
    }

    public void unloadAll() {
        loaded.clear();
    }

    public void saveAndUnloadAll() {
        saveAll();
        unloadAll();
    }

    public Collection<T> loadedProfiles() {
        return List.copyOf(loaded.values());
    }

    public Collection<UUID> loadedIds() {
        return List.copyOf(loaded.keySet());
    }

    public int size() {
        return loaded.size();
    }

    public boolean empty() {
        return loaded.isEmpty();
    }

    public String name() {
        return name;
    }

    public Repository<UUID, T> repository() {
        return repository;
    }

    void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        saveAndUnloadAll();
    }

    void close(boolean save) {
        if (stopped) {
            return;
        }

        stopped = true;

        if (save) {
            saveAndUnloadAll();
        } else {
            unloadAll();
        }
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("ProfileCache '" + name + "' has already stopped.");
        }
    }
}
