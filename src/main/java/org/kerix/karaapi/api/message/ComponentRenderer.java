package org.kerix.karaapi.api.message;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.kerix.karaapi.api.color.ColorGradient;
import org.kerix.karaapi.api.placeholder.PlaceholderContext;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;
import org.kerix.karaapi.paper.text.Mini;

import java.util.Objects;

public record ComponentRenderer(PlaceholderService placeholders) {

    public ComponentRenderer(PlaceholderService placeholders) {
        this.placeholders = Objects.requireNonNull(placeholders, "placeholders");
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
        return Mini.parse(plain(raw));
    }

    public Component component(OfflinePlayer player, String raw) {
        return Mini.parse(plain(player, raw));
    }

    public Component component(OfflinePlayer player, String raw, PlaceholderSet set) {
        return Mini.parse(plain(player, raw, set));
    }

    public Component component(PlaceholderContext context, String raw) {
        return Mini.parse(plain(context, raw));
    }

    /**
     * Use this when the whole rendered text should become a gradient.
     * <p>
     * This resolves placeholders first, then applies your ColorGradient API.
     */
    public Component gradient(
            String raw,
            String... colors
    ) {
        return gradient(null, raw, PlaceholderSet.empty(), colors);
    }

    public Component gradient(
            OfflinePlayer player,
            String raw,
            String... colors
    ) {
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

    /**
     * Use this when the text itself contains MiniMessage tags like:
     *
     * <green>Hello</green>
     * <gradient:#00ffaa:#ff00ff>Hello</gradient>
     * <p>
     * Placeholders are resolved first, MiniMessage tags are parsed after.
     */
    public Component mini(
            OfflinePlayer player,
            String raw,
            PlaceholderSet set
    ) {
        return component(player, raw, set);
    }
}
