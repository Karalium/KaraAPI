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
        arguments.add(CommandArgument.required(name, type));
        return this;
    }

    public <T> ArgumentSchema optional(String name, ArgumentType<T> type, T defaultValue) {
        arguments.add(CommandArgument.optional(name, type, defaultValue));
        return this;
    }

    public ParsedArguments parse(CommandContext context) {
        Objects.requireNonNull(context, "context");

        String[] raw = context.args();

        validate(raw);

        ParsedArguments parsed = new ParsedArguments();

        for (int index = 0; index < arguments.size(); index++) {
            CommandArgument<?> argument = arguments.get(index);

            if (index >= raw.length) {
                parsed.put(argument.name(), argument.defaultValue());
                continue;
            }

            Object value = argument.type().parse(context, raw[index]);
            parsed.put(argument.name(), value);
        }

        return parsed;
    }

    public void validate(String[] raw) {
        int provided = raw == null ? 0 : raw.length;
        int minimum = requiredCount();
        int maximum = arguments.size();

        if (provided < minimum) {
            throw new ArgumentParseException(
                    "Missing arguments. Expected: " + usage()
            );
        }

        if (provided > maximum) {
            throw new ArgumentParseException(
                    "Too many arguments. Expected: " + usage()
            );
        }
    }

    public boolean accepts(int providedArgumentCount) {
        return providedArgumentCount >= requiredCount()
                && providedArgumentCount <= arguments.size();
    }

    public int requiredCount() {
        int count = 0;

        for (CommandArgument<?> argument : arguments) {
            if (!argument.optional()) {
                count++;
            }
        }

        return count;
    }

    public int maxCount() {
        return arguments.size();
    }

    public String usage() {
        if (arguments.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (CommandArgument<?> argument : arguments) {
            if (!builder.isEmpty()) {
                builder.append(" ");
            }

            if (argument.optional()) {
                builder.append("[").append(argument.name()).append("]");
            } else {
                builder.append("<").append(argument.name()).append(">");
            }
        }

        return builder.toString();
    }

    public List<String> suggest(CommandContext context, int argumentIndex, String input) {
        if (argumentIndex < 0 || argumentIndex >= arguments.size()) {
            return List.of();
        }

        return arguments.get(argumentIndex).type().suggest(context, input);
    }

    public List<CommandArgument<?>> arguments() {
        return List.copyOf(arguments);
    }
}
