package org.kerix.karaapi.api.scheduler;

public interface ScheduledTaskHandle {

    void cancel();

    boolean cancelled();
}
