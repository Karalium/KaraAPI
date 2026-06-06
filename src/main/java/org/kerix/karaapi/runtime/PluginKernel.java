package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.annotation.InternalApi;
import org.kerix.karaapi.api.bootstrap.BootstrapContext;
import org.kerix.karaapi.api.bootstrap.PluginModule;
import org.kerix.karaapi.api.config.ConfigService;
import org.kerix.karaapi.api.menu.MenuService;
import org.kerix.karaapi.api.message.MessageService;
import org.kerix.karaapi.api.placeholder.PlaceholderService;
import org.kerix.karaapi.api.registry.RegistryService;
import org.kerix.karaapi.api.service.ServiceContainer;
import org.kerix.karaapi.api.storage.StorageService;
import org.kerix.karaapi.api.task.TaskService;
import org.kerix.karaapi.api.tick.TickOrchestrator;
import org.kerix.karaapi.api.ui.UiService;

import java.util.Objects;
import java.util.logging.Level;

@InternalApi(reason = "Per plugin runtime kernel managed by KaraRuntime.")
public final class PluginKernel {

    private final JavaPlugin apiPlugin;
    private final JavaPlugin hostPlugin;
    private final CoreServices core;

    private KernelState state = KernelState.NEW;

    public PluginKernel(JavaPlugin apiPlugin, JavaPlugin hostPlugin) {
        this.apiPlugin = Objects.requireNonNull(apiPlugin, "apiPlugin");
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.core = CoreServiceInstaller.install(apiPlugin, hostPlugin);
    }

    public void boot(PluginModule... modules) {
        Objects.requireNonNull(modules, "modules");

        if (state == KernelState.BOOTING || state == KernelState.BOOTED) {
            throw new IllegalStateException(hostPlugin.getName() + " is already booted.");
        }

        if (state == KernelState.STOPPING || state == KernelState.STOPPED) {
            throw new IllegalStateException(hostPlugin.getName() + " kernel has already shut down.");
        }

        state = KernelState.BOOTING;
        core.announcer().announceStart();

        try {
            configureModules(modules);

            core.services().startAll();
            core.services().registerTickables(core.ticks());
            core.services().logBindings();

            state = KernelState.BOOTED;
            core.announcer().announceStarted();
        } catch (Throwable throwable) {
            shutdown();
            throw throwable;
        }
    }

    private void configureModules(PluginModule... modules) {
        for (PluginModule module : modules) {
            if (module == null) {
                continue;
            }

            module.configure(core.context());
        }
    }

    public void shutdown() {
        if (state == KernelState.STOPPING || state == KernelState.STOPPED) {
            return;
        }

        state = KernelState.STOPPING;

        try {
            core.announcer().announceStop();

            core.ticks().unregisterAll();
            core.tasks().cancelAll();
            core.services().shutdownAll();

            core.announcer().announceStopped();
        } catch (Throwable throwable) {
            hostPlugin.getLogger().log(
                    Level.SEVERE,
                    "Error while shutting down " + hostPlugin.getName(),
                    throwable
            );
        } finally {
            state = KernelState.STOPPED;
        }
    }

    public JavaPlugin apiPlugin() {
        return apiPlugin;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    public BootstrapContext context() {
        return core.context();
    }

    public ServiceContainer services() {
        return core.services();
    }

    public TickOrchestrator ticks() {
        return core.ticks();
    }

    public ConfigService configs() {
        return core.configs();
    }

    public TaskService tasks() {
        return core.tasks();
    }

    public UiService ui() {
        return core.ui();
    }

    public MenuService menus() {
        return core.menus();
    }

    public PlaceholderService placeholders() {
        return core.placeholders();
    }

    public MessageService messages() {
        return core.messages();
    }

    public RegistryService registries() {
        return core.registries();
    }

    public StorageService storage() {
        return core.storage();
    }

    public boolean booted() {
        return state == KernelState.BOOTED;
    }

    public boolean stopped() {
        return state == KernelState.STOPPED;
    }

    public boolean stopping() {
        return state == KernelState.STOPPING;
    }
}
