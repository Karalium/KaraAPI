package org.kerix.karaapi.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.command.argument.ArgumentSchema;
import org.kerix.karaapi.api.command.argument.ParsedArguments;

import java.util.Arrays;
import java.util.Objects;

public final class CommandContext {

    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] fullArgs;
    private final int consumedArgs;
    private final CommandNode node;
    private final ParsedArguments parsed;

    public CommandContext(
            CommandSender sender,
            Command command,
            String label,
            String[] fullArgs,
            int consumedArgs,
            CommandNode node
    ) {
        this(sender, command, label, fullArgs, consumedArgs, node, ParsedArguments.empty());
    }

    public CommandContext(
            CommandSender sender,
            Command command,
            String label,
            String[] fullArgs,
            int consumedArgs,
            CommandNode node,
            ParsedArguments parsed
    ) {
        this.sender = Objects.requireNonNull(sender, "sender");
        this.command = Objects.requireNonNull(command, "command");
        this.label = label == null ? command.getName() : label;
        this.fullArgs = fullArgs == null ? new String[0] : Arrays.copyOf(fullArgs, fullArgs.length);
        this.consumedArgs = Math.max(0, consumedArgs);
        this.node = node;
        this.parsed = parsed == null ? ParsedArguments.empty() : parsed;
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

    public ParsedArguments parsed() {
        return parsed;
    }

    public <T> T arg(String name) {
        return parsed.get(name);
    }

    public String string(String name) {
        return parsed.string(name);
    }

    public int integer(String name) {
        return parsed.integer(name);
    }

    public long longNumber(String name) {
        return parsed.longNumber(name);
    }

    public double decimal(String name) {
        return parsed.decimal(name);
    }

    public boolean bool(String name) {
        return parsed.bool(name);
    }

    public ParsedArguments parse(ArgumentSchema schema) {
        return schema.parse(this);
    }

    CommandContext withParsed(ParsedArguments parsed) {
        return new CommandContext(
                sender,
                command,
                label,
                fullArgs,
                consumedArgs,
                node,
                parsed
        );
    }
}
