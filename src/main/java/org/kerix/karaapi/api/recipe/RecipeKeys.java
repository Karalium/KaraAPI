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
        Objects.requireNonNull(key, "key");

        return new NamespacedKey(
                plugin,
                key.trim()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "_")
                        .replace("-", "_")
        );
    }
}
