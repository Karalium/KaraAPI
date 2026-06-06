package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.ItemProvider;

public final class Recipes {

    private Recipes() {
    }

    public static ShapedRecipeBuilder shaped(NamespacedKey key, ItemStack result) {
        return ShapedRecipeBuilder.create(key, result);
    }

    public static ShapedRecipeBuilder shaped(NamespacedKey key, ItemProvider result) {
        return ShapedRecipeBuilder.create(key, result);
    }

    public static ShapelessRecipeBuilder shapeless(NamespacedKey key, ItemStack result) {
        return ShapelessRecipeBuilder.create(key, result);
    }

    public static ShapelessRecipeBuilder shapeless(NamespacedKey key, ItemProvider result) {
        return ShapelessRecipeBuilder.create(key, result);
    }
}
