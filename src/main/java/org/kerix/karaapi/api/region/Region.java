package org.kerix.karaapi.api.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public interface Region {

    String id();

    String worldName();

    boolean contains(Location location);

    Location center();

    Location randomLocation(Random random);

    default Optional<World> world() {
        return Optional.ofNullable(Bukkit.getWorld(worldName()));
    }

    default boolean sameWorld(Location location) {
        Objects.requireNonNull(location, "location");

        World world = location.getWorld();

        return world != null && world.getName().equals(worldName());
    }
}
