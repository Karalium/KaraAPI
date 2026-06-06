package org.kerix.karaapi.api.effect;

public final class EffectException extends RuntimeException {

    public EffectException(String message) {
        super(message);
    }

    public EffectException(String message, Throwable cause) {
        super(message, cause);
    }
}