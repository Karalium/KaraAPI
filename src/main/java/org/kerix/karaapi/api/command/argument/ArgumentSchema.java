package org.kerix.karaapi.api.command.argument;

import org.kerix.karaapi.api.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ArgumentSchema {

    private final List<CommandArgument<?>> arguments = new ArrayList<>();

    public static ArgumentSchema create() {
        return new ArgumentSchema();
    }

    public <T> ArgumentSchema required(String name, ArgumentType<T> type) {
        ensureCanAddNormalArgument();
        arguments.add(CommandArgument.required(name, type));
        return this;
    }

    public <T> ArgumentSchema optional(String name, ArgumentType<T> type, T defaultValue) {
        ensureCanAddNormalArgument();
        arguments.add(CommandArgument.optional(name, type, defaultValue));
        return this;
    }

    public ArgumentSchema greedy(String name) {
        if (!arguments.isEmpty() && arguments.get(arguments.size() - 1).greedy()) {
            throw new IllegalStateException("Only one greedy argument is allowed.");
        }

        arguments.add(CommandArgument.greedy(name));
        return this;
    }

    public ParsedArguments parse(CommandContext context) {
        Objects.requireNonNull(context, "context");

        String[] raw = context.args();
        ParsedArguments parsed = new ParsedArguments();

        int rawIndex = 0;

        for (CommandArgument<?> argument : arguments) {
            if (rawIndex >= raw.length) {
                if (argument.optional()) {
                    parsed.put(argument.name(), argument.defaultValue());
                    continue;
                }

                throw new ArgumentParseException("Missing argument: " + argument.name());
            }

            String input;

            if (argument.greedy()) {
                input = String.join(" ", List.of(raw).subList(rawIndex, raw.length));
                rawIndex = raw.length;
            } else {
                input = raw[rawIndex];
                rawIndex++;
            }

            Object value = argument.type().parse(context, input);
            parsed.put(argument.name(), value);
        }

        if (rawIndex < raw.length) {
            throw new ArgumentParseException("Too many arguments.");
        }

        return parsed;
    }

    public List<String> suggest(CommandContext context, int argumentIndex, String input) {
        if (argumentIndex < 0 || argumentIndex >= arguments.size()) {
            return List.of();
        }

        return arguments.get(argumentIndex).type().suggest(context, input);
    }

    public boolean empty() {
        return arguments.isEmpty();
    }

    public int size() {
        return arguments.size();
    }

    public List<CommandArgument<?>> arguments() {
        return List.copyOf(arguments);
    }

    public String usage() {
        return String.join(" ", arguments.stream()
                .map(CommandArgument::usage)
                .toList());
    }

    public ArgumentSchema copy() {
        ArgumentSchema copy = new ArgumentSchema();
        copy.arguments.addAll(arguments);
        return copy;
    }

    private void ensureCanAddNormalArgument() {
        if (!arguments.isEmpty() && arguments.get(arguments.size() - 1).greedy()) {
            throw new IllegalStateException("Cannot add arguments after a greedy argument.");
        }
    }
}
