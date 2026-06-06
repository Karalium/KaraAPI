package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class PlaceholderExpansionBuilder {

    private final String identifier;
    private final Map<String, PlaceholderExpansionResolver> placeholders =
            PlaceholderExpansion.mutablePlaceholderMap();

    private String author = "Unknown";
    private String version = "1.0.0";
    private boolean persist = true;
    private PlaceholderExpansionResolver resolver;

    PlaceholderExpansionBuilder(String identifier) {
        this.identifier = PlaceholderExpansion.normalizeIdentifier(identifier);
    }

    public PlaceholderExpansionBuilder author(String author) {
        this.author = author == null || author.isBlank() ? "Unknown" : author;
        return this;
    }

    public PlaceholderExpansionBuilder version(String version) {
        this.version = version == null || version.isBlank() ? "1.0.0" : version;
        return this;
    }

    public PlaceholderExpansionBuilder persist(boolean persist) {
        this.persist = persist;
        return this;
    }

    public PlaceholderExpansionBuilder resolve(PlaceholderExpansionResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver, "resolver");
        return this;
    }

    public PlaceholderExpansionBuilder placeholder(
            String name,
            PlaceholderExpansionResolver resolver
    ) {
        Objects.requireNonNull(resolver, "resolver");
        placeholders.put(
                PlaceholderExpansion.normalizeParam(name),
                resolver
        );
        return this;
    }

    public PlaceholderExpansionBuilder placeholder(
            String name,
            Function<OfflinePlayer, String> resolver
    ) {
        Objects.requireNonNull(resolver, "resolver");

        return placeholder(name, (player, params) -> resolver.apply(player));
    }

    public PlaceholderExpansionBuilder placeholder(
            String name,
            String value
    ) {
        return placeholder(name, (player, params) -> value);
    }

    public PlaceholderExpansionBuilder dynamic(
            BiFunction<OfflinePlayer, String, String> resolver
    ) {
        Objects.requireNonNull(resolver, "resolver");

        return resolve(resolver::apply);
    }

    public PlaceholderExpansion build() {
        return new PlaceholderExpansion(
                identifier,
                author,
                version,
                persist,
                resolver,
                placeholders
        );
    }
}
