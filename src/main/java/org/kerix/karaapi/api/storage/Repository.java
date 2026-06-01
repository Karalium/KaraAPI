package org.kerix.karaapi.api.storage;

import java.util.List;
import java.util.Optional;

public interface Repository<K, V> {

    void save(K id, V value);

    Optional<V> load(K id);

    boolean exists(K id);

    void delete(K id);

    List<K> ids();

    List<V> loadAll();
}
