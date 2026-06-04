package org.kerix.karaapi.api.scheduler;

public interface ScheduledTaskHandle {

    void cancel();

    boolean cancelled();

    default boolean running() {
        return !cancelled();
    }
}
