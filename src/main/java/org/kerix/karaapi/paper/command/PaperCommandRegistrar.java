package org.kerix.karaapi.paper.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.command.CommandNode;
import org.kerix.karaapi.api.startup.CommandRegistration;

import java.util.List;
import java.util.Objects;

public record PaperCommandRegistrar(JavaPlugin hostPlugin) {

    public PaperCommandRegistrar(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
    }

    public CommandRegistration register(CommandNode node) {
        Objects.requireNonNull(node, "node");

        PaperCommandAdapter adapter = new PaperCommandAdapter(node);
        return register(node.name(), adapter, adapter);
    }

    public CommandRegistration register(String name, CommandNode node) {
        Objects.requireNonNull(node, "node");

        PaperCommandAdapter adapter = new PaperCommandAdapter(node);
        return register(name, adapter, adapter);
    }

    public CommandRegistration register(String name, CommandExecutor executor) {
        Objects.requireNonNull(executor, "executor");

        PluginCommand command = requireCommand(name);
        command.setExecutor(executor);

        return registration(command);
    }

    public CommandRegistration register(
            String name,
            CommandExecutor executor,
            TabCompleter tabCompleter
    ) {
        Objects.requireNonNull(executor, "executor");
        Objects.requireNonNull(tabCompleter, "tabCompleter");

        PluginCommand command = requireCommand(name);
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);

        return registration(command);
    }

    public PluginCommand requireCommand(String name) {
        Objects.requireNonNull(name, "name");

        PluginCommand command = hostPlugin.getCommand(name);

        if (command == null) {
            throw new IllegalStateException(
                    "Command '" + name + "' is not defined in plugin.yml for " + hostPlugin.getName()
            );
        }

        return command;
    }

    private CommandRegistration registration(PluginCommand command) {
        return new CommandRegistration(command, () -> {
            command.setExecutor((sender, ignoredCommand, label, args) -> {
                sender.sendMessage(Component.text(
                        "This command is no longer available.",
                        NamedTextColor.RED
                ));
                return true;
            });

            command.setTabCompleter((sender, ignoredCommand, alias, args) -> List.of());
        });
    }
}
