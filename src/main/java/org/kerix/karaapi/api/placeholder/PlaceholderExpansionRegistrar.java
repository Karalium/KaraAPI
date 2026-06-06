package org.kerix.karaapi.api.placeholder;

public interface PlaceholderExpansionRegistrar {

    boolean available();

    PlaceholderExpansionRegistration register(PlaceholderExpansion expansion);
}
