package org.kerix.karaapi.api.item.custom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public record CustomItemAttack(
        CustomItemService service,
        CustomItem customItem,
        EntityDamageByEntityEvent event,
        Player player,
        Entity target,
        ItemStack item
) {

    public boolean cancelled() {
        return event.isCancelled();
    }

    public void cancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public void deny() {
        cancelled(true);
    }

    public double damage() {
        return event.getDamage();
    }

    public void damage(double damage) {
        event.setDamage(damage);
    }
}
