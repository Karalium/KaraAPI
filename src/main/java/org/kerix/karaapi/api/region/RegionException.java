package org.kerix.karaapi.api.region;

public final class RegionException extends RuntimeException {

    public RegionException(String message) {
        super(message);
    }

    public RegionException(String message, Throwable cause) {
        super(message, cause);
    }
}
