package org.kerix.karaapi.api.region;

import java.util.Locale;
import java.util.Objects;

public final class RegionKeys {

    private RegionKeys() {
    }

    public static String normalize(String id) {
        Objects.requireNonNull(id, "id");

        String normalized = id
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Region id cannot be blank.");
        }

        if (!normalized.matches("[a-z0-9_./]+")) {
            throw new IllegalArgumentException("Invalid region id: " + id);
        }

        return normalized;
    }
}
