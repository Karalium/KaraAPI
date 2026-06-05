package org.kerix.karaapi.paper.placeholder;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PlaceholderApiBridge {

    private static final String PLUGIN_NAME = "PlaceholderAPI";

    private PlaceholderApiBridge() {
    }

    public static boolean available(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        Plugin plugin = hostPlugin.getServer()
                .getPluginManager()
                .getPlugin(PLUGIN_NAME);

        return plugin != null && plugin.isEnabled();
    }
}
