package org.kerix.karaapi.paper.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.startup.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PaperListenerRegistrar(JavaPlugin hostPlugin) {

    public PaperListenerRegistrar(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public ListenerRegistration register(Listener listener) {
        Objects.requireNonNull(listener, "listener");

        hostPlugin.getServer()
                .getPluginManager()
                .registerEvents(listener, hostPlugin);

        return new ListenerRegistration(listener);
    }

    public List<ListenerRegistration> registerAll(Listener... listeners) {
        Objects.requireNonNull(listeners, "listeners");

        List<ListenerRegistration> registrations = new ArrayList<>();

        for (Listener listener : listeners) {
            registrations.add(register(listener));
        }

        return List.copyOf(registrations);
    }
}
