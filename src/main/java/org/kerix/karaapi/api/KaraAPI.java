package org.kerix.karaapi.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.bootstrap.PluginModule;
import org.kerix.karaapi.runtime.KaraRuntime;

import java.util.Objects;

public final class KaraAPI {

    private static KaraRuntime runtime;

    private KaraAPI() {
    }

    public static synchronized void init(JavaPlugin apiPlugin) {
        Objects.requireNonNull(apiPlugin, "apiPlugin");

        if (runtime != null) {
            throw new IllegalStateException("KaraAPI is already initialized.");
        }

        runtime = new KaraRuntime(apiPlugin);
    }

    public static PluginHandle boot(JavaPlugin hostPlugin, PluginModule... modules) {
        return runtime().boot(hostPlugin, modules);
    }

    public static void shutdown(JavaPlugin hostPlugin) {
        KaraRuntime current = runtime;

        if (current == null) {
            return;
        }

        current.shutdown(hostPlugin);
    }

    public static synchronized void shutdownAll() {
        if (runtime == null) {
            return;
        }

        try {
            runtime.shutdownAll();
        } finally {
            runtime = null;
        }
    }

    public static synchronized boolean initialized() {
        return runtime != null;
    }

    private static KaraRuntime runtime() {
        KaraRuntime current = runtime;

        if (current == null) {
            throw new IllegalStateException("KaraAPI has not been initialized.");
        }

        return current;
    }
}
