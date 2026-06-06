package org.kerix.karaapi.internal.annotation;

@FunctionalInterface
public interface PluginRequirementResolver {

    boolean isPluginAvailable(String pluginName);

    static PluginRequirementResolver alwaysAvailable() {
        return pluginName -> true;
    }
}
