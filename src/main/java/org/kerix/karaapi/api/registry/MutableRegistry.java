package org.kerix.karaapi.api.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class MutableRegistry<T> implements Registry<T> {

    private final String name;
    private final Map<String, T> values = new LinkedHashMap<>();

    public MutableRegistry(String name) {
        this.name = normalizeName(name);
    }

    public static <T> MutableRegistry<T> create(String name) {
        return new MutableRegistry<>(name);
    }

    public MutableRegistry<T> register(String id, T value) {
        Objects.requireNonNull(value, "value");

        String key = normalize(id);

        if (values.containsKey(key)) {
            throw new RegistryException(
                    "Duplicate registry entry '" + key + "' in registry '" + name + "'."
            );
        }

        values.put(key, value);
        return this;
    }

    public MutableRegistry<T> replace(String id, T value) {
        Objects.requireNonNull(value, "value");

        values.put(normalize(id), value);
        return this;
    }

    public T unregister(String id) {
        return values.remove(normalize(id));
    }

    public void clear() {
        values.clear();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean contains(String id) {
        return values.containsKey(normalize(id));
    }

    @Override
    public T get(String id) {
        T value = values.get(normalize(id));

        if (value == null) {
            throw new RegistryException(
                    "No registry entry '" + id + "' in registry '" + name + "'."
            );
        }

        return value;
    }

    @Override
    public Optional<T> find(String id) {
        return Optional.ofNullable(values.get(normalize(id)));
    }

    @Override
    public Set<String> ids() {
        return Set.copyOf(values.keySet());
    }

    @Override
    public Collection<T> values() {
        return List.copyOf(values.values());
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean empty() {
        return values.isEmpty();
    }

    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Registry name cannot be blank.");
        }

        return normalize(name);
    }

    private static String normalize(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Registry id cannot be blank.");
        }

        return id.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_");
    }
}
