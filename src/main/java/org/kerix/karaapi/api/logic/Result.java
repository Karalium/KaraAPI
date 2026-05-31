package org.kerix.karaapi.api.logic;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<T> {

    private final T value;
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, Objects.requireNonNull(error, "error"));
    }

    public static <T> Result<T> failure(String message) {
        return failure(new IllegalStateException(message));
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T value() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from failed Result.", error);
        }

        return value;
    }

    public Throwable error() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from successful Result.");
        }

        return error;
    }

    public Optional<T> optional() {
        return isSuccess() ? Optional.ofNullable(value) : Optional.empty();
    }

    public T orElse(T fallback) {
        return isSuccess() ? value : fallback;
    }

    public T orElseGet(Function<Throwable, T> fallback) {
        return isSuccess() ? value : fallback.apply(error);
    }

    public T orThrow() {
        if (isSuccess()) {
            return value;
        }

        if (error instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }

        throw new RuntimeException(error);
    }

    public <R> Result<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        if (isFailure()) {
            return Result.failure(error);
        }

        try {
            return Result.success(mapper.apply(value));
        } catch (Throwable throwable) {
            return Result.failure(throwable);
        }
    }

    public <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        if (isFailure()) {
            return Result.failure(error);
        }

        try {
            return Objects.requireNonNull(mapper.apply(value), "mapper result");
        } catch (Throwable throwable) {
            return Result.failure(throwable);
        }
    }

    public Result<T> ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }

        return this;
    }

    public Result<T> ifFailure(Consumer<Throwable> consumer) {
        if (isFailure()) {
            consumer.accept(error);
        }

        return this;
    }
}
