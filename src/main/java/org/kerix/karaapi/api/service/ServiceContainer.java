package org.kerix.karaapi.api.service;

import org.kerix.karaapi.api.lifecycle.Startable;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.lifecycle.Tickable;
import org.kerix.karaapi.api.tick.TickOrchestrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServiceContainer {

    private final Logger log;
    private final LinkedHashMap<Class<?>, Object> services = new LinkedHashMap<>();

    private boolean started;

    public ServiceContainer(Logger log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public <T> T bind(Class<T> key, T implementation) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(implementation, "implementation");

        if (started) {
            throw new IllegalStateException(
                    "[ServiceContainer] Cannot bind " + key.getSimpleName() + " after startAll()."
            );
        }

        if (services.containsKey(key)) {
            throw new IllegalStateException(
                    "[ServiceContainer] Duplicate binding for " + key.getSimpleName()
            );
        }

        services.put(key, implementation);

        log.fine("[ServiceContainer] Bound "
                + key.getSimpleName()
                + " -> "
                + implementation.getClass().getSimpleName());

        return implementation;
    }

    @SuppressWarnings("unchecked")
    public <T> T bind(T implementation) {
        Objects.requireNonNull(implementation, "implementation");

        return bind((Class<T>) implementation.getClass(), implementation);
    }

    public <T> T get(Class<T> key) {
        Objects.requireNonNull(key, "key");

        Object service = services.get(key);

        if (service == null) {
            throw new NoSuchElementException(
                    "[ServiceContainer] No binding for " + key.getSimpleName()
            );
        }

        return key.cast(service);
    }

    public <T> Optional<T> find(Class<T> key) {
        Objects.requireNonNull(key, "key");

        return Optional.ofNullable(services.get(key)).map(key::cast);
    }

    public boolean isBound(Class<?> key) {
        return services.containsKey(key);
    }

    public <T> void unbind(Class<T> key) {
        Objects.requireNonNull(key, "key");

        if (!services.containsKey(key)) {
            log.warning("[ServiceContainer] unbind() called for unregistered key: "
                    + key.getSimpleName());
            return;
        }

        services.remove(key);
        log.fine("[ServiceContainer] Unbound " + key.getSimpleName());
    }

    public void startAll() {
        if (started) {
            return;
        }

        for (Object service : services.values()) {
            if (!(service instanceof Startable startable)) {
                continue;
            }

            try {
                startable.start();

                log.fine("[ServiceContainer] Started "
                        + service.getClass().getSimpleName());
            } catch (Throwable throwable) {
                throw new IllegalStateException(
                        "[ServiceContainer] Failed to start "
                                + service.getClass().getSimpleName(),
                        throwable
                );
            }
        }

        started = true;
    }

    public void registerTickables(TickOrchestrator tickOrchestrator) {
        Objects.requireNonNull(tickOrchestrator, "tickOrchestrator");

        for (Object service : services.values()) {
            if (!(service instanceof Tickable tickable)) {
                continue;
            }

            tickOrchestrator.register(tickable);

            log.fine("[ServiceContainer] Registered tickable "
                    + service.getClass().getSimpleName()
                    + " every "
                    + tickable.tickInterval()
                    + " ticks");
        }
    }

    public void shutdownAll() {
        List<Object> reversed = new ArrayList<>(services.values());
        Collections.reverse(reversed);

        for (Object service : reversed) {
            if (!(service instanceof Stoppable stoppable)) {
                continue;
            }

            try {
                stoppable.stop();

                log.fine("[ServiceContainer] Stopped "
                        + service.getClass().getSimpleName());
            } catch (Throwable throwable) {
                log.log(
                        Level.SEVERE,
                        "[ServiceContainer] Error stopping "
                                + service.getClass().getSimpleName(),
                        throwable
                );
            }
        }

        services.clear();
        started = false;

        log.info("[ServiceContainer] All services stopped.");
    }

    public void logBindings() {
        log.info("[ServiceContainer] Registered services (" + services.size() + "):");

        services.forEach((key, implementation) ->
                log.info("  "
                        + String.format("%-40s", key.getSimpleName())
                        + " -> "
                        + implementation.getClass().getSimpleName()));
    }
}
