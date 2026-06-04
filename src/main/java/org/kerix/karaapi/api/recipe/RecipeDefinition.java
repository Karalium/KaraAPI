package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public interface RecipeDefinition {

    NamespacedKey key();

    Recipe recipe();

    default ItemStack result() {
        return recipe().getResult().clone();
    }
}
