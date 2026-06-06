package org.kerix.karaapi.api.placeholder;

public class PlaceholderException extends RuntimeException {
    public PlaceholderException(String message) {
        super(message);
    }

    public PlaceholderException(String message , Throwable cause) {
        super(message , cause);
    }
}
