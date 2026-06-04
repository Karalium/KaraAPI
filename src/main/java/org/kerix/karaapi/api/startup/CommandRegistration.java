package org.kerix.karaapi.api.startup;

import org.bukkit.command.PluginCommand;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommandRegistration implements AutoCloseable {

    private final PluginCommand command;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public CommandRegistration(PluginCommand command, Runnable unregisterAction) {
        this.command = Objects.requireNonNull(command, "command");
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    public PluginCommand command() {
        return command;
    }

    public boolean active() {
        return active.get();
    }

    public void unregister() {
        if (active.compareAndSet(true, false)) {
            unregisterAction.run();
        }
    }

    @Override
    public void close() {
        unregister();
    }
}