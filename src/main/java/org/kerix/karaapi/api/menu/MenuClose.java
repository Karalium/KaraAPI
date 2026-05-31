package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Objects;

public final class MenuClose {

    private final MenuService menus;
    private final Menu menu;
    private final InventoryCloseEvent event;
    private final Player player;

    public MenuClose(
            MenuService menus,
            Menu menu,
            InventoryCloseEvent event,
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

    public InventoryCloseEvent event() {
        return event;
    }

    public Player player() {
        return player;
    }

    public void reopenNextTick() {
        menus.openNextTick(player, menu);
    }

    public void openNextTick(Menu menu) {
        menus.openNextTick(player, menu);
    }
}
