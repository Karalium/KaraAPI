package org.kerix.karaapi.api.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;
import java.util.Random;

public interface Region {

    String id();

    String worldName();

    boolean contains(Location location);

    Location center();

    Location randomLocation(Random random);

    default Optional<World> world() {
        return Optional.ofNullable(org.bukkit.Bukkit.getWorld(worldName()));
    }
}
