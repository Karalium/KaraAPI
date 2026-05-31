package org.kerix.karaapi.api.command;

public final class KaraCommands {

    private KaraCommands() {
    }

    public static CommandBuilder command(String name) {
        return CommandBuilder.command(name);
    }
}
