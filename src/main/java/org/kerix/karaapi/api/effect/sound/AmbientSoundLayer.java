package org.kerix.karaapi.api.effect.sound;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.kerix.karaapi.api.effect.EffectComponent;
import org.kerix.karaapi.api.effect.EffectContext;
import org.kerix.karaapi.api.effect.EffectOutput;

import java.util.Objects;

public final class AmbientSoundLayer implements EffectComponent {

    private final String name;
    private final Sound sound;
    private final SoundCategory category;
    private final int fromTick;
    private final int toTick;
    private final int period;
    private final float volume;
    private final float pitch;

    private AmbientSoundLayer(Builder builder) {
        this.name = builder.name;
        this.sound = Objects.requireNonNull(builder.sound, "sound");
        this.category = builder.category;
        this.fromTick = builder.fromTick;
        this.toTick = builder.toTick;
        this.period = builder.period;
        this.volume = builder.volume;
        this.pitch = builder.pitch;
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

        if ((context.tick() - fromTick) % period != 0) {
            return;
        }

        output.sound(
                context.origin(),
                sound,
                category,
                volume,
                pitch
        );
    }

    public static final class Builder {

        private final String name;

        private Sound sound;
        private SoundCategory category = SoundCategory.AMBIENT;
        private int fromTick = 0;
        private int toTick = -1;
        private int period = 40;
        private float volume = 0.5f;
        private float pitch = 1.0f;

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        public Builder sound(Sound sound) {
            this.sound = Objects.requireNonNull(sound, "sound");
            return this;
        }

        public Builder category(SoundCategory category) {
            this.category = Objects.requireNonNull(category, "category");
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

        public Builder every(int ticks) {
            if (ticks <= 0) {
                throw new IllegalArgumentException("Period must be greater than zero.");
            }

            this.period = ticks;
            return this;
        }

        public Builder volume(float volume) {
            this.volume = volume;
            return this;
        }

        public Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public AmbientSoundLayer build() {
            return new AmbientSoundLayer(this);
        }
    }
}
