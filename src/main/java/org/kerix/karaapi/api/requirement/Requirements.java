package org.kerix.karaapi.api.requirement;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Predicate;

public final class Requirements {

    private Requirements() {
    }

    public static <T> Requirement<T> always() {
        return target -> RequirementResult.allow();
    }

    public static <T> Requirement<T> never(String message) {
        return target -> RequirementResult.deny(message);
    }

    public static <T> Requirement<T> predicate(
            Predicate<T> predicate,
            String denyMessage
    ) {
        Objects.requireNonNull(predicate, "predicate");

        return target -> predicate.test(target)
                ? RequirementResult.allow()
                : RequirementResult.deny(denyMessage);
    }

    public static Requirement<CommandSender> permission(String permission) {
        return sender -> {
            if (permission == null || permission.isBlank()) {
                return RequirementResult.allow();
            }

            return sender.hasPermission(permission)
                    ? RequirementResult.allow()
                    : RequirementResult.deny("You do not have permission.");
        };
    }

    public static Requirement<CommandSender> op() {
        return sender -> sender.isOp()
                ? RequirementResult.allow()
                : RequirementResult.deny("Only operators can do this.");
    }

    public static Requirement<CommandSender> playerOnly() {
        return sender -> sender instanceof Player
                ? RequirementResult.allow()
                : RequirementResult.deny("Only players can do this.");
    }

    public static Requirement<Player> world(String worldName) {
        return player -> {
            World world = player.getWorld();

            return world.getName().equalsIgnoreCase(worldName)
                    ? RequirementResult.allow()
                    : RequirementResult.deny("You cannot do this in this world.");
        };
    }

    public static Requirement<Player> within(double distance, Location location) {
        Objects.requireNonNull(location, "location");

        return player -> {
            if (!player.getWorld().equals(location.getWorld())) {
                return RequirementResult.deny("You are too far away.");
            }

            return player.getLocation().distanceSquared(location) <= distance * distance
                    ? RequirementResult.allow()
                    : RequirementResult.deny("You are too far away.");
        };
    }

    @SafeVarargs
    public static <T> Requirement<T> all(Requirement<T>... requirements) {
        return target -> {
            if (requirements == null) {
                return RequirementResult.allow();
            }

            for (Requirement<T> requirement : requirements) {
                RequirementResult result = requirement.check(target);

                if (result.denied()) {
                    return result;
                }
            }

            return RequirementResult.allow();
        };
    }

    @SafeVarargs
    public static <T> Requirement<T> any(Requirement<T>... requirements) {
        return target -> {
            if (requirements == null || requirements.length == 0) {
                return RequirementResult.allow();
            }

            RequirementResult last = RequirementResult.deny("Requirement failed.");

            for (Requirement<T> requirement : requirements) {
                RequirementResult result = requirement.check(target);

                if (result.allowed()) {
                    return result;
                }

                last = result;
            }

            return last;
        };
    }
}
