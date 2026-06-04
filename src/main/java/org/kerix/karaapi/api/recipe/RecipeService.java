package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RecipeService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final RecipeRegistrar registrar;
    private final Map<NamespacedKey, RecipeRegistration> registrations = new LinkedHashMap<>();

    private boolean stopped;

    public RecipeService(JavaPlugin hostPlugin, RecipeRegistrar registrar) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.registrar = Objects.requireNonNull(registrar, "registrar");
    }

    public RecipeRegistration register(RecipeDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        return register(definition.key(), definition.recipe());
    }

    public RecipeRegistration register(NamespacedKey key, Recipe recipe) {
        ensureRunning();
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(recipe, "recipe");

        if (registrations.containsKey(key)) {
            throw new RecipeException("Recipe is already registered: " + key);
        }

        registrar.register(recipe);

        RecipeRegistration registration = new RecipeRegistration(
                key,
                () -> unregister(key)
        );

        registrations.put(key, registration);

        return registration;
    }

    public void replace(RecipeDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        if (registered(definition.key())) {
            unregister(definition.key());
        }

        register(definition);
    }

    public void unregister(NamespacedKey key) {
        Objects.requireNonNull(key, "key");

        RecipeRegistration registration = registrations.remove(key);

        if (registration == null) {
            registrar.unregister(key);
            return;
        }

        registrar.unregister(key);
    }

    public boolean registered(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return registrations.containsKey(key);
    }

    public void discover(Player player, NamespacedKey key) {
        ensureRunning();
        registrar.discover(player, key);
    }

    public void discover(Player player, RecipeDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        discover(player, definition.key());
    }

    public void discoverAll(Player player) {
        ensureRunning();
        registrar.discover(player, registrations.keySet());
    }

    public Set<NamespacedKey> registeredKeys() {
        return Set.copyOf(registrations.keySet());
    }

    public int registeredCount() {
        return registrations.size();
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

        for (NamespacedKey key : Set.copyOf(registrations.keySet())) {
            unregister(key);
        }

        registrations.clear();
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("RecipeService has already stopped.");
        }
    }
}
