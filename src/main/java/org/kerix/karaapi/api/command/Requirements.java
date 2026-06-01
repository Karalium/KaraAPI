package org.kerix.karaapi.api.command;

import org.bukkit.entity.Player;

public final class Requirements {

    private Requirements() {
    }

    public static CommandRequirement playerOnly() {
        return context -> {
            if (context.sender() instanceof Player) {
                return CommandResult.success();
            }

            return CommandResult.fail("Only players can use this command.");
        };
    }

    public static CommandRequirement permission(String permission) {
        return context -> {
            if (permission == null || permission.isBlank()) {
                return CommandResult.success();
            }

            if (context.sender().hasPermission(permission)) {
                return CommandResult.success();
            }

            return CommandResult.fail("You do not have permission to use this command.");
        };
    }

    public static CommandRequirement anyPermission(String... permissions) {
        return context -> {
            if (permissions == null || permissions.length == 0) {
                return CommandResult.success();
            }

            for (String permission : permissions) {
                if (permission != null && context.sender().hasPermission(permission)) {
                    return CommandResult.success();
                }
            }

            return CommandResult.fail("You do not have permission to use this command.");
        };
    }

    public static CommandRequirement opOnly() {
        return context -> {
            if (context.sender().isOp()) {
                return CommandResult.success();
            }

            return CommandResult.fail("Only operators can use this command.");
        };
    }
}
