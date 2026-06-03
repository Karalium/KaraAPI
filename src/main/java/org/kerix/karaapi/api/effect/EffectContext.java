package org.kerix.karaapi.api.effect;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public record EffectContext(
        UUID instanceId,
        Location origin,
        int tick,
        int durationTicks,
        long seed
) {

    public EffectContext {
        Objects.requireNonNull(instanceId, "instanceId");
        Objects.requireNonNull(origin, "origin");
    }

    public double progress() {
        if (durationTicks <= 0) {
            return 1.0;
        }

        return Math.clamp((double) tick / durationTicks, 0.0, 1.0);
    }

    public boolean firstTick() {
        return tick == 0;
    }

    public boolean lastTick() {
        return tick >= durationTicks;
    }
}
