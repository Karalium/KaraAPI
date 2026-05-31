package org.kerix.karaapi.paper.text;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.Objects;

public final class PaperText {

    private PaperText() {
    }

    public static void message(Audience audience, Component message) {
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(message, "message");

        audience.sendMessage(message);
    }

    public static void actionBar(Audience audience, Component message) {
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(message, "message");

        audience.sendActionBar(message);
    }

    public static void title(Audience audience, Title title) {
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(title, "title");

        audience.showTitle(title);
    }

    public static void clearTitle(Audience audience) {
        Objects.requireNonNull(audience, "audience");

        audience.clearTitle();
    }

    public static void showBossBar(Audience audience, BossBar bossBar) {
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(bossBar, "bossBar");

        audience.showBossBar(bossBar);
    }

    public static void hideBossBar(Audience audience, BossBar bossBar) {
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(bossBar, "bossBar");

        audience.hideBossBar(bossBar);
    }
}
