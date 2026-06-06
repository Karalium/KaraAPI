package org.kerix.karaapi.api.lifecycle;

public interface Tickable {
    void tick();

    long tickInterval();
}
