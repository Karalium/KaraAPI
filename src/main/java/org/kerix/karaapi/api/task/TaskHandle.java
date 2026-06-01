package org.kerix.karaapi.api.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class TaskHandle {

    private final BukkitTask task;
    private final String name;
    private final boolean async;
    private final boolean repeating;
    private final Instant createdAt;
    private final Runnable onCancel;

    private volatile TaskState state = TaskState.RUNNING;

    public TaskHandle(
            BukkitTask task,
            String name,
            boolean async,
            boolean repeating,
            Runnable onCancel
    ) {
        this.task = Objects.requireNonNull(task, "task");
        this.name = name == null || name.isBlank() ? "unnamed-task" : name;
        this.async = async;
        this.repeating = repeating;
        this.onCancel = onCancel == null ? () -> {} : onCancel;
        this.createdAt = Instant.now();
    }

    public void cancel() {
        if (state == TaskState.CANCELLED) {
            return;
        }

        state = TaskState.CANCELLED;

        if (!task.isCancelled()) {
            task.cancel();
        }

        onCancel.run();
    }

    public boolean cancelled() {
        return state == TaskState.CANCELLED || task.isCancelled();
    }

    public boolean running() {
        return !cancelled();
    }

    public int id() {
        return task.getTaskId();
    }

    public String name() {
        return name;
    }

    public boolean async() {
        return async;
    }

    public boolean sync() {
        return !async;
    }

    public boolean repeating() {
        return repeating;
    }

    public boolean singleRun() {
        return !repeating;
    }

    public Plugin owner() {
        return task.getOwner();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Duration age() {
        return Duration.between(createdAt, Instant.now());
    }

    public TaskState state() {
        if (task.isCancelled()) {
            state = TaskState.CANCELLED;
        }

        return state;
    }

    public BukkitTask raw() {
        return task;
    }
}
