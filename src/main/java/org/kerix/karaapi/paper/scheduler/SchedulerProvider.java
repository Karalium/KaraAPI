package org.kerix.karaapi.paper.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.scheduler.SchedulerService;

import java.util.Objects;

public final class SchedulerProvider {

    private SchedulerProvider() {
    }

    public static SchedulerService create(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        return new SchedulerService(new BukkitSchedulerExecutor(plugin));
    }
}
