package org.kerix.karaapi.internal.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Closeables {

    private Closeables() {
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    public static void closeOrLog(
            AutoCloseable closeable,
            Logger logger,
            String message
    ) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception exception) {
            if (logger != null) {
                logger.log(Level.SEVERE, message, exception);
            }
        }
    }
}
