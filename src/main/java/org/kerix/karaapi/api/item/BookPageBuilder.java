package org.kerix.karaapi.api.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class BookPageBuilder {

    private Component page = Component.empty();

    public BookPageBuilder append(String text) {
        page = page.append(Component.text(text == null ? "" : text));
        return this;
    }

    public BookPageBuilder append(Component component) {
        page = page.append(component);
        return this;
    }

    public BookPageBuilder newline() {
        page = page.append(Component.text("\n"));
        return this;
    }

    public BookPageBuilder command(String text, String command) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.runCommand(command))
        );
    }

    public BookPageBuilder command(String text, String command, Component hover) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.runCommand(command))
                        .hoverEvent(HoverEvent.showText(hover))
        );
    }

    public BookPageBuilder suggest(String text, String command) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.suggestCommand(command))
        );
    }

    public BookPageBuilder url(String text, String url) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.openUrl(url))
        );
    }

    public BookPageBuilder url(String text, String url, Component hover) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.openUrl(url))
                        .hoverEvent(HoverEvent.showText(hover))
        );
    }

    public BookPageBuilder copy(String text, String copiedText) {
        return append(
                Component.text(text == null ? "" : text)
                        .clickEvent(ClickEvent.copyToClipboard(copiedText))
        );
    }

    public BookPageBuilder colored(String text, TextColor color) {
        return append(Component.text(text == null ? "" : text, color));
    }

    public BookPageBuilder colored(String text, String hex) {
        TextColor color = TextColor.fromHexString(hex);

        if (color == null) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }

        return colored(text, color);
    }

    public BookPageBuilder decorated(String text, TextDecoration... decorations) {
        return append(Component.text(text == null ? "" : text).decorate(decorations));
    }

    public Component build() {
        return page;
    }
}
