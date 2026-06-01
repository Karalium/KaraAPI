package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;

public record PlaceholderContext(
        OfflinePlayer player,
        PlaceholderSet placeholders
) {

    public static PlaceholderContext empty() {
        return new PlaceholderContext(null, PlaceholderSet.empty());
    }

    public static PlaceholderContext of(OfflinePlayer player) {
        return new PlaceholderContext(player, PlaceholderSet.empty());
    }

    public static PlaceholderContext of(OfflinePlayer player, PlaceholderSet placeholders) {
        return new PlaceholderContext(
                player,
                placeholders == null ? PlaceholderSet.empty() : placeholders
        );
    }

    public boolean hasPlayer() {
        return player != null;
    }
}
