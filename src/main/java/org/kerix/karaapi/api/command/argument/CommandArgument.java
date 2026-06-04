package org.kerix.karaapi.api.command.argument;

import java.util.Objects;

public record CommandArgument<T>(
        String name,
        ArgumentType<T> type,
        boolean optional,
        T defaultValue,
        boolean greedy
) {

    public CommandArgument {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Argument name cannot be blank.");
        }
    }

    public static <T> CommandArgument<T> required(String name, ArgumentType<T> type) {
        return new CommandArgument<>(name, type, false, null, false);
    }

    public static <T> CommandArgument<T> optional(String name, ArgumentType<T> type, T defaultValue) {
        return new CommandArgument<>(name, type, true, defaultValue, false);
    }

    public static CommandArgument<String> greedy(String name) {
        return new CommandArgument<>(name, Arguments.string(), false, null, true);
    }

    public String usage() {
        if (greedy) {
            return "<" + name + "...>";
        }

        if (optional) {
            return "[" + name + "]";
        }

        return "<" + name + ">";
    }
}
