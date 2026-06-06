package org.kerix.karaapi.api.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.Random;

public final class SphereRegion implements Region {

    private final String id;
    private final String worldName;

    private final double x;
    private final double y;
    private final double z;
    private final double radius;
    private final double radiusSquared;

    public SphereRegion(String id, Location center, double radius) {
        Objects.requireNonNull(center, "center");

        if (center.getWorld() == null) {
            throw new IllegalArgumentException("Sphere center must have a world.");
        }

        if (radius <= 0.0) {
            throw new IllegalArgumentException("Sphere radius must be greater than 0.");
        }

        this.id = RegionKeys.normalize(id);
        this.worldName = center.getWorld().getName();
        this.x = center.getX();
        this.y = center.getY();
        this.z = center.getZ();
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    public SphereRegion(
            String id,
            String worldName,
            double x,
            double y,
            double z,
            double radius
    ) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Sphere radius must be greater than 0.");
        }

        this.id = RegionKeys.normalize(id);
        this.worldName = Objects.requireNonNull(worldName, "worldName");
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.radiusSquared = radius * radius;
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

        double dx = location.getX() - x;
        double dy = location.getY() - y;
        double dz = location.getZ() - z;

        return dx * dx + dy * dy + dz * dz <= radiusSquared;
    }

    @Override
    public Location center() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new RegionException("World is not loaded: " + worldName);
        }

        return new Location(world, x, y, z);
    }

    @Override
    public Location randomLocation(Random random) {
        Objects.requireNonNull(random, "random");

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new RegionException("World is not loaded: " + worldName);
        }

        double theta = random.nextDouble() * Math.PI * 2.0;
        double phi = Math.acos(2.0 * random.nextDouble() - 1.0);
        double r = radius * Math.cbrt(random.nextDouble());

        double randomX = x + r * Math.sin(phi) * Math.cos(theta);
        double randomY = y + r * Math.cos(phi);
        double randomZ = z + r * Math.sin(phi) * Math.sin(theta);

        return new Location(world, randomX, randomY, randomZ);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public double radius() {
        return radius;
    }

    public double radiusSquared() {
        return radiusSquared;
    }
}
