package org.kerix.karaapi.api.startup;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ListenerRegistration implements AutoCloseable {

    private final Listener listener;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public ListenerRegistration(Listener listener) {
        this.listener = Objects.requireNonNull(listener, "listener");
    }

    public Listener listener() {
        return listener;
    }

    public boolean active() {
        return active.get();
    }

    public void unregister() {
        if (active.compareAndSet(true, false)) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public void close() {
        unregister();
    }
}
