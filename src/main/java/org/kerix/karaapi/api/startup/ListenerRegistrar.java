package org.kerix.karaapi.api.startup;

import org.bukkit.event.Listener;
import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ManagedService(
        value = ListenerRegistrar.class,
        priority = 95,
        registerAnnotatedTicks = false
)
@MainThread
public final class ListenerRegistrar implements Stoppable {

    private final ListenerGateway gateway;
    private final Set<ListenerRegistration> registrations = ConcurrentHashMap.newKeySet();

    private volatile boolean stopped;

    public ListenerRegistrar(ListenerGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public ListenerRegistration register(Listener listener) {
        ensureRunning();

        ListenerRegistration registration = gateway.register(
                Objects.requireNonNull(listener, "listener")
        );

        registrations.add(registration);
        return registration;
    }

    public List<ListenerRegistration> registerAll(Listener... listeners) {
        ensureRunning();
        Objects.requireNonNull(listeners, "listeners");

        List<ListenerRegistration> created = gateway.registerAll(listeners);
        registrations.addAll(created);

        return created;
    }

    public void unregister(ListenerRegistration registration) {
        if (registration == null) {
            return;
        }

        registration.unregister();
        registrations.remove(registration);
    }

    public void unregister(Listener listener) {
        if (listener == null) {
            return;
        }

        for (ListenerRegistration registration : Set.copyOf(registrations)) {
            if (registration.listener() == listener) {
                unregister(registration);
            }
        }
    }

    public void unregisterAll() {
        for (ListenerRegistration registration : Set.copyOf(registrations)) {
            registration.unregister();
        }

        registrations.clear();
    }

    public int registeredCount() {
        cleanup();
        return registrations.size();
    }

    public Set<ListenerRegistration> registrations() {
        cleanup();
        return Set.copyOf(registrations);
    }

    @Override
    public void stop() {
        stopped = true;
        unregisterAll();
    }

    private void cleanup() {
        registrations.removeIf(registration -> !registration.active());
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("ListenerRegistrar has already stopped.");
        }
    }
}
