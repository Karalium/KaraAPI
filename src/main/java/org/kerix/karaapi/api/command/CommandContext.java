package org.kerix.karaapi.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public final class CommandContext {

    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] fullArgs;
    private final int consumedArgs;
    private final CommandNode node;

    public CommandContext(
            CommandSender sender,
            Command command,
            String label,
            String[] fullArgs,
            int consumedArgs,
            CommandNode node
    ) {
        this.sender = Objects.requireNonNull(sender, "sender");
        this.command = Objects.requireNonNull(command, "command");
        this.label = label == null ? command.getName() : label;
        this.fullArgs = fullArgs == null ? new String[0] : Arrays.copyOf(fullArgs, fullArgs.length);
        this.consumedArgs = Math.max(0, consumedArgs);
        this.node = node;
    }

    public CommandSender sender() {
        return sender;
    }

    public Command command() {
        return command;
    }

    public String label() {
        return label;
    }

    public CommandNode node() {
        return node;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player player() {
        if (!(sender instanceof Player player)) {
            throw new IllegalStateException("Command sender is not a player.");
        }

        return player;
    }

    public String[] fullArgs() {
        return Arrays.copyOf(fullArgs, fullArgs.length);
    }

    public String[] args() {
        if (consumedArgs >= fullArgs.length) {
            return new String[0];
        }

        return Arrays.copyOfRange(fullArgs, consumedArgs, fullArgs.length);
    }

    public ArgumentReader arguments() {
        return new ArgumentReader(args());
    }

    public ArgumentReader fullArguments() {
        return new ArgumentReader(fullArgs);
    }

    public int consumedArgs() {
        return consumedArgs;
    }

    public String input() {
        return String.join(" ", fullArgs);
    }

    public String remainingInput() {
        return String.join(" ", args());
    }
}
