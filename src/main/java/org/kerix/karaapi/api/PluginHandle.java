package org.kerix.karaapi.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.bootstrap.BootstrapContext;
import org.kerix.karaapi.api.item.custom.CustomItemService;
import org.kerix.karaapi.api.menu.MenuService;
import org.kerix.karaapi.api.message.MessageService;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.profile.ProfileService;
import org.kerix.karaapi.api.region.RegionService;
import org.kerix.karaapi.api.registry.RegistryService;
import org.kerix.karaapi.api.requirement.RequirementService;
import org.kerix.karaapi.api.service.ServiceContainer;
import org.kerix.karaapi.api.startup.CommandRegistrar;
import org.kerix.karaapi.api.startup.ListenerRegistrar;
import org.kerix.karaapi.api.startup.StartupAnnouncer;
import org.kerix.karaapi.api.storage.StorageService;
import org.kerix.karaapi.api.tick.TickOrchestrator;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kerix.karaapi.api.config.ConfigService;
import org.kerix.karaapi.api.task.TaskService;
import org.kerix.karaapi.api.ui.UiService;


public final class PluginHandle {

    private final JavaPlugin hostPlugin;
    private final BootstrapContext context;
    private final Runnable shutdownAction;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public PluginHandle(
            JavaPlugin hostPlugin,
            BootstrapContext context,
            Runnable shutdownAction
    ) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.context = Objects.requireNonNull(context, "context");
        this.shutdownAction = Objects.requireNonNull(shutdownAction, "shutdownAction");
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public BootstrapContext context() {
        return context;
    }

    public ServiceContainer services() {
        return context.services();
    }

    public TickOrchestrator ticks() {
        return context.ticks();
    }

    public CommandRegistrar commands() {
        return context.commands();
    }

    public ListenerRegistrar listeners() {
        return context.listeners();
    }

    public StartupAnnouncer announcer() {
        return context.announcer();
    }

    public ConfigService configs() {
        return context.configs();
    }

    public TaskService tasks() {
        return context.tasks();
    }

    public UiService ui() {
        return context.ui();
    }

    public MenuService menus() {
        return context.menus();
    }

    public PlaceholderService placeholders() {
        return context.placeholders();
    }

    public MessageService messages() {
        return context.messages();
    }

    public RegistryService registries() {
        return context.registries();
    }

    public StorageService storage() {
        return context.storage();
    }

    public ProfileService profiles() {
        return context.profiles();
    }

    public CustomItemService customItems() {
        return context.customItems();
    }

    public RequirementService requirements() {
        return context.requirements();
    }

    public RegionService regions() {
        return context.regions();
    }

    public boolean isShutdown() {
        return shutdown.get();
    }

    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }

        shutdownAction.run();
    }
}
