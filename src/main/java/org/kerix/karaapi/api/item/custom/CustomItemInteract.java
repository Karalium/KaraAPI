package org.kerix.karaapi.api.item.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public record CustomItemInteract(
        CustomItemService service,
        CustomItem customItem,
        PlayerInteractEvent event,
        Player player,
        ItemStack item,
        EquipmentSlot hand
) {

    public void cancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}