package org.kerix.karaapi.api.effect;

import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Objects;

public record SimpleEffect(NamespacedKey key, int durationTicks, List<EffectComponent> components) implements Effect {

    public SimpleEffect(
            NamespacedKey key,
            int durationTicks,
            List<EffectComponent> components
    ) {
        if (durationTicks < 0) {
            throw new IllegalArgumentException("Effect duration cannot be negative.");
        }

        this.key = Objects.requireNonNull(key, "key");
        this.durationTicks = durationTicks;
        this.components = List.copyOf(Objects.requireNonNull(components, "components"));
    }
}
