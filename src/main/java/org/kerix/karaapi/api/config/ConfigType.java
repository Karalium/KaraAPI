package org.kerix.karaapi.api.config;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigType<T> {

    T read(ConfigurationSection section, String path, T defaultValue);

    void write(ConfigurationSection section, String path, T value);
}
