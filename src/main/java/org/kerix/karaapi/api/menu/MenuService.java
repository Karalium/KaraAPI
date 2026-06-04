package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.scheduler.SchedulerService;
import org.kerix.karaapi.paper.inventory.PaperMenuListener;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

import java.util.Objects;

public final class MenuService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final SchedulerService scheduler;
    private final PaperMenuListener listener;

    private boolean stopped;

    public MenuService(JavaPlugin hostPlugin, SchedulerService scheduler) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.listener = new PaperMenuListener(this);

        new PaperListenerRegistrar(hostPlugin).register(listener);
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

    public boolean owns(Inventory inventory) {
        return listener.isKaraMenu(inventory);
    }

    public PaperMenuListener listener() {
        return listener;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public SchedulerService scheduler() {
        return scheduler;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        HandlerList.unregisterAll(listener);

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

        menu.open(this, player);
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("MenuService has already stopped.");
        }
    }
}
