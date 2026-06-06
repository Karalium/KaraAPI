package org.kerix.karaapi.api.effect.particle;

import org.bukkit.Location;
import org.kerix.karaapi.api.effect.EffectComponent;
import org.kerix.karaapi.api.effect.EffectContext;
import org.kerix.karaapi.api.effect.EffectOutput;
import org.kerix.karaapi.api.effect.geometry.GeometryContext;
import org.kerix.karaapi.api.effect.geometry.GeometrySource;

import java.util.Objects;

public final class ParticleLayer implements EffectComponent {

    private final String name;
    private final GeometrySource geometry;
    private final ParticleStyle style;
    private final int amount;
    private final int fromTick;
    private final int toTick;
    private final int period;

    private ParticleLayer(Builder builder) {
        this.name = builder.name;
        this.geometry = Objects.requireNonNull(builder.geometry, "geometry");
        this.style = Objects.requireNonNull(builder.style, "style");
        this.amount = builder.amount;
        this.fromTick = builder.fromTick;
        this.toTick = builder.toTick;
        this.period = builder.period;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public String name() {
        return name;
    }

    @Override
    public void tick(EffectContext context, EffectOutput output) {
        if (context.tick() < fromTick) {
            return;
        }

        if (toTick >= 0 && context.tick() > toTick) {
            return;
        }

        if (period > 1 && (context.tick() - fromTick) % period != 0) {
            return;
        }

        GeometryContext geometryContext = new GeometryContext(
                context.origin(),
                amount,
                context.tick(),
                context.durationTicks(),
                context.seed()
        );

        geometry.generate(geometryContext, (x, y, z) -> {
            Location location = context.origin().clone().add(x, y, z);
            output.particle(location, style);
        });
    }

    public static final class Builder {

        private final String name;

        private GeometrySource geometry;
        private ParticleStyle style;
        private int amount = 1;
        private int fromTick = 0;
        private int toTick = -1;
        private int period = 1;

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        public Builder geometry(GeometrySource geometry) {
            this.geometry = Objects.requireNonNull(geometry, "geometry");
            return this;
        }

        public Builder style(ParticleStyle style) {
            this.style = Objects.requireNonNull(style, "style");
            return this;
        }

        public Builder amount(int amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("Amount cannot be negative.");
            }

            this.amount = amount;
            return this;
        }

        public Builder from(int tick) {
            if (tick < 0) {
                throw new IllegalArgumentException("Start tick cannot be negative.");
            }

            this.fromTick = tick;
            return this;
        }

        public Builder to(int tick) {
            this.toTick = tick;
            return this;
        }

        public Builder period(int ticks) {
            if (ticks <= 0) {
                throw new IllegalArgumentException("Period must be greater than zero.");
            }

            this.period = ticks;
            return this;
        }

        public ParticleLayer build() {
            return new ParticleLayer(this);
        }
    }
}
