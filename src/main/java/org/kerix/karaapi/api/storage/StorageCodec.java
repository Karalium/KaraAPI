package org.kerix.karaapi.api.storage;

import org.bukkit.configuration.ConfigurationSection;

public interface StorageCodec<T> {

    void write(ConfigurationSection section, T value);

    T read(ConfigurationSection section);
}
