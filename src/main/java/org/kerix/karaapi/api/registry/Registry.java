package org.kerix.karaapi.api.registry;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Registry<T> {

    String name();

    boolean contains(String id);

    T get(String id);

    Optional<T> find(String id);

    Set<String> ids();

    Collection<T> values();

    int size();

    boolean empty();
}
