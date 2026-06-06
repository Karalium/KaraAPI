package org.kerix.karaapi.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.DefaultXBoundary;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.SinceApi;
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

    @Contract("_, _ -> new")
    @MainThread
    public static @NonNull PluginHandle boot(JavaPlugin hostPlugin, PluginModule... modules) {
        return runtime().boot(hostPlugin, modules);
    }

    @MainThread
    public static void shutdown(JavaPlugin hostPlugin) {
        KaraRuntime current = runtime;

        if (current == null) {
            return;
        }

        current.shutdown(hostPlugin);
    }

    @MainThread
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

    @Contract(pure = true)
    private static @NonNull KaraRuntime runtime() {
        KaraRuntime current = runtime;

        if (current == null) {
            throw new IllegalStateException("KaraAPI has not been initialized.");
        }

        return current;
    }
}
