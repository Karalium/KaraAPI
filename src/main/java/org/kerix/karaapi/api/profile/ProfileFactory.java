package org.kerix.karaapi.api.profile;

import java.util.UUID;

@FunctionalInterface
public interface ProfileFactory<T> {

    T create(UUID uuid);
}
