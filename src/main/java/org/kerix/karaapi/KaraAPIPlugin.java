package org.kerix.karaapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.KaraAPI;
import org.kerix.karaapi.api.annotation.Adapter;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.SinceApi;

@SinceApi("2.0.0")
@Adapter("paper") @MainThread
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
