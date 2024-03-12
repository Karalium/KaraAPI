package org.kerix.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.api.configapi.ConfigManager;
import org.kerix.api.console.startapi.DebugMessageAPI;
import org.kerix.api.console.startapi.StatutMessageList;

public final class MinecraftAPI extends JavaPlugin {

    private static DebugMessageAPI debugMessageAPI;

    @Override
    public void onEnable() {
        debugMessageAPI = new DebugMessageAPI();
        debugMessageAPI.StatutPlugin(StatutMessageList.ENABLE , getDescription() , getLogger());
        ConfigManager.initFiles(this);
    }

    @Override
    public void onDisable() {
        debugMessageAPI.StatutPlugin(StatutMessageList.DISABLE , getDescription() , getLogger());
    }

    public DebugMessageAPI getDebugMessageAPI() {
        return debugMessageAPI;
    }

}
