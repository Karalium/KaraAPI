package org.kerix.karaapi.api.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.DependsOn;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.event.EventBus;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.registry.MutableRegistry;
import org.kerix.karaapi.api.registry.Registry;
import org.kerix.karaapi.paper.region.PaperRegionListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@ManagedService(
        value = RegionService.class,
        priority = 65,
        registerAnnotatedTicks = false
)
@DependsOn(EventBus.class)
@MainThread
public final class RegionService implements Stoppable {

    private final MutableRegistry<Region> regions = MutableRegistry.create("regions");
    private final Map<String, RegionRegistration> registrations = new HashMap<>();
    private final Map<UUID, Set<String>> memberships = new HashMap<>();
    private final EventBus events;

    private boolean stopped;

    public RegionService(EventBus events) {
        this.events = Objects.requireNonNull(events, "events");
    }

    public RegionRegistration register(Region region) {
        ensureRunning();
        Objects.requireNonNull(region, "region");

        String id = RegionKeys.normalize(region.id());

        if (registrations.containsKey(id)) {
            throw new RegionException("Region is already registered: " + id);
        }

        regions.register(id, region);

        RegionRegistration registration = new RegionRegistration(
                id,
                () -> unregister(id)
        );

        registrations.put(id, registration);

        return registration;
    }

    public RegionRegistration replace(Region region) {
        ensureRunning();
        Objects.requireNonNull(region, "region");

        String id = RegionKeys.normalize(region.id());

        if (registered(id)) {
            unregister(id);
        }

        return register(region);
    }

    public void unregister(String id) {
        ensureRunning();

        String key = RegionKeys.normalize(id);

        registrations.remove(key);
        regions.unregister(key);

        for (Set<String> playerRegions : memberships.values()) {
            playerRegions.remove(key);
        }
    }

    public Region get(String id) {
        return regions.get(RegionKeys.normalize(id));
    }

    public boolean registered(String id) {
        return regions.contains(RegionKeys.normalize(id));
    }

    public boolean contains(String id, Location location) {
        return get(id).contains(location);
    }

    public List<Region> at(Location location) {
        Objects.requireNonNull(location, "location");

        return regions.values()
                .stream()
                .filter(region -> region.contains(location))
                .toList();
    }

    public boolean insideAny(Location location) {
        Objects.requireNonNull(location, "location");

        return regions.values()
                .stream()
                .anyMatch(region -> region.contains(location));
    }

    public List<Player> playersInside(String id) {
        Region region = get(id);

        return region.world()
                .map(world -> world.getPlayers()
                        .stream()
                        .filter(player -> region.contains(player.getLocation()))
                        .toList())
                .orElse(List.of());
    }

    public Location randomLocation(String id) {
        return randomLocation(id, new Random());
    }

    public Location randomLocation(String id, Random random) {
        return get(id).randomLocation(random);
    }

    public Set<String> memberships(Player player) {
        Objects.requireNonNull(player, "player");

        return Set.copyOf(
                memberships.getOrDefault(player.getUniqueId(), Set.of())
        );
    }

    public boolean inside(Player player, String regionId) {
        Objects.requireNonNull(player, "player");

        return memberships(player).contains(RegionKeys.normalize(regionId));
    }

    public void update(Player player) {
        update(player, null, player.getLocation());
    }

    public void update(Player player, Location from, Location to) {
        ensureRunning();
        Objects.requireNonNull(player, "player");

        Location target = to == null ? player.getLocation() : to;
        UUID uuid = player.getUniqueId();

        Set<String> previous = memberships.getOrDefault(uuid, Set.of());
        Set<String> current = new HashSet<>();

        for (Region region : regions.values()) {
            if (region.contains(target)) {
                current.add(region.id());
            }
        }

        for (String regionId : previous) {
            if (!current.contains(regionId)) {
                Region region = regions.find(regionId).orElse(null);

                if (region != null) {
                    events.publish(new RegionLeave(this, region, player, clone(from), clone(target)));
                }
            }
        }

        for (String regionId : current) {
            if (!previous.contains(regionId)) {
                Region region = regions.find(regionId).orElse(null);

                if (region != null) {
                    events.publish(new RegionEnter(this, region, player, clone(from), clone(target)));
                }
            }
        }

        if (current.isEmpty()) {
            memberships.remove(uuid);
        } else {
            memberships.put(uuid, Set.copyOf(current));
        }
    }

    public void clear(Player player) {
        clear(player, player == null ? null : player.getLocation());
    }

    public void clear(Player player, Location location) {
        if (player == null) {
            return;
        }

        Set<String> previous = memberships.remove(player.getUniqueId());

        if (previous == null || previous.isEmpty()) {
            return;
        }

        for (String regionId : previous) {
            Region region = regions.find(regionId).orElse(null);

            if (region != null) {
                events.publish(new RegionLeave(this, region, player, clone(location), clone(location)));
            }
        }
    }

    public Registry<Region> registry() {
        return regions;
    }

    public int size() {
        return regions.size();
    }

    public boolean empty() {
        return regions.empty();
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        memberships.clear();
        registrations.clear();
        regions.clear();
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("RegionService has already stopped.");
        }
    }

    private static Location clone(Location location) {
        return location == null ? null : location.clone();
    }
}
