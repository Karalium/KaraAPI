package org.kerix.karaapi.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

final class FoliaScheduledTaskHandle implements ScheduledTaskHandle {

    private final ScheduledTask task;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    FoliaScheduledTaskHandle(ScheduledTask task) {
        this.task = Objects.requireNonNull(task, "task");
    }

    @Override
    public void cancel() {
        if (!cancelled.compareAndSet(false, true)) {
            return;
        }

        task.cancel();
    }

    @Override
    public boolean cancelled() {
        return cancelled.get() || task.isCancelled();
    }

    @Override
    public boolean running() {
        return !cancelled();
    }
}
