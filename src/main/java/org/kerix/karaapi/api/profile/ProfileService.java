package org.kerix.karaapi.api.profile;

import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.storage.Repository;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@ManagedService(
        value = ProfileService.class,
        priority = 50,
        registerAnnotatedTicks = false
)
public final class ProfileService implements Stoppable {

    private final Map<String, ProfileCache<?>> caches = new LinkedHashMap<>();

    private boolean stopped;

    public <T> ProfileCache<T> create(
            String name,
            Repository<UUID, T> repository,
            ProfileFactory<T> factory
    ) {
        ensureRunning();

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

    public boolean registered(String name) {
        return caches.containsKey(normalize(name));
    }

    public Set<String> cacheNames() {
        return Set.copyOf(caches.keySet());
    }

    public void unregister(String name, boolean save) {
        ensureRunning();

        ProfileCache<?> cache = caches.remove(normalize(name));

        if (cache != null) {
            cache.close(save);
        }
    }

    public void saveAll() {
        for (ProfileCache<?> cache : caches.values()) {
            cache.saveAll();
        }
    }

    public void unloadAll() {
        for (ProfileCache<?> cache : caches.values()) {
            cache.unloadAll();
        }
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;

        for (ProfileCache<?> cache : caches.values()) {
            cache.stop();
        }

        caches.clear();
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("ProfileService has already stopped.");
        }
    }

    private static String normalize(String name) {
        Objects.requireNonNull(name, "name");

        String normalized = name
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Profile cache name cannot be blank.");
        }

        return normalized;
    }
}
