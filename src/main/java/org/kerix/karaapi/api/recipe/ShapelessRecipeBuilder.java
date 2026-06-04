package org.kerix.karaapi.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.kerix.karaapi.api.item.ItemProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShapelessRecipeBuilder implements RecipeDefinition {

    private final NamespacedKey key;
    private final ItemProvider result;
    private final List<RecipeChoice> ingredients = new ArrayList<>();

    private String group;

    private ShapelessRecipeBuilder(NamespacedKey key, ItemProvider result) {
        this.key = Objects.requireNonNull(key, "key");
        this.result = Objects.requireNonNull(result, "result");
    }

    public static ShapelessRecipeBuilder create(NamespacedKey key, ItemStack result) {
        return new ShapelessRecipeBuilder(key, ItemProvider.of(result));
    }

    public static ShapelessRecipeBuilder create(NamespacedKey key, ItemProvider result) {
        return new ShapelessRecipeBuilder(key, result);
    }

    public ShapelessRecipeBuilder group(String group) {
        this.group = group == null || group.isBlank() ? null : group;
        return this;
    }

    public ShapelessRecipeBuilder ingredient(Material material) {
        Objects.requireNonNull(material, "material");
        return ingredient(new RecipeChoice.MaterialChoice(material));
    }

    public ShapelessRecipeBuilder exact(ItemStack item) {
        Objects.requireNonNull(item, "item");
        return ingredient(new RecipeChoice.ExactChoice(item.clone()));
    }

    public ShapelessRecipeBuilder exact(ItemProvider item) {
        Objects.requireNonNull(item, "item");
        return exact(item.build());
    }

    public ShapelessRecipeBuilder ingredient(RecipeChoice choice) {
        ingredients.add(Objects.requireNonNull(choice, "choice"));
        return this;
    }

    public List<RecipeChoice> ingredients() {
        return List.copyOf(ingredients);
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public ShapelessRecipe recipe() {
        ItemStack resultItem = result.build();

        if (resultItem == null || resultItem.getType().isAir()) {
            throw new IllegalStateException("Recipe result cannot be null or air.");
        }

        ShapelessRecipe recipe = new ShapelessRecipe(key, resultItem.clone());

        if (group != null) {
            recipe.setGroup(group);
        }

        for (RecipeChoice ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }

    public RecipeDefinition build() {
        return this;
    }
}
