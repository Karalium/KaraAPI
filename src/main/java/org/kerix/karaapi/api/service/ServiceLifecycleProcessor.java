package org.kerix.karaapi.api.service;

import org.kerix.karaapi.api.lifecycle.Tickable;

import java.util.List;

public interface ServiceLifecycleProcessor {

    void start(Object service);

    void stop(Object service);

    List<Tickable> tickables(Object service);
}
