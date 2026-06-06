package org.kerix.karaapi.api.effect.particle;

import org.bukkit.Particle;

import java.util.Objects;

public record ParticleStyle(
        Particle particle,
        int count,
        double offsetX,
        double offsetY,
        double offsetZ,
        double extra,
        Object data
) {

    public ParticleStyle {
        Objects.requireNonNull(particle, "particle");

        if (count <= 0) {
            throw new IllegalArgumentException("Particle count must be greater than zero.");
        }
    }

    public ParticleStyle withCount(int newCount) {
        return new ParticleStyle(
                particle,
                newCount,
                offsetX,
                offsetY,
                offsetZ,
                extra,
                data
        );
    }

    public ParticleStyle withOffset(double x, double y, double z) {
        return new ParticleStyle(
                particle,
                count,
                x,
                y,
                z,
                extra,
                data
        );
    }

    public ParticleStyle withExtra(double newExtra) {
        return new ParticleStyle(
                particle,
                count,
                offsetX,
                offsetY,
                offsetZ,
                newExtra,
                data
        );
    }
}
