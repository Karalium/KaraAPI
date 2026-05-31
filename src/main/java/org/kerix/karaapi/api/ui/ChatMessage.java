package org.kerix.karaapi.api.ui;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

public final class ChatMessage {

    private Component component;

    private ChatMessage(Component component) {
        this.component = Objects.requireNonNull(component, "component");
    }

    public static ChatMessage empty() {
        return new ChatMessage(Component.empty());
    }

    public static ChatMessage text(String text) {
        return new ChatMessage(Component.text(text == null ? "" : text));
    }

    public static ChatMessage component(Component component) {
        return new ChatMessage(component);
    }

    public ChatMessage append(String text) {
        this.component = component.append(Component.text(text == null ? "" : text));
        return this;
    }

    public ChatMessage append(Component other) {
        this.component = component.append(other);
        return this;
    }

    public ChatMessage color(TextColor color) {
        this.component = component.color(color);
        return this;
    }

    public ChatMessage color(String hex) {
        TextColor color = TextColor.fromHexString(hex);

        if (color == null) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }

        return color(color);
    }

    public ChatMessage decorate(TextDecoration... decorations) {
        this.component = component.decorate(decorations);
        return this;
    }

    public ChatMessage command(String command) {
        this.component = component.clickEvent(ClickEvent.runCommand(command));
        return this;
    }

    public ChatMessage suggest(String command) {
        this.component = component.clickEvent(ClickEvent.suggestCommand(command));
        return this;
    }

    public ChatMessage url(String url) {
        this.component = component.clickEvent(ClickEvent.openUrl(url));
        return this;
    }

    public ChatMessage copy(String text) {
        this.component = component.clickEvent(ClickEvent.copyToClipboard(text));
        return this;
    }

    public ChatMessage hover(String text) {
        return hover(Component.text(text == null ? "" : text));
    }

    public ChatMessage hover(Component hoverText) {
        this.component = component.hoverEvent(HoverEvent.showText(hoverText));
        return this;
    }

    public Component build() {
        return component;
    }

    public void send(Audience audience) {
        audience.sendMessage(component);
    }
}
