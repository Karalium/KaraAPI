package org.kerix.karaapi.api.startup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.command.CommandNode;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.paper.command.PaperCommandRegistrar;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandRegistrar implements Stoppable {

    private final PaperCommandRegistrar paper;
    private final Set<CommandRegistration> registrations = ConcurrentHashMap.newKeySet();

    private volatile boolean stopped;

    public CommandRegistrar(JavaPlugin hostPlugin) {
        this.paper = new PaperCommandRegistrar(
                Objects.requireNonNull(hostPlugin, "hostPlugin")
        );
    }

    public CommandRegistration register(CommandNode node) {
        ensureRunning();

        CommandRegistration registration = paper.register(node);
        registrations.add(registration);

        return registration;
    }

    public CommandRegistration register(String pluginYmlName, CommandNode node) {
        ensureRunning();

        CommandRegistration registration = paper.register(pluginYmlName, node);
        registrations.add(registration);

        return registration;
    }

    public CommandRegistration register(String name, CommandExecutor executor) {
        ensureRunning();

        CommandRegistration registration = paper.register(name, executor);
        registrations.add(registration);

        return registration;
    }

    public CommandRegistration register(
            String name,
            CommandExecutor executor,
            TabCompleter tabCompleter
    ) {
        ensureRunning();

        CommandRegistration registration = paper.register(name, executor, tabCompleter);
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