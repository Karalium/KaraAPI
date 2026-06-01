package org.kerix.karaapi.api.ui;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.Objects;

public record TitleMessage(
        Component title,
        Component subtitle,
        Duration fadeIn,
        Duration stay,
        Duration fadeOut
) {

    public static TitleMessage of(Component title, Component subtitle) {
        return new TitleMessage(
                title,
                subtitle,
                Duration.ofMillis(500),
                Duration.ofSeconds(2),
                Duration.ofMillis(500)
        );
    }

    public static TitleMessage of(
            Component title,
            Component subtitle,
            Duration fadeIn,
            Duration stay,
            Duration fadeOut
    ) {
        return new TitleMessage(title, subtitle, fadeIn, stay, fadeOut);
    }

    public TitleMessage {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(subtitle, "subtitle");
        Objects.requireNonNull(fadeIn, "fadeIn");
        Objects.requireNonNull(stay, "stay");
        Objects.requireNonNull(fadeOut, "fadeOut");
    }

    public Title asAdventureTitle() {
        return Title.title(
                title,
                subtitle,
                Title.Times.times(fadeIn, stay, fadeOut)
        );
    }

    public void send(Audience audience) {
        audience.showTitle(asAdventureTitle());
    }
}
