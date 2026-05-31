package org.kerix.karaapi.internal.collection;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class IdentityMaps {

    private IdentityMaps() {
    }

    public static <K, V> Map<K, V> newIdentityMap() {
        return new IdentityHashMap<>();
    }

    public static <K, V> Map<K, V> synchronizedIdentityMap() {
        return Collections.synchronizedMap(new IdentityHashMap<>());
    }
}
