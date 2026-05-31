package org.kerix.karaapi.api.requirement;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class RequirementResult {

    private static final RequirementResult ALLOWED = new RequirementResult(true, null);

    private final boolean allowed;
    private final Component message;

    private RequirementResult(boolean allowed, Component message) {
        this.allowed = allowed;
        this.message = message;
    }

    public static RequirementResult allow() {
        return ALLOWED;
    }

    public static RequirementResult deny(String message) {
        return deny(Component.text(message, NamedTextColor.RED));
    }

    public static RequirementResult deny(Component message) {
        return new RequirementResult(false, message);
    }

    public boolean allowed() {
        return allowed;
    }

    public boolean denied() {
        return !allowed;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public Component message() {
        return message;
    }
}
