package org.kerix.karaapi.paper.placeholder;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionSpec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

public final class PlaceholderApiBridge {

    private static final String PLUGIN_NAME = "PlaceholderAPI";
    private static final String ADAPTER_CLASS =
            "org.kerix.karaapi.paper.placeholder.papi.PlaceholderExpansionAdapter";

    private PlaceholderApiBridge() {
    }

    public static boolean available(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        Plugin plugin = hostPlugin.getServer()
                .getPluginManager()
                .getPlugin(PLUGIN_NAME);

        return plugin != null && plugin.isEnabled();
    }

    public static boolean registerExpansion(
            JavaPlugin hostPlugin,
            PlaceholderExpansionSpec expansion
    ) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");
        Objects.requireNonNull(expansion, "expansion");

        if (!available(hostPlugin)) {
            hostPlugin.getLogger().warning(
                    "[KaraAPI] Cannot register PlaceholderAPI expansion '"
                            + expansion.identifier()
                            + "' because PlaceholderAPI is not installed or not enabled."
            );
            return false;
        }

        try {
            Class<?> adapterClass = Class.forName(ADAPTER_CLASS);

            Constructor<?> constructor = adapterClass.getConstructor(
                    PlaceholderExpansionSpec.class
            );

            Object adapter = constructor.newInstance(expansion);
            Method registerMethod = adapterClass.getMethod("register");
            Object result = registerMethod.invoke(adapter);

            return result instanceof Boolean bool && bool;
        } catch (ReflectiveOperationException exception) {
            hostPlugin.getLogger().warning(
                    "[KaraAPI] Failed to register PlaceholderAPI expansion '"
                            + expansion.identifier()
                            + "': "
                            + exception.getMessage()
            );
            return false;
        }
    }
}
