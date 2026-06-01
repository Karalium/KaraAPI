package org.kerix.karaapi.internal.collection;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class IdentitySets {

    private IdentitySets() {
    }

    public static <T> Set<T> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    public static <T> Set<T> synchronizedIdentitySet() {
        return Collections.synchronizedSet(newIdentitySet());
    }
}
