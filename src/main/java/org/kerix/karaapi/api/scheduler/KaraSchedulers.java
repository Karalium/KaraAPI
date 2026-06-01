package org.kerix.karaapi.api.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.paper.scheduler.BukkitKaraScheduler;
import org.kerix.karaapi.paper.scheduler.FoliaKaraScheduler;

public final class KaraSchedulers {

    private KaraSchedulers() {
    }

    public static KaraScheduler create(JavaPlugin plugin) {
        if (FoliaKaraScheduler.available()) {
            return new FoliaKaraScheduler(plugin);
        }

        return new BukkitKaraScheduler(plugin);
    }
}