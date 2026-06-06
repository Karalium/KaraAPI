package org.kerix.karaapi.paper.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.kerix.karaapi.api.menu.Menu;
import org.kerix.karaapi.api.menu.MenuInventoryFactory;
import org.kerix.karaapi.api.menu.MenuItem;
import org.kerix.karaapi.api.menu.MenuService;

import java.util.Map;
import java.util.Objects;

public final class PaperMenuInventoryFactory implements MenuInventoryFactory {

    @Override
    public Inventory create(MenuService menus, Menu menu, Player viewer) {
        Objects.requireNonNull(menus, "menus");
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(viewer, "viewer");

        PaperMenuHolder holder = new PaperMenuHolder(
                menus,
                menu,
                viewer.getUniqueId()
        );

        Inventory inventory = PaperInventories.chest(
                holder,
                menu.rows(),
                menu.title()
        );

        holder.inventory(inventory);

        for (Map.Entry<Integer, MenuItem> entry : menu.items().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item());
        }

        return inventory;
    }
}
