package org.kerix.karaapi.api.startup;

import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ListenerRegistration implements Registration {

    private final Listener listener;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public ListenerRegistration(Listener listener, Runnable unregisterAction) {
        this.listener = Objects.requireNonNull(listener, "listener");
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public Listener listener() {
        return listener;
    }

    public Class<? extends Listener> listenerType() {
        return listener.getClass();
    }

    @Override
    public boolean active() {
        return active.get();
    }

    @Override
    public void unregister() {
        if (active.compareAndSet(true, false)) {
            unregisterAction.run();
        }
    }

    @Override
    public String toString() {
        return "ListenerRegistration{listener=" + listener.getClass().getName()
                + ", active=" + active() + '}';
    }
}
