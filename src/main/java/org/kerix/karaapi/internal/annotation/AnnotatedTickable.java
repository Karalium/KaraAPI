package org.kerix.karaapi.internal.annotation;

import org.kerix.karaapi.api.lifecycle.Tickable;

import java.lang.reflect.Method;
import java.util.Objects;

final class AnnotatedTickable implements Tickable {

    private final Object owner;
    private final Method method;
    private final long interval;
    private final AnnotationRuntime runtime;

    AnnotatedTickable(
            Object owner,
            Method method,
            long interval,
            AnnotationRuntime runtime
    ) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.method = Objects.requireNonNull(method, "method");
        this.interval = Math.max(1L, interval);
        this.runtime = Objects.requireNonNull(runtime, "runtime");

        this.method.setAccessible(true);
    }

    @Override
    public long tickInterval() {
        return interval;
    }

    @Override
    public void tick() {
        runtime.invoke(owner, method);
    }

    @Override
    public String toString() {
        return "AnnotatedTickable{owner=" + owner.getClass().getName()
                + ", method=" + method.getName()
                + ", interval=" + interval + '}';
    }
}
