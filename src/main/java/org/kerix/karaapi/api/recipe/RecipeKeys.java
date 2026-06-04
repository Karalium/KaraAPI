package org.kerix.karaapi.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Objects;

public final class RecipeKeys {

    private RecipeKeys() {
    }

    public static NamespacedKey of(JavaPlugin plugin, String key) {
        Objects.requireNonNull(plugin, "plugin");
        return new NamespacedKey(plugin, normalize(key));
    }

    public static String normalize(String key) {
        Objects.requireNonNull(key, "key");

        String normalized = key
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Recipe key cannot be blank.");
        }

        if (!normalized.matches("[a-z0-9_./]+")) {
            throw new IllegalArgumentException("Invalid recipe key: " + key);
        }

        return normalized;
    }
}
