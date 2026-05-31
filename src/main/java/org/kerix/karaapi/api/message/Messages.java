package org.kerix.karaapi.api.message;

public final class Messages {

    public static final MessageKey PREFIX =
            MessageKey.of("prefix", "<gray>[<aqua>Kara</aqua>]</gray>");

    public static final MessageKey NO_PERMISSION =
            MessageKey.of("messages.no-permission", "<red>You do not have permission.</red>");

    public static final MessageKey PLAYER_ONLY =
            MessageKey.of("messages.player-only", "<red>Only players can use this command.</red>");

    public static final MessageKey UNKNOWN_COMMAND =
            MessageKey.of("messages.unknown-command", "<red>Unknown command.</red>");

    private Messages() {
    }
}
