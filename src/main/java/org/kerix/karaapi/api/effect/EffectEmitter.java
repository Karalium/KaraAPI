package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.effect.particle.ParticleStyle;

import java.util.Collection;

public interface EffectEmitter {

    void particle(Collection<Player> viewers, Location location, ParticleStyle style);

    void sound(
            Collection<Player> viewers,
            Location location,
            Sound sound,
            SoundCategory category,
            float volume,
            float pitch
    );
}
