package org.kerix.karaapi.api.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.kerix.karaapi.paper.scoreboard.PaperSidebarRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Sidebar {

    private final PaperSidebarRenderer renderer;

    public Sidebar(Player player, Component title) {
        this.renderer = new PaperSidebarRenderer(
                Objects.requireNonNull(player, "player"),
                Objects.requireNonNull(title, "title")
        );
    }

    public Sidebar title(Component title) {
        renderer.title(Objects.requireNonNull(title, "title"));
        return this;
    }

    public Sidebar lines(Component... lines) {
        return lines(Arrays.asList(lines));
    }

    public Sidebar lines(List<Component> lines) {
        renderer.lines(List.copyOf(lines));
        return this;
    }

    public Sidebar clearLines() {
        renderer.clearLines();
        return this;
    }

    public void hide() {
        renderer.hide();
    }
}
