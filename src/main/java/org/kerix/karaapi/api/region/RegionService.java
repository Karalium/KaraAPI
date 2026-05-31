package org.kerix.karaapi.api.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.registry.MutableRegistry;

import java.util.List;
import java.util.Random;

public final class RegionService implements Stoppable {

    private final MutableRegistry<Region> regions = MutableRegistry.create("regions");

    public void register(Region region) {
        regions.register(region.id(), region);
    }

    public void replace(Region region) {
        regions.replace(region.id(), region);
    }

    public Region get(String id) {
        return regions.get(id);
    }

    public boolean contains(String id, Location location) {
        return get(id).contains(location);
    }

    public List<Region> at(Location location) {
        return regions.values()
                .stream()
                .filter(region -> region.contains(location))
                .toList();
    }

    public boolean insideAny(Location location) {
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
        return get(id).randomLocation(new Random());
    }

    public MutableRegistry<Region> registry() {
        return regions;
    }

    @Override
    public void stop() {
        regions.clear();
    }
}
