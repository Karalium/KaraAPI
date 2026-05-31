package org.kerix.karaapi.api.item.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public record CustomItemInventoryClick(
        CustomItemService service,
        CustomItem customItem,
        InventoryClickEvent event,
        Player player,
        ItemStack item,
        int slot,
        int rawSlot
) {

    public void cancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
