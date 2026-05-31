package org.kerix.karaapi.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kerix.karaapi.api.task.TaskHandle;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public record PaperScheduler(JavaPlugin hostPlugin) {

    public PaperScheduler(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public TaskHandle sync(String name, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTask(hostPlugin, () -> {
            try {
                runnable.run();
            } finally {
                TaskHandle handle = ref.get();

                if (handle != null) {
                    handle.cancel();
                }
            }
        });

        TaskHandle handle = handle(task, name, false, false);
        ref.set(handle);

        return handle;
    }

    public TaskHandle async(String name, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(hostPlugin, () -> {
            try {
                runnable.run();
            } finally {
                TaskHandle handle = ref.get();

                if (handle != null) {
                    handle.cancel();
                }
            }
        });

        TaskHandle handle = handle(task, name, true, false);
        ref.set(handle);

        return handle;
    }

    public TaskHandle later(String name, long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        long delay = Math.max(0L, delayTicks);
        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskLater(hostPlugin, () -> {
            try {
                runnable.run();
            } finally {
                TaskHandle handle = ref.get();

                if (handle != null) {
                    handle.cancel();
                }
            }
        }, delay);

        TaskHandle handle = handle(task, name, false, false);
        ref.set(handle);

        return handle;
    }

    public TaskHandle laterAsync(String name, long delayTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        long delay = Math.max(0L, delayTicks);
        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(hostPlugin, () -> {
            try {
                runnable.run();
            } finally {
                TaskHandle handle = ref.get();

                if (handle != null) {
                    handle.cancel();
                }
            }
        }, delay);

        TaskHandle handle = handle(task, name, true, false);
        ref.set(handle);

        return handle;
    }

    public TaskHandle timer(String name, long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        long delay = Math.max(0L, delayTicks);
        long period = Math.max(1L, periodTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                hostPlugin,
                runnable,
                delay,
                period
        );

        return handle(task, name, false, true);
    }

    public TaskHandle timerAsync(String name, long delayTicks, long periodTicks, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        long delay = Math.max(0L, delayTicks);
        long period = Math.max(1L, periodTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                hostPlugin,
                runnable,
                delay,
                period
        );

        return handle(task, name, true, true);
    }

    public TaskHandle timerControlled(
            String name,
            long delayTicks,
            long periodTicks,
            Consumer<TaskHandle> runnable
    ) {
        Objects.requireNonNull(runnable, "runnable");

        long delay = Math.max(0L, delayTicks);
        long period = Math.max(1L, periodTicks);

        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                hostPlugin,
                () -> {
                    TaskHandle handle = ref.get();

                    if (handle != null && handle.running()) {
                        runnable.accept(handle);
                    }
                },
                delay,
                period
        );

        TaskHandle handle = handle(task, name, false, true);
        ref.set(handle);

        return handle;
    }

    private TaskHandle handle(
            BukkitTask task,
            String name,
            boolean async,
            boolean repeating
    ) {
        return new TaskHandle(
                task,
                name,
                async,
                repeating,
                () -> {
                }
        );
    }
}
