package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Objects;

public final class MenuOpen {

    private final MenuService menus;
    private final Menu menu;
    private final InventoryOpenEvent event;
    private final Player player;

    public MenuOpen(
            MenuService menus,
            Menu menu,
            InventoryOpenEvent event,
            Player player
    ) {
        this.menus = Objects.requireNonNull(menus, "menus");
        this.menu = Objects.requireNonNull(menu, "menu");
        this.event = Objects.requireNonNull(event, "event");
        this.player = Objects.requireNonNull(player, "player");
    }

    public MenuService menus() {
        return menus;
    }

    public Menu menu() {
        return menu;
    }

    public InventoryOpenEvent event() {
        return event;
    }

    public Player player() {
        return player;
    }

    public void cancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public boolean cancelled() {
        return event.isCancelled();
    }
}