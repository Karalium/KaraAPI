package org.kerix.karaapi.paper.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.recipe.RecipeRegistrar;

import java.util.Collection;
import java.util.Objects;


@MainThread
public record PaperRecipeRegistrar(JavaPlugin plugin) implements RecipeRegistrar {

    public PaperRecipeRegistrar(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public void register(Recipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void unregister(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        Bukkit.removeRecipe(key);
    }

    @Override
    public void discover(Player player, NamespacedKey key) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(key, "key");

        player.discoverRecipe(key);
    }

    @Override
    public void discover(Player player, Collection<NamespacedKey> keys) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(keys, "keys");

        player.discoverRecipes(keys);
    }
}