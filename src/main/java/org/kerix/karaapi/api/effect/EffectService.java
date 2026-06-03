package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kerix.karaapi.api.effect.geometry.Motif;
import org.kerix.karaapi.api.effect.particle.ParticleStyle;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class EffectService implements Stoppable {

    private final JavaPlugin plugin;

    private final Map<NamespacedKey, Effect> effects = new LinkedHashMap<>();
    private final Map<NamespacedKey, Motif> motifs = new LinkedHashMap<>();
    private final Map<UUID, RunningEffect> runningEffects = new LinkedHashMap<>();

    private BukkitTask task;
    private boolean stopped;

    public EffectService(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void registerEffect(Effect effect) {
        ensureAlive();
        Objects.requireNonNull(effect, "effect");

        NamespacedKey key = effect.key();

        if (effects.containsKey(key)) {
            throw new IllegalArgumentException("Effect already registered: " + key);
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
            throw new IllegalArgumentException("Motif already registered: " + key);
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
        return play(key, origin, EffectAudience.nearby(64));
    }

    public EffectHandle play(NamespacedKey key, Location origin, EffectAudience audience) {
        Objects.requireNonNull(key, "key");

        Effect effect = effect(key)
                .orElseThrow(() -> new IllegalArgumentException("Unknown effect: " + key));

        return play(effect, origin, audience);
    }

    public EffectHandle play(Effect effect, Location origin) {
        return play(effect, origin, EffectAudience.nearby(64));
    }

    public EffectHandle play(Effect effect, Location origin, EffectAudience audience) {
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

    public int runningCount() {
        return runningEffects.size();
    }

    public boolean stopped() {
        return stopped;
    }

    public void stopAll() {
        runningEffects.clear();

        if (task != null) {
            task.cancel();
            task = null;
        }
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
        if (task != null) {
            return;
        }

        task = plugin.getServer()
                .getScheduler()
                .runTaskTimer(plugin, this::tick, 0L, 1L);
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

    static final class BukkitEffectOutput implements EffectOutput {

        private final Collection<Player> viewers;

        BukkitEffectOutput(Collection<Player> viewers) {
            this.viewers = List.copyOf(viewers);
        }

        @Override
        public Collection<Player> viewers() {
            return viewers;
        }

        @Override
        public void particle(Location location, ParticleStyle style) {
            for (Player viewer : viewers) {
                viewer.spawnParticle(
                        style.particle(),
                        location,
                        style.count(),
                        style.offsetX(),
                        style.offsetY(),
                        style.offsetZ(),
                        style.extra(),
                        style.data()
                );
            }
        }

        @Override
        public void sound(
                Location location,
                Sound sound,
                SoundCategory category,
                float volume,
                float pitch
        ) {
            for (Player viewer : viewers) {
                viewer.playSound(location, sound, category, volume, pitch);
            }
        }
    }
}
