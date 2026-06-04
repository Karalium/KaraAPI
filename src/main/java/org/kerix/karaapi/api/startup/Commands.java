package org.kerix.karaapi.api.startup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public interface Commands extends CommandExecutor, TabCompleter {

    @Override
    default List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        return Collections.emptyList();
    }
}
