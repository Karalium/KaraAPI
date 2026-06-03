package org.kerix.karaapi.api.effect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface EffectAudience {

    Collection<Player> resolve(Location origin);

    static EffectAudience world() {
        return origin -> {
            World world = origin.getWorld();

            if (world == null) {
                return List.of();
            }

            return List.copyOf(world.getPlayers());
        };
    }

    static EffectAudience nearby(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative.");
        }

        double radiusSquared = radius * radius;

        return origin -> {
            World world = origin.getWorld();

            if (world == null) {
                return List.of();
            }

            return world.getPlayers()
                    .stream()
                    .filter(player -> player.getLocation().getWorld() == world)
                    .filter(player -> player.getLocation().distanceSquared(origin) <= radiusSquared)
                    .toList();
        };
    }

    static EffectAudience single(Player player) {
        Objects.requireNonNull(player, "player");

        return origin -> List.of(player);
    }

    static EffectAudience filtered(EffectAudience base, Predicate<Player> predicate) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(predicate, "predicate");

        return origin -> base.resolve(origin)
                .stream()
                .filter(predicate)
                .toList();
    }
}
