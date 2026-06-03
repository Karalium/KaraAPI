package org.kerix.karaapi.api.effect.particle;

import org.bukkit.Color;
import org.bukkit.Particle;

public final class ParticleStyles {

    private ParticleStyles() {
    }

    public static ParticleStyle simple(Particle particle) {
        return new ParticleStyle(
                particle,
                1,
                0,
                0,
                0,
                0,
                null
        );
    }

    public static ParticleStyle dust(Color color, float size) {
        return new ParticleStyle(
                Particle.DUST,
                1,
                0,
                0,
                0,
                0,
                new Particle.DustOptions(color, size)
        );
    }

    public static ParticleStyle smoke() {
        return simple(Particle.SMOKE);
    }

    public static ParticleStyle flame() {
        return simple(Particle.FLAME);
    }

    public static ParticleStyle enchant() {
        return simple(Particle.ENCHANT);
    }
}
