package org.kerix.karaapi.internal.annotation;

public final class AnnotationException extends RuntimeException {

    public AnnotationException(String message) {
        super(message);
    }

    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
