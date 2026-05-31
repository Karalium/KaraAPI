package org.kerix.karaapi.api.placeholder;

public interface PlaceholderProvider {

    String name();

    boolean available();

    String apply(PlaceholderContext context, String input);
}
