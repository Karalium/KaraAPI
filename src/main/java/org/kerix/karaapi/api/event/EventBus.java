package org.kerix.karaapi.api.event;

import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedService(
        value = EventBus.class,
        priority = 20,
        registerAnnotatedTicks = false
)
public final class EventBus implements Stoppable {

    private final Map<Class<?>, CopyOnWriteArrayList<EventSubscription<?>>> subscriptions =
            new ConcurrentHashMap<>();

    private final Logger logger;

    public EventBus() {
        this(Logger.getLogger(EventBus.class.getName()));
    }

    public EventBus(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public <T> EventSubscription<T> subscribe(
            Class<T> eventType,
            EventListener<? super T> listener
    ) {
        return subscribe(eventType, EventPriority.NORMAL, false, listener);
    }

    public <T> EventSubscription<T> subscribe(
            Class<T> eventType,
            EventPriority priority,
            EventListener<? super T> listener
    ) {
        return subscribe(eventType, priority, false, listener);
    }

    public <T> EventSubscription<T> subscribe(
            Class<T> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            EventListener<? super T> listener
    ) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");

        AtomicReference<EventSubscription<T>> reference = new AtomicReference<>();

        EventSubscription<T> subscription = new EventSubscription<>(
                eventType,
                priority,
                ignoreCancelled,
                listener,
                () -> unsubscribeById(eventType, reference.get().id())
        );

        reference.set(subscription);

        subscriptions
                .computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>())
                .add(subscription);

        return subscription;
    }

    public void unsubscribe(EventSubscription<?> subscription) {
        if (subscription == null) {
            return;
        }

        unsubscribeById(subscription.eventType(), subscription.id());
    }

    public boolean unsubscribe(UUID id) {
        Objects.requireNonNull(id, "id");

        boolean removed = false;

        for (Class<?> eventType : List.copyOf(subscriptions.keySet())) {
            removed |= unsubscribeById(eventType, id);
        }

        return removed;
    }

    public <T> T publish(T event) {
        Objects.requireNonNull(event, "event");

        for (EventSubscription<?> subscription : matchingSubscriptions(event.getClass())) {
            if (!subscription.active()) {
                continue;
            }

            if (event instanceof CancellableEvent cancellable
                    && cancellable.cancelled()
                    && subscription.ignoreCancelled()) {
                continue;
            }

            try {
                subscription.handle(event);
            } catch (Throwable throwable) {
                logger.log(
                        Level.SEVERE,
                        "Error while handling event " + event.getClass().getName(),
                        throwable
                );
            }
        }

        return event;
    }

    public int subscriptionCount() {
        int count = 0;

        for (CopyOnWriteArrayList<EventSubscription<?>> list : subscriptions.values()) {
            count += list.size();
        }

        return count;
    }

    public boolean empty() {
        return subscriptionCount() == 0;
    }

    @Override
    public void stop() {
        for (CopyOnWriteArrayList<EventSubscription<?>> list : subscriptions.values()) {
            for (EventSubscription<?> subscription : list) {
                subscription.deactivate();
            }

            list.clear();
        }

        subscriptions.clear();
    }

    private boolean unsubscribeById(Class<?> eventType, UUID id) {
        if (id == null) {
            return false;
        }

        CopyOnWriteArrayList<EventSubscription<?>> list = subscriptions.get(eventType);

        if (list == null) {
            return false;
        }

        boolean removed = false;

        for (EventSubscription<?> subscription : List.copyOf(list)) {
            if (!subscription.id().equals(id)) {
                continue;
            }

            subscription.deactivate();
            list.remove(subscription);
            removed = true;
        }

        if (list.isEmpty()) {
            subscriptions.remove(eventType, list);
        }

        return removed;
    }

    private List<EventSubscription<?>> matchingSubscriptions(Class<?> eventClass) {
        List<EventSubscription<?>> result = new ArrayList<>();

        for (Map.Entry<Class<?>, CopyOnWriteArrayList<EventSubscription<?>>> entry : subscriptions.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventClass)) {
                result.addAll(entry.getValue());
            }
        }

        result.removeIf(subscription -> !subscription.active());
        result.sort(Comparator.comparing(EventSubscription::priority));

        return result;
    }
}
