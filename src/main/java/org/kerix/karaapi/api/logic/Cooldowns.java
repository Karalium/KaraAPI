package org.kerix.karaapi.api.logic;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Cooldowns<K> {

    private final Map<K, Long> expiresAt = new HashMap<>();

    public void start(K key, Duration duration) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(duration, "duration");

        long expiration = System.currentTimeMillis() + duration.toMillis();

        expiresAt.put(key, expiration);
    }

    public boolean active(K key) {
        Objects.requireNonNull(key, "key");

        Long expiration = expiresAt.get(key);

        if (expiration == null) {
            return false;
        }

        if (System.currentTimeMillis() >= expiration) {
            expiresAt.remove(key);
            return false;
        }

        return true;
    }

    public boolean ready(K key) {
        return !active(key);
    }

    public long remainingMillis(K key) {
        Objects.requireNonNull(key, "key");

        Long expiration = expiresAt.get(key);

        if (expiration == null) {
            return 0L;
        }

        long remaining = expiration - System.currentTimeMillis();

        if (remaining <= 0L) {
            expiresAt.remove(key);
            return 0L;
        }

        return remaining;
    }

    public Duration remaining(K key) {
        return Duration.ofMillis(remainingMillis(key));
    }

    public boolean tryUse(K key, Duration duration) {
        if (active(key)) {
            return false;
        }

        start(key, duration);
        return true;
    }

    public void clear(K key) {
        expiresAt.remove(key);
    }

    public void clearAll() {
        expiresAt.clear();
    }

    public void cleanup() {
        long now = System.currentTimeMillis();

        expiresAt.entrySet().removeIf(entry -> now >= entry.getValue());
    }

    public int size() {
        cleanup();
        return expiresAt.size();
    }
}
