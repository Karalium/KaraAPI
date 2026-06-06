package org.kerix.karaapi.api.event;

import java.util.Objects;
import java.util.UUID;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EventSubscription<T> {

    private final UUID id = UUID.randomUUID();
    private final Class<T> eventType;
    private final EventPriority priority;
    private final boolean ignoreCancelled;
    private final EventListener<? super T> listener;
    private final Runnable unsubscribeAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    EventSubscription(
            Class<T> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            EventListener<? super T> listener,
            Runnable unsubscribeAction
    ) {
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.ignoreCancelled = ignoreCancelled;
        this.listener = Objects.requireNonNull(listener, "listener");
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

    public boolean active() {
        return active.get();
    }

    public void handle(Object event) {
        if (!active()) {
            return;
        }

        listener.handle(eventType.cast(event));
    }

    public void unsubscribe() {
        if (active.compareAndSet(true, false)) {
            unsubscribeAction.run();
        }
    }

    void deactivate() {
        active.set(false);
    }
}
