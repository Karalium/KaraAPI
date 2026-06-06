package org.kerix.karaapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD
})
@Repeatable(RequiresPlugins.class)
public @interface RequiresPlugin {

    String value();

    boolean required() default true;
}
