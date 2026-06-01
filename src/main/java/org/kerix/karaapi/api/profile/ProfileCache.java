package org.kerix.karaapi.api.profile;

import org.kerix.karaapi.api.storage.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ProfileCache<T> {

    private final String name;
    private final Repository<UUID, T> repository;
    private final ProfileFactory<T> factory;
    private final Map<UUID, T> loaded = new ConcurrentHashMap<>();

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
        Objects.requireNonNull(uuid, "uuid");

        T profile = repository.load(uuid)
                .orElseGet(() -> factory.create(uuid));

        loaded.put(uuid, profile);
        return profile;
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
        Objects.requireNonNull(uuid, "uuid");

        return loaded.computeIfAbsent(
                uuid,
                id -> repository.load(id).orElseGet(() -> factory.create(id))
        );
    }

    public Optional<T> find(UUID uuid) {
        return Optional.ofNullable(loaded.get(uuid));
    }

    public boolean loaded(UUID uuid) {
        return loaded.containsKey(uuid);
    }

    public void save(UUID uuid) {
        T profile = loaded.get(uuid);

        if (profile != null) {
            repository.save(uuid, profile);
        }
    }

    public void saveAndUnload(UUID uuid) {
        save(uuid);
        loaded.remove(uuid);
    }

    public void unload(UUID uuid) {
        loaded.remove(uuid);
    }

    public void saveAll() {
        loaded.forEach(repository::save);
    }

    public void unloadAll() {
        loaded.clear();
    }

    public Collection<T> loadedProfiles() {
        return List.copyOf(loaded.values());
    }

    public int size() {
        return loaded.size();
    }

    public String name() {
        return name;
    }

    public Repository<UUID, T> repository() {
        return repository;
    }
}
