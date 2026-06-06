package org.kerix.karaapi.api.effect.sound;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.kerix.karaapi.api.effect.EffectComponent;
import org.kerix.karaapi.api.effect.EffectContext;
import org.kerix.karaapi.api.effect.EffectOutput;

import java.util.Objects;

public final class SoundLayer implements EffectComponent {

    private final String name;
    private final int tick;
    private final Sound sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;

    private SoundLayer(Builder builder) {
        this.name = builder.name;
        this.tick = builder.tick;
        this.sound = Objects.requireNonNull(builder.sound, "sound");
        this.category = builder.category;
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
        if (context.tick() != tick) {
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

        private int tick = 0;
        private Sound sound;
        private SoundCategory category = SoundCategory.PLAYERS;
        private float volume = 1.0f;
        private float pitch = 1.0f;

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        public Builder at(int tick) {
            if (tick < 0) {
                throw new IllegalArgumentException("Tick cannot be negative.");
            }

            this.tick = tick;
            return this;
        }

        public Builder sound(Sound sound) {
            this.sound = Objects.requireNonNull(sound, "sound");
            return this;
        }

        public Builder category(SoundCategory category) {
            this.category = Objects.requireNonNull(category, "category");
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

        public SoundLayer build() {
            return new SoundLayer(this);
        }
    }
}
