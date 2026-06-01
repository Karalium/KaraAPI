package org.kerix.karaapi.api.event;

public interface CancellableKaraEvent {

    boolean cancelled();

    void cancelled(boolean cancelled);
}
