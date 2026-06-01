package org.kerix.karaapi.api.event;

import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EventBus implements Stoppable {

    private final Map<Class<?>, List<EventSubscription<?>>> subscriptions = new IdentityHashMap<>();

    public <T> EventSubscription<T> subscribe(
            Class<T> eventType,
            EventHandler<T> handler
    ) {
        return subscribe(eventType, EventPriority.NORMAL, false, handler);
    }

    public <T> EventSubscription<T> subscribe(
            Class<T> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            EventHandler<T> handler
    ) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(handler, "handler");

        EventSubscription<T> subscription = new EventSubscription<>(
                eventType,
                priority,
                ignoreCancelled,
                handler,
                () -> unsubscribe(eventType, handler)
        );

        subscriptions
                .computeIfAbsent(eventType, ignored -> new ArrayList<>())
                .add(subscription);

        subscriptions.get(eventType).sort(
                Comparator.comparing(EventSubscription::priority)
        );

        return subscription;
    }

    public <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        List<EventSubscription<?>> list = subscriptions.get(eventType);

        if (list == null) {
            return;
        }

        list.removeIf(subscription -> subscription.eventType().equals(eventType));
        if (list.isEmpty()) {
            subscriptions.remove(eventType);
        }
    }

    public <T> T publish(T event) {
        Objects.requireNonNull(event, "event");

        for (EventSubscription<?> subscription : matchingSubscriptions(event.getClass())) {
            if (event instanceof CancellableKaraEvent cancellable
                    && cancellable.cancelled()
                    && subscription.ignoreCancelled()) {
                continue;
            }

            subscription.handle(event);
        }

        return event;
    }

    public int subscriptionCount() {
        return subscriptions.values()
                .stream()
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public void stop() {
        subscriptions.clear();
    }

    private List<EventSubscription<?>> matchingSubscriptions(Class<?> eventClass) {
        List<EventSubscription<?>> result = new ArrayList<>();

        for (Map.Entry<Class<?>, List<EventSubscription<?>>> entry : subscriptions.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventClass)) {
                result.addAll(entry.getValue());
            }
        }

        result.sort(Comparator.comparing(EventSubscription::priority));

        return result;
    }
}
