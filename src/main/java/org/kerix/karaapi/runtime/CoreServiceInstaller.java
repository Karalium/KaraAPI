package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.kerix.karaapi.api.annotation.InternalApi;
import org.kerix.karaapi.api.annotation.MainThread;
import org.kerix.karaapi.api.bootstrap.BootstrapContext;
import org.kerix.karaapi.api.config.ConfigService;
import org.kerix.karaapi.api.effect.EffectService;
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
import org.kerix.karaapi.api.scheduler.SchedulerService;
import org.kerix.karaapi.api.service.ServiceContainer;
import org.kerix.karaapi.api.startup.CommandRegistrar;
import org.kerix.karaapi.api.startup.ListenerRegistrar;
import org.kerix.karaapi.api.startup.StartupAnnouncer;
import org.kerix.karaapi.api.startup.StartupProfile;
import org.kerix.karaapi.api.storage.StorageService;
import org.kerix.karaapi.api.task.TaskService;
import org.kerix.karaapi.api.tick.TickOrchestrator;
import org.kerix.karaapi.api.ui.UiService;
import org.kerix.karaapi.internal.annotation.AnnotatedLifecycle;
import org.kerix.karaapi.internal.annotation.PluginRequirementResolver;
import org.kerix.karaapi.internal.annotation.ThreadAccess;
import org.kerix.karaapi.paper.command.PaperCommandRegistrar;
import org.kerix.karaapi.paper.inventory.PaperMenuListener;
import org.kerix.karaapi.paper.item.PaperCustomItemListener;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;
import org.kerix.karaapi.paper.region.PaperRegionListener;

import java.util.List;
import java.util.Objects;

@InternalApi(reason = "Runtime service composition root")
final class CoreServiceInstaller {

    private static final List<Installer> INSTALLERS = List.of(
            new FoundationInstaller(),
            new CommunicationInstaller(),
            new GameplayInstaller(),
            new StartupInstaller(),
            new ServiceBindingInstaller()
    );

    private CoreServiceInstaller() {
    }

    @MainThread
    static @NonNull CoreServices install(JavaPlugin apiPlugin, JavaPlugin hostPlugin) {
        RuntimeSetup setup = new RuntimeSetup(apiPlugin, hostPlugin);

        for (Installer installer : INSTALLERS) {
            installer.install(setup);
        }

        return setup.toCoreServices();
    }

    private interface Installer {

        void install(RuntimeSetup setup);
    }

    private static final class RuntimeSetup {

        private final JavaPlugin apiPlugin;
        private final JavaPlugin hostPlugin;
        private final ServiceContainer services;
        private final CoreAdapters adapters;

        private SchedulerService scheduler;
        private TickOrchestrator ticks;

        private ConfigService configs;
        private TaskService tasks;

        private PlaceholderService placeholders;
        private MessageService messages;
        private UiService ui;
        private MenuService menus;

        private RegistryService registries;
        private StorageService storage;
        private RequirementService requirements;
        private EventBus events;

        private StartupProfile profile;
        private StartupAnnouncer announcer;
        private CommandRegistrar commands;
        private ListenerRegistrar listeners;

        private ProfileService profiles;
        private CustomItemService customItems;
        private RegionService regions;

        private RecipeService recipes;
        private EffectService effects;

        private RuntimeSetup(JavaPlugin apiPlugin, JavaPlugin hostPlugin) {
            this.apiPlugin = Objects.requireNonNull(apiPlugin, "apiPlugin");
            this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");

            this.services = new ServiceContainer(
                    hostPlugin.getLogger(),
                    new AnnotatedLifecycle(
                            pluginName -> hostPlugin.getServer()
                                    .getPluginManager()
                                    .isPluginEnabled(pluginName),
                            () -> hostPlugin.getServer().isPrimaryThread()
                    )
            );
            this.adapters = PaperAdapterInstaller.install(hostPlugin);
        }

        private @NonNull CoreServices toCoreServices() {
            BootstrapContext context = new BootstrapContext(
                    require(apiPlugin, "apiPlugin"),
                    require(hostPlugin, "hostPlugin"),
                    require(services, "services"),
                    require(ticks, "ticks"),
                    require(commands, "commands"),
                    require(listeners, "listeners"),
                    require(announcer, "announcer"),
                    require(configs, "configs"),
                    require(tasks, "tasks"),
                    require(ui, "ui"),
                    require(menus, "menus"),
                    require(placeholders, "placeholders"),
                    require(messages, "messages"),
                    require(registries, "registries"),
                    require(storage, "storage"),
                    require(profiles, "profiles"),
                    require(customItems, "customItems"),
                    require(requirements, "requirements"),
                    require(regions, "regions"),
                    require(events, "events"),
                    require(scheduler, "scheduler"),
                    require(recipes, "recipes"),
                    require(effects, "effects")
            );

            return new CoreServices(
                    require(services, "services"),
                    require(scheduler, "scheduler"),
                    require(ticks, "ticks"),
                    require(configs, "configs"),
                    require(tasks, "tasks"),
                    require(ui, "ui"),
                    require(menus, "menus"),
                    require(placeholders, "placeholders"),
                    require(messages, "messages"),
                    require(registries, "registries"),
                    require(storage, "storage"),
                    require(profile, "profile"),
                    require(announcer, "announcer"),
                    require(commands, "commands"),
                    require(listeners, "listeners"),
                    require(profiles, "profiles"),
                    require(customItems, "customItems"),
                    require(requirements, "requirements"),
                    require(regions, "regions"),
                    require(events, "events"),
                    require(effects, "effects"),
                    require(recipes, "recipes"),
                    context
            );
        }

        @Contract(value = "null, _ -> fail; !null, _ -> param1", pure = true)
        private static <T> @NonNull T require(T value, String name) {
            if (value == null) {
                throw new IllegalStateException("Runtime service was not installed: " + name);
            }

            return value;
        }
    }

    private static final class FoundationInstaller implements Installer {

        @Override
        public void install(@NonNull RuntimeSetup setup) {
            setup.scheduler = setup.adapters.scheduler();
            setup.ticks = new TickOrchestrator(setup.hostPlugin, setup.scheduler);

            setup.configs = new ConfigService(setup.hostPlugin);
            setup.tasks = new TaskService(setup.hostPlugin, setup.scheduler);

            setup.registries = new RegistryService();
            setup.storage = new StorageService(setup.hostPlugin);
            setup.requirements = new RequirementService();
            setup.events = new EventBus(setup.hostPlugin.getLogger());
        }
    }

    private static final class CommunicationInstaller implements Installer {

        @Override
        public void install(@NonNull RuntimeSetup setup) {
            setup.placeholders = new PlaceholderService(
                    setup.hostPlugin,
                    setup.adapters.placeholderProviders(),
                    setup.adapters.placeholderExpansions()
            );

            setup.messages = new MessageService(
                    setup.hostPlugin,
                    setup.configs,
                    setup.placeholders
            );

            setup.ui = new UiService(
                    setup.hostPlugin,
                    setup.adapters.sidebars(),
                    setup.messages
            );
        }
    }

    private static final class GameplayInstaller implements Installer {

        @Override
        public void install(@NonNull RuntimeSetup setup) {
            setup.menus = new MenuService(
                    setup.hostPlugin,
                    setup.scheduler,
                    setup.adapters.menuInventories()
            );

            setup.profiles = new ProfileService();
            setup.customItems = new CustomItemService(setup.hostPlugin);
            setup.regions = new RegionService(setup.events);

            setup.recipes = new RecipeService(
                    setup.hostPlugin,
                    setup.adapters.recipes()
            );

            setup.effects = new EffectService(
                    setup.scheduler,
                    setup.adapters.effects()
            );
        }
    }

    private static final class StartupInstaller implements Installer {

        @Override
        public void install(@NonNull RuntimeSetup setup) {
            setup.profile = StartupProfile.from(setup.hostPlugin);

            setup.announcer = new StartupAnnouncer(
                    setup.hostPlugin.getLogger(),
                    setup.profile
            );

            setup.commands = new CommandRegistrar(
                    new PaperCommandRegistrar(setup.hostPlugin)
            );

            setup.listeners = new ListenerRegistrar(
                    new PaperListenerRegistrar(setup.hostPlugin)
            );

            registerDefaultListeners(setup);
        }

        private void registerDefaultListeners(@NonNull RuntimeSetup setup) {
            setup.listeners.register(new PaperMenuListener(setup.menus));
            setup.listeners.register(new PaperCustomItemListener(setup.customItems));
            setup.listeners.register(new PaperRegionListener(setup.regions));
        }
    }

    private static final class ServiceBindingInstaller implements Installer {

        @Override
        public void install(@NonNull RuntimeSetup setup) {
            ServiceContainer services = setup.services;

            services.bind(ServiceContainer.class, setup.services);
            services.bind(SchedulerService.class, setup.scheduler);
            services.bind(TickOrchestrator.class, setup.ticks);

            services.bind(ConfigService.class, setup.configs);
            services.bind(TaskService.class, setup.tasks);

            services.bind(PlaceholderService.class, setup.placeholders);
            services.bind(MessageService.class, setup.messages);
            services.bind(UiService.class, setup.ui);
            services.bind(MenuService.class, setup.menus);

            services.bind(RegistryService.class, setup.registries);
            services.bind(StorageService.class, setup.storage);
            services.bind(RequirementService.class, setup.requirements);
            services.bind(EventBus.class, setup.events);

            services.bind(ProfileService.class, setup.profiles);
            services.bind(CustomItemService.class, setup.customItems);
            services.bind(RegionService.class, setup.regions);

            services.bind(RecipeService.class, setup.recipes);
            services.bind(EffectService.class, setup.effects);

            services.bind(StartupProfile.class, setup.profile);
            services.bind(StartupAnnouncer.class, setup.announcer);
            services.bind(CommandRegistrar.class, setup.commands);
            services.bind(ListenerRegistrar.class, setup.listeners);
        }
    }
}
