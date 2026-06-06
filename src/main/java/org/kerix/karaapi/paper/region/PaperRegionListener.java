package org.kerix.karaapi.paper.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.region.RegionService;

import java.util.Objects;


@MainThread
public final class PaperRegionListener implements Listener {

    private final RegionService regions;

    public PaperRegionListener(RegionService regions) {
        this.regions = Objects.requireNonNull(regions, "regions");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        regions.update(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (samePosition(from, to)) {
            return;
        }

        Player player = event.getPlayer();

        regions.update(player, from, to);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        regions.clear(event.getPlayer());
    }

    private static boolean samePosition(Location first, Location second) {
        if (first == null || second == null) {
            return false;
        }

        if (first.getWorld() == null || second.getWorld() == null) {
            return false;
        }

        return first.getWorld().equals(second.getWorld())
                && first.getX() == second.getX()
                && first.getY() == second.getY()
                && first.getZ() == second.getZ();
    }
}
