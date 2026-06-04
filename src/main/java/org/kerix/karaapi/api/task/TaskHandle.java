package org.kerix.karaapi.api.task;

import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class TaskHandle implements ScheduledTaskHandle {

    private final ScheduledTaskHandle scheduled;
    private final String name;
    private final boolean async;
    private final boolean repeating;
    private final Instant createdAt = Instant.now();

    private volatile TaskState state = TaskState.RUNNING;

    TaskHandle(
            ScheduledTaskHandle scheduled,
            String name,
            boolean async,
            boolean repeating
    ) {
        this.scheduled = Objects.requireNonNull(scheduled, "scheduled");
        this.name = name == null || name.isBlank() ? "unnamed-task" : name;
        this.async = async;
        this.repeating = repeating;
    }

    @Override
    public void cancel() {
        if (state != TaskState.RUNNING) {
            return;
        }

        state = TaskState.CANCELLED;
        scheduled.cancel();
    }

    void complete() {
        if (state == TaskState.RUNNING) {
            state = TaskState.COMPLETED;
        }
    }

    @Override
    public boolean cancelled() {
        return state == TaskState.CANCELLED || scheduled.cancelled();
    }

    @Override
    public boolean running() {
        return state == TaskState.RUNNING && !scheduled.cancelled();
    }

    public boolean completed() {
        return state == TaskState.COMPLETED;
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

    public Instant createdAt() {
        return createdAt;
    }

    public Duration age() {
        return Duration.between(createdAt, Instant.now());
    }

    public TaskState state() {
        if (scheduled.cancelled() && state == TaskState.RUNNING) {
            state = TaskState.CANCELLED;
        }

        return state;
    }
}
