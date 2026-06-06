package org.kerix.karaapi.api.region;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RegionRegistration implements AutoCloseable {

    private final String id;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public RegionRegistration(String id, Runnable unregisterAction) {
        this.id = RegionKeys.normalize(id);
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public String id() {
        return id;
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
