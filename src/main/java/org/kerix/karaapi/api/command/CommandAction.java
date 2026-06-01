package org.kerix.karaapi.api.command;

@FunctionalInterface
public interface CommandAction {

    CommandResult execute(CommandContext context);
}
