package org.kerix.karaapi.api.effect;

import org.bukkit.NamespacedKey;

import java.util.List;

public interface Effect {

    NamespacedKey key();

    int durationTicks();

    List<EffectComponent> components();
}
