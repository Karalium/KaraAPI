package org.kerix.karaapi.api.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;
    private final boolean persist;
    private final PlaceholderExpansionResolver resolver;
    private final Map<String, PlaceholderExpansionResolver> placeholders;

    PlaceholderExpansion(
            String identifier,
            String author,
            String version,
            boolean persist,
            PlaceholderExpansionResolver resolver,
            Map<String, PlaceholderExpansionResolver> placeholders
    ) {
        this.identifier = normalizeIdentifier(identifier);
        this.author = author == null || author.isBlank() ? "Unknown" : author;
        this.version = version == null || version.isBlank() ? "1.0.0" : version;
        this.persist = persist;
        this.resolver = resolver;
        this.placeholders = Map.copyOf(placeholders == null ? Map.of() : placeholders);
    }

    public static PlaceholderExpansionBuilder expansion(String identifier) {
        return new PlaceholderExpansionBuilder(identifier);
    }

    public static PlaceholderExpansionBuilder expansion(JavaPlugin plugin, String identifier) {
        Objects.requireNonNull(plugin, "plugin");

        String author = plugin.getDescription().getAuthors().isEmpty()
                ? "Unknown"
                : String.join(", ", plugin.getDescription().getAuthors());

        return new PlaceholderExpansionBuilder(identifier)
                .author(author)
                .version(plugin.getDescription().getVersion())
                .persist(true);
    }

    public String identifier() {
        return identifier;
    }

    public String author() {
        return author;
    }

    public String version() {
        return version;
    }

    public boolean persist() {
        return persist;
    }

    public Map<String, PlaceholderExpansionResolver> placeholders() {
        return placeholders;
    }

    public String resolve(OfflinePlayer player, String params) {
        String normalized = normalizeParam(params);

        PlaceholderExpansionResolver direct = placeholders.get(normalized);

        if (direct != null) {
            return direct.resolve(player, normalized);
        }

        if (resolver != null) {
            return resolver.resolve(player, params);
        }

        return null;
    }

    public static String normalizeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "identifier");

        String normalized = identifier
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Placeholder expansion identifier cannot be blank.");
        }

        if (normalized.contains("%")
                || normalized.contains("{")
                || normalized.contains("}")
                || normalized.contains("_")) {
            throw new IllegalArgumentException(
                    "Placeholder expansion identifier may not contain %, {, }, or _: " + identifier
            );
        }

        return normalized;
    }

    static String normalizeParam(String params) {
        if (params == null) {
            return "";
        }

        return params.trim().toLowerCase(Locale.ROOT);
    }

    static Map<String, PlaceholderExpansionResolver> mutablePlaceholderMap() {
        return new LinkedHashMap<>();
    }
}