package org.kerix.karaapi.api.ui;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.annotation.DependsOn;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.message.MessageService;
import org.kerix.karaapi.api.placeholder.PlaceholderSet;

import java.time.Duration;
import java.util.Objects;

@ManagedService(
        value = UiService.class,
        priority = 45,
        registerAnnotatedTicks = false
)
@DependsOn(MessageService.class)
@MainThread
public final class UiService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final SidebarManager sidebars;

    private final MessageService messages;

    public UiService(JavaPlugin hostPlugin, SidebarRendererFactory sidebarRendererFactory , MessageService messages) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.sidebars = new SidebarManager(sidebarRendererFactory);
        this.messages = messages;
    }

    public void chat(Audience audience, Component message) {
        audience.sendMessage(message);
    }

    public void chat(Audience audience, String raw) {
        requireMessages();

        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendMessage(messages.component(player, raw, PlaceholderSet.empty()));
    }

    public void chat(Audience audience, String raw, PlaceholderSet placeholders) {
        requireMessages();

        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendMessage(messages.component(player, raw, placeholders));
    }

    public void actionBar(Audience audience, Component message) {
        audience.sendActionBar(message);
    }

    public void actionBar(Audience audience, String raw, PlaceholderSet placeholders) {
        requireMessages();

        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        audience.sendActionBar(messages.component(player, raw, placeholders));
    }

    public void gradientActionBar(
            Player player,
            String raw,
            PlaceholderSet placeholders,
            String... colors
    ) {
        requireMessages();

        player.sendActionBar(messages.gradient(player, raw, placeholders, colors));
    }

    public void title(
            Audience audience,
            Component title,
            Component subtitle
    ) {
        TitleMessage.of(title, subtitle).send(audience);
    }

    public void title(
            Audience audience,
            String title,
            String subtitle,
            PlaceholderSet placeholders
    ) {
        requireMessages();

        OfflinePlayer player = audience instanceof Player bukkitPlayer
                ? bukkitPlayer
                : null;

        TitleMessage.of(
                messages.component(player, title, placeholders),
                messages.component(player, subtitle, placeholders)
        ).send(audience);
    }

    public void title(
            Audience audience,
            Component title,
            Component subtitle,
            Duration fadeIn,
            Duration stay,
            Duration fadeOut
    ) {
        TitleMessage.of(title, subtitle, fadeIn, stay, fadeOut).send(audience);
    }

    public BossBarHandle bossBar(
            Component title,
            float progress,
            BossBar.Color color,
            BossBar.Overlay overlay
    ) {
        return BossBarHandle.create(title, progress, color, overlay);
    }

    public BossBarHandle bossBar(
            Player player,
            String raw,
            PlaceholderSet placeholders,
            float progress,
            BossBar.Color color,
            BossBar.Overlay overlay
    ) {
        requireMessages();

        return BossBarHandle.create(
                messages.component(player, raw, placeholders),
                progress,
                color,
                overlay
        );
    }

    public Sidebar sidebar(Player player, Component title) {
        return sidebars.getOrCreate(player, title);
    }

    public Sidebar sidebar(Player player, String title, PlaceholderSet placeholders) {
        requireMessages();

        return sidebars.getOrCreate(
                player,
                messages.component(player, title, placeholders)
        );
    }

    public void clearSidebar(Player player) {
        sidebars.remove(player);
    }

    public SidebarManager sidebars() {
        return sidebars;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        sidebars.clearAll();
    }

    private void requireMessages() {
        if (messages == null) {
            throw new IllegalStateException(
                    "UiService has no MessageService attached. Did PluginKernel call ui.messages(messages)?"
            );
        }
    }
}
