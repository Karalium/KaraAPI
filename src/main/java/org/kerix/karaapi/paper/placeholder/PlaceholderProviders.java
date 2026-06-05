package org.kerix.karaapi.paper.placeholder;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionRegistrar;
import org.kerix.karaapi.api.placeholder.PlaceholderProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlaceholderProviders {

    private PlaceholderProviders() {
    }

    public static List<PlaceholderProvider> create(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        List<PlaceholderProvider> providers = new ArrayList<>();

        PlaceholderApiProvider papi = PlaceholderApiProvider.tryCreate(hostPlugin);

        if (papi != null) {
            providers.add(papi);
            hostPlugin.getLogger().info("[KaraAPI] Hooked into PlaceholderAPI.");
        } else {
            hostPlugin.getLogger().fine("[KaraAPI] PlaceholderAPI not found; external placeholders disabled.");
        }

        return List.copyOf(providers);
    }

    public static PlaceholderExpansionRegistrar expansionRegistrar(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");
        return new PaperPlaceholderExpansionRegistrar(hostPlugin);
    }
}
