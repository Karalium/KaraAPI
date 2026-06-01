package org.kerix.karaapi.api.item;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Objects;

public final class ItemKeys {

    private ItemKeys() {
    }

    public static NamespacedKey of(JavaPlugin plugin, String key) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(key, "key");

        return new NamespacedKey(plugin, normalize(key));
    }

    private static String normalize(String key) {
        return key
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(' ', '_');
    }
}
