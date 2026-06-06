package org.kerix.karaapi.api.annotation;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiStatus.Internal
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.CONSTRUCTOR,
        ElementType.PACKAGE
})
public @interface InternalApi {

    String reason() default "";
}
