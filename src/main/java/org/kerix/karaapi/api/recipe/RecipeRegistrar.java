package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Collection;

public interface RecipeRegistrar {

    void register(Recipe recipe);

    void unregister(NamespacedKey key);

    void discover(Player player, NamespacedKey key);

    void discover(Player player, Collection<NamespacedKey> keys);
}
