package org.kerix.karaapi.api.command.argument;

import java.util.HashMap;
import java.util.Map;

public final class ParsedArguments {

    private final Map<String, Object> values;

    public ParsedArguments() {
        this.values = new HashMap<>();
    }

    private ParsedArguments(Map<String, Object> values) {
        this.values = new HashMap<>(values);
    }

    public static ParsedArguments empty() {
        return new ParsedArguments();
    }

    public <T> void put(String name, T value) {
        values.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No parsed argument named '" + name + "'.");
        }

        return (T) values.get(name);
    }

    public String string(String name) {
        return get(name);
    }

    public int integer(String name) {
        return get(name);
    }

    public long longNumber(String name) {
        return get(name);
    }

    public double decimal(String name) {
        return get(name);
    }

    public boolean bool(String name) {
        return get(name);
    }

    public boolean has(String name) {
        return values.containsKey(name);
    }

    public Map<String, Object> values() {
        return Map.copyOf(values);
    }
}
