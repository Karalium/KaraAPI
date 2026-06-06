package org.kerix.karaapi.api.effect.geometry;

import org.bukkit.Location;

import java.util.Objects;

public record GeometryContext(
        Location origin,
        int amount,
        int tick,
        int durationTicks,
        long seed
) {

    public GeometryContext {
        Objects.requireNonNull(origin, "origin");

        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
    }

    public double progress() {
        if (durationTicks <= 0) {
            return 1.0;
        }

        return Math.clamp((double) tick / durationTicks, 0.0, 1.0);
    }

    public GeometryContext withAmount(int newAmount) {
        return new GeometryContext(origin, newAmount, tick, durationTicks, seed);
    }
}
