package org.kerix.karaapi.paper.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.ui.SidebarRenderer;
import org.kerix.karaapi.api.ui.SidebarRendererFactory;

@MainThread
public final class PaperSidebarRendererFactory implements SidebarRendererFactory {

    @Override
    public SidebarRenderer create(Player player, Component title) {
        return new PaperSidebarRenderer(player, title);
    }
}
