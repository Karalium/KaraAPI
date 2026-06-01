package org.kerix.karaapi.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kerix.karaapi.api.scheduler.KaraScheduler;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;

import java.util.Objects;

public final class BukkitKaraScheduler implements KaraScheduler {

    private final JavaPlugin plugin;

    public BukkitKaraScheduler(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public ScheduledTaskHandle sync(Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public ScheduledTaskHandle later(long delayTicks, Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, runnable, Math.max(0L, delayTicks)));
    }

    @Override
    public ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTaskTimer(
                plugin,
                runnable,
                Math.max(0L, delayTicks),
                Math.max(1L, periodTicks)
        ));
    }

    @Override
    public ScheduledTaskHandle async(Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public ScheduledTaskHandle at(Location location, Runnable runnable) {
        return sync(runnable);
    }

    @Override
    public ScheduledTaskHandle entity(Entity entity, Runnable runnable) {
        return sync(runnable);
    }

    @Override
    public boolean folia() {
        return false;
    }

    private ScheduledTaskHandle wrap(BukkitTask task) {
        return new ScheduledTaskHandle() {
            @Override
            public void cancel() {
                task.cancel();
            }

            @Override
            public boolean cancelled() {
                return task.isCancelled();
            }
        };
    }
}
