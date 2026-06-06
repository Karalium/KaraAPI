package org.kerix.karaapi.api.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface SidebarRendererFactory {

    SidebarRenderer create(Player player, Component title);
}
