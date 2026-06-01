package org.kerix.karaapi.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CommandBuilder {

    private final String name;

    private final List<String> aliases = new ArrayList<>();
    private final List<CommandRequirement> requirements = new ArrayList<>();
    private final List<CommandNode> children = new ArrayList<>();

    private String usage;
    private CommandAction action;
    private CommandSuggestion suggestion;

    private CommandBuilder(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public static CommandBuilder command(String name) {
        return new CommandBuilder(name);
    }

    public CommandBuilder alias(String alias) {
        this.aliases.add(Objects.requireNonNull(alias, "alias"));
        return this;
    }

    public CommandBuilder aliases(String... aliases) {
        if (aliases != null) {
            this.aliases.addAll(Arrays.asList(aliases));
        }

        return this;
    }

    public CommandBuilder usage(String usage) {
        this.usage = usage;
        return this;
    }

    public CommandBuilder permission(String permission) {
        return requires(Requirements.permission(permission));
    }

    public CommandBuilder playerOnly() {
        return requires(Requirements.playerOnly());
    }

    public CommandBuilder opOnly() {
        return requires(Requirements.opOnly());
    }

    public CommandBuilder requires(CommandRequirement requirement) {
        this.requirements.add(Objects.requireNonNull(requirement, "requirement"));
        return this;
    }

    public CommandBuilder executes(CommandAction action) {
        this.action = Objects.requireNonNull(action, "action");
        return this;
    }

    public CommandBuilder suggests(CommandSuggestion suggestion) {
        this.suggestion = Objects.requireNonNull(suggestion, "suggestion");
        return this;
    }

    public CommandBuilder then(CommandBuilder child) {
        return then(child.build());
    }

    public CommandBuilder then(CommandNode child) {
        this.children.add(Objects.requireNonNull(child, "child"));
        return this;
    }

    public CommandNode build() {
        return new CommandNode(
                name,
                aliases,
                usage,
                action,
                suggestion,
                requirements,
                children
        );
    }
}
