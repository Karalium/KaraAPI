package org.kerix.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.kerix.api.configapi.ConfigManager;
import org.kerix.api.startapi.DebugMessageAPI;
import org.kerix.api.startapi.StatutMessageList;
import org.kerix.api.ui.ActionBar;
import org.kerix.api.ui.ScoreBoardBuilder;

public final class MinecraftAPI extends JavaPlugin {

    private static DebugMessageAPI debugMessageAPI;
    private static MinecraftAPI instance;

    @Override
    public void onEnable() {
        instance = this;
        debugMessageAPI = new DebugMessageAPI();
        debugMessageAPI.StatutPlugin(StatutMessageList.ENABLE , getDescription() , getLogger());
        ConfigManager.initFiles(this);

        for (Player p : Bukkit.getOnlinePlayers()){
            Scoreboard sb = new ScoreBoardBuilder(p.getName())
                    .setLines("BigMan" , "BigMan", "BigMan", "BigMan", "BigMan", "BigMan", "BigMan", "BigMan", "BigMan", "BigMan")
                    .setLine(16, "caca")
                    .build();
            p.setScoreboard(sb);
            ActionBar.send(p , 20 , "&aTest" , "&bTest" , "&cTest" , "&dTest" , "&eTest" , "&1Test");

        }
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
