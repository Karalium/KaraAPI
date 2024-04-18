package org.kerix.api.items.head;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.kerix.api.items.head.HeadsAPI.getHead;

public class HeadGiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /gethead <base64>");
            return true;
        }
        String base64String = args[0];

        p.getInventory().addItem(getHead(base64String));
        p.sendMessage("§aYou have received the head");
        return true;
    }
}
