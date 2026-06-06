package org.kerix.karaapi.api.startup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.kerix.karaapi.api.command.CommandNode;

public interface CommandGateway {

    CommandRegistration register(CommandNode node);

    CommandRegistration register(String pluginYmlName, CommandNode node);

    CommandRegistration register(String name, CommandExecutor executor);

    CommandRegistration register(
            String name,
            CommandExecutor executor,
            TabCompleter tabCompleter
    );
}