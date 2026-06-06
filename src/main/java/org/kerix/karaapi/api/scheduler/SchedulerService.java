package org.kerix.karaapi.api.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ManagedService(
        value = SchedulerService.class,
        registerAnnotatedTicks = false
)
@MainThread
public final class SchedulerService implements SchedulerExecutor, Stoppable {

    private final SchedulerExecutor executor;
    private final Set<ScheduledTaskHandle> tasks = ConcurrentHashMap.newKeySet();

    public SchedulerService(SchedulerExecutor executor) {
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    @Override
    public ScheduledTaskHandle sync(Runnable runnable) {
        return track(executor.sync(runnable));
    }

    @Override
    public ScheduledTaskHandle later(long delayTicks, Runnable runnable) {
        return track(executor.later(delayTicks, runnable));
    }

    @Override
    public ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        return track(executor.timer(delayTicks, periodTicks, runnable));
    }

    @Override
    public ScheduledTaskHandle async(Runnable runnable) {
        return track(executor.async(runnable));
    }

    @Override
    public ScheduledTaskHandle asyncLater(long delayTicks, Runnable runnable) {
        return track(executor.asyncLater(delayTicks, runnable));
    }

    @Override
    public ScheduledTaskHandle asyncTimer(long delayTicks, long periodTicks, Runnable runnable) {
        return track(executor.asyncTimer(delayTicks, periodTicks, runnable));
    }

    @Override
    public ScheduledTaskHandle at(Location location, Runnable runnable) {
        return track(executor.at(location, runnable));
    }

    @Override
    public ScheduledTaskHandle entity(Entity entity, Runnable runnable) {
        return track(executor.entity(entity, runnable));
    }

    @Override
    public boolean folia() {
        return executor.folia();
    }

    public int taskCount() {
        cleanup();
        return tasks.size();
    }

    public Set<ScheduledTaskHandle> tasks() {
        cleanup();
        return Set.copyOf(tasks);
    }

    public void cancelAll() {
        for (ScheduledTaskHandle task : Set.copyOf(tasks)) {
            task.cancel();
        }

        tasks.clear();
    }

    @Override
    public void stop() {
        cancelAll();
    }

    private ScheduledTaskHandle track(ScheduledTaskHandle task) {
        Objects.requireNonNull(task, "task");

        tasks.add(task);
        cleanup();

        return task;
    }

    private void cleanup() {
        tasks.removeIf(ScheduledTaskHandle::cancelled);
    }
}
