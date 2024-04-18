package org.kerix.api.ui;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class BossBarBuilder {
    private final String name;
    private Plugin plugin;
    private final Map<UUID, Player> players = new HashMap<>();
    private final Set<BarFlag> flags = new HashSet<>();
    private BarColor color = BarColor.WHITE;
    private BarStyle style = BarStyle.SOLID;
    private String[] animatedNames = {};
    private int animationInterval = 0;
    private int repetition = 5;
    private boolean isAnimated = false;
    private static final Map<BossBar, BossBarBuilder> builderMap = new HashMap<>();

    public BossBarBuilder(String name, Plugin plugin) { this.name = name; this.plugin = plugin; }
    public BossBarBuilder(String name) { this(name, (Plugin) null); }
    public BossBarBuilder(String name, BarColor color) { this(name); this.color = color; }
    public BossBarBuilder(String name, BarStyle style) { this(name); this.style = style; }
    public BossBarBuilder(String name, BarStyle style, BarColor color) { this(name, color); this.style = style; }
    public BossBarBuilder(String name, BarFlag... flags) { this(name); Collections.addAll(this.flags, flags); }
    public BossBarBuilder(String name, BarColor color, BarFlag... flags) { this(name, flags); this.color = color; }
    public BossBarBuilder(String name, BarStyle style, BarFlag... flags) { this(name, flags); this.style = style; }
    public BossBarBuilder(String name, BarStyle style, BarColor color, BarFlag... flags) { this(name, color, flags); this.style = style; }
    public BossBarBuilder(String name, Plugin plugin, String @NotNull ... animationframes) { this(name, plugin); this.animatedNames = animationframes; }
    public BossBarBuilder(Plugin plugin, String... animationframes) { this("", plugin, animationframes); }
    public BossBarBuilder(String name, Plugin plugin, int interval, int times, String... animationframes) { this(name, plugin, animationframes); this.animationInterval = interval; this.repetition = times; }
    public BossBarBuilder(Plugin plugin, int times, String... animationframes) { this("", plugin, 20, times, animationframes); }

    public BossBarBuilder color(BarColor color) {
        this.color = color;
        return this;
    }

    public BossBarBuilder style(BarStyle style) {
        this.style = style;
        return this;
    }

    public static BossBarBuilder get(BossBar bossBar) {
        return builderMap.get(bossBar);
    }

    public BossBarBuilder addPlayer(Player player) {
        players.put(player.getUniqueId(), player);
        return this;
    }

    public BossBarBuilder removePlayer(@NotNull Player player) {
        players.remove(player.getUniqueId());
        return this;
    }

    public BossBarBuilder addFlag(BarFlag flag) {
        flags.add(flag);
        return this;
    }

    public BossBarBuilder removeFlag(BarFlag flag) {
        flags.remove(flag);
        return this;
    }

    public BossBarBuilder addAnimation(int interval, String... animationFrames) {
        animatedNames = animationFrames;
        animationInterval = interval;
        isAnimated = true;
        return this;
    }

    private void animateName(BossBar bossBar) {
        new BukkitRunnable() {
            int count = 0;
            int animationIndex = 0;

            @Override
            public void run() {
                if (count >= repetition * animatedNames.length) {
                    cancel();
                    return;
                }
                bossBar.setTitle(animatedNames[animationIndex]);
                animationIndex = (animationIndex + 1) % animatedNames.length;
                count++;
            }
        }.runTaskTimer(plugin, 0, animationInterval);
    }
    public BossBar build() {
        BossBar bossBar = isAnimated ? Bukkit.createBossBar(animatedNames[0], color, style) : Bukkit.createBossBar(name, color, style);
        players.values().forEach(bossBar::addPlayer);
        flags.forEach(bossBar::addFlag);
        if (isAnimated) animateName(bossBar);
        return bossBar;
    }
}
