package org.kerix.karaapi.paper.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class PaperItems {

    private PaperItems() {
    }

    public static ItemStack create(Material material) {
        return create(material, 1);
    }

    public static ItemStack create(Material material, int amount) {
        Objects.requireNonNull(material, "material");

        if (!material.isItem()) {
            throw new IllegalArgumentException(material + " is not an item material.");
        }

        if (amount < 1) {
            throw new IllegalArgumentException("Item amount must be at least 1.");
        }

        return ItemStack.of(material, amount);
    }

    public static ItemStack withType(ItemStack item, Material material) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(material, "material");

        if (!material.isItem()) {
            throw new IllegalArgumentException(material + " is not an item material.");
        }

        return item.withType(material);
    }

    public static ItemStack copy(ItemStack item) {
        Objects.requireNonNull(item, "item");

        return item.clone();
    }
}
