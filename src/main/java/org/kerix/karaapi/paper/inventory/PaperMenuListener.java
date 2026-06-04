package org.kerix.karaapi.paper.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.kerix.karaapi.api.menu.Menu;
import org.kerix.karaapi.api.menu.MenuClick;
import org.kerix.karaapi.api.menu.MenuClose;
import org.kerix.karaapi.api.menu.MenuOpen;
import org.kerix.karaapi.api.menu.MenuService;

import java.util.Objects;
import java.util.Optional;

public final class PaperMenuListener implements Listener {

    private final MenuService menus;

    public PaperMenuListener(MenuService menus) {
        this.menus = Objects.requireNonNull(menus, "menus");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        PaperMenuHolder holder = holder(event.getInventory());

        if (holder == null) {
            return;
        }

        Menu menu = holder.menu();

        menu.handleOpen(new MenuOpen(
                menus,
                menu,
                event,
                player
        ));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        PaperMenuHolder holder = holder(event.getView().getTopInventory());

        if (holder == null) {
            return;
        }

        Menu menu = holder.menu();
        int topSize = event.getView().getTopInventory().getSize();
        boolean clickedTop = event.getRawSlot() >= 0 && event.getRawSlot() < topSize;

        if (!clickedTop && !menu.allowPlayerInventoryClicks()) {
            event.setCancelled(true);
            return;
        }

        if (menu.cancelClicks()) {
            event.setCancelled(true);
        }

        if (!clickedTop) {
            return;
        }

        MenuClick click = new MenuClick(
                menus,
                menu,
                event,
                player
        );

        if (menu.hasItem(event.getSlot())) {
            menu.item(event.getSlot()).click(click);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        PaperMenuHolder holder = holder(event.getInventory());

        if (holder == null) {
            return;
        }

        Menu menu = holder.menu();

        menu.handleClose(new MenuClose(
                menus,
                menu,
                event,
                player
        ));
    }

    public boolean isKaraMenu(Inventory inventory) {
        return holder(inventory) != null;
    }

    public Optional<Menu> menu(Inventory inventory) {
        PaperMenuHolder holder = holder(inventory);
        return holder == null ? Optional.empty() : Optional.of(holder.menu());
    }

    private PaperMenuHolder holder(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof PaperMenuHolder menuHolder)) {
            return null;
        }

        if (menuHolder.menus() != menus) {
            return null;
        }

        return menuHolder;
    }
}
