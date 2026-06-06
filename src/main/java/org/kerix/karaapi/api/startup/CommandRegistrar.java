package org.kerix.karaapi.api.startup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.kerix.karaapi.api.annotation.ApiBoundary;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.annotation.ManagedService;
import org.kerix.karaapi.api.command.CommandNode;
import org.kerix.karaapi.api.lifecycle.Stoppable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ManagedService(
        value = CommandRegistrar.class,
        priority = 90,
        registerAnnotatedTicks = false
)
@MainThread
public final class CommandRegistrar implements Stoppable {

    private final CommandGateway gateway;
    private final Set<CommandRegistration> registrations = ConcurrentHashMap.newKeySet();

    private volatile boolean stopped;

    public CommandRegistrar(CommandGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public CommandRegistration register(CommandNode node) {
        ensureRunning();

        CommandRegistration registration = gateway.register(
                Objects.requireNonNull(node, "node")
        );

        registrations.add(registration);
        return registration;
    }

    public CommandRegistration register(String pluginYmlName, CommandNode node) {
        ensureRunning();

        CommandRegistration registration = gateway.register(
                Objects.requireNonNull(pluginYmlName, "pluginYmlName"),
                Objects.requireNonNull(node, "node")
        );

        registrations.add(registration);
        return registration;
    }

    public CommandRegistration register(String name, CommandExecutor executor) {
        ensureRunning();

        CommandRegistration registration = gateway.register(
                Objects.requireNonNull(name, "name"),
                Objects.requireNonNull(executor, "executor")
        );

        registrations.add(registration);
        return registration;
    }

    public CommandRegistration register(
            String name,
            CommandExecutor executor,
            TabCompleter tabCompleter
    ) {
        ensureRunning();

        CommandRegistration registration = gateway.register(
                Objects.requireNonNull(name, "name"),
                Objects.requireNonNull(executor, "executor"),
                Objects.requireNonNull(tabCompleter, "tabCompleter")
        );

        registrations.add(registration);
        return registration;
    }

    public void unregister(CommandRegistration registration) {
        if (registration == null) {
            return;
        }

        registration.unregister();
        registrations.remove(registration);
    }

    public void unregisterAll() {
        for (CommandRegistration registration : Set.copyOf(registrations)) {
            registration.unregister();
        }

        registrations.clear();
    }

    public int registeredCount() {
        cleanup();
        return registrations.size();
    }

    public Set<CommandRegistration> registrations() {
        cleanup();
        return Set.copyOf(registrations);
    }

    @Override
    public void stop() {
        stopped = true;
        unregisterAll();
    }

    private void cleanup() {
        registrations.removeIf(registration -> !registration.active());
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("CommandRegistrar has already stopped.");
        }
    }
}