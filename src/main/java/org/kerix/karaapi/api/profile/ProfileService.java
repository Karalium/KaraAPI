package org.kerix.karaapi.api.profile;

import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.storage.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ProfileService implements Stoppable {

    private final Map<String, ProfileCache<?>> caches = new LinkedHashMap<>();

    public <T> ProfileCache<T> create(
            String name,
            Repository<UUID, T> repository,
            ProfileFactory<T> factory
    ) {
        String key = normalize(name);

        if (caches.containsKey(key)) {
            throw new IllegalStateException("Profile cache already exists: " + key);
        }

        ProfileCache<T> cache = new ProfileCache<>(key, repository, factory);
        caches.put(key, cache);

        return cache;
    }

    @SuppressWarnings("unchecked")
    public <T> ProfileCache<T> get(String name) {
        String key = normalize(name);

        ProfileCache<?> cache = caches.get(key);

        if (cache == null) {
            throw new IllegalStateException("Profile cache not found: " + key);
        }

        return (ProfileCache<T>) cache;
    }

    @Override
    public void stop() {
        for (ProfileCache<?> cache : caches.values()) {
            cache.saveAll();
            cache.unloadAll();
        }

        caches.clear();
    }

    private static String normalize(String name) {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Profile cache name cannot be blank.");
        }

        return name.trim().toLowerCase().replace(" ", "_");
    }
}
