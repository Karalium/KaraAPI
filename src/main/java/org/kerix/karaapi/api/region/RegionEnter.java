package org.kerix.karaapi.api.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record RegionEnter(
        RegionService regions,
        Region region,
        Player player,
        Location from,
        Location to
) {
}
