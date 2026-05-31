package org.kerix.karaapi.internal.debug;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DebugLog {

    private final Logger logger;
    private boolean enabled;

    public DebugLog(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return enabled;
    }

    public void info(String message) {
        if (enabled) {
            logger.info("[Debug] " + message);
        }
    }

    public void warning(String message) {
        if (enabled) {
            logger.warning("[Debug] " + message);
        }
    }

    public void severe(String message, Throwable throwable) {
        if (enabled) {
            logger.log(Level.SEVERE, "[Debug] " + message, throwable);
        }
    }

    public void time(String name, Runnable runnable) {
        if (!enabled) {
            runnable.run();
            return;
        }

        DebugTimer.measure(
                name,
                runnable,
                summary -> logger.info("[Debug] " + summary)
        );
    }
}
