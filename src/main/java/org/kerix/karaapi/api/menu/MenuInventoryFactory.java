package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface MenuInventoryFactory {

    Inventory create(MenuService menus, Menu menu, Player viewer);
}
