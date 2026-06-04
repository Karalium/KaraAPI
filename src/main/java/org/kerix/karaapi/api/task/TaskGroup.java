package org.kerix.karaapi.api.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;
import org.kerix.karaapi.api.scheduler.SchedulerService;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class TaskGroup {

    private final JavaPlugin hostPlugin;
    private final String name;
    private final SchedulerService scheduler;
    private final Set<TaskHandle> tasks = ConcurrentHashMap.newKeySet();

    public TaskGroup(JavaPlugin hostPlugin, String name, SchedulerService scheduler) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.name = name == null || name.isBlank() ? "default" : name;
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    public TaskHandle sync(Runnable runnable) {
        return sync("sync-task", runnable);
    }

    public TaskHandle sync(String taskName, Runnable runnable) {
        return single(taskName, false, action -> scheduler.sync(action), runnable);
    }

    public TaskHandle async(Runnable runnable) {
        return async("async-task", runnable);
    }

    public TaskHandle async(String taskName, Runnable runnable) {
        return single(taskName, true, action -> scheduler.async(action), runnable);
    }

    public TaskHandle later(long delayTicks, Runnable runnable) {
        return later("delayed-task", delayTicks, runnable);
    }

    public TaskHandle later(String taskName, long delayTicks, Runnable runnable) {
        return single(taskName, false, action -> scheduler.later(delayTicks, action), runnable);
    }

    public TaskHandle laterAsync(long delayTicks, Runnable runnable) {
        return laterAsync("delayed-async-task", delayTicks, runnable);
    }

    public TaskHandle laterAsync(String taskName, long delayTicks, Runnable runnable) {
        return single(taskName, true, action -> scheduler.asyncLater(delayTicks, action), runnable);
    }

    public TaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        return timer("timer-task", delayTicks, periodTicks, runnable);
    }

    public TaskHandle timer(String taskName, long delayTicks, long periodTicks, Runnable runnable) {
        ScheduledTaskHandle scheduled = scheduler.timer(delayTicks, periodTicks, runnable);
        return track(scheduled, taskName, false, true);
    }

    public TaskHandle timerAsync(long delayTicks, long periodTicks, Runnable runnable) {
        return timerAsync("async-timer-task", delayTicks, periodTicks, runnable);
    }

    public TaskHandle timerAsync(String taskName, long delayTicks, long periodTicks, Runnable runnable) {
        ScheduledTaskHandle scheduled = scheduler.asyncTimer(delayTicks, periodTicks, runnable);
        return track(scheduled, taskName, true, true);
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
        Objects.requireNonNull(runnable, "runnable");

        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        ScheduledTaskHandle scheduled = scheduler.timer(delayTicks, periodTicks, () -> {
            TaskHandle handle = ref.get();

            if (handle != null && handle.running()) {
                runnable.accept(handle);
            }
        });

        TaskHandle handle = track(scheduled, taskName, false, true);
        ref.set(handle);

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
        tasks.removeIf(task -> !task.running());
    }

    private TaskHandle single(
            String taskName,
            boolean async,
            SchedulerFunction schedulerFunction,
            Runnable runnable
    ) {
        Objects.requireNonNull(runnable, "runnable");

        AtomicReference<TaskHandle> ref = new AtomicReference<>();

        ScheduledTaskHandle scheduled = schedulerFunction.schedule(() -> {
            try {
                runnable.run();
            } finally {
                TaskHandle handle = ref.get();

                if (handle != null) {
                    handle.complete();
                }

                cleanup();
            }
        });

        TaskHandle handle = track(scheduled, taskName, async, false);
        ref.set(handle);

        return handle;
    }

    private TaskHandle track(
            ScheduledTaskHandle scheduled,
            String taskName,
            boolean async,
            boolean repeating
    ) {
        TaskHandle handle = new TaskHandle(scheduled, taskName, async, repeating);
        tasks.add(handle);
        cleanup();
        return handle;
    }

    @FunctionalInterface
    private interface SchedulerFunction {
        ScheduledTaskHandle schedule(Runnable runnable);
    }
}
