package org.kerix.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.api.configapi.ConfigManager;
import org.kerix.api.startapi.DebugMessageAPI;
import org.kerix.api.startapi.StatutMessageList;
import org.kerix.api.ui.BossBarBuilder;

public final class MinecraftAPI extends JavaPlugin {

    private static DebugMessageAPI debugMessageAPI;
    private static MinecraftAPI instance;
    @Override
    public void onEnable() {
        instance = this;
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

    public static synchronized MinecraftAPI getINSTANCE() {
        if (instance == null) {
            throw new IllegalStateException("Instance of MinecraftAPI cannot be created directly.");
        }
        return instance;
    }
}
