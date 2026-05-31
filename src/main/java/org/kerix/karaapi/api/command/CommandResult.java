package org.kerix.karaapi.api.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class CommandResult {

    private static final CommandResult SUCCESS = new CommandResult(true, null);
    private static final CommandResult FAILURE = new CommandResult(false, null);

    private final boolean success;
    private final Component message;

    private CommandResult(boolean success, Component message) {
        this.success = success;
        this.message = message;
    }

    public static CommandResult success() {
        return SUCCESS;
    }

    public static CommandResult failure() {
        return FAILURE;
    }

    public static CommandResult message(Component message) {
        return new CommandResult(true, message);
    }

    public static CommandResult message(String message) {
        return message(Component.text(message));
    }

    public static CommandResult fail(Component message) {
        return new CommandResult(false, message);
    }

    public static CommandResult fail(String message) {
        return fail(Component.text(message, NamedTextColor.RED));
    }

    public static CommandResult usage(String usage) {
        return fail(Component.text("Usage: " + usage, NamedTextColor.RED));
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public Component message() {
        return message;
    }
}
