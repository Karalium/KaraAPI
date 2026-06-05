package org.kerix.karaapi.api.placeholder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PlaceholderExpansionRegistration implements AutoCloseable {

    private final String identifier;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public PlaceholderExpansionRegistration(String identifier, Runnable unregisterAction) {
        this.identifier = PlaceholderExpansion.normalizeIdentifier(identifier);
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public String identifier() {
        return identifier;
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
