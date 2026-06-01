package org.kerix.karaapi.api.tick;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.lifecycle.Tickable;
import org.kerix.karaapi.api.task.TaskHandle;
import org.kerix.karaapi.paper.scheduler.PaperScheduler;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public final class TickOrchestrator implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final PaperScheduler scheduler;

    private final Map<Long, TickBucket> buckets = new HashMap<>();
    private final Map<Tickable, Long> registeredTickables = new IdentityHashMap<>();

    public TickOrchestrator(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.scheduler = new PaperScheduler(hostPlugin);
    }

    public void register(Tickable tickable) {
        Objects.requireNonNull(tickable, "tickable");

        if (registeredTickables.containsKey(tickable)) {
            return;
        }

        long interval = Math.max(1L, tickable.tickInterval());

        TickBucket bucket = buckets.computeIfAbsent(interval, this::createBucket);

        bucket.tickables.add(tickable);
        registeredTickables.put(tickable, interval);
    }

    public void unregister(Tickable tickable) {
        Objects.requireNonNull(tickable, "tickable");

        Long interval = registeredTickables.remove(tickable);

        if (interval == null) {
            return;
        }

        TickBucket bucket = buckets.get(interval);

        if (bucket == null) {
            return;
        }

        bucket.tickables.remove(tickable);

        if (bucket.tickables.isEmpty()) {
            bucket.task.cancel();
            buckets.remove(interval);
        }
    }

    public void unregisterAll() {
        for (TickBucket bucket : buckets.values()) {
            bucket.task.cancel();
            bucket.tickables.clear();
        }

        buckets.clear();
        registeredTickables.clear();
    }

    public Set<Tickable> getTickables(long interval) {
        TickBucket bucket = buckets.get(interval);

        if (bucket == null) {
            return Set.of();
        }

        return Set.copyOf(bucket.tickables);
    }

    public Map<Long, Set<Tickable>> getRegisteredTickablesByInterval() {
        Map<Long, Set<Tickable>> result = new HashMap<>();

        for (Map.Entry<Long, TickBucket> entry : buckets.entrySet()) {
            result.put(entry.getKey(), Set.copyOf(entry.getValue().tickables));
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    public void stop() {
        unregisterAll();
    }

    private TickBucket createBucket(long interval) {
        TickBucket bucket = new TickBucket();

        bucket.task = scheduler.timer(
                "tick-bucket-" + interval,
                interval,
                interval,
                () -> tickBucket(bucket)
        );

        return bucket;
    }

    private void tickBucket(TickBucket bucket) {
        List<Tickable> snapshot = List.copyOf(bucket.tickables);

        for (Tickable tickable : snapshot) {
            try {
                tickable.tick();
            } catch (Throwable throwable) {
                hostPlugin.getLogger().log(
                        Level.SEVERE,
                        "Error while ticking " + tickable.getClass().getName(),
                        throwable
                );
            }
        }
    }

    private static final class TickBucket {

        private final Set<Tickable> tickables =
                Collections.newSetFromMap(new IdentityHashMap<>());

        private TaskHandle task;
    }
}
