package org.kerix.karaapi.api.registry;

import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class RegistryService implements Stoppable {

    private final Map<String, MutableRegistry<?>> registries = new LinkedHashMap<>();

    public <T> MutableRegistry<T> create(String name) {
        String key = normalize(name);

        if (registries.containsKey(key)) {
            throw new RegistryException("Registry already exists: " + key);
        }

        MutableRegistry<T> registry = MutableRegistry.create(key);
        registries.put(key, registry);

        return registry;
    }

    @SuppressWarnings("unchecked")
    public <T> MutableRegistry<T> get(String name) {
        String key = normalize(name);

        MutableRegistry<?> registry = registries.get(key);

        if (registry == null) {
            throw new RegistryException("Registry not found: " + key);
        }

        return (MutableRegistry<T>) registry;
    }

    @SuppressWarnings("unchecked")
    public <T> MutableRegistry<T> getOrCreate(String name) {
        String key = normalize(name);

        return (MutableRegistry<T>) registries.computeIfAbsent(
                key,
                MutableRegistry::create
        );
    }

    public Optional<MutableRegistry<?>> find(String name) {
        return Optional.ofNullable(registries.get(normalize(name)));
    }

    public Set<String> names() {
        return Set.copyOf(registries.keySet());
    }

    @Override
    public void stop() {
        registries.clear();
    }

    private static String normalize(String name) {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Registry name cannot be blank.");
        }

        return name.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_");
    }
}
