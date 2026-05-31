package org.kerix.karaapi.api.logic;

import java.util.Objects;
import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private boolean initialized;
    private T value;

    private Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Override
    public T get() {
        if (!initialized) {
            value = supplier.get();
            initialized = true;
        }

        return value;
    }

    public boolean initialized() {
        return initialized;
    }

    public void clear() {
        initialized = false;
        value = null;
    }
}
