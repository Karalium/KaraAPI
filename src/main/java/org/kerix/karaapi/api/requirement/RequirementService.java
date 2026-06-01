package org.kerix.karaapi.api.requirement;

import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.registry.MutableRegistry;

public final class RequirementService implements Stoppable {

    private final MutableRegistry<Requirement<?>> requirements =
            MutableRegistry.create("requirements");

    public <T> void register(String id, Requirement<T> requirement) {
        requirements.register(id, requirement);
    }

    @SuppressWarnings("unchecked")
    public <T> Requirement<T> get(String id) {
        return (Requirement<T>) requirements.get(id);
    }

    public boolean contains(String id) {
        return requirements.contains(id);
    }

    @Override
    public void stop() {
        requirements.clear();
    }
}
