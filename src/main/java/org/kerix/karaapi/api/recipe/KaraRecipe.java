package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface KaraRecipe {

    NamespacedKey key();

    Recipe recipe();
}
