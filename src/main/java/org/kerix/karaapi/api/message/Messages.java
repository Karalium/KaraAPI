package org.kerix.karaapi.api.message;

public enum Messages {

    PREFIX("prefix", "<gray>[<aqua>Kara</aqua>]</gray>"),

    NO_PERMISSION("messages.no-permission", "<red>You do not have permission.</red>"),

    PLAYER_ONLY("messages.player-only", "<red>Only players can use this command.</red>"),

    UNKNOWN_COMMAND("messages.unknown-command", "<red>Unknown command.</red>");

    private final MessageKey key;

    Messages(String path, String defaultValue) {
        this.key = MessageKey.of(path, defaultValue);
    }

    public MessageKey key() {
        return key;
    }
}
