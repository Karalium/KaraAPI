package org.kerix.karaapi.api.item;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemProvider {

    ItemStack build();

    static ItemProvider of(ItemStack item) {
        return () -> item == null ? null : item.clone();
    }
}
