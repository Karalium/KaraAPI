package org.kerix.karaapi.paper.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Mini {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private Mini() {
    }

    public static Component parse(String input) {
        return MINI_MESSAGE.deserialize(input == null ? "" : input);
    }

    public static String serialize(Component component) {
        return MINI_MESSAGE.serialize(component == null ? Component.empty() : component);
    }

    public static MiniMessage instance() {
        return MINI_MESSAGE;
    }
}
