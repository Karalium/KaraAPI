package org.kerix.karaapi.api.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.scheduler.SchedulerService;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class TaskService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final SchedulerService scheduler;
    private final Map<String, TaskGroup> groups = new LinkedHashMap<>();
    private final TaskGroup defaultGroup;

    public TaskService(JavaPlugin hostPlugin, SchedulerService scheduler) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.defaultGroup = group("default");
    }

    public TaskGroup group(String name) {
        String key = normalize(name);

        return groups.computeIfAbsent(
                key,
                ignored -> new TaskGroup(hostPlugin, key, scheduler)
        );
    }

    public TaskGroup defaultGroup() {
        return defaultGroup;
    }

    public TaskHandle sync(Runnable runnable) {
        return defaultGroup.sync(runnable);
    }

    public TaskHandle sync(String name, Runnable runnable) {
        return defaultGroup.sync(name, runnable);
    }

    public TaskHandle async(Runnable runnable) {
        return defaultGroup.async(runnable);
    }

    public TaskHandle async(String name, Runnable runnable) {
        return defaultGroup.async(name, runnable);
    }

    public TaskHandle later(long delayTicks, Runnable runnable) {
        return defaultGroup.later(delayTicks, runnable);
    }

    public TaskHandle later(String name, long delayTicks, Runnable runnable) {
        return defaultGroup.later(name, delayTicks, runnable);
    }

    public TaskHandle laterAsync(long delayTicks, Runnable runnable) {
        return defaultGroup.laterAsync(delayTicks, runnable);
    }

    public TaskHandle laterAsync(String name, long delayTicks, Runnable runnable) {
        return defaultGroup.laterAsync(name, delayTicks, runnable);
    }

    public TaskHandle timer(long delayTicks, long periodTicks, Runnable runnable) {
        return defaultGroup.timer(delayTicks, periodTicks, runnable);
    }

    public TaskHandle timer(String name, long delayTicks, long periodTicks, Runnable runnable) {
        return defaultGroup.timer(name, delayTicks, periodTicks, runnable);
    }

    public TaskHandle timerAsync(long delayTicks, long periodTicks, Runnable runnable) {
        return defaultGroup.timerAsync(delayTicks, periodTicks, runnable);
    }

    public TaskHandle timerAsync(String name, long delayTicks, long periodTicks, Runnable runnable) {
        return defaultGroup.timerAsync(name, delayTicks, periodTicks, runnable);
    }

    public Set<String> groupNames() {
        return Set.copyOf(groups.keySet());
    }

    public Set<TaskHandle> tasks() {
        for (TaskGroup group : groups.values()) {
            group.cleanup();
        }

        return groups.values()
                .stream()
                .flatMap(group -> group.tasks().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public int taskCount() {
        int count = 0;

        for (TaskGroup group : groups.values()) {
            count += group.size();
        }

        return count;
    }

    public void cancelGroup(String name) {
        TaskGroup group = groups.get(normalize(name));

        if (group != null) {
            group.cancelAll();
        }
    }

    public void cancelAll() {
        for (TaskGroup group : groups.values()) {
            group.cancelAll();
        }
    }

    @Override
    public void stop() {
        cancelAll();
        groups.clear();
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public SchedulerService scheduler() {
        return scheduler;
    }

    private static String normalize(String name) {
        if (name == null || name.isBlank()) {
            return "default";
        }

        return name.trim().toLowerCase(Locale.ROOT);
    }
}
