package org.kerix.karaapi.api.effect;

import java.util.Objects;
import java.util.UUID;

public final class EffectHandle {

    private final UUID id;
    private final Runnable cancelAction;

    private boolean cancelled;

    public EffectHandle(UUID id, Runnable cancelAction) {
        this.id = Objects.requireNonNull(id, "id");
        this.cancelAction = Objects.requireNonNull(cancelAction, "cancelAction");
    }

    public UUID id() {
        return id;
    }

    public boolean cancelled() {
        return cancelled;
    }

    public void cancel() {
        if (cancelled) {
            return;
        }

        cancelled = true;
        cancelAction.run();
    }
}
