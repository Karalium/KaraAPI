package org.kerix.karaapi.runtime;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.paper.command.PaperCommandRegistrar;
import org.kerix.karaapi.paper.effect.PaperEffectEmitter;
import org.kerix.karaapi.paper.inventory.PaperMenuInventoryFactory;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;
import org.kerix.karaapi.paper.placeholder.PlaceholderProviders;
import org.kerix.karaapi.paper.recipe.PaperRecipeRegistrar;
import org.kerix.karaapi.paper.scheduler.SchedulerProvider;
import org.kerix.karaapi.paper.scoreboard.PaperSidebarRendererFactory;

import java.util.Objects;

public final class PaperAdapterInstaller{

    private PaperAdapterInstaller() {
    }

    public static CoreAdapters install(JavaPlugin hostPlugin) {
        Objects.requireNonNull(hostPlugin, "hostPlugin");

        return new CoreAdapters(
                SchedulerProvider.create(hostPlugin),
                new PaperMenuInventoryFactory(),
                new PaperRecipeRegistrar(hostPlugin),
                new PaperEffectEmitter(),
                PlaceholderProviders.create(hostPlugin),
                PlaceholderProviders.expansionRegistrar(hostPlugin),
                new PaperSidebarRendererFactory(),
                new PaperCommandRegistrar(hostPlugin),
                new PaperListenerRegistrar(hostPlugin)
        );
    }
}
