package org.kerix.karaapi.api.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;

public final class ItemText {

    private ItemText() {
    }

    public static Component empty() {
        return Component.empty();
    }

    public static Component text(String value) {
        return Component.text(value == null ? "" : value);
    }

    public static Component text(String value, TextColor color) {
        return Component.text(value == null ? "" : value, color);
    }

    public static List<Component> lines(String... lines) {
        List<Component> components = new ArrayList<>();

        if (lines == null) {
            return components;
        }

        for (String line : lines) {
            components.add(text(line));
        }

        return components;
    }
}
