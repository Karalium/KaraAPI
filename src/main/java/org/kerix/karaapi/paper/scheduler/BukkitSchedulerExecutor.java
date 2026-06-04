package org.kerix.karaapi.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;
import org.kerix.karaapi.api.scheduler.SchedulerExecutor;

import java.util.Objects;

final class BukkitSchedulerExecutor implements SchedulerExecutor {

    private final JavaPlugin plugin;

    BukkitSchedulerExecutor(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public ScheduledTaskHandle sync(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public ScheduledTaskHandle later(long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTaskLater(
                plugin,
                runnable,
                Math.max(0L, delayTicks)
        ));
    }

    @Override
    public ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTaskTimer(
                plugin,
                runnable,
                Math.max(0L, delayTicks),
                Math.max(1L, periodTicks)
        ));
    }

    @Override
    public ScheduledTaskHandle async(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public ScheduledTaskHandle asyncLater(long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(
                plugin,
                runnable,
                Math.max(0L, delayTicks)
        ));
    }

    @Override
    public ScheduledTaskHandle asyncTimer(long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return wrap(Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                runnable,
                Math.max(0L, delayTicks),
                Math.max(1L, periodTicks)
        ));
    }

    @Override
    public ScheduledTaskHandle at(Location location, Runnable runnable) {
        Objects.requireNonNull(location, "location");
        return sync(runnable);
    }

    @Override
    public ScheduledTaskHandle entity(Entity entity, Runnable runnable) {
        Objects.requireNonNull(entity, "entity");
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
                if (!task.isCancelled()) {
                    task.cancel();
                }
            }

            @Override
            public boolean cancelled() {
                return task.isCancelled();
            }
        };
    }
}
