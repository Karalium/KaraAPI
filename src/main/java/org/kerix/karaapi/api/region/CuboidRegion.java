package org.kerix.karaapi.api.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.Random;

public final class CuboidRegion implements Region {

    private final String id;
    private final String worldName;

    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public CuboidRegion(String id, Location first, Location second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");

        if (first.getWorld() == null || second.getWorld() == null) {
            throw new IllegalArgumentException("Region locations must have worlds.");
        }

        if (!first.getWorld().equals(second.getWorld())) {
            throw new IllegalArgumentException("Cuboid region locations must be in the same world.");
        }

        this.id = RegionKeys.normalize(id);
        this.worldName = first.getWorld().getName();

        this.minX = Math.min(first.getX(), second.getX());
        this.minY = Math.min(first.getY(), second.getY());
        this.minZ = Math.min(first.getZ(), second.getZ());
        this.maxX = Math.max(first.getX(), second.getX());
        this.maxY = Math.max(first.getY(), second.getY());
        this.maxZ = Math.max(first.getZ(), second.getZ());
    }

    public CuboidRegion(
            String id,
            String worldName,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        this.id = RegionKeys.normalize(id);
        this.worldName = Objects.requireNonNull(worldName, "worldName");

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String worldName() {
        return worldName;
    }

    @Override
    public boolean contains(Location location) {
        if (location == null || !sameWorld(location)) {
            return false;
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @Override
    public Location center() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new RegionException("World is not loaded: " + worldName);
        }

        return new Location(
                world,
                (minX + maxX) / 2.0,
                (minY + maxY) / 2.0,
                (minZ + maxZ) / 2.0
        );
    }

    @Override
    public Location randomLocation(Random random) {
        Objects.requireNonNull(random, "random");

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new RegionException("World is not loaded: " + worldName);
        }

        double x = minX + random.nextDouble() * Math.max(0.0, maxX - minX);
        double y = minY + random.nextDouble() * Math.max(0.0, maxY - minY);
        double z = minZ + random.nextDouble() * Math.max(0.0, maxZ - minZ);

        return new Location(world, x, y, z);
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double minZ() {
        return minZ;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double maxZ() {
        return maxZ;
    }
}
