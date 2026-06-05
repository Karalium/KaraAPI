package org.kerix.karaapi.paper.placeholder.papi;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansion;

import java.util.List;
import java.util.Objects;

public final class PlaceholderExpansionAdapter
        extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final PlaceholderExpansion expansion;

    public PlaceholderExpansionAdapter(PlaceholderExpansion expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion");
    }

    @Override
    public @NotNull String getIdentifier() {
        return expansion.identifier();
    }

    @Override
    public @NotNull String getAuthor() {
        return expansion.author();
    }

    @Override
    public @NotNull String getVersion() {
        return expansion.version();
    }

    @Override
    public boolean persist() {
        return expansion.persist();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.copyOf(expansion.placeholders().keySet());
    }

    @Override
    public @Nullable String onRequest(
            OfflinePlayer player,
            @NotNull String params
    ) {
        return expansion.resolve(player, params);
    }
}
