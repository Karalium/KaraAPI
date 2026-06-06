package org.kerix.karaapi.api.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.ItemProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MenuBuilder {

    private final Component title;
    private final int rows;
    private final Map<Integer, MenuItem> items = new LinkedHashMap<>();

    private boolean cancelClicks = true;
    private boolean allowPlayerInventoryClicks = false;
    private MenuOpenAction openAction;
    private MenuCloseAction closeAction;

    public MenuBuilder(Component title, int rows) {
        this.title = Objects.requireNonNull(title, "title");
        this.rows = validateRows(rows);
    }

    public MenuBuilder slot(int slot, ItemStack item) {
        return slot(slot, MenuItem.of(item));
    }

    public MenuBuilder slot(int slot, ItemProvider item) {
        return slot(slot, MenuItem.of(item.build()));
    }

    public MenuBuilder slot(int slot, ItemStack item, MenuAction action) {
        return slot(slot, MenuItem.of(item, action));
    }

    public MenuBuilder slot(int slot, ItemProvider item, MenuAction action) {
        return slot(slot, MenuItem.of(item.build(), action));
    }

    public MenuBuilder slot(int slot, MenuItem item) {
        validateSlot(slot);
        items.put(slot, Objects.requireNonNull(item, "item"));
        return this;
    }

    public MenuBuilder remove(int slot) {
        validateSlot(slot);
        items.remove(slot);
        return this;
    }

    public MenuBuilder fill(ItemStack item) {
        return fill(ItemProvider.of(item));
    }

    public MenuBuilder fill(ItemProvider item) {
        for (int slot = 0; slot < size(); slot++) {
            slot(slot, item);
        }

        return this;
    }

    public MenuBuilder fillEmpty(ItemStack item) {
        return fillEmpty(ItemProvider.of(item));
    }

    public MenuBuilder fillEmpty(ItemProvider item) {
        for (int slot = 0; slot < size(); slot++) {
            if (!items.containsKey(slot)) {
                slot(slot, item);
            }
        }

        return this;
    }

    public MenuBuilder border(ItemStack item) {
        return border(ItemProvider.of(item));
    }

    public MenuBuilder border(ItemProvider item) {
        for (int slot = 0; slot < size(); slot++) {
            int row = slot / 9;
            int column = slot % 9;
            boolean border = row == 0 || row == rows - 1 || column == 0 || column == 8;

            if (border) {
                slot(slot, item);
            }
        }

        return this;
    }

    public MenuBuilder cancelClicks(boolean cancelClicks) {
        this.cancelClicks = cancelClicks;
        return this;
    }

    public MenuBuilder allowPlayerInventoryClicks(boolean allowPlayerInventoryClicks) {
        this.allowPlayerInventoryClicks = allowPlayerInventoryClicks;
        return this;
    }

    public MenuBuilder onOpen(MenuOpenAction openAction) {
        this.openAction = openAction;
        return this;
    }

    public MenuBuilder onClose(MenuCloseAction closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    public Menu build() {
        return new Menu(
                title,
                rows,
                items,
                cancelClicks,
                allowPlayerInventoryClicks,
                openAction,
                closeAction
        );
    }

    private int size() {
        return rows * 9;
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= size()) {
            throw new IllegalArgumentException(
                    "Slot " + slot + " is outside menu size " + size() + "."
            );
        }
    }

    private static int validateRows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Menu rows must be between 1 and 6.");
        }

        return rows;
    }
}
