package org.kerix.api.startapi;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.api.KaraAPI;

public class Register {


    private static final KaraAPI minecraftAPI;

    static{
        minecraftAPI =  KaraAPI.getINSTANCE();
    }

    /**
     * Registers event listeners.
     *
     * @param listeners List of listeners to register.
     */
    public static void events(Listener... listeners) {
        for (Listener listener : listeners) {
            minecraftAPI.getServer().getPluginManager().registerEvents(listener, minecraftAPI);
        }
    }

    /**
     * Registers a command.
     *
     * @param commandName Name of the command.
     * @param executor    Command executor.
     */
    public static void command(String commandName, CommandExecutor executor , JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            plugin.getLogger().warning("Failed to register command '" + commandName + "'. Command not found.");
        }
    }
}
