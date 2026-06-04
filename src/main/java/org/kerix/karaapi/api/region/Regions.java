package org.kerix.karaapi.api.region;

import org.bukkit.Location;

public final class Regions {

    private Regions() {
    }

    public static CuboidRegion cuboid(String id, Location first, Location second) {
        return new CuboidRegion(id, first, second);
    }

    public static CuboidRegion cuboid(
            String id,
            String worldName,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        return new CuboidRegion(id, worldName, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static SphereRegion sphere(String id, Location center, double radius) {
        return new SphereRegion(id, center, radius);
    }

    public static SphereRegion sphere(
            String id,
            String worldName,
            double x,
            double y,
            double z,
            double radius
    ) {
        return new SphereRegion(id, worldName, x, y, z, radius);
    }

    public static RegionSelection selection() {
        return new RegionSelection();
    }
}
