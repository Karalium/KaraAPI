package org.kerix.karaapi.api.effect;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EffectHandle implements AutoCloseable {

    private final UUID id;
    private final Runnable cancelAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public EffectHandle(UUID id, Runnable cancelAction) {
        this.id = Objects.requireNonNull(id, "id");
        this.cancelAction = Objects.requireNonNull(cancelAction, "cancelAction");
    }

    public UUID id() {
        return id;
    }

    public boolean active() {
        return active.get();
    }

    public void cancel() {
        if (active.compareAndSet(true, false)) {
            cancelAction.run();
        }
    }

    @Override
    public void close() {
        cancel();
    }
}
