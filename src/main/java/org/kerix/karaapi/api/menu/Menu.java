package org.kerix.karaapi.api.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.paper.inventory.PaperInventories;
import org.kerix.karaapi.paper.inventory.PaperMenuHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Menu {

    private final Component title;
    private final int rows;
    private final Map<Integer, MenuItem> items;
    private final boolean cancelClicks;
    private final boolean allowPlayerInventoryClicks;
    private final MenuOpenAction openAction;
    private final MenuCloseAction closeAction;

    Menu(
            Component title,
            int rows,
            Map<Integer, MenuItem> items,
            boolean cancelClicks,
            boolean allowPlayerInventoryClicks,
            MenuOpenAction openAction,
            MenuCloseAction closeAction
    ) {
        this.title = Objects.requireNonNull(title, "title");
        this.rows = validateRows(rows);
        this.items = Map.copyOf(items == null ? Map.of() : items);
        this.cancelClicks = cancelClicks;
        this.allowPlayerInventoryClicks = allowPlayerInventoryClicks;
        this.openAction = openAction;
        this.closeAction = closeAction;
    }

    public static MenuBuilder builder(Component title, int rows) {
        return new MenuBuilder(title, rows);
    }

    public static MenuBuilder builder(String title, int rows) {
        return new MenuBuilder(Component.text(title == null ? "" : title), rows);
    }

    public Inventory createInventory(MenuService menus, Player viewer) {
        PaperMenuHolder holder = new PaperMenuHolder(menus, this, viewer.getUniqueId());
        Inventory inventory = PaperInventories.chest(holder, rows, title);

        holder.inventory(inventory);

        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item());
        }

        return inventory;
    }

    public void open(MenuService menus, Player player) {
        Inventory inventory = createInventory(menus, player);
        player.openInventory(inventory);
    }

    public Component title() {
        return title;
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return rows * 9;
    }

    public Map<Integer, MenuItem> items() {
        return Collections.unmodifiableMap(items);
    }

    public MenuItem item(int slot) {
        return items.get(slot);
    }

    public boolean hasItem(int slot) {
        return items.containsKey(slot);
    }

    public boolean cancelClicks() {
        return cancelClicks;
    }

    public boolean allowPlayerInventoryClicks() {
        return allowPlayerInventoryClicks;
    }

    public void handleOpen(MenuOpen open) {
        if (openAction != null) {
            openAction.open(open);
        }
    }

    public void handleClose(MenuClose close) {
        if (closeAction != null) {
            closeAction.close(close);
        }
    }

    public MenuBuilder toBuilder() {
        MenuBuilder builder = new MenuBuilder(title, rows)
                .cancelClicks(cancelClicks)
                .allowPlayerInventoryClicks(allowPlayerInventoryClicks);

        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            builder.slot(entry.getKey(), entry.getValue());
        }

        if (openAction != null) {
            builder.onOpen(openAction);
        }

        if (closeAction != null) {
            builder.onClose(closeAction);
        }

        return builder;
    }

    private static int validateRows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Menu rows must be between 1 and 6.");
        }

        return rows;
    }
}
