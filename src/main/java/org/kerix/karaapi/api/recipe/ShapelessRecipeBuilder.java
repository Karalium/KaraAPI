package org.kerix.karaapi.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShapelessRecipeBuilder implements RecipeDefinition {

    private final NamespacedKey key;
    private final ItemStack result;
    private final List<RecipeChoice> ingredients = new ArrayList<>();

    private ShapelessRecipeBuilder(NamespacedKey key, ItemStack result) {
        this.key = Objects.requireNonNull(key, "key");
        this.result = Objects.requireNonNull(result, "result").clone();
    }

    public static ShapelessRecipeBuilder create(NamespacedKey key, ItemStack result) {
        return new ShapelessRecipeBuilder(key, result);
    }

    public ShapelessRecipeBuilder ingredient(Material material) {
        ingredients.add(new RecipeChoice.MaterialChoice(material));
        return this;
    }

    public ShapelessRecipeBuilder exact(ItemStack item) {
        ingredients.add(new RecipeChoice.ExactChoice(item));
        return this;
    }

    public ShapelessRecipeBuilder ingredient(RecipeChoice choice) {
        ingredients.add(Objects.requireNonNull(choice, "choice"));
        return this;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public org.bukkit.inventory.Recipe recipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result.clone());

        for (RecipeChoice ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }
}
