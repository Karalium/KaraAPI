package org.kerix.karaapi.api.startup;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommandRegistration implements Registration {

    private final String name;
    private final List<String> aliases;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public CommandRegistration(String name, Runnable unregisterAction) {
        this(name, List.of(), unregisterAction);
    }

    public CommandRegistration(
            String name,
            List<String> aliases,
            Runnable unregisterAction
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.aliases = List.copyOf(Objects.requireNonNull(aliases, "aliases"));
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public String name() {
        return name;
    }

    public List<String> aliases() {
        return aliases;
    }

    @Override
    public boolean active() {
        return active.get();
    }

    @Override
    public void unregister() {
        if (active.compareAndSet(true, false)) {
            unregisterAction.run();
        }
    }

    @Override
    public String toString() {
        return "CommandRegistration{name='" + name + "', active=" + active() + '}';
    }
}