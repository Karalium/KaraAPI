package org.kerix.karaapi.api.item.custom;

@FunctionalInterface
public interface CustomItemAction<T> {

    void handle(T event);
}
