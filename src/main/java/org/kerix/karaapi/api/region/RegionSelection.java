package org.kerix.karaapi.api.region;

import org.bukkit.Location;

public final class RegionSelection {

    private Location first;
    private Location second;

    public void first(Location first) {
        this.first = clone(first);
    }

    public void second(Location second) {
        this.second = clone(second);
    }

    public boolean hasFirst() {
        return first != null;
    }

    public boolean hasSecond() {
        return second != null;
    }

    public boolean complete() {
        return first != null && second != null;
    }

    public CuboidRegion cuboid(String id) {
        if (!complete()) {
            throw new RegionException("Region selection is incomplete.");
        }

        return new CuboidRegion(id, first, second);
    }

    public Location first() {
        return clone(first);
    }

    public Location second() {
        return clone(second);
    }

    public void clear() {
        first = null;
        second = null;
    }

    private static Location clone(Location location) {
        return location == null ? null : location.clone();
    }
}
