package org.kerix.karaapi.api.placeholder;

import java.util.Objects;

public record Placeholder(
        String key,
        String value
) {

    public Placeholder {
        Objects.requireNonNull(key, "key");
        value = value == null ? "" : value;

        if (key.isBlank()) {
            throw new IllegalArgumentException("Placeholder key cannot be blank.");
        }
    }

    public static Placeholder of(String key, Object value) {
        return new Placeholder(key, value == null ? "" : String.valueOf(value));
    }

    public String token() {
        return "<" + key + ">";
    }

    public String percentToken() {
        return "%" + key + "%";
    }
}
