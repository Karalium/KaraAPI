package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RecipeRegistration implements AutoCloseable {

    private final NamespacedKey key;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public RecipeRegistration(NamespacedKey key, Runnable unregisterAction) {
        this.key = Objects.requireNonNull(key, "key");
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public NamespacedKey key() {
        return key;
    }

    public boolean active() {
        return active.get();
    }

    public void unregister() {
        if (active.compareAndSet(true, false)) {
            unregisterAction.run();
        }
    }

    @Override
    public void close() {
        unregister();
    }
}
