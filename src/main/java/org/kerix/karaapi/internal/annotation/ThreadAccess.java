package org.kerix.karaapi.internal.annotation;

@FunctionalInterface
public interface ThreadAccess {

    boolean isMainThread();

    static ThreadAccess unknown() {
        return () -> true;
    }
}
