package org.kerix.karaapi.paper.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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
}
