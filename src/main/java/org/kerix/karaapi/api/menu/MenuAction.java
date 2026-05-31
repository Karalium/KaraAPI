package org.kerix.karaapi.api.menu;

@FunctionalInterface
public interface MenuAction {

    void click(MenuClick click);
}
