package org.kerix.karaapi.api.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SidebarManager {

    private final SidebarRendererFactory rendererFactory;
    private final Map<UUID, Sidebar> sidebars = new HashMap<>();

    public SidebarManager(SidebarRendererFactory rendererFactory) {
        this.rendererFactory = Objects.requireNonNull(rendererFactory, "rendererFactory");
    }

    public Sidebar getOrCreate(Player player, Component title) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(title, "title");

        Sidebar sidebar = sidebars.get(player.getUniqueId());

        if (sidebar != null) {
            sidebar.title(title);
            return sidebar;
        }

        sidebar = new Sidebar(rendererFactory.create(player, title));
        sidebars.put(player.getUniqueId(), sidebar);

        return sidebar;
    }

    public void remove(Player player) {
        Objects.requireNonNull(player, "player");

        Sidebar sidebar = sidebars.remove(player.getUniqueId());

        if (sidebar != null) {
            sidebar.hide();
        }
    }

    public void clearAll() {
        for (Sidebar sidebar : sidebars.values()) {
            sidebar.hide();
        }

        sidebars.clear();
    }

    public int size() {
        return sidebars.size();
    }

    public boolean has(Player player) {
        Objects.requireNonNull(player, "player");
        return sidebars.containsKey(player.getUniqueId());
    }
}
