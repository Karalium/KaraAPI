package org.kerix.api.ui;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossBarBuilder {
    private String name;
    private Plugin plugin;
    private final Map<UUID, Player> players = new HashMap<>();
    private final Set<BarFlag> flags = new HashSet<>();
    private BarColor color = BarColor.WHITE;
    private BarStyle style = BarStyle.SOLID;
    private String[] animatedNames = {};
    private int animationInterval = 0;
    private int repetition = 5;

    public BossBarBuilder(String name) {
        this.name = name;
        this.plugin = null;
    }
    public BossBarBuilder(String name, BarColor color) {
        this(name);
        this.color = color;
    }
    public BossBarBuilder(String name, BarStyle style) {
        this(name);
        this.style = style;
    }
    public BossBarBuilder(String name, BarStyle style, BarColor color) {
        this(name);
        this.style = style;
        this.color = color;
    }
    public BossBarBuilder(String name, BarFlag... flags) {
        this(name);
        this.flags.addAll(Arrays.asList(flags));
    }
    public BossBarBuilder(String name, BarColor color, BarFlag... flags) {
        this(name, flags);
        this.color = color;
    }
    public BossBarBuilder(String name, BarStyle style, BarFlag... flags) {
        this(name, flags);
        this.style = style;
    }
    public BossBarBuilder(String name, BarStyle style, BarColor color, BarFlag... flags) {
        this(name, flags);
        this.style = style;
        this.color = color;
    }
    public BossBarBuilder(String name, Plugin plugin) {
        this(name);
        this.plugin = plugin;
    }
    public BossBarBuilder(int interval, Plugin plugin, String @NotNull ... animationframes) {
        this(animationframes[0], plugin);
        this.animatedNames = animationframes;
        this.animationInterval = interval;
    }
    public BossBarBuilder(Plugin plugin, String... animationframes) {
        this(20, plugin, animationframes);
    }
    public BossBarBuilder(int interval, Plugin plugin, int times, String... animationframes) {
        this(interval, plugin, animationframes);
        this.repetition = times;
    }
    public BossBarBuilder(Plugin plugin, int times, String... animationframes) {
        this(20, plugin, times, animationframes);
    }
    public BossBarBuilder color(BarColor color) {
        this.color = color;
        return this;
    }
    public BossBarBuilder style(BarStyle style) {
        this.style = style;
        return this;
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
    private String[] animationFrames() {
        return animatedNames;
    }
    public BossBar build() {
        BossBar bossBar = Bukkit.createBossBar(name, color, style);
        players.forEach((uuid, player) -> bossBar.addPlayer(player));
        flags.forEach(bossBar::addFlag);

        if (animatedNames.length != 0) {
            animateName();
        }

        return bossBar;
    }
    private void animateName() {
        new BukkitRunnable() {
            int count = 0;
            int animationIndex = 0;
            @Override
            public void run() {
                if (count >= repetition * animatedNames.length) {
                    cancel();
                    return;
                }
                BossBar bar = Bukkit.createBossBar(name, color, style);
                players.forEach((uuid, player) -> bar.addPlayer(player));
                flags.forEach(bar::addFlag);

                bar.setTitle(animatedNames[animationIndex]);
                animationIndex = (animationIndex + 1) % animatedNames.length;
                count++;
            }
        }.runTaskTimer(plugin, 0, animationInterval);
    }
}
