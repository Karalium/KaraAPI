package org.kerix.karaapi.api.placeholder;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Objects;

public record PlaceholderExpansionSpec(
        String identifier,
        String author,
        String version,
        boolean persist,
        PlaceholderExpansionResolver resolver
) {

    public PlaceholderExpansionSpec {
        identifier = normalizeIdentifier(identifier);
        author = author == null || author.isBlank() ? "Unknown" : author;
        version = version == null || version.isBlank() ? "1.0.0" : version;
        Objects.requireNonNull(resolver, "resolver");
    }

    public static PlaceholderExpansionSpec of(
            JavaPlugin plugin,
            String identifier,
            PlaceholderExpansionResolver resolver
    ) {
        Objects.requireNonNull(plugin, "plugin");

        String author = plugin.getDescription().getAuthors().isEmpty()
                ? "Unknown"
                : String.join(", ", plugin.getDescription().getAuthors());

        return new PlaceholderExpansionSpec(
                identifier,
                author,
                plugin.getDescription().getVersion(),
                true,
                resolver
        );
    }

    private static String normalizeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "identifier");

        String normalized = identifier
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Placeholder expansion identifier cannot be blank.");
        }

        return normalized;
    }
}
