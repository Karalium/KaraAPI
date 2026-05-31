package org.kerix.karaapi.api.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ConfigTypes {

    public static final ConfigType<String> STRING = new ConfigType<>() {
        @Override
        public String read(ConfigurationSection section, String path, String defaultValue) {
            return section.getString(path, defaultValue);
        }

        @Override
        public void write(ConfigurationSection section, String path, String value) {
            section.set(path, value);
        }
    };

    public static final ConfigType<Integer> INT = new ConfigType<>() {
        @Override
        public Integer read(ConfigurationSection section, String path, Integer defaultValue) {
            return section.getInt(path, defaultValue);
        }

        @Override
        public void write(ConfigurationSection section, String path, Integer value) {
            section.set(path, value);
        }
    };

    public static final ConfigType<Long> LONG = new ConfigType<>() {
        @Override
        public Long read(ConfigurationSection section, String path, Long defaultValue) {
            return section.getLong(path, defaultValue);
        }

        @Override
        public void write(ConfigurationSection section, String path, Long value) {
            section.set(path, value);
        }
    };

    public static final ConfigType<Double> DOUBLE = new ConfigType<>() {
        @Override
        public Double read(ConfigurationSection section, String path, Double defaultValue) {
            return section.getDouble(path, defaultValue);
        }

        @Override
        public void write(ConfigurationSection section, String path, Double value) {
            section.set(path, value);
        }
    };

    public static final ConfigType<Boolean> BOOLEAN = new ConfigType<>() {
        @Override
        public Boolean read(ConfigurationSection section, String path, Boolean defaultValue) {
            return section.getBoolean(path, defaultValue);
        }

        @Override
        public void write(ConfigurationSection section, String path, Boolean value) {
            section.set(path, value);
        }
    };

    public static final ConfigType<List<String>> STRING_LIST = new ConfigType<>() {
        @Override
        public List<String> read(ConfigurationSection section, String path, List<String> defaultValue) {
            if (!section.contains(path)) {
                return defaultValue == null ? List.of() : List.copyOf(defaultValue);
            }

            return section.getStringList(path);
        }

        @Override
        public void write(ConfigurationSection section, String path, List<String> value) {
            section.set(path, value == null ? List.of() : new ArrayList<>(value));
        }
    };

    public static final ConfigType<Material> MATERIAL = new ConfigType<>() {
        @Override
        public Material read(ConfigurationSection section, String path, Material defaultValue) {
            String raw = section.getString(path);

            if (raw == null || raw.isBlank()) {
                return defaultValue;
            }

            Material material = Material.matchMaterial(raw);

            if (material == null) {
                throw new ConfigException("Invalid material at '" + path + "': " + raw);
            }

            return material;
        }

        @Override
        public void write(ConfigurationSection section, String path, Material value) {
            section.set(path, value == null ? null : value.name());
        }
    };

    public static final ConfigType<ItemStack> ITEM = new ConfigType<>() {
        @Override
        public ItemStack read(ConfigurationSection section, String path, ItemStack defaultValue) {
            ItemStack item = section.getItemStack(path);

            if (item == null) {
                return defaultValue == null ? null : defaultValue.clone();
            }

            return item.clone();
        }

        @Override
        public void write(ConfigurationSection section, String path, ItemStack value) {
            section.set(path, value == null ? null : value.clone());
        }
    };

    private ConfigTypes() {
    }

    public static <E extends Enum<E>> ConfigType<E> enumType(Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");

        return new ConfigType<>() {
            @Override
            public E read(ConfigurationSection section, String path, E defaultValue) {
                String raw = section.getString(path);

                if (raw == null || raw.isBlank()) {
                    return defaultValue;
                }

                try {
                    return Enum.valueOf(enumClass, raw.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                    throw new ConfigException(
                            "Invalid enum value at '" + path + "': " + raw
                                    + ". Expected one of: " + List.of(enumClass.getEnumConstants()),
                            exception
                    );
                }
            }

            @Override
            public void write(ConfigurationSection section, String path, E value) {
                section.set(path, value == null ? null : value.name());
            }
        };
    }
}
