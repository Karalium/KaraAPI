package org.kerix.karaapi.runtime;

import org.kerix.karaapi.api.effect.EffectEmitter;
import org.kerix.karaapi.api.menu.MenuInventoryFactory;
import org.kerix.karaapi.api.placeholder.PlaceholderExpansionRegistrar;
import org.kerix.karaapi.api.placeholder.PlaceholderProvider;
import org.kerix.karaapi.api.recipe.RecipeRegistrar;
import org.kerix.karaapi.api.scheduler.SchedulerService;
import org.kerix.karaapi.api.ui.SidebarRendererFactory;
import org.kerix.karaapi.paper.command.PaperCommandRegistrar;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

import java.util.List;

record CoreAdapters(
        SchedulerService scheduler,
        MenuInventoryFactory menuInventories,
        RecipeRegistrar recipes,
        EffectEmitter effects,
        List<PlaceholderProvider> placeholderProviders,
        PlaceholderExpansionRegistrar placeholderExpansions,
        SidebarRendererFactory sidebars,
        PaperCommandRegistrar commands,
        PaperListenerRegistrar listeners
) {
}
