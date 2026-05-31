package org.kerix.karaapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.KaraAPI;

public final class KaraAPIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        KaraAPI.init(this);

        getLogger().info("KaraAPI runtime enabled.");
    }

    @Override
    public void onDisable() {
        KaraAPI.shutdownAll();

        getLogger().info("KaraAPI runtime disabled.");
    }
}
