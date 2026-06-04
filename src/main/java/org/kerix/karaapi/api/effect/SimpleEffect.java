package org.kerix.karaapi.api.effect;

import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Objects;

public record SimpleEffect(
        NamespacedKey key,
        int durationTicks,
        List<EffectComponent> components
) implements Effect {

    public SimpleEffect {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(components, "components");

        if (durationTicks < 0) {
            throw new IllegalArgumentException("Effect duration cannot be negative.");
        }

        components = List.copyOf(components);
    }
}
