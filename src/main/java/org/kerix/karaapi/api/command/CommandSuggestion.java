package org.kerix.karaapi.api.command;

import java.util.List;

@FunctionalInterface
public interface CommandSuggestion {

    List<String> suggest(CommandContext context, String current);
}
