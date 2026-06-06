package org.kerix.karaapi.api.startup;

import org.bukkit.event.Listener;

import java.util.List;

/**
 * Platform bridge used by ListenerRegistrar.
 *
 * <p>The public API owns this contract. Platform-specific implementations,
 * such as Paper listener registration, live outside the api package.</p>
 */
public interface ListenerGateway {

    ListenerRegistration register(Listener listener);

    List<ListenerRegistration> registerAll(Listener... listeners);
}
