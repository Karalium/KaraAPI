package org.kerix.karaapi.api.startup;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

public final class ListenerRegistrar {

    private final PaperListenerRegistrar paper;

    public ListenerRegistrar(JavaPlugin hostPlugin) {
        this.paper = new PaperListenerRegistrar(
                Objects.requireNonNull(hostPlugin, "hostPlugin")
        );
    }

    public void register(Listener listener) {
        paper.register(listener);
    }

    public void registerAll(Listener... listeners) {
        paper.registerAll(listeners);
    }
}
