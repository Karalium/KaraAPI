package org.kerix.karaapi.paper.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.placeholder.PlaceholderContext;
import org.kerix.karaapi.api.placeholder.PlaceholderProvider;

import java.lang.reflect.Method;
import java.util.Objects;

public final class PlaceholderApiProvider implements PlaceholderProvider {

    private static final String PLUGIN_NAME = "PlaceholderAPI";
    private static final String API_CLASS = "me.clip.placeholderapi.PlaceholderAPI";

    private final JavaPlugin hostPlugin;
    private final Method setPlaceholdersMethod;

    private PlaceholderApiProvider(JavaPlugin hostPlugin, Method setPlaceholdersMethod) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.setPlaceholdersMethod = Objects.requireNonNull(setPlaceholdersMethod, "setPlaceholdersMethod");
    }

    public static PlaceholderApiProvider tryCreate(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        Plugin plugin = hostPlugin.getServer()
                .getPluginManager()
                .getPlugin(PLUGIN_NAME);

        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> placeholderApiClass = Class.forName(API_CLASS);

            Method method = placeholderApiClass.getMethod(
                    "setPlaceholders",
                    OfflinePlayer.class,
                    String.class
            );

            return new PlaceholderApiProvider(hostPlugin, method);
        } catch (ReflectiveOperationException exception) {
            hostPlugin.getLogger().warning(
                    "[KaraAPI] PlaceholderAPI was found, but its API could not be hooked: "
                            + exception.getMessage()
            );
            return null;
        }
    }

    @Override
    public String name() {
        return PLUGIN_NAME;
    }

    @Override
    public boolean available() {
        return PlaceholderApiBridge.available(hostPlugin);
    }

    @Override
    public String apply(PlaceholderContext context, String input) {
        String safeInput = input == null ? "" : input;

        if (!available()) {
            return safeInput;
        }

        try {
            Object result = setPlaceholdersMethod.invoke(
                    null,
                    context.player(),
                    safeInput
            );

            return result == null ? safeInput : String.valueOf(result);
        } catch (ReflectiveOperationException exception) {
            hostPlugin.getLogger().warning(
                    "[KaraAPI] Failed to apply PlaceholderAPI placeholders: "
                            + exception.getMessage()
            );
            return safeInput;
        }
    }
}
