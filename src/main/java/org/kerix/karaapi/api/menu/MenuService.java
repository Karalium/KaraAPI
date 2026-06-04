package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.scheduler.SchedulerService;
import org.kerix.karaapi.api.startup.ListenerRegistration;
import org.kerix.karaapi.paper.inventory.PaperMenuInventoryFactory;
import org.kerix.karaapi.paper.inventory.PaperMenuListener;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

import java.util.Objects;

public final class MenuService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final SchedulerService scheduler;
    private final MenuInventoryFactory inventoryFactory;
    private final PaperMenuListener listener;
    private final ListenerRegistration listenerRegistration;

    private boolean stopped;

    public MenuService(JavaPlugin hostPlugin, SchedulerService scheduler) {
        this(hostPlugin, scheduler, new PaperMenuInventoryFactory());
    }

    public MenuService(
            JavaPlugin hostPlugin,
            SchedulerService scheduler,
            MenuInventoryFactory inventoryFactory
    ) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.inventoryFactory = Objects.requireNonNull(inventoryFactory, "inventoryFactory");
        this.listener = new PaperMenuListener(this);
        this.listenerRegistration = new PaperListenerRegistrar(hostPlugin).register(listener);
    }

    public void open(Player player, Menu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");

        ensureRunning();

        scheduler.entity(player, () -> openNow(player, menu));
    }

    public void openNextTick(Player player, Menu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");

        ensureRunning();

        scheduler.later(1L, () -> scheduler.entity(player, () -> openNow(player, menu)));
    }

    public void refresh(Player player) {
        Objects.requireNonNull(player, "player");

        ensureRunning();

        Inventory topInventory = player.getOpenInventory().getTopInventory();

        listener.menu(topInventory).ifPresent(menu -> open(player, menu));
    }

    public void refreshNextTick(Player player) {
        Objects.requireNonNull(player, "player");

        ensureRunning();

        scheduler.later(1L, () -> refresh(player));
    }

    public void close(Player player) {
        Objects.requireNonNull(player, "player");

        ensureRunning();

        scheduler.entity(player, player::closeInventory);
    }

    public void closeNextTick(Player player) {
        Objects.requireNonNull(player, "player");

        ensureRunning();

        scheduler.later(1L, () -> scheduler.entity(player, player::closeInventory));
    }

    public Inventory createInventory(Menu menu, Player viewer) {
        ensureRunning();
        return inventoryFactory.create(this, menu, viewer);
    }

    public boolean owns(Inventory inventory) {
        return listener.isKaraMenu(inventory);
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public SchedulerService scheduler() {
        return scheduler;
    }

    public MenuInventoryFactory inventoryFactory() {
        return inventoryFactory;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        listenerRegistration.unregister();

        for (Player player : hostPlugin.getServer().getOnlinePlayers()) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();

            if (listener.isKaraMenu(topInventory)) {
                player.closeInventory();
            }
        }
    }

    private void openNow(Player player, Menu menu) {
        if (stopped) {
            return;
        }

        Inventory inventory = createInventory(menu, player);
        player.openInventory(inventory);
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("MenuService has already stopped.");
        }
    }
}