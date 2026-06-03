package org.kerix.karaapi.paper.listener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.event.EventPriority;

import java.util.Objects;
import java.util.function.Consumer;

public record PaperListenerRegistrar(JavaPlugin hostPlugin) {

    public PaperListenerRegistrar(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public void register(Listener listener) {
        Objects.requireNonNull(listener, "listener");

        hostPlugin.getServer()
                .getPluginManager()
                .registerEvents(listener, hostPlugin);
    }

    public void registerAll(Listener... listeners) {
        Objects.requireNonNull(listeners, "listeners");

        for (Listener listener : listeners) {
            register(listener);
        }
    }

    public <E extends Event> void listen(
            Class<E> eventType,
            Consumer<E> handler
    ) {
        listen(eventType, EventPriority.NORMAL, false, handler);
    }

    public <E extends Event> void listen(
            Class<E> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            Consumer<E> handler
    ) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(handler, "handler");

        Listener listener = new Listener() {
        };

        EventExecutor executor = (registeredListener, event) -> {
            if (!eventType.isInstance(event)) {
                return;
            }

            handler.accept(eventType.cast(event));
        };

        hostPlugin.getServer().getPluginManager().registerEvent(
                eventType,
                listener,
                priority,
                executor,
                hostPlugin,
                ignoreCancelled
        );
    }
}
