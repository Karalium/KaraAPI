package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

final class RunningEffect {

    final UUID id;
    private final Effect effect;
    private final Location origin;
    private final EffectAudience audience;
    private final EffectEmitter emitter;
    private final long seed;

    private int tick;

    RunningEffect(
            UUID id,
            Effect effect,
            Location origin,
            EffectAudience audience,
            EffectEmitter emitter,
            long seed
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.effect = Objects.requireNonNull(effect, "effect");
        this.origin = Objects.requireNonNull(origin, "origin");
        this.audience = Objects.requireNonNull(audience, "audience");
        this.emitter = Objects.requireNonNull(emitter, "emitter");
        this.seed = seed;
    }

    void tick() {
        Collection<Player> viewers = audience.resolve(origin);

        EffectContext context = new EffectContext(
                id,
                origin.clone(),
                tick,
                effect.durationTicks(),
                seed
        );

        EffectOutput output = new EffectOutput(emitter, viewers);

        for (EffectComponent component : effect.components()) {
            component.tick(context, output);
        }

        tick++;
    }

    boolean finished() {
        return tick > effect.durationTicks();
    }
}
