package org.kerix.karaapi.api.item.custom;

import org.bukkit.inventory.ItemStack;

public interface CustomItem {

    String id();

    ItemStack create();

    default void onInteract(CustomItemInteract event) {
    }

    default void onAttack(CustomItemAttack event) {
    }

    default void onInventoryClick(CustomItemInventoryClick event) {
    }
}
