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
        ParsedArguments parsed = new ParsedArguments();

        for (int index = 0; index < arguments.size(); index++) {
            CommandArgument<?> argument = arguments.get(index);

            if (index >= raw.length) {
                if (argument.optional()) {
                    parsed.put(argument.name(), argument.defaultValue());
                    continue;
                }

                throw new ArgumentParseException("Missing argument: " + argument.name());
            }

            Object value = argument.type().parse(context, raw[index]);
            parsed.put(argument.name(), value);
        }

        return parsed;
    }

    public List<String> suggest(CommandContext context, int argumentIndex, String input) {
        if (argumentIndex < 0 || argumentIndex >= arguments.size()) {
            return List.of();
        }

        return arguments.get(argumentIndex).type().suggest(context, input);
    }
}
