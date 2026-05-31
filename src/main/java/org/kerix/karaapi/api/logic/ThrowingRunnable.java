package org.kerix.karaapi.api.logic;

@FunctionalInterface
public interface ThrowingRunnable {

    void run() throws Exception;
}
