package org.kerix.karaapi.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.bootstrap.PluginModule;
import org.kerix.karaapi.runtime.KaraRuntime;

public final class KaraAPI {

    private static KaraRuntime runtime;

    private KaraAPI() {
    }

    public static void init(JavaPlugin apiPlugin) {
        if (runtime != null) {
            throw new IllegalStateException("KaraAPI is already initialized.");
        }

        runtime = new KaraRuntime(apiPlugin);
    }

    public static PluginHandle boot(JavaPlugin hostPlugin, PluginModule... modules) {
        return runtime().boot(hostPlugin, modules);
    }

    public static void shutdown(JavaPlugin hostPlugin) {
        runtime().shutdown(hostPlugin);
    }

    public static void shutdownAll() {
        if (runtime != null) {
            runtime.shutdownAll();
        }
    }

    private static KaraRuntime runtime() {
        if (runtime == null) {
            throw new IllegalStateException("KaraAPI has not been initialized.");
        }

        return runtime;
    }
}
