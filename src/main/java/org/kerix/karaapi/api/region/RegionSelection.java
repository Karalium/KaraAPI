package org.kerix.karaapi.api.region;

import org.bukkit.Location;

public final class RegionSelection {

    private Location first;
    private Location second;

    public void first(Location first) {
        this.first = first == null ? null : first.clone();
    }

    public void second(Location second) {
        this.second = second == null ? null : second.clone();
    }

    public boolean complete() {
        return first != null && second != null;
    }

    public CuboidRegion cuboid(String id) {
        if (!complete()) {
            throw new IllegalStateException("Region selection is incomplete.");
        }

        return new CuboidRegion(id, first, second);
    }

    public Location first() {
        return first == null ? null : first.clone();
    }

    public Location second() {
        return second == null ? null : second.clone();
    }

    public void clear() {
        first = null;
        second = null;
    }
}
