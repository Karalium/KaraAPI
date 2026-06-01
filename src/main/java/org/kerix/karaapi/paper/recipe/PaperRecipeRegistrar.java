package org.kerix.karaapi.paper.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Objects;

public final class PaperRecipeRegistrar {

    private final JavaPlugin plugin;

    public PaperRecipeRegistrar(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void register(Recipe recipe) {
        Objects.requireNonNull(recipe, "recipe");

        Bukkit.addRecipe(recipe);
    }

    public void unregister(NamespacedKey key) {
        Objects.requireNonNull(key, "key");

        Bukkit.removeRecipe(key);
    }

    public void discover(Player player, NamespacedKey key) {
        player.discoverRecipe(key);
    }

    public void discover(Player player, Collection<NamespacedKey> keys) {
        player.discoverRecipes(keys);
    }

    public JavaPlugin plugin() {
        return plugin;
    }
}
