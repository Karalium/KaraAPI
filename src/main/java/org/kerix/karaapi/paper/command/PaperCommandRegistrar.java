package org.kerix.karaapi.paper.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.command.CommandNode;

import java.util.Objects;

public record PaperCommandRegistrar(JavaPlugin hostPlugin) {

    public PaperCommandRegistrar(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public void register(CommandNode node) {
        Objects.requireNonNull(node, "node");

        PaperCommandAdapter adapter = new PaperCommandAdapter(node);

        register(node.name(), adapter, adapter);
    }

    public void register(String name, CommandNode node) {
        Objects.requireNonNull(node, "node");

        PaperCommandAdapter adapter = new PaperCommandAdapter(node);

        register(name, adapter, adapter);
    }

    public void register(String name, CommandExecutor executor) {
        Objects.requireNonNull(executor, "executor");

        PluginCommand command = requireCommand(name);
        command.setExecutor(executor);
    }

    public void register(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        Objects.requireNonNull(executor, "executor");
        Objects.requireNonNull(tabCompleter, "tabCompleter");

        PluginCommand command = requireCommand(name);
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);
    }

    public PluginCommand requireCommand(String name) {
        Objects.requireNonNull(name, "name");

        PluginCommand command = hostPlugin.getCommand(name);

        if (command == null) {
            throw new IllegalStateException(
                    "Command '" + name + "' is not defined in plugin.yml for "
                            + hostPlugin.getName()
            );
        }

        return command;
    }
}
