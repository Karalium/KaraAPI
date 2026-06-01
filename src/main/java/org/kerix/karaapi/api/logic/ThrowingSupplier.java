package org.kerix.karaapi.api.logic;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
