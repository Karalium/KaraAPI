package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.PluginHandle;
import org.kerix.karaapi.api.bootstrap.PluginModule;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public final class KaraRuntime {

    private final JavaPlugin apiPlugin;
    private final Map<JavaPlugin, PluginKernel> kernels = new IdentityHashMap<>();

    public KaraRuntime(JavaPlugin apiPlugin) {
        this.apiPlugin = Objects.requireNonNull(apiPlugin, "apiPlugin");
    }

    public PluginHandle boot(JavaPlugin hostPlugin, PluginModule... modules) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");
        Objects.requireNonNull(modules, "modules");

        if (kernels.containsKey(hostPlugin)) {
            throw new IllegalStateException(
                    hostPlugin.getName() + " is already booted by KaraAPI."
            );
        }

        PluginKernel kernel = new PluginKernel(apiPlugin, hostPlugin);
        kernels.put(hostPlugin, kernel);

        try {
            kernel.boot(modules);

            return new PluginHandle(
                    hostPlugin,
                    kernel.context(),
                    () -> shutdown(hostPlugin)
            );
        } catch (Throwable throwable) {
            kernels.remove(hostPlugin);

            try {
                kernel.shutdown();
            } catch (Throwable shutdownError) {
                throwable.addSuppressed(shutdownError);
            }

            throw new IllegalStateException(
                    "Failed to boot " + hostPlugin.getName() + " using KaraAPI.",
                    throwable
            );
        }
    }

    public void shutdown(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        PluginKernel kernel = kernels.remove(hostPlugin);

        if (kernel == null) {
            return;
        }

        kernel.shutdown();
    }

    public void shutdownAll() {
        for (PluginKernel kernel : List.copyOf(kernels.values())) {
            try {
                kernel.shutdown();
            } catch (Throwable throwable) {
                apiPlugin.getLogger().log(
                        Level.SEVERE,
                        "Failed to shutdown a KaraAPI plugin kernel.",
                        throwable
                );
            }
        }

        kernels.clear();
    }

    public boolean isBooted(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        return kernels.containsKey(hostPlugin);
    }

    public Optional<PluginKernel> kernel(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        return Optional.ofNullable(kernels.get(hostPlugin));
    }

    public JavaPlugin apiPlugin() {
        return apiPlugin;
    }
}
