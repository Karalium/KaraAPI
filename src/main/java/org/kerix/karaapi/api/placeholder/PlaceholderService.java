package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.annotation.RequiresPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ManagedService(
        value = PlaceholderService.class,
        priority = 35,
        registerAnnotatedTicks = false
)
@RequiresPlugin(value = "PlaceholderAPI",required = false)
public final class PlaceholderService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final List<PlaceholderProvider> providers = new ArrayList<>();
    private final PlaceholderExpansionRegistrar expansionRegistrar;
    private final Map<String, PlaceholderExpansionRegistration> expansionRegistrations =
            new LinkedHashMap<>();

    private boolean stopped;

    public PlaceholderService(JavaPlugin hostPlugin) {
        this(hostPlugin, List.of(), null);
    }

    public PlaceholderService(
            JavaPlugin hostPlugin,
            Collection<? extends PlaceholderProvider> externalProviders,
            PlaceholderExpansionRegistrar expansionRegistrar
    ) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.expansionRegistrar = expansionRegistrar;

        registerProvider(new LocalPlaceholderProvider());

        if (externalProviders != null) {
            for (PlaceholderProvider provider : externalProviders) {
                registerProvider(provider);
            }
        }
    }

    public void registerProvider(PlaceholderProvider provider) {
        ensureRunning();
        Objects.requireNonNull(provider, "provider");
        providers.add(provider);
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

    public PlaceholderExpansionRegistration registerExpansion(
            PlaceholderExpansion expansion
    ) {
        ensureRunning();
        Objects.requireNonNull(expansion, "expansion");

        if (expansionRegistrar == null || !expansionRegistrar.available()) {
            throw new PlaceholderException(
                    "PlaceholderAPI is not installed or not enabled; cannot register expansion: "
                            + expansion.identifier()
            );
        }

        if (expansionRegistrations.containsKey(expansion.identifier())) {
            throw new PlaceholderException(
                    "Placeholder expansion is already registered: " + expansion.identifier()
            );
        }

        PlaceholderExpansionRegistration registration =
                expansionRegistrar.register(expansion);

        expansionRegistrations.put(expansion.identifier(), registration);

        return registration;
    }

    public PlaceholderExpansionRegistration expansion(
            PlaceholderExpansion expansion
    ) {
        return registerExpansion(expansion);
    }

    public void unregisterExpansion(String identifier) {
        String key = PlaceholderExpansion.normalizeIdentifier(identifier);

        PlaceholderExpansionRegistration registration =
                expansionRegistrations.remove(key);

        if (registration != null) {
            registration.unregister();
        }
    }

    public boolean expansionRegistered(String identifier) {
        return expansionRegistrations.containsKey(
                PlaceholderExpansion.normalizeIdentifier(identifier)
        );
    }

    public List<PlaceholderProvider> providers() {
        return List.copyOf(providers);
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;

        for (PlaceholderExpansionRegistration registration : expansionRegistrations.values()) {
            registration.unregister();
        }

        expansionRegistrations.clear();
        providers.clear();
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("PlaceholderService has already stopped.");
        }
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
            return context.placeholders().applyAll(input);
        }
    }
}
