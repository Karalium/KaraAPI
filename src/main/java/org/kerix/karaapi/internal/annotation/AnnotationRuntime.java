package org.kerix.karaapi.internal.annotation;


import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.RequiresPlugin;
import org.kerix.karaapi.api.annotation.RequiresPlugins;

import java.lang.reflect.Method;
import java.util.Objects;

final class AnnotationRuntime {

    private final PluginRequirementResolver plugins;
    private final ThreadAccess threadAccess;

    AnnotationRuntime(
            PluginRequirementResolver plugins,
            ThreadAccess threadAccess
    ) {
        this.plugins = Objects.requireNonNull(plugins, "plugins");
        this.threadAccess = Objects.requireNonNull(threadAccess, "threadAccess");
    }

    void invoke(Object owner, Method method) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(method, "method");

        validateRequirements(owner.getClass());
        validateRequirements(method);
        validateThread(owner.getClass(), method);

        try {
            method.setAccessible(true);
            method.invoke(owner);
        } catch (ReflectiveOperationException exception) {
            throw new AnnotationException(
                    "Failed to invoke annotated method "
                            + owner.getClass().getName()
                            + "#"
                            + method.getName(),
                    exception
            );
        }
    }

    void validateRequirements(Class<?> type) {
        RequiresPlugin single = type.getAnnotation(RequiresPlugin.class);
        RequiresPlugins multiple = type.getAnnotation(RequiresPlugins.class);

        validate(single, type.getName());

        if (multiple != null) {
            for (RequiresPlugin requirement : multiple.value()) {
                validate(requirement, type.getName());
            }
        }
    }

    void validateRequirements(Method method) {
        RequiresPlugin single = method.getAnnotation(RequiresPlugin.class);
        RequiresPlugins multiple = method.getAnnotation(RequiresPlugins.class);

        validate(single, method.toGenericString());

        if (multiple != null) {
            for (RequiresPlugin requirement : multiple.value()) {
                validate(requirement, method.toGenericString());
            }
        }
    }

    private void validate(RequiresPlugin requirement, String owner) {
        if (requirement == null) {
            return;
        }

        boolean available = plugins.isPluginAvailable(requirement.value());

        if (!available && requirement.required()) {
            throw new AnnotationException(
                    owner + " requires plugin '" + requirement.value() + "'"
            );
        }
    }

    private void validateThread(Class<?> type, Method method) {
        boolean requiresMainThread =
                type.isAnnotationPresent(MainThread.class)
                        || method.isAnnotationPresent(MainThread.class);

        if (!requiresMainThread) {
            return;
        }

        if (!threadAccess.isMainThread()) {
            throw new AnnotationException(
                    "Annotated method must run on the main server thread: "
                            + method.toGenericString()
            );
        }
    }
}
