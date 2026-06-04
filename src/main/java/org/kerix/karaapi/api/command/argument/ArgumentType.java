package org.kerix.karaapi.api.command.argument;

import org.kerix.karaapi.api.command.CommandContext;

import java.util.List;

import org.kerix.karaapi.api.command.CommandContext;

import java.util.List;

public interface ArgumentType<T> {

    T parse(CommandContext context, String input);

    default List<String> suggest(CommandContext context, String input) {
        return List.of();
    }
}
