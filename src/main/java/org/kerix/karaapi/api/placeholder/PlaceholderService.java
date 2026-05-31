package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.paper.placeholder.PlaceholderApiBridge;
import org.kerix.karaapi.paper.placeholder.PlaceholderApiProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlaceholderService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final List<PlaceholderProvider> providers = new ArrayList<>();

    public PlaceholderService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");

        registerProvider(new LocalPlaceholderProvider());

        PlaceholderApiProvider papi = PlaceholderApiProvider.tryCreate(hostPlugin);

        if (papi != null) {
            registerProvider(papi);
            hostPlugin.getLogger().info("[KaraAPI] Hooked into PlaceholderAPI.");
        } else {
            hostPlugin.getLogger().fine("[KaraAPI] PlaceholderAPI not found; PAPI placeholders disabled.");
        }
    }

    public void registerProvider(PlaceholderProvider provider) {
        Objects.requireNonNull(provider, "provider");

        providers.add(provider);
    }

    public boolean placeholderApiAvailable() {
        return PlaceholderApiBridge.available(hostPlugin);
    }

    public boolean registerExpansion(KaraPlaceholderExpansion expansion) {
        Objects.requireNonNull(expansion, "expansion");

        return PlaceholderApiBridge.registerExpansion(hostPlugin, expansion);
    }

    public String apply(String input) {
        return apply(PlaceholderContext.empty(), input);
    }

    public String apply(OfflinePlayer player, String input) {
        return apply(PlaceholderContext.of(player), input);
    }

    public String apply(OfflinePlayer player, String input, PlaceholderSet placeholders) {
        return apply(PlaceholderContext.of(player, placeholders), input);
    }

    public String apply(PlaceholderContext context, String input) {
        Objects.requireNonNull(context, "context");

        String result = input == null ? "" : input;

        for (PlaceholderProvider provider : providers) {
            if (!provider.available()) {
                continue;
            }

            result = provider.apply(context, result);
        }

        return result;
    }

    public List<PlaceholderProvider> providers() {
        return List.copyOf(providers);
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        providers.clear();
    }

    private static final class LocalPlaceholderProvider implements PlaceholderProvider {

        @Override
        public String name() {
            return "local";
        }

        @Override
        public boolean available() {
            return true;
        }

        @Override
        public String apply(PlaceholderContext context, String input) {
            return context.placeholders().applyBoth(input);
        }
    }
}
