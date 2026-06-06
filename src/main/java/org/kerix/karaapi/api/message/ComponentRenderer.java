package org.kerix.karaapi.api.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.kerix.karaapi.api.color.ColorGradient;
import org.kerix.karaapi.api.placeholder.PlaceholderContext;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;

import java.util.Objects;

public record ComponentRenderer(PlaceholderService placeholders, MiniMessage miniMessage) {

    public ComponentRenderer(PlaceholderService placeholders) {
        this(placeholders, MiniMessage.miniMessage());
    }

    public ComponentRenderer {
        Objects.requireNonNull(placeholders, "placeholders");
        Objects.requireNonNull(miniMessage, "miniMessage");
    }

    public String plain(String raw) {
        return plain(null, raw, PlaceholderSet.empty());
    }

    public String plain(OfflinePlayer player, String raw) {
        return plain(player, raw, PlaceholderSet.empty());
    }

    public String plain(OfflinePlayer player, String raw, PlaceholderSet set) {
        return placeholders.apply(player, raw, set);
    }

    public String plain(PlaceholderContext context, String raw) {
        return placeholders.apply(context, raw);
    }

    public Component component(String raw) {
        return miniMessage.deserialize(plain(raw));
    }

    public Component component(OfflinePlayer player, String raw) {
        return component(player, raw, PlaceholderSet.empty());
    }

    public Component component(OfflinePlayer player, String raw, PlaceholderSet set) {
        return miniMessage.deserialize(plain(player, raw, set));
    }

    public Component component(PlaceholderContext context, String raw) {
        return miniMessage.deserialize(plain(context, raw));
    }

    public String serialize(Component component) {
        return miniMessage.serialize(component == null ? Component.empty() : component);
    }

    public Component gradient(String raw, String... colors) {
        return gradient(null, raw, PlaceholderSet.empty(), colors);
    }

    public Component gradient(OfflinePlayer player, String raw, String... colors) {
        return gradient(player, raw, PlaceholderSet.empty(), colors);
    }

    public Component gradient(
            OfflinePlayer player,
            String raw,
            PlaceholderSet set,
            String... colors
    ) {
        String resolved = plain(player, raw, set);
        return ColorGradient.of(colors).component(resolved);
    }
}
