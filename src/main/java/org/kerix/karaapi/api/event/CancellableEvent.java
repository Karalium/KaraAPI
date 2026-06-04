package org.kerix.karaapi.api.event;

public interface CancellableEvent {

    boolean cancelled();

    void cancelled(boolean cancelled);
}
