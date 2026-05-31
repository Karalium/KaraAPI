package org.kerix.karaapi.api.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.config.ConfigService;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MessageService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final ConfigService configs;
    private final PlaceholderService placeholders;
    private final ComponentRenderer renderer;

    private final Map<String, MessageBundle> bundles = new LinkedHashMap<>();

    public MessageService(
            JavaPlugin hostPlugin,
            ConfigService configs,
            PlaceholderService placeholders
    ) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.configs = Objects.requireNonNull(configs, "configs");
        this.placeholders = Objects.requireNonNull(placeholders, "placeholders");
        this.renderer = new ComponentRenderer(placeholders);
    }

    public MessageBundle main() {
        return bundle("messages.yml");
    }

    public MessageBundle bundle(String fileName) {
        return bundles.computeIfAbsent(
                normalize(fileName),
                ignored -> new MessageBundle(configs.config(fileName), renderer)
        );
    }

    public String plain(String raw) {
        return renderer.plain(raw);
    }

    public String plain(OfflinePlayer player, String raw, PlaceholderSet set) {
        return renderer.plain(player, raw, set);
    }

    public Component component(String raw) {
        return renderer.component(raw);
    }

    public Component component(OfflinePlayer player, String raw) {
        return renderer.component(player, raw);
    }

    public Component component(OfflinePlayer player, String raw, PlaceholderSet set) {
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

    public Component component(MessageKey key) {
        return main().component(key);
    }

    public Component component(OfflinePlayer player, MessageKey key, PlaceholderSet set) {
        return main().component(player, key, set);
    }

    public void send(Audience audience, MessageKey key) {
        audience.sendMessage(component(key));
    }

    public void send(Audience audience, String raw) {
        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendMessage(component(player, raw, PlaceholderSet.empty()));
    }

    public void send(Audience audience, String raw, PlaceholderSet set) {
        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendMessage(component(player, raw, set));
    }

    public void send(Audience audience, MessageKey key, PlaceholderSet set) {
        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendMessage(main().component(player, key, set));
    }

    public void send(CommandSender sender, MessageKey key) {
        sender.sendMessage(component(key));
    }

    public void send(Player player, MessageKey key, PlaceholderSet set) {
        player.sendMessage(main().component(player, key, set));
    }

    public void reloadAll() {
        for (MessageBundle bundle : bundles.values()) {
            bundle.config().reload();
        }
    }

    public ComponentRenderer renderer() {
        return renderer;
    }

    public PlaceholderService placeholders() {
        return placeholders;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        bundles.clear();
    }

    private static String normalize(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "messages.yml";
        }

        String normalized = fileName.trim().replace("\\", "/");

        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) {
            normalized += ".yml";
        }

        return normalized;
    }
}
