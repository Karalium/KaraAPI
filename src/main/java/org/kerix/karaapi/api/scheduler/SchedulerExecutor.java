package org.kerix.karaapi.api.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SchedulerExecutor {

    ScheduledTaskHandle sync(Runnable runnable);

    ScheduledTaskHandle later(long delayTicks, Runnable runnable);

    ScheduledTaskHandle timer(long delayTicks, long periodTicks, Runnable runnable);

    ScheduledTaskHandle async(Runnable runnable);

    ScheduledTaskHandle asyncLater(long delayTicks, Runnable runnable);

    ScheduledTaskHandle asyncTimer(long delayTicks, long periodTicks, Runnable runnable);

    ScheduledTaskHandle at(Location location, Runnable runnable);

    ScheduledTaskHandle entity(Entity entity, Runnable runnable);

    boolean folia();
}
