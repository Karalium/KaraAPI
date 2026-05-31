package org.kerix.karaapi.api.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class MenuItem {

    private final ItemStack item;
    private final MenuAction action;

    private MenuItem(ItemStack item, MenuAction action) {
        this.item = Objects.requireNonNull(item, "item").clone();
        this.action = action;
    }

    public static MenuItem of(ItemStack item) {
        return new MenuItem(item, null);
    }

    public static MenuItem of(ItemStack item, MenuAction action) {
        return new MenuItem(item, action);
    }

    public ItemStack item() {
        return item.clone();
    }

    public boolean hasAction() {
        return action != null;
    }

    public void click(MenuClick click) {
        if (action != null) {
            action.click(click);
        }
    }
}
