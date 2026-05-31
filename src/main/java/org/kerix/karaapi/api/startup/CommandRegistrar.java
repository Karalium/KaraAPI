package org.kerix.karaapi.api.startup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.paper.command.PaperCommandRegistrar;

import java.util.Objects;

import org.kerix.karaapi.api.command.CommandNode;

public final class CommandRegistrar {

    private final PaperCommandRegistrar paper;

    public CommandRegistrar(JavaPlugin hostPlugin) {
        this.paper = new PaperCommandRegistrar(
                Objects.requireNonNull(hostPlugin, "hostPlugin")
        );
    }

    public void register(CommandNode node) {
        paper.register(node);
    }

    public void register(String pluginYmlName, CommandNode node) {
        paper.register(pluginYmlName, node);
    }

    public void register(String name, CommandExecutor executor) {
        paper.register(name, executor);
    }

    public void register(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        paper.register(name, executor, tabCompleter);
    }
}