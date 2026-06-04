package org.kerix.karaapi.paper.placeholder.papi;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionSpec;

import java.util.Objects;

public final class PlaceholderExpansionAdapter
        extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final PlaceholderExpansionSpec expansion;

    public PlaceholderExpansionAdapter(PlaceholderExpansionSpec expansion) {
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
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        return expansion.resolver().resolve(player, params);
    }
}
