package org.kerix.karaapi.api.ui;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

public final class BossBarHandle {

    private final BossBar bossBar;
    private final Set<Audience> viewers =
            Collections.newSetFromMap(new IdentityHashMap<>());

    private BossBarHandle(BossBar bossBar) {
        this.bossBar = Objects.requireNonNull(bossBar, "bossBar");
    }

    public static BossBarHandle create(
            Component title,
            float progress,
            BossBar.Color color,
            BossBar.Overlay overlay
    ) {
        return new BossBarHandle(
                BossBar.bossBar(
                        title,
                        clamp(progress),
                        color,
                        overlay
                )
        );
    }

    public BossBarHandle show(Audience audience) {
        Objects.requireNonNull(audience, "audience");

        audience.showBossBar(bossBar);
        viewers.add(audience);

        return this;
    }

    public BossBarHandle hide(Audience audience) {
        Objects.requireNonNull(audience, "audience");

        audience.hideBossBar(bossBar);
        viewers.remove(audience);

        return this;
    }

    public BossBarHandle hideAll() {
        for (Audience audience : Set.copyOf(viewers)) {
            audience.hideBossBar(bossBar);
        }

        viewers.clear();

        return this;
    }

    public BossBarHandle title(Component title) {
        bossBar.name(title);
        return this;
    }

    public BossBarHandle progress(float progress) {
        bossBar.progress(clamp(progress));
        return this;
    }

    public BossBarHandle color(BossBar.Color color) {
        bossBar.color(color);
        return this;
    }

    public BossBarHandle overlay(BossBar.Overlay overlay) {
        bossBar.overlay(overlay);
        return this;
    }

    public BossBar bossBar() {
        return bossBar;
    }

    private static float clamp(float value) {
        return Math.clamp(value, 0.0F, 1.0F);
    }
}
