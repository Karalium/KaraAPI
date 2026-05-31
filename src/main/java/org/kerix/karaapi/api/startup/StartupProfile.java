package org.kerix.karaapi.api.startup;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public record StartupProfile(
        String name,
        String version,
        List<String> authors
) {

    public static StartupProfile from(JavaPlugin plugin) {
        return new StartupProfile(
                plugin.getDescription().getName(),
                plugin.getDescription().getVersion(),
                plugin.getDescription().getAuthors()
        );
    }
}
