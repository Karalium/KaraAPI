package org.kerix.karaapi.api.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.paper.scheduler.PaperScheduler;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class TaskGroup {

    private final JavaPlugin hostPlugin;
    private final String name;
    private final PaperScheduler scheduler;
    private final Set<TaskHandle> tasks = ConcurrentHashMap.newKeySet();

    public TaskGroup(JavaPlugin hostPlugin, String name) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.name = name == null || name.isBlank() ? "default" : name;
        this.scheduler = new PaperScheduler(hostPlugin);
    }

    public TaskHandle sync(Runnable runnable) {
        return sync("sync-task", runnable);
    }

    public TaskHandle sync(String taskName, Runnable runnable) {
        TaskHandle handle = scheduler.sync(taskName, wrapSingle(runnable));
        tasks.add(handle);
        return handle;
    }

    public TaskHandle async(Runnable runnable) {
        return async("async-task", runnable);
    }

    public TaskHandle async(String taskName, Runnable runnable) {
        TaskHandle handle = scheduler.async(taskName, wrapSingle(runnable));
        tasks.add(handle);
        return handle;
    }

    public TaskHandle later(long delayTicks, Runnable runnable) {
        return later("delayed-task", delayTicks, runnable);
    }

    public TaskHandle later(String taskName, long delayTicks, Runnable runnable) {
        TaskHandle handle = scheduler.later(taskName, delayTicks, wrapSingle(runnable));
        tasks.add(handle);
        return handle;
    }

    public TaskHandle laterAsync(long delayTicks, Runnable runnable) {
        return laterAsync("delayed-async-task", delayTicks, runnable);
    }

    public TaskHandle laterAsync(String taskName, long delayTicks, Runnable runnable) {
        TaskHandle handle = scheduler.laterAsync(taskName, delayTicks, wrapSingle(runnable));
        tasks.add(handle);
        return handle;
    }

    public TaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        return timer("timer-task", delayTicks, periodTicks, runnable);
    }

    public TaskHandle timer(String taskName, long delayTicks, long periodTicks, Runnable runnable) {
        TaskHandle handle = scheduler.timer(taskName, delayTicks, periodTicks, runnable);
        tasks.add(handle);
        return handle;
    }

    public TaskHandle timerAsync(long delayTicks, long periodTicks, Runnable runnable) {
        return timerAsync("async-timer-task", delayTicks, periodTicks, runnable);
    }

    public TaskHandle timerAsync(String taskName, long delayTicks, long periodTicks, Runnable runnable) {
        TaskHandle handle = scheduler.timerAsync(taskName, delayTicks, periodTicks, runnable);
        tasks.add(handle);
        return handle;
    }

    public TaskHandle timerControlled(
            long delayTicks,
            long periodTicks,
            Consumer<TaskHandle> runnable
    ) {
        return timerControlled("controlled-timer-task", delayTicks, periodTicks, runnable);
    }

    public TaskHandle timerControlled(
            String taskName,
            long delayTicks,
            long periodTicks,
            Consumer<TaskHandle> runnable
    ) {
        TaskHandle handle = scheduler.timerControlled(taskName, delayTicks, periodTicks, runnable);
        tasks.add(handle);
        return handle;
    }

    public void cancelAll() {
        for (TaskHandle handle : Set.copyOf(tasks)) {
            handle.cancel();
        }

        tasks.clear();
    }

    public int size() {
        cleanup();
        return tasks.size();
    }

    public boolean empty() {
        return size() == 0;
    }

    public Set<TaskHandle> tasks() {
        cleanup();
        return Set.copyOf(tasks);
    }

    public String name() {
        return name;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public void cleanup() {
        tasks.removeIf(TaskHandle::cancelled);
    }

    private Runnable wrapSingle(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        return () -> {
            try {
                runnable.run();
            } finally {
                cleanup();
            }
        };
    }
}
