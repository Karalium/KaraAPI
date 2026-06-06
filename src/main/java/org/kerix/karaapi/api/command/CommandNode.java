package org.kerix.karaapi.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.kerix.karaapi.api.command.argument.ArgumentParseException;
import org.kerix.karaapi.api.command.argument.ArgumentSchema;
import org.kerix.karaapi.api.command.argument.ParsedArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class CommandNode {

    private final String name;
    private final Set<String> aliases;
    private final String usage;
    private final ArgumentSchema arguments;
    private final CommandAction action;
    private final CommandSuggestion suggestion;
    private final List<CommandRequirement> requirements;
    private final List<CommandNode> children;
    private final Map<String, CommandNode> childLookup;

    CommandNode(
            String name,
            Collection<String> aliases,
            String usage,
            ArgumentSchema arguments,
            CommandAction action,
            CommandSuggestion suggestion,
            List<CommandRequirement> requirements,
            List<CommandNode> children
    ) {
        this.name = normalizeRequired(name, "name");
        this.aliases = normalizeAliases(aliases);
        this.usage = usage;
        this.arguments = arguments == null ? ArgumentSchema.create() : arguments.copy();
        this.action = action;
        this.suggestion = suggestion;
        this.requirements = List.copyOf(requirements == null ? List.of() : requirements);
        this.children = List.copyOf(children == null ? List.of() : children);
        this.childLookup = buildChildLookup(this.children);
    }

    public String name() {
        return name;
    }

    public Set<String> aliases() {
        return aliases;
    }

    public String usage() {
        return usage;
    }

    public ArgumentSchema arguments() {
        return arguments.copy();
    }

    public List<CommandNode> children() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasAction() {
        return action != null;
    }

    public boolean matches(String input) {
        String normalized = normalize(input);
        return name.equals(normalized) || aliases.contains(normalized);
    }

    public CommandResult execute(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        Match match = find(args);

        CommandContext context = new CommandContext(
                sender,
                command,
                label,
                args,
                match.consumedArgs(),
                match.node(),
                ParsedArguments.empty()
        );

        for (CommandNode node : match.path()) {
            for (CommandRequirement requirement : node.requirements) {
                CommandResult result = requirement.check(context);

                if (result.isFailure()) {
                    return result;
                }
            }
        }

        if (match.node().action == null) {
            return CommandResult.usage(match.node().resolvedUsage(label));
        }

        try {
            ParsedArguments parsed = match.node().arguments.parse(context);
            CommandContext parsedContext = context.withParsed(parsed);

            return Objects.requireNonNullElse(
                    match.node().action.execute(parsedContext),
                    CommandResult.success()
            );
        } catch (ArgumentParseException | IllegalArgumentException exception) {
            return CommandResult.fail(exception.getMessage());
        }
    }

    public List<String> suggest(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        String[] safeArgs = args == null ? new String[0] : args;

        if (safeArgs.length == 0) {
            return childNamesStartingWith("");
        }

        CommandNode current = this;
        int depth = 0;

        while (depth < safeArgs.length - 1) {
            CommandNode child = current.child(safeArgs[depth]);

            if (child == null) {
                break;
            }

            current = child;
            depth++;
        }

        String currentInput = safeArgs[safeArgs.length - 1];

        List<String> childSuggestions = current.childNamesStartingWith(currentInput);

        if (!childSuggestions.isEmpty()) {
            return childSuggestions;
        }

        CommandContext context = new CommandContext(
                sender,
                command,
                label,
                safeArgs,
                depth,
                current,
                ParsedArguments.empty()
        );

        int argumentIndex = Math.max(0, safeArgs.length - depth - 1);
        List<String> argumentSuggestions = current.arguments.suggest(context, argumentIndex, currentInput);

        if (!argumentSuggestions.isEmpty()) {
            return filterStartingWith(argumentSuggestions, currentInput);
        }

        if (current.suggestion == null) {
            return List.of();
        }

        return filterStartingWith(current.suggestion.suggest(context, currentInput), currentInput);
    }

    private Match find(String[] args) {
        String[] safeArgs = args == null ? new String[0] : args;
        CommandNode current = this;
        List<CommandNode> path = new ArrayList<>();

        path.add(current);

        int depth = 0;

        while (depth < safeArgs.length) {
            CommandNode child = current.child(safeArgs[depth]);

            if (child == null) {
                break;
            }

            current = child;
            path.add(current);
            depth++;
        }

        return new Match(current, depth, path);
    }

    private CommandNode child(String input) {
        return childLookup.get(normalize(input));
    }

    private List<String> childNamesStartingWith(String input) {
        String normalized = normalize(input);

        return children.stream()
                .map(CommandNode::name)
                .filter(name -> name.startsWith(normalized))
                .toList();
    }

    private String resolvedUsage(String label) {
        String commandLabel = label == null || label.isBlank() ? name : label;

        if (usage != null && !usage.isBlank()) {
            return usage.replace("{label}", commandLabel);
        }

        StringBuilder builder = new StringBuilder("/")
                .append(commandLabel);

        if (!children.isEmpty()) {
            builder.append(" <")
                    .append(String.join("|", children.stream().map(CommandNode::name).toList()))
                    .append(">");
        }

        if (!arguments.empty()) {
            builder.append(" ")
                    .append(arguments.usage());
        }

        return builder.toString();
    }

    private static List<String> filterStartingWith(List<String> values, String input) {
        String lower = input == null ? "" : input.toLowerCase(Locale.ROOT);

        return values.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(lower))
                .toList();
    }

    private static Map<String, CommandNode> buildChildLookup(List<CommandNode> children) {
        Map<String, CommandNode> lookup = new HashMap<>();

        for (CommandNode child : children) {
            putChild(lookup, child.name(), child);

            for (String alias : child.aliases()) {
                putChild(lookup, alias, child);
            }
        }

        return Map.copyOf(lookup);
    }

    private static void putChild(Map<String, CommandNode> lookup, String key, CommandNode child) {
        CommandNode previous = lookup.put(normalize(key), child);

        if (previous != null && previous != child) {
            throw new IllegalStateException("Duplicate command child or alias: " + key);
        }
    }

    private static Set<String> normalizeAliases(Collection<String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return Set.of();
        }

        Set<String> result = new HashSet<>();

        for (String alias : aliases) {
            result.add(normalizeRequired(alias, "alias"));
        }

        return Set.copyOf(result);
    }

    private static String normalizeRequired(String input, String name) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command " + name + " cannot be blank.");
        }

        return normalize(input);
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
    }

    private record Match(
            CommandNode node,
            int consumedArgs,
            List<CommandNode> path
    ) {
    }
}
