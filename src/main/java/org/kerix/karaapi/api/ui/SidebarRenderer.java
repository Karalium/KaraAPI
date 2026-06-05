package org.kerix.karaapi.api.ui;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface SidebarRenderer {

    void title(Component title);

    void lines(List<Component> lines);

    void clearLines();

    void hide();
}
