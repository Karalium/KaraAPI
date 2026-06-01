package org.kerix.karaapi.api.event;

import java.util.Objects;
import java.util.UUID;

public final class EventSubscription<T> {

    private final UUID id = UUID.randomUUID();
    private final Class<T> eventType;
    private final EventPriority priority;
    private final boolean ignoreCancelled;
    private final EventHandler<T> handler;
    private final Runnable unsubscribeAction;

    EventSubscription(
            Class<T> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            EventHandler<T> handler,
            Runnable unsubscribeAction
    ) {
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.ignoreCancelled = ignoreCancelled;
        this.handler = Objects.requireNonNull(handler, "handler");
        this.unsubscribeAction = Objects.requireNonNull(unsubscribeAction, "unsubscribeAction");
    }

    public UUID id() {
        return id;
    }

    public Class<T> eventType() {
        return eventType;
    }

    public EventPriority priority() {
        return priority;
    }

    public boolean ignoreCancelled() {
        return ignoreCancelled;
    }

    public void handle(Object event) {
        handler.handle(eventType.cast(event));
    }

    public void unsubscribe() {
        unsubscribeAction.run();
    }
}
