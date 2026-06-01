package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class MenuClick {

    private final MenuService menus;
    private final Menu menu;
    private final InventoryClickEvent event;
    private final Player player;
    private final int slot;
    private final int rawSlot;
    private final ClickType clickType;
    private final InventoryAction action;
    private final ItemStack currentItem;

    public MenuClick(
            MenuService menus,
            Menu menu,
            InventoryClickEvent event,
            Player player
    ) {
        this.menus = Objects.requireNonNull(menus, "menus");
        this.menu = Objects.requireNonNull(menu, "menu");
        this.event = Objects.requireNonNull(event, "event");
        this.player = Objects.requireNonNull(player, "player");

        this.slot = event.getSlot();
        this.rawSlot = event.getRawSlot();
        this.clickType = event.getClick();
        this.action = event.getAction();
        this.currentItem = event.getCurrentItem() == null
                ? null
                : event.getCurrentItem().clone();
    }

    public MenuService menus() {
        return menus;
    }

    public Menu menu() {
        return menu;
    }

    public InventoryClickEvent event() {
        return event;
    }

    public Player player() {
        return player;
    }

    public int slot() {
        return slot;
    }

    public int rawSlot() {
        return rawSlot;
    }

    public ClickType clickType() {
        return clickType;
    }

    public InventoryAction action() {
        return action;
    }

    public ItemStack currentItem() {
        return currentItem == null ? null : currentItem.clone();
    }

    public boolean shiftClick() {
        return clickType.isShiftClick();
    }

    public boolean leftClick() {
        return clickType.isLeftClick();
    }

    public boolean rightClick() {
        return clickType.isRightClick();
    }

    public void cancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public boolean cancelled() {
        return event.isCancelled();
    }

    public void closeNextTick() {
        menus.closeNextTick(player);
    }

    public void openNextTick(Menu menu) {
        menus.openNextTick(player, menu);
    }

    public void refreshNextTick() {
        menus.openNextTick(player, menu);
    }
}
