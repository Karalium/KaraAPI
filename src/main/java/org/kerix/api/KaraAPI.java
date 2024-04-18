package org.kerix.api;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.api.configapi.ConfigManager;
import org.kerix.api.items.head.HeadGiveCommand;
import org.kerix.api.startapi.DebugMessageAPI;
import org.kerix.api.startapi.Register;
import org.kerix.api.startapi.StatutMessageList;

public final class KaraAPI extends JavaPlugin {

    private static DebugMessageAPI debugMessageAPI;
    private static volatile KaraAPI instance;

    public KaraAPI() {
        if(instance != null)
            throw new UnsupportedOperationException("Cannot instantiate MinecraftAPI class directly.");
    }

    @Override
    public void onEnable() {
        instance = this;
        debugMessageAPI = new DebugMessageAPI();
        debugMessageAPI.StatutPlugin(StatutMessageList.ENABLE , getDescription() , getLogger());
        ConfigManager.initFiles(this);

        Register.command("gethead" , new HeadGiveCommand() , this);
    }

    @Override
    public void onDisable() {
        debugMessageAPI.StatutPlugin(StatutMessageList.DISABLE , getDescription() , getLogger());
    }

    public DebugMessageAPI getDebugMessageAPI() {
        if(debugMessageAPI == null) return debugMessageAPI = new DebugMessageAPI();
        return debugMessageAPI;
    }

    public static synchronized KaraAPI getINSTANCE() {
        if (instance == null) {
            synchronized (KaraAPI.class) {
                if(instance == null)
                    throw new IllegalStateException("KaraAPI not found");
                return instance;
            }
        }
        return instance;
    }

    public PersistentDataAdapterContext getPersistentDataAdapterContext() throws IllegalStateException {
        RegisteredServiceProvider<PersistentDataAdapterContext> provider = getServer().getServicesManager().getRegistration(PersistentDataAdapterContext.class);
        if (provider == null) {
            throw new IllegalStateException("PersistentDataAdapterContext provider is not available.");
        } else {
            provider.getProvider();
        }
        return provider.getProvider();
    }
}
