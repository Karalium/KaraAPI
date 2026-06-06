package org.kerix.karaapi.api.annotation;

import jdk.jfr.Experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Experimental
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD
})
public @interface ExperimentalApi {

    String value() default "";
}
