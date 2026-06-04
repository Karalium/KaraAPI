package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.kerix.karaapi.api.effect.geometry.Motif;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.scheduler.ScheduledTaskHandle;
import org.kerix.karaapi.api.scheduler.SchedulerService;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class EffectService implements Stoppable {

    private final SchedulerService scheduler;
    private final EffectEmitter emitter;
    private final Map<NamespacedKey, Effect> effects = new LinkedHashMap<>();
    private final Map<NamespacedKey, Motif> motifs = new LinkedHashMap<>();
    private final Map<UUID, RunningEffect> runningEffects = new LinkedHashMap<>();

    private ScheduledTaskHandle task;
    private boolean stopped;

    public EffectService(SchedulerService scheduler, EffectEmitter emitter) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.emitter = Objects.requireNonNull(emitter, "emitter");
    }

    public void registerEffect(Effect effect) {
        ensureAlive();
        Objects.requireNonNull(effect, "effect");

        NamespacedKey key = effect.key();

        if (effects.containsKey(key)) {
            throw new EffectException("Effect already registered: " + key);
        }

        effects.put(key, effect);
    }

    public void unregisterEffect(NamespacedKey key) {
        ensureAlive();
        effects.remove(Objects.requireNonNull(key, "key"));
    }

    public Optional<Effect> effect(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(effects.get(key));
    }

    public Collection<Effect> effects() {
        return List.copyOf(effects.values());
    }

    public void registerMotif(NamespacedKey key, Motif motif) {
        ensureAlive();
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(motif, "motif");

        if (motifs.containsKey(key)) {
            throw new EffectException("Motif already registered: " + key);
        }

        motifs.put(key, motif);
    }

    public void unregisterMotif(NamespacedKey key) {
        ensureAlive();
        motifs.remove(Objects.requireNonNull(key, "key"));
    }

    public Optional<Motif> motif(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(motifs.get(key));
    }

    public Collection<Motif> motifs() {
        return List.copyOf(motifs.values());
    }

    public EffectHandle play(NamespacedKey key, Location origin) {
        return play(key, origin, EffectAudience.nearby(64.0));
    }

    public EffectHandle play(
            NamespacedKey key,
            Location origin,
            EffectAudience audience
    ) {
        Objects.requireNonNull(key, "key");

        Effect effect = effect(key)
                .orElseThrow(() -> new EffectException("Unknown effect: " + key));

        return play(effect, origin, audience);
    }

    public EffectHandle play(Effect effect, Location origin) {
        return play(effect, origin, EffectAudience.nearby(64.0));
    }

    public EffectHandle play(
            Effect effect,
            Location origin,
            EffectAudience audience
    ) {
        ensureAlive();
        Objects.requireNonNull(effect, "effect");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(audience, "audience");

        UUID id = UUID.randomUUID();

        RunningEffect runningEffect = new RunningEffect(
                id,
                effect,
                origin.clone(),
                audience,
                emitter,
                System.nanoTime()
        );

        runningEffects.put(id, runningEffect);
        ensureTaskRunning();

        return new EffectHandle(id, () -> runningEffects.remove(id));
    }

    public void stop(EffectHandle handle) {
        Objects.requireNonNull(handle, "handle");
        handle.cancel();
    }

    public void stopAll() {
        runningEffects.clear();

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public int runningCount() {
        return runningEffects.size();
    }

    public boolean stopped() {
        return stopped;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        stopAll();
        effects.clear();
        motifs.clear();
    }

    private void ensureTaskRunning() {
        if (task != null && task.running()) {
            return;
        }

        task = scheduler.timer(0L, 1L, this::tick);
    }

    private void tick() {
        Iterator<RunningEffect> iterator = runningEffects.values().iterator();

        while (iterator.hasNext()) {
            RunningEffect runningEffect = iterator.next();

            runningEffect.tick();

            if (runningEffect.finished()) {
                iterator.remove();
            }
        }

        if (runningEffects.isEmpty() && task != null) {
            task.cancel();
            task = null;
        }
    }

    private void ensureAlive() {
        if (stopped) {
            throw new IllegalStateException("EffectService has already been stopped.");
        }
    }
}
