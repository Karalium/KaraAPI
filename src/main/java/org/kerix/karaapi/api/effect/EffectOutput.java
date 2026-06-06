package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.effect.particle.ParticleStyle;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class EffectOutput {

    private final EffectEmitter emitter;
    private final Collection<Player> viewers;

    public EffectOutput(EffectEmitter emitter, Collection<Player> viewers) {
        this.emitter = Objects.requireNonNull(emitter, "emitter");
        this.viewers = List.copyOf(Objects.requireNonNull(viewers, "viewers"));
    }

    public Collection<Player> viewers() {
        return viewers;
    }

    public void particle(Location location, ParticleStyle style) {
        emitter.particle(viewers, location, style);
    }

    public void sound(
            Location location,
            Sound sound,
            SoundCategory category,
            float volume,
            float pitch
    ) {
        emitter.sound(viewers, location, sound, category, volume, pitch);
    }
}
