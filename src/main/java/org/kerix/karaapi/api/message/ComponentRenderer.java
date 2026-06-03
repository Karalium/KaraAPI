package org.kerix.karaapi.api.message;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.kerix.karaapi.api.color.ColorGradient;
import org.kerix.karaapi.api.placeholder.PlaceholderContext;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;
import org.kerix.karaapi.paper.text.Mini;

import java.util.Objects;
import java.util.function.Supplier;

public final class ComponentRenderer {

    private final PlaceholderService placeholders;
    private final Supplier<String> prefixSupplier;

    public ComponentRenderer(
            PlaceholderService placeholders,
            Supplier<String> prefixSupplier
    ) {
        this.placeholders = Objects.requireNonNull(placeholders, "placeholders");
        this.prefixSupplier = Objects.requireNonNull(prefixSupplier, "prefixSupplier");
    }

    public String plain(String raw) {
        return plain(null, raw, PlaceholderSet.empty());
    }

    public String plain(OfflinePlayer player, String raw) {
        return plain(player, raw, PlaceholderSet.empty());
    }

    public String plain(OfflinePlayer player, String raw, PlaceholderSet set) {
        PlaceholderSet merged = defaults(set);

        return placeholders.apply(player, raw, merged);
    }

    public String plain(PlaceholderContext context, String raw) {
        PlaceholderSet merged = defaults(context.placeholders());

        return placeholders.apply(
                PlaceholderContext.of(context.player(), merged),
                raw
        );
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

    public PlaceholderService placeholders() {
        return placeholders;
    }

    private PlaceholderSet defaults(PlaceholderSet set) {
        PlaceholderSet merged = PlaceholderSet.empty()
                .add("prefix", prefixSupplier.get());

        if (set != null) {
            merged.addAll(set);
        }

        return merged;
    }
}
