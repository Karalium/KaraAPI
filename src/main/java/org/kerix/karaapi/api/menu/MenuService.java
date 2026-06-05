package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.scheduler.SchedulerService;
import org.kerix.karaapi.paper.inventory.PaperMenuInventoryFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final SchedulerService scheduler;
    private final MenuInventoryFactory inventoryFactory;

    private final Map<UUID, Menu> openMenus = new ConcurrentHashMap<>();

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

        openMenu(player).ifPresent(menu -> open(player, menu));
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

    public boolean owns(Player player) {
        return hasOpenMenu(player);
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

        for (Player player : hostPlugin.getServer().getOnlinePlayers()) {
            if (hasOpenMenu(player)) {
                player.closeInventory();
            }
        }

        openMenus.clear();
    }

    public void trackOpen(Player player, Menu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");

        if (!stopped) {
            openMenus.put(player.getUniqueId(), menu);
        }
    }

    public void trackClose(Player player) {
        if (player != null) {
            openMenus.remove(player.getUniqueId());
        }
    }

    public Optional<Menu> openMenu(Player player) {
        Objects.requireNonNull(player, "player");
        return Optional.ofNullable(openMenus.get(player.getUniqueId()));
    }

    public boolean hasOpenMenu(Player player) {
        Objects.requireNonNull(player, "player");
        return openMenus.containsKey(player.getUniqueId());
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