package org.kerix.karaapi.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ShapedRecipeBuilder implements KaraRecipe {

    private final NamespacedKey key;
    private final ItemStack result;
    private final Map<Character, RecipeChoice> ingredients = new LinkedHashMap<>();

    private String[] shape;

    private ShapedRecipeBuilder(NamespacedKey key, ItemStack result) {
        this.key = Objects.requireNonNull(key, "key");
        this.result = Objects.requireNonNull(result, "result").clone();
    }

    public static ShapedRecipeBuilder create(NamespacedKey key, ItemStack result) {
        return new ShapedRecipeBuilder(key, result);
    }

    public ShapedRecipeBuilder shape(String... shape) {
        if (shape == null || shape.length == 0 || shape.length > 3) {
            throw new IllegalArgumentException("Recipe shape must have 1 to 3 rows.");
        }

        for (String row : shape) {
            if (row == null || row.length() > 3) {
                throw new IllegalArgumentException("Each recipe row must have 1 to 3 characters.");
            }
        }

        this.shape = shape;
        return this;
    }

    public ShapedRecipeBuilder ingredient(char key, Material material) {
        return ingredient(key, new RecipeChoice.MaterialChoice(material));
    }

    public ShapedRecipeBuilder exact(char key, ItemStack item) {
        return ingredient(key, new RecipeChoice.ExactChoice(item));
    }

    public ShapedRecipeBuilder ingredient(char key, RecipeChoice choice) {
        ingredients.put(key, Objects.requireNonNull(choice, "choice"));
        return this;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public Recipe recipe() {
        if (shape == null) {
            throw new IllegalStateException("Recipe shape has not been set.");
        }

        ShapedRecipe recipe = new ShapedRecipe(key, result.clone());
        recipe.shape(shape);

        for (Map.Entry<Character, RecipeChoice> entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }

        return recipe;
    }
}
