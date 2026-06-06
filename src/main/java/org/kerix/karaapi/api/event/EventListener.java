package org.kerix.karaapi.api.event;

@FunctionalInterface
public interface EventListener<T> {

    void handle(T event);
}
