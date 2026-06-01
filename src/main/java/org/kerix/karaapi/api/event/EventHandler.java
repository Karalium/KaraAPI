package org.kerix.karaapi.api.event;

@FunctionalInterface
public interface EventHandler<T> {

    void handle(T event);
}
