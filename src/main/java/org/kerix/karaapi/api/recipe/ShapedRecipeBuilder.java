package org.kerix.karaapi.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.kerix.karaapi.api.item.ItemProvider;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ShapedRecipeBuilder implements RecipeDefinition {

    private final NamespacedKey key;
    private final ItemProvider result;
    private final Map<Character, RecipeChoice> ingredients = new LinkedHashMap<>();

    private String[] shape;
    private String group;

    private ShapedRecipeBuilder(NamespacedKey key, ItemProvider result) {
        this.key = Objects.requireNonNull(key, "key");
        this.result = Objects.requireNonNull(result, "result");
    }

    public static ShapedRecipeBuilder create(NamespacedKey key, ItemStack result) {
        return new ShapedRecipeBuilder(key, ItemProvider.of(result));
    }

    public static ShapedRecipeBuilder create(NamespacedKey key, ItemProvider result) {
        return new ShapedRecipeBuilder(key, result);
    }

    public ShapedRecipeBuilder shape(String... shape) {
        validateShape(shape);
        this.shape = Arrays.copyOf(shape, shape.length);
        return this;
    }

    public ShapedRecipeBuilder group(String group) {
        this.group = group == null || group.isBlank() ? null : group;
        return this;
    }

    public ShapedRecipeBuilder ingredient(char key, Material material) {
        Objects.requireNonNull(material, "material");
        return ingredient(key, new RecipeChoice.MaterialChoice(material));
    }

    public ShapedRecipeBuilder exact(char key, ItemStack item) {
        Objects.requireNonNull(item, "item");
        return ingredient(key, new RecipeChoice.ExactChoice(item.clone()));
    }

    public ShapedRecipeBuilder exact(char key, ItemProvider item) {
        Objects.requireNonNull(item, "item");
        return exact(key, item.build());
    }

    public ShapedRecipeBuilder ingredient(char key, RecipeChoice choice) {
        if (key == ' ') {
            throw new IllegalArgumentException("Space cannot be used as a shaped recipe ingredient key.");
        }

        ingredients.put(key, Objects.requireNonNull(choice, "choice"));
        return this;
    }

    public Map<Character, RecipeChoice> ingredients() {
        return Map.copyOf(ingredients);
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public ShapedRecipe recipe() {
        if (shape == null) {
            throw new IllegalStateException("Recipe shape has not been set.");
        }

        ItemStack resultItem = result.build();

        if (resultItem == null || resultItem.getType().isAir()) {
            throw new IllegalStateException("Recipe result cannot be null or air.");
        }

        ShapedRecipe recipe = new ShapedRecipe(key, resultItem.clone());
        recipe.shape(shape);

        if (group != null) {
            recipe.setGroup(group);
        }

        for (Map.Entry<Character, RecipeChoice> entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }

        return recipe;
    }

    private static void validateShape(String[] shape) {
        if (shape == null || shape.length == 0 || shape.length > 3) {
            throw new IllegalArgumentException("Recipe shape must have 1 to 3 rows.");
        }

        int width = -1;

        for (String row : shape) {
            if (row == null || row.isEmpty() || row.length() > 3) {
                throw new IllegalArgumentException("Each recipe row must have 1 to 3 characters.");
            }

            if (width == -1) {
                width = row.length();
            }

            if (row.length() != width) {
                throw new IllegalArgumentException("All recipe rows must have the same width.");
            }
        }
    }

    public RecipeDefinition build() {
        return this;
    }
}
