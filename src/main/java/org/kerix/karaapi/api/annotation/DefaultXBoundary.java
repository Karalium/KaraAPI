package org.kerix.karaapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.TYPE,
        ElementType.PACKAGE
})
public @interface DefaultXBoundary {

    Layer value();

    enum Layer {
        API,
        RUNTIME,
        PAPER,
        INTERNAL
    }
}
