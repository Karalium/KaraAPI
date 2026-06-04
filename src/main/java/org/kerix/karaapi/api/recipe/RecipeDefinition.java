package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;

public interface RecipeDefinition {

    NamespacedKey key();

    org.bukkit.inventory.Recipe recipe();
}
