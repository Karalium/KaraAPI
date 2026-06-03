package org.kerix.karaapi.api.effect;

import org.bukkit.NamespacedKey;

import java.util.Objects;
import java.util.function.Consumer;

public final class Effects {

    private Effects() {
    }

    public static Effect describe(NamespacedKey key, Consumer<EffectBuilder> consumer) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(consumer, "consumer");

        EffectBuilder builder = new EffectBuilder(key);
        consumer.accept(builder);

        return builder.build();
    }
}
