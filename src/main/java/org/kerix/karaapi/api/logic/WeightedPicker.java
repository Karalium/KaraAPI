package org.kerix.karaapi.api.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class WeightedPicker<T> {

    private final List<Entry<T>> entries = new ArrayList<>();
    private double totalWeight;

    public WeightedPicker<T> add(T value, double weight) {
        Objects.requireNonNull(value, "value");

        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0.");
        }

        entries.add(new Entry<>(value, weight));
        totalWeight += weight;

        return this;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public T pick() {
        return pick(new Random());
    }

    public T pick(Random random) {
        Objects.requireNonNull(random, "random");

        if (entries.isEmpty()) {
            throw new IllegalStateException("Cannot pick from an empty WeightedPicker.");
        }

        double target = random.nextDouble() * totalWeight;
        double current = 0.0;

        for (Entry<T> entry : entries) {
            current += entry.weight();

            if (target <= current) {
                return entry.value();
            }
        }

        return entries.getLast().value();
    }

    public List<T> values() {
        return entries.stream()
                .map(Entry::value)
                .toList();
    }

    public void clear() {
        entries.clear();
        totalWeight = 0.0;
    }

    private record Entry<T>(T value, double weight) {
    }
}
