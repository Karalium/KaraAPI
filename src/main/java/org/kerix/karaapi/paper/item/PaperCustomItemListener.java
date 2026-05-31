package org.kerix.karaapi.paper.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.custom.CustomItemAttack;
import org.kerix.karaapi.api.item.custom.CustomItemInteract;
import org.kerix.karaapi.api.item.custom.CustomItemInventoryClick;
import org.kerix.karaapi.api.item.custom.CustomItemService;

import java.util.Objects;

public final class PaperCustomItemListener implements Listener {

    private final CustomItemService service;

    public PaperCustomItemListener(CustomItemService service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        ItemStack item = event.getItem();

        service.customItemOf(item).ifPresent(customItem ->
                customItem.onInteract(new CustomItemInteract(
                        service,
                        customItem,
                        event,
                        player,
                        item == null ? null : item.clone(),
                        event.getHand()
                ))
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        service.customItemOf(item).ifPresent(customItem ->
                customItem.onAttack(new CustomItemAttack(
                        service,
                        customItem,
                        event,
                        player,
                        event.getEntity(),
                        item.clone()
                ))
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        service.customItemOf(item).ifPresent(customItem ->
                customItem.onInventoryClick(new CustomItemInventoryClick(
                        service,
                        customItem,
                        event,
                        player,
                        item == null ? null : item.clone(),
                        event.getSlot(),
                        event.getRawSlot()
                ))
        );
    }
}
