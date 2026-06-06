package org.kerix.karaapi.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;
import org.kerix.karaapi.api.scheduler.SchedulerExecutor;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class FoliaSchedulerExecutor implements SchedulerExecutor {

    private final JavaPlugin plugin;

    FoliaSchedulerExecutor(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    static boolean available() {
        try {
            Bukkit.class.getMethod("getGlobalRegionScheduler");
            Bukkit.class.getMethod("getRegionScheduler");
            Bukkit.class.getMethod("getAsyncScheduler");
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }

    @Override
    public ScheduledTaskHandle sync(Runnable runnable) {
        Object scheduler = callStaticBukkit("getGlobalRegionScheduler");

        return invokeTask(
                scheduler,
                "run",
                new Class<?>[]{Plugin.class, Consumer.class},
                new Object[]{plugin, consumer(runnable)}
        );
    }

    @Override
    public ScheduledTaskHandle later(long delayTicks, Runnable runnable) {
        Object scheduler = callStaticBukkit("getGlobalRegionScheduler");

        return invokeTask(
                scheduler,
                "runDelayed",
                new Class<?>[]{Plugin.class, Consumer.class, long.class},
                new Object[]{plugin, consumer(runnable), Math.max(1L, delayTicks)}
        );
    }

    @Override
    public ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        Object scheduler = callStaticBukkit("getGlobalRegionScheduler");

        return invokeTask(
                scheduler,
                "runAtFixedRate",
                new Class<?>[]{Plugin.class, Consumer.class, long.class, long.class},
                new Object[]{
                        plugin,
                        consumer(runnable),
                        Math.max(1L, delayTicks),
                        Math.max(1L, periodTicks)
                }
        );
    }

    @Override
    public ScheduledTaskHandle async(Runnable runnable) {
        Object scheduler = callStaticBukkit("getAsyncScheduler");

        return invokeTask(
                scheduler,
                "runNow",
                new Class<?>[]{Plugin.class, Consumer.class},
                new Object[]{plugin, consumer(runnable)}
        );
    }

    @Override
    public ScheduledTaskHandle asyncLater(long delayTicks, Runnable runnable) {
        Object scheduler = callStaticBukkit("getAsyncScheduler");

        return invokeTask(
                scheduler,
                "runDelayed",
                new Class<?>[]{Plugin.class, Consumer.class, long.class, TimeUnit.class},
                new Object[]{
                        plugin,
                        consumer(runnable),
                        Math.max(1L, delayTicks) * 50L,
                        TimeUnit.MILLISECONDS
                }
        );
    }

    @Override
    public ScheduledTaskHandle asyncTimer(long delayTicks, long periodTicks, Runnable runnable) {
        Object scheduler = callStaticBukkit("getAsyncScheduler");

        return invokeTask(
                scheduler,
                "runAtFixedRate",
                new Class<?>[]{Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class},
                new Object[]{
                        plugin,
                        consumer(runnable),
                        Math.max(1L, delayTicks) * 50L,
                        Math.max(1L, periodTicks) * 50L,
                        TimeUnit.MILLISECONDS
                }
        );
    }

    @Override
    public ScheduledTaskHandle at(Location location, Runnable runnable) {
        Objects.requireNonNull(location, "location");

        Object scheduler = callStaticBukkit("getRegionScheduler");

        return invokeTask(
                scheduler,
                "run",
                new Class<?>[]{Plugin.class, Location.class, Consumer.class},
                new Object[]{plugin, location, consumer(runnable)}
        );
    }

    @Override
    public ScheduledTaskHandle entity(Entity entity, Runnable runnable) {
        Objects.requireNonNull(entity, "entity");

        try {
            Object scheduler = entity.getClass()
                    .getMethod("getScheduler")
                    .invoke(entity);

            return invokeTask(
                    scheduler,
                    "run",
                    new Class<?>[]{Plugin.class, Consumer.class, Runnable.class},
                    new Object[]{plugin, consumer(runnable), (Runnable) () -> {}}
            );
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not schedule entity task on Folia.", exception);
        }
    }

    @Override
    public boolean folia() {
        return true;
    }

    private Object callStaticBukkit(String methodName) {
        try {
            Method method = Bukkit.class.getMethod(methodName);
            return method.invoke(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not access Folia scheduler: " + methodName, exception);
        }
    }

    private Consumer<Object> consumer(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return ignored -> runnable.run();
    }

    private ScheduledTaskHandle invokeTask(
            Object scheduler,
            String methodName,
            Class<?>[] parameterTypes,
            Object[] arguments
    ) {
        try {
            Method method = scheduler.getClass().getMethod(methodName, parameterTypes);
            Object task = method.invoke(scheduler, arguments);

            return wrap(task);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not invoke Folia scheduler method: " + methodName, exception);
        }
    }

    private ScheduledTaskHandle wrap(Object task) {
        return getScheduledTaskHandle(task);
    }

    @NonNull
    static ScheduledTaskHandle getScheduledTaskHandle(Object task) {
        return new ScheduledTaskHandle() {
            private boolean cancelled;

            @Override
            public void cancel() {
                if (task == null || cancelled) {
                    return;
                }

                try {
                    task.getClass().getMethod("cancel").invoke(task);
                    cancelled = true;
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException("Could not cancel scheduled task.", exception);
                }
            }

            @Override
            public boolean cancelled() {
                return cancelled;
            }
        };
    }
}
