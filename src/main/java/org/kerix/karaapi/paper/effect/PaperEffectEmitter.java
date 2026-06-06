package org.kerix.karaapi.paper.effect;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.effect.EffectEmitter;
import org.kerix.karaapi.api.effect.particle.ParticleStyle;

import java.util.Collection;
import java.util.Objects;


@MainThread
public final class PaperEffectEmitter implements EffectEmitter {

    @Override
    public void particle(Collection<Player> viewers, Location location, ParticleStyle style) {
        Objects.requireNonNull(viewers, "viewers");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(style, "style");

        for (Player viewer : viewers) {
            viewer.spawnParticle(
                    style.particle(),
                    location,
                    style.count(),
                    style.offsetX(),
                    style.offsetY(),
                    style.offsetZ(),
                    style.extra(),
                    style.data()
            );
        }
    }

    @Override
    public void sound(
            Collection<Player> viewers,
            Location location,
            Sound sound,
            SoundCategory category,
            float volume,
            float pitch
    ) {
        Objects.requireNonNull(viewers, "viewers");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(sound, "sound");
        Objects.requireNonNull(category, "category");

        for (Player viewer : viewers) {
            viewer.playSound(location, sound, category, volume, pitch);
        }
    }
}
