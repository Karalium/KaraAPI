package org.kerix.karaapi.api.command;

public final class Commands {

    private Commands() {
    }

    public static CommandBuilder command(String name) {
        return CommandBuilder.command(name);
    }
}
