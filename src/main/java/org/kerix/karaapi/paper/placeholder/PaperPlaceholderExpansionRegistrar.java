package org.kerix.karaapi.paper.placeholder;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.placeholder.PlaceholderException;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansion;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionRegistrar;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionRegistration;
import org.kerix.karaapi.paper.placeholder.papi.PlaceholderExpansionAdapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class PaperPlaceholderExpansionRegistrar
        implements PlaceholderExpansionRegistrar {

    private final JavaPlugin hostPlugin;
    private final Map<String, PlaceholderExpansionAdapter> registered =
            new LinkedHashMap<>();

    public PaperPlaceholderExpansionRegistrar(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    @Override
    public boolean available() {
        return PlaceholderApiBridge.available(hostPlugin);
    }

    @Override
    public PlaceholderExpansionRegistration register(PlaceholderExpansion expansion) {
        Objects.requireNonNull(expansion, "expansion");

        if (!available()) {
            throw new PlaceholderException(
                    "PlaceholderAPI is not installed or not enabled."
            );
        }

        if (registered.containsKey(expansion.identifier())) {
            throw new PlaceholderException(
                    "Placeholder expansion is already registered: " + expansion.identifier()
            );
        }

        PlaceholderExpansionAdapter adapter =
                new PlaceholderExpansionAdapter(expansion);

        if (!adapter.register()) {
            throw new PlaceholderException(
                    "PlaceholderAPI rejected expansion: " + expansion.identifier()
            );
        }

        registered.put(expansion.identifier(), adapter);

        return new PlaceholderExpansionRegistration(
                expansion.identifier(),
                () -> unregister(expansion.identifier())
        );
    }

    public void unregister(String identifier) {
        String key = PlaceholderExpansion.normalizeIdentifier(identifier);

        PlaceholderExpansionAdapter adapter = registered.remove(key);

        if (adapter != null && adapter.isRegistered()) {
            adapter.unregister();
        }
    }
}
