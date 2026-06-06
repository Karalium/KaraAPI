package org.kerix.karaapi.internal.annotation;

import org.kerix.karaapi.api.annotation.DependsOn;
import org.kerix.karaapi.api.annotation.ManagedService;

import java.util.List;

public final class ServiceMetadata {

    private final Class<?> serviceType;
    private final Class<?> bindType;
    private final int priority;
    private final boolean autoStart;
    private final boolean autoStop;
    private final boolean registerAnnotatedTicks;
    private final List<Class<?>> dependencies;

    private ServiceMetadata(
            Class<?> serviceType,
            Class<?> bindType,
            int priority,
            boolean autoStart,
            boolean autoStop,
            boolean registerAnnotatedTicks,
            List<Class<?>> dependencies
    ) {
        this.serviceType = serviceType;
        this.bindType = bindType;
        this.priority = priority;
        this.autoStart = autoStart;
        this.autoStop = autoStop;
        this.registerAnnotatedTicks = registerAnnotatedTicks;
        this.dependencies = List.copyOf(dependencies);
    }

    public static ServiceMetadata of(Object service) {
        Class<?> type = service.getClass();

        ManagedService managed = type.getAnnotation(ManagedService.class);
        DependsOn dependsOn = type.getAnnotation(DependsOn.class);

        Class<?> bindType = type;
        int priority = 0;
        boolean autoStart = true;
        boolean autoStop = true;
        boolean registerAnnotatedTicks = true;

        if (managed != null) {
            bindType = managed.value() == Void.class ? type : managed.value();
            priority = managed.priority();
            autoStart = managed.autoStart();
            autoStop = managed.autoStop();
            registerAnnotatedTicks = managed.registerAnnotatedTicks();
        }

        List<Class<?>> dependencies = dependsOn == null
                ? List.of()
                : List.of(dependsOn.value());

        return new ServiceMetadata(
                type,
                bindType,
                priority,
                autoStart,
                autoStop,
                registerAnnotatedTicks,
                dependencies
        );
    }

    public Class<?> serviceType() {
        return serviceType;
    }

    public Class<?> bindType() {
        return bindType;
    }

    public int priority() {
        return priority;
    }

    public boolean autoStart() {
        return autoStart;
    }

    public boolean autoStop() {
        return autoStop;
    }

    public boolean registerAnnotatedTicks() {
        return registerAnnotatedTicks;
    }

    public List<Class<?>> dependencies() {
        return dependencies;
    }
}
