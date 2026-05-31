package org.kerix.karaapi.paper.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NonNull;
import org.kerix.karaapi.api.command.CommandNode;
import org.kerix.karaapi.api.command.CommandResult;

import java.util.List;
import java.util.Objects;

public record PaperCommandAdapter(CommandNode root) implements CommandExecutor, TabCompleter {

    public PaperCommandAdapter(CommandNode root) {
        this.root = Objects.requireNonNull(root, "root");
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String[] args
    ) {
        CommandResult result = root.execute(sender, command, label, args);

        if (result.hasMessage()) {
            sender.sendMessage(result.message());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String alias,
            String[] args
    ) {
        return root.suggest(sender, command, alias, args);
    }
}
