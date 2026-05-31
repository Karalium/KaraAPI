package org.kerix.karaapi.api.message;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.kerix.karaapi.api.config.YamlConfig;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;

import java.util.List;
import java.util.Objects;

public final class MessageBundle {

    private final YamlConfig config;
    private final ComponentRenderer renderer;

    public MessageBundle(
            YamlConfig config,
            ComponentRenderer renderer
    ) {
        this.config = Objects.requireNonNull(config, "config");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    public String raw(MessageKey key) {
        Objects.requireNonNull(key, "key");

        YamlConfiguration yaml = config.yaml();

        if (!yaml.contains(key.path())) {
            yaml.set(key.path(), key.defaultValue());
            config.save();
        }

        return yaml.getString(key.path(), key.defaultValue());
    }

    public List<String> rawList(String path) {
        return config.yaml().getStringList(path);
    }

    public String plain(MessageKey key) {
        return plain(null, key, PlaceholderSet.empty());
    }

    public String plain(MessageKey key, PlaceholderSet set) {
        return plain(null, key, set);
    }

    public String plain(OfflinePlayer player, MessageKey key) {
        return plain(player, key, PlaceholderSet.empty());
    }

    public String plain(OfflinePlayer player, MessageKey key, PlaceholderSet set) {
        String raw = raw(key);
        String prefixed = raw.replace("<prefix>", raw(Messages.PREFIX));

        return renderer.plain(player, prefixed, set);
    }

    public Component component(MessageKey key) {
        return component(null, key, PlaceholderSet.empty());
    }

    public Component component(MessageKey key, PlaceholderSet set) {
        return component(null, key, set);
    }

    public Component component(OfflinePlayer player, MessageKey key) {
        return component(player, key, PlaceholderSet.empty());
    }

    public Component component(OfflinePlayer player, MessageKey key, PlaceholderSet set) {
        return renderer.component(player, plain(player, key, set), PlaceholderSet.empty());
    }

    public Component render(OfflinePlayer player, String raw, PlaceholderSet set) {
        return renderer.component(player, raw, set);
    }

    public Component gradient(
            OfflinePlayer player,
            String raw,
            PlaceholderSet set,
            String... colors
    ) {
        return renderer.gradient(player, raw, set, colors);
    }

    public YamlConfig config() {
        return config;
    }

    public ComponentRenderer renderer() {
        return renderer;
    }
}
