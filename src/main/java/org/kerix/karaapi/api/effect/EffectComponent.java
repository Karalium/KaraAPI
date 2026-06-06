package org.kerix.karaapi.api.effect;

@FunctionalInterface
public interface EffectComponent {

    void tick(EffectContext context, EffectOutput output);
}
