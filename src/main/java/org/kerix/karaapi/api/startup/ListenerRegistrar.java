package org.kerix.karaapi.api.startup;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ListenerRegistrar implements Stoppable {

    private final PaperListenerRegistrar paper;
    private final Set<ListenerRegistration> registrations = ConcurrentHashMap.newKeySet();

    private volatile boolean stopped;

    public ListenerRegistrar(JavaPlugin hostPlugin) {
        this.paper = new PaperListenerRegistrar(
                Objects.requireNonNull(hostPlugin, "hostPlugin")
        );
    }

    public ListenerRegistration register(Listener listener) {
        Objects.requireNonNull(listener, "listener");
        ensureRunning();

        ListenerRegistration registration = paper.register(listener);
        registrations.add(registration);

        return registration;
    }

    public List<ListenerRegistration> registerAll(Listener... listeners) {
        Objects.requireNonNull(listeners, "listeners");
        ensureRunning();

        List<ListenerRegistration> created = paper.registerAll(listeners);
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
