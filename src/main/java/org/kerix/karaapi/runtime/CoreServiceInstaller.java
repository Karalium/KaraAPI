package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
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
import org.kerix.karaapi.paper.placeholder.PlaceholderProviders;
import org.kerix.karaapi.paper.scheduler.SchedulerProvider;

import java.util.Objects;

final class CoreServiceInstaller {

    private CoreServiceInstaller() {
    }

    static CoreServices install(JavaPlugin apiPlugin, JavaPlugin hostPlugin) {
        Objects.requireNonNull(apiPlugin, "apiPlugin");
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        ServiceContainer services = new ServiceContainer(hostPlugin.getLogger());

        SchedulerService scheduler = SchedulerProvider.create(hostPlugin);

        TickOrchestrator ticks = new TickOrchestrator(hostPlugin, scheduler);
        ConfigService configs = new ConfigService(hostPlugin);
        TaskService tasks = new TaskService(hostPlugin, scheduler);
        UiService ui = new UiService(hostPlugin);
        MenuService menus = new MenuService(hostPlugin, scheduler);

        PlaceholderService placeholders = new PlaceholderService(
                hostPlugin,
                PlaceholderProviders.create(hostPlugin)
        );

        MessageService messages = new MessageService(hostPlugin, configs, placeholders);

        ui.messages(messages);

        RegistryService registries = new RegistryService();
        StorageService storage = new StorageService(hostPlugin);
        StartupProfile profile = StartupProfile.from(hostPlugin);
        StartupAnnouncer announcer = new StartupAnnouncer(hostPlugin.getLogger(), profile);
        CommandRegistrar commands = new CommandRegistrar(hostPlugin);
        ListenerRegistrar listeners = new ListenerRegistrar(hostPlugin);
        ProfileService profiles = new ProfileService();
        CustomItemService customItems = new CustomItemService(hostPlugin);
        RequirementService requirements = new RequirementService();
        RegionService regions = new RegionService();
        EventBus events = new EventBus(hostPlugin.getLogger());
        RecipeService recipes = new RecipeService(hostPlugin);
        EffectService effects = new EffectService(hostPlugin);

        BootstrapContext context = new BootstrapContext(
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
                recipes,
                effects
        );


        CoreServices core = new CoreServices(
                services,
                scheduler,
                ticks,
                configs,
                tasks,
                ui,
                menus,
                placeholders,
                messages,
                registries,
                storage,
                profile,
                announcer,
                commands,
                listeners,
                profiles,
                customItems,
                requirements,
                regions,
                events,
                effects,
                recipes,
                context
        );

        bindCoreServices(core);

        return core;
    }

    private static void bindCoreServices(CoreServices core) {
        ServiceContainer services = core.services();

        services.bind(ServiceContainer.class, services);
        services.bind(SchedulerService.class, core.scheduler());
        services.bind(TickOrchestrator.class, core.ticks());
        services.bind(ConfigService.class, core.configs());
        services.bind(TaskService.class, core.tasks());
        services.bind(UiService.class, core.ui());
        services.bind(MenuService.class, core.menus());
        services.bind(PlaceholderService.class, core.placeholders());
        services.bind(MessageService.class, core.messages());
        services.bind(RegistryService.class, core.registries());
        services.bind(StorageService.class, core.storage());
        services.bind(ProfileService.class, core.profiles());
        services.bind(CustomItemService.class, core.customItems());
        services.bind(RequirementService.class, core.requirements());
        services.bind(RegionService.class, core.regions());
        services.bind(EventBus.class, core.events());
        services.bind(RecipeService.class, core.recipes());
        services.bind(StartupProfile.class, core.profile());
        services.bind(StartupAnnouncer.class, core.announcer());
        services.bind(CommandRegistrar.class, core.commands());
        services.bind(ListenerRegistrar.class, core.listeners());
    }
}
