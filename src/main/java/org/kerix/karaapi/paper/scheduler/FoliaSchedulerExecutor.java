package org.kerix.karaapi.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;
import org.kerix.karaapi.api.scheduler.SchedulerExecutor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Folia scheduler implementation.
 *
 * <p>This executor intentionally wraps Paper's ScheduledTask directly.
 * Do not use reflection here. Reflection breaks on some Folia scheduled task
 * implementations even when the reflected method appears public.</p>
 */
public final class FoliaSchedulerExecutor implements SchedulerExecutor {

    private static final long MILLIS_PER_TICK = 50L;

    private final JavaPlugin plugin;

    public FoliaSchedulerExecutor(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public ScheduledTaskHandle sync(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getGlobalRegionScheduler()
                .run(plugin, handle.consumer(runnable, true));

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle later(long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        if (delayTicks <= 0L) {
            return sync(runnable);
        }

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getGlobalRegionScheduler()
                .runDelayed(
                        plugin,
                        handle.consumer(runnable, true),
                        delayTicks(delayTicks)
                );

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getGlobalRegionScheduler()
                .runAtFixedRate(
                        plugin,
                        handle.consumer(runnable, false),
                        delayTicks(delayTicks),
                        periodTicks(periodTicks)
                );

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle async(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getAsyncScheduler()
                .runNow(plugin, handle.consumer(runnable, true));

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle asyncLater(long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        if (delayTicks <= 0L) {
            return async(runnable);
        }

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getAsyncScheduler()
                .runDelayed(
                        plugin,
                        handle.consumer(runnable, true),
                        ticksToMillis(delayTicks),
                        TimeUnit.MILLISECONDS
                );

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle asyncTimer(long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getAsyncScheduler()
                .runAtFixedRate(
                        plugin,
                        handle.consumer(runnable, false),
                        ticksToMillis(Math.max(1L, delayTicks)),
                        ticksToMillis(periodTicks(periodTicks)),
                        TimeUnit.MILLISECONDS
                );

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle at(Location location, Runnable runnable) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(runnable, "runnable");

        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Cannot schedule a region task at a location without a world.");
        }

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = plugin.getServer()
                .getRegionScheduler()
                .run(plugin, location, handle.consumer(runnable, true));

        handle.bind(task);
        return handle;
    }

    @Override
    public ScheduledTaskHandle entity(Entity entity, Runnable runnable) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(runnable, "runnable");

        FoliaTaskHandle handle = new FoliaTaskHandle();

        ScheduledTask task = entity.getScheduler()
                .run(
                        plugin,
                        handle.consumer(runnable, true),
                        handle::markFinished
                );

        handle.bind(task);
        return handle;
    }

    @Override
    public boolean folia() {
        return true;
    }

    private long delayTicks(long ticks) {
        return Math.max(1L, ticks);
    }

    private long periodTicks(long ticks) {
        return Math.max(1L, ticks);
    }

    private long ticksToMillis(long ticks) {
        return Math.max(MILLIS_PER_TICK, ticks * MILLIS_PER_TICK);
    }

    private void runSafely(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Scheduled Folia task failed.",
                    throwable
            );

            throw throwable;
        }
    }

    private final class FoliaTaskHandle implements ScheduledTaskHandle {

        private final AtomicReference<ScheduledTask> task = new AtomicReference<>();
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private final AtomicBoolean finished = new AtomicBoolean(false);

        private void bind(ScheduledTask scheduledTask) {
            Objects.requireNonNull(scheduledTask, "scheduledTask");

            if (!task.compareAndSet(null, scheduledTask)) {
                throw new IllegalStateException("Scheduled task handle was already bound.");
            }

            if (cancelled.get()) {
                scheduledTask.cancel();
            }
        }

        private Consumer<ScheduledTask> consumer(Runnable runnable, boolean completeAfterRun) {
            Objects.requireNonNull(runnable, "runnable");

            return scheduledTask -> {
                if (cancelled()) {
                    return;
                }

                try {
                    runSafely(runnable);
                } finally {
                    if (completeAfterRun) {
                        markFinished();
                    }
                }
            };
        }

        private void markFinished() {
            finished.set(true);
        }

        @Override
        public void cancel() {
            if (!cancelled.compareAndSet(false, true)) {
                return;
            }

            ScheduledTask scheduledTask = task.get();

            if (scheduledTask == null) {
                return;
            }

            scheduledTask.cancel();
        }

        @Override
        public boolean cancelled() {
            return cancelled.get() || finished.get();
        }

        /**
         * Convenience method.
         *
         * <p>Kept without {@code @Override} so this class still compiles if
         * ScheduledTaskHandle only declares cancel() and cancelled().</p>
         */
        public boolean running() {
            return !cancelled();
        }
    }
}
