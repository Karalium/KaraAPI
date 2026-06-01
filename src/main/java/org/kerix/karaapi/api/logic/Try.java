package org.kerix.karaapi.api.logic;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Try {

    private Try() {
    }

    public static Result<Void> run(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable, "runnable");

        try {
            runnable.run();
            return Result.success(null);
        } catch (Throwable throwable) {
            return Result.failure(throwable);
        }
    }

    public static <T> Result<T> get(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");

        try {
            return Result.success(supplier.get());
        } catch (Throwable throwable) {
            return Result.failure(throwable);
        }
    }

    public static void runOrLog(Logger logger, String message, ThrowingRunnable runnable) {
        Objects.requireNonNull(logger, "logger");
        Objects.requireNonNull(runnable, "runnable");

        try {
            runnable.run();
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, message, throwable);
        }
    }

    public static <T> T getOrElse(ThrowingSupplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    public static void runOrElse(ThrowingRunnable runnable, Consumer<Throwable> fallback) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            fallback.accept(throwable);
        }
    }
}
