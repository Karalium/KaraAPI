package org.kerix.karaapi.api.ui;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.kerix.karaapi.paper.text.PaperText;

public final class Ui {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private Ui() {
    }

    public static Component text(String text) {
        return Component.text(text == null ? "" : text);
    }

    public static Component mini(String input) {
        return MINI_MESSAGE.deserialize(input == null ? "" : input);
    }

    public static ChatMessage chat(String text) {
        return ChatMessage.text(text);
    }

    public static ChatMessage chat(Component component) {
        return ChatMessage.component(component);
    }

    public static ChatMessage miniChat(String input) {
        return ChatMessage.component(mini(input));
    }

    public static void send(Audience audience, Component message) {
        PaperText.message(audience, message);
    }

    public static void actionBar(Audience audience, Component message) {
        PaperText.actionBar(audience, message);
    }

    public static void title(Audience audience, TitleMessage title) {
        title.send(audience);
    }
}
