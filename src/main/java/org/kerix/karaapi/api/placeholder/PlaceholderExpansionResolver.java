package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;

@FunctionalInterface
public interface PlaceholderExpansionResolver {

    String resolve(OfflinePlayer player, String params);
}
