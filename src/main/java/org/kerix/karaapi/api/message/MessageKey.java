package org.kerix.karaapi.api.message;

import java.util.Objects;

public record MessageKey(
        String path,
        String defaultValue
) {

    public MessageKey {
        Objects.requireNonNull(path, "path");

        if (path.isBlank()) {
            throw new IllegalArgumentException("Message path cannot be blank.");
        }

        defaultValue = defaultValue == null ? "" : defaultValue;
    }

    public static MessageKey of(String path, String defaultValue) {
        return new MessageKey(path, defaultValue);
    }
}
