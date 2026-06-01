package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.bootstrap.BootstrapContext;
import org.kerix.karaapi.api.bootstrap.PluginModule;
import org.kerix.karaapi.api.config.ConfigService;
import org.kerix.karaapi.api.event.EventBus;
import org.kerix.karaapi.api.item.custom.CustomItemService;
import org.kerix.karaapi.api.menu.MenuService;
import org.kerix.karaapi.api.message.MessageService;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.profile.ProfileService;
import org.kerix.karaapi.api.recipe.RecipeService;
import org.kerix.karaapi.api.region.RegionService;
import org.kerix.karaapi.api.registry.RegistryService;
import org.kerix.karaapi.api.requirement.RequirementService;
import org.kerix.karaapi.api.scheduler.KaraScheduler;
import org.kerix.karaapi.api.scheduler.KaraSchedulers;
import org.kerix.karaapi.api.service.ServiceContainer;
import org.kerix.karaapi.api.startup.CommandRegistrar;
import org.kerix.karaapi.api.startup.ListenerRegistrar;
import org.kerix.karaapi.api.startup.StartupAnnouncer;
import org.kerix.karaapi.api.startup.StartupProfile;
import org.kerix.karaapi.api.storage.StorageService;
import org.kerix.karaapi.api.task.TaskService;
import org.kerix.karaapi.api.tick.TickOrchestrator;
import org.kerix.karaapi.api.ui.UiService;

import java.util.Objects;
import java.util.logging.Level;

public final class PluginKernel {

    private final JavaPlugin apiPlugin;
    private final JavaPlugin hostPlugin;

    private final ServiceContainer services;

    private final TickOrchestrator ticks;
    private final ConfigService configs;
    private final TaskService tasks;
    private final UiService ui;
    private final MenuService menus;

    private final PlaceholderService placeholders;
    private final MessageService messages;
    private final RegistryService registries;
    private final StorageService storage;

    private final StartupProfile profile;
    private final StartupAnnouncer announcer;
    private final CommandRegistrar commands;
    private final ListenerRegistrar listeners;

    private final ProfileService profiles;
    private final CustomItemService customItems;
    private final RequirementService requirements;
    private final RegionService regions;

    private final EventBus events;
    private final KaraScheduler scheduler;
    private final RecipeService recipes;

    private final BootstrapContext context;

    private volatile boolean booted;
    private volatile boolean shutdown;

    public PluginKernel(JavaPlugin apiPlugin, JavaPlugin hostPlugin) {
        this.apiPlugin = Objects.requireNonNull(apiPlugin, "apiPlugin");
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");

        this.services = new ServiceContainer(hostPlugin.getLogger());

        this.ticks = new TickOrchestrator(hostPlugin);
        this.configs = new ConfigService(hostPlugin);
        this.tasks = new TaskService(hostPlugin);
        this.ui = new UiService(hostPlugin);
        this.menus = new MenuService(hostPlugin);

        this.placeholders = new PlaceholderService(hostPlugin);
        this.messages = new MessageService(hostPlugin, configs, placeholders);
        this.ui.messages(messages);

        this.registries = new RegistryService();
        this.storage = new StorageService(hostPlugin);

        this.profile = StartupProfile.from(hostPlugin);
        this.announcer = new StartupAnnouncer(hostPlugin.getLogger(), profile);
        this.commands = new CommandRegistrar(hostPlugin);
        this.listeners = new ListenerRegistrar(hostPlugin);

        this.profiles = new ProfileService();
        this.customItems = new CustomItemService(hostPlugin);
        this.requirements = new RequirementService();
        this.regions = new RegionService();

        this.events = new EventBus();
        this.scheduler = KaraSchedulers.create(hostPlugin);
        this.recipes = new RecipeService(hostPlugin);

        this.context = new BootstrapContext(
                apiPlugin,
                hostPlugin,

                services,

                ticks,
                commands,
                listeners,
                announcer,

                configs,
                tasks,
                ui,
                menus,

                placeholders,
                messages,
                registries,
                storage,

                profiles,
                customItems,
                requirements,
                regions,

                events,
                scheduler,
                recipes
        );

        bindCoreServices();
    }

    private void bindCoreServices() {
        services.bind(ServiceContainer.class, services);

        services.bind(TickOrchestrator.class, ticks);
        services.bind(ConfigService.class, configs);
        services.bind(TaskService.class, tasks);
        services.bind(UiService.class, ui);
        services.bind(MenuService.class, menus);

        services.bind(PlaceholderService.class, placeholders);
        services.bind(MessageService.class, messages);
        services.bind(RegistryService.class, registries);
        services.bind(StorageService.class, storage);

        services.bind(ProfileService.class, profiles);
        services.bind(CustomItemService.class, customItems);
        services.bind(RequirementService.class, requirements);
        services.bind(RegionService.class, regions);

        services.bind(EventBus.class, events);
        services.bind(KaraScheduler.class, scheduler);
        services.bind(RecipeService.class, recipes);

        services.bind(StartupProfile.class, profile);
        services.bind(StartupAnnouncer.class, announcer);
        services.bind(CommandRegistrar.class, commands);
        services.bind(ListenerRegistrar.class, listeners);
    }

    public void boot(PluginModule... modules) {
        Objects.requireNonNull(modules, "modules");

        if (booted) {
            throw new IllegalStateException(hostPlugin.getName() + " is already booted.");
        }

        if (shutdown) {
            throw new IllegalStateException(hostPlugin.getName() + " kernel has already shut down.");
        }

        announcer.announceStart();

        try {
            for (PluginModule module : modules) {
                if (module == null) {
                    continue;
                }

                module.configure(context);
            }

            services.startAll();
            services.registerTickables(ticks);
            services.logBindings();

            booted = true;

            announcer.announceStarted();
        } catch (Throwable throwable) {
            shutdown();

            throw throwable;
        }
    }

    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;

        try {
            announcer.announceStop();

            ticks.unregisterAll();
            tasks.cancelAll();

            services.shutdownAll();

            booted = false;

            announcer.announceStopped();
        } catch (Throwable throwable) {
            hostPlugin.getLogger().log(
                    Level.SEVERE,
                    "Error while shutting down " + hostPlugin.getName(),
                    throwable
            );
        }
    }

    public JavaPlugin apiPlugin() {
        return apiPlugin;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public BootstrapContext context() {
        return context;
    }

    public ServiceContainer services() {
        return services;
    }

    public TickOrchestrator ticks() {
        return ticks;
    }

    public ConfigService configs() {
        return configs;
    }

    public TaskService tasks() {
        return tasks;
    }

    public UiService ui() {
        return ui;
    }

    public MenuService menus() {
        return menus;
    }

    public PlaceholderService placeholders() {
        return placeholders;
    }

    public MessageService messages() {
        return messages;
    }

    public RegistryService registries() {
        return registries;
    }

    public StorageService storage() {
        return storage;
    }

    public boolean booted() {
        return booted;
    }

}
