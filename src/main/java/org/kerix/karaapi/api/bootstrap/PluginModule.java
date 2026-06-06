package org.kerix.karaapi.api.bootstrap;

@FunctionalInterface
public interface PluginModule {

    void configure(BootstrapContext context);
}
