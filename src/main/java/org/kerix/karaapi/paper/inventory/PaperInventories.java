package org.kerix.karaapi.paper.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public final class PaperInventories {

    private PaperInventories() {
    }

    public static Inventory chest(
            InventoryHolder holder,
            int rows,
            Component title
    ) {
        Objects.requireNonNull(title, "title");

        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Chest inventory rows must be between 1 and 6.");
        }

        return Bukkit.createInventory(holder, rows * 9, title);
    }
}
