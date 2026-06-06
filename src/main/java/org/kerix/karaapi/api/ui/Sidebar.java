package org.kerix.karaapi.api.ui;

import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Sidebar {

    private final SidebarRenderer renderer;

    Sidebar(SidebarRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    public Sidebar title(Component title) {
        renderer.title(Objects.requireNonNull(title, "title"));
        return this;
    }

    public Sidebar lines(Component... lines) {
        return lines(Arrays.asList(lines));
    }

    public Sidebar lines(List<Component> lines) {
        renderer.lines(List.copyOf(Objects.requireNonNull(lines, "lines")));
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
