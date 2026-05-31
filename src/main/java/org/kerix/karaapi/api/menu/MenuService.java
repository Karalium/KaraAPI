package org.kerix.karaapi.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.paper.inventory.PaperMenuListener;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;
import org.kerix.karaapi.paper.scheduler.PaperScheduler;

import java.util.Objects;

public final class MenuService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final PaperScheduler scheduler;
    private final PaperMenuListener listener;

    public MenuService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.scheduler = new PaperScheduler(hostPlugin);
        this.listener = new PaperMenuListener(this);

        new PaperListenerRegistrar(hostPlugin).register(listener);
    }

    public void open(Player player, Menu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");

        menu.open(this, player);
    }

    public void openNextTick(Player player, Menu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");

        scheduler.later("open-menu-next-tick", 1L, () -> open(player, menu));
    }

    public void close(Player player) {
        Objects.requireNonNull(player, "player");

        player.closeInventory();
    }

    public void closeNextTick(Player player) {
        Objects.requireNonNull(player, "player");

        scheduler.later("close-menu-next-tick", 1L, player::closeInventory);
    }

    public PaperMenuListener listener() {
        return listener;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        for (Player player : hostPlugin.getServer().getOnlinePlayers()) {
            if (listener.isKaraMenu(player.getOpenInventory().getTopInventory())) {
                player.closeInventory();
            }
        }
    }
}
