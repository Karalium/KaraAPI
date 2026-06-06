package org.kerix.karaapi.api.startup;

public interface Registration extends AutoCloseable {

    boolean active();

    void unregister();

    @Override
    default void close() {
        unregister();
    }
}
