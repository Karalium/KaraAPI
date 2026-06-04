package org.kerix.karaapi.api.placeholder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class PlaceholderSet {

    private final Map<String, String> placeholders = new LinkedHashMap<>();

    private PlaceholderSet() {
    }

    public static PlaceholderSet empty() {
        return new PlaceholderSet();
    }

    public static PlaceholderSet of(Placeholder... placeholders) {
        PlaceholderSet set = new PlaceholderSet();

        if (placeholders != null) {
            for (Placeholder placeholder : placeholders) {
                set.add(placeholder);
            }
        }

        return set;
    }

    public static PlaceholderSet create() {
        return new PlaceholderSet();
    }

    public PlaceholderSet add(String key, Object value) {
        return add(Placeholder.of(key, value));
    }

    public PlaceholderSet add(Placeholder placeholder) {
        Objects.requireNonNull(placeholder, "placeholder");
        placeholders.put(placeholder.key(), placeholder.value());
        return this;
    }

    public PlaceholderSet addAll(PlaceholderSet other) {
        Objects.requireNonNull(other, "other");
        placeholders.putAll(other.placeholders);
        return this;
    }

    public String applyAngle(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String result = input;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("<" + entry.getKey() + ">", entry.getValue());
        }

        return result;
    }

    public String applyPercent(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String result = input;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return result;
    }

    public String applyBrace(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String result = input;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }

    public String applyAll(String input) {
        return applyBrace(applyPercent(applyAngle(input)));
    }

    public Map<String, String> asMap() {
        return Map.copyOf(placeholders);
    }

    public boolean isEmpty() {
        return placeholders.isEmpty();
    }
}