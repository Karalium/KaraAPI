package org.kerix.karaapi.internal.annotation;

import org.kerix.karaapi.api.annotation.OnStart;
import org.kerix.karaapi.api.annotation.OnStop;
import org.kerix.karaapi.api.annotation.TickEvery;
import org.kerix.karaapi.api.lifecycle.Tickable;
import org.kerix.karaapi.api.service.ServiceLifecycleProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class AnnotatedLifecycle implements ServiceLifecycleProcessor {

    private final AnnotationRuntime runtime;

    public AnnotatedLifecycle(
            PluginRequirementResolver plugins,
            ThreadAccess threadAccess
    ) {
        this.runtime = new AnnotationRuntime(
                Objects.requireNonNull(plugins, "plugins"),
                Objects.requireNonNull(threadAccess, "threadAccess")
        );
    }

    @Override
    public void start(Object service) {
        Objects.requireNonNull(service, "service");

        for (Method method : lifecycleMethods(service.getClass(), OnStart.class)) {
            validateNoArgs(method);
            runtime.invoke(service, method);
        }
    }

    @Override
    public void stop(Object service) {
        Objects.requireNonNull(service, "service");

        List<Method> methods = lifecycleMethods(service.getClass(), OnStop.class);
        methods.sort(Comparator.comparingInt(this::stopPriority).reversed());

        for (Method method : methods) {
            validateNoArgs(method);
            runtime.invoke(service, method);
        }
    }

    @Override
    public List<Tickable> tickables(Object service) {
        Objects.requireNonNull(service, "service");

        List<Tickable> tickables = new ArrayList<>();

        for (Method method : allMethods(service.getClass())) {
            TickEvery annotation = method.getAnnotation(TickEvery.class);

            if (annotation == null) {
                continue;
            }

            validateNoArgs(method);

            tickables.add(new AnnotatedTickable(
                    service,
                    method,
                    annotation.value(),
                    runtime
            ));
        }

        return List.copyOf(tickables);
    }

    private List<Method> lifecycleMethods(
            Class<?> type,
            Class<? extends java.lang.annotation.Annotation> annotation
    ) {
        List<Method> methods = new ArrayList<>();

        for (Method method : allMethods(type)) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }

        methods.sort(Comparator.comparingInt(this::startPriority));
        return methods;
    }

    private List<Method> allMethods(Class<?> type) {
        List<Method> methods = new ArrayList<>();

        Class<?> current = type;

        while (current != null && current != Object.class) {
            methods.addAll(List.of(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }

        return methods;
    }

    private int startPriority(Method method) {
        OnStart annotation = method.getAnnotation(OnStart.class);
        return annotation == null ? 0 : annotation.priority();
    }

    private int stopPriority(Method method) {
        OnStop annotation = method.getAnnotation(OnStop.class);
        return annotation == null ? 0 : annotation.priority();
    }

    private void validateNoArgs(Method method) {
        if (method.getParameterCount() == 0) {
            return;
        }

        throw new AnnotationException(
                "Annotated lifecycle method must not declare parameters: "
                        + method.toGenericString()
        );
    }
}
