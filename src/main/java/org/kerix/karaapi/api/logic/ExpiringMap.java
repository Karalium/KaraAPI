package org.kerix.karaapi.api.logic;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ExpiringMap<K, V> {

    private final Map<K, Entry<V>> values = new HashMap<>();

    public void put(K key, V value, Duration duration) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(duration, "duration");

        long expiresAt = System.currentTimeMillis() + duration.toMillis();

        values.put(key, new Entry<>(value, expiresAt));
    }

    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "key");

        Entry<V> entry = values.get(key);

        if (entry == null) {
            return Optional.empty();
        }

        if (entry.expired()) {
            values.remove(key);
            return Optional.empty();
        }

        return Optional.ofNullable(entry.value());
    }

    public V getOrNull(K key) {
        return get(key).orElse(null);
    }

    public boolean contains(K key) {
        return get(key).isPresent();
    }

    public void remove(K key) {
        values.remove(key);
    }

    public void clear() {
        values.clear();
    }

    public void cleanup() {
        values.entrySet().removeIf(entry -> entry.getValue().expired());
    }

    public int size() {
        cleanup();
        return values.size();
    }

    private record Entry<V>(V value, long expiresAt) {

        boolean expired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }
}
