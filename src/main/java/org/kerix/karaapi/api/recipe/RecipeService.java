package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.paper.recipe.PaperRecipeRegistrar;

import java.util.LinkedHashSet;
import java.util.Set;

public final class RecipeService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final PaperRecipeRegistrar registrar;
    private final Set<NamespacedKey> registered = new LinkedHashSet<>();

    public RecipeService(JavaPlugin hostPlugin) {
        this.hostPlugin = hostPlugin;
        this.registrar = new PaperRecipeRegistrar(hostPlugin);
    }

    public void register(KaraRecipe recipe) {
        registrar.register(recipe.recipe());
        registered.add(recipe.key());
    }

    public void unregister(NamespacedKey key) {
        registrar.unregister(key);
        registered.remove(key);
    }

    public void discover(Player player, NamespacedKey key) {
        registrar.discover(player, key);
    }

    public void discoverAll(Player player) {
        registrar.discover(player, registered);
    }

    public Set<NamespacedKey> registeredKeys() {
        return Set.copyOf(registered);
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        for (NamespacedKey key : Set.copyOf(registered)) {
            unregister(key);
        }

        registered.clear();
    }
}
