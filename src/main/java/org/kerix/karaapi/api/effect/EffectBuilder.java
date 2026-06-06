package org.kerix.karaapi.api.effect;

import org.bukkit.NamespacedKey;
import org.kerix.karaapi.api.effect.particle.ParticleLayer;
import org.kerix.karaapi.api.effect.sound.AmbientSoundLayer;
import org.kerix.karaapi.api.effect.sound.SoundLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class EffectBuilder {

    private final NamespacedKey key;
    private final List<EffectComponent> components = new ArrayList<>();

    private int durationTicks = 0;

    EffectBuilder(NamespacedKey key) {
        this.key = Objects.requireNonNull(key, "key");
    }

    public EffectBuilder duration(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Duration cannot be negative.");
        }

        this.durationTicks = ticks;
        return this;
    }

    public EffectBuilder component(EffectComponent component) {
        components.add(Objects.requireNonNull(component, "component"));
        return this;
    }

    public EffectBuilder particles(
            String name,
            Consumer<ParticleLayer.Builder> consumer
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(consumer, "consumer");

        ParticleLayer.Builder builder = ParticleLayer.builder(name);
        consumer.accept(builder);

        return component(builder.build());
    }

    public EffectBuilder sound(
            String name,
            Consumer<SoundLayer.Builder> consumer
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(consumer, "consumer");

        SoundLayer.Builder builder = SoundLayer.builder(name);
        consumer.accept(builder);

        return component(builder.build());
    }

    public EffectBuilder ambient(
            String name,
            Consumer<AmbientSoundLayer.Builder> consumer
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(consumer, "consumer");

        AmbientSoundLayer.Builder builder = AmbientSoundLayer.builder(name);
        consumer.accept(builder);

        return component(builder.build());
    }

    public Effect build() {
        return new SimpleEffect(key, durationTicks, components);
    }
}
