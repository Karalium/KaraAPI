package org.kerix.karaapi.paper.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.kerix.karaapi.api.menu.Menu;
import org.kerix.karaapi.api.menu.MenuService;

import java.util.Objects;
import java.util.UUID;

public final class PaperMenuHolder implements InventoryHolder {

    private final MenuService menus;
    private final Menu menu;
    private final UUID viewerId;

    private Inventory inventory;

    public PaperMenuHolder(MenuService menus, Menu menu, UUID viewerId) {
        this.menus = Objects.requireNonNull(menus, "menus");
        this.menu = Objects.requireNonNull(menu, "menu");
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId");
    }

    public MenuService menus() {
        return menus;
    }

    public Menu menu() {
        return menu;
    }

    public UUID viewerId() {
        return viewerId;
    }

    public void inventory(Inventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("Inventory has not been assigned yet.");
        }

        return inventory;
    }
}
