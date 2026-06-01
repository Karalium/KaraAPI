package org.kerix.karaapi.api.command;

@FunctionalInterface
public interface CommandRequirement {

    CommandResult check(CommandContext context);
}